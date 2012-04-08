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

import java.util.HashMap;
import java.util.Vector;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;

public class HighAvailabilityDefaultPreferences extends COM.dragonflow.SiteView.Preferences
{

    public static COM.dragonflow.Properties.StringProperty pServer;
    public static COM.dragonflow.Properties.StringProperty pIsSecure;
    public static COM.dragonflow.Properties.StringProperty pUsername;
    public static COM.dragonflow.Properties.StringProperty pPassword;
    public static COM.dragonflow.Properties.StringProperty pRunmirror;
    public static COM.dragonflow.Properties.StringProperty pWindow;
    public static COM.dragonflow.Properties.StringProperty pMirrorScheduleDay;
    public static COM.dragonflow.Properties.StringProperty pMirrorScheduleTime;
    public static COM.dragonflow.Properties.StringProperty pGlobalDependsOn;
    public static COM.dragonflow.Properties.StringProperty pGlobalDependsCondition;

    public HighAvailabilityDefaultPreferences()
    {
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1)
    {
        if(stringproperty == pPassword)
        {
            s = COM.dragonflow.Utils.TextUtils.obscure(s);
        } else
        if(stringproperty == pWindow)
        {
            checkIfValid((COM.dragonflow.Properties.ScalarProperty)stringproperty, s, httprequest, hashmap1);
        }
        return s;
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest)
        throws COM.dragonflow.SiteViewException.SiteViewException
    {
        java.util.Vector vector = new Vector();
        if(scalarproperty == pWindow)
        {
            vector.addElement("Daily");
            vector.addElement("Daily");
            vector.addElement("Weekly");
            vector.addElement("Weekly");
            vector.addElement("Hourly");
            vector.addElement("Hourly");
        } else
        if(scalarproperty != pGlobalDependsOn);
        return vector;
    }

    public static void main(String args[])
    {
        COM.dragonflow.StandardPreference.HighAvailabilityDefaultPreferences highavailabilitydefaultpreferences = new HighAvailabilityDefaultPreferences();
        java.util.HashMap hashmap = new HashMap();
        java.util.HashMap hashmap1 = new HashMap();
        COM.dragonflow.HTTP.HTTPRequest httprequest = new HTTPRequest();
        hashmap.put(pWindow, "no");
        highavailabilitydefaultpreferences.verify(pWindow, "Daily", httprequest, hashmap, hashmap1);
        java.lang.System.out.println("errors = " + hashmap1);
    }

    static 
    {
        pServer = new StringProperty("server");
        pServer.setDisplayText("Server", "Enter the server address and SiteView port of the server to mirror configurations from (for example, demo.siteview.com:8888).");
        pServer.setParameterOptions(true, 1, false);
        pIsSecure = new BooleanProperty("isSecure");
        pIsSecure.setDisplayText("Is SSL enabled", "Check this box if the server is a secured.");
        pIsSecure.setParameterOptions(true, 2, false);
        pUsername = new StringProperty("username");
        pUsername.setDisplayText("Admin Login", "Enter the administrator username for the primary SiteView installation.");
        pUsername.setParameterOptions(true, 3, false);
        pPassword = new StringProperty("password");
        pPassword.setDisplayText("Admin Password", "Enter the administrator password for the primary SiteView installation.");
        pPassword.setParameterOptions(true, 4, false);
        pPassword.isPassword = true;
        pRunmirror = new BooleanProperty("runmirror");
        pRunmirror.setDisplayText("Run mirror sync", "Check this box to create a mirror when these preferences are saved.");
        pRunmirror.setParameterOptions(true, 5, false);
        pWindow = new ScalarProperty("window");
        pWindow.setDisplayText("Schedule", "Enter the Schedule for when mirroring will occur (Ex: Daily 23:50 or Hourly 1). For Weekly schedules, it will occur on Sunday at the time entered.");
        pWindow.setParameterOptions(true, 6, false);
        pMirrorScheduleDay = new StringProperty("mirrorScheduleDay", "m,t,w,r,f,s,u");
        pMirrorScheduleDay.setDisplayText("", "");
        pMirrorScheduleDay.setParameterOptions(true, 7, false);
        pMirrorScheduleTime = new StringProperty("mirrorScheduleTime");
        pMirrorScheduleTime.setDisplayText("Schedule", "");
        pMirrorScheduleTime.setParameterOptions(true, 8, false);
        pGlobalDependsOn = new ScalarProperty("_globalDependsOn");
        pGlobalDependsOn.setDisplayText("Failover Monitor", "Select the failover monitor for the primary SiteView. If the failover monitor goes into error, then the monitoring on this SiteView will be enabled - when the failover monitor goes back to OK, then monitoring on this SiteView will be disabled. Note: the group that this monitor is in will not be overwritten by the mirror from the primary SiteView.");
        pGlobalDependsOn.setParameterOptions(true, 9, false);
        pGlobalDependsCondition = new StringProperty("_globalDependsCondition", "error");
        pGlobalDependsCondition.setDisplayText("", "");
        pGlobalDependsCondition.setParameterOptions(true, 10, false);
    }
}
