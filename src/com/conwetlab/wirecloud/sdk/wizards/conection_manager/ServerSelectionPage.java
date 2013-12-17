package com.conwetlab.wirecloud.sdk.wizards.conection_manager;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ServerSelectionPage extends WizardPage {
	  private Text urlText;
	  private Composite container;
	  private final String DEFAULT_DEPLOYMENT = "http://localhost:8000";

	  public ServerSelectionPage(String type) {
		  super(type);
		  setTitle(type);
		  setDescription("Select an URL to " + type.toLowerCase() + " a widget / operator");
	  }

	  @Override
	  public void createControl(Composite parent) {
	    container = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    container.setLayout(layout);
	    layout.numColumns = 2;
	    Label label1 = new Label(container, SWT.NULL);
	    label1.setText("Server");

	    urlText = new Text(container, SWT.BORDER | SWT.SINGLE);
	    urlText.setText(DEFAULT_DEPLOYMENT);
	    urlText.addKeyListener(new KeyListener() {

	      @Override
	      public void keyPressed(KeyEvent e) {
	        // TODO Auto-generated method stub

	      }

	      @Override
	      public void keyReleased(KeyEvent e) {
	        if (!urlText.getText().isEmpty()) {
	          setPageComplete(true);
	        }
	      }

	    });
	    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	    urlText.setLayoutData(gd);
	    // Required to avoid an error in the system
	    setControl(container);
	    setPageComplete(true);
	  }

	  public String getUrlText() {
		  return urlText.getText();
	  }

}
