/*
 * 
 * Created on 2005-3-7 1:06:31
 *
 * EJBMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>EJBMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.TextUtils;
import jgl.Array;

public class EJBMonitor extends XMLMonitor
{

 static StringProperty pPort;

 public EJBMonitor()
 {
 }

 public Array getConnectionStandardProperties()
 {
     Array array = super.getConnectionStandardProperties();
     array.add(pPort);
     return array;
 }

 public int getPort()
 {
     return getPropertyAsInteger(pPort);
 }

 public String getMetricListURL()
 {
     return "/?type=0&id=10";
 }

 public String getPostMetricURL()
 {
     return "/?type=0&id=12";
 }

 public String getMetricDataURL()
 {
     return "/?type=0&id=11";
 }

 public String getMetricListXSL()
 {
     return "templates.applications/EJBMetricList.xsl";
 }

 public String getPostMetricXSL()
 {
     return "templates.applications/EJBPostMetric.xsl";
 }

 public String getMetricDataXSL()
 {
     return "templates.applications/EJBMetricData.xsl";
 }

 public boolean isNeedPostMetricsError(String s)
 {
     return s.indexOf("Results are null. No measurements were selected") >= 0;
 }

 static 
 {
     pPort = new StringProperty("_port", "2003");
     pPort.setDisplayText("Port", "port number on server");
     pPort.setParameterOptions(false, 4, false);
     StringProperty astringproperty[] = {
         pPort
     };
     addProperties("COM.dragonflow.StandardMonitor.EJBMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.EJBMonitor", Rule.stringToClassifier("status != OK\terror"));
     addClassElement("COM.dragonflow.StandardMonitor.EJBMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "description", "Monitor Enterprise Java Bean (EJB) enabled systems.");
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "help", "EJBMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "title", "EJB");
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "class", "EJBMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "target", "_server");
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "topazName", "EJB");
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "classType", "application");
     setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "topazType", "Web Application Server");
//     jgl.HashMap hashmap = TopazConfigurator.getTopazConfig();
//     if(TopazAPI.isSiteViewConfiguredWithTopaz() && TextUtils.getValue(hashmap, "_enableOldEJBMonnitor").length() > 0)
//     {
//         setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "loadable", "true");
//     } else
//     {
//         setClassProperty("COM.dragonflow.StandardMonitor.EJBMonitor", "loadable", "false");
//     }
 }
}
