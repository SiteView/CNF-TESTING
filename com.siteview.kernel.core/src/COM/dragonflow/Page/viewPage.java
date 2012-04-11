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

import COM.dragonflow.SiteView.PortalQuery;

// Referenced classes of package COM.dragonflow.Page:
// CGI, xmlPage

public class viewPage extends COM.dragonflow.Page.CGI
{

    public viewPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!COM.dragonflow.Utils.LUtils.isCentraScopeLicense())
        {
            COM.dragonflow.Page.viewPage.printError(outputStream, "The XML/XSL interface is a feature that requires a CentraScope subscription.", "Please contact <A HREF=mailto:siteviewsales@merc-int.com>siteviewsales@merc-int.com</A> for information about CentraScope.", "/SiteView/htdocs/SiteView.html");
            return;
        }
        jgl.HashMap hashmap = COM.dragonflow.Page.xmlPage.buildQueryMap(request);
        StringBuffer stringbuffer = new StringBuffer();
        COM.dragonflow.SiteView.PortalQuery portalquery = new PortalQuery(hashmap, stringbuffer, request);
        portalquery.printXML();
        String s = stringbuffer.toString();
        String s1 = request.getValue("xsl");
        if(s1.length() > 0)
        {
            String s2 = COM.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.view", request.getAccount()) + java.io.File.separator + s1;
            try
            {
                String s4 = COM.dragonflow.Utils.FileUtils.readFile(s2).toString();
                COM.dragonflow.Utils.XSLUtils.convert(s, s4, outputStream, request, true);
            }
            catch(java.io.FileNotFoundException filenotfoundexception)
            {
                outputStream.println("<H2>Could not find XSL file: " + s2 + "</H2>");
            }
        } else
        {
            String s3 = request.getValue("xslURL");
            if(s3.length() > 0)
            {
                outputStream.println(COM.dragonflow.Utils.SiteViewXMLQuery.XML_HEADER);
                outputStream.println("<?xml-stylesheet type=\"text/xsl\" href=\"" + s3 + "\"?>");
                s = s.substring(COM.dragonflow.Utils.SiteViewXMLQuery.XML_HEADER.length());
            }
            outputStream.print(s);
        }
    }

    public void printCGIHeader()
    {
        String s = request.getValue("contentType");
        if(s.length() > 0)
        {
            COM.dragonflow.HTTP.HTTPRequest _tmp = request;
            COM.dragonflow.HTTP.HTTPRequest.printHeader(outputStream, 200, "OK", s);
        } else
        {
            request.printHeader(outputStream);
        }
    }

    public void printCGIFooter()
    {
        outputStream.flush();
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.viewPage viewpage = new viewPage();
        if(args.length > 0)
        {
            viewpage.args = args;
        }
        viewpage.handleRequest();
    }
}
