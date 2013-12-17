package com.conwetlab.wirecloud.sdk.projects;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import java.io.FileNotFoundException;

import com.conwetlab.wirecloud.sdk.filetemplates.WirecloudFileTemplate;
import com.conwetlab.wirecloud.sdk.filetemplates.OperatorConfigFileTemplate;
import com.conwetlab.wirecloud.sdk.filetemplates.OperatorFileTemplate;


public class OperatorProjectSupport extends ProjectSupport {
	 /**
     * For this marvelous project we need to:
     * - create the default Eclipse project
     * - add the custom project nature
     * - create the folder structure
     *
     * @param projectName
     * @param location
     * @param natureId
     * @return
     */
    public IProject createProject(String projectName, URI location, String nature) {
        
        final String IMG_DIR_NAME, IMG_DIR_PATH, JS_DIR_NAME, JS_DIR_PATH, SLASH, JS_FILE_NAME, JS_FILE_PATH, CONFIG_XML_NAME, CONFIG_XML_PATH;
        
        SLASH = "/";
        IMG_DIR_NAME = "images";
        IMG_DIR_PATH = IMG_DIR_NAME;
        JS_DIR_NAME = "js";
        JS_DIR_PATH = JS_DIR_NAME;
        
        JS_FILE_NAME = "main.js";
        JS_FILE_PATH = JS_DIR_PATH + SLASH + JS_FILE_NAME; 
          
        CONFIG_XML_NAME = "config.xml";
        CONFIG_XML_PATH = CONFIG_XML_NAME;

        IProject project = null;
        try {
        	project = createBaseProject(projectName, location);
        	addNature(project, "com.aptana.projects.webnature");
            addNature(project, nature);

            String[] paths = {IMG_DIR_PATH, JS_DIR_PATH};
            String[] filePaths = {JS_FILE_PATH, CONFIG_XML_PATH};
            WirecloudFileTemplate [] fileTemplateList = new WirecloudFileTemplate[filePaths.length];
            fileTemplateList[0] = new OperatorFileTemplate();
            fileTemplateList[1] = new OperatorConfigFileTemplate(projectName);
            
            addToProjectStructure(project, paths);
            try {
				addFilesToProjectStructure(project, filePaths, fileTemplateList);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				project = null;
			}
        } catch (CoreException e) {
            e.printStackTrace();
            project = null;
        }

        return project;
    }

}
