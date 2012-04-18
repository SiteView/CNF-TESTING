/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Utils:
// RemoteCommandLine, TextUtils, SocketSession

public class CGICommandLine extends com.dragonflow.Utils.RemoteCommandLine {

    public CGICommandLine() {
    }

    public String getMethodName() {
        return "http";
    }

    public String getMethodDisplayName() {
        return "HTTP";
    }

    public Array exec(String s, com.dragonflow.SiteView.Machine machine, boolean flag) {
        int i = com.dragonflow.Properties.StringProperty.toInteger(machine.getProperty(com.dragonflow.SiteView.Machine.pTimeout));
        if (i == 0) {
            i = 60000;
        } else {
            i *= 1000;
        }
        String s1 = machine.getProperty(com.dragonflow.SiteView.Machine.pHost);
        s1 = s1 + "?COMMAND=" + java.net.URLEncoder.encode(s);
        traceMessage(machine.getProperty(com.dragonflow.SiteView.Machine.pHost), machine, TO_REMOTE);
        traceMessage(s, machine, TO_REMOTE);
        traceMessage("URL: " + s1, machine, TO_REMOTE);
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        stringbuffer1.append(machine.getProperty(com.dragonflow.SiteView.Machine.pHost));
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        long l = com.dragonflow.Utils.TextUtils.toLong(com.dragonflow.Utils.TextUtils.getValue(hashmap, "_urlContentMatchMax"));
        if (l <= 0L) {
            l = 50000L;
        }
        progressMessage("Retrieving URL...");
        long al[] = com.dragonflow.StandardMonitor.URLMonitor.checkURL(com.dragonflow.Utils.SocketSession.getSession(null), s1, "", "", machine.getProperty("_proxy"), machine.getProperty("_proxyUsername"), machine.getProperty("_proxyPassword"), null,
                machine.getProperty("_login"), machine.getProperty("_password"), "", stringbuffer, l, "", 5, i, stringbuffer1);
        int j = (int) al[0];
        Array array = null;
        if (machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace)) {
            traceMessage(stringbuffer.toString(), machine, FROM_REMOTE);
        }
        if (detail && progressStream != null) {
            progressStream.println(stringbuffer);
        }
        if (j == com.dragonflow.StandardMonitor.URLMonitor.kURLok) {
            String s2 = com.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer.toString());
            array = com.dragonflow.SiteView.Platform.split('\n', s2);
            traceMessage(array, machine, FROM_REMOTE);
        } else {
            progressMessage("Error retrieving URL: " + com.dragonflow.SiteView.Monitor.lookupStatus(j));
            traceMessage("ERROR - STATUS = " + j, machine, FROM_REMOTE);
            exitValue = j;
        }
        if (array == null) {
            array = new Array();
        }
        if (flag) {
            array.pushFront("Retrieving URL: " + machine.getProperty(com.dragonflow.SiteView.Machine.pHost));
            array.add("");
            array.add("");
            array.add("Result: " + com.dragonflow.SiteView.Monitor.lookupStatus(j));
        }
        return array;
    }
}
