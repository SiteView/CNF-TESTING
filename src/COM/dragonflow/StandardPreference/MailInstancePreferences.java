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

import jgl.Array;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Properties.StringProperty;

public class MailInstancePreferences extends COM.dragonflow.SiteView.Preferences {

    private String settingName;

    public static COM.dragonflow.Properties.StringProperty pId;

    public static COM.dragonflow.Properties.StringProperty pName;

    public static COM.dragonflow.Properties.StringProperty pEmail;

    public static COM.dragonflow.Properties.StringProperty pDisabled;

    public static COM.dragonflow.Properties.StringProperty pTemplate;

    public static COM.dragonflow.Properties.StringProperty pSchedule;

    public MailInstancePreferences() {
        settingName = "_additionalMail";
    }

    public java.util.Vector test(String s) throws java.lang.Exception {
        java.util.Vector vector = new Vector();
        String s1 = COM.dragonflow.SiteView.Platform.productName + " test message";
        String s2 = "This is a test.  This is only a test.\n\n - " + COM.dragonflow.SiteView.Platform.productName;
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s5 = getValue("_mailAlertCC");
        String s6 = getProperty(pEmail);
        jgl.Array array = new Array();
        String s7 = COM.dragonflow.Utils.MailUtils.mail(hashmap, s6, s1, s2, s5, array);
        if (s7.length() == 0) {
            String s3 = "Mail Test Completed! The test message was sent to " + s6;
            vector.add(s3);
        } else {
            String s4 = "Mail Test Completed! The test message to " + s6 + " could not be sent." + " The error was: " + s7;
            throw new Exception(s4);
        }
        String s8;
        for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); vector.add(COM.dragonflow.Utils.TextUtils.escapeHTML(s8))) {
            s8 = (String) enumeration.nextElement();
        }

        return vector;
    }

    public boolean hasMultipleValues() {
        return true;
    }

    public String getSettingName() {
        return settingName;
    }

    public String getReturnName() {
        return pName.getName();
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1) {
        if (stringproperty.getName().equals("_name") && (s == null || s.length() == 0)) {
            s = (String) hashmap.get(pEmail.getName());
        } else if (stringproperty.getName().equals("_template")) {
            checkIfValid((COM.dragonflow.Properties.ScalarProperty) stringproperty, s, httprequest, hashmap1);
        } else if (stringproperty.getName().equals("_schedule")) {
            if (s.startsWith("_id=")) {
                String s1 = s.substring(4);
                java.util.Vector vector1 = null;
                try {
                    vector1 = getPreferenceProperties("ScheduleInstancePreferences", "_additionalSchedule", "_id", s1, COM.dragonflow.Api.APISiteView.FILTER_ALL);
                } catch (java.lang.Exception exception) {
                    java.lang.System.out.println("There was a problem with retrieving additional schedule string." + exception.toString());
                }
                if (vector1 != null) {
                    java.util.HashMap hashmap2 = (java.util.HashMap) vector1.get(0);
                    s = (String) hashmap2.get("_schedule");
                }
            } else if (!s.equals("")) {
                java.util.Vector vector = getSettings("_additionalSchedule");
                if (vector != null) {
                    for (java.util.Iterator iterator = vector.iterator(); iterator.hasNext();) {
                        java.util.HashMap hashmap3 = (java.util.HashMap) iterator.next();
                        java.util.Set set = hashmap3.keySet();
                        java.util.Iterator iterator1 = set.iterator();
                        while (iterator1.hasNext()) {
                            String s2 = (String) iterator1.next();
                            String s3 = (String) hashmap3.get(s2);
                            if (s2.indexOf("_schedule") != -1 && s3 != null && s3.indexOf(s) != -1) {
                                s = "_id=" + (String) hashmap3.get("_id");
                            }
                        }
                    }

                }
            }
        }
        return s;
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest) throws COM.dragonflow.SiteViewException.SiteViewException {
        java.util.Vector vector = new Vector();
        if (scalarproperty == pTemplate) {
            java.util.Vector vector1 = COM.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.mail", httprequest);
            vector.addElement("");
            vector.addElement("use template from alert");
            for (int i = 0; i < vector1.size(); i ++) {
                vector.addElement(vector1.elementAt(i));
                vector.addElement(vector1.elementAt(i));
            }

        }
        return vector;
    }

    static {
        pId = new StringProperty("_id");
        pId.setDisplayText("Id", "id associated with the mail preference");
        pName = new StringProperty("_name");
        pName.setDisplayText("Setting Name", "Enter the name of the e-mail settings used to specify e-mail targets when adding alerts.");
        pName.setParameterOptions(true, 1, false);
        pEmail = new StringProperty("_email");
        pEmail.setDisplayText("E-mail To", "Enter the e-mail addresses that alerts will be sent to when using the e-mail setting. Separate multiple addresses with commas (example: sysadmin@this-company.com, sales@this-company.com).");
        pEmail.setParameterOptions(true, 2, false);
        pDisabled = new BooleanProperty("_disabled", "");
        pDisabled.setDisplayText("Disabled", "Disable the e-mail settings to prevent alert e-mail messages from being sent via that e-mail setting.");
        pDisabled.setParameterOptions(true, 3, false);
        pTemplate = new ScalarProperty("_template");
        pTemplate.setDisplayText("Template", "Optional template to use with this e-mail settings - if a template is selected, this template will be used for sending the alert to this e-mail setting.");
        pTemplate.setParameterOptions(true, 4, false);
        pSchedule = new ScheduleProperty("_schedule", "");
        pSchedule.setDisplayText("Schedule", "Optional schedule for these settings to be enabled - for example, enable Sunday from 10:00 to 22:00.");
        pSchedule.setParameterOptions(true, 5, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pId, pName, pEmail, pDisabled, pTemplate, pSchedule };
        COM.dragonflow.StandardPreference.MailInstancePreferences.addProperties("COM.dragonflow.StandardPreference.MailInstancePreferences", astringproperty);
    }
}
