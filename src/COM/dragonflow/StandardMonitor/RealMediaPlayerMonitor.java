/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * RealMediaPlayerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>RealMediaPlayerMonitor</code>
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
import COM.dragonflow.Utils.RealPlayerMonitorUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.PrintStream;
import java.net.URLEncoder;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            MediaPlayerMonitorBase

public class RealMediaPlayerMonitor extends MediaPlayerMonitorBase
{

    public static final String pageURL = "/SiteView/cgi/go.exe/SiteView/?page=counter";
    protected static String dataSentinel = "LREAL_";
    protected static String returnURL;
    protected static StringProperty pCounters;
    protected static StringProperty pMediaURL;
    protected static StringProperty pDuration;
    protected static StringProperty pStatus;
    protected static StringProperty pMediaValues[];
    protected static String templateFile;
    protected static int mediaTrace = 0;
    protected static RealPlayerMonitorUtils realUtils = new RealPlayerMonitorUtils();

    public RealMediaPlayerMonitor()
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
        if(s.equals("LREAL_STREAM_QUALITY"))
        {
            setPropertiedItem("stream quality", f);
        } else
        if(s.equals("LREAL_LIVE_PAUSE_NUM"))
        {
            setPropertiedItem("live pause num", f);
        } else
        if(s.equals("LREAL_LIVE_PAUSE_TIME"))
        {
            setPropertiedItem("live pause time", f);
        } else
        if(s.equals("LREAL_BUFFERING_CONGESTION_NUM"))
        {
            setPropertiedItem("buffering congestion num", f);
        } else
        if(s.equals("LREAL_BUFFERING_CONGESTION_TIME"))
        {
            setPropertiedItem("buffering congestion time", f);
        } else
        if(s.equals("LREAL_BUFFERING_SEEK_TIME"))
        {
            setPropertiedItem("buffering seek time", f);
        } else
        if(s.equals("LREAL_BUFFERING_SEEK_NUM"))
        {
            setPropertiedItem("buffering seek num", f);
        } else
        if(s.equals("LREAL_BUFFERING_TIME"))
        {
            setPropertiedItem("buffering time", f);
        } else
        if(s.equals("LREAL_BUFFERING_NUM"))
        {
            setPropertiedItem("buffering num", f);
        } else
        if(s.equals("LREAL_FIRST_FRAME_TIME"))
        {
            setPropertiedItem("first frame time", f);
        } else
        if(s.equals("LREAL_NETWORK_PERFORMANCE"))
        {
            setPropertiedItem("network performance", f);
        } else
        if(s.equals("LREAL_CURRENT_BANDWIDTH"))
        {
            setPropertiedItem("bandwidth", f);
        } else
        if(s.equals("LREAL_LATE_PACKETS"))
        {
            setPropertiedItem("late packets", f);
        } else
        if(s.equals("LREAL_LOST_PACKETS"))
        {
            setPropertiedItem("lost packets", f);
        } else
        if(s.equals("LREAL_RECOVERED_PACKETS"))
        {
            setPropertiedItem("recovered packets", f);
        } else
        {
            LogManager.log("Error", "Unknown data point: " + s);
            return false;
        }
        return true;
    }

    protected String handleUpdateError(String s)
    {
        if(s.indexOf("Error -19800") > -1)
        {
            return "Make sure that Real Player is installed.\n" + s;
        } else
        {
            return s;
        }
    }

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=RealMediaPlayer&url=" + getProperty(pMediaURL) + "&duration=" + getProperty(pDuration);
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
        return realUtils.getNewMediaLog();
    }

    protected String getMediaCommand()
    {
        mediaLog = getNewMediaLog();
        return realUtils.getMediaCommand(getProperty(getPMediaURL()), getProperty(getPDuration()), mediaLog);
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
        templateFile = "counters.RealMediaPlayer";
        returnURL = "/SiteView/cgi/go.exe/SiteView/?page=monitor&class=RealMediaPlayerMonitor";
        pCounters = new StringProperty("_counters", getTemplateContent(getTemplatePath(), templateFile, false), "Real Media Player Selected Counters");
        pCounters.setDisplayText("Counters", "the current selection of counters</TD></TR><TR><TD></TD><TD><A HREF=\"/SiteView/cgi/go.exe/SiteView/?page=counter&returnURL=" + URLEncoder.encode(returnURL) + "&maxcounters=" + nMaxCounters + "&filename=" + templateFile + "&type=MultiContentBase\">choose counters</A>");
        pCounters.setParameterOptions(false, 1, false);
        pCounters.isMultiLine = true;
        String s = System.getProperty("RealMediaPlayerMonitor.debugMedia");
        if(s != null)
        {
            mediaTrace = TextUtils.toInt(s);
            System.out.println("RealMediaPlayerMonitor.debugMedia=" + s);
        }
        pMediaURL = new NumericProperty("_mediaURL", "", "");
        pMediaURL.setDisplayText("URL", "the URL of the object to monitor.");
        pMediaURL.setParameterOptions(true, 1, false);
        pDuration = new NumericProperty("_duration", "15000", "milleseconds");
        pDuration.setDisplayText("Duration", "Playback duration (milleseconds).");
        pDuration.setParameterOptions(true, 2, false);
        pStatus = new StringProperty("status", "n/a");
        StringProperty astringproperty[] = {
            pStatus, pMediaURL, pDuration, pCounters
        };
        StringProperty astringproperty1[] = new StringProperty[astringproperty.length + pMediaValues.length];
        System.arraycopy(astringproperty, 0, astringproperty1, 0, astringproperty.length);
        System.arraycopy(pMediaValues, 0, astringproperty1, astringproperty.length, pMediaValues.length);
        addProperties("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", astringproperty1);
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "description", "Monitor Real Networks media streams or files.");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "help", "RealMediaPlayerMonitor.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "title", "Real Media Player");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "class", "RealMediaPlayerMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "toolName", "Real Media Player Monitor Tool");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "toolDescription", "Real Media Player Monitor test.");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "topazName", "RealMediaPlayerMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "topazType", "RealMedia");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "toolPageDisable", "false");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "classType", "application");
        setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "applicationType", "MultiContentBase");
        if(!Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", "loadable", "false");
        }
        addClassElement("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", Rule.stringToClassifier("status != 'ok'\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.RealMediaPlayerMonitor", Rule.stringToClassifier("status == 'ok'\tgood"));
    }
}
