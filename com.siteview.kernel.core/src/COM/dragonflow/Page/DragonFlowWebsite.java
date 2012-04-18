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

// Referenced classes of package COM.dragonflow.Page:
// CGI

public abstract class DragonFlowWebsite extends COM.dragonflow.Page.CGI {

    public DragonFlowWebsite() {
    }

    void printHeader(HashMap hashmap) {
        String s = COM.dragonflow.Page.DragonFlowWebsite.getValue(
                hashmap, "_partnerRegistrationHeader");
        if (s.length() == 0) {
            s = getToolbar();
        }
        outputStream
                .println("<head>\n<title>DragonFlow software information as well as services offered by DragonFlow at dragonflow.com</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"/SiteView/docs/artwork_ssr/css/styles.css\" />\n<script type=\"text/javascript\">\nif(document.layers) document.write('<link rel=\"stylesheet\" type=\"text/css\" href=\"/SiteView/docs/artwork_ssr/css/styles_ns4.css\" />'); \nvar ansiteurl = '/SiteView/docs/artwork_ssr/';\n</script>\n<script src=\"/SiteView/docs/artwork_ssr/javascript/mainlib.js\" type=\"text/javascript\"></script>\n</head>\n");
        printBodyInfo();
        outputStream.println(s);
        outputStream.flush();
    }

    public String getToolbar() {
        String s = "<!-- toolbar-->\n<tr>\n<td valign=\"top\">\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"toolbar\">\n<form name=\"searchshort\" action=\"javascript:void(0);\" method=\"post\">\n<tr>\n <td width=\"99%\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"24\" alt=\"\" border=\"0\" /></td>\n <td nowrap><a href=\"http://support.dragonflow.com\" onmouseover=\"changeImages('tbsupport','/SiteView/docs/artwork_ssr/images/navigation/toolbar/support_over.gif')\" onmouseout=\"changeImages('tbsupport','/SiteView/docs/artwork_ssr/images/navigation/toolbar/support.gif')\" class=\"toolbar\"><img name=\"tbsupport\" src=\"/SiteView/docs/artwork_ssr/images/navigation/toolbar/support.gif\" width=\"44\" height=\"24\" alt=\"Support\" border=\"0\" /></a></td>\n <td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"38\" height=\"24\" alt=\"\" border=\"0\" /></td>\n <td nowrap><a href=\"http://www.dragonflow.com/products/downloads.html\" onmouseover=\"changeImages('tbdownload','/SiteView/docs/artwork_ssr/images/navigation/toolbar/download_over.gif');\" onmouseout=\"changeImages('tbdownload','/SiteView/docs/artwork_ssr/images/navigation/toolbar/download.gif')\" class=\"toolbar\"><img name=\"tbdownload\" src=\"/SiteView/docs/artwork_ssr/images/navigation/toolbar/download.gif\" width=\"54\" height=\"24\" alt=\"Download\" border=\"0\" /></a></td>\n <td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"36\" height=\"24\" alt=\"\" border=\"0\" /></td>\n <td nowrap><a href=\"http://www.dragonflow.com/us/company/corporate-info/contact-us/\" onmouseover=\"changeImages('tbcontact','/SiteView/docs/artwork_ssr/images/navigation/toolbar/contact_us_over.gif')\" onmouseout=\"changeImages('tbcontact','/SiteView/docs/artwork_ssr/images/navigation/toolbar/contact_us.gif')\" class=\"toolbar\"><img name=\"tbcontact\" src=\"/SiteView/docs/artwork_ssr/images/navigation/toolbar/contact_us.gif\" width=\"61\" height=\"24\" alt=\"Contact Us\" border=\"0\" /></a></td>\n <td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"31\" height=\"24\" alt=\"\" border=\"0\" /></td>\n <td nowrap><a href=\"http://www.dragonflow.com/ww/\" onmouseover=\"changeImages('tbworldwide','/SiteView/docs/artwork_ssr/images/navigation/toolbar/worlwide_over.gif')\" onmouseout=\"changeImages('tbworldwide','/SiteView/docs/artwork_ssr/images/navigation/toolbar/worlwide.gif')\"  class=\"toolbar\"><img name=\"tbworldwide\" src=\"/SiteView/docs/artwork_ssr/images/navigation/toolbar/worlwide.gif\" width=\"58\" height=\"24\" alt=\"Worldwide\" border=\"0\" /></a></td>\n <td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"24\" height=\"24\" alt=\"\" border=\"0\" /></td>\n</tr>\n</form>\n</table>\n</td>\n</tr>\n<!-- /toolbar -->\n";
        return s;
    }

    public void printBodyInfo() {
        outputStream
                .println("<body marginheight=\"0\" marginwidth=\"0\">\n<script type=\"text/javascript\" src=\"/SiteView/docs/artwork_ssr/javascript/primary_nav/sample_data.js\"></script>\n<script type=\"text/javascript\" src=\"/SiteView/docs/artwork_ssr/javascript/primary_nav/dqm_script.js\"></script>\n<!-- Main Page Table -->\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
    }

    public void printSubLogoBar() {
        outputStream
                .println("<tr>\n<td>\n<!-- Begin Sub LogoBar -->\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n<tr>\n<td><a href=\"http://www.dragonflow.com\"><img src=\"/SiteView/docs/artwork_ssr/images/DragonFlow_wbs.gif\" width=\"184\" height=\"69\" alt=\"DragonFlow\" border=\"0\" /></a></td>\n<td width=\"99%\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"9\" alt=\"\" border=\"0\" /></td>\n<td><img src=\"/SiteView/docs/artwork_ssr/images/common/home_tagline.gif\" width=\"411\" height=\"69\" alt=\"Optimizing Business Processes to Maximize Business Results\" border=\"0\" /></td>\n<td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"24\" height=\"24\" alt=\"\" border=\"0\" /></td>\n</tr>\n</table>\n<!-- End Sub LogoBar -->\n</td>\n</tr>\n");
    }

    public void printNavBar() {
        outputStream
                .println("<tr><td class=\"mast-supplement\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"6\" alt=\"\" border=\"0\" /></td></tr>\n<tr><td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"11\" height=\"1\" alt=\"\" border=\"0\" /></td></tr> \n<tr>\n<td>\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"primary-nav\">\n<tr>\n<td width=\"181\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"181\" height=\"35\" alt=\"\" border=\"0\" /></td>\n<td width=\"-1%\" class=\"primary-nav-delimeter\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"35\" alt=\"\" border=\"0\" /></td>\n<td width=\"-1%\"><a href=\"http://www.dragonflow.com/us/solutions/\"><img src=\"/SiteView/docs/artwork_ssr/images/navigation/primary/solutions.gif\" name=\"menu0\" id=\"menu0\" onmouseover=\"showMenu(event)\" onmouseout=\"hideMenu(event)\" width=\"79\" height=\"35\" alt=\"\" border=\"0\" /></a></td>\n<td width=\"-1%\" class=\"primary-nav-delimeter\"><img src=\"/SiteView/docs/artwork_ssr/images/empty1111.gif\" width=\"1\" height=\"35\" alt=\"\" border=\"0\" /></td>\n<td width=\"-1%\"><a href=\"http://www.dragonflow.com/products/\"><img src=\"/SiteView/docs/artwork_ssr/images/navigation/primary/products_on.gif\" name=\"menu1\" id=\"menu1\" onmouseover=\"showMenu(event)\" onmouseout=\"hideMenu(event)\" width=\"75\" height=\"35\" alt=\"\" border=\"0\" /></a></td>\n<td width=\"-1%\" class=\"primary-nav-delimeter\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"35\" alt=\"\" border=\"0\" /></td>\n<td width=\"-1%\"><a href=\"http://www.dragonflow.com/us/services/\"><img src=\"/SiteView/docs/artwork_ssr/images/navigation/primary/service_support.gif\" name=\"menu2\" id=\"menu2\" onmouseover=\"showMenu(event)\" onmouseout=\"hideMenu(event)\" width=\"128\" height=\"35\" alt=\"\" border=\"0\" /></a></td>\n<td width=\"-1%\" class=\"primary-nav-delimeter\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"35\" alt=\"\" border=\"0\" /></td>\n<td width=\"-1%\"><a href=\"http://www.dragonflow.com/us/alliances/\"><img src=\"/SiteView/docs/artwork_ssr/images/navigation/primary/alliances.gif\" name=\"menu3\" id=\"menu3\" onmouseover=\"showMenu(event)\" onmouseout=\"hideMenu(event)\" width=\"78\" height=\"35\" alt=\"\" border=\"0\" /></a></td>\n<td width=\"-1%\" class=\"primary-nav-delimeter\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"35\" alt=\"\" border=\"0\" /></td>\n<td width=\"-1%\"><a href=\"http://www.dragonflow.com/us/company/\"><img src=\"/SiteView/docs/artwork_ssr/images/navigation/primary/company.gif\" name=\"menu4\" id=\"menu4\" onmouseover=\"showMenu(event)\" onmouseout=\"hideMenu(event)\" width=\"71\" height=\"35\" alt=\"\" border=\"0\" /></a></td>\n<td width=\"-1%\" class=\"primary-nav-delimeter\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"35\" alt=\"\" border=\"0\" /></td>\n<td width=\"99%\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"35\" alt=\"\" border=\"0\" /></td>\n</tr>\n</table>\n</td>\n</tr>\n");
    }

    public void printPageTitle() {
        outputStream
                .println("<tr><td>\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" background=\"/SiteView/docs/artwork_ssr/images/masts/turn/filler.gif\">\n<tr>\n<td><img src=\"/SiteView/docs/artwork_ssr/images/masts/turn/products_left.jpg\" width=\"181\" height=\"87\" alt=\"Products\" border=\"0\" /></td>\n<td width=\"99%\"><img src=\"/SiteView/docs/artwork_ssr/images/masts/turn/products_middle.gif\" alt=\"Products\" width=\"452\" height=\"87\" border=\"0\" /></td>\n<td width=\"99%\" background=\"/SiteView/docs/artwork_ssr/images/masts/turn/filler.gif\"><img src=\"/SiteView/docs/artwork_ssr/images/masts/turn/filler.gif\" width=\"1\" height=\"87\" alt=\"\" border=\"0\" /></td>\n</tr>\n</table>\n</td></tr>\n<tr><td>\n<img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"11\" height=\"8\" alt=\"\" border=\"0\" /></td></tr>");
    }

    protected void printBeginContent() {
        outputStream
                .println("<td class=\"body-border\" width=\"-1%\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"24\" alt=\"\" border=\"0\" /></td>\n<td valign=\"top\" width=\"99%\">\n<!-- body content starts -->\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n<tr><td colspan=\"3\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"18\" alt=\"\" border=\"0\" /></td></tr>\n<tr>\n<td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"24\" height=\"1\" alt=\"\" border=\"0\" /></td>\n<td width=\"99%\">\n<!-- Begin Center Channel -->\n");
    }

    protected void printEndContent() {
        outputStream
                .println("<!-- End Center Channel -->\n</td>\n<td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"25\" height=\"1\" alt=\"\" border=\"0\" /></td>\n</tr>\n</table>\n<!-- body content ends -->\n</td>\n<td class=\"body-border\" width=\"-1%\"><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"1\" height=\"9\" alt=\"\" border=\"0\" /></td>\n</tr>\n");
    }

    public void printFooter(java.io.PrintWriter printwriter) {
        printwriter
                .println("<!-- footer -->\n<tr>\n<td>\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"footer\">\n<tr>\n<td><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"20\" height=\"9\" alt=\"\" border=\"0\" /></td>\n<td width=\"99%\">\n<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n<tr>\n<td align=\"center\" class=\"footer\">\n<img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"22\" height=\"5\" alt=\"\" border=\"0\" /><br clear=\"all\" />\n<a href=\"http://www.dragonflow.com/\" class=\"footer\">Home</a> | \n<a href=\"http://www.dragonflow.com/us/solutions/\" class=\"footer\">Solutions</a> |\n<a href=\"http://www.dragonflow.com/products/\" class=\"footer\">Products</a> | \n<a href=\"http://www.dragonflow.com/us/services/\" class=\"footer\">Service & Support</a> |\n<a href=\"http://www.dragonflow.com/us/alliances/\" class=\"footer\">Alliances</a> | \n<a href=\"http://www.dragonflow.com/us/company/\" class=\"footer\">Company</a> | \n<a href=\"http://www.dragonflow.com/us/company/corporate-info/contact-us/\" class=\"footer\">Contact Us</a> <br clear=\"all\" />\n<img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"22\" height=\"3\" alt=\"\" border=\"0\" /><br />\n<a href=\"http://www.dragonflow.com/company/copyright.html\" class=\"footer\">&copy; 2003</a>, DragonFlow |\n<a href=\"http://www.dragonflow.com/website/sitemap.html\" class=\"footer\">Sitemap</a> | \n<a href=\"http://www.dragonflow.com/website/feedback.html\" class=\"footer\">Feedback</a> | \n<a href=\"http://www.dragonflow.com/us/company/terms-of-use.html\" class=\"footer\">Terms of Use</a> |\n<a href=\"http://www.dragonflow.com/us/company/privacy-policy.html\" class=\"footer\">Privacy Policy</a>\n<br/><img src=\"/SiteView/docs/artwork_ssr/images/common/spacer.gif\" width=\"22\" height=\"5\" alt=\"\" border=\"0\" />\n</td>\n</tr>\n</table>\n</td>\n<td class=\"footerend\" width=\"22\"><img src=\"/SiteView/docs/artwork_ssr/images/empty1111.gif\" width=\"22\" height=\"9\" alt=\"\" border=\"0\" /></td>\n</tr>\n</table>\n</td>\n</tr>\n<!-- /footer -->\n");
    }
}
