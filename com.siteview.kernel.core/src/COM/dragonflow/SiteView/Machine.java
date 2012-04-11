/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * Machine.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Machine</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SSH.SSHClientBase;
import COM.dragonflow.Utils.RemoteCommandLine;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, MasterConfig, Platform, Portal,
// PortalSiteView, OSAdapter

public class Machine extends SiteViewObject {

    public static StringProperty pID;

    public static StringProperty pHost;

    public static StringProperty pMethod;

    public static StringProperty pSshAuthMethod;

    public static StringProperty pLogin;

    public static StringProperty pPassword;

    public static StringProperty pOS;

    public static StringProperty pName;

    public static StringProperty pTestStatus = new StringProperty("_status",
            "unknown");

    public static StringProperty pLoginPrompt;

    public static StringProperty pSecondaryPrompt;

    public static StringProperty pSecondaryResponse;

    public static StringProperty pPasswordPrompt;

    public static StringProperty pPrompt;

    public static StringProperty pTimeout;

    public static StringProperty pTrace;

    public static StringProperty pInitShellEnvironment;

    public static StringProperty pLocalUser;

    public static StringProperty pEncoding;

    public static StringProperty pSSHConnectionCaching;

    public static StringProperty pSSHConnectionsLimit;

    public static StringProperty pSSHClient;

    public static StringProperty pSshKeyFile;

    public static StringProperty pSSHForceVersion2;

    public static StringProperty pSSHCommandLine;

    public static StringProperty pSSHPort;

    public String owner;

    private static HashMap machineObjectTable = null;

    private static Object registerMachinesLock = new Object();

    private static Object machineTableLock = new Object();

    private static HashMap NTMachineObjectTable = null;

    private static Object registerNTMachinesLock = new Object();

    private static Object ntMachineTableLock = new Object();

    public static boolean registeredMachines = false;

    public static String REMOTE_PREFIX = "remote:";

    public static boolean traceAllMachines = false;

    public static String currentUser = null;

    public static String OS_NAME[];

    public static final String LOCAL_SITEVIEW = "$SiteViewServer";

    static HashMap remoteCommandLineClasses = null;

    private static Object remoteCommandLineClassesLock = new Object();

    public Machine() {
        owner = null;
    }

    public static HashMap getMachineTable() {
        if (machineObjectTable == null) {
            synchronized (registerMachinesLock) {
                if (machineObjectTable == null) {
                    HashMap hashmap = MasterConfig.getMasterConfig();
                    registerMachines(hashmap.values("_remoteMachine"));
                }
            }
        }
        return machineObjectTable;
    }

    public static void addNTMachineLogin(String s, String s1, String s2) {
        HashMap hashmap = getNTMachineTable();
        Machine machine = (Machine) hashmap.get(s.toLowerCase());
        if (machine == null) {
            HashMap hashmap1 = new HashMap();
            hashmap1.put("_host", s);
            hashmap1.put("_login", s1);
            hashmap1.put("_password", s2);
            Machine machine1 = new Machine();
            machine1.readFromHashMap(hashmap1);
            machine1.initialize(hashmap1);
            NTMachineObjectTable.put(machine1.getProperty(pHost).toLowerCase(),
                    machine1);
        }
    }

    static HashMap getNTMachineTable() {
        if (NTMachineObjectTable == null) {
            synchronized (registerNTMachinesLock) {
                if (NTMachineObjectTable == null) {
                    registerNTMachines(null);
                }
            }
        }
        return NTMachineObjectTable;
    }

    public static Machine getNTMachine(String s) {
        HashMap hashmap = getNTMachineTable();
        return (Machine) hashmap.get(s.toLowerCase());
    }

    public static String getCurrentUser() {
        if (currentUser == null) {
            currentUser = Platform.currentUser();
        }
        return currentUser;
    }

    public static void registerMachines(Enumeration enumeration) {
        registerMachines(enumeration, null);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     */
    public static void registerNTMachines(String s) {
        synchronized (ntMachineTableLock) {
            HashMap hashmap = MasterConfig.getMasterConfig();
            HashMap hashmap1;
            if (NTMachineObjectTable == null) {
                hashmap1 = new HashMap();
            } else {
                hashmap1 = NTMachineObjectTable;
            }
            HashMap hashmap2 = (HashMap) hashmap1.clone();
            String s2;
            for (Enumeration enumeration = hashmap.values("_remoteNTMachine"); enumeration
                    .hasMoreElements(); hashmap2.remove(s2)) {
                String s1 = (String) enumeration.nextElement();
                HashMap hashmap3 = TextUtils.stringToHashMap(s1);
                Machine machine = new Machine();
                machine.readFromHashMap(hashmap3);
                if (s != null) {
                    machine.owner = s;
                }
                machine.initialize(hashmap3);
                s2 = machine.getProperty(pHost).toLowerCase();
                hashmap1.put(s2, machine);
            }

            Enumeration enumeration1 = hashmap2.keys();
            while (enumeration1.hasMoreElements()) {
                Object obj1 = enumeration1.nextElement();
                if (((Machine) hashmap1.get(obj1)).owner == s) {
                    hashmap1.remove(obj1);
                }
            } 
            
            if (NTMachineObjectTable == null) {
                NTMachineObjectTable = hashmap1;
            }
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param enumeration
     * @param s
     */
    public static void registerMachines(Enumeration enumeration, String s) {
        synchronized (machineTableLock) {
            HashMap hashmap;
            if (machineObjectTable == null) {
                hashmap = new HashMap();
            } else {
                hashmap = machineObjectTable;
            }
            Array array = createMachines(enumeration);
            HashMap hashmap1 = (HashMap) hashmap.clone();
            for (int i = 0; i < array.size(); i++) {
                Machine machine = (Machine) array.at(i);
                machine.owner = s;
                String s1 = machine.getProperty(pID);
                hashmap1.remove(s1);
                hashmap.put(s1, machine);
            }

            Enumeration enumeration1 = hashmap1.keys();
            while (enumeration1.hasMoreElements()) {
                Object obj1 = enumeration1.nextElement();
                if (((Machine) hashmap.get(obj1)).owner == s) {
                    hashmap.remove(obj1);
                }
            }
            
            if (machineObjectTable == null) {
                machineObjectTable = hashmap;
            }
            registerNTMachines(s);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param enumeration
     * @return
     */
    private static Array createMachines(Enumeration enumeration) {
        Array array = new Array();
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            HashMap hashmap = TextUtils.stringToHashMap(s);
            if (hashmap.get("_host") != null) {
                Machine machine = createMachine(hashmap);
                if (traceAllMachines) {
                    machine.setProperty(pTrace, "true");
                }
                if (machine.getProperty(pLocalUser).length() == 0) {
                    machine.setProperty(pLocalUser, getCurrentUser());
                }
                array.add(machine);
            }
        } 
        return array;
    }

    public static Machine createMachine(HashMap hashmap) {
        Machine machine = new Machine();
        machine.readFromHashMap(hashmap);
        machine.initialize(hashmap);
        return machine;
    }

    public static Array getNTAllowedMethods() {
        Array array = new Array();
        if (Platform.isWindows()) {
            array.add("NetBIOS");
            array.add("NetBIOS");
        }
        array.add("ssh");
        array.add("SSH");
        return array;
    }

    public static Machine getMachine(String s) {
        if (Platform.isCommandLineRemote(s)) {
            if (isPortalMachineID(s)) {
                PortalSiteView portalsiteview = Portal
                        .getPortalSiteViewFromMachineID(s);
                if (portalsiteview != null) {
                    String s1 = s.substring(REMOTE_PREFIX.length());
                    s1 = getMachineFromMachineID(s1);
                    HashMap hashmap1 = portalsiteview.getMachineEntry(s1);
                    if (hashmap1 != null) {
                        return createMachine(hashmap1);
                    }
                }
            } else {
                HashMap hashmap = getMachineTable();
                String s2 = s.substring(REMOTE_PREFIX.length());
                return (Machine) hashmap.get(s2);
            }
        }
        return null;
    }

    public static String getMachinePathSeparator(String s) {
        return Platform.pathSeparator(getOS(s));
    }

    public static Machine getMachineByHost(String s) {
        HashMap hashmap = getMachineTable();
        for (Enumeration enumeration = hashmap.elements(); enumeration
                .hasMoreElements();) {
            Machine machine = (Machine) enumeration.nextElement();
            if (machine.getProperty(pHost).equalsIgnoreCase(s)) {
                return machine;
            }
        }

        return null;
    }

    public static String getNTMachineHost(String s) {
        Machine machine = getNTMachine(s);
        if (machine != null) {
            return machine.getProperty(pHost);
        } else {
            return s;
        }
    }

    public static Machine getMachineByName(String s) {
        HashMap hashmap = getMachineTable();
        for (Enumeration enumeration = hashmap.elements(); enumeration
                .hasMoreElements();) {
            Machine machine = (Machine) enumeration.nextElement();
            if (machine.getProperty(pName).equalsIgnoreCase(s)) {
                return machine;
            }
        }

        return null;
    }

    public static String getMachineName(String s) {
        Machine machine = getMachine(s);
        if (machine != null) {
            return machine.getProperty(pName);
        } else {
            return s;
        }
    }

    public static String getMachineHost(String s) {
        Machine machine = getMachine(s);
        if (machine != null) {
            return machine.getProperty(pHost);
        } else {
            return s;
        }
    }

    public String getProperty(StringProperty stringproperty) {
        if (stringproperty == pLoginPrompt) {
            int i = stringToOS(super.getProperty(pOS));
            if (super.getProperty(pLoginPrompt).length() == 0
                    && Platform.isUnix(i)) {
                return "login:";
            }
        } else if (stringproperty == pPasswordPrompt) {
            int j = stringToOS(super.getProperty(pOS));
            if (super.getProperty(pPasswordPrompt).length() == 0
                    && Platform.isUnix(j)) {
                return "assword:";
            }
        } else if (stringproperty == pPrompt) {
            int k = stringToOS(super.getProperty(pOS));
            if (super.getProperty(pPrompt).length() == 0 && Platform.isUnix(k)) {
                return "#";
            }
        } else if (stringproperty == pName
                && super.getProperty(pName).length() == 0) {
            return super.getProperty(pHost);
        }
        return super.getProperty(stringproperty);
    }

    public static Array getAllowedMethods() {
        Array array = new Array();
        Array array1 = getRemoteCommandLineClasses();
        for (int i = 0; i < array1.size(); i++) {
            Class class1 = (Class) array1.at(i);
            RemoteCommandLine remotecommandline = null;
            try {
                remotecommandline = (RemoteCommandLine) class1.newInstance();
            } catch (Exception e) {
            }
            if (remotecommandline != null
                    && (!Platform.isSGI() || !remotecommandline.getMethodName()
                            .equals("telnet"))) {
                array.add(remotecommandline.getMethodName());
                array.add(remotecommandline.getMethodDisplayName());
            }
        }

        return array;
    }

    public static Array getAllowedSshConnectionMethods() {
        String as[] = { "java", "Internal Java Libraries", "plink",
                Platform.isWindows() ? "Plink" : "External SSH Client" };
        return new Array(as);
    }

    public static Array getAllowedSshAuthMethods() {
        String as[] = { "password", "Password", "keyfile", "Key File" };
        return new Array(as);
    }

    public static Array getAllowedOSs() {
        Array array = new Array();
        array = OSAdapter.getOSs(array);
        return array;
    }

    public static int stringToOS(String s) {
        for (int i = 1; i < 8; i++) {
            if (s.equals(OS_NAME[i])) {
                return i;
            }
        }

        return 8;
    }

    public static String osToString(int i) {
        if (i >= 1 && i < 8) {
            return OS_NAME[i];
        } else {
            return "unknown";
        }
    }

    public static int getOS(String s) {
        if (s.length() == 0) {
            return Platform.getOs();
        }
        if (s.startsWith("\\\\")) {
            return 1;
        }
        Machine machine = getMachine(s);
        if (machine != null) {
            return stringToOS(machine.getProperty(pOS));
        }
        machine = getNTMachine(s);
        if (machine != null) {
            return stringToOS(machine.getProperty(pOS));
        } else {
            return Platform.getOs();
        }
    }

    public static String getOSName(String s) {
        if (s.length() == 0) {
            return OS_NAME[Platform.getOs()];
        }
        if (s.startsWith("\\\\")) {
            return OS_NAME[1];
        }
        Machine machine = getMachine(s);
        if (machine != null) {
            return machine.getProperty(pOS);
        }
        machine = getNTMachine(s);
        if (machine != null) {
            return machine.getProperty(pOS);
        } else {
            return OS_NAME[Platform.getOs()];
        }
    }

    public static OSAdapter getAdapter(String s) {
        return OSAdapter.getOSAdapter(getOSName(s));
    }

    public static String getCommandString(String s, String s1) {
        return getCommandString(s, s1, null);
    }

    public static String getCommandString(String s, String s1, HashMap hashmap) {
        String s2 = "";
        if (isPortalMachineID(s1)) {
            String s3 = getMachineFromMachineID(s1);
            if (s3.length() > 0 && !s3.startsWith("\\\\")) {
                s2 = "page=remoteOp&operation=run&command=" + s + "&machineID="
                        + s3;
            }
        } else {
            OSAdapter osadapter = getAdapter(s1);
            if (osadapter != null) {
                s2 = osadapter.getCommandString(s, hashmap);
            }
        }
        return s2;
    }

    public static String getCommandSetting(String s, String s1, String s2) {
        String s3 = "";
        OSAdapter osadapter = getAdapter(s);
        if (osadapter != null) {
            s3 = osadapter.getCommandSetting(s1, s2);
        }
        return s3;
    }

    public static String getServerIDFromMachineID(String s) {
        int i = s.lastIndexOf("@");
        String s1 = "";
        if (i != -1) {
            s1 = s.substring(i + 1);
            if (!s1.endsWith(":")) {
                s1 = s1 + ":";
            }
        }
        return s1;
    }

    public static String getMachineFromMachineID(String s) {
        int i = s.lastIndexOf("@");
        if (i != -1) {
            s = s.substring(0, i);
        }
        return s;
    }

    public static boolean isPortalMachineID(String s) {
        return Platform.isPortal() && s.indexOf("@") != -1;
    }

    public static String getFullMachineID(String s, HTTPRequest httprequest) {
        if (CGI.isPortalServerRequest(httprequest)) {
            String s1 = httprequest.getValue("group");
            String s2 = httprequest.getPortalServer();
            if (s2.length() == 0) {
                s2 = Portal.getServerID(s1);
            }
            s = s + "@" + s2;
        }
        return s;
    }

    public static boolean isNTSSH(String s) {
        if (s.startsWith("\\\\")) {
            s = s.substring(2);
        }
        Machine machine = getNTMachine(s);
        return machine != null && machine.getSetting("_os").equals("NT")
                && machine.getSetting("_method").equals("ssh");
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param machine
     * @return
     */
    public static RemoteCommandLine getRemoteCommandLine(Machine machine) {
        if (Machine.remoteCommandLineClasses == null) {
            synchronized (Machine.remoteCommandLineClassesLock) {
                if (Machine.remoteCommandLineClasses == null) {
                    HashMap hashmap = new HashMap();
                    Array array = getRemoteCommandLineClasses();
                    for (int i = 0; i < array.size(); i++) {
                        Class cls = (Class) array.at(i);
                        RemoteCommandLine remoteCommandLine = null;
                        try {
                            remoteCommandLine = (RemoteCommandLine) cls
                                    .newInstance();
                        } catch (Exception e) {
                            /* empty */
                        }
                        if (remoteCommandLine != null) {
                            hashmap.add(remoteCommandLine.getMethodName(), cls);
                        }
                    }

                    Machine.remoteCommandLineClasses = hashmap;
                }
            }
        }

        Class cmdLineClass = (Class) Machine.remoteCommandLineClasses
                .get(machine.getProperty(pMethod));
        if (cmdLineClass != null) {
            try {
                return (RemoteCommandLine) cmdLineClass.newInstance();
            } catch (Exception e) {
                /* empty */
            }
        }
        return null;
    }

    public static Array getRemoteCommandLineClasses() {
        Array array = new Array();
        File file = new File(Platform.getRoot()
                + "/classes/COM/dragonflow/Utils");
        String as[] = file.list();
        for (int i = 0; i < as.length; i++) {
            if (!as[i].endsWith("CommandLine.class")) {
                continue;
            }
            int j = as[i].lastIndexOf(".class");
            String s = as[i].substring(0, j);
            if (s.equals("CommandLine") || s.equals("RemoteCommandLine")) {
                continue;
            }
            try {
                Class class1 = Class.forName("COM.dragonflow.Utils." + s);
                array.add(class1);
            } catch (Throwable throwable) {
                LogManager
                        .log(
                                "Error",
                                "Machine.getRemoteCommandLineClasses Utils directory handler failed with exception '"
                                        + throwable.toString()
                                        + "' while processing file '"
                                        + as[i]
                                        + "'");
            }
        }

        File file1 = new File(Platform.getRoot() + "/classes/CustomRemote");
        if (file1.exists()) {
            String as1[] = file1.list();
            for (int k = 0; k < as1.length; k++) {
                if (!as1[k].endsWith("CommandLine.class")) {
                    continue;
                }
                int l = as1[k].lastIndexOf(".class");
                String s1 = as1[k].substring(0, l);
                try {
                    Class class2 = Class.forName("CustomRemote." + s1);
                    array.add(class2);
                } catch (Throwable throwable1) {
                    LogManager
                            .log(
                                    "Error",
                                    "Machine.getRemoteCommandLineClasses CustomRemote directory handler failed with exception '"
                                            + throwable1.toString()
                                            + "' while processing file '"
                                            + as1[k] + "'");
                }
            }

        }
        return array;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param hostName
     * @return
     */
    public static boolean isLocalHostname(String hostName) {
        if (hostName.equalsIgnoreCase("$SiteViewServer")) {
            return true;
        }
        if (hostName.startsWith("\\\\")) {
            hostName = hostName.substring(2);
        }

        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            InetAddress addressByName = InetAddress.getByName(hostName);
            if (localAddress.equals(addressByName)) {
                return true;
            }

            byte abyte0[] = addressByName.getAddress();
            if (abyte0[0] == 127 && abyte0[1] == 0 && abyte0[2] == 0
                    && abyte0[3] == 1) {
                return true;
            }
        } catch (UnknownHostException e) {
            LogManager.log("Error", "Could not find IP Address for host: "
                    + e.getMessage());
        }
        return false;
    }

    public static boolean isNetBIOSFormattedHostname(String s) {
        return s.startsWith("\\\\");
    }

    public static void main(String args[]) {
    }

    static {
        OS_NAME = new String[9];
        pID = new StringProperty("_id");
        pHost = new StringProperty("_host");
        pLogin = new StringProperty("_login", "");
        pPassword = new StringProperty("_password", "");
        pName = new StringProperty("_name", "");
        pOS = new StringProperty("_os", "");
        pMethod = new StringProperty("_method", "telnet");
        pSshAuthMethod = new StringProperty("_sshAuthMethod", "password");
        pLoginPrompt = new StringProperty("_loginPrompt", "");
        pSecondaryPrompt = new StringProperty("_secondaryPrompt");
        pSecondaryResponse = new StringProperty("_secondaryResponse");
        pPasswordPrompt = new StringProperty("_passwordPrompt", "");
        pPrompt = new StringProperty("_prompt", "");
        pTimeout = new NumericProperty("_timeout", "60");
        pTrace = new StringProperty("_trace", "");
        pInitShellEnvironment = new StringProperty("_initShellEnvironment", "");
        pEncoding = new StringProperty("_encoding", "");
        pSSHConnectionCaching = new StringProperty("_disableCache", "");
        pSSHConnectionsLimit = new NumericProperty("_sshConnectionsLimit", "3");
        pSSHClient = new StringProperty("_sshClient", "");
        pSshKeyFile = new StringProperty("_keyFile",
                SSHClientBase.DEFAULT_KEY_FILE);
        pSSHForceVersion2 = new StringProperty("_version2", "");
        pSSHCommandLine = new StringProperty("_sshCommand", "");
        pSSHPort = new NumericProperty("_sshPort", "22");
        pLocalUser = new StringProperty("_localUser", "");
        StringProperty astringproperty[] = { pID, pHost, pLogin, pPassword,
                pName, pOS, pMethod, pSshAuthMethod, pLoginPrompt,
                pPasswordPrompt, pPrompt, pTrace, pInitShellEnvironment,
                pEncoding, pLocalUser, pTimeout, pSecondaryPrompt,
                pSecondaryResponse, pSSHConnectionCaching,
                pSSHConnectionsLimit, pSSHClient, pSshKeyFile,
                pSSHForceVersion2, pSSHCommandLine, pSSHPort };
        addProperties("COM.dragonflow.SiteView.Machine", astringproperty);
        OS_NAME[0] = "unknown";
        OS_NAME[1] = "NT";
        OS_NAME[2] = "Sun";
        OS_NAME[3] = "SGI";
        OS_NAME[4] = "Mac";
        OS_NAME[5] = "HP";
        OS_NAME[6] = "Linux";
        OS_NAME[8] = "Unix";
    }
}
