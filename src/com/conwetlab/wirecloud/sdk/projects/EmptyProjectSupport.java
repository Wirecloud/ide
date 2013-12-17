package com.conwetlab.wirecloud.sdk.projects;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class EmptyProjectSupport extends ProjectSupport {
	private static final String JS_PATH = "js";

	@Override
	public IProject createProject(String projectName, URI location,
			String nature) {
		
		String [] paths = {JS_PATH};
		
		IProject project = null;
		
		try {
			project = createBaseProject(projectName, location);
			addNature(project, "com.aptana.projects.webnature");
			addNature(project, nature);
			if(!nature.equals("com.conwetlab.wirecloud.sdk.JasmineProjectNature")){
				addToProjectStructure(project, paths);
				addStaticFiles(project);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return project;
	}

	@Override
	public void addStaticFiles(IProject project) {
        createFileFromStaticTemplate(project, "static/WirecloudJSLib/WirecloudJSLib.js", "js/.WirecloudJSLib.js");
	}
}
