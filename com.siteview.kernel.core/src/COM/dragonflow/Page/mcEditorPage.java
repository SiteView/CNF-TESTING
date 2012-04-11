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
// prefsPage

public class mcEditorPage extends COM.dragonflow.Page.prefsPage {

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
