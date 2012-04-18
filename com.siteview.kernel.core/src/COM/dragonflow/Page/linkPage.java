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

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Utils.URLInfo;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.algorithms.Sorting;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class linkPage extends COM.dragonflow.Page.CGI
{

    static java.util.Hashtable mimeTypeMap;

    public linkPage()
    {
    }

    public static String lookupMimeTypeString(String s)
    {
        String s1 = (String)mimeTypeMap.get(s);
        if(s1 == null)
        {
            s1 = s;
        }
        return s1;
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        String s = "Link Check Summary Report";
        String s1 = request.getValue("file");
        int i = COM.dragonflow.Utils.URLInfo.sortType(request.getValue("sort"));
        printBodyHeader(s);
        printButtonBar("LinkSum.htm", "");
        if(s1.length() == 0)
        {
            outputStream.print("<HR><H3>Error retrieving link check information</H3><P>\nA data file was not specified<P><HR>\n");
        } else
        {
            String s2 = "";
            String s3 = "";
            java.io.FileInputStream fileinputstream = null;
            java.io.BufferedReader bufferedreader = null;
            Array array = new Array();
            boolean flag = true;
            int j = 0;
            try
            {
                fileinputstream = new FileInputStream(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "logs" + java.io.File.separator + request.getValue("file"));
                bufferedreader = COM.dragonflow.Utils.FileUtils.MakeInputReader(fileinputstream);
                String s4 = bufferedreader.readLine();
                if(s4 != null)
                {
                    s3 = COM.dragonflow.Utils.TextUtils.prettyDate(COM.dragonflow.Utils.TextUtils.stringToDate(s4));
                }
                while((s4 = bufferedreader.readLine()) != null) 
                {
                    if(s4.equals("DONE"))
                    {
                        flag = false;
                    } else
                    {
                        COM.dragonflow.Utils.URLInfo urlinfo = new URLInfo();
                        urlinfo.initialize(s4);
                        if(urlinfo.status != -1 && urlinfo.status != 200)
                        {
                            j++;
                        }
                        array.add(urlinfo);
                    }
                }
            }
            catch(java.io.IOException ioexception)
            {
                s2 = "Could not read summary data: " + ioexception.getMessage();
            }
            finally
            {
                try
                {
                    if(bufferedreader != null)
                    {
                        bufferedreader.close();
                    }
                    if(fileinputstream != null)
                    {
                        fileinputstream.close();
                    }
                }
                catch(java.io.IOException ioexception1) { }
            }
            if(s2.length() == 0)
            {
                Sorting.sort(array, new URLInfo(i));
                String s5 = "/SiteView/cgi/go.exe/SiteView?page=link&file=" + s1 + "&account=" + request.getAccount();
                boolean flag1 = request.getValue("show").length() > 0;
                String s6 = "";
                if(flag1)
                {
                    s6 = "&show=all";
                }
                String s7 = "showing error links, <a href=" + s5 + "&sort=" + request.getValue("sort") + "&show=all>show all links</a>";
                if(flag1)
                {
                    s7 = "showing all links, <a href=" + s5 + "&sort=" + request.getValue("sort") + ">show only error links</a>";
                }
                String s8 = "checked at " + s3;
                if(flag)
                {
                    s = "Link Check In Progress, Partial Report";
                    s8 = "started at " + s3;
                }
                outputStream.print("<p><CENTER><H2>" + s + "</H2><P>\n" + "<small>(" + array.size() + " links, " + j + " errors, " + s8 + ", " + s7 + ")</small></CENTER><p>\n" + "<TABLE WIDTH=\"100%\" BORDER=1 cellspacing=0><TR>" + "<TH><A HREF=" + s5 + s6 + "&sort=status>Status</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=size>Size (K bytes)</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=time>Time (secs)</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=modemTime>Est. 28.8 time (secs)</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=type>Type</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=url>URL</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=source>Source Page</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=external>External</A></TH>\n" + "<TH><A HREF=" + s5 + s6 + "&sort=count>Count</A></TH>\n" + "</TR>");
                if(j == 0 && !flag1)
                {
                    outputStream.println("<TR><TD COLSPAN=9 align=center>no link errors</TD></TR>\n");
                } else
                {
                    Enumeration enumeration = (Enumeration) array.iterator();
                    do
                    {
                        if(!enumeration.hasMoreElements())
                        {
                            break;
                        }
                        COM.dragonflow.Utils.URLInfo urlinfo1 = (COM.dragonflow.Utils.URLInfo)enumeration.nextElement();
                        String s9 = "";
                        String s10 = "-";
                        String s11 = "&nbsp;";
                        String s12 = "&nbsp;";
                        String s13 = "&nbsp;";
                        String s14 = "&nbsp;";
                        String s15 = "&nbsp";
                        String s16 = "&nbsp;";
                        if(flag1 || urlinfo1.status != 200 && urlinfo1.status != -1)
                        {
                            if(urlinfo1.status != -1)
                            {
                                s16 = "" + urlinfo1.count;
                                s15 = "no";
                                if(urlinfo1.externalLink)
                                {
                                    s15 = "yes";
                                }
                                if(urlinfo1.status != 200)
                                {
                                    s9 = "BGCOLOR=#ff6666";
                                }
                                s10 = COM.dragonflow.SiteView.Monitor.lookupStatus(urlinfo1.status);
                                s11 = urlinfo1.contentType;
                                s14 = COM.dragonflow.Utils.TextUtils.floatToString((float)urlinfo1.size / 3000F, 2);
                                if(urlinfo1.duration != -1L)
                                {
                                    s12 = COM.dragonflow.Utils.TextUtils.floatToString((float)urlinfo1.duration / 1000F, 2);
                                }
                                s13 = COM.dragonflow.Utils.TextUtils.floatToString((float)urlinfo1.size / 1000F, 2);
                            }
                            outputStream.print("<TR><TD " + s9 + ">" + s10 + "</TD>\n" + "<TD>" + s13 + "</TD>\n" + "<TD>" + s12 + "</TD>\n" + "<TD>" + s14 + "</TD>\n" + "<TD>" + s11 + "</TD>\n" + "<TD><A HREF=\"" + urlinfo1.url + "\" target=\"child\">" + urlinfo1.url + "</A></TD>" + "<TD><A HREF=\"" + urlinfo1.source + "\" target=\"child\">" + urlinfo1.source + "</A></TD>" + "<TD>" + s15 + "</TD>" + "<TD>" + s16 + "</TD>" + "</TR>\n");
                        }
                    } while(true);
                }
                outputStream.println("</TABLE><P>");
            } else
            {
                outputStream.print("<HR><H3>Error retrieving link check information</H3><P>\n" + s2 + "<P>" + "<HR>\n");
            }
        }
        printFooter(outputStream);
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.linkPage linkpage = new linkPage();
        if(args.length > 0)
        {
            linkpage.args = args;
        }
        linkpage.handleRequest();
    }

    static 
    {
        mimeTypeMap = new Hashtable();
        mimeTypeMap.put("text/html", "HTML");
        mimeTypeMap.put("text/plain", "Text");
        mimeTypeMap.put("image/gif", "GIF Image");
        mimeTypeMap.put("image/jpeg", "JPEG Image");
        mimeTypeMap.put("image/tiff", "TIFF Image");
    }
}
