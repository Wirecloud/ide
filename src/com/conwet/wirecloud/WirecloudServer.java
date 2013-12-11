package com.conwet.wirecloud;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.ServerDelegate;



public class WirecloudServer extends ServerDelegate {
//	private ArrayList<String> listToRetreat;


	@Override
	public ServerPort[] getServerPorts() {
		List<ServerPort> ports = new ArrayList<ServerPort>();
		ports.add(new ServerPort("server", "HTTP", 80, "HTTP"));         //$NON-NLS-1$

		return (ServerPort[])ports.toArray(new ServerPort[ports.size()]);
	}

	@Override
	public IStatus canModifyModules(IModule[] arg0, IModule[] arg1) {
		return Status.OK_STATUS;
	}

	@Override
	public IModule[] getChildModules(IModule[] arg0) {
		return arg0;
	}


	@Override
	public IModule[] getRootModules(IModule arg0) throws CoreException {

		return new IModule[]{arg0};
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove,
			IProgressMonitor monitor) throws CoreException {

	}





}
