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
