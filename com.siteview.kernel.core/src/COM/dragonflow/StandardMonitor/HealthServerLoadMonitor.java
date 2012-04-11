/*
 * 
 * Created on 2005-3-7 1:21:26
 *
 * HealthServerLoadMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>HealthServerLoadMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import java.io.File;
import java.util.Vector;
import jgl.HashMap;

//Referenced classes of package COM.dragonflow.StandardMonitor:
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
     String s = (COM.dragonflow.StandardMonitor.HealthServerLoadMonitor.class).getName();
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
