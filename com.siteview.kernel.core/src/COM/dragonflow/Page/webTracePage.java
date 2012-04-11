/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import COM.dragonflow.SiteView.WebtraceConnection;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class webTracePage extends COM.dragonflow.Page.CGI {

    public webTracePage() {
    }

    public void printBody() {
        String s = request.getValue("host");
        s = COM.dragonflow.Utils.TextUtils
                .keepChars(s,
                        ".-_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        printBodyHeader("Web Trace");
        outputStream
                .println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account="
                        + request.getAccount()
                        + "&AWRequest=yes>Diagnostic Tools</a>"
                        + "</center><P>");
        outputStream
                .println("<p>\n<CENTER><H2>Web Trace</H2></CENTER><P>\n<p>\n"
                        + getPagePOST("webTrace", "")
                        + "Web Trace is a tool that shows you the network path between two locations.\n"
                        + "It connects using HTTP\n"
                        + "and shows the address, the number of retries and errors to each hop in the path.\n"
                        + "When there is a problem with the network, web trace can often be used to\n"
                        + "narrow down where the problem is occuring.\n"
                        + "This form will do a web trace from this server to another location.  Enter\n"
                        + "the domain name or ip address of the other location.\n"
                        + "<p>\n"
                        + "Domain Name or IP address: <input type=text name=host size=60 value=\"\">\n"
                        + "<p>\n");
        outputStream
                .print("<input type=submit value=\"WebTrace\" class=\"VerBl8\">\n</FORM>\n");
        if (s.length() > 0) {
            outputStream.println("<PRE>Tracing route to " + s + "...\n\n"
                    + "(please wait about 30 seconds)\n\n");
            outputStream.flush();
            try {
                COM.dragonflow.SiteView.WebtraceConnection webtraceconnection = new WebtraceConnection();
                String s1 = webtraceconnection.connect();
                if (s1.equals("")) {
                    webtraceconnection.sendCommand(s);
                    String s2 = webtraceconnection.getResult();
                    if (s2.indexOf("xml") >= 0) {
                        String s3 = parseResult(s2);
                        outputStream.println(s3);
                    } else {
                        outputStream.println(s2 + "\n");
                    }
                    outputStream.println("TraceComplete\n\n</PRE>");
                    webtraceconnection.closeAll();
                } else {
                    outputStream.println("Could not perform webtrace command: "
                            + s1);
                    outputStream.println("</PRE>");
                }
            } catch (java.lang.Exception exception) {
                outputStream.println("Could not perform webtrace command: "
                        + exception);
                outputStream.println("</PRE>");
            }
        }
        outputStream.println("</BODY>");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    private String parseResult(String s) {
        String s1 = s;
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        int j = 0;
        while (true) {
            i = s1.indexOf("HOP ") + 4;
            j = s1.indexOf("/>");
            if (i >= 0 && j >= 0) {
                String s2 = s1.substring(i, j);
                s1 = s1.substring(j + 2);
                stringbuffer.append(parseHop(s2));
            }
        if (i <= 0 || j < 0) {
        return stringbuffer.toString();
        }
        }
    }

    private String parseHop(String s) {
        String s1 = s;
        StringBuffer stringbuffer = new StringBuffer();
        String s2 = s1.substring(s1.indexOf("id=") + 4, s1
                .indexOf("\" "));
        if ((new Integer(s2)).intValue() <= 9) {
            s2 = s2 + " ";
        }
        s2 = s2 + ")";
        s1 = s1.substring(s1.indexOf("\" ") + 2);
        String s3 = s1.substring(s1.indexOf("ip=") + 4, s1
                .indexOf("\" "));
        s1 = s1.substring(s1.indexOf("\" ") + 2);
        String s4 = "err: "
                + s1.substring(s1.indexOf("status") + 10, s1.indexOf("\" "));
        s1 = s1.substring(s1.indexOf("\" ") + 2);
        String s5 = "retry: "
                + s1.substring(s1.indexOf("retries") + 11, s1.indexOf("\" "));
        s1 = s1.substring(s1.indexOf("\" ") + 2);
        int i = s1.indexOf("time") + 8;
        double d = (new Double(s1.substring(i, s1.indexOf("\"", i + 1))))
                .doubleValue();
        String s6 = new String("");
        if (d >= 100D) {
            s6 = s6 + "[" + d + "]";
        } else if (d >= 10D) {
            s6 = s6 + "[0" + d + "]";
        } else {
            s6 = s6 + "[00" + d + "]";
        }
        stringbuffer.append(s2 + "  " + s5 + "  " + s4 + "\t" + s3 + "\t" + s6
                + "\n");
        return stringbuffer.toString();
    }
}
