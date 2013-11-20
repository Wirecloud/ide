package com.conwet.wirecloud;
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
