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

import com.recursionsw.jgl.Array;
import COM.dragonflow.HTTP.HTTPRequestException;
import COM.oroinc.text.perl.Perl5Util;
import SiteViewMain.MD5;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class regularExpressionPage extends COM.dragonflow.Page.CGI
{

    String findMatch;

    public regularExpressionPage()
    {
        findMatch = "Test Your Match";
    }

    private void setHiddenInfo()
    {
        outputStream.print("<input type=hidden name=page value=\"" + request.getValue("page") + "\">\n" + "<input type=hidden name=account value=\"" + request.getAccount() + "\">\n");
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_tools"))
        {
            throw new HTTPRequestException(557);
        }
        printBodyHeader("Regular Expression Test");
        printButtonBar("regexp.htm", "");
        String s = request.getValue("_yourText");
        String s1 = request.getValue("_yourRegExp");
        if(s1.length() <= 0)
        {
            request.setValue("_yourRegExp", "//");
            s1 = request.getValue("_yourRegExp");
        }
        request.setValue("_yourRegExp", COM.dragonflow.Utils.TextUtils.escapeHTML(s1));
        request.setValue("_yourText", COM.dragonflow.Utils.TextUtils.escapeHTML(request.getValue("_yourText")));
        outputStream.println("<p>\n<CENTER><H2>Regular Expression Test</H2></CENTER><P>\n<p>\n<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n");
        setHiddenInfo();
        outputStream.println("<TABLE><tr><td><img src=\"/SiteView/htdocs/artwork/LabelSpacer.gif\"></td><td></td></tr><TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">    Your Text that will be matched:  </TD><TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\">    <TEXTAREA name=_yourText rows=10 cols=80>" + COM.dragonflow.Utils.TextUtils.escapeHTML(s) + "</TEXTAREA>" + "  </TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\">" + "    <FONT SIZE=-1>   " + "         Copy and paste a block of text containing the text" + "          string or values you want to match<br><br>" + "    </FONT>" + "  </TD></TR></TABLE></TD></TR> " + "<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + "    Your Regular Expression :" + "  </TD><TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\">" + "    <input type=text name=_yourRegExp style=\"font-family: Courier\" value=\"" + COM.dragonflow.Utils.TextUtils.escapeHTML(s1) + "\" size=100>" + "  </TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\">" + "    <FONT SIZE=-1>" + "                  <p>Enter a regular expression between the slashes, to match some part of the text" + "              you entered. <a" + "              href=\"/SiteView/docs/regexp.htm\" TARGET=Help> Using" + "              Regular Expressions for Content Matching</a>" + "              </p>" + "    " + "              Quick reference: <br>" + "              Example: /abc(def)..ijk[A-Za-z0-9]{1,4}p(qr)s(t(u(v)w)x)yz/is<br>" + "              Use sets of parentheses <tt>(text)</tt> to match pieces of the total match.<br>" + "              <code>.*</code> - match everything. <code>.*?</code> - match any(or no) characters<br>" + "     </FONT>" + "  </TD></TR></TABLE></TD></TR>" + "</TABLE>");
        outputStream.print("<input type=submit name=\"submit\" value=\"" + findMatch + "\" class=\"VerBl8\">");
        outputStream.print("</FORM>&nbsp;&nbsp");
        String s2 = "";
        if(s.length() > 0 && s1.length() > 2 && s1.startsWith("/"))
        {
            outputStream.println("<br><hr>");
            outputStream.flush();
            try
            {
                outputStream.println("");
                java.util.Vector vector = COM.dragonflow.Utils.TextUtils.parseParentheses(s1);
                Array array = new Array();
                COM.oroinc.text.perl.Perl5Util perl5util = new Perl5Util();
                int i = COM.dragonflow.Utils.TextUtils.matchExpression(s, s1, array, perl5util);
                if(array.size() > 0)
                {
                    s2 = (String)array.get(0);
                }
                if(i > 0)
                {
                    COM.oroinc.text.perl.Perl5Util perl5util1 = new Perl5Util();
                    if(perl5util != null)
                    {
                        perl5util1 = perl5util;
                    }
                    int j = perl5util1.beginOffset(0);
                    int k = perl5util1.endOffset(0);
                    String s3 = COM.dragonflow.Utils.TextUtils.escapeHTML(s);
                    outputStream.println("<h2>Content Match Results</h2>");
                    outputStream.println("\nYour Regular Expression: <code>" + COM.dragonflow.Utils.TextUtils.escapeHTML(s1) + "</code><br>");
                    java.util.Iterator iterator = vector.iterator();
                    outputStream.println("<br><br><h2>Parsed parentheses and matches:</h2>");
                    outputStream.println("<table border=1>");
                    outputStream.println("<tr><td><b>Parentheses counted from left</b></td><td><b>matching text</b></td></tr>");
                    for(int l = 1; l < array.size(); l++)
                    {
                        String s4 = (String)array.get(l);
                        outputStream.println("<tr><td><pre><code>" + (iterator.hasNext() ? COM.dragonflow.Utils.TextUtils.escapeHTML(iterator.next().toString()) : "") + "</code></pre></td><td><code>" + (s4 == null ? "" : COM.dragonflow.Utils.TextUtils.escapeHTML(s4)) + "</code></td></tr>");
                    }

                    outputStream.println("</table>");
                    outputStream.println("<table border=1>");
                    outputStream.println("<tr><td><b>Whole Match Between Slashes:</b></td></tr>");
                    outputStream.println("<tr><td><code>" + COM.dragonflow.Utils.TextUtils.escapeHTML(s2) + "</code></td></tr><br>");
                    outputStream.println("</table>");
                    if(k > j)
                    {
                        SiteViewMain.MD5 md5 = new MD5(s);
                        String s5 = md5.asHex();
                        String s6 = "start" + s5;
                        String s7 = "end" + s5;
                        String s8 = s.substring(0, j);
                        s8 = s8 + s6;
                        s8 = s8 + s.substring(j, k);
                        s8 = s8 + s7;
                        s8 = s8 + s.substring(k);
                        s3 = COM.dragonflow.Utils.TextUtils.escapeHTML(s8);
                        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, s6, "<B><a name=\"match\"><font color=blue>");
                        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, s7, "</font></a></B>");
                    }
                    outputStream.println("<br><br><h2>Highlighted match in <font color=blue>blue</font>:</h2> \n");
                    outputStream.println("<a href=\"#match\">Go to the matching text</a>");
                    outputStream.println("<p>Starting Position: " + j + "<br>Ending Postion: " + k);
                    outputStream.println("<table border=1><tr><td>");
                    outputStream.println("<pre>");
                    outputStream.println(s3);
                    outputStream.println("</pre>");
                    outputStream.println("</td></tr></table>");
                } else
                {
                    ContentMatchError("Content match error");
                }
            }
            catch(java.lang.Exception exception)
            {
                outputStream.println("<h2>Content Match Results</h2>");
                outputStream.println("<p><b>Your regular expression failed to parse. <code>" + exception.toString() + "</code>");
                outputStream.println("</b></p>");
            }
        } else
        {
            ContentMatchError("Text or Regular Expression Field Empty or missing slashes. (example regex: /hello/s)");
        }
        outputStream.println("</BODY>");
        printFooter(outputStream);
        setHiddenInfo();
    }

    private void ContentMatchError(String s)
    {
        outputStream.println("<h2>Content Match Results</h2>");
        outputStream.println("<table border=\"0\">");
        outputStream.println("<tbody><tr><td>");
        outputStream.println("<code>" + s + "</code></td>");
        outputStream.println("<td></td>");
        outputStream.println("</tr></tbody>");
        outputStream.println("</table>");
        outputStream.println("<p><b>First rule of regular expressions: Start simple; get a match; iterate");
        outputStream.println("to more complex...</b></p>");
    }

    public static void main(String args[])
    {
        COM.dragonflow.Page.regularExpressionPage regularexpressionpage = new regularExpressionPage();
        if(args.length > 0)
        {
            regularexpressionpage.args = args;
        }
        regularexpressionpage.handleRequest();
    }
}
