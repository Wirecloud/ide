package com.conwetlab.wirecloud.sdk.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.conwetlab.wirecloud.sdk.wizards.messages"; //$NON-NLS-1$
	public static String WidgetProjectWizard_Description;
	public static String WidgetProjectWizard_WidgetProject;
	
	public static String OperatorProjectWizard_Description;
	public static String OperatorProjectWizard_OperatorProject;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
