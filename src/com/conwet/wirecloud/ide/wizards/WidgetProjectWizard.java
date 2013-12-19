/*
 *  Copyright (c) 2013 CoNWeT Lab., Universidad Politécnica de Madrid
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

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.conwet.wirecloud.ide.natures.WidgetProjectNature;
import com.conwet.wirecloud.ide.projects.ProjectSupport;
import com.conwet.wirecloud.ide.projects.WidgetProjectSupport;

public class WidgetProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	private WizardNewProjectCreationPage pageOne;
	private static final String WIZARD_NAME = "Wirecloud Widget Project Assistant";	 //$NON-NLS-1$
	private static final String PAGE_NAME = "Widget Project Assistant - First Page"; //$NON-NLS-1$
	
	private IConfigurationElement configurationElement;

	
	public WidgetProjectWizard() {
		setWindowTitle(WIZARD_NAME);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addPages() {
	    super.addPages();

	    this.pageOne = new WizardNewProjectCreationPage(PAGE_NAME);
	    this.pageOne.setTitle(Messages.WidgetProjectWizard_WidgetProject);
	    this.pageOne.setDescription(Messages.WidgetProjectWizard_Description);

	    addPage(this.pageOne);
	}


	@Override
	public boolean performFinish() {
		String name = this.pageOne.getProjectName();
		URI location = null;
		
		if (!this.pageOne.useDefaults()) {
			location = this.pageOne.getLocationURI();
		} // else location == null
		
		ProjectSupport project = new WidgetProjectSupport();
		project.createProject(name, location, WidgetProjectNature.NATURE_ID);
		
		// For launching a specific perspective when it creates the project // 
		BasicNewProjectResourceWizard.updatePerspective(this.configurationElement);
		
		return true;
	}

	// For launching a specific perspective when it creates the project // 
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		
			this.configurationElement = config;
	}

}
