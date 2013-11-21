package com.conwet.wirecloud.wizards;

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


public class ServerSelectionPage extends WizardFragment {

	  private Text urlPrefix;
	  private Text port;
	  private Composite container;
	  private final String DEFAULT_PORT = "80";
	  private final String DEFAULT_PREFIX = "/";

	  @Override
	  public boolean hasComposite() {
		  return true;
	  }

	  @Override
	  public Composite createComposite(Composite parent, IWizardHandle handle) {

		handle.setTitle("");
		handle.setDescription("Select the port and the URL path prefix of the Wirecloud Server");

	    container = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    GridLayout layout2 = new GridLayout();
	    container.setLayout(layout);
	    container.setLayout(layout2);
	    layout.numColumns = 2;
	    layout2.numColumns = 2;
	    Label label1 = new Label(container, SWT.NULL);
	    Label label2 = new Label(container, SWT.NULL);
	    
	    label1.setText("UrlPrefix");
	    label2.setText("Port");
	    urlPrefix = new Text(container, SWT.BORDER | SWT.SINGLE);
	    urlPrefix.setText(DEFAULT_PREFIX);
	    urlPrefix.addKeyListener(new KeyListener() {

	      @Override
	      public void keyPressed(KeyEvent e) {
	        // TODO Auto-generated method stub

	      }

	      @Override
	      public void keyReleased(KeyEvent e) {
	        if (!urlPrefix.getText().isEmpty()) {
	          setComplete(true);
	        }
	      }

	    });
	    port = new Text(container, SWT.BORDER | SWT.SINGLE);
	    port.setText(DEFAULT_PORT);
	    port.addKeyListener(new KeyListener() {
	    	
	    
	      @Override
	      public void keyPressed(KeyEvent e) {
	        // TODO Auto-generated method stub

	      }

	      @Override
	      public void keyReleased(KeyEvent e) {
	        if (!port.getText().isEmpty()) {
	          setComplete(true);
	        }
	      }

	    });
	    
	    
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    urlPrefix.setLayoutData(gd);
	    GridData gd2=new GridData(GridData.FILL_HORIZONTAL);
	    port.setLayoutData(gd2);

	    setComplete(true);
	    return container;
	  }

	  private IServerWorkingCopy getServer() {
	        IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel()
	                .getObject( TaskModel.TASK_SERVER );
	        return server;
	  }

	  public String getUrlText() {
		  return "http" + "://" + getServer().getHost() + ":" + port.getText() + urlPrefix.getText();
	  }
}
