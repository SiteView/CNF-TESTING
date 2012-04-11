/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WebSphereServletMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WebSphereServletMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.XMLMonitor;
import jgl.Array;
import jgl.HashMap;

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
        addProperties("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", Rule.stringToClassifier("status != OK\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "description", "Monitors a IBM WebSphere Application Server 3.02 or 3.5 using a Performance Servlet");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "help", "WebSphereServletMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "title", "WebSphere Performance Servlet");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "class", "WebSphereServletMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "target", "_target");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "topazName", "WebSphereServlet");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "topazType", "Web Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.WebSphereServletMonitor", "loadable", "true");
    }
}
