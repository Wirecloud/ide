package com.conwetlab.wirecloud.sdk.projects;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class JasmineUnitTestProjectSupport extends ProjectSupport {

	private static final String LIB_PATH = "lib";
	private static final String JASMINE_PATH = LIB_PATH + "/" + "jasmine-1.2.0";
	private static final String SPEC_PATH = "spec";
	private static final String SRC_PATH = "src";
	

	@Override
	public IProject createProject(String projectName, URI location,
			String nature) {
		
		String [] paths = {SRC_PATH, SPEC_PATH, LIB_PATH, JASMINE_PATH};
		
		IProject project = null;
		
		try {
			project = createBaseProject(projectName, location);
			addNature(project, "com.aptana.projects.webnature");
			addNature(project, nature);
			addToProjectStructure(project, paths);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addStaticFiles(project);
		return project;
	}

	@Override
	public void addStaticFiles(IProject project) {
		createFileFromStaticTemplate(project, 	"static/jasmine/SpecRunner.html","SpecRunner.html");
		
		createFileFromStaticTemplate(project, 	"static/jasmine" + "/" + JASMINE_PATH + "/" + "jasmine-html.js",
												JASMINE_PATH + "/" + "jasmine-html.js");
		createFileFromStaticTemplate(project, 	"static/jasmine" + "/" + JASMINE_PATH + "/" + "jasmine.css",
												JASMINE_PATH + "/" + "jasmine.css");
		
		createFileFromStaticTemplate(project, 	"static/jasmine" + "/" + JASMINE_PATH + "/" + "jasmine.js",
												JASMINE_PATH + "/" + "jasmine.js");
		
		createFileFromStaticTemplate(project, 	"static/jasmine" + "/" + JASMINE_PATH + "/" + "MIT.LICENSE",
												JASMINE_PATH + "/" + "MIT.LICENSE");
		
		createFileFromStaticTemplate(project, 	"static/jasmine" + "/" + SPEC_PATH + "/" + "mainSpec.js",
												SPEC_PATH + "/" + "mainSpec.js");
	}
}
