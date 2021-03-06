/*
 *  Copyright (c) 2013-2015 CoNWeT Lab., Universidad Politécnica de Madrid
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

public class WirecloudServerBehaviour extends ServerBehaviourDelegate {

	protected transient ServerMonitoringThread ping;

	@Override
	public void dispose() {
		super.dispose();
		
		stopPingThread();
	}

	@Override
	protected void initialize(IProgressMonitor monitor) {
		super.initialize(monitor);
		startPingThread();
	}

	@Override
	protected void publishStart(IProgressMonitor monitor) throws CoreException {

		super.publishStart(monitor);
	}

	@Override
	protected void publishServer(int kind, IProgressMonitor monitor)
			throws CoreException {
		super.publishServer(kind, monitor);
	}


	@Override
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {

		super.publishFinish(monitor);
	}

	@Override
	public void publish(int kind, List<IModule[]> modules,
			IProgressMonitor monitor, IAdaptable info) throws CoreException {
		super.publish(kind, modules, monitor, info);
	}


	@Override
	public IStatus publish(int kind, IProgressMonitor monitor) {
		IStatus status = super.publish(kind, monitor);
		return status;
	}

	@Override
	protected IStatus publishModule(int kind, IModule[] module, int deltaKind,
			IProgressMonitor monitor) {
		IStatus status = super.publishModule(kind, module, deltaKind, monitor);
		return status;
	}

    @Override
    protected void publishModule(int kind, int deltaKind, IModule[] module,
            IProgressMonitor monitor) throws CoreException {

        ArrayList<String> listToRetreat;
        IServer server = this.getServer();
        String TOKEN = server.getAttribute("TOKEN", "");
        Zip zipper = new Zip();
        WirecloudAPI api = null;
        IProject project = module[0].getProject();

        try {
            api = new WirecloudAPI(this.getFinalURL().toString());
            api.setToken(TOKEN);

            if (deltaKind != ServerBehaviourDelegate.REMOVED
                    && project.findMaxProblemSeverity(IMarker.PROBLEM, true,
                            IResource.DEPTH_INFINITE) == IMarker.SEVERITY_ERROR) {
                return;
            }

            // check deltaKind-> ADD, REMOVE or CHANGED module
            if (deltaKind == ServerBehaviourDelegate.ADDED) {
                File wgtFile = File.createTempFile(project.getName(), ".wgt");
                zipper.zipFile(project.getLocation().toOSString(), wgtFile, true);
                api.deployWGT(wgtFile, TOKEN);
            } else if (deltaKind == ServerBehaviourDelegate.REMOVED || deltaKind == ServerBehaviourDelegate.CHANGED) {
                listToRetreat = new ArrayList<>();

                File descriptionFile = new File(project.getLocation() + "/config.xml");
                try {
                    MACDescription description = new MACDescription(
                            descriptionFile);
                    if (description.vendor != null && description.name != null && description.version != null) {
                        String element = description.vendor + "/" + description.name + "/" + description.version;
                        listToRetreat.add(element);
                    }
                } catch (MACDescriptionParseException e) {}

                Iterator<String> resourcesToDeleteIterator = listToRetreat.iterator();
                while (resourcesToDeleteIterator.hasNext()) {
                    api.uninstallResource(resourcesToDeleteIterator.next());
                }

                if (deltaKind == ServerBehaviourDelegate.CHANGED) {
                    File wgtFile = File.createTempFile(project.getName(), ".wgt");
                    zipper.zipFile(project.getLocation().toOSString(), wgtFile, true);
                    api.deployWGT(wgtFile, TOKEN);
                }

            }
            super.publishModule(kind, deltaKind, module, monitor);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
	protected void publishModules(int kind, List modules, List deltaKind2,
			MultiStatus multi, IProgressMonitor monitor) {
		super.publishModules(kind, modules, deltaKind2, multi, monitor);
	}

	@Override
	public void restart(String launchMode) throws CoreException {
		super.restart(launchMode);
	}

	@Override
	protected MultiStatus performTasks(PublishOperation[] tasks,
			IProgressMonitor monitor) {
		return super.performTasks(tasks, monitor);
	}

	@Override
	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy,
			IProgressMonitor monitor) throws CoreException {
		super.setupLaunchConfiguration(workingCopy, monitor);
	}


	@Override
	protected void addRemovedModules(List<IModule[]> moduleList,
			List<Integer> kindList) {
		//added and removed module
		super.addRemovedModules(moduleList, kindList);
	}

	@Override
	public IStatus canRestart(String mode) {
		return super.canRestart(mode);
	}

	@Override
	public boolean canRestartModule(IModule[] module) {
		return super.canRestartModule(module);
	}

	@Override
	public boolean canPublishModule(IModule[] module) {
		return super.canPublishModule(module);
	}

	@Override
	public IStatus canStart(String launchMode) {
		if ("localhost".equals(this.getServer().getHost())) {
			return Status.OK_STATUS;
		} else {
			return Status.CANCEL_STATUS;
		}
	}

	@Override
	public IStatus canStop() {
		return super.canStop();
	}

	@Override
	public void startModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		super.startModule(module, monitor);
	}

	@Override
	public void stop(boolean arg0) {
		stopPingThread();
		this.setServerState(IServer.STATE_STOPPING);
	}

	protected void stopPingThread() {
		if (ping != null) {
			ping.stop();
			ping = null;
		}
		setServerStopped();
	}

	private URL getFinalURL() throws NumberFormatException, MalformedURLException {
		IServer server = getServer();

		int port = Integer.parseInt(server.getAttribute("PORT", "443"));
		String schema = server.getAttribute("PROTOCOL", "https");
		String domain = server.getHost();

		if (("https".equals(schema) && port == 443) || ("http".equals(schema) && port == 80)) {
			return new URL(schema, domain, "/");
		}

		return new URL(schema, domain, port, "/");
	}

	protected void startPingThread() {
		try {
			if (ping != null) {
				ping.stop();
			}
			setServerState(IServer.STATE_STARTING);
			ping = new ServerMonitoringThread((new URL(getFinalURL(), "api/features")).toString(), this);

		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}

	public void setServerRunning() {
		IServer server = getServer();
		int state = server.getServerState();
		if (state != IServer.STATE_STARTING) 
			this.setServerState(IServer.STATE_STARTING);
		this.setServerState(IServer.STATE_STARTED);
	}

	public void setServerStopped() {
		int state = getServer().getServerState();
		if (state != IServer.STATE_STOPPING) 
			this.setServerState(IServer.STATE_STOPPING);
		this.setServerState(IServer.STATE_STOPPED);
	}
}
