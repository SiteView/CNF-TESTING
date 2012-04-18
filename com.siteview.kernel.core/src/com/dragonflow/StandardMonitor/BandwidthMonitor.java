/*
 * 
 * Created on 2005-3-5 14:20:52
 *
 * BandwidthMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>BandwidthMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Page.CGI;
import com.dragonflow.Properties.BooleanProperty;
import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.SiteView.AtomicMonitor;
import com.dragonflow.SiteView.Monitor;
import com.dragonflow.SiteView.MonitorGroup;
import com.dragonflow.SiteView.Rule;
import com.dragonflow.SiteView.SiteViewGroup;
import com.dragonflow.SiteView.SubGroup;
import com.dragonflow.SiteViewException.SiteViewException;
import com.dragonflow.Utils.TextUtils;

public class BandwidthMonitor extends AtomicMonitor
{

 static StringProperty pRunMonitors;
 static StringProperty pDelay;
 static StringProperty pItems;
 static StringProperty pOperation;
 static StringProperty pStatus;
 static StringProperty pResult;
 static StringProperty pStatic;
 static StringProperty pValueLabels;
 public static final int STAT_COUNT = 2;
 public static final int STRING_COUNT = 2;
 HashMap labelsCache;

 public BandwidthMonitor()
 {
     labelsCache = null;
 }

 public String getHostname()
 {
     return "BandwidthHost";
 }

 public Array getLogProperties()
 {
     Array array = super.getLogProperties();
     array.add(pResult);
     array.add(pStatus);
     return array;
 }

 protected double[] initializeStats()
 {
     double ad[] = new double[2];
     for(int i = 0; i < ad.length; i++)
     {
         ad[i] = 0.0D;
     }

     return ad;
 }

 protected String[] initializeNameList()
 {
     String as[] = new String[2];
     for(int i = 0; i < as.length; i++)
     {
         as[i] = "";
     }

     return as;
 }

 protected void updateStats(double ad[], String as[], Monitor monitor, int i)
 {
     Enumeration enumeration = monitor.getStatePropertyObjects();
     do
     {
         if(!enumeration.hasMoreElements())
         {
             break;
         }
         StringProperty stringproperty = (StringProperty)enumeration.nextElement();
         if(stringproperty.getName().equals("snmpValue") || stringproperty.getName().equals("result") || stringproperty.getName().equals("value") || stringproperty.getName().equals("value0") || stringproperty.getName().equals("column1"))
         {
             String s = monitor.getProperty(stringproperty);
             if(!s.equals("n/a"))
             {
                 ad[i] = TextUtils.toDouble(s);
             } else
             {
                 ad[i] = -999D;
             }
             as[i] = monitor.getProperty("_name");
         }
     } while(true);
 }

 protected void addToMonitorList(Monitor monitor, Array array, HashMap hashmap)
 {
     if(monitor == this)
     {
         return;
     }
     String s = monitor.getProperty(pGroupID) + " " + monitor.getProperty(pID);
     if(hashmap.get(s) == null)
     {
         hashmap.put(s, monitor);
         array.add(monitor);
     }
 }

 protected Array getMonitorsToRun()
 {
     HashMap hashmap = new HashMap();
     Array array = new Array();
     SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
     Enumeration enumeration = getMultipleValues(pItems);
label0:
     do
     {
         if(!enumeration.hasMoreElements())
         {
             break;
         }
         String s = (String)enumeration.nextElement();
         String as[] = TextUtils.split(s);
         Object obj = null;
         Object obj1 = null;
         if(as.length == 1)
         {
             MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElement(as[0]);
             if(monitorgroup == null)
             {
                 continue;
             }
             Enumeration enumeration1 = monitorgroup.getMonitors();
             do
             {
                 Monitor monitor;
                 do
                 {
                     if(!enumeration1.hasMoreElements())
                     {
                         continue label0;
                     }
                     monitor = (Monitor)enumeration1.nextElement();
                 } while(!(monitor instanceof AtomicMonitor));
                 addToMonitorList(monitor, array, hashmap);
             } while(true);
         }
         if(as.length > 1)
         {
             String s1 = as[0] + SiteViewGroup.ID_SEPARATOR + as[1];
             MonitorGroup monitorgroup1 = (MonitorGroup)siteviewgroup.getElement(as[0]);
             if(monitorgroup1 != null)
             {
                 Monitor monitor1 = (Monitor)siteviewgroup.getElement(s1);
                 if(monitor1 != null)
                 {
                     addToMonitorList(monitor1, array, hashmap);
                 }
             }
         }
     } while(true);
     return array;
 }

 protected void checkSequentially(double ad[], String as[], long l, String s)
 {
     Array array = getMonitorsToRun();
     long l1 = getSettingAsLong("_CompositeStartupTime", 500);
     long l2 = getSettingAsLong("_CompositeCheckDelay", 500);
     int i = 0;
     AtomicMonitor atomicmonitor = (AtomicMonitor)array.get(i);
     do
     {
         if(atomicmonitor == null)
         {
             break;
         }
         try
         {
             BandwidthMonitor bandwidthmonitor = this;
             atomicmonitor.setTemporaryOwner(bandwidthmonitor);
             boolean flag = atomicmonitor.runUpdate(true);
             if(flag)
             {
                 progressString += "Running monitor " + atomicmonitor.getProperty(pName) + "\n";
                 setProperty(pStateString, "checking " + atomicmonitor.getProperty(pName) + "...");
                 try
                 {
                     Thread.sleep(l1);
                 }
                 catch(InterruptedException interruptedexception1) { }
                 while(atomicmonitor.getProperty(pRunning).length() > 0) 
                 {
                     try
                     {
                         Thread.sleep(l2);
                     }
                     catch(InterruptedException interruptedexception2) { }
                 }
             }
         }
         finally
         {
             updateStats(ad, as, atomicmonitor, i++);
             atomicmonitor.setTemporaryOwner(null);
         }
         if(atomicmonitor.getProperty(pCategory).equals(ERROR_CATEGORY))
         {
             if(s.equals("stop"))
             {
                 i = array.size() + 1;
                 progressString += "Skipping remaining monitors\n";
             } else
             if(s.equals("last") && i < array.size())
             {
                 progressString += "Skipping to last monitor\n";
                 i = array.size() - 1;
             }
         }
         if(i < array.size())
         {
             atomicmonitor = (AtomicMonitor)array.get(i);
         } else
         {
             atomicmonitor = null;
         }
         if(l > 0L && atomicmonitor != null)
         {
             try
             {
                 Thread.sleep(l);
             }
             catch(InterruptedException interruptedexception) { }
         }
     } while(true);
 }

 synchronized HashMap getLabels()
 {
     if(labelsCache == null)
     {
         labelsCache = new HashMap();
         if(getProperty(pValueLabels).length() > 0)
         {
             String s = getProperty(pValueLabels);
             labelsCache.add("Result", s);
         } else
         {
             labelsCache.add("Result", "Result");
         }
     }
     return labelsCache;
 }

 public String getPropertyName(StringProperty stringproperty)
 {
     String s = stringproperty.getName();
     String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
     if(s1.length() == 0)
     {
         s1 = s;
     }
     return s1;
 }

 public String GetPropertyLabel(StringProperty stringproperty, boolean flag)
 {
     String s = stringproperty.printString();
     String s1 = TextUtils.getValue(getLabels(), s);
     if(s1.length() != 0)
     {
         return s1;
     }
     if(flag)
     {
         return "";
     } else
     {
         return s;
     }
 }

 protected boolean update()
 {
     double ad[] = initializeStats();
     String as[] = initializeNameList();
     Enumeration enumeration = getMultipleValues(pItems);
     SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
     long l = getPropertyAsLong(pDelay) * 1000L;
     String s = getProperty(pStatic);
     s = s.trim();
     if(getPropertyAsBoolean(pRunMonitors))
     {
         checkSequentially(ad, as, l, "");
     } else
     {
         int i = 0;
         do
         {
             if(!enumeration.hasMoreElements())
             {
                 break;
             }
             String s2 = (String)enumeration.nextElement();
             Monitor monitor = (Monitor)siteviewgroup.getElement(s2.replace(' ', '/'));
             if(monitor != this)
             {
                 if(monitor != null)
                 {
                     if(!(monitor instanceof MonitorGroup))
                     {
                         updateStats(ad, as, monitor, i++);
                     }
                 } else
                 {
                     LogManager.log("Error", "Could not get monitor " + s2 + " in Composite Monitor " + getProperty(pName));
                 }
             }
         } while(true);
     }
     String s1 = getProperty(pOperation);
     double d = 0.0D;
     double d1 = ad[0];
     double d2 = ad[1];
     String s3 = "";
     String s4 = "ok";
     if(s1.equals("Add"))
     {
         if(d1 != -999D && d2 != -999D)
         {
             d = d1 + d2;
             s3 = s3 + as[0] + " = " + d1 + ", " + as[1] + " = " + d2 + ", Add Result= " + d;
         } else
         {
             s4 = "n/a";
             s3 = s3 + " Add Result= n/a";
         }
     } else
     if(s1.equals("Multiply"))
     {
         if(d1 != -999D && d2 != -999D)
         {
             d = d1 * d2;
             s3 = s3 + as[0] + " = " + d1 + ", " + as[1] + " = " + d2 + ", Multiply Result= " + d;
         } else
         {
             s4 = "n/a";
             s3 = s3 + " Multiply Result= n/a";
         }
     } else
     if(s1.equals("Subtract12"))
     {
         if(d1 != -999D && d2 != -999D)
         {
             d = d1 - d2;
             s3 = s3 + as[0] + " = " + d1 + ", " + as[1] + " = " + d2 + ", Subtract Result= " + d;
         } else
         {
             s4 = "n/a";
             s3 = s3 + " Subtract Result= n/a";
         }
     } else
     if(s1.equals("Subtract21"))
     {
         if(d1 != -999D && d2 != -999D)
         {
             d = d2 - d1;
             s3 = s3 + as[0] + " = " + d1 + ", " + as[1] + " = " + d2 + ", Subtract Result= " + d;
         } else
         {
             s4 = "n/a";
             s3 = s3 + " Subtract Result= n/a";
         }
     } else
     if(s1.equals("Divide12"))
     {
         if(d1 != -999D && d2 != -999D && d2 != 0.0D)
         {
             d = d1 / d2;
             s3 = s3 + as[0] + " = " + d1 + ", " + as[1] + " = " + d2 + ", Divide Result= " + d;
         } else
         {
             s4 = "n/a";
             s3 = s3 + " Divide Result= n/a";
         }
     } else
     if(s1.equals("Divide21"))
     {
         if(d1 != -999D && d2 != -999D && d1 != 0.0D)
         {
             d = d2 / d1;
             s3 = s3 + as[0] + " = " + d1 + ", " + as[1] + " = " + d2 + ", Divide Result= " + d;
         } else
         {
             s4 = "n/a";
             s3 = s3 + " Divide Result= n/a";
         }
     }
     if(!s4.equals("n/a") && s.length() > 0)
     {
         String s5 = s.substring(0, 1);
         s = s.substring(1);
         if(s.length() > 0)
         {
             double d3 = TextUtils.toDouble(s);
             if(d3 != 0.0D && d != -999D)
             {
                 if(s5.equals("+"))
                 {
                     d += d3;
                 } else
                 if(s5.equals("*"))
                 {
                     d *= d3;
                 } else
                 if(s5.equals("-"))
                 {
                     d -= d3;
                 } else
                 if(s5.equals("/"))
                 {
                     d /= d3;
                 }
             }
         }
         int j = s3.indexOf("Result= ");
         s3 = s3.substring(0, j) + "Result= " + d;
     }
     if(stillActive())
     {
         synchronized(this)
         {
             if(s4.equals("ok"))
             {
                 setProperty(pResult, String.valueOf(d));
                 setProperty(pStatus, s4);
                 if(getProperty(pValueLabels).length() > 0)
                 {
                     setProperty(pStateString, s3 + ", " + (String)getLabels().get("result") + " = " + d);
                 } else
                 {
                     setProperty(pStateString, s3);
                 }
             } else
             {
                 setProperty(pResult, "n/a");
                 setProperty(pStatus, "n/a");
                 setProperty(pNoData, "n/a");
                 setProperty(pStateString, s3);
             }
         }
     }
     return true;
 }

 public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
     throws SiteViewException
 {
     if(scalarproperty == pItems)
     {
         SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
         Array array = CGI.getAllowedGroupIDsForAccount(httprequest);
         Enumeration enumeration = (Enumeration) array.iterator();
         Vector vector1 = new Vector();
         Vector vector2 = new Vector();
         String s = getFullID();
         while(enumeration.hasMoreElements()) 
         {
             MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElement((String)enumeration.nextElement());
             if(monitorgroup != null)
             {
                 Enumeration enumeration2 = monitorgroup.getMonitors();
                 while(enumeration2.hasMoreElements()) 
                 {
                     Monitor monitor = (Monitor)enumeration2.nextElement();
                     if(!(monitor instanceof SubGroup) && !s.equals(monitor.getFullID()) && ("SNMPMonitor".equals(monitor.getClassProperty("class")) || (monitor instanceof BandwidthMonitor) || "ScriptMonitor".equals(monitor.getClassProperty("class")) || "NTCounterMonitor".equals(monitor.getClassProperty("class")) || "DatabaseMonitor".equals(monitor.getClassProperty("class")))) //dingbing.xu
                     {
                         vector2.addElement(monitor.getProperty(pGroupID) + " " + monitor.getProperty(pID));
                         vector2.addElement(monitorgroup.getProperty(pName) + ": " + monitor.getProperty(pName));
                     }
                 }
             }
         }
         for(Enumeration enumeration1 = vector2.elements(); enumeration1.hasMoreElements(); vector1.addElement(enumeration1.nextElement())) { }
         return vector1;
     } else
     if(scalarproperty == pOperation)
     {
         Vector vector = new Vector();
         vector.addElement("Add");
         vector.addElement("Add");
         vector.addElement("Multiply");
         vector.addElement("Multiply");
         vector.addElement("Subtract12");
         vector.addElement("Subtract 1 from 2");
         vector.addElement("Subtract21");
         vector.addElement("Subtract 2 from 1");
         vector.addElement("Divide12");
         vector.addElement("Divide 1 by 2");
         vector.addElement("Divide21");
         vector.addElement("Divide 2 by 1");
         return vector;
     } else
     {
         return super.getScalarValues(scalarproperty, httprequest, cgi);
     }
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
 {
     if(stringproperty == pItems)
     {
         if(s.trim().length() == 0)
         {
             hashmap.put(stringproperty, "no monitors selected");
         }
         return s;
     } else
     {
         return super.verify(stringproperty, s, httprequest, hashmap);
     }
 }

 public int getCostInLicensePoints()
 {
     return 0;
 }

 static 
 {
     pItems = new ScalarProperty("_item", "");
     ((ScalarProperty)pItems).multiple = true;
     ((ScalarProperty)pItems).listSize = 3;
     pItems.setDisplayText("Items", "Select two SNMP, Script, or Database monitors to be checked and their results calculated.");
     pItems.setParameterOptions(true, 1, false);
     pOperation = new ScalarProperty("_opertaion", "");
     pOperation.setDisplayText("Operation", "Select the operation to be performed on the results of the above monitors. ");
     pOperation.setParameterOptions(true, 2, false);
     pRunMonitors = new BooleanProperty("_checkSequentially", "");
     pRunMonitors.setDisplayText("Run Monitors", "Run each monitor before performing calculation.");
     pRunMonitors.setParameterOptions(true, 3, true);
     pDelay = new NumericProperty("_delay", "0");
     pDelay.setDisplayText("Monitor Delay", "If running each monitor, delay in seconds between monitor runs.");
     pDelay.setParameterOptions(true, 4, true);
     pStatic = new StringProperty("_static", "");
     pStatic.setDisplayText("Constant", "Operate a constant on the Operation result. For example entering *8 will multiply the Operation result by 8.");
     pStatic.setParameterOptions(true, 5, true);
     pValueLabels = new StringProperty("_valeLabels", "");
     pValueLabels.setDisplayText("Result Label", "Optional label for the result of the formula calculation.");
     pValueLabels.setParameterOptions(true, 6, true);
     pResult = new NumericProperty("result");
     pResult.setLabel("Result");
     pResult.setStateOptions(1);
     pStatus = new StringProperty("status");
     StringProperty astringproperty[] = {
         pItems, pRunMonitors, pDelay, pOperation, pResult, pStatus, pStatic, pValueLabels
     };
     addProperties("com.dragonflow.StandardMonitor.BandwidthMonitor", astringproperty);
     addClassElement("com.dragonflow.StandardMonitor.BandwidthMonitor", Rule.stringToClassifier("status == 'n/a'\terror", true));
     addClassElement("com.dragonflow.StandardMonitor.BandwidthMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "description", "Monitor two SNMP, Script, or Database monitors and perform an arithmetic operation on the results.");
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "help", "BandwidthMon.htm");
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "title", "Formula Composite");
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "class", "BandwidthMonitor");
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "topazName", "Bandwidth");
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "topazType", "System Resources");
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "target", "_opertaion");
     setClassProperty("com.dragonflow.StandardMonitor.BandwidthMonitor", "loadable", "true");
 }
}