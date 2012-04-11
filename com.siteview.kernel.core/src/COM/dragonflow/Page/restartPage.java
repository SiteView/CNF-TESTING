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

public class restartPage extends COM.dragonflow.Page.CGI {
    class ExitSiteView extends java.lang.Thread {

        public void run() {
            try {
                java.lang.Thread.currentThread();
                java.lang.Thread.sleep(1000L);
            } catch (java.lang.InterruptedException interruptedexception) {
            }
            java.lang.System.exit(0);
        }

        ExitSiteView() {
            super();
        }
    }

    public restartPage() {
    }

    public void printBody() throws java.lang.Exception {
        COM.dragonflow.HTTP.HTTPRequestThread.licenseExpired = false;
        COM.dragonflow.Page.CGI.printRefreshHeader(outputStream, "",
                "/SiteView/htdocs/SiteView.html", 0);
        outputStream.flush();
        (new ExitSiteView()).start();
    }
}
