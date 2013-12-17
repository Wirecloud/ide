package com.conwetlab.wirecloud.sdk.wizards.deployment;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DeploymentWizardPageOne extends WizardPage {
	private Text sourceFileField;
	private Composite container;

	public DeploymentWizardPageOne() {
		super("selectFiles");
		setTitle("Select files");
		setDescription("Select a .wgt or a .zip file to be deployed in Wirecloud");
	}


	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		Label label1 = new Label(container, SWT.NULL);
		label1.setText("Source File");
		sourceFileField = new Text(container, SWT.BORDER | SWT.SINGLE);
		sourceFileField.setText("");
		sourceFileField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!sourceFileField.getText().isEmpty()) {
					setPageComplete(true);
				}
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		sourceFileField.setLayoutData(gd);

		// Browse button //
		final Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseForSourceFile();
			}
		});
		button.setText("Browse...");

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	public String getSourceFileField() {
		return sourceFileField.getText();
	}


	protected void browseForSourceFile() {
		IPath path = browse(getSourceLocation(), false);
		if (path == null)
			return;
		IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		if (rootLoc.isPrefixOf(path))
			path = path.setDevice(null).removeFirstSegments(
					rootLoc.segmentCount());
		sourceFileField.setText(path.toOSString());
		setPageComplete(true);

	}

	private IPath browse(IPath path, boolean mustExist) {
		FileDialog dialog = new FileDialog(getShell(), mustExist ? SWT.OPEN
				: SWT.SAVE);
		if (path != null) {
			if (path.segmentCount() > 1)
				dialog.setFilterPath(path.removeLastSegments(1).toOSString());
			if (path.segmentCount() > 0)
				dialog.setFileName(path.lastSegment());
		}
		String result = dialog.open();
		if (result == null)
			return null;
		return new Path(result);
	}



	public IPath getSourceLocation() {
		String text = sourceFileField.getText().trim();
		if (text.length() == 0)
			return null;
		IPath path = new Path(text);
		if (!path.isAbsolute())
			path = ResourcesPlugin.getWorkspace().getRoot().getLocation()
			.append(path);
		return path;
	}
}
