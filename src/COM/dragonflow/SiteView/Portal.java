/*
 * 
 * Created on 2005-2-16 16:17:12
 *
 * Portal.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Portal</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, DetectPortalChange, PortalSiteView, MonitorGroup,
// PortalQuery, SiteViewGroup, Scheduler, Platform,
// Machine, PortalFilter

public class Portal extends SiteViewObject {

    static final int NO_CENTRASCOPE_DEBUG = 0;

    static final int LOGPULLER_DEBUG = 1;

    static final int CONFIGURATION_SYNC_DEBUG = 2;

    static final int REMOTE_URL_DEBUG = 4;

    static int centrascopeDebug;

    public static String PORTAL_ID_SEPARATOR = ":";

    public static String PORTAL_DIRECTORY = "portal";

    public static String PORTAL_SERVERS_CONFIG_PATH;

    DetectPortalChange checkConfiguration;

    private static Portal portal = null;

    private static long queryConfigLastModified = 0L;

    private static Array queryCache = new Array();

    private static long viewConfigLastModified = 0L;

    private static Array viewCache = new Array();

    public Portal() {
        checkConfiguration = null;
    }

    public static boolean isPortalID(String s) {
        return false;
    }

    public static Portal getPortal() {
        if (portal == null) {
            portal = new Portal();
            portal.checkConfiguration = new DetectPortalChange();
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            portal.checkConfiguration.execute();
            int i = siteviewgroup.getSettingAsLong("_fileCheckFrequency", 120) * 1000;
            if (siteviewgroup.maintenanceScheduler != null) {
                siteviewgroup.maintenanceScheduler
                        .scheduleRepeatedPeriodicAction(
                                portal.checkConfiguration, i);
            }
        }
        return portal;
    }

    public static void signalReload() {
        getPortal().checkConfiguration.execute();
    }

    void loadAllPortalSiteViews() {
        Object obj = null;
        try {
            Array array = FrameFile.readFromFile(PORTAL_SERVERS_CONFIG_PATH);
            for (int i = 1; i < array.size(); i++) {
                HashMap hashmap = (HashMap) array.at(i);
                loadPortalSiteView(hashmap);
            }

        } catch (IOException ioexception) {
            LogManager.log("Error", "Could not load portal servers file: "
                    + PORTAL_SERVERS_CONFIG_PATH);
        }
    }

    void loadPortalSiteView(HashMap hashmap) {
        LogManager.log("RunMonitor", "loading SiteView server "
                + TextUtils.getValue(hashmap, "_id"));
        PortalSiteView portalsiteview = new PortalSiteView();
        portalsiteview.readFromHashMap(hashmap);
        portalsiteview.initialize(hashmap);
        addElement(portalsiteview);
    }

    public static String[] getIDComponents(String s) {
        return getIDComponents(s, false);
    }

    public static String[] getIDComponents(String s, boolean flag) {
        String s1 = "";
        String s2 = "";
        String s3 = "";
        if (!flag) {
            portalAssert(isPortalID(s),
                    "Portal.getIDComponents - accepts only portal IDs");
        }
        int i = s.indexOf(PORTAL_ID_SEPARATOR);
        if (i != -1) {
            s1 = s.substring(0, i);
            s2 = s.substring(i + 1);
            int j = s2.indexOf("/");
            if (j >= 0) {
                s3 = s2.substring(j + 1);
                s2 = s2.substring(0, j);
            }
        }
        String as[] = new String[3];
        as[0] = s1;
        as[1] = s2;
        as[2] = s3;
        return as;
    }

    public static String makePortalID(String s, String s1, String s2) {
        String s3 = "";
        if (s.length() > 0) {
            s3 = s + PORTAL_ID_SEPARATOR;
            if (s1.length() > 0) {
                s3 = s3 + s1;
                if (s2.length() > 0) {
                    s3 = s3 + "/" + s2;
                }
            }
        }
        return s3;
    }

    public static String getServerID(String s) {
        String as[] = getIDComponents(s);
        return as[0];
    }

    public static String getGroupID(String s) {
        String as[] = getIDComponents(s);
        return as[1];
    }

    public static String cleanPortalServerID(String s) {
        if (s.endsWith(":") && s.length() > 1) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String getPortalSiteViewRootPath(String s) {
        return getPortalRootPath() + File.separator + s;
    }

    public static String getPortalRootPath() {
        return Platform.getRoot() + File.separator + PORTAL_DIRECTORY;
    }

    public static SiteViewObject getSiteViewForID(String s) {
        if (s.length() == 0 || !isPortalID(s)) {
            return SiteViewGroup.currentSiteView();
        } else {
            String s1 = getServerID(s);
            return getSiteViewForServerID(s1);
        }
    }

    public static SiteViewObject getSiteViewForServerID(String s) {
        s = cleanPortalServerID(s);
        return getPortal().getElementByID(s);
    }

    public static PortalSiteView getPortalSiteViewFromMachineID(String s) {
        PortalSiteView portalsiteview = null;
        if (Machine.isPortalMachineID(s)) {
            String s1 = Machine.getServerIDFromMachineID(s);
            portalsiteview = (PortalSiteView) getSiteViewForID(s1);
        }
        return portalsiteview;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     */
    public static void addTemplateConfigFiles(Array array) {
        Enumeration enumeration = getPortal().getElements();
        while (enumeration.hasMoreElements()) {
            PortalSiteView portalsiteview = (PortalSiteView) enumeration
                    .nextElement();
            String s = getPortalSiteViewRootPath(portalsiteview
                    .getProperty(PortalSiteView.pID))
                    + "/groups/" + TEMPLATES_FILE;
            File file = new File(s);
            if (file.exists()) {
                array.add(s);
            }
        } 
    }

    public String getSetting(String s) {
        String s1 = getProperty(s);
        if (s1.length() == 0) {
            return SiteViewGroup.currentSiteView().getSetting(s);
        } else {
            return s1;
        }
    }

    public SiteViewObject getElement(String s) {
        Object obj = null;
        portalAssert(isPortalID(s),
                "Portal.getElement - accepts only portal IDs: " + s);
        String as[] = getIDComponents(s);
        String s1 = as[0];
        String s2 = as[1];
        String s3 = as[2];
        if (s1.length() == 0) {
            obj = this;
        } else {
            PortalSiteView portalsiteview = (PortalSiteView) getElementByID(s1);
            if (portalsiteview != null) {
                if (s2.length() == 0) {
                    obj = portalsiteview;
                } else {
                    MonitorGroup monitorgroup = (MonitorGroup) portalsiteview
                            .getElementByID(s2);
                    if (monitorgroup != null) {
                        if (s3.length() == 0) {
                            obj = monitorgroup;
                        } else {
                            obj = monitorgroup.getElementByID(s3);
                        }
                    }
                }
            }
        }
        return ((SiteViewObject) (obj));
    }

    public static Array findObjects(String s, int i, HTTPRequest httprequest) {
        Array array = new Array();
        PortalQuery portalquery = new PortalQuery(PortalFilter
                .queryStringToMap(s), array, httprequest);
        portalquery.findObjects = i;
        portalquery.runQuery();
        return array;
    }

    public static Array findMonitors(String s, HTTPRequest httprequest) {
        return findObjects(s, 1, httprequest);
    }

    public static Array getQueries() {
        File file = new File(Platform.getRoot() + "/groups/query.config");
        long l = file.lastModified();
        if (queryConfigLastModified != l) {
            queryConfigLastModified = l;
            queryCache = new Array();
            refreshCache("query.config", file, queryCache);
        }
        return queryCache;
    }

    public static Array getEditableQueryArray() {
        return internalGetQueryArray(false);
    }

    public static Array getQueryArray() {
        return internalGetQueryArray(true);
    }

    public static Array internalGetQueryArray(boolean flag) {
        Array array = getQueries();
        Array array1 = new Array();
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.at(i);
            String s = TextUtils.getValue(hashmap, "_package");
            if (s.length() == 0) {
                array1.add(TextUtils.getValue(hashmap, "_id"));
                array1.add(TextUtils.getValue(hashmap, "_title"));
                continue;
            }
            if (flag) {
                array1.add(TextUtils.getValue(hashmap, "_id"));
                array1.add(TextUtils.getValue(hashmap, "_title") + " (in " + s
                        + " package)");
            }
        }

        return array1;
    }

    public static boolean isQueryID(String s) {
        return s.length() > 0 && s.indexOf("=") == -1;
    }

    public static HashMap getQuery(String s) {
        Array array = getQueries();
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.at(i);
            if (TextUtils.getValue(hashmap, "_id").equalsIgnoreCase(s)) {
                return hashmap;
            }
        }

        return null;
    }

    public static Array getViews() {
        File file = new File(Platform.getRoot() + "/groups/views.config");
        long l = file.lastModified();
        if (viewConfigLastModified != l) {
            viewConfigLastModified = l;
            viewCache = new Array();
            refreshCache("views.config", file, viewCache);
        }
        return viewCache;
    }

    public static Array getEditableViewArray() {
        return internalGetViewArray(false, true);
    }

    public static Array getViewArray() {
        return internalGetViewArray(true, true);
    }

    public static Array getTopViewArray() {
        return internalGetViewArray(true, false);
    }

    public static Array getParentViewArray() {
        return internalGetViewArray(true, true, false);
    }

    private static Array internalGetViewArray(boolean flag, boolean flag1) {
        Array array = getViews();
        Array array1 = new Array();
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.at(i);
            if (!flag1 && TextUtils.getValue(hashmap, "_parent").length() > 0) {
                continue;
            }
            String s = TextUtils.getValue(hashmap, "_package");
            if (s.length() == 0) {
                array1.add(TextUtils.getValue(hashmap, "_id"));
                array1.add(TextUtils.getValue(hashmap, "_title"));
                continue;
            }
            if (flag) {
                array1.add(TextUtils.getValue(hashmap, "_id"));
                array1.add(TextUtils.getValue(hashmap, "_title") + " (in " + s
                        + " package)");
            }
        }

        return array1;
    }

    private static Array internalGetViewArray(boolean flag, boolean flag1,
            boolean flag2) {
        Array array = getViews();
        Array array1 = new Array();
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.at(i);
            if (!flag1 && TextUtils.getValue(hashmap, "_parent").length() > 0) {
                continue;
            }
            String s = TextUtils.getValue(hashmap, "_package");
            if (s.length() == 0) {
                array1.add(TextUtils.getValue(hashmap, "_id"));
                array1.add(TextUtils.getValue(hashmap, "_title"));
                continue;
            }
            if (!flag) {
                continue;
            }
            String s1 = TextUtils.getValue(hashmap, "_parentID");
            String s2 = TextUtils.getValue(hashmap, "_id");
            if (s1.equalsIgnoreCase("") && !s2.endsWith("-")) {
                array1.add(TextUtils.getValue(hashmap, "_id"));
                array1.add(TextUtils.getValue(hashmap, "_title") + " (in " + s
                        + " package)");
            }
        }

        return array1;
    }

    public static HashMap getView(String s) {
        Array array = getViews();
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.at(i);
            if (TextUtils.getValue(hashmap, "_id").equalsIgnoreCase(s)) {
                return hashmap;
            }
            if (TextUtils.getValue(hashmap, "_title").equalsIgnoreCase(s)) {
                return hashmap;
            }
        }

        return null;
    }

    public static Array getEditableViewContentsArray(String s, boolean flag)
            throws IOException {
        Array array = new Array();
        if (flag) {
            array.add("");
            array.add("");
        }
        File file = new File(Platform.getRoot() + "/templates.view/");
        String as[] = file.list();
        if (as != null) {
            for (int i = 0; i < as.length; i++) {
                if (TextUtils.match(as[i], s)) {
                    array.add(as[i]);
                    array.add(as[i]);
                }
            }

        }
        return array;
    }

    public static String getViewContent(String s, HTTPRequest httprequest) {
        String s1 = "";
        String s2 = Platform.getUsedDirectoryPath("templates.view", httprequest
                .getAccount())
                + File.separator + s;
        try {
            s1 = FileUtils.readFile(s2).toString();
            s1 = TextUtils.replaceString(s1, "account=administrator",
                    "account=" + httprequest.getAccount());
            s1 = TextUtils.replaceString(s1,
                    "name=\"account\" value=\"administrator\"",
                    "name=\"account\" value=\"" + httprequest.getAccount()
                            + "\"");
        } catch (IOException ioexception) {
            LogManager.log("Error", "Could not read view file " + s2);
        }
        return s1;
    }

    private static void refreshCache(String s, File file, Array array) {
        try {
            Array array1 = FrameFile.readFromFile(file.getAbsolutePath());
            for (int i = 1; i < array1.size(); i++) {
                HashMap hashmap = (HashMap) array1.at(i);
                array.add(hashmap);
            }

            readExtraItems(s, array);
        } catch (IOException ioexception) {
            LogManager.log("Error", "Error reading file: "
                    + ioexception.getMessage());
        }
    }

    public static void readExtraItems(String s, Array array) throws IOException {
        File file = new File(Platform.getRoot() + File.separator
                + "templates.view");
        if (!file.exists()) {
            return;
        }
        String as[] = file.list();
        if (as == null) {
            return;
        }
        for (int i = 0; i < as.length; i++) {
            File file1 = new File(file, as[i]);
            if (!file1.isDirectory()) {
                continue;
            }
            File file2 = new File(file1, s);
            if (!file2.exists()) {
                continue;
            }
            Array array1 = FrameFile.readFromFile(file2.getAbsolutePath());
            for (int j = 0; j < array1.size(); j++) {
                HashMap hashmap = (HashMap) array1.at(j);
                String s1 = TextUtils.getValue(hashmap, "_id");
                s1 = file1.getName() + "-" + s1;
                hashmap.put("_id", s1);
                hashmap.put("_package", as[i]);
                array.add(hashmap);
            }

        }

    }

    private static void portalAssert(boolean flag, String s) {
        if (!flag) {
            System.out.println("ASSERT FAILED: " + s);
            LogManager.log("Error", "ASSERT FAILED: " + s);
            TextUtils.debugPrintStackTrace();
        }
    }

    public static void main(String args[]) {
        getPortal();
        Array array = Platform.getProcesses("remote:10@pete:");
        for (int i = 0; i < array.size(); i++) {
            System.out.println("PROCESS=" + array.at(i));
        }

        System.exit(0);
    }

    static {
        centrascopeDebug = 0;
        StringProperty astringproperty[] = new StringProperty[0];
        addProperties("COM.dragonflow.SiteView.Portal", astringproperty);
        String s = System.getProperty("CentraScope.debug");
        if (s != null) {
            centrascopeDebug = TextUtils.toInt(s);
            System.out.println("centrascopeDebug=" + centrascopeDebug);
        }
        PORTAL_SERVERS_CONFIG_PATH = Platform.getRoot() + File.separator
                + "groups" + File.separator + "portal.config";
    }
}