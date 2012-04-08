/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * NTDialupMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>NTDialupMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PercentProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.CounterLock;
import COM.dragonflow.Utils.TextUtils;

public class NTDialupMonitor extends AtomicMonitor
{

    static StringProperty pPhone;
    static StringProperty pUsername;
    static StringProperty pPassword;
    static StringProperty pMonitors;
    static StringProperty pTimeout;
    static StringProperty pTime;
    static StringProperty pStatus;
    static StringProperty pConnectTime;
    static StringProperty pAuthorizationTime;
    static StringProperty pMonitorCount;
    static StringProperty pMonitorErrorCount;
    static StringProperty pMonitorWarningCount;
    static StringProperty pPercentGood;
    static StringProperty pConnectSpeed;
    public static CounterLock dialupLock = new CounterLock(1);

    public NTDialupMonitor()
    {
    }

    public String getHostname()
    {
        return "NTDailUpHost";
    }

    protected boolean update()
    {
        synchronized(dialupLock)
        {
            String s = Platform.getRoot() + "/tools/dialup.exe";
            String s1 = Platform.getRoot() + "/tools/dialup.exe";
            long l = getPropertyAsLong(pTimeout);
            long l1 = Platform.timeMillis();
            boolean flag = false;
            String s2 = getSetting("_dialupOptions");
            if(s2.length() > 0)
            {
                s = s + " " + s2;
                s1 = s1 + " " + s2;
                if(s2.indexOf("-debug") >= 0)
                {
                    flag = true;
                }
            }
            s = s + " -d " + getProperty(pPhone);
            s = s + " -u " + getProperty(pUsername);
            s = s + " -p " + getProperty(pPassword);
            s = s + " -t " + l;
            s1 = s1 + " -hangup";
            LogManager.log("RunMonitor", getProperty(pName) + " dialing " + getProperty(pPhone));
            CommandLine commandline = new CommandLine();
            Array array = commandline.exec(s1, dialupLock);
            if(flag)
            {
                for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); LogManager.log("RunMonitor", "HANGUP: " + enumeration.nextElement())) { }
            }
            progressString += "Dialing " + getProperty(pPhone) + "\n";
            setProperty(pStateString, "dialing...");
            commandline = new CommandLine();
            array = commandline.exec(s, dialupLock);
            String s3 = "no data";
            String s4 = "status: ";
            String s5 = "connect: ";
            long l2 = 0L;
            String s6 = "authorization: ";
            long l3 = 0L;
            Enumeration enumeration1 = array.elements();
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                String s7 = (String)enumeration1.nextElement();
                if(flag)
                {
                    LogManager.log("RunMonitor", "DIAL: " + s7);
                }
                if(s7.startsWith(s4))
                {
                    s3 = s7.substring(s4.length()).trim();
                } else
                if(s7.startsWith(s5))
                {
                    l2 = TextUtils.toLong(s7.substring(s5.length()));
                } else
                if(s7.startsWith(s6))
                {
                    l3 = TextUtils.toLong(s7.substring(s6.length()));
                }
            } while(true);
            int i = 0;
            int j = 0;
            int k = 0;
            String s8 = "";
            long l4 = 0L;
            boolean flag1 = false;
            if(s3.equals("ok"))
            {
                progressString += "Connected\n";
                setProperty(pStateString, "connected...");
                HashMap hashmap = MasterConfig.getMasterConfig();
                try
                {
                    InetAddress inetaddress = InetAddress.getLocalHost();
                    LogManager.log("RunMonitor", "local ip address: " + inetaddress.getHostAddress());
                    if(inetaddress.getHostAddress().indexOf("127.0.0") == -1)
                    {
                        hashmap.put("_webserverAddress", inetaddress.getHostAddress());
                    } else
                    {
                        hashmap.put("_webserverAddress", Platform.getLocalIPAddress().at(0));
                    }
                    MasterConfig.saveMasterConfig(hashmap);
                    SiteViewGroup.updateStaticPages();
                    LogManager.log("RunMonitor", "webserveraddress:  " + getSetting("_webserverAddress"));
                }
                catch(UnknownHostException unknownhostexception)
                {
                    System.out.println("can't get local host");
                    LogManager.log("Error", "can't get local host" + unknownhostexception.toString());
                }
                Array array2 = getMonitorsToRun();
                long l6 = getSettingAsLong("_NTDialupMonitorStartupTime", 500);
                long l7 = getSettingAsLong("_NTDialupMonitorDelay", 500);
                Enumeration enumeration2 = array2.elements();
                try
                {
                    do
                    {
                        if(!enumeration2.hasMoreElements())
                        {
                            break;
                        }
                        AtomicMonitor atomicmonitor = (AtomicMonitor)enumeration2.nextElement();
                        if(Platform.timeMillis() - l1 > getPropertyAsLong(pTimeout) * 1000L)
                        {
                            flag1 = true;
                            break;
                        }
                        boolean flag2 = atomicmonitor.runUpdate(true);
                        if(flag2)
                        {
                            progressString += "Running monitor " + atomicmonitor.getProperty(pName) + "\n";
                            i++;
                            setProperty(pStateString, "checking " + atomicmonitor.getProperty(pName) + "...");
                            try
                            {
                                Thread.sleep(l6);
                            }
                            catch(InterruptedException interruptedexception) { }
                            do
                            {
                                if(atomicmonitor.getProperty(pRunning).length() <= 0)
                                {
                                    break;
                                }
                                if(Platform.timeMillis() - l1 > getPropertyAsLong(pTimeout) * 1000L)
                                {
                                    flag1 = true;
                                    s8 = atomicmonitor.getProperty(pName);
                                    break;
                                }
                                try
                                {
                                    Thread.sleep(l7);
                                }
                                catch(InterruptedException interruptedexception1) { }
                            } while(true);
                            if(atomicmonitor.getProperty(pCategory).equals(ERROR_CATEGORY))
                            {
                                j++;
                            } else
                            if(atomicmonitor.getProperty(pCategory).equals(WARNING_CATEGORY))
                            {
                                k++;
                            }
                        }
                    } while(true);
                }
                finally
                {
                    LogManager.log("RunMonitor", getProperty(pName) + " hanging up");
                    progressString += "Hanging up\n";
                    setProperty(pStateString, "hanging up...");
                    long l8 = Platform.timeMillis();
                    CommandLine commandline1 = new CommandLine();
                    Array array1 = commandline1.exec(s1, dialupLock);
                    if(flag)
                    {
                        for(Enumeration enumeration3 = array1.elements(); enumeration3.hasMoreElements(); LogManager.log("RunMonitor", "HANGUP: " + enumeration3.nextElement())) { }
                    }
                    l4 = Platform.timeMillis() - l8;
                }
            } else
            {
                s3 = "unable to connect - " + s3;
            }
            long l5 = Platform.timeMillis() - l1 - l4;
            if(stillActive())
            {
                synchronized(this)
                {
                    if(flag1)
                    {
                        s3 = "timed out after " + TextUtils.floatToString((float)l5 / 1000F, 2) + " sec";
                        if(s8.length() > 0)
                        {
                            s3 = s3 + " running monitor " + s8;
                        }
                    }
                    setProperty(pStatus, s3);
                    if(s3.equals("ok"))
                    {
                        int i1 = i - (j + k);
                        setProperty(pPercentGood, 100);
                        setProperty(pMonitorCount, i);
                        setProperty(pMonitorErrorCount, j);
                        setProperty(pMonitorWarningCount, k);
                        if(i > 0)
                        {
                            setProperty(pPercentGood, TextUtils.floatToString(((float)i1 / (float)i) * 100F, 0));
                        }
                        setProperty(pTime, l5);
                        setProperty(pConnectTime, l2);
                        setProperty(pAuthorizationTime, l3);
                        if(i > 0)
                        {
                            setProperty(pStateString, "" + i1 + " of " + i + " monitors ok in " + l5 / 1000L + " sec");
                        } else
                        {
                            setProperty(pStateString, "ok in " + l5 / 1000L + " sec");
                        }
                    } else
                    {
                        setProperty(pPercentGood, 0);
                        setProperty(pMonitorCount, "n/a");
                        setProperty(pMonitorErrorCount, "n/a");
                        setProperty(pMonitorWarningCount, "n/a");
                        setProperty(pTime, "n/a");
                        setProperty(pConnectTime, "n/a");
                        setProperty(pAuthorizationTime, "n/a");
                        setProperty(pNoData, "n/a");
                        setProperty(pStateString, s3);
                    }
                }
            }
        }
        return true;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pTime);
        array.add(pPercentGood);
        array.add(pMonitorCount);
        array.add(pMonitorErrorCount);
        array.add(pMonitorWarningCount);
        array.add(pConnectTime);
        array.add(pAuthorizationTime);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pPhone)
        {
            if(!Platform.hasLibrary("rasapi32.dll"))
            {
                hashmap.put(stringproperty, "Remote Access Service is required but not installed.  Add this service under the Network Control Panel.");
            } else
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, "phone number is required");
            }
        } else
        if(stringproperty == pTimeout)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, "timeout is required");
            } else
            if(!TextUtils.onlyChars(s, "0123456789"))
            {
                hashmap.put(stringproperty, "timeout must be a number");
            } else
            if(TextUtils.toInt(s) < 10)
            {
                hashmap.put(stringproperty, "timeout must be greater than 10 seconds");
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pMonitors)
        {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            Array array = CGI.getAllowedGroupIDsForAccount(httprequest);
            Enumeration enumeration = array.elements();
            Vector vector = new Vector();
            Vector vector1 = new Vector();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElement((String)enumeration.nextElement());
                if(monitorgroup != null)
                {
                    vector.addElement(monitorgroup.getProperty(pGroupID));
                    vector.addElement(monitorgroup.getProperty(pName));
                    Enumeration enumeration2 = monitorgroup.getMonitors();
                    while(enumeration2.hasMoreElements()) 
                    {
                        Monitor monitor = (Monitor)enumeration2.nextElement();
                        vector1.addElement(monitor.getProperty(pGroupID) + " " + monitor.getProperty(pID));
                        vector1.addElement(monitorgroup.getProperty(pName) + ": " + monitor.getProperty(pName));
                    }
                }
            } while(true);
            for(Enumeration enumeration1 = vector1.elements(); enumeration1.hasMoreElements(); vector.addElement(enumeration1.nextElement())) { }
            return vector;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    void addToMonitorList(Monitor monitor, Array array, HashMap hashmap)
    {
        if(monitor instanceof NTDialupMonitor)
        {
            return;
        }
        String s = monitor.getProperty(pGroupID) + " " + monitor.getProperty(pID);
        if(hashmap.get(s) == null)
        {
            hashmap.put(s, monitor);
            array.add(monitor);
        }
    }

    Array getMonitorsToRun()
    {
        HashMap hashmap = new HashMap();
        Array array = new Array();
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Enumeration enumeration = getMultipleValues(pMonitors);
label0:
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s = (String)enumeration.nextElement();
            String as[] = TextUtils.split(s);
            Object obj = null;
            Object obj1 = null;
            if(as.length == 1)
            {
                MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElement(as[0]);
                if(monitorgroup == null)
                {
                    continue;
                }
                Enumeration enumeration1 = monitorgroup.getMonitors();
                do
                {
                    Monitor monitor;
                    do
                    {
                        if(!enumeration1.hasMoreElements())
                        {
                            continue label0;
                        }
                        monitor = (Monitor)enumeration1.nextElement();
                    } while(!(monitor instanceof AtomicMonitor));
                    addToMonitorList(monitor, array, hashmap);
                } while(true);
            }
            if(as.length > 1)
            {
                String s1 = as[0] + SiteViewGroup.ID_SEPARATOR + as[1];
                MonitorGroup monitorgroup1 = (MonitorGroup)siteviewgroup.getElement(as[0]);
                if(monitorgroup1 != null)
                {
                    Monitor monitor1 = (Monitor)siteviewgroup.getElement(s1);
                    if(monitor1 != null)
                    {
                        addToMonitorList(monitor1, array, hashmap);
                    }
                }
            }
        } while(true);
        return array;
    }

    public boolean runExclusively()
    {
        return true;
    }

    static 
    {
        pPhone = new StringProperty("_phone");
        pPhone.setDisplayText("Phone number", "the phone number to dial into");
        pPhone.setParameterOptions(true, 1, false);
        pUsername = new StringProperty("_username");
        pUsername.setDisplayText("Account Login", "the login for the dial-up account");
        pUsername.setParameterOptions(true, 2, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Account Password", "the password for the dial-up account");
        pPassword.setParameterOptions(true, 3, false);
        pPassword.isPassword = true;
        pMonitors = new ScalarProperty("_monitors");
        pMonitors.setDisplayText("Monitor(s) to run", "the monitor(s) to run while dialed up - multiple monitors or groups can be selected");
        pMonitors.setParameterOptions(true, 4, false);
        ((ScalarProperty)pMonitors).multiple = true;
        ((ScalarProperty)pMonitors).listSize = 5;
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the maximum time, in seconds, to wait for all of the monitors to complete");
        pTimeout.setParameterOptions(true, 5, true);
        pTime = new NumericProperty("time", "0", "milliseconds");
        pTime.setLabel("total time");
        pTime.setStateOptions(1);
        pPercentGood = new PercentProperty("percentGood");
        pPercentGood.setLabel("% monitors good");
        pPercentGood.setStateOptions(2);
        pStatus = new StringProperty("status");
        pConnectTime = new NumericProperty("connectTime", "0", "milliseconds");
        pConnectTime.setLabel("time to connect");
        pConnectTime.setStateOptions(3);
        pAuthorizationTime = new NumericProperty("authorizationTime", "0", "milliseconds");
        pAuthorizationTime.setLabel("time to authorize");
        pAuthorizationTime.setStateOptions(4);
        pMonitorCount = new NumericProperty("monitorCount", "0");
        pMonitorErrorCount = new NumericProperty("monitorErrorCount", "0");
        pMonitorWarningCount = new NumericProperty("monitorWarningCount", "0");
        StringProperty astringproperty[] = {
            pPhone, pUsername, pPassword, pMonitors, pTimeout, pTime, pStatus, pConnectTime, pAuthorizationTime, pPercentGood, 
            pMonitorCount, pMonitorErrorCount, pMonitorWarningCount
        };
        addProperties("COM.dragonflow.StandardMonitor.NTDialupMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.NTDialupMonitor", Rule.stringToClassifier("status != 'ok'\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.NTDialupMonitor", Rule.stringToClassifier("percentGood != 100\twarning", true));
        addClassElement("COM.dragonflow.StandardMonitor.NTDialupMonitor", Rule.stringToClassifier("always\tgood", true));
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "description", "Dials up a remote server via Remote Access, and runs a set of monitors.");
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "help", "NTDialupMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "title", "Windows Dial-up");
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "class", "NTDialupMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "target", "_phone");
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "classType", "advanced");
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "topazName", "NT Dialup");
        setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "topazType", "Application Breakdown");
        if(!Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.NTDialupMonitor", "loadable", "false");
        }
    }
}
