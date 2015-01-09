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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.httpclient4.HttpClient4;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class WirecloudAPI extends WizardFragment {

    public final URL url;
    private static final String OAUTH_INFO_ENDPOINT = ".well-known/oauth";
    private static final String DEFAULT_AUTH_ENDPOINT = "oauth2/auth";
    private static final String DEFAULT_TOKEN_ENDPOINT = "oauth2/token";
    private static final String DEFAULT_UNIVERSAL_REDIRECT_URI_PATH = "oauth2/default_redirect_uri";
    private static final String RESOURCE_COLLECTION_PATH = "api/resources";
    private static final String RESOURCE_ENTRY_PATH = "api/resource";

    protected String AUTH_ENDPOINT;
    protected String TOKEN_ENDPOINT;
    private String token = null;

    public String UNIVERSAL_REDIRECT_URI;
    private ServiceTracker proxyTracker = null;

    public WirecloudAPI(String deploymentServer) throws MalformedURLException {
        this(new URL(deploymentServer));
    }

    public WirecloudAPI(URL deploymentServer) {
        try {
            deploymentServer = new URL(deploymentServer.getProtocol(),
                    deploymentServer.getHost(), deploymentServer.getPort(),
                    deploymentServer.getPath());
        } catch (Exception e) {
            // Should not happen as the URL is build from a valid URL
            e.printStackTrace();
        }
        this.url = deploymentServer;
        this.AUTH_ENDPOINT = DEFAULT_AUTH_ENDPOINT;
        this.TOKEN_ENDPOINT = DEFAULT_TOKEN_ENDPOINT;
        try {
            this.UNIVERSAL_REDIRECT_URI = new URL(deploymentServer,
                    DEFAULT_UNIVERSAL_REDIRECT_URI_PATH).toString();
        } catch (MalformedURLException e) {
            // Should not happen as the URL is build from a valid URL using a
            // constant
            e.printStackTrace();
        }
    }

    public IProxyService getProxyService() {
        if (proxyTracker == null) {
            proxyTracker = new ServiceTracker(FrameworkUtil.getBundle(
                    this.getClass()).getBundleContext(),
                    IProxyService.class.getName(), null);
            proxyTracker.open();
        }
        return (IProxyService) proxyTracker.getService();
    }

    protected CloseableHttpClient createHttpClient(URL url) {
        HttpClientBuilder hcBuilder = HttpClients.custom();

        IProxyService proxyService = getProxyService();
        IProxyData[] proxyDataForHost;
        try {
            proxyDataForHost = proxyService.select(url.toURI());
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
        for (IProxyData iProxyData : proxyDataForHost) {
            HttpHost proxy = new HttpHost(iProxyData.getHost(),
                    iProxyData.getPort(), iProxyData.getType());
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
                    proxy);
            hcBuilder.setRoutePlanner(routePlanner);
        }

        return hcBuilder.build();
    }

    public void getOAuthEndpoints() throws IOException,
            UnexpectedResponseException {
        URL url;
        try {
            url = new URL(this.url, OAUTH_INFO_ENDPOINT);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        HttpGet request = new HttpGet(url.toString());

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = createHttpClient(url);
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new UnexpectedResponseException();
            }

            try {
                JSONObject responseData = new JSONObject(
                        EntityUtils.toString(response.getEntity()));
                this.AUTH_ENDPOINT = responseData.getString("auth_endpoint");
                this.TOKEN_ENDPOINT = responseData.getString("token_endpoint");
                this.UNIVERSAL_REDIRECT_URI = responseData
                        .getString("default_redirect_uri");
            } catch (JSONException e) {
                throw new UnexpectedResponseException();
            }
        } finally {
            httpclient.close();
            if (response != null) {
                response.close();
            }
        }
    }

    public void deployWGT(File wgtFile, String token) throws IOException,
            FailureResponseException {
        URL url;
        try {
            url = new URL(this.url, RESOURCE_COLLECTION_PATH);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        HttpPost request = new HttpPost(url.toString());
        request.setHeader("Authorization", "Bearer " + token);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/octet-stream");
        request.setEntity(new FileEntity(wgtFile));

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = createHttpClient(url);
            response = httpclient.execute(request);

            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
            case 201:
                break;
            case 400:
            case 401:
                try {
                    throw FailureResponseException
                            .createFailureException(EntityUtils
                                    .toString(response.getEntity()));
                } catch (JSONException e) {
                    throw new UnexpectedResponseException();
                }
            default:
                throw new UnexpectedResponseException();
            }
        } finally {
            httpclient.close();
            if (response != null) {
                response.close();
            }
        }
    }

    public URL getAuthURL(String clientId, String redirectURI)
            throws OAuthSystemException, MalformedURLException {
        String url = new URL(this.url, AUTH_ENDPOINT).toString();

        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(url).setClientId(clientId)
                .setResponseType("code").setRedirectURI(redirectURI.toString())
                .buildQueryMessage();

        try {
            return new URL(request.getLocationUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public URL getAuthURL(String clientId) throws OAuthSystemException,
            MalformedURLException {
        return getAuthURL(clientId, UNIVERSAL_REDIRECT_URI);
    }

    public String obtainAuthToken(String code, String clientId,
            String clientSecret, String redirectURI)
            throws MalformedURLException {
        String url = new URL(this.url, TOKEN_ENDPOINT).toString();

        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(url.toString())
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId).setClientSecret(clientSecret)
                    .setRedirectURI(redirectURI.toString()).setCode(code)
                    .buildBodyMessage();

            OAuthClient oAuthClient = createoAuthClient(request);
            OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient
                    .accessToken(request, OAuthJSONAccessTokenResponse.class);

            this.token = oAuthResponse.getAccessToken();
            return token;

        } catch (OAuthProblemException e) {
            System.out.println("OAuth error: " + e.getError());
            System.out
                    .println("OAuth error description: " + e.getDescription());
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected OAuthClient createoAuthClient(OAuthClientRequest request)
            throws MalformedURLException {
        return new OAuthClient(new HttpClient4(createHttpClient(new URL(
                request.getLocationUri()))));

    }

    public String obtainAuthToken(String code, String clientId,
            String clientSecret) throws MalformedURLException {
        return obtainAuthToken(code, clientId, clientSecret,
                UNIVERSAL_REDIRECT_URI);
    }

    public String getToken() {
        return token;
    }

    public JSONObject obtainMashableComponents() throws IOException,
            UnexpectedResponseException {
        URL url;
        try {
            url = new URL(this.url, RESOURCE_COLLECTION_PATH);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        HttpGet request = new HttpGet(url.toString());
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = createHttpClient(url);
            request.setHeader("Authorization", "Bearer " + token);
            request.setHeader("Accept", "application/json");
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new UnexpectedResponseException();
            }

            try {
                return new JSONObject(
                        EntityUtils.toString(response.getEntity()));
            } catch (JSONException e) {
                throw new UnexpectedResponseException();
            }
        } finally {
            httpclient.close();
            if (response != null) {
                response.close();
            }
        }
    }

    public void uninstallResource(String resource) throws IOException,
            UnexpectedResponseException {
        URL url = manageURL(this.url, RESOURCE_ENTRY_PATH, resource);
        HttpDelete request = new HttpDelete(url.toString());
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = createHttpClient(url);
            request.setHeader("Authorization", "Bearer " + token);
            request.setHeader("Accept", "application/json");
            response = httpclient.execute(request);

            if (response.getStatusLine().getStatusCode() != 204) {
                throw new UnexpectedResponseException();
            }
        } finally {
            httpclient.close();
            if (response != null) {
                response.close();
            }
        }
    }

    private URL manageURL(URL base, String path, String resource) {
        URL ret = null;
        try {
            ret = new URL(base, new URI(null, null, path + "/" + resource,
                    null, null).toString());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }

        return ret;
    }

    public void setToken(String TOKEN) {
        this.token = TOKEN;
    }
}
