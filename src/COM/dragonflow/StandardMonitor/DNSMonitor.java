/*
 * 
 * Created on 2005-3-7 1:00:50
 *
 * DNSMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>DNSMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

public class DNSMonitor extends AtomicMonitor
{

 static StringProperty pServer;
 static StringProperty pHostName;
 static StringProperty pHostIP;
 static StringProperty pRoundTripTime;
 static StringProperty pStatus;
 static StringProperty pStatusText;
 private static final String DEFAULT_SERVER_TAGS[] = {
     "Server:", "Servidor:", "Serveur:", "Serveur\377:"
 };
 private static final String DNS_SERVER_TAGS_SETTING = "_dnsServerTags";
 private static String serverTags[];
 private static final String DEFAULT_NAME_TAGS[] = {
     "Name:", "Nombre:", "Navn:", "Nome:", "Nom :", "Nom\377:"
 };
 private static final String DNS_NAME_TAGS_SETTING = "_dnsNameTags";
 private static String nameTags[];

 public DNSMonitor()
 {
 }

 public String getHostname()
 {
     return getProperty(pServer);
 }

 public String getTestURL()
 {
     return "/SiteView/cgi/go.exe/SiteView?page=DNS&host=" + getProperty(pServer) + "&misc=" + getProperty(pHostName) + "&group=" + HTTPRequest.encodeString(I18N.toDefaultEncoding(getProperty(pGroupID)));
 }

 protected boolean update()
 {
     String s = getProperty(pServer);
     String s1 = getProperty(pHostName);
     String s2 = getProperty(pHostIP);
     boolean flag = true;
     String as[] = Platform.dnsLookup(s, s1, s2);
     int i = TextUtils.toInt(as[0]);
     long l = TextUtils.toInt(as[1]);
     String s3 = lookupStatus(i);
     synchronized(this)
     {
         setProperty(pStatus, i);
         setProperty(pStatusText, s3);
         if(i == kURLok)
         {
             if(l < 10L)
             {
                 l = 10L;
             }
             setProperty(pStateString, TextUtils.floatToString((float)l / 1000F, 2) + " sec");
             setProperty(pRoundTripTime, String.valueOf(l));
             setProperty(pMeasurement, getMeasurement(pRoundTripTime, 4000L));
         } else
         {
             setProperty(pStateString, s3);
             setProperty(pMeasurement, String.valueOf(0));
             setProperty(pRoundTripTime, "n/a");
             setProperty(pNoData, "n/a");
         }
     }
     return flag;
 }

 public Array getLogProperties()
 {
     Array array = super.getLogProperties();
     array.add(pStatus);
     array.add(pRoundTripTime);
     array.add(pStatusText);
     return array;
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
 {
     if(stringproperty == pServer)
     {
         if(s.length() == 0)
         {
             hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
         } else
         if(TextUtils.hasLetters(s))
         {
             hashmap.put(stringproperty, "no letters are allowed");
         } else
         if(TextUtils.hasSpaces(s))
         {
             hashmap.put(stringproperty, "no spaces are allowed");
         }
         return s;
     }
     if(stringproperty == pHostName)
     {
         if(s.length() == 0)
         {
             hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
         } else
         if(TextUtils.hasSpaces(s))
         {
             hashmap.put(stringproperty, "no spaces are allowed");
         }
         return s;
     }
     if(stringproperty == pHostIP)
     {
         if(TextUtils.hasLetters(s))
         {
             hashmap.put(stringproperty, "no letters are allowed");
         } else
         if(TextUtils.hasSpaces(s))
         {
             hashmap.put(stringproperty, "no spaces are allowed");
         }
         return s;
     } else
     {
         return super.verify(stringproperty, s, httprequest, hashmap);
     }
 }

 public String getProperty(StringProperty stringproperty)
     throws NullPointerException
 {
     if(stringproperty == pDiagnosticText)
     {
         if(getProperty(pCategory).equals("good"))
         {
             return "";
         } else
         {
             return diagnostic(getProperty(pServer), getPropertyAsInteger(pStatus));
         }
     } else
     {
         return super.getProperty(stringproperty);
     }
 }

 public Enumeration getRuntimeProperties(Vector vector, boolean flag)
 {
     vector.add("diagnosticText");
     return super.getRuntimeProperties(vector, flag);
 }

 public static String[] getNameTagsOptions()
 {
     return nameTags;
 }

 public static String[] getServerTagsOptions()
 {
     return serverTags;
 }

 static 
 {
     serverTags = null;
     nameTags = null;
     serverTags = TextUtils.getSettingAsStringArray("_dnsServerTags");
     if(serverTags == null)
     {
         serverTags = DEFAULT_SERVER_TAGS;
     }
     nameTags = TextUtils.getSettingAsStringArray("_dnsNameTags");
     if(nameTags == null)
     {
         nameTags = DEFAULT_NAME_TAGS;
     }
     pServer = new StringProperty("_server");
     pServer.setDisplayText("DNS Server Address", "IP address of the DNS server (example: 206.168.191.1)");
     pServer.setParameterOptions(true, 1, false);
     pHostName = new StringProperty("_hostname");
     pHostName.setDisplayText("Host Name", "the hostname to lookup (example: demo." + Platform.exampleDomain + ")");
     pHostName.setParameterOptions(true, 2, false);
     pHostIP = new StringProperty("_content");
     pHostIP.setDisplayText("Host Address", "optional IP address or list of addresses to verify against for the hostname (examples 206.168.191.21 or 206.168.191.21,206.168.191.46)");
     pHostIP.setParameterOptions(true, 3, true);
     pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
     pRoundTripTime.setLabel("round trip time");
     pRoundTripTime.setStateOptions(1);
     pStatus = new StringProperty("status");
     pStatusText = new StringProperty("statusText");
     StringProperty astringproperty[] = {
         pServer, pHostName, pHostIP, pRoundTripTime, pStatus, pStatusText
     };
     addProperties("COM.dragonflow.StandardMonitor.DNSMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.DNSMonitor", Rule.stringToClassifier("status = 200\tgood"));
     addClassElement("COM.dragonflow.StandardMonitor.DNSMonitor", Rule.stringToClassifier("status <> 200\terror"));
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "description", "Verifies that a DNS server is working.");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "help", "DnsMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "title", "DNS");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "class", "DNSMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "target", "_server");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "toolName", "DNS Lookup");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "topazName", "DNS");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "topazType", "System Resources");
     setClassProperty("COM.dragonflow.StandardMonitor.DNSMonitor", "toolDescription", "Test a DNS to see if it can resolve a domain name.");
     StringProperty astringproperty1[] = {
         pRoundTripTime, pStatusText
     };
     setClassPropertyByObject("COM.dragonflow.StandardMonitor.DNSMonitor", "history", astringproperty1);
 }
}
