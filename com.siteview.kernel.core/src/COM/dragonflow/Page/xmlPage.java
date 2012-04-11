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

import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Properties.HashMapOrdered;
import COM.dragonflow.Utils.SiteViewXMLQuery;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class xmlPage extends COM.dragonflow.Page.CGI
{

    static jgl.HashMap excludeVariableMap;

    public xmlPage()
    {
    }

    public static jgl.HashMap buildQueryMap(COM.dragonflow.HTTP.HTTPRequest httprequest)
        throws java.io.IOException
    {
        java.lang.Object obj = null;
        String s = httprequest.getValue("query");
        jgl.Array array = null;
        if(s.length() == 0)
        {
            obj = new HashMapOrdered(true);
            array = COM.dragonflow.Page.xmlPage.getAllowedGroupIDsForAccount(httprequest);
        } else
        {
            String s1 = COM.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.view", httprequest.getAccount()) + java.io.File.separator + s;
            jgl.Array array1 = COM.dragonflow.Properties.FrameFile.readFromFile(s1);
            if(array1.size() > 0)
            {
                obj = (jgl.HashMap)array1.at(0);
            }
            array = new Array();
            jgl.Array array2 = COM.dragonflow.Page.xmlPage.getGroupFilterForAccount(httprequest);
            Enumeration enumeration2 = ((jgl.HashMap) (obj)).values("group");
            do
            {
                if(!enumeration2.hasMoreElements())
                {
                    break;
                }
                String s3 = (String)enumeration2.nextElement();
                if(COM.dragonflow.Page.xmlPage.allowedByGroupFilter(s3, array2))
                {
                    array.add(enumeration2.nextElement());
                }
            } while(true);
        }
        for(int i = 0; i < array.size(); i++)
        {
            ((jgl.HashMap) (obj)).add("group", array.at(i));
        }

        Enumeration enumeration = httprequest.getVariables();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s2 = (String)enumeration.nextElement();
            if(excludeVariableMap.get(s2) == null)
            {
                if(((jgl.HashMap) (obj)).get(s2) != null)
                {
                    ((jgl.HashMap) (obj)).remove(s2);
                }
                Enumeration enumeration1 = httprequest.getValues(s2);
                while(enumeration1.hasMoreElements()) 
                {
                    ((jgl.HashMap) (obj)).add(s2, enumeration1.nextElement());
                }
            }
        } while(true);
        return ((jgl.HashMap) (obj));
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("license");
        if(s.length() == 0)
        {
            s = COM.dragonflow.Utils.LUtils.getLicenseKey();
        }
        if(!COM.dragonflow.Utils.LUtils.isCentraScopeLicense(s))
        {
            outputStream.println("<siteview>\n<error>The XML interface is a feature that requires a CentraScope subscription.</error>\n</siteview>");
            return;
        } else
        {
            jgl.HashMap hashmap = COM.dragonflow.Page.xmlPage.buildQueryMap(request);
            COM.dragonflow.Utils.SiteViewXMLQuery siteviewxmlquery = new SiteViewXMLQuery(hashmap, outputStream, request);
            siteviewxmlquery.printXML();
            return;
        }
    }

    public void printCGIHeader()
    {
        COM.dragonflow.HTTP.HTTPRequest _tmp = request;
        COM.dragonflow.HTTP.HTTPRequest.printHeader(outputStream, 200, "OK", "text/xml");
    }

    public void printCGIFooter()
    {
        outputStream.flush();
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.xmlPage xmlpage = new xmlPage();
        if(args.length > 0)
        {
            xmlpage.args = args;
        }
        xmlpage.handleRequest();
    }

    static 
    {
        excludeVariableMap = new HashMap();
        excludeVariableMap.put("query", "true");
        excludeVariableMap.put("page", "true");
        excludeVariableMap.put("xsl", "true");
        excludeVariableMap.put("account", "true");
        excludeVariableMap.put("license", "true");
    }
}
