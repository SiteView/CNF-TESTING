/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * TuxedoMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>TuxedoMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Properties.BrowsableProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import jgl.Array;

public class TuxedoMonitor extends DispatcherMonitor
    implements BrowsableMonitor
{

    static BrowsableProperty pBrowseCounters;
    static StringProperty pPort;
    static StringProperty pTuxClient;
    static StringProperty pTuxData;
    static StringProperty pUsername;
    static StringProperty pPassword;
    static DispatcherConnection dispConnection = new DispatcherConnection();

    public TuxedoMonitor()
    {
    }

    public boolean isDispatcher()
    {
        return true;
    }

    public Array getConnectionProperties()
    {
        Array array = new Array();
        array.add(pServerName);
        array.add(pPort);
        array.add(pTuxClient);
        array.add(pTuxData);
        array.add(pUsername);
        array.add(pPassword);
        return array;
    }

    public static void main(String args[])
    {
    }

    public String getTopazCounterLabel(StringProperty stringproperty)
    {
        return GetPropertyLabel(stringproperty, true);
    }

    public boolean isServerBased()
    {
        return true;
    }

    public boolean manageBrowsableSelectionsByID()
    {
        return false;
    }

    public boolean areBrowseIDsEqual(String s, String s1)
    {
        if(s == null || s1 == null)
        {
            return false;
        } else
        {
            return s.equals(s1);
        }
    }

    static 
    {
        Array array = new Array();
        pBrowseCounters = new BrowsableProperty("_browse", "browseName");
        pBrowseCounters.setDisplayText("Counters", "Current selection of counters.");
        pBrowseCounters.setParameterOptions(true, 1, false);
        array.add(pBrowseCounters);
        pPort = new StringProperty("_port");
        pPort.setDisplayText("Port", "Enter the port number for Tuxedo");
        pPort.setParameterOptions(false, true, 2, false);
        array.add(pPort);
        pUsername = new StringProperty("_usr");
        pUsername.setDisplayText("Username", "Enter the Username for Tuxedo");
        pUsername.setParameterOptions(false, true, 3, false);
        array.add(pUsername);
        pPassword = new StringProperty("_pwd");
        pPassword.setDisplayText("Password", "Enter the Password for Tuxedo");
        pUsername.setParameterOptions(false, true, 4, false);
        pPassword.isPassword = true;
        array.add(pPassword);
        pTuxClient = new StringProperty("_tuxclient");
        pTuxClient.setDisplayText("Client Name", "Enter the optional client name for Tuxedo");
        pTuxClient.setParameterOptions(false, true, 5, false);
        array.add(pTuxClient);
        pTuxData = new StringProperty("_tuxdata");
        pTuxData.setDisplayText("Connection Data", "Enter the optional Connection Data for Tuxedo");
        pTuxData.setParameterOptions(false, true, 6, false);
        pTuxData.isMultiLine = true;
        array.add(pTuxData);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int i = 0; i < array.size(); i++)
        {
            astringproperty[i] = (StringProperty)array.at(i);
        }

        addProperties("COM.dragonflow.StandardMonitor.TuxedoMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.TuxedoMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.TuxedoMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "description", "Monitors BEA Tuxedo server performance metrics");
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "help", "TuxedoMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "title", "Tuxedo");
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "class", "TuxedoMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "topazName", "Tuxedo");
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "topazType", "Database Server");
        if(Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "loadable", "true");
        } else
        {
            setClassProperty("COM.dragonflow.StandardMonitor.TuxedoMonitor", "loadable", "false");
        }
    }
}
