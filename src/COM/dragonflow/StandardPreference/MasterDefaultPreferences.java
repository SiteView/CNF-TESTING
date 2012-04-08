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

import COM.dragonflow.Properties.StringProperty;

public class MasterDefaultPreferences extends COM.dragonflow.SiteView.Preferences {

    public static COM.dragonflow.Properties.StringProperty pMaxFileTransferSize;

    public static COM.dragonflow.Properties.StringProperty pDisableLoginPage;

    public static COM.dragonflow.Properties.StringProperty pAdminChangedDate;

    public static COM.dragonflow.Properties.StringProperty pAdminPassword;

    public static COM.dragonflow.Properties.StringProperty pHttpListenerIP;

    public static COM.dragonflow.Properties.StringProperty pWebserverAddress;

    public static COM.dragonflow.Properties.StringProperty pPartner;

    public static COM.dragonflow.Properties.StringProperty pAccountHTML;

    public static COM.dragonflow.Properties.StringProperty pCustomer;

    public static COM.dragonflow.Properties.StringProperty pMaximumMonitors;

    public static COM.dragonflow.Properties.StringProperty pMaximumReportsCount;

    public static COM.dragonflow.Properties.StringProperty pMinimumFrequency;

    public static COM.dragonflow.Properties.StringProperty pAllowInvalidNode;

    public static COM.dragonflow.Properties.StringProperty pLogInGroup;

    public static COM.dragonflow.Properties.StringProperty pDisableWebtrace;

    public static COM.dragonflow.Properties.StringProperty pDisableRefreshPage;

    public static COM.dragonflow.Properties.StringProperty pPagerPort;

    public static COM.dragonflow.Properties.StringProperty pShowTopazPage;

    public static COM.dragonflow.Properties.StringProperty pLicense;

    public static COM.dragonflow.Properties.StringProperty pLicenseForX;

    public static COM.dragonflow.Properties.StringProperty pAdminName;

    public static COM.dragonflow.Properties.StringProperty pHttpActivePort;

    public static COM.dragonflow.Properties.StringProperty pTransFloatWindow;

    public static COM.dragonflow.Properties.StringProperty pBackupDyns;

    public static COM.dragonflow.Properties.StringProperty pIncludeAlertLogOld;

    public static COM.dragonflow.Properties.StringProperty pReportTableHTML;

    public static COM.dragonflow.Properties.StringProperty pReportTableHeaderHTML;

    public static COM.dragonflow.Properties.StringProperty pReportTableDataHTML;

    public static COM.dragonflow.Properties.StringProperty pHideReportCharts;

    public static COM.dragonflow.Properties.StringProperty pHideReportGraphs;

    public static COM.dragonflow.Properties.StringProperty pHideReportTables;

    public static COM.dragonflow.Properties.StringProperty pHideReportErrors;

    public static COM.dragonflow.Properties.StringProperty pHideReportWarnings;

    public static COM.dragonflow.Properties.StringProperty pHideReportGoods;

    public static COM.dragonflow.Properties.StringProperty pHideReportSummary;

    public static COM.dragonflow.Properties.StringProperty pHideReportLinks;

    public static COM.dragonflow.Properties.StringProperty pHealthDisableLogging;

    public MasterDefaultPreferences() {
    }

    static {
        pMaxFileTransferSize = new StringProperty("_maxFileTransferSize");
        pMaxFileTransferSize.setDisplayText("Max File Transfer Size", "");
        pMaxFileTransferSize.setParameterOptions(true, 1, false);
        pDisableLoginPage = new StringProperty("_disableLoginPage");
        pDisableLoginPage.setDisplayText("Dosable Login Page", "Set equal to true for disabling the new login page method (i.e., page not dialog). ");
        pDisableLoginPage.setParameterOptions(true, 2, false);
        pAdminChangedDate = new StringProperty("_adminChangedDate");
        pAdminChangedDate.setDisplayText("Admin Changed Date", "");
        pAdminChangedDate.setParameterOptions(true, 3, false);
        pAdminPassword = new StringProperty("_adminPassword");
        pAdminPassword.setDisplayText("Admin Password", "The login password for the SiteView administrator.");
        pAdminPassword.setParameterOptions(true, 4, false);
        pHttpListenerIP = new StringProperty("_httpListenerIP");
        pHttpListenerIP.setDisplayText("HTTP Listener IP", "");
        pHttpListenerIP.setParameterOptions(true, 5, false);
        pWebserverAddress = new StringProperty("_webserverAddress");
        pWebserverAddress.setDisplayText("Web Server Address", "the web server address, default is \"localhost\". May be changed to IP or computer name");
        pWebserverAddress.setParameterOptions(true, 6, false);
        pPartner = new StringProperty("_partner");
        pPartner.setDisplayText("Partner", "");
        pPartner.setParameterOptions(true, 7, false);
        pAccountHTML = new StringProperty("_accountHTML");
        pAccountHTML.setDisplayText("Partner", "");
        pAccountHTML.setParameterOptions(true, 8, false);
        pCustomer = new StringProperty("_customer");
        pCustomer.setDisplayText("Customer", "");
        pCustomer.setParameterOptions(true, 9, false);
        pMaximumMonitors = new StringProperty("_maximumMonitors");
        pMaximumMonitors.setDisplayText("Maximum Monitors", "");
        pMaximumMonitors.setParameterOptions(true, 10, false);
        pMaximumReportsCount = new StringProperty("_maximumReportsCount");
        pMaximumReportsCount.setDisplayText("Maximum Reports Count", "");
        pMaximumReportsCount.setParameterOptions(true, 11, false);
        pMinimumFrequency = new StringProperty("_minimumFrequency");
        pMinimumFrequency.setDisplayText("Minimum Frequency", "");
        pMinimumFrequency.setParameterOptions(true, 12, false);
        pAllowInvalidNode = new StringProperty("_allowInvalidNode");
        pAllowInvalidNode.setDisplayText("Allow Invalid Node", "");
        pAllowInvalidNode.setParameterOptions(true, 13, false);
        pLogInGroup = new StringProperty("_logInGroup");
        pLogInGroup.setDisplayText("Log In Group", "");
        pLogInGroup.setParameterOptions(true, 14, false);
        pDisableWebtrace = new StringProperty("_disableWebtrace");
        pDisableWebtrace.setDisplayText("Disable Webtrace", "");
        pDisableWebtrace.setParameterOptions(true, 15, false);
        pDisableRefreshPage = new StringProperty("_disableRefreshPage");
        pDisableRefreshPage.setDisplayText("Disable Refresh Page", "");
        pDisableRefreshPage.setParameterOptions(true, 16, false);
        pPagerPort = new StringProperty("_pagerPort");
        pPagerPort.setDisplayText("Pager Port", "");
        pPagerPort.setParameterOptions(true, 17, false);
        pShowTopazPage = new StringProperty("_showTopazPage");
//        pShowTopazPage.setDisplayText("Show " + COM.dragonflow.SiteView.TopazInfo.getTopazName() + " Page", "");
//        pShowTopazPage.setParameterOptions(true, 18, false);
        pLicense = new StringProperty("_license");
        pLicense.setDisplayText("License", "a license number SiteView will shut down after the evaulation period");
        pLicense.setParameterOptions(true, 19, false);
        pLicenseForX = new StringProperty("_licenseForX");
        pLicenseForX.setDisplayText("License For X", "");
        pLicenseForX.setParameterOptions(true, 20, false);
        pAdminName = new StringProperty("_adminName");
        pAdminName.setDisplayText("Admin Name", "");
        pAdminName.setParameterOptions(true, 21, false);
        pHttpActivePort = new StringProperty("_httpActivePort");
        pHttpActivePort.setDisplayText("HttpActivePort", "");
        pHttpActivePort.setParameterOptions(true, 22, false);
        pTransFloatWindow = new StringProperty("_transFloatWindow");
        pTransFloatWindow.setDisplayText("Trans Float Window", "");
        pTransFloatWindow.setParameterOptions(true, 23, false);
        pBackupDyns = new StringProperty("_backupDyns");
        pBackupDyns.setDisplayText("Backup Dyns", "");
        pBackupDyns.setParameterOptions(true, 24, false);
        pIncludeAlertLogOld = new StringProperty("_backupDyns");
        pIncludeAlertLogOld.setDisplayText("Backup Dyns", "");
        pIncludeAlertLogOld.setParameterOptions(true, 25, false);
        pReportTableHTML = new StringProperty("_reportTableHTML");
        pReportTableHTML.setDisplayText("Report Table HTML", "");
        pReportTableHTML.setParameterOptions(true, 26, false);
        pReportTableHeaderHTML = new StringProperty("_reportTableHeaderHTML");
        pReportTableHeaderHTML.setDisplayText("Report Table Header HTML", "");
        pReportTableHeaderHTML.setParameterOptions(true, 27, false);
        pReportTableDataHTML = new StringProperty("_reportTableDataHTML");
        pReportTableDataHTML.setDisplayText("Report Table Data HTML", "");
        pReportTableDataHTML.setParameterOptions(true, 28, false);
        pHideReportCharts = new StringProperty("_hideReportCharts");
        pHideReportCharts.setDisplayText("Hide Report", "");
        pHideReportCharts.setParameterOptions(true, 29, false);
        pHideReportGraphs = new StringProperty("_hideReportGraphs");
        pHideReportGraphs.setDisplayText("Hide Report Graphs", "");
        pHideReportGraphs.setParameterOptions(true, 30, false);
        pHideReportTables = new StringProperty("_hideReportTables");
        pHideReportTables.setDisplayText("Hide Report Tables", "");
        pHideReportTables.setParameterOptions(true, 31, false);
        pHideReportErrors = new StringProperty("_hideReportErrors");
        pHideReportErrors.setDisplayText("Hide Report Errors", "");
        pHideReportErrors.setParameterOptions(true, 32, false);
        pHideReportWarnings = new StringProperty("_hideReportWarnings");
        pHideReportWarnings.setDisplayText("Hide Report Warnings", "");
        pHideReportWarnings.setParameterOptions(true, 33, false);
        pHideReportGoods = new StringProperty("_hideReportGoods");
        pHideReportGoods.setDisplayText("Hide Report Goods", "");
        pHideReportGoods.setParameterOptions(true, 34, false);
        pHideReportSummary = new StringProperty("_hideReportSummary");
        pHideReportSummary.setDisplayText("Hide Report Summary", "");
        pHideReportSummary.setParameterOptions(true, 35, false);
        pHideReportLinks = new StringProperty("_hideReportLinks");
        pHideReportLinks.setDisplayText("Hide Report Links", "");
        pHideReportLinks.setParameterOptions(true, 36, false);
        pHealthDisableLogging = new StringProperty("_healthDisableLogging");
        pHealthDisableLogging.setDisplayText("Health Disable Logging", "");
        pHealthDisableLogging.setParameterOptions(true, 37, false);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pMaxFileTransferSize, pDisableLoginPage, pAdminChangedDate, pAdminPassword, pHttpListenerIP, pWebserverAddress, pPartner, pAccountHTML, pCustomer, pMaximumMonitors,
                pMaximumReportsCount, pMinimumFrequency, pAllowInvalidNode, pLogInGroup, pDisableWebtrace, pDisableRefreshPage, pPagerPort, pShowTopazPage, pLicense, pLicenseForX, pAdminName, pHttpActivePort, pTransFloatWindow, pBackupDyns,
                pIncludeAlertLogOld, pReportTableHTML, pReportTableHeaderHTML, pReportTableDataHTML, pHideReportCharts, pHideReportGraphs, pHideReportTables, pHideReportErrors, pHideReportWarnings, pHideReportGoods, pHideReportSummary, pHideReportLinks,
                pHealthDisableLogging };
        COM.dragonflow.StandardPreference.MasterDefaultPreferences.addProperties("COM.dragonflow.StandardPreference.MasterDefaultPreferences", astringproperty);
    }
}
