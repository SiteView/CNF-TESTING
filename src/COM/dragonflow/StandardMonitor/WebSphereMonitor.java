/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WebSphereMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WebSphereMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.WebSphere.ProcessWaiter;
import COM.dragonflow.Utils.WebSphere.ReaderThread;
import COM.dragonflow.Utils.WebSphere.WebSphereConnectionProperties;
import COM.dragonflow.Utils.WebSphere.WebSphereCounter;
import COM.dragonflow.Utils.WebSphere.WebSphereProcessLauncher;
import COM.dragonflow.Utils.WebSphere.WebSphereProcessProperties;
import COM.dragonflow.Utils.WebSphere.WebSphereServer;
import COM.dragonflow.Utils.WebSphere.WebSphereService;
import COM.dragonflow.Utils.NullSecurityManager;
import COM.dragonflow.Utils.TextUtils;

public class WebSphereMonitor extends BrowsableBase {

    private WebSphereServer remoteServer;

    public static boolean debug = false;

    static String xmlSourcePort;

    static StringProperty pServerName;

    static StringProperty pTarget;

    static NumericProperty pPort;

    static StringProperty pUsername;

    static StringProperty pPassword;

    static StringProperty pRealm;

    static ScalarProperty pVersion;

    static StringProperty pClientProps;

    static StringProperty pClasspath;

    static NumericProperty pTimeout;

    static StringProperty pWebSphereDir;

    static StringProperty pStatus;

    static boolean bWindows;

    static Properties props;

    static String propsFilename;

    static int nMaxCounters;

    public WebSphereMonitor() {
        remoteServer = null;
    }

    public String getHostname() {
        String s = getProperty(pTarget);
        if (s == null) {
            s = getProperty(pServerName);
        }
        return s;
    }

    protected boolean update() {
        debug("Entering WebSphereMonitor.update().");
        int i;
        for (i = 0; i < nMaxCounters && getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1)).length() != 0; i ++) {
        }
        if (i == 0) {
            debug("Leaving WebSphereMonitor.update() because there were no counters to retrieve.");
            return true;
        }
        if (stillActive()) {
            synchronized (this) {
                for (int j = 0; j < i; j ++) {
                    setProperty(PROPERTY_NAME_COUNTER_VALUE + (j + 1), "n/a");
                }

            }
        }
        WebSphereCounter awebspherecounter[] = new WebSphereCounter[i];
        for (int k = 0; k < i; k ++) {
            awebspherecounter[k] = new WebSphereCounter(getProperty(PROPERTY_NAME_COUNTER_ID + (k + 1)));
        }

        boolean flag = false;
        StringBuffer stringbuffer = new StringBuffer();
        if (getProperty(pVersion).equals("3.5x")) {
            flag = getCountersEPM(awebspherecounter, stringbuffer);
        } else {
            if (remoteServer == null || isRemoteReferenceStale()) {
                remoteServer = getWebSphereServer(stringbuffer);
            }
            if (remoteServer != null) {
                try {
                    if ((awebspherecounter = remoteServer.getCounters(awebspherecounter)) != null) {
                        flag = true;
                    }
                } catch (NoSuchObjectException nosuchobjectexception) {
                    error("Stale remote object reference in WebSphereMonitor: " + nosuchobjectexception + ".", nosuchobjectexception);
                    remoteServer = null;
                } catch (RemoteException remoteexception) {
                    flag = false;
                    String s = "Could not get counters from WebSphere Application Server.  (" + remoteexception.getMessage() + ").";
                    error(s, remoteexception);
                    stringbuffer.append(s);
                    remoteServer = null;
                }
            }
        }
        if (flag) {
            if (stillActive()) {
                synchronized (this) {
                    setProperty(pStatus, "OK");
                    int l = 0;
                    String s1 = "";
                    for (int i1 = 0; i1 < i; i1 ++) {
                        String s2 = awebspherecounter[i1].getValue();
                        if (s2 == null || s2.length() == 0 || s2.indexOf("n/a") >= 0 || s2.trim().equals("NaN")) {
                            l ++;
                        }
                        String s3 = getProperty(PROPERTY_NAME_COUNTER_NAME + (i1 + 1));
                        s1 = s1 + s3 + " = " + s2;
                        if (i1 != i - 1) {
                            s1 = s1 + ", ";
                        }
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1), s2);
                    }

                    setProperty(pStateString, s1);
                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), l);
                }
            }
        } else if (stillActive()) {
            synchronized (this) {
                setProperty(pNoData, "n/a");
                setProperty(pStatus, stringbuffer.toString());
                setProperty(pStateString, stringbuffer.toString());
                setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
            }
        }
        debug("Leaving WebSphereMonitor.update() and returning true.");
        return true;
    }

    private boolean isRemoteReferenceStale() {
        try {
            remoteServer.getServerName();
        } catch (NoSuchObjectException nosuchobjectexception) {
            LogManager.log("Error", "WebSphereMonitor found a stale reference to the WebSphereServer remote object.   Will attempt to get a new WebSphereServer now...");
            remoteServer = null;
            return true;
        } catch (RemoteException remoteexception) {
            LogManager.log("Error", "WebSphereMonitor encountered a RemoteException while checking its WebSphereServer remote object.  Exception was: " + remoteexception + "Will attempt to get a new WebSphereServer now...");
            remoteServer = null;
            return true;
        }
        return false;
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        File file = new File(getProperty(pWebSphereDir));
        if (!file.isDirectory()) {
            stringbuffer.append("Please supply a valid Websphere Folder");
            return "";
        }
        String s = "";
        if (getProperty(pVersion).equals("3.5x")) {
            StringBuffer stringbuffer1 = new StringBuffer();
            boolean flag = startWebSphereEPMProcess(null, stringbuffer1);
            if (!flag) {
                stringbuffer.append(stringbuffer1);
            } else {
                s = stringbuffer1.toString();
            }
        } else {
            try {
                remoteServer = getWebSphereServer(stringbuffer);
                if (remoteServer != null) {
                    s = remoteServer.getBrowseData();
                }
            } catch (RemoteException remoteexception) {
                error("WebSphereMonitor received RemoteException in getBrowseData(): " + remoteexception.getMessage(), remoteexception);
                stringbuffer.append("An error occurred while attempting to retrieve list of WebSphere counters.  Please check the connection properties, verify that the application server is running, and try again. (See the error log for details.)");
            }
        }
        return s;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param l
     * @param s
     * @return
     * @throws MalformedURLException
     * @throws RemoteException
     */
    private WebSphereService getWebSphereService(long l, String s) throws MalformedURLException, RemoteException {
        long l1;
        long l2;
        WebSphereService websphereservice;
        l1 = (new Date()).getTime();
        l2 = l1 + l;
        websphereservice = null;

        while (l2 > l1 && websphereservice == null) {
            try {
                websphereservice = (WebSphereService) Naming.lookup(s);
                return websphereservice;
            } catch (NotBoundException notboundexception) {
                l1 = (new Date()).getTime();
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException interruptedexception) {
                    /* empty */
                }
            }
        }
        return null;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringbuffer
     * @return
     */
    private WebSphereServer getWebSphereServer(StringBuffer stringbuffer) {
        WebSphereProcessProperties websphereprocessproperties;
        WebSphereConnectionProperties websphereconnectionproperties;
        websphereprocessproperties = new WebSphereProcessProperties(getProperty(pClasspath), getProperty(pClientProps), getProperty(pWebSphereDir), getProperty(pVersion), bWindows, getProperty(pServerName));
        websphereconnectionproperties = new WebSphereConnectionProperties(getProperty(pServerName), getProperty(pUsername), getProperty(pPassword), getPropertyAsInteger(pPort), getProperty(pVersion));
        int i = WebSphereProcessLauncher.launch(websphereprocessproperties);
        if (i == 0 || i == 1) {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new NullSecurityManager());
            }
            Object obj = null;
            WebSphereService websphereservice;
            try {
                if ((websphereservice = getWebSphereService(10000L, websphereprocessproperties.getURL())) != null) {
                    return websphereservice.getServer(websphereconnectionproperties);
                }
                LogManager.log("Error", "WebSphereService was never bound to the local registry in WebSphereMonitor.getWebSphereServer(). URL was:  " + websphereprocessproperties.getURL());
                return null;
            } catch (MalformedURLException e) {
                error("Exception while looking up WebSphereService at URL: " + websphereprocessproperties.getURL() + ". Exception was: " + e.getMessage(), e);
                stringbuffer.append(e);
                return null;
            } catch (RemoteException e) {
                error("WebSphereMonitor received RemoteException in getWebSphereService(): " + e.getMessage(), e);
                stringbuffer.append(e);
                return null;
            }
        }
        LogManager.log("Error", "WebSphereMonitor failed to launch WebSphereService.");
        stringbuffer.append("WebSphereMonitor failed to launch WebSphereService.");
        return null;
    }

    public Array getConnectionProperties() {
        Array array = new Array();
        array.add(pServerName);
        array.add(pPort);
        array.add(pUsername);
        array.add(pPassword);
        array.add(pRealm);
        array.add(pVersion);
        array.add(pWebSphereDir);
        array.add(pClientProps);
        array.add(pClasspath);
        return array;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        Vector vector = null;
        if (scalarproperty == pVersion) {
            vector = new Vector(3);
            vector.addElement("3.5x");
            vector.addElement("3.5x");
            vector.addElement("4.x");
            vector.addElement("4.x");
            vector.addElement("5.x");
            vector.addElement("5.x");
        } else {
            vector = super.getScalarValues(scalarproperty, httprequest, cgi);
        }
        return vector;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pServerName) {
            if (s.trim().length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if (stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE)) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s1.length() <= 0) {
                hashmap.put(stringproperty, "No counters selected");
            }
            return s;
        }
        if (stringproperty == pTarget) {
            if (s.length() == 0) {
                return getProperty(pServerName);
            }
        } else if (stringproperty == pWebSphereDir) {
            String s2 = System.getProperties().getProperty("file.separator");
            String s3 = getAPI();
            String s4 = s;
            int i = 1;
            do {
                String s5 = props.getProperty("classpath" + s3 + i);
                if (s5 == null) {
                    break;
                }
                boolean flag = false;
                try {
                    File file = new File(s4 + s2 + "lib" + s2 + s5);
                    if (!file.exists()) {
                        flag = true;
                    }
                } catch (Exception exception) {
                    flag = true;
                }
                if (flag) {
                    hashmap.put(stringproperty, "Selected WebSphere directory doesn't contain library lib" + s2 + s5);
                    return s;
                }
                i ++;
            } while (true);
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_WebSphereMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public String setBrowseID(Array array) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = array.size(); i > 0; i --) {
            if (i < array.size()) {
                stringbuffer.append("/");
            }
            stringbuffer.append(WebSphereCounter.normalize((String) array.at(i - 1)));
        }

        return stringbuffer.toString();
    }

    public static Properties getProps() {
        return props;
    }

    protected String getAPI() {
        String s = getProperty(pVersion);
        if (s.equals("3.5x")) {
            return "EPM";
        }
        if (s.equals("4.x")) {
            return "PMI";
        } else {
            return "JMX";
        }
    }

    public static String getAvailableApplicationsXML(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8) {
        WebSphereMonitor webspheremonitor = new WebSphereMonitor();
        webspheremonitor.setProperty(pServerName, s);
        webspheremonitor.setProperty(pPort, s1);
        webspheremonitor.setProperty(pUsername, s2);
        webspheremonitor.setProperty(pPassword, s3);
        webspheremonitor.setProperty(pRealm, s4);
        webspheremonitor.setProperty(pVersion, s5);
        webspheremonitor.setProperty(pWebSphereDir, s6);
        webspheremonitor.setProperty(pClientProps, s7);
        webspheremonitor.setProperty(pClasspath, s8);
        String s9 = "";
        StringBuffer stringbuffer = new StringBuffer();
        try {
            s9 = webspheremonitor.getBrowseData(stringbuffer);
            if (stringbuffer.length() > 0) {
                LogManager.log("Error", "WebSphereMonitor.getAvailableApplicationsXML failed: " + stringbuffer);
            }
        } catch (Exception exception) {
            error("WebSphereMonitor.getAvailableApplicationsXML failed: " + exception.getMessage(), exception);
        }
        return s9;
    }

    private void debug(String s) {
        if (debug) {
            LogManager.log("Error", s);
        }
    }

    private static void error(String s, Exception exception) {
        LogManager.log("Error", s);
        if (debug) {
            exception.printStackTrace();
        }
    }

    private boolean getCountersEPM(WebSphereCounter awebspherecounter[], StringBuffer stringbuffer) {
        StringBuffer stringbuffer1 = new StringBuffer();
        boolean flag = startWebSphereEPMProcess(awebspherecounter, stringbuffer1);
        if (flag) {
            String as[] = stringbuffer1.toString().split("\r\n");
            for (int i = 0; i < awebspherecounter.length; i ++) {
                StringBuffer stringbuffer2 = new StringBuffer();
                for (int j = 0; j < as.length; j ++) {
                    String s = as[j];
                    if (s.equals(">")) {
                        break;
                    }
                    if (stringbuffer2.length() > 0) {
                        stringbuffer2.append("\n");
                    }
                    stringbuffer2.append(s.substring(1));
                }

                if (stringbuffer2.length() == 0) {
                    stringbuffer2.append("n/a");
                }
                awebspherecounter[i].setValue(stringbuffer2.toString());
            }

        } else {
            stringbuffer.append(stringbuffer1);
        }
        return flag;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param awebspherecounter
     * @param stringbuffer
     * @return
     */
    boolean startWebSphereEPMProcess(WebSphereCounter awebspherecounter[], StringBuffer stringbuffer) {
        debug("Entering WebSphereMonitor.startWebSphereEPMProcess().");
        StringBuffer stringbuffer2;
        ReaderThread readerthread;
        ReaderThread readerthread1;
        int i1;
        try {
            String s = System.getProperties().getProperty("file.separator");
            String s1 = System.getProperties().getProperty("path.separator");
            String s2;
            if (bWindows) {
                s2 = "javaw.exe";
            } else {
                s2 = "java";
            }
            StringBuffer stringbuffer1 = new StringBuffer(getProperty(pClasspath));
            if (stringbuffer1.length() > 0) {
                stringbuffer1.append(s1);
            }
            stringbuffer1.append(s1).append("..").append(s).append("classes").append(s).append("epm.jar");
            String s3 = getProperty(pWebSphereDir);
            int i = s3.length();
            if (i > 0 && s3.charAt(i - 1) == '\\') {
                s3 = s3.substring(0, i - 1);
            }
            String s4 = s3 + s + "jdk" + s + "jre" + s + "bin" + s + s2;
            String s5 = getProperty(pClientProps);
            if (s5.length() == 0) {
                s5 = "sas.client.props";
            }
            s5 = s5.replace('\\', '/');
            if (s5.charAt(0) != '/' && s5.charAt(0) != ':') {
                s5 = s3.replace('\\', '/') + "/properties/" + s5;
            }
            if (bWindows) {
                s5 = "/" + s5;
            }
            String s6 = "-Dcom.ibm.CORBA.ConfigURL=file:" + s5;
            String s7 = "-Dcom.ibm.SOAP.ConfigURL=file:" + s5;
            int j = 1;
            for (String s9 = props.getProperty("classpathEPM" + j); s9 != null; j ++) {
                stringbuffer1.append(s1 + s3 + s + "lib" + s + s9);
            }
            j = 1;
            for (String s10 = props.getProperty("propertiesEPM" + j); s10 != null; j ++) {
                stringbuffer1.append(s1 + s3 + s + "properties" + s + s10);
            }

            String s8 = getProperty(pUsername);
            String s11 = getProperty(pPassword);
            String s12 = getProperty(pRealm);
            String s13 = getProperty(pPort);
            String s14 = getProperty(pServerName);
            Array array = new Array();
            array.add(s4);
            array.add(s6);
            array.add(s7);
            array.add("-classpath");
            array.add(stringbuffer1.toString());
            array.add("COM.dragonflow.SiteView.WebSphereMonitorEPM");
            if (!"".equals(s8)) {
                array.add("-username");
                array.add(s8);
                if (!"".equals(s11)) {
                    array.add("-password");
                    array.add(s11);
                }
                if (!"".equals(s12)) {
                    array.add("-realm");
                    array.add(s12);
                }
            }
            array.add("-dir");
            array.add("\"" + s3 + "\"");
            if (!"".equals(s13)) {
                array.add("-port");
                array.add(s13);
            }
            array.add(s14);
            if (awebspherecounter != null) {
                for (int k = 0; k < awebspherecounter.length; k ++) {
                    array.add(awebspherecounter[k].getName());
                }

            }
            String as[] = new String[array.size()];
            array.copyTo(as);
            int l;
            if (awebspherecounter != null && awebspherecounter.length > 0) {
                l = getPropertyAsInteger(pTimeout);
            } else {
                l = 0;
            }
            debug("WebSphereMonitor.startWebSphereEPMProcess() before calling Runtime.exec() with counters[]: " + array.toString());
            Process process = Runtime.getRuntime().exec(as, null, new File(Platform.getRoot() + File.separator + "classes"));
            debug("WebSphereMonitor.startWebSphereEPMProcess() after calling Runtime.exec(). ");
            stringbuffer2 = new StringBuffer();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            readerthread = new ReaderThread(bufferedreader, stringbuffer2);
            readerthread.start();
            BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            readerthread1 = new ReaderThread(bufferedreader1, "Error", "WebSphereMonitor EPM: ");
            readerthread1.start();
            ProcessWaiter processwaiter = new ProcessWaiter(process);
            processwaiter.start();
            processwaiter.join(l * 1000);
            try {
                i1 = process.exitValue();
            } catch (IllegalThreadStateException illegalthreadstateexception) {
                process.destroy();
                LogManager.log("Error", "WebSphere monitor timed out");
                error("WebSphereMonitor.startWebSphereEPMProcess() encountered IllegalThreadStateException: " + illegalthreadstateexception, illegalthreadstateexception);
                if (awebspherecounter != null && awebspherecounter.length == 0) {
                    stringbuffer.append("Timeout");
                } else {
                    for (int j1 = 0; j1 < awebspherecounter.length; j1 ++) {
                        stringbuffer.append("Timeout ");
                    }

                }
                debug("Leaving WebSphereMonitor.startWebSphereEPMProcess() due to exception: " + illegalthreadstateexception.toString());
                return false;
            }
            readerthread1.join();
            readerthread.join();
            stringbuffer.append(stringbuffer2.toString());
            debug("Leaving WebSphereMonitor.startWebSphereEPMProcess() with exit code: " + i1 + " (0 = no errors)");
            return i1 == 0;
        } catch (Exception e) {
            error("WebSphereMonitor.startWebSphereEPMProcess() encountered an exception: " + e.toString(), e);
            stringbuffer.append(e);
            return false;
        }
    }

    public boolean manageBrowsableSelectionsByID() {
        return true;
    }

    public boolean areBrowseIDsEqual(String s, String s1) {
        WebSphereCounter webspherecounter = new WebSphereCounter("counter1", s);
        WebSphereCounter webspherecounter1 = new WebSphereCounter("counter2", s1);
        String s2 = webspherecounter.getObjectNameFromID();
        String s3 = webspherecounter1.getObjectNameFromID();
        if (s2.equals(s3)) {
            String s4 = webspherecounter.getAttributeNameFromID();
            String s5 = webspherecounter1.getAttributeNameFromID();
            if (s4.equals(s5)) {
                return true;
            }
        }
        return false;
    }

    static {
        props = new Properties();
        propsFilename = Platform.getRoot() + File.separator + "templates.applications" + File.separator + "websphere.props";
        nMaxCounters = 10;
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_WebSphereMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 30;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
        try {
            props.load(new FileInputStream(propsFilename));
        } catch (Exception exception) {
            error("Cannot open Websphere properties file: " + propsFilename, exception);
        }
        String s = TextUtils.getValue(hashmap, "_webSphereDebug");
        if (s.length() > 0) {
            debug = true;
            LogManager.log("RunMonitor", "WebSphere Monitor debugging enabled (_webSphereDebug=" + s + ").");
        }
        xmlSourcePort = props.getProperty("port");
        if (xmlSourcePort == null) {
            xmlSourcePort = "2222";
        }
        bWindows = System.getProperties().getProperty("os.name").startsWith("Windows");
        Array array = new Array();
        pServerName = new StringProperty("_server");
        pServerName.setDisplayText("Server", "the name of the server <B>without</B> the backslashes");
        pServerName.setParameterOptions(false, true, 4, false);
        array.add(pServerName);
        pTarget = new StringProperty("_target");
        pTarget.setDisplayText("Target", "the logical name of the server. If empty, the hostname will be used.");
        pTarget.setParameterOptions(true, 5, false);
        array.add(pTarget);
        pPort = new NumericProperty("_Port", "900");
        pPort.setDisplayText("Port Number", "the port number of WebSphere server (SOAP port for WebSphere 5+, default is 8880)");
        array.add(pPort);
        pUsername = new StringProperty("_Username");
        pUsername.setDisplayText("User Name", "the username for login to WebSphere server");
        array.add(pUsername);
        pPassword = new StringProperty("_Password");
        pPassword.setDisplayText("Password", "the password for login to WebSphere server");
        pPassword.isPassword = true;
        array.add(pPassword);
        pRealm = new StringProperty("_Realm");
        pRealm.setDisplayText("Security Realm", "security realm for WebSphere server (3.5x only).");
        array.add(pRealm);
        String s1 = props.getProperty("WAS_HOME");
        if (s1 == null) {
            s1 = bWindows ? "C:\\WebSphere\\AppServer" : "/opt/websphere/appserver";
        }
        pWebSphereDir = new StringProperty("_WebSphereDir", s1);
        pWebSphereDir.setDisplayText("WebSphere Directory", "Enter path to WebSphere Directory. This directory should contain at least an Admin Console installation.");
        array.add(pWebSphereDir);
        pVersion = new ScalarProperty("_Version");
        pVersion.setDisplayText("Version", "version of WebSphere server");
        array.add(pVersion);
        pClientProps = new StringProperty("_ClientProps", "sas.client.props");
        pClientProps.setDisplayText("Client Properties File", "custom client properties file. For WebSphere 5.x+, you should select an appropriate soap.client.props file. <WebSphereDir>/properties/soap.client.props will be used by default.");
        array.add(pClientProps);
        pClasspath = new StringProperty("_Classpath");
        pClasspath.setDisplayText("Classpath", "extra classpath elements for monitor program");
        array.add(pClasspath);
        pTimeout = new NumericProperty("_Timeout", "60");
        pTimeout.setDisplayText("Timeout", "timeout in seconds");
        array.add(pTimeout);
        pTimeout.setParameterOptions(true, true, 1, true);
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setStateOptions(1);
        array.add(pStatus);
        StringProperty astringproperty1[] = new StringProperty[array.size()];
        for (int i = 0; i < array.size(); i ++) {
            astringproperty1[i] = (StringProperty) array.at(i);
        }

        String s2 = (COM.dragonflow.StandardMonitor.WebSphereMonitor.class).getName();
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
        addProperties(s2, astringproperty2);
        addClassElement(s2, Rule.stringToClassifier("countersInError > 0\terror"));
        addClassElement(s2, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s2, "description", "Monitors IBM WebSphere Application Server performance metrics.");
        setClassProperty(s2, "classType", "application");
        setClassProperty(s2, "help", "WebSphereMon.htm");
        setClassProperty(s2, "title", "WebSphere Application Server");
        setClassProperty(s2, "class", "WebSphereMonitor");
        setClassProperty(s2, "target", "_target");
        setClassProperty(s2, "topazName", "WebSphere");
        setClassProperty(s2, "topazType", "Web Application Server");
        setClassProperty(s2, "loadable", "true");
    }
}
