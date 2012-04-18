/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * WebLogic6xMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>WebLogic6xMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.NonDeferringClassLoader;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.ThreadPool;

public class WebLogic6xMonitor extends BrowsableBase {

    static NumericProperty pPort;

    static StringProperty pUsername;

    static StringProperty pPassword;

    static StringProperty pWeblogicJar;

    static BooleanProperty pSecure;

    public static StringProperty pServerName;

    static StringProperty pTarget;

    static StringProperty pStatus;

    static int nMaxCounters;

    private static HashMap cache = new HashMap();

    private static ThreadPool webLogicMonitorThreadsPool = new ThreadPool("WebLogicMonitors", null);

    public WebLogic6xMonitor() {
    }

    public String getHostname() {
        String s = getProperty(pTarget);
        if (s == null) {
            s = getProperty(pServerName);
        }
        return s;
    }

    protected boolean update() {
        int i;
        for (i = 0; i < nMaxCounters && getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1)).length() != 0; i ++) {
        }
        String as[] = new String[i];
        String as1[] = new String[i];
        for (int j = 0; j < i; j ++) {
            as[j] = getProperty(PROPERTY_NAME_COUNTER_NAME + (j + 1));
            as1[j] = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
        }

        String as2[] = new String[i];
        String s = monitorImplUpdate(as, as1, as2);
        if (stillActive()) {
            synchronized (this) {
                setProperty(pStatus, s);
                setProperty(pStateString, s);
                if (s.equals("OK")) {
                    String s1 = "";
                    boolean flag = false;
                    int l = 0;
                    for (int i1 = 0; i1 < i; i1 ++) {
                        String s2 = getProperty(PROPERTY_NAME_COUNTER_NAME + (i1 + 1));
                        String s3 = as2[i1];
                        if (filterValue(s3)) {
                            flag = true;
                            s3 = "n/a";
                        }
                        if (s3.indexOf("n/a") >= 0) {
                            l ++;
                        }
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1), s3);
                        s1 = s1 + s2 + " = " + s3;
                        if (i1 != i - 1) {
                            s1 = s1 + ", ";
                        }
                    }

                    if (flag) {
                        s1 = s1 + "RemoteRuntimeException - with nested exception [Start server side stack trace: java.lang.OutOfMemoryError  <<no stack trace available>> End]";
                    }
                    if (i > 0) {
                        setProperty(pStateString, s1);
                    }
                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), l);
                } else {
                    for (int k = 0; k < i; k ++) {
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (k + 1), "n/a");
                    }

                    setProperty(pNoData, "n/a");
                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
                }
            }
        }
        return true;
    }

    private boolean filterValue(String s) {
        if (s.length() > 500 && s.indexOf("RemoteRuntimeException - with nested exception") != -1) {
            int i = s.indexOf("Start server side stack trace: java.lang.OutOfMemoryError  <<no stack trace available>> End");
            if (i != -1) {
                i = s.indexOf("Start server side stack trace: java.lang.OutOfMemoryError  <<no stack trace available>> End", i + 50);
                if (i != -1) {
                    int j = s.indexOf("End");
                    if (j != -1) {
                        j = s.indexOf("server side stack trace", j);
                        if (j != -1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 
     */
    public String getBrowseData(StringBuffer stringbuffer) {
        try {
            return (String) callWL6Method("getBrowseData", new Class[] { StringBuffer.class }, new Object[] { stringbuffer });
        } catch (Exception e) {
            LogManager.log("Error", "WebLogic6x Failed to load counters: " + e.toString());
            return "Failed to load counters. See log for details.";
        }
    }

    public String setBrowseID(Array array) {
        if (array.size() < 2) {
            return "";
        } else {
            return (String) array.get(1) + "/" + (String) array.get(0);
        }
    }

    /**
     * 
     * 
     * @param as
     * @param as1
     * @param as2
     * @return
     */
    private String monitorImplUpdate(String as[], String as1[], String as2[]) {
        try {
            return (String) callWL6Method("update", new Class[] { String[].class, String[].class, String[].class }, new Object[] { as, as1, as2 });
        } catch (Exception e) {
            LogManager.log("Error", "WebLogic6x Update failed: " + e.toString());
            return "Update failed. See log for details.";
        }
    }

    /**
     * 
     * 
     * @param s
     * @param aclass
     * @param aobj
     * @return
     * @throws Exception
     */
    private Object callWL6Method(String s, Class aclass[], Object aobj[]) throws Exception {
        try {
            Object obj1;
            String s1 = getProperty(pServerName);
            String s2 = getProperty(pPort);
            String s4 = getProperty(pUsername);
            String s5 = getProperty(pPassword);
            boolean flag = getPropertyAsBoolean(pSecure);
            String s6 = getProperty(pWeblogicJar);
            String s7;
            if (flag) {
                s7 = "https";
            } else {
                s7 = "http";
            }
            String s8 = s7 + s1 + s2 + s4 + s5 + s6;
            NonDeferringClassLoader nondeferringclassloader = null;
            synchronized (this) {
                nondeferringclassloader = (NonDeferringClassLoader) cache.get(s8);
                if (nondeferringclassloader == null) {
                    if (s6 != null && !s6.equals("")) {
                        String s9 = null;
                        if (s6.charAt(0) == '/') {
                            s9 = s6.substring(1);
                        } else {
                            s9 = s6;
                        }
                        String s10 = "file:///" + Platform.getRoot() + File.separator + "classes" + File.separator;
                        URLClassLoader urlclassloader = new URLClassLoader(new URL[] { new URL(s10) });
                        nondeferringclassloader = new NonDeferringClassLoader(new URL[] { new URL("file:///" + s9) }, urlclassloader);
                    } else {
                        URL aurl[] = { new URL(s7 + "://" + s1 + "/classes/") };
                        nondeferringclassloader = new NonDeferringClassLoader(aurl);
                    }
                    cache.put(s8, nondeferringclassloader);
                }
            }
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            if (classloader != nondeferringclassloader) {
                Thread.currentThread().setContextClassLoader(nondeferringclassloader);
            }
            LogManager.log("RunMonitor", "WebLogic6x: Connected");
            LogManager.log("RunMonitor", "Detected WebLogic Version: " + nondeferringclassloader.loadClass("weblogic.version").getMethod("getReleaseBuildVersion", null).invoke(null, null));
            Class class1 = nondeferringclassloader.loadClass("COM.dragonflow.SiteView.Weblogic6xMonitorImpl");
            Object obj = class1.getConstructors()[0].newInstance(new Object[] { s1 + ":" + s2, s4, s5, new Boolean(flag) });
            Method method = class1.getMethod(s, aclass);
            obj1 = method.invoke(obj, aobj);
            Thread.currentThread().setContextClassLoader(classloader);
            return obj1;
        } catch (Throwable throwable) {
            LogManager.log("Error", throwable.toString());
            String s3 = throwable.getClass().getName();
            if (s3.equals("weblogic.common.internal.VersioningError")) {
                throw new Exception(
                        "Server may be running external jar files. This is a known WebLogic issue and has a few workarounds:\n1. Place the jar file after the weblogic.jar entry in the classpath\n2. Instead of a jar file, keep the external classes in a directory structure and include the root directory instead of the jar file in classpath\n");
            } else {
                throw new Exception("Unhandled exception thrown: " + s3 + " See log for details");
            }
        }
    }

    public Array getConnectionProperties() {
        Array array = new Array();
        array.add(pServerName);
        array.add(pPort);
        array.add(pUsername);
        array.add(pPassword);
        array.add(pSecure);
        array.add(pWeblogicJar);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE)) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s1.length() <= 0) {
                hashmap.put(stringproperty, "No counters selected");
            }
            return s;
        }
        if (stringproperty == pTarget && s.length() == 0) {
            return getProperty(pServerName);
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_WebLogic6xMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public COM.dragonflow.Utils.ThreadPool.SingleThread getThread() {
        COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = webLogicMonitorThreadsPool.getThread();
        singlethread.setNameIfNeeded(getProperty(pName) + "(" + getProperty(pGroupID) + "/" + getProperty(pID) + ") ");
        singlethread.setPriorityIfNeeded(7);
        thread = singlethread;
        return thread;
    }

    public boolean releaseOnExit() {
        if (Thread.currentThread() instanceof COM.dragonflow.Utils.ThreadPool.SingleThread) {
            COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = (COM.dragonflow.Utils.ThreadPool.SingleThread) Thread.currentThread();
            if (singlethread.getPoolName().equals(webLogicMonitorThreadsPool.getPoolName())) {
                return singlethread.getCustomProperty();
            }
        }
        return false;
    }

    public boolean stillActive() {
        if (Thread.currentThread() instanceof COM.dragonflow.Utils.ThreadPool.SingleThread) {
            COM.dragonflow.Utils.ThreadPool.SingleThread singlethread = (COM.dragonflow.Utils.ThreadPool.SingleThread) Thread.currentThread();
            if (singlethread.getPoolName().equals(webLogicMonitorThreadsPool.getPoolName())) {
                return singlethread.isSingleThreadAlive();
            }
        }
        return true;
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @param s5
     * @return
     */
    public static String getAvailableApplicationsXML(String s, String s1, String s2, String s3, String s4, String s5) {

        WebLogic6xMonitor weblogic6xmonitor = new WebLogic6xMonitor();
        weblogic6xmonitor.setProperty(pServerName, s);
        weblogic6xmonitor.setProperty(pPort, s1);
        weblogic6xmonitor.setProperty(pUsername, s2);
        weblogic6xmonitor.setProperty(pPassword, s3);
        weblogic6xmonitor.setProperty(pWeblogicJar, s4);
        weblogic6xmonitor.setProperty(pSecure, s5);
        StringBuffer stringbuffer = new StringBuffer();
        try {
            return (String) weblogic6xmonitor.callWL6Method("getBrowseData", new Class[] { StringBuffer.class }, new Object[] { stringbuffer });
        } catch (Exception e) {
            LogManager.log("Error", "Static getAvailableApplicationsXML failed: errorStr");
            return stringbuffer.toString();
        }
    }

    static {
        nMaxCounters = 30;
        Array array = new Array();
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_WebLogic6xMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 30;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
        pPort = new NumericProperty("_Port", "7001");
        pPort.setDisplayText("Port Number", "the port number of WebLogic server");
        array.add(pPort);
        pUsername = new StringProperty("_Username");
        pUsername.setDisplayText("User Name", "the username for login to WebLogic server");
        array.add(pUsername);
        pPassword = new StringProperty("_Password");
        pPassword.setDisplayText("Password", "the password for login to WebLogic server");
        pPassword.isPassword = true;
        array.add(pPassword);
        pServerName = new StringProperty("_server");
        pServerName.setDisplayText("Server", "the name of the server");
        pServerName.setParameterOptions(false, true, 4, false);
        array.add(pServerName);
        pTarget = new StringProperty("_target");
        pTarget.setDisplayText("Target", "the logical name of the server. If empty, the hostname will be used.");
        pTarget.setParameterOptions(true, 5, false);
        array.add(pTarget);
        pSecure = new BooleanProperty("_secure");
        pSecure.setDisplayText("Secure Server", "is this a secure server?");
        pSecure.setParameterOptions(false, true, 4, false);
        array.add(pSecure);
        pWeblogicJar = new StringProperty("_WeblogicJar");
        pWeblogicJar.setDisplayText("WebLogic Jar File",
                "For WebLogic 7.x or later, enter the absolute path to the weblogic.jar file on the SiteView machine. For example: <tt>c:\\bea\\weblogic7\\weblogic.jar</tt>. Leave blank to use default servlet (versions 6.x)");
        pWeblogicJar.setParameterOptions(false, true, 4, false);
        array.add(pWeblogicJar);
        pStatus = new StringProperty("status");
        pStatus.setLabel("status");
        pStatus.setStateOptions(1);
        array.add(pStatus);
        StringProperty astringproperty1[] = new StringProperty[array.size()];
        for (int i = 0; i < array.size(); i ++) {
            astringproperty1[i] = (StringProperty) array.get(i);
        }

        String s = (COM.dragonflow.StandardMonitor.WebLogic6xMonitor.class).getName();
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
        addProperties(s, astringproperty2);
        addClassElement(s, Rule.stringToClassifier("status != OK\terror"));
        addClassElement(s, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s, "description", "Monitors BEA WebLogic Application Server performance metrics.");
        setClassProperty(s, "classType", "application");
        setClassProperty(s, "help", "WebLogic6Mon.htm");
        setClassProperty(s, "title", "WebLogic Application Server");
        setClassProperty(s, "class", "WebLogic6xMonitor");
        setClassProperty(s, "target", "_target");
        setClassProperty(s, "topazName", "BEA WebLogic 6.0");
        setClassProperty(s, "topazType", "Web Application Server");
        setClassProperty(s, "loadable", "true");
    }
}
