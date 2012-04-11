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

import java.util.HashMap;
import java.util.Vector;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class TecEventHistoryPage extends COM.dragonflow.Page.CGI {

    public static final String COMPONENT_NAME = "ToolPage";

    public static final String DESIRED_HIST_SIZE_SELECT_NAME = "desiredHistSize";

    public static final String PURGE_EVENT_HIOSTORY_NAME = "purgeHistory";

    public static final String TEC_NAME = "tecName";

    public static final String TEC_PORT_NAME = "tecPort";

    private static final java.util.HashMap severityStyles_s_;

    public TecEventHistoryPage() {
    }

    public void printBody() throws java.lang.Exception {
//        COM.dragonflow.topaz.ems.GenericProbe.util.log.LogHelper logger_ = COM.dragonflow.topaz.ems.GenericProbe.util.log.LogPolicyFactory
//                .getInstance("Tivoli").getLogger("ToolPage");
//        printBodyHeader("Tivoli TEC Event History tool");
//        printButtonBar("TecEventHistory.htm", "");
//        COM.dragonflow.topaz.ems.PassiveMonitor.utils.DataStore.AbstractDataStore store = COM.dragonflow.topaz.ems.PassiveMonitor.tec.TecDmEventStore
//                .getInstance();
//        java.util.Vector tecListeners = new Vector();
//        COM.dragonflow.topaz.ems.PassiveMonitor.tec.TecDmEventStore
//                .getServerNames(tecListeners);
//        String desiredHistorySizeStr = request
//                .getValue("desiredHistSize");
//        logger_.debug("Desired event history " + desiredHistorySizeStr);
//        COM.dragonflow.topaz.ems.GenericProbe.util.EventHistory.AbstractEventHistory history = store
//                .history();
//        if ("true".equals(request.getValue("purgeHistory"))) {
//            history.purge();
//        }
//        int histCapacity = history.capacity();
//        int desiredHistorySize = histCapacity;
//        if (null != desiredHistorySizeStr && !"".equals(desiredHistorySizeStr)) {
//            try {
//                desiredHistorySize = java.lang.Integer
//                        .parseInt(desiredHistorySizeStr);
//                if (desiredHistorySize != histCapacity) {
//                    history.setCapacity(desiredHistorySize);
//                }
//            } catch (java.lang.NumberFormatException ex) {
//                logger_.error("Failed to change history capacity.", ex);
//                desiredHistorySize = histCapacity;
//            }
//        }
//        outputStream
//                .print("<p>\n<CENTER><H2>Tivoli TEC event history</H2></CENTER><P>\n<p>\n"
//                        + getPagePOST("TecEventHistory", "")
//                        + "Tivoli TEC Event History tool allows you to view "
//                        + desiredHistorySize
//                        + " recent events received by SiteView at address"
//                        + (tecListeners.size() <= 1 ? " " : "es "));
//        for (int i = 0; i < tecListeners.size(); i++) {
//            outputStream.print(tecListeners.elementAt(i));
//        }
//
//        outputStream.println("<p>\n");
//        outputStream.println("<TABLE BORDER=0>");
//        outputStream.print("Set History Capacity: <select name=");
//        outputStream.print("desiredHistSize");
//        outputStream.print(">");
//        for (int i = 10; i <= 100; i += 10) {
//            outputStream.print("<option value=\"");
//            outputStream.print(i);
//            outputStream
//                    .print(java.lang.Math.abs(desiredHistorySize - i) >= 10 ? "\">"
//                            : "\" SELECTED>");
//            outputStream.print(i);
//            outputStream.print("</option>");
//        }
//
//        outputStream.print("</select>");
//        java.util.Vector events = new Vector();
//        int size = history.getRecentEvents(events);
//        outputStream.println("</TABLE>");
//        outputStream
//                .println("<p>\n<input type=submit value=\"Show TEC Event History Entries\">\n<input type=hidden name=\"purgeHistory\" value=\"false\">\n<input type=submit value=\"Purge Event History\" onclick=\"javascript:purgeHistory.value=true;\">\n</FORM>\n");
//        outputStream
//                .println("<h3>TEC Event History (" + size + " events)</h3>");
//        outputStream
//                .println("<table border=1 cellspacing=0><tr><th>Date<th>Class<th>Severity<th>Status<th>Source<th>Hostname<th>Message");
//        logger_.debug("There're " + size + " events in the history cache.");
//        StringBuffer details = new StringBuffer();
//        for (int i = size - 1; i >= 0; i--) {
//            COM.dragonflow.topaz.ems.EventSourceAPI.AbstractEvent event = (COM.dragonflow.topaz.ems.EventSourceAPI.AbstractEvent) events
//                    .elementAt(i);
//            String severity = event.getValue("severity");
//            String styleStr = (String) severityStyles_s_
//                    .get(severity);
//            styleStr = null != styleStr ? styleStr
//                    : "background-color:#002bed;color:#ffffff";
//            outputStream
//                    .println("<tr><td align=\"center\">"
//                            + event.getValue("date")
//                            + "<td align=\"center\"><a href=\"javascript:void(0)\" onclick=\"showEventDetails("
//                            + (size - i - 1) + ");\">"
//                            + event.getValue("TecEventClassName")
//                            + "</a><td align=\"center\" STYLE=\"" + styleStr
//                            + "\" >" + severity + "<td align=\"center\">"
//                            + event.getValue("status")
//                            + "<td align=\"center\">"
//                            + event.getValue("source")
//                            + "<td align=\"center\">"
//                            + event.getValue("hostname")
//                            + "<td align=\"center\">" + event.getValue("msg"));
//            if (i < size - 1) {
//                details.append(",\n");
//            }
//            addEventDetailsHtmlString(event, details);
//        }
//
//        outputStream.println("</table>");
//        outputStream
//                .println("<SCRIPT LANGUAGE=javascript>   \n<!--                           \nvar evDetails = new Array(     \n"
//                        + details
//                        + ");                             \n"
//                        + "                               \n"
//                        + "function showEventDetails(idx) \n"
//                        + "{                              \n"
//                        + " if(idx >= 0 && idx < evDetails.length){\n"
//                        + "    var detailsWnd = window.open(\"about:blank\", \"\", \"menubar=no, toolbar=no, status=no, titilebar=no, scrollbars=yes\");\n"
//                        + "//detailsWnd.\n"
//                        + "\tvar detailsDoc = detailsWnd.document;\n"
//                        + "\tdetailsDoc.write(evDetails[idx]);\n"
//                        + " }\n"
//                        + "}\n" + "//-->\n" + "</SCRIPT>\n");
//        printContentEndComment();
//        printFooter(outputStream);
//        logger_.debug("There're " + size + " events in the history cache.");
    }

//    private StringBuffer addEventDetailsHtmlString(
//            COM.dragonflow.topaz.ems.EventSourceAPI.AbstractEvent event,
//            StringBuffer details) {
//        details = null != details ? details : new StringBuffer();
//        if (null != event) {
//            details
//                    .append("\"<HTML><BODY BGCOLOR=#ffffff link=#1155bb alink=#1155bb vlink=#1155bb><Center><h3>TEC Event Details</h3></Center><table border=1 cellspacing=0><tr><th>Name<th>Value");
//            Enumeration slots = event.getEntries_LogicOrder();
//            if (null != slots) {
//                java.util.Map.Entry slot;
//                for (; slots.hasMoreElements(); details.append(
//                        "<tr><td align=\\\"center\\\">").append(slot.getKey())
//                        .append("<td align=\\\"center\\\">").append(
//                                normalize(slot.getValue().toString()))) {
//                    slot = (java.util.Map.Entry) slots.nextElement();
//                }
//
//            }
//            details
//                    .append("</table><BR><Center><input type=button value=\\\"Close\\\" onmousedown=\\\"javascript:window.close()\\\"></Center></BODY></HTML>\"");
//        }
//        return details;
//    }

    private String normalize(String s) {
        String rc = "";
        if (null != s) {
            StringBuffer normal = new StringBuffer(s.length());
            for (int i = 0; i < s.length(); i++) {
                switch (s.charAt(i)) {
                case 10: // '\n'
                    normal.append("\\n");
                    break;

                case 13: // '\r'
                    normal.append("\\r");
                    break;

                case 34: // '"'
                    normal.append('\'');
                    break;

                default:
                    normal.append(s.charAt(i));
                    break;
                }
            }

            rc = normal.toString();
        }
        return rc;
    }

    static {
        severityStyles_s_ = new HashMap();
        severityStyles_s_
                .put("FATAL", "background-color:#000000;color:#ffffff");
        severityStyles_s_.put("CRITICAL",
                "background-color:#fb554d;color:#ffffff");
        severityStyles_s_
                .put("MINOR", "background-color:#f2bd00;color:#ffffff");
        severityStyles_s_.put("WARNING",
                "background-color:#ffff77;color:#000000");
        severityStyles_s_.put("HARMLESS",
                "background-color:#7cfc00;color:#000000");
        severityStyles_s_.put("UNKNOWN",
                "background-color:#002bed;color:#ffffff");
    }
}
