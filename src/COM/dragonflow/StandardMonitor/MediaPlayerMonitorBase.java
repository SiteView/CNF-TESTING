/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * MediaPlayerMonitorBase.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>MediaPlayerMonitorBase</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.MultiContentBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.TextUtils;

public abstract class MediaPlayerMonitorBase extends MultiContentBase
{

    protected static final int MEDIA_TRACE_NONE = 0;
    protected static final int MEDIA_TRACE_MIN = 1;
    protected static final int MEDIA_TRACE_TEMP = 2;
    protected static final int MEDIA_TRACE_PARSE = 4;
    protected static final int MEDIA_TRACE_ERROR = 8;
    protected static final int MEDIA_TRACE_COSTS = 16;
    protected static final int MEDIA_TRACE_ALL = 15;
    protected static final String applicationDir = "templates.applications";
    protected static final String path;
    protected String mediaLog;

    public MediaPlayerMonitorBase()
    {
        mediaLog = null;
    }

    protected abstract boolean parseFinal(String s, String s1);

    protected abstract int getMediaTrace();

    protected abstract String getDataSentinel();

    protected abstract StringProperty getPDuration();

    protected abstract StringProperty getPStatus();

    protected abstract StringProperty getPMediaURL();

    protected abstract String getNewMediaLog();

    protected abstract String getMediaCommand();

    public abstract String getCountersContent();

    protected StringProperty getStateValue(int i)
    {
        return getStateValues()[i];
    }

    protected abstract StringProperty[] getStateValues();

    public String getHostname()
    {
        return HTTPUtils.hostFromURL(getProperty(getPMediaURL()));
    }

    protected void mediaDebug(String s)
    {
        LogManager.log("RunMonitor", "MEDIA TRACE: " + s);
    }

    protected void setPropertiedItem(String s, float f)
    {
        if((getMediaTrace() & 4) != 0)
        {
            mediaDebug("setPropertiedItem(): " + s + Float.toString(f));
        }
        Array array = getCounters();
        int i = 0;
        do
        {
            if(i >= array.size())
            {
                break;
            }
            if((getMediaTrace() & 4) != 0)
            {
                mediaDebug("SetPropertiedItem(): >" + array.at(i) + "<,>" + s + "<");
            }
            if(array.at(i).equals(s))
            {
                if((getMediaTrace() & 4) != 0)
                {
                    mediaDebug("SetPropertiedItem(): MATCH Setting to " + Float.toString(f));
                }
                setProperty(getStateValues()[i], f);
                break;
            }
            i++;
        } while(true);
    }

    private boolean parseRecord(String s)
    {
        boolean flag = false;
        int i = s.indexOf(getDataSentinel());
        if(i >= 0)
        {
            String as[] = TextUtils.split(s);
            if(as.length == 2)
            {
                flag = parseFinal(as[0], as[1]);
            }
        }
        if((getMediaTrace() & 4) != 0)
        {
            mediaDebug("parse: " + s);
        }
        return flag;
    }

    private String prepLabel(String s)
    {
        String s1 = "";
        for(int i = 0; i < s.length(); i++)
        {
            if(s.charAt(i) == ' ')
            {
                s1 = s1 + "&nbsp;";
            } else
            {
                s1 = s1 + s.charAt(i);
            }
        }

        return s1;
    }

    protected String handleUpdateError(String s)
    {
        return s;
    }

    protected void setError(String s)
    {
        setProperty(pStateString, s);
        setProperty(getPStatus(), "Error");
        setProperty(pNoData, "n/a");
    }

    protected boolean update()
    {
        if(Platform.isWindows())
        {
            for(int i = 0; i < getStateValues().length; i++)
            {
                unsetProperty(getStateValues()[i]);
            }

            if((getMediaTrace() & 4) != 0)
            {
                mediaDebug("MediaPlayerMonitorBase(): Entered");
            }
            Array array = new Array();
            String s = getMediaCommand();
            if((getMediaTrace() & 4) != 0)
            {
                mediaDebug("getMediaCommand(): " + s);
            }
            CommandLine commandline = new CommandLine();
            array = commandline.exec(s);
            int j = commandline.getExitValue();
            if((getMediaTrace() & 4) != 0)
            {
                mediaDebug("mediaCommand: " + s + ",getExitValue(): " + j);
            }
            for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); mediaDebug("Results: " + enumeration.nextElement())) { }
            boolean flag = false;
            if(j == 0)
            {
                try
                {
                    BufferedReader bufferedreader = new BufferedReader(new FileReader(mediaLog));
                    do
                    {
                        String s1;
                        if((s1 = bufferedreader.readLine()) == null)
                        {
                            break;
                        }
                        if(parseRecord(s1))
                        {
                            flag = true;
                        }
                    } while(true);
                    if(flag)
                    {
                        String s3 = "URL: " + getProperty(getPMediaURL());
                        Array array1 = getCounters();
                        for(int l = 0; l < array1.size(); l++)
                        {
                            if(l == 0)
                            {
                                s3 = s3 + "&nbsp";
                            }
                            String s4 = prepLabel((String)array1.at(l));
                            s3 = s3 + s4 + "&nbsp;" + getProperty(getStateValues()[l]);
                            if(l < array1.size() - 1)
                            {
                                s3 = s3 + ", ";
                            }
                        }

                        setProperty(pStateString, s3);
                        setProperty(getPStatus(), "ok");
                    }
                }
                catch(FileNotFoundException filenotfoundexception)
                {
                    LogManager.log("Error", "Error Opening: " + mediaLog + " " + filenotfoundexception);
                    setProperty(pStateString, "Error opening media log file: " + mediaLog);
                    setProperty(getPStatus(), "Error");
                }
                catch(IOException ioexception)
                {
                    LogManager.log("Error", "Error reading: " + mediaLog + " " + ioexception);
                    setProperty(pStateString, "Error reading media log file: " + mediaLog);
                    setProperty(getPStatus(), "Error");
                }
            }
            if(!flag)
            {
                String s2 = "";
                StringBuffer stringbuffer = new StringBuffer();
                String s5 = "";
                System.out.println("Error in monitor !");
                try
                {
                    BufferedReader bufferedreader1 = new BufferedReader(new FileReader(mediaLog + "_"));
                    do
                    {
                        String s6;
                        if((s6 = bufferedreader1.readLine()) == null)
                        {
                            break;
                        }
                        stringbuffer.append(s6);
                        int k = s6.indexOf("Error:");
                        if(k >= 0)
                        {
                            if(s2.length() > 0)
                            {
                                s2 = s2 + "<br>";
                            }
                            s2 = s2 + s6.substring(k + 6).trim();
                        }
                    } while(true);
                }
                catch(FileNotFoundException filenotfoundexception1)
                {
                    s2 = "File Not Found Exception occurrred while reading error log file.";
                }
                catch(IOException ioexception1)
                {
                    s2 = "I/O Exception occurred while reading error log file.";
                }
                if(s2.length() > 0)
                {
                    setError(handleUpdateError(s2));
                } else
                {
                    setError(stringbuffer.toString());
                }
            }
            File file = new File(mediaLog);
            if(file != null)
            {
                if((getMediaTrace() & 2) != 0)
                {
                    mediaDebug("Delete Temp File: " + mediaLog);
                }
                try
                {
                    file.delete();
                }
                catch(SecurityException securityexception)
                {
                    LogManager.log("Error", "MediaPlayerMonitorBase Security Exception caught deleting temp file: " + mediaLog);
                }
            } else
            {
                LogManager.log("Error", "MediaPlayerMonitorBase Error deleting temp file: " + mediaLog);
            }
            file = new File(mediaLog + "_");
            if(file != null)
            {
                if((getMediaTrace() & 2) != 0)
                {
                    mediaDebug("Delete Temp File: " + mediaLog + "_");
                }
                try
                {
                    file.delete();
                }
                catch(SecurityException securityexception1)
                {
                    LogManager.log("Error", "MediaPlayerMonitorBase Security Exception caught deleting temp file: " + mediaLog);
                }
            } else
            {
                LogManager.log("Error", "MediaPlayerMonitorBase Error deleting temp file: " + mediaLog + "_");
            }
            if((getMediaTrace() & 4) != 0)
            {
                mediaDebug("MediaPlayerMonitorBase(): Exited");
            }
            return true;
        } else
        {
            return false;
        }
    }

    public int getCostInLicensePoints()
    {
        int i = getActiveCounters();
        if((getMediaTrace() & 0x10) != 0)
        {
            mediaDebug("This monitor costs " + i + " points.");
        }
        return i;
    }

    public static String getTemplatePath()
    {
        return path;
    }

    public int getActiveCounters()
    {
        Array array = getCounters();
        if(array == null)
        {
            return 0;
        }
        int i = array.size();
        if(i > nMaxCounters)
        {
            i = nMaxCounters;
        }
        return i;
    }

    synchronized Array getCounters()
    {
        Array array = new Array();
        String s = getCountersContent();
        String as[] = TextUtils.split(s, ",");
        if(as.length > 0)
        {
            for(int i = 0; i < as.length; i++)
            {
                array.add(as[i].trim());
            }

        }
        if((getMediaTrace() & 0x10) != 0)
        {
            mediaDebug("Active counters: " + s);
        }
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == getPDuration())
        {
            long l = Long.parseLong(s);
            if(s.trim().length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            if(l <= 0L)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " duration must be positive value (milleseconds).");
            }
            return s;
        }
        if(stringproperty == getCountersProperty())
        {
            String s1 = getCountersContent();
            if(s1.length() <= 0)
            {
                hashmap.put(stringproperty, "No counters selected");
            }
            return s;
        }
        if(stringproperty == getPMediaURL())
        {
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public static void main(String args[])
    {
    }

    static 
    {
        path = Platform.getRoot() + File.separator + "templates.applications" + File.separator;
        addProperties((COM.dragonflow.StandardMonitor.MediaPlayerMonitorBase.class).getName(), new StringProperty[0]);
    }
}
