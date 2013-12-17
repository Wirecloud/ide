package com.conwetlab.wirecloud.sdk.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.datatransfer.ZipFileImportWizard;

public class ImportWirecloudProjectWizard extends Wizard implements IImportWizard {

	public ImportWirecloudProjectWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		 ZipFileImportWizard wizard = new ZipFileImportWizard();
		 wizard.init(workbench, selection);
		 WizardDialog dialog = new WizardDialog(getShell(), wizard);
		 dialog.open();
	}

	public boolean performFinish() {
		return true;
	}

}
