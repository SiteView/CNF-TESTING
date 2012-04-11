/*
 * 
 * Created on 2005-2-16 16:49:34
 *
 * ServerMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ServerMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Hashtable;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.ServerProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, PortalSiteView, SiteViewGroup, IServerPropMonitor,
// Platform, Machine, Portal

public abstract class ServerMonitor extends AtomicMonitor implements Runnable,
        IServerPropMonitor {

    public static ServerProperty pMachineName;

    static Hashtable remotes = new Hashtable();

    static int remoteNT = 0;

    static int remoteUnix = 0;

    int os;

    public ServerMonitor() {
        os = Platform.getLocalPlatform();
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        String s = getProperty("_machineUsername");
        if (s.length() > 0) {
            String s1 = getProperty("_machinePassword");
            String s2 = getProperty(pMachineName);
            Machine.addNTMachineLogin(s2, s, s1);
        }
    }

    protected void startMonitor() {
        addRemote(this);
        super.startMonitor();
    }

    protected void stopMonitor() {
        super.stopMonitor();
        removeRemote(this);
    }

    public static int remoteNTServers() {
        return remoteNT;
    }

    public static int remoteUnixServers() {
        return remoteUnix;
    }

    public static int totalRemoteServers() {
        return remoteNT + remoteUnix;
    }

    public static boolean remoteExists(String s) {
        s = s.toUpperCase();
        return remotes.get(s) != null;
    }

    public static synchronized void removeRemote(
            IServerPropMonitor iserverpropmonitor) {
        String s = ((AtomicMonitor) iserverpropmonitor).getProperty(
                iserverpropmonitor.getServerProperty()).toUpperCase();
        if (s.length() == 0) {
            return;
        }
        if (((AtomicMonitor) iserverpropmonitor).getProperty(pDisabled)
                .length() > 0) {
            return;
        }
        Integer integer = (Integer) remotes.get(s);
        if (integer != null) {
            int i = integer.intValue();
            if (i <= 1) {
                if (s.startsWith("\\")) {
                    remoteNT--;
                } else {
                    remoteUnix--;
                }
                remotes.remove(s);
            } else {
                remotes.put(s, new Integer(i--));
            }
        }
    }

    public static synchronized void addRemote(
            IServerPropMonitor iserverpropmonitor) {
        String s = ((AtomicMonitor) iserverpropmonitor).getProperty(
                iserverpropmonitor.getServerProperty()).toUpperCase();
        if (s.length() == 0) {
            return;
        }
        if (((AtomicMonitor) iserverpropmonitor).getProperty(pDisabled)
                .length() > 0) {
            return;
        }
        Integer integer = (Integer) remotes.get(s);
        int i = 0;
        if (integer != null) {
            i = integer.intValue();
        } else if (s.startsWith("\\")) {
            remoteNT++;
        } else {
            remoteUnix++;
        }
        remotes.put(s, new Integer(i++));
    }

    public String getHostname() {
        return Machine.getMachineHost(getProperty(pMachineName));
    }

    public int getPlatform() {
        return os;
    }

    public void preprocessValuesTable(HashMap hashmap) {
        String s = "_machine";
        String s1 = (String) hashmap.get(s);
        if (s1 != null) {
            StringProperty stringproperty = getPropertyObject(s);
            if (stringproperty != null) {
                String s2 = TextUtils.getValue(hashmap, "__portalServerID");
                String s3 = s1;
                if (s2.length() > 0) {
                    s3 = s3 + "@" + s2;
                    if (!s3.endsWith(":")) {
                        s3 = s3 + ":";
                    }
                }
                os = Machine.getOS(s3);
                valuesTable.add(stringproperty, s1);
            }
            hashmap.remove(s);
        }
        super.preprocessValuesTable(hashmap);
    }

    public boolean remoteCommandLineAllowed() {
        return true;
    }

    static String machineNameFromID(String s) {
        if (Platform.isNTRemote(s)) {
            return Machine.getMachineFromMachineID(s);
        }
        if (Platform.isCommandLineRemote(s)) {
            return Machine.getMachineName(s);
        } else {
            return Machine.getMachineFromMachineID(s);
        }
    }

    public String defaultTitle() {
        String s = super.defaultTitle();
        String s1 = getProperty(pMachineName);
        if (s1.length() > 0) {
            s1 = getFullMachineID(this, s1);
            s = s + " on " + machineNameFromID(s1);
        }
        return s;
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        String s1 = super.verify(stringproperty, s, httprequest, hashmap);
        if (stringproperty == pMachineName) {
            String s2 = getFullID();
            SiteViewObject siteviewobject = Portal.getSiteViewForID(s2);
            if (siteviewobject instanceof PortalSiteView) {
                PortalSiteView portalsiteview = (PortalSiteView) siteviewobject;
                HashMap hashmap1 = portalsiteview.getMasterConfig();
                if (!s.equals(TextUtils.getValue(hashmap1, "_defaultMachine"))) {
                    hashmap1.put("_defaultMachine", s);
                    portalsiteview.saveMasterConfig();
                }
            } else if (siteviewobject instanceof SiteViewGroup) {
                SiteViewGroup siteviewgroup = SiteViewGroup
                        .currentSiteView();
                if (!s.equals(siteviewgroup.getProperty("_defaultMachine"))) {
                    siteviewgroup.setProperty("_defaultMachine", s);
                    siteviewgroup.saveSettings();
                }
            }
        }
        return s1;
    }

    public static String getFullMachineID(AtomicMonitor atomicmonitor, String s) {
        String s1 = atomicmonitor.getFullID();
        SiteViewObject siteviewobject = Portal.getSiteViewForID(s1);
        if (siteviewobject instanceof PortalSiteView) {
            s = s + "@" + Portal.getServerID(s1) + ":";
        }
        return s;
    }

    public void setProperty(StringProperty stringproperty, String s)
            throws NullPointerException {
        if (stringproperty == pMachineName) {
            String s1 = getFullMachineID(this, s);
            os = Machine.getOS(s1);
        }
        super.setProperty(stringproperty, s);
    }

    public StringProperty getServerProperty() {
        return pMachineName;
    }

    static {
        pMachineName = new ServerProperty("_machine", "");
        if (Platform.isWindows() || SiteViewGroup.allowRemoteCommandLine()) {
            pMachineName.setParameterOptions(true, 1, false);
            pMachineName.setDisplayText("Server", "the server to monitor");
        }
        StringProperty astringproperty[] = { pMachineName };
        addProperties("COM.dragonflow.SiteView.ServerMonitor", astringproperty);
    }
}
