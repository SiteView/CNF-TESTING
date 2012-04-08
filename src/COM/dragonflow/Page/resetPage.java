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
// CGI

public class resetPage extends COM.dragonflow.Page.CGI {

    public resetPage() {
    }

    public void printBody() {
        String s = request.getValue("group");
        String s1 = COM.dragonflow.Utils.I18N.UnicodeToString(s,
                COM.dragonflow.Utils.I18N.nullEncoding());
        if (request.getValue("class").equals("logEventHealth")) {
            outputStream.println("<P>Resetting log event health");
            COM.dragonflow.Health.logEventHealth.reset();
            printRefreshPage(getPageLink("monitor", "RefreshMonitor")
                    + "&refresh=true&group="
                    + COM.dragonflow.HTTP.HTTPRequest.encodeString(s1) + "&id="
                    + request.getValue("id"), 0);
        }
        COM.dragonflow.Page.resetPage.printBodyHeader(outputStream,
                "Resetting counters", "");
        outputStream.println("<p><a href="
                + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s1)
                + ">Return to Group page</a>");
        printFooter(outputStream);
        COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s1);
    }
}
