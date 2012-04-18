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
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public abstract class Logger {

    static String FIELD_SEPARATOR = "\t";

    protected java.util.Vector buffers;

    protected long bufferDuration;

    public String echoTo;

    protected String fullMonitorName;

    public Logger() {
        buffers = new Vector();
        bufferDuration = 0L;
        echoTo = "";
        fullMonitorName = null;
    }

    public Logger(long l) {
        buffers = new Vector();
        bufferDuration = 0L;
        echoTo = "";
        fullMonitorName = null;
        bufferDuration = l;
    }

    public static String dateToString(java.util.Date date) {
        return com.dragonflow.Utils.TextUtils.dateToString(date);
    }

    public void setEchoTo(String s) {
        echoTo = s.toLowerCase();
    }

    String getMonitorName(com.dragonflow.Properties.PropertiedObject propertiedobject) {
        if (fullMonitorName == null) {
            HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
            fullMonitorName = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_fullMonitorName");
        }
        String s = "";
        if (fullMonitorName.length() > 0) {
            s = com.dragonflow.Page.CGI.getGroupFullName(propertiedobject.getProperty(com.dragonflow.SiteView.AtomicMonitor.pGroupID));
            s = s + ": " + propertiedobject.getProperty(com.dragonflow.SiteView.AtomicMonitor.pName);
        } else {
            s = propertiedobject.getProperty(com.dragonflow.SiteView.AtomicMonitor.pName);
        }
        return s;
    }

    public void log(String s, java.util.Date date, com.dragonflow.Properties.PropertiedObject propertiedobject) {
        StringBuffer stringbuffer = new StringBuffer();
        Array array = propertiedobject.getLogProperties();
        String s1;
        for (Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); stringbuffer.append(s1)) {
            com.dragonflow.Properties.StringProperty stringproperty = (com.dragonflow.Properties.StringProperty) enumeration.nextElement();
            if (stringbuffer.length() > 0) {
                stringbuffer.append("\t");
            }
            if (stringproperty == com.dragonflow.SiteView.Monitor.pID) {
                s1 = propertiedobject.getProperty(stringproperty) + ":" + propertiedobject.getProperty(com.dragonflow.SiteView.AtomicMonitor.pSample);
            } else if (stringproperty == com.dragonflow.SiteView.AtomicMonitor.pName) {
                s1 = getMonitorName(propertiedobject);
            } else {
                s1 = propertiedobject.getProperty(stringproperty);
            }
            s1 = s1.replace('\r', ' ');
            s1 = s1.replace('\n', ' ');
            s1 = s1.replace('\t', ' ');
        }

        if (propertiedobject.getProperty(com.dragonflow.SiteView.Monitor.pCategory).equals(com.dragonflow.SiteView.Monitor.ERROR_CATEGORY)) {
            stringbuffer.append("\t");
            if (((com.dragonflow.SiteView.AtomicMonitor) propertiedobject).getProperty(com.dragonflow.SiteView.AtomicMonitor.pNoData).length() > 0) {
                stringbuffer.append(com.dragonflow.SiteView.Monitor.FAILURE);
            } else {
                stringbuffer.append(com.dragonflow.SiteView.Monitor.NON_FAILURE);
            }
        }
        log(s, date, stringbuffer.toString());
    }

    public void log(String s, java.util.Date date, String s1) {
    }

    public void close() {
    }

    public String customLogName() {
        return "";
    }

    public long getBufferDuration() {
        return bufferDuration;
    }

    public void flush() {
        for (; buffers.size() > 0; flush(buffers.remove(0))) {
        }
    }

    public void flush(java.lang.Object obj) {
    }

}
