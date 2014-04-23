package com.conwet.wirecloud.ide.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class MashableApplicationComponentImportWizard extends Wizard implements IImportWizard {

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		MashableApplicationComponentImport wizard = new MashableApplicationComponentImport();
		wizard.init(workbench, selection);
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		dialog.open();

	}

}
