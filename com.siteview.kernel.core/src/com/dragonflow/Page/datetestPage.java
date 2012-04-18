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

import java.util.Date;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class datetestPage extends com.dragonflow.Page.CGI
{

    public datetestPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        java.util.Date date = new Date(100, 9, 29, 0, 0, 0);
        long l = date.getTime() / 1000L;
        int i = 1440;
        outputStream.println("<HTML><P>Startdate=" + date.toString());
        if(request.isPost())
        {
            if(request.getValue("SUBMIT").equals("Submit Offset"))
            {
                i = (new Integer(request.getValue("OFFSET"))).intValue();
            }
            if(request.getValue("SUBMIT").equals("Submit Date"))
            {
                l = com.dragonflow.Utils.TextUtils.dateStringToSeconds(request.getValue("DATE"), "00:00");
            }
        } else
        {
            i = 1440;
        }
        date = new Date(date.getTime() + (long)(i * 60 * 1000));
        outputStream.println("<P>Enddate + " + i + "MIN=" + date.toString() + "<FORM NAME=\"datetestform\" ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n" + "<INPUT TYPE=HIDDEN NAME=page VALUE=\"datetest\">" + "<INPUT TYPE=TEXT NAME=OFFSET>" + "<INPUT TYPE=SUBMIT NAME=SUBMIT VALUE=\"Submit Offset\">" + "<P><HR align=left width=300><P>" + "<INPUT TYPE=TEXT NAME=DATE>" + "<INPUT TYPE=SUBMIT NAME=SUBMIT VALUE=\"Submit Date\">" + "</FORM>" + "<P>epoch value = " + l + "</HTML>");
        outputStream.flush();
    }

    public static void main(String args[])
    {
        (new datetestPage()).handleRequest();
    }
}
