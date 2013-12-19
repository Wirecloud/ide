/*
 *  Copyright (c) 2013 CoNWeT Lab., Universidad Politécnica de Madrid
 *  
 *  This file is part of Wirecloud IDE.
 *
 *  Wirecloud IDE is free software: you can redistribute it and/or modify
 *  it under the terms of the European Union Public Licence (EUPL)
 *  as published by the European Commission, either version 1.1
 *  of the License, or (at your option) any later version.
 *
 *  Wirecloud IDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 *
 *  You should have received a copy of the European Union Public Licence
 *  along with Wirecloud IDE.
 *  If not, see <https://joinup.ec.europa.eu/software/page/eupl/licence-eupl>.
 */

package com.conwet.wirecloud.ide;

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
