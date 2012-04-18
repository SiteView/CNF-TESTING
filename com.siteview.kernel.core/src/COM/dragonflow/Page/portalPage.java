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

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

import COM.dragonflow.SiteView.PortalFilter;
import COM.dragonflow.SiteView.PortalQuery;

// Referenced classes of package COM.dragonflow.Page:
// CGI, portalPreferencePage

public class portalPage extends COM.dragonflow.Page.CGI
{

    public portalPage()
    {
    }

    public static void convertViewCells(HashMap hashmap)
    {
        Enumeration enumeration = (Enumeration) hashmap.values("_cell");
        Array array = new Array();
        HashMap hashmap1;
        for(; enumeration.hasMoreElements(); array.add(hashmap1))
        {
            String s = (String)enumeration.nextElement();
            hashmap1 = COM.dragonflow.Utils.TextUtils.stringToHashMap(s);
        }

        hashmap.remove("_cell");
        for(int i = 0; i < array.size(); i++)
        {
            hashmap.add("_cell", array.get(i));
        }

    }

    public static HashMap getView(String s)
        throws java.io.IOException
    {
        String s1 = "";
        if(s.indexOf("-") > 0)
        {
            try
            {
                s1 = COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view/" + s.substring(0, s.indexOf("-")) + "/views.config";
                int i = s.indexOf("-") + 1;
                int j = s.length();
                s = s.substring(i, j);
            }
            catch(java.lang.Exception exception)
            {
                exception.printStackTrace();
            }
        } else
        {
            s1 = COM.dragonflow.SiteView.Platform.getRoot() + "/groups/views.config";
        }
        Array array = COM.dragonflow.Properties.FrameFile.readFromFile(s1);
        String s2 = "_id";
        if(COM.dragonflow.Utils.TextUtils.hasLetters(s))
        {
            s2 = "_title";
        }
        HashMap hashmap = COM.dragonflow.Page.portalPreferencePage.findFrame(array, s2, s, 1);
        if(hashmap != null)
        {
            COM.dragonflow.Page.portalPage.convertViewCells(hashmap);
        }
        return hashmap;
    }

    public static int[] getTableDimensions(Enumeration enumeration)
    {
        int i = 0;
        int j;
        HashMap hashmap;
        for(j = 0; enumeration.hasMoreElements(); j = java.lang.Math.max(j, COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "xEnd"))))
        {
            hashmap = (HashMap)enumeration.nextElement();
            i = java.lang.Math.max(i, COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "yEnd")));
        }

        int ai[] = new int[2];
        ai[0] = i;
        ai[1] = j;
        return ai;
    }

    public static HashMap[][] getLayout(Enumeration enumeration, int i, int j)
    {
        HashMap ahashmap[][] = new HashMap[i][j];
        for(int k = 0; k < ahashmap.length; k++)
        {
            for(int l = 0; l < ahashmap[k].length; l++)
            {
                ahashmap[k][l] = null;
            }

        }

        while(enumeration.hasMoreElements()) 
        {
            HashMap hashmap = (HashMap)enumeration.nextElement();
            int i1 = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "yStart"));
            int j1 = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "xStart"));
            ahashmap[i1 - 1][j1 - 1] = hashmap;
        }
        return ahashmap;
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!COM.dragonflow.Utils.LUtils.isCentraScopeLicense())
        {
            COM.dragonflow.Page.portalPage.printError(outputStream, "The XML/XSL interface is a feature that requires a " + COM.dragonflow.SiteView.Platform.productName + " subscription.", "Please contact <A HREF=mailto:siteviewsales@merc-int.com>siteviewsales@merc-int.com</A> for information about " + COM.dragonflow.SiteView.Platform.productName + ".", "/SiteView/htdocs/SiteView.html");
            return;
        }
        HashMap hashmap = getMasterConfig();
        String s = request.getValue("slide");
        if(s.equalsIgnoreCase("off"))
        {
            hashmap.remove("_portalFirstRun");
            hashmap.put("_portalFirstRun", "false");
            saveMasterConfig(hashmap);
        }
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_portalFirstRun").equalsIgnoreCase("true"))
        {
            printBodyHeader("CentraScope Start Slide Show");
            printSlideShow("http:/SiteView/htdocs/slides/CentraSlide1.htm");
            printFooter(outputStream);
            printCGIFooter();
            return;
        }
        String s1 = request.getValue("view");
        COM.dragonflow.SiteView.User user = request.getUser();
        if(s1.length() == 0)
        {
            s1 = user.getProperty("_homeView");
        }
        if(s1.length() == 0)
        {
            s1 = "1";
        }
        HashMap hashmap1 = COM.dragonflow.Page.portalPage.getView(s1);
        if(hashmap1 == null)
        {
            s1 = "1";
            hashmap1 = COM.dragonflow.Page.portalPage.getView(s1);
            COM.dragonflow.Log.LogManager.log("Error", "view id " + s1 + " missing, for user " + user.getProperty(COM.dragonflow.SiteView.User.pLogin) + " - using default view");
        }
        request.setValue("view", s1);
        if(((Enumeration) hashmap1.values("_framesHTML")).hasMoreElements())
        {
            java.lang.System.out.println("Building frame view");
            printFrameBody(hashmap1);
        } else
        {
            int ai[] = COM.dragonflow.Page.portalPage.getTableDimensions((Enumeration) hashmap1.values("_cell"));
            int i = ai[0];
            int j = ai[1];
            HashMap ahashmap[][] = COM.dragonflow.Page.portalPage.getLayout((Enumeration) hashmap1.values("_cell"), i, j);
            String s2 = COM.dragonflow.Page.portalPage.getValue(hashmap1, "_contentType");
            if(s2.length() == 0)
            {
                s2 = "text/html";
            }
            boolean flag = s2.equalsIgnoreCase("text/html");
            if(flag)
            {
                super.printCGIHeader();
                String s3 = COM.dragonflow.Page.portalPage.getValue(hashmap1, "_header");
                if(s3.length() > 0)
                {
                    s3 = COM.dragonflow.SiteView.Portal.getViewContent(s3, request);
                }
                if(s3.length() == 0)
                {
                    s3 = "<HEAD><TITLE>" + COM.dragonflow.SiteView.Platform.productName + "</TITLE></HEAD>" + "<BODY BGCOLOR=#FFFFFF><TABLE border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">";
                }
                if(request.getValue("debug").length() > 0)
                {
                    s3 = "<HEAD><TITLE>Debugging " + COM.dragonflow.SiteView.Platform.productName + "</TITLE></HEAD>" + "<BODY BGCOLOR=#FFFFFF><TABLE WIDTH=100% BORDER=3>";
                }
                outputStream.println(s3);
            } else
            {
                COM.dragonflow.HTTP.HTTPRequest _tmp = request;
                COM.dragonflow.HTTP.HTTPRequest.printHeader(outputStream, 200, "OK", s2);
            }
            for(int k = 0; k < i; k++)
            {
                for(int l = 0; l < j; l++)
                {
                    if(l == 0 && flag)
                    {
                        outputStream.println("<TR>");
                    }
                    HashMap hashmap2 = ahashmap[k][l];
                    if(hashmap2 != null)
                    {
                        String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "view");
                        if(s4.length() > 0)
                        {
                            if(flag)
                            {
                                int i1 = 1 + (COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "yEnd")) - COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "yStart")));
                                int j1 = 1 + (COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "xEnd")) - COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "xStart")));
                                String s5 = "";
                                String s6 = "";
                                if(i1 > 1)
                                {
                                    s5 = " ROWSPAN=" + i1;
                                }
                                if(j1 > 1)
                                {
                                    s6 = " COLSPAN=" + j1;
                                }
                                outputStream.println("<TD" + s5 + s6 + " VALIGN=TOP ALIGN=LEFT>");
                            }
                            printCellContents(hashmap2);
                            if(flag)
                            {
                                outputStream.println("</TD>");
                            }
                        }
                    }
                    if(l == j - 1 && flag)
                    {
                        outputStream.println("</TR>");
                    }
                }

            }

            if(flag)
            {
                outputStream.println("</TABLE>");
                printFooter(outputStream);
                outputStream.println("</HTML>");
            }
        }
    }

    void printFrameBody(HashMap hashmap)
        throws java.lang.Exception
    {
        String s = COM.dragonflow.Page.portalPage.getValue(hashmap, "_contentType");
        if(s.length() == 0)
        {
            s = "text/html";
        }
        boolean flag = s.equalsIgnoreCase("text/html");
        if(flag)
        {
            super.printCGIHeader();
            String s1 = COM.dragonflow.Page.portalPage.getValue(hashmap, "_header");
            if(s1.length() > 0)
            {
                s1 = COM.dragonflow.SiteView.Portal.getViewContent(s1, request);
            }
            String s2 = COM.dragonflow.Page.portalPage.getValue(hashmap, "_framesHTML");
            if(s2.length() > 0)
            {
                s1 = s1 + COM.dragonflow.SiteView.Portal.getViewContent(s2, request);
            }
            if(s1.length() == 0)
            {
                s1 = "<HEAD><TITLE>" + COM.dragonflow.SiteView.Platform.productName + "</TITLE></HEAD>\n" + COM.dragonflow.SiteView.Portal.getViewContent("frameSet.htm", request) + "\n" + "<noframes>\n<BODY BGCOLOR=#FFFFFF><p>This page uses frames, but your browser doesn't support them.</p>" + "\n</body>\n</noframes>\n</frameset>";
            }
            if(request.getValue("debug").length() > 0)
            {
                s1 = "<HEAD><TITLE>Debugging " + COM.dragonflow.SiteView.Platform.productName + "</TITLE></HEAD>" + "<BODY BGCOLOR=#FFFFFF><TABLE WIDTH=100% BORDER=3>";
            }
            outputStream.println(s1);
        } else
        {
            COM.dragonflow.HTTP.HTTPRequest _tmp = request;
            COM.dragonflow.HTTP.HTTPRequest.printHeader(outputStream, 200, "OK", s);
        }
        super.printCGIFooter();
        outputStream.flush();
    }

    void printCellContents(HashMap hashmap)
        throws java.io.IOException
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "view");
        if(s.endsWith(".xsl"))
        {
            printXSLContents(hashmap);
        } else
        if(!s.startsWith("http"))
        {
            if(s.endsWith(".gif") || s.endsWith(".jpg"))
            {
                outputStream.print("<IMG SRC=/SiteView/templates.view/" + s + ">");
            } else
            {
                String s1 = COM.dragonflow.SiteView.Portal.getViewContent(s, request);
                if(s1.length() > 0)
                {
                    outputStream.print(s1);
                } else
                {
                    outputStream.println("Could not read view name " + s);
                }
            }
        }
    }

    void printXSLContents(HashMap hashmap)
        throws java.io.IOException
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "query");
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "focus");
        String s2 = "";
        if(s1.length() > 0)
        {
            s2 = request.getValue(s1);
        }
        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "hint");
        if(s3.length() > 0)
        {
            if(s.length() > 0)
            {
                s = s + "&";
            }
            s = s + s3;
        }
        StringBuffer stringbuffer = new StringBuffer();
        COM.dragonflow.SiteView.PortalQuery portalquery = new PortalQuery(COM.dragonflow.SiteView.PortalFilter.queryStringToMap(s), stringbuffer, request);
        String s4 = request.getValue("customquery");
        if(!s4.equalsIgnoreCase(""))
        {
            portalquery = new PortalQuery(COM.dragonflow.SiteView.PortalFilter.queryStringToMap(s4), stringbuffer, request);
        }
        if(s2.length() > 0)
        {
            COM.dragonflow.SiteView.PortalFilter portalfilter = new PortalFilter(s2);
            portalquery.addFilter(portalfilter);
        }
        portalquery.printXML();
        String s5 = stringbuffer.toString();
        String s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "view");
        if(request.getValue("debug").length() > 0)
        {
            outputStream.println("<PRE>XML query: " + s + "</PRE>");
            outputStream.println("<PRE>XML focus: " + s2 + "</PRE>");
            outputStream.println("<PRE>XSL: " + s6 + "</PRE>");
            outputStream.println("<hr><PRE>" + COM.dragonflow.Utils.TextUtils.escapeHTML(s5) + "</PRE>");
        }
        if(s6.length() > 0)
        {
            String s7 = COM.dragonflow.SiteView.Portal.getViewContent(s6, request);
            if(s7.length() > 0)
            {
                if(request.getValue("debug").length() > 0)
                {
                    outputStream.println("<hr><PRE>" + COM.dragonflow.Utils.TextUtils.escapeHTML(s7) + "</PRE>");
                }
                COM.dragonflow.Utils.XSLUtils.convert(s5, s7, outputStream, request, false);
            } else
            {
                outputStream.println("<H2>Could not find XSL file: " + s6 + "</H2>");
            }
        }
    }

    public void printCGIHeader()
    {
    }

    public void printCGIFooter()
    {
        outputStream.flush();
    }

    public void printSlideShow(String s)
    {
        outputStream.println("<meta http-equiv=\"refresh\" content=\"15; URL=http://www.dragonflow.com/products/\">\n<title>DragonFlow Products</title> \n</head> \n<body><p><br><br><br></p>\n<p>This product is no longer available but the functionality is part of other  DragonFlow products.</p><p>Please wait a moment to be redirected to the DragonFlow Web site  or click <a href=\"http://www.dragonflow.com/products/\">here</a> to  go there now.</p><table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n<tr><td height=\"100\">  </td></tr></table>\n");
    }

    public void printFooter(java.io.PrintWriter printwriter)
    {
        COM.dragonflow.Page.portalPage.printFooter(printwriter, request);
    }

    public static void printFooter(java.io.PrintWriter printwriter, String s)
    {
        COM.dragonflow.Page.portalPage.printFooter(printwriter, s, false);
    }

    public static void printFooter(java.io.PrintWriter printwriter, String s, boolean flag)
    {
        COM.dragonflow.Page.portalPage.printFooter(printwriter, s, flag, true);
    }

    public static void printFooter(java.io.PrintWriter printwriter, String s, boolean flag, boolean flag1)
    {
        String s1 = "";
        String s2 = "SiteViewlogo.gif";
        if(COM.dragonflow.SiteView.Platform.isPortal())
        {
            s2 = "SiteReliancelogo.gif";
        }
        if(s.length() > 0 && COM.dragonflow.SiteView.Platform.isSiteSeerAccount(s))
        {
            s2 = "siteseerlogo.gif";
            COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s);
            String s5 = monitorgroup.getProperty("_partner");
            if(s5.length() > 0)
            {
                COM.dragonflow.SiteView.MonitorGroup monitorgroup1 = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s5);
                if(monitorgroup1 != null)
                {
                    s1 = monitorgroup1.getProperty("_partnerCopyrightHTML");
                }
            }
        }
        String s3 = "";
        String s4 = "";
        if(flag1)
        {
            s4 = "<p><img src=/SiteView/htdocs/artwork/" + s2 + ">";
        }
        printwriter.println("<CENTER>" + s4 + "\n" + "<p><small>" + COM.dragonflow.SiteView.Platform.productName + " " + COM.dragonflow.SiteView.Platform.getVersion() + ", " + COM.dragonflow.SiteView.Platform.copyright + s1 + "</small>" + s3 + "</center>\n</BODY>");
    }

    public static void printFooter(java.io.PrintWriter printwriter, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        COM.dragonflow.Page.portalPage.printFooter(printwriter, httprequest, false);
    }

    public static void printFooter(java.io.PrintWriter printwriter, COM.dragonflow.HTTP.HTTPRequest httprequest, boolean flag)
    {
        String s = "";
        String s1 = "SiteViewlogo.gif";
        if(COM.dragonflow.SiteView.Platform.isPortal())
        {
            s1 = "SiteReliancelogo.gif";
        }
        if(httprequest != null)
        {
            if(COM.dragonflow.SiteView.Platform.isSiteSeerAccount(httprequest.getAccount()))
            {
                s1 = "siteseerlogo.gif";
            }
            String s2 = httprequest.getPermission("_partner");
            if(s2.length() > 0)
            {
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                COM.dragonflow.SiteView.MonitorGroup monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewgroup.getElement(s2);
                if(monitorgroup != null)
                {
                    s = monitorgroup.getProperty("_partnerCopyrightHTML");
                }
            }
        }
        String s3 = "<br><a href=/SiteView/license.html>SiteView End User License Agreement</a><br> ";
        printwriter.println("<CENTER><p><img src=/SiteView/htdocs/artwork/" + s1 + "><p>\n" + "<font size=-2>" + COM.dragonflow.SiteView.Platform.productName + " " + COM.dragonflow.SiteView.Platform.getVersion() + ", " + COM.dragonflow.SiteView.Platform.copyright + s + s3 + "</font>" + "</center>\n</BODY>");
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.portalPage portalpage = new portalPage();
        if(args.length > 0)
        {
            portalpage.args = args;
        }
        portalpage.handleRequest();
    }
}
