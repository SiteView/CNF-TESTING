// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.Utils.FileUtils;

import java.io.*;
import jgl.Array;

public class UpdateDocs
{

    public UpdateDocs()
    {
    }

    public static void main(String args[])
        throws IOException
    {
        String s = "";
        int i = 0;
        Array array = new Array();
        pdf = false;
        if(args.length > 0 && (args[0].equals("-?") || args[0].equals("/?") || args[0].equals("-help")))
        {
            System.out.println("UpdateDocs SiteSeer Opens Platform.root + 'SeerTOC.txt' (list of HTML files and values)\nUpdateDocs -pdf     Makes PDF-ready format from HTML file input (input for HTMLDOC program)\nUpdateDocs -a       Opens Platform.root + '/docs'and adds HTML files to list\nUpdateDocs          Builds all.htm or SiteViewUG.htm\nUpdateDocs -trim    Perform DeleteBetweenTags from files that use <!--onlineStart--> comment sets\n");
            System.exit(0);
        }
        if(i < args.length)
            if(args[i].equals("-a"))
            {
                File file = new File(Platform.getRoot() + "\\docs");
                String args1[] = file.list();
                for(int j = 0; j < args1.length; j++)
                    if(args1[j].indexOf(".htm") != -1)
                        array.add(args1[j]);

                i++;
            } else
            if(args[i].equals("-pdf"))
            {
                pdf = true;
                pageBreakHTML = "<!-- NEED 5in -->";
                i++;
            } else
            if(args[i].equals("-trim"))
            {
                i++;
                if(args[i].length() > 0)
                {
                    trim = true;
                    String s1 = args[i];
                    PrintDoc(s1);
                    System.exit(0);
                } else
                {
                    System.err.println("missing file name argv for -trim");
                }
                System.exit(0);
            } else
            {
                System.err.println("\nno command arg given " + args[i]);
            }
        s = siteviewTOC;
        StringBuffer stringbuffer = FileUtils.readFile(Platform.getRoot() + "\\docs\\" + s);
        if(stringbuffer == null)
        {
            System.err.println("Error reading from the file: " + Platform.getRoot() + "\\docs\\" + s);
            System.exit(0);
        }
        String s2 = stringbuffer.toString();
        int k = 0;
        String s3 = "a href=\"";
        do
        {
            int l = s2.indexOf(s3, k);
            if(l == -1)
                break;
            l += s3.length();
            k = s2.indexOf("\"", l);
            if(k == -1)
                break;
            String s4 = s2.substring(l, k);
            if(s4.indexOf("UGtocshort.") == -1 && s4.indexOf("ReleaseNote") == -1 && s4.indexOf("nodoc.") == -1 && s4.indexOf("all") == -1 && s4.indexOf("ScopeUG") == -1)
                array.add(s4);
        } while(true);
        System.out.println("<HTML><HEAD><TITLE>SiteView User Guide</TITLE>");
        System.out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"siteviewUG.css\">");
        System.out.println("</HEAD><BODY BGCOLOR=\"#FFFFFF\">");
        if(!pdf && s.length() > 0)
            PrintTOC(s);
        for(int i1 = 0; i1 < array.size(); i1++)
        {
            String s5 = (String)array.at(i1);
            if(!(new File(Platform.getRoot() + "\\docs\\" + s5)).isDirectory() && s5.indexOf("http") < 0 && (s5.endsWith(".htm") || s5.endsWith(".html")))
                PrintDoc(s5);
        }

        System.out.println("</BODY></HTML>");
        System.exit(0);
    }

    static void PrintTOC(String s)
    {
        try
        {
            StringBuffer stringbuffer = FileUtils.readFile(Platform.getRoot() + "\\docs\\" + s);
            String s1 = stringbuffer.toString();
            s1 = MungeTOC(s1);
            s1 = Munge(s1, pdf);
            System.out.println(s1);
            System.out.println(pageBreakHTML);
        }
        catch(IOException ioexception)
        {
            System.out.println(ioexception + " TOC file not found, check the path in the code");
        }
    }

    static void PrintDoc(String s)
        throws IOException
    {
        File file = new File(s);
        StringBuffer stringbuffer = new StringBuffer();
        if(trim)
        {
            File file1 = new File(Platform.getRoot() + "\\" + s);
            if(file1.exists())
                stringbuffer = FileUtils.readFile(Platform.getRoot() + "\\" + s);
            else
                System.err.println("File not found: " + Platform.getRoot() + "\\" + s);
            String s1 = stringbuffer.toString();
            String s3 = DeleteBetweenTags(s1, "<!--onlineStart-->", "<!--onlineEnd-->");
            System.out.println(s3);
        } else
        {
            System.out.println("<a name=\"" + s + "\">&nbsp;</a>");
            File file2 = new File(Platform.getRoot() + "\\docs\\" + s);
            if(file2.exists())
                stringbuffer = FileUtils.readFile(Platform.getRoot() + "\\docs\\" + s);
            else
                System.err.println("File not found: " + Platform.getRoot() + "\\docs\\" + s);
            String s2 = stringbuffer.toString();
            s2 = Munge(s2, pdf);
            if(s.indexOf("UserGuide.htm") != -1)
                s2 = MungeUserGuide(s2);
            System.out.println(s2);
            System.out.println(pageBreakHTML);
        }
    }

    static String MungeTOC(String s)
    {
        s = DeleteBetweenTags(s, "<!--onlineStart-->", "<!--onlineEnd-->");
        String s1 = "<a href=\"";
        do
        {
            int i = s.indexOf("<a href=\"");
            if(i == -1)
                break;
            int j = i + s1.length();
            int k = s.indexOf("\">", j);
            if(k == -1)
                break;
            String s2 = s.substring(j, k);
            int l = s2.indexOf("#");
            if(l != -1)
                s2 = s2.substring(0, l);
            if(s2.indexOf("all.htm") != -1)
            {
                k = s.indexOf("</A>", k);
                s = s.substring(0, i - 1) + s.substring(k + 5);
            } else
            {
                s = s.substring(0, i) + "<A  HREF=\"#" + s2 + "\">" + s.substring(k + 2);
            }
        } while(true);
        return s;
    }

    static String MungeUserGuide(String s)
    {
        int i = s.indexOf("java/");
        if(i != -1)
            s = s.substring(0, i) + "../" + s.substring(i);
        i = s.indexOf("docs/SiteViewUG.htm");
        if(i != -1)
            s = s.substring(0, i) + s.substring(i + 5);
        i = 0;
        String s1 = "classes/";
        do
        {
            i = s.indexOf(s1, i);
            if(i != -1)
            {
                s = s.substring(0, i) + "../" + s.substring(i);
                i += s1.length();
            } else
            {
                return s;
            }
        } while(true);
    }

    static void MungeCustom(String s)
        throws IOException
    {
        String s1 = Platform.getRoot() + "\\docs\\" + s;
        System.out.println("file: " + s1);
        StringBuffer stringbuffer;
        String s2;
        try
        {
            File file = new File(s1);
            if(!file.canRead())
                return;
        }
        catch(Exception ioexception)
        {
            return;
        }
        stringbuffer = FileUtils.readFile(Platform.getRoot() + "\\docs\\" + s);
        s2 = stringbuffer.toString();
        s2 = DeleteBetweenTags(s2, "<a href=", ">");
        s2 = DeleteBetweenTags(s2, "</a", ">");
        FileUtils.writeFile(Platform.getRoot() + "\\docs\\" + s, s2);
    }

    static String Munge(String s, boolean flag)
        throws IOException
    {
        int i = s.indexOf("docs/images");
        if(i != -1)
            s = s.substring(0, i) + s.substring(i + 5);
        do
        {
            int j = s.indexOf("\"UGtoc.htm");
            if(j != -1)
            {
                s = s.substring(0, j) + "\"../" + s.substring(j + 1);
            } else
            {
                s = DeleteBetweenTags(s, "<!--pdfStart-->", "<!--pdfEnd-->");
                s = DeleteBetweenTags(s, "<!--pdfStart-->", "<!--pdfFoot-->");
                s = DeleteBetweenTags(s, "<!--onlineStart-->", "<!--onlineEnd-->");
                s = DeleteBetweenTags(s, "<head>", "</head>");
                s = DeleteBetweenTags(s, "<HEAD>", "</HEAD>");
                s = DeleteBetweenTags(s, "<BODY", ">");
                s = DeleteBetweenTags(s, "<body", ">");
                s = DeleteBetweenTags(s, "<!doctype", ">");
                s = DeleteBetweenTags(s, "<!DOCTYPE", ">");
                s = DeleteTag(s, "<HTML>");
                s = DeleteTag(s, "<html>");
                s = DeleteTag(s, "</HTML>");
                s = DeleteTag(s, "</html>");
                s = DeleteTag(s, "<BODY>");
                s = DeleteTag(s, "<body>");
                s = DeleteTag(s, "</BODY>");
                s = DeleteTag(s, "</body>");
                s = DeleteTag(s, "<HR><P>");
                return s;
            }
        } while(true);
    }

    static String DeleteBetweenTags(String s, String s1, String s2)
    {
        do
        {
            int i = s.indexOf(s1);
            if(i == -1)
                break;
            int j = s.indexOf(s2, i + s1.length());
            if(j == -1)
                break;
            j += s2.length();
            s = s.substring(0, i) + s.substring(j);
        } while(true);
        return s;
    }

    static String DeleteTag(String s, String s1)
    {
        int i = s.indexOf(s1);
        if(i != -1)
        {
            int j = i + s1.length();
            s = s.substring(0, i) + s.substring(j);
        }
        return s;
    }

    static String pageBreakHTML = "<!-- HALF PAGE -->";
    static String coverPage = "images\\SScopeUGCover.htm";
    static String siteviewTOC = "UGtoc.htm";
    static boolean pdf = false;
    static boolean trim = false;

}