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

package com.conwet.wirecloud.ide.filetemplates;

public class WidgetBuildFileTemplate  extends WirecloudFileTemplate{
	
	public WidgetBuildFileTemplate(String projectName){
		super();
		this.firstUpperProjectName = super.getFirstUpperProjectName(projectName);
		
	}
	
	
	@Override
	protected void createTemplate() {
		this.createConfigTemplate();
		
	}
	
	private void createConfigTemplate(){
		this.template.format(this.templateFormat(),
							this.xmlHeader(),
							this.project(this.firstUpperProjectName),
							this.property("project-name","${ant.project.name}"),
							this.property("unzip-destination", "unzipped"),
							this.targetNameWithContent("clean"),
							"<delete file=\"${project-name}.zip\"/>",
							"<delete dir=\"${unzip-destination}\"/>",
							this.closeTag("target"),
							this.targetNameWithContent("zip"),
							"<zip destfile=\"${project-name}.zip\">",
							"<fileset dir=\"${basedir}\" includes=\"**/*\"/>",
							this.closeTag("zip"),
							this.closeTag("target"),
							this.targetNameWithContent("unzip"),
							"<unzip src=\"${project-name}.zip\" dest=\"${unzip-destination}\"/>",
							this.closeTag("target"),
							this.closeTag("project"));
	}
	
	private String templateFormat(){
		String templateFormat;
		
		// Atomic format elements //
		String xmlElement, newLine, tab;
		newLine = "%n"; tab = "\t"; xmlElement = "%s";
		
		// Compound format elements //
		String zeroTab, oneTab, twoTab;
		zeroTab = xmlElement +  newLine;
		oneTab = tab + zeroTab;
		twoTab = tab + oneTab;
		
		templateFormat = 	zeroTab 		+	/* xmlHeader */
							zeroTab			+	/* project */
								oneTab		+	/* property */
								oneTab 		+	/* property */
								oneTab		+	/* target */
									twoTab	+	/* delete */
									twoTab	+	/* delete */
								oneTab		+	/* target */
								oneTab		+	/* taget */
									twoTab	+	/* zip */
									twoTab	+	/* fileset */
									twoTab	+	/* zip */
								oneTab		+	/* target */
								oneTab		+	/* target */
									twoTab	+	/* unzip */
								oneTab		+	/* target */
							zeroTab;			/* project */
									
		return templateFormat;		
	}
	private String xmlHeader(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}
	private String closeTag(String tag){
		return "</" + tag + ">";
	}
	private String targetNameWithContent (String content){
		return "<target name=\"" + content + "\">" ;
	}
	private String project(String name){
		return "<project name=\"" + name +"\" default=\"zip\" basedir=\".\">";
	}
	private String property(String name, String value){
		return "<property name=\"" + name + "\" value=\"" + value + "\"  />";
	}




}

