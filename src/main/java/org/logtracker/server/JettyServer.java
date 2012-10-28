package org.logtracker.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        WebAppContext webapp = new WebAppContext("", "/");
        Server server = new Server(7777);
        server.setHandler(webapp);

        server.start();
        server.join();
    }

}
