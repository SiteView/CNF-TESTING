/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package COM.dragonflow.Utils.WebSphere;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Utils.WebSphere:
// ProcessWaiter, ReaderThread, WebSphereProcessLauncher

public class WebSphereProcessProperties {

    private static long siteViewToken = java.lang.System.currentTimeMillis();

    private static java.util.Properties props;

    private static String propsFilename;

    private static boolean enableDebugPrintouts = false;

    private static long heartBeatFrequency;

    public static final int VERSION_3_5 = 0;

    public static final int VERSION_4_X = 1;

    public static final int VERSION_5_X = 2;

    public static final String VERSION_3_5_STR = "3.5x";

    public static final String VERSION_4_X_STR = "4.x";

    public static final String VERSION_5_X_STR = "5.x";

    private String hostname;

    private String classpath;

    private String webSphereDir;

    private String clientProps;

    private String jvmName;

    private String api;

    private int hashKey;

    private boolean isWindows;

    private boolean isRunning;

    private java.lang.Process proc;

    private int version;

    private static final String WEBSPHERE_SERVICE_CLASSNAME = "COM.dragonflow.Utils.WebSphere.WebSphereServiceImpl";

    private static final String CODEBASE;

    private static final String HOSTNAME = "localhost";

    private static final String FILE_SEP = java.lang.System.getProperties().getProperty("file.separator");

    private static final String PATH_SEP = java.lang.System.getProperties().getProperty("path.separator");

    public WebSphereProcessProperties(String s, String s1, String s2, String s3, boolean flag, String s4) {
        clientProps = "sas.client.props";
        isRunning = false;
        hostname = s4.toLowerCase();
        classpath = s;
        webSphereDir = s2;
        isWindows = flag;
        setJVMName();
        setClientProps(s1, flag);
        detectVersion(s3);
        detectAPI(version);
        augmentClasspath();
        if (version == 1) {
            hashKey = (webSphereDir + classpath + version + clientProps + hostname).hashCode();
        } else {
            hashKey = (webSphereDir + classpath + version + clientProps).hashCode();
        }
    }

    private void setClientProps(String s, boolean flag) {
        if (s == null || s.length() == 0) {
            s = clientProps;
        }
        s = s.replace('\\', '/');
        if (s.charAt(0) != '/' && s.charAt(0) != ':') {
            s = webSphereDir.replace('\\', '/') + "/properties/" + s;
        }
        if (flag) {
            s = "/" + s;
        }
        clientProps = s;
    }

    public String getURL() {
        StringBuffer stringbuffer = (new StringBuffer("rmi://")).append("localhost").append(":").append(COM.dragonflow.Utils.WebSphere.WebSphereProcessLauncher.getRegistryPort());
        stringbuffer.append("/WebSphereService").append(hashKey);
        return stringbuffer.toString();
    }

    private void setJVMName() {
        if (isWindows) {
            jvmName = "javaw.exe";
        } else {
            jvmName = "java";
        }
    }

    private void detectAPI(int i) {
        switch (i) {
        case 1: // '\001'
            api = "PMI";
            break;

        case 2: // '\002'
            api = "JMX";
            break;
        }
    }

    private void detectVersion(String s) {
        if (s.equals("4.x")) {
            version = 1;
        } else {
            version = 2;
        }
    }

    private void augmentClasspath() {
        StringBuffer stringbuffer = new StringBuffer(classpath);
        if (stringbuffer.length() > 0) {
            stringbuffer.append(PATH_SEP);
        }
        stringbuffer.append(".").append(PATH_SEP).append("..").append(FILE_SEP).append("classes");
        int i = 1;
        do {
            String s = props.getProperty("classpath" + api + i);
            if (s == null) {
                break;
            }
            stringbuffer.append(PATH_SEP + webSphereDir + FILE_SEP + "lib" + FILE_SEP + s);
            i ++;
        } while (true);
        i = 1;
        do {
            String s1 = props.getProperty("properties" + api + i);
            if (s1 != null) {
                stringbuffer.append(PATH_SEP + webSphereDir + FILE_SEP + "properties" + FILE_SEP + s1);
                i ++;
            } else {
                classpath = stringbuffer.toString();
                return;
            }
        } while (true);
    }

    public String getCommandLine() {
        StringBuffer stringbuffer = new StringBuffer();
        String as[] = getCommandLineArray();
        for (int i = 0; i < as.length; i ++) {
            stringbuffer.append(as[i]).append(" ");
        }

        return stringbuffer.toString();
    }

    public String[] getCommandLineArray() {
        int i = webSphereDir.length();
        if (i > 0 && webSphereDir.charAt(i - 1) == '\\') {
            webSphereDir = webSphereDir.substring(0, i - 1);
        }
        String s = webSphereDir + FILE_SEP + "java" + FILE_SEP + "jre" + FILE_SEP + "bin" + FILE_SEP + jvmName;
        String s1 = getClass().getPackage().getName();
        java.util.ArrayList arraylist = new ArrayList();
        arraylist.add(s);
        arraylist.add("-classpath");
        arraylist.add(classpath);
        arraylist.add("-Djava.rmi.server.codebase=" + CODEBASE);
        arraylist.add("-Djava.rmi.server.hostname=localhost");
        arraylist.add("-D" + s1 + "." + "registryURL" + "=" + getURL());
        arraylist.add("-D" + s1 + "." + "heartBeatFrequency" + "=" + heartBeatFrequency);
        arraylist.add("-D" + s1 + "." + "token" + "=" + siteViewToken);
        arraylist.add("-Dcom.ibm.CORBA.ConfigURL=file:" + clientProps);
        arraylist.add("-Dcom.ibm.SOAP.ConfigURL=file:" + clientProps);
        String s2 = java.lang.System.getProperty("COM.dragonflow.Utils.WebSphere.remoteDebugger");
        if (s2 != null && s2.equalsIgnoreCase("true")) {
            arraylist.add("-Xdebug");
            arraylist.add("-Xnoagent");
            arraylist.add("-Djava.compiler=NONE");
            arraylist.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005");
        }
        if (enableDebugPrintouts) {
            arraylist.add("-D" + s1 + "." + "debug" + "=true");
        }
        arraylist.add("COM.dragonflow.Utils.WebSphere.WebSphereServiceImpl");
        String as[] = new String[arraylist.size()];
        as = (String[]) arraylist.toArray(as);
        return as;
    }

    public String getClasspath() {
        return classpath;
    }

    public String getWebSphereDir() {
        return webSphereDir;
    }

    public int getVersion() {
        return version;
    }

    public boolean isWindows() {
        return isWindows;
    }

    public String getJvmName() {
        return jvmName;
    }

    public String getApi() {
        return api;
    }

    public java.lang.Process getProcess() {
        return proc;
    }

    public void registerNewProcess(java.lang.Process process) {
        isRunning = true;
        proc = process;
        COM.dragonflow.Utils.WebSphere.ProcessWaiter processwaiter = new ProcessWaiter(this);
        processwaiter.start();
        String s = "WebSphereService[" + hashKey + "]: ";
        java.io.BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        COM.dragonflow.Utils.WebSphere.ReaderThread readerthread = new ReaderThread(bufferedreader, "RunMonitor", s);
        readerthread.start();
        java.io.BufferedReader bufferedreader1 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        COM.dragonflow.Utils.WebSphere.ReaderThread readerthread1 = new ReaderThread(bufferedreader1, "Error", s);
        readerthread1.start();
    }

    public int getHashKey() {
        return hashKey;
    }

    public java.lang.Integer getHashKeyAsInteger() {
        return new Integer(hashKey);
    }

    public boolean isRunning() {
        return isRunning;
    }

    protected void setRunning(boolean flag) {
        isRunning = flag;
    }

    static {
        props = new Properties();
        propsFilename = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "templates.applications" + java.io.File.separator + "websphere.props";
        heartBeatFrequency = 3000L;
        try {
            props.load(new FileInputStream(propsFilename));
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "Cannot open Websphere properties file: " + propsFilename);
        }
        HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_webSphereDebug").length() > 0) {
            enableDebugPrintouts = true;
        }
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_webSphereServiceHeartBeatFrequency");
        if (s.length() > 0) {
            try {
                heartBeatFrequency = java.lang.Long.parseLong(s);
            } catch (java.lang.NumberFormatException numberformatexception) {
                heartBeatFrequency = 3000L;
                COM.dragonflow.Log.LogManager.log("Error", "Could not parse a number from _webSphereServiceHeartBeatFrequency=" + s + ".  Using the default of " + heartBeatFrequency);
            }
        }
        CODEBASE = "file:/" + COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "classes" + java.io.File.separator;
    }
}
