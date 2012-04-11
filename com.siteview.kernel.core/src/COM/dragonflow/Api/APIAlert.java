/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Api;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;

// Referenced classes of package COM.dragonflow.Api:
// APISiteView, SSInstanceProperty, SSPropertyDetails, Alert,
// SSAlertInstance, SSStringReturnValue, APIAlertCacheManager

public class APIAlert extends COM.dragonflow.Api.APISiteView
{

    public static final String DISABLED_EXPRESSION = "disabled and ";
    public static String REMOTE_PREFIX = "remote:";
    public static final String SSPARAM_MACHINE = "_machine";
    public static final String MASTER = "_master";
    static int alertArtID;
    static int historyArtID;
    static int helpArtID;
    static int siteviewArtID;
    static int groupArtID;
    static int alertDetailID;
    static int alertOnID;
    static int alertForID;
    static int alertDoID;
    static int alertDelID;
    static int alertEditID;
    static int alertAddID;
    static int alertPagerPrefsID;
    static int alertMailPrefsID;
    static int alertSNMPPrefsID;
    static int alertTestMailID;
    static int alertTestPagerID;
    static int alertTestSNMPID;
    static int alertErrorID;
    static int alertWarningID;
    static int alertGoodID;
    static int alertRunID;
    static int alertMailID;
    static int alertPageID;
    static int alertSNMPID;
    static int alertCustomID;
    static int alertAnyID;
    static int alertGroupID;
    static int alertNameID;
    static String french[];
    static String english[];
    String language[];
    static final boolean $assertionsDisabled; /* synthetic field */

    public APIAlert()
    {
        language = english;
    }

    public COM.dragonflow.Api.Alert create(String s, String s1, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        return addUpdate(false, "", s1, assinstanceproperty, s);
    }

    public COM.dragonflow.Api.Alert update(String s, String s1, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        return addUpdate(true, s, s1, assinstanceproperty, "");
    }

    private COM.dragonflow.Api.Alert addUpdate(boolean flag, String s, String s1, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[], String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.Alert alert = null;
        Object obj = null;
        try
        {
            if(!flag)
            {
                if(!COM.dragonflow.Api.Alert.hasValidTargetList(s1))
                {
                    throw new SiteViewParameterException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_ALERT_INVALID_TARGET_LIST, new String[] {
                        s1
                    });
                }
            } else
            if(s1 == null)
            {
                s1 = "";
            }
            String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
            COM.dragonflow.SiteView.Action action;
            if(flag)
            {
                alert = COM.dragonflow.Api.Alert.getInstance().getByID(COM.dragonflow.Utils.TextUtils.toLong(s));
                action = getAction(alert);
            } else
            {
                alert = COM.dragonflow.Api.Alert.getInstance().createAlert(as);
                COM.dragonflow.Api.Alert.getInstance().addToMap(alert);
                action = COM.dragonflow.SiteView.Action.createAction(s2);
            }
            if(!$assertionsDisabled && alert == null)
            {
                throw new AssertionError();
            }
            if(!$assertionsDisabled && action == null)
            {
                throw new AssertionError();
            }
            jgl.HashMap hashmap = processToOtherProperties(assinstanceproperty);
            jgl.HashMap hashmap1 = new HashMap();
            if(action != null && (hashmap1 = editActionProperties(action, hashmap, flag)).size() == 0)
            {
                String s3 = action.getActionString();
                String s4 = packCondition(hashmap, s3, hashmap1);
                String s5 = (String)hashmap.get("_UIContext");
                if(s5 != null && s5.length() > 0 && s1.length() > 0)
                {
                    alert.setContext(COM.dragonflow.Api.APISiteView.getContext(as, alert.getContext()));
                }
                s5 = (String)hashmap.get("_name");
                if(s5 != null && s5.length() > 0)
                {
                    alert.setName(s5);
                }
                if(flag)
                {
                    removeCondition(alert);
                    if(s1.length() > 0)
                    {
                        alert.update(as);
                    }
                }
                addCondition(alert, s4);
            } else
            {
                Enumeration enumeration = hashmap1.elements();
                throw new Exception((String)enumeration.nextElement());
            }
            COM.dragonflow.SiteView.DetectConfigurationChange detectconfigurationchange = COM.dragonflow.SiteView.DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "addUpdate"
            }, 0L, exception.getMessage());
        }
        return alert;
    }

    private COM.dragonflow.SiteView.Action getAction(COM.dragonflow.Api.Alert alert)
    {
        COM.dragonflow.SiteView.Action action = null;
        jgl.Array array = COM.dragonflow.SiteView.Platform.split('\t', alert.getCondStr());
        if(array.size() > 1)
        {
            String s = (String)array.at(1);
            action = COM.dragonflow.SiteView.Action.createAction(getActionClass(s));
            String s1 = "";
            if((s1 = alert.getContext()) != null)
            {
                action.setProperty(COM.dragonflow.SiteView.Action.pUIContext, s1);
            }
            if((s1 = alert.getName()) != null)
            {
                action.setProperty(COM.dragonflow.SiteView.Action.pName, s1);
            }
        }
        return action;
    }

    public void delete(String s)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try
        {
            COM.dragonflow.Api.Alert alert = COM.dragonflow.Api.Alert.getInstance().getByID(COM.dragonflow.Utils.TextUtils.toLong(s));
            if(!$assertionsDisabled && alert == null)
            {
                throw new AssertionError();
            }
            COM.dragonflow.Api.Alert.getInstance().removeFromMap(alert);
            removeCondition(alert);
            alert = null;
            COM.dragonflow.SiteView.DetectConfigurationChange detectconfigurationchange = COM.dragonflow.SiteView.DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "delete"
            }, 0L, exception.getMessage());
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @param s2
     * @return
     * @throws COM.dragonflow.SiteViewException.SiteViewException
     */
    public java.util.Vector test(String s, String s1, String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try {
        java.util.Vector vector;
        java.io.StringWriter stringwriter;
        java.io.PrintWriter printwriter;
        COM.dragonflow.SiteView.Monitor monitor;
            COM.dragonflow.Api.Alert alert = COM.dragonflow.Api.Alert.getInstance().getByID(java.lang.Long.parseLong(s));
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            monitor = null;
            if(s1 != null && !s1.equals(""))
            {
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().getGroup(s1);
                if(monitorgroup == null)
                {
                    throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                        "APIAlert", "test"
                    }, 0L, "Error: Could not find target monitor to test alert");
                }
                Enumeration enumeration = monitorgroup.getMonitors();
                COM.dragonflow.SiteView.Monitor monitor1;
                String s4;
                while (enumeration.hasMoreElements())
                    {
                    monitor1 = (COM.dragonflow.SiteView.Monitor)enumeration.nextElement();
                    s4 = monitor1.getProperty("_id");
                if (s2.equals(s4)) {
                monitor = monitor1;
                }
                    }
            } else
            {
                monitor = getTestMonitor(alert);
            }

            boolean flag = false;
        printwriter.print("Testing alert\n");
        if(monitor == null)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "test"
            }, 0L, "Error: Could not find target monitor to test alert");
        }
        Enumeration enumeration1 = monitor.getActionRules();
        while (enumeration1.hasMoreElements())
            {
            COM.dragonflow.SiteView.Rule rule = (COM.dragonflow.SiteView.Rule)enumeration1.nextElement();
            if(!rule.getProperty("_alertID").equals(s))
            {
                continue;
            }
            rule.doAction(monitor, printwriter);
            flag = true;
            break;
        } 
        
        if(!flag)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "test"
            }, 0L, "Error: Could not find alert " + s);
        }
        String s3 = stringwriter.getBuffer().toString();
        String as[] = COM.dragonflow.Utils.TextUtils.split(s3, "\n");
        vector = new Vector();
        for(int i = 0; i < as.length; i++)
        {
            vector.add(as[i]);
        }

        return vector;
        }
        catch (COM.dragonflow.SiteViewException.SiteViewException e) {
        e.fillInStackTrace();
        throw e;
        }
        catch (Exception e) {
        throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
            "APIAlert", "test"
        }, 0L, e.getMessage());
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param alert
     * @return
     */
     private COM.dragonflow.SiteView.Monitor getTestMonitor(COM.dragonflow.Api.Alert alert) {
        COM.dragonflow.SiteView.Monitor monitor = null;
            String s = alert.getIncludeFilter();
            if(s != null && !s.equals(""))
            {
                String as[] = COM.dragonflow.Utils.TextUtils.split(alert.getIncludeFilter(), ",");
                String as1[] = COM.dragonflow.Utils.TextUtils.split(as[0], " ");
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().getGroup(as1[0]);
                String s1 = as1[1];
                Enumeration enumeration = monitorgroup.getMonitors();
                COM.dragonflow.SiteView.Monitor monitor2;
                String s2;
                while (enumeration.hasMoreElements())
                    {
                    monitor2 = (COM.dragonflow.SiteView.Monitor)enumeration.nextElement();
                    s2 = monitor2.getProperty("_id");
                if (s1.equals(s2)) {
                monitor = monitor2;
                }
            }
            } else
            {
                COM.dragonflow.SiteView.Monitor monitor1 = alert.getMonitor();
                if((COM.dragonflow.SiteView.MonitorGroup.class).isInstance(monitor1))
                {
                    monitor = getFirstChildMonitor((COM.dragonflow.SiteView.MonitorGroup)monitor1);
                } else
                {
                    monitor = monitor1;
                }
            }
        return monitor;
    }

    private COM.dragonflow.SiteView.Monitor getFirstChildMonitor(COM.dragonflow.SiteView.MonitorGroup monitorgroup)
    {
        for(Enumeration enumeration = monitorgroup.getMonitors(); enumeration.hasMoreElements();)
        {
            COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)enumeration.nextElement();
            if(monitor instanceof COM.dragonflow.SiteView.MonitorGroup)
            {
                monitor = getFirstChildMonitor((COM.dragonflow.SiteView.MonitorGroup)monitor);
                if(monitor != null)
                {
                    return monitor;
                }
            } else
            if(monitor instanceof COM.dragonflow.SiteView.SubGroup)
            {
                COM.dragonflow.SiteView.MonitorGroup monitorgroup1 = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().getGroup(monitor.getProperty("_group"));
                monitor = getFirstChildMonitor(monitorgroup1);
                if(monitor != null)
                {
                    return monitor;
                }
            } else
            {
                return monitor;
            }
        }

        return null;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param action
     * @param hashmap
     * @param flag
     * @return
     */
    private jgl.HashMap editActionProperties(COM.dragonflow.SiteView.Action action, jgl.HashMap hashmap, boolean flag)
    {
        jgl.HashMap hashmap1 = new HashMap();
        jgl.Array array = action.getProperties();
        array = COM.dragonflow.Properties.StringProperty.sortByOrder(array);
        Enumeration enumeration = array.elements();

        while (enumeration.hasMoreElements())
            {
            COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            if(!stringproperty.isConfigurable)
            {
                continue;
            }
            if(stringproperty.isMultiLine)
            {
                String s = (String)hashmap.get(stringproperty.getName());
                String as[] = COM.dragonflow.Utils.TextUtils.split(s, "\r\n");
                if(flag && as.length > 0 || !flag)
                {
                    action.unsetProperty(stringproperty);
                    int i = 0;
                    while(i < as.length) 
                    {
                        String s3 = as[i];
                        s3 = action.verify(stringproperty, s3, null, hashmap1);
                        action.addProperty(stringproperty, s3);
                        i++;
                    }
                }
                continue;
            }
            if((stringproperty instanceof COM.dragonflow.Properties.ScalarProperty) && ((COM.dragonflow.Properties.ScalarProperty)stringproperty).multiple)
            {
                Enumeration enumeration1 = hashmap.values(stringproperty.getName());
                if((!flag || !enumeration1.hasMoreElements()) && flag)
                {
                    continue;
                }
                action.unsetProperty(stringproperty);

                java.lang.Object obj;

                while(enumeration1.hasMoreElements()) {
                     obj = enumeration1.nextElement();
                     if(obj instanceof String) {
                         action.addProperty(stringproperty, (String)obj);
                     } else if (obj instanceof String[]) {
                        String as1[] = (String[])obj;                  
                        for (int j = 0; j < as1.length; j ++) 
                        {
                            action.addProperty(stringproperty, as1[j]);
                        }
                     }
                }
                continue;
            }

            String s1 = (String)hashmap.get(stringproperty.getName());
            String s2 = "";
            if(!flag && s1 == null)
            {
                s1 = "";
            }
            if(s1 != null)
            {
                s2 = action.verify(stringproperty, s1, null, hashmap1);
            }
            if(s2 != null && s2.length() > 0)
            {
                action.setProperty(stringproperty, s2);
            }
        } 
        return hashmap1;
    }

    private jgl.HashMap processToOtherProperties(COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
    {
        jgl.HashMap hashmap = new HashMap(true);
        String s1 = "";
        for(int i = 0; i < assinstanceproperty.length; i++)
        {
            if(assinstanceproperty[i].getName().equals("toOther"))
            {
                s1 = (String)assinstanceproperty[i].getValue();
            } else
            {
                hashmap.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }
            if(!assinstanceproperty[i].getName().equals("_machine"))
            {
                continue;
            }
            String s = (String)assinstanceproperty[i].getValue();
            if(s == null)
            {
                continue;
            }
            String s2 = getHostname();
            if(s2 != null && !s2.equals(s))
            {
                hashmap.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }
        }

        if(s1.length() > 0)
        {
            hashmap.put("_to", s1);
        }
        return hashmap;
    }

    public COM.dragonflow.Api.SSInstanceProperty[] getClassAttributes(String s)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = null;
        try
        {
            jgl.HashMap hashmap = getClassAttribs(s);
            int i = 0;
            assinstanceproperty = new COM.dragonflow.Api.SSInstanceProperty[hashmap.size()];
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

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getClassAttributes"
            }, 0L, exception.getMessage());
        }
        return assinstanceproperty;
    }

    public COM.dragonflow.Api.SSPropertyDetails getClassPropertyDetails(String s, String s1, jgl.HashMap hashmap)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSPropertyDetails sspropertydetails = null;
        try
        {
            COM.dragonflow.Api.SSPropertyDetails asspropertydetails[] = getClassPropertiesDetails(s1, COM.dragonflow.Api.APISiteView.FILTER_ALL, hashmap);
            int i = 0;
            do
            {
                if(i >= asspropertydetails.length)
                {
                    break;
                }
                if(asspropertydetails[i].getName().equals(s))
                {
                    sspropertydetails = asspropertydetails[i];
                    break;
                }
                i++;
            } while(true);
            if(sspropertydetails == null)
            {
                throw new SiteViewParameterException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_PREFERENCE_NONEXISTANT_PROPERTY, new String[] {
                    s
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getClassPropertyDetails"
            }, 0L, exception.getMessage());
        }
        return sspropertydetails;
    }

    public COM.dragonflow.Api.SSPropertyDetails[] getClassPropertiesDetails(String s, int i, jgl.HashMap hashmap)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSPropertyDetails asspropertydetails[] = null;
        Object obj = null;
        try
        {
            String s1 = "";
            s1 = "COM.dragonflow.StandardAction." + s;
            java.lang.Class class1 = java.lang.Class.forName(s1);
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
            s1 = "COM.dragonflow.StandardAction." + s;
            jgl.Array array = getPropertiesForClass(siteviewobject, s1, "Action", i);
            jgl.Array array1 = getSynthesizedAlertProperties(null, null, null, i);
            asspropertydetails = new COM.dragonflow.Api.SSPropertyDetails[array.size() + array1.size()];
            for(int j = 0; j < array.size(); j++)
            {
                asspropertydetails[j] = getClassProperty((COM.dragonflow.Properties.StringProperty)array.at(j), (COM.dragonflow.SiteView.Action)siteviewobject, hashmap);
            }

            for(int k = 0; k < array1.size(); k++)
            {
                asspropertydetails[k + array.size()] = new SSPropertyDetails(((COM.dragonflow.Api.SSInstanceProperty)array1.at(k)).getName(), "TEXT", "", ((COM.dragonflow.Api.SSInstanceProperty)array1.at(k)).getLabel(), true, false, (String)((COM.dragonflow.Api.SSInstanceProperty)array1.at(k)).getValue(), new String[0], new String[0], "", true, false, 1000 + k, "", false, false, siteviewobject.getProperty(((COM.dragonflow.Api.SSInstanceProperty)array1.at(k)).getName()));
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getClassPropertiesDetails"
            }, 0L, exception.getMessage());
        }
        return asspropertydetails;
    }

    public COM.dragonflow.Api.SSPropertyDetails getInstancePropertyDetails(String s, String s1, String s2, String s3)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSPropertyDetails sspropertydetails = null;
        try
        {
            int i = s1.lastIndexOf("/");
            if(i != -1)
            {
                s1 = s1.substring(i + 1);
            }
            jgl.Array array = getConditions();
            if(array.size() > 0)
            {
                Enumeration enumeration = array.elements();
                while (enumeration.hasMoreElements())
                    {
                    jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
                    String s4 = COM.dragonflow.Utils.I18N.toDefaultEncoding((String)hashmap.get("group"));
                    if(s3.length() == 0 && s4.equals("_master") || s4.equals(s3))
                    {
                        String s5 = (String)hashmap.get("id");
                        String s6 = (String)hashmap.get("monitor");
                        if((s2.length() == 0 && s6.equals("_config") || s6.equals(s2)) && s5.equals(s1))
                        {
                            jgl.Array array1 = findCondition(COM.dragonflow.Utils.I18N.toDefaultEncoding(s4), s6, s5);
                            if(array1.size() > 1)
                            {
                                String s7 = (String)array1.at(1);
                                COM.dragonflow.SiteView.Action action = COM.dragonflow.SiteView.Action.createAction(getActionClass(s7));
                                jgl.Array array2 = new Array();
                                COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
                                getActionArguments(s7, array2, hashmapordered);
                                action.initializeFromArguments(array2, hashmapordered);
                                boolean flag = false;
                                jgl.Array array3 = action.getProperties();
                                array3 = COM.dragonflow.Properties.StringProperty.sortByOrder(array3);
                                enumeration = array3.elements();
                                while (enumeration.hasMoreElements())
                                    {
                                    COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                                    if(!flag && s.equals(stringproperty.getName()))
                                    {
                                        sspropertydetails = getClassProperty(stringproperty, action, new HashMap());
                                        flag = true;
                                    }
                                }
                                jgl.Array array4 = getSynthesizedAlertProperties(null, null, null, COM.dragonflow.Api.APISiteView.FILTER_ALL);
                                int j = 0;
                                while(j < array4.size()) 
                                {
                                    if(!flag && s.equals(((COM.dragonflow.Api.SSInstanceProperty)array4.at(j)).getName()))
                                    {
                                        sspropertydetails = new SSPropertyDetails(((COM.dragonflow.Api.SSInstanceProperty)array4.at(j)).getName(), "TEXT", "", ((COM.dragonflow.Api.SSInstanceProperty)array4.at(j)).getLabel(), true, false, (String)((COM.dragonflow.Api.SSInstanceProperty)array4.at(j)).getValue(), new String[0], new String[0], "", true, false, 1000 + j, "", false, false, action.getProperty(((COM.dragonflow.Api.SSInstanceProperty)array4.at(j)).getName()));
                                        flag = true;
                                    }
                                    j++;
                                }
                            }
                        }
                    }
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getInstancePropertyDetails"
            }, 0L, exception.getMessage());
        }
        return sspropertydetails;
    }

    public COM.dragonflow.Api.SVAlertInstance[] getInstances(String s, String s1, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SVAlertInstance assalertinstance[] = null;
        try
        {
            java.util.Vector vector = null;
            if(i == COM.dragonflow.Api.APISiteView.FILTER_ALERT_ASSOCIATED)
            {
                vector = COM.dragonflow.Api.Alert.getInstance().getAlertsAssociatedWithGroupOrMonitor(s1, s);
            } else
            {
                vector = COM.dragonflow.Api.Alert.getInstance().getAlertsResidingInGroupOrMonitor(s1, s);
            }
            Enumeration enumeration = vector.elements();
            java.util.Vector vector1 = new Vector();
            String s2;
            String s3;
            String s4;
            COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[];
            for(; enumeration.hasMoreElements(); vector1.addElement(new SVAlertInstance(s4, s3, s2, assinstanceproperty)))
            {
                COM.dragonflow.Api.Alert alert = (COM.dragonflow.Api.Alert)enumeration.nextElement();
                s2 = alert.getGroup();
                s3 = alert.getMonitorID();
                s4 = alert.getIDStr();
                assinstanceproperty = getInstanceProperties(s4, s3, s2, i);
            }

            assalertinstance = new COM.dragonflow.Api.SVAlertInstance[vector1.size()];
            for(int j = 0; j < vector1.size(); j++)
            {
                assalertinstance[j] = (COM.dragonflow.Api.SVAlertInstance)vector1.elementAt(j);
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getInstances"
            }, 0L, exception.getMessage());
        }
        return assalertinstance;
    }

    public COM.dragonflow.Api.SSInstanceProperty[] getInstanceProperties(String s, String s1, String s2, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = null;
        try
        {
            int j = s.lastIndexOf("/");
            if(j != -1)
            {
                s = s.substring(j + 1);
            }
            jgl.Array array = getConditions();
            if(array.size() > 0)
            {
                Enumeration enumeration = array.elements();
                while (enumeration.hasMoreElements())
                    {
                    jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
                    String s3 = COM.dragonflow.Utils.I18N.toDefaultEncoding((String)hashmap.get("group"));
                    if(s3.equals("_master") || s3.equals(s2))
                    {
                        String s4 = (String)hashmap.get("id");
                        String s5 = (String)hashmap.get("monitor");
                        if((s5.equals("_config") || s5.equals(s1)) && s4.equals(s))
                        {
                            jgl.Array array1 = findCondition(COM.dragonflow.Utils.I18N.toDefaultEncoding(s3), s5, s4);
                            if(array1.size() > 2)
                            {
                                String s6 = (String)array1.at(0);
                                String s7 = (String)array1.at(1);
                                packCondition(hashmap, s7, new HashMap());
                                COM.dragonflow.SiteView.Action action = COM.dragonflow.SiteView.Action.createAction(getActionClass(s7));
                                String s8 = "";
                                String s9 = getActionClass(s7);
                                if(array1.size() > 3)
                                {
                                    for(int k = 3; k < array1.size(); k++)
                                    {
                                        String s10 = (String)array1.at(k);
                                        if(s10.startsWith("_UIContext="))
                                        {
                                            s8 = s10.substring("_UIContext".length() + 1);
                                            continue;
                                        }
                                        if(s10.startsWith("_name="))
                                        {
                                            s9 = s10.substring("_name".length() + 1);
                                        }
                                    }

                                }
                                action.setProperty("_uniqueID", s);
                                action.setProperty("_UIContext", s8);
                                action.setProperty("_name", s9);
                                jgl.Array array2 = new Array();
                                COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
                                getActionArguments(s7, array2, hashmapordered);
                                action.initializeFromArguments(array2, hashmapordered);
                                assinstanceproperty = getPropertiesForAlertInstance(action, s6, hashmap, i);
                                COM.dragonflow.Api.SSInstanceProperty assinstanceproperty1[] = new COM.dragonflow.Api.SSInstanceProperty[assinstanceproperty.length + 1];
                                java.lang.System.arraycopy(assinstanceproperty, 0, assinstanceproperty1, 0, assinstanceproperty.length);
                                String s11 = COM.dragonflow.Api.Alert.getInstance().createTargetListStr(COM.dragonflow.Api.Alert.getInstance().getByID((new Long(s)).longValue()));
                                String as[] = COM.dragonflow.Utils.TextUtils.split(s11, ",");
                                for(int l = 0; l < as.length; l++)
                                {
                                    as[l] = as[l].trim();
                                }

                                assinstanceproperty1[assinstanceproperty1.length - 1] = new SSInstanceProperty("_targetList", "TargetList", as);
                                assinstanceproperty = assinstanceproperty1;
                            }
                        }
                    }
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getInstanceProperties"
            }, 0L, exception.getMessage());
        }
        return assinstanceproperty;
    }

    public COM.dragonflow.Api.SSInstanceProperty getInstanceProperty(String s, String s1, String s2, String s3)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty ssinstanceproperty = null;
        try
        {
            COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = null;
            int i = s1.lastIndexOf("/");
            if(i != -1)
            {
                s1 = s1.substring(i + 1);
            }
            jgl.Array array = getConditions();
            if(array.size() > 0)
            {
                Enumeration enumeration = array.elements();
                while (enumeration.hasMoreElements()) {
                    jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
                    String s4 = COM.dragonflow.Utils.I18N.toDefaultEncoding((String)hashmap.get("group"));
                    if(s3.length() == 0 && s4.equals("_master") || s4.equals(s3))
                    {
                        String s5 = (String)hashmap.get("id");
                        String s6 = (String)hashmap.get("monitor");
                        if((s2.length() == 0 && s6.equals("_config") || s6.equals(s2)) && s5.equals(s1))
                        {
                            jgl.Array array1 = findCondition(COM.dragonflow.Utils.I18N.toDefaultEncoding(s4), s6, s5);
                            if(array1.size() > 1)
                            {
                                String s7 = (String)array1.at(0);
                                String s8 = (String)array1.at(1);
                                packCondition(hashmap, s8, new HashMap());
                                COM.dragonflow.SiteView.Action action = COM.dragonflow.SiteView.Action.createAction(getActionClass(s8));
                                jgl.Array array2 = new Array();
                                COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
                                getActionArguments(s8, array2, hashmapordered);
                                action.initializeFromArguments(array2, hashmapordered);
                                assinstanceproperty = getPropertiesForAlertInstance(action, s7, hashmap, COM.dragonflow.Api.APISiteView.FILTER_ALL);
                            }
                        }
                    }
                }
            }
            
            for(int j = 0; j < assinstanceproperty.length; j++)
            {
                if(assinstanceproperty[j].getName().equals(s))
                {
                    ssinstanceproperty = assinstanceproperty[j];
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getInstanceProperty"
            }, 0L, exception.getMessage());
        }
        return ssinstanceproperty;
    }

    public COM.dragonflow.Api.SSStringReturnValue[] getAlertTypes()
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSStringReturnValue assstringreturnvalue[] = null;
        try
        {
            int i = findType("Alert");
            jgl.Array array = (jgl.Array)ssChildObjects.elementAt(i);
            assstringreturnvalue = new COM.dragonflow.Api.SSStringReturnValue[array.size()];
            for(int j = 0; j < array.size(); j++)
            {
                COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue = new SSStringReturnValue(((String[])array.at(j))[0]);
                int k = ssstringreturnvalue.getValue().lastIndexOf(".");
                if(k != -1)
                {
                    ssstringreturnvalue = new SSStringReturnValue(ssstringreturnvalue.getValue().substring(k + 1));
                }
                assstringreturnvalue[j] = ssstringreturnvalue;
            }

        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getAlertTypes"
            }, 0L, exception.getMessage());
        }
        return assstringreturnvalue;
    }

    public COM.dragonflow.Api.SSStringReturnValue getClassAttribute(String s, String s1)
        throws java.lang.Exception
    {
        jgl.HashMap hashmap = getClassAttribs(s);
        return new SSStringReturnValue((String)hashmap.get(s1));
    }

    private void removeCondition(COM.dragonflow.Api.Alert alert)
    {
        mungeCondition(alert, null);
    }

    private jgl.HashMap getClassAttribs(String s)
        throws java.lang.Exception
    {
        jgl.HashMap hashmap = null;
        String s1 = "";
        s1 = "COM.dragonflow.StandardAction." + s;
        java.lang.Class class1 = java.lang.Class.forName(s1);
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
        hashmap = siteviewobject.getClassProperties();
        return hashmap;
    }

    private COM.dragonflow.Api.SSPropertyDetails getClassProperty(COM.dragonflow.Properties.StringProperty stringproperty, COM.dragonflow.SiteView.Action action, jgl.HashMap hashmap)
        throws java.lang.Exception
    {
        boolean flag = false;
        String as[] = null;
        String as1[] = null;
        String s = "";
        String s1 = "";
        String s2 = "TEXT";
        try
        {
            COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
            httprequest.setUser(account);
            if((action instanceof COM.dragonflow.StandardAction.Run) && stringproperty.getName() != null && stringproperty.getName().equals("_machine") && hashmap.get("_machine") == null)
            {
                flag = true;
            }
            if(stringproperty.isPassword)
            {
                s2 = "PASSWORD";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ServerProperty)
            {
                s2 = "SERVER";
                boolean flag1 = true;
                boolean flag2 = false;
                boolean flag3 = true;
                if(action instanceof COM.dragonflow.SiteView.ServerAction)
                {
                    flag1 = false;
                    flag2 = true;
                }
                java.util.Vector vector = null;
                if(flag1)
                {
                    vector = getLocalServers();
                    vector = addServers(vector, "_remoteNTMachine", true);
                } else
                {
                    vector = new Vector();
                    java.net.InetAddress inetaddress = java.net.InetAddress.getLocalHost();
                    String s3 = inetaddress.getHostName();
                    vector.addElement(s3);
                    vector.addElement(s3);
                    if(flag2)
                    {
                        vector = addServers(vector, "_remoteNTMachine", true);
                    }
                }
                if(flag3)
                {
                    vector = addServers(vector, "_remoteMachine", false);
                }
                as = new String[vector.size() / 2];
                as1 = new String[vector.size() / 2];
                int i = 0;
                for(int k = 0; i < vector.size(); k++)
                {
                    as1[k] = (String)vector.elementAt(i);
                    as[k] = (String)vector.elementAt(i + 1);
                    i += 2;
                }

                s = "LIST";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScheduleProperty)
            {
                s2 = "SCHEDULE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScalarProperty)
            {
                s2 = "SCALAR";
                COM.dragonflow.Properties.ScalarProperty scalarproperty = (COM.dragonflow.Properties.ScalarProperty)stringproperty;
                java.lang.Class class1 = java.lang.Class.forName("COM.dragonflow.Page.monitorPage");
                COM.dragonflow.Page.CGI cgi = (COM.dragonflow.Page.CGI)class1.newInstance();
                cgi.initialize(httprequest, null);
                if(hashmap.get("_machine") != null)
                {
                    httprequest.setValue("_machine", (String)hashmap.get("_machine"));
                    action.setProperty("_machine", (String)hashmap.get("_machine"));
                }
                java.util.Vector vector1 = action.getScalarValues(scalarproperty, httprequest, cgi);
                as = new String[vector1.size() / 2];
                as1 = new String[vector1.size() / 2];
                int j = 0;
                for(int l = 0; j < vector1.size() / 2; l += 2)
                {
                    as1[j] = (String)vector1.elementAt(l);
                    as[j] = (String)vector1.elementAt(l + 1);
                    j++;
                }

                s = "LIST";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.RateProperty)
            {
                s2 = "RATE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.PercentProperty)
            {
                s2 = "PERCENT";
                s1 = ((COM.dragonflow.Properties.PercentProperty)stringproperty).getUnits();
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.FrequencyProperty)
            {
                s2 = "FREQUENCY";
                s1 = "seconds";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.FileProperty)
            {
                s2 = "FILE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.BooleanProperty)
            {
                s2 = "BOOLEAN";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.NumericProperty)
            {
                s2 = "NUMERIC";
                s1 = ((COM.dragonflow.Properties.NumericProperty)stringproperty).getUnits();
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_ALERT_EXCEPTION, new String[] {
                "APIAlert", "getClassProperty"
            }, 0L, exception.getMessage());
        }
        return new SSPropertyDetails(stringproperty.getName(), s2, stringproperty.getDescription(), stringproperty.getLabel(), stringproperty.isEditable, stringproperty.isMultivalued, "", as, as1, s, !stringproperty.isAdvanced, flag, stringproperty.getOrder(), s1, stringproperty.isAdvanced, stringproperty.isPassword, action.getProperty(stringproperty.getName()));
    }

    private jgl.Array getConditions()
        throws java.lang.Exception
    {
        COM.dragonflow.Api.APIAlertCacheManager apialertcachemanager = COM.dragonflow.Api.APIAlertCacheManager.getInstance();
        jgl.Array array = apialertcachemanager.getConditionsCache();
        if(array != null)
        {
            return array;
        }
        array = new Array();
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        addConditions(array, hashmap, "_master", "_config");
        jgl.Array array1 = new Array();
        jgl.Array array2 = getAllowedGroupIDs();
        for(Enumeration enumeration = array2.elements(); enumeration.hasMoreElements();)
        {
            String s = (String)enumeration.nextElement();
            jgl.Array array3 = ReadGroupFrames(s);
            Enumeration enumeration2 = getMonitors(array3);
            jgl.HashMap hashmap1 = null;
            if(enumeration2.hasMoreElements())
            {
                hashmap1 = (jgl.HashMap)enumeration2.nextElement();
            } else
            {
                hashmap1 = new HashMap();
            }
            addConditions(array, hashmap1, COM.dragonflow.Utils.I18N.toNullEncoding(s), "_config");
            while(enumeration2.hasMoreElements()) 
            {
                jgl.HashMap hashmap2 = (jgl.HashMap)enumeration2.nextElement();
                addConditions(array1, hashmap2, COM.dragonflow.Utils.I18N.toNullEncoding(s), (String)hashmap2.get("_id"));
            }
        }

        for(Enumeration enumeration1 = array1.elements(); enumeration1.hasMoreElements(); array.add(enumeration1.nextElement())) { }
        apialertcachemanager.setConditionsCache(array);
        return array;
    }

    private String getActionClass(String s)
    {
        int i = s.indexOf(" ");
        if(i >= 0)
        {
            return COM.dragonflow.Utils.TextUtils.toInitialUpper(s.substring(0, i));
        } else
        {
            return COM.dragonflow.Utils.TextUtils.toInitialUpper(s);
        }
    }

    void addConditions(jgl.Array array, jgl.HashMap hashmap, String s, String s1)
    {
        COM.dragonflow.Utils.I18N.test(s, 1);
        for(Enumeration enumeration = hashmap.values("_alertCondition"); enumeration.hasMoreElements();)
        {
            String s2 = (String)enumeration.nextElement();
            try
            {
                jgl.HashMap hashmap1 = parseCondition(s2, hashmap, s, s1);
                array.add(hashmap1);
            }
            catch(java.lang.Exception exception) { }
        }

    }

    private jgl.HashMap parseCondition(String s, jgl.HashMap hashmap, String s1, String s2)
        throws java.lang.Exception
    {
        jgl.HashMap hashmap1 = new HashMap();
        hashmap1.put("raw", s);
        hashmap1.put("group", s1);
        hashmap1.put("monitor", s2);
        jgl.Array array = COM.dragonflow.SiteView.Platform.split('\t', s);
        int i = array.size();
        String s3 = (String)array.at(0);
        String s4 = (String)array.at(1);
        String s5 = (String)array.at(2);
        hashmap1.put("expression", s3);
        hashmap1.put("id", s5);
        hashmap1.put("action", s4);
        if(i >= 3)
        {
            for(int j = 3; j < i; j++)
            {
                String s6 = (String)array.at(j);
                if(s6.startsWith("_UIContext="))
                {
                    hashmap1.put("_UIContext", s6.substring("_UIContext".length() + 1));
                    continue;
                }
                if(s6.startsWith("_name="))
                {
                    hashmap1.put("_name", s6.substring("_name".length() + 1));
                }
            }

        }
        COM.dragonflow.SiteView.Action action = COM.dragonflow.SiteView.Action.createAction(getActionClass(s4));
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSiteView();
        if(!s1.equals("_master"))
        {
            siteviewobject = siteviewobject.getElement(COM.dragonflow.Utils.I18N.toDefaultEncoding(s1));
        }
        action.setOwner(siteviewobject);
        String s7 = siteviewobject.getFullID();
        if(!s2.equals("_config"))
        {
            s7 = s7 + "/" + s2;
        }
        hashmap1.put("fullID", s7 + "/" + s5);
        jgl.Array array1 = new Array();
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        getActionArguments(s4, array1, hashmapordered);
        action.initializeFromArguments(array1, hashmapordered);
        String s8 = getCategory(s3);
        hashmap1.put("category", s8);
        String s9 = getCategoryName(s8);
        int k = getErrorCount(s3);
        if(k != -1)
        {
            s9 = s9 + " x " + k;
        }
        int l = getMultipleErrorCount(s3);
        if(l != -1)
        {
            int i1 = getAlwaysErrorCount(s3);
            s9 = s9 + " at least x " + i1 + ", every " + l;
        } else
        {
            int j1 = getAlwaysErrorCount(s3);
            if(k == -1 && j1 > 1)
            {
                s9 = s9 + " at least x " + j1;
            }
        }
        int k1 = getMaxErrorCount(s3);
        if(k1 != -1)
        {
            s9 = "group error x " + k1;
        }
        if(s3.indexOf("group.monitorsInError") >= 0)
        {
            s9 = "all in error";
        }
        String s10 = getStatusString(s3);
        if(s10.length() > 0)
        {
            s9 = s9 + ", status \"" + s10 + "\"";
        }
        String s11 = getDisabled(s3);
        if(s11.length() > 0)
        {
            s9 = "<B>(" + s11 + ")</B><BR>" + s9;
            hashmap1.put("disabled", "true");
        }
        hashmap1.put("on", s9);
        hashmap1.put("command", action.getClassProperty("label"));
        hashmap1.put("do", action.getActionDescription());
        String s12 = getNameString(s3);
        String s13 = conditionName(s1, hashmap, array);
        if(s12.length() > 0)
        {
            s13 = s13 + ", name \"" + s12 + "\"";
        }
        hashmap1.put("for", s13);
        hashmap1.put("groupName", displayName(s1, array));
        return hashmap1;
    }

    private jgl.Array findCondition(String s, String s1, String s2)
        throws java.lang.Exception
    {
        jgl.Array array = ReadGroupFrames(s);
        jgl.HashMap hashmap = findMonitor(array, s1);
        for(Enumeration enumeration = hashmap.values("_alertCondition"); enumeration.hasMoreElements();)
        {
            String s3 = (String)enumeration.nextElement();
            jgl.Array array1 = COM.dragonflow.SiteView.Platform.split('\t', s3);
            if(s2.equals(array1.at(2)))
            {
                return array1;
            }
        }

        return null;
    }

    private jgl.HashMap findMonitor(jgl.Array array, String s)
        throws java.lang.Exception
    {
        int i = findMonitorIndex(array, s);
        return (jgl.HashMap)array.at(i);
    }

    private String conditionName(String s, jgl.HashMap hashmap, jgl.Array array)
    {
        String s2 = null;
        if(array.size() > 3)
        {
            s2 = (String)array.at(3);
            if(s2.startsWith("_UIContext"))
            {
                s2 = "";
            }
        }
        String s1;
        if(s.equals("_master"))
        {
            if(s2 != null && s2.length() > 0)
            {
                s1 = "multiple monitors";
            } else
            {
                s1 = "any monitor";
            }
        } else
        if(isGroup(hashmap))
        {
            if(s2 != null && s2.length() > 0)
            {
                s1 = "multiple monitors";
            } else
            {
                s1 = "any monitor in group";
            }
        } else
        {
            s1 = (String)hashmap.get("_name");
        }
        if(s1.equals("multiple monitors"))
        {
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            StringBuffer stringbuffer = new StringBuffer();
            jgl.Array array1 = COM.dragonflow.SiteView.Platform.split(',', s2);
            for(int i = 0; i < array1.size(); i++)
            {
                String s3 = (String)array1.at(i);
                if(s3.indexOf(' ') < 0)
                {
                    continue;
                }
                COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)siteviewgroup.getElement(COM.dragonflow.Utils.I18N.toDefaultEncoding(s3.replace(' ', '/')));
                if(monitor != null)
                {
                    COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer, monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName));
                }
            }

            if(stringbuffer.length() == 0)
            {
                stringbuffer.append(s1);
            }
            return stringbuffer.toString();
        } else
        {
            return s1;
        }
    }

    void getActionArguments(String s, jgl.Array array, jgl.HashMap hashmap)
    {
        int i = s.indexOf(" ");
        String as[];
        if(i >= 0)
        {
            as = COM.dragonflow.Utils.TextUtils.split(s.substring(i + 1, s.length()));
        } else
        {
            as = new String[0];
        }
        for(int j = 0; j < as.length; j++)
        {
            int k = as[j].indexOf('=');
            if(k == -1)
            {
                as[j] = COM.dragonflow.Utils.TextUtils.replaceString(as[j], COM.dragonflow.SiteView.Action.EQUALS_SUBTITUTE, "=");
                array.add(as[j]);
            } else
            {
                as[j] = COM.dragonflow.Utils.TextUtils.replaceString(as[j], COM.dragonflow.SiteView.Action.EQUALS_SUBTITUTE, "=");
                hashmap.add(as[j].substring(0, k), as[j].substring(k + 1));
            }
        }

    }

    private COM.dragonflow.Api.SSInstanceProperty[] getPropertiesForAlertInstance(COM.dragonflow.SiteView.Action action, String s, jgl.HashMap hashmap, int i)
        throws java.lang.Exception
    {
        jgl.Array array = new Array();
        jgl.Array array1 = action.getProperties();
        array1 = COM.dragonflow.Properties.StringProperty.sortByOrder(array1);
        Enumeration enumeration = array1.elements();
        while (enumeration.hasMoreElements()) {
            boolean flag = false;
            COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            java.lang.Class class1 = action.getClass();
            String s1 = class1.getName();
            flag = returnProperty(stringproperty, i, action, s1);
            if(flag)
            {
                array.add(stringproperty);
            }
        } 
        jgl.Array array2 = getSynthesizedAlertProperties(action, s, hashmap, i);
        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = new COM.dragonflow.Api.SSInstanceProperty[array.size() + array2.size()];
        int j = 0;
        for(Enumeration enumeration1 = array.elements(); enumeration1.hasMoreElements(); j++)
        {
            COM.dragonflow.Properties.StringProperty stringproperty1 = (COM.dragonflow.Properties.StringProperty)enumeration1.nextElement();
            String s2 = stringproperty1.getName();
            String s3 = GetPropertyLabel(stringproperty1, true);
            if((stringproperty1 instanceof COM.dragonflow.Properties.ScalarProperty) && ((COM.dragonflow.Properties.ScalarProperty)stringproperty1).multiple)
            {
                Enumeration enumeration2 = action.getMultipleValues(stringproperty1);
                java.util.Vector vector = new Vector();
                java.util.Vector vector1 = new Vector();
                boolean flag1 = false;
                while(enumeration2.hasMoreElements()) 
                {
                    String s6 = (String)enumeration2.nextElement();
                    if(s2.equals("_to") && s6.indexOf(":") == -1)
                    {
                        flag1 = true;
                        vector1.add(s6);
                    } else
                    {
                        vector.add(s6);
                    }
                }
                if(flag1)
                {
                    String s7 = "";
                    for(int l = 0; l < vector1.size(); l++)
                    {
                        s7 = s7 + (String)vector1.elementAt(l);
                    }

                    assinstanceproperty[j] = new SSInstanceProperty(stringproperty1.getName(), s3, "");
                    array2.put(0, new SSInstanceProperty("toOther", s7));
                    continue;
                }
                String as[] = new String[vector.size()];
                for(int i1 = 0; i1 < vector.size(); i1++)
                {
                    as[i1] = (String)vector.elementAt(i1);
                }

                assinstanceproperty[j] = new SSInstanceProperty(stringproperty1.getName(), s3, as);
                continue;
            }
            String s4 = action.getProperty(stringproperty1);
            String s5 = (String)hashmap.get(s2);
            if(s5 != null && s5.length() > 0)
            {
                s4 = s5;
            }
            if(s4 == null)
            {
                s4 = "";
            }
            if(stringproperty1.isPassword)
            {
                s4 = COM.dragonflow.Utils.TextUtils.obscure(s4);
            }
            assinstanceproperty[j] = new SSInstanceProperty(stringproperty1.getName(), s3, s4);
        }

        for(int k = 0; k < array2.size(); k++)
        {
            assinstanceproperty[j + k] = (COM.dragonflow.Api.SSInstanceProperty)array2.at(k);
        }

        return assinstanceproperty;
    }

    private jgl.Array getSynthesizedAlertProperties(COM.dragonflow.SiteView.Action action, String s, jgl.HashMap hashmap, int i)
        throws java.lang.Exception
    {
        jgl.Array array = new Array();
        if(i == FILTER_CONFIGURATION_ADD_ALL || i == FILTER_CONFIGURATION_ADD_BASIC || i == FILTER_CONFIGURATION_ADD_ADVANCED || i == FILTER_CONFIGURATION_EDIT_ALL || i == FILTER_CONFIGURATION_EDIT_BASIC || i == FILTER_CONFIGURATION_EDIT_ADVANCED || i == FILTER_CONFIGURATION_ALL || i == COM.dragonflow.Api.APISiteView.FILTER_ALL)
        {
            String s1 = "undo";
            String s3 = "";
            String s5 = "";
            String s8 = "";
            String s10 = "";
            String s11 = "";
            String s12 = "";
            String s13 = "";
            int j = -1;
            int k = -1;
            int l = -1;
            int i1 = -1;
            int j1 = -1;
            int k1 = -1;
            if(s != null)
            {
                int l1 = s.indexOf("and monitorDoneTime >=");
                if(l1 > -1)
                {
                    int i2 = s.indexOf("monitorDoneTime <=");
                    if(i2 == -1)
                    {
                        String s15 = s.substring(l1);
                        s15 = "or " + s15.substring(4);
                        String s16 = "and monitorDoneTime <= " + COM.dragonflow.SiteView.Platform.timeMillis();
                        s = s.substring(0, l1) + s16 + " " + s15;
                    }
                }
                if(s5.length() == 0)
                {
                    s5 = getCategory(s);
                }
                j = getErrorCount(s);
                k = getAlwaysErrorCount(s);
                l = getMultipleErrorCount(s);
                i1 = getAlwaysErrorCount(s);
                k1 = getPreviousErrorCount(s);
                j1 = getMaxErrorCount(s);
                s8 = getNameString(s);
                s11 = getClassString(s);
                s10 = getStatusString(s);
                s12 = getDisabled(s);
                if(s.indexOf("monitorDoneTime") >= 0)
                {
                    s13 = s.substring(s.indexOf("monitorDoneTime"));
                }
            }
            if(j == -1 && l == -1)
            {
                s3 = "always";
            }
            if(j != -1)
            {
                s3 = "count";
            }
            if(l != -1)
            {
                s3 = "multiple";
            }
            if(j1 != -1)
            {
                s3 = "maxErrors";
            }
            if(s != null && s.indexOf("group.monitorsInError") >= 0)
            {
                s3 = "allErrors";
            }
            byte byte0 = -1;
            if(s5.equals("good"))
            {
                if(k1 != -1)
                {
                    byte0 = 1;
                } else
                {
                    k1 = 2;
                }
            }
            String s14 = "0";
            long l2 = 0L;
            long l3 = 0L;
            String s17 = "";
            String s18 = "";
            String s19 = "";
            String s20 = "";
            if(s12.length() > 0)
            {
                if(s12.equals("disabled"))
                {
                    s1 = "permanent";
                } else
                if(s12.startsWith("disabled until"))
                {
                    if(isTimedDisable(s13))
                    {
                        s1 = "timed";
                        s14 = "10";
                    } else
                    if(isScheduledDisable(s13))
                    {
                        s1 = "schedule";
                    }
                    l2 = getDisableScheduleTime("startTimeTime", s13);
                    l3 = getDisableScheduleTime("endTimeTime", s13);
                    s17 = COM.dragonflow.Utils.TextUtils.dateToMilitaryTime(new Date(l2));
                    s18 = COM.dragonflow.Utils.TextUtils.dateToMilitaryTime(new Date(l3));
                    s19 = COM.dragonflow.Utils.TextUtils.prettyDatePart(new Date(l2), false);
                    s20 = COM.dragonflow.Utils.TextUtils.prettyDatePart(new Date(l3), false);
                    long l4 = getDisableScheduleTime("endTimeTime", s13);
                    long l5 = l4 - COM.dragonflow.SiteView.Platform.timeMillis();
                    l5 /= 1000L;
                    l5 /= 60L;
                    s14 = (new Long(l5)).toString();
                    if(s1.equals("schedule"))
                    {
                        s14 = "";
                    }
                }
            }
            if(hashmap != null && hashmap.get("category") != null)
            {
                s5 = (String)hashmap.get("category");
            }
            array.add(new SSInstanceProperty("toOther", ""));
            array.add(new SSInstanceProperty("when", "When", s3));
            array.add(new SSInstanceProperty("category", "Category", s5));
            if(j != -1)
            {
                array.add(new SSInstanceProperty("errorCount", "ErrorCount", (new Integer(j)).toString()));
            }
            if(k != -1)
            {
                array.add(new SSInstanceProperty("alwaysErrorCount", "AlwaysErrorCount", (new Integer(k)).toString()));
            }
            if(i1 != -1)
            {
                array.add(new SSInstanceProperty("multipleStartCount", "MultipleStartCount", (new Integer(i1)).toString()));
            }
            if(l != -1)
            {
                array.add(new SSInstanceProperty("multipleErrorCount", "MultipleErrorCount", (new Integer(l)).toString()));
            }
            if(k1 != -1)
            {
                array.add(new SSInstanceProperty("previousErrorCount", "PreviousErrorCount", (new Integer(k1)).toString()));
            }
            if(j1 != -1)
            {
                array.add(new SSInstanceProperty("maxErrorCount", "MaxErrorCount", (new Integer(j1)).toString()));
            }
            if(s8.length() > 0)
            {
                array.add(new SSInstanceProperty("nameMatchString", "NameMatchString", s8));
            }
            if(s10.length() > 0)
            {
                array.add(new SSInstanceProperty("statusMatchString", "StatusMatchString", s10));
            }
            if(s11.length() > 0)
            {
                array.add(new SSInstanceProperty("classMatchString", "ClassMatchString", s11));
            }
            if(s1.length() > 0)
            {
                array.add(new SSInstanceProperty("alertDisable", "AlertDisable", s1));
            }
            if(byte0 > -1)
            {
                array.add(new SSInstanceProperty("usePreviousErrorCount", "UsePreviousErrorCount", "on"));
            } else
            {
                array.add(new SSInstanceProperty("usePreviousErrorCount", "UsePreviousErrorCount", ""));
            }
            array.add(new SSInstanceProperty("disableAlertUnits", "DisableAlertUnits", "seconds"));
            if(l2 > 0L)
            {
                array.add(new SSInstanceProperty("startTimeTime", "StartTimeTime", s17));
                if(s19.length() > 0)
                {
                    array.add(new SSInstanceProperty("startTimeDate", "StartTimeDate", s19));
                }
            }
            if(s1.equals("schedule"))
            {
                array.add(new SSInstanceProperty("disableAlertTime", "DisableAlertTime", ""));
            } else
            if(s14.length() > 0)
            {
                array.add(new SSInstanceProperty("disableAlertTime", "DisableAlertTime", (new Integer(s14)).toString()));
            }
            if(l3 > 0L)
            {
                array.add(new SSInstanceProperty("endTimeTime", "EndTimeTime", s18));
            }
            if(s20.length() > 0)
            {
                array.add(new SSInstanceProperty("endTimeDate", "EndTimeDate", s20));
            }
            if(s1.equals("undo"))
            {
                array.add(new SSInstanceProperty("disableAlertTime", "DisableAlertTime", ""));
                array.add(new SSInstanceProperty("endTimeTime", "EndTimeTime", ""));
                array.add(new SSInstanceProperty("endTimeDate", "EndTimeDate", ""));
                array.add(new SSInstanceProperty("startTimeTime", "StartTimeTime", ""));
                array.add(new SSInstanceProperty("startTimeDate", "StartTimeDate", ""));
            }
        }
        if(i != COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL && i != COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_BASIC && i != COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ADVANCED && i != COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL && i != COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_BASIC && i != COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ADVANCED)
        {
            String s2 = "";
            String s4 = "";
            String s6 = "";
            String s9 = "";
            if(hashmap != null)
            {
                if(action != null)
                {
                    String s7 = (String)action.getClassProperty("name");
                    s9 = (String)action.getClassProperty("class");
                }
                if(hashmap.get("command") != null)
                {
                    s2 = (String)hashmap.get("command");
                }
                if(hashmap.get("id") != null)
                {
                    s4 = (String)hashmap.get("id");
                }
            }
            array.add(new SSInstanceProperty("command", "Command", s2));
            array.add(new SSInstanceProperty("_id", "Id", s4));
            array.add(new SSInstanceProperty("_class", "Class", s9));
        }
        return array;
    }

    private String getCategory(String s)
    {
        if(s.indexOf("category eq 'error'") >= 0)
        {
            return "error";
        }
        if(s.indexOf("category eq 'good'") >= 0)
        {
            return "good";
        } else
        {
            return "warning";
        }
    }

    private String getString(int i)
    {
        return language[i];
    }

    private String getCategoryName(String s)
    {
        if(s.equals("error"))
        {
            return getString(alertErrorID);
        }
        if(s.equals("good"))
        {
            return getString(alertGoodID);
        } else
        {
            return getString(alertWarningID);
        }
    }

    private boolean isGroup(jgl.HashMap hashmap)
    {
        return hashmap.get("_id") == null;
    }

    private COM.dragonflow.SiteView.SiteViewObject getSiteView()
    {
        return COM.dragonflow.SiteView.Portal.getSiteViewForID("");
    }

    private String displayName(String s, jgl.Array array)
        throws java.lang.Exception
    {
        String s1 = null;
        if(array.size() > 3)
        {
            s1 = (String)array.at(3);
            if(s1.startsWith("_UIContext"))
            {
                s1 = "";
            }
        }
        if(s.equals("_master"))
        {
            if(COM.dragonflow.Utils.TextUtils.getValue(COM.dragonflow.SiteView.MasterConfig.getMasterConfig(), "_alertGroupDisplay").length() > 0)
            {
                if(s1 != null && s1.length() > 0)
                {
                    return "multiple groups";
                } else
                {
                    return "all groups";
                }
            }
            if(s1 != null && s1.length() > 0)
            {
                COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSiteView();
                StringBuffer stringbuffer = new StringBuffer();
                jgl.Array array1 = COM.dragonflow.SiteView.Platform.split(',', s1);
                for(int i = 0; i < array1.size(); i++)
                {
                    String s2 = (String)array1.at(i);
                    String as[] = COM.dragonflow.Utils.TextUtils.split(s2);
                    String s4 = as[0];
                    COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewobject.getElement(s4);
                    if(monitorgroup == null)
                    {
                        continue;
                    }
                    if(as.length > 1)
                    {
                        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)monitorgroup.getElementByID(as[1]);
                        if(monitor != null)
                        {
                            COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer, COM.dragonflow.Page.CGI.getGroupFullName(getGroupIDFull(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pID))) + ": " + monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName));
                        }
                    } else
                    {
                        COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer, COM.dragonflow.Page.CGI.getGroupFullName(getGroupIDFull(monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pID))));
                    }
                }

                return stringbuffer.toString();
            } else
            {
                return "all groups";
            }
        }
        if(!COM.dragonflow.SiteView.Platform.isStandardAccount(account) && s.equals(account))
        {
            if(s1 != null && s1.length() > 0)
            {
                COM.dragonflow.SiteView.SiteViewObject siteviewobject1 = getSiteView();
                StringBuffer stringbuffer1 = new StringBuffer();
                jgl.Array array2 = COM.dragonflow.SiteView.Platform.split(',', s1);
                for(int j = 0; j < array2.size(); j++)
                {
                    String s3 = (String)array2.at(j);
                    String as1[] = COM.dragonflow.Utils.TextUtils.split(s3);
                    String s5 = as1[0];
                    COM.dragonflow.SiteView.MonitorGroup monitorgroup1 = (COM.dragonflow.SiteView.MonitorGroup)siteviewobject1.getElement(s5);
                    if(monitorgroup1 == null)
                    {
                        continue;
                    }
                    if(as1.length > 1)
                    {
                        COM.dragonflow.SiteView.Monitor monitor1 = (COM.dragonflow.SiteView.Monitor)monitorgroup1.getElementByID(as1[1]);
                        if(monitor1 != null)
                        {
                            COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer1, COM.dragonflow.Page.CGI.getGroupFullName(getGroupIDFull(monitorgroup1.getProperty(COM.dragonflow.SiteView.Monitor.pID))) + ": " + monitor1.getProperty(COM.dragonflow.SiteView.Monitor.pName));
                        }
                    } else
                    {
                        COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer1, COM.dragonflow.Page.CGI.getGroupFullName(getGroupIDFull(monitorgroup1.getProperty(COM.dragonflow.SiteView.Monitor.pID))));
                    }
                }

                return stringbuffer1.toString();
            } else
            {
                return account;
            }
        } else
        {
            return getGroupFullName(COM.dragonflow.Utils.I18N.toDefaultEncoding(getGroupIDFull(s)));
        }
    }

    private String getGroupIDFull(String s)
    {
        return s;
    }

    private String getGroupFullName(String s)
        throws java.lang.Exception
    {
        COM.dragonflow.Utils.I18N.test(s, 0);
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = COM.dragonflow.SiteView.Portal.getSiteViewForID(s);
        COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor)siteviewobject.getElementByID(getGroupIDRelative(s));
        return getGroupPath(monitor, s, false);
    }

    private String getGroupIDRelative(String s)
    {
        if(COM.dragonflow.SiteView.Portal.isPortalID(s))
        {
            s = COM.dragonflow.SiteView.Portal.getGroupID(s);
        }
        return s;
    }

    private String getGroupPath(COM.dragonflow.SiteView.Monitor monitor, String s, boolean flag)
        throws java.lang.Exception
    {
        COM.dragonflow.Utils.I18N.test(s, 0);
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = COM.dragonflow.SiteView.Portal.getSiteViewForID(s);
        String s1 = "";
        do
        {
            if(s1.length() != 0)
            {
                s1 = ": " + s1;
            }
            String s2 = getGroupName(monitor, s);
            s = COM.dragonflow.HTTP.HTTPRequest.encodeString(s);
            if(flag && s1.length() != 0)
            {
                s1 = "<A HREF=Detail" + s + ".html>" + s2 + "</a>" + s1;
            } else
            {
                s1 = s2 + s1;
            }
            if(monitor == null)
            {
                break;
            }
            s = COM.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty("_parent"));
            if(s == null || s.length() == 0)
            {
                break;
            }
            monitor = (COM.dragonflow.SiteView.Monitor)siteviewobject.getElement(s);
        } while(true);
        return s1;
    }

    private String getGroupName(COM.dragonflow.SiteView.Monitor monitor, String s)
        throws java.lang.Exception
    {
        COM.dragonflow.Utils.I18N.test(s, 0);
        String s1;
        if(monitor == null)
        {
            s1 = COM.dragonflow.Utils.I18N.toNullEncoding(s);
            s1 = "MISSING GROUP (" + s1 + ")";
        } else
        {
            s1 = getGroupName(s);
        }
        return s1;
    }

    private String getGroupName(String s)
        throws java.lang.Exception
    {
        COM.dragonflow.Utils.I18N.test(s, 0);
        jgl.HashMap hashmap = null;
        jgl.Array array = ReadGroupFrames(s);
        Enumeration enumeration = array.elements();
        if(enumeration.hasMoreElements())
        {
            hashmap = (jgl.HashMap)enumeration.nextElement();
        } else
        {
            hashmap = new HashMap();
        }
        return getGroupName(hashmap, s);
    }

    private String getGroupName(jgl.HashMap hashmap, String s)
    {
        String s1 = null;
        if(hashmap != null)
        {
            s1 = (String)hashmap.get("_name");
        }
        if(s1 == null || s1.equals("config") || s1.length() == 0)
        {
            s1 = COM.dragonflow.Page.CGI.getGroupFullName(s);
        }
        return s1;
    }

    private jgl.Array getAllowedGroupIDs()
    {
        return getAllowedGroupIDsForAccount();
    }

    private jgl.Array getAllowedGroupIDsForAccount()
    {
        jgl.Array array = COM.dragonflow.Utils.I18N.toDefaultArray(COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().getGroupIDs());
        return filterGroupsForAccount(array);
    }

    private jgl.Array filterGroupsForAccount(jgl.Array array)
    {
        jgl.Array array1 = getGroupFilterForAccount("");
        if(array1 != null && array1.size() != 0)
        {
            Enumeration enumeration = array.elements();
            array = new Array();
            while (enumeration.hasMoreElements()) {
                String s = (String)enumeration.nextElement();
                if(allowedByGroupFilter(s, array1))
                {
                    array.add(s);
                }
            } 
        }
        return array;
    }

    private String getStatusString(String s)
    {
        return getStringParameter(s, "stateString contains ");
    }

    private String getNameString(String s)
    {
        return getStringParameter(s, "_name contains ");
    }

    private String getStringParameter(String s, String s1)
    {
        int i = s.indexOf(s1);
        if(i >= 0)
        {
            byte byte0 = 32;
            i += s1.length();
            if(s.charAt(i) == '\'')
            {
                byte0 = 39;
                i++;
            }
            StringBuffer stringbuffer = new StringBuffer();
            char c = ' ';
            do
            {
                if(i >= s.length())
                {
                    break;
                }
                char c1 = s.charAt(i++);
                if(c1 == byte0 && c != 92)
                {
                    break;
                }
                stringbuffer.append(c1);
                c = c1;
            } while(true);
            return stringbuffer.toString();
        } else
        {
            return "";
        }
    }

    private int getErrorCount(String s)
    {
        String s1 = "errorCount == ";
        int i = s.indexOf(s1);
        if(i < 0)
        {
            s1 = "warningCount == ";
            i = s.indexOf(s1);
        }
        if(i < 0)
        {
            s1 = "goodCount == ";
            i = s.indexOf(s1);
        }
        if(i != -1)
        {
            return java.lang.Math.max(0, COM.dragonflow.Utils.TextUtils.readInteger(s, i + s1.length()));
        } else
        {
            return -1;
        }
    }

    private int getAlwaysErrorCount(String s)
    {
        String s1 = "errorCount >= ";
        int i = s.indexOf(s1);
        if(i < 0)
        {
            s1 = "warningCount >= ";
            i = s.indexOf(s1);
        }
        if(i < 0)
        {
            s1 = "goodCount >= ";
            i = s.indexOf(s1);
        }
        if(i != -1)
        {
            return java.lang.Math.max(0, COM.dragonflow.Utils.TextUtils.readInteger(s, i + s1.length()));
        } else
        {
            return -1;
        }
    }

    private int getMultipleErrorCount(String s)
    {
        String s1 = "errorCount multipleOf ";
        int i = s.indexOf(s1);
        if(i < 0)
        {
            s1 = "warningCount multipleOf ";
            i = s.indexOf(s1);
        }
        if(i < 0)
        {
            s1 = "goodCount multipleOf ";
            i = s.indexOf(s1);
        }
        if(i != -1)
        {
            return java.lang.Math.max(0, COM.dragonflow.Utils.TextUtils.readInteger(s, i + s1.length()));
        } else
        {
            return -1;
        }
    }

    private int getMaxErrorCount(String s)
    {
        String s1 = "maxErrorCount == ";
        int i = s.indexOf(s1);
        if(i >= 0)
        {
            return java.lang.Math.max(0, COM.dragonflow.Utils.TextUtils.readInteger(s, i + s1.length()));
        } else
        {
            return -1;
        }
    }

    private String getDisabled(String s)
    {
        if(s.startsWith("disabled and "))
        {
            return "disabled";
        }
        if(s.indexOf("monitorDoneTime") >= 0)
        {
            String s1 = ">=";
            String s2 = "<=";
            String s3 = s.substring(s.indexOf("monitorDoneTime"));
            java.util.StringTokenizer stringtokenizer = new StringTokenizer(s3, " ");
            String s4 = "";
            String s5 = "";
            do
            {
                if(!stringtokenizer.hasMoreElements())
                {
                    break;
                }
                String s6 = (String)stringtokenizer.nextElement();
                if(s6.equals("monitorDoneTime"))
                {
                    String s7 = "";
                    String s8 = "";
                    if(stringtokenizer.hasMoreElements())
                    {
                        s7 = (String)stringtokenizer.nextElement();
                    }
                    if(stringtokenizer.hasMoreElements())
                    {
                        s8 = (String)stringtokenizer.nextElement();
                    }
                    if(s7.length() > 0 && s8.length() > 0)
                    {
                        if(s7.equals(s1))
                        {
                            s4 = s8;
                        } else
                        if(s7.equals(s2))
                        {
                            s5 = s8;
                        }
                    }
                }
            } while(true);
            if(s4.length() > 0)
            {
                if(s5.length() > 0)
                {
                    return "disabled until " + COM.dragonflow.Utils.TextUtils.dateToString((new Long(s4)).longValue());
                } else
                {
                    return "disabled until " + COM.dragonflow.Utils.TextUtils.dateToString((new Long(s4)).longValue());
                }
            }
        }
        return "";
    }

    private Enumeration getMonitors(jgl.Array array)
    {
        jgl.Array array1 = new Array();
        Enumeration enumeration = array.elements();
        if(enumeration.hasMoreElements())
        {
            array1.add(enumeration.nextElement());
        }
        while (enumeration.hasMoreElements()) {
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            if(COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap))
            {
                array1.add(hashmap);
            }
        } 
        return array1.elements();
    }

    private jgl.HashMap addCondition(COM.dragonflow.Api.Alert alert, String s)
    {
        return mungeCondition(alert, s);
    }

    private jgl.HashMap mungeCondition(COM.dragonflow.Api.Alert alert, String s)
    {
        jgl.HashMap hashmap = null;
        try
        {
            jgl.Array array = ReadGroupFrames(alert.getGroup());
            hashmap = findMonitor(array, alert.getMonitorID());
            String s1 = alert.getIDStr();
            String s2 = s + "\t" + s1;
            if(alert.getIncludeFilter().length() != 0)
            {
                s2 = s2 + "\t" + alert.getIncludeFilter();
            }
            if(alert.getContext().length() != 0)
            {
                s2 = s2 + "\t_UIContext=" + alert.getContext();
            }
            if(alert.getName().length() != 0)
            {
                s2 = s2 + "\t_name=" + alert.getName();
            }
            jgl.Array array1 = new Array();
            boolean flag = false;
            Enumeration enumeration = hashmap.values("_alertCondition");
            while (enumeration.hasMoreElements()) {
                String s3 = (String)enumeration.nextElement();
                jgl.Array array2 = COM.dragonflow.SiteView.Platform.split('\t', s3);
                if(array2.at(2).equals(s1))
                {
                    if(s != null)
                    {
                        array1.add(s2);
                        flag = true;
                    }
                } else
                {
                    array1.add(s3);
                }
            } 
            
            if(!flag && s != null)
            {
                array1.add(s2);
            }
            hashmap.put("_alertCondition", array1);
            WriteGroupFrames(alert.getGroup(), array);
            COM.dragonflow.SiteView.SiteViewGroup.SignalReload();
        }
        catch(java.io.IOException ioexception) { }
        catch(java.lang.Exception exception) { }
        return hashmap;
    }

    private String packCondition(jgl.HashMap hashmap, String s, jgl.HashMap hashmap1)
    {
        String s1 = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        String s6 = "";
        String s7 = (String)hashmap.get("category");
        if(s7 == null || s7.length() == 0)
        {
            s7 = "error";
        }
        String s8 = (String)hashmap.get("when");
        if(s8 != null)
        {
            if(s8.equals("count"))
            {
                int i = COM.dragonflow.Properties.StringProperty.toInteger((String)hashmap.get("errorCount"));
                if(i <= 0)
                {
                    i = 1;
                }
                s1 = " and " + s7 + "Count == " + i;
            } else
            if(s8.equals("always"))
            {
                int j = COM.dragonflow.Properties.StringProperty.toInteger((String)hashmap.get("alwaysErrorCount"));
                if(j == -1)
                {
                    hashmap.put("alwaysErrorCount", "1");
                }
                s1 = " and " + s7 + "Count >= " + hashmap.get("alwaysErrorCount");
            } else
            if(s8.equals("multiple"))
            {
                int k = COM.dragonflow.Properties.StringProperty.toInteger((String)hashmap.get("multipleStartCount"));
                if(k <= 0)
                {
                    hashmap.put("multipleStartCount", "1");
                }
                int j1 = COM.dragonflow.Properties.StringProperty.toInteger((String)hashmap.get("multipleErrorCount"));
                if(j1 <= 0)
                {
                    hashmap1.put("when", "every count must a number greater than zero");
                }
                if(j1 <= 0)
                {
                    j1 = 1;
                }
                float f = j1;
                int k1 = k % j1;
                if(k1 < 1000)
                {
                    f += (float)k1 / 1000F;
                }
                s1 = " and " + s7 + "Count >= " + k + " and " + s7 + "Count multipleOf " + f;
            } else
            if(s8.equals("allErrors"))
            {
                s1 = " and group.monitorsInGroup == group.monitorsInError and errorCount == 1";
            } else
            if(s8.equals("maxErrors"))
            {
                int l = COM.dragonflow.Properties.StringProperty.toInteger((String)hashmap.get("maxErrorCount"));
                if(l <= 0)
                {
                    hashmap.put("maxErrorCount", "1");
                }
                s1 = " and group.maxErrorCount == " + hashmap.get("maxErrorCount") + " and errorCount == " + hashmap.get("maxErrorCount");
            }
        }
        if(hashmap.get("nameMatchString") != null && ((String)hashmap.get("nameMatchString")).length() > 0)
        {
            s3 = " and _name contains '" + hashmap.get("nameMatchString") + "'";
        }
        if(hashmap.get("statusMatchString") != null && ((String)hashmap.get("statusMatchString")).length() > 0)
        {
            s5 = " and stateString contains '" + hashmap.get("statusMatchString") + "'";
        }
        if(hashmap.get("classMatchString") != null && ((String)hashmap.get("classMatchString")).length() > 0 && ((String)hashmap.get("classMatchString")).indexOf("Any") < 0)
        {
            s4 = " and _class contains '" + hashmap.get("classMatchString") + "'";
        }
        if(s7.equals("good") && hashmap.get("usePreviousErrorCount") != null && ((String)hashmap.get("usePreviousErrorCount")).length() > 0)
        {
            int i1 = COM.dragonflow.Properties.StringProperty.toInteger((String)hashmap.get("previousErrorCount"));
            if(i1 <= 0)
            {
                hashmap1.put("previousErrorCount", "error count must a number greater than zero");
            }
            s2 = " and errorCount >= " + hashmap.get("previousErrorCount");
        }
        String s9 = "";
        String s10 = (String)hashmap.get("alertDisable");
        if(s10 == null)
        {
            s10 = "";
        }
        if(s10.equals("permanent"))
        {
            if(actionAllowed("_alertDisable"))
            {
                s9 = "disabled and ";
            } else
            {
                hashmap1.put("alertDisable", "Permission denied");
            }
        } else
        if(s10.equals("timed"))
        {
            if(actionAllowed("_alertTempDisable"))
            {
                long l1 = java.lang.Long.parseLong((String)hashmap.get("disableAlertTime"));
                if(l1 <= 0L)
                {
                    hashmap1.put("disableAlertTime", "Duration of alert disable missing");
                } else
                {
                    String s11 = (String)hashmap.get("disableAlertUnits");
                    if(s11 != null)
                    {
                        if(s11.equals("minutes"))
                        {
                            l1 *= COM.dragonflow.Utils.TextUtils.MINUTE_SECONDS;
                        } else
                        if(s11.equals("hours"))
                        {
                            l1 *= COM.dragonflow.Utils.TextUtils.HOUR_SECONDS;
                        } else
                        if(s11.equals("days"))
                        {
                            l1 *= COM.dragonflow.Utils.TextUtils.DAY_SECONDS;
                        }
                    }
                    l1 *= 1000L;
                    l1 += COM.dragonflow.SiteView.Platform.timeMillis();
                    s6 = " and monitorDoneTime >= " + (new Long(l1)).toString();
                    COM.dragonflow.Utils.TextUtils.debugPrint("alert disabled until " + COM.dragonflow.Utils.TextUtils.dateToString(l1));
                }
            } else
            {
                hashmap1.put("alertDisable", "Permission denied");
            }
        } else
        if(s10.equals("schedule"))
        {
            if(actionAllowed("_alertTempDisable"))
            {
                if(COM.dragonflow.Utils.TextUtils.isDateStringValid((String)hashmap.get("startTimeDate")) && COM.dragonflow.Utils.TextUtils.isTimeStringValid((String)hashmap.get("startTimeTime")) && COM.dragonflow.Utils.TextUtils.isDateStringValid((String)hashmap.get("endTimeDate")) && COM.dragonflow.Utils.TextUtils.isTimeStringValid((String)hashmap.get("endTimeTime")))
                {
                    long l2 = COM.dragonflow.Utils.TextUtils.dateStringToSeconds((String)hashmap.get("startTimeDate"), (String)hashmap.get("startTimeTime"));
                    long l3 = COM.dragonflow.Utils.TextUtils.dateStringToSeconds((String)hashmap.get("endTimeDate"), (String)hashmap.get("endTimeTime"));
                    if(l3 <= COM.dragonflow.SiteView.Platform.timeMillis() / 1000L)
                    {
                        hashmap1.put("alertDisable", "End time is in the past");
                    }
                    if(l2 > l3)
                    {
                        hashmap1.put("alertDisable", "End time must be later than start time");
                    }
                    l2 *= 1000L;
                    l3 *= 1000L;
                    s6 = " and monitorDoneTime <= " + l2 + " or monitorDoneTime >= " + l3;
                } else
                {
                    if(!COM.dragonflow.Utils.TextUtils.isDateStringValid((String)hashmap.get("startTimeDate")))
                    {
                        hashmap1.put("startTimeDate", "Start date is invalid (use MM/DD/YY format)");
                    }
                    if(!COM.dragonflow.Utils.TextUtils.isTimeStringValid((String)hashmap.get("startTimeTime")))
                    {
                        hashmap1.put("startTimeTime", "Start time is invalid (use HH:MM format)");
                    }
                    if(!COM.dragonflow.Utils.TextUtils.isDateStringValid((String)hashmap.get("endTimeDate")))
                    {
                        hashmap1.put("endTimeDate", "End date is invalid (use MM/DD/YY format)");
                    }
                    if(!COM.dragonflow.Utils.TextUtils.isTimeStringValid((String)hashmap.get("endTimeTime")))
                    {
                        hashmap1.put("endTimeTime", "End time is invalid (use HH:MM format)");
                    }
                }
            } else
            {
                hashmap1.put("alertDisable", "Permission denied");
            }
        } else
        if(s10.equals("undo"))
        {
            if(actionAllowed("_alertDisable") || actionAllowed("_alertTempDisable"))
            {
                s6 = "";
                s9 = "";
            } else
            {
                hashmap1.put("alertDisable", "Permission denied");
            }
        }
        return s9 + "category eq '" + s7 + "'" + s1 + s2 + s3 + s5 + s4 + s6 + "\t" + s;
    }

    private boolean actionAllowed(String s)
    {
        boolean flag = false;
        if(user != null)
        {
            if(user.getProperty("_edit").length() > 0)
            {
                flag = true;
            } else
            {
                flag = user.getProperty(s).length() > 0;
            }
        }
        return flag;
    }

    private int getPreviousErrorCount(String s)
    {
        String s1 = "errorCount >= ";
        int i = s.indexOf(s1);
        if(i != -1)
        {
            return java.lang.Math.max(0, COM.dragonflow.Utils.TextUtils.readInteger(s, i + s1.length()));
        } else
        {
            return -1;
        }
    }

    private String getClassString(String s)
    {
        return getStringParameter(s, "_class contains ");
    }

    private boolean isTimedDisable(String s)
    {
        String s1 = "<=";
        for(java.util.StringTokenizer stringtokenizer = new StringTokenizer(s, " "); stringtokenizer.hasMoreElements();)
        {
            String s2 = (String)stringtokenizer.nextElement();
            if(s2.equals("monitorDoneTime"))
            {
                String s3 = (String)stringtokenizer.nextElement();
                if(s3.equals(s1))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isScheduledDisable(String s)
    {
        String s1 = "<=";
        String s2 = ">=";
        boolean flag = false;
        boolean flag1 = false;
        for(java.util.StringTokenizer stringtokenizer = new StringTokenizer(s, " "); stringtokenizer.hasMoreElements();)
        {
            String s3 = (String)stringtokenizer.nextElement();
            if(s3.equals("monitorDoneTime"))
            {
                String s4 = (String)stringtokenizer.nextElement();
                if(s4.equals(s1))
                {
                    flag = true;
                } else
                if(s4.equals(s2))
                {
                    flag1 = true;
                }
            }
            if(flag1 && flag)
            {
                return true;
            }
        }

        return flag1 && flag;
    }

    private long getDisableScheduleTime(String s, String s1)
    {
        String s2 = "<=";
        String s3 = ">=";
        String s4 = "";
        long l = 0L;
        java.util.StringTokenizer stringtokenizer = new StringTokenizer(s1, " ");
        do
        {
            if(!stringtokenizer.hasMoreElements())
            {
                break;
            }
            String s5 = (String)stringtokenizer.nextElement();
            if(s5.equals("monitorDoneTime"))
            {
                String s6 = (String)stringtokenizer.nextElement();
                if(s6.equals(s2) && s.equals("startTimeTime"))
                {
                    s4 = (String)stringtokenizer.nextElement();
                } else
                if(s6.equals(s3) && s.equals("endTimeTime"))
                {
                    s4 = (String)stringtokenizer.nextElement();
                }
            }
        } while(true);
        if(s4.length() > 0)
        {
            l = (new Long(s4)).longValue();
        }
        return l;
    }

    public String getHostname()
    {
        String s = null;
        try
        {
            s = java.net.InetAddress.getLocalHost().toString();
            int i = s.indexOf("/");
            if(i != -1)
            {
                s = s.substring(0, i);
            }
        }
        catch(java.net.UnknownHostException unknownhostexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "ApiAlert.getHostname:  Unable to retrieve local host.");
        }
        return s;
    }

    static 
    {
        $assertionsDisabled = !(COM.dragonflow.Api.APIAlert.class).desiredAssertionStatus();
        alertArtID = 1;
        historyArtID = 2;
        helpArtID = 3;
        siteviewArtID = 4;
        groupArtID = 5;
        alertDetailID = 6;
        alertOnID = 7;
        alertForID = 8;
        alertDoID = 9;
        alertDelID = 10;
        alertEditID = 11;
        alertAddID = 12;
        alertPagerPrefsID = 13;
        alertMailPrefsID = 14;
        alertSNMPPrefsID = 15;
        alertTestMailID = 16;
        alertTestPagerID = 17;
        alertTestSNMPID = 18;
        alertErrorID = 19;
        alertWarningID = 20;
        alertGoodID = 21;
        alertRunID = 22;
        alertMailID = 23;
        alertPageID = 24;
        alertSNMPID = 25;
        alertCustomID = 26;
        alertAnyID = 27;
        alertGroupID = 28;
        alertNameID = 29;
        english = new String[100];
        english[alertArtID] = "alerts";
        english[historyArtID] = "reports";
        english[helpArtID] = "help";
        english[siteviewArtID] = "siteview";
        english[groupArtID] = "groups";
        english[alertDetailID] = "Alert Definitions";
        english[alertOnID] = "On";
        english[alertForID] = "For";
        english[alertDoID] = "Do";
        english[alertNameID] = "Name";
        english[alertDelID] = "Del";
        english[alertEditID] = "Edit an Alert by clicking the Edit link for the Alert.";
        english[alertAddID] = "Add";
        english[alertPagerPrefsID] = "Set Preferences</a> for Pager Alerts";
        english[alertMailPrefsID] = "Set Preferences</a> for Mail Alerts";
        english[alertSNMPPrefsID] = "Set Preferences</a> for SNMP Trap Alerts";
        english[alertTestPagerID] = "Send</a> a test alert to the pager";
        english[alertTestMailID] = "Send</a> a test alert using e-mail";
        english[alertTestSNMPID] = "Send</a> a test alert using SNMP";
        english[alertErrorID] = "error";
        english[alertWarningID] = "warning";
        english[alertGoodID] = "good";
        english[alertRunID] = "Run";
        english[alertMailID] = "Send Mail to";
        english[alertPageID] = "Send Page Message";
        english[alertSNMPID] = "Send SNMP Trap Message";
        english[alertCustomID] = "Custom";
        english[alertAnyID] = "any Monitor";
        english[alertGroupID] = "any Monitor in group";
        french = new String[100];
        french[alertArtID] = "alertsfr";
        french[historyArtID] = "reportsfr";
        french[helpArtID] = "helpfr";
        french[siteviewArtID] = "siteview";
        french[groupArtID] = "groupsfr";
        french[alertDetailID] = "Alerte D&eacute;taill&eacute;e";
        french[alertOnID] = "En";
        french[alertForID] = "Pour";
        french[alertDoID] = "Faire";
        french[alertNameID] = "nom";
        french[alertDelID] = "Effacer";
        french[alertEditID] = "Editer une Alerte en cliquant sur le nom de l'Alerte.";
        french[alertAddID] = "Ajouter";
        french[alertPagerPrefsID] = "Etablir les Pr&eacute;f&eacute;rences</a> des Alertes de Pager";
        french[alertMailPrefsID] = "Etablir les Pr&eacute;f&eacute;rences</a> des Alertes de E-mail";
        french[alertSNMPPrefsID] = "Etablir les Pr&eacute;f&eacute;rences</a> des Alertes de SNMP Trap";
        french[alertTestPagerID] = "Envoyer</a> une alerte d'essai au pager";
        french[alertTestMailID] = "Envoyer</a> une alerte d'essai par email";
        french[alertTestSNMPID] = "Envoyer</a> une alerte d'essai par SNMP trap";
        french[alertErrorID] = "erreur";
        french[alertWarningID] = "attention";
        french[alertGoodID] = "bon";
        french[alertRunID] = "Perform";
        french[alertMailID] = "Envoyer par email";
        french[alertPageID] = "Envoyer au pager";
        french[alertSNMPID] = "Envoyer par SNMP Trap";
        french[alertCustomID] = "Custom";
        french[alertAnyID] = "Tous les Monitors";
        french[alertGroupID] = "Tous les Monitors dans l'equipe";
    }
}
