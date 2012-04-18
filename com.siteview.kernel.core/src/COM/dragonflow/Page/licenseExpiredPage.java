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

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class licenseExpiredPage extends COM.dragonflow.Page.CGI {

    public static String salesURL;

    public licenseExpiredPage() {
    }

    public void printBody() {
        try {
            String s = getProductName();
            if (request.isPost()) {
                if (request.getValue("licenseAccepted").length() > 0
                        && request.getValue("operation").indexOf("addLic") != -1) {
                    try {
                        COM.dragonflow.Utils.LUtils.updateGeneralLicense(request
                                .getValue("scopeLicense").trim(), false);
                        displayShutdownScreen(s);
                    } catch (java.lang.Exception exception) {
                        displayExpiredLicensePage(s, "Invalid License Key");
                    }
                }
            } else if (COM.dragonflow.Page.licenseExpiredPage.hasValidLicense()) {
                displayShutdownScreen(s);
            } else {
                displayExpiredLicensePage(s, "");
            }
            printFooter(outputStream);
        } catch (java.io.IOException ioexception) {
            java.lang.System.out.print("Unable to find setup.config");
        }
    }

    private void displayExpiredLicensePage(String s,
            String s1) {
        printBodyHeader(s + " license expired.");
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        outputStream
                .println("\n<IMG SRC=/SiteView/htdocs/artwork/SiteViewBanner.gif border=\"0\" alt=\"License Expired\">");
        outputStream.println("\n<h3>Please enter the " + s
                + " license key in the field provided.</h3>");
        if (s1 != null && s1.length() > 0) {
            outputStream
                    .println("<FORM NAME=\"dataForm\" ACTION=/SiteView/cgi/go.exe/SiteView method=POST\n><TABLE border=0 cellspacing=3>\n<TR><TD align=\"left\" valign=\"middle\"><b>"
                            + s
                            + " License Key</b></TD>"
                            + "<TD><TD ALIGN=LEFT><input type=text name=\"scopeLicense\" size=50 value=\""
                            + siteviewgroup.getSetting("_license")
                            + "\"></TD></TD>"
                            + "<TD><TD><b>("
                            + s1
                            + ")</b></TD></TD></TR>" + "</TABLE>\n");
        } else {
            outputStream
                    .println("<FORM NAME=\"dataForm\" ACTION=/SiteView/cgi/go.exe/SiteView method=POST\n><TABLE border=0 cellspacing=3>\n<TR><TD align=\"left\" valign=\"middle\"><b>"
                            + s
                            + " License Key</b></TD>"
                            + "<TD><TD ALIGN=LEFT><input type=text name=\"scopeLicense\" size=50 value=\""
                            + siteviewgroup.getSetting("_license")
                            + "\"></TD></TD></TR>" + "</TABLE>\n");
        }
        outputStream
                .println("\n<input type=\"hidden\" name=\"page\" value=\"licenseExpired\"><input type=\"hidden\" name=\"account\" value=\"administrator\"><input type=\"hidden\" name=\"licenseAccepted\" value=\"yes\"><input type=\"hidden\" name=\"operation\" value=\"addLicense\"><input type=\"submit\" value=\"Continue\"></FORM><p><br></p><p>For assistance in obtaining a "
                        + s
                        + " license key, contact dragonflow Sales "
                        + "<A HREF=" + salesURL + ">here</A></p>");
    }

    private String getProductName() throws java.io.IOException {
        Array array = COM.dragonflow.Properties.FrameFile
                .readFromFile(COM.dragonflow.SiteView.Platform.getRoot()
                        + java.io.File.separator + "classes"
                        + java.io.File.separator + "setup.config");
        HashMap hashmap = (HashMap) array.front();
        String s = (String) hashmap.get("_productName");
        if (s.length() > 0) {
            s = (String) hashmap.get("_productName");
        } else {
            s = "SiteView";
        }
        return s;
    }

    private void displayShutdownScreen(String s) {
        COM.dragonflow.SiteView.SiteViewGroup.cCurrentSiteView.startingUp = false;
        printBodyHeader("Confirm " + s + " Restart");
        outputStream
                .println("\n <IMG SRC=/SiteView/htdocs/artwork/SiteViewBanner.gif border=\"0\" alt=\"License Key Accepted\"\n>");
        outputStream
                .print("<h2>Update "
                        + s
                        + " Licensing</h2>"
                        + "<p>In order to update your SiteView licensing, it is necessary to restart"
                        + " the "
                        + s
                        + " service. "
                        + "<A HREF="
                        + "/SiteView/cgi/go.exe/SiteView?page=restart"
                        + ">Click here</A> "
                        + "to restart now. "
                        + "<b>Please allow SiteView to restart itself. This process will take 60 seconds and then SiteView will be ready for use. </b></p>"
                        + "<p>Alternatively, you can use your system's service manager or the scripts provided with "
                        + s + ".</p>" + "<p><b>NOTE: " + s.toUpperCase()
                        + " WILL NOT BE FUNCTIONAL UNTIL THE SERVICE"
                        + " HAS BEEN RESTARTED</b></p><hr>"
                        + " <table border=\"0\" width=\"90%\"><tr>");
    }

    private static boolean hasValidLicense() {
        boolean flag = false;
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup
                .currentSiteView();
        String s = siteviewgroup.getSetting("_license");
        if (COM.dragonflow.Utils.LUtils.isValidLicense(s)) {
            flag = true;
        }
        return flag;
    }

    static {
        salesURL = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView()
                .getSetting("_licenseSalesURL");
        if (salesURL.length() == 0) {
            salesURL = "http://www.dragonflow.com/us/company/corporate-info/contact-us/";
        }
    }
}
