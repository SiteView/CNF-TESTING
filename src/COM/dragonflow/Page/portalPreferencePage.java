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

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class portalPreferencePage extends COM.dragonflow.Page.CGI
{

    public portalPreferencePage()
    {
    }

    String getTitle()
    {
        return "generic title";
    }

    String getPageName()
    {
        return "generic";
    }

    String getHelpPage()
    {
        return "CentraPrefs.htm";
    }

    void printEditForm(String s, jgl.Array array, jgl.HashMap hashmap)
        throws java.lang.Exception
    {
        String s1 = request.getValue("id");
        jgl.HashMap hashmap1;
        if(s.startsWith("Edit"))
        {
            hashmap1 = COM.dragonflow.Page.portalPreferencePage.findFrameByID(array, s1);
        } else
        {
            hashmap1 = new HashMap();
        }
        printEditForm(s, hashmap1, hashmap);
    }

    void printEditForm(String s, jgl.HashMap hashmap, jgl.HashMap hashmap1)
        throws java.lang.Exception
    {
        String s1 = s;
        if(s.startsWith("Edit"))
        {
            s1 = "Update ";
        } else
        {
            s1 = "Add ";
        }
        String s2 = s1 + getTitle();
        printBodyHeader(s2);
        printButtonBar(getHelpPage(), "");
        outputStream.println("<p><H2>" + s2 + "</H2>\n");
        outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=page value=" + getPageName() + ">\n" + "<input type=hidden name=account value=" + request.getAccount() + ">\n" + "<input type=hidden name=operation value=" + s + ">\n" + "<input type=hidden name=id value=" + request.getValue("id") + ">\n");
        printBasicProperties(hashmap, hashmap1);
        outputStream.println("<P><TABLE WIDTH=100%><TR><TD><input type=submit value=\"" + s1 + "\"> " + getTitle() + "\n" + "</TD></TR></TABLE>");
        if(hasAdvancedOptions())
        {
            outputStream.println("<br><h3>Advanced Options</h3><TABLE>");
            printAdvancedProperties(hashmap, hashmap1);
            outputStream.println("</TABLE>");
            outputStream.println("<TABLE WIDTH=100%><TR><TD><input type=submit value=\"" + s1 + "\"> Server\n" + "</TD></TR></TABLE>");
            outputStream.println("</FORM>");
        }
        printFooter(outputStream);
    }

    void printBasicSiteSeerProperties(jgl.HashMap hashmap, jgl.HashMap hashmap1)
        throws java.io.IOException
    {
    }

    void printBasicProperties(jgl.HashMap hashmap, jgl.HashMap hashmap1)
        throws java.io.IOException
    {
    }

    void printAdvancedProperties(jgl.HashMap hashmap, jgl.HashMap hashmap1)
        throws java.io.IOException
    {
    }

    boolean hasAdvancedOptions()
    {
        return false;
    }

    void printListHeader()
    {
    }

    void printListItem(jgl.HashMap hashmap)
    {
    }

    public static void printHeadTag(java.io.PrintWriter printwriter, String s, String s1, String s2)
    {
        printwriter.println("<HEAD>\n" + COM.dragonflow.Page.CGI.nocacheHeader + s2 + s1 + "<TITLE>" + s + "</TITLE>\n</HEAD>");
    }

    public static jgl.HashMap findFrameByID(jgl.Array array, String s)
    {
        int i = COM.dragonflow.Page.portalPreferencePage.findFrameIndexByID(array, s);
        if(i >= 0)
        {
            return (jgl.HashMap)array.at(i);
        } else
        {
            return null;
        }
    }

    public static int findFrameIndexByID(jgl.Array array, String s)
    {
        return COM.dragonflow.Page.portalPreferencePage.findFrameIndex(array, "_id", s, 1);
    }

    public static jgl.HashMap findFrame(jgl.Array array, String s, String s1, int i)
    {
        int j = COM.dragonflow.Page.portalPreferencePage.findFrameIndex(array, s, s1, i);
        if(j >= 0)
        {
            return (jgl.HashMap)array.at(j);
        } else
        {
            return null;
        }
    }

    public static int findFrameIndex(jgl.Array array, String s, String s1, int i)
    {
        for(int j = i; j < array.size(); j++)
        {
            jgl.HashMap hashmap = (jgl.HashMap)array.at(j);
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, s).equals(s1))
            {
                return j;
            }
        }

        return -1;
    }

    void verify(jgl.HashMap hashmap, jgl.HashMap hashmap1)
    {
    }

    jgl.HashMap getResultFrame()
    {
        return fillInResultFrame(new HashMapOrdered(true));
    }

    jgl.HashMap fillInResultFrame(jgl.HashMap hashmap)
    {
        return hashmap;
    }

    void preProcessAdd(jgl.HashMap hashmap)
    {
    }

    void postProcessAdd(jgl.HashMap hashmap)
    {
    }

    boolean preProcessDelete(jgl.HashMap hashmap, StringBuffer stringbuffer)
    {
        return true;
    }

    void postProcessDelete(jgl.HashMap hashmap)
    {
    }

    String getConfigFilePath()
    {
        return "";
    }

    void setUniqueID(jgl.HashMap hashmap, jgl.HashMap hashmap1)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_nextID");
        if(s.length() == 0)
        {
            s = "1";
        }
        String s1 = COM.dragonflow.Utils.TextUtils.increment(s);
        hashmap.put("_nextID", s1);
        hashmap1.put("_id", s);
    }

    void printAddForm(String s)
        throws java.lang.Exception
    {
        String s1 = request.getAccount();
        String s2 = request.user.getProperty("_preference");
        if(!s1.equals("administrator") && !s2.equals("true"))
        {
            outputStream.println("<hr>Access Permission Error.<hr>");
            return;
        }
        jgl.Array array = new Array();
        array.add(new HashMapOrdered(true));
        try
        {
            String s3 = getConfigFilePath();
            if((new File(s3)).exists())
            {
                array = COM.dragonflow.Properties.FrameFile.readFromFile(s3);
            }
            if(array.size() == 0)
            {
                array.add(new HashMapOrdered(true));
            }
        }
        catch(java.io.IOException ioexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "problem reading config file " + getConfigFilePath() + ": " + ioexception.getMessage());
        }
        if(request.isPost())
        {
            jgl.HashMap hashmap = null;
            if(s.startsWith("Add"))
            {
                hashmap = getResultFrame();
                array.add(hashmap);
                jgl.HashMap hashmap2 = (jgl.HashMap)array.at(0);
                setUniqueID(hashmap2, hashmap);
                postProcessAdd(hashmap);
            } else
            {
                String s4 = request.getValue("id");
                hashmap = COM.dragonflow.Page.portalPreferencePage.findFrameByID(array, s4);
                if(hashmap != null)
                {
                    fillInResultFrame(hashmap);
                } else
                {
                    throw new Exception(getTitle() + " id (" + s4 + ") could not be found");
                }
            }
            jgl.HashMap hashmap3 = new HashMap();
            verify(hashmap, hashmap3);
            if(hashmap3.size() == 0)
            {
                COM.dragonflow.Properties.FrameFile.writeToFile(getConfigFilePath(), array);
                COM.dragonflow.SiteView.Portal.signalReload();
                printRefreshPage("/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount(), 0);
            } else
            {
                printEditForm(s, hashmap, hashmap3);
            }
        } else
        {
            jgl.HashMap hashmap1 = new HashMap();
            preProcessAdd(hashmap1);
            printEditForm(s, array, hashmap1);
        }
    }

    void printListForm(String s)
        throws java.io.IOException
    {
        String s1 = request.getAccount();
        String s2 = request.user.getProperty("_preference");
        if(!s1.equals("administrator") && !s2.equals("true"))
        {
            outputStream.println("<hr>Access Permission Error.<hr>");
            return;
        }
        String s3 = getTitle();
        printBodyHeader(s3);
        printButtonBar(getHelpPage(), "");
        outputStream.println("<p><H2>" + s3 + " List</H2><TABLE WIDTH=100% BORDER=2>");
        printListHeader();
        jgl.Array array = readListFrames();
        if(array.size() < 2)
        {
            outputStream.println("<TR><TD>no " + s3 + "s</TD></TR>");
        } else
        {
            for(int i = 1; i < array.size(); i++)
            {
                jgl.HashMap hashmap = (jgl.HashMap)array.at(i);
                printListItem(hashmap);
            }

        }
        outputStream.println();
        outputStream.println("</TABLE><BR>");
        printListFooter();
        printListOperations();
        printFooter(outputStream);
    }

    jgl.Array readListFrames()
    {
        jgl.Array array = null;
        try
        {
            array = COM.dragonflow.Properties.FrameFile.readFromFile(getConfigFilePath());
        }
        catch(java.io.IOException ioexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "could not read configuration file " + getConfigFilePath() + ": " + ioexception.getMessage());
            array = new Array();
        }
        return array;
    }

    void printListOperations()
    {
        outputStream.println("<A HREF=" + getPageLink(getPageName(), "Add") + ">Add</A> " + getTitle() + "\n");
    }

    void printListFooter()
    {
    }

    void printDeleteForm(String s)
        throws java.lang.Exception
    {
        String s1 = request.getAccount();
        String s2 = request.user.getProperty("_preference");
        if(!s1.equals("administrator") && !s2.equals("true"))
        {
            outputStream.println("<hr>Access Permission Error.<hr>");
            return;
        }
        String s3 = request.getValue("id");
        jgl.Array array = COM.dragonflow.Properties.FrameFile.readFromFile(getConfigFilePath());
        jgl.HashMap hashmap = COM.dragonflow.Page.portalPreferencePage.findFrameByID(array, s3);
        String s4 = COM.dragonflow.Page.portalPreferencePage.getValue(hashmap, "_title");
        if(request.isPost())
        {
            try
            {
                array.remove(hashmap);
                COM.dragonflow.Properties.FrameFile.writeToFile(getConfigFilePath(), array);
                postProcessDelete(hashmap);
                COM.dragonflow.SiteView.Portal.signalReload();
                printRefreshPage("/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount(), 0);
            }
            catch(java.lang.Exception exception)
            {
                printError("There was a problem deleting the server.", exception.toString(), "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
            }
        } else
        {
            printBodyHeader("Delete Confirmation");
            printButtonBar(getHelpPage(), "");
            StringBuffer stringbuffer = new StringBuffer();
            boolean flag = preProcessDelete(hashmap, stringbuffer);
            if(flag)
            {
                outputStream.println("<FONT SIZE=+1>Are you sure you want to remove the " + getTitle() + " <B>" + s4 + "</B>?</FONT>" + "<p><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>" + "<input type=hidden name=page value=" + getPageName() + ">" + "<input type=hidden name=operation value=\"" + s + "\">" + "<input type=hidden name=id value=\"" + s3 + "\">" + "<input type=hidden name=account value=" + request.getAccount() + ">" + "<TABLE WIDTH=100% BORDER=0><TR>" + "<TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit value=\"" + s + "\"></TD>" + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%><A HREF=/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount() + ">Return to Detail</A></TD><TD WIDTH=6%></TD>" + "</TR></TABLE></FORM>");
            } else
            {
                outputStream.println("<FONT SIZE=+1>" + getTitle() + " <B>" + s4 + " cannot be deleted</B></FONT><HR>\n" + "Being used by:<P><UL>\n" + stringbuffer.toString() + "</UL><P>" + "<A HREF=/SiteView/cgi/go.exe/SiteView?page=" + getPageName() + "&operation=List&account=" + request.getAccount() + ">Return to Detail</A></TD><TD WIDTH=6%></TD>");
            }
            printFooter(outputStream);
        }
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        if(s.length() == 0)
        {
            s = "List";
        }
        if(s.equals("List"))
        {
            printListForm(s);
        } else
        if(s.startsWith("Add"))
        {
            printAddForm(s);
        } else
        if(s.equals("Delete"))
        {
            printDeleteForm(s);
        } else
        if(s.startsWith("Edit"))
        {
            printAddForm(s);
        } else
        {
            printOtherForm(s);
        }
    }

    public void printOtherForm(String s)
        throws java.lang.Exception
    {
        printError("The link was incorrect", "unknown operation", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
    }

    public static void main(String args[])
    {
        (new portalPreferencePage()).handleRequest();
    }
}
