package com.conwet.wirecloud.ide;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;

public class WirecloudAPITest extends TestCase {

    private final String url = "http://wirecloud.example.com";
    private final String clientId = "2412";
    private final String clientSecret = "asdasd995c576d4270653aa5a5f0f3a28fa5dee30265a890f1fb6df91fe12ae45feb22637cd912774807681af10eea298193bd0d1ff9e3d5fcc0f6a5f1d7ddb724abd0";
    private final String UNIVERSAL_REDIRECT_URI = "http://auth.example.com/oauth2/default_redirect_uri";
    private final CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
    private final CloseableHttpClient mockedHttpClient = mock(CloseableHttpClient.class);
    private final HttpEntity mockedEntity = mock(HttpEntity.class);
    private final StatusLine mockedStatusLine = mock(StatusLine.class);
    private final OAuthClient mockedOAuthClient = mock(OAuthClient.class);
    private final OAuthJSONAccessTokenResponse mockedOAuthResponse = mock(OAuthJSONAccessTokenResponse.class);
    private final WirecloudAPI mockedAPI;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockedAPI.AUTH_ENDPOINT = "http://wirecloud.example.com/authorize";
        mockedAPI.TOKEN_ENDPOINT = "http://wirecloud.example.com/token";
    }

    public WirecloudAPITest(String testname) throws MalformedURLException {
        super(testname);

        mockedAPI = spy(new WirecloudAPI(url));
    }

    @Test
    public void testDeployWGT() throws ClientProtocolException, IOException,
            FailureResponseException {

        File wgtFile = new File("File");
        String token = null;
        doReturn(mockedHttpClient).when(mockedAPI).createHttpClient(
                eq(new URL("http://wirecloud.example.com/api/resources")));
        doReturn(mockedResponse).when(mockedHttpClient).execute(
                any(HttpUriRequest.class));
        doReturn(mockedStatusLine).when(mockedResponse).getStatusLine();
        doReturn(201).when(mockedStatusLine).getStatusCode();

        mockedAPI.deployWGT(wgtFile, token);
    }

    @Test
    public void testDeployWGT400() throws ClientProtocolException, IOException {
        File wgtFile = new File("File");
        String token = null;
        doReturn(mockedHttpClient).when(mockedAPI).createHttpClient(
                eq(new URL("http://wirecloud.example.com/api/resources")));
        doReturn(mockedResponse).when(mockedHttpClient).execute(
                any(HttpUriRequest.class));

        doReturn(mockedStatusLine).when(mockedResponse).getStatusLine();
        doReturn(400).when(mockedStatusLine).getStatusCode();
        doReturn(mockedEntity).when(mockedResponse).getEntity();
        String response = "{\"description\": \"Error parsing config.xml descriptor file\", \"details\": \"missing required field: t:Vendor\"}";
        InputStream is = new ByteArrayInputStream(response.getBytes());
        doReturn(is).when(mockedEntity).getContent();

        try {
            mockedAPI.deployWGT(wgtFile, token);
            fail("Should throw a exception.");
        } catch (FailureResponseException e) {

        }
    }

    @Test
    public void testGetOAuthEndpoints() throws IOException,
            UnexpectedResponseException {
        doReturn(mockedHttpClient).when(mockedAPI).createHttpClient(
                eq(new URL("http://wirecloud.example.com/.well-known/oauth")));
        doReturn(mockedResponse).when(mockedHttpClient).execute(
                any(HttpUriRequest.class));
        doReturn(mockedStatusLine).when(mockedResponse).getStatusLine();
        doReturn(200).when(mockedStatusLine).getStatusCode();
        doReturn(mockedEntity).when(mockedResponse).getEntity();
        String response = "{\"default_redirect_uri\": \"http://auth.example.com/oauth2/default_redirect_uri\", \"token_endpoint\": \"http://wirecloudtest.example.com/token\", \"version\": \"2.0\", \"auth_endpoint\": \"http://wirecloudtest.example.com/authorize\"}";
        InputStream is = new ByteArrayInputStream(response.getBytes());
        doReturn(is).when(mockedEntity).getContent();

        mockedAPI.getOAuthEndpoints();

        assertEquals("http://wirecloudtest.example.com/authorize",
                mockedAPI.AUTH_ENDPOINT);
        assertEquals("http://wirecloudtest.example.com/token",
                mockedAPI.TOKEN_ENDPOINT);
        assertEquals("http://auth.example.com/oauth2/default_redirect_uri",
                mockedAPI.UNIVERSAL_REDIRECT_URI);
    }

    @Test
    public void testGetAuthURL() throws MalformedURLException,
            OAuthSystemException {
        URL expectedURL = new URL(
                "http://wirecloud.example.com/authorize?response_type=code&redirect_uri=http%3A%2F%2Fauth.example.com%2Foauth2%2Fdefault_redirect_uri&client_id=2412");
        assertEquals(expectedURL,
                mockedAPI.getAuthURL(clientId, UNIVERSAL_REDIRECT_URI));
    }

    @Test
    public void testGetAuthURLInvalidURLs() throws OAuthSystemException {

        try {
            new WirecloudAPI("wrongURL");
            fail("Should throw a exception.");
        } catch (MalformedURLException e1) {
            // Success
        }

        mockedAPI.AUTH_ENDPOINT = " á://?";
        try {
            mockedAPI.getAuthURL(clientId, "url");
            fail("Should throw a exception.");
        } catch (MalformedURLException e) {
            // Success
        }
    }

    @Test
    public void testObtainAuthToken() throws MalformedURLException,
            OAuthSystemException, OAuthProblemException {

        doReturn(mockedOAuthClient).when(mockedAPI).createoAuthClient(
                any(OAuthClientRequest.class));
        doReturn(mockedOAuthResponse).when(mockedOAuthClient).accessToken(
                any(OAuthClientRequest.class),
                eq(OAuthJSONAccessTokenResponse.class));
        doReturn("token").when(mockedOAuthResponse).getAccessToken();
        assertEquals("token", mockedAPI.obtainAuthToken("code", clientId,
                clientSecret, UNIVERSAL_REDIRECT_URI));
    }

    @Test
    public void testObtainMashableComponents() throws ClientProtocolException,
            IOException {
        doReturn(mockedHttpClient).when(mockedAPI).createHttpClient(
                eq(new URL("http://wirecloud.example.com/api/resources")));
        doReturn(mockedResponse).when(mockedHttpClient).execute(
                any(HttpUriRequest.class));
        doReturn(mockedStatusLine).when(mockedResponse).getStatusLine();
        doReturn(200).when(mockedStatusLine).getStatusCode();
        doReturn(mockedEntity).when(mockedResponse).getEntity();
        String response = "";
        InputStream is = new ByteArrayInputStream(response.getBytes());
        doReturn(is).when(mockedEntity).getContent();

        try {
            mockedAPI.obtainMashableComponents();
            fail("UnexpectedResponse should be thrown.");
        } catch (UnexpectedResponseException e) {
        }

    }

    @Test
    public void testUninstallResource() throws ClientProtocolException,
            IOException, UnexpectedResponseException {
        doReturn(mockedHttpClient).when(mockedAPI).createHttpClient(
                eq(new URL("http://wirecloud.example.com/api/resource/WireCloud/Test/1.0")));
        doReturn(mockedResponse).when(mockedHttpClient).execute(
                any(HttpUriRequest.class));
        doReturn(mockedStatusLine).when(mockedResponse).getStatusLine();
        doReturn(204).when(mockedStatusLine).getStatusCode();

        mockedAPI.uninstallResource("WireCloud/Test/1.0");
    }

    @Test
    public void testUninstallResourceUnexpectedError() throws ClientProtocolException,
            IOException {
        doReturn(mockedHttpClient).when(mockedAPI).createHttpClient(
                eq(new URL("http://wirecloud.example.com/api/resource/WireCloud/Test/1.0")));
        doReturn(mockedResponse).when(mockedHttpClient).execute(
                any(HttpUriRequest.class));
        doReturn(mockedStatusLine).when(mockedResponse).getStatusLine();
        doReturn(200).when(mockedStatusLine).getStatusCode();

        try {
            mockedAPI.uninstallResource("WireCloud/Test/1.0");
            fail("UnexpectedResponse should be thrown.");
        } catch (UnexpectedResponseException e) {
            // Everything worked as expected
        }
    }
}
