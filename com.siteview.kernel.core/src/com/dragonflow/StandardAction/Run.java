/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Vector;

import com.dragonflow.Properties.NumericProperty;
import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;
import com.dragonflow.Utils.CommandLine;
import com.dragonflow.Utils.CounterLock;
import com.dragonflow.Utils.RemoteFile;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class Run extends com.dragonflow.SiteView.ServerAction
{

    static com.dragonflow.Properties.StringProperty pScript;
    static com.dragonflow.Properties.StringProperty pTemplate;
    static com.dragonflow.Properties.StringProperty pParameters;
    static com.dragonflow.Properties.StringProperty pTimeout;
    static final String DEFAULT_TIMEOUT = "-1";
    static com.dragonflow.Utils.CounterLock scriptLock = null;
    static Array scriptIDs = null;

    public void initializeFromArguments(Array array, HashMap hashmap)
    {
        initializeMachine(array, hashmap);
        if(array.size() > 0)
        {
            setProperty(pScript, array.get(0));
        }
        if(array.size() > 1)
        {
            setProperty(pTemplate, array.get(1));
        } else
        {
            setProperty(pTemplate, "Default");
        }
        if(array.size() > 2)
        {
            String s = (String)array.get(2);
            s = s.replace('_', ' ');
            s = s.replace('#', '_');
            setProperty(pParameters, s);
        } else
        {
            setProperty(pParameters, "");
        }
    }

    public String getActionString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("run");
        setMachineInActionString(stringbuffer);
        String s = com.dragonflow.Utils.I18N.toDefaultEncoding(getProperty(pScript));
        if(s.length() > 0)
        {
            stringbuffer.append(" ");
            stringbuffer.append(s);
        }
        String s1 = com.dragonflow.Utils.I18N.toDefaultEncoding(getProperty(pTemplate));
        String s2 = com.dragonflow.Utils.I18N.toDefaultEncoding(getProperty(pParameters));
        if(s2.length() > 0 && s1.length() == 0)
        {
            s1 = "Default";
        }
        if(s1.length() > 0 && (s2.length() > 0 || !s1.equals("Default")))
        {
            stringbuffer.append(" " + s1);
        }
        if(s2.length() > 0)
        {
            stringbuffer.append(" ");
            s2 = s2.replace('_', '#');
            stringbuffer.append(s2.replace(' ', '_'));
        }
        return stringbuffer.toString();
    }

    public String getActionDescription()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((String)getClassProperty("label"));
        String s = getProperty(pScript);
        if(s.length() > 0)
        {
            stringbuffer.append(" \"" + s + "\"");
        }
        String s1 = getProperty(pTemplate);
        if(s1.length() > 0 && !s1.equals("Default"))
        {
            stringbuffer.append(" " + s1);
        }
        String s2 = getProperty(pParameters);
        if(s2.length() > 0)
        {
            stringbuffer.append(" " + s2);
        }
        appendMachineDescription(stringbuffer);
        return stringbuffer.toString();
    }

    public boolean defaultsAreSet(HashMap hashmap)
    {
        return true;
    }

    public String verify(com.dragonflow.Properties.StringProperty stringproperty, String s, com.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pParameters && com.dragonflow.Utils.TextUtils.hasChars(s, "`;&|"))
        {
            hashmap.put(stringproperty, "script parameters have illegal characters");
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public java.util.Vector getScalarValues(com.dragonflow.Properties.ScalarProperty scalarproperty, com.dragonflow.HTTP.HTTPRequest httprequest, com.dragonflow.Page.CGI cgi)
        throws com.dragonflow.SiteViewException.SiteViewException
    {
        if(scalarproperty == pTemplate)
        {
            java.util.Vector vector = com.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.script", httprequest);
            java.util.Vector vector1 = new Vector();
            for(int i = 0; i < vector.size(); i++)
            {
                vector1.addElement(vector.elementAt(i));
                vector1.addElement(vector.elementAt(i));
            }

            return vector1;
        }
        if(scalarproperty == pScript)
        {
            return com.dragonflow.StandardMonitor.ScriptMonitor.getScriptList(getProperty(pMachineName), httprequest);
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public Run()
    {
        runType = 2;
    }

    public boolean execute()
    {
        return executeSync();
    }

    public static void setScriptLock(int i)
    {
        scriptLock = new CounterLock(i);
        scriptIDs = new Array();
        for(int j = 0; j < i; j++)
        {
            scriptIDs.add("" + j);
        }

    }

    static String getScriptLock()
    {
        String s;
        synchronized(scriptLock)
        {
            scriptLock.get();
            s = (String)scriptIDs.popFront();
        }
        return s;
    }

    static void releaseScriptLock(String s)
    {
        synchronized(scriptLock)
        {
            scriptLock.release();
            scriptIDs.pushBack(s);
        }
    }

    String[] appendParameters(String as[], String s)
    {
        String as1[] = as;
        if(s.length() != 0)
        {
            String as2[] = com.dragonflow.Utils.TextUtils.tokenize(s);
            for(int i = 0; i < as2.length; i++)
            {
                String s1 = as2[i];
                s1 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.createFromTemplate(s1));
                if(com.dragonflow.Utils.TextUtils.hasChars(s1, "`;&|<>"))
                {
                    com.dragonflow.Log.LogManager.log("Error", "Removed illegal characters from script parameter \"" + s1 + "\"");
                    s1 = com.dragonflow.Utils.TextUtils.removeChars(s1, "`;&|<>");
                }
                if(s1.length() >= 2)
                {
                    if(s1.startsWith("\"") && s1.endsWith("\""))
                    {
                        s1 = s1.substring(1, s1.length() - 1);
                    }
                    if(s1.startsWith("'") && s1.endsWith("'"))
                    {
                        s1 = s1.substring(1, s1.length() - 1);
                    }
                }
                if(com.dragonflow.SiteView.Platform.isWindows() && com.dragonflow.Utils.TextUtils.hasSpaces(s1))
                {
                    s1 = '"' + s1 + '"';
                }
                as2[i] = s1;
            }

            as1 = new String[as.length + as2.length];
            for(int j = 0; j < as.length; j++)
            {
                as1[j] = as[j];
            }

            for(int k = 0; k < as2.length; k++)
            {
                as1[as.length + k] = as2[k];
            }

        }
        return as1;
    }

    /**
     * 
     * 
     * @return
     */
    public boolean executeSync()
    {
        boolean flag;
        String s;
        flag = false;
        s = com.dragonflow.StandardAction.Run.getScriptLock();
        
        try {
        String s1 = "";
        long l = -1L;
        String s2 = "";
        String s3 = getMachineIDFromArgs();
        String s4 = "alert" + s + ".txt";
        String s5 = com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "scripts" + java.io.File.separator + s4;
        String s6 = "";
        if(com.dragonflow.SiteView.Platform.isRemote(s3))
        {
            com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            s6 = siteviewgroup.mainURL() + "/scripts/" + s4;
        }
        String s7;
        if(args.length == 0)
        {
            s1 = "missing script name";
            s7 = "missing";
        } else
        {
            s7 = args[0];
        }
        String s8 = "Default";
        if(args.length > 1 && args[1].length() > 0)
        {
            s8 = args[1];
        }
        String s9 = "";
        if(args.length > 2 && args[2].length() > 0)
        {
            s9 = args[2];
            s9 = s9.replace('_', ' ');
            s9 = s9.replace('#', '_');
        }
        if(s1.length() == 0)
        {
            StringBuffer stringbuffer = new StringBuffer();
            s1 = createMessage(stringbuffer, "templates.script", s8);
            if(s1.length() == 0)
            {
                try
                {
                    java.io.FileOutputStream fileoutputstream = new FileOutputStream(s5);
                    java.io.PrintWriter printwriter = com.dragonflow.Utils.FileUtils.MakeOutputWriter(fileoutputstream);
                    printwriter.print(stringbuffer);
                    printwriter.close();
                    fileoutputstream.close();
                }
                catch(java.lang.Exception exception)
                {
                    s1 = "Error writing alert.txt";
                }
            }
        }
        if(s1.length() == 0)
        {
            String s10 = "";
            String s12 = "";
            boolean flag1 = true;
            if(!com.dragonflow.SiteView.Platform.isRemote(s3))
            {
                java.io.File file = new File(com.dragonflow.SiteView.Platform.getRoot() + "/scripts/" + com.dragonflow.Utils.I18N.toDefaultEncoding(s7));
                s10 = file.getAbsolutePath();
                flag1 = file.exists();
                s12 = com.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "scripts";
            } else
            {
                String s15 = "scripts" + com.dragonflow.SiteView.Machine.getMachinePathSeparator(s3) + s7;
                com.dragonflow.Utils.RemoteFile remotefile = new RemoteFile(s3, s15);
                s10 = remotefile.getFullPath();
                s12 = remotefile.getPwd() + "scripts";
            }
            if(flag1)
            {
                com.dragonflow.Utils.CommandLine commandline = new CommandLine();
                String s16 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(com.dragonflow.SiteView.Monitor.pName));
                String s17 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(com.dragonflow.SiteView.Monitor.pStateString));
                s17 = com.dragonflow.Utils.TextUtils.parseNonPrintableCharacters(s17);
                String s18 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getOwner().getProperty(com.dragonflow.SiteView.Monitor.pName));
                if(com.dragonflow.SiteView.Platform.isWindows())
                {
                    if(com.dragonflow.Utils.TextUtils.hasSpaces(s16))
                    {
                        s16 = "\"" + s16 + "\"";
                    }
                    if(com.dragonflow.Utils.TextUtils.hasSpaces(s18))
                    {
                        s18 = "\"" + s18 + "\"";
                    }
                    s17 = "\"" + s17.replaceAll("\\\"", "\"\"") + "\"";
                }
                int i = com.dragonflow.SiteView.Machine.getOS(s3);
                String s19 = s5;
                if(s6.length() > 0)
                {
                    s19 = s6;
                }
                boolean flag2 = getSetting("_noAlertScriptParameters").length() <= 0;
                if(flag2)
                {
                    com.dragonflow.SiteView.Machine machine = com.dragonflow.SiteView.Machine.getMachine(s3);
                    if(machine != null)
                    {
                        String s20 = machine.getProperty("_noAlertScriptParameters");
                        if(s20.length() > 0)
                        {
                            flag2 = false;
                        }
                    }
                }
                Array array1 = new Array();
                if(com.dragonflow.SiteView.Platform.isWindows(i))
                {
                    array1.add("cmd");
                    array1.add("/c");
                }
                array1.add(s10);
                if(flag2)
                {
                    array1.add(s12);
                    if(com.dragonflow.Utils.TextUtils.hasChars(s16, "`;&|<>"))
                    {
                        com.dragonflow.Log.LogManager.log("Error", "Removed illegal characters from monitor name \"" + s16 + "\" before running script");
                        s16 = com.dragonflow.Utils.TextUtils.removeChars(s16, "`;&|<>");
                    }
                    array1.add(s16);
                    array1.add(s17);
                    array1.add(s19);
                    array1.add(monitor.getProperty(pID));
                    if(com.dragonflow.Utils.TextUtils.hasChars(s18, "`;&|<>"))
                    {
                        com.dragonflow.Log.LogManager.log("Error", "Removed illegal characters from group name \"" + s18 + "\" before running script");
                        s18 = com.dragonflow.Utils.TextUtils.removeChars(s18, "`;&|<>");
                    }
                    array1.add(s18);
                }
                String as[] = new String[array1.size()];
                for(int j = 0; j < array1.size(); j++)
                {
                    as[j] = (String)array1.get(j);
                }

                int k = getPropertyAsInteger(pTimeout) * 1000;
                if(k < 0)
                {
                    k = getSettingAsLong("_scriptMonitorTimeout", -1) * 1000;
                }
                as = appendParameters(as, s9);
                Array array;
                if(k > 0)
                {
                    String s21 = com.dragonflow.Utils.ParameterParser.arrayCmdToStringCmd(as);
                    array = commandline.exec(s21, s3, com.dragonflow.SiteView.Platform.monitorLock, k);
                } else
                {
                    array = commandline.exec(as, s3);
                }
                l = commandline.getExitValue();
                s1 = s7 + " (" + l + ")";
                if(l == 0L)
                {
                    flag = true;
                } else
                if(l == 4000L)
                {
                    monitor.signalMonitor();
                    s1 = s1 + ", re-running monitor";
                }
                for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements();)
                {
                    s2 = s2 + (String)enumeration.nextElement() + com.dragonflow.SiteView.Platform.FILE_NEWLINE;
                }

            } else
            {
                s1 = "file missing: " + s10;
            }
        }
        String s11 = "Script alert performed";
        if(!flag)
        {
            s11 = "SCRIPT ALERT ERROR RESULT";
            String s13 = getSetting("_autoEmail");
            String s14 = "There was a problem performing script alert." + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE + baseAlertLogEntry(s11, s1, flag) + " alert-result: " + l + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-output: " + s2 + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE;
            if(s13.length() != 0)
            {
                com.dragonflow.SiteView.SiteViewGroup siteviewgroup1 = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                siteviewgroup1.simpleMail(s13, "SiteView " + s11, s14);
            }
        }
        messageBuffer.append(s11 + ", " + s1);
        logAlert(baseAlertLogEntry(s11, s1, flag) + " alert-result: " + l + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-output: " + s2 + com.dragonflow.SiteView.Platform.FILE_NEWLINE + com.dragonflow.SiteView.Platform.FILE_NEWLINE);
        com.dragonflow.StandardAction.Run.releaseScriptLock(s);
        }
        catch (RuntimeException exception1) {
        com.dragonflow.StandardAction.Run.releaseScriptLock(s);
        throw exception1;
        }
        return flag;
    }

    static 
    {
        pScript = new ScalarProperty("_script", "");
        pScript.setDisplayText("Script", "the name of the script to be run. Scripts must reside in a directory called \"scripts\" on the server selected above.");
        pScript.setParameterOptions(true, 1, false);
        pTemplate = new ScalarProperty("_template", "Default");
        pTemplate.setDisplayText("Template", "the template used to create the file referenced by the script.");
        pTemplate.setParameterOptions(true, 2, false);
        pParameters = new StringProperty("_parameters", "");
        pParameters.setDisplayText("Parameters", "additional parameters to pass to the script - <a href=/SiteView/docs/Scripts.htm target=help>more info</a>");
        pParameters.setParameterOptions(true, 3, false);
        pTimeout = new NumericProperty("_timeout", "-1", "seconds");
        pTimeout.setDisplayText("Timeout", "The total time, in seconds, to wait for a successful script run. Default value is -1 (no timeout).");
        pTimeout.setParameterOptions(false, 2, true);
        com.dragonflow.Properties.StringProperty astringproperty[] = {
            pScript, pTemplate, pParameters, pTimeout
        };
        com.dragonflow.StandardAction.Run.addProperties("com.dragonflow.StandardAction.Run", astringproperty);
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "description", "Runs a shell script on this machine or a remote machine.");
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "help", "AlertRun.htm");
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "title", "Run Script");
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "label", "Run");
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "name", "Script");
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "class", "Run");
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "prefs", "");
        com.dragonflow.StandardAction.Run.setClassProperty("com.dragonflow.StandardAction.Run", "loadable", "true");
        com.dragonflow.StandardAction.Run.setScriptLock(5);
    }
}
