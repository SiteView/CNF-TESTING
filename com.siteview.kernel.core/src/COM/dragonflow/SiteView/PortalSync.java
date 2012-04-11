/*
 * 
 * Created on 2005-2-16 16:20:04
 *
 * PortalSync.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>PortalSync</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// PortalSiteView, Portal, MasterConfig

public class PortalSync {

    private static final int NO_BACKUP_FILE = 0;

    private static final int CREATE_BACKUP_FILE = 1;

    public long status;

    PrintWriter traceStream;

    static String START_TAG = "<HTML>";

    static String END_TAG = "</HTML>";

    public PortalSync(PrintWriter printwriter) {
        status = URLMonitor.kURLok;
        traceStream = null;
        traceStream = printwriter;
    }

    public PortalSync() {
        this(null);
    }

    public void sync() {
        sync("");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     */
    public void sync(String s) {
        Enumeration enumeration = Portal.getPortal().getElements();
        printProgress("Starting sync of configurations");
        while (enumeration.hasMoreElements()) {
            PortalSiteView portalsiteview = (PortalSiteView) enumeration
                    .nextElement();
            if (s.length() <= 0
                    || portalsiteview.getProperty(PortalSiteView.pID).equals(
                            s)) {
                if (portalsiteview.getProperty(PortalSiteView.pDisabled)
                        .length() > 0) {
                    printProgress("sync disabled for "
                            + portalsiteview
                                    .getProperty(PortalSiteView.pTitle));
                } else if (portalsiteview.getProperty(
                        PortalSiteView.pReadOnly).length() > 0) {
                    printProgress("SiteView "
                            + portalsiteview
                                    .getProperty(PortalSiteView.pTitle)
                            + " is read only");
                } else {
                    printProgress("Syncing with "
                            + portalsiteview
                                    .getProperty(PortalSiteView.pTitle));
                    boolean flag = syncSiteView(portalsiteview, true,
                            traceStream);
                    if (flag) {
                        printProgress("Syncing with "
                                + portalsiteview
                                        .getProperty(PortalSiteView.pTitle)
                                + " completed");
                    } else {
                        printProgress("Syncing with "
                                + portalsiteview
                                        .getProperty(PortalSiteView.pTitle)
                                + " failed");
                    }
                }
            }
        }
        printProgress("Sync of configurations complete");
    }

    public static boolean syncSiteView(PortalSiteView portalsiteview,
            boolean flag, PrintWriter printwriter) {
        long l = copyDirectory(portalsiteview, "/groups/", printwriter, 1);
        if (flag && l == 200L) {
            l = copyDirectory(portalsiteview, "/templates.eventlog/",
                    printwriter, 0);
            l = copyDirectory(portalsiteview, "/templates.history/",
                    printwriter, 0);
            l = copyDirectory(portalsiteview, "/templates.historyGraphics/",
                    printwriter, 0);
            l = copyDirectory(portalsiteview, "/templates.mail/", printwriter,
                    0);
            l = copyDirectory(portalsiteview, "/templates.os/", printwriter, 0);
            l = copyDirectory(portalsiteview, "/templates.page/", printwriter,
                    0);
            l = copyDirectory(portalsiteview, "/templates.perfmon/",
                    printwriter, 0);
            l = copyDirectory(portalsiteview, "/templates.post/", printwriter,
                    0);
            l = copyDirectory(portalsiteview, "/templates.script/",
                    printwriter, 0);
            l = copyDirectory(portalsiteview, "/templates.snmp/", printwriter,
                    0);
            l = copyDirectory(portalsiteview, "/templates.sound/",
                    printwriter, 0);
            l = copyDirectory(portalsiteview, "/templates.view/", printwriter,
                    0);
            l = copyDirectory(portalsiteview, "/scripts/", printwriter, 0);
        }
        updateStaticPagesSiteView(portalsiteview);
        return l == 200L;
    }

    static long copyDirectory(PortalSiteView portalsiteview, String s,
            PrintWriter printwriter, int i) {
        long l = 200L;
        printProgress("  copying directory " + s, printwriter);
        StringBuffer stringbuffer = new StringBuffer();
        l = listFiles(portalsiteview, s, stringbuffer);
        String s1 = "";
        if (l == 200L) {
            s1 = stringbuffer.toString();
            if (s1.startsWith("file load error")) {
                l = URLMonitor.kMonitorSpecificError;
            }
        } else {
            printProgress("<B>Error accessing SiteView "
                    + portalsiteview.getProperty(PortalSiteView.pTitle)
                    + " - " + URLMonitor.lookupStatus(l) + "</B>", printwriter);
        }
        if (l == 200L) {
            String as[] = TextUtils.split(s1);
            HashMap hashmap = new HashMap();
            for (int j = 0; j < as.length
                    && (j >= 1 || !as[j].startsWith("error:")); j++) {
                if (!as[j].endsWith(".bak") && !as[j].endsWith(".dyn")) {
                    hashmap.put(s + as[j], "true");
                }
            }

            String s2 = Portal.getPortalSiteViewRootPath(portalsiteview
                    .getProperty(PortalSiteView.pID));
            File file = new File(s2 + s);
            as = file.list();
            if (as != null) {
                for (int k = 0; k < as.length; k++) {
                    String s3 = s + as[k];
                    String s5 = s2 + s3;
                    if ((Portal.centrascopeDebug & 2) != 0) {
                        TextUtils.debugPrint("PARTIAL PATHNAME=" + s3);
                        TextUtils.debugPrint("FULL PATHNAME=" + s5);
                    }
                    hashmap.remove(s3);
                    StringBuffer stringbuffer1 = new StringBuffer();
                    StringBuffer stringbuffer2 = new StringBuffer();
                    try {
                        stringbuffer1.append(FileUtils.readFile(s5));
                        l = putFile(portalsiteview, s3, stringbuffer1,
                                stringbuffer2, i);
                        if (l != 200L) {
                            printProgress(
                                    "Could not put file " + s3 + ": " + l,
                                    printwriter);
                        }
                    } catch (Exception exception) {
                        printProgress("Could not read file " + s5 + ": "
                                + exception, printwriter);
                    }
                }

                for (Enumeration enumeration = hashmap.keys(); enumeration
                        .hasMoreElements();) {
                    String s4 = (String) enumeration.nextElement();
                    l = deleteFile(portalsiteview, s4);
                }

            }
        }
        return l;
    }

    public static long listFiles(PortalSiteView portalsiteview, String s,
            StringBuffer stringbuffer) {
        if ((Portal.centrascopeDebug & 2) != 0) {
            TextUtils.debugPrint("LISTING=" + s);
        }
        return doFileOperation(portalsiteview, s, "list", new StringBuffer(),
                stringbuffer, "");
    }

    public static long deleteGroup(String s, String s1) {
        PortalSiteView portalsiteview = (PortalSiteView) Portal
                .getSiteViewForServerID(s);
        long l = 0L;
        if (portalsiteview != null) {
            l = deleteFile(portalsiteview, "/groups/" + s1 + ".mg");
            if (l == 200L) {
                updateStaticPagesSiteView(portalsiteview);
            }
        }
        if ((Portal.centrascopeDebug & 2) != 0) {
            TextUtils.debugPrint("DELETED " + s + ":" + s1 + "=" + l);
        }
        return l;
    }

    public static String getPortalServerFromPath(String s) {
        File file = new File(s);
        File file1 = new File(file.getParent());
        File file2 = new File(file1.getParent());
        File file3 = new File(file2.getParent());
        if (!file3.getName().equalsIgnoreCase("portal")) {
            return "";
        } else {
            return file2.getName();
        }
    }

    public static long updateGroup(String s) {
        File file = new File(s);
        String s1 = file.getName();
        if (!s1.endsWith(".mg") && !s1.endsWith(".config")) {
            return 0L;
        }
        String s2 = getPortalServerFromPath(s);
        if (s2.length() == 0) {
            return 0L;
        }
        long l = 0L;
        PortalSiteView portalsiteview = (PortalSiteView) Portal
                .getSiteViewForServerID(s2);
        if (portalsiteview != null
                && portalsiteview.getProperty(PortalSiteView.pReadOnly)
                        .length() == 0) {
            StringBuffer stringbuffer = new StringBuffer();
            StringBuffer stringbuffer1 = new StringBuffer();
            try {
                String s3 = "/groups/" + s1;
                String s4 = Portal.getPortalSiteViewRootPath(s2) + s3;
                stringbuffer.append(FileUtils.readFile(s4));
                l = putFile(portalsiteview, s3, stringbuffer, stringbuffer1, 1);
            } catch (IOException ioexception) {
            }
        }
        return l;
    }

    public static void updateStaticPagesSiteView(
            PortalSiteView portalsiteview) {
        updateStaticPagesSiteView(portalsiteview, "");
    }

    public static void updateStaticPagesSiteView(
            PortalSiteView portalsiteview, String s) {
        if ((Portal.centrascopeDebug & 2) != 0) {
            TextUtils.debugPrint("UPDATING REMOTE STATIC PAGES=" + s);
        }
        String s1 = "/SiteView/cgi/go.exe/SiteView?page=remoteOp&account=administrator&operation=updateStaticPages";
        if (s.length() > 0) {
            s1 = s1 + "&group=" + CGI.getGroupIDRelative(s);
        }
        portalsiteview.sendURLToRemoteSiteView(s1, null);
    }

    public static long deleteFile(PortalSiteView portalsiteview, String s) {
        if ((Portal.centrascopeDebug & 2) != 0) {
            TextUtils.debugPrint("DELETING=" + s);
        }
        return doFileOperation(portalsiteview, s, "delete",
                new StringBuffer(), new StringBuffer(), "");
    }

    public static long putFile(PortalSiteView portalsiteview, String s,
            StringBuffer stringbuffer, StringBuffer stringbuffer1, int i) {
        if ((Portal.centrascopeDebug & 2) != 0) {
            TextUtils.debugPrint("PUTTING=" + s);
        }
        String s1 = "";
        if (i == 0) {
            s1 = "nobackup=true";
        }
        return doFileOperation(portalsiteview, s, "put", stringbuffer,
                stringbuffer1, s1);
    }

    private static long doFileOperation(PortalSiteView portalsiteview,
            String s, String s1, StringBuffer stringbuffer,
            StringBuffer stringbuffer1, String s2) {
        HashMap hashmap = MasterConfig.getMasterConfig();
        String s3 = portalsiteview.getProperty(PortalSiteView.pServer);
        String s4 = "http://";
        if (s3.indexOf("://") != -1) {
            s4 = "";
        }
        String s5 = s4 + s3 + "/SiteView/cgi/go.exe/SiteView";
        String s6 = portalsiteview.getProperty(PortalSiteView.pProxy);
        String s7 = portalsiteview.getProperty(PortalSiteView.pProxyUserName);
        String s8 = portalsiteview.getProperty(PortalSiteView.pProxyPassword);
        String s9 = portalsiteview.getProperty(PortalSiteView.pUserName);
        String s10 = portalsiteview.getProperty(PortalSiteView.pPassword);
        String s11 = portalsiteview.getProperty(PortalSiteView.pLoginAccount);
        String s12 = portalsiteview.getProperty(PortalSiteView.pTimeout);
        int i = 60000;
        if (s11.length() == 0) {
            s11 = "administrator";
        }
        if (s12.length() != 0) {
            i = TextUtils.toInt(s12) * 1000;
        }
        long l = -1L;
        if (TextUtils.getValue(hashmap, "_portalMaxData").length() > 0) {
            l = TextUtils.toLong(TextUtils.getValue(hashmap, "_portalMaxData"));
        }
        if (l < 1L) {
            l = 0x7a120L;
        }
        Array array = null;
        if (s1.equals("delete") || s1.equals("put")) {
            array = new Array();
            array.add("page=file");
            array.add("file=" + s);
            array.add("operation=" + s1);
            array.add("account=" + s11);
            array.add("data=" + stringbuffer);
            if (s2.length() > 0) {
                array.add(s2);
            }
        } else {
            s5 = s5 + "?page=file&account=" + s11 + "&file=" + s
                    + "&operation=" + s1;
            if (s2.length() > 0) {
                s5 = s5 + "&" + s2;
            }
        }
        SocketSession socketsession = SocketSession.getSession(null);
        long al[] = URLMonitor.checkURL(socketsession, s5, "", "", s6, s7, s8,
                array, s9, s10, "", stringbuffer1, l, "", 0, i, null);
        long l1 = al[0];
        socketsession.close();
        if (l1 == 200L) {
            String s13 = URLMonitor.getHTTPContent(stringbuffer1.toString())
                    .trim();
            if (s13.startsWith(START_TAG)) {
                s13 = s13.substring(START_TAG.length()).trim();
            }
            if (s13.endsWith(END_TAG)) {
                int j = s13.lastIndexOf(END_TAG);
                if (j >= 0) {
                    s13 = s13.substring(0, j).trim();
                }
            }
            stringbuffer1.setLength(0);
            stringbuffer1.append(s13);
        }
        return l1;
    }

    void printProgress(String s) {
        printProgress(s, traceStream);
    }

    static void printProgress(String s, PrintWriter printwriter) {
        if (printwriter != null) {
            printwriter.println(s + "<BR>");
            printwriter.flush();
        }
        LogManager.log("RunMonitor", s);
    }

    public static void main(String args[]) {
        PortalSync portalsync = new PortalSync();
        try {
            portalsync.sync();
        } catch (Exception exception) {
            System.out.println("GOT EXCEPTION=" + exception);
        }
    }

}