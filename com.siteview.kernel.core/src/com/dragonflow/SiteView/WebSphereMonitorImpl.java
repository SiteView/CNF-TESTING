/*
 * 
 * Created on 2005-2-16 17:34:07
 *
 * WebSphereMonitorImpl.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>WebSphereMonitorImpl</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import com.dragonflow.Utils.WebSphere.ConnectionException;
import com.dragonflow.Utils.WebSphere.WebSphereCounter;

public abstract class WebSphereMonitorImpl {

    protected String username;

    protected String password;

    protected String port;

    protected String host;

    boolean debug;

    public WebSphereMonitorImpl(String s, String s1, String s2, String s3) {
        port = "900";
        debug = Boolean.getBoolean("com.dragonflow.Utils.WebSphere.debug");
        host = s;
        port = s1;
        username = s2;
        password = s3;
    }

    protected void error(String s, Exception exception) {
        System.err.println(s);
        if (debug && exception != null) {
            exception.printStackTrace();
        }
    }

    protected void error(String s) {
        error(s, null);
    }

    public abstract void connect() throws ConnectionException;

    public abstract boolean getCounterList(StringBuffer stringbuffer);

    public abstract WebSphereCounter[] getCounterValues(WebSphereCounter awebspherecounter[]);
}
