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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.ui.wizard.WizardFragment;


public class WirecloudServerCreationWizard extends
WizardFragment {
	private ServerSelectionPage serverSelectionPage;
	private AuthenticationWizardPage authenticationWizardPage;

	public WirecloudServerCreationWizard() {
		serverSelectionPage = new ServerSelectionPage();
		authenticationWizardPage = new AuthenticationWizardPage();
	}


	@Override
	public List<WizardFragment> getChildFragments() {
		List<WizardFragment> list = new ArrayList<WizardFragment>();
		list.add(this.serverSelectionPage);
		list.add(this.authenticationWizardPage);
		setComplete(false);
		return list;
	}	

}
