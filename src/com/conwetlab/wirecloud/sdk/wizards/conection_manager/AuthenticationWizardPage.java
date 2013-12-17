package com.conwetlab.wirecloud.sdk.wizards.conection_manager;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.conwetlab.wirecloud.sdk.wapi.WirecloudAPI;
import com.conwetlab.wirecloud.sdk.wizards.deployment.QueryParameters;
import com.conwetlab.wirecloud.sdk.wizards.deployment.WirecloudDeploymentWizard;
import com.conwetlab.wirecloud.sdk.wizards.retreatment.WirecloudRetreatmentWizardI;

public class AuthenticationWizardPage extends WizardPage {

	private Composite container;
	private String code;
	private String token;
	private String type;
	private Browser browser;

	public AuthenticationWizardPage(String type) {
		super("Authentication");
		setTitle("Authentication");
		setDescription("Authenticate in Wirecloud");
		this.type = type;
	}

	public String getToken() {
		return this.token;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		container.setLayout(new FillLayout());

		try {
			browser = new Browser(container, SWT.NONE);
		} catch (SWTError e) {
			System.err.println("Error en la creación de browser!");
		}

		final AuthenticationWizardPage page = this;
		LocationListener locationListener = new LocationListener() {
			public void changed(LocationEvent event) {
				Browser browser = (Browser) event.widget;
				URL currentURL;
				try {
					currentURL = new URL(browser.getUrl());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				WirecloudAPI API = getWirecloudAPIFromWizardInstance();
				if (browser.getUrl().startsWith(API.UNIVERSAL_REDIRECT_URI)) {
					QueryParameters parameters = new QueryParameters(
							currentURL.getQuery());
					page.code = parameters.getParameter("code");
					page.token = API.obtainAuthToken(page.code);
					page.setPageComplete(page.token != null);
				}
			}

			public void changing(LocationEvent event) {
			}
		};
		browser.addLocationListener(locationListener);

		setControl(container);
		setPageComplete(false);
	}

	@Override
	public void setVisible(boolean visible) {

		if (visible) {
			WirecloudAPI wirecloudAPI = getWirecloudAPIFromWizardInstance();
			try {
				browser.setUrl(wirecloudAPI.getAuthURL("WirecloudIDE"));
			} catch (OAuthSystemException e) {
				e.printStackTrace();
			}
		}

		super.setVisible(visible);
	}

	private WirecloudAPI getWirecloudAPIFromWizardInstance(){
		WirecloudAPI ret;
		if (type.equals("Deploy")) {
			WirecloudDeploymentWizard wizard = (WirecloudDeploymentWizard) getWizard();
			ret = wizard.getWirecloudAPI();
		}
		else{
			WirecloudRetreatmentWizardI wizard = (WirecloudRetreatmentWizardI) getWizard();
			ret = wizard.getWirecloudAPI();
		}
		return ret;
	}

}
