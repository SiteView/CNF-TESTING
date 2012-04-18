/*
 * 
 * Created on 2005-3-5 14:23:02
 *
 * BrowsableSNMPMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>BrowsableSNMPMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Page.CGI;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.BrowsableSNMPBase;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.SiteViewException.SiteViewException;
import com.dragonflow.Utils.Snmp.BrowsableMIB;
import java.io.File;
import java.util.Vector;
import com.recursionsw.jgl.Array;

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
     addProperties("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", astringproperty);
     addClassElement("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", Rule.stringToClassifier("countersInError > 0\terror"));
     addClassElement("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "description", "Returns selected OIDs from the MIB of an SNMP entity.");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "help", "BrowsableSNMPMon.htm");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "title", "SNMP by MIB");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "class", "BrowsableSNMPMonitor");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "target", "_server");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "topazName", "SNMP by MIB Monitor");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "toolName", "SNMP by MIB");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "toolDescription", "Browses an SNMP MIB.");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "topazType", "System Resources");
     setClassProperty("com.dragonflow.StandardMonitor.BrowsableSNMPMonitor", "classType", "");
 }
}
