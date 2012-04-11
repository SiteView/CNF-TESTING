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

import java.io.File;
import java.util.Enumeration;

import jgl.Array;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.CommandLine;

public class SMS extends COM.dragonflow.SiteView.Action {

    static COM.dragonflow.Properties.StringProperty pPhoneNumber;

    static String SMS_BAT_FILE = "SMS.bat";

    public SMS() {
        runType = 2;
    }

    public boolean execute() {
        return executeSync();
    }

    public String getActionString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("SMS");
        String s = getProperty(pPhoneNumber);
        if (s.length() > 0) {
            stringbuffer.append(" ");
            stringbuffer.append(s);
        }
        return stringbuffer.toString();
    }

    public void initializeFromArguments(jgl.Array array, jgl.HashMap hashmap) {
        if (array.size() > 0) {
            setProperty(pPhoneNumber, array.at(0));
        }
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == pPhoneNumber && s.length() <= 0) {
            hashmap.put(stringproperty, "Need a phone number");
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public String getActionDescription() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((String) getClassProperty("label"));
        String s = getProperty(pPhoneNumber);
        if (s.length() > 0) {
            stringbuffer.append(" \"" + s + "\"");
        }
        return stringbuffer.toString();
    }

    public boolean executeSync() {
        boolean flag = false;
        try {
            String s = "";
            long l = -1L;
            String s1 = "";
            String s3 = args[0];
            if (s.length() == 0) {
                String s4 = "";
                boolean flag1 = true;
                java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/tools/" + SMS_BAT_FILE);
                s4 = file.getAbsolutePath();
                flag1 = file.exists();
                if (flag1) {
                    COM.dragonflow.Utils.CommandLine commandline = new CommandLine();
                    String s8 = monitor.getProperty(COM.dragonflow.SiteView.Monitor.pName);
                    String s9 = monitor.getProperty(COM.dragonflow.SiteView.Monitor.pStateString);
                    s9 = COM.dragonflow.Utils.TextUtils.parseNonPrintableCharacters(s9);
                    String s10 = monitor.getOwner().getProperty(COM.dragonflow.SiteView.Monitor.pName);
                    if (COM.dragonflow.Utils.TextUtils.hasSpaces(s8)) {
                        s8 = "\"" + s8 + "\"";
                    }
                    if (COM.dragonflow.Utils.TextUtils.hasSpaces(s10)) {
                        s10 = "\"" + s10 + "\"";
                    }
                    s9 = "\"" + s9 + "\"";
                    boolean flag2 = true;
                    boolean flag3 = true;
                    jgl.Array array1 = new Array();
                    String s2 = COM.dragonflow.SiteView.Platform.getRoot() + "/tools/" + SMS_BAT_FILE;
                    if (flag3) {
                        s2 = s2 + " " + s3;
                        if (COM.dragonflow.Utils.TextUtils.hasChars(s8, "`;&|<>")) {
                            COM.dragonflow.Log.LogManager.log("Error", "Removed illegal characters from monitor name \"" + s8 + "\" before running script");
                            s8 = COM.dragonflow.Utils.TextUtils.removeChars(s8, "`;&|<>");
                        }
                        s2 = s2 + " " + s8;
                        s2 = s2 + " " + s9;
                    }
                    jgl.Array array = commandline.exec(s2);
                    l = commandline.getExitValue();
                    s = s2 + " (" + l + ")";
                    if (l == 0L) {
                        flag = true;
                    } else if (l == 4000L) {
                        monitor.signalMonitor();
                        s = s + ", re-running monitor";
                    }
                    for (Enumeration enumeration = array.elements(); enumeration.hasMoreElements();) {
                        s1 = s1 + (String) enumeration.nextElement() + COM.dragonflow.SiteView.Platform.FILE_NEWLINE;
                    }

                } else {
                    s = "file missing: " + s4;
                }
            }
            String s5 = "SMS alert performed";
            if (!flag) {
                s5 = "SMS ALERT ERROR RESULT";
                String s6 = getSetting("_autoEmail");
                String s7 = "There was a problem performing SMS alert." + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + baseAlertLogEntry(s5, s, flag) + " alert-result: " + l
                        + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-output: " + s1 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE
                        + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE;
                if (s6.length() != 0) {
                    COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                    siteviewgroup.simpleMail(s6, "SiteView " + s5, s7);
                }
            }
            messageBuffer.append(s5 + ", " + s);
            logAlert(baseAlertLogEntry(s5, s, flag) + " alert-result: " + l + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-output: " + s1 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE);
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }
        return flag;
    }

    static {
        pPhoneNumber = new StringProperty("_phoneNumber", "");
        pPhoneNumber.setDisplayText("SMS Number", "The phone number that will receive the sms alert.");
        pPhoneNumber.setParameterOptions(true, 1, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pPhoneNumber };
        COM.dragonflow.StandardAction.SMS.addProperties("COM.dragonflow.StandardAction.SMS", astringproperty);
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "description", "Send an alert using wireless Short Message Service.");
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "help", "AlertSMS.htm");
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "title", "Send SMS Message");
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "label", "SMS");
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "name", "SMS");
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "class", "SMS");
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "prefs", "");
        COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "classType", "advanced");
        if (COM.dragonflow.SiteView.Platform.isWindows()) {
            COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "loadable", "true");
        } else {
            COM.dragonflow.StandardAction.SMS.setClassProperty("COM.dragonflow.StandardAction.SMS", "loadable", "false");
        }
    }
}
