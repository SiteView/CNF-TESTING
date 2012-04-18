/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * ServiceMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>ServiceMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.Machine;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.ServerMonitor;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.TextUtils;

public class ServiceMonitor extends ServerMonitor
{

    static ScalarProperty pServiceName;
    static ScalarProperty pUnixServiceName;
    static StringProperty pProcessName;
    static BooleanProperty pCheckMemory;
    static StringProperty pStatus;
    static StringProperty pProcessCPU;
    static StringProperty pProcessMemory;
    static StringProperty pUnixProcessMemory;
    static StringProperty pProcessCount;
    static StringProperty pLastMeasurement;
    static StringProperty pLastMeasurementTime;
    static final String userProcessName = "(using Process Name)";

    public ServiceMonitor()
    {
    }

    String processError(long l)
    {
        if(!isKnownStatus(l))
        {
            return "not found (" + l + ")";
        } else
        {
            return "not found (remote monitoring error - " + lookupStatus(l) + ")";
        }
    }

    public String getTestURL()
    {
        String s = URLEncoder.encode(getProperty(pMachineName));
        String s1 = "/SiteView/cgi/go.exe/SiteView?page=service&machine=" + s;
        return s1;
    }

    StringProperty getProcessMemoryProperty()
    {
        return Platform.isWindows(getPlatform()) ? pProcessMemory : pUnixProcessMemory;
    }

    protected boolean update()
    {
        String s = Platform.isWindows(getPlatform()) ? getProperty(pServiceName) : getProperty(pUnixServiceName);
        String s1 = getProperty(pProcessName);
        String s2 = getProperty(pMachineName);
        long l = 0L;
        long l1 = -1L;
        long l2 = -1L;
        long l3 = 1L;
        Array array = null;
        if(monitorDebugLevel == 3)
        {
            array = new Array();
        }
        if(Platform.isWindows(getPlatform()) && s1.length() > 0)
        {
            long l4 = (new Long(getProperty(pLastMeasurement))).longValue();
            long l6 = (new Long(getProperty(pLastMeasurementTime))).longValue();
            long al1[] = Platform.processUsed(s2, s1, l6, l4, this, array);
            l1 = al1[0];
            l = al1[1];
            long l7 = al1[2];
            long l8 = al1[3];
            l2 = al1[4];
            setProperty(pLastMeasurementTime, String.valueOf(l7));
            setProperty(pLastMeasurement, String.valueOf(l8));
        } else
        {
            long l5 = 0L;
            if(getPropertyAsBoolean(pCheckMemory))
            {
                String s4 = "";
                if(Machine.getCommandString("serviceMonitor", s2).length() > 0)
                {
                    s4 = Machine.getCommandSetting(s2, "serviceMonitor", "pageSize");
                } else
                {
                    s4 = Machine.getCommandSetting(s2, "processDetail", "pageSize");
                }
                if(s4.length() > 0)
                {
                    if(s4.equals("compute"))
                    {
                        l5 = -2L;
                    } else
                    {
                        l5 = TextUtils.toLong(s4);
                    }
                }
                if(l5 == 0L)
                {
                    l5 = getSettingAsLong("_memoryPageSize");
                }
            }
            long al[] = Platform.checkProcess(s, s2, l5, true, this, array);
            l = al[0];
            l3 = al[1];
            l2 = al[2];
        }
        if(stillActive())
        {
            synchronized(this)
            {
                if(Platform.isWindows(getPlatform()) && s1.length() > 0 || getPropertyAsBoolean(pCheckMemory))
                {
                    if(l1 == -1L)
                    {
                        setProperty(pProcessCPU, "n/a");
                        setProperty(pNoData, "n/a");
                        setProperty(pMeasurement, 0);
                    } else
                    {
                        setProperty(pProcessCPU, l1);
                        setProperty(pMeasurement, getMeasurement(pProcessCPU));
                    }
                    if(l2 == -1L)
                    {
                        setProperty(getProcessMemoryProperty(), "n/a");
                        setProperty(pNoData, "n/a");
                    } else
                    {
                        setProperty(getProcessMemoryProperty(), l2);
                    }
                }
                setProperty(pProcessCount, l);
                String s3;
                if(l > 0L)
                {
                    s3 = "running";
                } else
                {
                    if(l3 == 1L)
                    {
                        s3 = "not found";
                    } else
                    {
                        s3 = processError(l3);
                    }
                    if(monitorDebugLevel == 3 && array != null)
                    {
                        StringBuffer stringbuffer = new StringBuffer();
                        for(int i = 0; i < array.size(); i++)
                        {
                            stringbuffer.append(array.get(i) + "\n");
                        }

                        LogManager.log("Error", "ServiceMonitor: " + getFullID() + ", returned: " + s3 + ", output:\n" + stringbuffer);
                    }
                }
                setProperty(pStatus, s3);
                if(l > 1L)
                {
                    s3 = s3 + ", " + l + " processes";
                }
                if(l1 != -1L)
                {
                    s3 = s3 + ", " + l1 + "% cpu";
                }
                if(l2 != -1L)
                {
                    s3 = s3 + ", " + TextUtils.floatToString((float)l2 / 1048576F, 2) + "MB memory";
                }
                setProperty(pStateString, s3);
            }
        }
        return true;
    }

    public Enumeration getStatePropertyObjects(boolean flag)
    {
        Enumeration enumeration = super.getStatePropertyObjects(flag);
        Array array = new Array();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            StringProperty stringproperty = (StringProperty)enumeration.nextElement();
            if(stringproperty == pProcessCPU)
            {
                if(getProperty(pProcessName).length() > 0)
                {
                    array.add(stringproperty);
                }
            } else
            if(stringproperty == pProcessMemory || stringproperty == pUnixProcessMemory)
            {
                if(getProperty(pProcessName).length() > 0)
                {
                    array.add(stringproperty);
                } else
                if(getPropertyAsBoolean(pCheckMemory))
                {
                    array.add(stringproperty);
                }
            } else
            {
                array.add(stringproperty);
            }
        } while(true);
        return (Enumeration) array.iterator();
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pProcessCount);
        array.add(pProcessCPU);
        array.add(getProcessMemoryProperty());
        return array;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty, HTTPRequest httprequest, CGI cgi)
        throws SiteViewException
    {
        if(scalarproperty == pServiceName || scalarproperty == pUnixServiceName)
        {
            Array array = Platform.getProcesses(Machine.getFullMachineID(getProperty(pMachineName), httprequest), true);
            Enumeration enumeration = (Enumeration) array.iterator();
            Vector vector = new Vector();
            String s;
            for(; enumeration.hasMoreElements(); vector.addElement(s))
            {
                s = (String)enumeration.nextElement();
                vector.addElement(s);
            }

            if(Platform.isWindows())
            {
                vector.addElement("(using Process Name)");
                vector.addElement("(using Process Name)");
            }
            return vector;
        } else
        {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public String defaultTitle()
    {
        String s = super.defaultTitle();
        if(Platform.isWindows())
        {
            String s1 = getProperty(pProcessName);
            if(s1.length() > 0)
            {
                s = "Process: " + s1;
            }
        }
        return s;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(Platform.isWindows(getPlatform()) && stringproperty == pProcessName)
        {
            if(s.length() > 0)
            {
                setProperty(pServiceName, "(using Process Name)");
                String s1 = s.toLowerCase();
                if(s1.endsWith(".exe"))
                {
                    s = s.substring(0, s.length() - 4);
                }
            } else
            if(getProperty(pServiceName).equals("(using Process Name)"))
            {
                setProperty(pServiceName, "none");
            }
            return s;
        }
        if(stringproperty == pCheckMemory)
        {
            if(s.length() != 0)
            {
                String s2 = getProperty(pMachineName);
                if(Machine.getCommandString("serviceMonitor", s2).length() > 0)
                {
                    return s;
                }
                String s3 = getProperty(pUnixServiceName);
                int i = s3.indexOf(' ');
                if(i != -1)
                {
                    s3 = s3.substring(0, i);
                }
                int j = s3.lastIndexOf('/');
                if(j != -1)
                {
                    s3 = s3.substring(j + 1);
                }
                if(Machine.getOSName(s2).indexOf("HP") >= 0 && s3.length() > 14)
                {
                    s3 = s3.substring(0, 14);
                }
                setProperty(pUnixServiceName, s3);
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    static 
    {
        pServiceName = new ScalarProperty("_service", true);
        pServiceName.setWindowsPlatforms();
        pServiceName.setDisplayText("Service", "the NT service to monitor");
        pServiceName.setParameterOptions(true, 1, false);
        pServiceName.allowOther = true;
        pUnixServiceName = new ScalarProperty("_service", true);
        pUnixServiceName.setUnixPlatforms();
        pUnixServiceName.setDisplayText("Process", "the process to monitor");
        pUnixServiceName.setParameterOptions(true, 1, false);
        pUnixServiceName.allowOther = true;
        pProcessName = new StringProperty("_process");
        pProcessName.setWindowsPlatforms();
        pProcessName.setParameterOptions(true, 2, true);
        pProcessName.setDisplayText("Process Name", "optional process name for process count and cpu usage (example: httpd); use a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a>");
        pCheckMemory = new BooleanProperty("_checkMemory", "");
        pCheckMemory.setUnixPlatforms();
        pCheckMemory.setDisplayText("Measure Process Memory Use", "when selected, measure amount of virtual memory used by process");
        pCheckMemory.setParameterOptions(true, 6, true);
        pStatus = new StringProperty("status", "no data");
        pStatus.setStateOptions(1);
        pStatus.setIsThreshold(true);
        pProcessCPU = new NumericProperty("processCPU", "0", "%");
        pProcessCPU.setWindowsPlatforms();
        pProcessCPU.setLabel("cpu");
        pProcessCPU.setStateOptions(2);
        pProcessMemory = new NumericProperty("processMemory", "0", "bytes");
        pProcessMemory.setWindowsPlatforms();
        pProcessMemory.setLabel("memory");
        pProcessMemory.setStateOptions(3);
        pUnixProcessMemory = new NumericProperty("processMemory", "0", "bytes");
        pUnixProcessMemory.setUnixPlatforms();
        pUnixProcessMemory.setLabel("memory");
        pUnixProcessMemory.setIsThreshold(true);
        pUnixProcessMemory.setStateOptions(3);
        pProcessCount = new NumericProperty("processCount", "0", "processes");
        pProcessCount.setLabel("processes");
        pProcessCount.setIsThreshold(true);
        pProcessCount.setStateOptions(4);
        pLastMeasurement = new NumericProperty("lastMeasurement");
        pLastMeasurementTime = new NumericProperty("lastMeasurementTime");
        StringProperty astringproperty[] = {
            pServiceName, pUnixServiceName, pProcessName, pCheckMemory, pStatus, pProcessCPU, pProcessMemory, pUnixProcessMemory, pProcessCount, pLastMeasurementTime, 
            pLastMeasurement
        };
        addProperties("COM.dragonflow.StandardMonitor.ServiceMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.ServiceMonitor", Rule.stringToClassifier("status != 'running'\terror", true));
        addClassElement("COM.dragonflow.StandardMonitor.ServiceMonitor", Rule.stringToClassifier("status == 'running'\tgood", true));
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "description", "Determines whether a process is running.");
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "help", "ServMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "title", "Service");
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "class", "ServiceMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "target", "_service");
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "classType", "server");
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "topazName", "Service");
        setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "topazType", "System Resources");
        if(Platform.isWindows())
        {
            setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "toolName", "Services");
            setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "toolDescription", "Shows a list of currently running Services.");
        } else
        {
            setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "toolName", "Processes");
            setClassProperty("COM.dragonflow.StandardMonitor.ServiceMonitor", "toolDescription", "Shows a list of currently running Processes.");
        }
    }
}
