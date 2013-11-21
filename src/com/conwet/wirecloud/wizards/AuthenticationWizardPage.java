package com.conwet.wirecloud.wizards;

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
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import com.conwet.wirecloud.WirecloudAPI;


public class AuthenticationWizardPage extends WizardFragment {

	private Composite container;
	private String code;
	private String token;
	private Browser browser;

	public AuthenticationWizardPage() {
		
	
	}

	public String getToken() {
		return this.token;
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		handle.setTitle("Authentication");
		handle.setDescription("Authenticate in Wirecloud");
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
					e.printStackTrace();
					return;
				}

				WirecloudAPI API = getWirecloudAPIFromWizardInstance();
				if (browser.getUrl().startsWith(API.UNIVERSAL_REDIRECT_URI)) {
					QueryParameters parameters = new QueryParameters(
							currentURL.getQuery());
					page.code = parameters.getParameter("code");
					page.token = API.obtainAuthToken(page.code);
					page.setComplete(page.token != null);
				}
			}

			public void changing(LocationEvent event) {
			}
		};
		browser.addLocationListener(locationListener);

		setComplete(false);
		return container;
	}

//	@Override
//	public void setVisible(boolean visible) {
//
//		if (visible) {
//			WirecloudAPI wirecloudAPI = getWirecloudAPIFromWizardInstance();
//			try {
//				browser.setUrl(wirecloudAPI.getAuthURL("WirecloudIDE"));
//			} catch (OAuthSystemException e) {
//				e.printStackTrace();
//			}
//		}
//
//		super.setVisible(visible);
//	}

	private WirecloudAPI getWirecloudAPIFromWizardInstance(){
		return (WirecloudAPI) getTaskModel().getObject("WirecloudAPI");
	}

}
