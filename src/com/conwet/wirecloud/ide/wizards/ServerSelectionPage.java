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

	private Combo protocol;
	private Text hostName;
	private Text port;
	private Composite container;
	private Text wirecloudID;
	private Text wirecloudSecret;

	private final String DEFAULT_PROTOCOL = "https";
	private final String DEFAULT_PORT = "443";
	private IWizardHandle wizard_handle;

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public void enter() {
		hostName.setText( getServer().getHost());
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

			getServer().setAttribute("PROTOCOL", this.protocol.getText());
			getServer().setAttribute("PORT", Integer.parseInt(this.port.getText()));
			getServer().setAttribute("URLPREFIX", "/" /*this.urlPrefix.getText()*/);
			getServer().setAttribute("WIRECLOUDID", this.wirecloudID.getText());
			getServer().setAttribute("WIRECLOUDSECRET",this.wirecloudSecret.getText());

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
		protocol = new Combo(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		protocol.add("http");
		protocol.add("https");
		protocol.setText(DEFAULT_PROTOCOL);
		protocol.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if ("http".equals(protocol.getText())) {
					port.setText("80");
				} else {
					port.setText("443");
				}
				updateCompleteStatus();
			}

		});

		Label label2 = new Label(container, SWT.NULL);
		label2.setText("Host");
		hostName = new Text(container, SWT.BORDER | SWT.SINGLE);
		hostName.addKeyListener(new KeyListener() {
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
		port = new Text(container, SWT.BORDER | SWT.SINGLE);
		port.setText(DEFAULT_PORT);
		port.addKeyListener(new KeyListener() {
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
		wirecloudID = new Text(container, SWT.BORDER | SWT.SINGLE);
		wirecloudID.setText("");
		wirecloudID.addKeyListener(new KeyListener() {
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
		wirecloudSecret = new Text(container, SWT.BORDER | SWT.SINGLE);
		wirecloudSecret.setText("");
		wirecloudSecret.addKeyListener(new KeyListener() {
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
		port.setLayoutData(gd2);
		wirecloudID.setLayoutData(gd2);
		wirecloudSecret.setLayoutData(gd2);
		hostName.setLayoutData(gd2);
		setComplete(true);
		return container;
	}


	protected void updateCompleteStatus() {
		setComplete(!port.getText().isEmpty());
		wizard_handle.update();
	}

	private IServerWorkingCopy getServer() {
		IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel()
				.getObject( TaskModel.TASK_SERVER );
		return server;
	}

	public URL getFinalURL() throws NumberFormatException, MalformedURLException {
		return new URL(protocol.getText(), getServer().getHost(), Integer.parseInt(port.getText()), "/" /*urlPrefix.getText()*/);
	}

}
