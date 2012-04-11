/*
 * 
 * Created on 2005-3-7 1:11:23
 *
 * FileMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>FileMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.*;

import java.io.*;
import java.util.*;
import jgl.Array;
import jgl.HashMap;

//Referenced classes of package COM.dragonflow.StandardMonitor:
//         URLMonitor

public class FileMonitor extends AtomicMonitor
{

 static FileProperty pFilename;
 static StringProperty pCheckContent;
 static StringProperty pContentMatch;
 static StringProperty pCheckContentResetTime;
 static StringProperty pNoFileCheckExist;
 static NumericProperty pAge;
 static NumericProperty pSize;
 static StringProperty pLastChecksum;
 static StringProperty pStatus;
 static StringProperty pMatchValue;
 static StringProperty pLastCheckContentTime;

 public FileMonitor()
 {
 }

 public String getHostname()
 {
     String s = getProperty(pFilename);
     if(s.startsWith("\\\\"))
     {
         int i = s.indexOf("\\", 2);
         if(i != -1)
         {
             return s.substring(2, i);
         }
         LogManager.log("Error", "FileMonitor: getHostname() failed because the file path is illegal: " + s);
     }
     return "";
 }

 protected boolean update()
 {
     File file = pFilename.getValue(this);
     int i = 404;
     long l = -1L;
     long l1 = -1L;
     String s = "";
     String s1 = "";
     String s2 = "";
     String s3;
     if(!file.exists())
     {
         s3 = Platform.closeAndConnectNetBIOSIfRemoteDefined(getProperty(pFilename));
     }
     boolean flag = file.exists();
     if(flag)
     {
         i = 200;
         l1 = file.length();
         Date date = new Date();
         l = (date.getTime() - file.lastModified()) / 1000L / 60L;
         String s4 = I18N.UnicodeToString(getProperty(pContentMatch), I18N.nullEncoding());
         if(i == kURLok && (getPropertyAsBoolean(pCheckContent) || s4.length() > 0))
         {
             try
             {
                 String s6 = I18N.toDefaultEncoding(pFilename.getName(this));
                 String s7 = FileUtils.readFile(s6).toString();
                 if(getProperty(pCheckContent).length() > 0)
                 {
                     String s8 = getProperty(pLastChecksum);
                     String s9 = String.valueOf(PlatformNew.crc(s7));
                     if(s8.length() > 0 && !s9.equals(s8))
                     {
                         i = kURLContentChangedError;
                     }
                     s = s8;
                     boolean flag1 = true;
                     if(getProperty(pCheckContent).equals("baseline") && getPropertyAsLong(pCheckContentResetTime) < getPropertyAsLong(pLastCheckContentTime) && s8.length() > 0)
                     {
                         flag1 = false;
                     }
                     if(flag1 && getProperty(pCheckContent).equals("baseline"))
                     {
                         i = 200;
                     }
                     if(flag1)
                     {
                         s = s9;
                         setProperty(pLastCheckContentTime, Platform.timeMillis());
                     }
                 }
                 if(s4.length() > 0)
                 {
                     Array array = new Array();
                     i = TextUtils.matchExpression(s7, s4, array, new StringBuffer());
                     if(i != Monitor.kURLok)
                     {
                         String s10 = URLMonitor.getHTMLEncoding(s7);
                         i = TextUtils.matchExpression(s7, I18N.UnicodeToString(s4, s10), array, new StringBuffer());
                     }
                     if(array.size() > 0)
                     {
                         s2 = array.at(0).toString();
                     }
                 }
             }
             catch(Exception exception)
             {
                 i = kMonitorSpecificError;
                 s1 = exception.toString();
             }
         }
     }
     if(stillActive())
     {
         synchronized(this)
         {
             setProperty(pStatus, i);
             setProperty(pLastChecksum, s);
             setProperty(pMatchValue, I18N.StringToUnicode(s2, I18N.nullEncoding()));
             if(i != 200)
             {
                 setProperty(pAge, "n/a");
                 setProperty(pSize, "n/a");
                 setProperty(pMeasurement, 0);
                 setProperty(pNoData, "n/a");
                 if(i == kMonitorSpecificError)
                 {
                     setProperty(pStateString, s1);
                 } else
                 {
                     setProperty(pStateString, URLMonitor.lookupStatus(i));
                 }
             } else
             {
                 setProperty(pAge, l);
                 setProperty(pSize, l1);
                 setProperty(pMeasurement, getMeasurement(pAge, 10L));
                 String s5 = l1 + " bytes, " + l + " minutes old";
                 if(s2.length() > 0)
                 {
                     s5 = s5 + ", " + s2;
                 }
                 setProperty(pStateString, I18N.StringToUnicode(s5, I18N.nullEncoding()));
             }
         }
     }
     return true;
 }

 public Array getLogProperties()
 {
     Array array = super.getLogProperties();
     array.add(pSize);
     array.add(pAge);
     array.add(pMatchValue);
     return array;
 }

 public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
     throws SiteViewException
 {
     if(scalarproperty == pCheckContent)
     {
         Vector vector = new Vector();
         vector.addElement("");
         vector.addElement("no content checking");
         vector.addElement("on");
         vector.addElement("compare to last contents ");
         vector.addElement("baseline");
         vector.addElement("compare to saved contents");
         vector.addElement("reset");
         vector.addElement("reset saved contents");
         return vector;
     } else
     {
         return super.getScalarValues(scalarproperty, httprequest, cgi);
     }
 }

 public Enumeration getStatePropertyObjects(boolean flag)
 {
     Enumeration enumeration = super.getStatePropertyObjects(flag);
     boolean flag1 = false;
     if(getProperty(pContentMatch).length() > 0 && TextUtils.isValueExpression(getProperty(pContentMatch)))
     {
         flag1 = true;
     }
     Array array = new Array();
     do
     {
         if(!enumeration.hasMoreElements())
         {
             break;
         }
         StringProperty stringproperty = (StringProperty)enumeration.nextElement();
         if(stringproperty == pMatchValue)
         {
             if(flag1)
             {
                 array.add(stringproperty);
             }
         } else
         {
             array.add(stringproperty);
         }
     } while(true);
     return array.elements();
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
 {
     if(stringproperty == pNoFileCheckExist)
     {
         String s1 = getProperty(pFilename);
         if(!TextUtils.isSubstituteExpression(s1))
         {
             if(s1.length() > 0)
             {
                 File file = new File(s1);
                 String s2 = "";
                 boolean flag = s.length() == 0;
                 if(!file.exists())
                 {
                     s2 = Platform.closeAndConnectNetBIOSIfRemoteDefined(s1);
                 }
                 if(flag)
                 {
                     if(s2.length() > 0)
                     {
                         hashmap.put(pFilename, pFilename.getLabel() + ": " + s2);
                     } else
                     if(!file.exists())
                     {
                         hashmap.put(pFilename, " could not find log file");
                     }
                 }
             } else
             {
                 hashmap.put(pFilename, pFilename.getLabel() + " missing file name");
             }
         }
     } else
     if(stringproperty == pCheckContent)
     {
         if(s.equals("reset") || s.equals("baseline") && getProperty(pCheckContentResetTime).length() == 0)
         {
             setProperty(pCheckContentResetTime, Platform.timeMillis());
             s = "baseline";
         } else
         if(s.length() > 0 && !s.equals("on") && !s.equals("baseline"))
         {
             s = "on";
         }
         return s;
     }
     return super.verify(stringproperty, s, httprequest, hashmap);
 }

 public static void main(String args[])
 {
     File file = new File(args[0]);
     int i = TextUtils.toInt(args[1]);
     for(int j = 0; j < i; j++)
     {
         try
         {
             File file1 = new File(file, "file" + j + ".txt");
             FileUtils.writeFile(file1.getAbsolutePath(), "just some test contents");
         }
         catch(IOException ioexception)
         {
             System.out.println("EXCEPTION WRITING FILES");
         }
     }

 }

 static 
 {
     pFilename = new FileProperty("_filename", "");
     pFilename.fieldLabels("File Name", "Enter the full path or UNC name of the file (e.g. e:\\mydir\\myfile.log).");
     pFilename.setParameterOptions(true, 1, false);
     pContentMatch = new StringProperty("_content");
     pContentMatch.setDisplayText("Match Content", "optional text to match against content of the file. By default successful match makes monitor good.");
     pContentMatch.setParameterOptions(true, 2, true);
     pCheckContent = new ScalarProperty("_checkContent", "");
     pCheckContent.setDisplayText("Check for Content Changes", "generate error if the content of the file changes - resetting the saved contents updates the contents checked against during the next monitor run");
     pCheckContent.setParameterOptions(true, 3, true);
     pNoFileCheckExist = new BooleanProperty("_noFileCheckExist");
     pNoFileCheckExist.setDisplayText("No Error on File Not Found. <b>You MUST set the \"Good if\" condition to 'Status == 404'</b>", "if the file is not found then don't error.");
     pNoFileCheckExist.setParameterOptions(true, 4, true);
     pCheckContentResetTime = new StringProperty("_checkContentResetTime", "");
     pAge = new NumericProperty("age", "0", "minutes");
     pAge.setLabel("file age");
     pAge.setStateOptions(1);
     pSize = new NumericProperty("size", "0", "bytes");
     pSize.setLabel("size");
     pSize.setStateOptions(2);
     pMatchValue = new NumericProperty("matchValue");
     pMatchValue.setLabel("content match");
     pMatchValue.setStateOptions(3);
     pLastChecksum = new StringProperty("lastChecksum");
     pLastCheckContentTime = new StringProperty("lastCheckContentTime", "");
     pStatus = new StringProperty("status");
     pStatus.setLabel("status");
     pStatus.setIsThreshold(true);
     StringProperty astringproperty[] = {
         pFilename, pCheckContent, pAge, pSize, pLastChecksum, pContentMatch, pNoFileCheckExist, pStatus, pMatchValue, pLastCheckContentTime, 
         pCheckContentResetTime
     };
     addProperties("COM.dragonflow.StandardMonitor.FileMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.FileMonitor", Rule.stringToClassifier("status == 200\tgood"));
     addClassElement("COM.dragonflow.StandardMonitor.FileMonitor", Rule.stringToClassifier("status != 200\terror"));
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "description", "Monitor the size and age of a file.");
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "help", "FileSMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "title", "File");
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "class", "FileMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "classType", "advanced");
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "target", "_filename");
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "topazName", "Windows Resources");
     setClassProperty("COM.dragonflow.StandardMonitor.FileMonitor", "topazType", "System Resources");
 }
}