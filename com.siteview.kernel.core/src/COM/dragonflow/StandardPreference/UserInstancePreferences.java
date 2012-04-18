/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardPreference;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.Vector;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.recursionsw.jgl.algorithms.Sorting;
import com.recursionsw.jgl.predicates.LessString;

public class UserInstancePreferences extends COM.dragonflow.SiteView.Preferences {

    public static COM.dragonflow.Properties.StringProperty pLogin;

    public static COM.dragonflow.Properties.StringProperty pPassword;

    public static COM.dragonflow.Properties.StringProperty pPassword2;

    public static COM.dragonflow.Properties.StringProperty pLdapserver;

    public static COM.dragonflow.Properties.StringProperty pSecurityprincipal;

    public static COM.dragonflow.Properties.ScalarProperty pGroup;

    public static COM.dragonflow.Properties.StringProperty pDisabled;

    public static COM.dragonflow.Properties.StringProperty pRealName;

    public static COM.dragonflow.Properties.StringProperty pGroupEdit;

    public static COM.dragonflow.Properties.StringProperty pMonitorEdit;

    public static COM.dragonflow.Properties.StringProperty pAlertEdit;

    public static COM.dragonflow.Properties.StringProperty pReportEdit;

    public static COM.dragonflow.Properties.StringProperty pMultiEdit;

    public static COM.dragonflow.Properties.StringProperty pPreference;

    public static COM.dragonflow.Properties.StringProperty pReportAdhoc;

    public static COM.dragonflow.Properties.StringProperty pAlertAdhocReport;

    public static COM.dragonflow.Properties.StringProperty pAlertTest;

    public static COM.dragonflow.Properties.StringProperty pPreferenceTest;

    public static COM.dragonflow.Properties.StringProperty pGroupDisable;

    public static COM.dragonflow.Properties.StringProperty pMonitorDisable;

    public static COM.dragonflow.Properties.StringProperty pAlertDisable;

    public static COM.dragonflow.Properties.StringProperty pReportDisable;

    public static COM.dragonflow.Properties.StringProperty pGroupRefresh;

    public static COM.dragonflow.Properties.StringProperty pMonitorRefresh;

    public static COM.dragonflow.Properties.StringProperty pMonitorAcknowledge;

    public static COM.dragonflow.Properties.StringProperty pMonitorTools;

    public static COM.dragonflow.Properties.StringProperty pTools;

    public static COM.dragonflow.Properties.StringProperty pBrowse;

    public static COM.dragonflow.Properties.StringProperty pSupport;

    public static COM.dragonflow.Properties.StringProperty pMonitorRecent;

    public static COM.dragonflow.Properties.StringProperty pAlertRecentReport;

    public static COM.dragonflow.Properties.StringProperty pProgress;

    public static COM.dragonflow.Properties.StringProperty pLogs;

    public static COM.dragonflow.Properties.StringProperty pAlertList;

    public static COM.dragonflow.Properties.StringProperty pReportToolbar;

    public static COM.dragonflow.Properties.StringProperty pReportGenerate;

    public static COM.dragonflow.Properties.StringProperty pHealthDisable;

    public static COM.dragonflow.Properties.StringProperty pAlertTempDisable;

    public static COM.dragonflow.Properties.StringProperty pHealthView;

    public static COM.dragonflow.Properties.StringProperty pHealthEdit;

    public UserInstancePreferences() {
    }

    public String[] addPreferences(HashMap hashmap) throws COM.dragonflow.SiteViewException.SiteViewException {
        String as[] = new String[2];
        try {
            HashMap hashmap1 = new HashMap();
            COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
            String s = "";
            String s2 = "";
            Array array = COM.dragonflow.SiteView.User.readUsers();
            HashMap hashmap2 = new HashMap(true);
            HashMap hashmap3 = (HashMap) array.get(0);
            for (int i = 0; i < hashmap.size(); i ++) {
                for (Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements();) {
                    String s1 = (String) enumeration.nextElement();
                    String s3 = (String) hashmap.get(s1);
                    COM.dragonflow.Properties.StringProperty stringproperty = getPropertyObject(s1);
                    if (stringproperty != null) {
                        s3 = verify(stringproperty, s3, httprequest, hashmap1);
                        hashmap2.put(s1, s3);
                    } else {
                        throw new SiteViewParameterException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_PREFERENCE_NONEXISTANT_PROPERTY, new String[] { s1 });
                    }
                }

                if (hashmap1.size() > 0) {
                    java.util.HashMap hashmap4 = new java.util.HashMap();
                    COM.dragonflow.Properties.StringProperty stringproperty1;
                    for (Enumeration enumeration1 = (Enumeration) hashmap1.keys(); enumeration1.hasMoreElements(); hashmap4.put(stringproperty1.getName(), hashmap1.get(stringproperty1))) {
                        stringproperty1 = (COM.dragonflow.Properties.StringProperty) enumeration1.nextElement();
                    }

                    throw new SiteViewParameterException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_PREFERENCE_PROPERTY_EXCEPTION, hashmap4);
                }
            }

            String s4 = (String) hashmap3.get("_nextID");
            hashmap2.put(pID.getName(), "login" + s4);
            array.add(hashmap2);
            int j = (new Integer(s4)).intValue();
            as[0] = pID.getName();
            as[1] = (String) hashmap2.get(pID.getName());
            s4 = String.valueOf(++ j);
            hashmap3.put("_nextID", s4);
            array.put(0, hashmap3);
            COM.dragonflow.SiteView.User.writeUsers(array);
        } catch (java.lang.Exception exception) {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] { "UserInstancePreferences", "addPreferences" }, 0L, exception.getMessage());
        }
        return as;
    }

    public String[] updatePreferences(HashMap hashmap, String s, String s1) throws COM.dragonflow.SiteViewException.SiteViewException {
        String as[] = new String[2];
        try {
            Array array = COM.dragonflow.SiteView.User.readUsers();
            HashMap hashmap1 = (HashMap) array.get(0);
            as[0] = "";
            as[1] = "";
            int i = 1;
            do {
                if (i >= array.size()) {
                    break;
                }
                HashMap hashmap2 = (HashMap) array.get(i);
                String s2 = (String) hashmap2.get(s);
                if (s2.equals(s1)) {
                    for (Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements();) {
                        String s3 = (String) enumeration.nextElement();
                        String s4 = (String) hashmap.get(s3);
                        hashmap2.put(s3, s4);
                        as[0] = s;
                        as[1] = s1;
                    }

                    array.put(i, hashmap2);
                    break;
                }
                i ++;
            } while (true);
            COM.dragonflow.SiteView.User.writeUsers(array);
        } catch (java.lang.Exception exception) {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] { "UserInstancePreferences", "updatePreferences" }, 0L, exception.getMessage());
        }
        return as;
    }

    public void deletePreferences(String s, String s1) throws COM.dragonflow.SiteViewException.SiteViewException {
        try {
            Array array = COM.dragonflow.SiteView.User.readUsers();
            HashMap hashmap = (HashMap) array.get(0);
            int i = 1;
            do {
                if (i >= array.size()) {
                    break;
                }
                HashMap hashmap1 = (HashMap) array.get(i);
                String s2 = (String) hashmap1.get(s);
                if (s2.equals(s1)) {
                    array.remove(i);
                    break;
                }
                i ++;
            } while (true);
            COM.dragonflow.SiteView.User.writeUsers(array);
        } catch (java.lang.Exception exception) {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] { "UserInstancePreferences", "deletePreferences" }, 0L, exception.getMessage());
        }
    }

    public java.util.Vector getPreferenceProperties(String s, String s1, String s2, String s3, int i) throws COM.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        try {
            Array array = COM.dragonflow.SiteView.User.readUsers();
            Object obj = null;
            HashMap hashmap1 = (HashMap) array.get(0);
            int j = 1;
            do {
                if (j >= array.size()) {
                    break;
                }
                HashMap hashmap2 = (HashMap) array.get(j);
                if (s2.length() > 0) {
                    String s4 = (String) hashmap2.get(s2);
                    if (s4.equals(s3)) {
                        HashMap hashmap = hashmap2;
                        vector.add(hashmap);
                        break;
                    }
                } else {
                    vector.add(hashmap2);
                }
                j ++;
            } while (true);
        } catch (java.lang.Exception exception) {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] { "UserInstancePreferences", "getPreferenceProperties" }, 0L, exception.getMessage());
        }
        return vector;
    }

    public boolean hasMultipleValues() {
        return true;
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap, HashMap hashmap1) {
        return s;
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest) throws COM.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        if (scalarproperty == pGroup) {
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            Array array = siteviewgroup.getGroupIDs();
            Array array1 = new Array();
            HashMap hashmap = new HashMap();
            for (int i = 0; i < array.size(); i ++) {
                String s = (String) array.get(i);
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup) siteviewgroup.getElement(s);
                if (monitorgroup != null) {
                    String s3 = COM.dragonflow.Page.CGI.getGroupPath(monitorgroup, COM.dragonflow.Page.CGI.getGroupIDFull(s, siteviewgroup), false);
                    array1.add(s3);
                    hashmap.add(s3, s);
                }
            }

            Sorting.sort(array1, new LessString());
            for (int j = 0; j < array1.size(); j ++) {
                String s1 = (String) array1.get(j);
                String s2 = (String) hashmap.get(s1);
                vector.addElement(s2);
                vector.addElement(s1);
            }

        }
        return vector;
    }

    static {
        pLogin = new StringProperty("_login");
        pLogin.setDisplayText("Login name", "Enter the login name for this user, if empty, no login name is required");
        pLogin.setParameterOptions(true, 1, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "Enter the login password for this user. if empty, no password is required.  If using LDAP Authentication then this field is not used.");
        pPassword.setParameterOptions(true, 2, false);
        pPassword.isPassword = true;
        pLdapserver = new StringProperty("_ldapserver");
        pLdapserver.setDisplayText("LDAP service provider", "Enter the LDAP server to connect to (example: ldap://ldap.this-company.com:389) for authentication.");
        pLdapserver.setParameterOptions(true, 4, false);
        pSecurityprincipal = new StringProperty("_securityprincipal");
        pSecurityprincipal.setDisplayText("LDAP Security Principal", "If using LDAP Authentication then this field is the Security Principal of the type \" uid=testuser,ou=TEST,o=this-company.com\".");
        pSecurityprincipal.setParameterOptions(true, 5, false);
        pGroup = new ScalarProperty("_group");
        pGroup.setDisplayText("Groups", "Optional, restrict access to the selected groups - the default allows access to all groups.");
        pGroup.setParameterOptions(true, 6, false);
        pDisabled = new BooleanProperty("_disabled", "");
        pDisabled.setDisplayText("Disabled", "Check to disable logins for this user");
        pDisabled.setParameterOptions(true, 7, false);
        pRealName = new StringProperty("_realName");
        pRealName.setDisplayText("Title", "Optional, enter the title for this user.  The title will appear in the Name field of the User Preference table.  The default title is the login name.");
        pRealName.setParameterOptions(true, 8, false);
        pGroupEdit = new BooleanProperty("_groupEdit", "");
        pGroupEdit.setDisplayText("Edit Groups", "Add, rename, copy, or delete groups");
        pGroupEdit.setParameterOptions(true, 9, false);
        pMonitorEdit = new BooleanProperty("_monitorEdit", "");
        pMonitorEdit.setDisplayText("Edit Monitors", "Add, edit, or delete monitors");
        pMonitorEdit.setParameterOptions(true, 10, false);
        pAlertEdit = new BooleanProperty("_alertEdit", "");
        pAlertEdit.setDisplayText("Edit Alerts", "Add, edit, or delete alerts");
        pAlertEdit.setParameterOptions(true, 11, false);
        pReportEdit = new BooleanProperty("_reportEdit", "");
        pReportEdit.setDisplayText("Edit Reports", "Add, edit, or delete reports");
        pReportEdit.setParameterOptions(true, 12, false);
        pMultiEdit = new BooleanProperty("_multiEdit", "");
        pMultiEdit.setDisplayText("Edit Multi-view", "Add, edit or delete items on the Multi-View page");
        pMultiEdit.setParameterOptions(true, 13, false);
        pPreference = new BooleanProperty("_preference", "");
        pPreference.setDisplayText("Edit Preferences", "Use any of the Preference forms");
        pPreference.setParameterOptions(true, 14, false);
        pReportAdhoc = new BooleanProperty("_reportAdhoc", "");
        pReportAdhoc.setDisplayText("Create Adhoc Reports", "Create adhoc reports");
        pReportAdhoc.setParameterOptions(true, 15, false);
        pAlertAdhocReport = new BooleanProperty("_alertAdhocReport", "");
        pAlertAdhocReport.setDisplayText("Create Adhoc Alert Reports", "Create adhoc alert reports");
        pAlertAdhocReport.setParameterOptions(true, 16, false);
        pAlertTest = new BooleanProperty("_alertTest", "");
        pAlertTest.setDisplayText("Test Alerts", "Test an alert");
        pAlertTest.setParameterOptions(true, 17, false);
        pPreferenceTest = new BooleanProperty("_preferenceTest", "");
        pPreferenceTest.setDisplayText("Test Preferences", "Test a preference setting");
        pPreferenceTest.setParameterOptions(true, 18, false);
        pGroupDisable = new BooleanProperty("_groupDisable", "");
        pGroupDisable.setDisplayText("Disable Groups", "Disable or enable groups");
        pGroupDisable.setParameterOptions(true, 19, false);
        pMonitorDisable = new BooleanProperty("_monitorDisable", "");
        pMonitorDisable.setDisplayText("Disable Monitors", "Disable or enable monitors");
        pMonitorDisable.setParameterOptions(true, 20, false);
        pAlertDisable = new BooleanProperty("_alertDisable", "");
        pAlertDisable.setDisplayText("Disable Alerts", "Disable or enable alerts");
        pAlertDisable.setParameterOptions(true, 21, false);
        pReportDisable = new BooleanProperty("_reportDisable", "");
        pReportDisable.setDisplayText("Disable Reports", "Disable or enable reports");
        pReportDisable.setParameterOptions(true, 22, false);
        pGroupRefresh = new BooleanProperty("_groupRefresh", "");
        pGroupRefresh.setDisplayText("Refresh Groups", "Refresh groups");
        pGroupRefresh.setParameterOptions(true, 23, false);
        pMonitorRefresh = new BooleanProperty("_monitorRefresh", "");
        pMonitorRefresh.setDisplayText("Refresh Monitors", "Refresh monitors");
        pMonitorRefresh.setParameterOptions(true, 24, false);
        pMonitorAcknowledge = new BooleanProperty("_monitorAcknowledge", "");
        pMonitorAcknowledge.setDisplayText("Acknowledge Monitors", "Acknowledge monitors");
        pMonitorAcknowledge.setParameterOptions(true, 25, false);
        pMonitorTools = new BooleanProperty("_monitorTools", "");
        pMonitorTools.setDisplayText("Use Monitor Tools", "Use the Tools form for a monitor");
        pMonitorTools.setParameterOptions(true, 26, false);
        pTools = new BooleanProperty("_tools", "");
        pTools.setDisplayText("Use Tools", "Use the generic Tools forms");
        pTools.setParameterOptions(true, 27, false);
        pBrowse = new BooleanProperty("_browse", "");
        pBrowse.setDisplayText("Use the Browse Tool", "Use the Browse Monitor form");
        pBrowse.setParameterOptions(true, 28, false);
        pSupport = new BooleanProperty("_support", "");
        pSupport.setDisplayText("Use the Support Tool", "Use the Send Support Request form");
        pSupport.setParameterOptions(true, 29, false);
        pMonitorRecent = new BooleanProperty("_monitorRecent", "");
        pMonitorRecent.setDisplayText("View Monitor History Report", "View the recent history report for a monitor");
        pMonitorRecent.setParameterOptions(true, 30, false);
        pAlertRecentReport = new BooleanProperty("_alertRecentReport", "");
        pAlertRecentReport.setDisplayText("View Alert History Report", "View the recent history report for an alert");
        pAlertRecentReport.setParameterOptions(true, 31, false);
        pProgress = new BooleanProperty("_progress", "");
        pProgress.setDisplayText("View Progress", "View the Progress page showing monitors that are running");
        pProgress.setParameterOptions(true, 32, false);
        pLogs = new BooleanProperty("_logs", "");
        pLogs.setDisplayText("View Logs", "View the raw data for monitors, alerts and other logs");
        pLogs.setParameterOptions(true, 33, false);
        pAlertList = new BooleanProperty("_alertList", "");
        pAlertList.setDisplayText("View Alerts List", "View the list of the alerts in the alert page");
        pAlertList.setParameterOptions(true, 34, false);
        pReportToolbar = new BooleanProperty("_reportToolbar", "");
        pReportToolbar.setDisplayText("Show Report Toolbar", "Include links at the top on reports");
        pReportToolbar.setParameterOptions(true, 35, false);
        pReportGenerate = new BooleanProperty("_reportGenerate", "");
        pReportGenerate.setDisplayText("Generate Reports", "Generate a scheduled report manually");
        pReportGenerate.setParameterOptions(true, 36, false);
        pHealthDisable = new BooleanProperty("_healthDisable", "");
        pHealthDisable.setDisplayText("Disable/Enable Application Health Monitors", "Enable/Disable SiteView Health monitoring");
        pHealthDisable.setParameterOptions(true, 37, false);
        pAlertTempDisable = new BooleanProperty("_alertTempDisable", "");
        pAlertTempDisable.setDisplayText("Disable Alerts Temporarily", "disable or enable alerts temporarily");
        pAlertTempDisable.setParameterOptions(true, 38, false);
        pHealthView = new BooleanProperty("_healthView", "");
        pHealthView.setDisplayText("View Health Page", "View the SiteView Health (self-monitoring) page");
        pHealthView.setParameterOptions(true, 39, false);
        pHealthEdit = new BooleanProperty("_healthEdit", "");
        pHealthEdit.setDisplayText("Edit Health Parameters", "Edit SiteView Health parameters");
        pHealthEdit.setParameterOptions(true, 40, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pLogin, pPassword, pLdapserver, pSecurityprincipal, pGroup, pDisabled, pRealName, pGroupEdit, pMonitorEdit, pAlertEdit, pReportEdit, pMultiEdit, pPreference, pReportAdhoc,
                pAlertAdhocReport, pAlertTest, pPreferenceTest, pGroupDisable, pMonitorDisable, pAlertDisable, pReportDisable, pGroupRefresh, pMonitorRefresh, pMonitorAcknowledge, pMonitorTools, pTools, pBrowse, pSupport, pMonitorRecent,
                pAlertRecentReport, pProgress, pLogs, pAlertList, pReportToolbar, pReportGenerate, pHealthDisable, pAlertTempDisable, pHealthView, pHealthEdit };
        COM.dragonflow.StandardPreference.UserInstancePreferences.addProperties("COM.dragonflow.StandardPreference.UserInstancePreferences", astringproperty);
    }
}
