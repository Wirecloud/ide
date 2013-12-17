package com.conwet.wirecloud.wizards;

import java.net.MalformedURLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import com.conwet.wirecloud.WirecloudAPI;


public class ServerSelectionPage extends WizardFragment {


	private Text port;
	private Text urlPrefix;
	private Composite container;
	private final String DEFAULT_PORT = "80";
	private final String DEFAULT_PREFIX = "/";

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	protected void setComplete(boolean complete) {
		if (complete) {
			//If it is complete, a wirecloud API is created to share with AuthenticationWizard. And the port number is saved.
			WirecloudAPI wirecloudAPI = null;
			try {
				wirecloudAPI = new WirecloudAPI(getUrlText());
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			getTaskModel().putObject("WirecloudAPI", wirecloudAPI);
			
			getServer().setAttribute("PORT", Integer.parseInt(this.port.getText()));
			getServer().setAttribute("URLPREFIX", this.urlPrefix.getText());
		}
		
		super.setComplete(complete);
	}
	
	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {

		handle.setTitle("Select port and URL prefix");
		handle.setDescription("Select the port and the URL path prefix of the Wirecloud Server");

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		GridLayout layout2 = new GridLayout();
		container.setLayout(layout2);
		container.setLayout(layout);

		layout.numColumns = 2;
		layout2.numColumns = 2;
		Label label1 = new Label(container, SWT.NULL);
		Label label2 = new Label(container, SWT.NULL);

		label1.setText("Port");
		label2.setText("UrlPrefix");
		port = new Text(container, SWT.BORDER | SWT.SINGLE);
		port.setText(DEFAULT_PORT);
		port.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!port.getText().isEmpty()) {
					setComplete(true);
				}
			}

		});
		urlPrefix = new Text(container, SWT.BORDER | SWT.SINGLE);
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

		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		port.setLayoutData(gd);
		GridData gd2=new GridData(GridData.FILL_HORIZONTAL);
		urlPrefix.setLayoutData(gd2);


		setComplete(true);
		return container;
	}



	private IServerWorkingCopy getServer() {
		IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel()
				.getObject( TaskModel.TASK_SERVER );
		return server;
	}

	public String getUrlText(){

//if the server is not localhost the port is not necessary
	if(getServer().getHost().equals("localhost")){
		return urlPrefix.getText().equals("/") ? "http" + "://" + getServer().getHost() + ":" + port.getText()
					 : "http" + "://" + getServer().getHost() + ":" + port.getText() + urlPrefix.getText();
	}
	else return urlPrefix.getText().equals("/") ? getServer().getHost()
			 : getServer().getHost() + urlPrefix.getText();
		
	}
}
