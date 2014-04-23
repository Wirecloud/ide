/*
 *  Copyright (c) 2013-2014 CoNWeT Lab., Universidad Politécnica de Madrid
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

package com.conwet.wirecloud.ide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.wst.server.ui.wizard.WizardFragment;


public class WirecloudAPI extends WizardFragment {

	private URL urlToPost;
	private static final String OAUTH_INFO_ENDPOINT = ".well-known/oauth";
	private static final String DEFAULT_AUTH_ENDPOINT = "oauth2/auth";
	private static final String DEFAULT_TOKEN_ENDPOINT = "oauth2/token";
	private static final String DEFAULT_UNIVERSAL_REDIRECT_URI_PATH = "oauth2/default_redirect_uri";
	private static final String RESOURCE_COLLECTION_PATH = "api/resources";
	private static final String RESOURCE_ENTRY_PATH = "api/resource";

	private String AUTH_ENDPOINT;
	private String TOKEN_ENDPOINT;
	private String token = null;
	private String mashableComponents = null;
	
	public String UNIVERSAL_REDIRECT_URI;

	public WirecloudAPI(String deploymentServer) throws MalformedURLException {
		this(new URL(deploymentServer));
	}

	public WirecloudAPI(URL deploymentServer) {
		try {
			this.urlToPost = new URL(deploymentServer.getProtocol(), deploymentServer.getHost(), deploymentServer.getPort(), deploymentServer.getPath());
			this.AUTH_ENDPOINT = DEFAULT_AUTH_ENDPOINT;
			this.TOKEN_ENDPOINT = DEFAULT_TOKEN_ENDPOINT;
			this.UNIVERSAL_REDIRECT_URI = new URL(deploymentServer, DEFAULT_UNIVERSAL_REDIRECT_URI_PATH).toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void getOAuthEndpoints() throws IOException {
		WebTarget target = ClientBuilder.newClient().target(this.urlToPost.toString()).path(OAUTH_INFO_ENDPOINT);
		String response = target.request().get(String.class);
		try {
			JSONObject responseData = new JSONObject(response);
			this.AUTH_ENDPOINT = responseData.getString("auth_endpoint");
			this.TOKEN_ENDPOINT = responseData.getString("token_endpoint");
			this.UNIVERSAL_REDIRECT_URI = responseData.getString("default_redirect_uri");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deployWGT(InputStream wgtFile, String token) {
		WebTarget target = ClientBuilder.newClient().target(this.urlToPost.toString()).path(RESOURCE_COLLECTION_PATH);
		String response = target.request()
				.header("Authorization", "Bearer " + token)
				.post(Entity.entity(wgtFile, "application/octet-stream"), String.class);
	} 

	public URL getAuthURL(String clientId, String redirectURI) throws OAuthSystemException, MalformedURLException {
        String url = new URL(this.urlToPost, AUTH_ENDPOINT).toString();

        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(url)
                .setClientId(clientId)
                .setResponseType("code")
                .setRedirectURI(redirectURI.toString())
                .buildQueryMessage();

        try {
        	return new URL(request.getLocationUri());
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        	return null;
        }
	}

	public URL getAuthURL(String clientId) throws OAuthSystemException, MalformedURLException {
        return getAuthURL(clientId, UNIVERSAL_REDIRECT_URI);
	}

	public String obtainAuthToken(String code, String clientId, String clientSecret, String redirectURI) throws MalformedURLException {
		String url = new URL(this.urlToPost, TOKEN_ENDPOINT).toString();

		try {
	            OAuthClientRequest request = OAuthClientRequest
	            	.tokenLocation(url.toString())
	                .setGrantType(GrantType.AUTHORIZATION_CODE)
	                .setClientId(clientId)
	                .setClientSecret(clientSecret)
	                .setRedirectURI(redirectURI.toString())
	                .setCode(code)
	                .buildBodyMessage();

	            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

	            OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);

	            this.token = oAuthResponse.getAccessToken();
	            return token;
	            
	        } catch (OAuthProblemException e) {
	            System.out.println("OAuth error: " + e.getError());
	            System.out.println("OAuth error description: " + e.getDescription());
	        } catch (OAuthSystemException e) {
				e.printStackTrace();
			}

		return null;
	}

	public String obtainAuthToken(String code, String clientId, String clientSecret) throws MalformedURLException {
		return obtainAuthToken(code, clientId, clientSecret, UNIVERSAL_REDIRECT_URI);
	}

	public String getToken() {
		return token;
	}

	private void obtainMashableComponents(){
		this.mashableComponents = null;
		try {
			URL mashableComponetsURL = new URL(this.urlToPost, RESOURCE_COLLECTION_PATH);
			HttpURLConnection conn = (HttpURLConnection) mashableComponetsURL.openConnection();
			conn.setDoInput(true);
			conn.setUseCaches(false);
			
			// Request Headers
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "OAuth2" + " " + token);
			conn.setRequestProperty("Accept", "application/json");
			
			//Get Response	
		    InputStream is = conn.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    String line;
		    StringBuffer response = new StringBuffer(); 
		    while((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    mashableComponents = response.toString();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getMashableComponents(){
		obtainMashableComponents();
		return mashableComponents;
	}

	public void deleteCatalogueResource(String resource) {
		try {
			URL resourceURL = manageURL(this.urlToPost, RESOURCE_ENTRY_PATH, resource);
			HttpURLConnection conn = (HttpURLConnection) resourceURL.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			// Request Headers
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Authorization", "OAuth2" + " " + token);
			conn.setRequestProperty("Accept", "application/json");
			
			//Get Response	
		    InputStream is = conn.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    String line;
		    StringBuffer response = new StringBuffer(); 
		    while((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    //System.out.println(response.toString());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private URL manageURL(URL base, String path, String resource) {
		URL ret = null;
		try {
			ret = new URL(base, new URI(null, null, path + "/" + resource, null, null).toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	public void setToken(String TOKEN) {
		this.token = TOKEN;
	}
}
