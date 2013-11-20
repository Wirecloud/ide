package com.conwet.wirecloud;

import java.util.List;

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

	@Override
	protected void publishStart(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.publishStart(monitor);
	}

	@Override
	protected void publishServer(int kind, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		super.publishServer(kind, monitor);
	}

	@Override
	protected void publishModule(int kind, int deltaKind, IModule[] module,
			IProgressMonitor monitor) throws CoreException {
		super.publishModule(kind, deltaKind, module, monitor);
	}

	@Override
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.publishFinish(monitor);
	}

	@Override
	public void publish(int kind, List<IModule[]> modules,
			IProgressMonitor monitor, IAdaptable info) throws CoreException {
		// TODO Auto-generated method stub
		super.publish(kind, modules, monitor, info);
	}

	@Override
	public IStatus publish(int kind, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return super.publish(kind, monitor);
	}

	@Override
	protected IStatus publishModule(int kind, IModule[] module, int deltaKind,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return super.publishModule(kind, module, deltaKind, monitor);
	}

	@Override
	protected void publishModules(int kind, List modules, List deltaKind2,
			MultiStatus multi, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		super.publishModules(kind, modules, deltaKind2, multi, monitor);
	}

	@Override
	public void restart(String launchMode) throws CoreException {
		// TODO Auto-generated method stub
		super.restart(launchMode);
	}

	@Override
	protected MultiStatus performTasks(PublishOperation[] tasks,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return super.performTasks(tasks, monitor);
	}

	@Override
	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.setupLaunchConfiguration(workingCopy, monitor);
	}

	@Override
	protected void initialize(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		super.initialize(monitor);
//		try {
//			this.getServer().start("hola", monitor);
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//this.setServerState(IServer.STATE_STARTING);

	}

	@Override
	protected void addRemovedModules(List<IModule[]> moduleList,
			List<Integer> kindList) {
		// TODO Auto-generated method stub
		super.addRemovedModules(moduleList, kindList);
	}

	@Override
	public IStatus canRestart(String mode) {
		// TODO Auto-generated method stub
		return super.canRestart(mode);
	}

	@Override
	public boolean canRestartModule(IModule[] module) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return super.canStop();
	}

	@Override
	public void startModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("Starting MODULEEEEE....");
		super.startModule(module, monitor);
	}

	@Override
	public void stop(boolean arg0) {
		// TODO Auto-generated method stub
		this.setServerState(IServer.STATE_STOPPING);
		this.setServerState(IServer.STATE_STOPPED);
	}

}
