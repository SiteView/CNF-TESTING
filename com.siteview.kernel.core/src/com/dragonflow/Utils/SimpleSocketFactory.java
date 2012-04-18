/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.net.Socket;

public class SimpleSocketFactory extends javax.net.SocketFactory {

    private static com.dragonflow.Utils.SimpleSocketFactory default_factory = null;

    private int timeout;

    public SimpleSocketFactory() {
        java.lang.System.out.println("ssf instantiated.");
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        timeout = (int) siteviewgroup.getSettingAsLong("_ldapSocketTimeout");
        if (timeout <= 0) {
            timeout = 30000;
        }
    }

    public static javax.net.SocketFactory getDefault() {
        synchronized (com.dragonflow.Utils.SimpleSocketFactory.class) {
            if (default_factory == null) {
                default_factory = new SimpleSocketFactory();
            }
        }
        return default_factory;
    }

    public java.net.Socket createSocket(String s, int i) throws java.io.IOException, java.net.UnknownHostException {
        java.lang.System.out.println("create s1 called " + timeout);
        java.net.Socket socket = new Socket(s, i);
        socket.setSoTimeout(timeout);
        return socket;
    }

    public java.net.Socket createSocket(java.net.InetAddress inetaddress, int i) throws java.io.IOException, java.net.UnknownHostException {
        java.lang.System.out.println("create s2 called " + timeout);
        java.net.Socket socket = new Socket(inetaddress, i);
        socket.setSoTimeout(timeout);
        return socket;
    }

    public java.net.Socket createSocket(java.net.InetAddress inetaddress, int i, java.net.InetAddress inetaddress1, int j) throws java.io.IOException, java.net.UnknownHostException {
        java.net.Socket socket = new Socket(inetaddress, i, inetaddress1, j);
        socket.setSoTimeout(timeout);
        return socket;
    }

    public java.net.Socket createSocket(String s, int i, java.net.InetAddress inetaddress, int j) throws java.io.IOException, java.net.UnknownHostException {
        java.net.Socket socket = new Socket(s, i, inetaddress, j);
        socket.setSoTimeout(timeout);
        return socket;
    }

}
