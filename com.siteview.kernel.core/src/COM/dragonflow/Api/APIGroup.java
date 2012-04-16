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
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteView.Group;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;

// Referenced classes of package COM.dragonflow.Api:
// APISiteView, SSStringReturnValue, SSPropertyDetails, APIAlert,
// SSInstanceProperty, SSGroupInstance, Alert

public class APIGroup extends COM.dragonflow.Api.APISiteView
{

    public APIGroup()
    {
    }

    public COM.dragonflow.Api.SSStringReturnValue create(String s, String s1, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
        throws SiteViewException
    {
        String s2 = null;
        try
        {
            if(s == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_NAME_MISSING);
            }
            if(!COM.dragonflow.Utils.I18N.isI18N)
            {
                s2 = COM.dragonflow.Utils.TextUtils.keepChars(s, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
            } else
            {
                s2 = COM.dragonflow.Utils.FileUtils.getGroupNewFileName();
            }
            s2 = computeGroupName(s2);
            if(s2.length() == 0)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_NAME_EMPTY);
            }
            if(s2.equalsIgnoreCase("siteview"))
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_NAME_SITEVIEW);
            }
            if(assinstanceproperty == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_PROPERTIES_NOT_NULL);
            }
            jgl.HashMap hashmap = new HashMap();
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                hashmap.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }

            if(!COM.dragonflow.SiteView.Platform.isStandardAccount(account))
            {
                s2 = computeGroupName(account + "$" + s2);
            } else
            {
                s2 = computeGroupName(s2);
                jgl.Array array = new Array();
                COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
                updateConfig("_name", "config", hashmap, hashmapordered);
                if(s1 != null && s1.length() != 0)
                {
                    hashmapordered.put("_parent", s1);
                    jgl.Array array1 = ReadGroupFrames(s1);
                    jgl.HashMap hashmap1 = (jgl.HashMap)array1.at(0);
                    String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_nextID");
                    if(s3.length() == 0)
                    {
                        s3 = "1";
                    }
                    jgl.HashMap hashmap2 = new HashMap();
                    hashmap2.put("_id", s3);
                    hashmap2.put("_class", "SubGroup");
                    hashmap2.put("_group", s2);
                    hashmap2.put("_name", s);
                    array1.insert(array1.size(), hashmap2);
                    String s4 = COM.dragonflow.Utils.TextUtils.increment(s3);
                    hashmap1.put("_nextID", s4);
                    WriteGroupFrames(s1, array1);
                }
                if(!s.equals(s2))
                {
                    hashmapordered.put("_name", s);
                }
                array.add(hashmapordered);
                hashmapordered.put("_nextID", "1");
                updateConfig("_dependsOn", "", hashmap, hashmapordered);
                updateConfig("_dependsCondition", "good", hashmap, hashmapordered);
                updateConfig("_httpCharSet", "", hashmap, hashmapordered);
                updateConfig("__template", "", hashmap, hashmapordered);
                updateConfig("_frequency", "", hashmap, hashmapordered);
                updateConfig("_description", "", hashmap, hashmapordered);
                WriteGroupFrames(s2, array);
                COM.dragonflow.Api.APIGroup.forceConfigurationRefresh();
            }
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "create"
            }, 0L, exception.getMessage());
        }
        return new SSStringReturnValue(s2);
    }

    public void update(String s, COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
        throws SiteViewException
    {
        try
        {
            if(s.length() == 0 || s == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            if(assinstanceproperty.length <= 0)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_PROPERTIES_NOT_NULL);
            }
            jgl.Array array = ReadGroupFrames(s);
            Enumeration enumeration = array.elements();
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            jgl.HashMap hashmap1 = new HashMap();
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                hashmap1.add(assinstanceproperty[i].getName(), assinstanceproperty[i].getValue());
            }

            updateConfig("_name", "config", hashmap1, hashmap);
            updateConfig("_dependsOn", "", hashmap1, hashmap);
            updateConfig("_dependsCondition", "good", hashmap1, hashmap);
            updateConfig("_httpCharSet", "", hashmap1, hashmap);
            updateConfig("__template", "", hashmap1, hashmap);
            updateConfig("_frequency", "", hashmap1, hashmap);
            updateConfig("_description", "", hashmap1, hashmap);
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_parent");
            if(s1.length() != 0)
            {
                jgl.Array array1 = ReadGroupFrames(s1);
                int j = findSubGroupIndex(array1, s);
                jgl.HashMap hashmap2 = (jgl.HashMap)array1.at(j);
                updateConfig("_name", "config", hashmap1, hashmap2);
                WriteGroupFrames(s1, array1);
            }
            WriteGroupFrames(s, array);
            COM.dragonflow.Api.APIGroup.forceConfigurationRefresh();
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "update"
            }, 0L, exception.getMessage());
        }
    }

    public void delete(String s)
        throws SiteViewException
    {
        try
        {
            if(s.length() == 0 || s == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            deleteGroupInternal(s);
            saveGroups();
            COM.dragonflow.SiteView.DetectConfigurationChange detectconfigurationchange = COM.dragonflow.SiteView.DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "delete"
            }, 0L, exception.getMessage());
        }
    }

    public COM.dragonflow.Api.SSStringReturnValue move(String s, String s1)
        throws SiteViewException
    {
        String s2 = null;
        try
        {
            if(s.length() == 0 || s == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            if(s != null && s1 != null && s.equals(s1))
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_MOVE_DESTINATION_SELF);
            }
            String s3 = null;
            String s4 = s1;
            do
            {
                jgl.Array array = ReadGroupFrames(s4);
                Enumeration enumeration = array.elements();
                COM.dragonflow.Properties.HashMapOrdered hashmapordered = (COM.dragonflow.Properties.HashMapOrdered)enumeration.nextElement();
                s3 = (String)hashmapordered.get("_parent");
                if(s3 != null && s3.equals(s))
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_MOVE_DESTINATION_SUBGROUP);
                }
                s4 = s3;
            } while(s3 != null && s3.length() > 0);
            s2 = moveGroup(s, s1, new HashMap(), false);
            COM.dragonflow.SiteView.DetectConfigurationChange detectconfigurationchange = COM.dragonflow.SiteView.DetectConfigurationChange.getInstance();
            detectconfigurationchange.setConfigChangeFlag();
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "move"
            }, 0L, exception.getMessage());
        }
        return new SSStringReturnValue(s2);
    }

    public COM.dragonflow.Api.SSStringReturnValue copy(String s, String s1)
        throws SiteViewException
    {
        String s2 = null;
        try
        {
            if(s.length() == 0 || s == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            if(s != null && s1 != null && s.equals(s1))
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_COPY_DESTINATION_SELF);
            }
            String s3 = null;
            String s4 = s1;
            if(s4 != null && s4.length() > 0)
            {
                do
                {
                    jgl.Array array = ReadGroupFrames(s4);
                    Enumeration enumeration = array.elements();
                    COM.dragonflow.Properties.HashMapOrdered hashmapordered = (COM.dragonflow.Properties.HashMapOrdered)enumeration.nextElement();
                    s3 = (String)hashmapordered.get("_parent");
                    if(s3 != null && s3.equals(s))
                    {
                        throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_COPY_DESTINATION_SUBGROUP);
                    }
                    s4 = s3;
                } while(s3 != null && s3.length() > 0);
            }
            s2 = moveGroup(s, s1, new HashMap(), true);
            COM.dragonflow.Api.APIGroup.forceConfigurationRefresh();
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "copy"
            }, 0L, exception.getMessage());
        }
        return new SSStringReturnValue(s2);
    }

    public COM.dragonflow.Api.SSPropertyDetails getClassPropertyDetails(String s)
        throws SiteViewException
    {
        COM.dragonflow.Api.SSPropertyDetails sspropertydetails = null;
        try
        {
            String s1 = "COM.dragonflow.SiteView.Group";
            java.lang.Class class1 = java.lang.Class.forName(s1);
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
            COM.dragonflow.Properties.StringProperty stringproperty = siteviewobject.getPropertyObject(s);
            if(stringproperty == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_COPY_DESTINATION_SUBGROUP, new String[] {
                    s
                });
            }
            sspropertydetails = getClassProperty(stringproperty, null);
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getClassPropertyDetails"
            }, 0L, exception.getMessage());
        }
        return sspropertydetails;
    }

    public COM.dragonflow.Api.SSPropertyDetails[] getClassPropertiesDetails(int i)
        throws SiteViewException
    {
        COM.dragonflow.Api.SSPropertyDetails asspropertydetails[] = null;
        try
        {
            String s = "COM.dragonflow.SiteView.Group";
            java.lang.Class class1 = java.lang.Class.forName(s);
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
            jgl.Array array = getPropertiesForClass(siteviewobject, s, "Group", i);
            asspropertydetails = new COM.dragonflow.Api.SSPropertyDetails[array.size()];
            for(int j = 0; j < array.size(); j++)
            {
                asspropertydetails[j] = getClassProperty((COM.dragonflow.Properties.StringProperty)array.at(j), null);
            }

        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getClassPropertiesDetails"
            }, 0L, exception.getMessage());
        }
        return asspropertydetails;
    }

    public COM.dragonflow.Api.SSPropertyDetails getInstancePropertyScalars(String s, String s1)
        throws SiteViewException
    {
        COM.dragonflow.Api.SSPropertyDetails sspropertydetails = null;
        try
        {
            String s2 = "COM.dragonflow.SiteView.Group";
            java.lang.Class class1 = java.lang.Class.forName(s2);
            COM.dragonflow.SiteView.SiteViewObject siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
            COM.dragonflow.Properties.StringProperty stringproperty = siteviewobject.getPropertyObject(s);
            sspropertydetails = getClassProperty(stringproperty, s1);
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getInstancePropertyScalars"
            }, 0L, exception.getMessage());
        }
        return sspropertydetails;
    }

    public COM.dragonflow.Api.SSGroupInstance[] getInstances(String s, int i)
        throws SiteViewException
    {
        if(!isGroupAllowedForAccount(s))
        {
            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_OP_SS_GROUP_ACCESS_EXCEPTION);
        }
        COM.dragonflow.Api.SSGroupInstance assgroupinstance[] = null;
        try
        {
            boolean flag = true;
            if(s != null && s.length() > 0)
            {
                flag = false;
            }
            java.util.Vector vector = new Vector();
            java.lang.Object obj = null;
            if(flag)
            {
                obj = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().getTopLevelGroups();
            } else
            {
                obj = getSubGroups(s);
            }
            COM.dragonflow.Api.APIAlert apialert = new APIAlert();
            java.util.Iterator iterator = ((java.util.Collection) (obj)).iterator();
            while (iterator.hasNext()) {
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)iterator.next();
                String s1 = monitorgroup.getProperty(COM.dragonflow.SiteView.MonitorGroup.pID);
                if(isGroupAllowedForAccount(s1))
                {
                    String s2 = monitorgroup.getProperty(COM.dragonflow.SiteView.MonitorGroup.pParent);
                    if(flag && (s2 == null || s2.length() == 0) || !flag && s2 != null && s2.equals(s))
                    {
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = getInstanceProperties(s1, i);
                        int k = 0;
                        boolean flag1 = false;
                        if(COM.dragonflow.Api.Alert.getInstance().groupHasAlerts(s1))
                        {
                            flag1 = true;
                            k++;
                        }
                        boolean flag2 = false;
                        if(hasSubGroupDependencies(s1))
                        {
                            flag2 = true;
                            k++;
                        }
                        int l = 0;
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty1[] = new COM.dragonflow.Api.SSInstanceProperty[assinstanceproperty.length + k];
                        if(flag1)
                        {
                            assinstanceproperty1[l] = new SSInstanceProperty("hasDependencies", "true");
                            l++;
                        }
                        if(flag2)
                        {
                            assinstanceproperty1[l] = new SSInstanceProperty("hasSubDependencies", "true");
                        }
                        for(int i1 = 0; i1 < assinstanceproperty.length; i1++)
                        {
                            if(assinstanceproperty[i1].getName().equals("_name") && assinstanceproperty[i1].getValue().equals("config"))
                            {
                                assinstanceproperty1[i1 + k] = new SSInstanceProperty("_name", s1);
                            } else
                            {
                                assinstanceproperty1[i1 + k] = assinstanceproperty[i1];
                            }
                        }

                        vector.add(new SSGroupInstance(s1, assinstanceproperty1));
                    }
                }
            } 
            assgroupinstance = new COM.dragonflow.Api.SSGroupInstance[vector.size()];
            for(int j = 0; j < vector.size(); j++)
            {
                assgroupinstance[j] = (COM.dragonflow.Api.SSGroupInstance)vector.get(j);
            }

        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getInstances"
            }, 0L, exception.getMessage());
        }
        return assgroupinstance;
    }

    public COM.dragonflow.Api.SSInstanceProperty[] getInstanceProperties(String s, int i)
        throws SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = null;
        try
        {
            if(s.length() == 0 || s == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            jgl.Array array = getPropertiesForGroupInstance("COM.dragonflow.SiteView.Group", i);
            COM.dragonflow.SiteView.MonitorGroup monitorgroup = COM.dragonflow.SiteView.SiteViewGroup.getMonitorGroup(s);
            int j = 0;
            COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue = null;
            try
            {
                if(i == COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL || i == COM.dragonflow.Api.APISiteView.FILTER_ALL)
                {
                    j = 1;
//                    ssstringreturnvalue = getTopazID(s);
                }
            }
            catch(java.lang.Exception exception1)
            {
                j = 0;
            }
            assinstanceproperty = new COM.dragonflow.Api.SSInstanceProperty[j + array.size()];
            int k = 0;
            for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements();)
            {
                COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                String s1 = monitorgroup.getProperty(stringproperty.getName());
                if(stringproperty.isPassword)
                {
                    s1 = COM.dragonflow.Utils.TextUtils.obscure(s1);
                }
                assinstanceproperty[k] = new SSInstanceProperty(stringproperty.getName(), s1);
                k++;
            }

            if(j > 0)
            {
//                assinstanceproperty[k] = new SSInstanceProperty("topazGroupID", COM.dragonflow.SiteView.TopazInfo.getTopazName() + " Group ID", ssstringreturnvalue.getValue());
            }
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getInstanceProperties"
            }, 0L, exception.getMessage());
        }
        return assinstanceproperty;
    }

    public COM.dragonflow.Api.SSInstanceProperty getInstanceProperty(String s, String s1, int i)
        throws SiteViewException
    {
        COM.dragonflow.Api.SSInstanceProperty ssinstanceproperty = null;
        try
        {
            if(s1.length() == 0 || s1 == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            jgl.Array array = getPropertiesForGroupInstance("COM.dragonflow.SiteView.Group", i);
            COM.dragonflow.SiteView.MonitorGroup monitorgroup = COM.dragonflow.SiteView.SiteViewGroup.getMonitorGroup(s1);
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                String s2 = monitorgroup.getProperty(stringproperty.getName());
                if(stringproperty.isPassword)
                {
                    s2 = COM.dragonflow.Utils.TextUtils.obscure(s2);
                }
                if(stringproperty.getName().equals(s))
                {
                    ssinstanceproperty = new SSInstanceProperty(stringproperty.getName(), s2);
                }
            } 
            
            if(s.equals("topazGroupID"))
            {
                try
                {
//                    COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue = getTopazID(s1);
//                    ssinstanceproperty = new SSInstanceProperty("topazGroupID", COM.dragonflow.SiteView.TopazInfo.getTopazName() + " Group ID", ssstringreturnvalue.getValue());
                }
                catch(java.lang.Exception exception1)
                {
                    throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_TOPAZ_NOT_CONFIGURED);
                }
            }
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getInstanceProperty"
            }, 0L, exception.getMessage());
        }
        return ssinstanceproperty;
    }

    public java.util.Collection getAllGroupInstances()
        throws SiteViewException
    {
        return getAllAllowedGroups();
    }

    public java.util.Collection getChildGroupInstances(String s)
        throws SiteViewException
    {
        return getSubGroups(s);
    }

    public void refreshGroup(String s, boolean flag)
        throws SiteViewException
    {
        try
        {
            if(s.length() == 0 || s == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)COM.dragonflow.SiteView.SiteViewGroup.currentSiteView().getElementByID(s);
            if(monitorgroup == null)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_NOT_VALID, new String[] {
                    s
                });
            }
            COM.dragonflow.SiteView.MonitorGroup _tmp = monitorgroup;
            monitorgroup.setProperty(COM.dragonflow.SiteView.MonitorGroup.pLastUpdate, String.valueOf(java.lang.System.currentTimeMillis()));
            java.util.Vector vector = new Vector();
            vector.add(monitorgroup);
            java.util.Vector vector1 = new Vector();
            COM.dragonflow.SiteView.ConfigurationChanger.getGroupsMonitors(vector, vector1, null, flag);
            for(int i = 0; i < vector1.size(); i++)
            {
                ((COM.dragonflow.SiteView.AtomicMonitor)vector1.get(i)).runUpdate(false);
            }

        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "refreshGroup"
            }, 0L, exception.getMessage());
        }
    }

//    public COM.dragonflow.Api.SSStringReturnValue getTopazID(String s)
//        throws SiteViewException
//    {
//        COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue = null;
////        COM.dragonflow.TopazIntegration.TopazServerSettings topazserversettings = COM.dragonflow.TopazIntegration.TopazManager.getInstance().getTopazServerSettings();
//        if(topazserversettings.isConnected())
//        {
//            ssstringreturnvalue = new SSStringReturnValue((new Integer(COM.dragonflow.TopazIntegration.TopazConfigManager.getInstance().getGroup(s).getTopazId())).toString());
//        } else
//        {
//            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_TOPAZ_NOT_CONFIGURED);
//        }
//        return ssstringreturnvalue;
//    }

    private jgl.Array getPropertiesForGroupInstance(String s, int i)
        throws SiteViewException
    {
        jgl.Array array = new Array();
        try
        {
            COM.dragonflow.SiteView.Group group = new Group();
            jgl.Array array1 = group.getProperties();
            array1 = COM.dragonflow.Properties.StringProperty.sortByOrder(array1);
            Enumeration enumeration = array1.elements();
            while (enumeration.hasMoreElements()) {
                boolean flag = false;
                COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                int j = s.lastIndexOf(".");
                flag = returnProperty(stringproperty, i, group, s.substring(j + 1));
                if(flag)
                {
                    array.add(stringproperty);
                }
            } 
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getPropertiesForGroupInstance"
            }, 0L, exception.getMessage());
        }
        return array;
    }

    private COM.dragonflow.Api.SSPropertyDetails getClassProperty(COM.dragonflow.Properties.StringProperty stringproperty, String s)
        throws SiteViewException
    {
        String s1 = "TEXT";
        String s2 = "LIST";
        String as[] = null;
        String as1[] = null;
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = null;
        String s3 = "";
        try
        {
            if(stringproperty == null)
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                    "APIGroup", "getClassProperty"
                });
            }
            if(stringproperty.isPassword)
            {
                s1 = "PASSWORD";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ServerProperty)
            {
                s1 = "SERVER";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScheduleProperty)
            {
                s1 = "SCHEDULE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.ScalarProperty)
            {
                s1 = "SCALAR";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.RateProperty)
            {
                s1 = "RATE";
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.PercentProperty)
            {
                s1 = "PERCENT";
                s3 = ((COM.dragonflow.Properties.PercentProperty)stringproperty).getUnits();
            } else
            if(stringproperty instanceof COM.dragonflow.Properties.FrequencyProperty)
            {
                s1 = "FREQUENCY";
                s3 = "seconds";
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
                s3 = ((COM.dragonflow.Properties.NumericProperty)stringproperty).getUnits();
            }
            COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
            if(stringproperty instanceof COM.dragonflow.Properties.ScalarProperty)
            {
                COM.dragonflow.Properties.ScalarProperty scalarproperty = (COM.dragonflow.Properties.ScalarProperty)stringproperty;
                java.lang.Class class1 = java.lang.Class.forName("COM.dragonflow.SiteView.Group");
                if(s != null)
                {
                    httprequest.setValue("groupID", s);
                }
                siteviewobject = (COM.dragonflow.SiteView.SiteViewObject)class1.newInstance();
                java.lang.Class class2 = java.lang.Class.forName("COM.dragonflow.Page.groupPage");
                COM.dragonflow.Page.CGI cgi = (COM.dragonflow.Page.CGI)class2.newInstance();
                cgi.initialize(httprequest, null);
                java.util.Vector vector = siteviewobject.getScalarValues(scalarproperty, httprequest, cgi);
                as = new String[vector.size() / 2];
                as1 = new String[vector.size() / 2];
                int i = 0;
                for(int j = 0; i < vector.size() / 2; j += 2)
                {
                    as1[i] = (String)vector.elementAt(j);
                    as[i] = (String)vector.elementAt(j + 1);
                    i++;
                }

            }
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getClassProperty"
            }, 0L, exception.getMessage());
        }
        return new SSPropertyDetails(stringproperty.getName(), s1, stringproperty.getDescription(), stringproperty.getLabel(), stringproperty.isEditable, stringproperty.isMultivalued, stringproperty.getDefault(), as, as1, s2, false, false, stringproperty.getOrder(), s3, stringproperty.isAdvanced, stringproperty.isPassword, siteviewobject.getProperty(stringproperty.getName()));
    }

    private String moveGroup(String s, String s1, jgl.HashMap hashmap, boolean flag)
        throws SiteViewException
    {
        String s2 = null;
        try
        {
            String s3 = "";
            if(s.length() == 0)
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_EMPTY);
            }
            if(s.equalsIgnoreCase("siteview"))
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_NAME_SITEVIEW);
            }
            COM.dragonflow.Properties.HashMapOrdered hashmapordered = null;
            jgl.Array array = ReadGroupFrames(s);
            Enumeration enumeration = array.elements();
            hashmapordered = (COM.dragonflow.Properties.HashMapOrdered)enumeration.nextElement();
            String s4 = (String)hashmapordered.get("_name");
            if(s4.equals("config"))
            {
                s4 = s;
            }
            if(exceedsLicenseLimit(array, s))
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_LICENSE_LIMIT);
            }
            if(flag)
            {
                s = computeGroupName(s);
            }
            s2 = s;
            if(s1 == null || s1.length() == 0)
            {
                s1 = "";
            }
            if(!flag)
            {
                s3 = (String)hashmapordered.get("_parent");
            }
            hashmapordered.put("_name", "config");
            if(s3 != null && s3.length() != 0)
            {
                jgl.Array array1 = ReadGroupFrames(s3);
                Enumeration enumeration1 = array1.elements();
                enumeration1.nextElement();
                int i = findSubGroupIndex(array1, s4);
                array1.remove(i);
                WriteGroupFrames(s3, array1);
            }
            if(!s4.equals(s))
            {
                hashmapordered.put("_name", s4);
            }
            hashmapordered.put("_parent", s1);
            if(s1 != null && s1.length() > 0)
            {
                try
                {
                    jgl.Array array2 = ReadGroupFrames(s1);
                    jgl.HashMap hashmap1 = (jgl.HashMap)array2.at(0);
                    jgl.HashMap hashmap2 = new HashMap();
                    String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_nextID");
                    if(s5.length() == 0)
                    {
                        s5 = "1";
                    }
                    hashmap2.put("_id", s5);
                    hashmap2.put("_class", "SubGroup");
                    hashmap2.put("_group", s);
                    String s6 = COM.dragonflow.Utils.TextUtils.increment(s5);
                    hashmap1.put("_nextID", s6);
                    hashmap2.put("_name", s4);
                    array2.insert(array2.size(), hashmap2);
                    WriteGroupFrames(s1, array2);
                }
                catch(SiteViewException siteviewexception1)
                {
                    throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_CREATE_EXCEPTION, new String[] {
                        siteviewexception1.toString()
                    });
                }
            }
            updateConfig("_dependsOn", "", hashmap, hashmapordered);
            updateConfig("_dependsCondition", "good", hashmap, hashmapordered);
            updateConfig("_httpCharSet", "", hashmap, hashmapordered);
            updateConfig("__template", "", hashmap, hashmapordered);
            updateConfig("_description", "", hashmap, hashmapordered);
            try
            {
                removeAlertContextProperties(array);
                WriteGroupFrames(s, array);
                COM.dragonflow.SiteView.DetectConfigurationChange detectconfigurationchange = COM.dragonflow.SiteView.DetectConfigurationChange.getInstance();
                detectconfigurationchange.setConfigChangeFlag();
            }
            catch(SiteViewException siteviewexception2)
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_CREATE, new String[] {
                    siteviewexception2.getMessage()
                });
            }
            try
            {
                copySubGroups(s);
            }
            catch(java.lang.Exception exception1)
            {
                throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_COPY_SUBGROUP, new String[] {
                    exception1.getMessage()
                });
            }
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "moveGroup"
            }, 0L, exception.getMessage());
        }
        return s2;
    }

    private void removeAlertContextProperties(jgl.Array array)
    {
        boolean flag = false;
        for(int i = 0; i < array.size(); i++)
        {
            jgl.HashMap hashmap = (jgl.HashMap)array.at(i);
            jgl.Array array1 = COM.dragonflow.Utils.TextUtils.getMultipleValues(hashmap, "_alertCondition");
            java.util.HashMap hashmap1 = new java.util.HashMap();
            for(int j = 0; j < array1.size(); j++)
            {
                hashmap1.clear();
                COM.dragonflow.Api.Alert.getInstance().parseCondition((String)array1.at(j), hashmap1);
                if(hashmap1.get("_UIContext") != null)
                {
                    hashmap1.put("_UIContext", "");
                    array1.put(j, COM.dragonflow.Api.Alert.getInstance().createCondStr(hashmap1));
                    flag = true;
                }
            }

            if(flag)
            {
                hashmap.put("_alertCondition", array1);
            }
        }

    }

    private void copySubGroups(String s)
        throws SiteViewException
    {
        try
        {
            jgl.Array array = new Array();
            jgl.Array array1 = ReadGroupFrames(s);
            Enumeration enumeration = array1.elements();
            boolean flag = false;
            while (enumeration.hasMoreElements()) {
                jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
                if(COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap) && hashmap.get("_class").equals("SubGroup"))
                {
                    String s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_group"));
                    jgl.Array array2 = ReadGroupFrames(s1);
                    s1 = computeGroupName(s1);
                    if(array2.size() > 0)
                    {
                        jgl.HashMap hashmap1 = (jgl.HashMap)array2.at(0);
                        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_name");
                        if(s3 != null && s3.length() > 0 && s3.equalsIgnoreCase("config"))
                        {
                            hashmap1.put("_name", COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_name"));
                        }
                        hashmap1.put("_parent", s);
                        WriteGroupFrames(s1, array2);
                        hashmap.put("_group", s1);
                        array.add(s1);
                        flag = true;
                    }
                }
            } 
            
            if(flag)
            {
                WriteGroupFrames(s, array1);
            }
            String s2;
            for(Enumeration enumeration1 = array.elements(); enumeration1.hasMoreElements(); copySubGroups(s2))
            {
                s2 = (String)enumeration1.nextElement();
            }

        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "copySubGroups"
            }, 0L, exception.getMessage());
        }
    }

    private void updateConfig(String s, String s1, jgl.HashMap hashmap, jgl.HashMap hashmap1)
    {
        String s2 = "";
        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, s);
        if(s3 != null && s3.length() > 0)
        {
            s2 = s3;
        } else
        {
            String s4 = (String)hashmap1.get(s);
            if(s4 == null || s4.length() == 0)
            {
                s2 = s1;
            }
        }
        if(s2 != null && s2.length() > 0)
        {
            if(s.equals("_description"))
            {
                updateDescription(hashmap1, s2);
            } else
            {
                hashmap1.put(s, s2);
            }
        }
    }

    private void updateDescription(jgl.HashMap hashmap, String s)
    {
        hashmap.remove("_description");
        jgl.Array array = COM.dragonflow.SiteView.Platform.split('\n', s);
        for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); hashmap.add("_description", enumeration.nextElement())) { }
    }

    private boolean exceedsLicenseLimit(jgl.Array array, String s)
    {
        boolean flag = COM.dragonflow.Utils.LUtils.wouldExceedLimit(array, s);
        return flag;
    }

    private jgl.Array getReportFrameList()
        throws SiteViewException
    {
        jgl.Array array = new Array();
        try
        {
            if(!COM.dragonflow.SiteView.Platform.isStandardAccount(account))
            {
                array = ReadGroupFrames(account);
            } else
            {
                array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "history.config");
            }
        }
        catch(SiteViewException siteviewexception)
        {
            array = new Array();
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "getReportFrameList"
            }, 0L, exception.getMessage());
        }
        return array;
    }

    private int findSubGroupIndex(jgl.Array array, String s)
        throws SiteViewException
    {
        Enumeration enumeration = array.elements();
        int i = 0;
        enumeration.nextElement();
        i++;
        int j = -1;
        for(; enumeration.hasMoreElements(); i++)
        {
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            if(!COM.dragonflow.SiteView.Monitor.isMonitorFrame(hashmap))
            {
                continue;
            }
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_group");
            if(s1.length() == 0 || !s1.equals(s))
            {
                continue;
            }
            j = i;
            break;
        }

        if(j == -1)
        {
            throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_SUBGROUP_NOT_FOUND, new String[] {
                "APIGroup", "getPropertiesForGroupInstance", s
            });
        } else
        {
            return j;
        }
    }

    private void saveReportFrameList(jgl.Array array, String s)
        throws SiteViewException
    {
        try
        {
            if(!COM.dragonflow.SiteView.Platform.isStandardAccount(s))
            {
                WriteGroupFrames(s, array);
            } else
            {
                COM.dragonflow.Properties.FrameFile.writeToFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "history.config", array);
            }
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_EXCEPTION, new String[] {
                "APIGroup", "saveReportFrameList"
            }, 0L, exception.getMessage());
        }
    }

    private void deleteGroupFromReport()
        throws SiteViewException
    {
        deleteMonitorFromReport();
    }

    private void deleteMonitorFromReport()
        throws SiteViewException
    {
        try
        {
            jgl.Array array = new Array();
            jgl.Array array1 = getReportFrameList();
            Enumeration enumeration = array1.elements();
            while (enumeration.hasMoreElements()) {
                jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
                if(!COM.dragonflow.SiteView.Monitor.isReportFrame(hashmap))
                {
                    array.add(hashmap);
                } else
                {
                    jgl.HashMap hashmap1 = copyHashMap(hashmap);
                    hashmap1.remove("monitors");
                    Enumeration enumeration1 = hashmap.values("monitors");
                    while (enumeration1.hasMoreElements()) {
                        String s = (String)enumeration1.nextElement();
                        String as[] = COM.dragonflow.Utils.TextUtils.split(s);
                        if(as.length >= 1)
                        {
                            hashmap1.add("monitors", s);
                        }
                    }
                    enumeration1 = hashmap1.values("monitors");
                    if(enumeration1.hasMoreElements())
                    {
                        array.add(hashmap1);
                    }
                }
            } 
            saveReportFrameList(array, account);
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "deleteMonitorFromReport"
            }, 0L, exception.getMessage());
        }
    }

    private jgl.HashMap copyHashMap(jgl.HashMap hashmap)
    {
        java.lang.Object obj;
        if(hashmap instanceof COM.dragonflow.Properties.HashMapOrdered)
        {
            obj = new HashMapOrdered(true);
        } else
        {
            obj = new HashMap();
        }
        java.lang.Object obj1;
        for(Enumeration enumeration = hashmap.keys(); enumeration.hasMoreElements(); ((jgl.HashMap) (obj)).put(obj1, hashmap.get(obj1)))
        {
            obj1 = enumeration.nextElement();
        }

        return ((jgl.HashMap) (obj));
    }

    private void deleteGroupInternal(String s)
        throws SiteViewException
    {
        try
        {
            if(debug)
            {
                COM.dragonflow.Utils.TextUtils.debugPrint("DELETING GROUP " + s);
            }
            java.io.File file = new File(getGroupFilePath(s, ".mg"));
            if(debug)
            {
                COM.dragonflow.Utils.TextUtils.debugPrint("DELETING GROUP IN FILE " + file);
            }
            if(!file.exists())
            {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_GROUP_ID_NOT_VALID, new String[] {
                    s
                });
            }
            jgl.Array array = getGroupFrames(s);
            Enumeration enumeration = array.elements();
            jgl.HashMap hashmap = (jgl.HashMap)enumeration.nextElement();
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_parent");
            if(s1.length() != 0)
            {
                jgl.Array array1 = getGroupFrames(s1);
                int i = findSubGroupIndex(array1, s);
                if(i >= 1)
                {
                    array1.remove(i);
                }
            }
            jgl.Array array2 = new Array();
            jgl.Array array3 = new Array();
            while (enumeration.hasMoreElements()) {
                jgl.HashMap hashmap1 = (jgl.HashMap)enumeration.nextElement();
                String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_class");
                if(s2.equals("SubGroup"))
                {
                    String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_group");
                    if(s3.length() != 0)
                    {
                        array2.add(s3);
                    }
                } else
                {
                    COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                    String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_id");
                    String s5 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s + COM.dragonflow.SiteView.SiteViewGroup.ID_SEPARATOR + s4);
                    COM.dragonflow.SiteView.AtomicMonitor atomicmonitor = (COM.dragonflow.SiteView.AtomicMonitor)siteviewgroup.getElement(s5);
                    if(atomicmonitor != null)
                    {
                        if(!s2.equals("SubGroup"))
                        {
                            siteviewgroup.notifyMonitorDeletion(atomicmonitor);
                        }
//                        if(COM.dragonflow.SiteView.TopazConfigurator.configInTopazAndRegistered() && !s2.equals("SubGroup"))
//                        {
//                            array3.add(atomicmonitor);
//                        }
                    }
                }
            } 
//            if(COM.dragonflow.SiteView.TopazConfigurator.configInTopazAndRegistered())
//            {
//                StringBuffer stringbuffer = new StringBuffer();
//                COM.dragonflow.SiteView.TopazConfigurator.updateTopaz(array3, 1, stringbuffer, false, null);
//                if(stringbuffer.length() > 0)
//                {
//                    COM.dragonflow.Log.LogManager.log("Topaz", " Error occurred during delete of group in " + COM.dragonflow.SiteView.TopazInfo.getTopazName() + " " + stringbuffer);
//                }
//            }
            for(int j = 0; j < array2.size(); j++)
            {
                deleteGroupInternal((String)array2.at(j));
            }

            if(COM.dragonflow.Properties.JdbcConfig.configInDB())
            {
                COM.dragonflow.Properties.JdbcConfig.deleteGroupFromDB(s);
            }
//            if(COM.dragonflow.SiteView.TopazConfigurator.configInTopazAndRegistered())
//            {
//                COM.dragonflow.SiteView.TopazConfigurator.deleteGroupFromTopaz(s);
//            }
            deleteGroupFromReport();
            if(debug)
            {
                COM.dragonflow.Utils.TextUtils.debugPrint("DELETING FILE " + file);
            }
            file.delete();
            java.io.File file1 = new File(getGroupFilePath(s, ".mg.bak"));
            if(file1.exists())
            {
                file1.delete();
            }
            java.io.File file2 = new File(getGroupFilePath(s, ".dyn"));
            if(file2.exists())
            {
                file2.delete();
            }
            java.io.File file3 = new File(getGroupFilePath(s, ".dyn.bak"));
            if(file3.exists())
            {
                file3.delete();
            }
            groups.remove(s);
        }
        catch(SiteViewException siteviewexception)
        {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        }
        catch(java.lang.Exception exception)
        {
            throw new SiteViewOperationalException(SiteViewErrorCodes.ERR_OP_SS_GROUP_EXCEPTION, new String[] {
                "APIGroup", "deleteGroupInternal"
            }, 0L, exception.getMessage());
        }
    }

    private String computeGroupName(String s)
    {
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        jgl.Array array = siteviewgroup.getGroupFileIDs();
        String s1 = s.toUpperCase() + ".";
        int i = -1;
        Enumeration enumeration = array.elements();
        while (enumeration.hasMoreElements()) {
            String s2 = (String)enumeration.nextElement();
            s2 = s2.toUpperCase() + '.';
            if(s2.startsWith(s1))
            {
                int j = COM.dragonflow.Utils.TextUtils.readInteger(s2, s1.length());
                if(j < 0)
                {
                    j = 0;
                }
                if(j > i)
                {
                    i = j;
                }
            }
        } 
        
        if(i++ >= 0)
        {
            s = s + "." + i;
        }
        return s;
    }

    public boolean hasSubGroupDependencies(String s)
        throws SiteViewException
    {
        java.util.Collection collection = getChildGroupInstances(s);
        return collection != null && collection.size() > 0;
    }
}
