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

package com.conwet.wirecloud.ide.projects;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.framework.Bundle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.conwet.wirecloud.ide.filetemplates.WirecloudFileTemplate;

public abstract class ProjectSupport {

	
    public abstract IProject createProject(String projectName, URI location);

    /**
     * Just do the basics: create a basic project.
     *
     * @param location
     * @param projectName
     * @throws CoreException 
     */
    protected static IProject createBaseProject(String projectName, URI location) throws CoreException {
    	IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

    	if (!newProject.exists()) {
            URI projectLocation = location;
            IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
            if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
                projectLocation = null;
            }

            desc.setLocationURI(projectLocation);
            try {
                newProject.create(desc, null);
                if (!newProject.isOpen()) {
                    newProject.open(null);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        	IFacetedProject facetedProject = ProjectFacetsManager.create(newProject, true, null);

            // Install JavaScript and MAC facets
            Set<IProjectFacet> facets = new HashSet<>(facetedProject.getFixedProjectFacets());

            IProjectFacet jsFacet = ProjectFacetsManager.getProjectFacet("wst.jsdt.web");
            facets.add(jsFacet);
            facetedProject.installProjectFacet(jsFacet.getDefaultVersion(), null, null);

            facetedProject.setFixedProjectFacets(facets);
            IProjectFacet macFacet = ProjectFacetsManager.getProjectFacet("com.conwet.applicationmashup.mac");
            facetedProject.installProjectFacet(macFacet.getDefaultVersion(), null, null);

            facets.add(jsFacet);
            facets.add(macFacet);
            facetedProject.setFixedProjectFacets(facets);
        }
    	
        return newProject;
    }

    private static void createFolder(IFolder folder) throws CoreException {
        IContainer parent = folder.getParent();
        if (parent instanceof IFolder) {
            createFolder((IFolder) parent);
        }
        if (!folder.exists()) {
            folder.create(false, true, null);
        }
    }

    
    private static void createFile(IFile file, WirecloudFileTemplate fileTemplate) throws CoreException, FileNotFoundException {    	
    	byte[] bytes = fileTemplate.getTemplateContent().getBytes();
        InputStream source = new ByteArrayInputStream(bytes);
        file.create(source, IResource.NONE, null);
    }
    
    /**
     * Create a folder structure with a parent root, overlay, and a few child
     * folders.
     *
     * @param newProject
     * @param paths
     * @throws CoreException
     */
    protected static void addToProjectStructure(IProject newProject, String[] paths) throws CoreException {
        for (String path : paths) {
            IFolder etcFolders = newProject.getFolder(path);
            createFolder(etcFolders);
        }
    }

    
    /**
     * Create files in folder structure with a parent root.
     *
     * @param newProject
     * @param paths
     * @param fileTemplates
     * @throws CoreException
     * @throws FileNotFoundException 
     */
    protected void addFilesToProjectStructure(IProject newProject, String[] paths, WirecloudFileTemplate[] fileTemplates) throws CoreException, FileNotFoundException {
        for (int i = 0; i < paths.length; i++) {
        	String path = paths[i];
        	WirecloudFileTemplate fileTemplate = fileTemplates[i];
        	IFile file = newProject.getFile(path);
        	createFile(file, fileTemplate);
        }
    }

    protected static void createFileFromStaticTemplate(IProject newProject, String templateRoute, String destRoute){
    	Bundle bundle = Platform.getBundle("com.conwet.wirecloud.ide");
    	URL fileURL = bundle.getEntry(templateRoute);
    	File file = null;
    	try {
    		file = new File(FileLocator.toFileURL(fileURL).toURI());
    	} catch (URISyntaxException e1) {
    	    e1.printStackTrace();
    	} catch (IOException e1) {
    	    e1.printStackTrace();
    	}
    	IFile projectFile = newProject.getFile(destRoute);
        InputStream source = null;
		try {
			source = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			projectFile.create(source, IResource.NONE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
    
    public static void addNature(IProject project, String nature) throws CoreException {
        if (!project.hasNature(nature)) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = nature;
            description.setNatureIds(newNatures);

            IProgressMonitor monitor = null;
            project.setDescription(description, monitor);
        }
    }
}