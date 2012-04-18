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

import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.StandardAction.Page;

public class PagerDefaultPreferences extends COM.dragonflow.SiteView.Preferences {

    public static COM.dragonflow.Properties.StringProperty pPagerPort;

    public static COM.dragonflow.Properties.StringProperty pPagerSpeed;

    public static COM.dragonflow.Properties.StringProperty pPagerHangupCmd;

    public static COM.dragonflow.Properties.StringProperty pPagerInitOptions;

    public static COM.dragonflow.Properties.StringProperty pPagerTimeout;

    public static COM.dragonflow.Properties.StringProperty pPagerType;

    public static COM.dragonflow.Properties.StringProperty pPagerAlphaPhone;

    public static COM.dragonflow.Properties.StringProperty pPagerAlphaPIN;

    public static COM.dragonflow.Properties.StringProperty pPagerDirectPhone;

    public static COM.dragonflow.Properties.StringProperty pPagerOptionPhone;

    public static COM.dragonflow.Properties.StringProperty pPagerOption;

    public static COM.dragonflow.Properties.StringProperty pPagerCustom;

    public PagerDefaultPreferences() {
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
            page.setProperty("_pagerSpeed", (String) hashmap.get("_pagerSpeed"));
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
        Array array = page.pagerSend(s);
        String s2 = "";
        String s3 = "";
        if (array != null) {
            Enumeration enumeration = (Enumeration) array.iterator();
            while (enumeration.hasMoreElements()) {
                String s6 = (String) enumeration.nextElement();
                if (s6.indexOf("message rejected") >= 0) {
                    String s4 = "The message was rejected - this is usually because the PIN number is incorrect or expired. If you're using a 10 digit PIN, try using just the last 7 digits, and no punctuation. If this does not work, try using all 10 digits of the PIN. Less commonly, this error can occur if the message is too long for the paging service.";
                    vector.add(s6);
                }
            }
            
            for (Enumeration enumeration1 = (Enumeration) array.iterator(); enumeration1.hasMoreElements();) {
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

    static {
        pPagerPort = new ScalarProperty("_pagerPort", "");
        pPagerPort.setDisplayText("Modem port", "");
        pPagerPort.setParameterOptions(true, 1, false);
        pPagerSpeed = new ScalarProperty("_pagerSpeed", "");
        pPagerSpeed.setDisplayText("Modem speed", "Most paging companies use 1200 baud - you only need to change the modem speed if your paging company requires it.");
        pPagerSpeed.setParameterOptions(true, 2, false);
        pPagerHangupCmd = new ScalarProperty("_pagerHangupCmd", "");
        pPagerInitOptions = new ScalarProperty("_pagerInitOptions", "");
        pPagerTimeout = new ScalarProperty("_pagerTimeout", "60");
        pPagerType = new ScalarProperty("_pagerType");
        pPagerType
                .setDisplayText(
                        "Different methods for sending a message to your paging service.",
                        "The Preferred method is to connect to a modem at your pager service. When a modem connection is used, SiteView is able to verify that the message was sent successfully. And, the messages can contain a description of the problem.The other options allow numeric messages to be sent using touch tone dialing. Touch tone dialing is limited to numeric messages. It also cannot confirm that your paging service correctly received the message.");
        pPagerType.setParameterOptions(true, 3, false);
        pPagerAlphaPhone = new StringProperty("_pagerAlphaPhone");
        pPagerAlphaPhone.setDisplayText("Modem number",
                "Contact your paging service to find out the Modem Number for your pager. This is also sometimes called the TAP/IXO number. (If you call this number, you should hear a modem answer it with a lovely screeching sound).");
        pPagerAlphaPhone.setParameterOptions(true, 4, false);
        pPagerAlphaPIN = new StringProperty("_pagerAlphaPIN");
        pPagerAlphaPIN.setDisplayText("PIN number", "Enter the last seven digits of your pager's PIN number.");
        pPagerAlphaPIN.setParameterOptions(true, 5, false);
        pPagerDirectPhone = new StringProperty("_pagerDirectPhone");
        pPagerDirectPhone.setDisplayText("Phone number", "Use this option if you use a direct phone number to send a page. Most local paging companies work like this.");
        pPagerDirectPhone.setParameterOptions(true, 6, false);
        pPagerOptionPhone = new StringProperty("_pagerOptionPhone");
        pPagerOptionPhone.setDisplayText("Phone number",
                "Use this option if you have a direct number but need to enter a command before sending a page. Also, use this option if your paging company uses a single phone number for all pagers and requests a PIN number before sending a page.");
        pPagerOptionPhone.setParameterOptions(true, 7, false);
        pPagerOption = new StringProperty("_pagerOption");
        pPagerOption.setDisplayText("Send page command", "");
        pPagerOption.setParameterOptions(true, 8, false);
        pPagerCustom = new StringProperty("_pagerCustom");
        pPagerCustom
                .setDisplayText(
                        "Modem command",
                        "Use this option if your paging company does not use either of the two choices above. The modem command should contain the phone number to dial, any additional digits, followed by $message.SiteView replaces $message with the message specified for each Alert. where the message should be inserted. The comma character creates a short pause.For example, if the pager company's number is 123-4567, your pager PIN is 333-3333, and each command must be followed by the # key, the command might look like \"ATDT 123-4567,,333-3333#,,$message#\"");
        pPagerCustom.setParameterOptions(true, 9, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pPagerPort, pPagerSpeed, pPagerHangupCmd, pPagerInitOptions, pPagerTimeout, pPagerType, pPagerAlphaPhone, pPagerAlphaPIN, pPagerDirectPhone, pPagerOptionPhone, pPagerOption,
                pPagerCustom };
        COM.dragonflow.StandardPreference.PagerDefaultPreferences.addProperties("COM.dragonflow.StandardPreference.PagerDefaultPreferences", astringproperty);
    }
}
