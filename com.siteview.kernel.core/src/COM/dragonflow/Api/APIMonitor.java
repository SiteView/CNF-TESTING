/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Api;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jgl.Array;
import jgl.HashMap;

import org.xml.sax.InputSource;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteView.ApplicationBase;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.BrowsableCache;
import COM.dragonflow.SiteView.BrowsableMonitor;
import COM.dragonflow.SiteView.BrowsableSNMPBase;
import COM.dragonflow.SiteView.ConfigurationChanger;
import COM.dragonflow.SiteView.DetectConfigurationChange;
import COM.dragonflow.SiteView.ISelectableCounter;
import COM.dragonflow.SiteView.IServerPropMonitor;
import COM.dragonflow.SiteView.Machine;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.NTCounterBase;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteView.ScheduleManager;
import COM.dragonflow.SiteView.ServerMonitor;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteView.User;
import COM.dragonflow.SiteView.monitorUtils;
import COM.dragonflow.SiteViewException.SiteViewAvailabilityException;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;
import COM.dragonflow.Utils.HTMLTagParser;

// Referenced classes of package COM.dragonflow.Api:
// APISiteView, SSInstanceProperty, SSStringReturnValue, SSMonitorInstance,
// SSPropertyDetails, Alert

public class APIMonitor extends APISiteView
{
    private class OrderComparitor
        implements java.util.Comparator
    {

        public int compare(java.lang.Object obj, java.lang.Object obj1)
        {
            return ((StringProperty)obj).getOrder() - ((StringProperty)obj1).getOrder();
        }

        public boolean equals(java.lang.Object obj)
        {
            return obj instanceof OrderComparitor;
        }

        private OrderComparitor()
        {
            super();
        }

    }

    class Worker
        implements java.lang.Runnable
    {

        AtomicMonitor mon;
        boolean timedOut;
        java.lang.Object mutex;

        public void run()
        {
            mon.run();
            timedOut = false;
            synchronized(mutex)
            {
                mutex.notify();
            }
        }

        public Worker(AtomicMonitor atomicmonitor, java.lang.Object obj)
        {
            super();
            timedOut = true;
            mon = atomicmonitor;
            mutex = obj;
        }
    }


    public static final String[] TARGET_TAGS = {
        "/A", "INPUT", "/FORM", "/OPTION", "/SELECT", "/TEXTAREA", "FRAME", "BASE", "AREA", "META", 
        "IFRAME"
    };
    public static final String[] FORM_INPUT_TAGS = {
        "INPUT", "SELECT"
    };
    public static final int displayMax = 80;
    public static final int formNameMax = 30;
    public static final String openBrace = "{";
    public static final String closeBrace = "}";
    public static final String formType = "form";
    public static final String frameType = "frame";
    public static final String thisPostData = "_postData";
    private static String OP_ADD = "add";
    private static String OP_TEMP = "temp";
    private static String OP_EDIT = "edit";
    public static final String PORT_OTHER = "portOther";
    public static final String MAX_SEARCH_DEPTH_OTHER = "_maxSearchDepthOther";
    public static final String OID_OTHER = "oidOther";
    public static final String PERCENTAGEBASE_OTHER = "percentageBaseOther";
    public static final String SCALE_OTHER = "scaleOther";
    public static final String SERVICE_OTHER = "serviceOther";
    public static final String TOPAZ_LOGGING = "topazLogging";
    public static final String TOPAZ_NOT_LOG_TO_TOPAZ = "_notLogToTopaz";
    public static final String TOPAZ_LOG_EVERYTHING = "logEverything";
    public static final String SSPARAM_PORT = "_port";
    public static final String SSPARAM_MAXSEARCHDEPTH = "_maxSearchDepth";
    public static final String SSPARAM_OID = "_oid";
    public static final String SSPARAM_PERCENTAGEBASE = "_percentageBase";
    public static final String SSPARAM_SCALE = "_scale";
    public static final String SSPARAM_SERVICE = "_service";
    public static final String SSPARAM_COUNTERS = "_counters";
    public static final String SSPARAM_BROWSE = "_browse";
    public static final String SSPARAM_DESCRIPTION = "_description";
    public static final String SSPARAM_MONITOR_DESCRIPTION = "_monitorDescription";
    public static final String SSPARAM_MACHINE = "_machine";
    public static final String SSPARAM_POSTDATA_PREFIX = "_postData";
    public static final String SSPARAM_REFERENCE_PREFIX = "_reference";
    private static StringProperty pMonDisable = new StringProperty("monitorsDisable");
    private static StringProperty pMonDisableTime = new StringProperty("disableMonitorsTime");
    private static StringProperty pMonDisableDesc = new StringProperty("monitorDisableDescription");
    private static StringProperty pMonDisableStartDate = new StringProperty("disableMonitorsStartTimeDate");
    private static StringProperty pMonDisableStartTime = new StringProperty("disableMonitorsStartTimeTime");
    private static StringProperty pMonDisableEndDate = new StringProperty("disableMonitorsEndTimeDate");
    private static StringProperty pMonDisableEndTime = new StringProperty("disableMonitorsEndTimeTime");
    private static StringProperty pAlertDisable = new StringProperty("groupAlertsDisable");
    private static StringProperty pAlertDisableTime = new StringProperty("disableGroupAlertsTime");
    private static StringProperty pAlertDisableDesc = new StringProperty("alertDisableDescription");
    private static StringProperty pAlertDisableStartDate = new StringProperty("disableGroupAlertsStartTimeDate");
    private static StringProperty pAlertDisableStartTime = new StringProperty("disableGroupAlertsStartTimeTime");
    private static StringProperty pAlertDisableEndDate = new StringProperty("disableGroupAlertsEndTimeDate");
    private static StringProperty pAlertDisableEndTime = new StringProperty("disableGroupAlertsEndTimeTime");
    static final boolean $assertionsDisabled; /* synthetic field */

    public APIMonitor()
    {
    }

    public SSStringReturnValue create(String s, String s1, SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        String s2 = "";
        try
        {
            jgl.HashMap hashmap = new HashMap(true);
            String s3 = "";
            boolean flag = false;
            boolean flag1 = false;
            String s5 = null;
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                boolean flag2 = false;
                if(assinstanceproperty[i].getName().equals("portOther"))
                {
                    flag = true;
                    s5 = "_port";
                } else
                if(assinstanceproperty[i].getName().equals("oidOther"))
                {
                    flag = true;
                    s5 = "_oid";
                } else
                if(assinstanceproperty[i].getName().equals("_maxSearchDepthOther"))
                {
                    flag = true;
                    s5 = "_maxSearchDepth";
                } else
                if(assinstanceproperty[i].getName().equals("percentageBaseOther"))
                {
                    flag = true;
                    s5 = "_percentageBase";
                } else
                if(assinstanceproperty[i].getName().equals("scaleOther"))
                {
                    flag = true;
                    s5 = "_scale";
                } else
                if(assinstanceproperty[i].getName().equals("serviceOther"))
                {
                    flag = true;
                    s5 = "_service";
                } else
                if(assinstanceproperty[i].getName().equals("topazLogging"))
                {
                    flag1 = true;
                } else
                if(assinstanceproperty[i].getName().equals("_perfmonMsmtsProp"))
                {
                    hashmap.put(assinstanceproperty[i].getName(), monitorUtils.transformPerfmonMeasurementsToMgFormat((String)assinstanceproperty[i].getValue()));
                }
                if(flag)
                {
                    String s4 = (String)assinstanceproperty[i].getValue();
                    if(s4.length() > 0)
                    {
                        hashmap.put(s5, s4);
                    }
                    flag = false;
                    continue;
                }
                if(flag1)
                {
                    String s6 = "";
                    if(assinstanceproperty[i].getValue() != null && ((String)assinstanceproperty[i].getValue()).length() > 0)
                    {
                        s6 = (String)assinstanceproperty[i].getValue();
                        if(!s6.equals("logEverything"))
                        {
                            hashmap.put(s6, "true");
                        }
                    }
                    if(!s6.equals("_notLogToTopaz"))
                    {
                        hashmap.put("_notLogToTopaz", "");
                    }
                    if(!s6.equals("_logOnlyMonitorData"))
                    {
                        hashmap.put("_logOnlyMonitorData", "");
                    }
                    if(!s6.equals("_logOnlyThresholdMeas"))
                    {
                        hashmap.put("_logOnlyThresholdMeas", "");
                    }
                    if(!s6.equals("_onlyStatusChanges"))
                    {
                        hashmap.put("_onlyStatusChanges", "");
                    }
                    flag1 = false;
                    continue;
                }
                if(assinstanceproperty[i].getName().equals("_port"))
                {
                    if((String)hashmap.get("_port") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_oid"))
                {
                    if((String)hashmap.get("_oid") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_maxSearchDepth"))
                {
                    if((String)hashmap.get("_maxSearchDepth") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_percentageBase"))
                {
                    if((String)hashmap.get("_percentageBase") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_scale"))
                {
                    if((String)hashmap.get("_scale") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_service") && (String)hashmap.get("_service") != null)
                {
                    flag2 = true;
                }
                if(assinstanceproperty[i].getName().equals("_description") || assinstanceproperty[i].getName().equals("_monitorDescription") && assinstanceproperty[i].getValue() != null)
                {
                    assinstanceproperty[i] = new SSInstanceProperty(assinstanceproperty[i].getName(), COM.dragonflow.Utils.TextUtils.replaceString((String)assinstanceproperty[i].getValue(), "\r\n", " "));
                }
                if(!flag2)
                {
                    hashmap.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
                }
            }

            jgl.HashMap hashmap1 = MasterConfig.getMasterConfig();
            int j = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_URLSequenceMonitorSteps"));
            for(int k = 0; k < j; k++)
            {
                String postData = (String)hashmap.get("_postData" + (new Integer(k + 1)).toString());
                int l = -1;
                do
                {
                    if(postData != null && postData.length() > 0)
                    {
                        l = postData.indexOf("{[");
                        int i1 = postData.indexOf("}");
                        if(l >= 0 && i1 >= 0)
                        {
                            postData = postData.substring(0, l) + postData.substring(i1 + 1);
                        }
                        hashmap.put("_postData" + (new Integer(k + 1)).toString(), postData);
                    }
                } while(l != -1);
                String reference = (String)hashmap.get("_reference" + (new Integer(k + 1)).toString());
                if(reference == null || reference.length() <= 0)
                {
                    continue;
                }
                int j1 = reference.indexOf("{[");
                int k1 = reference.indexOf("}");
                if(j1 >= 0 && k1 >= 0)
                {
                    reference = reference.substring(k1 + 1);
                }
                hashmap.put("_reference" + (new Integer(k + 1)).toString(), reference);
            }

            fixDisableAlertingParams(hashmap);
            fixDisableGroupOrMonitorParams(hashmap);
            removeAPIDisableProperties(hashmap);
            if(s.equals("WebServiceMonitor"))
            {
                processWSDLParameters(hashmap);
            }
            String machine = (String)hashmap.get("_machine");
            processMachineName(machine, hashmap);
            AtomicMonitor atomicmonitor = instantiateMonitor(s);
            setMonitorProperties(OP_ADD, atomicmonitor, "", s1, hashmap);
            atomicmonitor.initialize(hashmap);
            validateProperties(hashmap, atomicmonitor, s, APISiteView.FILTER_CONFIGURATION_ADD_ALL);
            writeMonitor(OP_ADD, atomicmonitor, "", s1);
            ScheduleManager schedulemanager = ScheduleManager.getInstance();
            schedulemanager.addMonitorToScheduleObject(atomicmonitor);
            s2 = atomicmonitor.getProperty("_id");
            APIMonitor.forceConfigurationRefresh();
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "create"
            }, 0L, exception.getMessage());
        }
        return new SSStringReturnValue(s2);
    }

    public void update(String s, String s1, SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try
        {
            jgl.HashMap hashmap = new HashMap(true);
            String s2 = "";
            boolean flag = false;
            boolean flag1 = false;
            String s4 = null;
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                boolean flag2 = false;
                if(assinstanceproperty[i].getName().equals("portOther"))
                {
                    flag = true;
                    s4 = "_port";
                } else
                if(assinstanceproperty[i].getName().equals("oidOther"))
                {
                    flag = true;
                    s4 = "_oid";
                } else
                if(assinstanceproperty[i].getName().equals("_maxSearchDepthOther"))
                {
                    flag = true;
                    s4 = "_maxSearchDepth";
                } else
                if(assinstanceproperty[i].getName().equals("percentageBaseOther"))
                {
                    flag = true;
                    s4 = "_percentageBase";
                } else
                if(assinstanceproperty[i].getName().equals("scaleOther"))
                {
                    flag = true;
                    s4 = "_scale";
                } else
                if(assinstanceproperty[i].getName().equals("serviceOther"))
                {
                    flag = true;
                    s4 = "_service";
                } else
                if(assinstanceproperty[i].getName().equals("topazLogging"))
                {
                    flag1 = true;
                } else
                if(assinstanceproperty[i].getName().equals("_perfmonMsmtsProp"))
                {
                    hashmap.put(assinstanceproperty[i].getName(), monitorUtils.transformPerfmonMeasurementsToMgFormat((String)assinstanceproperty[i].getValue()));
                }
                if(flag)
                {
                    String s3 = (String)assinstanceproperty[i].getValue();
                    if(s3.length() > 0)
                    {
                        hashmap.put(s4, s3);
                    }
                    flag = false;
                    continue;
                }
                if(flag1)
                {
                    String s5 = "";
                    if(assinstanceproperty[i].getValue() != null && ((String)assinstanceproperty[i].getValue()).length() > 0)
                    {
                        s5 = (String)assinstanceproperty[i].getValue();
                        if(!s5.equals("logEverything"))
                        {
                            hashmap.put(s5, "true");
                        }
                    }
                    if(!s5.equals("_notLogToTopaz"))
                    {
                        hashmap.put("_notLogToTopaz", "");
                    }
                    if(!s5.equals("_logOnlyMonitorData"))
                    {
                        hashmap.put("_logOnlyMonitorData", "");
                    }
                    if(!s5.equals("_logOnlyThresholdMeas"))
                    {
                        hashmap.put("_logOnlyThresholdMeas", "");
                    }
                    if(!s5.equals("_onlyStatusChanges"))
                    {
                        hashmap.put("_onlyStatusChanges", "");
                    }
                    flag1 = false;
                    continue;
                }
                if(assinstanceproperty[i].getName().equals("_port"))
                {
                    if((String)hashmap.get("_port") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_oid"))
                {
                    if((String)hashmap.get("_oid") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_maxSearchDepth"))
                {
                    if((String)hashmap.get("_maxSearchDepth") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_percentageBase"))
                {
                    if((String)hashmap.get("_percentageBase") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_scale"))
                {
                    if((String)hashmap.get("_scale") != null)
                    {
                        flag2 = true;
                    }
                } else
                if(assinstanceproperty[i].getName().equals("_service") && (String)hashmap.get("_service") != null)
                {
                    flag2 = true;
                }
                if(assinstanceproperty[i].getName().equals("_description") || assinstanceproperty[i].getName().equals("_monitorDescription") && assinstanceproperty[i].getValue() != null)
                {
                    assinstanceproperty[i] = new SSInstanceProperty(assinstanceproperty[i].getName(), COM.dragonflow.Utils.TextUtils.replaceString((String)assinstanceproperty[i].getValue(), "\r\n", " "));
                }
                if(!flag2)
                {
                    hashmap.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
                }
            }

            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s1);
            MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s1);
            AtomicMonitor atomicmonitor = null;
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor)enumeration.nextElement();
                String s7 = monitor.getProperty("_id");
                if((s7 != null) & s7.equals(s))
                {
                    atomicmonitor = (AtomicMonitor)monitor;
                    break;
                }
            } 
            String s6 = "_postData1";
            if(atomicmonitor.getPropertyObject(s6) != null)
            {
                fixPostDataParams(hashmap);
            }
            fixDisableAlertingParams(hashmap);
            fixDisableGroupOrMonitorParams(hashmap);
            removeAPIDisableProperties(hashmap);
            String s8 = (String)hashmap.get("_machine");
            processMachineName(s8, hashmap);
            if(atomicmonitor != null)
            {
                String s9 = atomicmonitor.getClass().toString();
                int j = s9.lastIndexOf(".");
                if(j != -1)
                {
                    s9 = s9.substring(j + 1);
                }
                if(s9.equals("WebServiceMonitor"))
                {
                    processWSDLParameters(hashmap);
                }
                ScheduleManager schedulemanager = ScheduleManager.getInstance();
                String s10 = schedulemanager.getScheduleIdFromMonitor(atomicmonitor);
                setMonitorProperties(OP_EDIT, atomicmonitor, s, s1, hashmap);
                validateProperties(hashmap, atomicmonitor, s9, FILTER_CONFIGURATION_EDIT_ALL);
                writeMonitor(OP_EDIT, atomicmonitor, s, s1);
                schedulemanager.updateMonitorInScheduleObject(s10, atomicmonitor);
            } else
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                    s1 + "/" + s
                });
            }
            DetectConfigurationChange detectconfigurationchange = DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "update"
            }, 0L, exception.getMessage());
        }
    }

    private void fixPostDataParams(jgl.HashMap hashmap)
    {
        jgl.HashMap hashmap1 = MasterConfig.getMasterConfig();
        int i = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_URLSequenceMonitorSteps"));
        for(int j = 0; j < i; j++)
        {
            String s = (String)hashmap.get("_postData" + (new Integer(j + 1)).toString());
            int k = -1;
            do
            {
                if(s != null && s.length() > 0)
                {
                    k = s.indexOf("{[");
                    int l = s.indexOf("}");
                    if(k >= 0 && l >= 0)
                    {
                        s = s.substring(0, k) + s.substring(l + 1);
                    }
                    hashmap.put("_postData" + (new Integer(j + 1)).toString(), s);
                }
            } while(k != -1);
            String s1 = (String)hashmap.get("_reference" + (new Integer(j + 1)).toString());
            if(s1 == null || s1.length() <= 0)
            {
                continue;
            }
            int i1 = s1.indexOf("{[");
            int j1 = s1.indexOf("}");
            if(i1 >= 0 && j1 >= 0)
            {
                s1 = s1.substring(j1 + 1);
            }
            hashmap.put("_reference" + (new Integer(j + 1)).toString(), s1);
        }

    }

    private void processMachineName(String s, jgl.HashMap hashmap)
        throws COM.dragonflow.SiteViewException.SiteViewParameterException
    {
        if(s != null)
        {
            if(s.length() > 0)
            {
                try
                {
                    if(s.startsWith("\\\\"))
                    {
                        s = s.substring(2);
                    } else
                    if(s.startsWith("remote:"))
                    {
                        s = Machine.getMachineHost(s);
                    }
                    java.net.InetAddress inetaddress = java.net.InetAddress.getByName(s);
                    String s1 = inetaddress.getHostAddress();
                    java.net.InetAddress inetaddress1 = java.net.InetAddress.getLocalHost();
                    String s2 = inetaddress1.getHostAddress();
                    if(s1.equals(s2))
                    {
                        hashmap.remove("_machine");
                    }
                }
                catch(java.net.UnknownHostException unknownhostexception)
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_NO_IP, new String[] {
                        "localhost"
                    });
                }
                catch(java.lang.Exception exception)
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_NO_IP, new String[] {
                        s
                    });
                }
            } else
            {
                hashmap.remove("_machine");
            }
        }
    }

    public void delete(String s, String s1)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try
        {
            ScheduleManager schedulemanager = ScheduleManager.getInstance();
            AtomicMonitor atomicmonitor = (AtomicMonitor)SiteViewGroup.currentSiteView().getElement(s1 + "/" + s);
            schedulemanager.deleteMonitorFromScheduleObject(schedulemanager.getScheduleIdFromMonitor(atomicmonitor), atomicmonitor.getFullID());
            deleteMonitorInternal(s1 + " " + s);
            saveGroups();
            DetectConfigurationChange detectconfigurationchange = DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "delete"
            }, 0L, exception.getMessage());
        }
    }

    public SSStringReturnValue move(String s, String s1, String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        String s3 = "";
        try
        {
            jgl.Array array = new Array();
            jgl.Array array1 = new Array();
            array1.add("");
            array.add(s1 + " " + s);
            ConfigurationChanger configurationchanger = new ConfigurationChanger();
            String as[] = new String[0];
            configurationchanger.manageMonitors(array, array1, s2, true, as);
            jgl.Array array2 = ReadGroupFrames(s2);
            s3 = (String)((jgl.HashMap)array2.at(0)).get("_nextID");
            long l = java.lang.Long.parseLong(s3);
            s3 = String.valueOf(l - 1L);
            DetectConfigurationChange detectconfigurationchange = DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "move"
            }, 0L, exception.getMessage());
        }
        return new SSStringReturnValue(s3);
    }

    public SSStringReturnValue copy(String s, String s1, String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        String s3 = "";
        try
        {
            jgl.Array array = new Array();
            jgl.Array array1 = new Array();
            array.add(s1 + " " + s);
            ConfigurationChanger configurationchanger = new ConfigurationChanger();
            String as[] = new String[0];
            jgl.Array array2 = getGroupFrames(s1);
            int i = COM.dragonflow.Page.CGI.findMonitorIndex(array2, s);
            if(i >= 1)
            {
                jgl.HashMap hashmap = (jgl.HashMap)array2.at(i);
                array1.add(hashmap.get("_name"));
            }
            jgl.Array array3 = configurationchanger.manageMonitors(array, array1, s2, false, as);
            s3 = (String)((jgl.HashMap)array3.at(0)).get("_nextID");
            long l = java.lang.Long.parseLong(s3);
            s3 = String.valueOf(l - 1L);
            DetectConfigurationChange detectconfigurationchange = DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "copy"
            }, 0L, exception.getMessage());
        }
        return new SSStringReturnValue(s3);
    }

    public SSMonitorInstance runExisting(String s, String s1, long l)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        return runExisting(s, s1, l, false);
    }

    /**
     * 
     * 
     * @deprecated Method runExisting is deprecated
     */
    public SSMonitorInstance runExisting(String s, String s1, long l, boolean flag)
    throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSMonitorInstance ssmonitorinstance;
        
        ssmonitorinstance = null;
        try
        {
            AtomicMonitor atomicmonitor;
            boolean flag1;
            
            
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s1);
            MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s1);
            atomicmonitor = null;
            if(monitorgroup != null)
            {
                Enumeration enumeration = monitorgroup.getMonitors();
                Monitor monitor;
                String s2;
                while (enumeration.hasMoreElements())
                {
                    monitor = (Monitor)enumeration.nextElement();
                    s2 = monitor.getProperty("_id");
                    if (s2 != null && s2.equals(s)) {
                        atomicmonitor = (AtomicMonitor)monitor;
                    }
                }
                
                flag1 = false;
                if(atomicmonitor != null)
                {
                    
                    long l1 = (new Date()).getTime();
                    
                    while (atomicmonitor.isRunning())
                    {
                        Platform.sleep(1000L);
                        if(atomicmonitor.isRunning() && l >= 0L && (new Date()).getTime() > l1 + l)
                        {
                            flag1 = true;
                            break;
                        }
                    } 
                    
                    if(flag1)
                    {
                        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_API_MONITOR_TIMED_OUT_WAITING);
                    }
                    atomicmonitor.runUpdate(true);
                    if(atomicmonitor.isRunning())
                    {
                        l1 = (new Date()).getTime();
                        while (atomicmonitor.isRunning())
                        {
                            Platform.sleep(1000L);
                            if (atomicmonitor.isRunning() && l >= 0L && (new Date()).getTime() > l1 + l) {
                                flag1 = true;
                            }
                        }
                    }
                    if(flag1)
                    {
                        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_API_MONITOR_TIMED_OUT);
                    }
                    SSInstanceProperty assinstanceproperty[] = getPropertiesForMonitorInstance(atomicmonitor, null, FILTER_RUNTIME_ALL);
                    if(!atomicmonitor.collectionErrorOccurred() || flag)
                    {
                        ssmonitorinstance = new SSMonitorInstance(s1, s, assinstanceproperty);
                    } else
                    {
                        COM.dragonflow.SiteViewException.SiteViewError siteviewerror = atomicmonitor.getCollectionError();
                        if(siteviewerror.getType() == 1)
                        {
                            throw new SiteViewAvailabilityException(siteviewerror);
                        } else
                        {
                            throw new SiteViewOperationalException(siteviewerror);
                        }
                    }
                } else {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                            s1 + "/" + s
                    });
                }
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                    "APIMonitor", "runExisting"
            }, 0L, exception.getMessage());
        }
        return ssmonitorinstance;
    }

    public SSInstanceProperty[] runTemporary(String s, SSInstanceProperty assinstanceproperty[], long l)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        return runTemporary(s, assinstanceproperty, l, false);
    }

    /**
     * 
     * 
     * @deprecated Method runTemporary is deprecated
     */
    public SSInstanceProperty[] runTemporary(String s, SSInstanceProperty assinstanceproperty[], long l, boolean flag)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSInstanceProperty assinstanceproperty1[] = null;
        AtomicMonitor atomicmonitor;
        COM.dragonflow.Utils.ThreadPool.SingleThread singlethread;
        
        try {
        jgl.HashMap hashmap = new HashMap(true);
        for(int i = 0; i < assinstanceproperty.length; i++)
        {
            hashmap.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
        }

        atomicmonitor = instantiateMonitor(s);
        setMonitorProperties(OP_TEMP, atomicmonitor, "", "", hashmap);
        validateProperties(hashmap, atomicmonitor, s, APISiteView.FILTER_CONFIGURATION_ADD_ALL);
        singlethread = AtomicMonitor.monitorThreadsPool.getThread();
        singlethread.setName("Temporary monitor run thread");
        java.lang.Object obj1 = new Object();
        synchronized (obj1) {
        Worker worker = null;
        try
        {
            worker = new Worker(atomicmonitor, obj1);
            singlethread.activate(worker);
            if(l < 0L)
            {
                obj1.wait();
            } else
            {
                obj1.wait(l);
            }
        }
        catch(java.lang.InterruptedException interruptedexception) { }
        if(flag)
        {
            return getPropertiesForMonitorInstance(atomicmonitor, null, APISiteView.FILTER_RUNTIME_ALL);
        }
        if(!worker.timedOut)
        {
            if(!atomicmonitor.collectionErrorOccurred())
            {
                assinstanceproperty1 = getPropertiesForMonitorInstance(atomicmonitor, null, APISiteView.FILTER_RUNTIME_ALL);
            } else
            {
                COM.dragonflow.SiteViewException.SiteViewError siteviewerror = atomicmonitor.getCollectionError();
                if(siteviewerror.getType() == 1)
                {
                    throw new SiteViewAvailabilityException(siteviewerror);
                } else
                {
                    throw new SiteViewOperationalException(siteviewerror);
                }
            }
        } else
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_API_MONITOR_TIMED_OUT);
        }
        }
        }
        catch (SiteViewException e) {
        e.fillInStackTrace();
        throw e;
        }
        catch (Exception e) {
        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
            "APIMonitor", "runTemporary"
        }, 0L, e.getMessage());
        }

        return assinstanceproperty1;
    }

    /**
     * 
     * 
     * @param s
     * @return
     * @throws COM.dragonflow.SiteViewException.SiteViewException
     */
    public SSInstanceProperty[] getClassAttributes(String s)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try {
        SSInstanceProperty assinstanceproperty[];
        jgl.HashMap hashmap = getClassAttribs(s);
        assinstanceproperty = null;
        int i = 0;
        assinstanceproperty = new SSInstanceProperty[hashmap.size()];
        for(Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements();)
        {
            String s1 = (String)enumeration.nextElement();
            String s2 = "";
            if(!s1.equals("elements"))
            {
                s2 = (String)hashmap.get(s1);
            }
            assinstanceproperty[i] = new SSInstanceProperty(s1, s2);
            i++;
        }

        return assinstanceproperty;
        }
        catch (SiteViewException e) {
        e.fillInStackTrace();
        throw e;
        }
        catch (Exception e) {
        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
            "APIMonitor", "getClassAttributes"
        }, 0L, e.getMessage());
    }
    }

    public SSPropertyDetails[] getClassPropertiesDetails(String s, int i, SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSPropertyDetails asspropertydetails[] = null;
        try
        {
            jgl.Array array = new Array();
            AtomicMonitor atomicmonitor = instantiateMonitor(s);
            for(int j = 0; j < assinstanceproperty.length; j++)
            {
                atomicmonitor.setProperty(assinstanceproperty[j].getName(), (String)assinstanceproperty[j].getValue());
            }

            for(Enumeration enumeration = getFilteredMonitorProperties(atomicmonitor, new Vector(), i); enumeration.hasMoreElements(); array.add(enumeration.nextElement())) { }
            java.util.HashMap hashmap = new java.util.HashMap();
            for(int k = 0; k < assinstanceproperty.length; k++)
            {
                if(assinstanceproperty[k] != null)
                {
                    hashmap.put(assinstanceproperty[k].getName(), assinstanceproperty[k].getValue());
                }
            }

            Vector vector = createThresholdProperties(i, array, atomicmonitor, hashmap);
            asspropertydetails = new SSPropertyDetails[array.size() + vector.size()];
            for(int l = 0; l < array.size(); l++)
            {
                asspropertydetails[l] = getClassProperty((StringProperty)array.at(l), atomicmonitor, hashmap, false);
            }

            for(int i1 = 0; i1 < vector.size(); i1++)
            {
                asspropertydetails[i1 + array.size()] = (SSPropertyDetails)vector.elementAt(i1);
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getClassPropertiesDetails"
            }, 0L, exception.getMessage());
        }
        return asspropertydetails;
    }

    public SSPropertyDetails getClassPropertyDetails(String s, String s1, SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSPropertyDetails sspropertydetails = null;
        try
        {
            Object obj = null;
            AtomicMonitor atomicmonitor = instantiateMonitor(s1);
            if(APIMonitor.isValidObject(atomicmonitor.getClass().getName(), "Monitor"))
            {
                java.util.HashMap hashmap = new java.util.HashMap();
                for(int i = 0; i < assinstanceproperty.length; i++)
                {
                    if(assinstanceproperty[i] != null)
                    {
                        hashmap.put(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
                    }
                }

                for(int j = 0; j < assinstanceproperty.length; j++)
                {
                    if(assinstanceproperty[j] != null)
                    {
                        atomicmonitor.setProperty(assinstanceproperty[j].getName(), (String)assinstanceproperty[j].getValue());
                    }
                }

                StringProperty stringproperty = atomicmonitor.getPropertyObject(s);
                if(s.indexOf("error-") >= 0 || s.indexOf("warning-") >= 0 || s.indexOf("good-") >= 0)
                {
                    Enumeration enumeration = atomicmonitor.getStatePropertyObjects(false);
                    if(!atomicmonitor.isMultiThreshold())
                    {
                        jgl.Array array = atomicmonitor.getProperties();
                        enumeration = array.elements();
                    }
                    jgl.Array array1 = new Array();
                    while (enumeration.hasMoreElements()) {
                        StringProperty stringproperty1 = (StringProperty)enumeration.nextElement();
                        if(stringproperty1.isThreshold())
                        {
                            array1.add(stringproperty1);
                        }
                    } 

                    sspropertydetails = getThreshold(s, array1, atomicmonitor);
                    if(sspropertydetails == null)
                    {
                        throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_NONEXISTANT_PROPERTY, new String[] {
                            s1, s
                        });
                    }
                } else
                if(s.indexOf("availableObjects") != -1)
                {
                    stringproperty = new StringProperty("availableObjects", "", "Available Objects");
                    stringproperty.isMultiLine = true;
                    sspropertydetails = getClassProperty(stringproperty, atomicmonitor, hashmap, true);
                } else
                if(s.indexOf("availableCountersHierarchical") != -1)
                {
                    stringproperty = new StringProperty("availableCountersHierarchical", "", "Available Counters Hierarchical");
                    stringproperty.isMultiLine = true;
                    sspropertydetails = getClassProperty(stringproperty, atomicmonitor, hashmap, true);
                } else
                if(s.indexOf("availableCounters") != -1)
                {
                    stringproperty = new StringProperty("availableCounters", "", "Available Counters");
                    stringproperty.isMultiLine = true;
                    sspropertydetails = getClassProperty(stringproperty, atomicmonitor, hashmap, true);
                } else
                if(s.indexOf("defaultCounters") != -1)
                {
                    stringproperty = new StringProperty("defaultCounters", "", "Default Counters");
                    stringproperty.isMultiLine = true;
                    sspropertydetails = getClassProperty(stringproperty, atomicmonitor, hashmap, true);
                } else
                if(s.indexOf("availableInstances") != -1)
                {
                    stringproperty = new StringProperty("availableInstances", "", "Available Instances");
                    stringproperty.isMultiLine = true;
                    sspropertydetails = getClassProperty(stringproperty, atomicmonitor, hashmap, true);
                } else
                {
                    if(s != null && stringproperty == null)
                    {
                        throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_PROPERTY_NOT_FOUND, new String[] {
                            s
                        });
                    }
                    sspropertydetails = getClassProperty(stringproperty, atomicmonitor, hashmap, true);
                }
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getClassPropertyDetails"
            }, 0L, exception.getMessage());
        }
        return sspropertydetails;
    }

    public SSPropertyDetails getInstancePropertyDetails(String s, String s1, String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSPropertyDetails sspropertydetails = null;
        try
        {
            jgl.Array array = null;
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            s2 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s2);
            MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s2);
            AtomicMonitor atomicmonitor = null;
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor)enumeration.nextElement();
                String s3 = monitor.getProperty("_id");
                if((s3 != null) & s3.equals(s1))
                {
                    atomicmonitor = (AtomicMonitor)monitor;
                    break;
                }
            }
            
            if(atomicmonitor != null)
            {
                StringProperty stringproperty = atomicmonitor.getPropertyObject(s);
                if(s.indexOf("error-") >= 0 || s.indexOf("warning-") >= 0 || s.indexOf("good-") >= 0)
                {
                    Enumeration enumeration1 = array.elements();
                    jgl.Array array1 = new Array();
                    while (enumeration1.hasMoreElements()) {
                        StringProperty stringproperty1 = (StringProperty)enumeration1.nextElement();
                        if(stringproperty1.isThreshold())
                        {
                            array1.add(stringproperty1);
                        }
                    } 
                    
                    sspropertydetails = getThreshold(s, array1, atomicmonitor);
                    if(sspropertydetails == null)
                    {
                        throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_NONEXISTANT_PROPERTY, new String[] {
                            s2 + "/" + s1, s
                        });
                    }
                } else
                {
                    sspropertydetails = getClassProperty(stringproperty, atomicmonitor, new java.util.HashMap(), true);
                }
            } else
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                    s2 + "/" + s1
                });
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getInstancePropertyDetails"
            }, 0L, exception.getMessage());
        }
        return sspropertydetails;
    }

    public SSMonitorInstance[] getInstances(String groupid, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSMonitorInstance assmonitorinstance[] = null;
        try
        {
            if(groupid == null || groupid.length() == 0)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_GROUP_ID_MISSING);
            }
            Vector vector = new Vector();
            java.util.Collection collection = getMonitorsForGroup(groupid);
            String s1;
            String s2;
            SSInstanceProperty assinstanceproperty1[];
            for(java.util.Iterator iterator = collection.iterator(); iterator.hasNext(); vector.addElement(new SSMonitorInstance(s1, s2, assinstanceproperty1)))
            {
                Monitor monitor = (Monitor)iterator.next();
                s1 = monitor.getProperty(Monitor.pOwnerID);
                if(!$assertionsDisabled && !s1.equals(groupid))
                {
                    throw new AssertionError();
                }
                s2 = monitor.getProperty(Monitor.pID);
                SSInstanceProperty assinstanceproperty[] = getInstanceProperties(s2, s1, i);
                int k = 0;
                boolean flag = false;
                if(Alert.getInstance().getAlertsResidingInGroupOrMonitor(s1, s2).size() > 0)
                {
                    flag = true;
                    k++;
                }
                assinstanceproperty1 = new SSInstanceProperty[assinstanceproperty.length + k];
                if(flag)
                {
                    assinstanceproperty1[0] = new SSInstanceProperty("hasDependencies", "true");
                }
                for(int l = 0; l < assinstanceproperty.length; l++)
                {
                    assinstanceproperty1[l + k] = assinstanceproperty[l];
                }

            }

            assmonitorinstance = new SSMonitorInstance[vector.size()];
            for(int j = 0; j < vector.size(); j++)
            {
                assmonitorinstance[j] = (SSMonitorInstance)vector.elementAt(j);
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getInstances"
            }, 0L, exception.getMessage());
        }
        return assmonitorinstance;
    }

    public SSInstanceProperty[] getInstanceProperties(String monitorid, String groupid, int i, boolean flag)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSInstanceProperty assinstanceproperty[] = null;
        try
        {
            AtomicMonitor atomicmonitor = null;
            java.util.Collection collection = getMonitorsForGroup(groupid);
            java.util.Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                Monitor monitor = (Monitor)iterator.next();
                String s2 = monitor.getProperty("_id");
                if(s2 != null && s2.equals(monitorid))
                {
                atomicmonitor = (AtomicMonitor)monitor;
                break;
                }
            } 
            if(atomicmonitor != null)
            {
                assinstanceproperty = getPropertiesForMonitorInstance(atomicmonitor, "", i);
                if(flag && Alert.getInstance().getAlertsResidingInGroupOrMonitor(groupid, monitorid).size() > 0)
                {
                    SSInstanceProperty assinstanceproperty1[] = new SSInstanceProperty[assinstanceproperty.length + 1];
                    java.lang.System.arraycopy(assinstanceproperty, 0, assinstanceproperty1, 0, assinstanceproperty.length);
                    assinstanceproperty1[assinstanceproperty1.length - 1] = new SSInstanceProperty("hasDependencies", "true");
                    assinstanceproperty = assinstanceproperty1;
                }
            } else
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                		groupid + "/" + monitorid
                });
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getInstanceProperties"
            }, 0L, exception.getMessage());
        }
        return assinstanceproperty;
    }

    public SSInstanceProperty[] getInstanceProperties(String monitorid, String groupid, int i) throws SiteViewException
    {
        return getInstanceProperties(monitorid, groupid, i, false);
    }

    public SSInstanceProperty getInstanceProperty(String monitorid, String groupid, String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSInstanceProperty ssinstanceproperty = null;
        try
        {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            s2 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s2);
            MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s2);
            AtomicMonitor atomicmonitor = null;
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor)enumeration.nextElement();
                String s3 = monitor.getProperty("_id");
                if((s3 != null) & s3.equals(groupid))
                {
                atomicmonitor = (AtomicMonitor)monitor;
                break;
                }
            }
            Object obj = null;
            if(atomicmonitor != null)
            {
                SSInstanceProperty assinstanceproperty[] = getPropertiesForMonitorInstance(atomicmonitor, monitorid, APISiteView.FILTER_ALL);
                for(int i = 0; i < assinstanceproperty.length; i++)
                {
                    if(assinstanceproperty[i].getName().equals(monitorid))
                    {
                        ssinstanceproperty = assinstanceproperty[i];
                    }
                }

            } else
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                    s2 + "/" + groupid
                });
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, 0L, exception.getMessage());
        }
        return ssinstanceproperty;
    }

    public SSInstanceProperty[] getURLStepProperties(String s, String s1, SSInstanceProperty assinstanceproperty[], String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSInstanceProperty assinstanceproperty1[] = null;
        try
        {
            jgl.HashMap hashmap = new HashMap(true);
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                hashmap.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }

            AtomicMonitor atomicmonitor = instantiateMonitor(s);
            setMonitorProperties(OP_TEMP, atomicmonitor, "", s1, hashmap);
            hashmap = validateProperties(hashmap, atomicmonitor, s, APISiteView.FILTER_CONFIGURATION_ADD_ALL);
            StringBuffer stringbuffer = new StringBuffer();
            StringBuffer stringbuffer1 = new StringBuffer();
            StringBuffer stringbuffer2 = new StringBuffer();
            StringBuffer stringbuffer3 = new StringBuffer();
            long al[] = COM.dragonflow.StandardMonitor.URLSequenceMonitor.checkURLSequence(hashmap, stringbuffer, null, stringbuffer3, stringbuffer1, stringbuffer2, (COM.dragonflow.StandardMonitor.URLSequenceMonitor)atomicmonitor);
            SSInstanceProperty assinstanceproperty2[] = getPropertiesForMonitorInstance(atomicmonitor, "", APISiteView.FILTER_ALL);
            assinstanceproperty1 = new SSInstanceProperty[assinstanceproperty2.length + 7];
            String s3 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer1.toString());
            assinstanceproperty1[0] = new SSInstanceProperty("content", s3);
            if(s3.length() > 0)
            {
                HTMLTagParser htmltagparser = new HTMLTagParser(s3, TARGET_TAGS);
                jgl.HashMap hashmap1 = MasterConfig.getMasterConfig();
                htmltagparser.ignoreScripts = false;
                htmltagparser.ignoreNoscripts = false;
                if(hashmap1.get("_urlHTMLInJavaScript") != null && ((String)hashmap1.get("_urlHTMLInJavaScript")).length() == 0)
                {
                    htmltagparser.ignoreScripts = true;
                    htmltagparser.ignoreNoscripts = true;
                }
                htmltagparser.process();
                Enumeration enumeration = getLinks(htmltagparser);
                String s4 = "";
                for(; enumeration.hasMoreElements(); enumeration.nextElement())
                {
                    s4 = s4 + (String)enumeration.nextElement() + "\r\n";
                }

                assinstanceproperty1[1] = new SSInstanceProperty("links", s4);
                s4 = "";
                for(Enumeration enumeration1 = getForms(htmltagparser, atomicmonitor, s2); enumeration1.hasMoreElements(); enumeration1.nextElement())
                {
                    s4 = s4 + (String)enumeration1.nextElement() + "\r\n";
                }

                assinstanceproperty1[2] = new SSInstanceProperty("forms", s4);
                for(int k = 0; k < assinstanceproperty2.length; k++)
                {
                    if(assinstanceproperty2[k].getName().equals("_postData" + s2))
                    {
                        assinstanceproperty2[k] = new SSInstanceProperty("_postData" + s2, atomicmonitor.getProperty("_postData" + s2));
                    }
                }

                s4 = "";
                for(Enumeration enumeration2 = getFrames(htmltagparser); enumeration2.hasMoreElements(); enumeration2.nextElement())
                {
                    s4 = s4 + (String)enumeration2.nextElement() + "\r\n";
                }

                assinstanceproperty1[3] = new SSInstanceProperty("frames", s4);
                s4 = "";
                for(Enumeration enumeration3 = getRefresh(htmltagparser); enumeration3.hasMoreElements(); enumeration3.nextElement())
                {
                    s4 = s4 + (String)enumeration3.nextElement() + "\r\n";
                }

                assinstanceproperty1[4] = new SSInstanceProperty("metas", s4);
                String s5 = "";
                int l = stringbuffer3.toString().toLowerCase().lastIndexOf("<hr><b><a name=step");
                if(l >= 0 && l < stringbuffer3.length())
                {
                    int i1 = -1;
                    for(i1 = stringbuffer2.toString().toLowerCase().indexOf("content-type:"); i1 >= 0 && stringbuffer2.toString().substring(i1 + 13).toLowerCase().indexOf("content-type:") >= 0; i1 += stringbuffer2.toString().substring(i1 + 13).toLowerCase().indexOf("content-type:") + 13) { }
                    if(i1 >= 0)
                    {
                        int j1 = stringbuffer2.toString().substring(i1).toLowerCase().indexOf("<!doctype");
                        if(j1 < 0)
                        {
                            j1 = stringbuffer2.toString().substring(i1).toLowerCase().indexOf("<html");
                        }
                        if(j1 > 0)
                        {
                            i1 += j1;
                        }
                    }
                    s5 = stringbuffer2.toString().substring(0, i1);
                }
                assinstanceproperty1[5] = new SSInstanceProperty("header", s5);
                assinstanceproperty1[6] = new SSInstanceProperty("httpstatus", (new Long(al[0])).toString());
            }
            for(int j = 0; j < assinstanceproperty2.length; j++)
            {
                assinstanceproperty1[j + 7] = assinstanceproperty2[j];
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getURLStepProperties"
            }, 0L, exception.getMessage());
        }
        return assinstanceproperty1;
    }

    public java.util.Collection getAllMonitors()
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        Vector vector = new Vector();
        ConfigurationChanger.getGroupsMonitors(getAllAllowedGroups(), vector, null, false);
        return vector;
    }

    public java.util.Collection getChildMonitors(String groupid)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        return getMonitorsForGroup(groupid);
    }

    public SSStringReturnValue[] getMonitorTypes()
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSStringReturnValue assstringreturnvalue[] = null;
        try
        {
            int i = findType("Monitor");
            jgl.Array array = (jgl.Array)ssChildObjects.elementAt(i);
            assstringreturnvalue = new SSStringReturnValue[array.size()];
            for(int j = 0; j < array.size(); j++)
            {
                SSStringReturnValue ssstringreturnvalue = new SSStringReturnValue(((String[])array.at(j))[0]);
                int k = ssstringreturnvalue.getValue().lastIndexOf(".");
                if(k != -1)
                {
                    ssstringreturnvalue = new SSStringReturnValue(ssstringreturnvalue.getValue().substring(k + 1));
                }
                assstringreturnvalue[j] = ssstringreturnvalue;
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getMonitorTypes"
            }, 0L, exception.getMessage());
        }
        return assstringreturnvalue;
    }

    public SSStringReturnValue getTopazID(String s, String s1)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSStringReturnValue ssstringreturnvalue = null;
        try
        {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s1);
            MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s1);
            AtomicMonitor atomicmonitor = null;
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor)enumeration.nextElement();
                String s2 = monitor.getProperty("_id");
                if((s2 != null) & s2.equals(s))
                {
                atomicmonitor = (AtomicMonitor)monitor;
                break;
                }
            } 
            if(atomicmonitor != null)
            {
//                ssstringreturnvalue = getTopazID(atomicmonitor);
            } else
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_TOPAZ_ID, new String[] {
                    s1, s
                });
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getTopazID"
            }, 0L, exception.getMessage());
        }
        return ssstringreturnvalue;
    }

    /**
     * 
     * 
     * @param s
     * @param s1
     * @param hashmap
     * @throws COM.dragonflow.SiteViewException.SiteViewException
     */
    public void addBrowsableCounters(String s, String s1, java.util.HashMap hashmap)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try {       
        AtomicMonitor atomicmonitor;
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s1);
        MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s1);
        atomicmonitor = null;
        Enumeration enumeration = monitorgroup.getMonitors();
        while (enumeration.hasMoreElements())
            {
            Monitor monitor = (Monitor)enumeration.nextElement();
            String s2 = monitor.getProperty("_id");
            if((s2 != null) & s2.equals(s))
            {
            atomicmonitor = (AtomicMonitor)monitor;
            break;
            }
        } 
        
        String s9;
        String s3;
        int i;
        jgl.HashMap hashmap1;
        java.util.Iterator iterator;
        if(atomicmonitor instanceof BrowsableMonitor) {
        if(atomicmonitor != null) {
        s9 = ((BrowsableMonitor)atomicmonitor).getBrowseName();
        s3 = ((BrowsableMonitor)atomicmonitor).getBrowseID();
        i = s3.length();
        hashmap1 = new HashMap();
        java.util.Set set = hashmap.keySet();
        iterator = set.iterator();
        }
        else {
        throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
            s1 + "/" + s
        });
        }
        }
        else {
        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_BROWSABLE_TYPE_REQUIRED, new String[] {
            s1 + "/" + s
        });
        }

        while (iterator.hasNext())
        {
            String s4 = (String)iterator.next();
            String s6 = (String)hashmap.get(s4);
            jgl.HashMap hashmap2 = MasterConfig.getMasterConfig();
            int k = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_browsableContentMaxCounters"));
            if(k == 0)
            {
                k = 10;
            }
            for(int l = 0; l < k; l++)
            {
                if(atomicmonitor.getProperty(s3 + l) != null && atomicmonitor.getProperty(s3 + l).equals(s6))
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_COUNTER_ALREADY_EXISTS, new String[] {
                        s6
                    });
                }
            }

            int i1 = 1;
            while (true)
            {
                if(s4.startsWith(s3))
                {
                    if(hashmap1.get(s3 + i1) == null && (atomicmonitor.getProperty(s3 + i1) == null || atomicmonitor.getProperty(s3 + i1).length() == 0) && hashmap1.get(s9 + i1) == null && (atomicmonitor.getProperty(s9 + i1) == null || atomicmonitor.getProperty(s9 + i1).length() == 0))
                    {
                        hashmap1.put(s3 + i1, hashmap.get(s4));
                        String s7 = s9 + s4.substring(i);
                        if(hashmap.get(s7) != null && ((String)hashmap.get(s7)).length() > 0)
                        {
                            hashmap1.put(s9 + i1, hashmap.get(s7));
                        } else
                        {
                            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_COUNTER_ID);
                        }
                        break; 
                    }
                } else
                {
                    if(!s4.startsWith(s9))
                    {
                        throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_COUNTER_PROPERTY_NOT_VALID);
                    }
                    break; 
                }
                i1++;
            } 
        }
        
        String s5 = atomicmonitor.getClass().toString();
        int j = s5.lastIndexOf(".");
        if(j != -1)
        {
            s5 = s5.substring(j + 1);
        }
        jgl.Array array = ReadGroupFrames(s1);
        AtomicMonitor atomicmonitor1 = AtomicMonitor.MonitorCreate(array, s, "");
        setMonitorProperties(OP_EDIT, atomicmonitor1, s, s1, hashmap1);
        validateProperties(hashmap1, atomicmonitor1, s5, APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
        writeMonitor(OP_EDIT, atomicmonitor1, s, s1);
        DetectConfigurationChange dcc;
        dcc = DetectConfigurationChange.getInstance();
        dcc.setConfigChangeFlag();
        }
        catch (SiteViewException e) {
        e.fillInStackTrace();
        throw e;
        }
        catch (Exception e) {
        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
            "APIMonitor", "addBrowsableCounters"
        }, 0L, e.getMessage());
        }

        return;
    }

    public void removeBrowsableCounters(String s, String s1, java.util.HashMap hashmap)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try
        {
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s1);
            MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s1);
            AtomicMonitor atomicmonitor = null;
            Enumeration enumeration = monitorgroup.getMonitors();
            while (enumeration.hasMoreElements()) {
                Monitor monitor = (Monitor)enumeration.nextElement();
                String s3 = monitor.getProperty("_id");
                if((s3 != null) & s3.equals(s))
                {
                atomicmonitor = (AtomicMonitor)monitor;
                break;
                }
            } 
            if(atomicmonitor instanceof BrowsableMonitor)
            {
                if(atomicmonitor != null)
                {
                    String s2 = ((BrowsableMonitor)atomicmonitor).getBrowseName();
                    String s4 = ((BrowsableMonitor)atomicmonitor).getBrowseID();
                    jgl.HashMap hashmap1 = new HashMap();
                    java.util.Set set = hashmap.keySet();
                    for(java.util.Iterator iterator = set.iterator(); iterator.hasNext();)
                    {
                        String s5 = (String)iterator.next();
                        String s7 = (String)hashmap.get(s5);
                        jgl.HashMap hashmap2 = MasterConfig.getMasterConfig();
                        int j = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_browsableContentMaxCounters"));
                        if(j == 0)
                        {
                            j = 10;
                        }
                        boolean flag = false;
                        int k = 0;
                        do
                        {
                            if(k >= j)
                            {
                                break;
                            }
                            if(atomicmonitor.getProperty(s4 + k) != null && atomicmonitor.getProperty(s4 + k).endsWith(s7))
                            {
                                flag = true;
                                hashmap1.put(s4 + k, "");
                                hashmap1.put(s2 + k, "");
                                break;
                            }
                            k++;
                        } while(true);
                        if(!flag)
                        {
                            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_COUNTER_DOES_NOT_EXIST, new String[] {
                                s7
                            });
                        }
                    }

                    String s6 = atomicmonitor.getClass().toString();
                    int i = s6.lastIndexOf(".");
                    if(i != -1)
                    {
                        s6 = s6.substring(i + 1);
                    }
                    jgl.Array array = ReadGroupFrames(s1);
                    AtomicMonitor atomicmonitor1 = AtomicMonitor.MonitorCreate(array, s, "");
                    setMonitorProperties(OP_EDIT, atomicmonitor1, s, s1, hashmap1);
                    validateProperties(hashmap1, atomicmonitor1, s6, APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
                    writeMonitor(OP_EDIT, atomicmonitor1, s, s1);
                } else
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                        s1 + "/" + s
                    });
                }
            } else
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_BROWSABLE_TYPE_REQUIRED, new String[] {
                    s1 + "/" + s
                });
            }
            DetectConfigurationChange detectconfigurationchange = DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "removeBrowsableCounters"
            }, 0L, exception.getMessage());
        }
    }

    public SSStringReturnValue getClassAttribute(String s, String s1)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSStringReturnValue ssstringreturnvalue = null;
        try
        {
            jgl.HashMap hashmap = getClassAttribs(s);
            ssstringreturnvalue = new SSStringReturnValue((String)hashmap.get(s1));
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                exception.getMessage()
            });
        }
        return ssstringreturnvalue;
    }

    private Enumeration getFilteredMonitorProperties(AtomicMonitor atomicmonitor, Vector vector, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        java.util.ArrayList arraylist = new ArrayList();
        Enumeration enumeration = null;
        try
        {
            String monitorType = atomicmonitor.getClass().getName();
            StringProperty pClass = new StringProperty("_class");
            pClass.setValue(monitorType);
            arraylist.add(pClass);
            
            if(monitorType.indexOf(".") != -1)
            {
                monitorType = monitorType.substring(monitorType.lastIndexOf(".") + 1);
            }
            if(i == FILTER_ALL)
            {
                jgl.Array array = atomicmonitor.getProperties();
                enumeration = array.elements();
            } else
            if(i == FILTER_CONFIGURATION_ADD_ALL)
            {
                enumeration = atomicmonitor.getConfigurationAddProperties(vector, false, 0);
            } else
            if(i == FILTER_CONFIGURATION_ADD_BASIC)
            {
                enumeration = atomicmonitor.getConfigurationAddProperties(vector, false, 1);
            } else
            if(i == FILTER_CONFIGURATION_ADD_ADVANCED)
            {
                enumeration = atomicmonitor.getConfigurationAddProperties(vector, false, 2);
            } else
            if(i == FILTER_CONFIGURATION_EDIT_ALL)
            {
                enumeration = atomicmonitor.getConfigurationEditProperties(vector, false, 0);
            } else
            if(i == FILTER_CONFIGURATION_EDIT_BASIC)
            {
                enumeration = atomicmonitor.getConfigurationEditProperties(vector, false, 1);
            } else
            if(i == FILTER_CONFIGURATION_EDIT_ADVANCED)
            {
                enumeration = atomicmonitor.getConfigurationEditProperties(vector, false, 2);
            } else
            if(i == FILTER_CONFIGURATION_ALL)
            {
                enumeration = atomicmonitor.getConfigurationAllProperties(vector, false);
            } else
            if(i == FILTER_CONFIGURATION_ADD_REQUIRED)
            {
                enumeration = atomicmonitor.getConfigurationRequiredProperties(monitorType);
            } else
            if(i == FILTER_RUNTIME_ALL)
            {
                enumeration = atomicmonitor.getRuntimeProperties(vector, false);
            } else
            if(i == FILTER_RUNTIME_MEASUREMENTS)
            {
                enumeration = atomicmonitor.getMeasurementProperties(vector, false);
            } else
            if(i == FILTER_CONFIGURATION_ADD_ALL_NOT_EMPTY)
            {
                enumeration = atomicmonitor.getConfigurationAddProperties(vector, true, 0);
            } else
            if(i == FILTER_CONFIGURATION_ADD_BASIC_NOT_EMPTY)
            {
                enumeration = atomicmonitor.getConfigurationAddProperties(vector, true, 1);
            } else
            if(i == FILTER_CONFIGURATION_ADD_ADVANCED_NOT_EMPTY)
            {
                enumeration = atomicmonitor.getConfigurationAddProperties(vector, true, 2);
            } else
            if(i == FILTER_CONFIGURATION_EDIT_ALL_NOT_EMPTY)
            {
                enumeration = atomicmonitor.getConfigurationEditProperties(vector, true, 0);
            } else
            if(i == FILTER_CONFIGURATION_EDIT_BASIC_NOT_EMPTY)
            {
                enumeration = atomicmonitor.getConfigurationEditProperties(vector, true, 1);
            } else
            if(i == FILTER_CONFIGURATION_EDIT_ADVANCED_NOT_EMPTY)
            {
                enumeration = atomicmonitor.getConfigurationEditProperties(vector, true, 2);
            } else
            if(i == FILTER_CONFIGURATION_ALL_NOT_EMPTY)
            {
                enumeration = atomicmonitor.getConfigurationAllProperties(vector, true);
            } else
            if(i == PREREQ_OP)
            {
                Object obj = null;
                jgl.Array array1 = new Array();
                jgl.Array array2 = atomicmonitor.getProperties();
                for(int j = 0; j < array2.size(); j++)
                {
                    SSPropertyDetails sspropertydetails = getClassPropertyDetails(((StringProperty)array2.at(j)).getName(), monitorType, new SSInstanceProperty[0]);
                    if(sspropertydetails.isPrerequisite())
                    {
                        array1.add(array2.at(j));
                    }
                }

                enumeration = array1.elements();
            } else
            if(!$assertionsDisabled)
            {
                throw new AssertionError();
            }
            StringProperty stringproperty;
            for(; enumeration.hasMoreElements(); arraylist.add(stringproperty))
            {
                stringproperty = (StringProperty)enumeration.nextElement();
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getFilteredMonitorProperties", exception.getMessage()
            });
        }
        java.util.Collections.sort(arraylist, new OrderComparitor());
        return java.util.Collections.enumeration(arraylist);
    }

//    private SSStringReturnValue getTopazID(AtomicMonitor atomicmonitor)
//        throws COM.dragonflow.SiteViewException.SiteViewException
//    {
//        SSStringReturnValue ssstringreturnvalue = null;
//        try
//        {
//            COM.dragonflow.TopazIntegration.TopazServerSettings topazserversettings = COM.dragonflow.TopazIntegration.TopazManager.getInstance().getTopazServerSettings();
//            if(topazserversettings.isConnected())
//            {
//                ssstringreturnvalue = new SSStringReturnValue((new Integer(COM.dragonflow.TopazIntegration.TopazConfigManager.getInstance().getMonitor(atomicmonitor.getUniqueInternalId()).getTopazId())).toString());
//            } else
//            {
//                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_TOPAZ_NOT_CONFIGURED);
//            }
//        }
//        catch(java.lang.Exception exception)
//        {
//            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
//                "APIMonitor", "getTopazID", exception.getMessage()
//            });
//        }
//        return ssstringreturnvalue;
//    }

    private SSInstanceProperty[] getPropertiesForMonitorInstance(AtomicMonitor atomicmonitor, String monitorid, int filter)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSInstanceProperty assinstanceproperty[] = null;
        try
        {
            SSInstanceProperty assinstanceproperty1[] = null;
            jgl.Array array = new Array();
            Vector vector = new Vector();
            String s1 = null;
            Vector vector1 = new Vector();
            boolean flag = true;
            Object obj = null;
            Enumeration enumeration = null;
            if(monitorid != null && monitorid.length() > 0)
            {
                jgl.Array array1 = new Array();
                StringProperty stringproperty = atomicmonitor.getPropertyObject(monitorid);
                if(stringproperty != null)
                {
                    array1.add(stringproperty);
                    enumeration = array1.elements();
                }
            } else
            {
                enumeration = getFilteredMonitorProperties(atomicmonitor, new Vector(), filter);
                enumeration = adjustVirtualProperties(enumeration);
            }
            while (enumeration.hasMoreElements()) {
                boolean flag1 = false;
                boolean flag2 = false;
                boolean flag3 = false;
                StringProperty stringproperty2 = (StringProperty)enumeration.nextElement();
                String s2 = stringproperty2.getName();
                if(s2 != null)
                {
                    String s3 = "";
                    if(s2.equals("_port"))
                    {
                        flag2 = true;
                        s1 = "portOther";
                    } else
                    if(s2.equals("_oid"))
                    {
                        flag2 = true;
                        s1 = "oidOther";
                    } else
                    if(s2.equals("_percentageBase"))
                    {
                        flag2 = true;
                        s1 = "percentageBaseOther";
                    } else
                    if(s2.equals("_scale"))
                    {
                        flag2 = true;
                        s1 = "scaleOther";
                    } else
                    if(s2.equals("_service"))
                    {
                        flag2 = true;
                        s1 = "serviceOther";
                    } else
                    if(s2.equals("_notLogToTopaz") || s2.equals("_logOnlyMonitorData") || s2.equals("_logOnlyThresholdMeas") || s2.equals("_onlyStatusChanges"))
                    {
                        s3 = atomicmonitor.getProperty(stringproperty2);
                        if(s3 != null && s3.length() > 0)
                        {
                            flag3 = true;
                            flag = false;
                        }
                    }
                    if(flag2)
                    {
                        HTTPRequest httprequest = new HTTPRequest();
                        java.lang.Class class1 = null;
                        class1 = java.lang.Class.forName("COM.dragonflow.Page.monitorPage");
                        CGI cgi = (CGI)class1.newInstance();
                        cgi.initialize(httprequest, null);
                        if(stringproperty2 instanceof ScalarProperty)
                        {
                            Vector vector3 = atomicmonitor.getScalarValues((ScalarProperty)stringproperty2, httprequest, cgi);
                            String as1[] = new String[vector3.size() / 2];
                            String as2[] = new String[vector3.size() / 2];
                            int i2 = 0;
                            int k2 = 0;
                            while (i2 < vector3.size() / 2)
                                {
                                as2[i2] = (String)vector3.elementAt(k2);
                                as1[i2] = (String)vector3.elementAt(k2 + 1);
                                if(as2[i2].equals(s3))
                                {
                                    flag1 = true;
                                    break;
                                }
                                i2++;
                                k2 += 2;
                            } 
                            if(!flag1)
                            {
                                vector.add(new SSInstanceProperty(s1, s3));
                            } else
                            {
                                vector.add(new SSInstanceProperty(s1, ""));
                            }
                        }
                        array.add(stringproperty2);
                    } else
                    if(flag3)
                    {
                        if(s3 != null && s3.length() > 0)
                        {
                            StringProperty stringproperty4 = new StringProperty("topazLogging", s2);
                            array.add(stringproperty4);
                        }
                    } else
                    {
                        array.add(stringproperty2);
                    }
                }
            } 
            if(flag)
            {
                StringProperty stringproperty1 = new StringProperty("topazLogging", "logEverything");
                array.add(stringproperty1);
            }
            assinstanceproperty1 = processInstanceThresholdProperties(filter, atomicmonitor);
            Vector vector2 = new Vector();
            fixDisableAlertingParamsOut(atomicmonitor, vector2);
            fixDisableGroupOrMonitorParamsOut(atomicmonitor, vector2);
            int j = 0;
            SSStringReturnValue ssstringreturnvalue = null;
            try
            {
                if(filter == APISiteView.FILTER_CONFIGURATION_ALL || filter == APISiteView.FILTER_ALL)
                {
                    j = 1;
                }
            }
            catch(java.lang.Exception exception1)
            {
                j = 0;
            }
            assinstanceproperty = new SSInstanceProperty[j + array.size() + assinstanceproperty1.length + vector.size() + vector1.size() + vector2.size()];
            int k = 0;
            for(Enumeration enumeration1 = array.elements(); enumeration1.hasMoreElements();)
            {
                StringProperty stringproperty3 = (StringProperty)enumeration1.nextElement();
                String s4 = GetPropertyLabel(stringproperty3, true);
                java.lang.Object obj1;
                if(stringproperty3.isMultiLine)
                {
                    Enumeration enumeration2 = atomicmonitor.getMultipleValues(stringproperty3);
                    String s7 = "";
                    if(enumeration2.hasMoreElements())
                    {
                        while(enumeration2.hasMoreElements()) 
                        {
                            s7 = s7 + (String)enumeration2.nextElement() + "\r\n";
                        }
                    } else
                    {
                        String s8 = atomicmonitor.getProperty(stringproperty3);
                        String as3[] = COM.dragonflow.Utils.TextUtils.split(s8, ",");
                        for(int j2 = 0; j2 < as3.length; j2++)
                        {
                            s7 = s7 + as3[j2] + "\r\n";
                        }
                    }
                    obj1 = s7;
                } else
                if((stringproperty3 instanceof ScalarProperty) && ((ScalarProperty)stringproperty3).multiple)
                {
                    String s5 = atomicmonitor.getProperty(stringproperty3);
                    String as[] = COM.dragonflow.Utils.TextUtils.split(s5, ",");
                    for(int l1 = 0; l1 < as.length; l1++)
                    {
                        as[l1] = as[l1].trim();
                    }

                    obj1 = as;
                } else
                {
                    String s6 = atomicmonitor.getProperty(stringproperty3);
                    if(stringproperty3.isPassword)
                    {
                        s6 = COM.dragonflow.Utils.TextUtils.obscure(s6);
                    }
                    obj1 = s6;
                }
                if(stringproperty3.getName().equals("_perfmonMsmtsProp"))
                {
                    obj1 = monitorUtils.transformMgFormatToPerfmonMeasurements((String)obj1);
                }
                assinstanceproperty[k] = new SSInstanceProperty(stringproperty3.getName(), s4, obj1);
                k++;
            }

            for(int l = 0; l < vector.size(); l++)
            {
                SSInstanceProperty ssinstanceproperty = (SSInstanceProperty)vector.elementAt(l);
                assinstanceproperty[k + l] = ssinstanceproperty;
                if(ssinstanceproperty.getValue() != "")
                {
                    setTheCorrespondingSiteViewVarToZero(ssinstanceproperty, assinstanceproperty);
                }
            }

            k += vector.size();
            for(int i1 = 0; i1 < vector2.size(); i1++)
            {
                assinstanceproperty[k++] = (SSInstanceProperty)vector2.elementAt(i1);
            }

            for(int j1 = 0; j1 < vector1.size(); j1++)
            {
                assinstanceproperty[k + j1] = (SSInstanceProperty)vector1.elementAt(j1);
            }

            k += vector1.size();
            for(int k1 = 0; k1 < assinstanceproperty1.length; k1++)
            {
                assinstanceproperty[k + k1] = assinstanceproperty1[k1];
            }

            k += assinstanceproperty1.length;
            if(j > 0)
            {
//                assinstanceproperty[k] = new SSInstanceProperty("topazMonitorID", TopazInfo.getTopazName() + " Monitor ID", ssstringreturnvalue.getValue());
            }
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getPropertiesForMonitorInstance", exception.getMessage()
            });
        }
        return assinstanceproperty;
    }

    private void setTheCorrespondingSiteViewVarToZero(SSInstanceProperty ssinstanceproperty, SSInstanceProperty assinstanceproperty[])
    {
        String s = ssinstanceproperty.getName();
        int i = s.indexOf("Other");
        if(i != -1)
        {
            String s1 = "_" + s.substring(0, i);
            int j = 0;
            do
            {
                if(j >= assinstanceproperty.length)
                {
                    break;
                }
                SSInstanceProperty ssinstanceproperty1 = assinstanceproperty[j];
                if(ssinstanceproperty1.getName().equals(s1))
                {
                    assinstanceproperty[j] = new SSInstanceProperty(assinstanceproperty[j].getName(), assinstanceproperty[j].getLabel(), "0");
                    break;
                }
                j++;
            } while(true);
        } else
        {
            COM.dragonflow.Log.LogManager.log("Error", "Error finding the word 'Other' in the otherProperty: " + s + " -- unable to indicate to Flipper that an 'Other' property is in use.");
        }
    }

    private SSInstanceProperty[] processInstanceThresholdProperties(int i, AtomicMonitor atomicmonitor)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSInstanceProperty assinstanceproperty[] = new SSInstanceProperty[0];
        try
        {
            if(i == FILTER_CONFIGURATION_ADD_ALL || i == FILTER_CONFIGURATION_ADD_BASIC || i == FILTER_CONFIGURATION_ADD_ADVANCED || i == FILTER_CONFIGURATION_EDIT_ALL || i == FILTER_CONFIGURATION_EDIT_BASIC || i == FILTER_CONFIGURATION_EDIT_ADVANCED || i == FILTER_ALL || i == FILTER_CONFIGURATION_ALL || i == FILTER_CONFIGURATION_ALL && i == FILTER_CONFIGURATION_ALL_NOT_EMPTY)
            {
                Vector vector = getInstanceThresholds(atomicmonitor, i);
                assinstanceproperty = new SSInstanceProperty[vector.size()];
                for(int j = 0; j < vector.size(); j++)
                {
                    assinstanceproperty[j] = (SSInstanceProperty)vector.elementAt(j);
                }

            }
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "processInstanceThresholdProperties", exception.getMessage()
            });
        }
        return assinstanceproperty;
    }

    private Vector createThresholdProperties(int i, jgl.Array array, SiteViewObject siteviewobject, java.util.HashMap hashmap)
    {
        Vector vector = new Vector();
        if(i == APISiteView.FILTER_CONFIGURATION_ADD_ALL || i == APISiteView.FILTER_CONFIGURATION_ADD_BASIC || i == APISiteView.FILTER_CONFIGURATION_ADD_ADVANCED || i == APISiteView.FILTER_CONFIGURATION_EDIT_ALL || i == APISiteView.FILTER_CONFIGURATION_EDIT_BASIC || i == APISiteView.FILTER_CONFIGURATION_EDIT_ADVANCED || i == APISiteView.FILTER_ALL || i == APISiteView.FILTER_CONFIGURATION_ALL)
        {
            Enumeration enumeration = array.elements();
            jgl.Array array1 = new Array();
            while (enumeration.hasMoreElements()) {
                StringProperty stringproperty = (StringProperty)enumeration.nextElement();
                if(stringproperty.isThreshold())
                {
                    array1.add(stringproperty);
                }
            } 
            if(i != APISiteView.FILTER_ALL)
            {
                for(int j = 0; j < array1.size(); j++)
                {
                    array.remove(array1.at(j));
                }

            }
            vector = getThresholds(array1, siteviewobject, hashmap);
        }
        return vector;
    }

    protected Vector getThresholds(jgl.Array array, SiteViewObject siteviewobject, java.util.HashMap hashmap)
    {
        int i = getThresholdNum((Monitor)siteviewobject);
        Vector vector = new Vector();
        if(siteviewobject instanceof Monitor)
        {
            jgl.Array array1 = new Array();
            jgl.Array array2 = new Array();
            for(int j = 0; j < array.size(); j++)
            {
                StringProperty stringproperty = (StringProperty)array.at(j);
                if(stringproperty != Monitor.pNumStdDev && stringproperty != Monitor.pNumPercent || siteviewobject.hasValue(Monitor.pBaselineDate))
                {
                    array1.add(stringproperty.getName());
                    array2.add(stringproperty.getLabel());
                }
            }

            getThresholdCounters(hashmap, array1, i);
            int k = getThresholdNum((Monitor)siteviewobject);
            String as[] = {
                "error", "warning", "good"
            };
            String as1[] = {
                "condition", "comparison", "parameter"
            };
            for(int l = 0; l < as.length; l++)
            {
                for(int i1 = 0; i1 < k; i1++)
                {
                    String s = "";
                    String s2 = "";
                    String s3 = "";
                    for(int j1 = 0; j1 < as1.length; j1++)
                    {
                        String s4 = as1[j1];
                        String s10 = "THRESHOLD";
                        String s5 = as[l] + "-" + s4 + i1;
                        String s6 = as[l] + " " + s4;
                        String s7 = as[l] + " threshold " + s4;
                        String s8 = "SCALAR";
                        String s11 = "";
                        String as2[];
                        String as3[];
                        String s9;
                        if(s4.equals("condition"))
                        {
                            String as4[] = processClassifiers((Monitor)siteviewobject, as[l]);
                            if(as4[i1] == null)
                            {
                                as4[i1] = "";
                            }
                            as2 = new String[array1.size()];
                            as3 = new String[array2.size()];
                            String s12 = as4[i1];
                            if(s12.indexOf(" ") > 0)
                            {
                                int k1 = s12.indexOf(" ");
                                int i2 = s12.indexOf(" ", k1 + 1);
                                String s1 = s12.substring(0, k1);
                                s2 = s12.substring(k1 + 1, i2);
                                s3 = s12.substring(i2 + 1);
                                s9 = s1;
                            } else
                            {
                                s9 = s12;
                            }
                            for(int l1 = 0; l1 < array1.size(); l1++)
                            {
                                if(array1.at(l1) != null)
                                {
                                    as2[l1] = (String)array1.at(l1);
                                    as3[l1] = (String)array2.at(l1);
                                }
                            }

                            s6 = as[l] + " if:";
                        } else
                        if(s4.equals("comparison"))
                        {
                            as2 = createComparisonList();
                            as3 = createComparisonList();
                            s9 = s2;
                        } else
                        {
                            as2 = null;
                            as3 = null;
                            s8 = "TEXT";
                            s9 = s3;
                        }
                        vector.add(new SSPropertyDetails(s5, s8, s7, s6, true, false, s9, as2, as3, s10, false, false, 0, s11, true, false, siteviewobject.getProperty(s5)));
                    }

                }

            }

        }
        return vector;
    }

    private String[] createComparisonList()
    {
        String as[] = new String[8];
        as[0] = ">=";
        as[1] = ">";
        as[2] = "==";
        as[3] = "!=";
        as[4] = "<";
        as[5] = "<=";
        as[6] = "contains";
        as[7] = "!contains";
        return as;
    }

    private void getThresholdCounters(java.util.HashMap hashmap, jgl.Array array, int i)
    {
        if(hashmap.containsKey("_counters") || hashmap.containsKey("_counter"))
        {
            int j = 0;
            int l;
            if(array.contains("value0"))
            {
                l = j;
            } else
            {
                l = j + 1;
            }
            String s = (String)hashmap.get("_counters");
            if(s == null)
            {
                s = (String)hashmap.get("_counter");
            }
            if(s != null)
            {
                int j1 = 0;
                int k1 = s.indexOf(",");
                do
                {
                    if(j >= i)
                    {
                        break;
                    }
                    if(k1 >= 0)
                    {
                        array.replace("value" + l, s.substring(j1, k1));
                    } else
                    {
                        array.replace("value" + l, s.substring(j1));
                        break;
                    }
                    j1 = k1 + 1;
                    k1 = s.indexOf(",", j1);
                    l++;
                    j++;
                } while(true);
            }
        } else
        if(hashmap.containsKey("_browseName1"))
        {
            for(int k = 0; k < i; k++)
            {
                int i1 = k + 1;
                String s1 = (String)hashmap.get("_browseName" + i1);
                if(s1 != null)
                {
                    array.replace("value" + i1, s1);
                }
            }

        }
    }

    private SSPropertyDetails getThreshold(String s, jgl.Array array, Monitor monitor)
    {
        int i = getThresholdNum(monitor);
        String s2 = s.substring(0, s.indexOf("-"));
        String as[] = new String[0];
        String as1[] = new String[0];
        String s6 = "";
        String s7 = "THRESHOLD";
        String s8 = "";
        String as2[] = processClassifiers(monitor, s2);
        for(int j = 0; j < as2.length; j++)
        {
            if(as2[j] == null)
            {
                as2[j] = "";
            }
        }

        String s9 = as2[0];
        String s10 = "";
        String s12 = "";
        if(s9.indexOf(" ") > 0)
        {
            int k = s9.indexOf(" ");
            int i1 = s9.indexOf(" ", k + 1);
            String s11 = s9.substring(0, k);
            s12 = s9.substring(k + 1, i1);
            s6 = s11;
        } else
        {
            s6 = s9;
        }
        String s5 = "SCALAR";
        String s1;
        if(s.indexOf("condition") >= 0)
        {
            s1 = "condition";
            Vector vector = new Vector();
            Vector vector1 = new Vector();
            vector.add(s9 + " (default)");
            vector1.add("default");
            for(int k1 = 0; k1 < array.size(); k1++)
            {
                StringProperty stringproperty = (StringProperty)array.at(k1);
                String s13 = monitor.GetPropertyLabel(stringproperty, true);
                String s14 = stringproperty.getName();
                if(s13 != null && s13.length() > 0)
                {
                    vector.add(s13);
                    vector1.add(s14);
                }
            }

            as = new String[vector.size()];
            for(int l1 = 0; l1 < vector.size(); l1++)
            {
                as[l1] = (String)vector.elementAt(l1);
            }

            as1 = new String[vector.size()];
            for(int i2 = 0; i2 < vector1.size(); i2++)
            {
                as1[i2] = (String)vector1.elementAt(i2);
            }

            s5 = "TEXT";
        } else
        if(s.indexOf("comparison") >= 0)
        {
            s1 = "comparison";
            as = createComparisonList();
            s6 = s12;
        } else
        {
            s1 = "parameter";
            as = null;
            as1 = null;
        }
        String s3 = s2 + " " + s1;
        String s4 = s2 + " threshold " + s1;
        int l = s.indexOf(s1) + s1.length();
        int j1 = (new Integer(s.substring(l))).intValue();
        SSPropertyDetails sspropertydetails;
        if(j1 < i)
        {
            sspropertydetails = new SSPropertyDetails(s, s5, s4, s3, true, false, s6, as, as1, s7, false, false, 0, s8, true, false, monitor.getProperty(s));
        } else
        {
            sspropertydetails = null;
        }
        return sspropertydetails;
    }

    private String[] processClassifiers(Monitor monitor, String s)
    {
        Enumeration enumeration = monitor.getClassifiers();
        int i = getThresholdNum(monitor);
        String as[] = new String[i];
        for(int j = 0; j < i; j++)
        {
            as[j] = null;
        }

        String s1 = "SetProperty category " + s;
        int k = 0;
        while (enumeration.hasMoreElements()) {
            Rule rule = (Rule)enumeration.nextElement();
            if(k < i && s1.equals(rule.getProperty(Rule.pAction)))
            {
                if(rule.getOwner() == monitor)
                {
                    as[k] = rule.getProperty(Rule.pExpression);
                }
                k++;
            }
        }
        String as1[] = new String[i];
        as1[0] = "default";
        return as[0] != null ? as : as1;
    }

    protected Vector getInstanceThresholds(Monitor monitor, int i)
    {
        Vector vector = new Vector();
        int j = getThresholdNum(monitor);
        java.util.HashMap hashmap = new java.util.HashMap();
        Enumeration enumeration = monitor.getProperties().elements();
        while (enumeration.hasMoreElements()) {
            StringProperty stringproperty = (StringProperty)enumeration.nextElement();
            if(stringproperty.isThreshold())
            {
                String s = monitor.GetPropertyLabel(stringproperty, true);
                if(s.length() != 0)
                {
                    hashmap.put(stringproperty.getName(), monitor.getProperty(stringproperty));
                }
            }
        } 
        String as[] = {
            "error", "warning", "good"
        };
        for(int k = 0; k < as.length; k++)
        {
            String as1[] = processClassifiers(monitor, as[k]);
            for(int l = 0; l < j; l++)
            {
                String as2[] = new String[0];
                if(as1[l] != null)
                {
                    as2 = COM.dragonflow.Utils.TextUtils.tokenize(as1[l]);
                }
                String s1 = as[k] + "-condition" + l;
                String s2 = "";
                if(as2.length > 0)
                {
                    String s3 = as2[0];
                    s2 = s3;
                } else
                if(i == APISiteView.FILTER_CONFIGURATION_ALL_NOT_EMPTY)
                {
                    continue;
                }
                String s4 = as[k] + "-comparison" + l;
                String s5 = "";
                if(as2.length > 1)
                {
                    s5 = as2[1];
                }
                String s6 = as[k] + "-parameter" + l;
                String s7 = "";
                if(as2.length > 2)
                {
                    s7 = as2[2];
                }
                vector.add(new SSInstanceProperty(s1, s2));
                vector.add(new SSInstanceProperty(s4, s5));
                vector.add(new SSInstanceProperty(s6, s7));
            }

        }

        return vector;
    }

    private void validateCustomProperties(jgl.HashMap hashmap, AtomicMonitor atomicmonitor, jgl.HashMap hashmap1)
    {
        jgl.HashMap hashmap2 = MasterConfig.getMasterConfig();
        Enumeration enumeration = hashmap2.values("_monitorEditCustom");
        while (enumeration.hasMoreElements()) {
            String s = "";
            String s1 = (String)enumeration.nextElement();
            String as[] = COM.dragonflow.Utils.TextUtils.split(s1, "|");
            String s2 = as[0];
            if(s2.length() > 0)
            {
                if(!s2.startsWith("_"))
                {
                    s2 = "_" + s2;
                }
                if(!shouldPrintProperty(atomicmonitor, s2) || hashmap.get(s2) == null)
                {
                    continue;
                }
                String s3 = "";
                if(as.length > 1 && as.length > 3)
                {
                    s3 = as[3];
                }
                if(s3.equals("") || !atomicmonitor.getProperty(s2).equals(""))
                {
                    s = (String)hashmap.get(s2);
                } else
                {
                    s = s3;
                }
            }
            hashmap.remove(s2);
            hashmap1.add(s2, s);
        } 
    }

    private jgl.HashMap validateProperties(jgl.HashMap hashmap, AtomicMonitor atomicmonitor, String s, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        jgl.HashMap hashmap1 = new HashMap(true);
        try
        {
            Enumeration enumeration = getFilteredMonitorProperties(atomicmonitor, new Vector(), i);
            jgl.HashMap hashmap2 = new HashMap();
            COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
            if(enumeration != null)
            {
                hashmap1.put("_class", s);
                if(hashmap.get("_class") != null)
                {
                    hashmap.remove("_class");
                }
                hashmap.remove("_logOnlyMonitorData");
                hashmap.remove("_logOnlyThresholdMeas");
                hashmap.remove("_onlyStatusChanges");
                hashmap.remove(Monitor.pDisabledDescription.getName());
                hashmap.remove(Monitor.pDisabled.getName());
                hashmap.remove(Monitor.pTimedDisable.getName());
                hashmap.remove(Monitor.pAlertDisabled.getName());
                while (enumeration.hasMoreElements()) {
                    StringProperty stringproperty = (StringProperty)enumeration.nextElement();
                    String s1 = stringproperty.getName();
                    Enumeration enumeration2 = hashmap.values(s1);
                    hashmap.remove(s1);
                    if(!stringproperty.isThreshold())
                    {
                        while(enumeration2.hasMoreElements()) 
                        {
                            java.lang.Object obj = enumeration2.nextElement();
                            if(obj == null || obj.equals(""))
                            {
                                obj = stringproperty.getDefault();
                            }
                            if(obj != null && (stringproperty instanceof COM.dragonflow.Properties.BooleanProperty) && obj.equals("0"))
                            {
                                obj = "";
                            }
                            if(obj instanceof String)
                            {
                                obj = atomicmonitor.verify(stringproperty, (String)obj, httprequest, hashmap2);
                            }
                            hashmap1.add(s1, obj);
                        }
                    }
                }
                validateCustomProperties(hashmap, atomicmonitor, hashmap1);
                if(hashmap2.size() == 0)
                {
                    atomicmonitor.verifyAll(hashmap2);
                }
            }
            if(hashmap2.size() > 0)
            {
                java.util.HashMap hashmap3 = new java.util.HashMap();
                StringProperty stringproperty1;
                for(Enumeration enumeration1 = hashmap2.keys(); enumeration1.hasMoreElements(); hashmap3.put(stringproperty1.getName(), hashmap2.get(stringproperty1)))
                {
                    stringproperty1 = (StringProperty)enumeration1.nextElement();
                }

                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_VERIFICATION_ERRORS, hashmap3);
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                exception.getMessage()
            });
        }
        return hashmap1;
    }

    private jgl.HashMap getClassAttribs(String s)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        AtomicMonitor atomicmonitor = instantiateMonitor(s);
        if(APIMonitor.isValidObject(atomicmonitor.getClass().getName(), "Monitor"))
        {
            return atomicmonitor.getClassProperties();
        } else
        {
            return null;
        }
    }

    private SSPropertyDetails getClassProperty(StringProperty stringproperty, AtomicMonitor atomicmonitor, java.util.HashMap hashmap, boolean flag)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        boolean flag1 = false;
        String s = "";
        boolean flag2 = false;
        String s3 = "";
        String s4 = "TEXT";
        String as[] = null;
        String as1[] = null;
        String s5 = "";
        try
        {
            if(atomicmonitor instanceof BrowsableMonitor)
            {
                jgl.Array array = ((BrowsableMonitor)atomicmonitor).getConnectionProperties();
                for(int i = 0; i < array.size(); i++)
                {
                    String s1 = ((StringProperty)array.at(i)).getName();
                    atomicmonitor.unsetProperty(s1);
                    if(hashmap.get(s1) == null)
                    {
                        continue;
                    }
                    String s8 = (String)hashmap.get(s1);
                    StringProperty stringproperty1 = atomicmonitor.getPropertyObject(s1);
                    if((stringproperty1 instanceof COM.dragonflow.Properties.BooleanProperty) && s8.equals("0"))
                    {
                        s8 = "";
                    }
                    atomicmonitor.setProperty(s1, s8);
                }

                if(hashmap.get("reloadCounters") != null)
                {
                    flag2 = (new Boolean((String)hashmap.get("reloadCounters"))).booleanValue();
                }
            }
            if((atomicmonitor instanceof IServerPropMonitor) && stringproperty.getName() != null)
            {
                if((atomicmonitor instanceof NTCounterBase) && stringproperty.getName() != null && stringproperty.getName().equals("counterObject") && hashmap.get("counterObject") == null)
                {
                    flag1 = true;
                }
                if(stringproperty.getName().equals("_machine") && hashmap.get("_machine") == null)
                {
                    flag1 = true;
                }
                if(stringproperty.getName().equals("_pdhMachine") && hashmap.get("_pdhMachine") == null)
                {
                    flag1 = true;
                }
                if(stringproperty.getName().equals("_server") && hashmap.get("_server") == null)
                {
                    flag1 = true;
                }
            } else
            if(((atomicmonitor instanceof BrowsableMonitor) || (atomicmonitor instanceof BrowsableSNMPBase)) && stringproperty.getName() != null)
            {
                jgl.Array array1 = ((BrowsableMonitor)atomicmonitor).getConnectionProperties();
                int j = 0;
                do
                {
                    if(j >= array1.size())
                    {
                        break;
                    }
                    String s2 = ((StringProperty)array1.at(j)).getName();
                    if(stringproperty.getName().equals(s2) && hashmap.get(s2) == null)
                    {
                        flag1 = true;
                        break;
                    }
                    j++;
                } while(true);
            }
            COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
            httprequest.setUser(account);
            if(stringproperty.isPassword)
            {
                s4 = "PASSWORD";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ServerProperty)
            {
                s4 = "SERVER";
                Vector vector = returnMachineScalarValues(atomicmonitor);
                as = new String[vector.size() / 2];
                as1 = new String[vector.size() / 2];
                int k = 0;
                for(int l = 0; k < vector.size(); l++)
                {
                    as1[l] = (String)vector.elementAt(k);
                    as[l] = (String)vector.elementAt(k + 1);
                    k += 2;
                }

                s5 = "LIST";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScheduleProperty)
            {
                s4 = "SCHEDULE";
                COM.dragonflow.Properties.ScheduleProperty scheduleproperty = (COM.dragonflow.Properties.ScheduleProperty)stringproperty;
                java.lang.Class class1 = java.lang.Class.forName("COM.dragonflow.Page.monitorPage");
                COM.dragonflow.Page.CGI cgi = (COM.dragonflow.Page.CGI)class1.newInstance();
                cgi.initialize(httprequest, null);
                Vector vector1 = atomicmonitor.getScalarValues(scheduleproperty, httprequest, cgi);
                as = new String[vector1.size() / 2];
                as1 = new String[vector1.size() / 2];
                int i2 = 0;
                for(int k2 = 0; i2 < vector1.size() / 2; k2 += 2)
                {
                    as1[i2] = (String)vector1.elementAt(k2);
                    as[i2] = (String)vector1.elementAt(k2 + 1);
                    i2++;
                }

                s5 = "LIST";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScalarProperty)
            {
                s4 = "SCALAR";
                COM.dragonflow.Properties.ScalarProperty scalarproperty = (COM.dragonflow.Properties.ScalarProperty)stringproperty;
                java.lang.Class class2 = java.lang.Class.forName("COM.dragonflow.Page.monitorPage");
                COM.dragonflow.Page.CGI cgi1 = (COM.dragonflow.Page.CGI)class2.newInstance();
                cgi1.initialize(httprequest, null);
                if(hashmap.get("_machine") != null)
                {
                    jgl.HashMap hashmap3 = COM.dragonflow.Utils.jglUtils.toJgl(hashmap);
                    processMachineName((String)hashmap.get("_machine"), hashmap3);
                    hashmap = COM.dragonflow.Utils.jglUtils.fromJgl(hashmap3);
                }
                if(hashmap.get("_machine") != null)
                {
                    httprequest.setValue("_machine", (String)hashmap.get("_machine"));
                    atomicmonitor.setProperty("_machine", (String)hashmap.get("_machine"));
                } else
                {
                    httprequest.setValue("_machine", "");
                    atomicmonitor.unsetProperty("_machine");
                }
                Vector vector2 = null;
                if(stringproperty.getName().equals("_machine"))
                {
                    vector2 = returnMachineScalarValues(atomicmonitor);
                } else
                {
                    vector2 = atomicmonitor.getScalarValues(scalarproperty, httprequest, cgi1);
                }
                as = new String[vector2.size() / 2];
                as1 = new String[vector2.size() / 2];
                int j2 = 0;
                for(int l2 = 0; j2 < vector2.size() / 2; l2 += 2)
                {
                    as1[j2] = (String)vector2.elementAt(l2);
                    as[j2] = (String)vector2.elementAt(l2 + 1);
                    j2++;
                }

                s5 = "LIST";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.RateProperty)
            {
                s4 = "RATE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.PercentProperty)
            {
                s4 = "PERCENT";
                s3 = ((COM.dragonflow.Properties.PercentProperty)stringproperty).getUnits();
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.FrequencyProperty)
            {
                s4 = "FREQUENCY";
                s3 = "seconds";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.FileProperty)
            {
                s4 = "FILE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.BrowsableProperty)
            {
                s4 = "BROWSABLE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.BooleanProperty)
            {
                s4 = "BOOLEAN";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.NumericProperty)
            {
                s4 = "NUMERIC";
                s3 = ((COM.dragonflow.Properties.NumericProperty)stringproperty).getUnits();
            } else
            if(stringproperty instanceof StringProperty)
            {
                s4 = "TEXT";
                if((atomicmonitor instanceof NTCounterBase) && stringproperty.getName() != null && stringproperty.getName().equals("availableObjects"))
                {
                    String s6 = (String)hashmap.get("_machine");
                    if(s6 != null)
                    {
                        atomicmonitor.setProperty("_machine", s6);
                        jgl.Array array7 = ((NTCounterBase)atomicmonitor).getAvailableCounters();
                        jgl.HashMap hashmap2 = new HashMap();
                        for(int j1 = 0; j1 < array7.size(); j1++)
                        {
                            if(((COM.dragonflow.Utils.PerfCounter)array7.at(j1)).object != null)
                            {
                                hashmap2.put(((COM.dragonflow.Utils.PerfCounter)array7.at(j1)).object, "");
                            }
                        }

                        int k1 = 0;
                        Enumeration enumeration = hashmap2.keys();
                        as = new String[hashmap2.size()];
                        while(enumeration.hasMoreElements()) 
                        {
                            as[k1++] = (String)enumeration.nextElement();
                        }
                    }
                } else
                if((atomicmonitor instanceof NTCounterBase) && stringproperty.getName() != null && stringproperty.getName().equals("availableCounters"))
                {
                    jgl.Array array2 = new Array(0);
                    StringBuffer stringbuffer2 = new StringBuffer();
                    jgl.Array array8 = new Array();
                    String s13 = (String)hashmap.get("_machine");
                    if(s13 != null)
                    {
                        atomicmonitor.setProperty("_machine", s13);
                        String s16 = (String)hashmap.get("counterObject");
                        if(s16 != null)
                        {
                            array8.add(s16);
                            jgl.Array array3 = NTCounterBase.getPerfCounters(s13, array8, stringbuffer2, "");
                            jgl.HashMap hashmap4 = new HashMap();
                            for(int j3 = 0; j3 < array3.size(); j3++)
                            {
                                if(((COM.dragonflow.Utils.PerfCounter)array3.at(j3)).counterName != null)
                                {
                                    hashmap4.put(((COM.dragonflow.Utils.PerfCounter)array3.at(j3)).counterName, "");
                                }
                            }

                            int k3 = 0;
                            Enumeration enumeration1 = hashmap4.keys();
                            as = new String[hashmap4.size()];
                            while(enumeration1.hasMoreElements()) 
                            {
                                as[k3++] = (String)enumeration1.nextElement();
                            }
                            hashmap4 = new HashMap();
                            for(int j5 = 0; j5 < array3.size(); j5++)
                            {
                                if(((COM.dragonflow.Utils.PerfCounter)array3.at(j5)).counterID != null)
                                {
                                    hashmap4.put(((COM.dragonflow.Utils.PerfCounter)array3.at(j5)).counterID, "");
                                }
                            }

                            k3 = 0;
                            enumeration1 = hashmap4.keys();
                            as1 = new String[hashmap4.size()];
                            while(enumeration1.hasMoreElements()) 
                            {
                                as1[k3++] = (String)enumeration1.nextElement();
                            }
                        }
                    }
                } else
                if((atomicmonitor instanceof NTCounterBase) && stringproperty.getName() != null && stringproperty.getName().equals("availableInstances"))
                {
                    jgl.Array array4 = new Array(0);
                    StringBuffer stringbuffer3 = new StringBuffer();
                    jgl.Array array9 = new Array();
                    String s14 = (String)hashmap.get("_machine");
                    if(s14 != null)
                    {
                        atomicmonitor.setProperty("_machine", s14);
                        String s17 = (String)hashmap.get("counterObject");
                        if(s17 != null)
                        {
                            array9.add(s17);
                            jgl.Array array5 = NTCounterBase.getPerfCounters(s14, array9, stringbuffer3, "");
                            jgl.HashMap hashmap5 = new HashMap();
                            for(int l3 = 0; l3 < array5.size(); l3++)
                            {
                                if(((COM.dragonflow.Utils.PerfCounter)array5.at(l3)).instance != null)
                                {
                                    hashmap5.put(((COM.dragonflow.Utils.PerfCounter)array5.at(l3)).instance, "");
                                }
                            }

                            int i4 = 0;
                            Enumeration enumeration2 = hashmap5.keys();
                            as = new String[hashmap5.size()];
                            while(enumeration2.hasMoreElements()) 
                            {
                                as[i4++] = (String)enumeration2.nextElement();
                            }
                        }
                    }
                } else
                if((atomicmonitor instanceof ISelectableCounter) && stringproperty.getName() != null && stringproperty.getName().equals("defaultCounters"))
                {
                    String s7 = ((ISelectableCounter)atomicmonitor).getDefaultCounters();
                    as = COM.dragonflow.Utils.TextUtils.split(s7, ",");
                } else
                if((atomicmonitor instanceof BrowsableMonitor) && stringproperty.getName() != null && stringproperty.getName().equals("availableCounters"))
                {
                    StringBuffer stringbuffer = new StringBuffer("");
                    String s9 = "";
                    boolean flag3 = false;
                    if(((BrowsableMonitor)atomicmonitor).isUsingCountersCache())
                    {
                        if(!flag2)
                        {
                            s9 = BrowsableCache.getXmlFile(BrowsableCache.getXmlFileName(atomicmonitor));
                            if(s9 == null || s9.length() == 0)
                            {
                                flag3 = true;
                            }
//							else
//                            if(atomicmonitor instanceof COM.dragonflow.StandardMonitor.SiebelCmdLineMonitor)
//                            {
//                                s9 = ((COM.dragonflow.StandardMonitor.SiebelCmdLineMonitor)atomicmonitor).postProcessBrowseTree(s9);
//                            }
                        } else
                        {
                            flag3 = true;
                        }
                        if(flag3)
                        {
                            BrowsableCache.deleteXmlFile(BrowsableCache.getXmlFileName(atomicmonitor));
                            s9 = ((BrowsableMonitor)atomicmonitor).getBrowseData(stringbuffer).trim();
                            BrowsableCache.saveXmlFile(BrowsableCache.getXmlFileName(atomicmonitor), s9);
                        }
                    } else
                    {
                        s9 = ((BrowsableMonitor)atomicmonitor).getBrowseData(stringbuffer).trim();
                    }
                    if(stringbuffer.length() == 0)
                    {
                        org.w3c.dom.Document document = createDocumentFromString(s9);
                        Vector vector3 = new Vector();
                        Vector vector4 = new Vector();
                        org.w3c.dom.NodeList nodelist = document.getDocumentElement().getChildNodes();
                        int l4 = nodelist.getLength();
                        for(int k5 = 0; k5 < l4; k5++)
                        {
                            findCounters(atomicmonitor, vector4, vector3, nodelist.item(k5), 1);
                        }

                        as = new String[vector4.size()];
                        for(int l5 = 0; l5 < vector4.size(); l5++)
                        {
                            as[l5] = (String)vector4.elementAt(l5);
                        }

                        as1 = new String[vector3.size()];
                        for(int i6 = 0; i6 < vector3.size(); i6++)
                        {
                            as1[i6] = (String)vector3.elementAt(i6);
                        }

                    } else
                    {
                        if(flag)
                        {
                            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_COUNTER_EXCEPTION, new String[] {
                                stringbuffer.toString()
                            });
                        }
                        as = new String[1];
                        as[0] = "unable to retrieve counters";
                        as1 = new String[1];
                        as1[0] = stringbuffer.toString();
                    }
                } else
                if((atomicmonitor instanceof BrowsableMonitor) && stringproperty.getName() != null && stringproperty.getName().equals("availableCountersHierarchical"))
                {
                    if(atomicmonitor.isDispatcher())
                    {
                        SiteViewGroup.currentSiteView().checkDispatcherForStart(atomicmonitor);
                    }
                    StringBuffer stringbuffer1 = new StringBuffer("");
                    String s10 = "";
                    boolean flag4 = false;
                    if(((BrowsableMonitor)atomicmonitor).isUsingCountersCache())
                    {
                        if(!flag2)
                        {
                            s10 = BrowsableCache.getXmlFile(BrowsableCache.getXmlFileName(atomicmonitor));
                            if(s10 == null || s10.length() == 0)
                            {
                                flag4 = true;
                            }
//							else
//                            if(atomicmonitor instanceof COM.dragonflow.StandardMonitor.SiebelCmdLineMonitor)
//                            {
//                                s10 = ((COM.dragonflow.StandardMonitor.SiebelCmdLineMonitor)atomicmonitor).postProcessBrowseTree(s10);
//                            }
                        } else
                        {
                            flag4 = true;
                        }
                        if(flag4)
                        {
                            BrowsableCache.deleteXmlFile(BrowsableCache.getXmlFileName(atomicmonitor));
                            s10 = ((BrowsableMonitor)atomicmonitor).getBrowseData(stringbuffer1).trim();
                            BrowsableCache.saveXmlFile(BrowsableCache.getXmlFileName(atomicmonitor), s10);
                        }
                    } else
                    {
                        s10 = ((BrowsableMonitor)atomicmonitor).getBrowseData(stringbuffer1).trim();
                    }
                    if(stringbuffer1.length() == 0)
                    {
                        org.w3c.dom.Document document1 = createDocumentFromString(s10);
                        s10 = getIDs(document1, (BrowsableMonitor)atomicmonitor);
                        as = new String[1];
                        as[0] = s10;
                        as1 = new String[1];
                        as1[0] = "not applicable";
                    } else
                    {
                        if(flag)
                        {
                            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_COUNTER_EXCEPTION, new String[] {
                                stringbuffer1.toString()
                            });
                        }
                        as = new String[1];
                        as[0] = "unable to retrieve counters";
                        as1 = new String[1];
                        as1[0] = stringbuffer1.toString();
                    }
                } else
                if((atomicmonitor instanceof ApplicationBase) && stringproperty.getName() != null && stringproperty.getName().equals("availableCounters"))
                {
                    jgl.Array array6 = ((ApplicationBase)atomicmonitor).getAvailableCounters();
                    if(array6 != null)
                    {
                        as = new String[array6.size()];
                        as1 = new String[array6.size()];
                        java.util.HashMap hashmap1 = new java.util.HashMap();
                        int i1 = 0;
                        int l1 = 0;
                        for(; i1 < array6.size(); i1++)
                        {
                            String s18 = (String)array6.at(i1);
                            if(s18 != null)
                            {
                                as[l1] = s18;
                                as1[l1] = s18;
                                l1++;
                                hashmap1.put(s18, "");
                            }
                        }

                    }
                } else
                if((atomicmonitor instanceof COM.dragonflow.StandardMonitor.PDHMonitor) && stringproperty.getName() != null)
                {
                    java.lang.Object aobj[] = new java.lang.Object[1];
                    StringBuffer stringbuffer4 = new StringBuffer();
                    if(stringproperty.getName().equals("availableObjects") || stringproperty.getName().equals("availableCounters") || stringproperty.getName().equals("availableInstances"))
                    {
                        String s11 = (String)hashmap.get("_pdhMachine");
                        if(s11 != null)
                        {
                            atomicmonitor.setProperty("_pdhMachine", s11);
                            boolean flag5 = false;
                            if(stringproperty.getName().equals("availableObjects"))
                            {
                                boolean flag6 = ((COM.dragonflow.StandardMonitor.PDHMonitor)atomicmonitor).getAvailableObjects(aobj, stringbuffer4);
                                if(flag6)
                                {
                                    StringProperty astringproperty[] = (StringProperty[])aobj[0];
                                    as = new String[astringproperty.length];
                                    as1 = new String[astringproperty.length];
                                    for(int i3 = 0; i3 < astringproperty.length; i3++)
                                    {
                                        as[i3] = astringproperty[i3].getName();
                                        as1[i3] = astringproperty[i3].getName();
                                    }

                                } else
                                {
                                    throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_PERFMON_EXCEPTION, new String[] {
                                        stringbuffer4.toString()
                                    });
                                }
                            } else
                            if(stringproperty.getName().equals("availableCounters"))
                            {
                                String s19 = (String)hashmap.get("counterObject");
                                if(s19 != null)
                                {
                                    boolean flag7 = ((COM.dragonflow.StandardMonitor.PDHMonitor)atomicmonitor).getAvailableCounters(s19, aobj, stringbuffer4);
                                    if(flag7)
                                    {
                                        StringProperty astringproperty1[] = (StringProperty[])aobj[0];
                                        as = new String[astringproperty1.length];
                                        as1 = new String[astringproperty1.length];
                                        for(int j4 = 0; j4 < astringproperty1.length; j4++)
                                        {
                                            as[j4] = astringproperty1[j4].getName();
                                            as1[j4] = astringproperty1[j4].getName();
                                        }

                                    } else
                                    {
                                        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_PERFMON_EXCEPTION, new String[] {
                                            stringbuffer4.toString()
                                        });
                                    }
                                }
                            } else
                            if(stringproperty.getName().equals("availableInstances"))
                            {
                                String s20 = (String)hashmap.get("counterObject");
                                if(s20 != null)
                                {
                                    boolean flag8 = ((COM.dragonflow.StandardMonitor.PDHMonitor)atomicmonitor).getAvailableInstances(s20, aobj, stringbuffer4);
                                    if(flag8)
                                    {
                                        StringProperty astringproperty2[] = (StringProperty[])aobj[0];
                                        as = new String[astringproperty2.length];
                                        as1 = new String[astringproperty2.length];
                                        for(int k4 = 0; k4 < astringproperty2.length; k4++)
                                        {
                                            as[k4] = astringproperty2[k4].getName();
                                            as1[k4] = astringproperty2[k4].getName();
                                        }

                                    } else
                                    {
                                        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_PERFMON_EXCEPTION, new String[] {
                                            stringbuffer4.toString()
                                        });
                                    }
                                }
                            }
                        }
                    }
                } 
//				else if((atomicmonitor instanceof COM.dragonflow.StandardMonitor.MQStatusMonitor) && stringproperty.getName() != null)
//                {
//                    java.lang.Object aobj1[] = new java.lang.Object[1];
//                    StringBuffer stringbuffer5 = new StringBuffer();
//                    if(stringproperty.getName().equals("availableObjects") || stringproperty.getName().equals("availableCounters") || stringproperty.getName().equals("availableInstances"))
//                    {
//                        String s12 = (String)hashmap.get("_serverHostName");
//                        String s15 = (String)hashmap.get("_serverPortNumber");
//                        String s21 = (String)hashmap.get("_channel");
//                        if(s12 != null && s15 != null && s21 != null)
//                        {
//                            atomicmonitor.setProperty("_serverHostName", s12);
//                            atomicmonitor.setProperty("_serverPortNumber", s15);
//                            atomicmonitor.setProperty("_channel", s21);
//                            boolean flag9 = false;
//                            if(stringproperty.getName().equals("availableObjects"))
//                            {
//                                boolean flag10 = false;//((COM.dragonflow.StandardMonitor.MQStatusMonitor)atomicmonitor).getAvailableObjects(aobj1, stringbuffer5);
//                                if(flag10)
//                                {
//                                    StringProperty astringproperty3[] = (StringProperty[])aobj1[0];
//                                    as = new String[astringproperty3.length];
//                                    as1 = new String[astringproperty3.length];
//                                    for(int i5 = 0; i5 < astringproperty3.length; i5++)
//                                    {
//                                        as[i5] = astringproperty3[i5].getName();
//                                        as1[i5] = astringproperty3[i5].getName();
//                                    }
//
//                                } else
//                                {
//                                    throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_PERFMON_EXCEPTION, new String[] {
//                                        stringbuffer5.toString()
//                                    });
//                                }
//                            } else
//                            if(stringproperty.getName().equals("availableCounters"))
//                            {
//                                String s22 = (String)hashmap.get("counterObject");
//                                if(s22 != null)
//                                {
//                                    boolean flag11 = false;//((COM.dragonflow.StandardMonitor.MQStatusMonitor)atomicmonitor).getAvailableCounters(s22, aobj1, stringbuffer5);
//                                    if(flag11)
//                                    {
//                                        StringProperty astringproperty4[] = (StringProperty[])aobj1[0];
//                                        as = new String[astringproperty4.length];
//                                        as1 = new String[astringproperty4.length];
//                                        for(int j6 = 0; j6 < astringproperty4.length; j6++)
//                                        {
//                                            as[j6] = astringproperty4[j6].getName();
//                                            as1[j6] = astringproperty4[j6].getName();
//                                        }
//
//                                    } else
//                                    {
//                                        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_PERFMON_EXCEPTION, new String[] {
//                                            stringbuffer5.toString()
//                                        });
//                                    }
//                                }
//                            } else
//                            if(stringproperty.getName().equals("availableInstances"))
//                            {
//                                String s23 = (String)hashmap.get("counterObject");
//                                if(s23 != null)
//                                {
//                                    boolean flag12 = false;//((COM.dragonflow.StandardMonitor.MQStatusMonitor)atomicmonitor).getAvailableInstances(s23, aobj1, stringbuffer5);
//                                    if(flag12)
//                                    {
//                                        StringProperty astringproperty5[] = (StringProperty[])aobj1[0];
//                                        as = new String[astringproperty5.length];
//                                        as1 = new String[astringproperty5.length];
//                                        for(int k6 = 0; k6 < astringproperty5.length; k6++)
//                                        {
//                                            as[k6] = astringproperty5[k6].getName();
//                                            as1[k6] = astringproperty5[k6].getName();
//                                        }
//
//                                    } else
//                                    {
//                                        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_PERFMON_EXCEPTION, new String[] {
//                                            stringbuffer5.toString()
//                                        });
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getClassProperty", exception.getMessage()
            });
        }
        return new SSPropertyDetails(stringproperty.getName(), s4, stringproperty.getDescription(), stringproperty.getLabel(), stringproperty.isEditable, stringproperty.isMultivalued, stringproperty.getDefault(), as, as1, s5, isRequiredProperty(stringproperty.getName()), flag1, stringproperty.getOrder(), s3, stringproperty.isAdvanced, stringproperty.isPassword, atomicmonitor.getProperty(stringproperty.getName()));
    }

    private Vector removeDupMachines(Vector vector)
    {
        Vector vector1 = new Vector();
        java.util.HashSet hashset = new HashSet();
        if(vector.size() > 1)
        {
            vector1.add(vector.elementAt(0));
            vector1.add(vector.elementAt(1));
            hashset.add(((String)vector.elementAt(0)).toLowerCase());
        }
        for(int i = vector.size() - 2; i > 0; i -= 2)
        {
            String s = ((String)vector.elementAt(i)).toLowerCase();
            if(!hashset.contains(s))
            {
                hashset.add(s);
                vector1.add(vector.elementAt(i));
                vector1.add(vector.elementAt(i + 1));
            }
        }

        return vector1;
    }

    private Vector returnMachineScalarValues(AtomicMonitor atomicmonitor)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        Vector vector = null;
        try
        {
            boolean flag = true;
            boolean flag1 = false;
            boolean flag2 = true;
            if(atomicmonitor instanceof COM.dragonflow.StandardMonitor.LogMonitor)
            {
                flag = false;
            }
            if(atomicmonitor instanceof COM.dragonflow.StandardMonitor.ScriptMonitor)
            {
                flag = false;
                flag1 = true;
            }
            if((atomicmonitor instanceof COM.dragonflow.StandardMonitor.NTCounterMonitor) || (atomicmonitor instanceof COM.dragonflow.StandardMonitor.NTEventLogMonitor) || (atomicmonitor instanceof NTCounterBase))
            {
                flag2 = false;
            }
            if(flag)
            {
                vector = getLocalServers();
                vector = addServers(vector, "_remoteNTMachine", false);
            } else
            {
                vector = new Vector();
                try
                {
                    java.net.InetAddress inetaddress = java.net.InetAddress.getLocalHost();
                    String s = inetaddress.getHostName();
                    if(Platform.isWindows())
                    {
                        s = "\\\\" + s;
                    }
                    vector.addElement(s);
                    vector.addElement(s);
                }
                catch(java.net.UnknownHostException unknownhostexception)
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_NO_IP, new String[] {
                        "localhost"
                    });
                }
                if(flag1)
                {
                    vector = addServers(vector, "_remoteNTMachine", true);
                }
            }
            if(flag2)
            {
                vector = addServers(vector, "_remoteMachine", false);
            }
            vector = removeDupMachines(vector);
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "getClassProperty", exception.getMessage()
            });
        }
        return vector;
    }

    private org.w3c.dom.Document createDocumentFromString(String s)
        throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException
    {
        javax.xml.parsers.DocumentBuilderFactory documentbuilderfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        documentbuilderfactory.setValidating(false);
        javax.xml.parsers.DocumentBuilder documentbuilder = documentbuilderfactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentbuilder.parse(new InputSource(new StringReader(s)));
        return document;
    }

    protected static boolean isValidObject(String s, String s1)
    {
        boolean flag = false;
        if(s.endsWith(s1))
        {
            try
            {
                java.lang.Class class1 = java.lang.Class.forName(s);
                if(SiteViewObject.loadableClass(class1))
                {
                    flag = true;
                }
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("error", "APIMonitor: failed to load class: " + s + ", Exception: " + exception.getMessage());
            }
            catch(java.lang.NoClassDefFoundError noclassdeffounderror)
            {
                COM.dragonflow.Log.LogManager.log("error", "APIMonitor: NoClassDefFoundError, failed to load class: " + s + ", Exception: " + noclassdeffounderror.getMessage());
            }
        }
        return flag;
    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    protected static boolean isAddableMonitor(String s)
    {
        try {
        Object obj = null;
        java.lang.Object obj1;
        java.lang.Class class1 = java.lang.Class.forName(s);
        obj1 = SiteViewObject.getClassPropertyByObject(class1.getName(), "addable");
        if(obj1 != null)
        {
            return obj1.equals("true");
        }
        return true;
        }
        catch (java.lang.ClassNotFoundException classnotfoundexception) {
        return false;
        }
    }

    private String getIDs(org.w3c.dom.Document document, BrowsableMonitor browsablemonitor)
    {
        if(document.getDocumentElement() != null)
        {
            org.w3c.dom.NodeList nodelist = document.getElementsByTagName("counter");
            int i = nodelist.getLength();
            for(int j = 0; j < i; j++)
            {
                org.w3c.dom.Node node = nodelist.item(j);
                String s = node.getNodeName();
                if(s.toLowerCase().equals("counter"))
                {
                    String s1 = browsablemonitor.setBrowseID(getNodeIdNames(node));
                    ((org.w3c.dom.Element)node).setAttribute("id", s1);
                }
            }

        } else
        {
            COM.dragonflow.Log.LogManager.log("error", "Document Element is null");
        }
        java.io.StringWriter stringwriter = new StringWriter();
        try
        {
            javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(new DOMSource(document.getDocumentElement()), new StreamResult(stringwriter));
        }
        catch(javax.xml.transform.TransformerException transformerexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "TransformationException while serializing XML Document in APIMonitor.getIDs: " + transformerexception.getMessage());
        }
        return stringwriter.toString();
    }

    /**
     * 
     * 
     * @param s
     * @param atomicmonitor
     * @param s1
     * @param s2
     * @param hashmap
     * @throws COM.dragonflow.SiteViewException.SiteViewException
     */
    private void setMonitorProperties(String s, AtomicMonitor atomicmonitor, String s1, String s2, jgl.HashMap hashmap)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try {
        User user;
        COM.dragonflow.HTTP.HTTPRequest httprequest;
        SiteViewGroup siteviewgroup;
        int j;
        String s5;
        jgl.HashMap hashmap1;
        Enumeration enumeration;
        user = User.getUserForAccount("administrator");
        httprequest = new HTTPRequest();
        httprequest.setUser("administrator");
        String s3 = user.getPermission("_monitorType", (String)atomicmonitor.getClassProperty("class"));
        if(s3.equals("optional"))
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_LICENSE_TYPE, new String[] {
                (String)atomicmonitor.getClassProperty("class")
            });
        }
        siteviewgroup = SiteViewGroup.currentSiteView();
        if(s.equals(OP_ADD))
        {
            s2 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s2);
            Monitor monitor = (Monitor)SiteViewGroup.currentSiteView().getElementByID(s2);
            atomicmonitor.setOwner(monitor);
            if(COM.dragonflow.Utils.TextUtils.toInt(s3) > 0)
            {
                int k = monitor.countMonitorsOfClass(hashmap.get("class") == null ? "" : (String)hashmap.get("class"), user.getValue("_account"));
                if(k >= COM.dragonflow.Utils.TextUtils.toInt(s3))
                {
                    throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_LICENSE_LIMIT, new String[] {
                        (new Integer(k)).toString()
                    });
                }
            }
            int l = COM.dragonflow.Utils.TextUtils.toInt(user.getPermission("_maximumMonitors"));
            if(l > 0)
            {
                int i1 = monitor.countMonitors(user.getValue("_account"));
                if(i1 >= l)
                {
                    throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_LICENSE_LIMIT, new String[] {
                        (new Integer(i1)).toString()
                    });
                }
            }
        }
        if(s.equals(OP_ADD) || s.equals(OP_TEMP))
        {
            if(COM.dragonflow.Utils.LUtils.wouldExceedLimit(atomicmonitor))
            {
                int i = COM.dragonflow.Utils.LUtils.getLicensedPoints();
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_LICENSE_LIMIT, new String[] {
                    (new Integer(i)).toString()
                });
            }
            if(!COM.dragonflow.Utils.LUtils.isMonitorTypeAllowed(atomicmonitor))
            {
                String s4 = (String)atomicmonitor.getClassProperty("title");
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_LICENSE_TYPE, new String[] {
                    s4
                });
            }
        }
        j = COM.dragonflow.Utils.TextUtils.toInt(user.getPermission("_minimumFrequency"));
        s5 = atomicmonitor.defaultTitle();
        hashmap1 = new HashMap();
        String s6 = ServerMonitor.pMachineName.getName();
        if(hashmap.get(s6) != null)
        {
            String s7 = (String)hashmap.get(s6);
            if(!s7.startsWith("\\\\") && !s7.startsWith("remote:") && !s7.equals(""))
            {
                s7 = "remote:" + s7;
                hashmap.put(s6, s7);
            }
        }
        if(hashmap.get("_counters") != null)
        {
            String s8 = (String)hashmap.get("_counters");
            String as[] = COM.dragonflow.Utils.TextUtils.split(s8, ",");
            int l1 = atomicmonitor.getMaxCounters();
            if(as.length > l1)
            {
                jgl.HashMap hashmap3 = MasterConfig.getMasterConfig();
                hashmap3.put("_ApplicationMonitorMaxCounters", (new Integer(as.length)).toString());
                MasterConfig.saveMasterConfig(hashmap3);
                ((ISelectableCounter)atomicmonitor).increaseCounters(as.length);
            }
            ((ISelectableCounter)atomicmonitor).setCountersPropertyValue(atomicmonitor, s8);
        }
        if(atomicmonitor instanceof COM.dragonflow.StandardMonitor.URLSequenceMonitor)
        {
            jgl.HashMap hashmap2 = MasterConfig.getMasterConfig();
            int j1 = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_URLSequenceMonitorSteps"));
            if(j1 == 0)
            {
                j1 = 20;
            }
            int i2 = j1;
            int j2 = j1 + 1;
            while (true)
            {
                String s15 = (String)hashmap.get("_referenceType" + j2);
                if(s15 == null)
                {
                    break;
                }
                i2++;
                j2++;
            } 
            if(i2 > j1)
            {
                hashmap2.put("_URLSequenceMonitorSteps", (new Integer(i2)).toString());
                MasterConfig.saveMasterConfig(hashmap2);
                COM.dragonflow.StandardMonitor.URLSequenceMonitor.allocateStepProperties(i2);
            }
        }
        if(atomicmonitor instanceof BrowsableMonitor)
        {
            jgl.Array array = ((BrowsableMonitor)atomicmonitor).getConnectionProperties();
            for(int k1 = 0; k1 < array.size(); k1++)
            {
                String s10 = ((StringProperty)array.at(k1)).getName();
                String s12 = (String)hashmap.get(s10);
                if(s12 != null)
                {
                    atomicmonitor.setProperty(s10, s12);
                }
            }

            String s9 = ((BrowsableMonitor)atomicmonitor).getBrowseName();
            String s11 = ((BrowsableMonitor)atomicmonitor).getBrowseID();
            boolean flag = hashmap.get(s9 + 1) != null;
            if(flag)
            {
                int k2 = ((BrowsableMonitor)atomicmonitor).getMaxCounters();
                int i3;
                for(i3 = 1; hashmap.get(s9 + i3) != null; i3++) { }
                if(--i3 > k2)
                {
                    java.util.List list = BrowsableBase.createNewBrowsableCounters(k2, i3);
                    for(java.util.Iterator iterator = list.iterator(); iterator.hasNext(); COM.dragonflow.Properties.PropertiedObject.addPropertyToPropertyMap(atomicmonitor.getClass().getName(), (StringProperty)iterator.next())) { }
                    ((BrowsableMonitor)atomicmonitor).setMaxCounters(i3);
                    k2 = i3;
                }
                for(int j4 = 1; j4 <= k2; j4++)
                {
                    String s24 = (String)hashmap.get(s9 + j4);
                    String s28 = (String)hashmap.get(s11 + j4);
                    atomicmonitor.unsetProperty(s9 + j4);
                    atomicmonitor.unsetProperty(s11 + j4);
                    atomicmonitor.unsetProperty(BrowsableBase.PROPERTY_NAME_COUNTER_VALUE + j4);
                    if(s24 != null)
                    {
                        if(!$assertionsDisabled && !atomicmonitor.hasProperty(s9 + j4))
                        {
                            throw new AssertionError();
                        }
                        atomicmonitor.setProperty(s9 + j4, s24);
                        jgl.Array array3 = new Array();
                        String as3[] = s24.split("/");
                        for(int i5 = as3.length - 1; i5 >= 0; i5--)
                        {
                            array3.add(as3[i5]);
                        }

                        hashmap.put(s9 + j4, ((BrowsableMonitor)atomicmonitor).setBrowseName(array3));
                    }
                    if(s28 == null)
                    {
                        continue;
                    }
                    if(!$assertionsDisabled && !atomicmonitor.hasProperty(s11 + j4))
                    {
                        throw new AssertionError();
                    }
                    atomicmonitor.setProperty(s11 + j4, s28);
                    int k4 = s28.indexOf(" ");
                    if(k4 < 0)
                    {
                        continue;
                    }
                    String s34 = s28.substring(0, k4);
                    if(!COM.dragonflow.Utils.TextUtils.isNumber(s34))
                    {
                        jgl.Array array4 = new Array();
                        array4.add(s28);
                        hashmap.put(s11 + j4, ((BrowsableMonitor)atomicmonitor).setBrowseID(array4));
                    }
                }

            }
            int l2 = 1;
            String s19 = (String)hashmap.get("uniqueID");
            if(s19 != null && s19.length() > 0)
            {
                jgl.HashMap hashmap4 = BrowsableCache.getCache(s19, true, false);
                jgl.HashMap hashmap5 = (jgl.HashMap)hashmap4.get("selectNames");
                jgl.HashMap hashmap6 = (jgl.HashMap)hashmap4.get("selectIDs");
                for(Enumeration enumeration4 = hashmap5.keys(); enumeration4.hasMoreElements();)
                {
                    String s35 = (String)enumeration4.nextElement();
                    String s36 = (String)hashmap6.get(s35);
                    atomicmonitor.setProperty(s9 + l2, s35);
                    if(s36 != null)
                    {
                        atomicmonitor.setProperty(s11 + l2, s36);
                    }
                    l2++;
                }

            }
        }
        if(s.equals(OP_ADD) || s.equals(OP_TEMP))
        {
            siteviewgroup.checkDispatcherForStart(atomicmonitor);
        }
        
        jgl.Array array1 = atomicmonitor.getProperties();
        array1 = StringProperty.sortByOrder(array1);
        enumeration = array1.elements();
        StringProperty stringproperty;
        String s14;
        while (enumeration.hasMoreElements())
            {
            stringproperty = (StringProperty)enumeration.nextElement();
        if (s.equals(OP_ADD) || s.equals(OP_TEMP) || hashmap.get(stringproperty.getName()) != null && atomicmonitor.propertyInTemplate(stringproperty) && stringproperty.isConfigurable) {
        if(stringproperty.isMultiLine)
        {
            String s13 = "";
            if(stringproperty.getName().equals("_counters"))
            {
                s13 = atomicmonitor.getProperty(stringproperty);
            } else
            {
                s13 = (String)hashmap.get(stringproperty.getName());
                if(s13 == null)
                {
                    s13 = atomicmonitor.getProperty(stringproperty);
                }
            }
            String as1[] = COM.dragonflow.Utils.TextUtils.split(s13, "\r\n");
            atomicmonitor.unsetProperty(stringproperty);
            int j3 = 0;
            while(j3 < as1.length) 
            {
                String s21 = as1[j3];
                atomicmonitor.addProperty(stringproperty, s21);
                j3++;
            }
            continue; 
        }
        if((stringproperty instanceof COM.dragonflow.Properties.ScalarProperty) && ((COM.dragonflow.Properties.ScalarProperty)stringproperty).multiple && stringproperty.getName().indexOf("Remote") == -1)
        {
            java.lang.Object obj1 = hashmap.get(stringproperty.getName());
            if(obj1 instanceof String[])
            {
                String as2[] = (String[])hashmap.get(stringproperty.getName());
                atomicmonitor.unsetProperty(stringproperty);
                int k3 = 0;
                while(k3 < as2.length) 
                {
                    String s22 = as2[k3];
                    atomicmonitor.addProperty(stringproperty, s22);
                    k3++;
                }
            } else
            if(obj1 instanceof String)
            {
                String s16 = (String)hashmap.get(stringproperty.getName());
                atomicmonitor.unsetProperty(stringproperty);
                atomicmonitor.addProperty(stringproperty, s16);
            }
            continue; /* Loop/switch isn't completed */
        }
        s14 = (String)hashmap.get(stringproperty.getName());
        if(s14 == null)
        {
            s14 = atomicmonitor.getProperty(stringproperty);
        }
        if(!s.equals(OP_TEMP))
        {
            String s17 = stringproperty.getName();
            if((s17.equals("_getImages") || s17.equals("_getFrames")) && s14.equals("on") && httprequest.isSiteSeerAccount())
            {
                int l3 = COM.dragonflow.Utils.TextUtils.toInt(user.getPermission("_maximumFAndIMonitors"));
                String s23 = httprequest.getValue("operation");
                String s25 = s1;
                String s29 = httprequest.getValue("class");
                if(s29.equals("") && !s25.equals(""))
                {
                    String s32 = s2 + SiteViewObject.ID_SEPARATOR + s25;
                    s32 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s32);
                    Monitor monitor1 = (Monitor)siteviewgroup.getElement(s32);
                    s29 = monitor1.getProperty("_class");
                }
                if(s29.startsWith("URLRemote"))
                {
                    String s33 = COM.dragonflow.Utils.I18N.toDefaultEncoding(user.getValue("_account"));
                    MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElement(s33);
                    String s37 = monitorgroup.getProperty(Monitor.pGroupID);
                    jgl.Array array5 = monitorgroup.getMonitorsOfClass("", s37);
                    Enumeration enumeration5 = array5.elements();
                    int j5 = 0;
                    while (enumeration5.hasMoreElements())
                        {
                        Monitor monitor2 = (Monitor)enumeration5.nextElement();
                        if(!s23.equals("Edit"))
                        {
//                            if(((monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteMonitor) || (monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteSequenceMonitor)) && (monitor2.getProperty("_getImages").equals("on") || monitor2.getProperty("_getFrames").equals("on")))
//                            {
//                                j5++;
//                            }
//                            continue;
                        }
                        if(monitor2.getProperty("id").equals(s1) && (monitor2.getProperty("_getImages").equals("on") || monitor2.getProperty("_getFrames").equals("on")))
                        {
                            break;
                        }
//                        if(((monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteMonitor) || (monitor2 instanceof COM.dragonflow.StandardMonitor.URLRemoteSequenceMonitor)) && (monitor2.getProperty("_getImages").equals("on") || monitor2.getProperty("_getFrames").equals("on")))
//                        {
//                            j5++;
//                        }
                    }
                    
                    if(j5 >= l3)
                    {
                        return;
                    }
                }
            }
        }
        
        String s18 = s14;
        if(j > 0 && (stringproperty instanceof COM.dragonflow.Properties.FrequencyProperty))
        {
            int i4 = COM.dragonflow.Utils.TextUtils.toInt(s18);
            if(i4 != 0 && i4 < j)
            {
                hashmap1.put(stringproperty, "For this account, monitors must run at intervals of " + COM.dragonflow.Utils.TextUtils.secondsToString(j) + " or more.");
            }
        }
        if(stringproperty == Monitor.pName)
        {
            if(s18.equals(s5))
            {
                atomicmonitor.setProperty(stringproperty, "");
            } else
            {
                atomicmonitor.setProperty(stringproperty, s18);
            }
        } else
        if((stringproperty instanceof COM.dragonflow.Properties.ScalarProperty) && ((COM.dragonflow.Properties.ScalarProperty)stringproperty).multiple)
        {
            jgl.Array array2 = new Array();
            if(!Platform.isStandardAccount(user.getValue("_account")) && stringproperty.getName().equals("_location"))
            {
                Enumeration enumeration1 = atomicmonitor.getMultipleValues(stringproperty);
                String s26 = "";
                String s30 = "";
                while (enumeration1.hasMoreElements())
                    {
                    String s27 = (String)enumeration1.nextElement();
                    String s31 = (String)COM.dragonflow.Utils.HTTPUtils.locationMap.get(s27);
                    if(s31 != null && !s31.equals(""))
                    {
                        int l4 = COM.dragonflow.Utils.HTTPUtils.getDisplayOrder(s31);
                        if(l4 < 0)
                        {
                            array2.add(s27);
                        }
                    }
                } 
            }
            Enumeration enumeration2 = httprequest.getValues(stringproperty.getName());
            atomicmonitor.unsetProperty(stringproperty);
            while (enumeration2.hasMoreElements()) {
                atomicmonitor.addProperty(stringproperty, (String)enumeration2.nextElement());
            }
            if(array2 != null && array2.size() > 0)
            {
                Enumeration enumeration3 = array2.elements();
                while(enumeration3.hasMoreElements()) 
                {
                    atomicmonitor.addProperty(stringproperty, (String)enumeration3.nextElement());
                }
            }
        } else
        {
            String s20 = atomicmonitor.getProperty(stringproperty);
            atomicmonitor.setProperty(stringproperty, s18);
            if(stringproperty.getName().equals("_disabled"))
            {
                if(s20.length() <= 0 && s18.length() > 0)
                {
                    siteviewgroup.notifyMonitorDeletion(atomicmonitor);
                } else
                if(s20.length() > 0 && s18.length() <= 0)
                {
                    siteviewgroup.checkDispatcherForStart(atomicmonitor);
                }
            }
        }
        }
            }

        saveThresholds(atomicmonitor, hashmap);
        saveCustomProperties(atomicmonitor, hashmap);
        if(atomicmonitor.getProperty(Monitor.pName).length() == 0)
        {
            atomicmonitor.setProperty(Monitor.pName, atomicmonitor.defaultTitle());
        }
    }
    catch (SiteViewException e) {
        e.fillInStackTrace();
        throw e;
    }
    catch (Exception e) {
        throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
            "APIMonitor", "setMonitorProperties", e.getMessage()
        });
    }
    }

    private void writeMonitor(String s, AtomicMonitor atomicmonitor, String s1, String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        String s3 = s1;
        try
        {
            COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
            int i = saveOrdering(httprequest);
            jgl.HashMap hashmap = atomicmonitor.getValuesTable();
            jgl.Array array = null;
            if(!$assertionsDisabled && s.equals(OP_TEMP))
            {
                throw new AssertionError();
            }
            array = ReadGroupFrames(s2);
            if(s.equals(OP_ADD))
            {
                jgl.HashMap hashmap1 = (jgl.HashMap)array.at(0);
                s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_nextID");
                if(s3.length() == 0)
                {
                    s3 = "1";
                }
                hashmap.remove(Monitor.pID);
                hashmap.remove("_id");
                hashmap.put("_id", s3);
                hashmap.put("_class", atomicmonitor.getClassProperty("class"));
                array.insert(i, hashmap);
                String s4 = COM.dragonflow.Utils.TextUtils.increment(s3);
                hashmap1.put("_nextID", s4);
            } else
            {
                int j = monitorUtils.findMonitorIndex(array, s1);
                java.lang.Object obj = array.at(j);
                array.remove(obj);
                array.insert(j, hashmap);
            }
//            if(TopazConfigurator.configInTopazAndRegistered())
//            {
//                jgl.Array array1 = new Array();
//                array1.add(atomicmonitor);
//                if(s.equals(OP_EDIT) && atomicmonitor.isDispatcher())
//                {
//                    DispatcherMonitor.notifyDispatcherMonitor(DispatcherMonitor.EDIT_MON, atomicmonitor);
//                }
//            }
            if(!s.equals(OP_TEMP))
            {
                WriteGroupFrames(s2, array);
            }
            if(s.equals(OP_ADD) || s.equals(OP_TEMP))
            {
                atomicmonitor.setProperty(Monitor.pID, s3);
            }
            if(!s.equals(OP_EDIT) && !s.equals(OP_ADD) || atomicmonitor.getProperty(Monitor.pDisabled).length() != 0 || atomicmonitor.getPropertyAsLong(AtomicMonitor.pFrequency) == 0L || !SiteViewGroup.currentSiteView().internalServerActive())
            {
                DetectConfigurationChange detectconfigurationchange = DetectConfigurationChange.getInstance();
                detectconfigurationchange.setConfigChangeFlag();
            }
            if(atomicmonitor instanceof BrowsableMonitor)
            {
                BrowsableCache.getCache(s1, false, true);
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                "APIMonitor", "writeMonitor", exception.getMessage()
            });
        }
    }

    private void saveThresholds(Monitor monitor, jgl.HashMap hashmap)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        int i = getThresholdNum(monitor);
        String as[] = {
            "error", "warning", "good"
        };
        String s = "-condition";
        String s1 = "-comparison";
        String s2 = "-parameter";
        monitor.unsetProperty("_classifier");
        Rule rule;
        for(Enumeration enumeration = monitor.getClassifiers(); enumeration.hasMoreElements(); monitor.removeElement(rule))
        {
            rule = (Rule)enumeration.nextElement();
        }

        for(int j = 0; j < as.length; j++)
        {
            for(int k = 0; k < i; k++)
            {
                String s3 = as[j] + s + k;
                String s4 = as[j] + s1 + k;
                String s5 = as[j] + s2 + k;
                String s6 = (String)hashmap.get(s3);
                String s7 = (String)hashmap.get(s4);
                String s8 = (String)hashmap.get(s5);
                hashmap.remove(s3);
                hashmap.remove(s4);
                hashmap.remove(s5);
                if(s6 == null || s6.equals("default"))
                {
                    continue;
                }
                if(s7 == null)
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_INVALID_THRESHOLD_COND_MISSING);
                }
                if(s8 == null)
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_INVALID_THRESHOLD_VAL_MISSING);
                }
                String s9 = s6 + " " + s7 + " " + s8 + "\t" + as[j];
                saveThreshold(monitor, k, as[j], s9);
            }

        }

    }

    private void saveThreshold(Monitor monitor, int i, String s, String s1)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        int j = getThresholdNum(monitor);
        String s2 = "SetProperty category " + s;
        if(i > j)
        {
            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_INVALID_THRESHOLD, new String[] {
                s1, (new Integer(i)).toString()
            });
        }
        Rule rule = Rule.stringToClassifier(s1);
        if(s2.equals(rule.getProperty(Rule.pAction)))
        {
            if(rule != null)
            {
                monitor.addProperty("_classifier", s1);
                monitor.addElement(rule);
            } else
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_INVALID_THRESHOLD, new String[] {
                    s1, (new Integer(i)).toString()
                });
            }
        }
    }

    private void saveCustomProperties(Monitor monitor, jgl.HashMap hashmap)
    {
        jgl.HashMap hashmap1 = MasterConfig.getMasterConfig();
        Enumeration enumeration = hashmap1.values("_monitorEditCustom");
        while (enumeration.hasMoreElements()) {
            String s = (String)enumeration.nextElement();
            String as[] = COM.dragonflow.Utils.TextUtils.split(s, "|");
            String s1 = as[0];
            if(s1.length() > 0)
            {
                if(!s1.startsWith("_"))
                {
                    s1 = "_" + s1;
                }
                if(shouldPrintProperty(monitor, s1))
                {
                    String s2 = (String)hashmap.get(s1);
                    if(s2 != null)
                    {
                        s2 = s2.replace('\r', ' ');
                        s2 = s2.replace('\n', ' ');
                        monitor.setProperty(s1, (String)hashmap.get(s1));
                    }
                }
            }
        } 
    }

    private boolean shouldPrintProperty(SiteViewObject siteviewobject, String s)
    {
        return !siteviewobject.propertyInTemplate(s);
    }

    private int saveOrdering(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        int i = COM.dragonflow.Utils.TextUtils.toInt(httprequest.getValue("ordering"));
        if(i <= 0)
        {
            i = 1;
        }
        return i;
    }

    private void deleteMonitorInternal(String s)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try
        {
            int i = s.indexOf(' ');
            if(i >= 0)
            {
                String s1 = s.substring(0, i);
                String s2 = s.substring(i + 1);
                jgl.Array array = null;
                array = getGroupFrames(s1);
                int j = findMonitorIndex(array, s2);
                if(j >= 1)
                {
                    SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                    StringBuffer stringbuffer = new StringBuffer();
                    String s3 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s1 + SiteViewGroup.ID_SEPARATOR + s2);
                    AtomicMonitor atomicmonitor = (AtomicMonitor)siteviewgroup.getElement(s3);
                    if(atomicmonitor != null)
                    {
                        jgl.Array array1 = new Array();
                        siteviewgroup.notifyMonitorDeletion(atomicmonitor);
//                        if(TopazConfigurator.configInTopazAndRegistered())
//                        {
//                            array1.add(atomicmonitor);
//                            TopazConfigurator.updateTopaz(array1, 1, stringbuffer, false, null);
//                            if(stringbuffer.length() > 0)
//                            {
//                                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_TOPAZ_DELETE_EXCEPTION, new String[] {
//                                    s2, stringbuffer.toString()
//                                });
//                            }
//                        }
                    }
                    array.remove(j);
                } else
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                        s1 + "/" + s2
                    });
                }
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                exception.getMessage()
            });
        }
    }

    private Enumeration getRefresh(HTMLTagParser htmltagparser)
    {
        Vector vector = new Vector();
        Enumeration enumeration = htmltagparser.findTags("meta");
        while (enumeration.hasMoreElements()) {
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            String s = new String("");
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "http-equiv").equalsIgnoreCase("refresh"))
            {
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "content").trim();
                vector.addElement(s1);
                vector.addElement(s1.length() <= 80 ? ((java.lang.Object) (s1)) : ((java.lang.Object) (s1.substring(0, 79))));
            }
        }
        return vector.elements();
    }

    private Enumeration getFrames(HTMLTagParser htmltagparser)
    {
        Vector vector = new Vector();
        Enumeration enumeration = htmltagparser.findTags("frame");
        Enumeration enumeration1 = htmltagparser.findTags("iframe");
        String s;
        for(; enumeration.hasMoreElements(); vector.addElement(s.length() <= 80 ? ((java.lang.Object) (s)) : ((java.lang.Object) (s.substring(0, 79)))))
        {
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "name").trim();
            if(s.length() == 0)
            {
                s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "src").trim();
            }
            vector.addElement(s);
        }

        String s1;
        for(; enumeration1.hasMoreElements(); vector.addElement(s1.length() <= 80 ? ((java.lang.Object) (s1)) : ((java.lang.Object) (s1.substring(0, 79)))))
        {
            jgl.HashMap hashmap1 = (jgl.HashMap)enumeration1.nextElement();
            s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name").trim();
            if(s1.length() == 0)
            {
                s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "src").trim();
            }
            vector.addElement(s1);
        }

        return vector.elements();
    }

    private Enumeration getForms(HTMLTagParser htmltagparser, AtomicMonitor atomicmonitor, String s)
    {
        Vector vector = new Vector();
        Enumeration enumeration = htmltagparser.findTags("form");
        int i = 0;
        while (enumeration.hasMoreElements()) {
            i++;
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "name").trim();
            if(s1.length() == 0)
            {
                s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "action").trim();
            }
            s1 = "[" + i + "]" + s1;
            if(s1.length() > 30)
            {
                s1 = s1.substring(0, 29);
            }
            Enumeration enumeration1 = htmltagparser.findTags(hashmap, FORM_INPUT_TAGS);
            int j = 0;
            String s2 = "";
            String s5 = "";
            String s7 = "";
            while (enumeration1.hasMoreElements()) {
                String s3 = "";
                String s6 = "";
                String s8 = "";
                jgl.HashMap hashmap1 = (jgl.HashMap)enumeration1.nextElement();
                String s9 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "tag");
                if(s9.equals("select"))
                {
                    String s10 = null;
                    String s12 = null;
                    Enumeration enumeration2 = htmltagparser.findTags(hashmap1, "option");
                    while (enumeration2.hasMoreElements()) {
                        jgl.HashMap hashmap2 = (jgl.HashMap)enumeration2.nextElement();
                        if(s12 == null)
                        {
                            s12 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "value");
                        } else
                        if(s10 == null && hashmap2.get("selected") != null)
                        {
                            s10 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "value");
                        }
                    }
                    if(s10 == null)
                    {
                        s10 = s12;
                    }
                    s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                    s8 = s10;
                } else
                {
                    String s11 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "type").toLowerCase();
                    if(s11.equals("submit"))
                    {
                        j++;
                        s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                        if(s3.length() == 0)
                        {
                            s3 = "Submit";
                        }
                    } else
                    if(s11.equals("image"))
                    {
                        j++;
                        s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                        if(s3.length() == 0)
                        {
                            s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "alt");
                            if(s3.length() == 0)
                            {
                                s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                                if(s3.length() == 0)
                                {
                                    s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "src");
                                    if(s3.length() == 0)
                                    {
                                        s3 = "[" + j + "]";
                                    }
                                }
                            }
                        }
                    } else
                    if(s11.equals("text") || s11.equals("password") || s11.equals("hidden") || s11.equals("checkbox") || s11.equals(""))
                    {
                        s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                        s8 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                    } else
                    if(s11.equals("radio") && hashmap1.get("checked") != null)
                    {
                        s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                        s8 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                    }
                    if(s3.length() > 0)
                    {
                        String s13 = "";
                        if(i > 1)
                        {
                            Enumeration enumeration3 = vector.elements();
                            if(enumeration3.hasMoreElements())
                            {
                                String s14 = (String)enumeration3.nextElement();
                                if(s3.equals(s14))
                                {
                                    s13 = "[" + i + "]";
                                    break;
                                }
                                s14 = (String)enumeration3.nextElement();
                            }
                        }
                        if(s3.length() > 40)
                        {
                            s3 = s3.substring(0, 39);
                        }
                        vector.addElement("{" + s1 + "}" + s3 + s13);
                        vector.addElement("{" + s1 + "}" + s3);
                    }
                }
                if(s6.length() > 0)
                {
                    atomicmonitor.setProperty("_postData" + s, atomicmonitor.getProperty("_postData" + s) + (atomicmonitor.getProperty("_postData" + s).length() <= 0 ? "" : "\n") + "{" + s1 + "}" + s6 + "=" + s8);
                }
            } 
            if(j == 0)
            {
                String s4 = "[" + i + "]";
                vector.addElement("{" + s1 + "}" + s4);
                vector.addElement("{" + s1 + "}" + s4);
            }
        } 
        return vector.elements();
    }

    private Enumeration getLinks(HTMLTagParser htmltagparser)
    {
        Vector vector = new Vector();
        String s;
        for(Enumeration enumeration = htmltagparser.findTags("a"); enumeration.hasMoreElements(); vector.addElement(s.length() <= 80 ? ((java.lang.Object) (s)) : ((java.lang.Object) (s.substring(0, 79)))))
        {
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "contents").trim();
            vector.addElement(s);
        }

        String s1;
        for(Enumeration enumeration1 = htmltagparser.findTags("area"); enumeration1.hasMoreElements(); vector.addElement(s1.length() <= 80 ? ((java.lang.Object) (s1)) : ((java.lang.Object) (s1.substring(0, 79)))))
        {
            jgl.HashMap hashmap1 = (jgl.HashMap)enumeration1.nextElement();
            s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "contents").trim();
            if(s1.length() == 0 || s1.toLowerCase().lastIndexOf("<img") >= 0)
            {
                s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "href").trim();
            }
            vector.addElement(s1);
        }

        return vector.elements();
    }

    private void findCounters(Monitor monitor, Vector vector, Vector vector1, org.w3c.dom.Node node, int i)
    {
        if(node.getNodeType() == 1)
        {
            org.w3c.dom.NodeList nodelist = node.getChildNodes();
            int j = nodelist.getLength();
            String s = ((org.w3c.dom.Element)node).getAttribute("name");
            if(s != null)
            {
                String s1 = COM.dragonflow.Utils.TextUtils.arrayToString(getNodeNames(node));
                String s2 = node.getNodeName();
                if(s2.toLowerCase().equals("counter"))
                {
                    String s3 = ((BrowsableMonitor)monitor).setBrowseID(getNodeIdNames(node));
                    vector1.addElement(s3);
                    vector.addElement(s1);
                }
            }
            for(int k = 0; k < j; k++)
            {
                findCounters(monitor, vector, vector1, nodelist.item(k), i + 2);
            }

        }
    }

    private jgl.Array getNodeNames(org.w3c.dom.Node node)
    {
        jgl.Array array = new Array();
        String s = ((org.w3c.dom.Element)node).getAttribute("name");
        if(s == null)
        {
            return array;
        }
        array.add(s);
        org.w3c.dom.Node node1 = node.getParentNode();
        do
        {
            if(node1 == null)
            {
                break;
            }
            String s1 = ((org.w3c.dom.Element)node1).getAttribute("name");
            if(s1 == null || s1.length() <= 0)
            {
                break;
            }
            array.add(s1);
            node1 = node1.getParentNode();
        } while(true);
        return array;
    }

    jgl.Array getNodeIdNames(org.w3c.dom.Node node)
    {
        jgl.Array array = new Array();
        String s = "id";
        String s1 = ((org.w3c.dom.Element)node).getAttribute(s);
        if(s1 == null || s1.length() == 0)
        {
            s = "name";
            s1 = ((org.w3c.dom.Element)node).getAttribute(s);
        }
        if(s1 == null)
        {
            return array;
        }
        array.add(s1);
        org.w3c.dom.Node node1 = node.getParentNode();
        do
        {
            if(node1 == null)
            {
                break;
            }
            String s2 = ((org.w3c.dom.Element)node1).getAttribute(s);
            if(s2 == null || s2.length() <= 0)
            {
                break;
            }
            array.add(s2);
            node1 = node1.getParentNode();
        } while(true);
        return array;
    }

    private AtomicMonitor instantiateMonitor(String s)
        throws COM.dragonflow.SiteViewException.SiteViewParameterException
    {
        java.lang.Class class1 = null;
        String s1 = "COM.dragonflow.StandardMonitor." + s;
        try
        {
            class1 = java.lang.Class.forName(s1);
        }
        catch(java.lang.ClassNotFoundException classnotfoundexception)
        {
            String s2 = "COM.dragonflow.CustomMonitor." + s;
            try
            {
                class1 = java.lang.Class.forName(s2);
            }
            catch(java.lang.ClassNotFoundException classnotfoundexception1)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_TYPE_NOT_VALID, new String[] {
                    s
                });
            }
        }
        AtomicMonitor atomicmonitor = null;
        try
        {
            atomicmonitor = (AtomicMonitor)class1.newInstance();
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_TYPE_NOT_VALID, new String[] {
                s
            });
        }
        if(!SiteViewObject.loadableClass(class1))
        {
            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_NOT_LOADABLE, new String[] {
                s
            });
        } else
        {
            return atomicmonitor;
        }
    }

    /**
     * 
     * @param s
     * @param s1
     * @return
     * @throws COM.dragonflow.SiteViewException.SiteViewException
     */
    public SSMonitorInstance resetCounters(String s, String s1)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        SSMonitorInstance ssmonitorinstance;
            ssmonitorinstance = null;
            try
            {
                AtomicMonitor atomicmonitor;
                    SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
                    s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s1);
                    MonitorGroup monitorgroup = (MonitorGroup)siteviewgroup.getElementByID(s1);
                    atomicmonitor = null;
                    if(monitorgroup != null)
                    {
                    Enumeration enumeration = monitorgroup.getMonitors();
                    Monitor monitor;
                    String s2;
                    while (enumeration.hasMoreElements())
                        {
                        monitor = (Monitor)enumeration.nextElement();
                        s2 = monitor.getProperty("_id");
                    if (s2 != null && s2.equals(s)) {
                    atomicmonitor = (AtomicMonitor)monitor;
                    }
                }
                if(atomicmonitor != null)
                {
                    atomicmonitor.resetCounters();
                } else
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_UNASSOCIATED_ID, new String[] {
                        s1 + "/" + s
                    });
                }
            }
            }
            catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
            {
                siteviewexception.fillInStackTrace();
                throw siteviewexception;
            }
            catch(java.lang.Exception exception)
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_MONITOR_EXCEPTION, new String[] {
                    "APIMonitor", "resetCounters"
                }, 0L, exception.getMessage());
            }
        return ssmonitorinstance;
    }

    private Enumeration adjustVirtualProperties(Enumeration enumeration)
    {
        Vector vector = new Vector();
        while (enumeration.hasMoreElements())
            {
            StringProperty stringproperty = (StringProperty)enumeration.nextElement();
            if(stringproperty == Monitor.pAlertDisabled)
            {
                vector.add(pAlertDisable);
                vector.add(pAlertDisableTime);
                vector.add(pAlertDisableDesc);
                vector.add(pAlertDisableStartTime);
                vector.add(pAlertDisableEndTime);
                vector.add(pAlertDisableStartDate);
                vector.add(pAlertDisableEndDate);
            } else
            if(stringproperty == Monitor.pDisabled)
            {
                vector.add(pMonDisable);
                vector.add(pMonDisableTime);
                vector.add(pMonDisableDesc);
                vector.add(pMonDisableStartTime);
                vector.add(pMonDisableEndTime);
                vector.add(pMonDisableStartDate);
                vector.add(pMonDisableEndDate);
            } else
            if(stringproperty != Monitor.pDisabledDescription && stringproperty != Monitor.pTimedDisable)
            {
                vector.add(stringproperty);
            }
        }
        return vector.elements();
    }

    static 
    {
        $assertionsDisabled = !(APIMonitor.class).desiredAssertionStatus();
    }
}
