/*
 * 
 * Created on 2005-2-16 16:22:20
 *
 * Preferences.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Preferences</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import jgl.Array;
import COM.dragonflow.Api.APISiteView;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;
import COM.dragonflow.StandardPreference.DynamicUpdateInstancePreferences;
import COM.dragonflow.StandardPreference.MailInstancePreferences;
import COM.dragonflow.StandardPreference.PagerInstancePreferences;
import COM.dragonflow.StandardPreference.RemoteNTInstancePreferences;
import COM.dragonflow.StandardPreference.RemoteUnixInstancePreferences;
import COM.dragonflow.StandardPreference.SNMPInstancePreferences;
import COM.dragonflow.StandardPreference.ScheduleInstancePreferences;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// SiteViewObject, MasterConfig

public class Preferences extends SiteViewObject {

    private static StringProperty pNextAdditionalMailID = new StringProperty(
            "_nextAdditionalMailID");

    private static StringProperty pNextAdditionalPagerID = new StringProperty(
            "_nextAdditionalPagerID");

    private static StringProperty pNextAdditionalScheduleID = new StringProperty(
            "_nextAdditionalScheduleID");

    private static StringProperty pNextAdditionalSNMPID = new StringProperty(
            "_nextAdditionalSNMPID");

    private static StringProperty pNextDynamicID = new StringProperty(
            "_nextDynamicID");

    private static StringProperty pNextRemoteID = new StringProperty(
            "_nextRemoteID");

    private static StringProperty pNextRemoteNTID = new StringProperty(
            "_nextNTRemoteID");

    public static final String SSPARAM_SCHEDULE = "_schedule";

    public Preferences() {
    }

    public Vector test(String s) throws Exception {
        throw new Exception("Preference Type does not support a test operation");
    }

    public boolean hasMultipleValues() {
        return false;
    }

    public String getSettingName() {
        return "";
    }

    public String getReturnName() {
        return null;
    }

    protected StringProperty getNextIdProperty(Preferences preferences) {
        StringProperty stringproperty = new StringProperty("");
        if (preferences instanceof MailInstancePreferences) {
            stringproperty = pNextAdditionalMailID;
        } else if (preferences instanceof PagerInstancePreferences) {
            stringproperty = pNextAdditionalPagerID;
        } else if (preferences instanceof ScheduleInstancePreferences) {
            stringproperty = pNextAdditionalScheduleID;
        } else if (preferences instanceof SNMPInstancePreferences) {
            stringproperty = pNextAdditionalSNMPID;
        } else if (preferences instanceof RemoteUnixInstancePreferences) {
            stringproperty = pNextRemoteID;
        } else if (preferences instanceof RemoteNTInstancePreferences) {
            stringproperty = pNextRemoteNTID;
        } else if (preferences instanceof DynamicUpdateInstancePreferences) {
            stringproperty = pNextDynamicID;
        }
        return stringproperty;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public Vector getSettings(String s) {
        Vector vector = new Vector();
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        if (hashmap != null) {
            Object obj = null;
            Object obj1 = null;
            if (s != null && s.length() > 0) {
                Enumeration enumeration = hashmap.values(s);
                while (enumeration.hasMoreElements()) {
                    HashMap hashmap1 = new HashMap();
                    String s1 = "";
                    s1 = (String) enumeration.nextElement();
                    String as[] = TextUtils.split(s1);
                    for (int i = 0; i < as.length; i++) {
                        int j = as[i].indexOf('=');
                        if (j <= 0) {
                            continue;
                        }
                        String s2 = as[i].substring(0, j);
                        if (s2 != null) {
                            String s3 = as[i].substring(j + 1)
                                    .replace('_', ' ');
                            hashmap1.put(s2, s3);
                        }
                    }

                    if (hashmap1 != null) {
                        vector.add(hashmap1);
                    }
                } 
            }
        }
        return vector;
    }

    public String[] addPreferences(HashMap hashmap) throws SiteViewException {
        jgl.HashMap hashmap1 = MasterConfig.getMasterConfig();
        String s = "";
        String s2 = "";
        String s5 = "";
        String as[] = new String[2];
        if (hasMultipleValues()) {
            String s6 = (String) hashmap1
                    .get(getNextIdProperty(this).getName());
            if (s6 == null || s6.length() <= 0) {
                s6 = "1";
            }
            String s3 = pID.getName() + "=" + s6 + " "
                    + TextUtils.hashMapToString(hashmap);
            hashmap1.add(getSettingName(), s3);
            int i = (new Integer(s6)).intValue();
            as[0] = pID.getName();
            as[1] = s6;
            s6 = String.valueOf(++i);
            hashmap1.put(getNextIdProperty(this).getName(), s6);
        } else {
            Set set = hashmap.keySet();
            String s1;
            String s4;
            for (Iterator iterator = set.iterator(); iterator.hasNext(); hashmap1
                    .put(s1, s4)) {
                s1 = (String) iterator.next();
                s4 = (String) hashmap.get(s1);
            }

            as[0] = "";
            as[1] = "";
        }
        MasterConfig.saveMasterConfig(hashmap1);
        return as;
    }

    public String[] updatePreferences(HashMap hashmap, String s, String s1)
            throws SiteViewException {
        jgl.HashMap hashmap1 = MasterConfig.getMasterConfig();
        String as[] = new String[2];
        if (hasMultipleValues()) {
            Enumeration enumeration = hashmap1.values(getSettingName());
            boolean flag = false;
            Array array = new Array();
            while (enumeration.hasMoreElements()) {
                jgl.HashMap hashmap2 = TextUtils
                        .stringToHashMap((String) enumeration.nextElement());
                String s4 = (String) hashmap2.get("_id");
                if (s4 == null) {
                    s4 = "";
                }
                if (s4.equals(s1)) {
                    Set set1 = hashmap.keySet();
                    String s5;
                    String s6;
                    for (Iterator iterator1 = set1.iterator(); iterator1
                            .hasNext(); hashmap2.put(s5, s6)) {
                        s5 = (String) iterator1.next();
                        s6 = (String) hashmap.get(s5);
                    }

                    array.add(hashmap2);
                    flag = true;
                    as[0] = s;
                    as[1] = s1;
                } else {
                    array.add(hashmap2);
                }
            }
            if (!flag) {
                throw new SiteViewOperationalException(
                        SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_UPDATE_EXCEPTION);
            }
            hashmap1.remove(getSettingName());
            for (int i = 0; i < array.size(); i++) {
                hashmap1.add(getSettingName(), TextUtils
                        .hashMapToOrderedString((jgl.HashMap) array.at(i)));
            }

        } else {
            Set set = hashmap.keySet();
            String s2;
            String s3;
            for (Iterator iterator = set.iterator(); iterator.hasNext(); hashmap1
                    .put(s2, s3)) {
                s2 = (String) iterator.next();
                s3 = (String) hashmap.get(s2);
                hashmap1.remove(s2);
            }

            as[0] = "";
            as[1] = "";
        }
        MasterConfig.saveMasterConfig(hashmap1);
        return as;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @throws SiteViewException
     */
    public void deletePreferences(String s, String s1)
            throws SiteViewException {
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        Array array = new Array();
        boolean flag = false;
        if (hasMultipleValues()) {
            Enumeration enumeration = hashmap.values(getSettingName());
            while (enumeration.hasMoreElements()) {
                jgl.HashMap hashmap1 = TextUtils
                        .stringToHashMap((String) enumeration.nextElement());
                String s2 = (String) hashmap1.get("_id");
                if (s2 == null) {
                    s2 = "";
                }
                if (!s2.equals(s1)) {
                    array.add(hashmap1);
                }
                if (s2.equals(s1)) {
                    flag = true;
                }
            } 

            hashmap.remove(getSettingName());
            for (int i = 0; i < array.size(); i++) {
                hashmap.add(getSettingName(), TextUtils
                        .hashMapToOrderedString((jgl.HashMap) array.at(i)));
            }

            MasterConfig.saveMasterConfig(hashmap);
            if (!flag) {
                throw new SiteViewParameterException(
                        SiteViewErrorCodes.ERR_PARAM_API_PREFERENCE_INSTANCE_NOTFOUND);
            } else {
                return;
            }
        } else {
            throw new SiteViewOperationalException(
                    SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_DELETE_NOT_ALLOWED);
        }
    }

    public Vector getPreferenceProperties(String s, String s1, String s2,
            String s3, int i) throws SiteViewException {
        // TODO need review
        Vector vector = new Vector();
        do {
            Vector vector_24_;
            try {
                Vector vector_25_ = new Vector();
                String string_26_ = "COM.dragonflow.StandardPreference." + s;
                Class var_class = Class.forName(string_26_);
                Preferences preferences_27_ = (Preferences) var_class
                        .newInstance();
                jgl.HashMap hashmap = MasterConfig.getMasterConfig();
                if (hashmap == null)
                    throw new SiteViewOperationalException(
                            SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_RETRIEVE_MASTER_SETTINGS);
                Object object = null;
                Object object_28_ = null;
                if (s1 != null && s1.length() > 0) {
                    Enumeration enumeration = hashmap.values(s1);
                    while (enumeration.hasMoreElements()) {
                        HashMap hashmap_29_ = new HashMap();
                        String string_30_ = "";
                        string_30_ = (String) enumeration.nextElement();
                        String[] strings = TextUtils.split(string_30_);
                        for (int i_31_ = 0; i_31_ < strings.length; i_31_++) {
                            int i_32_ = strings[i_31_].indexOf('=');
                            if (i_32_ > 0) {
                                String string_33_ = strings[i_31_].substring(0,
                                        i_32_);
                                if (string_33_ != null) {
                                    String string_34_ = strings[i_31_]
                                            .substring(i_32_ + 1).replace('_',
                                                    ' ');
                                    string_34_ = TextUtils
                                            .storedValueToValue(string_34_);
                                    hashmap_29_.put(string_33_, string_34_);
                                }
                            }
                        }
                        if (i == APISiteView.FILTER_CONFIGURATION_ALL)
                            hashmap_29_.put("_class", s);
                        if (s3 != null && s3 != "") {
                            String string_35_ = (String) hashmap_29_.get(s2);
                            if (string_35_ != null && s3.equals(string_35_)) {
                                vector.add(hashmap_29_);
                                break;
                            }
                        } else
                            vector.add(hashmap_29_);
                    }
                } else {
                    HashMap hashmap_36_ = new HashMap();
                    Array array = preferences_27_.getProperties();
                    for (int i_37_ = 0; i_37_ < array.size(); i_37_++) {
                        StringProperty stringproperty = (StringProperty) array
                                .at(i_37_);
                        String string_38_ = (String) hashmap.get(stringproperty
                                .getName());
                        if (string_38_ == null)
                            string_38_ = stringproperty.getDefault();
                        string_38_ = TextUtils.storedValueToValue(string_38_);
                        if (stringproperty.isPassword)
                            string_38_ = TextUtils.obscure(string_38_);
                        hashmap_36_.put(((StringProperty) array.at(i_37_))
                                .getName(), string_38_);
                    }
                    if (i == APISiteView.FILTER_CONFIGURATION_ALL)
                        hashmap_36_.put("_class", s);
                    if (i == APISiteView.FILTER_CONFIGURATION_ALL)
                        hashmap_36_.remove(s2);
                    vector.add(hashmap_36_);
                }
                if (vector == null
                        || i != APISiteView.FILTER_CONFIGURATION_ALL)
                    break;
                for (int i_39_ = 0; i_39_ < vector.size(); i_39_++) {
                    HashMap hashmap_40_ = new HashMap();
                    HTTPRequest httprequest = new HTTPRequest();
                    HashMap hashmap_41_ = (HashMap) vector.get(i_39_);
                    Set set = hashmap_41_.keySet();
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        String string_42_ = (String) iterator.next();
                        String string_43_ = (String) hashmap_41_
                                .get(string_42_);
                        hashmap_41_.put(string_42_, string_43_);
                        StringProperty stringproperty = preferences_27_
                                .getPropertyObject(string_42_);
                        if (stringproperty != null) {
                            String string_44_ = verify(stringproperty,
                                    string_43_, httprequest, hashmap_41_,
                                    hashmap_40_);
                            if (string_44_ != null && string_44_ != "")
                                hashmap_41_.put(string_42_, string_44_);
                        }
                    }
                    vector_25_.add(hashmap_41_);
                }
                vector_24_ = vector_25_;
            } catch (Exception exception) {
                throw new SiteViewOperationalException(
                        SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION,
                        new String[] { "Preferences", "getPreferenceProperties" },
                        0L, exception.getMessage());
            }
            return vector_24_;
        } while (false);
        return vector;
    }

    public HashMap validateProperties(HashMap hashmap, Array array,
            HashMap hashmap1) throws Exception {
        HashMap hashmap2 = new HashMap();
        if (hashmap.get("_class") != null) {
            hashmap.remove("_class");
        }
        HashMap hashmap3 = (HashMap) hashmap.clone();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                StringProperty stringproperty = (StringProperty) array.at(i);
                String s1 = stringproperty.getName();
                String s2 = (String) hashmap.get(s1);
                if (hashmap.get(s1) == null) {
                    continue;
                }
                hashmap3.remove(s1);
                if (s2 == null) {
                    s2 = stringproperty.getDefault();
                }
                if (s2 instanceof String) {
                    s2 = verify(stringproperty, s2, new HTTPRequest(), hashmap,
                            hashmap1);
                }
                hashmap2.put(s1, s2);
            }

        }
        if (hashmap1.size() > 0) {
            HashMap hashmap4 = new HashMap();
            Set set = hashmap1.keySet();
            Object obj;
            for (Iterator iterator = set.iterator(); iterator.hasNext(); hashmap4
                    .put(obj.toString(), hashmap1.get(obj))) {
                obj = iterator.next();
            }

            throw new SiteViewParameterException(
                    SiteViewErrorCodes.ERR_PARAM_API_PREFERENCE_VERIFICATION_ERRORS,
                    hashmap4);
        }
        if (hashmap3.size() > 0) {
            String s = "";
            Set set1 = hashmap3.keySet();
            for (Iterator iterator1 = set1.iterator(); iterator1.hasNext();) {
                if (s.length() > 0) {
                    s = s + ", ";
                }
                s = s + iterator1.next();
            }

            LogManager.log("error",
                    "APIPreferences: Unrecognized properties for add operation: "
                            + s);
        }
        return hashmap2;
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap, HashMap hashmap1) {
        return s;
    }

    public Vector getScalarValues(ScalarProperty scalarproperty,
            HTTPRequest httprequest, CGI cgi) throws SiteViewException {
        return new Vector();
    }

    protected void checkIfValid(ScalarProperty scalarproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        boolean flag = false;
        flag = true;
        if (!flag) {
            hashmap.put(scalarproperty, "Please enter a valid value.");
        }
    }

    public static void main(String args[]) throws Exception {
    }

}
