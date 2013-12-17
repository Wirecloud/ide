package com.conwetlab.wirecloud.sdk.wizards.deployment;

import java.net.MalformedURLException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.Wizard;

import com.conwetlab.wirecloud.sdk.wapi.WirecloudAPI;
import com.conwetlab.wirecloud.sdk.wizards.conection_manager.AuthenticationWizardPage;
import com.conwetlab.wirecloud.sdk.wizards.conection_manager.ServerSelectionPage;


public class WirecloudDeploymentWizard extends Wizard {

	protected DeploymentWizardPageOne one;
	protected ServerSelectionPage two= new ServerSelectionPage("Deploy");
	protected AuthenticationWizardPage three = new AuthenticationWizardPage("Deploy");

	private String currentWirecloudAPIURL = "";
	private WirecloudAPI wirecloudAPI;
	private String chosenWidget;
	public WirecloudDeploymentWizard() {
		super();
		setNeedsProgressMonitor(true);
		one=new DeploymentWizardPageOne();
		addPage(one);
		addPage(two);
		addPage(three);
	}

	public WirecloudDeploymentWizard(String widgetName) {
		super();
		setNeedsProgressMonitor(true);
		this.chosenWidget=widgetName;
		addPage(two);
		addPage(three);
	}

	@Override
	public boolean performFinish() {
		if(chosenWidget!=null){
			wirecloudAPI.deployWGT(chosenWidget, three.getToken());
			
		}
		
		else {
			wirecloudAPI.deployWGT(one.getSourceFileField(), three.getToken());
		}
		return true;
	}

	public WirecloudAPI getWirecloudAPI() {
		if (!two.getUrlText().equals(currentWirecloudAPIURL)) {
			currentWirecloudAPIURL = two.getUrlText();
			try {
				wirecloudAPI = new WirecloudAPI(two.getUrlText());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wirecloudAPI;
	}
} 
