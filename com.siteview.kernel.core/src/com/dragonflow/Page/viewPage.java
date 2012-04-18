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

import com.recursionsw.jgl.HashMap;

import com.dragonflow.SiteView.PortalQuery;

// Referenced classes of package com.dragonflow.Page:
// CGI, xmlPage

public class viewPage extends com.dragonflow.Page.CGI
{

    public viewPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!com.dragonflow.Utils.LUtils.isCentraScopeLicense())
        {
            com.dragonflow.Page.viewPage.printError(outputStream, "The XML/XSL interface is a feature that requires a CentraScope subscription.", "Please contact <A HREF=mailto:siteviewsales@merc-int.com>siteviewsales@merc-int.com</A> for information about CentraScope.", "/SiteView/htdocs/SiteView.html");
            return;
        }
        HashMap hashmap = com.dragonflow.Page.xmlPage.buildQueryMap(request);
        StringBuffer stringbuffer = new StringBuffer();
        com.dragonflow.SiteView.PortalQuery portalquery = new PortalQuery(hashmap, stringbuffer, request);
        portalquery.printXML();
        String s = stringbuffer.toString();
        String s1 = request.getValue("xsl");
        if(s1.length() > 0)
        {
            String s2 = com.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.view", request.getAccount()) + java.io.File.separator + s1;
            try
            {
                String s4 = com.dragonflow.Utils.FileUtils.readFile(s2).toString();
                com.dragonflow.Utils.XSLUtils.convert(s, s4, outputStream, request, true);
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
                outputStream.println(com.dragonflow.Utils.SiteViewXMLQuery.XML_HEADER);
                outputStream.println("<?xml-stylesheet type=\"text/xsl\" href=\"" + s3 + "\"?>");
                s = s.substring(com.dragonflow.Utils.SiteViewXMLQuery.XML_HEADER.length());
            }
            outputStream.print(s);
        }
    }

    public void printCGIHeader()
    {
        String s = request.getValue("contentType");
        if(s.length() > 0)
        {
            com.dragonflow.HTTP.HTTPRequest _tmp = request;
            com.dragonflow.HTTP.HTTPRequest.printHeader(outputStream, 200, "OK", s);
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
        com.dragonflow.Page.viewPage viewpage = new viewPage();
        if(args.length > 0)
        {
            viewpage.args = args;
        }
        viewpage.handleRequest();
    }
}
