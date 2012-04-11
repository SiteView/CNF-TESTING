/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * PatrolMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>PatrolMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.EmsDummyMonitor;
import COM.dragonflow.Utils.LUtils;
import jgl.Array;
import jgl.HashMap;

public class PatrolMonitor extends DispatcherMonitor
    implements BrowsableMonitor
{

    static BrowsableProperty pBrowseCounters;
    static StringProperty pUsername;
    static StringProperty pPassword;
    static StringProperty pPort;
    static DispatcherConnection dispConnection = new DispatcherConnection();

    public PatrolMonitor()
    {
    }

    public boolean manageBrowsableSelectionsByID()
    {
        return false;
    }

    public boolean areBrowseIDsEqual(String arg0, String arg1)
    {
        return false;
    }

    public String getTopazCounterLabel(StringProperty prop)
    {
        return GetPropertyLabel(prop, true);
    }

    public boolean isDispatcher()
    {
        return true;
    }

    public String verify(StringProperty property, String value, HTTPRequest request, HashMap errors)
    {
        if(property == pBrowseCounters)
        {
            String counterID = getProperty(DispatcherMonitor.ID_PROPERTY + 1);
            if(counterID.length() <= 0)
            {
                errors.put(property, "No counters selected");
            }
            return value;
        } else
        {
            return super.verify(property, value, request, errors);
        }
    }

    public Array getConnectionProperties()
    {
        Array result = new Array();
        result.add(DispatcherMonitor.pServerName);
        result.add(pUsername);
        result.add(pPassword);
        result.add(pPort);
        return result;
    }

    public boolean isServerBased()
    {
        return true;
    }

    public void setMaxCounters(int maxCounters)
    {
        DispatcherMonitor.nMaxCounters = maxCounters;
        HashMap config = MasterConfig.getMasterConfig();
        config.put("_DispatcherMaxCounters", (new Integer(maxCounters)).toString());
        MasterConfig.saveMasterConfig(config);
    }

    public static void main(String args[])
    {
    }

    static 
    {
        Array propertyArray = new Array();
        pBrowseCounters = new BrowsableProperty("_browse", "browseName");
        pBrowseCounters.setDisplayText("Counters", "Current selection of counters.");
        pBrowseCounters.setParameterOptions(true, 1, false);
        propertyArray.add(pBrowseCounters);
        pUsername = new StringProperty("_usr");
        pUsername.setDisplayText("Username", "Enter the Username for Patrol");
        pUsername.setParameterOptions(false, 2, false);
        propertyArray.add(pUsername);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "Enter the Password for Patrol");
        pPassword.setParameterOptions(false, 3, false);
        pPassword.isPassword = true;
        propertyArray.add(pPassword);
        pPort = new StringProperty("_port");
        pPort.setDisplayText("Port", "Enter the Port for Patrol");
        pPort.setParameterOptions(false, 4, false);
        propertyArray.add(pPort);
        StringProperty myProperties[] = new StringProperty[propertyArray.size()];
        for(int i = 0; i < propertyArray.size(); i++)
        {
            myProperties[i] = (StringProperty)propertyArray.at(i);
        }

        PropertiedObject.addProperties("COM.dragonflow.StandardMonitor.PatrolMonitor", myProperties);
        SiteViewObject.addClassElement("COM.dragonflow.StandardMonitor.PatrolMonitor", Rule.stringToClassifier("countersInError == " + DispatcherMonitor.nMaxCounters + "\terror"));
        SiteViewObject.addClassElement("COM.dragonflow.StandardMonitor.PatrolMonitor", Rule.stringToClassifier("always\tgood"));
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "description", "Monitor a Patrol Agent");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "help", "PatrolMonitor.htm");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "title", "BMC PATROL Metrics");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "class", "PatrolMonitor");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "target", "_server");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "topazName", "Patrol");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "classType", "advanced");
        PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "topazType", "Database Server");
        boolean isEmsLicensed = LUtils.isValidSSforXLicense(new EmsDummyMonitor());
        if(DispatcherSitter.isDispatcherInstalled() && isEmsLicensed)
        {
            PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "loadable", "true");
        } else
        {
            PropertiedObject.setClassProperty("COM.dragonflow.StandardMonitor.PatrolMonitor", "loadable", "false");
        }
    }
}
