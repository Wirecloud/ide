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

package com.conwetlab.wirecloud.sdk.filetemplates;

public class WidgetConfigFileTemplate extends WirecloudFileTemplate {
	
	public WidgetConfigFileTemplate(String projectName){
		super();
		this.firstUpperProjectName = super.getFirstUpperProjectName(projectName);
		this.displayName = super.humanReadableProjectName(projectName);
	}
	
	@Override
	protected void createTemplate() {
		this.createConfigTemplate();
		
	}
	
	private void createConfigTemplate(){
		this.template.format(this.templateFormat(),
							this.xmlHeader(),
							this.xmlTemplate(),
								this.openTag("Catalog.ResourceDescription"),
									this.oneLineTag("Vendor"),
									this.oneLineTagWithContent("Name", this.firstUpperProjectName),
									this.oneLineTagWithContent("DisplayName", this.displayName),
									this.oneLineTag("Author"),
									this.oneLineTag("Version"),
									this.oneLineTag("Mail"),
									this.oneLineTag("Description"),
									this.oneLineTagWithContent("ImageURI", "images/catalogue.png"),
									this.oneLineTagWithContent("iPhoneImageURI", "images/catalogue.png"),
									this.oneLineTag("WikiURI"),
								this.closeTag("Catalog.ResourceDescription"),
								this.openTag("Platform.Preferences"),
									this.preference(),
								this.closeTag("Platform.Preferences"),
								this.openTag("Platform.StateProperties"),
									this.property(),
								this.closeTag("Platform.StateProperties"),
								this.openTag("Platform.Wiring"),
									this.endpoint("InputEndpoint"),
									this.endpoint("OutputEndpoint"),
								this.closeTag("Platform.Wiring"),
								this.openTag("Platform.Link"),
									this.link(),
								this.closeTag("Platform.Link"),
								this.rendering(),
							this.closeTag("Template"));
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
							zeroTab 		+	/* Template */
								oneTab		+	/* Catalog.ResourceDescription */
									twoTab 	+	/* Vendor */
									twoTab	+	/* Name */
									twoTab	+	/* DisplayName */
									twoTab	+	/* Author */
									twoTab	+	/* Version */
									twoTab	+	/* Mail */
									twoTab	+	/* Description */
									twoTab	+	/* ImageURI */
									twoTab	+	/* iPhoneImageURI */
									twoTab	+	/* WikiURI */
								oneTab		+	/* Catalog.ResourceDescription */
								oneTab		+	/* Platform.Preferences */
									twoTab	+	/* Preference */
								oneTab		+	/* Platform.Preferences */
								oneTab		+	/* Platform.StateProperties */
									twoTab 	+	/* Property */
								oneTab		+	/* Platform.StateProperties */
								oneTab		+	/* Platform.Wiring */
									twoTab	+	/* InputEndpoint */
									twoTab	+	/* OutputEndpoint */
								oneTab		+	/* Platform.Wiring */
								oneTab		+	/* Platform.Link */
									twoTab	+	/* XHTML */
								oneTab		+	/* Platform.Link */
								oneTab		+	/* Platform.Rendering */
							zeroTab;			/* Template */
									
		return templateFormat;		
	}
	
	private String xmlHeader(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}
	
	private String xmlTemplate(){
		return "<Template xmlns=\"http://wirecloud.conwet.fi.upm.es/ns/template#\">";
	}
	
	private String oneLineTag(String tag){
		return this.openTag(tag) + this.closeTag(tag);
	}
	
	private String openTag(String tag){
		return "<" + tag + ">";
	}
	
	private String closeTag(String tag){
		return "</" + tag + ">";
	}
	
	private String oneLineTagWithContent (String tag, String content){
		return this.openTag(tag) + content + this.closeTag(tag);
	}
	private String preference(){
		return "<Preference name=\"\" type=\"\" description=\"\" label=\"\" default=\"\"><Option name=\"\" value=\"\" /></Preference>";
	}
	private String property(){
		return "<Property name=\"\" type=\"\" label=\"\" />";
	}
	private String endpoint(String endpointType){
		return "<" + endpointType + " name=\"\" type=\"\" description=\"\" label=\"\" friendcode=\"\"></" + endpointType + ">";
	}
	private String link(){
		return "<XHTML href=\"index.html\" contenttype=\"text/html\" cacheable=\"false\" use-platform-style=\"true\"/>";
	}
	private String rendering(){
		return "<Platform.Rendering width=\"5\" height=\"20\"/>";
	}

}
