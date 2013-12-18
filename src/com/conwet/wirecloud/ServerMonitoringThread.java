/*
 *  Copyright (c) 2013 CoNWeT Lab., Universidad Politécnica de Madrid
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

package com.conwet.wirecloud;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMonitoringThread {

	private static final Logger logger = LoggerFactory.getLogger(ServerMonitoringThread.class);

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
            	//e.printStackTrace();
            	fWirecloudServer.setServerStopped();
               logger.debug("HTTP ping failed, connection to server {} was refused", fWirecloudServer.getServer().getHost());
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