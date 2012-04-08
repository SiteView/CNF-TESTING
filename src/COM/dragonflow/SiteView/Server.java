/*
 * 
 * Created on 2005-2-16 16:45:42
 *
 * Server.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Server</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.StandardMonitor.URLMonitor;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.SocketSession;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// MasterConfig, ServerControlThread, Platform

public class Server {

    public static final String SERVERS_FILENAME = "multi.config";

    static Array serversCache = null;

    static HashMap nameCache = new HashMap();

    public Server() {
    }

    public static HashMap findServer(Array array, String s) {
        for (Enumeration enumeration = array.elements(); enumeration
                .hasMoreElements();) {
            HashMap hashmap = (HashMap) enumeration.nextElement();
            if (s.equals(TextUtils.getValue(hashmap, "server"))) {
                return hashmap;
            }
        }

        return null;
    }

    static void clearServersCache() {
        serversCache = null;
    }

    public static long fetchServerState(HashMap hashmap, HashMap hashmap1,
            StringBuffer stringbuffer) {
        if (TextUtils.getValue(hashmap1, "class").length() > 0) {
            return 200L;
        }
        if (TextUtils.getValue(hashmap1, "server").equals("_local")) {
            return 200L;
        }
        String s = TextUtils.getValue(hashmap1, "server");
        String s1;
        if (TextUtils.getValue(hashmap1, "securehttp").length() > 0) {
            s1 = "https://";
        } else {
            s1 = "http://";
        }
        if (s.indexOf("://") != -1) {
            s1 = "";
        }
        String s3 = TextUtils.getValue(hashmap1, "account");
        String s2;
        if (s3.length() > 0) {
            s2 = s1
                    + s
                    + "/SiteView/htdocs/Progress.html?hideRecent=true&account="
                    + s3;
        } else {
            s2 = s1 + s + "/SiteView/htdocs/Progress.html?hideRecent=true";
        }
        String s4 = "";
        String s5 = TextUtils.getValue(hashmap1, "proxy");
        String s6 = TextUtils.getValue(hashmap1, "proxyusername");
        String s7 = TextUtils.getValue(hashmap1, "proxypassword");
        String s8 = TextUtils.getValue(hashmap1, "username");
        String s9 = TextUtils.getValue(hashmap1, "password");
        String s10 = TextUtils.getValue(hashmap1, "timeout");
        int i = 60000;
        if (s10.length() != 0) {
            i = TextUtils.toInt(s10) * 1000;
        }
        if (s8.length() == 0) {
            s8 = TextUtils.getValue(hashmap, "_adminUsername");
            s9 = TextUtils.getValue(hashmap, "_adminPassword");
        }
        long l = -1L;
        if (TextUtils.getValue(hashmap, "_overviewMaxData").length() > 0) {
            l = TextUtils.toLong(TextUtils
                    .getValue(hashmap, "_overviewMaxData"));
        }
        if (l < 1L) {
            l = 0x7a120L;
        }
        SocketSession socketsession = SocketSession.getSession(null);
        socketsession.setStreamEncoding("UTF-8");
        long al[] = URLMonitor.checkURL(socketsession, s2, s4, "", s5, s6, s7,
                null, s8, s9, "", stringbuffer, l, "", 0, i, null);
        long l1 = al[0];
        socketsession.close();
        return l1;
    }

    public static long remoteOp(HashMap hashmap, String s, String s1,
            StringBuffer stringbuffer, StringBuffer stringbuffer1) {
        String s2 = TextUtils.getValue(hashmap, "server");
        String s3 = TextUtils.getValue(hashmap, "account");
        String s4 = TextUtils.getValue(hashmap, "proxy");
        String s5 = TextUtils.getValue(hashmap, "proxyusername");
        String s6 = TextUtils.getValue(hashmap, "proxypassword");
        String s7 = TextUtils.getValue(hashmap, "username");
        String s8 = TextUtils.getValue(hashmap, "password");
        String s9 = TextUtils.getValue(hashmap, "timeout");
        int i = 60000;
        int j = 0x7a120;
        String s10 = "";
        if (s9.length() != 0) {
            i = TextUtils.toInt(s9) * 1000;
        }
        HashMap hashmap1 = MasterConfig.getMasterConfig();
        if (s7.length() == 0) {
            s7 = TextUtils.getValue(hashmap1, "_adminUsername");
            s8 = TextUtils.getValue(hashmap1, "_adminPassword");
        }
        String s11 = "http://" + s2
                + "/SiteView/cgi/go.exe/SiteView?page=file&operation=" + s
                + "&file=" + s1 + "&account=" + s3;
        Array array = null;
        if (s.equals("delete") || s.equals("put")) {
            array = new Array();
            array.add("page=file");
            array.add("file=" + s1);
            array.add("operation=" + s);
            array.add("account=" + s3);
            array.add("data=" + stringbuffer);
            s11 = "http://" + s2 + "/SiteView/cgi/go.exe/SiteView";
        }
        SocketSession socketsession = SocketSession.getSession(null);
        long al[] = URLMonitor.checkURL(socketsession, s11, s10, "", s4, s5,
                s6, array, s7, s8, "", stringbuffer1, j, "", 0, i, null);
        long l = al[0];
        socketsession.close();
        return l;
    }

    public static void main(String args[]) throws Exception {
        String s = "get";
        String s1 = "";
        String s2 = "/groups/master.config";
        String s3 = "demo.siteview.com:8888";
        int i = 0;
        do {
            if (i >= args.length) {
                break;
            }
            if (args[i].equals("-i")) {
                s1 = args[++i];
                i++;
            } else if (args[i].equals("-o")) {
                s2 = args[++i];
                i++;
            } else if (args[i].equals("-c")) {
                s = args[++i];
                i++;
            } else if (args[i].equals("-s")) {
                s3 = args[++i];
                i++;
            }
        } while (true);
        StringBuffer stringbuffer = new StringBuffer();
        if (s1.length() > 0) {
            stringbuffer.append(FileUtils.readFile(s1));
        }
        HashMap hashmap = findServer(readServers(), s3);
        StringBuffer stringbuffer1 = new StringBuffer();
        long l = remoteOp(hashmap, s, s2, stringbuffer, stringbuffer1);
        System.out.println("status=" + l);
        System.out.println("results=(" + stringbuffer1 + ")");
    }

    public static void startUpdating() {
        ServerControlThread.startUpdating();
    }

    public static void stopUpdating() {
        ServerControlThread.stopUpdating();
        clearServersCache();
    }

    public static String LocalServerName() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        String s = TextUtils.getValue(hashmap, "_webserverAddress");
        if (s.length() == 0) {
            s = "Local Server";
        } else {
            String s1 = (String) hashmap.get("_httpActivePort");
            if (s1 != null) {
                s = s + ":" + s1;
            }
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    public static Array readServers() {
        if (serversCache == null) {
            serversCache = new Array();
            nameCache = new HashMap();
            try {
                serversCache = FrameFile.readFromFile(Platform.getRoot()
                        + "/groups/" + "multi.config");
            } catch (Exception exception) {
            }
            if (serversCache.size() == 0) {
                HashMap hashmap = new HashMap();
                hashmap.put("server", "_local");
                serversCache.add(hashmap);
            }
            Enumeration enumeration = serversCache.elements();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap1 = (HashMap) enumeration.nextElement();
                if (hashmap1 != null) {
                    String s = TextUtils.getValue(hashmap1, "server");
                    if (s.equals("_local")
                            && TextUtils.getValue(hashmap1, "title").length() == 0) {
                        hashmap1.put("title", LocalServerName());
                    }
                    hashmap1.put("_id", s);
                    nameCache.put(s, hashmap1);
                    hashmap1.put("_path", s);
                    hashmap1.put("_status", "-1");
                    hashmap1.put("_items", new Array());
                    if (TextUtils.getValue(hashmap1, "title").length() == 0) {
                        hashmap1.put("title",
                                hashmap1.get("server") == null ? "Unknown"
                                        : hashmap1.get("server"));
                    }
                }
            } 
            
            enumeration = serversCache.elements();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap2 = (HashMap) enumeration.nextElement();
                String s1 = TextUtils.getValue(hashmap2, "parent");
                if (s1.length() != 0) {
                    HashMap hashmap3 = (HashMap) nameCache.get(s1);
                    if (hashmap3 != null) {
                        Array array = (Array) hashmap3.get("_items");
                        array.add(hashmap2);
                    }
                }
            } 
        }
        return serversCache;
    }

    public static void writeServers(Array array) throws IOException {
        FrameFile.writeToFile(Platform.getRoot() + "/groups/" + "multi.config",
                array, "_", false);
        unloadServers();
        loadServers();
    }

    public static void writeFileServers(Array array) throws IOException {
        FrameFile.writeToFile(Platform.getRoot() + "/groups/" + "multi.config",
                array, "_", false);
    }

    public static void addServers(String s, Array array) {
        addServers((HashMap) nameCache.get(s), array);
    }

    static void addServers(HashMap hashmap, Array array) {
        if (hashmap != null) {
            if (TextUtils.getValue(hashmap, "class").length() == 0) {
                array.add(hashmap);
            } else {
                Array array1 = (Array) hashmap.get("_items");
                HashMap hashmap1;
                for (Enumeration enumeration = array1.elements(); enumeration
                        .hasMoreElements(); addServers(hashmap1, array)) {
                    hashmap1 = (HashMap) enumeration.nextElement();
                }

            }
        }
    }

    public static String getTitle(HashMap hashmap) {
        return TextUtils.getValue(hashmap, "title");
    }

    public static String getPath(HashMap hashmap) {
        return TextUtils.getValue(hashmap, "_path");
    }

    public static String getMachineName(HashMap hashmap) {
        String s = TextUtils.getValue(hashmap, "server");
        int i = s.indexOf(':');
        if (i != -1) {
            s = s.substring(0, i);
        }
        return s;
    }

    public static Array loadItems(HashMap hashmap) {
        Array array = (Array) hashmap.get("_items");
        if (array != null) {
            return array;
        } else {
            Array array1 = new Array();
            hashmap.put("_items", array1);
            return array1;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param flag
     * @return
     */
    public static Array getServers(String s, boolean flag) {
        Array array = new Array();
        Array array1 = readServers();
        if (s.length() == 0) {
            Enumeration enumeration = array1.elements();
            while (enumeration.hasMoreElements()) {
                HashMap hashmap1 = (HashMap) enumeration.nextElement();
                if (TextUtils.getValue(hashmap1, "parent").length() <= 0
                        && (flag || !TextUtils.getValue(hashmap1, "server")
                                .equals("_local"))) {
                    array.add(hashmap1);
                }
            } 
            
        } else {
            HashMap hashmap = findServer(array1, s);
            if (hashmap != null) {
                Array array2 = (Array) hashmap.get("_items");
                HashMap hashmap2;
                for (Enumeration enumeration1 = array2.elements(); enumeration1
                        .hasMoreElements(); array.add(hashmap2)) {
                    hashmap2 = (HashMap) enumeration1.nextElement();
                }

            }
        }
        return array;
    }

    public static void loadServers() {
        LogManager.log("RunMonitor", "Loading multi.config");
        readServers();
    }

    public static void unloadServers() {
        LogManager.log("Debug", "unloading multi.config");
        stopUpdating();
    }

}