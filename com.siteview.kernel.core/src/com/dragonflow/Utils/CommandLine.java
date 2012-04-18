 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package com.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */

import SiteViewMain.ServicePrinter;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Utils:
// CounterLock, RemoteCommandLine, ThreadPool, FileUtils,
// SocketSession, TextUtils

public class CommandLine
{

    public static String PERFEX_EXECSYNC_CMD = "-execsync ";
    public static String PERFEX_EXECSYNC_TIMEOUT = "ERROR: process timeout";
    public static String PERFEX_EXECSYNC_LOGON_FAILURE = "ERROR: Logon failed";
    public int exitValue;
    public long duration;
    long startTime;
    private static boolean showDebug = false;
    static boolean useProcessPool = true;
    static java.lang.Object execLock = new Object();
    public static boolean recording = false;
    public static boolean playback = false;
    public static Array recorded = null;
    public static long timeout = -1L;

    public CommandLine()
    {
        exitValue = -1;
    }

    public static void main(String args[])
    {
    }

    public static java.lang.Process execSync(String s)
        throws java.io.IOException
    {
        java.lang.Process process;
        synchronized(execLock)
        {
            process = java.lang.Runtime.getRuntime().exec(s);
        }
        return process;
    }

    public static java.lang.Process execSync(String as[])
        throws java.io.IOException
    {
        java.lang.Process process;
        synchronized(execLock)
        {
            process = java.lang.Runtime.getRuntime().exec(as);
        }
        return process;
    }

    protected String quoteCommandLineArg(String s)
    {
        StringBuffer stringbuffer = new StringBuffer("\"");
        int i = 0;
        while (true)
        {
            int j = s.indexOf('"', i);
            if(j != -1)
            {
                stringbuffer.append(s.substring(i, j));
                stringbuffer.append("\\\"");
                i = j + 1;
            } else
            {
                stringbuffer.append(s.substring(i));
                stringbuffer.append("\"");
                return stringbuffer.toString();
            }
        }
    }

    public Array exec(java.util.Vector vector)
    {
        return exec(vector, true);
    }

    public Array exec(java.util.Vector vector, boolean flag)
    {
        StringBuffer stringbuffer = new StringBuffer();
        java.util.Iterator iterator = vector.iterator();
        while (iterator.hasNext())
            {
            String s = (String)iterator.next();
            if(s != null)
            {
                if(stringbuffer.length() > 0)
                {
                    stringbuffer.append(" ");
                }
                if(flag)
                {
                    stringbuffer.append(quoteCommandLineArg(s));
                } else
                {
                    stringbuffer.append(s);
                }
            }
        } 
        return exec(stringbuffer.toString());
    }

    public Array exec(String s)
    {
        return exec(s, new CounterLock(1));
    }

    public Array exec(String s, String s1)
    {
        return exec(s, s1, new CounterLock(1));
    }

    public Array exec(String s, String s1, com.dragonflow.Utils.CounterLock counterlock, com.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        return exec(s, s1, counterlock, -1, atomicmonitor);
    }

    public Array exec(String s, String s1, com.dragonflow.Utils.CounterLock counterlock)
    {
        return exec(s, s1, counterlock, -1, null);
    }

    public Array exec(String s, String s1, com.dragonflow.Utils.CounterLock counterlock, int i)
    {
        return exec(s, s1, counterlock, i, null);
    }

    public Array exec(String s, String s1, com.dragonflow.Utils.CounterLock counterlock, int i, com.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        if(showDebug)
        {
            java.lang.System.out.println("EXEC (String,String,CounterLock) called with command \"" + s + "\"");
        }
        boolean flag = false;
        com.dragonflow.SiteView.Machine machine = null;
        if(i > 0)
        {
            s = com.dragonflow.Utils.CommandLine.getExecSyncCmd(s1, s, i, true);
        }
        if(com.dragonflow.SiteView.Platform.isNTRemote(s1) || com.dragonflow.SiteView.Machine.isNTSSH(s1))
        {
            machine = com.dragonflow.SiteView.Machine.getNTMachine(s1);
            if(machine != null && machine.getSetting("_method").equals("ssh"))
            {
                flag = true;
            }
        }
        if(!flag && (s1.length() == 0 || com.dragonflow.SiteView.Platform.isNTRemote(s1)))
        {
            return exec(s, counterlock, atomicmonitor);
        }
        if(machine == null)
        {
            machine = com.dragonflow.SiteView.Machine.getMachine(s1);
        }
        if(machine != null)
        {
            com.dragonflow.Utils.RemoteCommandLine remotecommandline = com.dragonflow.SiteView.Machine.getRemoteCommandLine(machine);
            if(remotecommandline != null)
            {
                if(playback)
                {
                    return com.dragonflow.Utils.CommandLine.nextOutput();
                }
                Array array = remotecommandline.exec(s, machine);
                exitValue = remotecommandline.exitValue;
                if(recording)
                {
                    com.dragonflow.Utils.CommandLine.recordOutput(array);
                }
                return array;
            }
            com.dragonflow.Log.LogManager.log("Error", "Unknown Remote Command Line method: " + machine.getProperty(com.dragonflow.SiteView.Machine.pMethod) + " for machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pName));
        } else
        {
            com.dragonflow.Log.LogManager.log("Error", "Could not find machine " + s1 + " to run remote");
        }
        return new Array();
    }

    public Array exec(String s, com.dragonflow.Utils.CounterLock counterlock)
    {
        return exec(s, counterlock, ((com.dragonflow.SiteView.AtomicMonitor) (null)));
    }

    public Array exec(String s, com.dragonflow.Utils.CounterLock counterlock, com.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        if(timeout > 0L)
        {
            return exec1(null, s, counterlock);
        }
        if(playback)
        {
            return com.dragonflow.Utils.CommandLine.nextOutput();
        }
        if(showDebug)
        {
            java.lang.System.out.println("EXEC (String,CounterLock) called with command \"" + s + "\"");
        }
        if(s.startsWith("http://"))
        {
            return runHTTPCommand(s);
        }
        Array array = new Array();
        startTime = java.lang.System.currentTimeMillis();
        if(showDebug)
        {
            java.lang.System.out.println("EXEC calling runtime exec");
        }
        boolean flag = false;
        java.lang.Process process = null;
        java.io.BufferedReader bufferedreader = null;
        if(useProcessPool && s.indexOf("webtrace") == -1)
        {
            int ai[] = new int[1];
            try
            {
                flag = com.dragonflow.ProcessUtils.ProcessMgr.exec(s, array, ai, atomicmonitor);
            }
            catch(java.io.IOException ioexception)
            {
                com.dragonflow.Log.LogManager.log("Error", "ProcessPool: Failed to execute: " + s + " exception: " + ioexception.getMessage());
            }
            if(flag)
            {
                exitValue = ai[0];
            }
        }
        if(!flag)
        {
            try
            {
                counterlock.get();
                process = com.dragonflow.Utils.CommandLine.execSync(s);
                if(showDebug)
                {
                    java.lang.System.out.println("EXEC runtime exec returned");
                }
                if(s.indexOf("webtrace") == -1)
                {
                    com.dragonflow.Utils.ThreadPool.SingleThread singlethread = SiteViewMain.ServicePrinter.servicePrinterThreadPool.getThread();
                    SiteViewMain.ServicePrinter serviceprinter = new ServicePrinter();
                    serviceprinter.printing = showDebug;
                    serviceprinter.stream = com.dragonflow.Utils.FileUtils.MakeInputReader(process.getErrorStream());
                    singlethread.activate(serviceprinter);
                    bufferedreader = com.dragonflow.Utils.FileUtils.MakeInputReader(process.getInputStream());
                } else
                {
                    bufferedreader = com.dragonflow.Utils.FileUtils.MakeInputReader(process.getErrorStream());
                }
                while(true)
                {
                    String s1 = bufferedreader.readLine();
                    if(s1 == null)
                    {
                        break;
                    }
                    array.add(s1);
                } 
                if(showDebug)
                {
                    java.lang.System.out.println("EXEC calling waitFor");
                }
                exitValue = process.waitFor();
                if(showDebug)
                {
                    java.lang.System.out.println("EXEC waitFor returned");
                }
            }
            catch(java.lang.Exception exception)
            {
                if(showDebug)
                {
                    java.lang.System.out.println("*** exec Exception");
                    java.lang.System.out.println("  cmd:" + s);
                    java.lang.System.out.println(" exit:" + exitValue);
                    java.lang.System.out.println("lines:" + array.size());
                    java.lang.System.out.println("error:" + exception.toString());
                }
            }
            finally
            {
                if(process != null)
                {
                    process.destroy();
                }
                process = null;
                try
                {
                    if(bufferedreader != null)
                    {
                        bufferedreader.close();
                    }
                }
                catch(java.io.IOException ioexception1)
                {
                    com.dragonflow.Log.LogManager.log("Error", "Failed to close the BufferedReader, exception " + ioexception1.getMessage());
                }
                counterlock.release();
            }
        }
        duration = java.lang.System.currentTimeMillis() - startTime;
        if(showDebug)
        {
            java.lang.System.out.println("*** exec");
            java.lang.System.out.println("  cmd:" + s);
            java.lang.System.out.println(" exit:" + exitValue);
            java.lang.System.out.println("lines:" + array.size());
            java.lang.System.out.println(" time:" + duration + "ms");
        }
        if(recording)
        {
            com.dragonflow.Utils.CommandLine.recordOutput(array);
        }
        return array;
    }

    public Array exec(String as[], String s)
    {
        return exec(as, s, new CounterLock(1));
    }

    public Array exec(String as[], String s, com.dragonflow.Utils.CounterLock counterlock)
    {
        if(showDebug)
        {
            java.lang.System.out.println("EXEC (String[],String,CounterLock) called with command \"" + as + "\"");
        }
        if(s.length() == 0 || com.dragonflow.SiteView.Platform.isNTRemote(s))
        {
            return exec(as, counterlock);
        }
        com.dragonflow.SiteView.Machine machine = com.dragonflow.SiteView.Machine.getMachine(s);
        if(machine != null)
        {
            com.dragonflow.Utils.RemoteCommandLine remotecommandline = com.dragonflow.SiteView.Machine.getRemoteCommandLine(machine);
            if(remotecommandline != null)
            {
                if(playback)
                {
                    return com.dragonflow.Utils.CommandLine.nextOutput();
                }
                Array array = remotecommandline.exec(as, machine);
                exitValue = remotecommandline.exitValue;
                if(recording)
                {
                    com.dragonflow.Utils.CommandLine.recordOutput(array);
                }
                return array;
            }
            com.dragonflow.Log.LogManager.log("Error", "Unknown Remote Command Line method: " + machine.getProperty(com.dragonflow.SiteView.Machine.pMethod) + " for machine " + machine.getProperty(com.dragonflow.SiteView.Machine.pName));
        } else
        {
            com.dragonflow.Log.LogManager.log("Error", "Could not find machine " + s + " to run remote");
        }
        return new Array();
    }

    public Array exec(String as[])
    {
        if(showDebug)
        {
            java.lang.System.out.println("EXEC (String[]) called with command \"" + as + "\"");
        }
        return exec(as, new CounterLock(1));
    }

    /**
     * 
     * 
     * @param as
     * @param counterlock
     * @return
     */
    public Array exec(String as[], com.dragonflow.Utils.CounterLock counterlock)
    {
        Array array;
        java.lang.Process process;
        if(timeout > 0L)
        {
            return exec1(as, null, counterlock);
        }
        if(playback)
        {
            return com.dragonflow.Utils.CommandLine.nextOutput();
        }
        array = new Array();
        process = null;
        if(showDebug)
        {
            java.lang.System.out.println("EXEC (String[],CounterLock) called with command \"" + as + "\"");
            for(int i = 0; i < as.length; i++)
            {
                java.lang.System.out.println(i + " [" + as[i] + "]");
            }

        }
        
        try {
        counterlock.get();
        startTime = java.lang.System.currentTimeMillis();
        if(showDebug)
        {
            java.lang.System.out.println("EXEC calling Runtime");
        }
        process = com.dragonflow.Utils.CommandLine.execSync(as);
        if(showDebug)
        {
            java.lang.System.out.println("EXEC Runtime returned");
        }
        com.dragonflow.Utils.ThreadPool.SingleThread singlethread = SiteViewMain.ServicePrinter.servicePrinterThreadPool.getThread();
        SiteViewMain.ServicePrinter serviceprinter = new ServicePrinter();
        serviceprinter.printing = showDebug;
        serviceprinter.stream = com.dragonflow.Utils.FileUtils.MakeInputReader(process.getErrorStream());
        singlethread.activate(serviceprinter);
        java.io.BufferedReader bufferedreader = com.dragonflow.Utils.FileUtils.MakeInputReader(process.getInputStream());
        while (true)
        {
            String s = bufferedreader.readLine();
            if(showDebug)
            {
                java.lang.System.out.println("*** line (" + s + ")");
            }
            if(s == null)
            {
                break;
            }
            array.add(s);
        } 
        
        if(showDebug)
        {
            java.lang.System.out.println("EXEC calling waitFor");
        }
        exitValue = process.waitFor();
        if(showDebug)
        {
            java.lang.System.out.println("EXEC waitFor returned");
        }
        process = null;
        duration = java.lang.System.currentTimeMillis() - startTime;
        counterlock.release();
        }
        catch (RuntimeException exception1) {
            counterlock.release();
            throw exception1;
        }
        catch (java.lang.Exception exception) {
        if(showDebug)
        {
            java.lang.System.out.println("*** exec: " + as + ", exit: " + exitValue + ", lines: " + array.size() + ", error: " + exception);
        }
        counterlock.release();
        }
    
        if(process != null)
        {
            process.destroy();
        }
        if(showDebug)
        {
            java.lang.System.out.println("**** exec: " + as + ", exit: " + exitValue + ", lines: " + array.size());
        }
        if(recording)
        {
            com.dragonflow.Utils.CommandLine.recordOutput(array);
        }
        return array;
    }

    public int getExitValue()
    {
        return exitValue;
    }

    public Array runHTTPCommand(String s)
    {
        return com.dragonflow.Utils.CommandLine.runHTTPCommand(s, "", "", "", "", "", "", this);
    }

    public static Array runHTTPCommand(String s, String s1, String s2, String s3, String s4, String s5, String s6, com.dragonflow.Utils.CommandLine commandline)
    {
        StringBuffer stringbuffer = new StringBuffer();
        com.dragonflow.Utils.SocketSession socketsession = com.dragonflow.Utils.SocketSession.getSession(null);
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        long l = siteviewgroup.getSettingAsLong("urlContentMatchMax", 51200);
        int i = com.dragonflow.Utils.TextUtils.toInt(s6) * 1000;
        if(i <= 0)
        {
            i = com.dragonflow.StandardMonitor.URLMonitor.DEFAULT_TIMEOUT;
        }
        long al[] = com.dragonflow.StandardMonitor.URLMonitor.checkURL(socketsession, s, "", "", s3, s4, s5, null, s1, s2, "", stringbuffer, l, "", 0, i, null);
        long l1 = al[0];
        socketsession.close();
        if(l1 != 200L)
        {
            com.dragonflow.Log.LogManager.log("Error", "HTTP CommandLine Error: " + l1);
            com.dragonflow.Log.LogManager.log("Error", "URL was: " + s);
            com.dragonflow.Log.LogManager.log("Error", "Content was:\n" + stringbuffer);
        }
        if(commandline != null)
        {
            commandline.exitValue = (int)l1;
            if(commandline.exitValue == com.dragonflow.StandardMonitor.URLMonitor.kURLok)
            {
                commandline.exitValue = 0;
            }
            commandline.duration = al[1];
        }
        int j = -1;
        byte byte0 = -1;
        String s7 = com.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer.toString());
        Array array = new Array();
        boolean flag = false;
            int k;
            while ((k = s7.indexOf("\n", j + 1)) >= 0)
            {
            String s8 = s7.substring(j + 1, k);
            j = k;
            if(!flag && s8.startsWith("<PRE>"))
            {
                flag = true;
                continue;
            }
            if(flag && s8.startsWith("</PRE>"))
            {
                flag = false;
                break;
            }
            if(flag)
            {
                array.add(s8);
            }
        } 
        return array;
    }

    public static void startRecording()
    {
        recorded = new Array();
        recording = true;
    }

    public static void recordOutput(Array array)
    {
        if(recording)
        {
            recorded.add(array);
        }
    }

    public static void stopRecording()
    {
        recording = false;
    }

    public static void startPlayback()
    {
        com.dragonflow.Utils.CommandLine.stopRecording();
        playback = true;
    }

    public static Array nextOutput()
    {
        Array array;
        if(recorded.size() >= 1)
        {
            array = (Array)recorded.popFront();
        } else
        {
            array = new Array();
            array.add("*** no more output to play back ***");
        }
        return array;
    }

    public static void stopPlayback()
    {
        playback = false;
        recorded = null;
    }

    static String arrayToString(Array array)
    {
        String s = "";
        for(int i = 0; i < array.size(); i++)
        {
            s = s + array.get(i);
            s = s + "\n";
        }

        return s;
    }

    boolean parseInput(char c, StringBuffer stringbuffer)
    {
        if(c == '\n')
        {
            return true;
        }
        if(c != '\r')
        {
            stringbuffer.append(c);
        }
        return false;
    }

    /**
     * 
     * 
     * @param as
     * @param s
     * @param counterlock
     * @return
     */
    Array exec1(String as[], String s, com.dragonflow.Utils.CounterLock counterlock)
    {
        byte byte0;
        StringBuffer stringbuffer;
        Array array;
        java.lang.Process process;
        byte0 = 2;
        if(playback)
        {
            return com.dragonflow.Utils.CommandLine.nextOutput();
        }
        stringbuffer = new StringBuffer("");
        if(s != null)
        {
            stringbuffer.append(s);
        } else
        {
            for(int i = 0; i < as.length; i++)
            {
                stringbuffer.append(i + " [" + as[i] + "],");
            }

        }
        array = new Array();
        process = null;
        if(showDebug)
        {
            java.lang.System.out.println("EXEC (String,CounterLock) called with command: \"" + stringbuffer.toString() + "\"");
        }
        
        try {
        counterlock.get();
        for(int j = 0; j < byte0; j++)
        {
            startTime = java.lang.System.currentTimeMillis();
            long l = startTime + timeout;
            if(showDebug)
            {
                java.lang.System.out.println("EXEC calling Runtime, loopCnt=" + j);
            }
            if(s != null)
            {
                process = com.dragonflow.Utils.CommandLine.execSync(s);
            } else
            {
                process = com.dragonflow.Utils.CommandLine.execSync(as);
            }
            if(showDebug)
            {
                java.lang.System.out.println("EXEC Runtime returned");
            }
            java.io.BufferedReader bufferedreader = com.dragonflow.Utils.FileUtils.MakeInputReader(process.getInputStream());
            java.io.BufferedReader bufferedreader1 = com.dragonflow.Utils.FileUtils.MakeInputReader(process.getErrorStream());
            StringBuffer stringbuffer2 = new StringBuffer("");
            StringBuffer stringbuffer3 = new StringBuffer("");
            boolean flag = false;
            int k = 0;
            while (!flag)
                {
                try
                {
                    while (bufferedreader.ready())
                        {
                        if(parseInput((char)bufferedreader.read(), stringbuffer3))
                        {
                            array.add(stringbuffer3.toString());
                            stringbuffer3.setLength(0);
                            l = java.lang.System.currentTimeMillis() + timeout;
                        }
                    } 
                    
                    while (bufferedreader1.ready()) {
                        stringbuffer2.append((char)bufferedreader1.read());
                    }
                    
                    exitValue = process.exitValue();
                    flag = true;
                    j = byte0;
                    while (bufferedreader.ready())
                        {
                        if(parseInput((char)bufferedreader.read(), stringbuffer3))
                        {
                            array.add(stringbuffer3.toString());
                            stringbuffer3.setLength(0);
                        }
                    } 
                    
                    while (bufferedreader1.ready()) {
                        stringbuffer2.append((char)bufferedreader1.read());
                    }
                    if(stringbuffer3.length() > 0)
                    {
                        array.add(stringbuffer3.toString());
                        stringbuffer3.setLength(0);
                    }
                }
                catch(java.lang.IllegalThreadStateException illegalthreadstateexception)
                {
                    if(java.lang.System.currentTimeMillis() >= l)
                    {
                        process.destroy();
                        flag = true;
                        StringBuffer stringbuffer5 = new StringBuffer("***** exec: Process destroyed, cmd:" + stringbuffer.toString());
                        com.dragonflow.Log.LogManager.log("Error", stringbuffer5.toString());
                        if(showDebug)
                        {
                            java.lang.System.out.println(stringbuffer5.toString());
                        }
                    } else
                    {
                        com.dragonflow.SiteView.Platform.sleep(k >= 10 ? '\u01F4' : 150);
                        k++;
                    }
                }
            } 
            
            if(showDebug)
            {
                java.lang.System.out.println("***** exec # lines=" + array.size() + ", err.length()=" + stringbuffer2.length());
            }
            if(stringbuffer2.length() <= 0)
            {
                continue;
            }
            StringBuffer stringbuffer4 = new StringBuffer("***** exec: errorBuffer=" + stringbuffer2.toString() + ", cmd:" + stringbuffer.toString());
            com.dragonflow.Log.LogManager.log("Error", stringbuffer4.toString());
            if(showDebug)
            {
                java.lang.System.out.println(stringbuffer4.toString());
            }
        }

        process = null;
        duration = java.lang.System.currentTimeMillis() - startTime;
        counterlock.release();
        }
        catch (RuntimeException exception1) {
            counterlock.release();
            throw exception1;
            }
        catch (java.lang.Exception exception) {
        StringBuffer stringbuffer1 = new StringBuffer("***** exec: Exception:" + exception.toString() + ", cmd:" + stringbuffer.toString());
        com.dragonflow.Log.LogManager.log("Error", stringbuffer1.toString());
        if(showDebug)
        {
            java.lang.System.out.println(stringbuffer1.toString());
            java.lang.System.out.println(" exit:" + exitValue);
            java.lang.System.out.println("lines:" + array.size());
            java.lang.System.out.println("error:" + exception.toString());
        }
        counterlock.release();
        }
        
        if(process != null)
        {
            process.destroy();
        }
        if(showDebug)
        {
            java.lang.System.out.println("*** exec cmd:" + stringbuffer.toString());
            java.lang.System.out.println(" exit:" + exitValue);
            java.lang.System.out.println("lines:" + array.size());
            java.lang.System.out.println(" time:" + duration + "ms");
        }
        if(recording)
        {
            com.dragonflow.Utils.CommandLine.recordOutput(array);
        }
        return array;
    }

    public static String getExecSyncCmd(String s, String s1, int i, boolean flag)
    {
        return com.dragonflow.Utils.CommandLine.getExecSyncCmd(s, s1, i, flag, "", "", "", "", false);
    }

    public static String getExecSyncCmd(String s, String s1, int i, boolean flag, String s2)
    {
        return com.dragonflow.Utils.CommandLine.getExecSyncCmd(s, s1, i, flag, s2, "", "", "", false);
    }

    public static String getExecSyncCmd(String s, String s1, int i, boolean flag, String s2, String s3, String s4, String s5, 
            boolean flag1)
    {
        boolean flag2 = false;
        if(com.dragonflow.SiteView.Platform.isWindows())
        {
            if(s == null || s.length() == 0)
            {
                flag2 = true;
            } else
            {
                com.dragonflow.SiteView.Machine machine = null;
                Object obj = null;
                machine = com.dragonflow.SiteView.Machine.getNTMachine(s);
                if(machine != null)
                {
                    String s7 = machine.getSetting("_method");
                    if(s7.equals("") || s7.equalsIgnoreCase("NetBIOS"))
                    {
                        flag2 = true;
                    }
                }
            }
        }
        if(!flag2)
        {
            return s1;
        }
        String s6 = " 0 ";
        if(flag)
        {
            s6 = " 1 ";
        }
        if(s2 == null || s2.length() == 0)
        {
            s2 = "default";
        }
        String s8 = "";
        if(s3.length() > 0)
        {
            s8 = s8 + " -u " + s3;
        }
        if(s5.length() > 0)
        {
            s8 = s8 + " -p " + s5;
        }
        if(s4.length() > 0)
        {
            s8 = s8 + " -do " + s4;
        }
        if(com.dragonflow.SiteView.Platform.isWindows())
        {
            s1 = com.dragonflow.Utils.TextUtils.replaceChar(s1, '"', "\\\"");
            return com.dragonflow.SiteView.Platform.perfexCommand(s) + PERFEX_EXECSYNC_CMD + "\"" + s1 + "\" " + i + s6 + s2 + (flag1 ? " 1" : " 0") + s8;
        } else
        {
            return s1;
        }
    }

    /**
     * 
     * 
     * @param s
     * @param i
     * @return
     */
    public static java.lang.Process killProcessesByName(String s, int i)
    {
        try {
        if(com.dragonflow.SiteView.Platform.isWindows())
        {
        return com.dragonflow.Utils.CommandLine.execSync(com.dragonflow.Utils.CommandLine.getExecSyncCmd("", com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "tools" + java.io.File.separator + "kill " + s, i, true));
        }
        }
        catch (java.io.IOException ioexception) {
        ioexception.printStackTrace();
        }
        return null;
    }

    static 
    {
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_processPool");
        if(s.length() > 0 && s.equals("false"))
        {
            useProcessPool = false;
        }
        showDebug = java.lang.System.getProperty("CommandLine.debug") != null;
    }
}
