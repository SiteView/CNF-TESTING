/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.ExternalUtils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings;

public class SiteViewExternalHttpRequest
{

    public SiteViewExternalHttpRequest()
    {
    }

    public static void main(String args[])
    {
        if(args.length < 2)
        {
            COM.dragonflow.ExternalUtils.SiteViewExternalHttpRequest.printUsage();
            java.lang.System.exit(1);
        }
        String s = "";
        String s1 = null;
        String s2 = null;
        String s3 = null;
        if(args[0].equals("-page"))
        {
            String s4 = "http";
            jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_httpActivePort");
            String s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_httpSecurePort");
            if(s6.length() > 0)
            {
                s4 = "https";
            }
            jgl.HashMap hashmap1 = COM.dragonflow.SiteView.User.findUser(COM.dragonflow.SiteView.User.readUsers(), "administrator");
            if(hashmap1 != null)
            {
                s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_login");
                s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_password");
                s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_domain");
            }
            s = s4 + "://127.0.0.1:" + s5 + "/SiteView/cgi/go.exe/SiteView?page=" + args[1] + "&account=administrator";
        } else
        if(args[0].equals("-url"))
        {
            s = args[1];
        }
        StringBuffer stringbuffer = new StringBuffer();
        COM.dragonflow.ApacheHttpClientUtils.HTTPRequestSettings httprequestsettings = new HTTPRequestSettings(s, s1, s2, s3, null, null, null, null);
        COM.dragonflow.ApacheHttpClientUtils.ApacheHttpUtils.getRequest(httprequestsettings, stringbuffer);
        java.lang.System.exit(0);
    }

    private static void printUsage()
    {
        java.lang.System.out.println("usage:");
        java.lang.System.out.println("-page page - will perform an HTTPRequest to this page on the same SS machine, port 8888");
        java.lang.System.out.println("-url fullUrl - will perform an HTTPRequest to the specified URL");
    }
}
