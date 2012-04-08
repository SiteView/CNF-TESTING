/*
 * 
 * Created on 2005-3-7 0:59:53
 *
 * DirectoryMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>DirectoryMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import jgl.Array;
import jgl.HashMap;

public class DirectoryMonitor extends AtomicMonitor
{

 static FileProperty pPath;
 static StringProperty pMatch;
 static StringProperty pNoSubdirectories;
 static StringProperty pSize;
 static StringProperty pFileCount;
 static StringProperty pExists;
 static StringProperty pPermitted;
 static NumericProperty pAge;
 long totalSize;
 long fileCount;
 long age;
 Date now;

 public DirectoryMonitor()
 {
     totalSize = 0L;
     fileCount = 0L;
     age = 0L;
 }

 public static boolean fileIsDirectory(File file)
 {
     boolean flag = file.isDirectory();
     if(!flag)
     {
         String as[] = file.list();
         if(as != null)
         {
             flag = true;
         }
     }
     return flag;
 }

 public String getHostname()
 {
     String s = getProperty(pPath);
     if(s.startsWith("\\\\"))
     {
         int i = s.indexOf("\\", 2);
         if(i != -1)
         {
             return s.substring(2, i);
         }
         LogManager.log("Error", "DircetoryMonitor: getHostname() failed because the directory path is illegal: " + s);
     }
     return "";
 }

 protected boolean update()
 {
     File file = pPath.getValue(this);
     String s;
     if(!file.exists())
     {
         s = Platform.closeAndConnectNetBIOSIfRemoteDefined(getProperty(pPath));
     }
     boolean flag = file.exists();
     boolean flag1 = file.isDirectory();
     boolean flag2 = true;
     String s1 = I18N.UnicodeToString(getProperty(pMatch), I18N.nullEncoding());
     if(flag && flag1)
     {
         LogManager.log("RunMonitor", "Directory Found:" + getProperty(pPath));
         boolean flag3 = !getPropertyAsBoolean(pNoSubdirectories);
         totalSize = 0L;
         fileCount = 0L;
         now = new Date();
         long l = Math.min(Math.max(0L, file.lastModified()), now.getTime());
         age = (now.getTime() - l) / 1000L / 60L;
         LogManager.log("RunMonitor", "Counting Files" + (flag3 ? " recursively." : "."));
         flag2 = countFiles(file, System.getProperty("DirectoryMonitor.debug") != null, flag3, s1);
     }
     if(!flag2 || !flag)
     {
         LogManager.log("RunMonitor", "Failed to count files. Permitted = " + flag2 + "Exists = " + flag);
     }
     if(stillActive())
     {
         synchronized(this)
         {
             if(flag && flag1 && flag2)
             {
                 setProperty(pAge, age);
                 setProperty(pExists, "exists");
                 setProperty(pPermitted, "permitted");
                 setProperty(pSize, totalSize);
                 setProperty(pFileCount, fileCount);
                 setProperty(pStateString, fileCount + " files, " + TextUtils.bytesToString(totalSize) + ", time since modified: " + age + " min.");
             } else
             {
                 if(!flag)
                 {
                     setProperty(pExists, "missing");
                     setProperty(pPermitted, "permitted");
                     setProperty(pStateString, "directory not found");
                     setProperty(pNoData, "n/a");
                 } else
                 if(!flag2)
                 {
                     setProperty(pExists, "exists");
                     setProperty(pPermitted, "denied");
                     setProperty(pStateString, "access denied");
                     setProperty(pNoData, "n/a");
                 } else
                 {
                     setProperty(pExists, "file");
                     setProperty(pStateString, "directory was a file");
                 }
                 setProperty(pSize, 0);
                 setProperty(pFileCount, 0);
                 setProperty(pAge, "n/a");
             }
         }
     }
     return true;
 }

 private boolean countFiles(File file, boolean flag, boolean flag1, String s)
 {
     s = I18N.StringToUnicode(s, "");
     if(flag)
     {
         System.out.println("folder: " + file.getAbsolutePath());
     }
     String as[] = file.list();
     if(as == null)
     {
         return false;
     }
     boolean flag2 = s.length() == 0;
     for(int i = 0; i < as.length; i++)
     {
         File file1 = new File(file, as[i]);
         if(flag)
         {
             System.out.println(i + ": " + file1.getAbsolutePath());
         }
         if(file1.isFile())
         {
             if(!flag2 && !TextUtils.match(as[i], s))
             {
                 continue;
             }
             totalSize += file1.length();
             fileCount++;
             long l = now.getTime();
             long l1 = file1.lastModified();
             long l2 = Math.min(Math.max(0L, l1), l);
             long l3 = (l - l2) / 1000L / 60L;
             if(l3 < age)
             {
                 age = l3;
             }
             continue;
         }
         if(file1.isDirectory() && flag1 && !countFiles(file1, flag, flag1, s))
         {
             return false;
         }
     }

     return true;
 }

 public Array getLogProperties()
 {
     Array array = super.getLogProperties();
     array.add(pFileCount);
     array.add(pSize);
     array.add(pExists);
     array.add(pPermitted);
     array.add(pAge);
     return array;
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
 {
     if(stringproperty == pPath)
     {
         if(s.length() == 0)
         {
             hashmap.put(pPath, pPath.getLabel() + " missing path name");
         } else
         {
             File file = new File(s);
             String s2 = "";
             if(!file.exists())
             {
                 s2 = Platform.closeAndConnectNetBIOSIfRemoteDefined(s);
             }
             if(s2.length() > 0)
             {
                 hashmap.put(pPath, pPath.getLabel() + ": " + s2);
             } else
             if(!fileIsDirectory(pPath.verify(s, hashmap)))
             {
                 hashmap.put(stringproperty, stringproperty.getLabel() + " is not a directory");
             }
             return s;
         }
     } else
     if(stringproperty == pMatch)
     {
         String s1 = TextUtils.legalMatchString(s);
         if(s1.length() > 0)
         {
             hashmap.put(stringproperty, s1);
         }
         return s;
     }
     return super.verify(stringproperty, s, httprequest, hashmap);
 }

 public static void main(String args[])
 {
     String s = ".";
     long l = System.currentTimeMillis();
     if(args.length > 0)
     {
         s = args[0];
     }
     String s1 = "";
     if(args.length > 1)
     {
         s1 = args[1];
     }
     DirectoryMonitor directorymonitor = new DirectoryMonitor();
     directorymonitor.countFiles(new File(s), false, false, s1);
     System.out.println("files: " + directorymonitor.fileCount + ", bytes: " + directorymonitor.totalSize + ", millis: " + (System.currentTimeMillis() - l));
 }

 static 
 {
     pPath = new FileProperty("_path", "");
     pPath.fieldLabels("Directory Path", "Full path or UNC name of directory (example: c:\\firstdir\\seconddir).");
     pPath.setParameterOptions(true, 1, false);
     pNoSubdirectories = new BooleanProperty("_noRecurse", "");
     pNoSubdirectories.setDisplayText("No Subdirectories", "check this if you <B>do not</B> want SiteView to count files in subdirectories.");
     pNoSubdirectories.setParameterOptions(true, 2, true);
     pMatch = new StringProperty("_match", "");
     pMatch.setDisplayText("File Name Match", "use a substring or a <a href=/SiteView/docs/regexp.htm>regular expression</a> to match - only file names that match will be counted.");
     pMatch.setParameterOptions(true, 3, true);
     pSize = new NumericProperty("size", "0", "bytes");
     pSize.setLabel("total of file sizes");
     pSize.setStateOptions(1);
     pPermitted = new StringProperty("permitted", "denied");
     pPermitted.setLabel("access permitted");
     pPermitted.setStateOptions(2);
     pPermitted.setIsThreshold(true);
     pFileCount = new NumericProperty("fileCount", "0");
     pFileCount.setLabel("number of files");
     pFileCount.setStateOptions(3);
     pExists = new StringProperty("exists", "missing");
     pExists.setLabel("directory exists");
     pExists.setStateOptions(4);
     pAge = new NumericProperty("age", "0", "minutes");
     pAge.setLabel("time since modified");
     pAge.setStateOptions(1);
     StringProperty astringproperty[] = {
         pPath, pNoSubdirectories, pMatch, pSize, pFileCount, pPermitted, pExists, pAge
     };
     addProperties("COM.dragonflow.StandardMonitor.DirectoryMonitor", astringproperty);
     addClassElement("COM.dragonflow.StandardMonitor.DirectoryMonitor", Rule.stringToClassifier("exists == 'missing'\terror", true));
     addClassElement("COM.dragonflow.StandardMonitor.DirectoryMonitor", Rule.stringToClassifier("always\tgood", true));
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "description", "Directory file count and size monitor");
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "help", "DirMon.htm");
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "title", "Directory");
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "class", "DirectoryMonitor");
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "classType", "advanced");
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "topazName", "Windows Resources");
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "topazType", "System Resources");
     setClassProperty("COM.dragonflow.StandardMonitor.DirectoryMonitor", "target", "_path");
 }
}