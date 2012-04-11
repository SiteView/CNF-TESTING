/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WindowsMediaPlayerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WindowsMediaPlayerMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.WindowsMediaPlayerMonitorUtils;

import java.io.PrintStream;
import java.net.URLEncoder;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            MediaPlayerMonitorBase

public class WindowsMediaPlayerMonitor extends MediaPlayerMonitorBase
{

    public static final String pageURL = "/SiteView/cgi/go.exe/SiteView/?page=counter";
    protected static String dataSentinel = "LRMSS_";
    protected static String returnURL;
    protected static StringProperty pCounters;
    protected static StringProperty pMediaURL;
    protected static StringProperty pDuration;
    protected static StringProperty pStatus;
    protected static StringProperty pMediaValues[];
    protected static String templateFile;
    protected static int mediaTrace = 0;
    protected static WindowsMediaPlayerMonitorUtils wmpUtils = new WindowsMediaPlayerMonitorUtils();

    public WindowsMediaPlayerMonitor()
    {
    }

    protected boolean parseFinal(String s, String s1)
    {
        if(s1.charAt(s1.length() - 1) == '.')
        {
            s1 = s1.substring(0, s1.length() - 1);
        }
        if((mediaTrace & 4) != 0)
        {
            mediaDebug("parseFinal(): " + s + "," + s1);
        }
        float f = TextUtils.toFloat(s1);
        if(s.equals("LRMSS_PACKET_QUALITY"))
        {
            setPropertiedItem("packet quality", f);
        } else
        if(s.equals("LRMSS_TIME_QUALITY"))
        {
            setPropertiedItem("time quality", f);
        } else
        if(s.equals("LRMSS_STREAM_COUNT"))
        {
            setPropertiedItem("stream count", f);
        } else
        if(s.equals("LRMSS_STREAM_RATE"))
        {
            setPropertiedItem("stream rate", f);
        } else
        if(s.equals("LRMSS_BUFFERING_COUNT"))
        {
            setPropertiedItem("buffering count", f);
        } else
        if(s.equals("LRMSS_BUFFERING_TIME"))
        {
            setPropertiedItem("buffering time", f);
        } else
        if(s.equals("LRMSS_INTERRUPTS"))
        {
            setPropertiedItem("interrupts", f);
        } else
        if(s.equals("LRMSS_PACKETS_LOST"))
        {
            setPropertiedItem("packets lost", f);
        } else
        if(s.equals("LRMSS_PACKETS_RECOVERED"))
        {
            setPropertiedItem("packets recovered", f);
        } else
        if(s.equals("LRMSS_RATIO_BANDWIDTH"))
        {
            setPropertiedItem("ratio bandwidth", f);
        } else
        if(s.equals("LRMSS_RECM_BANDWIDTH"))
        {
            setPropertiedItem("recommended bandwidth", f);
        } else
        if(s.equals("LRMSS_RECM_DURATION"))
        {
            setPropertiedItem("recommended duration", f);
        } else
        if(s.equals("LRMSS_SAMPLING_RATE"))
        {
            setPropertiedItem("sampling rate", f);
        } else
        if(s.equals("LRMSS_STREAM_MAX"))
        {
            setPropertiedItem("stream max", f);
        } else
        if(s.equals("LRMSS_STREAM_MIN"))
        {
            setPropertiedItem("stream min", f);
        } else
        {
            LogManager.log("Error", "Unknown data point: " + s);
            return false;
        }
        return true;
    }

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=WindowsMediaPlayer&url=" + getProperty(pMediaURL) + "&duration=" + getProperty(pDuration);
        return s;
    }

    protected int getMediaTrace()
    {
        return mediaTrace;
    }

    protected String getDataSentinel()
    {
        return dataSentinel;
    }

    protected StringProperty getPDuration()
    {
        return pDuration;
    }

    protected StringProperty getPStatus()
    {
        return pStatus;
    }

    protected StringProperty getPMediaURL()
    {
        return pMediaURL;
    }

    public String getCountersContent()
    {
        return getProperty(pCounters);
    }

    public StringProperty getCountersProperty()
    {
        return pCounters;
    }

    public void setCountersContent(String s)
    {
        setProperty(pCounters, s);
    }

    public String getReturnURL()
    {
        return returnURL;
    }

    protected String getNewMediaLog()
    {
        return wmpUtils.getNewMediaLog();
    }

    protected String getMediaCommand()
    {
        mediaLog = getNewMediaLog();
        return wmpUtils.getMediaCommand(getProperty(getPMediaURL()), getProperty(getPDuration()), mediaLog);
    }

    public String getTemplateFile()
    {
        return templateFile;
    }

    protected StringProperty[] getStateValues()
    {
        return pMediaValues;
    }

    static 
    {
        pMediaValues = staticInitializer(nMaxCounters);
        templateFile = "counters.windowsmediaplayer";
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=WindowsMediaPlayerMonitor";
        pCounters = new StringProperty("_counters", getTemplateContent(getTemplatePath(), templateFile, false), "Windows Media Player Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&filename=" + templateFile + "&type=MultiContentBase\">choose counters</A>");
        pCounters.setParameterOptions(false, 1, false);
        pCounters.isMultiLine = true;
        String s = System.getProperty("WindowsMediaPlayerMonitor.debugMedia");
        if(s != null)
        {
            mediaTrace = TextUtils.toInt(s);
            System.out.println("WindowsMediaPlayerMonitor.debugMedia=" + s);
        }
        pMediaURL = new NumericProperty("_mediaURL", "", "");
        pMediaURL.setDisplayText("URL", "the URL of the object to monitor.");
        pMediaURL.setParameterOptions(true, 1, false);
        pDuration = new NumericProperty("_duration", "15", "seconds");
        pDuration.setDisplayText("Duration", "Playback duration (seconds).");
        pDuration.setParameterOptions(true, 2, false);
        pStatus = new StringProperty("status", "n/a");
        StringProperty astringproperty[] = {
            pStatus, pMediaURL, pDuration, pCounters
        };
        StringProperty astringproperty1[] = new StringProperty[astringproperty.length + pMediaValues.length];
        System.arraycopy(astringproperty, 0, astringproperty1, 0, astringproperty.length);
        System.arraycopy(pMediaValues, 0, astringproperty1, astringproperty.length, pMediaValues.length);
        addProperties("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", astringproperty1);
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "description", "Monitors Microsoft Media multimedia streams or files.");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "help", "WindowsMediaPlayerMonitor.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "title", "Windows Media Player");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "class", "WindowsMediaPlayerMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "toolName", "Windows Media Player Monitor Tool");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "toolDescription", "Windows Media Player Monitor test.");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "topazName", "WindowsMedia");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "topazType", "WindowsMedia");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "toolPageDisable", "false");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "applicationType", "MultiContentBase");
        if(!Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", "loadable", "false");
        }
        addClassElement("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", Rule.stringToClassifier("status != 'ok'\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.WindowsMediaPlayerMonitor", Rule.stringToClassifier("status == 'ok'\tgood"));
    }
}
