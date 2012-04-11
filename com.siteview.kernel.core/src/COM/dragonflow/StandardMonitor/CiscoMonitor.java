/*
 * 
 * Created on 2005-3-7 0:52:16
 *
 * CiscoMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>CiscoMonitor</code>
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

public class CiscoMonitor extends BrowsableSNMPBase
{

 private String ciscoWorksMIB;

 public CiscoMonitor()
 {
     ciscoWorksMIB = "CISCOWORKS-MIB.my";
 }

 public Array getConnectionProperties()
 {
     Array array = super.getConnectionProperties();
     return array;
 }

 protected String getMonitorType()
 {
     return "Cisco";
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
             if(browsablemib.containsMIB(ciscoWorksMIB))
             {
                 vector.add(ciscoWorksMIB);
                 vector.add(ciscoWorksMIB);
             }
         }
         catch(Exception exception)
         {
             LogManager.log("Error", "CiscoWorks Monitor could not get BrowsableMIB instance: " + exception.getMessage());
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
     addProperties("COM.dragonflow.StandardMonitor.CiscoMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.CiscoMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
     addClassElement("COM.dragonflow.StandardMonitor.CiscoMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "description", "Monitors Cisco Works server metrics.");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "help", "CiscoWorksMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "title", "Cisco Works");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "class", "CiscoMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "target", "_server");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "topazName", "Cisco Works");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "classType", "application");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "topazType", "System Resources");
     setClassProperty("COM.dragonflow.StandardMonitor.CiscoMonitor", "loadable", "true");
 }
}
