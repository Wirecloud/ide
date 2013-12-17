package com.conwet.wirecloud.wizards;

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
