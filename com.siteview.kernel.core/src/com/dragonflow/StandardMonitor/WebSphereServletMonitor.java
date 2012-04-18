/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WebSphereServletMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>WebSphereServletMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.SiteView.XMLMonitor;
import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class WebSphereServletMonitor extends XMLMonitor
{

    static StringProperty pPort;
    static StringProperty pServletURL;
    static StringProperty pTarget;

    public WebSphereServletMonitor()
    {
    }

    public Array getConnectionStandardProperties()
    {
        Array array = super.getConnectionStandardProperties();
        array.add(pServletURL);
        array.add(pPort);
        return array;
    }

    public int getPort()
    {
        String s = getProperty(pPort);
        if(s.length() == 0)
        {
            return super.getPort();
        } else
        {
            return Integer.parseInt(s);
        }
    }

    public String getMetricListURL()
    {
        return getProperty(pServletURL);
    }

    public String getPostMetricURL()
    {
        return null;
    }

    public String getMetricDataURL()
    {
        return getProperty(pServletURL);
    }

    public String getMetricListXSL()
    {
        return "templates.applications/WASServlet.xsl";
    }

    public String getPostMetricXSL()
    {
        return null;
    }

    public String getMetricDataXSL()
    {
        return "templates.applications/WASServlet.xsl";
    }

    public String getHostname()
    {
        String s = getProperty(pTarget);
        if(s == null)
        {
            s = getProperty(pHostname);
        }
        return s;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pTarget && s.length() == 0)
        {
            return getProperty(pHostname);
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    static 
    {
        pServletURL = new StringProperty("_url", "");
        pServletURL.setDisplayText("Servlet URL", "URL of Performance Servlet. On a WAS 4.0 server, the default URL is /wasPerfTool/servlet/perfservlet. On earlier versions, the URL is chosen during the installation of the Servlet. In either case, the URL can be found in the Servlet properties page on the Admin Console.");
        pServletURL.setParameterOptions(false, 4, false);
        pPort = new StringProperty("_port", "");
        pPort.setDisplayText("Port", "port number on server - default is 80 for non-secure & 443 for secure");
        pPort.setParameterOptions(false, 4, false);
        pTarget = new StringProperty("_target");
        pTarget.setDisplayText("Target", "the logical name of the server. If empty, the hostname will be used.");
        pTarget.setParameterOptions(true, 5, false);
        StringProperty astringproperty[] = {
            pServletURL, pPort, pTarget
        };
        addProperties("com.dragonflow.StandardMonitor.WebSphereServletMonitor", astringproperty);
        addClassElement("com.dragonflow.StandardMonitor.WebSphereServletMonitor", Rule.stringToClassifier("status != OK\terror"));
        addClassElement("com.dragonflow.StandardMonitor.WebSphereServletMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "description", "Monitors a IBM WebSphere Application Server 3.02 or 3.5 using a Performance Servlet");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "help", "WebSphereServletMon.htm");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "title", "WebSphere Performance Servlet");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "class", "WebSphereServletMonitor");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "target", "_target");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "topazName", "WebSphereServlet");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "classType", "application");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "topazType", "Web Application Server");
        setClassProperty("com.dragonflow.StandardMonitor.WebSphereServletMonitor", "loadable", "true");
    }
}
