/*
 * 
 * Created on 2005-3-7 1:00:23
 *
 * DiskSpaceMonitor.java
 *
 * History:
 *
 */
package com.dragonflow.StandardMonitor;

/**
 * Comment for <code>DiskSpaceMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import com.dragonflow.HTTP.HTTPRequest;
import com.dragonflow.Log.LogManager;
import com.dragonflow.Page.CGI;
import com.dragonflow.Properties.*;
import com.dragonflow.SiteView.*;
import com.dragonflow.SiteViewException.SiteViewException;
import java.net.URLEncoder;
import java.util.Vector;
import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class DiskSpaceMonitor extends ServerMonitor
{

 static ScalarProperty pDisk;
 static ScalarProperty pFileSystem;
 static StringProperty pPercentFull;
 static StringProperty pFreeSpace;

 public DiskSpaceMonitor()
 {
 }

 StringProperty diskProperty()
 {
     return getPropertyObject("_disk");
 }

 protected boolean update()
 {
     String s = getProperty(diskProperty());
     Array array = null;
     if(monitorDebugLevel == 3)
     {
         array = new Array();
     }
     String s1 = getProperty(pMachineName);
     long al[] = Platform.getDiskFull(s, s1, this, array);
     long l = al[0];
     long l1 = al[2];
     long l2 = l1 - al[1];
     String s2 = "" + l2 / 0x100000L;
     String s3 = "" + l1 / 0x100000L;
     if(stillActive())
     {
         synchronized(this)
         {
             if(l == -1L)
             {
                 setProperty(pPercentFull, "n/a");
                 setProperty(pFreeSpace, "n/a");
                 setProperty(pMeasurement, 0);
                 setProperty(pStateString, "no data");
                 setProperty(pNoData, "n/a");
                 if(monitorDebugLevel == 3 && array != null)
                 {
                     StringBuffer stringbuffer = new StringBuffer();
                     for(int i = 0; i < array.size(); i++)
                     {
                         stringbuffer.append(array.get(i) + "\n");
                     }

                     LogManager.log("Error", "DiskSpaceMonitor: " + getFullID() + " failed, output:\n" + stringbuffer);
                 }
             } else
             {
                 setProperty(pPercentFull, l);
                 setProperty(pFreeSpace, l2 / 0x100000L);
                 setProperty(pMeasurement, getMeasurement(pPercentFull));
                 setProperty(pStateString, l + "% full, " + s2 + "MB free, " + s3 + "MB total");
             }
         }
     }
     return true;
 }

 public Array getLogProperties()
 {
     Array array = super.getLogProperties();
     array.add(pPercentFull);
     array.add(pFreeSpace);
     return array;
 }

 public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
     throws SiteViewException
 {
     if(scalarproperty == diskProperty())
     {
         return Platform.getDisks(Machine.getFullMachineID(getProperty(pMachineName), httprequest));
     } else
     {
         return super.getScalarValues(scalarproperty, httprequest, cgi);
     }
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
 {
     if(stringproperty == diskProperty())
     {
         return s;
     } else
     {
         return super.verify(stringproperty, s, httprequest, hashmap);
     }
 }

 public String getTestURL()
 {
     HashMap hashmap = MasterConfig.getMasterConfig();
     int i = Machine.getOS(getProperty(pMachineName));
     if(Platform.isWindows(i))
     {
         String s = URLEncoder.encode(getProperty(pMachineName));
         String s1 = "/SiteView/cgi/go.exe/SiteView?page=perfCounter&counterObject=LogicalDisk&machineName=" + s;
         return s1;
     } else
     {
         return null;
     }
 }

 static 
 {
     pDisk = new ScalarProperty("_disk", true);
     pDisk.setWindowsPlatforms();
     pDisk.setDisplayText("Disk", "the local disk drive to be monitored<br><b>NOTE: If this field is blank, click <a href=\"/SiteView/docs/DiskMon.htm#disk\" TARGET=\"Help\">here</a> to trouble shoot the problem.");
     pDisk.setParameterOptions(true, 2, false);
     pFileSystem = new ScalarProperty("_disk", true);
     pFileSystem.setUnixPlatforms();
     pFileSystem.setDisplayText("Filesystem", "the filesystem to be monitored");
     pFileSystem.setParameterOptions(true, 2, false);
     pPercentFull = new PercentProperty("percentFull");
     pPercentFull.setLabel("percent full");
     pPercentFull.setStateOptions(1);
     pFreeSpace = new NumericProperty("freeSpace", "0", "MB");
     pFreeSpace.setLabel("MB free");
     pFreeSpace.setStateOptions(2);
     StringProperty astringproperty[] = {
         pPercentFull, pFreeSpace, pDisk, pFileSystem
     };
     addProperties("com.dragonflow.StandardMonitor.DiskSpaceMonitor", astringproperty);
     addClassElement("com.dragonflow.StandardMonitor.DiskSpaceMonitor", Rule.stringToClassifier("percentFull > 98\terror", true));
     addClassElement("com.dragonflow.StandardMonitor.DiskSpaceMonitor", Rule.stringToClassifier("percentFull > 95\twarning", true));
     addClassElement("com.dragonflow.StandardMonitor.DiskSpaceMonitor", Rule.stringToClassifier("percentFull == n/a\terror"));
     addClassElement("com.dragonflow.StandardMonitor.DiskSpaceMonitor", Rule.stringToClassifier("always\tgood"));
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "description", "Determines the amount of space used on a disk.");
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "help", "DiskMon.htm");
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "title", "Disk Space");
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "class", "DiskSpaceMonitor");
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "target", "_disk");
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "topazName", "Disk Space");
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "topazType", "System Resources");
     setClassProperty("com.dragonflow.StandardMonitor.DiskSpaceMonitor", "classType", "server");
 }
}
