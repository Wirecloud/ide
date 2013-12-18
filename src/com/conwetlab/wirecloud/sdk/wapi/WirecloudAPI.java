package com.conwetlab.wirecloud.sdk.wapi;

import java.io.BufferedReader;
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


public class WirecloudAPI {

	private URL urlToPost;
	private static final String AUTH_ENDPOINT = "/oauth2/auth";
	private static final String AUTH_TOKEN = "/oauth2/token";
	private static final String RESOURCE_COLLECTION_PATH = "/api/resources";
	private static final String RESOURCE_ENTRY_PATH = "/api/resource/";
	private String token = null;
	private String mashableComponents = null;
	
	private static final String UNIVERSAL_REDIRECT_URI_PATH = "/oauth2/default_redirect_uri";
	public String UNIVERSAL_REDIRECT_URI;

	public WirecloudAPI(String deploymentServer) throws MalformedURLException {
		this.urlToPost = new URL(deploymentServer);
		this.UNIVERSAL_REDIRECT_URI = this.urlToPost + UNIVERSAL_REDIRECT_URI_PATH;
	}

	public void deployWGT(String wgtFile, String token) {
		try {
			httpConn(new URL(this.urlToPost, RESOURCE_COLLECTION_PATH), wgtFile, token);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			
		}
	} 

	public String getAuthURL(String clientId, String redirectURI) throws OAuthSystemException {
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(this.urlToPost + AUTH_ENDPOINT)
                .setClientId(clientId)
                .setResponseType("code")
                .setRedirectURI(redirectURI)
                .buildQueryMessage();

        return request.getLocationUri();
	}

	public String getAuthURL(String clientId) throws OAuthSystemException {
        return getAuthURL(clientId, UNIVERSAL_REDIRECT_URI);
	}

	public String obtainAuthToken(String code, String redirectURI) {
		 try {
	            OAuthClientRequest request = OAuthClientRequest
	            	.tokenLocation(this.urlToPost + AUTH_TOKEN)
	                .setGrantType(GrantType.AUTHORIZATION_CODE)
	                .setClientId("WirecloudIDE")
	                .setClientSecret("WirecloudSecret")
	                .setRedirectURI(redirectURI)
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

	public String getToken (){
		return token;
	}
	
	private void httpConn(URL url, String wgtFile, String token) {
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
			URI resourceToDelete = manageURL(this.urlToPost, RESOURCE_ENTRY_PATH, resource);
			URL resourceURL = resourceToDelete.toURL();
			HttpURLConnection conn = (HttpURLConnection) resourceURL.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			// Request Headers
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Authorization", "OAuth2" + " " + token);
			//conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private URI manageURL(URL host, String path, String resource){
		URI ret = null;
		try {
			ret = new URI(host.getProtocol(), null, host.getHost(), host.getPort(), path + resource, null, null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
}
