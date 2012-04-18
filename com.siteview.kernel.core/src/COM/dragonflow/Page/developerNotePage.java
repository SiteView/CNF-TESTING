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
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class developerNotePage extends COM.dragonflow.Page.CGI
{

    private static String NOTE_CACHE_FILENAME = COM.dragonflow.SiteView.Platform.getRoot() + "/htdocs/developerNoteList.html";

    public developerNotePage()
    {
    }

    void printMessageForm(String s, String s1)
        throws java.io.IOException
    {
        java.lang.Object obj = null;
        if(request.isPost())
        {
            StringBuffer stringbuffer = new StringBuffer();
            obj = new HashMapOrdered(true);
            ((HashMap) (obj)).put("title", request.getValue("title"));
            if(COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "title").length() == 0)
            {
                COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer, "title missing");
            }
            ((HashMap) (obj)).put("product", request.getValue("product"));
            String s3 = "";
            for(Enumeration enumeration = request.getValues("topic"); enumeration.hasMoreElements();)
            {
                String s5 = (String)enumeration.nextElement();
                if(s3.length() > 0)
                {
                    s3 = s3 + ",";
                }
                s3 = s3 + s5;
            }

            ((HashMap) (obj)).put("topics", s3);
            if(COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "topics").length() == 0)
            {
                COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer, "at least one topic required");
            }
            ((HashMap) (obj)).put("body", request.getValue("body"));
            if(COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "body").trim().length() == 0)
            {
                COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer, "body of note missing");
            }
            ((HashMap) (obj)).put("weight", request.getValue("weight"));
            ((HashMap) (obj)).put("releases", request.getValue("releases"));
            ((HashMap) (obj)).put("author", request.getValue("author"));
            ((HashMap) (obj)).put("id", request.getValue("id"));
            ((HashMap) (obj)).put("updated", COM.dragonflow.Utils.TextUtils.prettyDateDate(COM.dragonflow.Utils.TextUtils.prettyDate(COM.dragonflow.SiteView.Platform.timeMillis())));
            String s6 = request.getValue("idnum");
            if(s6.length() == 0)
            {
                HashMap hashmap1 = getMasterConfig();
                String s9 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_developerNoteNextID");
                int i = COM.dragonflow.Utils.TextUtils.toInt(s9);
                if(i < 10000)
                {
                    i = 10000;
                }
                for(java.io.File file2 = new File(s1 + "noteDN" + i + ".htm"); file2.exists(); file2 = new File(s1 + "noteDN" + i + ".htm"))
                {
                    i++;
                }

                s6 = "DN" + i;
                i++;
                hashmap1.put("_developerNoteNextID", "" + i);
                saveMasterConfig(hashmap1);
            }
            ((HashMap) (obj)).put("idnum", s6);
            ((HashMap) (obj)).put("filename", "note" + s6 + ".htm");
            String s8 = s1 + "noteInternalTemplate.txt";
            String s10 = COM.dragonflow.Utils.FileUtils.readFile(s8).toString();
            if(s10.length() == 0)
            {
                COM.dragonflow.Utils.TextUtils.addToBuffer(stringbuffer, "could not find template " + s8);
            }
            if(stringbuffer.length() > 0)
            {
                outputStream.println("<HR><H2>" + stringbuffer.toString() + "</H2><HR><P>");
            } else
            {
                SiteViewMain.SupportNoteUtils.writeNoteFile(s1, ((HashMap) (obj)), s10);
                java.io.File file1 = new File(NOTE_CACHE_FILENAME);
                if(file1.exists())
                {
                    file1.delete();
                }
                printRefreshPage("/SiteView/cgi/go.exe/SiteView?page=developerNote", 0);
                return;
            }
        }
        String s2 = s + " Developer Note";
        printBodyHeader(s2);
        if(obj == null)
        {
            String s4 = request.getValue("idnum");
            if(s4.length() > 0)
            {
                java.io.File file = new File(s1 + "note" + s4 + ".htm");
                if(file.exists())
                {
                    obj = SiteViewMain.SupportNoteUtils.readNote(file);
                } else
                {
                    outputStream.println("<HR><H3>Could not read note file " + file + "</H3><HR><P>");
                }
            }
        }
        if(obj == null)
        {
            obj = new HashMapOrdered(true);
        }
        HashMap hashmap = new HashMap();
        for(Enumeration enumeration1 = (Enumeration) ((HashMap) (obj)).values("topic"); enumeration1.hasMoreElements(); hashmap.put(enumeration1.nextElement(), "true")) { }
        String s7 = "";
        Array array = COM.dragonflow.Properties.FrameFile.readFromFile(s1 + "topics.txt");
        for(Enumeration enumeration2 = (Enumeration) array.iterator(); enumeration2.hasMoreElements();)
        {
            HashMap hashmap2 = (HashMap)enumeration2.nextElement();
            String s11 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "id");
            String s13 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "title");
            String s15 = "";
            if(hashmap.get(s11) != null)
            {
                s15 = " selected";
            }
            s7 = s7 + "<option " + s15 + " value=\"" + s11 + "\">" + s13 + "\n";
        }

        Array array1 = new Array();
        array1.add("common");
        array1.add("common");
        array1.add("frequent");
        array1.add("frequent");
        array1.add("normal");
        array1.add("normal");
        array1.add("infrequent");
        array1.add("infrequent");
        array1.add("rare");
        array1.add("rare");
        String s12 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "weight");
        if(s12.length() == 0)
        {
            s12 = "normal";
        }
        String s14 = COM.dragonflow.Page.developerNotePage.getOptionsHTML(array1, s12);
        Array array2 = new Array();
        array2.add("SiteView,SiteSeer");
        array2.add("SiteView and SiteSeer");
        array2.add("SiteSeer");
        array2.add("SiteSeer only");
        array2.add("SiteView");
        array2.add("SiteView all platforms");
        array2.add("SiteView NT");
        array2.add("SiteView NT");
        array2.add("SiteView Unix");
        array2.add("SiteView Unix");
        array2.add("CentraScope");
        array2.add("CentraScope");
        String s16 = COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "product");
        if(s16.length() == 0)
        {
            s16 = "SiteView,SiteSeer";
        }
        String s17 = COM.dragonflow.Page.developerNotePage.getOptionsHTML(array2, s16);
        outputStream.println("<p><H2>" + s2 + "</H2>");
        outputStream.println("Use this form to add a note to the developer note database.  Every night, the developernote index is updated on the internal web site.<p>");
        outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST><input type=hidden name=page value=developerNote><input type=hidden name=account value=" + request.getAccount() + ">" + "<BLOCKQUOTE><DL>" + "<DT><B>Title:</B> <input type=text name=title size=80 value=\"" + COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "title") + "\"><BR>" + "<DD><P>" + "<DT><B>Note applies to:</B> <select name=product>" + s17 + "</select>" + "<DD><P>" + "<DT><B>Topics</B>" + "<DD><TABLE>" + "<tr><td><select name=topic size=10 multiple>" + s7 + "</select>" + "</TABLE>" + "<DT>This is a <select name=weight>" + s14 + "</select> question\n" + "<DD><P>" + "<DT><B>Author:</B> <input type=text name=author size=50 value=\"" + COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "author") + "\"><BR>" + "<DD>Your name so that people who have more questions can contact you<P>" + "<DT><B>Releases:</B> <input type=text name=releases size=50 value=\"" + COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "releases") + "\"><BR>" + "<DD>enter one or more releases that this note applies to<P>" + "</DL></BLOCKQUOTE>" + "<B>Notes:</B>" + "<textarea cols=80 rows=12 name=body>" + COM.dragonflow.Utils.TextUtils.escapeHTML(COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "body")) + "</textarea><BR>" + "<input type=hidden name=idnum value=\"" + COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "idnum") + "\">\n" + "<input type=hidden name=id value=\"" + COM.dragonflow.Utils.TextUtils.getValue(((HashMap) (obj)), "id") + "\">\n" + "<input type=hidden name=operation value=\"" + s + "\">\n" + "<P><input type=submit name=send value=\"Save Developer Note\"><P>" + "</FORM>");
        printFooter(outputStream);
    }

    void printDeleteForm(String s)
    {
        String s1 = request.getValue("idnum");
        if(request.isPost())
        {
            try
            {
                java.io.File file = new File(s + "note" + s1 + ".htm");
                file.delete();
                java.io.File file2 = new File(NOTE_CACHE_FILENAME);
                if(file2.exists())
                {
                    file2.delete();
                }
                printRefreshPage("/SiteView/cgi/go.exe/SiteView?page=developerNote", 0);
            }
            catch(java.lang.Exception exception)
            {
                printError("There was a problem deleting the note.", exception.toString(), "/SiteView/cgi/go.exe/SiteView?page=developerNote");
            }
        } else
        {
            java.io.File file1 = new File(s + "note" + s1 + ".htm");
            HashMap hashmap = SiteViewMain.SupportNoteUtils.readNote(file1);
            outputStream.println("<FONT SIZE=+1>Are you sure you want to delete developer note <B>" + s1 + "</B>" + " regarding &quot;" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "title") + "&quot;?</FONT>" + "<p><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>" + "<INPUT TYPE=HIDDEN NAME=operation VALUE=\"Delete\">" + "<INPUT TYPE=HIDDEN NAME=page VALUE=developerNote>" + "<INPUT TYPE=HIDDEN NAME=idnum VALUE=\"" + s1 + "\">" + "<TABLE WIDTH=100% BORDER=0><TR>" + "<TD WIDTH=6%></TD><TD WIDTH=41%><input type=submit VALUE=\"Delete Developer Note\"></TD>" + "<TD WIDTH=6%></TD><TD ALIGN=RIGHT WIDTH=41%><A HREF=/SiteView/cgi/go.exe/SiteView?page=developerNote>Return to Developer Note List</A></TD><TD WIDTH=6%></TD>" + "</TR></TABLE></FORM>");
            printFooter(outputStream);
        }
    }

    public void printList(String s)
    {
        printBodyHeader("Developer Notes");
        String s1 = "";
        try
        {
            s1 = COM.dragonflow.Utils.FileUtils.readFile(NOTE_CACHE_FILENAME).toString();
        }
        catch(java.io.IOException ioexception) { }
        if(s1.length() == 0)
        {
            Array array = SiteViewMain.SupportNoteUtils.readNotes(s);
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append("<H2>Developer Notes</H2><HR>\n");
            stringbuffer.append("<P><A HREF=/SiteView/cgi/go.exe/SiteView?page=developerNote&operation=Add>Add</A> new developer note<P>\n");
            stringbuffer.append("<TABLE><TR><TH>ID<TH>Edit<TH>Title<TH>Author<TH>Delete</TR>\n");
            for(int i = array.size() - 1; i >= 0; i--)
            {
                HashMap hashmap = (HashMap)array.get(i);
                stringbuffer.append("<TR><TD>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "idnum"));
                stringbuffer.append("<TD><A HREF=/SiteView/cgi/go.exe/SiteView?page=developerNote&operation=Edit&idnum=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "idnum") + ">Edit</A>\n");
                stringbuffer.append("<TD><A HREF=/SiteView/cgi/go.exe/SiteView?page=developerNote&operation=Display&idnum=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "idnum") + ">" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "title") + "</A>\n");
                stringbuffer.append("<TD>" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "author") + "\n");
                stringbuffer.append("<TD><A HREF=/SiteView/cgi/go.exe/SiteView?page=developerNote&operation=Delete&idnum=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "idnum") + ">Delete</A></TR>\n");
            }

            stringbuffer.append("</TABLE>\n");
            s1 = stringbuffer.toString();
            try
            {
                COM.dragonflow.Utils.FileUtils.writeFile(NOTE_CACHE_FILENAME, s1);
            }
            catch(java.io.IOException ioexception1) { }
        }
        outputStream.println(s1);
        printFooter(outputStream);
    }

    void printDisplayPage(String s)
    {
        String s1 = request.getValue("idnum");
        String s2 = s + "note" + s1 + ".htm";
        try
        {
            outputStream.print(COM.dragonflow.Utils.FileUtils.readFile(s2));
        }
        catch(java.io.IOException ioexception)
        {
            outputStream.println("<HR>Could not read " + s2);
        }
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_developer"))
        {
            throw new HTTPRequestException(557);
        }
        HashMap hashmap = getMasterConfig();
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_developerNoteSourcePath");
        if(s.length() == 0)
        {
            s = "F:/developersite/notes/";
        }
        String s1 = request.getValue("operation");
        if(s1.length() == 0)
        {
            s1 = "List";
        }
        if(s1.equals("List"))
        {
            printList(s);
        } else
        if(s1.equals("Delete"))
        {
            printDeleteForm(s);
        } else
        if(s1.equals("Display"))
        {
            printDisplayPage(s);
        } else
        {
            printMessageForm(s1, s);
        }
    }

    public static void main(String args[])
    {
        COM.dragonflow.Page.developerNotePage developernotepage = new developerNotePage();
        if(args.length > 0)
        {
            developernotepage.args = args;
        }
        developernotepage.handleRequest();
    }

}
