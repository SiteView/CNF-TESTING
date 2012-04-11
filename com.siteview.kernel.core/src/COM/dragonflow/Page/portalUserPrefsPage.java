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


// Referenced classes of package COM.dragonflow.Page:
// userPrefsPage, portalChooserPage

public class portalUserPrefsPage extends COM.dragonflow.Page.userPrefsPage
{

    public portalUserPrefsPage()
    {
    }

    String getHelpPage()
    {
        return "UserPrefs.htm";
    }

    void printAccessFields(String s, jgl.HashMap hashmap, jgl.HashMap hashmap1)
        throws java.lang.Exception
    {
        String s1;
        if(request.getValue("item").length() > 0)
        {
            s1 = null;
        } else
        {
            s1 = COM.dragonflow.Page.portalUserPrefsPage.getValue(hashmap1, "_query");
        }
        String s2 = "Query";
        String s3 = "</TD></TR><TR><TD><FONT SIZE=-1>optional, restrict access to the selected items - the default allows access to all items</FONT></TD></TR>";
        COM.dragonflow.Page.portalChooserPage.printQueryChooseList(outputStream, request, s2, s3, s1);
        jgl.Array array = COM.dragonflow.SiteView.Portal.getParentViewArray();
        outputStream.println("<TR><TD ALIGN=RIGHT>Home View</TD><TD><TABLE><TR><TD ALIGN=LEFT><select size=1 name=_homeView>" + COM.dragonflow.Page.portalUserPrefsPage.getOptionsHTML(array, COM.dragonflow.Page.portalUserPrefsPage.getValue(hashmap1, "_homeView")) + "</SELECT></TD></TR>" + "<TR><TD><FONT SIZE=-1>the home view (front page) for this user</FONT></TD></TR>" + "</TABLE></TD><TD></TD></TR>");
        jgl.Array array1 = COM.dragonflow.SiteView.Portal.getEditableViewContentsArray("Bar", true);
        outputStream.println("<TR><TD ALIGN=RIGHT>Top Bar</TD><TD><TABLE><TR><TD ALIGN=LEFT><select size=1 name=_buttonBar>" + COM.dragonflow.Page.portalUserPrefsPage.getOptionsHTML(array1, COM.dragonflow.Page.portalUserPrefsPage.getValue(hashmap1, "_buttonBar")) + "</SELECT></TD></TR>" + "<TR><TD><FONT SIZE=-1>the top function bar for this user on portal generated pages</FONT></TD></TR>" + "</TABLE></TD><TD></TD></TR>");
    }

    void printPermissionsCheckBoxes(boolean flag, jgl.HashMap hashmap)
    {
        printOption(flag, hashmap, "Edit Groups", "_groupEdit", "add, rename, copy, or delete groups");
        printOption(flag, hashmap, "Edit Monitors", "_monitorEdit", "add, edit, or delete monitors");
        printOption(flag, hashmap, "Edit Alerts", "_alertEdit", "add, edit, or delete alerts");
        printOption(flag, hashmap, "Edit Reports", "_reportEdit", "add, edit, or delete reports");
        printOption(flag, hashmap, "Edit Preferences", "_preference", "use any of the Preference forms");
        printOption(flag, hashmap, "Create Adhoc Reports", "_reportAdhoc", "create adhoc reports");
        printOption(flag, hashmap, "Test Alerts", "_alertTest", "test an alert");
        printOption(flag, hashmap, "Tests Preferences", "_preferenceTest", "test a preference setting");
        printOption(flag, hashmap, "Disable Groups", "_groupDisable", "disable or enable groups");
        printOption(flag, hashmap, "Disable Monitors", "_monitorDisable", "disable or enable monitors");
        printOption(flag, hashmap, "Disable Alerts", "_alertDisable", "disable or enable alerts");
        printOption(flag, hashmap, "Disable Reports", "_reportDisable", "disable or enable reports");
        printOption(flag, hashmap, "Refresh Groups", "_groupRefresh", "refresh groups");
        printOption(flag, hashmap, "Refresh Monitors", "_monitorRefresh", "refresh monitors");
        printOption(flag, hashmap, "Acknowledge Monitors", "_monitorAcknowledge", "acknowledge monitors");
        printOption(flag, hashmap, "Use Monitor Tools", "_monitorTools", "use the Tools form for a monitor");
        printOption(flag, hashmap, "Use Tools", "_tools", "use the generic Tools forms");
        printOption(flag, hashmap, "Use the Support Tool", "_support", "use the Send Support Request form");
        printOption(flag, hashmap, "View Monitor History Report", "_monitorRecent", "view the recent history report for a monitor");
        printOption(flag, hashmap, "View Progress", "_progress", "view the Progress page showing monitors that are running");
        printOption(flag, hashmap, "View Logs", "_logs", "view the raw data for monitors, alerts and other logs");
        printOption(flag, hashmap, "View Alerts List", "_alertList", "view the list of the alerts in the alert page");
        printOption(flag, hashmap, "Show Report Toolbar", "_reportToolbar", "include links at the top on reports");
        printOption(flag, hashmap, "Generate Reports", "_reportGenerate", "generate a scheduled report manually");
    }

    void printPrefsBar(String s)
    {
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.portalUserPrefsPage portaluserprefspage = new portalUserPrefsPage();
        if(args.length > 0)
        {
            portaluserprefspage.args = args;
        }
        portaluserprefspage.handleRequest();
    }
}
