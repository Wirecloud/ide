package com.conwet.wirecloud;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

public class MACFacetInstallDelegate implements IDelegate {

	@Override
	public void execute(final IProject pj, final IProjectFacetVersion fv,
			final Object config, final IProgressMonitor monitor)

	throws CoreException

	{
		monitor.done();
		WirecloudModuleFactory factory = new WirecloudModuleFactory();
		factory.createModule(pj);
	}

}
