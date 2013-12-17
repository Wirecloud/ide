package com.conwetlab.wirecloud.sdk.wizards.retreatment;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.conwetlab.wirecloud.sdk.wapi.WirecloudAPI;

public class RetreatmentWizardIIPageOne extends WizardPage {
	  private Composite container;
	  private RetreatmentsManager manager;
	  private HashMap<String, String> mashableComponents;
	  private HashMap<Button, Boolean> buttonStateMap;
	  private HashMap<Button, String> buttonsToResourcesMap;
	  private ArrayList<String> resourcesToDelete;
	  
	  public RetreatmentWizardIIPageOne(WirecloudAPI wapi) {
	    super("retreat");
	    setTitle("Retreatment");
	    setDescription("Select the widget / operator to retreat.");
	    this.manager = RetreatmentsManager.getInstance(wapi);
	    this.mashableComponents = null;
	    this.resourcesToDelete = new ArrayList<String>();
	    this.buttonStateMap = new HashMap<Button, Boolean>();
	    this.buttonsToResourcesMap = new HashMap<Button, String>();
	  }

	  @Override
	  public void createControl(Composite parent) {
		  
		container = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    
	    container.setLayout(layout);
	    layout.numColumns = 2;
	    
	    mashableComponents = this.manager.getMashableComponents();
	    displayMashableComponents();
	    
	    setControl(container);
	    setPageComplete(false);
	  }
	  
	  private void displayMashableComponents(){
		  Button button;
		  Iterator <?> it = this.mashableComponents.keySet().iterator();
		  while(it.hasNext()){
			  button = new Button(container, SWT.CHECK);
			  button.addSelectionListener(setPageComplete);
			  String resource = (String)it.next();
			  button.setText(this.mashableComponents.get(resource));
			  this.buttonStateMap.put(button, false);
			  this.buttonsToResourcesMap.put(button, resource );
		  }
		  
	  }
	  
	  private SelectionListener setPageComplete = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(someButtonIsEnabled(e));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setPageComplete(someButtonIsEnabled(e));
			}
		}; 
		
		private boolean someButtonIsEnabled(SelectionEvent e){
			Button eventButton = (Button)e.getSource();
			changeButtonState(eventButton);
			return checkAllButtonsState();
		}
		
		private void changeButtonState(Button button){
			if(this.buttonStateMap.containsKey(button)){
				boolean newState = !(this.buttonStateMap.remove(button));
				this.buttonStateMap.put(button, newState);
				if(!newState){
					this.resourcesToDelete.remove(this.buttonsToResourcesMap.get(button));
				}
			}
		}
		
		private boolean checkAllButtonsState(){
			Iterator<Button> buttonIterator = this.buttonStateMap.keySet().iterator();
			
			while(buttonIterator.hasNext()){
				Button key = null;
				key = buttonIterator.next();
				if(this.buttonStateMap.get(key)){
					if(!this.resourcesToDelete.contains(this.buttonsToResourcesMap.get(key))){
						this.resourcesToDelete.add(this.buttonsToResourcesMap.get(key));
					}
				}
			}
			this.manager.setListToRetreat(this.resourcesToDelete);
			return ! this.resourcesToDelete.isEmpty();
		}
}
