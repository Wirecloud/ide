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

public class OperatorFileTemplate extends WirecloudFileTemplate {

	public OperatorFileTemplate(){
		super();
	}
	@Override
	protected void createTemplate() {
		this.createJSTemplate();
	}
	
	private void createJSTemplate(){
		this.template.format(	this.templateFormat(),
								this.createComment("jshint browser:true"),
								this.createComment("global MashupPlatform"),
								this.registerCallback(), 
								this.pushEvent(),
								this.closeRegisterCallback());
	}
	
	private String templateFormat(){
		String templateFormat;
		
		// Atomic format elements //
		String jsElement, newLine, tab;
		newLine = "%n"; tab = "\t"; jsElement = "%s";
		
		// Compound format elements //
		String zeroTab, oneTab;
		zeroTab = jsElement +  newLine;
		oneTab = tab + zeroTab;
		
		templateFormat = zeroTab 	+ 	/* comment */
						 zeroTab 	+ 	/* comment */
						 newLine 	+
						 zeroTab	+	/* registerCallback */
						 oneTab		+	/* pushEvent */
						 zeroTab;		/* closeRegistercallback */

		
		return templateFormat;
	}
	
	private String openComment(){
		return "/*";
	}
	
	private String closeComment(){
		return "*/";
	}
	
	private String content(String content){
		return content;
	}
	
	private String createComment(String content){
		return this.openComment() +  this.content(content) + this.closeComment();
	}
	
	private String registerCallback(){
		return "MashupPlatform.wiring.registerCallback(\"nameInput\", function(param) {";
	}
	
	private String pushEvent(){
		return "//MashupPlatform.wiring.pushEvent(\"nameOutput\", param);";
	}
	
	private String closeRegisterCallback(){
		return "});";
	}
}
