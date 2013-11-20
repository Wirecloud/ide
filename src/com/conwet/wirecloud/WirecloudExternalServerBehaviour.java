package com.conwet.wirecloud;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.generic.core.internal.ExternalServerBehaviour;
import org.eclipse.wst.server.core.IModule;

public class WirecloudExternalServerBehaviour extends ExternalServerBehaviour {

	
	@Override
	protected synchronized void setServerStarted() {
		// TODO Auto-generated method stub
		super.setServerStarted();
	}

	@Override
	public void stop(boolean force) {
		// TODO Auto-generated method stub
		super.stop(force);
	}

	@Override
	protected void terminate() {
		// TODO Auto-generated method stub
		super.terminate();
	}

	@Override
	public String getStartClassName() {
		// TODO Auto-generated method stub
		return super.getStartClassName();
	}

	@Override
	protected void shutdown(int state) {
		// TODO Auto-generated method stub
		super.shutdown(state);
	}

	@Override
	protected void stopImpl() {
		// TODO Auto-generated method stub
		super.stopImpl();
	}

	@Override
	public IStatus canStart(String launchMode) {
		// TODO Auto-generated method stub
		return super.canStart(launchMode);
	}

	@Override
	public IStatus canStop() {
		// TODO Auto-generated method stub
		return super.canStop();
	}

	@Override
	protected void initialize(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		super.initialize(monitor);
	}

	@Override
	public IStatus publish(int kind, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return super.publish(kind, monitor);
	}

	@Override
	protected void publishStart(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.publishStart(monitor);
	}

	@Override
	public void restart(String launchMode) throws CoreException {
		// TODO Auto-generated method stub
		super.restart(launchMode);
	}

	@Override
	public void restartModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		super.restartModule(module, monitor);
	}

	@Override
	public void startModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		super.startModule(module, monitor);
	}

	@Override
	public void stopModule(IModule[] module, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		super.stopModule(module, monitor);
	}

}
