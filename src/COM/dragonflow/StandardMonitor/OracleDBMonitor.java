/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * OracleDBMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>OracleDBMonitor</code>
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

public class OracleDBMonitor extends DispatcherMonitor
    implements BrowsableMonitor
{

    static BrowsableProperty pBrowseCounters;
    static StringProperty pUsername;
    static StringProperty pPassword;
    static DispatcherConnection dispConnection = new DispatcherConnection();

    public OracleDBMonitor()
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
        array.add(pUsername);
        array.add(pPassword);
        return array;
    }

    public static void main(String args[])
    {
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
        pUsername = new StringProperty("_usr");
        pUsername.setDisplayText("Username", "Enter the Username for Oracle");
        pUsername.setParameterOptions(false, 2, false);
        array.add(pUsername);
        pPassword = new StringProperty("_pwd");
        pPassword.setDisplayText("Password", "Enter the Password for Oracle");
        pUsername.setParameterOptions(false, 3, false);
        pPassword.isPassword = true;
        array.add(pPassword);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int i = 0; i < array.size(); i++)
        {
            astringproperty[i] = (StringProperty)array.at(i);
        }

        addProperties("COM.dragonflow.StandardMonitor.OracleDBMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.OracleDBMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.OracleDBMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "description", "Monitor Oracle database servers (deprecated, use OracleJDBC Monitor).");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "help", "oracleDBMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "title", "OracleDB");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "class", "OracleDBMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "topazName", "Oracle");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "topazType", "Database Server");
        setClassProperty("COM.dragonflow.StandardMonitor.OracleDBMonitor", "loadable", "false");
    }
}
