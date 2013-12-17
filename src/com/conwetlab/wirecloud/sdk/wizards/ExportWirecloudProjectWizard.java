package com.conwetlab.wirecloud.sdk.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.datatransfer.ZipFileExportWizard;

public class ExportWirecloudProjectWizard extends Wizard implements IExportWizard {

	private ZipFileExportWizard wizard;
	
	public ExportWirecloudProjectWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.wizard = new ZipFileExportWizard();
		this.wizard.init(workbench, selection);
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		dialog.open();
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
