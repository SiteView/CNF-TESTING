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

import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewParameterException;

public class ScheduleInstancePreferences extends COM.dragonflow.SiteView.Preferences {

    public static final String dayLetters[] = { "U", "M", "T", "W", "R", "F", "S" };

    public static String ADDITINAL_SCHEDULE_SETTING_KEY = "_additionalSchedule";

    private String settingName;

    protected String days[] = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    public static COM.dragonflow.Properties.StringProperty pName;

    public static COM.dragonflow.Properties.StringProperty pSchedule;

    public ScheduleInstancePreferences() {
        settingName = ADDITINAL_SCHEDULE_SETTING_KEY;
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

    public String[] updatePreferences(java.util.HashMap hashmap, String s, String s1) throws COM.dragonflow.SiteViewException.SiteViewException {
        String as[] = super.updatePreferences(hashmap, s, s1);
        COM.dragonflow.SiteView.ScheduleManager schedulemanager = COM.dragonflow.SiteView.ScheduleManager.getInstance();
        String s2 = (String) hashmap.get("_schedule");
        schedulemanager.updateSchedule(as[1], s2);
        return as;
    }

    public void deletePreferences(String s, String s1) throws COM.dragonflow.SiteViewException.SiteViewException {
        COM.dragonflow.SiteView.ScheduleManager schedulemanager = COM.dragonflow.SiteView.ScheduleManager.getInstance();
        if (!schedulemanager.hasMonitorReferences(s1)) {
            super.deletePreferences(s, s1);
        } else {
            throw new SiteViewParameterException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_PARAM_API_PREFERENCE_MONITOR_REF_SCHEDULE);
        }
    }

    protected String appendSchedule(String s, String s1) {
        if (s != null) {
            if (s1.length() > 0 && s.length() > 0) {
                s1 = s1 + ",";
            }
            s1 = s1 + s;
        }
        return s1;
    }

    protected String getDayLetter(String s) {
        String s1 = "";
        if (s != null) {
            if (s.indexOf(days[0]) != -1) {
                return dayLetters[0];
            }
            if (s.indexOf(days[1]) != -1) {
                return dayLetters[1];
            }
            if (s.indexOf(days[2]) != -1) {
                return dayLetters[2];
            }
            if (s.indexOf(days[3]) != -1) {
                return dayLetters[3];
            }
            if (s.indexOf(days[4]) != -1) {
                return dayLetters[4];
            }
            if (s.indexOf(days[5]) != -1) {
                return dayLetters[5];
            }
            if (s.indexOf(days[6]) != -1) {
                return dayLetters[6];
            }
        }
        return s1;
    }

    static {
        pName = new StringProperty("_name");
        pName.setDisplayText("Setting Name", "Enter the name of the e-mail settings used to specify e-mail targets when adding alerts.");
        pName.setParameterOptions(true, 8, false);
        pSchedule = new ScheduleProperty("_schedule", "");
        pSchedule.setDisplayText("Schedule", "");
        pSchedule.setParameterOptions(true, 9, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pName, pSchedule };
        COM.dragonflow.StandardPreference.ScheduleInstancePreferences.addProperties("COM.dragonflow.StandardPreference.ScheduleInstancePreferences", astringproperty);
    }
}
