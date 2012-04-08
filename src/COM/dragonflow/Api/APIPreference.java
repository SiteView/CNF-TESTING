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

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import jgl.Array;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;

// Referenced classes of package COM.dragonflow.Api:
// APISiteView, SSInstanceProperty, SSPropertyDetails, SSPreferenceInstance,
// SSStringReturnValue

public class APIPreference extends COM.dragonflow.Api.APISiteView
{

    public APIPreference()
    {
    }

    public COM.dragonflow.Api.SSInstanceProperty create(String s, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty ssinstanceproperty = null;
        try
        {
            String s1 = "COM.dragonflow.StandardPreference." + s;
            java.util.HashMap hashmap = new HashMap();
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                hashmap.put(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }

            java.lang.Class class1 = java.lang.Class.forName(s1);
            COM.dragonflow.SiteView.Preferences preferences = (COM.dragonflow.SiteView.Preferences)class1.newInstance();
            jgl.Array array = getPropertiesForClass(preferences, s1, "Preferences", COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL);
            hashmap = preferences.validateProperties(hashmap, array, new HashMap());
            String as[] = preferences.addPreferences(hashmap);
            ssinstanceproperty = new SSInstanceProperty(as[0], as[1]);
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "create"
            }, 0L, exception.getMessage());
        }
        return ssinstanceproperty;
    }

    public COM.dragonflow.Api.SSInstanceProperty update(String s, String s1, String s2, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty ssinstanceproperty = null;
        try
        {
            String s3 = "COM.dragonflow.StandardPreference." + s;
            java.util.HashMap hashmap = new HashMap();
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                hashmap.put(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }

            java.lang.Class class1 = java.lang.Class.forName(s3);
            COM.dragonflow.SiteView.Preferences preferences = (COM.dragonflow.SiteView.Preferences)class1.newInstance();
            jgl.Array array = getPropertiesForClass(preferences, s3, "Preferences", COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
            hashmap = preferences.validateProperties(hashmap, array, new HashMap());
            String as[] = preferences.updatePreferences(hashmap, s1, s2);
            ssinstanceproperty = new SSInstanceProperty(as[0], as[1]);
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "update"
            }, 0L, exception.getMessage());
        }
        return ssinstanceproperty;
    }

    public void delete(String s, String s1, String s2)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try
        {
            String s3 = "COM.dragonflow.StandardPreference." + s;
            java.lang.Class class1 = java.lang.Class.forName(s3);
            COM.dragonflow.SiteView.Preferences preferences = (COM.dragonflow.SiteView.Preferences)class1.newInstance();
            preferences.deletePreferences(s1, s2);
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "delete"
            }, 0L, exception.getMessage());
        }
    }

    public java.util.Vector test(String s, String s1, String s2, String s3, boolean flag)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        java.util.Vector vector = new Vector();
        try
        {
            String s4 = "COM.dragonflow.StandardPreference." + s;
            java.lang.Class class1 = java.lang.Class.forName(s4);
            COM.dragonflow.SiteView.Preferences preferences = (COM.dragonflow.SiteView.Preferences)class1.newInstance();
            COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = getInstanceProperties(s, preferences.getSettingName(), s1, s2, COM.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL);
            if(assinstanceproperty != null && assinstanceproperty.length > 0)
            {
                for(int i = 0; i < assinstanceproperty.length; i++)
                {
                    String s5 = assinstanceproperty[i].getName();
                    String s6 = (String)assinstanceproperty[i].getValue();
                    if(s5 != null && s6 != null)
                    {
                        preferences.setProperty(s5, s6);
                    }
                }

                vector = preferences.test(s3);
                assinstanceproperty = getInstanceProperties(s, preferences.getSettingName(), s1, s2, COM.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL);
                if(assinstanceproperty != null && assinstanceproperty.length > 0)
                {
                    if(flag)
                    {
                        vector = new Vector();
                    }
                    for(int j = 0; j < assinstanceproperty.length; j++)
                    {
                        if(flag)
                        {
                            if(assinstanceproperty[j].getName().equals("_status"))
                            {
                                vector.addElement(assinstanceproperty[j].getValue());
                            }
                        } else
                        {
                            vector.addElement(assinstanceproperty[j].getValue());
                        }
                    }

                }
            } else
            {
                vector.addElement("attribute " + s1 + " = " + s2 + " not found");
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "test"
            }, 0L, exception.getMessage());
        }
        return vector;
    }

    public COM.dragonflow.Api.SSPropertyDetails[] getClassPropertiesDetails(String s, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        Object obj = null;
        COM.dragonflow.Api.SSPropertyDetails asspropertydetails[] = null;
        try
        {
            String s1 = "COM.dragonflow.StandardPreference." + s;
            java.lang.Class class1 = java.lang.Class.forName(s1);
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
            jgl.Array array = getPropertiesForClass(siteviewobject, s1, "Preferences", i);
            java.util.Vector vector = new Vector();
            if(i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL || i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_BASIC || i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ADVANCED || i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL || i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_BASIC || i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ADVANCED)
            {
                Enumeration enumeration = array.elements();
                jgl.Array array1 = new Array();
                while (enumeration.hasMoreElements()) {
                    COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                    if(stringproperty.isThreshold())
                    {
                        array1.add(stringproperty);
                    }
                }
                for(int k = 0; k < array1.size(); k++)
                {
                    array.remove(array1.at(k));
                }

            }
            asspropertydetails = new COM.dragonflow.Api.SSPropertyDetails[array.size() + vector.size()];
            for(int j = 0; j < array.size(); j++)
            {
                asspropertydetails[j] = getClassProperty((COM.dragonflow.Properties.StringProperty)array.at(j), (COM.dragonflow.SiteView.Preferences)siteviewobject);
            }

        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "getClassPropertiesDetails"
            }, 0L, exception.getMessage());
        }
        return asspropertydetails;
    }

    public COM.dragonflow.Api.SSPropertyDetails getClassPropertyDetails(String s, String s1, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSPropertyDetails sspropertydetails = null;
        try
        {
            String s2 = "COM.dragonflow.StandardPreference." + s1;
            java.lang.Class class1 = java.lang.Class.forName(s2);
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
            COM.dragonflow.Properties.StringProperty stringproperty = siteviewobject.getPropertyObject(s);
            sspropertydetails = getClassProperty(stringproperty, (COM.dragonflow.SiteView.Preferences)siteviewobject);
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "getClassPropertyDetails"
            }, 0L, exception.getMessage());
        }
        return sspropertydetails;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @param s2
     * @param s3
     * @param i
     * @return
     * @throws COM.dragonflow.SiteViewException.SiteViewException
     */
    public COM.dragonflow.Api.SSPreferenceInstance[] getInstances(String s, String s1, String s2, String s3, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        try {
        java.util.Vector vector = new Vector();
        COM.dragonflow.Api.SSPreferenceInstance asspreferenceinstance[];
        String s4 = "COM.dragonflow.StandardPreference." + s;
        java.lang.Class class1 = java.lang.Class.forName(s4);
        COM.dragonflow.SiteView.Preferences preferences = (COM.dragonflow.SiteView.Preferences)class1.newInstance();
        s1 = preferences.getSettingName();
        java.util.Vector vector1 = preferences.getPreferenceProperties(s, s1, "", "", i);
        java.util.HashSet hashset = new HashSet();
        for(int j = 0; j < vector1.size(); j++)
        {
            java.util.HashMap hashmap = (java.util.HashMap)vector1.elementAt(j);
            String s5 = (String)hashmap.get(COM.dragonflow.SiteView.Preferences.pID.getName());
            if(hashset.contains(s5))
            {
                COM.dragonflow.Log.LogManager.log("error", "Duplicate preference ID for " + s + ": " + s5);
                continue;
            }
            hashset.add(s5);
            COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = new COM.dragonflow.Api.SSInstanceProperty[hashmap.size()];
            java.util.Set set = hashmap.keySet();
            java.util.Iterator iterator = set.iterator();
            for(int l = 0; iterator.hasNext(); l++)
            {
                String s6 = (String)iterator.next();
                assinstanceproperty[l] = new SSInstanceProperty(s6, hashmap.get(s6));
            }

            vector.add(new SSPreferenceInstance(s1, assinstanceproperty));
        }

        asspreferenceinstance = new COM.dragonflow.Api.SSPreferenceInstance[vector.size()];
        for(int k = 0; k < vector.size(); k++)
        {
            asspreferenceinstance[k] = (COM.dragonflow.Api.SSPreferenceInstance)vector.elementAt(k);
        }

        return asspreferenceinstance;
        }
        catch (SiteViewException e) {
        e.fillInStackTrace();
        throw e;
        }
        catch (Exception e) {
        throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
            "APIPreference", "getInstances"
        }, 0L, e.getMessage());
        }
    }

    public COM.dragonflow.Api.SSInstanceProperty[] getInstanceProperties(String s, String s1, String s2, String s3, int i)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = null;
        try
        {
            String s4 = "COM.dragonflow.StandardPreference." + s;
            Object obj = null;
            java.lang.Class class1 = java.lang.Class.forName(s4);
            COM.dragonflow.SiteView.Preferences preferences = (COM.dragonflow.SiteView.Preferences)class1.newInstance();
            if(s1 != null && s1.length() == 0)
            {
                s1 = preferences.getSettingName();
            }
            java.util.Vector vector = preferences.getPreferenceProperties(s, s1, s2, s3, i);
            if(vector.size() > 0)
            {
                java.util.HashMap hashmap = (java.util.HashMap)vector.elementAt(0);
                assinstanceproperty = new COM.dragonflow.Api.SSInstanceProperty[hashmap.size()];
                java.util.Set set = hashmap.keySet();
                java.util.Iterator iterator = set.iterator();
                for(int j = 0; iterator.hasNext(); j++)
                {
                    String s5 = (String)iterator.next();
                    assinstanceproperty[j] = new SSInstanceProperty(s5, hashmap.get(s5));
                }

            } else
            {
                throw new SiteViewParameterException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_PREFERENCE_INSTANCE_NOTFOUND, new String[] {
                    s2, s3
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
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "getInstanceProperties"
            }, 0L, exception.getMessage());
        }
        return assinstanceproperty;
    }

    public COM.dragonflow.Api.SSStringReturnValue[] getPreferenceTypes()
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        COM.dragonflow.Api.SSStringReturnValue assstringreturnvalue[] = null;
        try
        {
            java.util.Vector vector = new Vector();
            java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/classes/COM/dragonflow/StandardPreference");
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                int k = as[i].lastIndexOf(".class");
                if(k != -1)
                {
                    vector.addElement(as[i].substring(0, k));
                }
            }

            assstringreturnvalue = new COM.dragonflow.Api.SSStringReturnValue[vector.size()];
            for(int j = 0; j < vector.size(); j++)
            {
                assstringreturnvalue[j] = new SSStringReturnValue((String)vector.elementAt(j));
            }

        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "getPreferenceTypes"
            }, 0L, exception.getMessage());
        }
        return assstringreturnvalue;
    }

    private COM.dragonflow.Api.SSPropertyDetails getClassProperty(COM.dragonflow.Properties.StringProperty stringproperty, COM.dragonflow.SiteView.Preferences preferences)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        String as[] = null;
        String as1[] = null;
        String s = "";
        String s1 = "TEXT";
        String s2 = "";
        boolean flag = false;
        try
        {
            COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
            httprequest.setUser(account);
            if(stringproperty.isPassword)
            {
                s1 = "PASSWORD";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ServerProperty)
            {
                s1 = "SERVER";
                java.util.Vector vector = null;
                if(httprequest.getValue("noNTRemote").length() == 0)
                {
                    vector = getLocalServers();
                    vector = addServers(vector, "_remoteNTMachine", true);
                } else
                {
                    vector = new Vector();
                    vector.addElement("this server");
                    vector.addElement("this server");
                }
                if(httprequest.getValue("noremote").length() == 0)
                {
                    vector = addServers(vector, "_remoteMachine", false);
                }
                String s3 = httprequest.getValue("server");
                if(s3.length() == 0)
                {
                    s3 = "this server";
                } else
                {
                    s3 = COM.dragonflow.SiteView.Machine.getFullMachineID(s3, httprequest);
                }
                as = new String[vector.size() / 2];
                as1 = new String[vector.size() / 2];
                int i = 0;
                for(int j = 0; i < vector.size(); j++)
                {
                    String s4 = (String)vector.elementAt(i);
                    as[j + 1] = s4;
                    as1[j] = s4;
                    i += 2;
                }

                s = "LIST";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScheduleProperty)
            {
                s1 = "SCHEDULE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScalarProperty)
            {
                s1 = "SCALAR";
                COM.dragonflow.Properties.ScalarProperty scalarproperty = (COM.dragonflow.Properties.ScalarProperty)stringproperty;
                java.lang.Class class1 = java.lang.Class.forName("COM.dragonflow.Page.monitorPage");
                COM.dragonflow.Page.CGI cgi = (COM.dragonflow.Page.CGI)class1.newInstance();
                cgi.initialize(httprequest, null);
                java.util.Vector vector1 = preferences.getScalarValues(scalarproperty, httprequest, cgi);
                as = new String[vector1.size() / 2];
                as1 = new String[vector1.size() / 2];
                int k = 0;
                for(int l = 0; k < vector1.size() / 2; l += 2)
                {
                    as1[k] = (String)vector1.elementAt(l);
                    as[k] = (String)vector1.elementAt(l + 1);
                    k++;
                }

                s = "LIST";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.RateProperty)
            {
                s1 = "RATE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.PercentProperty)
            {
                s1 = "PERCENT";
                s2 = ((COM.dragonflow.Properties.PercentProperty)stringproperty).getUnits();
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.FrequencyProperty)
            {
                s1 = "FREQUENCY";
                s2 = "seconds";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.FileProperty)
            {
                s1 = "FILE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.BrowsableProperty)
            {
                s1 = "BROWSABLE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.BooleanProperty)
            {
                s1 = "BOOLEAN";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.NumericProperty)
            {
                s1 = "NUMERIC";
                s2 = ((COM.dragonflow.Properties.NumericProperty)stringproperty).getUnits();
            }
        }
        catch(COM.dragonflow.SiteViewException.SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] {
                "APIPreference", "getClassProperty"
            }, 0L, exception.getMessage());
        }
        return new SSPropertyDetails(stringproperty.getName(), s1, stringproperty.getDescription(), stringproperty.getLabel(), stringproperty.isEditable, stringproperty.isMultivalued, stringproperty.getDefault(), as, as1, s, !stringproperty.isAdvanced, flag, stringproperty.getOrder(), s2, stringproperty.isAdvanced, stringproperty.isPassword, preferences.getProperty(stringproperty.getName()));
    }

    public static void main(String args[])
    {
        try
        {
            COM.dragonflow.Api.APIPreference apipreference = new APIPreference();
            apipreference.delete("RemoteNTPreferences", "_id", "10");
        }
        catch(java.lang.Exception exception) { }
    }
}
