/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class resetPage extends com.dragonflow.Page.CGI {

    public resetPage() {
    }

    public void printBody() {
        String s = request.getValue("group");
        String s1 = com.dragonflow.Utils.I18N.UnicodeToString(s,
                com.dragonflow.Utils.I18N.nullEncoding());
        if (request.getValue("class").equals("logEventHealth")) {
            outputStream.println("<P>Resetting log event health");
            com.dragonflow.Health.logEventHealth.reset();
            printRefreshPage(getPageLink("monitor", "RefreshMonitor")
                    + "&refresh=true&group="
                    + com.dragonflow.HTTP.HTTPRequest.encodeString(s1) + "&id="
                    + request.getValue("id"), 0);
        }
        com.dragonflow.Page.resetPage.printBodyHeader(outputStream,
                "Resetting counters", "");
        outputStream.println("<p><a href="
                + com.dragonflow.Page.CGI.getGroupDetailURL(request, s1)
                + ">Return to Group page</a>");
        printFooter(outputStream);
        com.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s1);
    }
}
