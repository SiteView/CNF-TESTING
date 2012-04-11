/*
 * 
 * Created on 2005-2-16 16:20:58
 *
 * PQVPrintChooserHTML.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PQVPrintChooserHTML</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.io.PrintWriter;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// PortalQueryVisitor, SubGroup, PortalSiteView, MonitorGroup,
// SiteViewObject, Platform, Monitor

public class PQVPrintChooserHTML extends PortalQueryVisitor {

    HashMap currentState;

    HashMap selectedMap;

    PrintWriter outputStream;

    CGI page;

    public PQVPrintChooserHTML(CGI cgi, HashMap hashmap, HashMap hashmap1) {
        currentState = null;
        selectedMap = null;
        outputStream = null;
        page = null;
        outputStream = cgi.outputStream;
        currentState = hashmap;
        selectedMap = hashmap1;
        page = cgi;
    }

    protected String getIndentHTML(int i) {
        int j = i * 11;
        if (j == 0) {
            j = 1;
        }
        return "<img src=/SiteView/htdocs/artwork/empty1111.gif height=11 width="
                + j + " border=0>";
    }

    boolean siteviewPre(PortalSiteView portalsiteview) {
        String s = portalsiteview.getFullID();
        boolean flag = currentState.get(s) != null;
        printItem(portalsiteview, 0, flag, true);
        return flag;
    }

    boolean groupPre(MonitorGroup monitorgroup, PortalSiteView portalsiteview) {
        String s;
        boolean flag;
        boolean flag1;
        label0: {
            s = monitorgroup.getFullID();
            flag = false;
            flag1 = false;
            boolean flag2 = false;
            String s1 = monitorgroup.getProperty(MonitorGroup.pParent);
            do {
                if (s1.length() <= 0) {
                    break label0;
                }
                boolean flag3 = true;
                SiteViewObject siteviewobject = portalsiteview
                        .getElement(s1);
                if (siteviewobject == null) {
                    break;
                }
                String s2 = siteviewobject.getFullID();
                if (currentState.get(s2) == null) {
                    flag = true;
                    break label0;
                }
                s1 = siteviewobject.getProperty(MonitorGroup.pParent);
            } while (true);
            flag = true;
        }
        if (!flag) {
            flag1 = currentState.get(s) != null;
            int i = TextUtils.toInt(monitorgroup.getProperty("groupLevel"));
            printItem(monitorgroup, (i + 1) * 3, flag1, true);
        }
        return flag1;
    }

    boolean monitorPre(Monitor monitor, MonitorGroup monitorgroup,
            PortalSiteView portalsiteview) {
        if (!(monitor instanceof SubGroup)) {
            int i = TextUtils.toInt(monitorgroup.getProperty("groupLevel"));
            printItem(monitor, (i + 2) * 3, false, false);
        }
        return false;
    }

    void printItem(SiteViewObject siteviewobject, int i, boolean flag,
            boolean flag1) {
        String s = siteviewobject.getFullID();
        String s1 = getIndentHTML(i);
        String s2 = Platform.getURLPath("htdocs", request.getAccount())
                + "/Detail";
        String s3 = siteviewobject.getProperty("_name");
        if (s3.length() == 0) {
            s3 = siteviewobject.getProperty("_title");
        }
        outputStream.print("<TR><TD>");
        outputStream.print(s1);
        if (flag1) {
            if (flag) {
                outputStream
                        .print("<input type=image name=close"
                                + s
                                + " src=/SiteView/htdocs/artwork/Minus.gif alt=\"close\" border=0>");
            } else {
                outputStream
                        .print("<input type=image name=open"
                                + s
                                + " src=/SiteView/htdocs/artwork/Plus.gif alt=\"open\" border=0>");
            }
        }
        outputStream.print("<input type=checkbox name=item value=\"" + s
                + "\" " + TextUtils.getValue(selectedMap, s) + ">");
        outputStream.print("<B><A HREF=" + s2 + HTTPRequest.encodeString(s)
                + ".html>" + s3);
        outputStream.println("</A></B></TD></TR>");
    }

    public static void main(String args[]) throws IOException {
    }
}