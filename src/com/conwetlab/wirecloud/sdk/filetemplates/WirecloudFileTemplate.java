package com.conwetlab.wirecloud.sdk.filetemplates;

import java.util.ArrayList;
import java.util.Formatter;

public abstract class WirecloudFileTemplate {
	
	protected Formatter template;
	protected String firstUpperProjectName;
	protected String firstLowerProjectName;
	protected String displayName;
	
	public WirecloudFileTemplate(){
		this.template = new Formatter();
	}
	
	protected abstract void createTemplate();
	
	public String getTemplateContent(){
		this.createTemplate();
		return this.template.toString();
	}
	
	protected String getFirstUpperProjectName(String projectName){
		StringBuffer firstUpperProjectName = new StringBuffer(projectName);
		firstUpperProjectName.setCharAt(0, Character.toUpperCase(firstUpperProjectName.charAt(0)));
		return firstUpperProjectName.toString();
	}
	
	protected String getFirstLowerProjectName(String projectName){
		StringBuffer firstLowerProjectName = new StringBuffer(projectName);
		firstLowerProjectName.setCharAt(0, Character.toLowerCase(firstLowerProjectName.charAt(0)));
		return firstLowerProjectName.toString();
	}
	
	protected String humanReadableProjectName(String projectName){
		String projectNameAux = new String(projectName);
		ArrayList<String> splitedProjectName = this.splitProjecName(projectNameAux);
		StringBuffer humanReadableProjectName = new StringBuffer();
		
		if(! splitedProjectName.isEmpty()){
			
			humanReadableProjectName.append(this.getFirstUpperProjectName(splitedProjectName.get(0)));
			
			for(int i = 1; i < splitedProjectName.size(); i++){
				humanReadableProjectName.append(" ");
				humanReadableProjectName.append(this.getFirstUpperProjectName(splitedProjectName.get(i)));
			}
			
			return humanReadableProjectName.toString();
		}
		
		else{
			return this.getFirstUpperProjectName(projectName);
		}
	}
	
	private ArrayList<String> splitProjecName(String projectName){
		String projectNameAux = this.getFirstLowerProjectName(projectName);
		ArrayList<String> splitedProjectName = new ArrayList<String>();
		
		int beginSubStr = 0, endSubStr = 0, nextSubStr = 0, i = 0;
		
		for(i = 0; i < projectNameAux.length(); ++i){
			if(Character.isUpperCase(projectNameAux.charAt(i))){
				beginSubStr = nextSubStr; 
				endSubStr = i;
				nextSubStr = i;
				splitedProjectName.add(projectNameAux.substring(beginSubStr, endSubStr));
			}
			endSubStr = i;
		}
		
		splitedProjectName.add(projectNameAux.substring(nextSubStr, i));
		
		return splitedProjectName;
	}
}
