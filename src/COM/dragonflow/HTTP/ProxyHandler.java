/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.HTTP;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;

import jgl.Array;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.HTTP:
// HTTPRequest

public class ProxyHandler {

    public static jgl.HashMap monitor = new HashMapOrdered(true);

    public static boolean started = false;

    public static int step = 0;

    public static String groupID = "recorded";

    public ProxyHandler() {
    }

    public static void handleProxyRequest(COM.dragonflow.HTTP.HTTPRequest httprequest, java.io.PrintWriter printwriter) {
        String s = httprequest.rawURL;
        if (s.indexOf("siteviewRecordStart") >= 0) {
            started = true;
            groupID = COM.dragonflow.Utils.I18N.toDefaultEncoding(httprequest.getValue("groupID"));
            if (groupID.length() == 0) {
                groupID = "recorded";
            }
            COM.dragonflow.Utils.TextUtils.debugPrint("STARTING");
            String s1 = "<HTML><HEAD><TITLE>Starting Recording</TITLE></HEAD>\n<BODY><H2>Starting Recording</H2></BODY></HTML>";
            COM.dragonflow.HTTP.HTTPRequest _tmp = httprequest;
            COM.dragonflow.HTTP.HTTPRequest.printHeader(printwriter, 200, "OK", "text/html", s1.length(), null, -1L, -1L);
            printwriter.print(s1);
            return;
        }
        if (s.indexOf("siteviewRecordStop") >= 0) {
            started = false;
            COM.dragonflow.Utils.TextUtils.debugPrint("ENDING");
            String s2 = "<HTML><HEAD><TITLE>Done Recording</TITLE></HEAD>\n<BODY><H2>Done Recording</H2>\n<P><A HREF=http://localhost:8888" + COM.dragonflow.Page.CGI.getGroupDetailURL(httprequest, groupID) + ">Go to group</A>\n"
                    + "</BODY></HTML>";
            COM.dragonflow.HTTP.HTTPRequest _tmp1 = httprequest;
            COM.dragonflow.HTTP.HTTPRequest.printHeader(printwriter, 500, "Internal Server Error", "text/html", s2.length(), null, -1L, -1L);
            printwriter.print(s2);
            COM.dragonflow.HTTP.ProxyHandler.saveMonitor();
            return;
        }
        COM.dragonflow.Utils.TextUtils.debugPrint("Request\n" + httprequest.postDetails());
        String s3 = "";
        String s4 = "";
        String s5 = "";
        String s6 = "";
        String s7 = "";
        if (s.indexOf(":443") >= 0) {
            s7 = "[NT]";
            if (httprequest.requestMethod.equals("CONNECT")) {
                COM.dragonflow.HTTP.HTTPRequest _tmp2 = httprequest;
                COM.dragonflow.HTTP.HTTPRequest.printHeader(printwriter, 200, "OK", "text/html", 0L, null, -1L, -1L);
                return;
            }
        }
        String s8 = "";
        int i = 60000;
        COM.dragonflow.Utils.TextUtils.debugPrint("userName=" + s7);
        COM.dragonflow.Utils.TextUtils.debugPrint("method=" + httprequest.requestMethod);
        long l = 0xf4240L;
        StringBuffer stringbuffer = new StringBuffer();
        jgl.Array array = new Array();
        for (Enumeration enumeration = httprequest.getVariables(); enumeration.hasMoreElements();) {
            String s9 = (String) enumeration.nextElement();
            Enumeration enumeration2 = httprequest.getValues(s9);
            while (enumeration2.hasMoreElements()) {
                array.add(s9 + "=" + enumeration2.nextElement());
            }
        }

        if (array.size() == 0) {
            array = null;
        }
        COM.dragonflow.Utils.TextUtils.debugPrint("GETTING URL=" + s);
        if (array != null) {
            for (Enumeration enumeration1 = array.elements(); enumeration1.hasMoreElements(); COM.dragonflow.Utils.TextUtils.debugPrint((String) enumeration1.nextElement())) {
            }
        }
        StringBuffer stringbuffer1 = new StringBuffer("");
        COM.dragonflow.Utils.SocketSession socketsession = COM.dragonflow.Utils.SocketSession.getSession(null);
        long al[] = COM.dragonflow.StandardMonitor.URLMonitor.checkURL(socketsession, s, s3, "", s4, s5, s6, array, s7, s8, "", stringbuffer, l, "", 100, i, null, stringbuffer1);
        socketsession.close();
        COM.dragonflow.Utils.TextUtils.debugPrint("RESULT=" + al[0]);
        String s10 = stringbuffer1.toString();
        int j = s10.indexOf("CONTENT-TYPE:");
        int k = -1;
        if (j >= 0) {
            k = s10.substring(j).toUpperCase().indexOf("TEXT");
            if (k < 0) {
                k = s10.substring(j).toUpperCase().indexOf("MULTIPART/X-MIXED-REPLACE");
            }
        }
        if (j >= 0 && k >= 0 && k - j < 40) {
            COM.dragonflow.Utils.TextUtils.debugPrint("RESULT BUFFER=\n" + stringbuffer1.toString());
        }
        String s11 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPHeaders(stringbuffer.toString());
        if (started) {
            boolean flag = true;
            jgl.Array array1 = new Array();
            if (COM.dragonflow.Utils.TextUtils.matchExpression(s11, "/content-type:\\s*(\\S*)/i", array1, new StringBuffer()) > 0) {
                String s12 = (String) array1.at(0);
                COM.dragonflow.Utils.TextUtils.debugPrint("CONTENTTYPE=" + s12);
                if (!s12.equalsIgnoreCase("text/html")) {
                    flag = false;
                }
            }
            if (flag) {
                COM.dragonflow.Utils.TextUtils.debugPrint("SAVING STEP");
                step ++;
                monitor.put("_referenceType" + step, "url");
                monitor.put("_reference" + step, s);
                if (array != null) {
                    for (int i1 = 0; i1 < array.size(); i1 ++) {
                        monitor.add("_postData" + step, array.at(i1));
                    }

                }
            }
        }
        COM.dragonflow.Utils.TextUtils.debugPrint("HEADERS=\n" + s11 + "----------\n\n");
        printwriter.print(stringbuffer.toString());
    }

    static void saveMonitor() {
        monitor.put("_class", "URLSequenceMonitor");
        monitor.put("_id", "1");
        monitor.put("_timeout", "60");
        monitor.put("_frequency", "9000");
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        hashmapordered.put("_name", "config");
        hashmapordered.put("_nextID", "2");
        try {
            jgl.Array array = new Array();
            array.add(hashmapordered);
            array.add(monitor);
            String s = COM.dragonflow.SiteView.Platform.getRoot() + "/groups/" + groupID + ".mg";
            COM.dragonflow.Utils.TextUtils.debugPrint("SAVING TO " + s);
            COM.dragonflow.Properties.FrameFile.writeToFile(s, array);
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "Error saving group " + exception.getMessage());
        }
        COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(groupID);
    }

}
