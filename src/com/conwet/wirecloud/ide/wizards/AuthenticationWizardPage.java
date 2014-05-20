/*
 *  Copyright (c) 2013-2014 CoNWeT Lab., Universidad Politécnica de Madrid
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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.IMessageProvider;
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

import com.conwet.wirecloud.ide.WirecloudAPI;


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
			handle.setMessage("Error loading webview. For more information go to http://www.eclipse.org/swt/faq.php#howdetectmozilla", IMessageProvider.ERROR);
		}

		setComplete(false);
		return container;
	}

	@Override
    public void enter() {

        boolean auth_info_available = false;
        final WirecloudAPI API = getWirecloudAPIFromWizardInstance();

        Browser.clearSessions();

        try {
            API.getOAuthEndpoints();
            auth_info_available = true;
        } catch (Exception e) {
            e.printStackTrace();

            handle.setMessage("Error querying basic info to the WireCloud Server. Are you sure there is a WireCloud server running at \"" + API.url + "\" ?", IMessageProvider.ERROR);
            browser.setUrl("about:blank");
        }

        URL url = null;
        try {
            if (auth_info_available) {
                url = API.getAuthURL(getServer().getAttribute("WIRECLOUDID", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();

            handle.setMessage("Error opening the OAuth2 workflow for authentication. Are you sure the provided authentication parameters (client id and client secret) are valid?", IMessageProvider.ERROR);
            browser.setUrl("about:blank");
        }

        if (url != null) {
            browser.setUrl(url.toString());
            handle.setMessage("", IMessageProvider.NONE);
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

                if (browser.getUrl().startsWith(API.UNIVERSAL_REDIRECT_URI)) {
                    QueryParameters parameters = new QueryParameters(currentURL.getQuery());
                    page.code = parameters.getParameter("code");
                    String clientId = getServer().getAttribute("WIRECLOUDID", "");
                    String clientSecret = getServer().getAttribute("WIRECLOUDSECRET", "");
                    try {
                        page.token = API.obtainAuthToken(page.code, clientId, clientSecret);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
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
