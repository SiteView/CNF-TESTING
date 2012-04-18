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
// prefsPage

public class mcEditorPage extends com.dragonflow.Page.prefsPage {

    public mcEditorPage() {
    }

    public void printBody() throws java.lang.Exception {
        printBodyHeader("Functionality not available");
        printButtonBar("GetStart.htm", "");
        outputStream
                .println("<p>\n<b>The MCEditor functionality has been removed from SiteView.</b>\n<p>\n<a href=\"/SiteView/htdocs/SiteView.html\">Click here</a> to go to the main SiteView page.");
        printFooter(outputStream);
    }
}
