package com.conwetlab.wirecloud.sdk.filetemplates;

public class WidgetCssFileTemplate extends WirecloudFileTemplate {
	
	public WidgetCssFileTemplate(){
		super();
	}

	@Override
	protected void createTemplate() {
		this.createCssTemplate();
	}

	private void createCssTemplate() {
		this.template.format("%s%n\t%s%n\t%s%n%s", "* {", "margin: 0;","padding: 0;","}");		
	}

}
