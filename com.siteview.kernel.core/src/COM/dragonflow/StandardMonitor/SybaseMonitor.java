/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * SybaseMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>SybaseMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import jgl.Array;
import jgl.HashMap;

public class SybaseMonitor extends BrowsableMdrvBase
{

    static StringProperty pUsername;
    static StringProperty pPassword;
    static StringProperty pPasswordEx;
    static StringProperty pSybServer;
    static String monDll = "sybase_mon.dll";

    public SybaseMonitor()
    {
    }

    public Array getConnectionProperties()
    {
        Array array = new Array();
        array.add(pSybServer);
        array.add(pUsername);
        array.add(pPasswordEx);
        return array;
    }

    protected String getTargetNode()
    {
        return "Sybase" + getProperty(pSybServer);
    }

    public void initialize(HashMap hashmap)
    {
        super.initialize(hashmap);
        String s = getProperty(pPasswordEx);
        if(s.length() > 0)
        {
            return;
        }
        s = getProperty(pPassword);
        if(s.length() > 0)
        {
            setProperty(pPasswordEx, s);
            setProperty(pPassword, "");
        }
    }

    public String getHostname()
    {
        return getProperty(pSybServer);
    }

    protected String getMonDll()
    {
        return monDll;
    }

    static 
    {
        Array array = new Array();
        pSybServer = new StringProperty("_server", "");
        pSybServer.setDisplayText("Server", "Sybase server name.");
        pSybServer.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 1, false);
        array.add(pSybServer);
        pUsername = new StringProperty("_usr");
        pUsername.setDisplayText("Username", "Enter the Username for Sybase");
        pUsername.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 2, false);
        array.add(pUsername);
        pPassword = new StringProperty("_pwd");
        pPassword.setDisplayText("Password", "Enter the Password for Sybase");
        pPassword.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 3, false);
        pPassword.isPassword = true;
        array.add(pPassword);
        pPasswordEx = new StringProperty("_sybasePassword");
        pPasswordEx.setDisplayText("Password", "Enter the Password for Sybase");
        pPasswordEx.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 4, false);
        pPasswordEx.isPassword = true;
        array.add(pPasswordEx);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for(int i = 0; i < array.size(); i++)
        {
            astringproperty[i] = (StringProperty)array.at(i);
        }

        String s = (COM.dragonflow.StandardMonitor.SybaseMonitor.class).getName();
        addProperties(s, astringproperty);
        addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror", true));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitors Sybase database server performance metrics.");
        setClassProperty(s, "help", "sybaseMon.htm");
        setClassProperty(s, "title", "Sybase");
        setClassProperty(s, "class", "SybaseMonitor");
        setClassProperty(s, "target", "_server");
        setClassProperty(s, "topazName", "Sybase");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "topazType", "Database Server");
        if(Platform.isWindows())
        {
            setClassProperty(s, "loadable", "true");
        } else
        {
            setClassProperty(s, "loadable", "false");
        }
    }
}
