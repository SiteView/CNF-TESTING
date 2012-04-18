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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;

import COM.dragonflow.Utils.MgParser;

import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class licenseConvertPage extends COM.dragonflow.Page.CGI
{

    String CONVERT_HOST;
    String CONVERT_URL;
    String GROUPS_DIR;
    String LOGS_DIR;
    String LOG_NAME;
    private String _title;
    private String _introDescription;
    private String _introContinue;
    private String _introText;
    private String _entryText;

    public licenseConvertPage()
    {
        CONVERT_HOST = "demo.siteview.com";
        CONVERT_URL = "http://" + CONVERT_HOST + "/SiteView/cgi/go.exe/SiteView?" + "page=demoLicense" + "&operation=converted" + "&employee=automatic" + "&silent=true";
        GROUPS_DIR = "/groups/";
        LOGS_DIR = "logs";
        LOG_NAME = "monitorCount.log";
        _title = "<center>\n<h2>SiteView 7.0 License Conversion</h2>\n</center>";
        _introDescription = "\nSiteView 7.0 and subsequent SiteView releases require a new license key. \nThis page will help you convert your current key to a 7.0-compatible license key. \n<p>\nImportant things to know about converting your keys: \n<p>\n<dl>\n<dt><b>Who's eligible?</b></dt>\n<dd>License conversions are only available for licenses covered under \ncurrent Customer Care subscriptions.  Please contact SiteView Sales at \n(303) 443-2266 or by emailing <a href=mailto:siteviewsales@merc-int.com> siteviewsales@merc-int.com</a> if you are unsure about \nyour Customer Care status.</dd>\n<p>\n<dt><b>How much does it cost?</b></dt>\n<dd>All licenses covered under Customer Care subscriptions may be converted \nto new licenses for free.</dd>\n<p>\n<dd>Licenses not covered under Customer Care may also be converted.  By \nconverting a license not covered under Customer Care, you agree to renew \nyour Customer Care subscription for a year, and you will be billed for \nyour Customer Care subscription.</dd>\n<p>\n<dt><b>How does this affect my future Customer Care renewals?</b></dt>\n<dd>Please note that after converting to a new license, the Customer Care costs \nfor that license will be based upon its current value. We are happy to adjust \nyour license in order to help defray your future Customer Care costs; please \ncontact SiteView Sales at (303) 443-2266 or <a href=mailto:siteviewsales@merc-int.com> siteviewsales@merc-int.com</a> if you wish \nto explore this option.</dd>\n<p>\n<dt><b>How will my new license compare to my current license?</b></dt>\n<dd>Your new license is based entirely upon what you already own.  The size \nof your new key will be equivalent to the one you are converting.  Your \npool of unused monitor credits will not decrease in size.</dd>\n<p>\n<dt><b>How does this affect my SiteView environment?</b></dt> \n<dd>If you convert your license, you will no longer be able to use any \nversion of SiteView released prior to 7.0.  The converse is also true: \nif you do not convert your \nlicense, you will not be able to use SiteView versions 7.0 and later.</dd> \n</dl>\n<p>\n<hr>\n<p>";
        _introContinue = "<a href=\"/SiteView/cgi/go.exe/SiteView?page=licenseConvert&operation=entry\">Next >></a>";
        _introText = "\n<center>\n<table width=\"75%\" border=0>\n<tr><td>" + _introDescription + "</td></tr>" + "\n</table>" + "\n<table width=\"75%\" border=0>" + "\n<tr><td width=\"90%\"></td>" + "\n<td width=\"10%\" halign=right>" + _introContinue + "\n</td></tr>\n</table>\n</center>";
        _entryText = "This installation of SiteView has the following configuration:";
    }

    public static void main(String args[])
    {
        COM.dragonflow.Page.licenseConvertPage licenseconvertpage = new licenseConvertPage();
        licenseconvertpage.handleRequest();
    }

    public void printBody()
    {
        String s = request.getValue("operation");
        printButtonBar("", "");
        outputStream.println("\n<p>\n" + _title + "\n<p>\n");
        if(s.equals("entry"))
        {
            COM.dragonflow.Utils.MgParser mgparser = new MgParser(COM.dragonflow.SiteView.Platform.getRoot() + GROUPS_DIR);
            java.lang.Object aobj[] = mgparser.getUsageStatistics(true, true, true);
            String s4 = _buildLogFile(aobj);
            outputStream.println("\n<center>\n<table width=80%>\n<tr><td>");
            _printEntryPage(aobj);
            outputStream.println("\n</td></tr>\n<tr><td halign=right>");
            String s7 = _getLicenseKey();
            _printSubmitForm(s7, java.net.URLEncoder.encode(s4));
            if(_writeLogFile(s4, s7) < 0)
            {
                outputStream.println("\n<b>ERROR:</b> Could not write data to log file " + COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separatorChar + LOGS_DIR + java.io.File.separatorChar + LOG_NAME);
            }
            outputStream.println("</td>\n</tr></table>\n</center>");
        } else
        if(s.equals("submit"))
        {
            String s1 = request.getValue("oldLicense");
            String s2 = request.getValue("logFile");
            if(request.hasValue("now"))
            {
                _printUserInfoForm(s1, s2);
            } else
            if(request.hasValue("later"))
            {
                boolean flag = false;
                if((new File(COM.dragonflow.SiteView.Platform.getRoot() + "/" + LOGS_DIR + "/" + LOG_NAME)).exists())
                {
                    flag = true;
                }
                _printDataSavedPage(flag);
                outputStream.println("\n</td></tr>\n</table></center>");
            }
        } else
        if(s.equals("converted"))
        {
            String s3 = CONVERT_URL;
            s3 = s3 + "&oldLicense=" + request.getValue("oldLicense");
            s3 = s3 + "&contact=" + java.net.URLEncoder.encode(request.getValue("userName"));
            s3 = s3 + "&company=" + java.net.URLEncoder.encode(request.getValue("companyName"));
            s3 = s3 + "&email=" + request.getValue("emailAddr");
            s3 = s3 + "&logFile=" + request.getValue("logFile");
            long al[];
            if(!request.getValue("proxyHost").equals(""))
            {
                al = COM.dragonflow.StandardMonitor.URLMonitor.checkURL(s3);
            } else
            {
                al = COM.dragonflow.StandardMonitor.URLMonitor.checkURL(s3, request.getValue("proxyHost"), request.getValue("proxyUser"), request.getValue("proxyPass"));
            }
            if(al[0] == 200L)
            {
                String s5 = _stripHTMLEncoding(request.getValue("logFile"));
                String s8 = "The following data was submitted by the automatic license converter.\n\nContact Email: " + request.getValue("emailAddr") + "\nContact Name: " + request.getValue("userName") + "\nCompany: " + request.getValue("companyName") + "\nOld License: " + request.getValue("oldLicense") + "\n\nLog File: " + "\n" + s5 + "\nEND" + "\n" + "\nemail=" + request.getValue("emailAddr") + "";
                COM.dragonflow.Utils.MailUtils.mail(getMasterConfig(), "siteviewsales@merc-int.com", "License Conversion Data submitted", s8);
                _printSubmittedPage();
            } else
            {
                String s6 = _stripHTMLEncoding(request.getValue("logFile"));
                String s9 = "This customer's attempt to convert a license failed (failure was " + COM.dragonflow.StandardMonitor.URLMonitor.lookupStatus(al[0]) + ").  They expect to be " + "contacted within 2 business days for assistance." + "\n" + "\nContact Email: " + request.getValue("emailAddr") + "\nContact Name: " + request.getValue("userName") + "\nCompany: " + request.getValue("companyName") + "\nOld License: " + request.getValue("oldLicense") + "\n\nLog File: " + "\n" + s6 + "\nEND" + "\n" + "\nemail=" + request.getValue("emailAddr") + "";
                COM.dragonflow.Utils.MailUtils.mail(getMasterConfig(), "siteviewsales@merc-int.com", "Sales Attention Required: Automatic Conversion attempt failed", s9);
                _printFailedPage(al[0]);
            }
        } else
        {
            _printIntroPage();
        }
        outputStream.println("\n<p><p>\n");
        printFooter(outputStream);
    }

    private void _printIntroPage()
    {
        String s = "";
        s = s + _introText;
        outputStream.println(s);
    }

    private void _printSubmittedPage()
    {
        String s = "\n<center><table width=\"75%\">\n<tr><td><font size=+1>License Email Sent</font></td></tr>\n<tr><td>&nbsp;</td></tr>\n<tr><td>Your configuration data was successfully submitted.  Please check your \nemail account for your new license key.</td></tr>\n<tr><td>&nbsp;</td></tr>\n<tr><td halign=center><a href=\"SiteView/htdocs/SiteView.html\">Exit Conversion \nProgram (return to SiteView)</td></tr>\n</table></center>";
        outputStream.println(s);
    }

    private void _printFailedPage(long l)
    {
        String s = "\n<center><table width=\"75%\">\n<tr><td>We were unable to complete your automatic conversion (error: " + COM.dragonflow.StandardMonitor.URLMonitor.lookupStatus(l) + ")." + "\n<p>" + "\nAn email has been sent to dragonflow on your behalf containing your conversion " + "\ndata.  We will contact you " + "\nwithin the next 2 business days at the email address you provided with your new " + "\nlicense key.  Please feel free to contact us in the meantime at (303) 443-2266 " + "\nor by sending email to siteviewsales@merc-int.com." + "\n</td></tr>" + "\n<tr><td>&nbsp;</td></tr>" + "\n<tr><td halign=center><a href=\"SiteView/htdocs/SiteView.html\">Exit Conversion " + "\nProgram (return to SiteView)</td></tr>" + "\n</table></center>";
        outputStream.println(s);
    }

    private void _printDataSavedPage(boolean flag)
    {
        String s = "";
        s = "\nRemember, you'll need to convert your license to run SiteView 7.0 permanently.\nIf you haven't yet decided whether to upgrade, please feel free to download the latest version of \nSiteView to use its 10-day built-in evaluation.  SiteView can be downloaded \nfrom <a href=http://support.dragonflow.com/>http://support.dragonflow.com/</a>.\n<p>\nIf you have questions about converting your licenses, please contact SiteView \nSales by calling (303) 443-2266 or by emailing siteviewsales@merc-int.com. \n<p>";
        if(flag)
        {
            s = s + "\nYour conversion data has been saved at \n<b><tt>" + COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separatorChar + LOGS_DIR + java.io.File.separatorChar + LOG_NAME + "</tt></b>.  Please include this file when sending email about " + "\nlicense conversions to help us assist you more quickly.";
        }
        outputStream.println(s);
    }

    private void _printEntryPage(java.lang.Object aobj[])
    {
        String s = "";
        s = s + _entryText + "\n<p>\n";
        s = s + "\n<ul>\n";
        s = s + "\n<li>license key: " + _getLicenseKey();
        s = s + "\n<li>points used: " + aobj[0] + "</li>";
        s = s + "\n<li>monitors used: " + aobj[1] + "</li>";
        s = s + "\n<li>total URL Sequence steps: " + aobj[3] + "</li>";
        s = s + "\n<li>total URL List Items: " + aobj[4] + "</li>";
        s = s + "\n<p>\n<li>monitor types used: \n";
        s = s + "<ul>";
        HashMap hashmap = (HashMap)aobj[2];
        for(Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements();)
        {
            String s1 = (String)enumeration.nextElement();
            s = s + "\n<li>" + s1 + ": " + hashmap.get(s1) + " monitor";
            if((new Integer((String)hashmap.get(s1))).intValue() > 1)
            {
                s = s + "s";
            }
            s = s + "</li>\n<br>";
        }

        s = s + "\n</ul>\n</ul>\n<p>\n";
        outputStream.println(s);
    }

    private String _buildLogFile(java.lang.Object aobj[])
    {
        String s = "";
        String s1 = "\r\n";
        s = s + "START" + s1;
        s = s + "Date: ";
        java.util.Date date = new Date();
        s = s + date.toString();
        s = s + s1;
        s = s + "Total monitors: ";
        s = s + aobj[1].toString();
        s = s + s1;
        s = s + "Total URLSequence Steps: ";
        s = s + aobj[3].toString();
        s = s + s1;
        s = s + "Total URLList Items: ";
        s = s + aobj[4].toString();
        s = s + s1;
        s = s + "Total Points Used: ";
        s = s + aobj[0].toString();
        s = s + s1;
        s = s + s1;
        String s2 = date.toString() + ": ";
        if(aobj[2] != null)
        {
            HashMap hashmap = (HashMap)aobj[2];
            for(Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements();)
            {
                String s3 = (String)enumeration.nextElement();
                String s4 = (String)hashmap.get(s3);
                s = s + s3 + ": " + s4;
                s = s + s1;
                s2 = s2 + s3.substring(0, 1).toUpperCase();
                s2 = s2 + s4;
            }

        }
        s = s + s1;
        s = s + COM.dragonflow.Utils.TextUtils.obscure(s2);
        return s;
    }

    private int _writeLogFile(String s, String s1)
    {
        try
        {
            java.io.FileWriter filewriter = new FileWriter(COM.dragonflow.SiteView.Platform.getRoot() + "/" + LOGS_DIR + "/" + LOG_NAME);
            java.io.BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            bufferedwriter.write("Old License: " + s1 + "\r\n\r\n");
            bufferedwriter.write(s);
            bufferedwriter.write("\r\nEND");
            bufferedwriter.flush();
            filewriter.flush();
            bufferedwriter.close();
            filewriter.close();
        }
        catch(java.io.IOException ioexception)
        {
            outputStream.println(ioexception.toString());
            return -1;
        }
        return 0;
    }

    private int _writeLogFile(String s, String s1, String s2, String s3, String s4)
    {
        try
        {
            java.io.FileWriter filewriter = new FileWriter(COM.dragonflow.SiteView.Platform.getRoot() + "/" + LOGS_DIR + "/" + LOG_NAME);
            java.io.BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            bufferedwriter.write("Contact Name: " + s1 + "\r\n" + "Company: " + s2 + "\r\n" + "Email Address: " + s3 + "\r\n" + "Old License: " + s4 + "\r\n\r\n");
            bufferedwriter.write(s);
            bufferedwriter.write("\r\nEND");
            bufferedwriter.flush();
            filewriter.flush();
            bufferedwriter.close();
            filewriter.close();
        }
        catch(java.io.IOException ioexception)
        {
            outputStream.println(ioexception.toString());
            return -1;
        }
        return 0;
    }

    private void _sendData(String s, String s1, String s2, String s3, String s4)
    {
        String s5 = CONVERT_URL + "&oldLicense=" + s + "&contact=" + s1 + "&company=" + s2 + "&email=" + s3 + "&logFile=" + s4 + "&silent=true";
        java.lang.System.out.println("url: " + s5);
        COM.dragonflow.StandardMonitor.URLMonitor.checkURL(s5);
    }

    private String _getEndUserHTML()
    {
        String s = "\n<p>\n<form name=userinfo action=\"/SiteView/cgi/go.exe/SiteView?page=licenseConvert&operation=entry method=POST\">\n<table>\n<tr><td>Your Name: </td>\n<td><input type=text name=userName size=50></td></tr>\n<tr><td>Your Company: </td>\n<td><input type=text name=companyName size=50></td></tr>\n<tr><td>Your Email Address: </td>\n<td><input type=text name=emailAddr size=50></td></tr>\n<tr><td><input type=submit value=\"Submit\"></td></tr>\n</table>\n<input type=hidden name=page value=licenseConvert>\n<input type=hidden name=operation value=entry>\n</form>";
        return s;
    }

    private void _printSubmitForm(String s, String s1)
    {
        String s2 = "\n<p>\n<hr>\n<p>\nIf you are ready to convert your license, click the \"Get License Now\" button.\n<p>Otherwise, select the \"Get License Later\" button.  Your configuration data\nwill not be sent to dragonflow and your license will not be converted at this time.\n<p>\n<form name=submitData action=\"/SiteView/cgi/go.exe/SiteView\" method=POST>\n<table>\n<tr><td><input type=submit value=\"Get License Now\" name=now></td>\n<td><input type=submit value=\"Get License Later\" name=later></td>\n</tr></table>\n<input type=hidden name=page value=licenseConvert>\n<input type=hidden name=operation value=submit>\n<input type=hidden name=oldLicense value=" + s + ">" + "\n<input type=hidden name=logFile value=" + s1 + ">" + "\n</form>";
        outputStream.println(s2);
    }

    private void _printUserInfoForm(String s, String s1)
    {
        String s2 = "<form name=\"sendData\" action=\"/SiteView/cgi/go.exe/SiteView\" method=POST>\n<input type=hidden name=oldLicense value=" + s + ">" + "\n<input type=hidden name=logFile value=" + s1 + ">" + "\n<input type=hidden name=operation value=converted>" + "\n<input type=hidden name=page value=licenseConvert>" + "\n<center>\n<table width=\"75%\">" + "\n<tr><td>We'll need a little more information from you to convert your license. " + "\nThis is just so we know who to contact if we have any questions. " + "\n<p>" + "\n<table>" + "\n<tr><td>What is your name? </td>" + "\n<td><input type=text name=userName size=50></td></tr>" + "\n<tr><td>What is the name of the company that owns this license? </td>" + "\n<td><input type=text name=companyName size=50></td></tr>" + "\n<tr><td>What is your email address? </td>" + "\n<td><input type=text name=emailAddr size=50></td></tr>" + "\n<tr><td>&nbsp;</td></tr>" + "\n<tr><td><input type=submit value=\"Convert This License\" name=convertIt></td></tr>" + "\n</table>" + "\n<p>" + "\nThe information you saw on the previous page about your SiteView configuration " + "\nand the three fields you entered above is the only data that will be sent" + "\nto dragonflow to complete your license conversion." + "\n<p>" + "\nIf your network requires that you use a proxy for HTTP connections, enter your " + "\nproxy information in the fields below." + "\n<p>" + "\n<table>" + "\n<tr><td>(optional) HTTP Proxy name and port" + "\n<br>(for example, myproxy.mydomain.com:3128)</td>" + "\n<td><input type=text name=proxyHost size=50></td></tr>" + "\n<tr><td>(optional) Proxy Username</td>" + "\n<td><input type=text name=proxyUser size=50></td></tr>" + "\n<tr><td>(optional) Proxy Password</td>" + "\n<td><input type=text name=proxyPass size=50></td></tr>" + "\n</table>" + "\n</td></tr>\n</table>" + "\n</form>";
        outputStream.println(s2);
    }

    private String _getLicenseKey()
    {
        return _getLicenseKey(COM.dragonflow.SiteView.MasterConfig.getMasterConfig());
    }

    private String _getLicenseKey(HashMap hashmap)
    {
        String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_license");
        if(s.equals("") || s.startsWith("EX"))
        {
            s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_licenseOld");
        }
        return s;
    }

    private String _stripHTMLEncoding(String s)
    {
        java.util.StringTokenizer stringtokenizer = new StringTokenizer(s, "+");
        String s1;
        for(s1 = ""; stringtokenizer.hasMoreElements(); s1 = s1 + " ")
        {
            s1 = s1 + stringtokenizer.nextElement();
        }

        int i;
        while((i = s1.indexOf("%0D%0A")) >= 0) 
        {
            String s2 = s1.substring(0, i);
            String s6 = s1.substring(i + 6);
            s1 = s2 + "\r\n" + s6;
        }
        while((i = s1.indexOf("%3A")) >= 0) 
        {
            String s3 = s1.substring(0, i);
            String s7 = s1.substring(i + 3);
            s1 = s3 + ":" + s7;
        }
        while((i = s1.indexOf("%28")) >= 0) 
        {
            String s4 = s1.substring(0, i);
            String s8 = s1.substring(i + 3);
            s1 = s4 + "(" + s8;
        }
        while((i = s1.indexOf("%29")) >= 0) 
        {
            String s5 = s1.substring(0, i);
            String s9 = s1.substring(i + 3);
            s1 = s5 + ")" + s9;
        }
        return s1;
    }
}
