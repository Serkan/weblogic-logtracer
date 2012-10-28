package org.logtracker.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@ApplicationScoped
@ManagedBean(name = "LogBroadcaster")
public class LogBroadcaster {

    private boolean isStarted;

    private List<String> serverList = new ArrayList<String>();

    private Map<String, String> serverMap = new HashMap<String, String>();

    public void initTracker() {
        if (!isStarted) {
            extractServerInfo();

            for (String serverName : serverMap.keySet()) {
                serverList.add(serverName);
            }

            for (Entry<String, String> server : serverMap.entrySet()) {
                String name = server.getKey();
                String listenAddress = server.getValue();
                if (listenAddress == null) {
                    bindLocalServer(name);
                } else {
                    bindRemoteServer(name);
                }
            }

        }
        isStarted = true;
    }

    private void bindRemoteServer(final String name) {
        File logFile = new File(BaseConstants.getInstance().get(
                BaseConstants.DOMAIN_HOME)
                + "/remote_servers/" + name + "/logs/" + name + ".log");

        Tailer tailer = new Tailer(logFile, new TailerListenerAdapter() {

            @Override
            public void handle(String line) {
                push(name, line);
            }
        }, 100);
        Thread thread = new Thread(tailer);
        thread.start();

    }

    private void bindLocalServer(final String name) {
        File logFile = new File(BaseConstants.getInstance().get(
                BaseConstants.DOMAIN_HOME)
                + "/servers/" + name + "/logs/" + name + ".log");

        Tailer tailer = new Tailer(logFile, new TailerListenerAdapter() {

            @Override
            public void handle(String line) {
                push(name, line);
            }
        }, 100);
        Thread thread = new Thread(tailer);
        thread.start();

    }

    private void extractServerInfo() {
        File config = new File(BaseConstants.getInstance().get(
                BaseConstants.DOMAIN_HOME)
                + "/config/config.xml");
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(config);
            NodeList servers = doc.getElementsByTagName("server");
            for (int i = 0; i < servers.getLength(); i++) {
                Node item = servers.item(i);
                // server configuration iteration
                String serverName = null;
                String listenAddress = null;
                // server properties
                NodeList serverProperties = item.getChildNodes();
                for (int k = 0; k < serverProperties.getLength(); k++) {
                    Node property = serverProperties.item(k);
                    String nodeName = property.getNodeName();
                    if (nodeName.equals("name")) {
                        serverName = property.getFirstChild().getNodeValue();
                    } else if (nodeName.equals("listen-address")) {
                        if (property.getFirstChild() != null) {
                            listenAddress = property.getFirstChild()
                                    .getNodeValue();
                        }
                    }
                }
                boolean exist = ExcludeList.getInstance().exist(serverName);
                if (!exist) {
                    serverMap.put(serverName, listenAddress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void push(String name, String msg) {
        PushContext pushContext = PushContextFactory.getDefault()
                .getPushContext();
        JSONObject json = new JSONObject();
        try {
            json.append("channel", name);
            json.append("message", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pushContext.push("/serverlog", json.toString());
        System.out.println(msg);

    }

    /**
     * @return the serverList
     */
    public List<String> getServerList() {
        return serverList;
    }

    /**
     * @param serverList the serverList to set
     */
    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }

}
