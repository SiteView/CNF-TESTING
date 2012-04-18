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

import java.util.Vector;

import COM.dragonflow.SiteView.CompareSlot;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public abstract class machineChooserPage extends COM.dragonflow.Page.CGI {

    public machineChooserPage() {
    }

    java.util.Vector getLocalServers() {
        java.util.Vector vector = null;
        if (!isPortalServerRequest()) {
            vector = COM.dragonflow.SiteView.Platform.getServers();
        } else {
            COM.dragonflow.SiteView.PortalSiteView portalsiteview = (COM.dragonflow.SiteView.PortalSiteView) getSiteView();
            if (portalsiteview != null) {
                String s = "/SiteView/cgi/go.exe/SiteView?page=remoteOp&operation=getServers&account=administrator";
                Array array = portalsiteview.sendURLToRemoteSiteView(s,
                        null);
                vector = new Vector();
                vector.addElement("this server");
                vector.addElement("this server");
                for (int i = 1; i < array.size(); i++) {
                    String s1 = (String) array.get(i);
                    s1 = s1.trim();
                    String s2 = COM.dragonflow.SiteView.Machine
                            .getFullMachineID(s1, request);
                    vector.addElement(s2);
                    vector.addElement(s1);
                }

            }
        }
        return vector;
    }

    java.util.Vector addNTSSHServers(java.util.Vector vector, String s)
            throws java.io.IOException {
        Array array = readMachines(s);
        Sorting.sort(array, new CompareSlot("_name",
                COM.dragonflow.SiteView.CompareSlot.DIRECTION_LESS));
        boolean flag = s.indexOf("NT") == -1;
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.get(i);
            String s1 = "";
            s1 = COM.dragonflow.SiteView.Machine.getFullMachineID(
                    COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_host"),
                    request);
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(
                    hashmap, "_name");
            if (s2.length() == 0) {
                s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_host");
            }
            if (COM.dragonflow.SiteView.Machine.isNTSSH(s1)) {
                vector.addElement(s1);
                vector.addElement(s2);
            }
        }

        return vector;
    }

    public java.util.Vector addServers(java.util.Vector vector,
            String s) throws java.io.IOException {
        Array array = readMachines(s);
        Sorting.sort(array, new CompareSlot("_name",
                COM.dragonflow.SiteView.CompareSlot.DIRECTION_LESS));
        boolean flag = s.indexOf("NT") == -1;
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.get(i);
            String s1 = "";
            if (flag) {
                s1 = COM.dragonflow.SiteView.Machine.getFullMachineID(
                        COM.dragonflow.SiteView.Machine.REMOTE_PREFIX
                                + COM.dragonflow.Utils.TextUtils.getValue(
                                        hashmap, "_id"), request);
            } else {
                s1 = COM.dragonflow.SiteView.Machine.getFullMachineID(
                        COM.dragonflow.Utils.TextUtils
                                .getValue(hashmap, "_host"), request);
            }
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(
                    hashmap, "_name");
            if (s2.length() == 0) {
                s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_host");
            }
            vector.addElement(s1);
            vector.addElement(s2);
        }

        return vector;
    }
}
