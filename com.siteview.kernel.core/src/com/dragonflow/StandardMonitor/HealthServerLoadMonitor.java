/*
 * 
 * Created on 2005-3-7 1:21:26
 *
 * HealthServerLoadMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>HealthServerLoadMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Page.CGI;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.Platform;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.SiteViewException.SiteViewException;
import java.io.File;
import java.util.Vector;
import com.recursionsw.jgl.HashMap;

//Referenced classes of package com.dragonflow.StandardMonitor:
//         BrowsableNTCounterMonitor

public class HealthServerLoadMonitor extends BrowsableNTCounterMonitor
{

 protected static StringProperty pStatus;
 static String returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=HealthServerLoadMonitor";
 public static String templateFile = "";

 public HealthServerLoadMonitor()
 {
 }

 protected String getCounterFilename()
 {
     String s = Platform.getRoot() + File.separator + "templates.health" + File.separator + "health.xml";
     return s;
 }

 protected boolean update()
 {
     setProperty(pHost, "");
     setProperty(pFile, "health.xml");
     return super.update();
 }

 public String getReturnURL()
 {
     return returnURL;
 }

 public String getTemplateFile()
 {
     return templateFile;
 }

 public boolean isServerBased()
 {
     return false;
 }

 public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
     throws SiteViewException
 {
     if(scalarproperty == pFile)
     {
         Vector vector = new Vector();
         vector.addElement("health.xml");
         vector.addElement("health.xml");
         return vector;
     } else
     {
         return super.getScalarValues(scalarproperty, httprequest, cgi);
     }
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
 {
     return super.verify(stringproperty, s, httprequest, hashmap);
 }

 public int getCostInLicensePoints()
 {
     return 0;
 }

 static 
 {
     templateFile = Platform.isWindows(Platform.getOs()) ? "ntcounters.health" : "unixcounters.health";
     String s = (com.dragonflow.StandardMonitor.HealthServerLoadMonitor.class).getName();
     pStatus = new StringProperty("status", "n/a");
     StringProperty astringproperty[] = {
         pStatus
     };
     addProperties(s, astringproperty);
     setClassProperty(s, "class", "HealthServerLoadMonitor");
     setClassProperty(s, "description", "Monitors the health of the SiteView server");
     setClassProperty(s, "title", "Health of SiteView Server");
     setClassProperty(s, "help", "HealthServerLoadMonitor.htm");
     setClassProperty(s, "target", "_server");
     setClassProperty(s, "topazName", "Health Server Load Monitor");
     setClassProperty(s, "classType", "application");
     setClassProperty(s, "addable", "false");
     addClassElement(s, Rule.stringToClassifier("status != 'ok'\terror"));
     addClassElement(s, Rule.stringToClassifier("status == 'ok'\tgood"));
 }
}
