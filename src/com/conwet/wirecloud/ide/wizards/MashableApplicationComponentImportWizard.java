/*
 *  Copyright (c) 2014 CoNWeT Lab., Universidad Politécnica de Madrid
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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.WorkbenchPlugin;

public class MashableApplicationComponentImportWizard extends Wizard implements
        IImportWizard {

    private WizardProjectsImportPage mainPage;

    public MashableApplicationComponentImportWizard() {
        IDialogSettings workbenchSettings = WorkbenchPlugin.getDefault().getDialogSettings();
        IDialogSettings section = workbenchSettings
                .getSection("MashableApplicationComponentImportWizard");//$NON-NLS-1$
        if (section == null) {
            section = workbenchSettings.addNewSection("MashableApplicationComponentImportWizard");//$NON-NLS-1$
        }
        setDialogSettings(section);
    }

    @Override
    public boolean performCancel() {
        mainPage.performCancel();
        return true;
    }

    @Override
    public boolean performFinish() {
        return mainPage.createProjects();
    }

    @Override
    public void addPages() {
        super.addPages();
        mainPage = new WizardProjectsImportPage();
        addPage(mainPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("Import page");
        setNeedsProgressMonitor(true);
    }

}
