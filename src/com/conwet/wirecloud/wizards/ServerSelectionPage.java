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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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

import com.conwet.wirecloud.WirecloudAPI;


public class ServerSelectionPage extends WizardFragment {

	private Combo protocol;
	private Label hostLabel;
	private Text port;
	private Text urlPrefix;
	private Composite container;

	private final String DEFAULT_PROTOCOL = "https";
	private final String DEFAULT_PORT = "443";
	private final String DEFAULT_PREFIX = "/";

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public void enter() {
		hostLabel.setText("://" + getServer().getHost() + ":");
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
		} else {
			getTaskModel().putObject("WirecloudAPI", null);

			getServer().setAttribute("PROTOCOL", "");
			getServer().setAttribute("PORT", "");
			getServer().setAttribute("URLPREFIX", "");		
		}
	}
	
	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {

		handle.setTitle("Select port and URL prefix");
		handle.setDescription("Select the port and the URL path prefix of the Wirecloud Server");
		final IWizardHandle wizard_handle = handle;

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		GridLayout layout2 = new GridLayout();
		container.setLayout(layout2);
		container.setLayout(layout);

		layout.numColumns = 3;
		layout2.numColumns = 3;
		Label label1 = new Label(container, SWT.NULL);
		Label label2 = new Label(container, SWT.NULL);
		Label label3 = new Label(container, SWT.NULL);
		//Label label4 = new Label(container, SWT.NULL);

		label1.setText("Protocol");
		label2.setText("");
		label3.setText("Port");
		//label4.setText("URL prefix");

		protocol = new Combo(container, SWT.BORDER | SWT.SINGLE);
		protocol.add("http");
		protocol.add("https");
		protocol.setText(DEFAULT_PROTOCOL);

		hostLabel = new Label(container, SWT.NULL);

		port = new Text(container, SWT.BORDER | SWT.SINGLE);
		port.setText(DEFAULT_PORT);
		port.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				setComplete(!port.getText().isEmpty());
				wizard_handle.update();
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

		setComplete(true);
		return container;
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
