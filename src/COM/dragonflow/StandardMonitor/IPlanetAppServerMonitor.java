/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * IPlanetAppServerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>IPlanetAppServerMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Snmp.BrowsableMIB;
import COM.dragonflow.Utils.TextUtils;

import java.util.Vector;
import jgl.Array;

public class IPlanetAppServerMonitor extends BrowsableSNMPBase
{

    private String IPlanetAppServerMIB;

    public IPlanetAppServerMonitor()
    {
        IPlanetAppServerMIB = "NO_IPLANET_APPSERVER_MIB_AVAILABLE";
    }

    public Array getConnectionProperties()
    {
        Array array = super.getConnectionProperties();
        return array;
    }

    protected String getMonitorType()
    {
        return "IPlanet Application Server";
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pMIB)
        {
            Vector vector = new Vector();
            try
            {
                BrowsableMIB browsablemib = BrowsableMIB.getInstance();
                if(browsablemib.containsMIB(IPlanetAppServerMIB))
                {
                    vector.add(IPlanetAppServerMIB);
                    vector.add(IPlanetAppServerMIB);
                }
            }
            catch(Exception exception)
            {
                LogManager.log("Error", "IPlanet AppServer Monitor could not get BrowsableMIB instance: " + exception.getMessage());
            }
            if(vector.size() == 0)
            {
                vector.add("No MIBs Available");
                vector.add("No MIBs Available");
            }
            return vector;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    static 
    {
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "description", "Monitors IPlanet Application servers using snmp.");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "help", "IPASMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "title", "IPlanet Application Server");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "class", "IPlanetAppServerMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "topazName", "IPlanet Application");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "topazType", "Web Application Server");
        if(TextUtils.getValue(hashmap, "_allowIPlanet").length() > 0)
        {
            setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "loadable", "true");
        } else
        {
            setClassProperty("COM.dragonflow.StandardMonitor.IPlanetAppServerMonitor", "loadable", "false");
        }
    }
}
