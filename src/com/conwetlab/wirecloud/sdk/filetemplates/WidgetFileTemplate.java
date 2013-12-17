package com.conwetlab.wirecloud.sdk.filetemplates;

public class WidgetFileTemplate extends WirecloudFileTemplate {
	
	public WidgetFileTemplate(String projectName){
		super();
		this.firstLowerProjectName = super.getFirstLowerProjectName(projectName);
		this.firstUpperProjectName = super.getFirstUpperProjectName(projectName);
	}
	
	@Override
	protected void createTemplate() {
		this.createJsTemplate();		
	}
	
	private void createJsTemplate(){
		this.template.format(this.templateFormat(),
							  this.createComment("jshint browser:true"),
							  this.createComment("global MashupPlatform"),
							  this.beginJsClass(),
							  this.useStrict(),
							  this.jsConstructorFunction(),
							  this.endJsFunction(),
							  this.jsInitFunction(),
							  this.endJsFunction(),
							  this.windowAtribute(),
							  this.endJsClass(),
							  this.newWidgetObject(),
							  this.listener());
	}
	
	private String templateFormat(){
		String templateFormat;
		
		// Atomic format elements //
		String jsElement, newLine, tab;
		newLine = "%n"; tab = "\t"; jsElement = "%s";
		
		// Compound format elements //
		String zeroTab, oneTab, function;
		zeroTab = jsElement +  newLine;
		oneTab = tab + zeroTab;
		function = oneTab + newLine + oneTab;
		
		templateFormat = zeroTab 	+ 	/* comment */
						 zeroTab 	+ 	/* comment */
						 newLine 	+
						 zeroTab 	+ 	/* js class */
						 newLine 	+
						 oneTab 	+  	/* use strict */
						 function	+	/* js constructor */
						 newLine	+
						 function	+ 	/* init */
						 newLine	+	
						 oneTab		+	/* window */
						 newLine	+
						 zeroTab	+	/* end js class */
						 newLine	+	
						 zeroTab	+	/* new widget object */
						 newLine	+	
						 zeroTab;		/* listener */
		
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
	
	private String beginJsClass(){
		return "(function() {";
	}
	
	private String endJsClass(){
		return "})();";
	}
	
	private String useStrict(){
		return "\"use strict\";";
	}
	
	private String createComment(String content){
		return this.openComment() +  this.content(content) + this.closeComment();
	}
	
	private String jsConstructorFunction(){
		return "var " + this.firstUpperProjectName + " = function " + this.firstUpperProjectName + "() {";
	}
	
	private String endJsFunction(){
		return "};";
	}
	
	private String jsInitFunction(){
		return this.firstUpperProjectName + ".prototype.init = function init() {";
	}
	
	private String windowAtribute(){
		return "window." + this.firstUpperProjectName + " = " + this.firstUpperProjectName + ";";
	}
	
	private String newWidgetObject(){
		return "var " + this.firstLowerProjectName + " = new " + this.firstUpperProjectName + "();";
	}
	
	private String listener(){
		return "window.addEventListener(\"DOMContentLoaded\", " + this.firstLowerProjectName + ".init.bind(" + this.firstLowerProjectName + "), false);";
	}
}
