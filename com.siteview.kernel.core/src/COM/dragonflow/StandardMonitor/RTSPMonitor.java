/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * RTSPMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>RTSPMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Vector;

import javax.media.CachingControl;
import javax.media.CachingControlEvent;
import javax.media.ConnectionErrorEvent;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataLostErrorEvent;
import javax.media.DataStarvedEvent;
import javax.media.DeallocateEvent;
import javax.media.EndOfMediaEvent;
import javax.media.GainControl;
import javax.media.InternalErrorEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.PackageManager;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.RestartingEvent;
import javax.media.StartEvent;
import javax.media.StopAtTimeEvent;
import javax.media.StopByRequestEvent;
import javax.media.StopEvent;
import javax.media.Time;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.TextUtils;

import com.real.media.RMOnPosChangedEvent;

public class RTSPMonitor extends AtomicMonitor implements ControllerListener {

    public static int media_trace = 0;

    public boolean testRun;

    public PrintWriter testStream;

    public String testUrl;

    public static int DEFAULT_TIMEOUT = 0x493e0;

    public static int DEFAULT_STOP_TIMEOUT = 30000;

    public long lastPercentComplete;

    static StringProperty pMediaUrlParameter;

    static StringProperty pTimeout;

    static StringProperty pStopTime;

    static StringProperty pStatus;

    static StringProperty pDownloadTime;

    static StringProperty pRate;

    static StringProperty pPlaybackRate;

    static StringProperty pStartLatency;

    static StringProperty pDownloadBytes;

    static StringProperty pMaxMeasurement;

    static StringProperty pPlayDuration;

    Player player;

    int player_errorCount;

    boolean player_error;

    boolean playerRealized;

    boolean player_done;

    boolean player_stop;

    String playerEventReason;

    String playerLastPercent;

    public RTSPMonitor() {
        testRun = false;
        testStream = null;
        testUrl = null;
        lastPercentComplete = 0L;
        player = null;
        player_errorCount = 0;
        player_error = false;
        playerRealized = false;
        player_done = false;
        player_stop = false;
        playerEventReason = "Uninitialized";
        playerLastPercent = "0%";
    }

    public String getHostname() {
        return HTTPUtils.hostFromURL(getProperty(pMediaUrlParameter));
    }

    void MediaDebugList() {
        Vector vector = PackageManager.getContentPrefixList();
        Enumeration enumeration = vector.elements();
        MediaDebug("Content Prefix List - ");
        String s;
        for (; enumeration.hasMoreElements(); MediaDebug("\t\t:" + s)) {
            s = (String) enumeration.nextElement();
        }

        vector = PackageManager.getProtocolPrefixList();
        enumeration = vector.elements();
        MediaDebug("Protocol Prefix List - ");
        String s1;
        for (; enumeration.hasMoreElements(); MediaDebug("\t\t:" + s1)) {
            s1 = (String) enumeration.nextElement();
        }

        vector = Manager.getDataSourceList("");
        enumeration = vector.elements();
        MediaDebug("Sources - ");
        String s2;
        for (; enumeration.hasMoreElements(); MediaDebug("\t\t:" + s2)) {
            s2 = (String) enumeration.nextElement();
        }

        vector = Manager.getHandlerClassList("");
        enumeration = vector.elements();
        MediaDebug("Handlers - ");
        String s3;
        for (; enumeration.hasMoreElements(); MediaDebug("\t\t:" + s3)) {
            s3 = (String) enumeration.nextElement();
        }

        vector = null;
        enumeration = null;
    }

    private void MediaDebug(String s) {
        if (testRun && testStream != null) {
            testStream.println(s);
            testStream.flush();
        } else {
            TextUtils.debugPrint("RTSPMonitor: " + s);
        }
    }

    public synchronized void controllerUpdate(ControllerEvent controllerevent) {
        if (controllerevent instanceof StartEvent) {
            playerEventReason = "Start";
        } else if (controllerevent instanceof RealizeCompleteEvent) {
            playerEventReason = "Controller Realized";
            playerRealized = true;
        } else if (controllerevent instanceof PrefetchCompleteEvent) {
            playerEventReason = "Prefetch complete";
            playerRealized = true;
        } else if (controllerevent instanceof RestartingEvent) {
            playerEventReason = "Restarting...";
        } else if (controllerevent instanceof DataStarvedEvent) {
            playerEventReason = "Data Starved";
            player_errorCount ++;
            player_done = true;
            player_error = true;
        } else if (controllerevent instanceof DataLostErrorEvent) {
            playerEventReason = "Data Lost";
            player_errorCount ++;
            player_done = true;
            player_error = true;
        } else if (controllerevent instanceof ResourceUnavailableEvent) {
            playerEventReason = "Resource Unavailable";
            player_done = true;
            player_error = true;
        } else if (controllerevent instanceof EndOfMediaEvent) {
            playerEventReason = "End of Media";
            playerLastPercent = "100%";
            player_done = true;
        } else if (controllerevent instanceof StopAtTimeEvent) {
            playerEventReason = "Stop At Time Event";
            player_done = true;
        } else if (controllerevent instanceof StopByRequestEvent) {
            playerEventReason = "Stop Requested";
            player_done = true;
        } else if (controllerevent instanceof ControllerErrorEvent) {
            playerEventReason = "Controller Error";
            player_done = true;
            player_error = true;
        } else if (controllerevent instanceof ConnectionErrorEvent) {
            playerEventReason = "Connection Error";
            player_done = true;
            player_error = true;
        } else if (controllerevent instanceof InternalErrorEvent) {
            playerEventReason = "Internal Error";
            player_done = true;
            player_error = true;
        } else if (controllerevent instanceof DeallocateEvent) {
            playerEventReason = "Deallocate";
            player_stop = true;
        } else if (controllerevent instanceof StopEvent) {
            playerEventReason = "Stop Event";
            player_stop = true;
        } else if (controllerevent instanceof ControllerClosedEvent) {
            playerEventReason = "Controller Closing";
            player = null;
        } else if (controllerevent instanceof CachingControlEvent) {
            if (testRun || media_trace == 1) {
                long l = ((CachingControlEvent) controllerevent).getContentProgress();
                if (l > 0L) {
                    CachingControl cachingcontrol = ((CachingControlEvent) controllerevent).getCachingControl();
                    long l1 = cachingcontrol.getContentLength();
                    long l2 = (100L * l) / l1;
                    System.out.println(l2 + " , " + lastPercentComplete);
                    if (l2 - lastPercentComplete > 10L) {
                        byte byte0 = 2;
                        String s = Long.toString(l2);
                        if (s.length() < 2) {
                            byte0 = 1;
                        }
                        s = s.substring(0, byte0);
                        playerLastPercent = s + "%";
                        MediaDebug("Completed: " + playerLastPercent);
                        lastPercentComplete = l2;
                    }
                }
                return;
            }
        } else if (!(controllerevent instanceof RMOnPosChangedEvent)) {
            playerEventReason = "Unhandled Event" + controllerevent;
        }
        if (testRun || media_trace == 1) {
            MediaDebug("Event: " + playerEventReason);
        }
    }

    protected void createPlayer(String s) {
        if (testRun && testStream != null) {
            testStream.println("Creating a virtual media player.");
            testStream.println("URL: " + s);
        }
        Object obj = null;
        long l = Platform.timeMillis() + (long) DEFAULT_STOP_TIMEOUT;
        try {
            playerRealized = false;
            playerEventReason = "Media Locator Error";
            MediaLocator medialocator = new MediaLocator(s);
            if (medialocator != null) {
                try {
                    playerEventReason = "Player Create Error";
                    player = Manager.createPlayer(medialocator);
                    if (player != null) {
                        if (testRun || media_trace == 1) {
                            MediaDebug("Player was created for: " + medialocator.toString());
                        }
                        player.addControllerListener(this);
                        playerEventReason = "Player Starting";
                        player.start();
                        for (; !player_done && !playerRealized && Platform.timeMillis() < l; Platform.sleep(500L)) {
                        }
                    }
                } catch (NoPlayerException noplayerexception) {
                    if (testRun || media_trace == 1) {
                        MediaDebug("No Player Exception: " + noplayerexception);
                    }
                } catch (MalformedURLException malformedurlexception) {
                    if (testRun || media_trace == 1) {
                        MediaDebug("Invalid media file URL: " + s);
                    }
                    setProperty(pStateString, "Malformed URL Expression");
                } catch (IOException ioexception) {
                    if (testRun || media_trace == 1) {
                        MediaDebug("IO Exception encountered opening " + medialocator);
                    }
                    setProperty(pStateString, "A Java I/O Exception has occurred");
                } catch (Exception exception) {
                    if (testRun || media_trace == 1) {
                        MediaDebug("Exception: " + exception);
                    }
                }
            }
        } catch (Exception exception1) {
            if (testRun || media_trace == 1) {
                MediaDebug("Unknown exception: " + exception1);
            }
        }
    }

    protected void destroyPlayer() {
        long l = Platform.timeMillis() + (long) DEFAULT_STOP_TIMEOUT;
        if (player == null) {
            return;
        }
        try {
            player_stop = false;
            player.stop();
            player.deallocate();
            player.close();
            for (; !player_stop && Platform.timeMillis() < l; Platform.sleep(500L)) {
            }
        } catch (Exception exception) {
        }
    }

    protected void monitorFailed(int i, String s) {
        if (!testRun) {
            setProperty(pStateString, s);
            setProperty(pStatus, -1);
            setProperty(pRate, 0);
            setProperty(pStartLatency, 0);
            setProperty(pDownloadTime, 0);
            setProperty(pDownloadBytes, 0);
            setProperty(pPlayDuration, 0);
            setProperty(pNoData, "n/a");
        }
    }

    protected void debugPlayerState() {
        String s;
        if (player.getState() == 100) {
            s = "Unrealized";
        } else if (player.getState() == 200) {
            s = "Realizing";
        } else if (player.getState() == 400) {
            s = "Prefetching";
        } else if (player.getState() == 500) {
            s = "Prefetched";
        } else if (player.getState() == 600) {
            s = "Started";
        } else {
            s = "Something Else";
        }
        if (testRun || media_trace == 1) {
            MediaDebug("Player State: " + player + " state: " + s);
        }
    }

    protected boolean update() {
        Object obj = null;
        long l = StringProperty.toLong(getProperty(pTimeout));
        long l1 = 0L;
        long l3 = 0L;
        long l4 = getPropertyAsLong(pMaxMeasurement);
        float f = 0.0F;
        int i = StringProperty.toInteger(getProperty(pStopTime)) * 1000;
        boolean flag = false;
        player_done = false;
        player_error = false;
        if (l == 0L) {
            l = DEFAULT_TIMEOUT;
        } else {
            l *= 1000L;
        }
        try {
            String s;
            if (testRun) {
                s = testUrl;
            } else {
                s = getProperty(pMediaUrlParameter);
            }
            long l5 = Platform.timeMillis();
            l += Platform.timeMillis();
            player = null;
            createPlayer(s);
            if (player != null) {
                do {
                    if (player_done || playerRealized || flag) {
                        break;
                    }
                    if (Platform.timeMillis() > l) {
                        if (testRun || media_trace == 1) {
                            MediaDebug("Timed out waiting to realize");
                        }
                        flag = true;
                    }
                } while (true);
                if (playerRealized || player_done) {
                    if (playerRealized) {
                        if (testRun || media_trace == 1) {
                            MediaDebug("Virtual Player Realized");
                        }
                        if (i != 0) {
                            Time time = new Time(i);
                            player.setStopTime(time);
                        }
                        GainControl gaincontrol = player.getGainControl();
                        if (testRun || media_trace != 1) {
                            gaincontrol.setMute(true);
                        }
                    }
                    while (!player_done && !flag) {
                        if (Platform.timeMillis() > l) {
                            flag = true;
                        } else {
                            Platform.sleep(500L);
                        }
                    }
                    if (!flag) {
                        long l2 = Platform.timeMillis() - l5;
                        float f2;
                        double d;
                        double d1;
                        if (playerRealized) {
                            if (testRun || media_trace == 1) {
                                MediaDebug("Virtual Playback Completed.");
                            }
                            javax.media.Control acontrol[] = player.getControls();
                            for (int j = 0; j < acontrol.length; j ++) {
                                if (acontrol[j] instanceof CachingControl) {
                                    CachingControl cachingcontrol = (CachingControl) acontrol[j];
                                    l3 = cachingcontrol.getContentLength();
                                }
                            }

                            f2 = player.getRate();
                            d = player.getDuration().getSeconds();
                            Time time1 = player.getStartLatency();
                            if (time1 == Controller.LATENCY_UNKNOWN) {
                                time1 = new Time(0L);
                            }
                            d1 = time1.getSeconds();
                        } else {
                            d = 0.0D;
                            Time time2 = new Time(0L);
                            d1 = 0.0D;
                            l3 = 0L;
                            f2 = 0.0F;
                        }
                        String s1;
                        if (l3 > 0L && l2 > 0L) {
                            float f1;
                            if (l2 > 0L) {
                                f1 = (float) l3 / (float) (l2 / 1000L);
                            } else {
                                f1 = l3;
                            }
                            s1 = TextUtils.floatToString(f1, 2);
                        } else {
                            s1 = "0.00";
                        }
                        if (player_error) {
                            if (testRun || media_trace == 1) {
                                MediaDebug("Player Error: " + playerEventReason);
                            }
                            monitorFailed(kURLUnknownError, playerEventReason);
                        } else if (!testRun) {
                            setProperty(pStatus, kURLok);
                            setProperty(pRate, s1);
                            setProperty(pDownloadTime, l2);
                            setProperty(pStartLatency, (float) d1);
                            setProperty(pDownloadBytes, l3);
                            setProperty(pPlaybackRate, TextUtils.floatToString(f2, 2));
                            setProperty(pPlayDuration, TextUtils.floatToString((float) d, 2));
                            if (playerRealized) {
                                setProperty(pStateString, "Duration: " + TextUtils.floatToString((float) l2 / 1000F, 2) + " secs, " + "Bytes Streamed: " + l3 + ", " + "Rate: " + s1 + " bps, " + "Play Duration: " + (float) d + " secs");
                            } else {
                                setProperty(pStateString, TextUtils.floatToString((float) l2 / 1000F, 2) + " sec");
                            }
                        } else {
                            MediaDebug("Rate: " + s1);
                            MediaDebug("Download Time: " + l2);
                            MediaDebug("Start Latency: " + d1);
                            MediaDebug("Bytes Downloaded: " + l3);
                            MediaDebug("Playback Rate: " + f2);
                            MediaDebug("Duration: " + d);
                        }
                        if (!testRun) {
                            setProperty(pMeasurement, getMeasurement(pDownloadTime, l4));
                        }
                    } else {
                        if (testRun || media_trace == 1) {
                            MediaDebug("Player Failed to realize.");
                        }
                        monitorFailed(kURLTimeoutError, "Player timed out");
                    }
                } else if (player_done) {
                    if (testRun || media_trace == 1) {
                        MediaDebug("Player Failed to realize.");
                    }
                    monitorFailed(kMonitorSpecificError, playerEventReason);
                } else if (flag) {
                    if (testRun || media_trace == 1) {
                        MediaDebug("Player timed out waiting to realize.");
                    }
                    monitorFailed(kURLTimeoutError, "Timed out waiting to realize");
                }
            } else {
                if (testRun || media_trace == 1) {
                    MediaDebug("No Player.");
                }
                monitorFailed(kURLContentErrorFound, playerEventReason);
            }
            destroyPlayer();
        } catch (Exception exception) {
            destroyPlayer();
        }
        if (media_trace == 1) {
            MediaDebugList();
        }
        return true;
    }

    public String getTestURL() {
        String s = "/SiteView/cgi/go.exe/SiteView?page=RTSP&url=" + getProperty(pMediaUrlParameter);
        return s;
    }

    public int testUpdate(PrintWriter printwriter, String s) {
        System.out.println("Test Media Pass: " + s + " Stream: " + printwriter);
        testStream = printwriter;
        testRun = true;
        testUrl = s;
        update();
        return 0;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pDownloadTime);
        array.add(pDownloadBytes);
        array.add(pRate);
        array.add(pPlaybackRate);
        array.add(pStartLatency);
        array.add(pPlayDuration);
        return array;
    }

    public String defaultTitle() {
        return "RTSP:" + getProperty(pMediaUrlParameter);
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pMediaUrlParameter) {
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public static void main(String args[]) {
    }

    static {
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, seconds, to wait for the stream to complete.");
        pTimeout.setParameterOptions(true, 1, true);
        pStopTime = new NumericProperty("_stoptime", "0", "seconds");
        pStopTime.setDisplayText("Stop Time", "Stop download after a number of seconds has elapsed.");
        pStopTime.setParameterOptions(true, 2, true);
        pMediaUrlParameter = new StringProperty("_MediaUrlParameter", "");
        pMediaUrlParameter.setDisplayText("Media Url", "The URL of the sreaming media file (.wav, .avi, .mpg etc.) to monitor");
        pMediaUrlParameter.setParameterOptions(true, 1, false);
        pMaxMeasurement = new NumericProperty("_maxMeasurement", "0", "milleseconds");
        pMaxMeasurement.setDisplayText("Maximum for gauge Measurement",
                "optional value to specify as maximum for gauge display in milleseconds based on download time (example: if the downloadtime of the media is 10 seconds and this value is set at 5 seconds (5000ms) than the gauge will show at 50%");
        pMaxMeasurement.setParameterOptions(true, 3, true);
        pStatus = new StringProperty("status", "n/a");
        pDownloadBytes = new NumericProperty("downloadBytes", "0");
        pDownloadBytes.setLabel("downloaded bytes");
        pDownloadBytes.setStateOptions(1);
        pDownloadTime = new NumericProperty("downloadTime", "0", "milliseconds");
        pDownloadTime.setLabel("download time");
        pDownloadTime.setStateOptions(2);
        pPlaybackRate = new NumericProperty("playbackRate", "0", "seconds");
        pPlaybackRate.setLabel("media time");
        pPlaybackRate.setStateOptions(3);
        pStartLatency = new NumericProperty("startLatency", "0");
        pStartLatency.setLabel("start latency");
        pStartLatency.setStateOptions(4);
        pRate = new NumericProperty("rate", "0", "bytes per second");
        pRate.setLabel("download rate (bps)");
        pRate.setStateOptions(5);
        pPlayDuration = new NumericProperty("playDuration", "0", "seconds");
        pPlayDuration.setLabel("download play duration");
        pPlayDuration.setStateOptions(6);
        StringProperty astringproperty[] = { pStatus, pMediaUrlParameter, pTimeout, pStopTime, pPlaybackRate, pDownloadTime, pDownloadBytes, pStartLatency, pMaxMeasurement, pRate, pPlayDuration };
        addProperties("COM.dragonflow.StandardMonitor.RTSPMonitor", astringproperty);
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "description", "Monitors a variety of real time streaming media files including, .wav, .au, .mid, .mov, .mpg files and sources.");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "help", "RTSPMonitor.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "title", "RTSP");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "class", "RTSPMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "toolName", "Stream Monitor Tool");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "toolDescription", "Performs Custom RTSP Monitor test.");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "topazName", "RTSP");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "topazType", "Streaming Media");
        setClassProperty("COM.dragonflow.StandardMonitor.RTSPMonitor", "toolPageDisable", "false");
        addClassElement("COM.dragonflow.StandardMonitor.RTSPMonitor", Rule.stringToClassifier("status != 200\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.RTSPMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.RTSPMonitor", Rule.stringToClassifier("always\twarning", true));
        String s = System.getProperty("RTSPMonitor.trace");
        if (s != null) {
            media_trace = TextUtils.toInt(s);
        }
    }
}
