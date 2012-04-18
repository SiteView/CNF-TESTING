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
import java.io.FileOutputStream;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class mapbuilderPage extends COM.dragonflow.Page.CGI
{

    String action;
    String color1;
    String color2;
    String usenameforid;
    String serverconnecterrorxsl;
    String queryid;
    int last;
    HashMap configHash;
    HashMap mdHash;
    String version;
    String replacestr[] = {
        "\r", "\n", "&CR;", "&LF;"
    };
    String putbackstr[] = {
        "&CR;", "&LF;", "\r", "\n"
    };

    public mapbuilderPage()
    {
        queryid = "";
        version = "v1.01c";
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuildertransparentwidth");
        if(s.equals(""))
        {
            s = "1000";
        }
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuildertransparentheight");
        if(s1.equals(""))
        {
            s1 = "640";
        }
        color1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuildercolor1");
        if(color1.equals(""))
        {
            color1 = "#CCFFFF";
        }
        color2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuildercolor2");
        if(color2.equals(""))
        {
            color2 = "#CCFFCC";
        }
        String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuilderdebug");
        if(s2.equals("") || s2.equals("false"))
        {
            s2 = "false";
        }
        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuilderxslversion");
        if(s3.equals(""))
        {
            s3 = "http://www.w3.org/TR/WD-xsl";
        }
        usenameforid = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuilderusenameforid");
        if(!usenameforid.equals(""))
        {
            usenameforid = "true";
        } else
        {
            usenameforid = "false";
        }
        serverconnecterrorxsl = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mapbuilderserverconnecterrorxsl");
        if(serverconnecterrorxsl.equals(""))
        {
            serverconnecterrorxsl = "<a><xsl:attribute name=\"href\">/SiteView/cgi/go.exe/SiteView?page=portalServer";
            serverconnecterrorxsl += "</xsl:attribute><img><xsl:attribute name=\"src\">/SiteView/htdocs/artwork/alertDisable.gif</xsl:attribute></img></a>";
        }
        configHash = new HashMap();
        mdHash = new HashMap();
        mdHash = MonitorDisplayHash(mdHash);
        Enumeration enumeration = request.getVariables();
        action = request.getValue("action");
        if(action.equals("add"))
        {
            printButtonBar("mapbuilder.htm", "");
            if(request.getValue("config").equals(""))
            {
                configHash = updateHashMap(enumeration, configHash);
                printAddForm(configHash);
                String s4 = "" + request.getValue("xslname") + "_" + request.getValue("account") + ".conf";
                saveConfig(configHash, s4);
                savePages(request, configHash);
            } else
            {
                String s5 = "" + request.getValue("config");
                configHash = loadConfig(configHash, s5);
                if(!request.getValue("mymap.x").equals(""))
                {
                    configHash = updateHashMap(enumeration, configHash);
                }
                printAddForm(configHash);
                saveConfig(configHash, s5);
                savePages(request, configHash);
            }
        } else
        if(action.equals("save"))
        {
            configHash = updateHashMap(enumeration, configHash);
            printButtonBar("mapbuilder.htm", "");
            savePages(request, configHash);
        } else
        if(action.equals("view"))
        {
            String s6 = "";
            String s8 = "";
            String s9 = "";
            if(!request.getValue("config").equals(""))
            {
                String s10 = "" + request.getValue("config");
                configHash = loadConfig(configHash, s10);
                s6 = (String)configHash.get("imagemap");
                s8 = request.getValue("config");
                s9 = "page=mapbuilder&account=" + request.getAccount() + "&config=" + s8 + "&action=view&imagemap=" + s6;
                s9 = s9 + "&xslname=" + (String)configHash.get("xslname");
            } else
            {
                configHash = updateHashMap(enumeration, configHash);
                String s11 = "" + request.getValue("xslname") + "_" + request.getValue("account") + ".conf";
                saveConfig(configHash, s11);
                saveView();
                s6 = request.getValue("imagemap");
                s8 = s11;
                s9 = "page=mapbuilder&account=" + request.getAccount() + "&config=" + s8 + "&action=view&imagemap=" + s6 + "&queryid=" + queryid;
                s9 = s9 + "&xslname=" + request.getValue("xslname");
            }
            outputStream.println("<meta http-equiv=\"Expires\" content=\"0\"><meta http-equiv=\"Pragma\" content=\"no-cache\"><meta HTTP-EQUIV=\"Refresh\" CONTENT=\"60; URL=/SiteView/cgi/go.exe/SiteView?" + s9 + "\">" + "</head><body ");
            if(s6.startsWith("#"))
            {
                outputStream.println("bgcolor=");
            } else
            {
                outputStream.println("background=");
            }
            outputStream.println(s6 + ">");
            outputStream.println("<form method=post action=\"/SiteView/cgi/go.exe/SiteView\" target=myedit><input type=hidden name=page value=mapbuilder><input type=hidden name=account value=" + request.getAccount() + ">" + "<input type=hidden name=config value=" + s8 + ">" + "<input type=hidden name=queryid value=" + queryid + ">" + "<input type=hidden name=action value=add>");
            outputStream.println("<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>");
            if(request.getUserAgent().indexOf("MSIE") >= 0)
            {
                for(int i = 1; i < COM.dragonflow.Utils.TextUtils.toInt((String)configHash.get("last")); i++)
                {
                    if(!((String)configHash.get("item" + i)).equals(""))
                    {
                        outputStream.println("<div style=\"{position: absolute; left: " + (String)configHash.get("mymap.x" + i) + "px; top: " + (String)configHash.get("mymap.y" + i) + "px}\" >" + "<img src=/SiteView/htdocs/artwork/smallokay.gif><font color=red >" + i + "</font></div>");
                    } else
                    {
                        outputStream.println("<div style=\"{position: absolute; left: " + (String)configHash.get("mymap.x" + i) + "px; top: " + (String)configHash.get("mymap.y" + i) + "px}\" >" + (String)configHash.get("label" + i) + "<font color=red >" + i + "</font></div>");
                    }
                }

                outputStream.println("<div style=\"{position: absolute; left: 0px; top: 0px}\" ><input type=image name=mymap src=/SiteView/htdocs/artwork/backtransparent.gif height=" + s1 + " width=" + s + " ISMAP ><br></div>" + "<div style=\"{position: absolute; left: 780px; top: 525px}\" >" + "<a href=/SiteView/cgi/go.exe/SiteView?page=mapbuilder&account=" + request.getAccount() + "&config=" + s8 + "&action=add" + " target=myedit>Jump to edit configuration page</a></div>");
            } else
            {
                for(int j = 1; j < COM.dragonflow.Utils.TextUtils.toInt((String)configHash.get("last")); j++)
                {
                    if(!((String)configHash.get("item" + j)).equals(""))
                    {
                        outputStream.println("<layer left=" + (String)configHash.get("mymap.x" + j) + " top=" + (String)configHash.get("mymap.y" + j) + " >" + "<img src=/SiteView/htdocs/artwork/smallokay.gif><font color=red >" + j + "</font></layer>");
                    } else
                    {
                        outputStream.println("<layer  left=" + (String)configHash.get("mymap.x" + j) + " top=" + (String)configHash.get("mymap.y" + j) + " >" + (String)configHash.get("label" + j) + "<font color=red >" + j + "</font></layer>");
                    }
                }

                outputStream.println("<layer left=0 top=0> <input type=image name=mymap src=/SiteView/htdocs/artwork/backtransparent.gif height=600 width=1000 ISMAP ><br></layer><layer left=780 top=525><a href=/SiteView/cgi/go.exe/SiteView?page=mapbuilder&account=" + request.getAccount() + "&config=" + s8 + "&action=add" + " target=myedit>Jump to edit configuration page</a></layer>");
            }
            outputStream.println("</form>");
        } else
        if(action.equals("chooser"))
        {
            String s7 = "" + request.getValue("xslname") + "_" + request.getValue("account") + ".conf";
            configHash = loadConfig(configHash, s7);
            configHash = updateHashMap(enumeration, configHash);
            printButtonBar("mapbuilder.htm", "");
            printAddForm(configHash);
            saveConfig(configHash, s7);
            savePages(request, configHash);
        } else
        {
            printButtonBar("mapbuilder.htm", "");
            outputStream.println("<h2>Configure mapbuilder GUI Tool " + version + "</h2>" + "<a href=/SiteView/htdocs/mapbuilderhelp.htm >HELP</A><BR><table>" + "<form name=map method=get action=\"/SiteView/cgi/go.exe/SiteView\" >" + "<input type=hidden name=account value=" + request.getAccount() + ">" + "<input type=hidden name=page value=mapbuilder>" + "<input type=hidden name=last value=0>" + "<input type=hidden name=action value=view>" + "<input type=hidden name=queryid value=\"" + queryid + "\">" + "<tr><td><b>Address of imagemap</b></td>" + "<td><input type=text size=50 name=imagemap ></td></tr>" + "<tr><td><b>XSL Name</b></td>" + "<td><input type=text size=50 name=xslname value=defaultmapbuilder" + "></td></tr>" + "<tr><td><b>Image for Good Status</b></td>" + "<td><input type=text size=50 name=imagegood></td></tr>" + "<tr><td><b>Image for Warning Status</b></td>" + "<td><input type=text size=50 name=imagewarn></td></tr>" + "<tr><td><b>Image for Error Status</b></td>" + "<td><input type=text size=50 name=imageerror></td></tr>");
            outputStream.println("<tr><td><b>Load Config file</b></td><td>");
            printConfigSelect();
            outputStream.println("<br></td></tr></table><input type=submit value=\"submit\"><br></form>");
        }
        printFooter(outputStream);
    }

    public HashMap MonitorDisplayHash(HashMap hashmap)
    {
        try
        {
            Array array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view/xsltemplates.conf");
            if(array != null && array.size() > 0)
            {
                hashmap = (HashMap)array.get(0);
            }
        }
        catch(java.io.IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        return hashmap;
    }

    public void printMonitorDisplaySelect(String s, int i)
    {
        int j = 1;
        String s1 = "onchange=document.forms[0].label" + i + ".value=\"\";";
        outputStream.println("<select name=monitordisplay" + i + " size=1 " + s1 + " ><option value=\"\">");
        for(; mdHash.get("_name" + j) != null; j++)
        {
            outputStream.println("<option value=\"_xsltext" + j + "\"");
            if(s.equals("_xsltext" + j))
            {
                outputStream.println(" SELECTED>");
            } else
            {
                outputStream.println(">");
            }
            outputStream.println((String)mdHash.get("_display" + j));
        }

        outputStream.println("</select>");
    }

    public void printfooterSelect()
    {
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view");
        try
        {
            String as[] = file.list();
            outputStream.println("<select name=config size=1>");
            outputStream.println("<option value=\"\" ></option>");
            for(int i = 0; i < as.length; i++)
            {
                if(COM.dragonflow.Utils.TextUtils.match(as[i], "/.*footer.htm$/"))
                {
                    outputStream.println("<option value=\"" + as[i] + "\" ");
                    outputStream.println(">" + as[i] + " </option>");
                }
            }

            outputStream.println("</select>");
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void printheaderSelect()
    {
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view");
        try
        {
            String as[] = file.list();
            outputStream.println("<select name=config size=1>");
            outputStream.println("<option value=\"\" ></option>");
            for(int i = 0; i < as.length; i++)
            {
                if(COM.dragonflow.Utils.TextUtils.match(as[i], "/.*header.htm$/"))
                {
                    outputStream.println("<option value=\"" + as[i] + "\" ");
                    outputStream.println(">" + as[i] + " </option>");
                }
            }

            outputStream.println("</select>");
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void printConfigSelect()
    {
        java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view");
        String s = request.getAccount();
        try
        {
            String as[] = file.list();
            outputStream.println("<select name=config size=1>");
            outputStream.println("<option value=\"\" ></option>");
            for(int i = 0; i < as.length; i++)
            {
                if(COM.dragonflow.Utils.TextUtils.match(as[i], "/.*_" + s + ".conf$/"))
                {
                    outputStream.println("<option value=\"" + as[i] + "\" ");
                    outputStream.println(">" + as[i] + " </option>");
                }
            }

            outputStream.println("</select>");
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void printAddForm(HashMap hashmap)
    {
        outputStream.println("<form method=post action=\"/SiteView/cgi/go.exe/SiteView\" name=presentation ><input type=hidden name=action value=add><input type=submit value=\"Update\"><br>");
        outputStream.println("<table border=1 cellspacing=0><tr><th colspan=7><a href=/SiteView/cgi/go.exe/SiteView?page=mapbuilder>Start a New Mapbuilder</a>/Current Configuration:<font color=red size=+1>" + (String)hashmap.get("xslname") + "</font>   /<a href=/SiteView/cgi/go.exe/SiteView?page=mapbuilder&account=" + request.getAccount() + "&config=" + (String)configHash.get("xslname") + "_" + request.getAccount() + ".conf" + "&action=view target=mapview>" + "Go To ImageMap</a>/<a href=/SiteView/htdocs/mapbuilderhelp.htm> HELP PAGE</A></th></tr>" + "<tr><th>#</th><th>X</th><th>Y</th><th>Monitor</tH><th>Label/XSL(if assigned a monitor.)</th><th>XSL</th><th>DEL</TH>" + "</tr>");
        for(int i = 1; i < COM.dragonflow.Utils.TextUtils.toInt((String)hashmap.get("last")); i++)
        {
            String s = "";
            int j = i % 2;
            if(j > 0)
            {
                s = color1;
            } else
            {
                s = color2;
            }
            outputStream.println("<tr><td bgcolor=" + s + "><b>" + i + "</b></td><td bgcolor=" + s + ">" + "<input type=text size=3 name=mymap.x" + i + " value=\"" + (String)hashmap.get("mymap.x" + i) + "\">");
            outputStream.println("</td><td bgcolor=" + s + ">" + "<input type=text size=3 name=mymap.y" + i + " value=\"" + (String)hashmap.get("mymap.y" + i) + "\">");
            outputStream.println("</td><td bgcolor=" + s + ">" + "<A HREF=\"/SiteView/cgi/go.exe/SiteView?page=portalChooser&account=" + request.getAccount() + "&returnURL=" + "%2FSiteView%2Fcgi%2Fgo.exe%2FSiteView%3Fpage%3Dmapbuilder%26account%3D" + request.getAccount() + "%26action%3Dchooser%26itemnumber%3D" + i + "%26xslname%3D" + (String)hashmap.get("xslname") + "\">choose monitor</A>");
            String s1 = getNameforID((String)hashmap.get("item" + i));
            outputStream.println("<input type=hidden name=item" + i + " value=\"" + (String)hashmap.get("item" + i) + "\"><b>" + s1 + "</b><br> ");
            if(hashmap.get("monitordisplay" + i) != null)
            {
                printMonitorDisplaySelect((String)hashmap.get("monitordisplay" + i), i);
            } else
            {
                printMonitorDisplaySelect("", i);
            }
            outputStream.println("</td>");
            outputStream.println("<td bgcolor=" + s + "><textarea rows=2 cols=60 name=label" + i + " wordwrap=on>" + COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("label" + i), putbackstr) + "</textarea></td>");
            outputStream.println("<td bgcolor=" + s + "><input type=checkbox name=xsl" + i + " ></td>");
            outputStream.println("<td bgcolor=" + s + "><input type=checkbox name=delete" + i + " ></td></tr>");
        }

        outputStream.println("</table><input type=hidden name=page value=mapbuilder><input type=hidden name=account value=" + request.getAccount() + ">" + "<input type=hidden name=queryid value=\"" + queryid + "\">" + "<input type=hidden name=last value=" + (String)hashmap.get("last") + ">" + "<table border=0><tr><td><table border=0>" + "<tr><td>Address of Image</td><td><input type=text size=50 name=imagemap value=" + (String)hashmap.get("imagemap") + "></td></tr>" + "<tr><td>XSL Name</td><td><input type=text size=50 name=xslname value=" + (String)hashmap.get("xslname") + "></td></tr>" + "<tr><td>Image for Good Status</td><td><input type=text size=50 name=imagegood value=" + (String)hashmap.get("imagegood") + "></td></tr>" + "<tr><td>Image for Warning Status</td><td><input type=text size=50 name=imagewarn value=" + (String)hashmap.get("imagewarn") + "></td></tr>" + "<tr><td>Image for Error Status</td><td><input type=text size=50 name=imageerror value=" + (String)hashmap.get("imageerror") + "></td></tr>" + "<tr><td>Load Config file</td><td>");
        printConfigSelect();
        outputStream.println("<input type=submit value=\"Update\"></td></tr></table></td><td><b>HTML Header</b><br><textarea cols=60 rows=3 name=header>");
        if(request.getValue("header").equals("") && (hashmap.get("header") == null || ((String)hashmap.get("header")).equals("")))
        {
            writeHeader(outputStream);
        } else
        if(hashmap.get("header") != null)
        {
            outputStream.println(COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("header"), putbackstr));
        } else
        {
            outputStream.println(request.getValue("header"));
        }
        outputStream.println("</textarea><br><b>HTML Footer</b><br><textarea cols=60 rows=3 name=footer>");
        if(request.getValue("footer").equals("") && (hashmap.get("footer") == null || ((String)hashmap.get("footer")).equals("")))
        {
            writeFooter(outputStream);
        } else
        if(hashmap.get("footer") != null)
        {
            outputStream.println(COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("footer"), putbackstr));
        } else
        {
            outputStream.println(request.getValue("footer"));
        }
        outputStream.println("</textarea><br></td></tr></table>");
    }

    public void writeHeader(java.io.PrintWriter printwriter)
    {
        printwriter.println("<head>\r<title>Global SiteReliance Portal</title>\r<meta http-equiv=\"Expires\" content=\"0\">\r<meta http-equiv=\"Pragma\" content=\"no-cache\">\r<meta http-equiv=\"Refresh\" content=\"60\">\r<script language=\"JavaScript\">\r<!-- hide script from other browsers\rbrowserName = navigator.appName;\rthislocation=new String(window.location.search);\rif (browserName == \"Netscape\" && thislocation.indexOf(\"select-browser=netscape\") == -1) {\rwindow.location=window.location + \"&select-browser=netscape\";\r// stop hiding script from other browsers -->\r}\r</script>\r</head><BODY ");
        if(((String)configHash.get("imagemap")).startsWith("#"))
        {
            outputStream.println("bgcolor=");
        } else
        {
            outputStream.println("background=");
        }
        outputStream.println((String)configHash.get("imagemap") + " >");
    }

    public void writeFooter(java.io.PrintWriter printwriter)
    {
        printwriter.println("<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>");
    }

    public void writeXSLtags(HashMap hashmap, boolean flag, java.io.PrintWriter printwriter)
    {
        printwriter.println("<?xml version=\"1.0\"?>\r<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/TR/WD-xsl\">\r<xsl:template match=\"/\">");
        for(int i = 1; i <= 2; i++)
        {
            if(i == 1)
            {
                printwriter.println("<xsl:choose><xsl:when test=\"/enterprise/select-browser[. = 'netscape']\">\r");
            } else
            {
                printwriter.println("<xsl:otherwise>\r");
            }
            String s = "";
            String s1 = "";
            String s2 = "";
            String s3 = "";
            for(int j = 1; j < COM.dragonflow.Utils.TextUtils.toInt((String)hashmap.get("last")); j++)
            {
                String as[] = calculateXSLString(i, j, hashmap);
                if(as[1].equals("monitor"))
                {
                    s = s + as[0] + "\r";
                    continue;
                }
                if(as[1].equals("group"))
                {
                    s1 = s1 + as[0] + "\r";
                    continue;
                }
                if(as[1].equals("server"))
                {
                    s1 = s1 + as[0] + "\r";
                } else
                {
                    s3 = s3 + COM.dragonflow.Utils.TextUtils.replaceString(as[0], "&", "&amp;") + "\r";
                }
            }

            if(!s.equals(""))
            {
                printwriter.println("<xsl:for-each select=\"/enterprise/siteview/group/monitor\">\r" + s + "</xsl:for-each>\r");
            }
            if(!s1.equals(""))
            {
                printwriter.println("<xsl:for-each select=\"/enterprise/siteview/group\">\r" + s1 + "</xsl:for-each>\r");
            }
            if(!s2.equals(""))
            {
                printwriter.println("<xsl:for-each select=\"/enterprise/siteview/server\">\r" + s2 + "</xsl:for-each>\r");
            }
            printwriter.println(s3);
            if(i == 1)
            {
                printwriter.println("</xsl:when>\r");
            } else
            {
                printwriter.println("</xsl:otherwise></xsl:choose>\r");
            }
        }

        printwriter.println("</xsl:template>\r</xsl:stylesheet>\r");
    }

    public void saveConfig(HashMap hashmap, String s)
    {
        Array array = new Array();
        array.add(hashmap);
        try
        {
            COM.dragonflow.Properties.FrameFile.writeToFile(COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view/" + s + "", array, "", true, true);
        }
        catch(java.io.IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    public HashMap updateHashMap(Enumeration enumeration, HashMap hashmap)
    {
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s = (String)enumeration.nextElement();
            hashmap.put(s, request.getValue(s));
            if(s.startsWith("label") || s.startsWith("header") || s.startsWith("footer"))
            {
                hashmap.put(s, COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get(s), replacestr));
            }
        } while(true);
        int i = COM.dragonflow.Utils.TextUtils.toInt((String)hashmap.get("last"));
        if(!request.getValue("mymap.x").equals("0") && !request.getValue("mymap.x").equals(""))
        {
            hashmap.put("mymap.x" + i, request.getValue("mymap.x"));
            hashmap.put("mymap.y" + i, request.getValue("mymap.y"));
            hashmap.put("item" + i, "");
            hashmap.put("label" + i, "");
            hashmap.put("delete" + i, "");
            hashmap.put("xsl" + i, "");
            hashmap.put("monitordisplay" + i, "");
            hashmap.put("last", "" + (i + 1));
            i++;
        }
        if(((String)hashmap.get("last")).equals("0"))
        {
            hashmap.put("last", "1");
        }
        int j = 0;
        for(int k = 1; k < i; k++)
        {
            String s1 = "";
            if(hashmap.get("delete" + k) != null)
            {
                s1 = (String)hashmap.get("delete" + k);
                hashmap.put("delete" + k, "");
            } else
            {
                s1 = "";
            }
            if(s1.equals("on"))
            {
                j++;
                hashmap.put("last", "" + (COM.dragonflow.Utils.TextUtils.toInt((String)hashmap.get("last")) - 1));
                continue;
            }
            if(j > 0)
            {
                hashmap.put("mymap.x" + (k - j), (String)hashmap.get("mymap.x" + k));
                hashmap.put("mymap.y" + (k - j), (String)hashmap.get("mymap.y" + k));
                hashmap.put("item" + (k - j), (String)hashmap.get("item" + k));
                hashmap.put("label" + (k - j), (String)hashmap.get("label" + k));
                hashmap.put("monitordisplay" + (k - j), (String)hashmap.get("monitordisplay" + k));
                hashmap.put("mymap.x" + k, "");
                hashmap.put("mymap.y" + k, "");
                hashmap.put("item" + k, "");
                hashmap.put("label" + k, "");
                hashmap.put("monitordisplay" + k, "");
            }
        }

        i = COM.dragonflow.Utils.TextUtils.toInt((String)hashmap.get("last"));
        for(int l = 1; l < i; l++)
        {
            String s2;
            String s3;
            if(hashmap.get("monitordisplay" + l) != null && !((String)hashmap.get("monitordisplay" + l)).equals(""))
            {
                s2 = COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("monitordisplay" + l), "_xsltext", "_type");
                s3 = COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("monitordisplay" + l), "_xsltext", "_noedit");
                if(mdHash.get(s3) != null)
                {
                    s3 = (String)mdHash.get(s3);
                } else
                {
                    s3 = "";
                }
            } else
            {
                s2 = "";
                s3 = "";
            }
            if(hashmap.get("label" + l) != null && ((String)hashmap.get("label" + l)).equals("") && !s2.equals("") && ((String)mdHash.get(s2)).equals("label"))
            {
                hashmap.put("item" + l, "");
                hashmap.put("label" + l, (String)mdHash.get((String)hashmap.get("monitordisplay" + l)));
                continue;
            }
            if(hashmap.get("label" + l) != null && !s2.equals("") && ((String)mdHash.get(s2)).equals("monitor") && hashmap.get("item" + l) != null && !((String)hashmap.get("item" + l)).equals("") && hashmap.get("xsl" + l) != null && ((String)hashmap.get("xsl" + l)).equals("on") && hashmap.get("monitordisplay" + l) != null && !s3.equals("true"))
            {
                hashmap.put("label" + l, (String)mdHash.get((String)hashmap.get("monitordisplay" + l)));
            }
        }

        if(!request.getValue("item").equals(""))
        {
            hashmap.put("item" + request.getValue("itemnumber"), request.getValue("item"));
            hashmap.put("monitordisplay" + request.getValue("itemnumber"), "");
            hashmap.put("label" + request.getValue("itemnumber"), "");
        }
        return hashmap;
    }

    public HashMap loadConfig(HashMap hashmap, String s)
    {
        try
        {
            Array array = COM.dragonflow.Properties.FrameFile.readFromFile(COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view/" + s + "");
            if(array != null && array.size() > 0)
            {
                hashmap = (HashMap)array.get(0);
            }
        }
        catch(java.io.IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        return hashmap;
    }

    public String saveQuery()
    {
        String s = "";
        int i = 0;
        Array array = new Array();
        array.add(new HashMapOrdered(true));
        String s1 = COM.dragonflow.SiteView.Platform.getRoot() + "/groups/query.config";
        try
        {
            if((new File(s1)).exists())
            {
                array = COM.dragonflow.Properties.FrameFile.readFromFile(s1);
            }
        }
        catch(java.io.IOException ioexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "problem reading config file " + s1 + ": " + ioexception.getMessage());
        }
        HashMap hashmap = null;
        if(request.getValue("queryid").equals(""))
        {
            hashmap = (HashMap)array.get(0);
            s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_nextID");
            if(s.length() == 0)
            {
                s = "1";
            }
            String s2 = COM.dragonflow.Utils.TextUtils.increment(s);
            hashmap.put("_nextID", s2);
            HashMap hashmap1 = null;
            hashmap1.put("_id", s);
            hashmap1.put("_query", "property=stateString&property=category&propertySet=mainParameters&propertySet=links&property=last&property=_group&item=heidi:siteseernodes");
            hashmap1.put("_title", "testquery" + s);
            array.add(hashmap1);
        } else
        {
            i = 0;
            int j = 1;
            do
            {
                if(j >= array.size())
                {
                    break;
                }
                HashMap hashmap2 = (HashMap)array.get(j);
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_id").equals(request.getValue("queryid")))
                {
                    i = j;
                    break;
                }
                j++;
            } while(true);
            if(i >= 0)
            {
                hashmap = (HashMap)array.get(i);
            }
            hashmap.put("_id", "" + i);
            hashmap.put("_query", "property=stateString&property=category&propertySet=mainParameters&propertySet=links&property=_group&item=heidi:siteseernodes");
            hashmap.put("_title", "testupdate" + i);
        }
        try
        {
            COM.dragonflow.Properties.FrameFile.writeToFile(s1, array);
            COM.dragonflow.Log.LogManager.log("RunMonitor", "wrote out  " + s1);
        }
        catch(java.io.IOException ioexception1)
        {
            COM.dragonflow.Log.LogManager.log("Error", "problem writing config file " + s1 + ": " + ioexception1.getMessage());
        }
        COM.dragonflow.SiteView.Portal.signalReload();
        if(!s.equals(""))
        {
            return s;
        } else
        {
            return "" + i;
        }
    }

    public void saveView()
    {
        Array array = new Array();
        array.add(new HashMapOrdered(true));
        String s = COM.dragonflow.SiteView.Platform.getRoot() + "/groups/views.config";
        try
        {
            if((new File(s)).exists())
            {
                array = COM.dragonflow.Properties.FrameFile.readFromFile(s);
            }
        }
        catch(java.io.IOException ioexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "problem reading config file " + s + ": " + ioexception.getMessage());
        }
        HashMap hashmap = (HashMap)array.get(0);
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_nextID");
        if(s1.length() == 0)
        {
            s1 = "1";
        }
        String s2 = COM.dragonflow.Utils.TextUtils.increment(s1);
        hashmap.put("_nextID", s2);
        COM.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
        String s3 = request.getValue("xslname") + "_" + request.getAccount() + "_Header.htm";
        String s4 = request.getValue("xslname") + "\\_" + request.getAccount() + "\\_footer.htm";
        String s5 = request.getValue("xslname") + "\\_" + request.getAccount() + ".xsl";
        String s6 = request.getValue("xslname");
        hashmapordered.add("_cell", "view=" + s5 + " yStart=1 yEnd=1 xEnd=1 query=1 xStart=1 hint= focus=");
        hashmapordered.add("_cell", "view=" + s4 + " yStart=2 yEnd=2 xEnd=1 query=1 xStart=1 hint= focus=");
        hashmapordered.put("_title", s6);
        hashmapordered.put("_id", s1);
        hashmapordered.put("_header", s3);
        array.add(hashmapordered);
        try
        {
            COM.dragonflow.Properties.FrameFile.writeToFile(s, array);
        }
        catch(java.io.IOException ioexception1)
        {
            COM.dragonflow.Log.LogManager.log("Error", "problem writing config file " + s + ": " + ioexception1.getMessage());
        }
        COM.dragonflow.SiteView.Portal.signalReload();
    }

    public void savePages(COM.dragonflow.HTTP.HTTPRequest httprequest, HashMap hashmap)
    {
        String s = (String)hashmap.get("xslname");
        String s1 = s;
        if(s1.equals(""))
        {
            s1 = "defaultmapbuilder";
        }
        if(s.equals(""))
        {
            s = COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view/" + s1;
        } else
        {
            s = COM.dragonflow.SiteView.Platform.getRoot() + "/templates.view/" + s1;
        }
        String s2 = httprequest.getAccount();
        try
        {
            java.io.PrintWriter printwriter = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(new FileOutputStream(s + "_" + s2 + ".xsl"));
            if(httprequest.getValue("xsl").equals(""))
            {
                writeXSLtags(hashmap, true, printwriter);
            } else
            {
                printwriter.println(httprequest.getValue("xsl"));
            }
            printwriter.close();
        }
        catch(java.io.IOException ioexception)
        {
            outputStream.println("Couldn't save file:" + s + "_" + s2 + ".xsl<br>");
            ioexception.printStackTrace();
        }
        catch(java.lang.Exception exception)
        {
            outputStream.println("Couldn't save file:" + s + "_" + s2 + ".xsl<br>");
            exception.printStackTrace();
        }
        try
        {
            java.io.PrintWriter printwriter1 = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(new FileOutputStream(s + "_" + s2 + "_Header.htm"));
            if(httprequest.getValue("header").equals("") && (hashmap.get("header") == null || ((String)hashmap.get("header")).equals("")))
            {
                writeHeader(printwriter1);
            } else
            {
                printwriter1.println(COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("header"), putbackstr));
            }
            printwriter1.close();
        }
        catch(java.io.IOException ioexception1)
        {
            outputStream.println("Couldn't save file:" + s + "_" + s2 + "_Header.htm<br>");
            ioexception1.printStackTrace();
        }
        catch(java.lang.Exception exception1)
        {
            outputStream.println("Couldn't save file:" + s + "_" + s2 + "_Header.htm<br>");
            exception1.printStackTrace();
        }
        try
        {
            java.io.PrintWriter printwriter2 = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(new FileOutputStream(s + "_" + s2 + "_footer.htm"));
            if(httprequest.getValue("footer").equals("") && (hashmap.get("footer") == null || ((String)hashmap.get("footer")).equals("")))
            {
                writeFooter(printwriter2);
            } else
            {
                printwriter2.println(COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("footer"), putbackstr));
            }
            printwriter2.close();
        }
        catch(java.io.IOException ioexception2)
        {
            outputStream.println("Couldn't save file:" + s + s2 + "_footer.htm<br>");
            ioexception2.printStackTrace();
        }
        catch(java.lang.Exception exception2)
        {
            outputStream.println("Couldn't save file:" + s + "_" + s2 + "_footer.htm<br>");
            exception2.printStackTrace();
        }
        outputStream.println("Your Browser is currently: " + httprequest.getUserAgent() + "<br>" + "<a href=/SiteView/cgi/go.exe/SiteView?page=portal&view=" + (String)hashmap.get("xslname") + " target=finalview>Click here to see CentraScope View</a>");
    }

    public String[] calculateXSLString(int i, int j, HashMap hashmap)
    {
        int k = 0;
        int l = 0;
        String s = COM.dragonflow.SiteView.Platform.getVersion();
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, " ");
        s = as[0];
        as = COM.dragonflow.Utils.TextUtils.split(s, ".");
        k = COM.dragonflow.Utils.TextUtils.toInt(as[0]);
        l = COM.dragonflow.Utils.TextUtils.toInt(as[1]);
        boolean flag = false;
        if(k > 0 && l >= 5)
        {
            flag = false;
        } else
        {
            flag = true;
        }
        String as1[] = new String[2];
        String s3 = (String)hashmap.get("mymap.x" + j);
        String s4 = (String)hashmap.get("mymap.y" + j);
        String s5 = (String)hashmap.get("item" + j);
        String s6;
        String s7;
        if(i > 1)
        {
            s6 = "     <div>\r";
            s6 = s6 + "        <xsl:attribute name=\"style\">\r";
            s6 = s6 + "        {position: absolute; left: " + s3 + "px; top: " + s4 + "px}\r";
            s6 = s6 + "          </xsl:attribute>\r";
            s7 = "</div>";
        } else
        {
            s6 = "     <layer>\r";
            s6 = s6 + "        <xsl:attribute name=\"left\">\r" + s3 + "";
            s6 = s6 + "          </xsl:attribute>\r";
            s6 = s6 + "        <xsl:attribute name=\"top\">\r" + s4 + "";
            s6 = s6 + "          </xsl:attribute>\r";
            s7 = "</layer>";
        }
        if(!s5.equals(""))
        {
            COM.dragonflow.SiteView.Portal portal = COM.dragonflow.SiteView.Portal.getPortal();
            COM.dragonflow.SiteView.Portal _tmp = portal;
            String as2[] = COM.dragonflow.SiteView.Portal.getIDComponents(s5);
            String s8 = as2[0];
            String s9 = as2[1];
            String s10 = as2[2];
            String s11;
            String s12;
            String s13;
            String s14;
            String s15;
            if(!s10.equals(""))
            {
                s15 = "../../";
                s14 = "monitor";
                if(flag)
                {
                    s11 = s8 + ":" + s9;
                } else
                {
                    s11 = s9;
                }
                if(usenameforid.equals("true"))
                {
                    s12 = "_name";
                    s13 = getNameforID(s5);
                } else
                {
                    s12 = "_id";
                    s13 = s10;
                }
            } else
            if(!s9.equals("") || !s9.equals(" "))
            {
                s15 = "../";
                s14 = "group";
                if(flag)
                {
                    s11 = s8 + ":" + s9;
                } else
                {
                    s11 = s9;
                }
                if(usenameforid.equals("true"))
                {
                    s12 = "_name";
                    s13 = getNameforID(s5);
                } else
                {
                    s12 = "_id";
                    s13 = s9;
                }
            } else
            {
                s15 = "./";
                s14 = "server";
                if(flag)
                {
                    s11 = s8 + ":";
                } else
                {
                    s11 = s8;
                }
                if(usenameforid.equals("true"))
                {
                    s12 = "_name";
                    s13 = getNameforID(s5);
                } else
                {
                    s12 = "_id";
                    s13 = s8;
                }
            }
            String s16 = (String)hashmap.get("imagegood");
            String s17 = (String)hashmap.get("imagewarn");
            String s18 = (String)hashmap.get("imageerror");
            String s19 = "";
            String s20 = "";
            boolean flag1 = false;
            if(!s16.equals("") || !s17.equals("") || !s18.equals(""))
            {
                flag1 = true;
            }
            if(s16.equals(""))
            {
                s16 = "<xsl:value-of select='/enterprise/categoryArtSmall'/>";
            }
            if(s17.equals(""))
            {
                s17 = "<xsl:value-of select='categoryArt'/>";
            }
            if(s18.equals(""))
            {
                s18 = "<xsl:value-of select='categoryArt'/>";
            }
            String s21 = "";
            if(hashmap.get("label" + j) != null && !((String)hashmap.get("label" + j)).equals(""))
            {
                s21 = COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("label" + j), putbackstr);
                s21 = COM.dragonflow.Utils.TextUtils.replaceString(s21, "&", "&amp;");
            } else
            if(((String)hashmap.get("monitordisplay" + j)).equals(""))
            {
                s21 = (String)mdHash.get("_xsltext1");
            } else
            {
                s21 = (String)mdHash.get((String)hashmap.get("monitordisplay" + j));
            }
            String s22 = s21;
            String s23 = s21;
            String s26 = "";
            String s24 = COM.dragonflow.Utils.TextUtils.replaceString(s21, "<xsl:value-of select='categoryArt'/>", s16);
            String s25 = COM.dragonflow.Utils.TextUtils.replaceString(s22, "<xsl:value-of select='categoryArt'/>", s17);
            s26 = COM.dragonflow.Utils.TextUtils.replaceString(s23, "<xsl:value-of select='categoryArt'/>", s18);
            if(flag)
            {
                s19 = "groupID";
            } else
            {
                s19 = "../_id";
            }
            String s1;
            if(!s24.equals(s21) && flag1)
            {
                s1 = "<xsl:if test=\"" + s19 + "[. = '" + s11 + "']\">\r";
                if(!flag)
                {
                    s1 = s1 + "<xsl:if test=\"../../_id[. = '" + s8 + "']\">\r";
                }
                s1 = s1 + "<xsl:if test=\"" + s12 + "[. ='" + s13 + "']\">\r" + "  <xsl:choose><xsl:when test=\"" + s15 + "serverConnect[. = 'good' || . = '']\">\r" + "      <xsl:choose><xsl:when test=\"category[. = 'good']\">\r" + s6 + s24 + s7 + "      </xsl:when>\r" + "<xsl:when test=\"category[. = 'warn']\">\r" + s6 + s25 + s7 + "      </xsl:when>\r" + "      <xsl:otherwise>\r" + s6 + s26 + s7 + "         </xsl:otherwise>\r" + "        </xsl:choose>\r" + "     </xsl:when>\r" + " <xsl:otherwise>\r" + s6 + serverconnecterrorxsl + s7 + "</xsl:otherwise>\r" + "</xsl:choose>\r";
                if(!flag)
                {
                    s1 = s1 + "</xsl:if>\r";
                }
                s1 = s1 + "      </xsl:if>\r    </xsl:if>\r\r";
            } else
            if(!s24.equals(s21))
            {
                s1 = "<xsl:if test=\"" + s19 + "[. = '" + s11 + "']\">\r";
                if(!flag)
                {
                    s1 = s1 + "<xsl:if test=\"../../_id[. = '" + s8 + "']\">\r";
                }
                s1 = s1 + "<xsl:if test=\"" + s12 + "[. ='" + s13 + "']\">\r" + "  <xsl:choose><xsl:when test=\"" + s15 + "serverConnect[. = 'good' || . = '']\">\r" + "      <xsl:choose><xsl:when test=\"category[. = 'good']\">\r" + s6 + s24 + s7 + "      </xsl:when>\r" + "      <xsl:otherwise>\r" + s6 + s26 + s7 + "         </xsl:otherwise>\r" + "        </xsl:choose>\r" + "     </xsl:when>\r" + " <xsl:otherwise>\r" + s6 + serverconnecterrorxsl + s7 + "</xsl:otherwise>\r" + "</xsl:choose>\r";
                if(!flag)
                {
                    s1 = s1 + "</xsl:if>\r";
                }
                s1 = s1 + "      </xsl:if>\r    </xsl:if>\r";
            } else
            {
                s1 = "<xsl:if test=\"" + s19 + "[. = '" + s11 + "']\">\r";
                if(!flag)
                {
                    s1 = s1 + "<xsl:if test=\"../../_id[. = '" + s8 + "']\">\r";
                }
                s1 = s1 + "<xsl:if test=\"" + s12 + "[. ='" + s13 + "']\">\r" + "  <xsl:choose><xsl:when test=\"" + s15 + "serverConnect[. = 'good' || . = '']\">\r" + s6 + s24 + s7 + "     </xsl:when>\r" + " <xsl:otherwise>\r" + s6 + serverconnecterrorxsl + s7 + "</xsl:otherwise>\r" + "</xsl:choose>\r";
                if(!flag)
                {
                    s1 = s1 + "</xsl:if>\r";
                }
                s1 = s1 + "      </xsl:if>\r      </xsl:if>\r";
            }
            as1[0] = s1;
            as1[1] = s14;
        } else
        {
            String s2 = "";
            s2 = s2 + s6;
            s2 = s2 + COM.dragonflow.Utils.TextUtils.replaceString((String)hashmap.get("label" + j), putbackstr);
            s2 = s2 + s7;
            as1[0] = s2;
            as1[1] = "label";
        }
        return as1;
    }

    public String getNameforID(String s)
    {
        COM.dragonflow.SiteView.Portal portal = COM.dragonflow.SiteView.Portal.getPortal();
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = portal.getElement(s);
        String s1 = "";
        if(siteviewobject instanceof COM.dragonflow.SiteView.Monitor)
        {
            COM.dragonflow.SiteView.Monitor _tmp = (COM.dragonflow.SiteView.Monitor)siteviewobject;
            s1 = siteviewobject.getProperty(COM.dragonflow.SiteView.Monitor.pName);
        } else
        {
            s1 = s;
        }
        return s1;
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.mapbuilderPage mapbuilderpage = new mapbuilderPage();
        if(args.length > 0)
        {
            mapbuilderpage.args = args;
        }
        mapbuilderpage.handleRequest();
    }
}
