/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * IPlanetWSMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>IPlanetWSMonitor</code>
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

public class IPlanetWSMonitor extends BrowsableSNMPBase
{

    private String IPlanetWSMIB;

    public IPlanetWSMonitor()
    {
        IPlanetWSMIB = "NO_IPLANET_WS_MIB_AVAILABLE";
    }

    public Array getConnectionProperties()
    {
        Array array = super.getConnectionProperties();
        return array;
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
                if(browsablemib.containsMIB(IPlanetWSMIB))
                {
                    vector.add(IPlanetWSMIB);
                    vector.add(IPlanetWSMIB);
                }
            }
            catch(Exception exception)
            {
                LogManager.log("Error", "IPlanet WS Monitor could not get BrowsableMIB instance: " + exception.getMessage());
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

    protected String getMonitorType()
    {
        return "IPlanet WebServer";
    }

    static 
    {
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "description", "Monitors IPlanet Web servers using snmp.");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "help", "IPWSMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "title", "IPlanet Web Server");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "class", "IPlanetWSMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "topazName", "IPlanet Web Server");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "topazType", "Web Server");
        if(TextUtils.getValue(hashmap, "_allowIPlanet").length() > 0)
        {
            setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "loadable", "true");
        } else
        {
            setClassProperty("COM.dragonflow.StandardMonitor.IPlanetWSMonitor", "loadable", "false");
        }
    }
}
