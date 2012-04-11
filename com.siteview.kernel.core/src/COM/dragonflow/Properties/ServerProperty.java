/*
 * 
 * Created on 2005-2-28 7:07:17
 *
 * ServerProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>ServerProperty</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.net.URLEncoder;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Page.CGI;
import COM.dragonflow.SiteView.IServerPropMonitor;
import COM.dragonflow.SiteView.Machine;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.ServerAction;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.StandardMonitor.LogMonitor;

// Referenced classes of package COM.dragonflow.Properties:
// StringProperty

public class ServerProperty extends StringProperty {

    public ServerProperty(String s) {
        this(s, "");
    }

    public ServerProperty(String s, String s1) {
        super(s, s1);
    }

    public void printProperty(CGI cgi, PrintWriter printwriter, SiteViewObject siteviewobject,
            HTTPRequest httprequest, HashMap hashmap, boolean flag) {
        String s = "";
        String s1 = siteviewobject.getProperty(this);
        String s2 = "";
        if ((siteviewobject instanceof ServerAction) || (siteviewobject instanceof LogMonitor)) {
            if (Platform.isNTRemote(s1)) {
                s1 = "";
            }
            s2 = "&noNTRemote=true";
        } else if ((siteviewobject instanceof IServerPropMonitor)
                && !((IServerPropMonitor) siteviewobject).remoteCommandLineAllowed()) {
            if (Platform.isUnix(cgi.platformOS())) {
                return;
            }
            if (Platform.isCommandLineRemote(s1)) {
                s1 = "";
            }
            s2 = "&noremote=true";
        }
        String s3 = s1;
        if (s3.length() == 0) {
            s3 = "this server";
        } else {
            String s4 = Machine.getFullMachineID(s1, httprequest);
            s3 = Machine.getMachineName(s4);
            if (s3.startsWith("\\\\") && s3.equals(s4)) {
                s3 = Machine.getMachineFromMachineID(s4);
            }
        }
        printwriter.println("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + getLabel() + "</TD>"
                + "<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><TABLE border=1 cellspacing=0><TR><TD>" + s3
                + "</TD></TR></TABLE></TD><TD><input type=hidden name=" + getName() + " value=\"" + s1 + "\">"
                + "<a href=" + cgi.getPageLink("server", "") + "&server=" + URLEncoder.encode(s1) + s2 + "&returnURL="
                + URLEncoder.encode(httprequest.rawURL) + ">choose server</a>"
                + "</TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + getDescription()
                + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s + "</I></TD></TR>");
    }
}
