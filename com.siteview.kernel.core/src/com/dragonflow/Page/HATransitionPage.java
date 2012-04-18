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

import com.dragonflow.HTTP.HTTPRequestException;
import com.dragonflow.SiteView.MirrorConfiguration;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class HATransitionPage extends com.dragonflow.Page.CGI {

    private static boolean debug = false;

    public HATransitionPage() {
    }

    public void printBody() throws java.lang.Exception {
        String s = "OK";
        if (!request.actionAllowed("_file")) {
            throw new HTTPRequestException(557);
        }
        String s1 = request.getValue("operation");
        java.lang.Boolean boolean1 = null;
        try {
            if (s1.equalsIgnoreCase("restartTopazReporting")) {
//                status("Received Request to Restart "
//                        + com.dragonflow.SiteView.TopazInfo.getTopazName()
//                        + " Reporting...");
//                com.dragonflow.TopazIntegration.TopazManager.getInstance()
//                        .restart();
//                status("Restarting "
//                        + com.dragonflow.SiteView.TopazInfo.getTopazName()
//                        + " Reporting...");
                boolean1 = java.lang.Boolean.TRUE;
            } else if (s1.equalsIgnoreCase("copyConfiguration")) {
                status("Copying Configuration to Mirror...");
                com.dragonflow.SiteView.MirrorConfiguration mirrorconfiguration = new MirrorConfiguration();
                if (mirrorconfiguration != null) {
                    mirrorconfiguration.execute();
                }
                boolean1 = java.lang.Boolean.FALSE;
            } else {
                s = "ERROR: HATransitionPage - Unrecognized operation";
            }
            if (boolean1 != null) {
                com.dragonflow.SiteView.MirrorConfiguration
                        .setMirrorStateToHAInControl(boolean1.booleanValue());
            }
            outputStream.println(s);
        } catch (java.lang.Exception exception) {
//            com.dragonflow.TopazIntegration.TopazFileLogger.getLogger().severe(
//                    "Error in HATransitionPage in either resetting "
//                            + com.dragonflow.SiteView.TopazInfo.getTopazName()
//                            + " reporter or copying mirror configuration: "
//                            + exception.getMessage());
            outputStream.println("ERROR: " + exception.getMessage());
        }
    }

    public void printCGIHeader() {
        request.printHeader(outputStream);
    }

    public void printCGIFooter() {
        outputStream.flush();
    }

    private void status(String s) {
        if (debug) {
            java.lang.System.out.println(s);
        }
    }

    static {
        String s = java.lang.System.getProperty(
                "HATransitionPage.debug", "false");
        if (s.equalsIgnoreCase("true")) {
            debug = true;
        }
    }
}
