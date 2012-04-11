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

import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.ScheduleProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardAction.Page;

public class PagerInstancePreferences extends COM.dragonflow.SiteView.Preferences {

    private String settingName;

    public static COM.dragonflow.Properties.StringProperty pName;

    public static COM.dragonflow.Properties.StringProperty pDisabled;

    public static COM.dragonflow.Properties.StringProperty pPagerSpeed;

    public static COM.dragonflow.Properties.StringProperty pPagerType;

    public static COM.dragonflow.Properties.StringProperty pPagerAlphaPhone;

    public static COM.dragonflow.Properties.StringProperty pPagerAlphaPIN;

    public static COM.dragonflow.Properties.StringProperty pPagerDirectPhone;

    public static COM.dragonflow.Properties.StringProperty pPagerOptionPhone;

    public static COM.dragonflow.Properties.StringProperty pPagerOption;

    public static COM.dragonflow.Properties.StringProperty pPagerCustom;

    public static COM.dragonflow.Properties.StringProperty pSchedule;

    public PagerInstancePreferences() {
        settingName = "_additionalPager";
    }

    public java.util.Vector test(String s) throws java.lang.Exception {
        java.util.Vector vector = new Vector();
        vector.add("Sending test page...");
        COM.dragonflow.StandardAction.Page page = new Page();
        java.util.Vector vector1 = getPreferenceProperties("PagerDefaultPreferences", "", "", "", COM.dragonflow.Api.APISiteView.FILTER_ALL);
        java.util.HashMap hashmap = (java.util.HashMap) vector1.elementAt(0);
        if (getProperty("_pagerType").length() > 0) {
            page.setProperty("_pagerType", getProperty("_pagerType"));
            page.setProperty("_pagerAlphaPhone", getProperty("_pagerAlphaPhone"));
            page.setProperty("_pagerOptionPhone", getProperty("pagerOptionPhone"));
            page.setProperty("_pagerDirectPhone", getProperty("_pagerOptionPhone"));
            page.setProperty("_pagerAlphaPIN", getProperty("_pagerAlphaPIN"));
            page.setProperty("_pagerOption", getProperty("_pagerOption"));
            page.setProperty("_pagerTimeout", getProperty("_pagerTimeout"));
            page.setProperty("_pagerPort", (String) hashmap.get("_pagerPort"));
            page.setProperty("_pagerSpeed", getProperty("_pagerSpeed"));
            page.setProperty("_pagerHangupCmd", (String) hashmap.get("_pagerHangupCmd"));
            page.setProperty("_pagerInitOptions", (String) hashmap.get("_pagerInitOptions"));
            page.setProperty("_pagerTimeout", (String) hashmap.get("_pagerTimeout"));
            if (page.getProperty("_pagerType").equals("numeric")) {
                if (page.getProperty("_pagerOption").length() > 0) {
                    page.setProperty("_pagerType", "option");
                } else {
                    page.setProperty("_pagerType", "direct");
                }
            }
        }
        if (s.length() == 0) {
            s = "Test Message";
        }
        String s1 = page.getPagerCommand(s);
        vector.add(s1);
        jgl.Array array = page.pagerSend(s);
        String s2 = "";
        String s3 = "";
        if (array != null) {
            Enumeration enumeration = array.elements();
            while (enumeration.hasMoreElements()) {
                String s6 = (String) enumeration.nextElement();
                if (s6.indexOf("message rejected") >= 0) {
                    String s4 = "The message was rejected - this is usually because the PIN number is incorrect or expired. If you're using a 10 digit PIN, try using just the last 7 digits, and no punctuation. If this does not work, try using all 10 digits of the PIN. Less commonly, this error can occur if the message is too long for the paging service.";
                    vector.add(s6);
                }
            } 
            
            for (Enumeration enumeration1 = array.elements(); enumeration1.hasMoreElements();) {
                String s7 = (String) enumeration1.nextElement() + "\n";
                vector.add(s7);
                s2 = s7 + "\n";
            }

        }
        if (s2.length() == 0) {
            vector.add("The test message was sent to the pager");
        } else {
            String s5 = "The test message to the pager could not be sent. The error was: " + s2 + ". The pager command was: " + s1 + ". Suggestions: " + " Check the cable connection to the server serial port."
                    + " Check the cable connection to the modem." + " Check that the modem is powered on." + " Check that the modem is connected to a phone line." + " Listen to the modem.  You should hear a dial tone and then hear the modem dial."
                    + " Listen after the modem dials.  For alphanumeric messages, you should hear a modem answer (a screeching noise)" + " Look at the commands being sent and received.  If some characters"
                    + " are missing (for example, 'cnct' instead of 'connect'), try using a 14.4 or slower modem." + " Sometimes, there are flow control problems with higher speed modems.";
            throw new Exception(s5);
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
        if (stringproperty == pPagerType) {
            checkIfValid((COM.dragonflow.Properties.ScalarProperty) stringproperty, s, httprequest, hashmap1);
            if (s.equals("alpha")) {
                String s1 = (String) hashmap.get(pPagerAlphaPhone.getName());
                String s6 = (String) hashmap.get(pPagerAlphaPIN.getName());
                if (s1 == null || s1.length() <= 0) {
                    hashmap1.put(pPagerAlphaPhone, "Please enter a value for the " + pPagerAlphaPhone.getLabel());
                }
                if (s6 == null || s6.length() <= 0) {
                    hashmap1.put(pPagerAlphaPIN, "Please enter a value for the " + pPagerAlphaPIN.getLabel());
                }
            } else if (s.equals("direct")) {
                String s2 = (String) hashmap.get(pPagerDirectPhone.getName());
                if (s2 == null || s2.length() <= 0) {
                    hashmap1.put(pPagerDirectPhone, "Please enter a value for the " + pPagerDirectPhone.getLabel());
                }
            } else if (s.equals("option")) {
                String s3 = (String) hashmap.get(pPagerOptionPhone.getName());
                String s7 = (String) hashmap.get(pPagerOption.getName());
                if (s3 == null || s3.length() <= 0) {
                    hashmap1.put(pPagerOptionPhone, "Please enter a value for the " + pPagerOptionPhone.getLabel());
                }
                if (s7 == null || s7.length() <= 0) {
                    hashmap1.put(pPagerOption, "Please enter a value for the " + pPagerOption.getLabel());
                }
            } else if (s.equals("custom")) {
                String s4 = (String) hashmap.get(pPagerCustom.getName());
                if (s4 == null || s4.length() <= 0) {
                    hashmap1.put(pPagerCustom, "Please enter a value for the " + pPagerCustom.getLabel());
                }
            }
        } else if (stringproperty == pPagerSpeed) {
            checkIfValid((COM.dragonflow.Properties.ScalarProperty) stringproperty, s, httprequest, hashmap1);
        } else if (stringproperty == pName && (s == null || s.length() == 0)) {
            if (hashmap.get(pPagerType.getName()).equals("alpha")) {
                s = (String) hashmap.get(pPagerAlphaPhone.getName());
            } else if (hashmap.get(pPagerType.getName()).equals("direct")) {
                s = (String) hashmap.get(pPagerDirectPhone.getName());
            } else if (hashmap.get(pPagerType.getName()).equals("option")) {
                s = (String) hashmap.get(pPagerOptionPhone.getName());
            } else {
                s = (String) hashmap.get(pPagerCustom.getName());
            }
        } else if (stringproperty.getName().equals("_schedule")) {
            if (s.startsWith("_id=")) {
                String s5 = s.substring(4);
                java.util.Vector vector1 = null;
                try {
                    vector1 = getPreferenceProperties("ScheduleInstancePreferences", "_additionalSchedule", "_id", s5, COM.dragonflow.Api.APISiteView.FILTER_ALL);
                } catch (java.lang.Exception exception) {
                    java.lang.System.out.println("There was a problem with retrieving additional schedule string." + exception.toString());
                }
                if (vector1 != null) {
                    java.util.HashMap hashmap2 = (java.util.HashMap) vector1.get(0);
                    s = (String) hashmap2.get("_schedule");
                }
            } else {
                java.util.Vector vector = getSettings("_additionalSchedule");
                if (vector != null) {
                    for (java.util.Iterator iterator = vector.iterator(); iterator.hasNext();) {
                        java.util.HashMap hashmap3 = (java.util.HashMap) iterator.next();
                        java.util.Set set = hashmap3.keySet();
                        java.util.Iterator iterator1 = set.iterator();
                        while (iterator1.hasNext()) {
                            String s8 = (String) iterator1.next();
                            String s9 = (String) hashmap3.get(s8);
                            if (s8.indexOf("_schedule") != -1 && s9 != null && s9.indexOf(s) != -1) {
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
        if (scalarproperty == pPagerSpeed) {
            vector.addElement("300");
            vector.addElement("300");
            vector.addElement("1200");
            vector.addElement("1200");
            vector.addElement("2400");
            vector.addElement("2400");
            vector.addElement("9600");
            vector.addElement("9600");
        } else if (scalarproperty == pPagerType) {
            vector.addElement("alpha");
            vector.addElement("Preferred: Send Alphanumeric messages by connecting to Modem Number at paging service");
            vector.addElement("direct");
            vector.addElement("Dial phone number and enter message (numeric only)");
            vector.addElement("option");
            vector.addElement("Dial phone number, enter a command, and then enter message (numeric only)");
            vector.addElement("custom");
            vector.addElement("Customized modem command string (numeric only)");
        }
        return vector;
    }

    static {
        pName = new StringProperty("_name");
        pName.setDisplayText("Setting Name", "Enter the name of the pager settings used to specify pagers when adding alerts.");
        pName.setParameterOptions(true, 1, false);
        pDisabled = new BooleanProperty("_disabled", "");
        pDisabled.setDisplayText("Disabled", "Disable the pager settings to prevent alert pages from being sent via that pager setting.");
        pDisabled.setParameterOptions(true, 2, false);
        pPagerSpeed = new ScalarProperty("_pagerSpeed", "");
        pPagerSpeed.setDisplayText("Modem speed", "Most paging companies use 1200 baud - you only need to change the modem speed if your paging company requires it.");
        pPagerSpeed.setParameterOptions(true, 3, false);
        pPagerType = new ScalarProperty("_pagerType");
        pPagerType
                .setDisplayText(
                        "Different methods for sending a message to your paging service.",
                        "The Preferred method is to connect to a modem at your pager service. When a modem connection is used, SiteView is able to verify that the message was sent successfully. And, the messages can contain a description of the problem.The other options allow numeric messages to be sent using touch tone dialing. Touch tone dialing is limited to numeric messages. It also cannot confirm that your paging service correctly received the message.");
        pPagerType.setParameterOptions(true, 4, false);
        pPagerAlphaPhone = new StringProperty("_pagerAlphaPhone");
        pPagerAlphaPhone.setDisplayText("Modem number",
                "Contact your paging service to find out the Modem Number for your pager. This is also sometimes called the TAP/IXO number. (If you call this number, you should hear a modem answer it with a lovely screeching sound).");
        pPagerAlphaPhone.setParameterOptions(true, 5, false);
        pPagerAlphaPIN = new StringProperty("_pagerAlphaPIN");
        pPagerAlphaPIN.setDisplayText("PIN number", "Enter the last seven digits of your pager's PIN number.");
        pPagerAlphaPIN.setParameterOptions(true, 6, false);
        pPagerDirectPhone = new StringProperty("_pagerDirectPhone");
        pPagerDirectPhone.setDisplayText("Phone number", "Use this option if you use a direct phone number to send a page. Most local paging companies work like this.");
        pPagerDirectPhone.setParameterOptions(true, 7, false);
        pPagerOptionPhone = new StringProperty("_pagerOptionPhone");
        pPagerOptionPhone.setDisplayText("Phone number",
                "Use this option if you have a direct number but need to enter a command before sending a page. Also, use this option if your paging company uses a single phone number for all pagers and requests a PIN number before sending a page.");
        pPagerOptionPhone.setParameterOptions(true, 8, false);
        pPagerOption = new StringProperty("_pagerOption");
        pPagerOption.setDisplayText("Send page command", "");
        pPagerOption.setParameterOptions(true, 9, false);
        pPagerCustom = new StringProperty("_pagerCustom");
        pPagerCustom
                .setDisplayText(
                        "Modem command",
                        "Use this option if your paging company does not use either of the two choices above. The modem command should contain the phone number to dial, any additional digits, followed by $message.SiteView replaces $message with the message specified for each Alert. where the message should be inserted. The comma character creates a short pause.For example, if the pager company's number is 123-4567, your pager PIN is 333-3333, and each command must be followed by the # key, the command might look like \"ATDT 123-4567,,333-3333#,,$message#\"");
        pPagerCustom.setParameterOptions(true, 10, false);
        pSchedule = new ScheduleProperty("_schedule", "");
        pSchedule.setDisplayText("Schedule", "Optional schedule for these settings to be enabled - for example, enable Sunday from 10:00 to 22:00.");
        pSchedule.setParameterOptions(true, 11, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pName, pDisabled, pPagerSpeed, pPagerType, pPagerAlphaPhone, pPagerAlphaPIN, pPagerDirectPhone, pPagerOptionPhone, pPagerOption, pPagerCustom, pSchedule };
        COM.dragonflow.StandardPreference.PagerInstancePreferences.addProperties("COM.dragonflow.StandardPreference.PagerInstancePreferences", astringproperty);
    }
}
