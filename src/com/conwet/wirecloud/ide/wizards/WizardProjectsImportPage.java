/*
 *  Copyright (c) 2013-2015 CoNWeT Lab., Universidad Politécnica de Madrid
 *
 *  This file is part of Wirecloud IDE.
 *
 *  Wirecloud IDE is free software: you can redistribute it and/or modify
 *  it under the terms of the European Union Public Licence (EUPL)
 *  as published by the European Commission, either version 1.1
 *  of the License, or (at your option) any later version.
 *
 *  Wirecloud IDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 *
 *  You should have received a copy of the European Union Public Licence
 *  along with Wirecloud IDE.
 *  If not, see <https://joinup.ec.europa.eu/software/page/eupl/licence-eupl>.
 */

package com.conwet.wirecloud.ide.wizards;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

import com.conwet.wirecloud.ide.MACDescription;
import com.conwet.wirecloud.ide.MACDescriptionParseException;
import com.conwet.wirecloud.ide.importcode.*;
import com.conwet.wirecloud.ide.natures.OperatorProjectNature;
import com.conwet.wirecloud.ide.natures.WidgetProjectNature;
import com.conwet.wirecloud.ide.projects.ProjectSupport;

/**
 * The WizardProjectsImportPage is the page that allows the user to import
 * projects from a particular location.
 */
public class WizardProjectsImportPage extends WizardDataTransferPage implements
IOverwriteQuery {

    /**
     * The name of the folder containing metadata information for the workspace.
     */
    public static final String METADATA_FOLDER = ".metadata"; //$NON-NLS-1$

    /**
     * The file that contains all information of the widget or operator.
     */
    public static final String MAC_DESCRIPTION_FILE = "config.xml"; //$NON-NLS-1$

	/**
	 * Messages
	 */
	private static final String ImportProjectDescription = "Select an archive file to search for existing Widget/Operator projects.";
	private static final String ImportProjectWarning = "Project name already exists, please select a valid Widget/Operator project.";
	/**
	 * The import structure provider.
	 *
	 * @since 3.4
	 */
	private ILeveledImportStructureProvider structureProvider;

	/**
	 * @since 3.5
	 *
	 */
	private final class ProjectLabelProvider extends LabelProvider implements IColorProvider{

		public String getText(Object element) {
			return ((ProjectRecord) element).getProjectLabel();
		}

		public Color getBackground(Object element) {
			return null;
		}

		public Color getForeground(Object element) {
			ProjectRecord projectRecord = (ProjectRecord) element;
			if(projectRecord.hasConflicts)
				return getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY);
			return null;
		}
	}

	/**
	 * Class declared public only for test suite.
	 *
	 */
	public class ProjectRecord {

        Object resourceDescriptionFile;
        Object projectDescriptionFile;
        String projectName;
        boolean hasConflicts;

        public MACDescription macDescription = null;
        public IProjectDescription description = null;

        ProjectRecord(File file, File projectFile) {
            resourceDescriptionFile = file;
            projectDescriptionFile = projectFile;
            setProjectName();
        }

        /**
         * Create a record for a project based on the info in the file.
         *
         * @param file
         */
        ProjectRecord(Object file, Object projectFile) {
            resourceDescriptionFile = file;
            projectDescriptionFile = projectFile;
            setProjectName();
        }

        /**
         * Set the name of the project based on the resource description (config.xml).
         */
        private void setProjectName() {
            try {

                if (resourceDescriptionFile instanceof File) {
                    try {
                        macDescription = new MACDescription((File) resourceDescriptionFile);
                    } catch (MACDescriptionParseException e) {
                        // Ignore
                    }

                    if (projectDescriptionFile != null) {
                        IPath path = new Path(((File)projectDescriptionFile).getPath());
                        if (!isDefaultLocation(path)) {
                            try {
                                this.description = IDEWorkbenchPlugin.getPluginWorkspace().loadProjectDescription(path);
                            } catch (CoreException e) {
                                // Use info from the config.xml file
                            }
                        }
                    }

                } else {
                    try {
                        macDescription = new MACDescription(structureProvider.getContents(resourceDescriptionFile));
                    } catch (MACDescriptionParseException e) {
                        // Ignore
                    }

                    if (projectDescriptionFile != null) {
                        InputStream stream = structureProvider.getContents(projectDescriptionFile);
                        if (stream != null) {
                            try {
                                IDEWorkbenchPlugin.getPluginWorkspace().loadProjectDescription(stream);
                            } catch (CoreException e) {
                                // Use info from the config.xml file
                            }
                        }
                    }
                }

                if (this.description != null) {
                    projectName = this.description.getName();
                } else if (macDescription != null) {
                    projectName = macDescription.name;
                    this.description = IDEWorkbenchPlugin.getPluginWorkspace().newProjectDescription(projectName);
                } else {
                    IPath path = new Path(((File)resourceDescriptionFile).getPath());
                    projectName = path.segment(path.segmentCount() - 2);
                    this.description = IDEWorkbenchPlugin.getPluginWorkspace().newProjectDescription(projectName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Returns whether the given project description file path is in the
         * default location for a project
         *
         * @param path
         *      The path to examine
         * @return Whether the given path is the default location for a project
         */
        private boolean isDefaultLocation(IPath path) {
            // The project description file must at least be within the project,
            // which is within the workspace location
            if (path.segmentCount() < 2) {
                return false;
            }
            return path.removeLastSegments(2).toFile().equals(Platform.getLocation().toFile());
        }

		/**
		 * Get the name of the project
		 *
		 * @return String
		 */
		public String getProjectName() {
			return projectName;
		}

		/**
		 * Gets the label to be used when rendering this project record in the
		 * UI.
		 *
		 * @return String the label
		 * @since 3.4
		 */
		public String getProjectLabel() {
			return projectName;
		}

		/**
		 * @return Returns the hasConflicts.
		 */
		public boolean hasConflicts() {
			return hasConflicts;
		}
	}

	// dialog store id constants
	private final static String STORE_COPY_PROJECT_ID = "WizardProjectsImportPage.STORE_COPY_PROJECT_ID"; //$NON-NLS-1$

	private final static String STORE_ARCHIVE_SELECTED = "WizardProjectsImportPage.STORE_ARCHIVE_SELECTED"; //$NON-NLS-1$

	private Text directoryPathField;

	private CheckboxTreeViewer projectsList;

	private Button copyCheckbox;

	private boolean copyFiles = false;

	private boolean lastCopyFiles = false;

	private ProjectRecord[] selectedProjects = new ProjectRecord[0];

	// Keep track of the directory that we browsed to last time
	// the wizard was invoked.
	private static String previouslyBrowsedDirectory = ""; //$NON-NLS-1$

	// Keep track of the archive that we browsed to last time
	// the wizard was invoked.
	private static String previouslyBrowsedArchive = ""; //$NON-NLS-1$

	private Button projectFromDirectoryRadio;

	private Button projectFromArchiveRadio;

	private Text archivePathField;

	private Button browseDirectoriesButton;

	private Button browseArchivesButton;

	private IProject[] wsProjects;

    // constant from WizardArchiveFileResourceImportPage1
    private static final String[] FILE_IMPORT_MASK = {
        "*.wgt;*.zip;*.tar;*.tar.gz;*.tgz", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$

	// The initial path to set
	private String initialPath;

	// The last selected path to minimize searches
	private String lastPath;
	// The last time that the file or folder at the selected path was modified
	// to mimize searches
	private long lastModified;

	/**
	 * Creates a new project creation wizard page.
	 *
	 */
	public WizardProjectsImportPage() {
		this("wizardExternalProjectsPage", null, null); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of the receiver.
	 *
	 * @param pageName
	 */
	public WizardProjectsImportPage(String pageName) {
		this(pageName,null, null);
	}

	/**
	 * More (many more) parameters.
	 *
	 * @param pageName
	 * @param initialPath
	 * @param currentSelection
	 * @since 3.5
	 */
	public WizardProjectsImportPage(String pageName,String initialPath,
			IStructuredSelection currentSelection) {
		super(pageName);
		this.initialPath = initialPath;
		setPageComplete(false);
		setTitle(DataTransferMessages.WizardProjectsImportPage_ImportProjectsTitle);
		setDescription(ImportProjectDescription);
	}

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
     * .Composite)
     */
    public void createControl(Composite parent) {

        initializeDialogUnits(parent);

        Composite workArea = new Composite(parent, SWT.NONE);
        setControl(workArea);

        workArea.setLayout(new GridLayout());
        workArea.setLayoutData(new GridData(GridData.FILL_BOTH
                | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        createProjectsRoot(workArea);
        createProjectsList(workArea);
        createOptionsGroup(workArea);
        Dialog.applyDialogFont(workArea);
    }


    @Override
    protected void createOptionsGroupButtons(Group optionsGroup) {
        copyCheckbox = new Button(optionsGroup, SWT.CHECK);
        copyCheckbox.setText(DataTransferMessages.WizardProjectsImportPage_CopyProjectsIntoWorkspace);
        copyCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        copyCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                copyFiles = copyCheckbox.getSelection();
                // need to refresh the project list as projects already
                // in the workspace directory are treated as conflicts
                // and should be hidden too
                projectsList.refresh(true);
            }
        });
    }

	/**
	 * Create the checkbox list for the found projects.
	 *
	 * @param workArea
	 */
	private void createProjectsList(Composite workArea) {

		Label title = new Label(workArea, SWT.NONE);
		title
		.setText(DataTransferMessages.WizardProjectsImportPage_ProjectsListTitle);

		Composite listComposite = new Composite(workArea, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = false;
		listComposite.setLayout(layout);

		listComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		projectsList = new CheckboxTreeViewer(listComposite, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = new PixelConverter(projectsList.getControl()).convertWidthInCharsToPixels(25);
		gridData.heightHint = new PixelConverter(projectsList.getControl()).convertHeightInCharsToPixels(10);
		projectsList.getControl().setLayoutData(gridData);
		projectsList.setContentProvider(new ITreeContentProvider() {

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java
			 * .lang.Object)
			 */
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements
			 * (java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				return getProjectRecords();
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java
			 * .lang.Object)
			 */
			public boolean hasChildren(Object element) {
				return false;
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java
			 * .lang.Object)
			 */
			public Object getParent(Object element) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {

			}

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
			 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});

		projectsList.setLabelProvider(new ProjectLabelProvider());

		projectsList.addCheckStateListener(new ICheckStateListener() {
			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged
			 * (org.eclipse.jface.viewers.CheckStateChangedEvent)
			 */
			public void checkStateChanged(CheckStateChangedEvent event) {
				ProjectRecord element = (ProjectRecord) event.getElement();
				if(element.hasConflicts) {
					projectsList.setChecked(element, false);
				}
				setPageComplete(projectsList.getCheckedElements().length > 0);
			}
		});

		projectsList.setInput(this);
		projectsList.setComparator(new ViewerComparator());
		createSelectionButtons(listComposite);
	}

	/**
	 * Create the selection buttons in the listComposite.
	 *
	 * @param listComposite
	 */
	private void createSelectionButtons(Composite listComposite) {
		Composite buttonsComposite = new Composite(listComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonsComposite.setLayout(layout);

		buttonsComposite.setLayoutData(new GridData(
				GridData.VERTICAL_ALIGN_BEGINNING));

		Button selectAll = new Button(buttonsComposite, SWT.PUSH);
		selectAll.setText(DataTransferMessages.DataTransfer_selectAll);
		selectAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (int i = 0; i < selectedProjects.length; i++) {
					if(selectedProjects[i].hasConflicts)
						projectsList.setChecked(selectedProjects[i], false);
					else
						projectsList.setChecked(selectedProjects[i], true);
				}
				setPageComplete(projectsList.getCheckedElements().length > 0);
			}
		});
		Dialog.applyDialogFont(selectAll);
		setButtonLayoutData(selectAll);

		Button deselectAll = new Button(buttonsComposite, SWT.PUSH);
		deselectAll.setText(DataTransferMessages.DataTransfer_deselectAll);
		deselectAll.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {

				projectsList.setCheckedElements(new Object[0]);
				setPageComplete(false);
			}
		});
		Dialog.applyDialogFont(deselectAll);
		setButtonLayoutData(deselectAll);

		Button refresh = new Button(buttonsComposite, SWT.PUSH);
		refresh.setText(DataTransferMessages.DataTransfer_refresh);
		refresh.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				if (projectFromDirectoryRadio.getSelection()) {
					updateProjectsList(directoryPathField.getText().trim());
				} else {
					updateProjectsList(archivePathField.getText().trim());
				}
			}
		});
		Dialog.applyDialogFont(refresh);
		setButtonLayoutData(refresh);
	}

	/**
	 * Create the area where you select the root directory for the projects.
	 *
	 * @param workArea
	 * 		Composite
	 */
	private void createProjectsRoot(Composite workArea) {

		// project specification group
		Composite projectGroup = new Composite(workArea, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 0;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project from directory radio button
		projectFromDirectoryRadio = new Button(projectGroup, SWT.RADIO);
		projectFromDirectoryRadio
		.setText(DataTransferMessages.WizardProjectsImportPage_RootSelectTitle);

		// project location entry field
		this.directoryPathField = new Text(projectGroup, SWT.BORDER);

		GridData directoryPathData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		directoryPathData.widthHint = new PixelConverter(directoryPathField).convertWidthInCharsToPixels(25);
		directoryPathField.setLayoutData(directoryPathData);

		// browse button
		browseDirectoriesButton = new Button(projectGroup, SWT.PUSH);
		browseDirectoriesButton
		.setText(DataTransferMessages.DataTransfer_browse);
		setButtonLayoutData(browseDirectoriesButton);

		// new project from archive radio button
		projectFromArchiveRadio = new Button(projectGroup, SWT.RADIO);
		projectFromArchiveRadio
		.setText(DataTransferMessages.WizardProjectsImportPage_ArchiveSelectTitle);

		// project location entry field
		archivePathField = new Text(projectGroup, SWT.BORDER);

		GridData archivePathData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		archivePathData.widthHint = new PixelConverter(archivePathField).convertWidthInCharsToPixels(25);
		archivePathField.setLayoutData(archivePathData); // browse button
		browseArchivesButton = new Button(projectGroup, SWT.PUSH);
		browseArchivesButton.setText(DataTransferMessages.DataTransfer_browse);
		setButtonLayoutData(browseArchivesButton);

		projectFromDirectoryRadio.setSelection(true);
		archivePathField.setEnabled(false);
		browseArchivesButton.setEnabled(false);

		browseDirectoriesButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetS
			 * elected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				handleLocationDirectoryButtonPressed();
			}

		});

		browseArchivesButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				handleLocationArchiveButtonPressed();
			}

		});

		directoryPathField.addTraverseListener(new TraverseListener() {

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse
			 * .swt.events.TraverseEvent)
			 */
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					updateProjectsList(directoryPathField.getText().trim());
				}
			}

		});

		directoryPathField.addFocusListener(new FocusAdapter() {

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt
			 * .events.FocusEvent)
			 */
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
				updateProjectsList(directoryPathField.getText().trim());
			}

		});

		archivePathField.addTraverseListener(new TraverseListener() {

			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse
			 * .swt.events.TraverseEvent)
			 */
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					updateProjectsList(archivePathField.getText().trim());
				}
			}

		});

		archivePathField.addFocusListener(new FocusAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt
			 * .events.FocusEvent)
			 */
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
				updateProjectsList(archivePathField.getText().trim());
			}
		});

		projectFromDirectoryRadio.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				directoryRadioSelected();
			}
		});

		projectFromArchiveRadio.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				archiveRadioSelected();
			}
		});
	}

	private void archiveRadioSelected() {
		if (projectFromArchiveRadio.getSelection()) {
			directoryPathField.setEnabled(false);
			browseDirectoriesButton.setEnabled(false);
			archivePathField.setEnabled(true);
			browseArchivesButton.setEnabled(true);
			updateProjectsList(archivePathField.getText());
			archivePathField.setFocus();
			copyCheckbox.setSelection(true);
			copyCheckbox.setEnabled(false);
		}
	}

	private void directoryRadioSelected() {
		if (projectFromDirectoryRadio.getSelection()) {
			directoryPathField.setEnabled(true);
			browseDirectoriesButton.setEnabled(true);
			archivePathField.setEnabled(false);
			browseArchivesButton.setEnabled(false);
			updateProjectsList(directoryPathField.getText());
			directoryPathField.setFocus();
			copyCheckbox.setEnabled(true);
			copyCheckbox.setSelection(copyFiles);
		}
	}

	/*
	 * (non-Javadoc) Method declared on IDialogPage. Set the focus on path
	 * fields when page becomes visible.
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && this.projectFromDirectoryRadio.getSelection()) {
			this.directoryPathField.setFocus();
		}
		if (visible && this.projectFromArchiveRadio.getSelection()) {
			this.archivePathField.setFocus();
		}
	}

	/**
	 * Update the list of projects based on path. Method declared public only
	 * for test suite.
	 *
	 * @param path
	 */
	public void updateProjectsList(final String path) {
		// on an empty path empty selectedProjects
		if (path == null || path.length() == 0) {
			setMessage(ImportProjectDescription);
			selectedProjects = new ProjectRecord[0];
			projectsList.refresh(true);
			projectsList.setCheckedElements(selectedProjects);
			setPageComplete(projectsList.getCheckedElements().length > 0);
			lastPath = path;
			return;
		}

		final File directory = new File(path);
		long modified = directory.lastModified();
		if (path.equals(lastPath) && lastModified == modified && lastCopyFiles == copyFiles) {
			// since the file/folder was not modified and the path did not
			// change, no refreshing is required
			return;
		}

		lastPath = path;
		lastModified = modified;
		lastCopyFiles = copyFiles;

		// We can't access the radio button from the inner class so get the
		// status beforehand
		final boolean dirSelected = this.projectFromDirectoryRadio.getSelection();
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				/*
				 * (non-Javadoc)
				 *
				 * @see
				 * org.eclipse.jface.operation.IRunnableWithProgress#run(org
				 * .eclipse.core.runtime.IProgressMonitor)
				 */
				public void run(IProgressMonitor monitor) {

					monitor.beginTask(DataTransferMessages.WizardProjectsImportPage_SearchingMessage, 100);
					selectedProjects = new ProjectRecord[0];
					Collection<ProjectRecord> files = new ArrayList<ProjectRecord>();
					monitor.worked(10);
					if (!dirSelected
							&& ArchiveFileManipulations.isTarFile(path)) {
						TarFile sourceTarFile = getSpecifiedTarSourceFile(path);
						if (sourceTarFile == null) {
							return;
						}

						structureProvider = new TarLeveledStructureProvider(sourceTarFile);
						Object child = structureProvider.getRoot();

						if (!collectProjectFilesFromProvider(files, child, 0, monitor)) {
							return;
						}
						monitor.worked(50);
						monitor.subTask(DataTransferMessages.WizardProjectsImportPage_ProcessingMessage);
						selectedProjects = (ProjectRecord[]) files.toArray(new ProjectRecord[files.size()]);
					} else if (!dirSelected
							&& ArchiveFileManipulations.isZipFile(path)) {
						ZipFile sourceFile = getSpecifiedZipSourceFile(path);
						if (sourceFile == null) {
							return;
						}
						structureProvider = new ZipLeveledStructureProvider(
								sourceFile);
						Object child = structureProvider.getRoot();

						if (!collectProjectFilesFromProvider(files, child, 0, monitor)) {
							return;
						}
						monitor.worked(50);
						monitor.subTask(DataTransferMessages.WizardProjectsImportPage_ProcessingMessage);
						selectedProjects = (ProjectRecord[]) files.toArray(new ProjectRecord[files.size()]);
					} else if (dirSelected && directory.isDirectory()) {

						if (!collectProjectFilesFromDirectory(files, directory,
								null, monitor)) {
							return;
						}
						monitor.worked(50);
						monitor.subTask(DataTransferMessages.WizardProjectsImportPage_ProcessingMessage);
						selectedProjects = (ProjectRecord[]) files.toArray(new ProjectRecord[files.size()]);
					} else {
						monitor.worked(60);
					}
					monitor.done();
				}

			});
		} catch (InvocationTargetException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		} catch (InterruptedException e) {
			// Nothing to do if the user interrupts.
		}

		projectsList.refresh(true);
		ProjectRecord[] projects = getProjectRecords();
		boolean displayWarning = false;
		for (int i = 0; i < projects.length; i++) {
			if(projects[i].hasConflicts) {
				displayWarning = true;
				projectsList.setGrayed(projects[i], true);
			}else {
				projectsList.setChecked(projects[i], true);
			}
		}

		if (displayWarning) {
			setMessage(
					ImportProjectWarning,
					WARNING);
		} else {
			setMessage(ImportProjectDescription);
		}
		setPageComplete(projectsList.getCheckedElements().length > 0);
		if(selectedProjects.length == 0) {
			setMessage(
					DataTransferMessages.WizardProjectsImportPage_noProjectsToImport,
					WARNING);
		}
	}

	/**
	 * Answer a handle to the zip file currently specified as being the source.
	 * Return null if this file does not exist or is not of valid format.
	 */
	private ZipFile getSpecifiedZipSourceFile(String fileName) {
		if (fileName.length() == 0) {
			return null;
		}

		try {
			return new ZipFile(fileName);
		} catch (ZipException e) {
			displayErrorDialog(DataTransferMessages.ZipImport_badFormat);
		} catch (IOException e) {
			displayErrorDialog(DataTransferMessages.ZipImport_couldNotRead);
		}

		archivePathField.setFocus();
		return null;
	}

	/**
	 * Answer a handle to the zip file currently specified as being the source.
	 * Return null if this file does not exist or is not of valid format.
	 */
	private TarFile getSpecifiedTarSourceFile(String fileName) {
		if (fileName.length() == 0) {
			return null;
		}

		try {
			return new TarFile(fileName);
		} catch (TarException e) {
			displayErrorDialog(DataTransferMessages.TarImport_badFormat);
		} catch (IOException e) {
			displayErrorDialog(DataTransferMessages.ZipImport_couldNotRead);
		}

		archivePathField.setFocus();
		return null;
	}

	/**
	 * Display an error dialog with the specified message.
	 *
	 * @param message
	 * 		the error message
	 */
	protected void displayErrorDialog(String message) {
		MessageDialog.open(MessageDialog.ERROR, getContainer().getShell(),
				getErrorDialogTitle(), message, SWT.SHEET);
	}

	/**
	 * Get the title for an error dialog. Subclasses should override.
	 */
	protected String getErrorDialogTitle() {
		return IDEWorkbenchMessages.WizardExportPage_internalErrorTitle;
	}

    /**
     * Collect the list of config.xml files that are under directory into files.
     *
     * @param files
     * @param directory
     * @param directoriesVisited
     *            Set of canonical paths of directories, used as recursion guard
     * @param monitor
     *            The monitor to report to
     * @return boolean <code>true</code> if the operation was completed.
     */
    private boolean collectProjectFilesFromDirectory(
            Collection<ProjectRecord> files, File directory,
            Set<String> directoriesVisited, IProgressMonitor monitor) {

        if (monitor.isCanceled()) {
            return false;
        }
        monitor.subTask(NLS.bind(
                DataTransferMessages.WizardProjectsImportPage_CheckingMessage,
                directory.getPath()));
        File[] contents = directory.listFiles();
        if (contents == null) {
            return false;
        }

        // Initialize recursion guard for recursive symbolic links
        if (directoriesVisited == null) {
            directoriesVisited = new HashSet<String>();
            try {
                directoriesVisited.add(directory.getCanonicalPath());
            } catch (IOException exception) {

                StatusManager.getManager().handle(
                        StatusUtil.newStatus(IStatus.ERROR,
                                exception.getLocalizedMessage(), exception));
            }
        }

        Path directoryPath = new Path(directory.getPath());
        File file = new File(directoryPath.append(MAC_DESCRIPTION_FILE).toOSString());
        if (file.isFile()) {
            File projectFile = new File(directoryPath.append(IProjectDescription.DESCRIPTION_FILE_NAME).toOSString());
            if (!projectFile.isFile()) {
                projectFile = null;
            }
            files.add(new ProjectRecord(file, projectFile));
            // don't search sub-directories since we can't have nested
            // projects
            return true;
        }

        // no project description found, so recurse into sub-directories
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].isDirectory()
                    && !contents[i].getName().equals(METADATA_FOLDER)) {
                try {
                    String canonicalPath = contents[i].getCanonicalPath();
                    if (!directoriesVisited.add(canonicalPath)) {
                        // already been here --> do not recurse
                        continue;
                    }
                } catch (IOException exception) {
                    StatusManager
                            .getManager()
                            .handle(StatusUtil.newStatus(IStatus.ERROR,
                                    exception.getLocalizedMessage(), exception));

                }
                collectProjectFilesFromDirectory(files, contents[i],
                        directoriesVisited, monitor);
            }
        }
        return true;
    }

    /**
     * Collect the list of config.xml files that are under directory into files.
     *
     * @param files
     * @param monitor
     *            The monitor to report to
     * @return boolean <code>true</code> if the operation was completed.
     */
    private boolean collectProjectFilesFromProvider(
            Collection<ProjectRecord> files, Object entry, int level,
            IProgressMonitor monitor) {

        if (monitor.isCanceled()) {
            return false;
        }
        monitor.subTask(NLS.bind(
                DataTransferMessages.WizardProjectsImportPage_CheckingMessage,
                structureProvider.getLabel(entry)));
        List children = structureProvider.getChildren(entry);
        if (children == null) {
            return false;
        }
        Iterator childrenEnum = children.iterator();
        Object file = null;
        Object projectFile = null;
        while (childrenEnum.hasNext()) {
            Object child = childrenEnum.next();
            String elementLabel = structureProvider.getLabel(child);
            if (elementLabel.equals(MAC_DESCRIPTION_FILE)) {
                file = child;
            } else if (elementLabel.equals(IProjectDescription.DESCRIPTION_FILE_NAME)) {
                projectFile = child;
            }
        }
        if (file != null) {
            files.add(new ProjectRecord(file, projectFile));
        }

        // no project description found, so recurse into sub-directories
        childrenEnum = children.iterator();
        while (childrenEnum.hasNext()) {
            Object child = childrenEnum.next();
            String elementLabel = structureProvider.getLabel(child);
            if (structureProvider.isFolder(child)) {
                collectProjectFilesFromProvider(files, child, level + 1, monitor);
            }
        }

        return true;
    }

	/**
	 * The browse button has been selected. Select the location.
	 */
	protected void handleLocationDirectoryButtonPressed() {

		DirectoryDialog dialog = new DirectoryDialog(directoryPathField
				.getShell(), SWT.SHEET);
		dialog
		.setMessage(DataTransferMessages.WizardProjectsImportPage_SelectDialogTitle);

		String dirName = directoryPathField.getText().trim();
		if (dirName.length() == 0) {
			dirName = previouslyBrowsedDirectory;
		}

		if (dirName.length() == 0) {
			dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace()
					.getRoot().getLocation().toOSString());
		} else {
			File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(new Path(dirName).toOSString());
			}
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			previouslyBrowsedDirectory = selectedDirectory;
			directoryPathField.setText(previouslyBrowsedDirectory);
			updateProjectsList(selectedDirectory);
		}

	}

	/**
	 * The browse button has been selected. Select the location.
	 */
	protected void handleLocationArchiveButtonPressed() {

		FileDialog dialog = new FileDialog(archivePathField.getShell(), SWT.SHEET);
		dialog.setFilterExtensions(FILE_IMPORT_MASK);
		dialog.setText(DataTransferMessages.WizardProjectsImportPage_SelectArchiveDialogTitle);

		String fileName = archivePathField.getText().trim();
		if (fileName.length() == 0) {
			fileName = previouslyBrowsedArchive;
		}

		if (fileName.length() == 0) {
			dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace()
					.getRoot().getLocation().toOSString());
		} else {
			File path = new File(fileName).getParentFile();
			if (path != null && path.exists()) {
				dialog.setFilterPath(path.toString());
			}
		}

		String selectedArchive = dialog.open();
		if (selectedArchive != null) {
			previouslyBrowsedArchive = selectedArchive;
			archivePathField.setText(previouslyBrowsedArchive);
			updateProjectsList(selectedArchive);
		}

	}

	/**
	 * Create the selected projects
	 *
	 * @return boolean <code>true</code> if all project creations were
	 * 	successful.
	 */
	public boolean createProjects() {
		//		saveWidgetValues();
		final Object[] selected = projectsList.getCheckedElements();
		createdProjects = new ArrayList<IProject>();
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask("", selected.length); //$NON-NLS-1$
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}
					for (int i = 0; i < selected.length; i++) {
						createExistingProject((ProjectRecord) selected[i],
								new SubProgressMonitor(monitor, 1));
					}
				} finally {
					monitor.done();
				}
			}
		};
		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;

		} catch (InvocationTargetException e) {
			// one of the steps resulted in a core exception
			Throwable t = e.getTargetException();
			String message = DataTransferMessages.WizardExternalProjectImportPage_errorMessage;
			IStatus status;
			if (t instanceof CoreException) {
				status = ((CoreException) t).getStatus();
			} else {
				status = new Status(IStatus.ERROR,
						IDEWorkbenchPlugin.IDE_WORKBENCH, 1, message, t);
			}
			ErrorDialog.openError(getShell(), message, null, status);
			return false;
		}
		ArchiveFileManipulations.closeStructureProvider(structureProvider,
				getShell());

		return true;
	}

	List<IProject> createdProjects;

	/**
	 * Performs clean-up if the user cancels the wizard without doing anything
	 */
	public void performCancel() {
		ArchiveFileManipulations.closeStructureProvider(structureProvider,
				getShell());
	}

	/**
	 * Create the project described in record. If it is successful return true.
	 *
	 * @param record
	 * @return boolean <code>true</code> if successful
	 * @throws InterruptedException
	 */
	private boolean createExistingProject(final ProjectRecord record,
			IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		String projectName = record.getProjectName();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(projectName);
		createdProjects.add(project);

		if (!(record.resourceDescriptionFile instanceof File)) {
			// import from archive
			ImportOperation operation = new ImportOperation(project
					.getFullPath(), structureProvider.getRoot(), structureProvider, null);

			operation.setContext(getShell());
			operation.run(monitor);
			try {
			    project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 70));
			} catch (CoreException e) {
			    throw new InvocationTargetException(e);
			} finally {
			    monitor.done();
			}
			prepareProject(record);
			return true;
		}

		// import from file system
		File importSource = null;
		if (copyFiles) {
			// import project from location copying files - use default project
			// location for this workspace
			URI locationURI = new File(((File)record.resourceDescriptionFile).getParent()).toURI();
			if (locationURI != null) {
				IStatus result = ResourcesPlugin.getWorkspace().validateProjectLocationURI(project,
						locationURI);
				if (!result.isOK()) {
					throw new InvocationTargetException(new CoreException(result));
				}

				importSource = new File(locationURI);
				IProjectDescription desc = workspace.newProjectDescription(projectName);
				desc.setBuildSpec(record.description.getBuildSpec());
				desc.setComment(record.description.getComment());
				desc.setDynamicReferences(record.description.getDynamicReferences());
				desc.setNatureIds(record.description.getNatureIds());
				desc.setReferencedProjects(record.description.getReferencedProjects());
				record.description = desc;
			}
		} else {
		    IPath locationPath = new Path(((File)record.resourceDescriptionFile).getParent());
		    record.description.setLocation(locationPath);
		}

		try {
			monitor
			.beginTask(
					DataTransferMessages.WizardProjectsImportPage_CreateProjectsTask,
					100);
			project.create(record.description, new SubProgressMonitor(monitor,
					30));
			project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 70));
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
		}

		// import operation to import project files if copy checkbox is selected
		if (copyFiles && importSource != null) {
			List filesToImport = FileSystemStructureProvider.INSTANCE
					.getChildren(importSource);
			ImportOperation operation = new ImportOperation(project
					.getFullPath(), importSource,
					FileSystemStructureProvider.INSTANCE, this, filesToImport);
			operation.setContext(getShell());
			operation.setOverwriteResources(true); // need to overwrite
			// .project, .classpath
			// files
			operation.setCreateContainerStructure(false);
			operation.run(monitor);
		}

		prepareProject(record);
		return true;
	}

	/**
	 * The <code>WizardDataTransfer</code> implementation of this
	 * <code>IOverwriteQuery</code> method asks the user whether the existing
	 * resource at the given path should be overwritten.
	 *
	 * @param pathString
	 * @return the user's reply: one of <code>"YES"</code>, <code>"NO"</code>,
	 * 	<code>"ALL"</code>, or <code>"CANCEL"</code>
	 */
	public String queryOverwrite(String pathString) {

		Path path = new Path(pathString);

		String messageString;
		// Break the message up if there is a file name and a directory
		// and there are at least 2 segments.
		if (path.getFileExtension() == null || path.segmentCount() < 2) {
			messageString = NLS.bind(
					IDEWorkbenchMessages.WizardDataTransfer_existsQuestion,
					pathString);
		} else {
			messageString = NLS
					.bind(
							IDEWorkbenchMessages.WizardDataTransfer_overwriteNameAndPathQuestion,
							path.lastSegment(), path.removeLastSegments(1)
							.toOSString());
		}

		final MessageDialog dialog = new MessageDialog(getContainer()
				.getShell(), IDEWorkbenchMessages.Question, null,
				messageString, MessageDialog.QUESTION, new String[] {
			IDialogConstants.YES_LABEL,
			IDialogConstants.YES_TO_ALL_LABEL,
			IDialogConstants.NO_LABEL,
			IDialogConstants.NO_TO_ALL_LABEL,
			IDialogConstants.CANCEL_LABEL }, 0) {
			protected int getShellStyle() {
				return super.getShellStyle() | SWT.SHEET;
			}
		};
		String[] response = new String[] { YES, ALL, NO, NO_ALL, CANCEL };
		// run in syncExec because callback is from an operation,
		// which is probably not running in the UI thread.
		getControl().getDisplay().syncExec(new Runnable() {
			public void run() {
				dialog.open();
			}
		});
		return dialog.getReturnCode() < 0 ? CANCEL : response[dialog
		                                                      .getReturnCode()];
	}

	/**
	 * Method used for test suite.
	 *
	 * @return Button the Import from Directory RadioButton
	 */
	public Button getProjectFromDirectoryRadio() {
		return projectFromDirectoryRadio;
	}

	/**
	 * Method used for test suite.
	 *
	 * @return CheckboxTreeViewer the viewer containing all the projects found
	 */
	public CheckboxTreeViewer getProjectsList() {
		return projectsList;
	}

	/**
	 * Retrieve all the projects in the current workspace.
	 *
	 * @return IProject[] array of IProject in the current workspace
	 */

	private IProject[] getProjectsInWorkspace() {
		if (wsProjects == null) {
			wsProjects = IDEWorkbenchPlugin.getPluginWorkspace().getRoot()
					.getProjects();
		}
		return wsProjects;
	}

	/**
	 * Get the array of  project records that can be imported from the
	 * source workspace or archive, selected by the user. If a project with the
	 * same name exists in both the source workspace and the current workspace,
	 * then the hasConflicts flag would be set on that project record.
	 *
	 * Method declared public for test suite.
	 *
	 * @return ProjectRecord[] array of projects that can be imported into the
	 * 	workspace
	 */
	public ProjectRecord[] getProjectRecords() {
		List<ProjectRecord> projectRecords = new ArrayList<ProjectRecord>();
		for (int i = 0; i < selectedProjects.length; i++) {
			if ( (isProjectInWorkspacePath(selectedProjects[i].getProjectName()) && copyFiles)||
					isProjectInWorkspace(selectedProjects[i].getProjectName())) {
				selectedProjects[i].hasConflicts = true;
			}
			projectRecords.add(selectedProjects[i]);
		}
		return (ProjectRecord[]) projectRecords.toArray(new ProjectRecord[projectRecords.size()]);
	}

	/**
	 * Determine if there is a directory with the project name in the workspace path.
	 *
	 * @param projectName the name of the project
	 * @return true if there is a directory with the same name of the imported project
	 */
	private boolean isProjectInWorkspacePath(String projectName){
		final IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
		IPath wsPath = workspace.getRoot().getLocation();
		IPath localProjectPath = wsPath.append(projectName);
		return localProjectPath.toFile().exists();
	}

	/**
	 * Determine if the project with the given name is in the current workspace.
	 *
	 * @param projectName
	 * 		String the project name to check
	 * @return boolean true if the project with the given name is in this
	 * 	workspace
	 */
	private boolean isProjectInWorkspace(String projectName) {
		if (projectName == null) {
			return false;
		}
		IProject[] workspaceProjects = getProjectsInWorkspace();
		for (int i = 0; i < workspaceProjects.length; i++) {
			if (projectName.equals(workspaceProjects[i].getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Use the dialog store to restore widget values to the values that they
	 * held last time this wizard was used to completion, or alternatively,
	 * if an initial path is specified, use it to select values.
	 *
	 * Method declared public only for use of tests.
	 */
	public void restoreWidgetValues() {

		// First, check to see if we have resore settings, and
		// take care of the checkbox
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			// checkbox
			copyFiles = settings.getBoolean(STORE_COPY_PROJECT_ID);
			copyCheckbox.setSelection(copyFiles);
			lastCopyFiles = copyFiles;
		}

		// Second, check to see if we don't have an initial path,
		// and if we do have restore settings.  If so, set the
		// radio selection properly to restore settings

		if (initialPath==null && settings!=null)
		{
			// radio selection
			boolean archiveSelected = settings
					.getBoolean(STORE_ARCHIVE_SELECTED);
			projectFromDirectoryRadio.setSelection(!archiveSelected);
			projectFromArchiveRadio.setSelection(archiveSelected);
			if (archiveSelected) {
				archiveRadioSelected();
			} else {
				directoryRadioSelected();
			}
		}
		// Third, if we do have an initial path, set the proper
		// path and radio buttons to the initial value. Move
		// cursor to the end of the path so user can see the
		// most relevant part (directory / archive name)
		else if (initialPath != null) {
			boolean dir = new File(initialPath).isDirectory();

			projectFromDirectoryRadio.setSelection(dir);
			projectFromArchiveRadio.setSelection(!dir);

			if (dir) {
				directoryPathField.setText(initialPath);
				directoryPathField.setSelection(initialPath.length());
				directoryRadioSelected();
			} else {
				archivePathField.setText(initialPath);
				archivePathField.setSelection(initialPath.length());
				archiveRadioSelected();
			}
		}
	}

	/**
	 * Since Finish was pressed, write widget values to the dialog store so that
	 * they will persist into the next invocation of this wizard page.
	 *
	 * Method declared public only for use of tests.
	 */
	public void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			settings.put(STORE_COPY_PROJECT_ID, copyCheckbox.getSelection());

			settings.put(STORE_ARCHIVE_SELECTED, projectFromArchiveRadio
					.getSelection());
		}
	}

    public void prepareProject(ProjectRecord record) throws InvocationTargetException {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(record.getProjectName());
        IFacetedProject facetedProject;
        try {
            facetedProject = ProjectFacetsManager.create(project, true, null);

            // Install JavaScript and MAC facets
            Set<IProjectFacet> facets = new HashSet<>(facetedProject.getFixedProjectFacets());

            IProjectFacet jsFacet = ProjectFacetsManager.getProjectFacet("wst.jsdt.web");
            facets.add(jsFacet);
            facetedProject.installProjectFacet(jsFacet.getDefaultVersion(), null, null);

            facetedProject.setFixedProjectFacets(facets);
            IProjectFacet macFacet = ProjectFacetsManager.getProjectFacet("com.conwet.applicationmashup.mac");
            facetedProject.installProjectFacet(macFacet.getDefaultVersion(), null, null);

            facets.add(jsFacet);
            facets.add(macFacet);
            facetedProject.setFixedProjectFacets(facets);

            // Install widget or operator nature
            if (record.macDescription != null) {
                switch (record.macDescription.type) {
                case "widget":
                    ProjectSupport.addNature(project, WidgetProjectNature.NATURE_ID);
                    break;
                case "operator":
                    ProjectSupport.addNature(project, OperatorProjectNature.NATURE_ID);
                }
            }
        } catch (CoreException e) {
            throw new InvocationTargetException(e);
        }
    }

	/**
	 * Method used for test suite.
	 *
	 * @return Button copy checkbox
	 */
	public Button getCopyCheckbox() {
		return copyCheckbox;
	}

    @Override
    public void handleEvent(Event event) {
    }

    @Override
    protected boolean allowNewContainerName() {
        return true;
    }
}
