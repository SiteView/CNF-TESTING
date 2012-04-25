
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Platform</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteViewException.SiteViewAvailabilityException;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;
import COM.dragonflow.StandardMonitor.DNSMonitor;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.LineReader;
import COM.dragonflow.Utils.LocaleUtils;
import COM.dragonflow.Utils.MailUtils;
import COM.dragonflow.Utils.Registry;
import COM.dragonflow.Utils.RemoteCommandLine;
import COM.dragonflow.Utils.SSHCommandLine;
import COM.dragonflow.Utils.TextUtils;
import SiteViewMain.UpdateConfig;

// Referenced classes of package COM.dragonflow.SiteView:
// PlatformNew, MasterConfig, Machine, Portal,
// AtomicMonitor, Monitor, OSAdapter

public class Platform {

    public Platform() {
    }

    public static String expiredName() {
        return getRoot() + "/groups/license.expired";
    }

    public static boolean isPortal() {
        return portalDirectoryExists;
    }

    public static void setIsPortal(boolean flag) {
        portalDirectoryExists = flag;
    }

    public static boolean hasPortalLicense(String s) {
        if (!isPortal())
            return false;
        s = s.toLowerCase();
        if (s.startsWith("sr"))
            return true;
        return s.length() == 0;
    }

    public static void sleep(long l) {
        if (l > 0L)
            try {
                Thread.currentThread();
                Thread.sleep(l);
            } catch (InterruptedException interruptedexception) {
            }
    }

    public static void enableUserAccess() {
        File file = new File(getRoot() + "/userhtml");
        if (!file.exists()) {
            file.mkdir();
            chmod(file, "rwx");
        }
        userAccessAllowed = (new File(getRoot() + File.separator + "userhtml"))
                .exists();
    }

    public static String hostFromURL(String s) {
        int i = s.indexOf("://");
        if (i == -1)
            return null;
        String s1 = s.substring(i + 3);
        int j = s1.indexOf(":");
        if (j != -1)
            s1 = s1.substring(0, j);
        j = s1.indexOf("/");
        if (j != -1)
            s1 = s1.substring(0, j);
        return s1;
    }

    public static String refreshLabel(int i) {
        if (i == 0)
            return "";
        else
            return LocaleUtils.getResourceBundle().getString("PageRefreshRate")
                    + " " + i + " "
                    + LocaleUtils.getResourceBundle().getString("Seconds");
    }

    public static String platformName() {
        String s = "WIN";
        if (isUnix())
            s = "Unix";
        return s;
    }

    public static Date makeDate() {
        return new Date(timeMillis());
    }

    public static boolean noRoute(Exception exception) {
        return PlatformNew.noRoute(exception);
    }

    public static Array setupTimeZoneOffset() {
		//dingbing.xu ??
        Array array = new Array();
        if (isWindows() && PlatformNew.timeZoneOffset == -1) {
            CommandLine commandline = new CommandLine();
            array = commandline.exec(perfexCommand("", false) + " -t");
            timeZoneOffset = 0;
            if (array.size() >= 1)
                timeZoneOffset = TextUtils.toInt(TextUtils.readColumn(
                        (String) array.at(0), 2));
            PlatformNew.fixTimeZoneDefault(timeZoneOffset * 1000);
            array.add("exit: " + commandline.getExitValue());
        }
        return array;
    }

    public static long timeMillis() {
        setupTimeZoneOffset();
        long l = System.currentTimeMillis();
        return l;
    }

    public static void setSocketTimeout(Socket socket, int i) {
        PlatformNew.setSocketTimeout(socket, i);
    }

    public static void setSocketTimeout(DatagramSocket datagramsocket, int i) {
        PlatformNew.setSocketTimeout(datagramsocket, i);
    }

    public static void main(String args[]) throws Exception {
        if (args.length == 1) {
            if (args[0].equals("Disks")) {
                Vector vector = getDisks("");
                String s3;
                for (Enumeration enumeration5 = vector.elements(); enumeration5
                        .hasMoreElements(); System.out.println("_name=" + s3)) {
                    String s1 = (String) enumeration5.nextElement();
                    System.out.println("_disk=" + s1);
                    s3 = (String) enumeration5.nextElement();
                }

            } else if (args[0].equals("WebServers")) {
                Vector vector1 = getWebServers("");
                String s4;
                for (Enumeration enumeration6 = vector1.elements(); enumeration6
                        .hasMoreElements(); System.out.println("_name=" + s4)) {
                    String s2 = (String) enumeration6.nextElement();
                    System.out.println("_serverName=" + s2);
                    s4 = (String) enumeration6.nextElement();
                }

            } else {
                System.out.println("unknown option: " + args[0]
                        + ", use Disks or WebServers");
            }
            return;
        }
        //System.out.println("encoding=" + System.getProperty("file.encoding"));
        String s = "";
        if (args.length > 1 && args[0].equals("-machine"))
            s = args[1];
        HashMap hashmap = MasterConfig.getMasterConfig();
        Machine.registerMachines(hashmap.values("_remoteMachine"));
        long al[] = cpuUsed(s, 0L, 0L, new long[16]);
        if (al != null) {
            System.out.println("cpu: " + al[0] + "% used");
            for (int i = 0; i < al.length; i++)
                System.out.println("  cpu[" + i + "] = " + al[i]);

        }
        timeMillis();
        long al1[] = getMemoryFull(s, 0L, 0L);
        System.out.println("memory: " + al1[0] + "% used, " + al1[1]
                + " used, " + al1[2] + " total, " + al1[3] + " delta faults, "
                + al1[4] + " delta time, " + al1[5] + " faults, " + al1[6]
                + " time, " + al1[7] + " freq");
        for (int j = 0; j < al1.length; j++)
            System.out.println("  mem[" + j + "] = " + al1[j]);

        timeMillis();
        long al2[] = processUsed(s, "java", 0L, 0L);
        System.out.println("processUsed java: " + al2[0] + "," + al2[1] + ","
                + al2[2] + "," + al2[3]);
        timeMillis();
        long al3[] = checkProcess("java", s, 8192L);
        System.out.println("checkProcess java: " + al3[0] + ", " + al3[1]
                + ", " + al3[2]);
        Vector vector2 = getDisks(s);
        String s5;
        long al4[];
        for (Enumeration enumeration = vector2.elements(); enumeration
                .hasMoreElements(); System.out.println("disk=" + s5 + ", name="
                + enumeration.nextElement() + ", %full=" + al4[0] + ", used="
                + al4[1] + ", total=" + al4[2])) {
            s5 = (String) enumeration.nextElement();
            al4 = getDiskFull(s5, "");
        }

        vector2 = getWebServers(s);
        String s6;
        for (Enumeration enumeration1 = vector2.elements(); enumeration1
                .hasMoreElements(); System.out.println("web: (" + s6 + ")"))
            s6 = (String) enumeration1.nextElement();

        vector2 = getServers();
        String s7;
        for (Enumeration enumeration2 = vector2.elements(); enumeration2
                .hasMoreElements(); System.out.println("server: (" + s7 + ")"))
            s7 = (String) enumeration2.nextElement();

        for (int k = -43200; k <= 43200; k += 3600)
            System.out.println(k / 3600 + ": " + timeZoneName(k));

        PlatformNew.printTimezone();
        System.out.println("time: " + TextUtils.prettyDate());
        PlatformNew.printTimezone();
        timeMillis();
        System.out.println("root: " + getRoot());
        System.out.println("os: " + getOs());
        Array array = getIPAddresses("");
        String s8;
        for (Enumeration enumeration3 = array.elements(); enumeration3
                .hasMoreElements(); System.out.println("ip: (" + s8 + ")"))
            s8 = (String) enumeration3.nextElement();

        array = getDNSAddresses();
        String s9;
        for (Enumeration enumeration4 = array.elements(); enumeration4
                .hasMoreElements(); System.out.println("dns: (" + s9 + ")"))
            s9 = (String) enumeration4.nextElement();

        int ai[] = ping("localhost", 5000, 1, 64);
        System.out.println("ping: " + ai[0] + " ms, " + ai[1] + " successes");
    }

    /**
     * CAUTION: decompiled by hand --FooSleeper
     * 
     * @return
     */
    public static String getVersion() {
		return "";
//        Object versionSync = getVersionSync;
//        synchronized (versionSync) {
//            if (Platform.versionString != null) {
//                BufferedReader bufferedReader = null;
//                try {
//                    String versionFilePath = getRoot() + File.separator + "dat"
//                            + File.separator + "Version.txt";
//                    String buildDateFilePath = getRoot() + File.separator
//                            + "dat" + File.separator + "build_date.txt";
//                    String buildDate = "(Date unknown)";
//                    String datePrefix = "Date:";
//
//                    try {
//                        bufferedReader = new BufferedReader(new FileReader(
//                                buildDateFilePath));
//                        String line = bufferedReader.readLine();
//                        if (line.startsWith(datePrefix)) {
//                            buildDate = line.substring(datePrefix.length())
//                                    .trim();
//                        }
//                    } catch (FileNotFoundException e) {
//                        LogManager
//                                .log("Error", "Could not load "
//                                        + buildDateFilePath
//                                        + " date of build is unknown "
//                                        + e.getMessage());
//                    }
//
//                    if (bufferedReader != null) {
//                        bufferedReader.close();
//                        bufferedReader = null;
//                    }
//
//                    bufferedReader = new BufferedReader(new FileReader(
//                            versionFilePath));
//                    String buildNumber = "(Build unknown)";
//                    boolean hasBuildNumber = false;
//                    String versionNumber = "(Version unknown)";
//                    boolean hasVersionNumber = false;
//                    for (String line = bufferedReader.readLine(); line != null;) {
//                        String versionPrefix = "Version:";
//                        String buildPrefix = "Build:";
//                        if (line.startsWith(versionPrefix)) {
//                            versionNumber = line.substring(
//                                    versionPrefix.length()).trim();
//                            hasVersionNumber = true;
//                        }
//                        if (line.startsWith(buildPrefix)) {
//                            buildNumber = line.substring(buildPrefix.length())
//                                    .trim();
//                            hasBuildNumber = true;
//                        }
//                        if (!hasVersionNumber || !hasBuildNumber) {
//                            continue;
//                        }
//                        Platform.versionString = versionNumber + " "
//                                + buildDate + " build " + buildNumber;
//                        return Platform.versionString;
//                    }
//                } catch (IOException e) {
//                    LogManager.log("Error",
//                            "Could get version and build number."
//                                    + e.getMessage());
//                    LogManager.logException(e);
//                } finally {
//                    if (bufferedReader != null) {
//                        try {
//                            bufferedReader.close();
//                        } catch (IOException e) {
//                            LogManager.logException(e);
//                        }
//                        bufferedReader = null;
//                    }
//                }
//            }
//            Platform.versionString = Platform.version;
//            return Platform.versionString;

//        }
    }

    public static int getLocalPlatform() {
        return getOs();
    }

    public static boolean isLocalPlatform(int i) {
        return getOs() == i;
    }

    public static boolean isWindows() {
        return getOs() == 1;
    }

    public static boolean isWindows(int i) {
        return i == 1;
    }

    public static boolean isSGI() {
        return getOs() == 3;
    }

    public static boolean isSGI(int i) {
        return i == 3;
    }

    public static boolean isSolaris() {
        return getOs() == 2;
    }

    public static boolean isSolaris(int i) {
        return i == 2;
    }

    public static boolean isLinux() {
        return getOs() == 6;
    }

    public static boolean isLinux(int i) {
        return i == 6;
    }

    public static boolean isHP() {
        return getOs() == 5;
    }

    public static boolean isHP(int i) {
        return i == 5;
    }

    public static boolean isUnix() {
        return getOs() != 1;
    }

    public static boolean isUnix(int i) {
        return i != 1;
    }

    public static String chmodCommand() {
        String s = "chmod";
        switch (getOs()) {
        case 2: // '\002'
        case 3: // '\003'
        case 5: // '\005'
            s = "/usr/bin/chmod";
            break;

        case 6: // '\006'
            s = "/bin/chmod";
            break;
        }
        return s;
    }

    public static void chmod(String s, String s1) {
        chmod(new File(s), s1);
    }

    public static boolean isStandardAccount(String s) {
        return s.equals("administrator") || s.equals("user")
                || s.startsWith("login") || s.length() == 0;
    }

    public static boolean isSiteSeerAccount(String s) {
        return !isStandardAccount(s);
    }

    public static boolean isSiteSeerServer() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        String s = TextUtils.getValue(hashmap, "_siteseerServer");
        return s.length() != 0 && s.equals("true");
    }

    public static String getDirectoryPath(String s, HTTPRequest httprequest) {
        return getDirectoryPath(s, httprequest, false);
    }

    public static String getUsedDirectoryPath(String s, HTTPRequest httprequest) {
        return getDirectoryPath(s, httprequest, true);
    }

    public static String getDirectoryPath(String s, HTTPRequest httprequest,
            boolean flag) {
        return getDirectoryPathImplementation(s, null, httprequest, flag);
    }

    public static String getDirectoryPath(String s, String s1) {
        return getDirectoryPathImplementation(s, s1, null, false);
    }

    public static String getUsedDirectoryPath(String s, String s1) {
        return getDirectoryPathImplementation(s, s1, null, true);
    }

    private static String getDirectoryPathImplementation(String s, String s1,
            HTTPRequest httprequest, boolean flag) {
        String s2 = "";
        String s3 = "";
        if (httprequest != null) {
            if (s1 == null)
                s1 = httprequest.getAccount();
            String s4 = Portal.cleanPortalServerID(httprequest
                    .getPortalServer());
            if (s4.length() != 0) {
                s3 = File.separator + Portal.PORTAL_DIRECTORY + File.separator
                        + s4;
                TextUtils.debugPrint("SERVERPATH=" + s3);
            }
        }
        if (!isStandardAccount(s1)) {
            s2 = getRoot() + File.separator + "accounts" + File.separator + s1
                    + File.separator + s;
            if (flag && !(new File(s2)).exists())
                s2 = getRoot() + s3 + File.separator + s;
        } else {
            s2 = getRoot() + s3 + File.separator + s;
        }
        return s2;
    }

    public static String getURLPath(String s, String s1) {
        String s2 = "";
        if (s1.equals("user") || s1.equals("administrator"))
            s2 = "/SiteView/" + s;
        else
            s2 = "/SiteView/accounts/" + s1 + "/" + s;
        return s2;
    }

    public static void chmod(File file, String s) {
        if (isUnix() && file.exists()) {
            HashMap hashmap = MasterConfig.getMasterConfig();
            if (TextUtils.getValue(hashmap, "_setUnixFilePermissions").equals(
                    "false"))
                return;
            String s1 = TextUtils.getValue(hashmap, "_unixFileMask").trim();
            int i;
            if (s == "rwx")
                i = 511;
            else if (s == "rw")
                i = 438;
            else
                i = 292;
            if (!s1.equals(""))
                try {
                    int j = Integer.parseInt(s1, 8);
                    j %= 64;
                    i &= ~j;
                } catch (NumberFormatException numberformatexception) {
                    LogManager.log("Error",
                            "Invalid File Mask set. Using default settings of "
                                    + Integer.toString(i, 8));
                }
            String s2 = chmodCommand() + " " + Integer.toString(i, 8) + " "
                    + file.getAbsolutePath();
            CommandLine commandline = new CommandLine();
            commandline.exec(s2);
        }
    }

    public static int[] ping(String host, int i, int j, int k) {
        return ping(host, i, j, k, null);
    }

    //TODO: for testing only
    public static int[] ping(String host, int i, int j, int k,Monitor monitor) {
    	return ping( host,  i,  j,  k, monitor);
    }
    /**
     * 
     * 
     * @param host
     * @param i
     * @param j
     * @param k
     * @param atomicmonitor
     * @return
     */
    public static int[] ping(String host, int i, int j, int k,AtomicMonitor atomicmonitor) {
        if (atomicmonitor != null)
            atomicmonitor.currentStatus = "PingMonitor executing cmd...";
        String s1 = pingCommand(host, i, j, k);
        float f = 0.0F;
        int l = 0;
        if (k >= 1 && k <= 60000) {
            CommandLine commandline = new CommandLine();
            Array array = commandline.exec(s1, monitorLock, atomicmonitor);
            if (commandline.getExitValue() < 0) {
                f = PING_COMMAND_FAILED;
            } else {
                Enumeration enumeration = array.elements();
                while (enumeration.hasMoreElements()) {
                    String s2 = (String) enumeration.nextElement();
                    float f1 = -1F;
                    if (s2.indexOf("transmitted") < 0 || !isLinux()) {
                        switch (getOs()) {
                        case 1: // '\001'
                        case 2: // '\002'
                        case 3: // '\003'
                        case 5: // '\005'
                        case 6: // '\006'
                            f1 = TextUtils.findInteger(s2, " time", "ms");
                            if (f1 == -1F)
                                f1 = TextUtils.findInteger(s2, " Zeit", "ms");
                            if (f1 == -1F)
                                f1 = TextUtils.findInteger(s2, " temps", "ms");
                            if (f1 == -1F)
                                f1 = TextUtils.findInteger(s2, " tiempo", "ms");
                            if (f1 == -1F)
                                f1 = TextUtils.findInteger(s2, " durata", "ms");
                            if (f1 == -1F)
                                f1 = TextUtils.findInteger(s2, " tempo", "ms");
                            if (f1 == -1F)
                                f1 = TextUtils.findInteger(s2, " aika", "ms");
                            if (f1 == -1F) {
                                f1 = TextUtils.findInteger(s2, " time", "usec");
                                if (f1 != -1F)
                                    f1 /= 1000F;
                            }
                            break;
                        }
                        if (f1 != -1F) {
                            l++;
                            f += f1;
                        }
                    }
                }
            }
        }
        int ai[] = new int[2];
        ai[0] = Math.round(f);
        ai[1] = l;
        return ai;
    }

    public static String pingCommand(String s, int i, int j, int k) {
        String s1 = null;
        switch (getOs()) {
        case 1: // '\001'
            int l = j * (i / 1000 + 1) + 10;
            s1 = perfexCommand("") + " -timeout " + l + " -ping -n " + j
                    + " -w " + i + " -l " + k + " " + s;
            break;

        case 6: // '\006'
            s1 = "/bin/ping -c " + j + " -w " + i / 1000 + " -s " + k + " " + s;
            break;

        case 2: // '\002'
            s1 = "/usr/sbin/ping -sn " + s + " " + k + " " + j;
            break;

        case 5: // '\005'
            s1 = "/usr/sbin/ping " + s + " " + k + " -n " + j;
            break;

        case 3: // '\003'
            s1 = "/usr/etc/ping -c " + j + " -s " + k + " " + s;
            break;
        }
        return s1;
    }

    public static Array traceRoute(String s, String s1) {
        CommandLine commandline = new CommandLine();
        return commandline.exec(traceCommand(s, s1), new CounterLock(1));
    }

    public static String traceCommand(String s, String s1) {
        if (s1.length() == 0)
            switch (getOs()) {
            case 1: // '\001'
                s1 = "tracert";
                break;

            case 6: // '\006'
                s1 = "/usr/sbin/traceroute";
                break;

            case 2: // '\002'
                s1 = "/usr/local/bin/traceroute";
                break;

            case 5: // '\005'
                s1 = "/usr/contrib/bin/traceroute";
                break;

            case 3: // '\003'
                s1 = "/usr/etc/traceroute";
                break;
            }
        return s1 + " " + s;
    }

    public static String nslookupCommand() {
        String s = null;
        switch (getOs()) {
        case 1: // '\001'
            s = "nslookup";
            break;

        case 6: // '\006'
            s = "/usr/bin/nslookup";
            break;

        case 2: // '\002'
            s = "/usr/sbin/nslookup";
            break;

        case 5: // '\005'
            s = "/usr/bin/nslookup -norecurse";
            break;

        case 3: // '\003'
            s = "/usr/sbin/nslookup";
            break;
        }
        return s;
    }

    public static String[] dnsLookup(String s, String s1, String s2) {
        return dnsLookup(s, s1, s2, null);
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param s2
     * @param atomicmonitor
     * @return
     */
    public static String[] dnsLookup(String s, String s1, String s2,
            AtomicMonitor atomicmonitor) {
        String as[] = new String[3];
        String s3 = nslookupCommand() + " " + s1 + " " + s;
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s3, monitorLock, atomicmonitor);
        as[1] = "" + commandline.duration;
        Enumeration enumeration = array.elements();
        String s4 = "";
        boolean flag = false;
        boolean flag1 = false;
        while (enumeration.hasMoreElements()) {
            String s5 = I18N.toDefaultEncoding((String) enumeration
                    .nextElement());
            if (getOs() == 5) {
                int j = s5.indexOf("Name Server:");
                if (j != -1) {
                    String s6 = s5.substring(12).trim();
                    s5 = (String) enumeration.nextElement();
                    String s7 = s5.substring(8).trim();
                    if (!s6.equals(s) && !s7.equals(s))
                        break;
                    flag = true;
                    continue;
                }
            }
            if (TextUtils.stringContainsSubstringFromArray(s5, DNSMonitor
                    .getServerTagsOptions())) {
                if (s5.indexOf("UnKnown") == -1)
                    flag = true;
                continue;
            }
            if (TextUtils.stringContainsSubstringFromArray(s5, DNSMonitor
                    .getNameTagsOptions())) {
                s5 = (String) enumeration.nextElement();
                if (s5.indexOf("Address") != -1) {
                    s4 = s5;
                    while (enumeration.hasMoreElements()) {
                        s5 = (String) enumeration.nextElement();
                        if (s5.length() == 0 || s5.charAt(0) == ' ') {
                            break;
                        }
                        s4 = s4 + s5;
                    } 
                }
                break;
            }
            if (s5.indexOf("existent") != -1)
                flag1 = true;
        } 
        
        int i;
        if (s4.length() != 0) {
            i = Monitor.kURLok;
            if (s2.length() > 0) {
                String as1[] = TextUtils.split(s2, ",");
                boolean flag2 = false;
                int k = 0;
                while (k < as1.length) {
                    String s8 = as1[k].trim();
                    if (TextUtils.foundIPAddress(s4, s8)) {
                        flag2 = true;
                        break;
                    }
                    k++;
                } 
                
                if (!flag2)
                    i = Monitor.kDNSIPAddressMismatch;
            }
        } else if (flag1 || flag)
            i = Monitor.kURLBadHostNameError;
        else
            i = Monitor.kURLNoConnectionError;
        as[0] = "" + i;
        as[2] = s4;
        return as;
    }

    static String demoPath() {
        if (isWindows())
            return "C:/ChangeLog.txt";
        else
            return "/ChangeLog.txt";
    }

    public static boolean isDemo() {
        File file = new File(demoPath());
        return file.exists();
    }

    public static CounterLock getLock(String s) {
        CounterLock counterlock = (CounterLock) remoteLocks.get(s);
        if (counterlock == null) {
            counterlock = new CounterLock(1);
            remoteLocks.put(s, counterlock);
        }
        return counterlock;
    }

    public static long[] getDiskFull(String s, String s1) {
        return getDiskFull(s, s1, null, null);
    }

    public static long[] getDiskFull(String s, String s1,
            AtomicMonitor atomicmonitor, Array array) {
        //TODO need review
        long al[];
        label0: {
            al = new long[3];
            al[0] = -1L;
            al[1] = 0L;
            al[2] = 0L;
            int i = Machine.getOS(s1);
            HashMap hashmap = new HashMap();
            hashmap.put("disk", s);
            String s2 = Machine.getCommandString("disk", s1, hashmap);
            if (s2.length() == 0)
                s2 = dfCommand(s, s1, i);
            String s3 = s1;
            if (s3.startsWith("\\\\"))
                s3 = s3.substring(2);
            Machine machine = Machine.getNTMachine(s3);
            Array array1 = null;
            if (machine != null && Machine.isNTSSH(s3)) {
                if (s2.indexOf("\\\\" + s3) > 0)
                    s2 = TextUtils.replaceString(s2, "\\\\" + s3, "");
                s2 = s2.substring(s2.indexOf("perfex"));
                SSHCommandLine sshcommandline = new SSHCommandLine();
                array1 = sshcommandline.exec(s2, machine, false);
            } else {
                CommandLine commandline = new CommandLine();
                array1 = commandline.exec(s2, s1, getLock(s1), atomicmonitor);
            }
            Enumeration enumeration = array1.elements();
            if (array != null)
                array.copy(array1);
            if (i == 1) {
                boolean flag = false;
                boolean flag1 = false;
                String s4 = "name: " + s;
                long l = -1L;
                long l1 = -1L;
                while (enumeration.hasMoreElements()) {
                    String s5 = (String) enumeration.nextElement();
                    if (flag1) {
                        if (flag) {
                            if (l < 0L)
                                l = getPerfData(s5, "408:",
                                        "% PERF_RAW_FRACTION");
                            if (l1 < 0L)
                                l1 = getPerfData(s5, "408:", "PERF_RAW_BASE");
                            if (l < 0L || l1 <= 0L)
                                continue;
                            long l2 = (100L * l) / l1;
                            al[0] = 100L - l2;
                            al[1] = (l1 - l) * 1024L * 1024L;
                            al[2] = l1 * 1024L * 1024L;
                            break;
                        }
                        if (s5.equals(s4) || s5.equals(s4 + ":"))
                            flag = true;
                    } else if (s5.indexOf("object: 236") == 0)
                        flag1 = true;
                } 
                break label0;
            }
            OSAdapter osadapter = Machine.getAdapter(s1);
            if (osadapter == null) {
                LogManager.log("Error", "Could not get adapter for machine "
                        + s1);
                return al;
            }
            int j = osadapter.getCommandSettingAsInteger("disk", "total");
            int k = osadapter.getCommandSettingAsInteger("disk", "free");
            int i1 = osadapter
                    .getCommandSettingAsInteger("disk", "percentUsed");
            LineReader linereader = new LineReader(array1, osadapter, "disk");
            int j1;
            int k1;
            int i2;
            String s6;
            do {
                do {
                    if (!linereader.processLine())
                        break label0;
                    j1 = j;
                    k1 = k;
                    i2 = i1;
                } while (linereader.skipLine());
                s6 = linereader.getCurrentLine();
            } while (s6.indexOf(s) < 0);
            String s7 = linereader.readColumn(i2, "percentUsed");
            if (s7.length() == 0 && enumeration.hasMoreElements()) {
                linereader.processLine();
                j1--;
                k1--;
                i2--;
                s7 = linereader.readColumn(i2, "percentUsed");
            }
            if (s7.length() > 0)
                al[0] = TextUtils.readLong(s7, 0);
            s7 = linereader.readColumn(j1, "total");
            if (s7.length() > 0)
                al[2] = TextUtils.readLong(s7, 0) * 1024L;
            s7 = linereader.readColumn(k1, "free");
            if (s7.length() > 0)
                al[1] = al[2] - TextUtils.readLong(s7, 0) * 1024L;
        }
        return al;
    }

    static String dfCommand(String s) {
        return dfCommand(s, "");
    }

    public static String sendModemCommand() {
        return sendModemCommand("");
    }

    public static String sendModemCommand(String s) {
        if (Machine.isPortalMachineID(s))
            return "page=SendModem&option=$START$";
        if (isWindows() && !Machine.isNTSSH(s))
            return getRoot() + "/tools/SendModem.exe *" + pid;
        else
            return getRoot() + "/tools/SendModem";
    }

    public static String perfexCommand(String s) {
        return perfexCommand(s, true);
    }

    public static String perfexCommand(String s, boolean flag) {
        String s1 = s;
        String s2 = "";
        if (isWindows() && !Machine.isNTSSH(s1))
            s2 = getRoot() + "/tools/perfex.exe *" + pid;
        else
            s2 = "perfex";
        if (Machine.isPortalMachineID(s)) {
            s2 = "page=perfex&option=$START$";
            s1 = Machine.getMachineFromMachineID(s);
        }
        String s3 = "";
        String s4 = "";
        if (flag) {
            HashMap hashmap = MasterConfig.getMasterConfig();
            if (hashmap != null) {
                s3 = (String) hashmap.get("_perfexOptions");
                if (s3 == null)
                    s3 = "";
                s4 = (String) hashmap.get("_perfexTimeout");
                if (s4 == null)
                    s4 = "";
            }
        }
        if (isNTRemote(s1)) {
            if (s3.indexOf("-connect") == -1)
                s3 = s3 + " -connect";
            String s5 = " " + s1;
            Machine machine = Machine.getNTMachine(s1);
            if (s4 != null && s4.length() > 0)
                s5 = s5 + " -timeout " + s4;
            if (machine != null) {
                String s6 = machine.getProperty(Machine.pPassword);
                if (s6.length() <= 0)
                    s6 = "\"\"";
                s5 = s5 + " -u " + machine.getProperty(Machine.pLogin) + " -p "
                        + s6;
            }
            s2 = s2 + s5;
        }
        if (s3.length() > 0)
            s2 = s2 + " " + s3;
        s2 = s2 + " ";
        if ((platformDebugTrace & kDebugPerfex) != 0)
            LogManager.log("RunMonitor", "Perfex command: " + s2);
        return s2;
    }

    static String dfCommand(String s, String s1) {
        return dfCommand(s, s1, getOs());
    }

    static String dfCommand(String s, String s1, int i) {
        String s2 = null;
        if (s1.startsWith("http://")) {
            s2 = s1 + "perfex.exe?" + URLEncoder.encode("-cgi =236");
            return s2;
        }
        switch (i) {
        case 1: // '\001'
            s2 = perfexCommand(s1) + " =236";
            break;

        case 5: // '\005'
            s2 = "/usr/bin/df -kP " + s;
            break;

        case 2: // '\002'
            s2 = "/usr/bin/df -k " + s;
            break;

        case 6: // '\006'
            s2 = "/bin/df -k " + s;
            break;

        case 3: // '\003'
            s2 = "/usr/sbin/df -k " + s;
            break;
        }
        return s2;
    }

    static String memoryCommand(String s) {
        return memoryCommand(s, getOs());
    }

    static String memoryCommand(String s, int i) {
        String s1 = null;
        if (s.startsWith("http://")) {
            s1 = s + "perfex.exe?" + URLEncoder.encode("-cgi =4");
            return s1;
        }
        switch (i) {
        case 1: // '\001'
            s1 = perfexCommand(s) + " =4";
            break;

        case 2: // '\002'
            s1 = "/usr/sbin/swap -l";
            break;

        case 5: // '\005'
            s1 = "/usr/sbin/swapinfo -d";
            break;

        case 3: // '\003'
            s1 = "/sbin/swap -l";
            break;

        case 6: // '\006'
            s1 = "/usr/bin/free -b";
            break;
        }
        return s1;
    }

    public static long getPerfData(String s, String s1, String s2) {
        String s3 = s.trim();
        if (s3.startsWith(s1))
            return TextUtils.findLong(s3, s1, s2);
        else
            return -1L;
    }

    public static long[] getMemoryFull(String s, long l, long l1) {
        return getMemoryFull(s, l, l1, null, null);
    }

    /**
     * 
     * 
     * @param s
     * @param l
     * @param l1
     * @param atomicmonitor
     * @param array
     * @return
     */
    public static long[] getMemoryFull(String s, long l, long l1,
            AtomicMonitor atomicmonitor, Array array) {
        // TODO need review
        long al[];
        label0: {
            al = new long[8];
            al[0] = -1L;
            al[1] = 0L;
            al[2] = 0L;
            al[3] = 0L;
            al[4] = 0L;
            al[5] = 0L;
            al[6] = 0L;
            al[7] = 1L;
            int i = Machine.getOS(s);
            String s1 = Machine.getCommandString("memory", s);
            if (s1.length() == 0)
                s1 = memoryCommand(s, i);
            String s2 = s;
            if (s2.startsWith("\\\\"))
                s2 = s2.substring(2);
            Machine machine = Machine.getNTMachine(s2);
            Array array1 = null;
            if (machine != null && Machine.isNTSSH(s2)) {
                if (s1.indexOf("\\\\" + s2) > 0)
                    s1 = TextUtils.replaceString(s1, "\\\\" + s2, "");
                s1 = s1.substring(s1.indexOf("perfex"));
                SSHCommandLine sshcommandline = new SSHCommandLine();
                array1 = sshcommandline.exec(s1, machine, false);
            } else {
                CommandLine commandline = new CommandLine();
                array1 = commandline.exec(s1, s, getLock(s), atomicmonitor);
            }
            if (array != null)
                array.copy(array1);
            Enumeration enumeration = array1.elements();
            if (i == 1) {
                long l2 = -1L;
                long l4 = -1L;
                long l6 = -1L;
                long l8 = -1L;
                long l10 = -1L;
                long l11 = -1L;
                long l12 = -1L;
                do {
                    if (!enumeration.hasMoreElements())
                        break label0;
                    String s4 = (String) enumeration.nextElement();
                    if (l6 < 0L)
                        l6 = getPerfData(s4, "PerfTime:", null);
                    if (l10 < 0L)
                        l10 = getPerfData(s4, "PerfFreq:", null);
                    if (l2 < 0L)
                        l2 = getPerfData(s4, "26:", "PERF_COUNTER_");
                    if (l8 < 0L)
                        l8 = getPerfData(s4, "40:",
                                "/second PERF_COUNTER_COUNTER");
                    if (l11 < 0L)
                        l11 = getPerfData(s4, "1406:", "% PERF_RAW_FRACTION");
                    if (l12 < 0L) {
                        l12 = getPerfData(s4, "1406:", "PERF_RAW_BASE");
                        if (l12 > 0L)
                            l4 = (l2 * l12) / l11;
                    }
                } while (l2 < 0L || l4 <= 0L || l8 <= 0L);
                al[0] = (100L * l2) / l4;
                al[1] = l2;
                al[2] = l4;
                if (l == 0L)
                    al[3] = -1L;
                else
                    al[3] = l8 - l;
                if (l1 == 0L)
                    al[4] = 0L;
                else
                    al[4] = l6 - l1;
                al[5] = l8;
                al[6] = l6;
                al[7] = l10;
            } else {
                long l3 = -1L;
                long l5 = -1L;
                long l7 = -1L;
                long l9 = -1L;
                int j = -1;
                int k = -1;
                int i1 = -1;
                int j1 = -1;
                int k1 = 1;
                String s3 = "";
                String s5 = "";
                String s6 = "";
                OSAdapter osadapter = Machine.getAdapter(s);
                if (osadapter == null) {
                    LogManager.log("Error",
                            "Could not get adapter for machine " + s);
                    return al;
                }
                k = osadapter.getCommandSettingAsInteger("memory", "total", k);
                j = osadapter.getCommandSettingAsInteger("memory", "free", j);
                i1 = osadapter.getCommandSettingAsInteger("memory", "used", i1);
                j1 = osadapter.getCommandSettingAsInteger("memory",
                        "usedPercentage", j1);
                k1 = osadapter.getCommandSettingAsInteger("memory", "swapUnit",
                        k1);
                s3 = osadapter.getCommandSetting("memory", "totalMatch");
                s6 = osadapter.getCommandSetting("memory", "freeMatch");
                s5 = osadapter.getCommandSetting("memory", "usedMatch");
                if (k1 == 0)
                    k1 = 512;
                LineReader linereader = new LineReader(array1, osadapter,
                        "memory");
                while (linereader.processLine()) {
                    if (!linereader.skipLine()) {
                        if (k >= 0
                                && (s3.length() == 0 || TextUtils.match(
                                        linereader.getCurrentLine(), s3))) {
                            String s7 = linereader.readColumn(k, "total");
                            if (s7.length() > 0) {
                                if (l3 == -1L)
                                    l3 = 0L;
                                l3 += TextUtils.readLong(s7, 0) * (long) k1;
                            }
                        }
                        if (j >= 0
                                && (s6.length() == 0 || TextUtils.match(
                                        linereader.getCurrentLine(), s6))) {
                            String s8 = linereader.readColumn(j, "free");
                            if (s8.length() > 0) {
                                if (l5 == -1L)
                                    l5 = 0L;
                                l5 += TextUtils.readLong(s8, 0) * (long) k1;
                            }
                        }
                        if (i1 >= 0
                                && (s5.length() == 0 || TextUtils.match(
                                        linereader.getCurrentLine(), s5))) {
                            String s9 = linereader.readColumn(i1, "used");
                            if (s9.length() > 0) {
                                if (l7 == -1L)
                                    l7 = 0L;
                                l7 += TextUtils.readLong(s9, 0) * (long) k1;
                            }
                        }
                        if (j1 >= 0) {
                            String s10 = linereader.readColumn(j1,
                                    "usedPercentage");
                            if (s10.length() > 0)
                                l9 = TextUtils.readLong(s10, 0);
                        }
                    }
                } 
                if (j1 >= 0) {
                    if (l9 >= 0L && l3 >= 0L) {
                        l7 = (l3 * l9) / 100L;
                        l5 = (l3 * (100L - l9)) / 100L;
                    }
                } else {
                    if (i1 >= 0) {
                        if (l5 >= 0L && l7 >= 0L)
                            l3 = l5 + l7;
                    } else if (l5 >= 0L && l3 >= 0L)
                        l7 = l3 - l5;
                    if (l3 != 0L)
                        l9 = (100L * l7) / l3;
                }
                if (l3 != -1L) {
                    al[0] = l9;
                    al[1] = l7;
                    al[2] = l3;
                    long l13 = timeMillis();
                    long l14 = pageFaults(s);
                    String s11 = osadapter.getCommandSetting("pageFault",
                            "units", "pages");
                    if (s11.endsWith("/sec")) {
                        al[3] = l14;
                        al[4] = 1000L;
                        al[5] = 0L;
                        al[6] = 0L;
                        al[7] = 1L;
                    } else {
                        if (l == 0L)
                            al[3] = -1L;
                        else
                            al[3] = l14 - l;
                        if (l1 == 0L)
                            al[4] = 0L;
                        else
                            al[4] = l13 - l1;
                        al[5] = l14;
                        al[6] = l13;
                        al[7] = 1000L;
                    }
                }
            }
        }
        return al;
    }

    public static long pageFaults(String s) {
        int i = Machine.getOS(s);
        CommandLine commandline = new CommandLine();
        String s1 = Machine.getCommandString("pageFault", s);
        if (s1.length() == 0)
            s1 = pageFaultCommand(i);
        Array array = commandline.exec(s1, s, monitorLock);
        long l = -1L;
        OSAdapter osadapter = Machine.getAdapter(s);
        long l1 = 1L;
        int j = 1;
        int k = 0;
        if (osadapter == null) {
            LogManager.log("Error", "Could not get adapter for machine " + s);
            return l;
        }
        k = osadapter.getCommandSettingAsInteger("pageFault", "outPageFaults",
                k);
        if (k == 0)
            j = osadapter.getCommandSettingAsInteger("pageFault", "pageFaults",
                    j);
        else
            j = osadapter.getCommandSettingAsInteger("pageFault",
                    "inPageFaults", j);
        String s2 = osadapter.getCommandSetting("pageFault", "units", "pages");
        if (s2.endsWith("/sec"))
            l1 = 1000L;
        LineReader linereader = new LineReader(array, osadapter, "pageFault");
        do {
            if (!linereader.processLine())
                break;
            if (!linereader.skipLine()) {
                String s3 = linereader.readColumn(j, "pageFaults");
                if (TextUtils.isNumber(s3)) {
                    if (l == -1L)
                        l = 0L;
                    if (TextUtils.isFloat(s3))
                        l += (long) (TextUtils.toFloat(s3) * (float) l1);
                    else
                        l += TextUtils.toLong(s3) * l1;
                }
                if (k != 0) {
                    String s4 = linereader.readColumn(k,
                            "secondPageFaultsColumn");
                    if (TextUtils.isNumber(s4)) {
                        if (l == -1L)
                            l = 0L;
                        if (TextUtils.isFloat(s4))
                            l += (long) (TextUtils.toFloat(s4) * (float) l1);
                        else
                            l += TextUtils.toLong(s4) * l1;
                    }
                }
            }
        } while (true);
        if (s2.equals("k/sec")) {
            float f = osadapter
                    .getCommandSettingAsLong("pageFault", "pageSize");
            if (f > 0.0F)
                l = (long) ((float) (l * 1024L) / f);
        }
        return l;
    }

    public static Array getProcesses(String s) {
        return getProcesses(s, false);
    }

    public static Array getProcesses(String s, boolean flag) {
        Array array = new Array();
        readProcessList(array, s, new CounterLock(1), false, flag);
        return array;
    }

    public static Array processList() {
        Array array = new Array();
        readProcessList(array, "", monitorLock, false);
        return array;
    }

    public static int readProcessList(Array array, String s,
            CounterLock counterlock) {
        return readProcessList(array, s, counterlock, false);
    }

    public static int readProcessList(Array array, String s,
            CounterLock counterlock, boolean flag) {
        return readProcessList(array, s, counterlock, flag, false);
    }

    public static int readProcessList(Array array, String s,
            CounterLock counterlock, boolean flag, boolean flag1) {
        return readProcessList(array, s, counterlock, flag, flag1, null);
    }

    public static int readProcessList(Array array, String s,
            CounterLock counterlock, boolean flag, boolean flag1,
            AtomicMonitor atomicmonitor) {
        int i = Machine.getOS(s);
        String s1 = "";
        String s2 = "";
        if (flag1) {
            s2 = "serviceMonitor";
            s1 = Machine.getCommandString(s2, s);
        }
        if (s1.length() == 0) {
            s2 = flag ? "processDetail" : "process";
            s1 = Machine.getCommandString(s2, s);
        }
        if (s1.length() == 0)
            if (flag)
                s1 = psDetailCommand(s, i);
            else
                s1 = psCommand(s, i);
        int j = 0;
        String s3 = s;
        if (s3.startsWith("\\\\"))
            s3 = s3.substring(2);
        Machine machine = Machine.getNTMachine(s3);
        Array array1 = null;
        int k = 0;
        boolean flag2 = false;
        if (machine != null && Machine.isNTSSH(s3)) {
            flag2 = true;
            if (s1.indexOf("\\\\" + s3) > 0)
                s1 = TextUtils.replaceString(s1, "\\\\" + s3, "");
            s1 = s1.substring(s1.indexOf("perfex"));
            SSHCommandLine sshcommandline = new SSHCommandLine();
            array1 = sshcommandline.exec(s1, machine, false);
            k = sshcommandline.exitValue;
        } else {
            CommandLine commandline = new CommandLine();
            array1 = commandline.exec(s1, s, counterlock, atomicmonitor);
            k = commandline.getExitValue();
        }
        if (isWindows(i)) {
            for (int l = 0; l < array1.size(); l++) {
                String s4 = (String) array1.at(l);
                if (s4.indexOf("end perfex") < 0 && s4.indexOf("Copyright") < 0
                        && s4.indexOf("Microsoft Windows") < 0
                        && (!flag2 || s4.indexOf("-s") <= 0))
                    array.add(s4.trim());
            }

        } else {
            j = k;
            if (j != 0)
                LogManager.log("Error", "Service Monitor error: " + k);
            if (array1.size() > 0) {
                OSAdapter osadapter = Machine.getAdapter(s);
                if (osadapter == null) {
                    LogManager.log("Error",
                            "Could not get adapter for machine " + s);
                    return -2;
                }
                LineReader linereader = new LineReader(array1, osadapter, s2);
                do {
                    if (!linereader.processLine())
                        break;
                    if (!linereader.skipLine()) {
                        String s5 = linereader.readColumnByName("name");
                        if (s5.indexOf("<defunct>") == -1) {
                            if (flag)
                                s5 = linereader.readColumnByName("size") + ","
                                        + s5;
                            array.add(s5);
                        }
                    }
                } while (true);
            }
        }
        return j;
    }

    public static long[] checkProcess(String s, String s1, long l) {
        return checkProcess(s, s1, l, false);
    }

    public static long[] checkProcess(String s, String s1, long l, boolean flag) {
        return checkProcess(s, s1, l, flag, null, null);
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param l
     * @param flag
     * @param atomicmonitor
     * @param array
     * @return
     */
    public static long[] checkProcess(String s, String s1, long l,
            boolean flag, AtomicMonitor atomicmonitor, Array array) {
        boolean flag1 = isWindows(Machine.getOS(s1));
        long l1 = 0L;
        long l2 = -1L;
        s = s.toLowerCase();
        boolean flag2 = l != 0L;
        if (array == null)
            array = new Array();
        int i = readProcessList(array, s1, getLock(s1), flag2, flag,
                atomicmonitor);
        Enumeration enumeration = array.elements();
        boolean flag3 = TextUtils.isRegularExpression(s);
        while (enumeration.hasMoreElements()) {
            String s2 = ((String) enumeration.nextElement()).toLowerCase();
            boolean flag4 = false;
            if (flag1)
                flag4 = s2.equals(s);
            else
                flag4 = s2.indexOf(s) >= 0;
            if (flag3)
                flag4 = TextUtils.match(s2, s);
            if (!flag4)
                continue;
            l1++;
            if (flag1)
                break;
            if (flag2) {
                Array array1 = split(',', s2);
                if (array1.size() > 0) {
                    String s3 = (String) array1.at(0);
                    s3 = s3.trim();
                    if (l == -2L) {
                        l = 1L;
                        if (TextUtils.endsWithIgnoreCase(s3, "k"))
                            l = 1024L;
                        else if (TextUtils.endsWithIgnoreCase(s3, "m"))
                            l = 0x100000L;
                        else if (TextUtils.endsWithIgnoreCase(s3, "g"))
                            l = 0x40000000L;
                    }
                    float f = TextUtils.readFloat(s3, 0) * (float) l;
                    if (l2 == -1L)
                        l2 = (long) f;
                    else
                        l2 += (long) f;
                }
            }
        } 
        
        long al[] = new long[3];
        al[0] = l1;
        al[1] = i;
        al[2] = l2;
        return al;
    }

    public static String processOK(String s) {
        return processOK(s, "");
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @return
     */
    public static String processOK(String s, String s1) {
        String s2 = "/bin/ps -ef";
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s2, s1, monitorLock);
        String s3 = "not running";
        int i = 0;
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            String s4 = (String) enumeration.nextElement();
            s4 = s4.trim();
            String s5 = TextUtils.readColumn(s4, 3);
            if (s5.equals(s) && s4.indexOf("defunct") != -1)
                i++;
            String s6 = TextUtils.readColumn(s4, 2);
            if (s6.equals(s))
                s3 = "";
        }
        
        if (i >= 10)
            return "defunct";
        else
            return s3;
    }

    public static String psCommand(String s) {
        return psCommand(s, getOs());
    }

    public static String psCommand(String s, int i) {
        String s1 = null;
        if (s.startsWith("http://")) {
            s1 = s + "SendModem.exe?-cgi+-s";
            return s1;
        }
        switch (i) {
        case 1: // '\001'
            s1 = perfexCommand(s) + " -s";
            break;

        case 2: // '\002'
            s1 = "/usr/bin/ps -ef";
            break;

        case 6: // '\006'
            s1 = "/bin/ps -ef";
            break;

        case 3: // '\003'
            s1 = "/usr/bin/ps -eo args";
            break;

        case 5: // '\005'
            s1 = "/usr/bin/ps -ef";
            break;
        }
        return s1;
    }

    public static String psCommand(String s, boolean flag) {
        int i = Machine.getOS(s);
        String s1;
        if (flag)
            s1 = psDetailCommand(s, i);
        else
            s1 = psCommand(s, i);
        String s2 = flag ? "processDetail" : "process";
        String s3 = Machine.getCommandString(s2, s);
        if (s3.length() > 0)
            s1 = s3;
        return s1;
    }

    public static String psDetailCommand(String s) {
        return psDetailCommand(s, getOs());
    }

    public static String psDetailCommand(String s, int i) {
        String s1 = null;
        if (s.startsWith("http://")) {
            s1 = s + "perfex.exe?" + URLEncoder.encode("-cgi =230");
            return s1;
        }
        switch (i) {
        case 1: // '\001'
            s1 = perfexCommand(s) + " =230";
            break;

        case 6: // '\006'
            s1 = "/bin/ps -el";
            break;

        case 2: // '\002'
            s1 = "/usr/bin/ps -el";
            break;

        case 3: // '\003'
            s1 = "/usr/bin/ps -el";
            break;

        case 5: // '\005'
            s1 = "/usr/bin/ps -el";
            break;
        }
        return s1;
    }

    public static boolean isNTRemote(String s) {
        return s.length() > 0 && s.charAt(0) == '\\';
    }

    public static boolean isRemote(String s) {
        return isNTRemote(s) || isCommandLineRemote(s);
    }

    public static boolean isCommandLineRemote(String s) {
        return s.length() > 0
                && TextUtils.startsWithIgnoreCase(s, Machine.REMOTE_PREFIX);
    }

    public static String dirCommand(int i, int j) {
        String s = null;
        switch (i) {
        case 1: // '\001'
            s = "dir /B";
            if (j == DIR_DIRECTORIES)
                s = s + " /AD";
            else if (j == DIR_FILES)
                s = s + "/A-D";
            break;

        default:
            s = "ls -A -1";
            if (j == DIR_DIRECTORIES) {
                s = s + " -p";
                break;
            }
            if (j == DIR_FILES)
                s = s + " -p";
            break;
        }
        return s;
    }

    public static String dirCommand(String s, int i) {
        String s1 = Machine.getCommandString("directory", s);
        if (s1.length() == 0)
            s1 = dirCommand(Machine.getOS(s), i);
        return s1;
    }

    public static String currentDirectoryCommand(String s) {
        String s1 = Machine.getCommandString("currentDirectory", s);
        if (s1.length() == 0)
            s1 = currentDirectoryCommand(Machine.getOS(s));
        return s1;
    }

    public static String currentDirectoryCommand(int i) {
        String s = "pwd";
        switch (i) {
        case 1: // '\001'
            s = "cd";
            break;

        default:
            s = "pwd";
            break;
        }
        return s;
    }

    public static String pathSeparator(int i) {
        String s = "/";
        switch (i) {
        case 1: // '\001'
            s = "\\";
            break;

        default:
            s = "/";
            break;
        }
        return s;
    }

    public static long[] processCPU(String s, String s1) {
        return processCPU(s, s1, null, null);
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param atomicmonitor
     * @param array
     * @return
     */
    public static long[] processCPU(String s, String s1,
            AtomicMonitor atomicmonitor, Array array) {
        long al[] = new long[4];
        al[0] = 0L;
        al[1] = 0L;
        al[2] = 0L;
        al[3] = 0L;
        int i = 1;
        byte byte0 = 2;
        byte byte1 = 3;
        Array array1 = null;
        String s2 = perfexCommand(s) + " -pc =230";
        if (s.startsWith("http://"))
            s2 = s + "perfex.exe?" + URLEncoder.encode("-cgi -pc =230");
        String s3 = s;
        if (s3.startsWith("\\\\"))
            s3 = s3.substring(2);
        Machine machine = Machine.getNTMachine(s3);
        if (machine != null && Machine.isNTSSH(s)) {
            if (s2.indexOf("\\\\" + s3) > 0)
                s2 = TextUtils.replaceString(s2, "\\\\" + s3, "");
            s2 = s2.substring(s2.indexOf("perfex"));
            SSHCommandLine sshcommandline = new SSHCommandLine();
            array1 = sshcommandline.exec(s2, machine, false);
        } else {
            CommandLine commandline = new CommandLine();
            array1 = commandline.exec(s2, getLock(s), atomicmonitor);
        }
        Enumeration enumeration = array1.elements();
        if (array != null) {
            array.copy(array1);
        }
        boolean flag = false;
        long l = -1L;
        long l1 = 0L;
        while (enumeration.hasMoreElements()) {
            String s4 = (String) enumeration.nextElement();
            if (l < 0L)
                l = TextUtils.findLong(s4, "PerfTime100nSec:", null);
            else if (s4.startsWith("processorCount:"))
                l1 = TextUtils.findLong(s4, "processorCount:", null);
            if (s4.startsWith("name:")) {
                String s5 = s4.substring(5);
                s5 = s5.trim();
                flag = false;
                if (s5.equals(s1) || TextUtils.match(s5, s1))
                    flag = true;
            } else if (s4.length() == 0)
                flag = false;
            else if (flag) {
                long l2 = getPerfData(s4, "184:", "PERF_COUNTER_RAWCOUNT");
                if (l2 == -1L)
                    l2 = getPerfData(s4, "184:", "PERF_COUNTER_LARGE_RAWCOUNT");
                if (l2 != -1L)
                    al[byte1] += l2;
                long l3 = getPerfData(s4, "6:", "% PERF_100NSEC_TIMER");
                if (l3 != -1L) {
                    al[i] += l3;
                    al[byte0]++;
                }
            }
        } 
        
        if (l1 <= 0L)
            l1 = 1L;
        al[0] = l * l1;
        return al;
    }

    public static long[] processUsed(String s, String s1, long l, long l1) {
        return processUsed(s, s1, l, l1, null, null);
    }

    public static long[] processUsed(String s, String s1, long l, long l1,
            AtomicMonitor atomicmonitor, Array array) {
        long l2 = 0L;
        long l3 = 0L;
        long l4 = 0L;
        if (l == 0L) {
            long al[] = processCPU(s, s1, atomicmonitor, array);
            l = al[0];
            l1 = al[1];
            sleep(2000L);
        }
        long al1[] = processCPU(s, s1, atomicmonitor, array);
        l3 = al1[0];
        l4 = al1[1];
        long l5 = al1[2];
        long l6 = al1[3];
        long l7 = l3 - l;
        if (l7 > 0L) {
            l2 = (100L * (l4 - l1)) / l7;
        } else {
            l = l3;
            l1 = l4;
            sleep(2000L);
            long al2[] = processCPU(s, s1, atomicmonitor, array);
            l3 = al2[0];
            l4 = al2[1];
            l5 = al2[2];
            l6 = al2[3];
            long l8 = l3 - l;
            if (l8 > 0L)
                l2 = 100L - (100L * (l4 - l1)) / l8;
        }
        if (l2 < 0L)
            l2 = 0L;
        long al3[] = { l2, l5, l3, l4, l6 };
        return al3;
    }

    public static String timeZoneName(int i) {
        Hashtable hashtable = timeZones();
        String s = (String) hashtable.get("" + i);
        if (s == null)
            return "";
        int j = (25200 - i) / 3600;
        String s1;
        if (j < 0) {
            j = -j;
            s1 = "GMT +" + TextUtils.numberToString(j) + ":00";
        } else if (j > 0)
            s1 = "GMT -" + TextUtils.numberToString(j) + ":00";
        else
            s1 = "GMT";
        if (s.length() > 0)
            s1 = s + ", " + s1;
        return s1;
    }

    public static Hashtable timeZones() {
        if (timeZoneMap == null) {
            timeZoneMap = new Hashtable();
            timeZoneMap.put("72000", "");
            timeZoneMap.put("68400", "Auckland");
            timeZoneMap.put("64800", "");
            timeZoneMap.put("61200", "Melbourne, Sydney");
            timeZoneMap.put("57600", "Tokyo, Seoul");
            timeZoneMap.put("54000", "Hong Kong, Perth");
            timeZoneMap.put("50400", "Bangkok, Jakarta");
            timeZoneMap.put("46800", "Dhaka");
            timeZoneMap.put("43200", "");
            timeZoneMap.put("39600", "");
            timeZoneMap.put("36000", "Moscow");
            timeZoneMap.put("32400", "Helsinki, Eastern Europe, Israel");
            timeZoneMap.put("28800", "Paris, Berlin, Stockholm, Amsterdam");
            timeZoneMap.put("25200", "London");
            timeZoneMap.put("21600", "");
            timeZoneMap.put("18000", "Mid-Atlantic");
            timeZoneMap.put("14400", "Brasilia, Buenos Aires");
            timeZoneMap.put("10800", "Atlantic Time");
            timeZoneMap.put("7200", "Eastern Time");
            timeZoneMap.put("3600", "Central Time");
            timeZoneMap.put("0", "Mountain Time");
            timeZoneMap.put("-3600", "Pacific Time");
            timeZoneMap.put("-7200", "Alaska");
            timeZoneMap.put("-10800", "Hawaii");
            timeZoneMap.put("-14400", "");
            timeZoneMap.put("-18000", "");
            timeZoneMap.put("-21600", "");
        }
        return timeZoneMap;
    }

    /**
     * 
     * 
     * @param s
     * @param i
     * @param j
     * @param atomicmonitor
     * @param array
     * @return
     * @throws SiteViewException
     */
    public static long[] processorCPU(String s, int i, int j,
            AtomicMonitor atomicmonitor, Array array) throws SiteViewException {
        int k = 3 + i;
        long al[] = new long[k];
        for (int l = 0; l < k; l++)
            al[l] = -1L;

        String s1 = cpuCommand(s, j);
        if ((platformDebugTrace & kDebugCPU) != 0)
            LogManager.log("RunMonitor", "[kDebugCPU] Command: " + s1);
        String s2 = s;
        if (s2.startsWith("\\\\"))
            s2 = s2.substring(2);
        Machine machine = Machine.getNTMachine(s2);
        Array array1 = null;
        int i1 = 0;
        CommandLine commandline = new CommandLine();
        array1 = commandline.exec(s1, s, getLock(s), atomicmonitor);
        i1 = commandline.getExitValue();
        if (array != null)
            array.copy(array1);
        Enumeration enumeration = array1.elements();
        if (i1 != 0) {
            String s3 = "";
            if (atomicmonitor != null)
                s3 = " monitor: " + atomicmonitor.getFullID();
            if (i1 == 999) {
                LogManager
                        .log("Error", "CPU Monitor Timed Out(" + s + ")" + s3);
                throw new SiteViewAvailabilityException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_MONITOR_TIMED_OUT);
            } else {
                LogManager.log("Error", "CPU Monitor error (" + s + "): " + i1
                        + s3);
                throw new SiteViewAvailabilityException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL);
            }
        }
        long l1 = -1L;
        long l2 = -1L;
        long l3 = 0L;
        int j1 = 0;
        boolean flag = false;
        while (enumeration.hasMoreElements()) {
            String s4 = (String) enumeration.nextElement();
            if ((platformDebugTrace & kDebugCPU) != 0)
                LogManager.log("RunMonitor", "[kDebugCPU] Results: " + s4);
            if (l2 < 0L)
                l2 = TextUtils.findLong(s4, "PerfTime100nSec:", null);
            if (s4.startsWith("name: _Total"))
                flag = false;
            else if (s4.startsWith("name: "))
                flag = true;
            else if (s4.length() == 0)
                flag = false;
            else if (flag) {
                long l4 = getPerfData(s4, "6:", "% PERF_100NSEC_TIMER_INV");
                if (l4 != -1L) {
                    if ((platformDebugTrace & kDebugCPU) != 0)
                        LogManager.log("RunMonitor",
                                "[kDebugCPU] Measurement - CPU# " + j1
                                        + " Value: " + l4);
                    if (3 + j1 < al.length)
                        al[3 + j1] = l4;
                    if (l1 == -1L)
                        l1 = l4;
                    else
                        l1 += l4;
                    l3++;
                    j1++;
                }
            }
        }
        
        al[0] = l2 * l3;
        al[1] = l1;
        al[2] = l3;
        if ((platformDebugTrace & kDebugCPU) != 0)
            LogManager.log("RunMonitor",
                    "[kDebugCPU] CPU Final Results - Time: " + al[0]
                            + " Measurement: " + al[1] + " # CPUS: " + al[2]);
        return al;
    }

    static long[] cpuUsedWindows(String s, long l, long l1, long al[],
            AtomicMonitor atomicmonitor, Array array) throws SiteViewException {
        int i = al.length;
        int j = 4 + i;
        long al1[] = new long[j];
        for (int k = 0; k < j; k++)
            al1[k] = -1L;

        long l2 = -1L;
        long l3 = 0L;
        long l4 = 0L;
        long l5 = 1L;
        if (l == 0L) {
            long al2[] = processorCPU(s, i, 1, atomicmonitor, array);
            if (al2 == null)
                return al1;
            l = al2[0];
            l1 = al2[1];
            for (int i1 = 0; i1 < i; i1++)
                al[i1] = al2[3 + i1];

            sleep(2000L);
        }
        long al3[] = processorCPU(s, i, 1, atomicmonitor, array);
        if (al3 == null)
            return al1;
        l3 = al3[0];
        l4 = al3[1];
        l5 = al3[2];
        long l6 = l3 - l;
        if (l6 > 0L)
            l2 = 100L - (100L * (l4 - l1)) / l6;
        if (l6 <= 0L || l4 - l1 < 0L || l3 == 0L || l == 0L || l2 < 0L
                || l2 > 100L) {
            l2 = -1L;
            throw new SiteViewAvailabilityException(
                    SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL);
        }
        long l7 = 0L;
        l7 = l6;
        if (l5 > 0L)
            l7 = l6 / l5;
        for (int j1 = 0; (long) j1 < l5; j1++) {
            if (al[j1] != 0L)
                al1[4 + j1] = 100L - (100L * (al3[3 + j1] - al[j1])) / l7;
            if (al1[4 + j1] < 0L)
                al1[4 + j1] = 100L - (100L * (al3[3 + j1] - al[j1])) / l6;
            if (al1[4 + j1] < 0L || al1[4 + j1] > 100L) {
                LogManager.log("error",
                        "Invalid CPU value.  Platform.cpuUsed: " + s + " cpu "
                                + j1 + " = " + al1[4 + j1] + "measurements = "
                                + al3[3 + j1] + ", last = " + al[j1]
                                + ", interval = " + l7 + ", mt = " + l3
                                + ", lmt = " + l);
                return null;
            }
            if ((platformDebugTrace & kDebugCPU) != 0)
                LogManager.log("RunMonitor", "[kDebugCPU] CPU# " + j1
                        + " last: " + al[j1] + " Measure: " + al3[3 + j1]
                        + " Used: " + al1[4 + j1]);
            al[j1] = al3[3 + j1];
        }

        al1[0] = l2;
        al1[1] = l3;
        al1[2] = l4;
        al1[3] = l5;
        return al1;
    }

    public static long[] cpuUsed(String s, long l, long l1, long al[])
            throws SiteViewException {
        return cpuUsed(s, l, l1, al, null, null);
    }

    public static long[] cpuUsed(String s, long l, long l1, long al[],
            AtomicMonitor atomicmonitor, Array array) throws SiteViewException {
        long al1[] = null;
        int i = al.length;
        int j = Machine.getOS(s);
        if (j == 1) {
            if (atomicmonitor != null)
                atomicmonitor.currentStatus = "CPUMonitor retrieving the CPU Used on windows machine...";
            al1 = cpuUsedWindows(s, l, l1, al, atomicmonitor, array);
            if (al1 == null)
                cpuUsedWindows(s, 0L, l1, al, atomicmonitor, array);
        } else {
            if (atomicmonitor != null)
                atomicmonitor.currentStatus = "CPUMonitor retrieving the CPU Used on unix machine...";
            int k = 4 + i;
            al1 = new long[k];
            for (int i1 = 0; i1 < k; i1++)
                al1[i1] = -1L;

            long l2 = -1L;
            long l3 = 0L;
            long l4 = 0L;
            long l5 = 1L;
            CommandLine commandline = new CommandLine();
            String s1 = Machine.getCommandString("cpu", s);
            if (s1.length() == 0)
                s1 = cpuCommand(s, j);
            Array array1 = commandline.exec(s1, s, monitorLock, atomicmonitor);
            if (array != null)
                array.copy(array1);
            int j1 = -1;
            int k1 = 0;
            int i2 = -1;
            int j2 = 0;
            int k2 = -1;
            HashMap hashmap = new HashMap();
            OSAdapter osadapter = Machine.getAdapter(s);
            if (osadapter == null) {
                LogManager.log("Error", "Could not get adapter for machine "
                        + s);
                throw new SiteViewOperationalException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_NO_MACHINE_ADAPTOR);
            }
            j1 = osadapter.getCommandSettingAsInteger("cpu", "wait", j1);
            k1 = osadapter.getCommandSettingAsInteger("cpu", "idle", k1);
            i2 = osadapter.getCommandSettingAsInteger("cpu", "cpu", i2);
            LineReader linereader = new LineReader(array1, osadapter, "cpu");
            do {
                if (!linereader.processLine())
                    break;
                if (!linereader.skipLine()) {
                    String s2 = "";
                    if (j1 >= 0)
                        s2 = linereader.readColumn(j1, "wait");
                    String s3 = linereader.readColumn(k1, "idle");
                    int j4 = -1;
                    if (i2 >= 0) {
                        String s4 = linereader.readColumn(i2, "cpu");
                        String s5 = osadapter.getCommandSetting("cpu",
                                "cpuNamePrefix", "CPU");
                        if (s4.toUpperCase().startsWith(s5.toUpperCase()))
                            s4 = s4.substring(s5.length());
                        j4 = TextUtils.toInt(s4);
                        if (TextUtils.toInt(s4) > j2)
                            j2 = TextUtils.toInt(s4);
                    }
                    boolean flag = false;
                    boolean flag1 = false;
                    if (s3.length() > 0) {
                        int k4 = TextUtils.readInteger(s2, 0);
                        if (k4 == -1)
                            k4 = 0;
                        int i5 = TextUtils.readInteger(s3, 0);
                        if (i5 != -1) {
                            int j5 = k4 + i5;
                            if (j5 < 0)
                                j5 = 0;
                            if (j5 > 100)
                                j5 = 100;
                            k2 = j5;
                            if (j4 >= 0) {
                                Integer integer2 = (Integer) hashmap.get(""
                                        + j4);
                                if (integer2 == null)
                                    hashmap.put("" + j4, new Integer(j5));
                            }
                        }
                    }
                }
            } while (true);
            if (k2 != -1) {
                if (hashmap.size() == 0) {
                    l2 = 100 - k2;
                    l5 = 1L;
                } else if (hashmap.size() == 1) {
                    Integer integer = (Integer) hashmap.get("" + j2);
                    if (integer != null) {
                        int i3 = integer.intValue();
                        l2 = 100 - i3;
                        l5 = 1L;
                    }
                } else {
                    l5 = hashmap.size();
                    if (l5 > (long) i)
                        l5 = i;
                    int j3 = 0;
                    int k3 = 0;
                    for (int i4 = 0; i4 <= j2; i4++) {
                        Integer integer1 = (Integer) hashmap.get("" + i4);
                        if (integer1 == null)
                            continue;
                        al1[4 + k3] = 100 - integer1.intValue();
                        j3 += integer1.intValue();
                        al[k3] = 1L;
                        if (++k3 >= i)
                            break;
                    }

                    l2 = (100L * l5 - (long) j3) / l5;
                    if (l2 < 0L)
                        l2 = 0L;
                    if (l2 > 100L)
                        l2 = 100L;
                }
            } else {
                throw new SiteViewAvailabilityException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL);
            }
            al1[0] = l2;
            al1[1] = l3;
            al1[2] = l4;
            al1[3] = l5;
        }
        return al1;
    }

    static String pageFaultCommand(int i) {
        String s = null;
        switch (i) {
        case 5: // '\005'
            s = "/usr/bin/vmstat -s";
            break;

        case 6: // '\006'
            s = "/usr/bin/vmstat -n 3 2";
            break;

        case 2: // '\002'
            s = "/usr/bin/vmstat -s";
            break;

        case 3: // '\003'
            s = "/usr/bin/sar -p 3";
            break;
        }
        return s;
    }

    public static String cpuCommand(String s, int i) {
        String s1 = null;
        if (s.startsWith("http://")) {
            s1 = s + "perfex.exe?" + URLEncoder.encode("-cgi =238");
            return s1;
        }
        switch (i) {
        case 1: // '\001'
            s1 = perfexCommand(s) + " =238";
            break;

        case 2: // '\002'
            s1 = "/usr/bin/mpstat 3 2";
            break;

        case 6: // '\006'
            s1 = "top p 0 n 3 d 2 b";
            break;

        case 5: // '\005'
            s1 = "/usr/sbin/sar 3";
            break;

        case 3: // '\003'
            s1 = "/usr/bin/sar 3";
            break;
        }
        return s1;
    }

    public static Calendar getSystemTime(String s, StringBuffer stringbuffer)
            throws SiteViewException {
        int i = 0;
        Calendar calendar = null;
        Machine machine = null;
        if (s.equals("") || Machine.isLocalHostname(s))
            return new GregorianCalendar();
        if ((machine = Machine.getMachineByHost(s)) != null) {
            String s1 = "remote:" + machine.getProperty(Machine.pID);
            HashMap hashmap = new HashMap();
            String s3 = Machine.getCommandString("systemTime", s1, hashmap);
            Array array1 = null;
            RemoteCommandLine remotecommandline = null;
            remotecommandline = Machine.getRemoteCommandLine(machine);
            if (isUnix(Machine.getOS(s1)))
                s3 = s3 + " | cat";
            array1 = remotecommandline.exec(s3, machine);
            i = remotecommandline.exitValue;
            if (array1 != null && array1.size() >= 1
                    && remotecommandline.exitValue == 0)
                calendar = parseDateFromUnixDateOutput(array1, stringbuffer);
        } else {
            if (!Machine.isNetBIOSFormattedHostname(s))
                s = "\\\\" + s;
            String s2 = "net time " + s;
            CommandLine commandline = new CommandLine();
            Array array = commandline.exec(s2, s, getLock(s), null);
            i = commandline.exitValue;
            if (array != null && array.size() >= 1
                    && commandline.exitValue == 0) {
                calendar = parseDateFromNetTimeOutput(array, s);
                if (calendar == null) {
                    stringbuffer
                            .append("getSystemTime failed to get time from "
                                    + s);
                    return null;
                }
                int j = parseSecondsFromPerfexDate(s);
                calendar.set(13, j);
            }
        }
        if (i != 0) {
            if (i == Monitor.kURLTimeoutError) {
                LogManager.log("Error", "getSystemTime Request Timed Out(" + s
                        + ")");
                throw new SiteViewAvailabilityException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_MONITOR_TIMED_OUT);
            } else {
                LogManager.log("Error", "getSystemTime Request error (" + s
                        + "): " + i);
                throw new SiteViewAvailabilityException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL);
            }
        } else {
            return calendar;
        }
    }

    private static Calendar parseDateFromUnixDateOutput(Array array,
            StringBuffer stringbuffer) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss");
        SimpleDateFormat simpledateformat1 = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss 'GMT'Z");
        Object obj = null;
        Date date1 = null;
        TimeZone timezone = null;
        int i = 0;
        i = 0;
        do {
            if (i >= array.size())
                break;
            String s = ((String) array.at(i)).trim();
            if (s.startsWith("ssDateStart"))
                break;
            i++;
        } while (true);
        if (i < array.size())
            i++;
        else
            i = 0;
        String s1 = ((String) array.at(i)).trim();
        String s2 = ((String) array.at(i + 1)).trim();
        try {
            Date date = simpledateformat.parse(s1);
            date1 = simpledateformat.parse(s2);
            long l = date1.getTime() - date.getTime();
            long l1 = l / 0x36ee80L;
            long l2 = l % 0x36ee80L;
            String s3 = l <= 0L ? "-" : "+";
            String s4 = "";
            String s5 = "";
            if (Math.abs(l1) < 10L)
                s4 = "0";
            if (Math.abs(l2) < 10L)
                s5 = "0";
            s4 = s4 + Long.toString(Math.abs(l1));
            s5 = s5 + Long.toString(Math.abs(l2));
            String s6 = "GMT" + s3 + s4 + s5;
            timezone = TimeZone.getTimeZone(s6);
            String s7 = s2 + " " + s6;
            date1 = simpledateformat1.parse(s7);
        } catch (ParseException parseexception) {
            stringbuffer.append("Date string in an unexpected format: ")
                    .append(parseexception.getMessage());
            return null;
        }
        GregorianCalendar gregoriancalendar = new GregorianCalendar();
        gregoriancalendar.setTimeZone(timezone);
        gregoriancalendar.setTime(date1);
        return gregoriancalendar;
    }

    private static Calendar parseDateFromNetTimeOutput(Array array, String s) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat(
                "'Current time at " + s + " is' M/d/yyyy hh:mm aa");
        SimpleDateFormat simpledateformat1 = new SimpleDateFormat(
                "'Local time ('z') at " + s + " is' M/d/yyyy hh:mm aa");
        Pattern pattern = Pattern.compile("Local time \\((.+)\\) at.*");
        Date date = null;
        Date date1 = null;
        TimeZone timezone = null;
        Object obj = null;
        for (int i = 0; i < array.size(); i++) {
            String s1 = (String) array.at(i);
            if (s1.startsWith("Current time at")) {
                try {
                    date = simpledateformat.parse(s1);
                } catch (ParseException parseexception) {
                    LogManager.log("Error",
                            "Error parsing 'net time' command's \"Current time\" output: "
                                    + parseexception.getMessage());
                }
                continue;
            }
            if (!s1.startsWith("Local time"))
                continue;
            try {
                date1 = simpledateformat1.parse(s1);
                Matcher matcher = pattern.matcher(s1);
                if (matcher.matches())
                    timezone = TimeZone.getTimeZone(matcher.group(1));
            } catch (ParseException parseexception1) {
                LogManager.log("Error",
                        "Error parsing 'net time' command's \"Local time\" output: "
                                + parseexception1.getMessage());
            }
        }

        if (date1 != null && timezone != null) {
            GregorianCalendar gregoriancalendar = new GregorianCalendar();
            gregoriancalendar.setTimeZone(timezone);
            gregoriancalendar.setTime(date1);
            return gregoriancalendar;
        }
        if (date != null) {
            GregorianCalendar gregoriancalendar1 = new GregorianCalendar();
            TimeZone timezone1 = SimpleTimeZone.getDefault();
            gregoriancalendar1.setTimeZone(timezone1);
            gregoriancalendar1.setTime(date);
            return gregoriancalendar1;
        } else {
            return null;
        }
    }

    private static int parseSecondsFromPerfexDate(String s) {
        int i = 0;
        String s1 = perfexCommand(s) + " -date";
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s1, s, getLock(s), null);
        if (array != null && commandline.exitValue == 0) {
            int j = 0;
            do {
                if (j >= array.size())
                    break;
                String s2 = (String) array.at(j);
                Pattern pattern = Pattern
                        .compile("Date:\\s\\d+\\-\\d+\\-\\d+\\s\\d+:\\d+:(\\d+)\\s.*");
                Matcher matcher = pattern.matcher(s2);
                if (matcher.matches()) {
                    i = Integer.parseInt(matcher.group(1));
                    break;
                }
                j++;
            } while (true);
        }
        return i;
    }

    public static String[][] getFileList(String s, String s1,
            StringBuffer stringbuffer) throws SiteViewException {
        Machine machine = null;
        if (Machine.isLocalHostname(s))
            return getFileListLocal(makeAbsolutePath(s1), stringbuffer);
        if (Machine.isNetBIOSFormattedHostname(s) || s1.startsWith("\\\\")) {
            String s2 = closeAndConnectNetBIOSIfRemoteDefined(s1);
            if (s2.length() > 0) {
                stringbuffer.append(
                        "getFileList encountered the following error: ")
                        .append(s2);
                return (String[][]) null;
            } else {
                return getFileListLocal(s1, stringbuffer);
            }
        }
        if ((machine = Machine.getMachineByHost(s)) != null) {
            String s3 = "remote:" + machine.getProperty(Machine.pID);
            HashMap hashmap = new HashMap();
            hashmap.put("directory", s1);
            String s4 = Machine.getCommandString("fileList", s3, hashmap);
            Array array = null;
            RemoteCommandLine remotecommandline = null;
            remotecommandline = Machine.getRemoteCommandLine(machine);
            if (isUnix(Machine.getOS(s3)))
                s4 = s4 + " | cat";
            array = remotecommandline.exec(s4, machine);
            if (array != null && remotecommandline.exitValue == 0)
                return parseUnixFileList(array, stringbuffer);
            if (remotecommandline.exitValue == Monitor.kURLTimeoutError) {
                LogManager.log("Error", "getFileList Request Timed Out(" + s
                        + ")");
                throw new SiteViewAvailabilityException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_MONITOR_TIMED_OUT);
            } else {
                LogManager.log("Error", "getFileList Request error (" + s
                        + "): " + remotecommandline.exitValue);
                throw new SiteViewAvailabilityException(
                        SiteViewErrorCodes.ERR_AVAIL_SS_GENERAL);
            }
        } else {
            stringbuffer.append(
                    "getFileList did not recognize the format of server: ")
                    .append(s).append(" and directory: ").append(s1);
            return (String[][]) null;
        }
    }

    private static String[][] getFileListLocal(String s,
            StringBuffer stringbuffer) {
        File file = new File(s);
        FileFilter filefilter = new FileFilter() {

            public boolean accept(File file2) {
                return !file2.isDirectory();
            }

        };
        File afile[] = file.listFiles(filefilter);
        if (afile == null) {
            stringbuffer.append("getFileList could not locate the directory: ")
                    .append(s);
            return (String[][]) null;
        }
        String as[][] = new String[afile.length][];
        for (int i = 0; i < afile.length; i++) {
            File file1 = afile[i];
            as[i] = new String[2];
            as[i][0] = file1.getName();
            as[i][1] = Long.toString(file1.length());
        }

        return as;
    }

    public static String makeAbsolutePath(String s) {
        File file = new File(s);
        if (file.isAbsolute())
            return file.getAbsolutePath();
        else
            return getRoot() + File.separator + s;
    }

    private static String[][] parseUnixFileList(Array array,
            StringBuffer stringbuffer) {
        String as[][] = new String[array.size()][2];
        for (int i = 0; i < array.size(); i++) {
            String s = ((String) array.at(i)).trim();
            String as1[] = s.split("!");
            if (as1.length == 2) {
                String s1 = as1[1];
                String s2 = as1[0];
                as[i] = (new String[] { s1, s2 });
            } else {
                stringbuffer
                        .append("getFileList encountered unrecognized output from 'ls' command");
                return (String[][]) null;
            }
        }

        return as;
    }

    /**
     * 
     * 
     * @param s
     * @param flag
     * @return
     */
    private static int contactUsingNetBIOS(String s, boolean flag) {
        int i = 200;
        if (!s.equals("this server") && s.length() != 0) {
            String s1 = perfexCommand(s)
                    + (flag ? " -connect" : " -disconnect");
            CommandLine commandline = new CommandLine();
            Array array = commandline.exec(s1, getLock(s));
            boolean flag1 = false;
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                String s2 = (String) enumeration.nextElement();
                int j = s2.indexOf("Connected to");
                if (j == -1)
                    continue;
                flag1 = true;
                break;
            } 
            
            if (!flag1)
                i = Monitor.kURLNoConnectionError;
        }
        return i;
    }

    private static String getHostname(String s) {
        if (s.startsWith("\\\\")) {
            int i = s.indexOf("\\", 2);
            if (i > 2)
                return s.substring(0, i);
        }
        return "";
    }

    public static String closeAndConnectNetBIOSIfRemoteDefined(String s) {
        String s1 = "";
        try {
            String s2 = getHostname(s);
            if (s2.length() > 0) {
                Machine machine = Machine.getNTMachine(s2);
                if (machine != null) {
                    contactUsingNetBIOS(s2, false);
                    int i = contactUsingNetBIOS(s2, true);
                    if (i == Monitor.kURLNoConnectionError) {
                        s1 = "SiteView was not able to connect to NT remote: <b>"
                                + machine.getProperty(Machine.pName);
                        s1 = s1
                                + ".</b>  The remote file needs to be mounted locally or the NT remote needs to test OK.";
                    }
                }
            }
        } catch (Exception exception) {
            s1 = s1 + " Error attempting to connect to NT remote: "
                    + exception.toString();
        }
        return s1;
    }

    static String killCommand() {
        String s = null;
        switch (getOs()) {
        case 1: // '\001'
            s = getRoot() + File.separator + "tools" + File.separator
                    + "kill.exe";
            break;

        case 2: // '\002'
            s = "/usr/sbin/killall";
            break;

        case 6: // '\006'
            s = "/usr/bin/killall";
            break;
        }
        return s;
    }

    static String netstatCommand(int i) {
        String s = null;
        switch (i) {
        case 1: // '\001'
            s = "netstat";
            break;

        case 2: // '\002'
            s = "/usr/bin/netstat";
            break;

        case 6: // '\006'
            s = "/bin/netstat";
            break;

        case 3: // '\003'
            s = "/usr/etc/netstat";
            break;

        case 5: // '\005'
            s = "/usr/bin/netstat";
            break;
        }
        return s;
    }

    static Array RegistryArray(byte abyte0[]) {
        Array array = split('\0', abyte0);
        String s = (String) array.back();
        if (s.length() == 0)
            array.popBack();
        return array;
    }

    static Array split(char c, byte abyte0[]) {
        char ac[] = new char[abyte0.length];
        for (int i = 0; i < abyte0.length; i++)
            ac[i] = (char) abyte0[i];

        return split(c, ac);
    }

    static Array split(char c, char ac[]) {
        Array array = new Array();
        int i = 0;
        for (int j = 0; j < ac.length; j++)
            if (ac[j] == c) {
                String s1 = new String(ac, i, j - i);
                array.add(s1);
                i = j + 1;
            }

        if (i != ac.length) {
            String s = new String(ac, i, ac.length - i);
            array.add(s);
        }
        return array;
    }

    public static Array split(char c, String s) {
        Array array = new Array();
        int i = 0;
        for (int j = 0; j < s.length(); j++)
            if (s.charAt(j) == c) {
                String s2 = s.substring(i, j);
                array.add(s2);
                i = j + 1;
            }

        if (i != s.length()) {
            String s1 = s.substring(i, s.length());
            array.add(s1);
        }
        return array;
    }

    public static String dottedIPString(byte abyte0[]) {
        int i = abyte0[0];
        if (i < 0)
            i += 256;
        int j = abyte0[1];
        if (j < 0)
            j += 256;
        int k = abyte0[2];
        if (k < 0)
            k += 256;
        int l = abyte0[3];
        if (l < 0)
            l += 256;
        return i + "." + j + "." + k + "." + l;
    }

    public static boolean isDottedIP(String s) {
        if (s.length() < 7)
            return false;
        int i = 0;
        for (int j = 0; j < s.length(); j++) {
            char c = s.charAt(j);
            if (c == '.') {
                i++;
                continue;
            }
            if (!Character.isDigit(c))
                return false;
        }

        return i == 3;
    }

    public static Array getIPAddresses(String s) {
        Array array = new Array();
        if (s.length() > 0) {
            Array array1 = new Array();
            if (s.startsWith("\\")) {
                String s1 = perfexCommand(s) + " -ip";
                String s2 = s;
                if (s2.startsWith("\\\\"))
                    s2 = s2.substring(2);
                Machine machine = Machine.getNTMachine(s2);
                if (machine != null && Machine.isNTSSH(s2)) {
                    if (s1.indexOf("\\\\" + s2) > 0)
                        s1 = TextUtils.replaceString(s1, "\\\\" + s2, "");
                    s1 = s1.substring(s1.indexOf("perfex"));
                    SSHCommandLine sshcommandline = new SSHCommandLine();
                    array1 = sshcommandline.exec(s1, machine, false);
                } else {
                    CommandLine commandline1 = new CommandLine();
                    array1 = commandline1.exec(s1);
                }
            } else {
                int i = Machine.getOS(s);
                String s3 = netstatCommand(i) + " -i";
                CommandLine commandline = new CommandLine();
                array1 = commandline.exec(s3, s);
            }
            for (Enumeration enumeration = array1.elements(); enumeration
                    .hasMoreElements();) {
                String s4 = (String) enumeration.nextElement();
                String as[] = TextUtils.split(s4, " ");
                int k = 0;
                while (k < as.length) {
                    String s6 = as[k].trim();
                    if (isDottedIP(s6) && !s6.equals("0.0.0.0"))
                        array.add(s6);
                    k++;
                }
            }

        } else {
            try {
                InetAddress inetaddress = InetAddress.getLocalHost();
                InetAddress ainetaddress[] = InetAddress
                        .getAllByName(inetaddress.getHostName());
                for (int j = 0; j < ainetaddress.length; j++) {
                    String s5 = dottedIPString(ainetaddress[j].getAddress());
                    if (!s5.equals("127.0.0.1"))
                        array.pushFront(s5);
                }

            } catch (Exception exception) {
            }
        }
        return array;
    }

    public static String resolveDNS(String s) {
        String as[] = dnsLookup("", s, "");
        if (TextUtils.toInt(as[0]) == Monitor.kURLok) {
            String s1 = as[2];
            String s2 = "Address:  ";
            String s4 = "Aliases:";
            if (s1.indexOf(s2) >= 0) {
                if (s1.substring(s2.length()).indexOf(".") > 0) {
                    if (s1.indexOf(s4) > 0)
                        s = s1.substring(s2.length(), s1.indexOf(s4));
                    else
                        s = s1.substring(s2.length());
                    s = s.trim();
                }
            } else {
                String s3 = "Addresses:  ";
                if (s1.indexOf(s3) >= 0) {
                    String as1[] = TextUtils.split(s1.substring(s3.length()),
                            ",");
                    if (as1[0].indexOf(".") > 0) {
                        s = as1[0];
                        s = s.trim();
                    }
                }
            }
        }
        return s;
    }

    public static String getDefaultIPAddress() {
        String s = "localhost";
        Array array = getIPAddresses("");
        if (array.size() > 0)
            s = (String) array.at(0);
        return s;
    }

    /**
     * 
     * 
     * @return
     */
    public static Array getDNSAddresses() {
        //TODO need review
        Array array;
        label0: {
            array = new Array();
            if (getOs() == 1)
                try {
                    CommandLine commandline = new CommandLine();
                    Array array1 = commandline.exec(nslookupCommand()
                            + " demo.siteview.com");
                    Enumeration enumeration = array1.elements();
                    if (array1.size() <= 0)
                        break label0;
                    String s3;
                    do {
                        String s2;
                        do {
                            if (!enumeration.hasMoreElements())
                                break label0;
                            s2 = (String) enumeration.nextElement();
                            s2 = s2.trim();
                        } while (s2.indexOf("Address:") != 0);
                        s3 = TextUtils.readColumn(s2, 2);
                    } while (s3.length() == 0);
                    array.add(s3);
                } catch (Exception exception) {
                }
            else
                try {
                    BufferedReader bufferedreader = FileUtils
                            .MakeInputReader(new FileInputStream(
                                    "/etc/resolv.conf"));
                    do {
                        String s;
                        if ((s = bufferedreader.readLine()) == null)
                            break;
                        if (s.indexOf("nameserver") != -1) {
                            String s1 = TextUtils.readColumn(s, 2);
                            Array array2 = split('.', s1);
                            if (array2.size() == 4 && !s1.equals("0.0.0.0"))
                                array.add(s1);
                        }
                    } while (true);
                    bufferedreader.close();
                } catch (FileNotFoundException filenotfoundexception) {
                } catch (IOException ioexception) {
                }
        }
        return array;
    }

    public static String getSysInfo() {
        String s = "";
        if (getOs() == 3)
            try {
                CommandLine commandline = new CommandLine();
                Array array = commandline.exec("/sbin/sysinfo -s");
                Enumeration enumeration = array.elements();
                if (array.size() > 0)
                    s = Long.toHexString(TextUtils.toLong((String) enumeration
                            .nextElement()));
                commandline = new CommandLine();
                array = commandline.exec("/sbin/uname -a");
                enumeration = array.elements();
                if (array.size() > 0)
                    s = s + " " + (String) enumeration.nextElement();
            } catch (Exception exception) {
            }
        return s;
    }

    /**
     * 
     * 
     * @param s
     * @return
     * @throws SiteViewException
     */
    public static Vector getDisks(String s) throws SiteViewException {
        Vector vector = new Vector();
        int i = Machine.getOS(s);
        String s1 = Machine.getCommandString("disks", s);
        if (s1.length() == 0)
            s1 = dfCommand("", s, i);
        String s2 = s;
        if (s2.startsWith("\\\\"))
            s2 = s2.substring(2);
        Machine machine = Machine.getNTMachine(s2);
        Array array = null;
        if (machine != null && Machine.isNTSSH(s)) {
            if (s1.indexOf("\\\\" + s2) > 0)
                s1 = TextUtils.replaceString(s1, "\\\\" + s2, "");
            s1 = s1.substring(s1.indexOf("perfex"));
            SSHCommandLine sshcommandline = new SSHCommandLine();
            array = sshcommandline.exec(s1, machine, true);
        } else {
            CommandLine commandline = new CommandLine();
            array = commandline.exec(s1, s);
        }
        Enumeration enumeration = array.elements();
        if (i == 1) {
            boolean flag = false;
            while (enumeration.hasMoreElements()) {
                String s3 = (String) enumeration.nextElement();
                if (flag) {
                    if (s3.indexOf("object:") == 0)
                        break;
                    String s4 = "name: ";
                    int l = s3.indexOf(s4);
                    if (l != -1 && s3.indexOf("Total") == -1) {
                        String s5 = s3.substring(l + s4.length());
                        if (s5.endsWith(":"))
                            s5 = s5.substring(0, s5.length() - 1);
                        vector.addElement(s5);
                        vector.addElement(s5);
                    }
                } else if (s3.indexOf("object: 236") == 0)
                    flag = true;
            } 
        } else {
            int j = 1;
            int k = 6;
            OSAdapter osadapter = Machine.getAdapter(s);
            if (osadapter == null) {
                LogManager.log("Error", "Could not get adapter for machine "
                        + s);
                return vector;
            }
            j = osadapter.getCommandSettingAsInteger("disks", "name");
            k = osadapter.getCommandSettingAsInteger("disks", "mount");
            boolean flag1 = osadapter
                    .getCommandSetting("disks", "noNameFilter").length() > 0;
            LineReader linereader = new LineReader(array, osadapter, "disks");
            while (linereader.processLine()) {
                int i1 = k;
                if (!linereader.skipLine()) {
                    String s6 = linereader.getCurrentLine();
                    if (s6.length() > 1 && (flag1 || s6.indexOf("/dev") == 0)) {
                        String s8 = linereader.readColumn(j, "name");
                        if (s6.split(" ").length == 1) {
                            linereader.processLine();
                            i1--;
                        }
                        String s9 = linereader.readColumn(i1, "mount");
                        if (s9.length() == 0 && enumeration.hasMoreElements()) {
                            String s7 = (String) enumeration.nextElement();
                            s9 = linereader.readColumn(i1 - 1, "mount");
                        }
                        if (s8.length() == 0 || s9.length() == 0)
                            throw new SiteViewParameterException(
                                    SiteViewErrorCodes.ERR_PARAM_API_UNABLE_TO_RETRIEVE_DISKS,
                                    new String[] { s2 });
                        vector.addElement(s8);
                        vector.addElement(s8 + " (" + s9 + ")");
                    }
                }
            }
        }
        return vector;
    }

    static Array hackRead() {
        return hackRead(getRoot() + "/test.txt");
    }

    static Array hackRead(String s) {
        Array array = new Array();
        try {
            BufferedReader bufferedreader = FileUtils
                    .MakeInputReader(new FileInputStream(s));
            System.err.println("HACK READING=" + s);
            do {
                String s1 = bufferedreader.readLine();
                System.out.println("READING HACK LINE=" + s1);
                if (s1 == null)
                    break;
                array.add(s1);
            } while (true);
        } catch (Exception exception) {
            System.err.println("HACK READ FAILED=" + s);
        }
        return array;
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static Vector getWebServers(String s) {
        Vector vector = new Vector();
        String s1 = perfexCommand(s) + " -w";
        if (s.startsWith("http://"))
            s1 = s + "perfex.exe?-cgi -w";
        Array array = null;
        String s2 = s;
        if (s2.startsWith("\\\\"))
            s2 = s2.substring(2);
        Machine machine = Machine.getNTMachine(s2);
        if (machine != null && Machine.isNTSSH(s2)) {
            if (s1.indexOf("\\\\" + s2) > 0)
                s1 = TextUtils.replaceString(s1, "\\\\" + s2, "");
            s1 = s1.substring(s1.indexOf("perfex"));
            SSHCommandLine sshcommandline = new SSHCommandLine();
            array = sshcommandline.exec(s1, machine, false);
        } else {
            CommandLine commandline = new CommandLine();
            array = commandline.exec(s1);
        }
        Enumeration enumeration = array.elements();
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean flag5 = false;
        while (enumeration.hasMoreElements()) {
            String s3 = (String) enumeration.nextElement();
            if (flag3 && s3.indexOf("name: ") == 0) {
                String s4 = s3.substring(6, s3.length());
                vector.addElement("Netscape|" + s4);
                vector.addElement("Netscape server (" + s4 + ")");
            }
            if (flag4 && s3.indexOf("name: ") == 0) {
                String s5 = s3.substring(6, s3.length());
                vector.addElement("Netscape3|" + s5);
                vector.addElement("Netscape server (" + s5 + ")");
            }
            if (flag5 && s3.indexOf("name: ") == 0) {
                String s6 = s3.substring(6, s3.length());
                vector.addElement("Netscape35|" + s6);
                vector.addElement("Netscape server (" + s6 + ")");
            }
            if (s3.indexOf("object:") == 0) {
                flag3 = false;
                flag4 = false;
                flag5 = false;
            }
            if (s3.indexOf("object: Netscape Server") == 0)
                flag3 = true;
            else if (s3.indexOf("object: Netscape Enterprise 3.0") == 0)
                flag4 = true;
            else if (s3.indexOf("object: Netscape Enterprise 3.5") == 0)
                flag5 = true;
            else if (s3.indexOf("object: Web Service") == 0)
                flag1 = true;
            else if (s3.indexOf("object: HTTP Service") == 0)
                flag = true;
            else if (s3.indexOf("object: WebServer") == 0)
                flag2 = true;
        }
        
        if (flag) {
            vector.addElement("Microsoft|");
            vector.addElement("Microsoft IIS");
        }
        if (flag1) {
            vector.addElement("Microsoft4|");
            vector.addElement("Microsoft IIS");
        }
        if (flag2) {
            vector.addElement("WebSite|");
            vector.addElement("O'Reilly WebSite");
        }
        return vector;
    }

    public static boolean hasLibrary(String s) {
        String s1 = (String) hasLibraries.get(s);
        if (s1 == null) {
            CommandLine commandline = new CommandLine();
            String s2 = perfexCommand("") + " -x " + s;
            commandline.exec(s2);
            int i = commandline.getExitValue();
            s1 = "" + i;
            hasLibraries.put(s, s1);
        }
        return s1.equals("0");
    }

    public static int CheckPermissions(String s, HashMap hashmap, Array array) {
        if (s.equals("this server") || s.length() == 0)
            return 0;
        int i = 0;
        String s1 = s;
        String s2 = perfexCommand(s) + " =238";
        if (s1.startsWith("\\\\"))
            s1 = s1.substring(2);
        Machine machine = Machine.getNTMachine(s1);
        Array array1 = null;
        if (Machine.isNTSSH(s)) {
            if (s2.indexOf("\\\\" + s1) > 0)
                s2 = TextUtils.replaceString(s2, "\\\\" + s1, "");
            s2 = s2.substring(s2.indexOf("perfex"));
            SSHCommandLine sshcommandline = new SSHCommandLine();
            array1 = sshcommandline.exec(s2, machine, true);
            i = sshcommandline.exitValue;
        } else {
            if (isCommandLineRemote(s))
                return 0;
            CommandLine commandline = new CommandLine();
            if (s.startsWith("http://"))
                s2 = s + "perfex.exe?" + URLEncoder.encode("-cgi =238");
            if ((platformDebugTrace & kDebugPerfex) != 0)
                LogManager.log("RunMonitor",
                        "[kDebugPerfex]Check Permissions - Perfex command: "
                                + s2);
            array1 = commandline.exec(s2, s);
            i = commandline.getExitValue();
        }
        for (int j = 0; j < array1.size(); j++) {
            String s3 = (String) array1.at(j);
            array.add(s3.trim());
            if ((platformDebugTrace & kDebugPerfex) != 0)
                LogManager.log("RunMonitor",
                        "[kDebugPerfex]Check Permissions - Results: " + s3);
        }

        return i;
    }

    /**
     * 
     * 
     * @return
     */
    public static Vector getServers() {
        Vector vector = new Vector();
        vector.addElement("this server");
        vector.addElement("this server");
        String s = "net view";
        CommandLine commandline = new CommandLine();
        Array array = commandline.exec(s);
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            String s1 = (String) enumeration.nextElement();
            if (s1.startsWith("\\\\")) {
                String s2 = TextUtils.readColumn(s1, 1);
                vector.addElement(s2);
                vector.addElement(s2);
            }
        } 

        return vector;
    }

    static String currentUser() {
        return System.getProperty("user.name");
    }

    public static String netEthernetStatsCommand() {
        String s = null;
        switch (getOs()) {
        case 1: // '\001'
            s = "netstat -e";
        // fall through

        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        default:
            return s;
        }
    }

    public static String netConnectionStatsCommand() {
        String s = null;
        switch (getOs()) {
        case 1: // '\001'
            s = "netstat -n";
        // fall through

        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        default:
            return s;
        }
    }

    /**
     * 
     * 
     * @return
     */
    public static long[] getNetStats() {
        long al[] = new long[4];
        CommandLine commandline = new CommandLine();
        String s = netEthernetStatsCommand();
        String s1 = netConnectionStatsCommand();
        Array array = commandline.exec(s, monitorLock);
        Array array1 = commandline.exec(s1, monitorLock);
        if (isWindows()) {
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                String s2 = (String) enumeration.nextElement();
                if (!TextUtils.readColumn(s2, 1).equals("Bytes"))
                    continue;
                String s4 = TextUtils.readColumn(s2, 2);
                String s7 = TextUtils.readColumn(s2, 3);
                al[0] = s4.length() <= 0 ? -1L : TextUtils.readLong(s4, 0);
                al[1] = s7.length() <= 0 ? -1L : TextUtils.readLong(s7, 0);
                break;
            } 
            
            enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                String s3 = (String) enumeration.nextElement();
                if (!TextUtils.readColumn(s3, 1).equals("Errors"))
                    continue;
                String s5 = TextUtils.readColumn(s3, 2);
                String s8 = TextUtils.readColumn(s3, 3);
                if (s5.length() > 0 && s8.length() > 0)
                    al[2] = TextUtils.readLong(s5, 0)
                            + TextUtils.readLong(s8, 0);
                else
                    al[2] = -1L;
                break;
            }
            
            enumeration = array1.elements();
            int i = 0;
            while (enumeration.hasMoreElements()) {
                String s6 = (String) enumeration.nextElement();
                if (!TextUtils.readColumn(s6, 1).equals(""))
                    i++;
            } 
            
            if (i >= 2)
                al[3] = i - 2;
            else
                al[3] = -1L;
        }
        return al;
    }

    public static boolean restartSiteView() {
        return writeControlFile(new File(getRoot() + "/groups/restart.config"));
    }

    public static boolean shouldRestartSiteView() {
        File file = new File(getRoot() + "/groups/restart.config");
        if (file.exists())
            return file.delete();
        else
            return false;
    }

    /**
     * 
     * 
     * @param file
     * @return
     */
    public static boolean writeControlFile(File file) {
		if(file.getName().endsWith("firstFlash.dyn"))
			System.out.println("here");
        FileOutputStream fileoutputstream = null;
        PrintWriter printwriter = null;

        try {
            fileoutputstream = new FileOutputStream(file);
            printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            printwriter.println(productName + " control file");
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (printwriter != null)
                    printwriter.close();
                if (fileoutputstream != null)
                    fileoutputstream.close();
            } catch (IOException e) {
                /* empty */
            }
        }
        return true;
    }

    public static String licenseHeader(HashMap hashmap, boolean flag, String s) {
        if (!s.equals("administrator") && !s.equals(""))
            return "";
        String s1 = LUtils.getLicenseSummary(hashmap, false);
        if (s1.length() != 0)
            s1 = "<HR><CENTER><b>" + s1 + "</b></CENTER><HR>";
        return s1;
    }

    public static Array getLocalIPAddress() {
        Array array = new Array();
        Array array1 = new Array();
        String s = "ipconfig";
        CommandLine commandline = new CommandLine();
        array = commandline.exec(s);
        for (Enumeration enumeration = array.elements(); enumeration
                .hasMoreElements();) {
            String s1 = (String) enumeration.nextElement();
            String as[] = TextUtils.split(s1, " ");
            int i = 0;
            while (i < as.length) {
                String s2 = as[i].trim();
                if (isDottedIP(s2) && !s2.equals("0.0.0.0")) {
                    LogManager.log("RunMonitor", "ip address= " + s2);
                    array1.add(s2);
                }
                i++;
            }
        }

        return array1;
    }

    public static void shutdownWarning(HashMap hashmap, long l, String s, int i) {
        String s1 = TextUtils.getValue(hashmap, "_shutdownWarningSent");
        String s2 = TextUtils.getValue(hashmap, "_autoEmail");
        if (s2.length() != 0 && s1.length() == 0) {
            String s3 = productName + " shutting down in " + l
                    + " days - evaluation expired";
            String s4 = "Hi,\n\nHere's a quick note about the "
                    + productName
                    + " software that you have "
                    + "installed on your server.\n"
                    + "\n"
                    + "Our evaluation licenses allow prospective customers to evaluate "
                    + productName
                    + "\n"
                    + "for 10 days.  Your copy of "
                    + productName
                    + " was installed at "
                    + s
                    + ".\n"
                    + "\n"
                    + "We hope that means you're ready to purchase one or more "
                    + productName
                    + " licenses.\n"
                    + "\n"
                    + productName
                    + " will TURN ITSELF OFF IN "
                    + l
                    + " DAYS\n"
                    + "\n"
                    + "When you purchase a license, we will provide instructions for restarting\n"
                    + productName
                    + " and preserving your current monitoring configuration.\n"
                    + "\n" + "Call us today to keep using " + productName
                    + " without interruption!\n" + "\n" + salesContact + "\n\n"
                    + "If you choose not to purchase " + productName
                    + ", please remove the software.\n"
                    + "Instructions for removing " + productName
                    + " can be found at\n" + "   " + homeURLPrefix
                    + "/uninstall.htm\n\n";
            if (i == 2) {
                s3 = productName + " shutting down in " + l
                        + " days - subscription license expiring";
                s4 = "Hi,\n\nThe subscription license for your "
                        + productName
                        + " software will expire soon\n"
                        + "\n"
                        + "Unless you renew your subscription license, your "
                        + productName
                        + " software will\n"
                        + "turn itself off in "
                        + l
                        + " days.\n"
                        + "\n"
                        + "When you renew your subscription, we will provide a new license which\n"
                        + "preserve your current monitoring configuration.\n"
                        + "\n" + "Contact us today to keep using "
                        + productName + " without interruption!\n" + "\n"
                        + salesContact + "\n\n";
            }
            if (LUtils.getLicenseType(hashmap) != 0)
                s4 = s4 + "license=" + LUtils.getLicenseKey(hashmap);
            hashmap.put("_shutdownWarningSent", "true");
            MasterConfig.saveMasterConfig(hashmap);
            hashmap.put("_fromAddress", salesContactEmail);
            String s5 = MailUtils.mail(hashmap, s2, s3, s4, "", null, false);
            LogManager.log("Error", "Shutdown warning email sent to " + s2
                    + ", " + s5);
        }
    }

    public static void shutdownSignal(String s, long l) {
        FileOutputStream fileoutputstream = null;
        PrintWriter printwriter = null;
        try {
            fileoutputstream = new FileOutputStream(expiredName());
            printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
            printwriter.println(s + " " + l);
        } catch (IOException ioexception) {
        } finally {
            try {
                if (printwriter != null)
                    printwriter.close();
                if (fileoutputstream != null)
                    fileoutputstream.close();
            } catch (IOException ioexception1) {
            }
        }
    }

    public static void shutdownMail(HashMap hashmap, String s, long l,
            String s1, int i) {
        String s2 = TextUtils.getValue(hashmap, "_shutdownMailSent");
        String s3 = TextUtils.getValue(hashmap, "_autoEmail");
        if (s3.length() != 0 && s2.length() == 0) {
            String s4 = productName + " shut down - evaluation expired";
            String s5 = "Your "
                    + productName
                    + " evaluation software turned itself off today.\n"
                    + "\n"
                    + "Our evaluation licenses allow prospective customers to evaluate "
                    + productName
                    + "\n"
                    + "for 10 days.  Your copy of "
                    + productName
                    + " was installed at "
                    + s1
                    + ".\n"
                    + "\n"
                    + "We hope that means you're ready to purchase one or more "
                    + productName
                    + " licenses.\n"
                    + "\n"
                    + "When you purchase a license, we will provide instructions for restarting\n"
                    + productName
                    + " and preserving your current monitoring configuration.\n"
                    + "\n" + "Call us today to keep using " + productName
                    + " without interruption!\n" + "\n" + salesContact + "\n\n"
                    + "If you choose not to purchase " + productName
                    + ", please remove the software.\n"
                    + "Instructions for removing " + productName
                    + " can be found at\n" + "   " + homeURLPrefix
                    + "/uninstall.htm\n\n";
            if (i == 2) {
                s4 = productName + " shut down - subscription license expired";
                s5 = "Your "
                        + productName
                        + " software turned itself off today because the subscription license has expired.\n"
                        + "\n"
                        + "If you renew your subscription, we will provide a new license which will\n"
                        + "reactivate your subscription and preserve your current monitoring configuration.\n"
                        + "\n" + "Contact us today to continue using "
                        + productName + "!\n" + "\n" + salesContact + "\n\n";
            }
            if (LUtils.getLicenseType(hashmap) != 0)
                s5 = s5 + "license=" + LUtils.getLicenseKey(hashmap);
            hashmap.put("_shutdownMailSent", "true");
            MasterConfig.saveMasterConfig(hashmap);
            hashmap.put("_fromAddress", salesContactEmail);
            String s6 = MailUtils.mail(hashmap, s3, s4, s5, "", null, false);
            LogManager.log("Error", "Shutdown email sent to " + s3 + ", " + s6);
        }
        LogManager.log("RunMonitor", productName
                + " shutting down, LICENSE EXPIRED");
        LogManager
                .log("Error", productName + " shutting down, LICENSE EXPIRED");
    }

    public static boolean licenseExpired() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        boolean flag = false;
        String s = LUtils.getLicenseKey(hashmap);
 		if (!LUtils.isPermanentLicense(s)) {
            int i = LUtils.getLicenseType(s);
            int j = LUtils.getDaysRemaining(s);
            int k = LUtils.getShutdownDays(i);
            String s1 = TextUtils.getValue(hashmap, "_headerHTML");
            String s2 = TextUtils.getValue(hashmap, "_mainHTML");
            if (LUtils.isValidLicense(s)
                    && (s1.indexOf("licenseConvert") >= 0 || s2
                            .indexOf("licenseConvert") >= 0))
                hashmap = UpdateConfig.manageConverterLink(hashmap);
            if (j < k)
                flag = true;
        }

		return flag;
    }

    public static void checkExpired(HashMap hashmap) {
        int i = versionAsNumber;
        String s = TextUtils.numberToString(i);
        int j = 0;
        String s1 = "";
        s1 = TextUtils.getValue(hashmap, "_version");
        if (s1 != null && s1.length() > 0)
            j = TextUtils.toInt(s1);
        if (j == 0) {
            if (TextUtils.getValue(hashmap, "_license") != null)
                hashmap.put("_licenseOld", TextUtils.getValue(hashmap,
                        "_license"));
            hashmap.put("_license", "");
            hashmap.put("_installTime", "" + TextUtils.timeSeconds());
            hashmap.put("_version", s);
            MasterConfig.saveMasterConfig(hashmap);
        } else if (j > 700) {
            LogManager.log("RunMonitor", "Updating master.config file from \""
                    + s1 + "\" to \"" + s + "\"");
            hashmap.put("_version", s);
            MasterConfig.saveMasterConfig(hashmap);
        }
        String s2 = LUtils.getLicenseKey(hashmap);
        if (!LUtils.isPermanentLicense(s2)) {
            String s3 = TextUtils.getValue(hashmap, "_installed");
            int k = LUtils.getLicenseType(s2);
            int l = LUtils.getDaysRemaining(s2);
            int i1 = LUtils.getWarningDays(k);
            int j1 = LUtils.getShutdownDays(k);
            String s4 = TextUtils.getValue(hashmap, "_headerHTML");
            String s5 = TextUtils.getValue(hashmap, "_mainHTML");
            if (LUtils.isValidLicense(s2)
                    && (s4.indexOf("licenseConvert") >= 0 || s5
                            .indexOf("licenseConvert") >= 0))
                hashmap = UpdateConfig.manageConverterLink(hashmap);
            if (l < j1)
                shutdownMail(hashmap, s2, l, s3, k);
            else if (l < i1)
                shutdownWarning(hashmap, i1, s3, k);
        }
    }

    public static boolean isQTInstalled() {
        if (qtpInstalled == -1 && isWindows())
            if (Registry
                    .queryStringValue(
                            "HKEY_LOCAL_MACHINE",
                            "SOFTWARE\\DragonFlow\\QuickTest Professional\\CurrentVersion",
                            "QuickTest Professional").length() > 0)
                qtpInstalled = 1;
            else
                qtpInstalled = 0;
        return qtpInstalled == 1;
    }

    public static boolean isALTInstalled() {
        if (altInstalled == -1 && isWindows())
            if (Registry
                    .queryStringValue(
                            "HKEY_LOCAL_MACHINE",
                            "SOFTWARE\\DragonFlow\\ECC\\CurrentVersion",
                            "SiteView Enterprise Control Center").length() > 0)
                altInstalled = 1;
            else
                altInstalled = 0;
        return altInstalled == 1;
    }

    public static boolean isSystemAccount() {
        return isService() && userName.equalsIgnoreCase("SYSTEM");
    }

    private static void initOs() {
        if (isOsInitialized)
            return;
        isOsInitialized = true;
        String s = System.getProperty("os.name").toUpperCase();
        if (s.startsWith("WINDOWS")) {
            os = 1;
            FILE_NEWLINE = "\r\n";
        } else if (s.equals("IRIX"))
            os = 3;
        else if (s.equals("SOLARIS") || s.equals("SUNOS"))
            os = 2;
        else if (s.equals("HP-UX"))
            os = 5;
        else if (s.equals("LINUX"))
            os = 6;
        else if (s.equals("MAC OS") || s.equals("MACOS"))
            os = 4;
        else if (s.equals("MacOSX"))
            os = 7;
    }

    public static int getOs() {
        initOs();
        return os;
    }

    private static void initRoot() {
        if (isRootInitialized)
            return;
        isRootInitialized = true;
        if (getOs() == 4 && getRoot().equals("."))
            root = "/MacintoshHD/dev/SiteView/classes";
        String s = System.getProperty("PATH_TRANSLATED", System
                .getProperty("user.dir"));
        int i = s.toLowerCase().lastIndexOf("siteview");
        if (i != -1) {
            root = s.substring(0, i + 9);
        } else {
			int j = s.toLowerCase().lastIndexOf("sitevi~");			
//            int j = s.toLowerCase().lastIndexOf("sitesc~");
            if (j != -1)
                root = s.substring(0, j + 8);
        }
    }

    private static String tryGetRoot()
    {
    	String troot=new String();
//      initRoot();
		 initOs();/*add by dingbing.xu*/
		 if(isWindows())
		 {
			 troot= System.getProperty("user.dir");

		 }
		 else
			 troot = "/SiteView";

		 System.out.println("====== current diectory: "+troot);
		 
		 return troot;
    }

    public static String getRoot() {
		return root;
    }

    private static void initUserAccessAllowed() {
        if (isUserAccessAllowed) {
            return;
        } else {
            isUserAccessAllowed = true;
            userAccessAllowed = (new File(getRoot() + File.separator
                    + "userhtml")).exists();
            return;
        }
    }

    public static boolean isUserAccessAllowed() {
        initUserAccessAllowed();
        return userAccessAllowed;
    }

    public static void setService(boolean flag) {
        isService = flag;
    }

    public static boolean isService() {
        return isService;
    }

    public static final int evalDays = 10;

    public static final int totalDays = 60;

    public static final int kWIN = 1;

    static final int kSUN = 2;

    static final int kSGI = 3;

    static final int kMac = 4;

    static final int kHP = 5;

    static final int kLinux = 6;

    static final int kMacOSX = 7;

    static final int kOtherUnix = 8;

    public static final int NO_PLATFORM = 0;

    public static final int FIRST_PLATFORM = 1;

    public static final int LAST_PLATFORM = 8;

    public static String exampleDomain = "siteview.com";

    public static String exampleURL = "http://demo.siteview.com/test.htm";

    public static String exampleMailServer = "mail.this-company.com";

    public static String supportPhone = "1-877-837-8457";

    public static String homeURLPrefix;

    public static String urlOrderOptions;

    public static String companyName = "DragonFlow";

    public static String supportEmail = "support@dragonflow.com";

    public static String siteseerSupportEmail = "siteseersupport@dragonflow.com";

    public static String salesEmail = "siteviewsales@dragonflow.com";

    public static String salesPhone = "1-888-443-2266";

    public static String salesContactName;

    public static String salesContactEmail;

    public static String salesContactPhone;

    public static String licenseContactEmail = "siteviewlicense@dragonflow.com";

    public static String copyright;

    public static String companyLogo = "<a href=\"http://www.dragonflow.com/\" target=\"web\"><img src=/SiteView/htdocs/artwork/DragonFlow2_Websafe_xsml.gif  border=\"\" alt=\"\"></a> ";

    public static String salesFooter;

    public static String salesContact;

    private static boolean isOsInitialized = false;

    private static int os = 1;

    private static boolean portalDirectoryExists = false;

    public static String productName = "SiteView";

    private static int qtpInstalled = -1;

    private static int altInstalled = -1;

    public static int kDebugCPU = 8;

    public static int kDebugCommand = 4;

    public static int kDebugSendModem = 2;

    public static int kDebugPerfex = 1;

    public static int kDebugNone;

    public static int platformDebugTrace;

    public static final String SERVICE_NAME_PREFIX = "username=";

    public static int pid = 0;

    public static String userName = "";

    private static boolean isRootInitialized = false;

    private static String root = tryGetRoot();

    private static boolean isUserAccessAllowed = false;

    private static boolean userAccessAllowed = false;

    public static String FILE_NEWLINE = "\n";

    private static String version = "7.9";

    public static int versionAsNumber = 750;

    public static CounterLock monitorLock = new CounterLock(1);

    public static String customConfigPath = "";

    public static String serverName = "";

    public static final String SYSTEM_USER = "SYSTEM";

    public static String versionString = null;

    public static Object getVersionSync = new Object();

    private static boolean isService = false;

    private static final String UNIX_DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

    private static final String UNIX_DATE_FORMAT_WITH_ZONE = "MM/dd/yyyy HH:mm:ss 'GMT'Z";

    public static String charSetTag = "";

    public static int timeZoneOffset = -1;

    public static int PING_COMMAND_FAILED = -999;

    public static final int PING_SUCCESSES = 1;

    public static final int PING_TOTAL_TIME = 0;

    static HashMap remoteLocks = new HashMap();

    public static int DIR_ALL_FILES = 0;

    public static int DIR_DIRECTORIES = 1;

    public static int DIR_FILES = 2;

    static Hashtable timeZoneMap = null;

    static int currentCPUFile = 1;

    static String cpuFile = "D:\\ccpu";

    static HashMap hasLibraries = new HashMap();

    static {
        homeURLPrefix = "http://www.dragonflow.com";
        urlOrderOptions = "/orderoptions.htm";
        salesContactName = "SiteView Sales Team";
        salesContactEmail = "siteviewsales@dragonflow.com";
        salesContactPhone = "1-888-443-2266 option 3";
        copyright = "<a href=\""
                + homeURLPrefix
                + "/company/copyright.html\" target=\"web\"> Copyright &copy; 2004 </a>"
                + " , All rights reserved.\n";
        salesFooter = "For info about ordering, upgrades, and support:\n   "
                + homeURLPrefix + "\n" + "   " + salesContactEmail + "\n"
                + "   " + salesContactPhone + "\n";
        salesContact = "    " + salesContactName + ", " + salesContactPhone
                + "\n" + "    " + salesContactEmail + "\n" + "    "
                + homeURLPrefix + urlOrderOptions + "\n";
        kDebugNone = 0;
        platformDebugTrace = kDebugNone;
        String s = System.getProperty("Platform.debug");
        if (s != null) {
            platformDebugTrace = TextUtils.toInt(s);
            System.out.println("platformDebugTrace=" + platformDebugTrace);
        }
    }
}