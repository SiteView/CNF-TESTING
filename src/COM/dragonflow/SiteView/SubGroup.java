/*
 * 
 * Created on 2005-2-16 17:02:51
 *
 * SubGroup.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>SubGroup</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.I18N;

// Referenced classes of package COM.dragonflow.SiteView:
// Monitor, MonitorGroup, SiteViewGroup, SiteViewObject

public class SubGroup extends Monitor {

    public static StringProperty pGroup;

    private static String BGTableClass = "subgroup";

    public SubGroup() {
    }

    public String getHostname() {
        return "SubGroupHost";
    }

    protected Array calculateIPAddresses() {
        return new Array();
    }

    public MonitorGroup lookupGroup() {
        SiteViewObject siteviewobject = SiteViewGroup
                .currentSiteViewForID(getFullID());
        return (MonitorGroup) siteviewobject.getElement(I18N
                .toDefaultEncoding(getProperty(pGroup)));
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        setProperty(pDisabled, "");
    }

    public String getProperty(StringProperty stringproperty)
            throws NullPointerException {
        if (stringproperty == pCategory || stringproperty == pStateString
                || stringproperty == pMeasurement
                || stringproperty == pLastUpdate) {
            MonitorGroup monitorgroup = lookupGroup();
            if (monitorgroup != null) {
                return monitorgroup.getProperty(stringproperty);
            }
            LogManager.log("Error", "Bad subgroup link: "
                    + I18N.toDefaultEncoding(getProperty(pGroup) + " in group "
                            + getProperty(pOwnerID)));
            if (stringproperty == pCategory) {
                return Monitor.NODATA_CATEGORY;
            }
            if (stringproperty == pStateString) {
                return "bad subgroup link";
            } else {
                return "";
            }
        } else {
            return super.getProperty(stringproperty);
        }
    }

    protected void printTableCategoryEntry(PrintWriter printwriter,
            HTTPRequest httprequest, boolean flag, String s, String s1) {
        String s2 = "";
        String s3 = "";
        String s4 = getProperty(pGroup);
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup
                .getElement(s4);
        String s5 = s4;
        if (monitorgroup != null) {
            s5 = monitorgroup.getProperty(Monitor.pName);
        }
        printwriter.print("<TD ALIGN=CENTER BGCOLOR=#000000>" + s2
                + "<IMG BORDER=0 SRC=" + MonitorGroup.getCategoryArt(s1)
                + " ALT=\"" + s + "\">" + s3 + "</TD>");
    }

    protected void printTableAckEntry(PrintWriter printwriter,
            HTTPRequest httprequest, boolean flag, String s, String s1) {
        String s2 = "";
        String s3 = "";
        String s4 = HTTPRequest.encodeString(I18N.UnicodeToString(
                getProperty(pGroupID), I18N.nullEncoding()));
        String s5 = getProperty(pID);
        String s8 = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        String s6;
        String s7;
        if (!flag) {
            s6 = "Acknowledge";
            s7 = "Acknowledge Group Monitor States";
            if (httprequest.actionAllowed("_monitorAcknowledge")) {
                s2 = "<A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation="
                        + s6
                        + "&monitor="
                        + s4
                        + "+"
                        + s5
                        + s8
                        + "&account="
                        + httprequest.getAccount()
                        + "&returnURL="
                        + s4
                        + "&returnLabel=" + "Group+Detail" + ">";
                s3 = "</A>";
            }
        } else {
            s6 = "AcknowledgeClear";
            s7 = "Acknowledged by: " + s1 + ", " + s;
            if (httprequest.actionAllowed("_monitorAcknowledge")) {
                s2 = "<A HREF=/SiteView/cgi/go.exe/SiteView?page=manage&operation="
                        + s6
                        + "&monitor="
                        + s4
                        + "+"
                        + s5
                        + s8
                        + "&account="
                        + httprequest.getAccount()
                        + "&returnURL="
                        + s4
                        + "&returnLabel=" + "Group+Detail" + ">";
                s3 = "</A>";
            }
        }
        printwriter.print("<TD ALIGN=CENTER BGCOLOR=#000000>" + s2
                + "<IMG BORDER=0 SRC=" + MonitorGroup.getAckArt(s6) + " ALT=\""
                + s7 + "\">" + s3 + "</TD>");
    }

    protected void printTableStatusEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        String s = I18N.toDefaultEncoding(getProperty(pGroup));
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        SiteViewObject siteviewobject = siteviewgroup.getElement(s);
        if (siteviewobject instanceof MonitorGroup) {
            String s1;
            try {
                Array array = CGI.ReadGroupFrames(s, null);
                int i = array.size() - 1;
                s1 = String.valueOf(i);
            } catch (Exception exception) {
                s1 = "unknown number";
            }
            printwriter.print("<TD CLASS=" + BGTableClass + ">" + s1
                    + " in group, ");
            MonitorGroup monitorgroup = (MonitorGroup) siteviewobject;
            String s2 = monitorgroup
                    .getProperty(MonitorGroup.pGroupErrorDisplay);
            if (s2.equals("0")) {
                printwriter.print("none in error");
            } else {
                printwriter.print(s2 + " errors");
            }
            printwriter.print("</TD>");
        } else {
            printwriter.print("<TD>no data</TD>");
        }
    }

    protected void printTableNameEntry(PrintWriter printwriter,
            HTTPRequest httprequest, String s) {
        String s1 = httprequest.getValue("_health").length() <= 0 ? ""
                : "?_health=true";
        printwriter.print("<TD CLASS="
                + BGTableClass
                + ">"
                + "<A HREF="
                + "/SiteView/"
                + httprequest.getAccountDirectory()
                + "/Detail"
                + HTTPRequest.encodeString(I18N
                        .toDefaultEncoding(getProperty(pGroup))) + ".html" + s1
                + ">" + getProperty(pName) + "</A></TD>");
    }

    protected void printTableRefreshEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        printwriter.print("<TD CLASS=" + BGTableClass + ">&nbsp;</TD>");
    }

    protected void printTableEditEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        String s = httprequest.getValue("_health").length() <= 0 ? ""
                : "?_health=true";
        printwriter.print("<TD ALIGN=CENTER CLASS="
                + BGTableClass
                + "><A HREF="
                + "/SiteView/"
                + httprequest.getAccountDirectory()
                + "/Detail"
                + HTTPRequest.encodeString(I18N
                        .toDefaultEncoding(getProperty(pGroup))) + ".html" + s
                + ">Edit</A></TD>");
    }

    protected void printTableDeleteEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        String s = httprequest.getValue("_health").length() <= 0 ? ""
                : "&_health=true";
        printwriter
                .print("<TD ALIGN=CENTER CLASS="
                        + BGTableClass
                        + "><A HREF=/SiteView/cgi/go.exe/SiteView?page=group&operation=Delete&group="
                        + HTTPRequest.encodeString(I18N
                                .toDefaultEncoding(getProperty(pGroup))) + s
                        + "&account=" + httprequest.getAccount()
                        + ">X</A></TD>");
    }

    protected void printTableMoreEntry(PrintWriter printwriter,
            HTTPRequest httprequest) {
        printwriter.print("<TD CLASS=" + BGTableClass + ">&nbsp;</TD>");
    }

    protected void printTableUpdatedEntry(PrintWriter printwriter, String s) {
        printwriter.print("<TD CLASS=" + BGTableClass + "><SMALL>" + s
                + "</SMALL></TD>");
    }

    protected void printTableGaugeEntry(PrintWriter printwriter, String s) {
        printwriter.print("<TD ALIGN=CENTER BGCOLOR=#000000>" + s + "</TD>");
    }

    public static void main(String args[]) {
        System.out.println("\n---------------------------");
        System.out.println("  Testing SubGroup Monitor");
        System.out.println("---------------------------\n");
        new SubGroup();
        System.out.println("\n--------");
        System.out.println("  done");
        System.out.println("--------\n");
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public boolean isDisabled() {
        try {
            Enumeration enumeration = this.lookupGroup().getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor) enumeration.nextElement();
                if (!monitor.isDisabled()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    static {
        pGroup = new StringProperty("_group");
        StringProperty astringproperty[] = { pGroup };
        addProperties("COM.dragonflow.SiteView.SubGroup", astringproperty);
    }
}
