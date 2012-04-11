/*
 * 
 * Created on 2005-3-5 14:18:17
 *
 * ApacheMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ApacheMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.PrintStream;
import java.util.Vector;
import jgl.HashMap;

//Referenced classes of package COM.dragonflow.StandardMonitor:
//         URLMonitor, URLContentMonitor

public class ApacheMonitor extends URLContentBase
{

 static StringProperty pURL;
 static StringProperty pOS;
 static StringProperty pCounters;
 static StringProperty pTimeout;
 static StringProperty pErrorContent;
 static StringProperty pProxy;
 static StringProperty pUserName;
 static StringProperty pChallengeResponse;
 static StringProperty pPassword;
 static StringProperty pProxyUserName;
 static StringProperty pProxyPassword;
 static StringProperty pStatus;
 boolean servermatch;
 static String returnURL;
 public static String templateFile;
 public static int DEFAULT_TIMEOUT = 60000;

 public ApacheMonitor()
 {
     servermatch = false;
 }

 public String getReturnURL()
 {
     return HTTPRequest.encodeString(returnURL);
 }

 public StringProperty getCountersProperty()
 {
     return pCounters;
 }

 public void setCountersContent(String s)
 {
     setProperty(pCounters, s);
 }

 public String getHostname()
 {
     return HTTPUtils.hostFromURL(getProperty(pURL));
 }

 public String getTemplateFile()
 {
     return templateFile;
 }

 protected boolean update()
 {
     String s = "";
     String s1 = getProperty(pURL);
     String s2 = buildRegExp();
     String s3 = getProperty(pProxy);
     String s4 = getProperty(pProxyPassword);
     String s5 = getProperty(pProxyUserName);
     String s6 = getProperty(pUserName);
     if(getProperty(pChallengeResponse).length() > 0)
     {
         s6 = URLMonitor.NT_CHALLENGE_RESPONSE_TAG + s6;
     }
     String s7 = getProperty(pPassword);
     int i = getPropertyAsInteger(pTimeout) * 1000;
     if(i == 0)
     {
         i = DEFAULT_TIMEOUT;
     }
     if(s1.startsWith("[NT]"))
     {
         s1 = s1.substring(4);
     }
     StringBuffer stringbuffer = new StringBuffer();
     String s8 = "";
     progressString += "Retrieving URL " + getProperty(pURL) + "\n";
     SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
     long l = siteviewgroup.getSettingAsLong("urlContentMatchMax", 51200);
     long al[] = URLContentMonitor.checkURL(s1, s2, "", s3, s5, s4, null, s6, s7, "", stringbuffer, l, "", 0, i, new StringBuffer());
     long l1 = al[0];
     for(int j = 0; j < pValues.length; j++)
     {
         unsetProperty(pValues[j]);
     }

     if(TextUtils.isValueExpression(s2))
     {
         if(isContentMatchStatus(l1))
         {
             s8 = updateMatchValues(s, s2, stringbuffer.toString(), false);
         } else
         {
             updateErrorValues(s, s2);
         }
     }
     String s9 = "";
     if(stillActive())
     {
         synchronized(this)
         {
             if(l1 == 200L && s8.length() > 0)
             {
                 setProperty(pStateString, s8);
                 setProperty(pStatus, String.valueOf(l1));
             } else
             if(!servermatch)
             {
                 String s10 = "Server page and counter mismatch ";
                 setProperty(pStatus, " no data ");
             } else
             {
                 String s11;
                 if(l1 == 200L)
                 {
                     s11 = "Invalid match expression or counters";
                     setProperty(pStatus, " no match ");
                 } else
                 {
                     s11 = lookupStatus(l1) + s8;
                     setProperty(pStatus, lookupStatus(l1));
                     setProperty(pNoData, "n/a");
                 }
                 setProperty(getLocationProperty(pStateString, s), s11);
                 setProperty(getLocationProperty(pMeasurement, s), String.valueOf(0));
             }
         }
     }
     return true;
 }

 public String getCountersContent()
 {
     String s = getProperty(pURL);
     String s1 = getProperty(pOS);
     String s2 = getProperty(pCounters);
     return s2;
 }

 public String buildRegExp()
 {
     String s = getProperty(pURL);
     String s1 = new String("/");
     String as[] = TextUtils.split(getCountersContent(), ",");
     String s2 = getProperty(pOS);
     boolean flag = false;
     for(int i = 0; i < as.length; i++)
     {
         if(s.indexOf("auto") >= 1 && as[i].indexOf(" ") == -1 && as[i].indexOf("/") == -1 || as[i].indexOf("al Acc") != -1 || as[i].indexOf("al kBy") != -1)
         {
             as[i] = TextUtils.replaceString(as[i], "Workers", "[\\w]{1,8}");
             s1 = s1.concat(".*?" + as[i] + ":\\s?([\\d\\.%]{0,28})");
             servermatch = true;
             continue;
         }
         if(s.indexOf("refresh") >= 1 && (as[i].indexOf(" ") >= 1 || as[i].indexOf("/") >= 0) && as[i].indexOf("tal Acc") == -1 && as[i].indexOf("tal k") == -1)
         {
             if(as[i].indexOf("/") != -1 || as[i].indexOf("quest") != -1 || as[i].indexOf("idle") != -1 || as[i].indexOf("load") != -1)
             {
                 as[i] = TextUtils.replaceString(as[i], "workers", "[\\w]{1,8}");
                 as[i] = TextUtils.replaceString(as[i], "/", "\\/");
                 s1 = s1.concat(".*?([\\d\\.%]{0,12})\\s?" + as[i]);
             } else
             if(as[i].indexOf("Tot") != -1)
             {
                 s1 = s1.concat(".*?" + as[i] + ":\\s?([\\s\\w\\d\\\\.]{1,52})");
             } else
             {
                 s1 = s1.concat(".*?" + as[i] + ":\\s?([\\s\\w\\d\\-:,\\/\\.)(]{1,96})");
             }
             servermatch = true;
         } else
         {
             s1 = s1.concat(".");
             servermatch = false;
         }
     }

     s1 = s1.concat("/si");
     if(flag)
     {
         System.out.println("\nApache regexp: " + s1);
     }
     return s1;
 }

 public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
     throws SiteViewException
 {
     if(scalarproperty == pOS)
     {
         Vector vector = new Vector();
         vector.addElement("Unix");
         vector.addElement("Unix");
         vector.addElement("Linux");
         vector.addElement("Linux");
         vector.addElement("NT");
         vector.addElement("NT");
         return vector;
     } else
     {
         return super.getScalarValues(scalarproperty, httprequest, cgi);
     }
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
 {
     if(stringproperty == pURL)
     {
         if(s == null || s.length() == 0)
         {
             hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
         } else
         if(TextUtils.hasSpaces(s))
         {
             hashmap.put(stringproperty, "no spaces are allowed");
         }
         return s;
     }
     if(stringproperty == pCounters)
     {
         if(s.equals("No Counters available for this machine"))
         {
             hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
         }
         return s;
     } else
     {
         return super.verify(stringproperty, s, httprequest, hashmap);
     }
 }

 static 
 {
     returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=ApacheMonitor";
     templateFile = "counters.apache";
     pCounters = new StringProperty("_counters", getTemplateContent(getTemplatePath(), templateFile, false), "Apache Selected Counters");
     pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + HTTPRequest.encodeString(returnURL) + "&maxcounters=" + nMaxCounters + "&filename=" + templateFile + "&type=URLContent\">choose counters</A>");
     pCounters.setParameterOptions(false, 1, false);
     pCounters.isMultiLine = true;
     pURL = new StringProperty("_url");
     pURL.setDisplayText("URL", "the server administration URL (example: http://server:port/server-status?auto)");
     pURL.setParameterOptions(true, 2, false);
     pTimeout = new NumericProperty("_timeout", "60", "seconds");
     pTimeout.setDisplayText("Timeout", "the time out, in seconds, to wait for the response");
     pTimeout.setParameterOptions(true, 1, true);
     pOS = new ScalarProperty("_OS", "Unix");
     pOS.setDisplayText("Server OS", "optional Operating System of server, default is Unix");
     pOS.setParameterOptions(true, 2, true);
     pProxy = new StringProperty("_proxy");
     pProxy.setDisplayText("HTTP Proxy", "optional proxy server to use including port (example: proxy." + Platform.exampleDomain + ":8080)");
     pProxy.setParameterOptions(true, 3, true);
     pStatus = new StringProperty("status");
     pUserName = new StringProperty("_username");
     pUserName.setDisplayText("Authorization User Name", "optional user name if the URL requires authorization");
     pUserName.setParameterOptions(true, 4, true);
     pPassword = new StringProperty("_password");
     pPassword.setDisplayText("Authorization Password", "optional password if the URL requires authorization");
     pPassword.setParameterOptions(true, 5, true);
     pPassword.isPassword = true;
     pChallengeResponse = new BooleanProperty("_challengeResponse", "");
     pChallengeResponse.setDisplayText("NT Challenge Response", "when selected, use NT Challenge Response authorization");
     pChallengeResponse.setParameterOptions(true, 6, true);
     pProxyUserName = new StringProperty("_proxyusername");
     pProxyUserName.setDisplayText("Proxy Server User Name", "optional user name if the proxy server requires authorization");
     pProxyUserName.setParameterOptions(true, 7, true);
     pProxyPassword = new StringProperty("_proxypassword");
     pProxyPassword.setDisplayText("Proxy Server Password", "optional password if the proxy server requires authorization");
     pProxyPassword.setParameterOptions(true, 8, true);
     pProxyPassword.isPassword = true;
     StringProperty astringproperty[] = {
         pURL, pCounters, pTimeout, pProxy, pProxyUserName, pProxyPassword, pChallengeResponse, pUserName, pPassword, pStatus, 
         pOS
     };
     addProperties("COM.dragonflow.StandardMonitor.ApacheMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.ApacheMonitor", Rule.stringToClassifier("status == 200\tgood"));
     addClassElement("COM.dragonflow.StandardMonitor.ApacheMonitor", Rule.stringToClassifier("status != 200\terror"));
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "description", "Monitors Apache Server HTTP response code statistics.");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "help", "ApacheServerMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "title", "Apache Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "class", "ApacheMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "target", "_url");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "topazName", "Apache");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "topazType", "Web Server");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "classType", "application");
     setClassProperty("COM.dragonflow.StandardMonitor.ApacheMonitor", "applicationType", "URLContent");
 }
}