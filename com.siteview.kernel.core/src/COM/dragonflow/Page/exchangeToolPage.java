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

import java.io.File;
import java.text.SimpleDateFormat;

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class exchangeToolPage extends COM.dragonflow.Page.CGI {

    private static java.text.DateFormat mFileDateFormat;

    public exchangeToolPage() {
    }

    public void printBody() throws java.lang.Exception {
        if (!request.actionAllowed("_tools")) {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("internalId");
        String s1 = request.getValue("targetDir");
        String s2 = request.getValue("name");
        printButtonBar(null, "");
        printBodyHeader("Exchange");
        outputStream
                .println("<center><h2>Exchange Report Tool</h2></center><P>");
        outputStream
                .println("The Exchange Report Tool allows you to view the results of previous runs of certain Exchange monitors.<P>");
        outputStream.println("<b>Previous Runs for \"" + s2 + "\"</b><P>");
        java.io.File file = new File(s1);
        if (file.isDirectory()) {
            String as[] = file.list();
            for (int i = as.length - 1; i >= 0; i--) {
                if (!as[i].startsWith(s + ".")) {
                    continue;
                }
                int j = as[i].indexOf(".");
                String s3 = as[i].substring(j + 1);
                synchronized (mFileDateFormat) {
                    java.util.Date date = mFileDateFormat.parse(s3);
                    String s4 = s1 + java.io.File.separator + as[i];
                    outputStream
                            .println("<a href=\"/SiteView/cgi/go.exe/SiteView?page=exchangeReport&file="
                                    + java.net.URLEncoder.encode(s4, "UTF-8")
                                    + "\">"
                                    + java.text.DateFormat
                                            .getDateTimeInstance().format(date)
                                    + "</a><P>");
                }
            }

        }
        printFooter(outputStream);
    }

    static {
        mFileDateFormat = new SimpleDateFormat(
                COM.dragonflow.SiteView.ExchangeToolBase.REPORT_FILE_DATE_FORMAT);
    }
}
