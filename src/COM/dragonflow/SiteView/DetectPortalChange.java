/*
 * 
 * Created on 2005-2-15 12:51:41
 *
 * DetectPortalChange.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>DetectPortalChange</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Action, PortalSiteView, MonitorGroup, Portal,
// Monitor

public class DetectPortalChange extends Action {

    public DetectPortalChange() {
        runType = 1;
    }

    public synchronized boolean execute() {
        checkPortals();
        for (Enumeration enumeration = Portal.getPortal().getElements(); enumeration
                .hasMoreElements();) {
            PortalSiteView portalsiteview = (PortalSiteView) enumeration
                    .nextElement();
            String s = Portal.getPortalSiteViewRootPath(portalsiteview
                    .getProperty(PortalSiteView.pID))
                    + "/groups/";
            File file = new File(s);
            String as[] = file.list();
            HashMap hashmap = new HashMap();
            Array array = new Array();
            Array array1 = new Array();
            Array array2 = new Array();
            if (as != null) {
                for (int i = 0; i < as.length; i++) {
                    if (as[i].endsWith(".mg")) {
                        int j = as[i].lastIndexOf(".mg");
                        String s1 = as[i].substring(0, j);
                        MonitorGroup monitorgroup1 = (MonitorGroup) portalsiteview
                                .getElement(s1);
                        hashmap.put(s1, "yes");
                        if (monitorgroup1 == null) {
                            array.add(s1);
                            continue;
                        }
                        File file3 = new File(s + s1 + ".mg");
                        String s7 = "" + file3.lastModified();
                        if (!monitorgroup1.getProperty("groupFileLastModified")
                                .equals(s7)) {
                            array2.add(s1);
                        }
                        continue;
                    }
                    if (as[i].equals("master.config")) {
                        File file1 = new File(s + as[i]);
                        if (file1.lastModified() != portalsiteview
                                .getMasterConfigLastModified()) {
                            portalsiteview.clearConfigCache();
                            portalsiteview.getMasterConfig();
                        }
                        continue;
                    }
                    if (!as[i].equals(TEMPLATES_FILE)) {
                        continue;
                    }
                    File file2 = new File(s + as[i]);
                    if (file2.lastModified() != portalsiteview
                            .getTemplatesConfigLastModified()) {
                        resetTemplateCache();
                    }
                }

            }
            Array array3 = portalsiteview.getGroups();
            for (int k = 0; k < array3.size(); k++) {
                MonitorGroup monitorgroup = (MonitorGroup) array3.at(k);
                String s6 = monitorgroup.getProperty(Monitor.pID);
                if (hashmap.get(s6) == null) {
                    array1.add(s6);
                }
            }

            for (int l = 0; l < array1.size(); l++) {
                String s2 = (String) array1.at(l);
                portalsiteview.removeGroup(s2);
            }

            for (int i1 = 0; i1 < array2.size(); i1++) {
                String s3 = (String) array2.at(i1);
                portalsiteview.removeGroup(s3);
            }

            for (int j1 = 0; j1 < array2.size(); j1++) {
                String s4 = (String) array2.at(j1);
                portalsiteview.loadGroup(s4);
            }

            int k1 = 0;
            while (k1 < array.size()) {
                String s5 = (String) array.at(k1);
                portalsiteview.loadGroup(s5);
                k1++;
            }
        }

        return true;
    }

    /**
     * CAUTION: Decompiled by hand.
     *
     */
    void checkPortals() {
        String s = Portal.PORTAL_SERVERS_CONFIG_PATH;
        boolean flag = false;
        try {
            Array array;
            if ((new File(s)).exists()) {
                array = FrameFile.readFromFile(s);
            } else {
                array = new Array();
            }
            HashMap hashmap = new HashMap();
            for (int i = 1; i < array.size(); i++) {
                HashMap hashmap1 = (HashMap) array.at(i);
                String s1 = TextUtils.getValue(hashmap1, "_id");
                String s2 = TextUtils.getValue(hashmap1, "_lastUpdate");
                hashmap.put(s1, "true");
                PortalSiteView portalsiteview2 = (PortalSiteView) Portal
                        .getPortal()
                        .getElement(s1 + Portal.PORTAL_ID_SEPARATOR);
                if (portalsiteview2 == null) {
                    Portal.getPortal().loadPortalSiteView(hashmap1);
                    flag = true;
                    continue;
                }
                String s3 = portalsiteview2.getProperty("_lastUpdate");
                if (!s3.equals(s2)) {
                    Portal.getPortal().removeElement(portalsiteview2);
                    Portal.getPortal().loadPortalSiteView(hashmap1);
                    flag = true;
                }
            }

            Enumeration enumeration = Portal.getPortal().getElements();
            Array array1 = new Array();
            while (enumeration.hasMoreElements()) {
                PortalSiteView portalsiteview = (PortalSiteView) enumeration
                        .nextElement();
                if (hashmap.get(portalsiteview.getProperty(pID)) == null) {
                    array1.add(portalsiteview);
                }
            } 
            
            for (int j = 0; j < array1.size(); j++) {
                PortalSiteView portalsiteview1 = (PortalSiteView) array1
                        .at(j);
                Portal.getPortal().removeElement(portalsiteview1);
            }

            if (flag) {
                initializeHTTPLocations();
            }
        } catch (Exception exception) {
            LogManager.log("Error", "Could not read portal config " + s + ": "
                    + exception.getMessage());
        }
    }

    public void initializeHTTPLocations() {
        HTTPUtils.locations = new Array();
        HTTPUtils.locationMap = new HashMap();
        String s;
        for (Enumeration enumeration = Portal.getPortal().getElements(); enumeration
                .hasMoreElements(); HTTPUtils.locationMap.add(HTTPUtils
                .getLocationID(s), s)) {
            PortalSiteView portalsiteview = (PortalSiteView) enumeration
                    .nextElement();
            s = portalsiteview.getProperty(PortalSiteView.pID)
                    + ":,"
                    + portalsiteview.getProperty(PortalSiteView.pTitle)
                    + ","
                    + portalsiteview.expandPartialURL("/scripts/get.exe")
                    + ","
                    + portalsiteview
                            .expandPartialURL("/scripts/png.exe/scripts")
                    + ","
                    + portalsiteview
                            .expandPartialURL("/scripts/trace.exe/scripts");
            HTTPUtils.locations.add(s);
        }

    }

    public String toString() {
        return "detect portal configuration change";
    }
}