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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;

public class MACFacetInstallDelegate implements IDelegate {

	@Override
	public void execute(final IProject pj, final IProjectFacetVersion fv,
			final Object config, final IProgressMonitor monitor)

	throws CoreException

	{
		try {
			WirecloudModuleFactory factory = new WirecloudModuleFactory();
			factory.createModule(pj);
			
			IJavaScriptProject jsProject = JavaScriptCore.create(pj);
			JsGlobalScopeContainerInitializer container = new MashableApplicationComponentLibraryInitilizer();

			// Add the Mashable Application Component JavaScript Library to the project
			IIncludePathEntry entry = JavaScriptCore.newContainerEntry(container.getPath());
			IIncludePathEntry[] ipaths = jsProject.getRawIncludepath();
			IIncludePathEntry[] newPaths = new IIncludePathEntry[ipaths.length +1];
			System.arraycopy(ipaths, 0, newPaths, 0, ipaths.length);
			newPaths[ipaths.length] = entry;
			jsProject.setRawIncludepath(newPaths, null);
		} finally {
			monitor.done();
		}
	}

}
