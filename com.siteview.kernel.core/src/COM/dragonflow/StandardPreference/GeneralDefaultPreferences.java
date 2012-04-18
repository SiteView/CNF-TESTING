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

import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteViewException.SiteViewOperationalException;

public class GeneralDefaultPreferences extends COM.dragonflow.SiteView.Preferences {

    public static COM.dragonflow.Properties.StringProperty pLicense;

    public static COM.dragonflow.Properties.StringProperty pLicenseForX;

    public static COM.dragonflow.Properties.StringProperty pBackups2Keep;

    public static COM.dragonflow.Properties.StringProperty pMainGaugesPerRow;

    public static COM.dragonflow.Properties.StringProperty pLocaleEnabled;

    public static COM.dragonflow.Properties.StringProperty pAcknowledgeMonitors;

    public static COM.dragonflow.Properties.StringProperty pDisplayGauges;

    public static COM.dragonflow.Properties.StringProperty pAuthorizedIP;

    public static COM.dragonflow.Properties.StringProperty pCheckAddressAndLogin;

    public static COM.dragonflow.Properties.StringProperty pHttpPort;

    public static COM.dragonflow.Properties.StringProperty pCreateStaticHTML;

    public static COM.dragonflow.Properties.StringProperty pWebserverAddress;

    public static COM.dragonflow.Properties.StringProperty pIsI18N;

    public static COM.dragonflow.Properties.StringProperty pSuspendMonitors;

    public static COM.dragonflow.Properties.StringProperty pDefaultAuthUsername;

    public static COM.dragonflow.Properties.StringProperty pDefaultAuthPassword;

    public static COM.dragonflow.Properties.StringProperty pAlertIconLink;

    public static COM.dragonflow.Properties.StringProperty pReportIconLink;

    public GeneralDefaultPreferences() {
    }

    public String getReturnName() {
        return pLicense.getName();
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, java.util.HashMap hashmap, java.util.HashMap hashmap1) {
        if (stringproperty == pHttpPort && !s.equals("")) {
            try {
                int i = (new Integer(s.trim())).intValue();
                if (i == 0) {
                    hashmap1.put(stringproperty, "HTTP Port must be a number greater than 0");
                }
            } catch (java.lang.NumberFormatException numberformatexception) {
                hashmap1.put(stringproperty, "The entry is not a valid number.");
            }
        } else if (stringproperty == pAuthorizedIP) {
            if (s.length() > 0 && !COM.dragonflow.Utils.TextUtils.onlyChars(s, "0123456789,.*")) {
                hashmap1.put(stringproperty, "The IP Address allowed access contained illegal characters");
            }
        } else if (stringproperty == pCheckAddressAndLogin) {
            if (s.length() > 0 && ((String) hashmap.get(pAuthorizedIP.getName())).length() == 0) {
                hashmap1.put(stringproperty, "To enable \"Require both IP address and Login\" you must fill in authorized IP address(es))");
            }
        } else if (stringproperty == pMainGaugesPerRow) {
            if (s.equals("")) {
                s = stringproperty.getDefault();
            }
            int j = COM.dragonflow.Utils.TextUtils.toInt(s);
            if (j < 1 || j > 20) {
                s = stringproperty.getDefault();
            }
        } else if (stringproperty == pBackups2Keep) {
            if (s.equals("")) {
                s = stringproperty.getDefault();
            }
            int k = COM.dragonflow.Utils.TextUtils.toInt(s);
            if (k < 1 || k > 80) {
                s = stringproperty.getDefault();
            }
        }
        return s;
    }

    public String[] updatePreferences(java.util.HashMap hashmap, String s, String s1) throws COM.dragonflow.SiteViewException.SiteViewException {
        String as[] = new String[2];
        try {
            String s2 = (String) hashmap.get(pLicenseForX.getName());
            HashMap hashmap1 = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s3 = (String) hashmap1.get(pLicenseForX.getName());
            as = super.updatePreferences(hashmap, s, s1);
            if ((s3 != null && s3.length() > 0 || s2 != null && s2.length() > 0) && checkLicenseChange(s3, s2) && s2.length() > 0) {
                COM.dragonflow.Utils.LUtils.addMonitorType(s2);
            }
            COM.dragonflow.Api.APISiteView.refreshSSChildObjects();
        } catch (COM.dragonflow.SiteViewException.SiteViewException siteviewexception) {
            siteviewexception.fillInStackTrace();
            throw siteviewexception;
        } catch (java.lang.Exception exception) {
            throw new SiteViewOperationalException(COM.dragonflow.Resource.SiteViewErrorCodes.ERR_OP_SS_PREFERENCE_EXCEPTION, new String[] { "GeneralDefaultPreferences", "updatePreferences" }, 0L, exception.getMessage());
        }
        return as;
    }

    private boolean checkLicenseChange(String s, String s1) {
        boolean flag = false;
        if (s == null) {
            s = "";
        }
        if (s1 == null) {
            s1 = "";
        }
        if (s.length() != s1.length()) {
            flag = true;
        } else if (s1.length() > 0 && s.length() > 0) {
            String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
            String as1[] = COM.dragonflow.Utils.TextUtils.split(s, ",");
            int i = 0;
            for (int j = 0; j < as.length; j ++) {
                for (int k = 0; k < as1.length; k ++) {
                    if (as1[k].trim().equals(as[j].trim())) {
                        i ++;
                    }
                }

            }

            if (i != as1.length) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    static {
        int i = 1;
        pLicense = new StringProperty("_license");
        pLicense.setDisplayText("License Number", "Enter the license number for this copy of SiteView. The license number is given to you when you purchase a license and is required after the 10 day evaluation period.");
        pLicense.setParameterOptions(true, i ++, false);
        pMainGaugesPerRow = new StringProperty("_mainGaugesPerRow", "3");
        pMainGaugesPerRow.setDisplayText("Groups per Row", "Enter the number of group names that you would like in each row on the main SiteView Screen.");
        pMainGaugesPerRow.setParameterOptions(true, i ++, false);
        pLocaleEnabled = new BooleanProperty("_localeEnabled", "");
        pLocaleEnabled.setDisplayText("Locale-specific display of date and time", "Choose this option to enable the display of dates and times in a locale-specific manner.  The default is the United States format.");
        pLocaleEnabled.setParameterOptions(true, i ++, false);
        pAcknowledgeMonitors = new BooleanProperty("_acknowledgeMonitors", "");
        pAcknowledgeMonitors.setDisplayText("Enable Operator Acknowledgement Functionality",
                "Choose this option to enable Operator Acknowledgement Functionality.  This allows users to click on a monitor status icon and enter an acknowledgement text and time.");
        pAcknowledgeMonitors.setParameterOptions(true, i ++, false);
        pDisplayGauges = new BooleanProperty("_displayGauges", "");
        pDisplayGauges.setDisplayText("Blue gauges", "Check this option to enable the display blue gauges for monitors and groups.");
        pDisplayGauges.setParameterOptions(true, i ++, false);
        pAuthorizedIP = new StringProperty("_authorizedIP");
        pAuthorizedIP
                .setDisplayText(
                        "IP Addresses Allowed Access",
                        "The IP address that is always allowed access to SiteView. Use a trailing wildcard character to specify a range of IP addresses. For example, 206.168.191.* allows access to the 206.168.191 subnet. To allow multiple IP address, separate them by commas. To allow any IP address leave this field blank.");
        pAuthorizedIP.setParameterOptions(true, i ++, false);
        pCheckAddressAndLogin = new BooleanProperty("_checkAddressAndLogin", "");
        pCheckAddressAndLogin
                .setDisplayText("Require both IP address and Login",
                        "This option limits access to connections that have both an allowed IP address and a correct username and password. When this option is turned off, it allows access to anyone who has either an allowed IP address or a valid username and password.");
        pCheckAddressAndLogin.setParameterOptions(true, i ++, false);
        pHttpPort = new StringProperty("_httpPort");
        pHttpPort.setDisplayText("SiteView Port", "Enter the port number for the SiteView built-in web server. Leaving this blank will prevent SiteView from serving web pages.");
        pHttpPort.setParameterOptions(true, i ++, false);
        pCreateStaticHTML = new BooleanProperty("_createStaticHTML", "");
        pCreateStaticHTML.setDisplayText("Create static HTML pages", "This option determines whether SiteView creates static HTML pages. Select this option if you access SiteView information using a separate web server application.");
        pCreateStaticHTML.setParameterOptions(true, i ++, false);
        pWebserverAddress = new StringProperty("_webserverAddress");
        pWebserverAddress.setDisplayText("Web Server Address",
                "Enter the domain name or address of the web server serving the SiteView web pages (for example, demo.siteview.com).  This option is used for providing links to SiteView management report pages in history summary e-mail.");
        pWebserverAddress.setParameterOptions(true, i ++, false);
        pLicenseForX = new StringProperty("_licenseForX");
        pLicenseForX.setDisplayText("License Number For X", "Enter the license number for X for this copy of SiteView. The license number is given to you when you purchase X.");
        pLicenseForX.setParameterOptions(true, i ++, false);
        pBackups2Keep = new StringProperty("_backups2Keep", "1");
        pBackups2Keep.setDisplayText("Backups To Keep", "Enter the number of .mg files to keep as backups.");
        pBackups2Keep.setParameterOptions(true, i ++, false);
        pIsI18N = new BooleanProperty("_isI18N", "");
        pIsI18N.setDisplayText("International Version",
                "Enables SiteView to work with multiple character sets. When checked, all character encodings will be honored (for example, foreign language web pages). When unchecked, only the local system encoding is honored.");
        pIsI18N.setParameterOptions(true, i ++, false);
        pSuspendMonitors = new BooleanProperty("_suspendMonitors", "");
        pSuspendMonitors.setDisplayText("Suspend Monitors", "Suspend all monitors. When checked, all monitors will be suspended. When unchecked, all monitors will start running according to their previous configuration.");
        pSuspendMonitors.setParameterOptions(true, i ++, false);
        pDefaultAuthUsername = new StringProperty("_defaultAuthUsername", "");
        pDefaultAuthUsername.setDisplayText("Username", "Enter the default username to be used for authentication with remote systems. Both \"username\" and \"DOMAIN\\username\" are valid formats.");
        pDefaultAuthUsername.setParameterOptions(true, i ++, false);
        pDefaultAuthPassword = new StringProperty("_defaultAuthPassword", "");
        pDefaultAuthPassword.setDisplayText("Password", "Enter the default password to be used for authentication with remote systems.");
        pDefaultAuthPassword.setParameterOptions(true, i ++, false);
        pAlertIconLink = new BooleanProperty("_alertIconLink", "");
        pAlertIconLink.setDisplayText("Alert Icons", "Enables display of alert information for groups and monitors. ");
        pAlertIconLink.setParameterOptions(true, i ++, false);
        pReportIconLink = new BooleanProperty("_reportIconLink", "");
        pReportIconLink.setDisplayText("Report Icons", "Enables display of report information for groups and monitors. ");
        pReportIconLink.setParameterOptions(true, i ++, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pLicense, pMainGaugesPerRow, pLocaleEnabled, pAcknowledgeMonitors, pDisplayGauges, pAuthorizedIP, pCheckAddressAndLogin, pHttpPort, pCreateStaticHTML, pWebserverAddress, pLicenseForX,
                pBackups2Keep, pIsI18N, pSuspendMonitors, pDefaultAuthUsername, pDefaultAuthPassword, pAlertIconLink, pReportIconLink };
        COM.dragonflow.StandardPreference.GeneralDefaultPreferences.addProperties("COM.dragonflow.StandardPreference.GeneralDefaultPreferences", astringproperty);
    }
}
