/*
 * 
 * Created on 2005-3-5 14:23:02
 *
 * BrowsableSNMPMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>BrowsableSNMPMonitor</code>
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
import java.io.File;
import java.util.Vector;
import jgl.Array;

public class BrowsableSNMPMonitor extends BrowsableSNMPBase
{

 public BrowsableSNMPMonitor()
 {
 }

 public Array getConnectionProperties()
 {
     Array array = super.getConnectionProperties();
     return array;
 }

 protected String getMonitorType()
 {
     return "SNMP by MIB Monitor";
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
             String as[] = browsablemib.getCompiledMIBs();
             vector.add(as[0]);
             vector.add(as[0]);
             for(int i = 1; i < as.length; i++)
             {
                 new File(as[i]);
                 vector.add(as[i]);
                 vector.add((new File(as[i])).getName());
             }

         }
         catch(Exception exception)
         {
             LogManager.log("Error", "BrowsableSNMPMonitor could not get BrowsableMIB instance: " + exception.getMessage());
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
     addProperties("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
     addClassElement("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "description", "Returns selected OIDs from the MIB of an SNMP entity.");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "help", "BrowsableSNMPMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "title", "SNMP by MIB");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "class", "BrowsableSNMPMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "target", "_server");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "topazName", "SNMP by MIB Monitor");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "toolName", "SNMP by MIB");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "toolDescription", "Browses an SNMP MIB.");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "topazType", "System Resources");
     setClassProperty("COM.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "classType", "");
 }
}
