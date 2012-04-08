/*
 * 
 * Created on 2005-3-7 0:53:23
 *
 * ComPlusMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ComPlusMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableURLContentBase;
import COM.dragonflow.SiteView.ComPlusConnector;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import jgl.Array;

public class ComPlusMonitor extends BrowsableURLContentBase
{

 public static StringProperty pHostName;
 public static StringProperty pPortNumber;
 public static StringProperty pUserName;
 public static StringProperty pPassword;
 private ComPlusConnector myComPlusConn;

 public ComPlusMonitor()
 {
     myComPlusConn = null;
 }

 protected boolean update()
 {
     StringBuffer stringbuffer = new StringBuffer();
     HashMap hashmap = new HashMap();
     int i;
     for(i = 0; i < nMaxCounters && getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1)).length() != 0; i++) { }
     String as[] = new String[i];
     if(myComPlusConn == null)
     {
         myComPlusConn = getMyComPlusConnection();
     }
     boolean flag = synchronizeWithSSMetricsMap();
     if(flag)
     {
         flag = myComPlusConn.syncProbeWithSSMetricsMap(hashmap, stringbuffer, this);
     }
     if(flag)
     {
         if(hashmap.size() != 0)
         {
             parseForCounterValues(hashmap, as);
         } else
         {
             String s = myComPlusConn.getMetricValuesFromProbe(stringbuffer, this);
             parseForCounterValues(s, as);
         }
     }
     int j = 0;
     if(stillActive())
     {
         synchronized(this)
         {
             String s1 = "";
             if(stringbuffer.length() <= 0)
             {
                 for(int k = 0; k < as.length; k++)
                 {
                     String s2 = getProperty(PROPERTY_NAME_COUNTER_NAME + (k + 1));
                     if(as[k].equals(""))
                     {
                         setProperty(PROPERTY_NAME_COUNTER_VALUE + (k + 1), "n/a");
                         s1 = s1 + s2 + " = " + "<not found>";
                         if(k != as.length - 1)
                         {
                             s1 = s1 + ", ";
                         }
                         j++;
                         continue;
                     }
                     setProperty(PROPERTY_NAME_COUNTER_VALUE + (k + 1), TextUtils.removeChars(as[k], ","));
                     s1 = s1 + s2 + " = " + as[k];
                     if(k != as.length - 1)
                     {
                         s1 = s1 + ", ";
                     }
                 }

                 setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), j);
             } else
             {
                 for(int l = 0; l < nMaxCounters; l++)
                 {
                     setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1), "n/a");
                 }

                 setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
                 s1 = stringbuffer.toString();
             }
             setPrecision();
             setProperty(pStateString, s1);
         }
     }
     return true;
 }

 public String getBrowseData(StringBuffer stringbuffer)
 {
     StringBuffer stringbuffer1 = new StringBuffer();
     if(myComPlusConn == null)
     {
         myComPlusConn = getMyComPlusConnection();
     }
     boolean flag = myComPlusConn.getMetricListFromProbe(stringbuffer1, stringbuffer, this, getBrowseTreeTimeout());
     if(!flag)
     {
         return "";
     }
     String s = stringbuffer1.toString();
     if(s.indexOf(getStatString()) < 0)
     {
         stringbuffer.append("File does not contain system statistics.");
         return "";
     } else
     {
         String s1 = createXMLFromTemplate(s);
         return s1;
     }
 }

 protected String createXMLFromTemplate(String s)
 {
     String s1 = s;
     int i = s.indexOf("<browse_data>");
     if(i > 0)
     {
         s1 = s.substring(i);
     }
     return s1;
 }

 protected void parseForCounterValues(String s, String as[])
 {
     Object obj = null;
     HashMap hashmap = new HashMap();
     for(int i = 0; i < as.length; i++)
     {
         as[i] = "";
     }

     myComPlusConn.parseProbeReturnValuesIntoHashMap(hashmap, s);
     for(int j = 0; j < nMaxCounters; j++)
     {
         String s1 = getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1));
         if(s1.length() == 0)
         {
             break;
         }
         String s2 = (String)hashmap.get(s1);
         if(s2 != null)
         {
             as[j] = s2;
         }
     }

 }

 private void parseForCounterValues(HashMap hashmap, String as[])
 {
     Object obj = null;
     for(int i = 0; i < as.length; i++)
     {
         as[i] = "";
     }

     for(int j = 0; j < nMaxCounters; j++)
     {
         String s = getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1));
         if(s.length() == 0)
         {
             break;
         }
         String s1 = (String)hashmap.get(s);
         if(s1 != null)
         {
             as[j] = s1;
         }
     }

 }

 protected void setPrecision()
 {
 }

 public String setBrowseName(Array array)
 {
     String s = "";
     if(array.size() <= 0)
     {
         return s;
     }
     for(int i = array.size() - 1; i >= 0; i--)
     {
         if(s.length() > 0)
         {
             s = s + "\\";
         }
         s = s + (String)array.at(i);
     }

     return s;
 }

 public Array getConnectionProperties()
 {
     Array array = super.getConnectionProperties();
     array.insert(0, pHostName);
     array.insert(1, pPortNumber);
     array.insert(2, pUserName);
     array.insert(3, pPassword);
     return array;
 }

 public String getHostname()
 {
     String s = getProperty(pHostName);
     if(s == null || s.length() == 0)
     {
         return "";
     } else
     {
         return s + ":" + getProperty(pPortNumber);
     }
 }

 protected String getStatString()
 {
     return "<browse_data>";
 }

 private boolean synchronizeWithSSMetricsMap()
 {
     boolean flag = true;
     String s = getProperty(pFullID);
     HashSet hashset = new HashSet();
     int i = 0;
     do
     {
         if(i >= nMaxCounters)
         {
             break;
         }
         String s1 = getProperty(PROPERTY_NAME_COUNTER_NAME + (i + 1));
         if(s1.length() == 0)
         {
             break;
         }
         flag = recordSSMetricUse(s1, s);
         hashset.add(s1);
         i++;
     } while(true);
     Iterator iterator = getSSMetricsListForMonitor(s).iterator();
     do
     {
         try
         {
             if(!iterator.hasNext())
             {
                 break;
             }
             String s2 = (String)iterator.next();
             if(!hashset.contains(s2))
             {
                 flag = removeSSMetricUse(s2, s);
             }
             continue;
         }
         catch(ConcurrentModificationException concurrentmodificationexception) { }
         break;
     } while(true);
     if(debug)
     {
         printSSMetricsMap();
     }
     return flag;
 }

 public void notifyDelete()
 {
     removeSSMetricUse(null, getProperty(pFullID));
     super.notifyDelete();
     if(debug)
     {
         printSSMetricsMap();
     }
 }

 public void notifyMove(String s)
 {
     String s1 = s;
     if(!s.startsWith("SiteView"))
     {
         s1 = "SiteView/" + s;
     }
     removeSSMetricUse(null, s1);
     super.notifyMove(s1);
 }

 private boolean recordSSMetricUse(String s, String s1)
 {
     boolean flag = true;
     HashSet hashset = null;
     if(myComPlusConn == null)
     {
         myComPlusConn = getMyComPlusConnection();
     }
     synchronized(myComPlusConn.getMetricsMap())
     {
         hashset = (HashSet)myComPlusConn.getMetricsMap().get(s);
     }
     if(hashset != null)
     {
         hashset.add(s1);
     } else
     {
         HashSet hashset1 = new HashSet();
         hashset1.add(s1);
         synchronized(myComPlusConn.getMetricsMap())
         {
             myComPlusConn.getMetricsMap().put(s, hashset1);
         }
     }
     return flag;
 }

 private boolean removeSSMetricUse(String s, String s1)
 {
     boolean flag = true;
     Object obj = null;
     if(myComPlusConn == null)
     {
         myComPlusConn = getMyComPlusConnection();
     }
     synchronized(myComPlusConn.getMetricsMap())
     {
         Iterator iterator;
         if(s != null)
         {
             HashSet hashset = new HashSet();
             hashset.add(s);
             iterator = hashset.iterator();
         } else
         {
             iterator = myComPlusConn.getMetricsMap().keySet().iterator();
         }
         ArrayList arraylist = new ArrayList();
         do
         {
             if(!iterator.hasNext())
             {
                 break;
             }
             String s2 = (String)iterator.next();
             HashSet hashset1 = (HashSet)myComPlusConn.getMetricsMap().get(s2);
             if(hashset1 != null)
             {
                 hashset1.remove(s1);
                 if(hashset1.isEmpty())
                 {
                     arraylist.add(s2);
                 }
             }
         } while(true);
         for(int i = 0; i < arraylist.size(); i++)
         {
             myComPlusConn.getMetricsMap().remove(arraylist.get(i));
         }

     }
     return flag;
 }

 private HashSet getSSMetricsListForMonitor(String s)
 {
     HashSet hashset = new HashSet();
     synchronized(myComPlusConn.getMetricsMap())
     {
         Iterator iterator = myComPlusConn.getMetricsMap().keySet().iterator();
         do
         {
             if(!iterator.hasNext())
             {
                 break;
             }
             String s1 = (String)iterator.next();
             HashSet hashset1 = (HashSet)myComPlusConn.getMetricsMap().get(s1);
             if(hashset1.contains(s))
             {
                 hashset.add(s1);
             }
         } while(true);
     }
     return hashset;
 }

 private ComPlusConnector getMyComPlusConnection()
 {
     Array array = getConnectionProperties();
     String s = getProperty((StringProperty)array.at(0));
     String s1 = getProperty((StringProperty)array.at(1));
     String s2 = getProperty((StringProperty)array.at(2));
     String s3 = getProperty((StringProperty)array.at(3));
     int i = getPropertyAsInteger(pTimeout);
     if(i <= 0)
     {
         i = 60;
     }
     return ComPlusConnector.getComPlusConnection(s, s1, s2, s3, getProperty(pProxy), getProperty(pProxyUserName), getProperty(pProxyPassword), this, i);
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, jgl.HashMap hashmap)
 {
     if(stringproperty == pTimeout)
     {
         if(TextUtils.isInteger(s))
         {
             if(TextUtils.toInt(s) <= 0)
             {
                 hashmap.put(stringproperty, "Timeout value must be greater than zero.");
                 return s;
             }
         } else
         {
             hashmap.put(stringproperty, "Timeout value must be numeric.");
             return s;
         }
     }
     return super.verify(stringproperty, s, httprequest, hashmap);
 }

 protected String getBrowseTreeTimeoutParameterName()
 {
     return "_comPlusMonitorGetBrowseTreeTimeout";
 }

 private int extractProbeActiveMonitorCountFromResponse(String s)
 {
     int i = 10;
     int j = s.indexOf("ActiveMonitors:");
     if(j > 0)
     {
         int k = s.indexOf("\r\n", j);
         if(k <= 0)
         {
             return 0;
         }
         String s1 = s.substring(j + 15, k);
         i = Integer.parseInt(s1.trim());
     }
     return i;
 }

 private void printSSMetricsMap()
 {
     Iterator iterator = myComPlusConn.getMetricsMap().keySet().iterator();
     System.out.println("METRICS MAP\n");
     for(; iterator.hasNext(); System.out.println("\n\n"))
     {
         String s = (String)iterator.next();
         System.out.println(s + " Used By: ");
         HashSet hashset = (HashSet)myComPlusConn.getMetricsMap().get(s);
         for(Iterator iterator1 = hashset.iterator(); iterator1.hasNext(); System.out.println((String)iterator1.next() + ",")) { }
     }

 }

 static 
 {
     SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
     ComPlusConnector.setProbeUpdateInterval(siteviewgroup.getSettingAsLong("_comPlusProbeUpdateInterval", 5000));
     String s = System.getProperty("ComPlusMonitor.debug", "false");
     debug = s.equalsIgnoreCase("true");
     pHostName = new StringProperty("_serverHostName");
     pHostName.setDisplayText("COM+ Probe Host Name", "Host name of the COM+ server-side probe (example: www.mysap.com)");
     pHostName.setParameterOptions(false, true, 2, false);
     pPortNumber = new StringProperty("_serverPortNumber", "8008");
     pPortNumber.setDisplayText("COM+ Probe Port Number", "Port number of the COM+ server-side probe (example: 8008)");
     pPortNumber.setParameterOptions(false, true, 3, false);
     pUserName = new StringProperty("_username");
     pUserName.setDisplayText("Authorization User Name", "optional user name if the COM+ server requires authorization");
     pUserName.setParameterOptions(false, true, 4, false);
     pPassword = new StringProperty("_password");
     pPassword.setDisplayText("Authorization Password", "optional password if the COM+ server requires authorization");
     pPassword.setParameterOptions(false, true, 5, false);
     pPassword.isPassword = true;
     StringProperty astringproperty[] = {
         pHostName, pPortNumber, pUserName, pPassword
     };
     String s1 = (COM.dragonflow.StandardMonitor.ComPlusMonitor.class).getName();
     addProperties(s1, astringproperty);
     addClassElement(s1, Rule.stringToClassifier("countersInError > 0\terror"));
     addClassElement(s1, Rule.stringToClassifier("always\tgood"));
     setClassProperty(s1, "description", "Monitor components running on a remote COM+ Server");
     setClassProperty(s1, "help", "ComPlusMon.htm");
     setClassProperty(s1, "title", "COM+ Server");
     setClassProperty(s1, "class", "ComPlusMonitor");
     setClassProperty(s1, "target", "_server");
     setClassProperty(s1, "topazName", "COM+");
     setClassProperty(s1, "classType", "application");
     setClassProperty(s1, "topazType", "Web Application Server");
     if(LUtils.isValidSSforXLicense(new ComPlusMonitor()))
     {
         setClassProperty(s1, "loadable", "true");
     } else
     {
         setClassProperty(s1, "loadable", "false");
     }
 }
}