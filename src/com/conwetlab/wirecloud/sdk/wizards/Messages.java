package com.conwetlab.wirecloud.sdk.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.conwetlab.wirecloud.sdk.wizards.messages"; //$NON-NLS-1$
	public static String WidgetProjectWizard_Description;
	public static String WidgetProjectWizard_WidgetProject;
	
	public static String EmptyWidgetProjectWizard_Description;
	public static String EmptyWidgetProjectWizard_EmptyWidgetProject;
	
	public static String OperatorProjectWizard_Description;
	public static String OperatorProjectWizard_OperatorProject;
	
	public static String EmptyOperatorProjectWizard_Description;
	public static String EmptyOperatorProjectWizard_EmptyOperatorProjec;
	
	public static String JasmineTestProjectWizard_Description;
	public static String JasmineTestProjectWizard_JasmineTestProjec;
	
	public static String EmptyJasmineTestProjectWizard_EmptyJasmineTestProjec;
	public static String EmptyJasmineTestProjectWizard_Description;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
