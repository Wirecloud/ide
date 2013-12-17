package com.conwetlab.wirecloud.sdk.wizards.retreatment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.conwetlab.wirecloud.sdk.wapi.WirecloudAPI;

public class RetreatmentsManager {
	
	private static RetreatmentsManager INSTANCE = null;
	
	private WirecloudAPI wirecloudAPI;
	private HashMap<String, String> mashableComponents;
	private ArrayList<String> listToRetreat;
	
	private RetreatmentsManager(WirecloudAPI wapi){
		this.wirecloudAPI = wapi;
		this.mashableComponents = new HashMap<String, String>();
		this.listToRetreat = null;
	}
	
	// Singleton pattern
	private static void createInstance(WirecloudAPI wapi){
		if(INSTANCE == null){
			INSTANCE = new RetreatmentsManager(wapi);
		}
	}
	
	 public static RetreatmentsManager getInstance(WirecloudAPI wapi) {
	        createInstance(wapi);
	        return INSTANCE;
	 }

	private JSONObject getJSONFromString(String jsonString){
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			Object obj = parser.parse(jsonString);
			jsonObject = (JSONObject) obj;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getMashableComponents() {
		
		JSONObject mashableComponentsJSON = getJSONFromString(this.wirecloudAPI.getMashableComponents());
		
		Iterator<String> keysIterator = mashableComponentsJSON.keySet().iterator();
		Iterator<?> valuesIterator = mashableComponentsJSON.values().iterator();
		
		while(keysIterator.hasNext() && valuesIterator.hasNext()){
			String mashableComponentkey = keysIterator.next();
			
			JSONObject atributes = getJSONFromString(valuesIterator.next().toString());
			String displayName, version;
			displayName = getDisplayName(atributes);
			version = getVersion(atributes);
			String mashableComponentValue = displayName + " " + version;
			
			mashableComponents.put(mashableComponentkey, mashableComponentValue);
		}
		return mashableComponents;
	}
	
	private String getVersion(JSONObject atributes) {
		return atributes.get("version").toString();
	}

	private String getDisplayName(JSONObject atributes) {
		return atributes.get("display_name").toString() ;
	}

	protected void setListToRetreat(ArrayList<String> list){
		this.listToRetreat = list;
	}

	public void deleteResources() {
		Iterator<String> resourcesToDeleteIterator = this.listToRetreat.iterator();
		
		while(resourcesToDeleteIterator.hasNext()){
			this.wirecloudAPI.deleteCatalogueResource(resourcesToDeleteIterator.next());
		}
		
	}
}
