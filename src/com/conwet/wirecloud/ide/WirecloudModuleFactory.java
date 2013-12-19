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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

public class WirecloudModuleFactory extends ProjectModuleFactoryDelegate {

	private IProjectFacet facet = ProjectFacetsManager.getProjectFacet("com.conwet.applicationmashup.mac");
	private List<ModuleDelegate> moduleDelegates = new ArrayList<ModuleDelegate>();

	@Override
	public ModuleDelegate getModuleDelegate(IModule module) {
        for (ModuleDelegate moduleDelegate : moduleDelegates) {
            if (moduleDelegate.getModule() == module) {
                    return moduleDelegate;
            }
        }
        return null;
	}
	
    @Override
    protected IModule createModule(IProject project) {
            try {
                    IFacetedProject facetedProject = ProjectFacetsManager.create(project);
                    if (facetedProject != null && facetedProject.hasProjectFacet(facet)) {
                            IProjectFacetVersion projectFacetVersion = facetedProject.getInstalledVersion(facet);
                            String version = projectFacetVersion.getVersionString();
                            IModule module = createModule(project.getName(), project.getName(),
                            		"com.conwet.applicationmashup.mac", version, project);

                            MashableApplicationComponentDeployable deployable = new MashableApplicationComponentDeployable(project);
                            deployable.initialize(module);
                            moduleDelegates.add(deployable);
                            
                            return module;
                    }
            } catch (CoreException e) {
                    e.printStackTrace();
            }
            return null;
    }

}
