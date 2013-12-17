package com.conwetlab.wirecloud.sdk.wizards;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.conwetlab.wirecloud.sdk.natures.WidgetProjectNature;
import com.conwetlab.wirecloud.sdk.projects.EmptyProjectSupport;
import com.conwetlab.wirecloud.sdk.projects.ProjectSupport;

public class EmptyWidgetProjectWizard extends Wizard implements INewWizard {
	
	private WizardNewProjectCreationPage pageOne;
	private static final String WIZARD_NAME = "Wirecloud Empty Widget Project Assistant";	 //$NON-NLS-1$
	private static final String PAGE_NAME = "Empty Widget Project Assistant - First Page"; //$NON-NLS-1$
	
	private IConfigurationElement configurationElement;

	public EmptyWidgetProjectWizard() {
		setWindowTitle(WIZARD_NAME);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.addPages();

	    this.pageOne = new WizardNewProjectCreationPage(PAGE_NAME);
	    this.pageOne.setTitle(Messages.EmptyWidgetProjectWizard_EmptyWidgetProject);
	    this.pageOne.setDescription(Messages.EmptyWidgetProjectWizard_Description);

	    addPage(this.pageOne);

	}

	@Override
	public boolean performFinish() {
		String name = this.pageOne.getProjectName();
		URI location = null;
		
		if (!this.pageOne.useDefaults()) {
			location = this.pageOne.getLocationURI();
		} // else location == null

		ProjectSupport project = new EmptyProjectSupport();
		project.createProject(name, location, WidgetProjectNature.NATURE_ID);
		
		// For launching a specific perspective when it creates the project // 
		BasicNewProjectResourceWizard.updatePerspective(this.configurationElement);
		
		return true;
	}
	
	// For launching a specific perspective when it creates the project // 
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		
			this.configurationElement = config;
	}

}
