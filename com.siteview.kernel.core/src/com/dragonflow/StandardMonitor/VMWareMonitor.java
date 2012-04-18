/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * VMWareMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>VMWareMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.util.Vector;

import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Page.CGI;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.BrowsableSNMPBase;
import com.dragonflow.SiteView.MasterConfig;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.SiteViewException.SiteViewException;
import com.dragonflow.Utils.TextUtils;
import com.dragonflow.Utils.Snmp.BrowsableMIB;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

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
        HashMap hashmap = MasterConfig.getMasterConfig();
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties("com.dragonflow.StandardMonitor.VMWareMonitor", astringproperty);
        addClassElement("com.dragonflow.StandardMonitor.VMWareMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement("com.dragonflow.StandardMonitor.VMWareMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "description", "Monitors VMWare servers using snmp.");
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "help", "VMWareMon.htm");
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "title", "VM Ware");
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "class", "VMWareMonitor");
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "target", "_server");
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "topazName", "VMWare");
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "classType", "application");
        setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "topazType", "Load Balancer");
        if(TextUtils.getValue(hashmap, "_allowVMWare").length() > 0)
        {
            setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "loadable", "true");
        } else
        {
            setClassProperty("com.dragonflow.StandardMonitor.VMWareMonitor", "loadable", "false");
        }
    }
}
