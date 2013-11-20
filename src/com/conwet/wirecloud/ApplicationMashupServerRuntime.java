package com.conwet.wirecloud;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

public class ApplicationMashupServerRuntime extends RuntimeDelegate {

	@Override
	public IStatus validate() {
		return Status.OK_STATUS;
	}

}