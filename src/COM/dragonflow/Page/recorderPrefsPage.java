/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;

import java.io.File;
import java.util.Enumeration;

import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Utils.CommandLine;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage, CGI

public class recorderPrefsPage extends COM.dragonflow.Page.prefsPage {

    public static final String configUserName = "_impersonatingUserName";

    public static final String configPassword = "_impersonatingPassword";

    public static final String configDomain = "_impersonatingDomain";

    static final String ELEM_NAME_USER_NAME = "userName";

    static final String ELEM_NAME_PASSWORD = "password";

    static final String ELEM_NAME_DOMAIN = "domain";

    static final String ELEM_NAME_QTP_ROOT = "qtpTestRoot";

    static final String ELEM_NAME_ALT_ROOT = "altTestRoot";

    static final String ELEM_NAME_SAVE_PASSWORD = "savePassword";

    static final String SAVE = "Save Changes";

    static final String TEST_ALT = "Test credentials for ALT";

    static final String TEST_QTP = "Test credentials for QTP";

    private static final String altCredsTestPath;

    static final boolean $assertionsDisabled; /* synthetic field */

    public recorderPrefsPage() {
    }

    void printPreferencesSaved() {
        printPreferencesSaved("/SiteView/" + request.getAccountDirectory()
                + "/SiteView.html", "", 0);
    }

    void savePreferences() {
        try {
            jgl.HashMap hashmap = getMasterConfig();
            hashmap.put("_impersonatingUserName", request.getValue("userName"));
            if (request.getValue("savePassword").equals("1")) {
                hashmap.put("_impersonatingPassword", request
                        .getValue("password"));
            }
            hashmap.put("_impersonatingDomain", request.getValue("domain"));
            hashmap.put("_qtpTestRoot", request.getValue("qtpTestRoot"));
            hashmap.put("_altTestRoot", request.getValue("altTestRoot"));
            saveMasterConfig(hashmap);
            printPreferencesSaved();
        } catch (java.io.IOException ioexception) {
            printError("The preferences could not be saved",
                    "master.config file could not be saved", "10");
        }
    }

    void printForm(String s) {
        jgl.HashMap hashmap = getMasterConfig();
//        boolean flag = isQTPLicensed();
		boolean flag = false;
        boolean flag1 = s.equals("Test credentials for ALT");
        boolean flag2 = s.equals("Test credentials for QTP");
        boolean flag3 = flag1 || flag2;
        boolean flag4 = request.getValue("savePassword").equals("1");
        printBodyHeader("Recorder Scripts Preferences");
        printButtonBar("recorderPrefs.htm", "", getSecondNavItems(request));
        printPrefsBar("Recorder");
        if (flag) {
            outputStream
                    .print(getPagePOST("recorderPrefs", "save")
                            + "<CENTER><H2>Web Transaction Recorder Preferences</H2></CENTER><BR>\n"
                            + "<p>SiteView can run web transactions recorded using DragonFlow's Astra LoadTest (ALT) or Quick Test Pro (QTP). "
                            + " In order to run QTP scripts, SiteView has to run as an interactive service in the context of the LocalSystem account. "
                            + " On the other hand, the process of QTP / ALT Script monitor has to run with user privileges."
                            + " To achieve these requirements, SiteView needs to run the processes of QTP / ALT Script monitors using a user's credentials. "
                            + " Please enter the user credentials in this form. It can be any user with permissions to run QTP / ALT scripts using the QTP / ALT Recorder.</p>");
        } else {
            outputStream
                    .print(getPagePOST("recorderPrefs", "save")
                            + "<CENTER><H2>Web Transaction Recorder Preferences</H2></CENTER><BR>\n"
                            + "<p>SiteView can run web transactions recorded using DragonFlow's Astra LoadTest (ALT).\n"
                            + " The process of ALT Script monitor has to run with the privileges of the user that installed ALT. "
                            + " Please enter this user's credentials in this form.</p>");
        }
        outputStream
                .print(" <hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5><TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> User Name:</TD><TD VALIGN=TOP> <input type=text name=userName size=20 value=\""
                        + (flag3 ? request.getValue("userName")
                                : COM.dragonflow.Page.recorderPrefsPage
                                        .getValue(hashmap,
                                                "_impersonatingUserName"))
                        + "\">\n"
                        + "</TD><TD VALIGN=TOP>Enter the user name to be used to run recorded ALT "
                        + (flag ? "or QTP " : "")
                        + "web transaction monitors. </TD></TR>\n"
                        + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> "
                        + "Password:</TD><TD VALIGN=TOP> <input type=password name="
                        + "password"
                        + " size=20 value=\""
                        + (flag3 ? request.getValue("password") : "")
                        + "\" onchange=passChanged() >\n"
                        + "</TD><TD VALIGN=TOP>Enter the password to be used to run recorded ALT "
                        + (flag ? "or QTP " : "")
                        + "web transaction monitors. </TD></TR>\n"
                        + "<input type=hidden name="
                        + "savePassword"
                        + " value=\""
                        + (flag4 ? "1" : "0")
                        + "\" >\n"
                        + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> "
                        + "Domain:</TD><TD VALIGN=TOP> <input type=text name="
                        + "domain"
                        + " size=20 value=\""
                        + (flag3 ? request.getValue("domain")
                                : COM.dragonflow.Page.recorderPrefsPage
                                        .getValue(hashmap,
                                                "_impersonatingDomain"))
                        + "\">\n"
                        + "</TD><TD VALIGN=TOP>Enter the domain to be used to run recorded ALT "
                        + (flag ? "or QTP " : "")
                        + "web transaction monitors. </TD></TR></table>");
        if (flag3) {
            if (COM.dragonflow.SiteView.Platform.userName.length() > 0
                    && request.getValue("userName").equalsIgnoreCase(
                            COM.dragonflow.SiteView.Platform.userName)) {
                outputStream
                        .print("<br><b>You are running SiteView service with the same user name as you defined here.\nThe defined credentials will be ignored.</b></br>");
            } else {
                boolean flag5 = test(request, flag2);
                outputStream.print("<br><b>LogOn "
                        + (flag5 ? "succeeded."
                                : "failed, please check the user credentials.")
                        + "</b></br>");
            }
        } else {
            outputStream.print("<br> </br>");
        }
        outputStream
                .print("<br><input type=submit name=oper value=\"Test credentials for ALT\"> \n</br>"
                        + (flag ? "<br><input type=submit name=oper value=\"Test credentials for QTP\"> \n</br>"
                                : "")
                        + " <hr><TABLE border=0 width=100% cellspacing=0 cellpadding=5>"
                        + "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> "
                        + "Recorder tests path root:</TD><TD VALIGN=TOP> <input type=text name="
                        + "altTestRoot"
                        + " size=40 value=\""
                        + (flag3 ? request.getValue("altTestRoot")
                                : COM.dragonflow.Page.recorderPrefsPage
                                        .getValue(hashmap, "_altTestRoot"))
                        + "\"> \n"
                        + "</TD><TD VALIGN=TOP>Enter the path of the ALT tests directory. "
                        + "This directory will be used to list the available tests when adding a new ALT Script Monitor. </TD></TR>"
                        + (flag ? "<TR><TD ALIGN=LEFT VALIGN=TOP WIDTH=20%> QTP tests path root:</TD><TD VALIGN=TOP> <input type=text name=qtpTestRoot size=40 value=\""
                                + (flag3 ? request.getValue("qtpTestRoot")
                                        : COM.dragonflow.Page.recorderPrefsPage
                                                .getValue(hashmap,
                                                        "_qtpTestRoot"))
                                + "\"> \n"
                                + "</TD><TD VALIGN=TOP>Enter the path of the QTP tests directory. "
                                + "This directory will be used to list the available tests when adding a new QTP Script Monitor. </TD></TR>"
                                : "")
                        + "</table>\n"
                        + "<hr><br><input type=submit name=oper value=\""
                        + "Save Changes"
                        + "\">\n</br>"
                        + "</FORM>\n"
                        + "<br>\n"
                        + "<script language=\"javascript\"> function passChanged() { document.getElementsByName(\""
                        + "savePassword"
                        + "\").item(0).value=\"1\"; } </script>");
        printFooter(outputStream);
    }

    public void printBody() throws java.lang.Exception {
        if (!request.actionAllowed("_preference")) {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("oper");
        if (request.isPost() && s.equals("Save Changes")) {
            savePreferences();
        } else if (request.isPost()
                && (s.equals("Test credentials for ALT") || s
                        .equals("Test credentials for QTP"))) {
            printForm(s);
        } else {
            printForm("");
        }
    }

    private boolean test(COM.dragonflow.HTTP.HTTPRequest httprequest,
            boolean flag) {
        String s = httprequest.getValue("userName");
        if (s.length() == 0) {
            return false;
        }
        String s1;
        if (httprequest.getValue("savePassword").equals("1")) {
            s1 = httprequest.getValue("password");
        } else {
            jgl.HashMap hashmap = getMasterConfig();
            s1 = COM.dragonflow.Page.recorderPrefsPage.getValue(hashmap,
                    "_impersonatingPassword");
        }
        String s2 = httprequest.getValue("domain");
        COM.dragonflow.Utils.CommandLine commandline = new CommandLine();
        if (flag) {
            String s3 = COM.dragonflow.SiteView.Platform
                    .perfexCommand("")
                    + "-logon 1 -u "
                    + s
                    + (s1.length() <= 0 ? "" : " -p " + s1)
                    + (s2.length() <= 0 ? "" : " -do " + s2);
            jgl.Array array = commandline.exec(s3);
            for (Enumeration enumeration = array.elements(); enumeration
                    .hasMoreElements();) {
                String s6 = (String) enumeration
                        .nextElement();
                if (s6
                        .startsWith(COM.dragonflow.Utils.CommandLine.PERFEX_EXECSYNC_LOGON_FAILURE)) {
                    return false;
                }
            }

            return true;
        } else {
            String s4 = COM.dragonflow.Utils.TempFileManager
                    .getTempDirPath()
                    + java.io.File.separator + "ALTPermissionsTest.log";
            String s5 =/* COM.dragonflow.StandardMonitor.ALTMonitor.MdrvExePath*/
                    " -usr \""
                    + altCredsTestPath
                    + "\""
                    + " -drv_log_file \"" + s4 + "\"";
            if (!$assertionsDisabled && !(new File(altCredsTestPath)).exists()) {
                throw new AssertionError();
            }
            java.io.File file = new File(s4);
            if (file.exists()) {
                file.delete();
            }
            s5 = COM.dragonflow.Utils.CommandLine.getExecSyncCmd("", s5, 20000,
                    true, "", s, s2, s1, false);
            commandline.exec(s5);
            StringBuffer stringbuffer;
            try {
                stringbuffer = COM.dragonflow.Utils.FileUtils.readFile(s4);
            } catch (java.io.IOException ioexception) {
                return false;
            }
            return stringbuffer.indexOf("Test run successfully") > 0;
        }
    }

//    private boolean isQTPLicensed() {
////        return COM.dragonflow.Utils.LUtils.isValidSSforXLicense(new QTMonitor());
//    }

    static {
        $assertionsDisabled = !(COM.dragonflow.Page.recorderPrefsPage.class)
                .desiredAssertionStatus();
        altCredsTestPath = COM.dragonflow.SiteView.Platform.getRoot()
                + java.io.File.separator + "templates.applications"
                + java.io.File.separator + "ALTPermissionsTest"
                + java.io.File.separator + "ALTPermissionsTest.usr";
    }
}
