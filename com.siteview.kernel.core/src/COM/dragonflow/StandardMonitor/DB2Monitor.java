/*
 * 
 * Created on 2005-3-7 0:58:57
 *
 * DB2Monitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>DB2Monitor</code>
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

public class DB2Monitor extends BrowsableExeBase
{

 static BrowsableProperty pBrowseCounters;
 static StringProperty pUsername;
 static StringProperty pPassword;
 static StringProperty pInstance;
 static StringProperty pDb2Server;
 static String monDll = "db2mon_adapter.dll";

 public DB2Monitor()
 {
 }

 public Array getConnectionProperties()
 {
     Array array = new Array();
     array.add(pDb2Server);
     array.add(pUsername);
     array.add(pPassword);
     array.add(pInstance);
     return array;
 }

 public String getHostname()
 {
     return getProperty(pDb2Server);
 }

 protected String getMonDll()
 {
     return monDll;
 }

 public static void main(String args[])
 {
 }

 static 
 {
     Array array = new Array();
     pDb2Server = new StringProperty("_server", "");
     pDb2Server.setDisplayText("Server", "DB2 server name.");
     pDb2Server.setParameterOptions(false, true, BrowsableBase.COUNTER_PROPERTY_INDEX + 1, false);
     array.add(pDb2Server);
     pUsername = new StringProperty("_usr");
     pUsername.setDisplayText("Username", "Enter the Username for Db2");
     pUsername.setParameterOptions(false, true, 2, false);
     array.add(pUsername);
     pPassword = new StringProperty("_pwd");
     pPassword.setDisplayText("Password", "Enter the Password for Db2");
     pPassword.setParameterOptions(false, true, 3, false);
     pPassword.isPassword = true;
     array.add(pPassword);
     pInstance = new StringProperty("_instance");
     pInstance.setDisplayText("Node Name", "Enter the Node Name for Db2");
     pInstance.setParameterOptions(false, true, 4, false);
     array.add(pInstance);
     StringProperty astringproperty[] = new StringProperty[array.size()];
     for(int i = 0; i < array.size(); i++)
     {
         astringproperty[i] = (StringProperty)array.at(i);
     }

     String s = (COM.dragonflow.StandardMonitor.DB2Monitor.class).getName();
     addProperties(s, astringproperty);
     addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror"));
     addClassElement(s, Rule.stringToClassifier("always\tgood"));
     setClassProperty(s, "description", "Monitors DB2 Database server performance metrics.");
     setClassProperty(s, "help", "db2Mon.htm");
     setClassProperty(s, "title", "DB2");
     setClassProperty(s, "class", "DB2Monitor");
     setClassProperty(s, "target", "_server");
     setClassProperty(s, "topazName", "cm_db2_mon");
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