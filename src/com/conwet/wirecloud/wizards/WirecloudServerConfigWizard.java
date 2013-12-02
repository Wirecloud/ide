package com.conwet.wirecloud.wizards;

import java.net.MalformedURLException;


import org.eclipse.jface.wizard.Wizard;

import com.conwet.wirecloud.WirecloudAPI;
 

public class WirecloudServerConfigWizard extends Wizard {

	
	//protected ServerSelectionPage one= new ServerSelectionPage("Deploy");
	//protected AuthenticationWizardPage two = new AuthenticationWizardPage("Deploy");
//	private String widget2Deploy;
//	private String currentWirecloudAPIURL = "";
//	private WirecloudAPI wirecloudAPI;
//
//	public WirecloudServerConfigWizard(String widget2Deploy) {
//		super();
//		setNeedsProgressMonitor(true);
//		this.widget2Deploy=widget2Deploy;
//		//addPage(one);
//		//addPage(two);
//	}


	@Override
	public boolean performFinish() {
	//	wirecloudAPI.deployWGT(widget2Deploy, two.getToken());
		return true;
	}

/*	public WirecloudAPI getWirecloudAPI() {
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
	}*/
} 
