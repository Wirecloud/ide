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

package com.conwet.wirecloud.wizards;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import com.conwet.wirecloud.WirecloudAPI;


public class AuthenticationWizardPage extends WizardFragment {

	private Composite container;
	private String code;
	private String token;
	private Browser browser;
	private IWizardHandle handle;
	private LocationListener locationListener;

	@Override
	public boolean hasComposite() {
		return true;
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

		this.handle = handle;

		try {
			browser = new Browser(container, SWT.NONE);
		} catch (SWTError e) {
			System.err.println("Error en la creación de browser!");
		}
		
		setComplete(false);
		return container;
	}

	@Override
	public void enter() {

		final WirecloudAPI API = getWirecloudAPIFromWizardInstance();

		try {
			browser.setUrl(API.getAuthURL("WirecloudIDE").toString());
		} catch (OAuthSystemException e1) {
			e1.printStackTrace();
		}

		final AuthenticationWizardPage page = this;
		final IWizardHandle wizard_handle = handle;
		locationListener = new LocationListener() {
			public void changed(LocationEvent event) {
				Browser browser = (Browser) event.widget;
				URL currentURL;
				try {
					currentURL = new URL(browser.getUrl());
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return;
				}

				
				if (browser.getUrl().startsWith(API.UNIVERSAL_REDIRECT_URI.toString())) {
					QueryParameters parameters = new QueryParameters(
							currentURL.getQuery());
					page.code = parameters.getParameter("code");
					page.token = API.obtainAuthToken(page.code);
					page.setComplete(page.token != null);
					wizard_handle.update();
					getServer().setAttribute("TOKEN", page.token);
				}
			}

			public void changing(LocationEvent event) {
			}
		};
		browser.addLocationListener(locationListener);

		this.handle.update();
		super.enter();
	}

	@Override
	public void exit() {
		if (locationListener != null) {
			browser.removeLocationListener(locationListener);
			locationListener = null;
		}
	}

	private IServerWorkingCopy getServer() {
		IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel()
				.getObject( TaskModel.TASK_SERVER );
		return server;
	}


	private WirecloudAPI getWirecloudAPIFromWizardInstance(){
		return (WirecloudAPI) getTaskModel().getObject("WirecloudAPI");
	}

}
