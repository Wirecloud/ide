package com.conwetlab.wirecloud.sdk.filetemplates;


public class WidgetHtmlFileTemplate extends WirecloudFileTemplate {

	
	public WidgetHtmlFileTemplate (String projectName){
		super();
		this.displayName = super.humanReadableProjectName(projectName);
	}
		
	@Override
	protected void createTemplate() {
		this.createHtmlTemplate();
	}
	
	private void createHtmlTemplate(){
		this.template.format(this.templateFormat(), 
				this.createHtml5Tag(), this.createOpenHtmlTag(), this.createOpenHeadTag(), this.createMetaTag(), this.titleTag(),
				this.createCssSection(), this.createLinkCssTag(), this.createJsSection(), this.createScriptTag(), this.createCloseHeadTag(),
				this.createOpenBodyTag(), this.createCloseBodyTag(),	this.createCloseHtmlTag());
	}
	
	private String templateFormat(){
		// Atomic format elements //
		String htmlElement, newLine, tab;
		newLine = "%n"; tab = "\t"; htmlElement = "%s";
		
		// Compound format elements //
		String doctypeFormat, zeroTabHtmlTagFormat, oneTabHtmlTagFormat, twoTabHtmlTagFormat;
		
		doctypeFormat = htmlElement + newLine + newLine;
		zeroTabHtmlTagFormat = htmlElement + newLine;
		oneTabHtmlTagFormat = tab + zeroTabHtmlTagFormat;
		twoTabHtmlTagFormat = tab + oneTabHtmlTagFormat;
	
		// Final template format //
		String templateFormat = doctypeFormat + 				/* doctype */
								zeroTabHtmlTagFormat + 			/* html */
									oneTabHtmlTagFormat +		/* head */
										twoTabHtmlTagFormat +	/* meta */
										twoTabHtmlTagFormat +	/* title */
										newLine +				/* new line */
										twoTabHtmlTagFormat	+	/* Css comment */
										twoTabHtmlTagFormat +	/* link*/	
										newLine +				/* new line */
										twoTabHtmlTagFormat +	/* Js comment  */
										twoTabHtmlTagFormat +	/* script */
									oneTabHtmlTagFormat +		/* head */
									oneTabHtmlTagFormat +		/* body */
									oneTabHtmlTagFormat +		/* body */
								zeroTabHtmlTagFormat;			/* html */
		
		return templateFormat;
	}
	
	private String createHtml5Tag(){
		return "<!DOCTYPE html>";
	}
	
	private String createOpenHtmlTag(){
		return "<html>";
	}

	private String createCloseHtmlTag(){
		return "</html>";
	}
	
	private String createOpenHeadTag(){
		return "<head>";
	}
	
	private String createCloseHeadTag(){
		return "</head>";
	}
	
	private String createMetaTag(){
		return "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
	}
	
	private String createOpenTitleTag(){
		return "<title>";
	}
	
	private String createCloseTitleTag(){
		return "</title>";
	}
	
	private String createDefaultWidgetName(){
		return this.displayName;
	}
	
	private String titleTag(){
		return this.createOpenTitleTag() + this.createDefaultWidgetName() + this.createCloseTitleTag();
	}
	
	private String createCssSection(){
		return "<!-- CSS -->";
	}
	
	private String createJsSection(){
		return "<!-- JS -->";
	}
	
	private String createLinkCssTag(){
		return "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/style.css\"/>";
	}
	
	private String createOpenScriptJsTag(){
		return "<script type=\"text/javascript\" src=\"js/main.js\">";
	}
	
	private String createCloseScriptJsTag(){
		return "</script>";
	}

	private String createScriptTag(){
		return this.createOpenScriptJsTag() + this.createCloseScriptJsTag();
	}
	
	private String createOpenBodyTag(){
		return "<body>";
	}
	
	private String createCloseBodyTag(){
		return "</body>";
	}
}