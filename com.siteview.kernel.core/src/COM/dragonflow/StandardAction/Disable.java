/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardAction;

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

import jgl.HashMap;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;

public class Disable extends COM.dragonflow.SiteView.Action {

    static COM.dragonflow.Properties.StringProperty pAction;

    static COM.dragonflow.Properties.StringProperty pTarget;

    static COM.dragonflow.Properties.StringProperty pTargetMatch;

    public void initializeFromArguments(jgl.Array array, jgl.HashMap hashmap) {
        if (array.size() > 0) {
            setProperty(pAction, array.at(0));
        }
        if (array.size() > 1) {
            for (int i = 1; i < array.size(); i ++) {
                addProperty(pTarget, (String) array.at(i));
            }

        }
        setProperty(pTargetMatch, COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_targetMatch").replace('_', ' '));
    }

    public String getActionString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("disable");
        stringbuffer.append(" ");
        stringbuffer.append(getProperty(pAction));
        if (getProperty(pTargetMatch).length() > 0) {
            stringbuffer.append(" _targetMatch=");
            stringbuffer.append(getProperty(pTargetMatch).replace(' ', '_'));
        }
        String s;
        for (Enumeration enumeration = getMultipleValues(pTarget); enumeration.hasMoreElements(); stringbuffer.append(s)) {
            s = (String) enumeration.nextElement();
            stringbuffer.append(" ");
        }

        return stringbuffer.toString();
    }

    public String getActionDescription() {
        StringBuffer stringbuffer = new StringBuffer();
        if (getProperty(pAction).startsWith("enable")) {
            stringbuffer.append("Enable ");
        } else {
            stringbuffer.append("Disable ");
        }
        if (getProperty(pTargetMatch).length() > 0) {
            stringbuffer.append(" names matching \"");
            stringbuffer.append(getProperty(pTargetMatch));
            stringbuffer.append("\" in ");
        }
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        Enumeration enumeration = getMultipleValues(pTarget);
        for (String s = ""; enumeration.hasMoreElements(); s = ", ") {
            String s1 = (String) enumeration.nextElement();
            if (s1.equals("_groupWithMonitorName_")) {
                stringbuffer.append("group with file name matching the triggering monitor's name");
                continue;
            }
            if (s1.equals("_groupContainingMonitor_")) {
                stringbuffer.append("the group containing the triggering monitor");
                continue;
            }
            COM.dragonflow.SiteView.Monitor monitor = (COM.dragonflow.SiteView.Monitor) siteviewgroup.getElement(s1);
            if (monitor == null) {
                continue;
            }
            stringbuffer.append(s);
            stringbuffer.append(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName));
            if (s1.indexOf("/") == -1) {
                stringbuffer.append(" Group");
            }
        }

        return stringbuffer.toString();
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == pTargetMatch) {
            String s1 = COM.dragonflow.Utils.TextUtils.legalMatchString(s);
            if (s1.length() > 0) {
                hashmap.put(stringproperty, s1);
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public java.util.Vector getScalarValues(COM.dragonflow.Properties.ScalarProperty scalarproperty, COM.dragonflow.HTTP.HTTPRequest httprequest, COM.dragonflow.Page.CGI cgi) throws COM.dragonflow.SiteViewException.SiteViewException {
        if (scalarproperty == pAction) {
            java.util.Vector vector = new Vector();
            vector.addElement("disable");
            vector.addElement("Disable");
            vector.addElement("enable");
            vector.addElement("Enable");
            return vector;
        }
        if (scalarproperty == pTarget) {
            java.util.Vector vector1 = new Vector();
            java.util.Vector vector2 = new Vector();
            jgl.Array array = COM.dragonflow.Page.CGI.getAllowedGroupIDsForAccount(httprequest);
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                jgl.Array array1 = null;
                try {
                    array1 = COM.dragonflow.Page.CGI.ReadGroupFrames(s, null);
                } catch (java.io.IOException ioexception) {
                    continue;
                }
                Enumeration enumeration2 = COM.dragonflow.Page.CGI.getMonitors(array1);
                jgl.HashMap hashmap = (jgl.HashMap) enumeration2.nextElement();
                String s1 = COM.dragonflow.Page.CGI.getGroupName(hashmap, s);
                vector1.addElement(s);
                vector1.addElement(s1);
                while (enumeration2.hasMoreElements()) {
                    jgl.HashMap hashmap1 = (jgl.HashMap) enumeration2.nextElement();
                    String s2 = s + "/" + hashmap1.get("_id");
                    vector2.addElement(s2);
                    vector2.addElement(s1 + ": " + COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_name"));
                }
            } 
            for (Enumeration enumeration1 = vector2.elements(); enumeration1.hasMoreElements(); vector1.addElement(enumeration1.nextElement())) {
            }
            vector1.addElement("_groupWithMonitorName_");
            vector1.addElement("group with file name matching the triggering monitor's name");
            vector1.addElement("_groupContainingMonitor_");
            vector1.addElement("the group containing the triggering monitor");
            return vector1;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public Disable() {
        runType = 2;
    }

    /**
     * 
     */
    public boolean execute() {
        String s = "true";
        String s1 = "Disabled ";
        if (args.length > 0 && args[0].startsWith("enable")) {
            s = "";
            String s2 = "Enabled ";
        }
        String s3 = getProperty("_targetMatch").replace('_', ' ');
        jgl.HashMap hashmap = new HashMap();
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        for (int i = 1; i < args.length; i ++) {
            String s4 = args[i];
            String s7 = s4;
            String s8 = "";
            if (s4.equals("_groupWithMonitorName_")) {
                s7 = COM.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName));
            } else if (s4.equals("_groupContainingMonitor_")) {
                s7 = COM.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(COM.dragonflow.SiteView.Monitor.pGroupID));
            } else {
                int j = s4.indexOf("/");
                if (j >= 0) {
                    s7 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s4.substring(0, j));
                    s8 = s4.substring(j + 1);
                }
            }
            try {
                jgl.Array array = (jgl.Array) hashmap.get(s7);
                if (array == null) {
                    array = COM.dragonflow.Page.CGI.ReadGroupFrames(s7, null);
                }
                if (array != null) {
                    hashmap.put(s7, array);
                    if (array.size() <= 0) {
                        continue;
                    }
                    String s11 = COM.dragonflow.Page.CGI.getGroupName((jgl.HashMap) array.at(0), s7);
                    Enumeration enumeration1 = COM.dragonflow.Page.CGI.getMonitors(array);
                    if (enumeration1.hasMoreElements()) {
                        enumeration1.nextElement();
                    }
                        jgl.HashMap hashmap1;
                        String s13;
                        while (enumeration1.hasMoreElements()) {
                            hashmap1 = (jgl.HashMap) enumeration1.nextElement();
                            s13 = "";
                        if (s8.length() != 0 && !COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_id").equals(s8)) {
                        if (s3.length() > 0) {
                            s13 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, COM.dragonflow.SiteView.Monitor.pName.getName());
                            if (!COM.dragonflow.Utils.TextUtils.match(s13, s3)) {
                                continue;
                            }
                        }
                        hashmap1.put(COM.dragonflow.SiteView.Monitor.pDisabled.getName(), s);
                        COM.dragonflow.SiteView.Platform.sleep(1000L);
                        if (s13.length() > 0) {
                            addToBuffer(stringbuffer, s13);
                        } else if (s11.length() > 0) {
                            addToBuffer(stringbuffer, s11);
                        }
                        if (s8.length() != 0) {
                            break;
                        }
                        s11 = "";
                        }
                    } 
                }
                else {
                addToBuffer(stringbuffer1, "could not read group " + s7);
                }
            } catch (java.lang.Exception exception1) {
                addToBuffer(stringbuffer1, "error " + exception1.getMessage());
            }
        }

        Enumeration enumeration = hashmap.keys();
        String s5 = "";
        String s6;
        for (; enumeration.hasMoreElements(); COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s6)) {
            s6 = (String) enumeration.nextElement();
            try {
                COM.dragonflow.Page.CGI.WriteGroupFrames(s6, (jgl.Array) hashmap.get(s6), null);
            } catch (java.lang.Exception exception) {
                addToBuffer(stringbuffer1, "error writing group file " + s6 + "  " + exception.getMessage());
            }
        }

        COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
        boolean flag = stringbuffer1.length() == 0;
        String s9 = stringbuffer.toString();
        String s10 = "Disable";
        if (s.length() == 0) {
            s10 = "Enable";
        }
        String s12 = s10 + " alert performed";
        if (!flag) {
            s12 = s10.toUpperCase() + " ALERT NOT PERFORMED";
            s9 = stringbuffer1.toString();
        }
        messageBuffer.append(s12 + ", " + s9);
        logAlert(baseAlertLogEntry(s12, s9, flag));
        return flag;
    }

    void addToBuffer(StringBuffer stringbuffer, String s) {
        if (stringbuffer.length() > 0) {
            stringbuffer.append(", ");
        }
        stringbuffer.append(s);
    }

    static {
        pAction = new ScalarProperty("_action", "disable");
        pAction.setDisplayText("Action", "disable or enable the target groups and monitors.");
        pAction.setParameterOptions(true, 1, false);
        pTarget = new ScalarProperty("_target", "");
        pTarget.setDisplayText("Target(s)", "the group(s) and/or monitor(s) to enable or disable - to select several items, hold down the Control key (on most platforms) while clicking item names.");
        pTarget.setParameterOptions(true, 2, false);
        ((COM.dragonflow.Properties.ScalarProperty) pTarget).multiple = true;
        ((COM.dragonflow.Properties.ScalarProperty) pTarget).listSize = 5;
        pTargetMatch = new StringProperty("_targetMatch", "");
        pTargetMatch.setDisplayText("Target Name Match", "optional text to match against the monitor names that will be disabled or enabled.");
        pTargetMatch.setParameterOptions(true, 3, true);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pAction, pTarget };
        COM.dragonflow.StandardAction.Disable.addProperties("COM.dragonflow.StandardAction.Disable", astringproperty);
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "description", "Disables or enables a set of groups or monitors.");
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "help", "AlertDisable.htm");
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "title", "Disable or Enable Monitor(s)");
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "label", "Disable or Enable Monitor(s)");
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "name", "Disable or Enable Monitor(s)");
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "class", "Disable");
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "loadable", "true");
        COM.dragonflow.StandardAction.Disable.setClassProperty("COM.dragonflow.StandardAction.Disable", "classType", "advanced");
    }
}
