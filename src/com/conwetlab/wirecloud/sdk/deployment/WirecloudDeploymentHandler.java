package com.conwetlab.wirecloud.sdk.deployment;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.wizard.WizardDialog;

import com.conwetlab.wirecloud.sdk.wizards.deployment.WirecloudDeploymentWizard;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class WirecloudDeploymentHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public WirecloudDeploymentHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		WizardDialog dialog = new WizardDialog(window.getShell(), new WirecloudDeploymentWizard());
		dialog.open();
		return null;
	}

}
