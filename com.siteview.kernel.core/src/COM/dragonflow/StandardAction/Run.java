/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardAction;

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

import com.recursionsw.jgl.Array;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.RemoteFile;

public class Run extends COM.dragonflow.SiteView.ServerAction
{

    static COM.dragonflow.Properties.StringProperty pScript;
    static COM.dragonflow.Properties.StringProperty pTemplate;
    static COM.dragonflow.Properties.StringProperty pParameters;
    static COM.dragonflow.Properties.StringProperty pTimeout;
    static final String DEFAULT_TIMEOUT = "-1";
    static COM.dragonflow.Utils.CounterLock scriptLock = null;
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
        String s = COM.dragonflow.Utils.I18N.toDefaultEncoding(getProperty(pScript));
        if(s.length() > 0)
        {
            stringbuffer.append(" ");
            stringbuffer.append(s);
        }
        String s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(getProperty(pTemplate));
        String s2 = COM.dragonflow.Utils.I18N.toDefaultEncoding(getProperty(pParameters));
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

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pParameters && COM.dragonflow.Utils.TextUtils.hasChars(s, "`;&|"))
        {
            hashmap.put(stringproperty, "script parameters have illegal characters");
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest, COM.dragonflow.Page.CGI cgi)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        if(scalarproperty == pTemplate)
        {
            java.util.Vector vector = COM.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.script", httprequest);
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
            return COM.dragonflow.StandardMonitor.ScriptMonitor.getScriptList(getProperty(pMachineName), httprequest);
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
            String as2[] = COM.dragonflow.Utils.TextUtils.tokenize(s);
            for(int i = 0; i < as2.length; i++)
            {
                String s1 = as2[i];
                s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(monitor.createFromTemplate(s1));
                if(COM.dragonflow.Utils.TextUtils.hasChars(s1, "`;&|<>"))
                {
                    COM.dragonflow.Log.LogManager.log("Error", "Removed illegal characters from script parameter \"" + s1 + "\"");
                    s1 = COM.dragonflow.Utils.TextUtils.removeChars(s1, "`;&|<>");
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
                if(COM.dragonflow.SiteView.Platform.isWindows() && COM.dragonflow.Utils.TextUtils.hasSpaces(s1))
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
        s = COM.dragonflow.StandardAction.Run.getScriptLock();
        
        try {
        String s1 = "";
        long l = -1L;
        String s2 = "";
        String s3 = getMachineIDFromArgs();
        String s4 = "alert" + s + ".txt";
        String s5 = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "scripts" + java.io.File.separator + s4;
        String s6 = "";
        if(COM.dragonflow.SiteView.Platform.isRemote(s3))
        {
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
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
                    java.io.PrintWriter printwriter = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(fileoutputstream);
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
            if(!COM.dragonflow.SiteView.Platform.isRemote(s3))
            {
                java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/scripts/" + COM.dragonflow.Utils.I18N.toDefaultEncoding(s7));
                s10 = file.getAbsolutePath();
                flag1 = file.exists();
                s12 = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "scripts";
            } else
            {
                String s15 = "scripts" + COM.dragonflow.SiteView.Machine.getMachinePathSeparator(s3) + s7;
                COM.dragonflow.Utils.RemoteFile remotefile = new RemoteFile(s3, s15);
                s10 = remotefile.getFullPath();
                s12 = remotefile.getPwd() + "scripts";
            }
            if(flag1)
            {
                COM.dragonflow.Utils.CommandLine commandline = new CommandLine();
                String s16 = COM.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName));
                String s17 = COM.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pStateString));
                s17 = COM.dragonflow.Utils.TextUtils.parseNonPrintableCharacters(s17);
                String s18 = COM.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getOwner().getProperty(COM.dragonflow.SiteView.Monitor.pName));
                if(COM.dragonflow.SiteView.Platform.isWindows())
                {
                    if(COM.dragonflow.Utils.TextUtils.hasSpaces(s16))
                    {
                        s16 = "\"" + s16 + "\"";
                    }
                    if(COM.dragonflow.Utils.TextUtils.hasSpaces(s18))
                    {
                        s18 = "\"" + s18 + "\"";
                    }
                    s17 = "\"" + s17.replaceAll("\\\"", "\"\"") + "\"";
                }
                int i = COM.dragonflow.SiteView.Machine.getOS(s3);
                String s19 = s5;
                if(s6.length() > 0)
                {
                    s19 = s6;
                }
                boolean flag2 = getSetting("_noAlertScriptParameters").length() <= 0;
                if(flag2)
                {
                    COM.dragonflow.SiteView.Machine machine = COM.dragonflow.SiteView.Machine.getMachine(s3);
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
                if(COM.dragonflow.SiteView.Platform.isWindows(i))
                {
                    array1.add("cmd");
                    array1.add("/c");
                }
                array1.add(s10);
                if(flag2)
                {
                    array1.add(s12);
                    if(COM.dragonflow.Utils.TextUtils.hasChars(s16, "`;&|<>"))
                    {
                        COM.dragonflow.Log.LogManager.log("Error", "Removed illegal characters from monitor name \"" + s16 + "\" before running script");
                        s16 = COM.dragonflow.Utils.TextUtils.removeChars(s16, "`;&|<>");
                    }
                    array1.add(s16);
                    array1.add(s17);
                    array1.add(s19);
                    array1.add(monitor.getProperty(pID));
                    if(COM.dragonflow.Utils.TextUtils.hasChars(s18, "`;&|<>"))
                    {
                        COM.dragonflow.Log.LogManager.log("Error", "Removed illegal characters from group name \"" + s18 + "\" before running script");
                        s18 = COM.dragonflow.Utils.TextUtils.removeChars(s18, "`;&|<>");
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
                    String s21 = COM.dragonflow.Utils.ParameterParser.arrayCmdToStringCmd(as);
                    array = commandline.exec(s21, s3, COM.dragonflow.SiteView.Platform.monitorLock, k);
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
                    s2 = s2 + (String)enumeration.nextElement() + COM.dragonflow.SiteView.Platform.FILE_NEWLINE;
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
            String s14 = "There was a problem performing script alert." + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + baseAlertLogEntry(s11, s1, flag) + " alert-result: " + l + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-output: " + s2 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE;
            if(s13.length() != 0)
            {
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup1 = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                siteviewgroup1.simpleMail(s13, "SiteView " + s11, s14);
            }
        }
        messageBuffer.append(s11 + ", " + s1);
        logAlert(baseAlertLogEntry(s11, s1, flag) + " alert-result: " + l + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-output: " + s2 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE);
        COM.dragonflow.StandardAction.Run.releaseScriptLock(s);
        }
        catch (RuntimeException exception1) {
        COM.dragonflow.StandardAction.Run.releaseScriptLock(s);
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
        COM.dragonflow.Properties.StringProperty astringproperty[] = {
            pScript, pTemplate, pParameters, pTimeout
        };
        COM.dragonflow.StandardAction.Run.addProperties("COM.dragonflow.StandardAction.Run", astringproperty);
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "description", "Runs a shell script on this machine or a remote machine.");
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "help", "AlertRun.htm");
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "title", "Run Script");
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "label", "Run");
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "name", "Script");
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "class", "Run");
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "prefs", "");
        COM.dragonflow.StandardAction.Run.setClassProperty("COM.dragonflow.StandardAction.Run", "loadable", "true");
        COM.dragonflow.StandardAction.Run.setScriptLock(5);
    }
}
