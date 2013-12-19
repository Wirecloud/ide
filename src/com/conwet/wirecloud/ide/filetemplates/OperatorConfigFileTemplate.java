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

public class OperatorConfigFileTemplate extends WirecloudFileTemplate {

	public OperatorConfigFileTemplate(String projectName){
		super();
		this.firstUpperProjectName = super.getFirstUpperProjectName(projectName);
		this.displayName = super.humanReadableProjectName(projectName);
	}
	@Override
	protected void createTemplate() {
		this.createOperatorConfigTemplate();
	}
	
	private void createOperatorConfigTemplate(){
		this.template.format(	this.templateFormat(), 
								this.xmlHead(),
								
								this.openRDFTag(),
								this.xmlns("usdl-core=\"http://www.linked-usdl.org/ns/usdl-core#\""),
								this.xmlns("foaf=\"http://xmlns.com/foaf/0.1/\""),
								this.xmlns("rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""),
								this.xmlns("dcterms=\"http://purl.org/dc/terms/\""),
								this.xmlns("skos=\"http://www.w3.org/2004/02/skos/core#\""),
								this.xmlns("wire=\"http://wirecloud.conwet.fi.upm.es/ns/widget#\""),
								this.xmlns("vCard =\"http://www.w3.org/2006/vcard/ns#\""),
								this.xmlns("gr=\"http://purl.org/goodrelations/v1#\">"),
								
								this.printLine("<wire:Operator rdf:about=\"http://wirecloud.com/test\">"),

								this.operatorTitle(),
								this.operatorDisplayName(),
								this.printLine("<dcterms:description></dcterms:description>"),
								this.printLine("<dcterms:creator rdf:resource=\"http://creatoruri/\"/>"),
								this.printLine("<usdl-core:versionInfo>0.1</usdl-core:versionInfo>"),
								this.printLine("<wire:hasImageUri rdf:resource=\"images/catalogue.png\"/>"),
								this.printLine("<usdl-core:utilizedResource rdf:resource=\"js/main.js\"/>"),
								this.printLine("<foaf:page rdf:resource=\"http://conwet.fi.upm.es/docs/display/wirecloud\"/>"),
								this.printLine("<usdl-core:hasProvider rdf:resource=\"http://vendoruri/\"/>"),
								
								this.printLine("<vCard:addr>"),
								this.printLine("<vCard:Work>"),
								this.printLine("<vCard:email>sblanco@conwet.com</vCard:email>"),
								this.printLine("</vCard:Work>"),
								this.printLine("</vCard:addr>"),
								
								this.printLine("<wire:hasPlatformWiring>"),
								this.printLine("<wire:PlatformWiring rdf:ID=\"wiring\">"),
								this.printLine("<wire:hasInputEndpoint>"),
								this.printLine("<wire:InputEndpoint rdf:ID=\"input_1\">"),
								this.printLine("<dcterms:title></dcterms:title>"),
								this.printLine("<dcterms:description></dcterms:description>"),
								this.printLine("<wire:actionLabel></wire:actionLabel>"),
								this.printLine("<wire:type>text</wire:type>"),
								this.printLine("<rdfs:label></rdfs:label>"),
								this.printLine("<wire:friendcode></wire:friendcode>"),
								this.printLine("</wire:InputEndpoint>"),
								this.printLine("</wire:hasInputEndpoint>"),
								this.printLine("<wire:hasOutputEndpoint>"),
								this.printLine("<wire:OutputEndpoint rdf:ID=\"output_1\">"),
								this.printLine("<dcterms:title></dcterms:title>"),
								this.printLine("<dcterms:description></dcterms:description>"),
								this.printLine("<wire:type>text</wire:type>"),
								this.printLine("<rdfs:label></rdfs:label>"),
								this.printLine("<wire:friendcode></wire:friendcode>"),
								this.printLine("</wire:OutputEndpoint>"),
								this.printLine("</wire:hasOutputEndpoint>"),
								this.printLine("</wire:PlatformWiring>"),
								this.printLine("</wire:hasPlatformWiring>"),
								this.printLine("</wire:Operator>"),
								
								this.printLine("<foaf:Person rdf:about=\"http://creatoruri/\">"),
								this.printLine("<foaf:name>sblanco</foaf:name>"),
								this.printLine("</foaf:Person>"),
								
								this.printLine("<gr:BusinessEntity rdf:about=\"http://vendoruri/\">"),
								this.printLine("<foaf:name>CoNWeT</foaf:name>"),
								this.printLine("</gr:BusinessEntity>"),
								
								this.printLine("<foaf:Image rdf:about=\"images/catalogue.png\">"),
								this.printLine("<dcterms:title>ImageURI</dcterms:title>"),
								this.printLine("</foaf:Image>"),
								
								this.printLine("<usdl-core:Resource rdf:about=\"js/main.js\">"),
								this.printLine("<wire:index>0</wire:index>"),
								this.printLine("</usdl-core:Resource>"),
								
								this.printLine("</rdf:RDF>"));		
	}
	
	
	private String templateFormat(){
		String templateFormat;
		
		// Atomic format elements //
		String jsElement, newLine, tab;
		newLine = "%n"; tab = "\t"; jsElement = "%s";
		
		// Compound format elements //
		String zeroTab, oneTab, twoTab, threeTab, fourTab, fiveTab, sixTab;
		zeroTab = jsElement +  newLine;
		oneTab = tab + zeroTab;
		twoTab = tab + oneTab;
		threeTab = tab + twoTab;
		fourTab = tab + threeTab;
		fiveTab = tab + fourTab;
		sixTab =  tab + fiveTab;
		
		templateFormat =zeroTab								+	/* xmlHead */
						zeroTab								+	/* openRDFTag */
							oneTab							+	/* xmlns */
							oneTab							+	/* xmlns */
							oneTab							+	/* xmlns */
							oneTab							+	/* xmlns */
							oneTab							+	/* xmlns */
							oneTab							+	/* xmlns */
							oneTab							+	/* xmlns */
							oneTab							+	/* xmlns */
							newLine							+
							
							oneTab							+	/* wire:Operator */
								twoTab						+	/* dcterms:title */
								twoTab						+	/* wire:displayName */
								twoTab						+	/* dcterms:description */
								twoTab						+	/* dcterms:creator */
								twoTab						+	/* usdl-core:versionInfo */
								twoTab						+	/* wire:hasImageUri */
								twoTab						+	/* usdl-core:utilizableResource */
								twoTab						+	/* foaf:page */
								twoTab						+	/* usdl-core:hasProvider */
								newLine						+
								
								twoTab						+	/* vCard:addr */
									threeTab				+	/* vCard:work */
										fourTab				+	/* vCard:email */
									threeTab				+	/* close vCard:work */
								twoTab						+	/* close vCard:addr */
								newLine						+			
								
								twoTab						+	/* wire:hasPlatformWiring */
									threeTab				+	/* wire:PlatformWiring */
										fourTab				+	/* wire:hasInputEndpoint */
											fiveTab			+	/* wire:InputEndpoint */
												sixTab		+	/* dcterms:title */
												sixTab 		+	/* dcterms:description */
												sixTab		+	/* wire:actionLabel */
												sixTab		+	/* wire:type */
												sixTab		+	/* rdfs:label */
												sixTab		+	/* wire:friendcode */
											fiveTab			+	/* close wire:InputEndpoint */
										fourTab				+	/* close wire:hasInputEndpoint */
										fourTab				+	/* wire:hasOutputEndpoint */
											fiveTab			+	/* wire:OutputEndpoint*/
												sixTab		+	/* dcterms:title */
												sixTab		+	/* dcterms:description */
												sixTab		+	/* wire:type */
												sixTab		+	/* rdfs:label */
												sixTab		+	/* wire:friendcode */
											fiveTab			+	/* close wire:OutputEndpoint */
										fourTab				+	/* close wire:hasOutputEndpoint */
									threeTab				+	/* close wire:PlatformWiring */
								twoTab						+	/* close wire:hasPlatformWiring */
							oneTab							+	/* close wire:Operator */
							newLine							+
							
							oneTab							+	/* foaf:Person */
								twoTab						+	/* foaf:name */
							oneTab							+	/* close foaf:Person  */
							newLine							+	
							
							oneTab							+	/* gr:BusinessEntity */
								twoTab						+	/* foaf:name */
							oneTab							+	/* close gr:BusinessEntity */
							newLine							+	
							
							oneTab							+	/* foaf:Image */
								twoTab						+	/* dcterms:title */
							oneTab							+	/* close foaf:Image */
							newLine							+	
							
							oneTab							+	/* usdl-core:Resource */
								twoTab						+	/* wire:index */
							oneTab							+	/* close usdl-core:Resource */
						zeroTab								;	/* close rdf:RDF */	
							
		return templateFormat;
	}
	
	private String xmlHead(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}
	
	private String openRDFTag(){
		return "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"";
	}
	
	private String xmlns(String xmlns){
		return "xmlns:" + xmlns;
	}
	
	private String printLine(String content){
		return content;
	}
	
	private String operatorTitle(){
		return "<dcterms:title>" + this.firstUpperProjectName + "</dcterms:title>";
	}
	
	private String operatorDisplayName(){
		return "<wire:displayName>" + this.displayName + "</wire:displayName>";
	}

}
