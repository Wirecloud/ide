package com.conwetlab.wirecloud.sdk.wizards.retreatment;

import org.eclipse.jface.wizard.Wizard;


import com.conwetlab.wirecloud.sdk.wapi.WirecloudAPI;


public class WirecloudRetreatmentWizardII extends Wizard {

	protected RetreatmentWizardIIPageOne one;

	private WirecloudAPI wirecloudAPI;

	public WirecloudRetreatmentWizardII(WirecloudAPI wapi) {
		super();
		setNeedsProgressMonitor(true);
		this.wirecloudAPI = wapi;
	}

	@Override
	public void addPages() {
		one = new RetreatmentWizardIIPageOne(wirecloudAPI);
		addPage(one);
	}

	@Override
	public boolean performFinish() {
		wirecloudAPI.deleteCatalogueResources();
		return true;
	}
} 
