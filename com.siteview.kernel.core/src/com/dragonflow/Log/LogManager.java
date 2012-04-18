/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Log;

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

// Referenced classes of package com.dragonflow.Log:
// LogThread, ConsoleLogger, DailyFileLogger, FileLogger,
// Logger, JdbcLogger, TopazLogger, SSEELogger

public class LogManager {

    static boolean unittest = false;

    static boolean shutdown = false;

    static HashMap loggers = new HashMap(true);

    static long lastLogTime = 0L;

    static com.dragonflow.Log.JdbcLogger jdbcLogger = null;

//    static com.dragonflow.Log.TopazLogger topazLogger = null;

    static com.dragonflow.Log.SSEELogger sseeLogger = null;

    static HashMap rtConfig;

    static String strLoggers[];

    static com.dragonflow.Log.LogThread logThread = null;

    public LogManager() {
    }

    public static void initialize(String s, int i, int j, int k, boolean flag, boolean flag1, int l, long l1) {
        logThread = new LogThread();
        logThread.start();
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        if (flag) {
            com.dragonflow.Log.ConsoleLogger consolelogger = new ConsoleLogger();
            com.dragonflow.Log.LogManager.registerLogger("RunMonitor", consolelogger);
            com.dragonflow.Log.LogManager.registerLogger("Error", consolelogger);
            com.dragonflow.Log.LogManager.registerLogger("Alert", consolelogger);
            com.dragonflow.Log.LogManager.registerLogger("URL", consolelogger);
            com.dragonflow.Log.LogManager.registerLogger("SiteViewLog", consolelogger);
            com.dragonflow.Log.LogManager.registerLogger("AccountSiteViewLog", consolelogger);
        }
        String s1 = s + java.io.File.separator + "logs" + java.io.File.separator;
        try {
            if (com.dragonflow.Utils.TextUtils.getValue(hashmap, "_createDailyErrorLog").length() > 0) {
                com.dragonflow.Log.DailyFileLogger dailyfilelogger = new DailyFileLogger(s1 + "error.log", l1, l);
                com.dragonflow.Log.LogManager.registerLogger("Error", dailyfilelogger);
            } else {
                com.dragonflow.Log.FileLogger filelogger = new FileLogger(s1 + "error.log", j, 0);
                com.dragonflow.Log.LogManager.registerLogger("Error", filelogger);
            }
        } catch (java.io.IOException ioexception) {
            java.lang.System.err.println("Could not open error.log");
        }
        try {
            if (com.dragonflow.Utils.TextUtils.getValue(hashmap, "_createDailyRunMonitorLog").length() > 0) {
                com.dragonflow.Log.DailyFileLogger dailyfilelogger1 = new DailyFileLogger(s1 + "RunMonitor.log", l1, l);
                com.dragonflow.Log.LogManager.registerLogger("RunMonitor", dailyfilelogger1);
            } else {
                com.dragonflow.Log.FileLogger filelogger1 = new FileLogger(s1 + "RunMonitor.log", j, 0);
                com.dragonflow.Log.LogManager.registerLogger("RunMonitor", filelogger1);
            }
        } catch (java.io.IOException ioexception1) {
            java.lang.System.err.println("Could not open text log");
        }
        try {
            com.dragonflow.Log.FileLogger filelogger2 = new FileLogger(s1 + "Operator.log", j, 0);
            com.dragonflow.Log.LogManager.registerLogger("Operator", filelogger2);
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
            com.dragonflow.Log.LogManager.registerLogger("SiteViewLog", ((com.dragonflow.Log.Logger) (obj)));
        } catch (java.io.IOException ioexception3) {
            java.lang.System.err.println("Could not open SiteView.log");
        }
        try {
            if (com.dragonflow.Utils.TextUtils.getValue(hashmap, "_createDailyAccessLog").length() > 0) {
                com.dragonflow.Log.DailyFileLogger dailyfilelogger2 = new DailyFileLogger(s1 + "access.log", l1, l);
                com.dragonflow.Log.LogManager.registerLogger("HTTP", dailyfilelogger2);
            } else {
                com.dragonflow.Log.FileLogger filelogger3 = new FileLogger(s1 + "access.log", j, 0);
                filelogger3.setWriteDate(false);
                com.dragonflow.Log.LogManager.registerLogger("HTTP", filelogger3);
            }
        } catch (java.io.IOException ioexception4) {
            java.lang.System.err.println("Could not open access.log");
        }
        try {
            com.dragonflow.Log.FileLogger filelogger4 = new FileLogger(s1 + "alert.log", j, 0);
            com.dragonflow.Log.LogManager.registerLogger("Alert", filelogger4);
        } catch (java.io.IOException ioexception5) {
            java.lang.System.err.println("Could not open alert.log");
        }
        try {
            com.dragonflow.Log.FileLogger filelogger5 = new FileLogger(s1 + "url.log", j, 0);
            com.dragonflow.Log.LogManager.registerLogger("URL", filelogger5);
        } catch (java.io.IOException ioexception6) {
            java.lang.System.err.println("Could not open url.log");
        }
        java.lang.Object obj1 = null;
        try {
            Array array = com.dragonflow.Log.LogManager.getCustomLoggerClasses();
            obj1 = array;
            for (int i1 = 0; i1 < array.size(); i1 ++) {
                java.lang.Class class1 = (java.lang.Class) array.get(i1);
                obj1 = class1;
                com.dragonflow.Log.Logger logger = (com.dragonflow.Log.Logger) class1.newInstance();
                String s2 = "SiteViewLog";
                if (logger.customLogName().length() > 0) {
                    s2 = logger.customLogName();
                }
                com.dragonflow.Log.LogManager.registerLogger(s2, logger);
            }

        } catch (java.lang.Exception exception) {
            java.lang.System.err.println("Could not open Custom Logger " + obj1 + " exception: " + exception);
        }
    }

    public static long getLastLogTime() {
        return lastLogTime;
    }

    public static void unregisterLogger(String s, com.dragonflow.Log.Logger logger) {
        Enumeration enumeration = (Enumeration) loggers.values(s.toLowerCase());
        while (enumeration.hasMoreElements()) {
            com.dragonflow.Log.Logger logger1 = (com.dragonflow.Log.Logger) enumeration.nextElement();
            if (logger.equals(logger1)) {
                loggers.remove(s.toLowerCase());
            }
        } 
        logThread.removeLogger(logger);
    }

    public static void registerLogger(String s, com.dragonflow.Log.Logger logger) {
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
                if (jdbcLogger == null && (logger instanceof com.dragonflow.Log.JdbcLogger)) {
                    jdbcLogger = (com.dragonflow.Log.JdbcLogger) logger;
                }
                if (!s.equals("SiteViewLog") && jdbcLogger != null) {
                    loggers.add(s.toLowerCase(), jdbcLogger);
                }
//                if (topazLogger == null && (logger instanceof com.dragonflow.Log.TopazLogger)) {
//                    topazLogger = (com.dragonflow.Log.TopazLogger) logger;
//                }
                if (sseeLogger == null && (logger instanceof com.dragonflow.Log.SSEELogger)) {
                    sseeLogger = (com.dragonflow.Log.SSEELogger) logger;
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
            ((com.dragonflow.Log.Logger) hashmapiterator.value()).close();
        }

        logThread.end();
    }

    public static boolean loggerRegistered(String s) {
        return loggers.get(s.toLowerCase()) != null;
    }

    public static void log(String s) {
        com.dragonflow.Log.LogManager.log("", s);
    }

    public static void log(String s, String s1) {
        com.dragonflow.Log.LogManager.log(s, s1, com.dragonflow.SiteView.Platform.makeDate());
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
                com.dragonflow.Log.Logger logger = (com.dragonflow.Log.Logger) enumeration.nextElement();
                logger.log(s, date, s1);
                flag = true;
                if (logger.echoTo.length() > 0) {
                    com.dragonflow.Log.LogManager.log(logger.echoTo, s1, date);
                }
            } 
            if (flag) {
                lastLogTime = com.dragonflow.SiteView.Platform.timeMillis();
            }
        } catch (java.lang.Exception exception) {
            java.lang.System.out.println("LOGERROR: " + exception.toString());
        }
        if (s.equalsIgnoreCase("Error")) {
            com.dragonflow.Health.logEventHealth.log(s1);
        }
    }

    public static void log(com.dragonflow.Properties.PropertiedObject propertiedobject) {
        com.dragonflow.Log.LogManager.log("", propertiedobject);
    }

    public static void log(String s, com.dragonflow.Properties.PropertiedObject propertiedobject) {
        com.dragonflow.Log.LogManager.log(s, propertiedobject, com.dragonflow.SiteView.Platform.makeDate());
    }

    public static void log(String s, com.dragonflow.Properties.PropertiedObject propertiedobject, java.util.Date date) {
        if (unittest || shutdown) {
            return;
        }
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        String s1 = siteviewgroup.getProperty("_healthDisableLogging");
        if (s1.length() > 0 && (propertiedobject instanceof com.dragonflow.SiteView.SiteViewObject)) {
            com.dragonflow.SiteView.SiteViewObject siteviewobject = ((com.dragonflow.SiteView.SiteViewObject) propertiedobject).getOwner();
            if (com.dragonflow.SiteView.Health.isInHealth(siteviewobject)) {
                return;
            }
        }
        s = s.toLowerCase();
        boolean flag = false;
        Enumeration enumeration = (Enumeration) loggers.values(s);
        while (enumeration.hasMoreElements()) {
            com.dragonflow.Log.Logger logger = (com.dragonflow.Log.Logger) enumeration.nextElement();
            String s2 = logger.getClass().getName();
            int i = s2.lastIndexOf('.');
            if (i > 0) {
                s2 = s2.substring(i + 1);
            }
            propertiedobject.currentStatus = "logging...";
            logger.log(s, date, propertiedobject);
            flag = true;
            if (logger.echoTo.length() > 0) {
                com.dragonflow.Log.LogManager.log(logger.echoTo, propertiedobject, date);
            }
        } 
        if (flag) {
            lastLogTime = com.dragonflow.SiteView.Platform.timeMillis();
        }
    }

    public static Array getCustomLoggerClasses() {
        Array array = new Array();
        java.io.File file = new File(com.dragonflow.SiteView.Platform.getRoot() + "/classes/CustomLogger/");
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
        com.dragonflow.Log.LogManager.log("error", com.dragonflow.Log.LogManager.getStackTrace(throwable));
    }

    public static void triggerLogging(com.dragonflow.Log.Logger logger) {
        logThread.triggerLogging(logger);
    }

    static {
        rtConfig = com.dragonflow.SiteView.SiteViewGroup.loadRTConfig();
        strLoggers = com.dragonflow.Utils.TextUtils.split(com.dragonflow.Utils.TextUtils.getValue(rtConfig, "_loggers").toLowerCase(), ";");
        String s = java.lang.System.getProperty("LogManager.unittest");
        if (s != null && s.length() > 0) {
            unittest = true;
        }
    }
}
