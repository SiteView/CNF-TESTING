/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.HashMapIterator;

// Referenced classes of package COM.dragonflow.Log:
// LogThread, ConsoleLogger, DailyFileLogger, FileLogger,
// Logger, JdbcLogger, TopazLogger, SSEELogger

public class LogManager {

    static boolean unittest = false;

    static boolean shutdown = false;

    static HashMap loggers = new HashMap(true);

    static long lastLogTime = 0L;

    static COM.dragonflow.Log.JdbcLogger jdbcLogger = null;

//    static COM.dragonflow.Log.TopazLogger topazLogger = null;

    static COM.dragonflow.Log.SSEELogger sseeLogger = null;

    static HashMap rtConfig;

    static String strLoggers[];

    static COM.dragonflow.Log.LogThread logThread = null;

    public LogManager() {
    }

    public static void initialize(String s, int i, int j, int k, boolean flag, boolean flag1, int l, long l1) {
        logThread = new LogThread();
        logThread.start();
        HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        if (flag) {
            COM.dragonflow.Log.ConsoleLogger consolelogger = new ConsoleLogger();
            COM.dragonflow.Log.LogManager.registerLogger("RunMonitor", consolelogger);
            COM.dragonflow.Log.LogManager.registerLogger("Error", consolelogger);
            COM.dragonflow.Log.LogManager.registerLogger("Alert", consolelogger);
            COM.dragonflow.Log.LogManager.registerLogger("URL", consolelogger);
            COM.dragonflow.Log.LogManager.registerLogger("SiteViewLog", consolelogger);
            COM.dragonflow.Log.LogManager.registerLogger("AccountSiteViewLog", consolelogger);
        }
        String s1 = s + java.io.File.separator + "logs" + java.io.File.separator;
        try {
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_createDailyErrorLog").length() > 0) {
                COM.dragonflow.Log.DailyFileLogger dailyfilelogger = new DailyFileLogger(s1 + "error.log", l1, l);
                COM.dragonflow.Log.LogManager.registerLogger("Error", dailyfilelogger);
            } else {
                COM.dragonflow.Log.FileLogger filelogger = new FileLogger(s1 + "error.log", j, 0);
                COM.dragonflow.Log.LogManager.registerLogger("Error", filelogger);
            }
        } catch (java.io.IOException ioexception) {
            java.lang.System.err.println("Could not open error.log");
        }
        try {
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_createDailyRunMonitorLog").length() > 0) {
                COM.dragonflow.Log.DailyFileLogger dailyfilelogger1 = new DailyFileLogger(s1 + "RunMonitor.log", l1, l);
                COM.dragonflow.Log.LogManager.registerLogger("RunMonitor", dailyfilelogger1);
            } else {
                COM.dragonflow.Log.FileLogger filelogger1 = new FileLogger(s1 + "RunMonitor.log", j, 0);
                COM.dragonflow.Log.LogManager.registerLogger("RunMonitor", filelogger1);
            }
        } catch (java.io.IOException ioexception1) {
            java.lang.System.err.println("Could not open text log");
        }
        try {
            COM.dragonflow.Log.FileLogger filelogger2 = new FileLogger(s1 + "Operator.log", j, 0);
            COM.dragonflow.Log.LogManager.registerLogger("Operator", filelogger2);
        } catch (java.io.IOException ioexception2) {
            java.lang.System.err.println("Could not open operator log");
        }
        try {
            java.lang.Object obj = null;
            if (flag1) {
                obj = new DailyFileLogger(s1 + "SiteView.log", l1, l);
            } else {
                obj = new FileLogger(s1 + "SiteView.log", i, k);
            }
            COM.dragonflow.Log.LogManager.registerLogger("SiteViewLog", ((COM.dragonflow.Log.Logger) (obj)));
        } catch (java.io.IOException ioexception3) {
            java.lang.System.err.println("Could not open SiteView.log");
        }
        try {
            if (COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_createDailyAccessLog").length() > 0) {
                COM.dragonflow.Log.DailyFileLogger dailyfilelogger2 = new DailyFileLogger(s1 + "access.log", l1, l);
                COM.dragonflow.Log.LogManager.registerLogger("HTTP", dailyfilelogger2);
            } else {
                COM.dragonflow.Log.FileLogger filelogger3 = new FileLogger(s1 + "access.log", j, 0);
                filelogger3.setWriteDate(false);
                COM.dragonflow.Log.LogManager.registerLogger("HTTP", filelogger3);
            }
        } catch (java.io.IOException ioexception4) {
            java.lang.System.err.println("Could not open access.log");
        }
        try {
            COM.dragonflow.Log.FileLogger filelogger4 = new FileLogger(s1 + "alert.log", j, 0);
            COM.dragonflow.Log.LogManager.registerLogger("Alert", filelogger4);
        } catch (java.io.IOException ioexception5) {
            java.lang.System.err.println("Could not open alert.log");
        }
        try {
            COM.dragonflow.Log.FileLogger filelogger5 = new FileLogger(s1 + "url.log", j, 0);
            COM.dragonflow.Log.LogManager.registerLogger("URL", filelogger5);
        } catch (java.io.IOException ioexception6) {
            java.lang.System.err.println("Could not open url.log");
        }
        java.lang.Object obj1 = null;
        try {
            Array array = COM.dragonflow.Log.LogManager.getCustomLoggerClasses();
            obj1 = array;
            for (int i1 = 0; i1 < array.size(); i1 ++) {
                java.lang.Class class1 = (java.lang.Class) array.get(i1);
                obj1 = class1;
                COM.dragonflow.Log.Logger logger = (COM.dragonflow.Log.Logger) class1.newInstance();
                String s2 = "SiteViewLog";
                if (logger.customLogName().length() > 0) {
                    s2 = logger.customLogName();
                }
                COM.dragonflow.Log.LogManager.registerLogger(s2, logger);
            }

        } catch (java.lang.Exception exception) {
            java.lang.System.err.println("Could not open Custom Logger " + obj1 + " exception: " + exception);
        }
    }

    public static long getLastLogTime() {
        return lastLogTime;
    }

    public static void unregisterLogger(String s, COM.dragonflow.Log.Logger logger) {
        Enumeration enumeration = (Enumeration) loggers.values(s.toLowerCase());
        while (enumeration.hasMoreElements()) {
            COM.dragonflow.Log.Logger logger1 = (COM.dragonflow.Log.Logger) enumeration.nextElement();
            if (logger.equals(logger1)) {
                loggers.remove(s.toLowerCase());
            }
        } 
        logThread.removeLogger(logger);
    }

    public static void registerLogger(String s, COM.dragonflow.Log.Logger logger) {
        boolean flag = false;
        if (strLoggers.length == 0) {
            flag = true;
        } else {
            String s1 = logger.getClass().toString();
            String s2 = s1.substring(s1.lastIndexOf(".") + 1);
            int i = 0;
            do {
                if (i >= strLoggers.length) {
                    break;
                }
                if (strLoggers[i].equals(s.toLowerCase() + "=" + s2.toLowerCase())) {
                    flag = true;
                    break;
                }
                i ++;
            } while (true);
        }
        if (flag) {
            loggers.add(s.toLowerCase(), logger);
            logThread.addLogger(logger);
            if (s.startsWith("SiteViewLog")) {
                if (jdbcLogger == null && (logger instanceof COM.dragonflow.Log.JdbcLogger)) {
                    jdbcLogger = (COM.dragonflow.Log.JdbcLogger) logger;
                }
                if (!s.equals("SiteViewLog") && jdbcLogger != null) {
                    loggers.add(s.toLowerCase(), jdbcLogger);
                }
//                if (topazLogger == null && (logger instanceof COM.dragonflow.Log.TopazLogger)) {
//                    topazLogger = (COM.dragonflow.Log.TopazLogger) logger;
//                }
                if (sseeLogger == null && (logger instanceof COM.dragonflow.Log.SSEELogger)) {
                    sseeLogger = (COM.dragonflow.Log.SSEELogger) logger;
                }
//                if (!s.equals("SiteViewLog") && topazLogger != null) {
//                    loggers.add(s.toLowerCase(), topazLogger);
//                }
            }
        }
    }

    public static void shutdown() {
        shutdown = true;
        for (HashMapIterator hashmapiterator = loggers.begin(); !hashmapiterator.atEnd(); hashmapiterator.advance()) {
            ((COM.dragonflow.Log.Logger) hashmapiterator.value()).close();
        }

        logThread.end();
    }

    public static boolean loggerRegistered(String s) {
        return loggers.get(s.toLowerCase()) != null;
    }

    public static void log(String s) {
        COM.dragonflow.Log.LogManager.log("", s);
    }

    public static void log(String s, String s1) {
        COM.dragonflow.Log.LogManager.log(s, s1, COM.dragonflow.SiteView.Platform.makeDate());
    }

    public static void log(String s, String s1, java.util.Date date) {
        if (unittest || shutdown) {
            java.lang.System.out.println(s1);
            return;
        }
        s = s.toLowerCase();
        try {
            boolean flag = false;
            Enumeration enumeration = (Enumeration) loggers.values(s);
            while (enumeration.hasMoreElements()) {
                COM.dragonflow.Log.Logger logger = (COM.dragonflow.Log.Logger) enumeration.nextElement();
                logger.log(s, date, s1);
                flag = true;
                if (logger.echoTo.length() > 0) {
                    COM.dragonflow.Log.LogManager.log(logger.echoTo, s1, date);
                }
            } 
            if (flag) {
                lastLogTime = COM.dragonflow.SiteView.Platform.timeMillis();
            }
        } catch (java.lang.Exception exception) {
            java.lang.System.out.println("LOGERROR: " + exception.toString());
        }
        if (s.equalsIgnoreCase("Error")) {
            COM.dragonflow.Health.logEventHealth.log(s1);
        }
    }

    public static void log(COM.dragonflow.Properties.PropertiedObject propertiedobject) {
        COM.dragonflow.Log.LogManager.log("", propertiedobject);
    }

    public static void log(String s, COM.dragonflow.Properties.PropertiedObject propertiedobject) {
        COM.dragonflow.Log.LogManager.log(s, propertiedobject, COM.dragonflow.SiteView.Platform.makeDate());
    }

    public static void log(String s, COM.dragonflow.Properties.PropertiedObject propertiedobject, java.util.Date date) {
        if (unittest || shutdown) {
            return;
        }
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        String s1 = siteviewgroup.getProperty("_healthDisableLogging");
        if (s1.length() > 0 && (propertiedobject instanceof COM.dragonflow.SiteView.SiteViewObject)) {
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = ((COM.dragonflow.SiteView.SiteViewObject) propertiedobject).getOwner();
            if (COM.dragonflow.SiteView.Health.isInHealth(siteviewobject)) {
                return;
            }
        }
        s = s.toLowerCase();
        boolean flag = false;
        Enumeration enumeration = (Enumeration) loggers.values(s);
        while (enumeration.hasMoreElements()) {
            COM.dragonflow.Log.Logger logger = (COM.dragonflow.Log.Logger) enumeration.nextElement();
            String s2 = logger.getClass().getName();
            int i = s2.lastIndexOf('.');
            if (i > 0) {
                s2 = s2.substring(i + 1);
            }
            propertiedobject.currentStatus = "logging...";
            logger.log(s, date, propertiedobject);
            flag = true;
            if (logger.echoTo.length() > 0) {
                COM.dragonflow.Log.LogManager.log(logger.echoTo, propertiedobject, date);
            }
        } 
        if (flag) {
            lastLogTime = COM.dragonflow.SiteView.Platform.timeMillis();
        }
    }

    public static Array getCustomLoggerClasses() {
        Array array = new Array();
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/classes/CustomLogger/");
        if (!file.exists()) {
            return array;
        }
        String as[] = file.list();
        if (as != null) {
            for (int i = 0; i < as.length; i ++) {
                String s = "";
                if (!as[i].endsWith("Logger.class")) {
                    continue;
                }
                int j = as[i].lastIndexOf(".class");
                s = as[i].substring(0, j);
                try {
                    java.lang.Class class1 = java.lang.Class.forName("CustomLogger." + s);
                    array.add(class1);
                } catch (java.lang.Exception exception) {
                    java.lang.System.out.println("Could not create Custom Logger class " + s + "  " + exception);
                }
            }

        }
        return array;
    }

    public static String getStackTrace(java.lang.Throwable throwable) {
        java.lang.StackTraceElement astacktraceelement[] = throwable.getStackTrace();
        StringBuffer stringbuffer = new StringBuffer(throwable.getClass().getName() + "\n");
        for (int i = 0; i < astacktraceelement.length; i ++) {
            java.lang.StackTraceElement stacktraceelement = astacktraceelement[i];
            stringbuffer.append(stacktraceelement.toString() + "\n");
        }

        return stringbuffer.toString();
    }

    public static void logException(java.lang.Throwable throwable) {
        COM.dragonflow.Log.LogManager.log("error", COM.dragonflow.Log.LogManager.getStackTrace(throwable));
    }

    public static void triggerLogging(COM.dragonflow.Log.Logger logger) {
        logThread.triggerLogging(logger);
    }

    static {
        rtConfig = COM.dragonflow.SiteView.SiteViewGroup.loadRTConfig();
        strLoggers = COM.dragonflow.Utils.TextUtils.split(COM.dragonflow.Utils.TextUtils.getValue(rtConfig, "_loggers").toLowerCase(), ";");
        String s = java.lang.System.getProperty("LogManager.unittest");
        if (s != null && s.length() > 0) {
            unittest = true;
        }
    }
}
