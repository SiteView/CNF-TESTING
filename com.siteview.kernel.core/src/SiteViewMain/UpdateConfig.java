// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:21
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import COM.dragonflow.SiteView.*;
import COM.dragonflow.Utils.*;

import java.io.*;
import java.util.Vector;
import jgl.HashMap;

public class UpdateConfig
{

    public UpdateConfig()
    {
    }

    public static void main(String args[])
    {
        int i = 0;
        String s = "none";
        if(args.length == 1)
            s = "";
        else
            s = args[i++];
        String s1 = args[i++];
        String s2 = "";
        if(args.length > 2)
            s2 = args[i];
        try
        {
            Health.mergeConfig();
        }
        catch(Exception exception)
        {
            System.out.println("Error merging health.config.  Health may not operate properly");
        }
        String s3 = "localhost";
        try
        {
            if(!s.equals("build"))
            {
                HashMap hashmap = MasterConfig.getMasterConfig();
                s3 = updateWebserverAddress(hashmap);
                if(s.length() != 0)
                    hashmap.put("_autoEmail", s);
                if(s1.length() != 0 && hashmap.get("_httpPort") == null)
                    hashmap.put("_httpPort", s1);
                if(s2.length() != 0)
                    hashmap.put("_haInstall", s2);
                if(TextUtils.getValue(hashmap, "_version").equals(""))
                    moveOldLicense(hashmap);
                MasterConfig.saveMasterConfig(hashmap);
            }
        }
        catch(Exception exception1)
        {
            System.out.println("UpdateConfig: " + exception1);
        }
        updateFiles("http", s1, s3, "", "");
        System.exit(0);
    }

    public static String updateWebserverAddress(HashMap hashmap)
    {
        String s = (String)hashmap.get("_webserverAddress");
        if(s == null)
        {
            s = Platform.getDefaultIPAddress();
            hashmap.put("_webserverAddress", s);
        }
        return s;
    }

    public static void updateFiles(String s, String s1, String s2, String s3, String s4)
    {
        String s5 = s + "://" + s2 + ":" + s1 + "/SiteView";
        String s6 = "/Open SiteView.htm";
        String s7 = Platform.productName;
        if(Platform.isPortal())
            s6 = "/Open CentraScope.htm";
        FileReplace(Platform.getRoot() + "/README.htm", "<!--LINK1-->", "<A HREF=\"" + s5 + "\" TARGET=MAIN>Open " + s7 + "</A>");
        FileReplace(Platform.getRoot() + "/README.htm", "<!--LINK2-->", s5);
        FileReplace(Platform.getRoot() + "/README.htm", "<!--LINK3-->", s1);
        if(Platform.isWindows())
        {
            FileReplace(Platform.getRoot() + s6, "<!--LINK1-->", "<A HREF=\"" + s5 + "\" TARGET=MAIN>Open " + s7 + "</A>");
            FileReplace(Platform.getRoot() + s6, "<!--LINK2-->", s5);
            FileReplace(Platform.getRoot() + s6, "<!--LINK3-->", s1);
        }
        FileReplace(Platform.getRoot() + "/admin.htm", "<!--LINK1-->", "<meta HTTP-EQUIV=\"Refresh\" CONTENT=\"0; URL=" + s5 + "/htdocs/SiteView.html\">");
        FileReplace(Platform.getRoot() + "/user.htm", "<!--LINK1-->", "<meta HTTP-EQUIV=\"Refresh\" CONTENT=\"0; URL=" + s5 + "/userhtml/SiteView.html\">");
        if(s4.length() > 0)
            FileUtils.copyFile(Platform.getRoot() + "/user.htm", s4);
        if(s3.length() > 0)
            FileUtils.copyFile(Platform.getRoot() + "/admin.htm", s3);
    }

    public static void FileReplace(String s, String s1, String s2)
    {
        try
        {
            Vector vector = new Vector();
            BufferedReader bufferedreader = FileUtils.MakeInputReader(new FileInputStream(s));
            do
            {
                String s3 = bufferedreader.readLine();
                if(s3 == null)
                    break;
                int i = s3.indexOf(s1);
                if(i != -1)
                {
                    int k = s3.indexOf(s1, i + s1.length());
                    if(k != -1)
                        s3 = s3.substring(0, i + s1.length()) + s2 + s3.substring(k);
                }
                vector.addElement(s3);
            } while(true);
            bufferedreader.close();
            PrintWriter printwriter = FileUtils.MakeOutputWriter(new FileOutputStream(s));
            for(int j = 0; j < vector.size(); j++)
                printwriter.println(vector.elementAt(j));

            printwriter.close();
        }
        catch(Exception exception)
        {
            System.out.println("FileReplace: " + s + ", " + exception);
        }
    }

    public static HashMap moveOldLicense(HashMap hashmap)
    {
        int i = Platform.versionAsNumber;
        String s = TextUtils.numberToString(i);
        int j = 0;
        String s1 = "";
        s1 = TextUtils.getValue(hashmap, "_version");
        if(!s1.equals("") && s1.length() > 0)
            j = TextUtils.toInt(s1);
        if(j == 0)
        {
            if(!TextUtils.getValue(hashmap, "_license").equals(""))
                hashmap.put("_licenseOld", TextUtils.getValue(hashmap, "_license"));
            hashmap.put("_license", "");
            hashmap.put("_installTime", "" + TextUtils.timeSeconds());
            hashmap.put("_version", s);
            hashmap = manageConverterLink(hashmap);
        }
        return hashmap;
    }

    public static HashMap manageConverterLink(HashMap hashmap)
    {
        String s = TextUtils.getValue(hashmap, "_mainHTML");
        String s1 = TextUtils.getValue(hashmap, "_headerHTML");
        String s2 = TextUtils.getValue(hashmap, "_licenseOld");
        String s3 = TextUtils.getValue(hashmap, "_license");
        if(!s2.equals(""))
            if(!LUtils.getLicenseErrorString(s3).equals("") || LUtils.getLicenseType(s3) == 0)
            {
                if(s.indexOf(convertPage) < 0)
                {
                    String s4 = s + currentLink;
                    hashmap.put("_mainHTML", s4);
                }
            } else
            {
                String s5;
                for(s5 = s; s5.indexOf(currentLink) >= 0; s5 = removeLink(s5, currentLink));
                for(; s5.indexOf(oldLink) >= 0; s5 = removeLink(s5, oldLink));
                if(s5.indexOf(convertPage) >= 0)
                {
                    hashmap.put("_mainHTMLold", s5);
                    s5 = "";
                }
                hashmap.put("_mainHTML", s5);
                String s6;
                for(s6 = s1; s6.indexOf(oldLink) >= 0; s6 = removeLink(s6, oldLink));
                if(s6.indexOf(convertPage) >= 0)
                {
                    hashmap.put("_headerHTMLold", s6);
                    s6 = "";
                }
                hashmap.put("_headerHTML", s6);
            }
        return hashmap;
    }

    private static String removeLink(String s, String s1)
    {
        int i = s.indexOf(s1);
        String s2 = "";
        if(s.length() == s1.length())
            return s2;
        if(i == 0)
            s2 = s.substring(s1.length() - 1);
        else
        if(s1.length() + i < s.length())
        {
            String s3 = s.substring(0, i);
            String s4 = s.substring(i + s1.length());
            s2 = s3 + s4;
        } else
        if(i == -1)
            s2 = "";
        else
            s2 = s.substring(0, i);
        return s2;
    }

    static String convertPage;
    static String currentLink;
    static String oldLink;

    static 
    {
        convertPage = "/SiteView/cgi/go.exe/SiteView?page=licenseConvert";
        currentLink = "<tr><td><center><font size=+1><b><a href=" + convertPage + ">Convert your license to a SiteView 7.0 License " + "Key</a></b></font></center></td></tr>" + "<tr><td>&nbsp;</td></tr>";
        oldLink = "<p><center><a href=\"" + convertPage + "\">SiteView 7.0 Licenses</a></center><p>";
    }
}