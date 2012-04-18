/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.StandardAction;

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

import com.dragonflow.Properties.ScalarProperty;
import com.dragonflow.Properties.StringProperty;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class Disable extends com.dragonflow.SiteView.Action {

    static com.dragonflow.Properties.StringProperty pAction;

    static com.dragonflow.Properties.StringProperty pTarget;

    static com.dragonflow.Properties.StringProperty pTargetMatch;

    public void initializeFromArguments(Array array, HashMap hashmap) {
        if (array.size() > 0) {
            setProperty(pAction, array.get(0));
        }
        if (array.size() > 1) {
            for (int i = 1; i < array.size(); i ++) {
                addProperty(pTarget, (String) array.get(i));
            }

        }
        setProperty(pTargetMatch, com.dragonflow.Utils.TextUtils.getValue(hashmap, "_targetMatch").replace('_', ' '));
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
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
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
            com.dragonflow.SiteView.Monitor monitor = (com.dragonflow.SiteView.Monitor) siteviewgroup.getElement(s1);
            if (monitor == null) {
                continue;
            }
            stringbuffer.append(s);
            stringbuffer.append(monitor.getProperty(com.dragonflow.SiteView.Monitor.pName));
            if (s1.indexOf("/") == -1) {
                stringbuffer.append(" Group");
            }
        }

        return stringbuffer.toString();
    }

    public String verify(com.dragonflow.Properties.StringProperty stringproperty, String s, com.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pTargetMatch) {
            String s1 = com.dragonflow.Utils.TextUtils.legalMatchString(s);
            if (s1.length() > 0) {
                hashmap.put(stringproperty, s1);
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public java.util.Vector getScalarValues(com.dragonflow.Properties.ScalarProperty scalarproperty, com.dragonflow.HTTP.HTTPRequest httprequest, com.dragonflow.Page.CGI cgi) throws com.dragonflow.SiteViewException.SiteViewException {
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
            Array array = com.dragonflow.Page.CGI.getAllowedGroupIDsForAccount(httprequest);
            Enumeration enumeration = (Enumeration) array.iterator();
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                Array array1 = null;
                try {
                    array1 = com.dragonflow.Page.CGI.ReadGroupFrames(s, null);
                } catch (java.io.IOException ioexception) {
                    continue;
                }
                Enumeration enumeration2 = com.dragonflow.Page.CGI.getMonitors(array1);
                HashMap hashmap = (HashMap) enumeration2.nextElement();
                String s1 = com.dragonflow.Page.CGI.getGroupName(hashmap, s);
                vector1.addElement(s);
                vector1.addElement(s1);
                while (enumeration2.hasMoreElements()) {
                    HashMap hashmap1 = (HashMap) enumeration2.nextElement();
                    String s2 = s + "/" + hashmap1.get("_id");
                    vector2.addElement(s2);
                    vector2.addElement(s1 + ": " + com.dragonflow.Utils.TextUtils.getValue(hashmap1, "_name"));
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
        HashMap hashmap = new HashMap();
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        for (int i = 1; i < args.length; i ++) {
            String s4 = args[i];
            String s7 = s4;
            String s8 = "";
            if (s4.equals("_groupWithMonitorName_")) {
                s7 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(com.dragonflow.SiteView.Monitor.pName));
            } else if (s4.equals("_groupContainingMonitor_")) {
                s7 = com.dragonflow.Utils.I18N.toDefaultEncoding(monitor.getProperty(com.dragonflow.SiteView.Monitor.pGroupID));
            } else {
                int j = s4.indexOf("/");
                if (j >= 0) {
                    s7 = com.dragonflow.Utils.I18N.toDefaultEncoding(s4.substring(0, j));
                    s8 = s4.substring(j + 1);
                }
            }
            try {
                Array array = (Array) hashmap.get(s7);
                if (array == null) {
                    array = com.dragonflow.Page.CGI.ReadGroupFrames(s7, null);
                }
                if (array != null) {
                    hashmap.put(s7, array);
                    if (array.size() <= 0) {
                        continue;
                    }
                    String s11 = com.dragonflow.Page.CGI.getGroupName((HashMap) array.get(0), s7);
                    Enumeration enumeration1 = com.dragonflow.Page.CGI.getMonitors(array);
                    if (enumeration1.hasMoreElements()) {
                        enumeration1.nextElement();
                    }
                        HashMap hashmap1;
                        String s13;
                        while (enumeration1.hasMoreElements()) {
                            hashmap1 = (HashMap) enumeration1.nextElement();
                            s13 = "";
                        if (s8.length() != 0 && !com.dragonflow.Utils.TextUtils.getValue(hashmap1, "_id").equals(s8)) {
                        if (s3.length() > 0) {
                            s13 = com.dragonflow.Utils.TextUtils.getValue(hashmap1, com.dragonflow.SiteView.Monitor.pName.getName());
                            if (!com.dragonflow.Utils.TextUtils.match(s13, s3)) {
                                continue;
                            }
                        }
                        hashmap1.put(com.dragonflow.SiteView.Monitor.pDisabled.getName(), s);
                        com.dragonflow.SiteView.Platform.sleep(1000L);
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

        Enumeration enumeration = (Enumeration) hashmap.keys();
        String s5 = "";
        String s6;
        for (; enumeration.hasMoreElements(); com.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s6)) {
            s6 = (String) enumeration.nextElement();
            try {
                com.dragonflow.Page.CGI.WriteGroupFrames(s6, (Array) hashmap.get(s6), null);
            } catch (java.lang.Exception exception) {
                addToBuffer(stringbuffer1, "error writing group file " + s6 + "  " + exception.getMessage());
            }
        }

        com.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
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
        ((com.dragonflow.Properties.ScalarProperty) pTarget).multiple = true;
        ((com.dragonflow.Properties.ScalarProperty) pTarget).listSize = 5;
        pTargetMatch = new StringProperty("_targetMatch", "");
        pTargetMatch.setDisplayText("Target Name Match", "optional text to match against the monitor names that will be disabled or enabled.");
        pTargetMatch.setParameterOptions(true, 3, true);
        com.dragonflow.Properties.StringProperty astringproperty[] = { pAction, pTarget };
        com.dragonflow.StandardAction.Disable.addProperties("com.dragonflow.StandardAction.Disable", astringproperty);
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "description", "Disables or enables a set of groups or monitors.");
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "help", "AlertDisable.htm");
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "title", "Disable or Enable Monitor(s)");
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "label", "Disable or Enable Monitor(s)");
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "name", "Disable or Enable Monitor(s)");
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "class", "Disable");
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "loadable", "true");
        com.dragonflow.StandardAction.Disable.setClassProperty("com.dragonflow.StandardAction.Disable", "classType", "advanced");
    }
}
