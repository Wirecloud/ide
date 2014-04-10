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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.eclipse.wst.server.ui.wizard.WizardFragment;


public class WirecloudAPI extends WizardFragment {

	private URL urlToPost;
	private static final String AUTH_ENDPOINT = "oauth2/auth";
	private static final String AUTH_TOKEN = "oauth2/token";
	private static final String RESOURCE_COLLECTION_PATH = "api/resources";
	private static final String RESOURCE_ENTRY_PATH = "api/resource";
	private String token = null;
	private String mashableComponents = null;
	private String wirecloudID;
	private String wirecloudSecret;
	
	private static final String UNIVERSAL_REDIRECT_URI_PATH = "oauth2/default_redirect_uri";
	public URL UNIVERSAL_REDIRECT_URI;

	public WirecloudAPI(String deploymentServer,String wirecloudID, String wirecloudSecret) throws MalformedURLException {
		this(new URL(deploymentServer),wirecloudID,wirecloudSecret);
	}

	public WirecloudAPI(URL deploymentServer, String wirecloudID, String wirecloudSecret) {
		try {
			this.urlToPost = new URL(deploymentServer.getProtocol(), deploymentServer.getHost(), deploymentServer.getPort(), deploymentServer.getPath());
			this.UNIVERSAL_REDIRECT_URI = new URL(deploymentServer, UNIVERSAL_REDIRECT_URI_PATH);
			this.wirecloudID = wirecloudID;
			this.wirecloudSecret = wirecloudSecret;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public void deployWGT(String wgtFile, String token) {
		try {
			httpConn(new URL(this.urlToPost, RESOURCE_COLLECTION_PATH), new File(wgtFile), token);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			
		}
	} 

	public void deployWGT(File wgtFile, String token) {
		try {
			httpConn(new URL(this.urlToPost, RESOURCE_COLLECTION_PATH), wgtFile, token);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			
		}
	} 

	public URL getAuthURL(String clientId, URL redirectURI) throws OAuthSystemException {
		String url = this.urlToPost.toString() + AUTH_ENDPOINT;

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

	public URL getAuthURL(String clientId) throws OAuthSystemException {
        return getAuthURL(clientId, UNIVERSAL_REDIRECT_URI);
	}

	public String obtainAuthToken(String code, URL redirectURI) {
		String url = this.urlToPost.toString() + AUTH_TOKEN;

		try {
	            OAuthClientRequest request = OAuthClientRequest
	            	.tokenLocation(url.toString())
	                .setGrantType(GrantType.AUTHORIZATION_CODE)
	                .setClientId(this.wirecloudID)
	                .setClientSecret(this.wirecloudSecret)
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

	public String obtainAuthToken(String code) {
		return obtainAuthToken(code, UNIVERSAL_REDIRECT_URI);
	}

	public String getToken() {
		return token;
	}
	
	private void httpConn(URL url, File wgtFile, String token) {
		try {
			FileInputStream wgt = new FileInputStream(wgtFile);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// Request Headers
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "OAuth2" + " " + token);
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			conn.setRequestProperty("Content-Length", "" + wgt.available());
			conn.setRequestProperty("Accept", "application/json");

			OutputStream out = conn.getOutputStream();

			byte[] buffer = new byte[1024];
			int len = wgt.read(buffer);
			while (len != -1) {
			    out.write(buffer, 0, len);
			    len = wgt.read(buffer);
			}
			out.flush();
			wgt.close();
			out.close();
			
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
			
			//conn.disconnect();	

		} catch (IOException e) {
			e.printStackTrace();
		}	
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
