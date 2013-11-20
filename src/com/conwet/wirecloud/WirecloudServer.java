package com.conwet.wirecloud;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.ServerDelegate;


public class WirecloudServer extends ServerDelegate {

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
		
		IModule[] modules=new IModule[]{arg0};
		return modules;
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove,
			IProgressMonitor monitor) throws CoreException {
		
		IServer server = this.getServer();
		Zip zipper = new Zip();
		
		try {
			WirecloudAPI api = new WirecloudAPI("http://" + server.getHost() + ":8000");
			for (IModule module : add) {
				IProject project = module.getProject();
				String newPath = "/tmp/" + project.getName() + ".wgt";
				zipper.zipFile(project.getLocation().toOSString(), newPath, true);
				api.deployWGT(newPath, "wirecloud_token");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configurationChanged() {
		// TODO Auto-generated method stub
		super.configurationChanged();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}


	@Override
	public void importRuntimeConfiguration(IRuntime runtime,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.importRuntimeConfiguration(runtime, monitor);
	}


	@Override
	public boolean isUseProjectSpecificSchedulingRuleOnPublish() {
		// TODO Auto-generated method stub
		return super.isUseProjectSpecificSchedulingRuleOnPublish();
	}



}
