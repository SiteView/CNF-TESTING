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

public class restartPage extends com.dragonflow.Page.CGI {
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
        com.dragonflow.HTTP.HTTPRequestThread.licenseExpired = false;
        com.dragonflow.Page.CGI.printRefreshHeader(outputStream, "",
                "/SiteView/htdocs/SiteView.html", 0);
        outputStream.flush();
        (new ExitSiteView()).start();
    }
}
