package com.conwetlab.wirecloud.sdk.wizards.retreatment;

import java.net.MalformedURLException;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

import com.conwetlab.wirecloud.sdk.wapi.WirecloudAPI;
import com.conwetlab.wirecloud.sdk.wizards.conection_manager.AuthenticationWizardPage;
import com.conwetlab.wirecloud.sdk.wizards.conection_manager.ServerSelectionPage;


public class WirecloudRetreatmentWizardI extends Wizard {

	protected ServerSelectionPage one;
	protected AuthenticationWizardPage two;
	protected RetreatmentWizardIIPageOne three;

	private String currentWirecloudAPIURL = "";
	private WirecloudAPI wirecloudAPI;

	public WirecloudRetreatmentWizardI() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new ServerSelectionPage("Retreat");
		two = new AuthenticationWizardPage("Retreat");
		addPage(one);
		addPage(two);
	}

	@Override
	public boolean performFinish() {
		WizardDialog dialog = new WizardDialog(getShell(), new WirecloudRetreatmentWizardII(wirecloudAPI));
		dialog.open();
		return true;
	}

	public WirecloudAPI getWirecloudAPI() {
		if (!one.getUrlText().equals(currentWirecloudAPIURL)) {
			currentWirecloudAPIURL = one.getUrlText();
			try {
				wirecloudAPI = new WirecloudAPI(one.getUrlText());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wirecloudAPI;
	}
} 
