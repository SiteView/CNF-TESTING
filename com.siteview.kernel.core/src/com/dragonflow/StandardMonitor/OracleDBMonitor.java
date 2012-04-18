/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * OracleDBMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>OracleDBMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import com.dragonflow.Properties.BrowsableProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.*;
import com.recursionsw.jgl.Array;

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
            astringproperty[i] = (StringProperty)array.get(i);
        }

        addProperties("com.dragonflow.StandardMonitor.OracleDBMonitor", astringproperty);
        addClassElement("com.dragonflow.StandardMonitor.OracleDBMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement("com.dragonflow.StandardMonitor.OracleDBMonitor", Rule.stringToClassifier("always\tgood"));
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "description", "Monitor Oracle database servers (deprecated, use OracleJDBC Monitor).");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "help", "oracleDBMon.htm");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "title", "OracleDB");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "class", "OracleDBMonitor");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "target", "_server");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "topazName", "Oracle");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "classType", "application");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "topazType", "Database Server");
        setClassProperty("com.dragonflow.StandardMonitor.OracleDBMonitor", "loadable", "false");
    }
}
