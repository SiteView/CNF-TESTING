/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class SNMPPage extends com.dragonflow.Page.CGI {

    private int mibBlkSize;

    public SNMPPage() {
        mibBlkSize = 1;
    }

    private void NextLink(String s, String s1,
            String s2, String s3, String s4,
            String s5) {
        outputStream.println(getPagePOST("SNMP", ""));
        outputStream.println("<TABLE BORDER=0>");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Host IP Address:</TD><TD><input type=text name=host value=\""
                        + s + "\" size=32></TD></TR>\n");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Next OID:</TD><TD><input type=text name=nextoid value=\""
                        + s1 + "\" size=32></TD></TR>\n");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Index:</TD><TD><input type=text name=index value=\""
                        + s3 + "\" size=10></TD></TR>\n");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Community:</TD><TD><input type=text name=community value=\""
                        + s2 + "\" size=10></TD></TR>\n");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Version (V1 or V2):</TD><TD><input type=text name=version value=\""
                        + s5 + "\" size=10></TD></TR>\n");
        outputStream
                .println("<TR><TD ALIGN=RIGHT>Number of Records to get:</TD><TD><input type=text name=blocksize value=\""
                        + s4 + "\" size=10></TD></TR>\n");
        outputStream.println("</TABLE>");
        outputStream
                .println("<p>\n<input type=submit value=\"Next Block of OIDs\">\n</FORM>\n");
    }

    public void TableRecord(String s, String s1,
            String s2) {
        outputStream.println("<tr>");
        outputStream.println("<td>OID: </td><td>" + s2 + "</td>");
        outputStream.println("<td>Next OID: </td><td>" + s + "</td>");
        outputStream.println("<td>Value: </td><td>" + s1 + "</td>");
        outputStream.println("</tr>");
    }

    public void printBody() {
        String s = request.getValue("host");
        String s1 = request.getValue("nextoid");
        String s2 = request.getValue("index");
        String s3 = request.getValue("community");
        String s4 = request.getValue("version");
        String s5 = request.getValue("blocksize");
        if (com.dragonflow.Utils.TextUtils.toInt(s5) <= 0) {
            s5 = "1";
        }
        if (!s4.equals("V1") || !s4.equals("V2")) {
            s4 = "V1";
        }
        printButtonBar("SNMPTool.htm", "");
        printBodyHeader("SNMP Diagnostic Page");
        outputStream
                .print("<P><CENTER><H2>SNMP</H2></CENTER><P>SNMP Diagnostic page is a tool that will retrieve the next nth OIDs in the specified object using \"GET NEXT\" requests to the host.");
        NextLink(s, s1, s3, s2, s5, s4);
        outputStream.println("<pre>");
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        s1 = s1 + "++";
        if (request.isPost()) {
            for (int i = 0; i < com.dragonflow.Utils.TextUtils.toInt(s5); i++) {
                String s6 = com.dragonflow.StandardMonitor.SNMPMonitor
                        .readSNMPValue(s, "SNMP Tools Page", 5, 1, s3,
                                stringbuffer, s1, s2, false, s4);
                String s7 = com.dragonflow.StandardMonitor.SNMPMonitor
                        .readSNMPValue(s, "SNMP Tools Page", 5, 1, s3,
                                stringbuffer1,
                                s1.substring(0, s1.length() - 2), s2, false, s4);
                if (s6.length() <= 0) {
                    s7 = " No next Variable " + stringbuffer.toString();
                }
                if (s7.length() <= 0) {
                    s7 = " No such Variable " + stringbuffer1.toString();
                }
                TableRecord(s6, s7, s1.substring(0, s1.length() - 2));
                String s8 = s6.substring(s6.lastIndexOf("."), s6
                        .length());
                if (s8.equals(".0")) {
                    s1 = s6.substring(0, s6.lastIndexOf(s8)) + "++";
                } else {
                    s1 = s6 + "++";
                }
            }

        }
        printFooter(outputStream);
        outputStream.println("</pre>");
        outputStream.flush();
    }
}
