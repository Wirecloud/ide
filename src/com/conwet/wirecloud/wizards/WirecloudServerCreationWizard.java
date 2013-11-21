package com.conwet.wirecloud.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.ui.wizard.WizardFragment;

public class WirecloudServerCreationWizard extends
WizardFragment {

	@Override
	public List getChildFragments() {
		List list = new ArrayList<WizardFragment>();
		list.add(new ServerSelectionPage());
		list.add(new AuthenticationWizardPage());
		return list;
	}

		

}
