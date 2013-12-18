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

package com.conwetlab.wirecloud.sdk.projects;

import java.net.URI;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.conwetlab.wirecloud.sdk.filetemplates.WidgetBuildFileTemplate;
import com.conwetlab.wirecloud.sdk.filetemplates.WirecloudFileTemplate;
import com.conwetlab.wirecloud.sdk.filetemplates.WidgetConfigFileTemplate;
import com.conwetlab.wirecloud.sdk.filetemplates.WidgetCssFileTemplate;
import com.conwetlab.wirecloud.sdk.filetemplates.WidgetFileTemplate;
import com.conwetlab.wirecloud.sdk.filetemplates.WidgetHtmlFileTemplate;

public class WidgetProjectSupport extends ProjectSupport {
	
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

        
        final String IMG_DIR_NAME, IMG_DIR_PATH, CSS_DIR_NAME, CSS_DIR_PATH, JS_DIR_NAME, JS_DIR_PATH, SLASH, HTML_NAME, HTML_PATH,
        			 JS_FILE_NAME, JS_FILE_PATH, CSS_FILE_NAME, CSS_FILE_PATH, CONFIG_XML_NAME, CONFIG_XML_PATH,  BUILD_XML_NAME,
        			 BUILD_XML_PATH;
        
        SLASH = "/";
        IMG_DIR_NAME = "images";
        IMG_DIR_PATH = IMG_DIR_NAME;
        CSS_DIR_NAME = "css";
        CSS_DIR_PATH = CSS_DIR_NAME;
        JS_DIR_NAME = "js";
        JS_DIR_PATH = JS_DIR_NAME;
        
        JS_FILE_NAME = "main.js";
        JS_FILE_PATH = JS_DIR_PATH + SLASH + JS_FILE_NAME; 
        
        CSS_FILE_NAME = "style.css";
        CSS_FILE_PATH = CSS_DIR_PATH + SLASH + CSS_FILE_NAME;
        
        CONFIG_XML_NAME = "config.xml";
        CONFIG_XML_PATH = CONFIG_XML_NAME;
        
        HTML_NAME = "index.html";
        HTML_PATH = HTML_NAME;
        
        BUILD_XML_NAME = "build.xml";
        BUILD_XML_PATH = BUILD_XML_NAME;

        IProject project = null;
        try {
        	project = createBaseProject(projectName, location);
            addNature(project, nature);

            String[] paths = {IMG_DIR_PATH, CSS_DIR_PATH, JS_DIR_PATH};
            String[] filePaths = {JS_FILE_PATH, CSS_FILE_PATH, CONFIG_XML_PATH, HTML_PATH,BUILD_XML_PATH};
            WirecloudFileTemplate [] fileTemplateList = new WirecloudFileTemplate[filePaths.length];
            fileTemplateList[0] = new WidgetFileTemplate(projectName);
            fileTemplateList[1] = new WidgetCssFileTemplate();
            fileTemplateList[2] = new WidgetConfigFileTemplate(projectName);
            fileTemplateList[3] = new WidgetHtmlFileTemplate(projectName);
            fileTemplateList[4] = new WidgetBuildFileTemplate(projectName);
            
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
