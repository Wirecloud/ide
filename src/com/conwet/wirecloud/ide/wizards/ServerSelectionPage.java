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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import com.conwet.wirecloud.ide.WirecloudAPI;


public class ServerSelectionPage extends WizardFragment {

	private Combo protocolInput;
	private Text hostNameInput;
	private Text portInput;
	private Composite container;
	private Text wirecloudIDInput;
	private Text wirecloudSecretInput;

	private final String DEFAULT_PROTOCOL = "https";
	private final String DEFAULT_PORT = "443";
	private IWizardHandle wizard_handle;

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public void enter() {
		hostNameInput.setText( getServer().getHost());
		container.layout();
	}

	@Override
	public void exit() {
		super.exit();

		if (isComplete()) {

			WirecloudAPI wirecloudAPI = null;
			try {
				wirecloudAPI = new WirecloudAPI(getFinalURL());

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			getTaskModel().putObject("WirecloudAPI", wirecloudAPI);

			getServer().setHost(hostNameInput.getText());
			getServer().setAttribute("PROTOCOL", this.protocolInput.getText());
			getServer().setAttribute("PORT", Integer.parseInt(this.portInput.getText()));
			getServer().setAttribute("URLPREFIX", "/" /*this.urlPrefix.getText()*/);
			getServer().setAttribute("WIRECLOUDID", this.wirecloudIDInput.getText());
			getServer().setAttribute("WIRECLOUDSECRET",this.wirecloudSecretInput.getText());

		} else {
			getTaskModel().putObject("WirecloudAPI", null);

			getServer().setAttribute("PROTOCOL", "");
			getServer().setAttribute("PORT", "");
			getServer().setAttribute("URLPREFIX", "");
			getServer().setAttribute("WIRECLOUDID", "");
			getServer().setAttribute("WIRECLOUDSECRET","");

		}
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {

		handle.setTitle("Select port and URL prefix");
		handle.setDescription("Select the port and the URL path prefix of the Wirecloud Server");
		wizard_handle = handle;

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		Label label1 = new Label(container, SWT.NULL);
		label1.setText("Protocol");
		protocolInput = new Combo(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		protocolInput.add("http");
		protocolInput.add("https");
		protocolInput.setText(DEFAULT_PROTOCOL);
		protocolInput.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if ("http".equals(protocolInput.getText())) {
					portInput.setText("80");
				} else {
					portInput.setText("443");
				}
				updateCompleteStatus();
			}

		});

		Label label2 = new Label(container, SWT.NULL);
		label2.setText("Host");
		hostNameInput = new Text(container, SWT.BORDER | SWT.SINGLE);
		hostNameInput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				updateCompleteStatus();
			}
		});

		Label label3 = new Label(container, SWT.NULL);
		label3.setText("Port");
		portInput = new Text(container, SWT.BORDER | SWT.SINGLE);
		portInput.setText(DEFAULT_PORT);
		portInput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				updateCompleteStatus();
			}
		});

		Label label4 = new Label(container, SWT.NULL);
		label4.setText("WirecloudID: ");
		wirecloudIDInput = new Text(container, SWT.BORDER | SWT.SINGLE);
		wirecloudIDInput.setText("");
		wirecloudIDInput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				updateCompleteStatus();
			}

		});

		Label label5 = new Label(container, SWT.NULL);
		label5.setText("WirecloudSecret: ");
		wirecloudSecretInput = new Text(container, SWT.BORDER | SWT.SINGLE);
		wirecloudSecretInput.setText("");
		wirecloudSecretInput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				updateCompleteStatus();
			}

		});

		/*urlPrefix = new Text(container, SWT.BORDER | SWT.SINGLE);
		urlPrefix.setText(DEFAULT_PREFIX);
		urlPrefix.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!urlPrefix.getText().isEmpty()) {
					setComplete(true);
				}
			}
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});*/

		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		portInput.setLayoutData(gd2);
		wirecloudIDInput.setLayoutData(gd2);
		wirecloudSecretInput.setLayoutData(gd2);
		hostNameInput.setLayoutData(gd2);
		setComplete(true);
		return container;
	}


	protected void updateCompleteStatus() {
		setComplete(!portInput.getText().isEmpty());
		wizard_handle.update();
	}

	private IServerWorkingCopy getServer() {
		IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel()
				.getObject( TaskModel.TASK_SERVER );
		return server;
	}

	public URL getFinalURL() throws NumberFormatException, MalformedURLException {
		int port = Integer.parseInt(portInput.getText());
		String schema = protocolInput.getText();
		String domain = hostNameInput.getText();

		if (("https".equals(schema) && port == 443) || ("http".equals(schema) && port == 80)) {
			return new URL(schema, domain, "/");
		}

		return new URL(schema, domain, port, "/");
	}

}
