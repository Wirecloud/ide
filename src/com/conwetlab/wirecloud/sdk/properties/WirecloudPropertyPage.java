package com.conwetlab.wirecloud.sdk.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class WirecloudPropertyPage extends PropertyPage{

	private static final String WIRECLOUD_DEPLOYMENT_TITLE = "&Deployment server:";
	private static final String WIRECLOUD_DEPLOYMENT_PROPERTY = "WIRECLOUD_DEPLOYMENT";
	private static final String DEFAULT_WIRECLOUD_DEPLOYMENT = "http://localhost:8000";

	private static final int TEXT_FIELD_WIDTH = 50;

	private Text defaultDeploymentServer;

	/**
	 * Constructor for WirecloudPropertyPage.
	 * Class based on auto-generated template in Eclipse Sample Property Page
	 */
	public WirecloudPropertyPage() {
		super();
	}


	@SuppressWarnings("unused")
	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void server(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		// Label for deployment server field
		Label deployLabel = new Label(composite, SWT.NONE);
		deployLabel.setText(WIRECLOUD_DEPLOYMENT_TITLE);

		// Deployment server text field
		defaultDeploymentServer = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		defaultDeploymentServer.setLayoutData(gd);

		// Populate deployment server text field
		try {
			String server =
				((IResource) getElement()).getPersistentProperty(
					new QualifiedName("", WIRECLOUD_DEPLOYMENT_PROPERTY));
			defaultDeploymentServer.setText((server != null) ? server : DEFAULT_WIRECLOUD_DEPLOYMENT);
		} catch (CoreException e) {
			defaultDeploymentServer.setText(DEFAULT_WIRECLOUD_DEPLOYMENT);
		}
	}
	

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		server(composite);
		//vendor(composite);
		//addSeparator(composite);
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	protected void performDefaults() {
		super.performDefaults();
		// Populate the deploy text field with the default value
		defaultDeploymentServer.setText(DEFAULT_WIRECLOUD_DEPLOYMENT);
	}
	
	public boolean performOk() {
		// store the value in the deploy text field
		try {
			((IResource) getElement()).setPersistentProperty(
				new QualifiedName("", WIRECLOUD_DEPLOYMENT_PROPERTY),
				defaultDeploymentServer.getText());
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
	
}
