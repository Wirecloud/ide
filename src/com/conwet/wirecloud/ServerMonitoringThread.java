package com.conwet.wirecloud;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ServerMonitoringThread {

    // delay before pinging starts
    private static final int PING_DELAY = 2000;

    // delay between pings
    private static final int PING_INTERVAL = 10000;

    private boolean stop;

    private String fUrl;
    private WirecloudServerBehaviour fWirecloudServer;

    /**
     * Create a new PingThread.
     * 
     * @param server
     * @param url
     * @param wirecloudServer
     */
    public ServerMonitoringThread(String url, WirecloudServerBehaviour wirecloudServer) {
        super();
        this.fUrl = url;
        this.fWirecloudServer = wirecloudServer;
        Thread t = new Thread() {
            public void run() {
                ping();
            }
        };
        t.setDaemon(true);
        t.start();
    }
    
    /**
     * Ping the server until it is started. Then set the server
     * state to STATE_STARTED.
     */
    protected void ping() {
        try {
            Thread.sleep(PING_DELAY);
        } catch (Exception e) {
            // ignore
        }

        while (!stop) {
            try {
                URL pingUrl = new URL(fUrl);
                URLConnection conn = pingUrl.openConnection();
                int statusCode = ((HttpURLConnection)conn).getResponseCode();

                if (statusCode != 200) {
                	throw new Exception();
                }

                fWirecloudServer.setServerRunning();

            } catch (Exception e) {
            	e.printStackTrace();
                // pinging failed
            	fWirecloudServer.setServerStopped();
            }

            try {
                Thread.sleep(PING_INTERVAL);
            } catch (InterruptedException e2) {
                // ignore
            }
        }
    }

    /**
     * Tell the pinging to stop.
     */
    public void stop() {
        stop = true;
    }
}