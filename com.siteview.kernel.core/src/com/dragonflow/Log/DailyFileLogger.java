/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.util.Date;
import java.util.Enumeration;

import com.dragonflow.SiteView.GreaterDate;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Reversing;
import com.recursionsw.jgl.algorithms.Sorting;

// Referenced classes of package com.dragonflow.Log:
// BaseFileLogger, FileWithPath, LogManager

public class DailyFileLogger extends com.dragonflow.Log.BaseFileLogger
{

    public boolean writeDate;
    static final String BUFFER_DURATION = "_logDailyFileBufferDuration";
    static final long DEFAULT_BUFFER_DURATION = 0L;
    static final String MAX_BUFFER_SIZE = "_logDailyFileMaxBufferSize";
    static final long DEFAULT_MAX_BUFFER_SIZE = 0L;
    String directoryPath;
    String suffix;
    String baseFileName;
    String basePath;
    private com.dragonflow.Log.FileWithPath currentLogFile;

    public DailyFileLogger(String s, long l, int i)
        throws java.io.IOException
    {
        this(s, l, i, 0L, 0);
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        bufferDuration = com.dragonflow.Log.DailyFileLogger.getLongSetting(hashmap, "_logDailyFileBufferDuration", 0L);
        initBuffer((int)com.dragonflow.Log.DailyFileLogger.getLongSetting(hashmap, "_logDailyFileMaxBufferSize", 0L));
    }

    public DailyFileLogger(String s, long l, int i, long l1, int j)
        throws java.io.IOException
    {
        super(l1, j);
        writeDate = true;
        directoryPath = "";
        suffix = "";
        baseFileName = "";
        basePath = "";
        currentLogFile = new FileWithPath();
        java.io.File file = new File(s);
        baseFileName = file.getName();
        basePath = s;
        directoryPath = file.getParent();
        int k = baseFileName.lastIndexOf(".");
        if(k >= 0)
        {
            suffix = baseFileName.substring(k);
            baseFileName = baseFileName.substring(0, k);
        }
        basePath = directoryPath + java.io.File.separator + baseFileName;
        java.io.File file1 = new File(directoryPath);
        String as[] = file1.list();
        Array array = new Array();
        for(int i1 = 0; i1 < as.length; i1++)
        {
            if(!as[i1].startsWith(baseFileName) || !as[i1].endsWith(suffix))
            {
                continue;
            }
            java.util.Date date2 = null;
            if(as[i1].equals(baseFileName + suffix))
            {
                date2 = new Date(0L);
            } else
            {
                date2 = com.dragonflow.Utils.TextUtils.fileNameToDay(as[i1].substring(baseFileName.length()));
            }
            array.add(date2);
        }

        Sorting.sort(array, new GreaterDate());
        Reversing.reverse(array);
        if(i > 0)
        {
            do
            {
                if(array.size() <= i + 1 || array.size() <= 2)
                {
                    break;
                }
                java.util.Date date = (java.util.Date)array.popFront();
                if(date.getTime() == 0L)
                {
                    java.io.File file2 = new File(basePath + suffix);
                    java.io.File file6 = new File(basePath + suffix + ".old");
                    boolean flag2 = file2.renameTo(file6);
                    if(!flag2)
                    {
                        com.dragonflow.Log.LogManager.log("Error", "Could not rename old log file: " + file2.getAbsolutePath());
                    }
                } else
                {
                    java.io.File file3 = new File(basePath + com.dragonflow.Utils.TextUtils.dayToFileName(date) + suffix);
                    boolean flag = file3.delete();
                    if(!flag)
                    {
                        com.dragonflow.Log.LogManager.log("Error", "Could not remove old log file: " + file3.getAbsolutePath());
                    }
                }
            } while(true);
        }
        if(l > 0L)
        {
            do
            {
                if(totalFileSizes(array) <= l || array.size() <= 2)
                {
                    break;
                }
                java.util.Date date1 = (java.util.Date)array.popFront();
                if(date1.getTime() == 0L)
                {
                    java.io.File file4 = new File(basePath + suffix);
                    java.io.File file7 = new File(basePath + suffix + ".old");
                    boolean flag3 = file4.renameTo(file7);
                    if(!flag3)
                    {
                        com.dragonflow.Log.LogManager.log("Error", "Could not rename old log file: " + file4.getAbsolutePath());
                    }
                } else
                {
                    java.io.File file5 = new File(basePath + com.dragonflow.Utils.TextUtils.dayToFileName(date1) + suffix);
                    boolean flag1 = file5.delete();
                    if(!flag1)
                    {
                        com.dragonflow.Log.LogManager.log("Error", "Could not remove old log file: " + file5.getAbsolutePath());
                    }
                }
            } while(true);
        }
    }

    long totalFileSizes(Array array)
    {
        Enumeration enumeration = (Enumeration) array.iterator();
        long l;
        java.io.File file;
        for(l = 0L; enumeration.hasMoreElements(); l += file.length())
        {
            java.util.Date date = (java.util.Date)enumeration.nextElement();
            file = null;
            if(date.getTime() == 0L)
            {
                file = new File(basePath + suffix);
            } else
            {
                file = new File(basePath + com.dragonflow.Utils.TextUtils.dayToFileName(date) + suffix);
            }
        }

        return l;
    }

    public void flush(java.lang.Object obj)
    {
        String s = ((StringBuffer)obj).toString();
        if(s.length() <= 0)
        {
            return;
        }
        java.util.Date date = new Date();
        String s1 = basePath + com.dragonflow.Utils.TextUtils.dayToFileName(date) + suffix;
        Object obj1 = null;
        try
        {
            java.io.OutputStreamWriter outputstreamwriter = currentLogFile.getFile(s1, true);
            outputstreamwriter.write(s, 0, s.length());
            outputstreamwriter.flush();
        }
        catch(java.io.IOException ioexception)
        {
            java.lang.System.err.println("Could not open log file " + s1);
        }
    }

    public void log(String s, java.util.Date date, String s1)
    {
        StringBuffer stringbuffer = new StringBuffer();
        if(writeDate)
        {
            stringbuffer.append(com.dragonflow.Log.DailyFileLogger.dateToString(date)).append(FIELD_SEPARATOR);
        }
        stringbuffer.append(s1).append(com.dragonflow.SiteView.Platform.FILE_NEWLINE);
        addToFileBuffer(stringbuffer);
    }

    public static void main(String args[])
    {
        int i = 0x186a0;
        byte byte0 = 10;
        boolean flag = false;
        for(int l = 0; l < args.length; l++)
        {
            if(args[l].equals("-s"))
            {
                int j = com.dragonflow.Utils.TextUtils.toInt(args[++l]);
                continue;
            }
            if(args[l].equals("-d"))
            {
                int k = com.dragonflow.Utils.TextUtils.toInt(args[++l]);
                continue;
            }
            boolean flag1;
            if(args[l].equals("-a"))
            {
                flag1 = true;
            }
        }

    }
}
