/*
 * 
 * Created on 2005-3-7 1:11:05
 *
 * F5Monitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>F5Monitor</code>
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
import COM.dragonflow.SiteView.BrowsableSNMPBase;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.Snmp.BrowsableMIB;
import java.util.Vector;
import jgl.Array;

public class F5Monitor extends BrowsableSNMPBase
{

 private String F5BigIPMIB;

 public F5Monitor()
 {
     F5BigIPMIB = "LOAD-BAL-SYSTEM-MIB.txt";
 }

 public Array getConnectionProperties()
 {
     Array array = super.getConnectionProperties();
     return array;
 }

 protected String getMonitorType()
 {
     return "F5";
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
             if(browsablemib.containsMIB(F5BigIPMIB))
             {
                 vector.add(F5BigIPMIB);
                 vector.add(F5BigIPMIB);
             }
         }
         catch(Exception exception)
         {
             LogManager.log("Error", "F5Monitor could not get BrowsableMIB instance: " + exception.getMessage());
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
     StringProperty astringproperty[] = new StringProperty[0];
     addProperties("COM.dragonflow.StandardMonitor.F5Monitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.F5Monitor", Rule.stringToClassifier("countersInError > 0\terror"));
     addClassElement("COM.dragonflow.StandardMonitor.F5Monitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "description", "Monitors F5 Big-IP load balancer performance metrics.");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "help", "F5SNMPMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "title", "F5 Big-IP");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "class", "F5Monitor");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "target", "_server");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "topazName", "F5");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "classType", "application");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "topazType", "Load Balancer");
     setClassProperty("COM.dragonflow.StandardMonitor.F5Monitor", "loadable", "true");
 }
}