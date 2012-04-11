/*
 * 
 * Created on 2005-3-7 1:06:04
 *
 * EBusinessTransactionMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>EBusinessTransactionMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.CompositeBase;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import java.util.Vector;

public class EBusinessTransactionMonitor extends CompositeBase
{

 static StringProperty pWhenError;
 static StringProperty pSingleSession;
 static StringProperty pDelay;

 public EBusinessTransactionMonitor()
 {
 }

 public String getHostname()
 {
     return "EBusinessHost";
 }

 protected boolean update()
 {
     int ai[] = initializeStats();
     String as[] = initializeNameList();
     long l = getPropertyAsLong(pDelay) * 1000L;
     boolean flag = getPropertyAsBoolean(pSingleSession);
     checkSequentially(ai, as, flag, l, getProperty(pWhenError));
     updateProperties(ai, as, true);
     return true;
 }

 public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
     throws SiteViewException
 {
     if(scalarproperty == pWhenError)
     {
         Vector vector = new Vector();
         vector.addElement("");
         vector.addElement("continue, run the remainder of the monitors");
         vector.addElement("stop");
         vector.addElement("stop, do not run any of the remaining monitors");
         vector.addElement("last");
         vector.addElement("run the last monitor");
         return vector;
     } else
     {
         return super.getScalarValues(scalarproperty, httprequest, cgi);
     }
 }

 public String defaultTitle()
 {
     String s = super.defaultTitle();
     return "Multi: " + s;
 }

 static 
 {
     pWhenError = new ScalarProperty("_whenError", "");
     pWhenError.setDisplayText("When Error", "Follow this rule if a monitor enounters an error.");
     pWhenError.setParameterOptions(true, 3, true);
     pSingleSession = new BooleanProperty("_singleSession", "");
     pSingleSession.setDisplayText("Single Session", "Run all monitors in a single session - if this box is checked then URL monitors use the same set of cookies and the same connection to the server.");
     pSingleSession.setParameterOptions(true, 4, true);
     pDelay = new NumericProperty("_delay", "0");
     pDelay.setDisplayText("Monitor Delay", "Delay in seconds between monitors");
     pDelay.setParameterOptions(true, 5, true);
     StringProperty astringproperty[] = {
         pWhenError, pDelay, pSingleSession
     };
     addProperties("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", Rule.stringToClassifier("itemsInError > 0\terror", true));
     addClassElement("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", Rule.stringToClassifier("itemsInError == 'n/a'\terror"));
     addClassElement("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", Rule.stringToClassifier("itemsInWarning > 0\twarning", true));
     addClassElement("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "description", "Verify an eBusiness transaction by checking the complete chain of processes and actions, including front-end web servers, email notifications, back-end databases, and extranet applications");
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "help", "ebusinesstransaction.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "title", "eBusiness Chain");
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "class", "EBusinessTransactionMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "classType", "");
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "target", "_monitor");
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "topazName", "Ebus Chain Monitor");
     setClassProperty("COM.dragonflow.StandardMonitor.EBusinessTransactionMonitor", "topazType", "Web Application Server");
 }
}