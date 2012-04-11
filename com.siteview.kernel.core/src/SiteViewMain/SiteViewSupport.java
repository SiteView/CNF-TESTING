// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:20
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import COM.dragonflow.Log.JdbcLogger;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.*;
import COM.dragonflow.SiteView.*;
import COM.dragonflow.StandardAction.Run;
import COM.dragonflow.StandardPreference.ScheduleInstancePreferences;
import COM.dragonflow.Utils.*;

import java.io.*;
import java.util.*;
import java.util.HashSet;

import jgl.*;
import jgl.HashMap;

public class SiteViewSupport
{

    public SiteViewSupport()
    {
    }

    public static void ShutdownProcess()
    {
        int i = -1;
        try
        {
            shuttingDown = true;
            LogManager.log("Error", Platform.productName + " shutting down..");
            if(siteView != null)
            {
                SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                i = siteviewgroup.getSettingAsLong("_systemExitCode", 0);
                siteviewgroup.stopSiteView(false, Platform.productName + " shut down");
                System.out.println("SiteView shutdown: exit.");
            }
        }
        catch(Exception exception)
        {
            System.out.println("SiteView error during shutdown, Exception: " + exception.getMessage());
            exception.printStackTrace();
        }
        finally
        {
            File file = new File(Platform.getRoot() + "\\dat\\events\\shutdown");
            if(file.exists())
                file.setLastModified(System.currentTimeMillis());
            Runtime.getRuntime().halt(i);
        }
    }

    public static void InitProcess()
    {
        Thread thread = Thread.currentThread();
        thread.setName("SiteView Main");
        thread.setPriority(8);
        (new File(Platform.expiredName())).delete();
        if(SiteViewGroup.killProcessesIsSet)
        {
            SiteViewGroup.setupKillableProcesses();
            SiteViewGroup.killProcesses();
        }
        File file = new File(Platform.getRoot() + "/htdocs/artwork/user.css");
        if(!file.exists())
        {
            FileOutputStream fileoutputstream = null;
            PrintWriter printwriter = null;
            try
            {
                fileoutputstream = new FileOutputStream(file);
                printwriter = FileUtils.MakeOutputWriter(fileoutputstream);
                printwriter.println(" ");
            }
            catch(IOException ioexception) { }
            finally
            {
                try
                {
                    if(printwriter != null)
                        printwriter.close();
                    if(fileoutputstream != null)
                        fileoutputstream.close();
                }
                catch(IOException ioexception1) { }
            }
        }
        boolean flag = true;
        if(argv.length >= 1)
        {
            if(argv[0].equals("x"))
            {
                Platform.setService(true);
                if(Platform.isWindows())
                {
                    Platform.pid = SiteViewGroup.monitorPID();
                    File file1 = new File(Platform.getRoot() + "/groups/dos.config");
                    flag = file1.exists();
                } else
                {
                    if(argv.length >= 4)
                        Platform.pid = TextUtils.toInt(argv[3]);
                    if(Platform.pid != 0)
                        SiteViewGroup.saveMonitorPID(Platform.pid);
                }
            }
            System.out.println("SiteView monitor process: " + Platform.pid);
        }
        if(argv.length >= 2)
            parentPid = argv[1];
        System.out.println("SiteView service process: " + parentPid);
        if(argv.length >= 3)
            if(argv[2].equals("-i"))
                flag = true;
            else
            if(argv[2].equals("-x"))
                flag = false;
        String s = "";
        for(int i = 0; i < argv.length; i++)
        {
            if(argv[i].equals("-config"))
            {
                if(argv.length > i + 1)
                {
                    String s1 = argv[i + 1];
                    Platform.customConfigPath = Platform.getRoot() + File.separator + "groups" + File.separator + s1;
                }
                continue;
            }
            if(argv[i].equals("-ss_server") && argv.length > i + 1)
                Platform.serverName = argv[i + 1];
        }

        HashMap hashmap = CopyDefaultConfig(false, true);
        if(hashmap == null)
        {
            System.out.println("Cannot read Master.config, Shutting down");
            System.exit(-1);
        }
        I18N.isI18N = TextUtils.getValue(hashmap, "_isI18N").length() > 0;
        if(TextUtils.getValue(hashmap, "_configJdbcURL").length() > 0)
            JdbcConfig.inInitStage = true;
        PortalDefaultConfig();
        CheckForKeyStore();
        if(TextUtils.getValue(hashmap, "_backupGroups").length() > 0)
        {
            File file2 = new File(Platform.getRoot() + "/groupsbackup");
            Date date = new Date();
            boolean flag1 = false;
            if(file2.exists())
            {
                long l = file2.lastModified();
                long l1 = date.getTime();
                flag1 = l < l1 - 0x4ef6d80L;
            } else
            {
                flag1 = true;
            }
            if(flag1)
                backupGroups();
        }
        String s2 = TextUtils.getValue(hashmap, "_redirectStandardError");
        if(s2.length() > 0)
        {
            File file3 = new File(Platform.getRoot() + File.separator + "logs" + File.separator + "stderr_" + TextUtils.dayToFileName() + ".log");
            System.out.println("Redirecting standard error to " + file3.getAbsolutePath());
            try
            {
                if(!file3.exists())
                    file3.createNewFile();
                System.setErr(new PrintStream(new FileOutputStream(file3, true)));
//	               Thread thread1 = new Thread(new Runnable(s2){
//                    public void run()
//                    {
//                        int j3 = 60;
//                        try
//                        {
//                            j3 = Integer.parseInt(standardErrorTime);
//                        }
//                        catch(Exception exception3) { }
//                        if(j3 <= 0)
//                            j3 = 60;
//                        System.err.println(" Printing timestamps to log every " + j3 + " seconds");
//                        do
//                            try
//                            {
//                                Thread.currentThread();
//                                Thread.sleep((long)j3 * 1000L);
//                                System.err.println(TextUtils.dateToString());
//                            }
//                            catch(InterruptedException interruptedexception) { }
//                        while(true);
//                    }            
//		            {
//		                super();
//		            }
//                });
//                thread1.start();
            }
            catch(IOException ioexception2)
            {
                System.err.println(" Unable redirect Standard Error to " + file3.getAbsolutePath() + " " + ioexception2.getMessage());
            }
        }
        if(TextUtils.getValue(hashmap, "_redirectStandardOut").length() > 0)
        {
            File file4 = new File(Platform.getRoot() + File.separator + "logs" + File.separator + "stdout_" + TextUtils.dayToFileName() + ".log");
            System.out.println("Redirecting standard output to " + file4.getAbsolutePath());
            try
            {
                if(!file4.exists())
                    file4.createNewFile();
                System.setOut(new PrintStream(new FileOutputStream(file4, true)));
            }
            catch(IOException ioexception3)
            {
                System.err.println(" Unable redirect Standard Output to " + file4.getAbsolutePath() + " " + ioexception3.getMessage());
            }
        }
        if(TextUtils.getValue(hashmap, "_disableRemoteCommandLine").length() > 0)
            SiteViewGroup.enableRemoteCommandLine = false;
        if(TextUtils.getValue(hashmap, "_traceRemoteCommandLine").length() > 0)
            Machine.traceAllMachines = true;
        if(TextUtils.getValue(hashmap, "_localPortRetryCount").length() > 0)
            RemoteCommandLine.localPortRetryCount = TextUtils.toInt(TextUtils.getValue(hashmap, "_localPortRetryCount"));
        float f = TextUtils.toFloat(TextUtils.getValue(hashmap, "_timeZoneOffset"));
        if(f != -999F)
        {
            int j = (int)(f * 60F * 60F) * 1000;
            PlatformNew.fixTimeZoneDefault(j);
        }
        Array array = Platform.setupTimeZoneOffset();
        int k = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxMonitorSkips"));
        if(k > 0)
            AtomicMonitor.maxMonitorSkips = k;
        String s3 = TextUtils.getValue(hashmap, "_oemCopyright");
        if(s3.length() > 0)
            Platform.copyright = s3;
        s3 = TextUtils.getValue(hashmap, "_oemCompanyName");
        if(s3.length() > 0)
            Platform.companyName = s3;
        s3 = TextUtils.getValue(hashmap, "_oemExampleDomain");
        if(s3.length() > 0)
            Platform.exampleDomain = s3;
        s3 = TextUtils.getValue(hashmap, "_oemExampleURL");
        if(s3.length() > 0)
            Platform.exampleURL = s3;
        s3 = TextUtils.getValue(hashmap, "_oemExampleMailServer");
        if(s3.length() > 0)
            Platform.exampleMailServer = s3;
        s3 = TextUtils.getValue(hashmap, "_oemHomeURLPrefix");
        if(s3.length() > 0)
            Platform.homeURLPrefix = s3;
        s3 = TextUtils.getValue(hashmap, "_oemSupportEmail");
        if(s3.length() > 0)
            Platform.supportEmail = s3;
        s3 = TextUtils.getValue(hashmap, "_oemSupportPhone");
        if(s3.length() > 0)
            Platform.supportPhone = s3;
        s3 = TextUtils.getValue(hashmap, "_oemSalesEmail");
        if(s3.length() > 0)
            Platform.salesEmail = s3;
        s3 = TextUtils.getValue(hashmap, "_oemSalesPhone");
        if(s3.length() > 0)
            Platform.salesPhone = s3;
        s3 = TextUtils.getValue(hashmap, "_oemSalesFooter");
        if(s3.length() > 0)
        {
            s3 = s3.replace('^', '\n');
            Platform.salesFooter = s3;
        }
        s3 = TextUtils.getValue(hashmap, "_oemSalesContactEmail");
        if(s3.length() > 0)
            Platform.salesContactEmail = s3;
        s3 = TextUtils.getValue(hashmap, "_oemSalesContact");
        if(s3.length() > 0)
        {
            s3 = s3.replace('^', '\n');
            Platform.salesContact = s3;
        }
        int i1 = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxScriptAlertProcesses"));
        if(i1 > 1)
            Run.setScriptLock(i1);
        int j1 = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxMonitorProcesses"));
        if(j1 > 1)
            Platform.monitorLock = new CounterLock(j1);
        String s4 = TextUtils.getValue(hashmap, "_httpCharSet");
        //if(s4 != null && s4.length() != 0)
          //  Platform.charSetTag = "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html;CHARSET=" + s4 + "\">\n";
		Platform.charSetTag = "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html;CHARSET=GBK\">\n";
		
        int k1 = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxLogSize"));
        int i2 = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxAuxLogSize"));
        int j2 = TextUtils.toInt(TextUtils.getValue(hashmap, "_logKeepDays"));
        long l2 = TextUtils.toInt(TextUtils.getValue(hashmap, "_dailyLogTotalLimit"));
        int k2 = TextUtils.toInt(TextUtils.getValue(hashmap, "_dailyLogKeepDays"));
        String s5 = TextUtils.getValue(hashmap, "_dailySiteViewLogs");
        LogManager.initialize(Platform.getRoot(), k1, i2, j2, flag, s5.length() > 0, k2, l2);
        String s6 = TextUtils.getValue(hashmap, "_logJdbcURLSiteViewLog");
        if(s6.length() != 0)
            try
            {
//                JdbcLogger jdbclogger = new JdbcLogger(hashmap);
//                LogManager.registerLogger("SiteViewLog", jdbclogger);
            }
            catch(Exception exception1)
            {
                LogManager.log("Error", "jdbc log, not opened: " + exception1);
            }
        Platform.checkExpired(hashmap);
        LogManager.log("Error", Platform.productName + " process started, " + Platform.getVersion());
        LogManager.log("RunMonitor", Platform.productName + " process started, " + Platform.getVersion());
        LogManager.log("RunMonitor", " " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        String s7 = System.getProperty("java.vm.name");
        if(s7 != null)
            LogManager.log("RunMonitor", " " + s7 + ", " + System.getProperty("java.vm.version"));
        else
            LogManager.log("RunMonitor", " Java Runtime " + System.getProperty("java.version"));
        LogManager.log("RunMonitor", " Time zone offset is " + (double)Platform.timeZoneOffset / 3600D + " hours");
        for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); LogManager.log("RunMonitor", " (" + enumeration.nextElement() + ")"));
        int i3 = TextUtils.toInt(TextUtils.getValue(hashmap, "_maxMonitorsRunning"));
        if(i3 > 0)
            MonitorQueue.monitorThrottle = new CounterLock(i3);
        LogManager.log("RunMonitor", " Maximum monitor threads: " + i3);
        if(TextUtils.getValue(hashmap, "_localeEnabled").equals("CHECKED"))
        {
            LocaleUtils.setLocale(TextUtils.getValue(hashmap, "_localeCountry"), TextUtils.getValue(hashmap, "_localeLanguage"));
            LocaleUtils.setResourceBundle();
            LocaleUtils.setNumberFormatter();
        }
        String s8 = TextUtils.getValue(hashmap, "_percentPrecision");
        if(s8.length() > 0 && TextUtils.isInteger(s8))
            NumericProperty.percentPrecision = TextUtils.toInt(s8);
        s8 = TextUtils.getValue(hashmap, "_minutesPrecision");
        if(s8.length() > 0 && TextUtils.isInteger(s8))
            NumericProperty.minutesPrecision = TextUtils.toInt(s8);
        s8 = TextUtils.getValue(hashmap, "_secondsPrecision");
        if(s8.length() > 0 && TextUtils.isInteger(s8))
            NumericProperty.secondsPrecision = TextUtils.toInt(s8);
        s8 = TextUtils.getValue(hashmap, "_millisecondsPrecision");
        if(s8.length() > 0 && TextUtils.isInteger(s8))
            NumericProperty.millisecondsPrecision = TextUtils.toInt(s8);
        String s9 = TextUtils.getValue(hashmap, "_CommandLineTimeout");
        if(s9.length() > 0 && TextUtils.isInteger(s9))
        {
            CommandLine.timeout = TextUtils.toLong(s9) * 1000L;
            if(CommandLine.timeout <= 0L)
                CommandLine.timeout = -1L;
        }
        try
        {
            Enumeration enumeration1 = hashmap.values("_urlLocation");
            HTTPUtils.locations = new Array();
            if(enumeration1.hasMoreElements())
            {
                String s10;
                for(; enumeration1.hasMoreElements(); HTTPUtils.locationMap.add(HTTPUtils.getLocationID(s10), s10))
                {
                    s10 = (String)enumeration1.nextElement();
                    HTTPUtils.locations.add(s10);
                }

            }
        }
        catch(Exception exception2)
        {
            LogManager.log("Error", "Error reading URL Remote location: " + exception2);
        }
        SocketStream.initialize(hashmap);
    }

    public static void InitProcess2()
    {
        siteView = new SiteViewGroup();
        if(siteView.getSetting("_debugLog").length() > 0)
        {
            LogManager.log("RunMonitor", "debug logging enabled");
            DebugWatcher.initializeDebugWatcher(Platform.getRoot(), true, true);
        }
    }

    private static void backupGroups()
    {
        String s = Platform.getRoot() + "/groupsbackup";
        String s1 = Platform.getRoot() + "/groups";
        cleanDir(s);
        File file = new File(s1);
        String as[] = file.list();
        File file1 = new File(s);
        if(!file1.exists())
            file1.mkdir();
        for(int i = 0; as != null && i < as.length; i++)
        {
            File file2 = new File(s1, as[i]);
            if(!file2.exists())
                continue;
            try
            {
                StringBuffer stringbuffer = FileUtils.readFile(s1 + "/" + as[i]);
                FileUtils.writeFile(s + "/" + as[i], stringbuffer.toString());
            }
            catch(Exception exception)
            {
                System.out.println("Could not backup file: " + as[i] + " to: " + s + " Error: " + exception.getMessage());
            }
        }

    }

    private static void cleanDir(String s)
    {
        File file = new File(s);
        if(file.exists())
        {
            String s1 = "attrib -R " + s + "/*.*";
            if(!Platform.isWindows())
                s1 = "chmod -R u+rw " + s;
            CommandLine commandline = new CommandLine();
            Array array = commandline.exec(s1);
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                Exception exception = null;
                File file1 = new File(s, as[i]);
                if(!file1.exists())
                    continue;
                try
                {
                    file1.delete();
                }
                catch(Exception exception1)
                {
                    exception = exception1;
                }
                if(!file1.exists())
                    continue;
                String s2 = "File Still found";
                if(exception != null)
                    s2 = exception.toString();
                LogManager.log("error", "error deleting " + file1.getAbsolutePath() + s2);
            }

        }
    }

    public static void StartProcess()
    {
        siteView.startSiteView();
        startingUp = false;
    }

    public static void WaitForProcess()
    {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        long l = Platform.timeMillis();
        String s = siteviewgroup.getSetting("_monitorProcessCheckDelay");
        if(s.length() > 0)
        {
            procCheckSleepInterval = (new Long(s)).longValue();
            if(procCheckSleepInterval < 30L)
                procCheckSleepInterval = 30L;
            else
            if(procCheckSleepInterval > 300L)
                procCheckSleepInterval = 300L;
        }
        LogManager.log("RunMonitor", "  (will auto restart in " + MirrorConfiguration.getRestartDelay() + " hours)");
        String s1 = System.getProperty("intellij.debug");
        boolean flag = s1 == null || s1.length() == 0;
label0:
        do
        {
            do
            {
                do
                {
                    if(Platform.timeMillis() >= l + 0x36ee80L * MirrorConfiguration.getRestartDelay())
                        break label0;
                    Platform.sleep(1000L * procCheckSleepInterval);
                } while(parentPid.length() <= 0);
                if(Platform.processOK(parentPid).length() <= 0)
                    continue;
                LogManager.log("RunMonitor", "stop requested");
                LogManager.log("Error", "stop requested");
                if(flag)
                    break label0;
            } while(SiteViewGroup.monitorPID() == Platform.pid);
            LogManager.log("RunMonitor", "stopping, replaced by new monitor process");
            LogManager.log("Error", "stopping, replaced by new monitor process");
        } while(!flag);
    }

    public static void mergeDefault(Array array, String s)
    {
        for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements();)
            if(s.equals(enumeration.nextElement()))
                return;

        array.add(s);
    }

    public static void mergeDefaults(Array array, Object obj)
    {
        if(obj != null)
            if(obj instanceof Array)
            {
                for(Enumeration enumeration = ((Array)obj).elements(); enumeration.hasMoreElements(); mergeDefault(array, (String)enumeration.nextElement()));
            } else
            {
                mergeDefault(array, (String)obj);
            }
    }

    public static HashMap CopyDefaultConfig()
    {
        return CopyDefaultConfig(true, false);
    }

    public static HashMap CopyDefaultConfig(boolean flag, boolean flag1)
    {
        HashMap hashmap;
        if(addOnlyIfMissing == null)
            synchronized(addOnlyIfMissingSync)
            {
                if(addOnlyIfMissing == null)
                {
                    addOnlyIfMissing = new HashSet();
                    addOnlyIfMissing.add(ScheduleInstancePreferences.ADDITINAL_SCHEDULE_SETTING_KEY);
                }
            }
        hashmap = new HashMap();
        String s;
        s = Platform.getRoot() + "/classes/default.config";
        Object obj1 = null;
        hashmap = MasterConfig.getMasterConfig();
        if(hashmap == null)
            return null;
        HashMap hashmap1 = null;
		try
		{
			Array array = FrameFile.readFromFile(s);
	        if(array != null && array.size() > 0)
	        {
	            hashmap1 = (HashMap)array.at(0);
	//            break MISSING_BLOCK_LABEL_127;
	        }
	        return hashmap;
		}
		catch(IOException obj)
		{
			
		}
        try
        {
            for(HashMapIterator hashmapiterator = hashmap1.begin(); !hashmapiterator.atEnd(); hashmapiterator.advance())
            {
                String s1 = (String)hashmapiterator.key();
                Object obj2 = hashmap.get(s1);
                Object obj3 = hashmapiterator.value();
                if(obj2 == null)
                {
                    hashmap.put(s1, obj3);
                    continue;
                }
                if(!(obj3 instanceof Array))
                    continue;
                Array array2 = new Array();
                mergeDefaults(array2, obj2);
                if(array2.size() == 0 || !addOnlyIfMissing.contains(s1))
                    mergeDefaults(array2, obj3);
                hashmap.put(s1, array2);
            }

            TextUtils.rewriteMapEntry(hashmap, "_private");
            if(flag1)
            {
                File file = new File(Platform.getRoot() + "/groups/master.config");
                Array array1 = new Array();
                array1.add(hashmap);
                if(FrameFile.printFile(file, array1, "_", true, true))
                    System.out.println("Error saving Master.config at startup");
            } else
            {
                MasterConfig.saveMasterConfig(hashmap);
            }
        }
        catch(Exception exception1)
        {
            LogManager.log("Error", "reading configuration: " + exception1);
        }
        return hashmap;
    }

    private static HashMap _modifyConfigForTopazWatchdog(HashMap hashmap)
    {
        hashmap.put("_overviewOptions", "toolbar=no,width=500,height=400,directories=no,status=no,scrollbars=yes,resizable=1,menubar=no");
        hashmap.put("_dailyLogKeepDays", "1");
        hashmap.put("_version", TextUtils.numberToString(Platform.versionAsNumber));
        hashmap.put("_reportHideLogo", "true");
        return hashmap;
    }

    public static void CheckForKeyStore()
    {
        if(!(new File(Platform.getRoot() + "/groups/serverKeystore")).exists())
            FileUtils.copyFile(Platform.getRoot() + "/classes/serverKeystore", Platform.getRoot() + "/groups/serverKeystore");
    }

    public static void PortalDefaultConfig()
    {
        if(Platform.isPortal())
        {
            if(!(new File(Platform.getRoot() + "/groups/views.config")).exists())
                FileUtils.copyFile(Platform.getRoot() + "/classes/defaultviews.config", Platform.getRoot() + "/groups/views.config");
            if(!(new File(Platform.getRoot() + "/groups/query.config")).exists())
                FileUtils.copyFile(Platform.getRoot() + "/classes/defaultquery.config", Platform.getRoot() + "/groups/query.config");
            if(!(new File(Platform.getRoot() + "/groups/layouts.config")).exists())
                FileUtils.copyFile(Platform.getRoot() + "/classes/defaultlayouts.config", Platform.getRoot() + "/groups/layouts.config");
            if(!(new File(Platform.getRoot() + "/groups/portal.config")).exists())
            {
                FileUtils.copyFile(Platform.getRoot() + "/classes/defaultportal.config", Platform.getRoot() + "/groups/portal.config");
                Array array = null;
                try
                {
                    array = FrameFile.readFromFile(Platform.getRoot() + "/groups/portal.config");
                }
                catch(Exception exception)
                {
                    System.out.println("Exception: trouble setting up defualt portal.config" + exception);
                    return;
                }
                Enumeration enumeration = array.elements();
                do
                {
                    if(!enumeration.hasMoreElements())
                        break;
                    HashMap hashmap = (HashMap)enumeration.nextElement();
                    String s = (String)hashmap.get("_id");
                    if(s != null)
                    {
                        File file = new File(Platform.getRoot() + "/portal/" + s);
                        if(!file.exists())
                            file.mkdir();
                    }
                } while(true);
            }
        }
    }

    public static boolean isStartingUp()
    {
        return startingUp;
    }

    static SiteViewGroup siteView = null;
    public static String argv[] = new String[0];
    static String parentPid = "";
    public static long procCheckSleepInterval = 180L;
    public static boolean shuttingDown = false;
    private static boolean startingUp = true;
    private static final String SHUTDOWN_FILE = "\\dat\\events\\shutdown";
    private static HashSet addOnlyIfMissing = null;
    private static Object addOnlyIfMissingSync = new Object();

}