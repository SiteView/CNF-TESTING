/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * VMWareMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>VMWareMonitor</code>
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

public class VMWareMonitor extends BrowsableSNMPBase
{

    private String VMWareMIB;

    public VMWareMonitor()
    {
        VMWareMIB = "NO_VMWARE_MIB_AVAILABLE";
    }

    public Array getConnectionProperties()
    {
        Array array = super.getConnectionProperties();
        return array;
    }

    protected String getMonitorType()
    {
        return "VMWare";
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
                if(browsablemib.containsMIB(VMWareMIB))
                {
                    vector.add(VMWareMIB);
                    vector.add(VMWareMIB);
                }
            }
            catch(Exception exception)
            {
                LogManager.log("Error", "VMWare Monitor could not get BrowsableMIB instance: " + exception.getMessage());
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
        addProperties("COM.dragonflow.StandardMonitor.VMWareMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.VMWareMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.VMWareMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "description", "Monitors VMWare servers using snmp.");
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "help", "VMWareMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "title", "VM Ware");
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "class", "VMWareMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "topazName", "VMWare");
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "topazType", "Load Balancer");
        if(TextUtils.getValue(hashmap, "_allowVMWare").length() > 0)
        {
            setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "loadable", "true");
        } else
        {
            setClassProperty("COM.dragonflow.StandardMonitor.VMWareMonitor", "loadable", "false");
        }
    }
}
