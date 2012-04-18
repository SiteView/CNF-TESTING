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

import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;

import com.dragonflow.SiteView.CompareSlot;

// Referenced classes of package com.dragonflow.Page:
// CGI

public abstract class machineChooserPage extends com.dragonflow.Page.CGI {

    public machineChooserPage() {
    }

    java.util.Vector getLocalServers() {
        java.util.Vector vector = null;
        if (!isPortalServerRequest()) {
            vector = com.dragonflow.SiteView.Platform.getServers();
        } else {
            com.dragonflow.SiteView.PortalSiteView portalsiteview = (com.dragonflow.SiteView.PortalSiteView) getSiteView();
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
                    String s2 = com.dragonflow.SiteView.Machine
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
                com.dragonflow.SiteView.CompareSlot.DIRECTION_LESS));
        boolean flag = s.indexOf("NT") == -1;
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.get(i);
            String s1 = "";
            s1 = com.dragonflow.SiteView.Machine.getFullMachineID(
                    com.dragonflow.Utils.TextUtils.getValue(hashmap, "_host"),
                    request);
            String s2 = com.dragonflow.Utils.TextUtils.getValue(
                    hashmap, "_name");
            if (s2.length() == 0) {
                s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_host");
            }
            if (com.dragonflow.SiteView.Machine.isNTSSH(s1)) {
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
                com.dragonflow.SiteView.CompareSlot.DIRECTION_LESS));
        boolean flag = s.indexOf("NT") == -1;
        for (int i = 0; i < array.size(); i++) {
            HashMap hashmap = (HashMap) array.get(i);
            String s1 = "";
            if (flag) {
                s1 = com.dragonflow.SiteView.Machine.getFullMachineID(
                        com.dragonflow.SiteView.Machine.REMOTE_PREFIX
                                + com.dragonflow.Utils.TextUtils.getValue(
                                        hashmap, "_id"), request);
            } else {
                s1 = com.dragonflow.SiteView.Machine.getFullMachineID(
                        com.dragonflow.Utils.TextUtils
                                .getValue(hashmap, "_host"), request);
            }
            String s2 = com.dragonflow.Utils.TextUtils.getValue(
                    hashmap, "_name");
            if (s2.length() == 0) {
                s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_host");
            }
            vector.addElement(s1);
            vector.addElement(s2);
        }

        return vector;
    }
}
