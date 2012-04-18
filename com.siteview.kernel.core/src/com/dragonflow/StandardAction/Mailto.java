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

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

public class Mailto extends com.dragonflow.SiteView.Action {

    public static com.dragonflow.Properties.StringProperty pTo;

    public static com.dragonflow.Properties.StringProperty pTemplate;

    static String ID_STRING = "_id:";

    public void initializeFromArguments(Array array, HashMap hashmap) {
        hashmap.remove(pTo);
        for (int i = 0; i < array.size() - 1; i ++) {
            addProperty(pTo, (String) array.get(i));
        }

        String s = "Default";
        if (array.size() > 0) {
            s = (String) array.get(array.size() - 1);
        }
        setProperty(pTemplate, s);
    }

    public String getActionString() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("mailto");
        Enumeration enumeration = getMultipleValues(pTo);
        if (enumeration.hasMoreElements()) {
            for (; enumeration.hasMoreElements(); stringbuffer.append(enumeration.nextElement())) {
                stringbuffer.append(" ");
            }

            if (getProperty(pTemplate).length() > 0) {
                stringbuffer.append(" ");
                stringbuffer.append(getProperty(pTemplate));
            }
        }
        return stringbuffer.toString();
    }

    public String getActionDescription() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((String) getClassProperty("label"));
        String s = getProperty(pTemplate);
        if (s.length() > 0 && !s.equals("Default")) {
            stringbuffer.append(" " + s);
        }
        Enumeration enumeration = getMultipleValues(pTo);
        while (enumeration.hasMoreElements()) {
            String s1 = (String) enumeration.nextElement();
            if (s1.startsWith(ID_STRING)) {
                String s2 = s1.substring(ID_STRING.length());
                s1 = getSettingName(s2);
            }
            if (s1.length() > 0) {
                stringbuffer.append(" " + s1 + ",");
            }
        } 
        if (stringbuffer.charAt(stringbuffer.length() - 1) == ',') {
            stringbuffer.setLength(stringbuffer.length() - 1);
        }
        return stringbuffer.toString();
    }

    public String getSettingName(String s) {
        String s1 = getOwner().getMailSettings(s);
        if (s1.length() > 0) {
            String s2 = "_name=";
            int i = s1.indexOf(s2);
            if (i >= 0) {
                int j = s1.indexOf(" ", i);
                if (j == -1) {
                    j = s1.length();
                }
                String s3 = s1.substring(i + s2.length(), j);
                return com.dragonflow.Utils.TextUtils.storedValueToValue(s3);
            }
        }
        return "";
    }

    public boolean defaultsAreSet(HashMap hashmap) {
        String s = (String) hashmap.get("_mailServer");
        return s != null && s.length() != 0 && !s.equals("mail.dragonflow.com") && !s.equals("gateway.dragonflow.com") && !s.equals("gateway.Dragonflow.com") || hashmap.get("_account") != null;
    }

    public String verify(com.dragonflow.Properties.StringProperty stringproperty, String s, com.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pTo) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            if (!s.startsWith(ID_STRING)) {
                s = com.dragonflow.Utils.TextUtils.toEmailList(s);
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public java.util.Vector getScalarValues(com.dragonflow.Properties.ScalarProperty scalarproperty, com.dragonflow.HTTP.HTTPRequest httprequest, com.dragonflow.Page.CGI cgi) throws com.dragonflow.SiteViewException.SiteViewException {
        if (scalarproperty == pTemplate) {
            java.util.Vector vector = com.dragonflow.SiteView.SiteViewGroup.getTemplateList("templates.mail", httprequest);
            java.util.Vector vector1 = new Vector();
            for (int i = 0; i < vector.size(); i ++) {
                vector1.addElement(com.dragonflow.Utils.I18N.toNullEncoding((String) vector.elementAt(i)));
                vector1.addElement(com.dragonflow.Utils.I18N.toNullEncoding((String) vector.elementAt(i)));
            }

            return vector1;
        }
        if (scalarproperty == pTo) {
            Enumeration enumeration = null;
            if (httprequest.isStandardAccount()) {
                HashMap hashmap = cgi.getMasterConfig();
                enumeration = (Enumeration) hashmap.values("_additionalMail");
            } else {
                com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                com.dragonflow.SiteView.SiteViewObject siteviewobject = siteviewgroup.getElement(httprequest.getAccount());
                enumeration = siteviewobject.getMultipleValues("_additionalMail");
            }
            java.util.Vector vector2 = new Vector();
            HashMap hashmap1;
            for (; enumeration.hasMoreElements(); vector2.addElement(com.dragonflow.Utils.TextUtils.getValue(hashmap1, "_name"))) {
                hashmap1 = com.dragonflow.Utils.TextUtils.stringToHashMap((String) enumeration.nextElement());
                vector2.addElement(ID_STRING + com.dragonflow.Utils.TextUtils.getValue(hashmap1, "_id"));
            }

            return vector2;
        } else {
            return super.getScalarValues(scalarproperty, httprequest, cgi);
        }
    }

    public Mailto() {
        runType = 2;
    }

    public boolean execute() {
        boolean flag = false;
        String s = "unknown";
        String s2 = com.dragonflow.SiteView.Platform.productName + " Alert, " + monitor.getProperty(com.dragonflow.SiteView.Monitor.pCategory) + ", " + monitor.getProperty(com.dragonflow.SiteView.Monitor.pName) + ", "
                + monitor.getProperty(com.dragonflow.SiteView.Monitor.pStateString);
        String s3 = "";
        String s4 = "Default";
        if (args.length > 0 && args[args.length - 1] != null && args[args.length - 1].length() != 0) {
            s4 = com.dragonflow.Utils.I18N.toDefaultEncoding(args[args.length - 1]);
        }
        String as[] = getMailtoArgs();
        for (int i = 0; i < as.length - 1; i ++) {
            String s5 = as[i];
            if (s5.startsWith(ID_STRING)) {
                String s6 = as[i].substring(ID_STRING.length());
                HashMap hashmap = com.dragonflow.Utils.TextUtils.stringToHashMap(getOwner().getMailSettings(s6));
                if (com.dragonflow.Utils.TextUtils.getValue(hashmap, "_disabled").length() > 0) {
                    continue;
                }
                String s7 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_schedule");
                if (s7.length() > 0 && !com.dragonflow.Properties.ScheduleProperty.isEnabled(s7)) {
                    continue;
                }
                s5 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_email");
                s5.trim();
                if (s5.length() == 0) {
                    continue;
                }
                String s8 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_template");
                if (s8.length() > 0 && !s8.equals(s4)) {
                    flag |= _sendMailAlert(s5, s2, com.dragonflow.Utils.I18N.toDefaultEncoding(s8));
                    continue;
                }
            }
            s3 = s3 + "," + s5;
        }

        if (s3.length() > 0) {
            s3 = s3.substring(1);
        }
        if (s3.length() == 0) {
            String s1;
            if (!flag) {
                s1 = "Alert was not sent. E-mail address(es) missing or invalid";
            }
            return flag;
        } else {
            flag |= _sendMailAlert(s3, s2, s4);
            return flag;
        }
    }

    private String[] getMailtoArgs() {
        boolean flag = false;
        Array array = new Array();
        for (int i = 0; i < args.length - 1; i ++) {
            String s = args[i];
            String as[] = com.dragonflow.Utils.TextUtils.split(s, ",");
            for (int k = 0; k < as.length; k ++) {
                String s1 = as[k];
                if (s1.equals("ALL")) {
                    flag = true;
                } else {
                    array.add(s1);
                }
            }

        }

        if (!flag) {
            return args;
        }
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        int j = hashmap.count("_additionalMail");
        String as1[] = new String[j + array.size() + 1];
        Enumeration enumeration = (Enumeration) hashmap.values("_additionalMail");
        int l = 0;
        while (enumeration.hasMoreElements()) {
            HashMap hashmap1 = com.dragonflow.Utils.TextUtils.stringToHashMap((String) enumeration.nextElement());
            as1[l ++] = ID_STRING + hashmap1.get("_id");
        }
        for (int i1 = 0; i1 < array.size(); i1 ++) {
            as1[l ++] = (String) array.get(i1);
        }

        as1[l] = args[args.length - 1];
        return as1;
    }

    private boolean _sendMailAlert(String s, String s1, String s2) {
        StringBuffer stringbuffer = new StringBuffer();
        String s3 = createMessage(stringbuffer, "templates.mail", s2);
        String s4 = "";
        boolean flag = false;
        if (s3.length() == 0) {
            s4 = stringbuffer.toString();
            String s5 = "[skipheaders]";
            boolean flag1 = true;
            int i = s4.indexOf(s5);
            if (i != -1) {
                s1 = s5;
                flag1 = false;
                s4 = s4.substring(i + s5.length() + 1);
            }
            s5 = "[skipfooters]";
            i = s4.indexOf(s5);
            if (i != -1) {
                flag1 = false;
                s4 = s4.substring(i + s5.length() + 1);
            }
            String s7 = "[To:";
            i = s4.indexOf(s7);
            if (i != -1) {
                int j = s4.indexOf("]", i);
                if (j != -1) {
                    s = s4.substring(i + s7.length(), j).trim();
                    s4 = s4.substring(j + 1);
                }
            }
            String s8 = "[Subject:";
            i = s4.indexOf(s8);
            if (i != -1) {
                int k = s4.indexOf("]", i);
                if (k != -1) {
                    s1 = s4.substring(i + s8.length(), k);
                    s4 = s4.substring(k + 1);
                }
            }
            String s9 = getSetting("_mailAlertCC");
            HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s10 = getSetting("_mailSubjectMax");
            if (s10.length() > 0) {
                hashmap.put("_mailSubjectMax", s10);
            }
            String s11 = getSetting("_hideServerInSubject");
            if (s11.length() > 0) {
                hashmap.put("_hideServerInSubject", s11);
            }
            String s12 = com.dragonflow.Utils.MailUtils.mail(hashmap, s, com.dragonflow.Utils.I18N.UnicodeToString(s1, com.dragonflow.Utils.I18N.nullEncoding()), com.dragonflow.Utils.I18N.toNullEncoding(s4), s9, null, flag1, getSetting("_account"));
            if (s12.length() == 0) {
                flag = true;
                s3 = s;
            } else {
                s3 = s + ", " + s12;
            }
        }
        String s6 = "Email alert sent";
        if (!flag) {
            s6 = "EMAIL ALERT NOT SENT";
        }
        messageBuffer.append(s6 + ", " + com.dragonflow.Utils.I18N.toDefaultEncoding(s3));
        logAlert(baseAlertLogEntry(s6, s3, flag) + " alert-to: " + s + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-subject: " + com.dragonflow.Utils.I18N.toDefaultEncoding(s1) + com.dragonflow.SiteView.Platform.FILE_NEWLINE + " alert-body: "
                + s4 + com.dragonflow.SiteView.Platform.FILE_NEWLINE, false);
        return flag;
    }

    static {
        pTo = new ScalarProperty("_to");
        pTo.setDisplayText("To", "either choose one or more e-mail setting(s), or enter the e-mail address of the person to send the mail to, separate multiple addresses with commas (example: " + com.dragonflow.SiteView.Platform.supportEmail + ", "
                + com.dragonflow.SiteView.Platform.salesEmail + ")");
        pTo.setParameterOptions(true, 1, false);
        ((com.dragonflow.Properties.ScalarProperty) pTo).multiple = true;
        ((com.dragonflow.Properties.ScalarProperty) pTo).allowOther = true;
        ((com.dragonflow.Properties.ScalarProperty) pTo).otherSize = 40;
        ((com.dragonflow.Properties.ScalarProperty) pTo).defaultToOther = true;
        pTemplate = new ScalarProperty("_template", "Default");
        pTemplate.setDisplayText("Template", "choose which template to use for formatting the contents of the message.  If you are sending mail to a pager, choose one of the \"short\" templates.");
        pTemplate.setParameterOptions(true, 2, false);
        com.dragonflow.Properties.StringProperty astringproperty[] = { pTo, pTemplate };
        com.dragonflow.StandardAction.Mailto.addProperties("com.dragonflow.StandardAction.Mailto", astringproperty);
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "description", "Sends an alert message via e-mail.");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "help", "AlertMailto.htm");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "title", "Send Mail");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "label", "Send Mail to");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "name", "E-Mail");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "class", "Mailto");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "prefs", "mailPrefs");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "accountPrefs", "mailPrefs");
        com.dragonflow.StandardAction.Mailto.setClassProperty("com.dragonflow.StandardAction.Mailto", "loadable", "true");
    }
}
