/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * Health.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>Health</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Api.APIGroup;
import COM.dragonflow.Api.APISiteView;
import COM.dragonflow.ConfigurationManager.CfgChangesSink;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// MonitorGroup, SiteViewObject, SubGroup, ConfigurationChanger,
// Machine, Platform, SiteViewGroup

public class Health implements CfgChangesSink {

    public static String ok = "good";

    public static final String groupID = "__Health__";

    public static final String groupFileName = "__Health__.mg";

    private static Health thisHealth = null;

    private static Set groupIDsInHealth = new HashSet();

    public static Array indicators = null;

    public static boolean debug = false;

    public static Health getHealth() {
        if (thisHealth == null) {
            thisHealth = new Health();
        }
        return thisHealth;
    }

    protected Health() {
        String s = System.getProperty("Health.debug");
        if (s != null) {
            debug = true;
            System.out.println("Health.debug= '" + debug + "'");
        }
        thisHealth = this;
        ConfigurationChanger.registerCfgChangesSink(this);
    }

    public static void mergeConfig() {
        insureMinHealth();
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     */
    private static void insureMinHealth() {
        try {
            Array array = FrameFile.readFromFile(Health.CheckCreateHealth());
            if (array.size() > 1) {
                return;
            } else {
                HashMap hashmap = new HashMap();
                String as[] = Health.getHealth().getTemplateList(
                        "administrator");
                if (as != null && as.length > 0 && as[0].length() > 0) {
                    hashmap.put("_healthTemplateSet", as[0]);
                    hashmap.put("_healthDisableLogging", "");
                    getHealth().update(hashmap, "administrator");
                }
            }
        } catch (Exception exception) {
            LogManager.log("error",
                    "Couldn't add default health monitors to the Health subsystem. Got Error: "
                            + exception.getMessage());
        }
    }

    public String update(HashMap hashmap, String s) throws SiteViewException {
        String s1 = (String) hashmap.get("_healthTemplateSet");
        String s2 = "";
        String s3 = "";
        int i = Machine.getOS("");
        String s4 = i != 1 ? "Unix" : "NT";
        if (!Platform.isStandardAccount(s)) {
            s2 = Platform.getRoot() + File.separator + "accounts"
                    + File.separator + s + File.separator + "templates.health";
            if (!(new File(s2)).exists()) {
                s2 = Platform.getRoot() + File.separator + "templates.health";
            }
            s3 = Platform.getRoot() + File.separator + "accounts"
                    + File.separator + s + File.separator + "groups";
            if (!(new File(s3)).exists()) {
                s3 = Platform.getRoot() + File.separator + "groups";
            }
        } else {
            s2 = Platform.getRoot() + File.separator + "templates.health";
            s3 = Platform.getRoot() + File.separator + "groups";
        }
        if (s1 != null && s1.length() > 0) {
            File file = new File(s2);
            String as[] = file.list();
            Array array = null;
            try {
                array = FrameFile.readFromFile(s3 + "/" + "__Health__.mg");
            } catch (IOException ioexception) {
            }
            if (array == null || array.size() == 0) {
                array = createHealthGroup();
                APISiteView.forceConfigurationRefresh();
            }
            Array array1 = null;
            try {
                array1 = FrameFile.readFromFile(s2 + "/" + s4 + s1);
            } catch (IOException ioexception1) {
                String as1[] = { ioexception1.getMessage() };
                throw new SiteViewParameterException(
                        SiteViewErrorCodes.ERR_OP_SS_HEALTH_NO_TEMPLATE_FILE,
                        as1);
            }
            if (array != null && array1 != null) {
                HashMap hashmap1 = (HashMap) array.at(0);
                String s5 = (String) hashmap1.get("_nextID");
                for (int j = 1; j < array1.size(); j++) {
                    HashMap hashmap2 = (HashMap) array1.at(j);
                    HashMap hashmap3 = new HashMap();
                    hashmap3.put("_id", s5);
                    String s6;
                    for (Enumeration enumeration = hashmap2.keys(); enumeration
                            .hasMoreElements(); hashmap3.put(s6, hashmap2
                            .get(s6))) {
                        s6 = (String) enumeration.nextElement();
                    }

                    array.add(hashmap3);
                    s5 = TextUtils.increment(s5);
                }

                hashmap1.put("_nextID", s5);
                try {
                    FrameFile.writeToFile(s3 + "/" + "__Health__.mg", array);
                } catch (IOException ioexception2) {
                    String as2[] = { "__Health__.mg", ioexception2.getMessage() };
                    throw new SiteViewParameterException(
                            SiteViewErrorCodes.ERR_OP_SS_HEALTH_CANNOT_WRITE,
                            as2);
                }
            }
            hashmap.remove("_healthTemplateSet");
            updateMaster(hashmap);
            APIGroup.forceConfigurationRefresh();
            return s1;
        } else {
            hashmap.remove("_healthTemplateSet");
            updateMaster(hashmap);
            APIGroup.forceConfigurationRefresh();
            return "";
        }
    }

    public String[] getTemplateList(String s) throws SiteViewException {
        Vector vector = new Vector();
        String s1 = "";
        String s2 = "";
        int i = Machine.getOS("");
        String s3 = i != 1 ? "Unix" : "NT";
        if (!Platform.isStandardAccount(s)) {
            s1 = Platform.getRoot() + File.separator + "accounts"
                    + File.separator + s + File.separator + "templates.health";
            if (!(new File(s1)).exists()) {
                s1 = Platform.getRoot() + s2 + File.separator
                        + "templates.health";
            }
        } else {
            s1 = Platform.getRoot() + s2 + File.separator + "templates.health";
        }
        File file = new File(s1);
        String as[] = file.list();
        for (int j = 0; as != null && j < as.length; j++) {
            if (as[j].indexOf(".") < 0 && as[j].startsWith(s3)) {
                vector.addElement(as[j].substring(s3.length()));
            }
        }

        String as1[] = new String[vector.size()];
        for (int k = 0; k < vector.size(); k++) {
            as1[k] = (String) vector.elementAt(k);
        }

        return as1;
    }

    public static String getHealthState() {
        String s = "good";
        String s1 = CheckCreateHealth();
        MonitorGroup monitorgroup = MonitorGroup.loadGroup("__Health__", s1);
        if (monitorgroup != null) {
            String s2 = monitorgroup.getProperty(MonitorGroup.pCategory);
            s = s2;
        } else {
            LogManager.log("Error", "Unable to find Health System.");
        }
        return s;
    }

    protected static String CheckCreateHealth() {
        String s = Platform.getRoot() + "/groups/" + "__Health__.mg";
        File file = new File(s);
        if (!file.exists()) {
            try {
                createHealthGroup();
            } catch (SiteViewParameterException siteviewparameterexception) {
                LogManager.log("Error", "Unable to write Health file Error: "
                        + siteviewparameterexception.getLocalizedMessage());
            }
        }
        return s;
    }

    public static boolean isInHealth(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        String s1 = s;
        int i = s.toLowerCase().indexOf(".mg");
        if (i >= 0) {
            s1 = s.substring(0, i);
        }
        return inHealth(s1);
    }

    public static boolean isInHealth(SiteViewObject siteviewobject) {
        if (siteviewobject == null) {
            return false;
        } else {
            String s = (siteviewobject instanceof MonitorGroup) ? siteviewobject
                    .getProperty(SiteViewObject.pID)
                    : siteviewobject.getProperty("_group");
            return inHealth(s);
        }
    }

    private static boolean inHealth(String s) {
        if (s == null || s.length() == 0)
            return false;
        String s1 = s;
        int i = s.toLowerCase().indexOf(".mg");
        if (i >= 0)
            s1 = s.substring(0, i);
        return inHealth(s1);
    }

    public void notifyAdjustedGroups(SiteViewGroup siteviewgroup,
            Collection collection, Collection collection1,
            Collection collection2) {
        loadHealthGroupList();
    }

   /**
    * CAUTION: Decompiled by hand.
    *
    */
    public static void loadHealthGroupList() {
        synchronized (groupIDsInHealth) {
            groupIDsInHealth.clear();
            SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
            MonitorGroup monitorgroup = (MonitorGroup) siteviewgroup
                    .getElementByID("__Health__");
            if (monitorgroup == null) {
                createHealthGroupList("__Health__");
            } else {
                groupIDsInHealth.add(monitorgroup
                        .getProperty(SiteViewObject.pID));
                Enumeration enumeration = monitorgroup.getElements();
                while (enumeration.hasMoreElements()) {
                    SiteViewObject siteviewobject = (SiteViewObject) enumeration
                            .nextElement();
                    if (siteviewobject instanceof SubGroup) {
                        addSubGroup((SubGroup) siteviewobject);
                    }
                }
            }
        }
    }

    private static void createHealthGroupList(String s) {
        if (groupIDsInHealth.contains(s)) {
            return;
        }
        groupIDsInHealth.add(s);
        String s1 = Platform.getRoot() + "/groups/";
        try {
            Array array = FrameFile.readFromFile(s1 + s + ".mg");
            for (int i = 1; i < array.size(); i++) {
                HashMap hashmap = (HashMap) array.at(i);
                String s2 = (String) hashmap.get("_class");
                if (s2 != null && s2.equals("SubGroup")) {
                    createHealthGroupList((String) hashmap.get("_group"));
                }
            }

        } catch (IOException ioexception) {
            return;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param subgroup
     */
    private static void addSubGroup(SubGroup subgroup) {
        if (groupIDsInHealth.contains(subgroup.getProperty("_group"))) {
            return;
        }
        groupIDsInHealth.add(subgroup.getProperty("_group"));
        MonitorGroup monitorgroup = subgroup.lookupGroup();
        Enumeration enumeration = monitorgroup.getElements();
        while (enumeration.hasMoreElements()) {
            SiteViewObject siteviewobject = (SiteViewObject) enumeration
                    .nextElement();
            if (siteviewobject instanceof SubGroup) {
                SubGroup subgroup1 = (SubGroup) siteviewobject;
                addSubGroup(subgroup1);
            }
        }
    }

    public static Array createHealthGroup() throws SiteViewParameterException {
        String s = Platform.getRoot() + File.separator + "groups"
                + File.separator + "__Health__.mg";
        File file = new File(s);
        Array array = null;
        if (!file.exists()) {
            array = new Array();
            HashMap hashmap = new HashMap();
            hashmap.put("_encoding", I18N.getDefaultEncoding());
            hashmap.put("_health", "true");
            hashmap.put("_dependsCondition", "good");
            hashmap.put("_fileEncoding", I18N.getDefaultEncoding());
            hashmap.put("_name", "Health");
            hashmap.put("_nextID", "1");
            array.add(hashmap);
            try {
                FrameFile.writeToFile(s, array);
            } catch (IOException ioexception1) {
                String as[] = { "__Health__.mg", ioexception1.getMessage() };
                throw new SiteViewParameterException(
                        SiteViewErrorCodes.ERR_OP_SS_HEALTH_CANNOT_WRITE, as);
            }
        } else {
            try {
                array = FrameFile.readFromFile(s);
            } catch (IOException ioexception) {
                LogManager.log("error",
                        "Couldn't read the Health group. Got Error: "
                                + ioexception.getMessage());
            }
        }
        return array;
    }

    private void updateMaster(HashMap hashmap) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String s;
        for (Enumeration enumeration = hashmap.keys(); enumeration
                .hasMoreElements(); siteviewgroup.setProperty(s,
                (String) hashmap.get(s))) {
            s = (String) enumeration.nextElement();
        }

        siteviewgroup.saveSettings();
    }

    static {
        CheckCreateHealth();
        loadHealthGroupList();
    }
}
