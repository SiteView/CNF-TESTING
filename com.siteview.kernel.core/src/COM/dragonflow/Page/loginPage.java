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

import com.recursionsw.jgl.HashMap;

// Referenced classes of package COM.dragonflow.Page:
// CGI

public class loginPage extends COM.dragonflow.Page.CGI
{

    public loginPage()
    {
    }

    void printLoginForm(String s)
        throws java.io.IOException
    {
        HashMap hashmap = getMasterConfig();
        HashMap hashmap1 = new HashMap();
        String s1 = COM.dragonflow.SiteView.Platform.productName + " Login";
        super.printCGIHeader();
        printBodyHeader(s1);
        String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginHTMLHeader");
        if(s2.length() > 0)
        {
            outputStream.println(s2);
        }
        outputStream.println("<center><H2>" + COM.dragonflow.SiteView.Platform.productName + " Login</H2></center><hr>");
        outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST><input type=hidden name=page value=login><input type=hidden name=groupRedirect value=" + request.getValue("groupRedirect") + ">" + "<input type=hidden name=operation value=Login>" + "<TABLE>" + "<TR><TD ALIGN=RIGHT>Login</TD>" + "<TD><TABLE><TR><TD ALIGN=LEFT><input type=text name=_login size=30 value=\"\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>enter login name</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Page.loginPage.getValue(hashmap1, "_login") + "</I></TD></TR>" + "<TR><TD ALIGN=RIGHT>Password</TD>" + "<TD><TABLE><TR><TD ALIGN=LEFT><input type=password name=_password size=30 value=\"\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>enter password</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Page.loginPage.getValue(hashmap1, "_password") + "</I></TD></TR>" + "<TR><TD>&nbsp;</TD></TR>" + "<TR><TD COLSPAN=2 ALIGN=CENTER><input type=submit VALUE=\"Log In\"></TD></TR>" + "<TR><TD>&nbsp;</TD></TR>");
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_disableLoginChangePassword").length() == 0)
        {
            outputStream.println("<TR><TD ALIGN=LEFT><H3>Change Password</H3></TD></TR><TR><TD ALIGN=RIGHT>New password</TD><TD><TABLE><TR><TD ALIGN=LEFT><input type=password name=_newPassword size=30 value=\"\"></TD></TR><TR><TD><FONT SIZE=-1>to change your password, enter your new password here</FONT></TD></TR></TABLE></TD><TD><I>" + COM.dragonflow.Page.loginPage.getValue(hashmap1, "_newPassword") + "</I></TD></TR>" + "<TR><TD ALIGN=RIGHT>New password (again)</TD>" + "<TD><TABLE><TR><TD ALIGN=LEFT><input type=password name=_newPassword2 size=30 value=\"\"></TD></TR>" + "<TR><TD><FONT SIZE=-1>enter new password again to verify</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + COM.dragonflow.Page.loginPage.getValue(hashmap1, "_newPassword2") + "</I></TD></TR>");
        }
        outputStream.println("</TABLE><p></FORM>");
        printFooter(outputStream);
    }

    String getUserTitle(HashMap hashmap)
    {
        String s = COM.dragonflow.Page.loginPage.getValue(hashmap, "_realName");
        if(s.length() == 0)
        {
            s = COM.dragonflow.Page.loginPage.getValue(hashmap, "_login");
        }
        return s;
    }

    void printLoginComplete(String s)
        throws java.lang.Exception
    {
        String s1 = request.getValue("_login");
        String s2 = request.getValue("_password");
        HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        Array array = COM.dragonflow.SiteView.User.findUsersForLogin(s1, s2);
        String s3 = "";
        if(array.size() > 1)
        {
            s3 = "This username/password cannot be used to login using this login. Use the specific login URL instead (http://" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_webserverAddress") + "/SiteView/?account=yourlogin" + "<hr>";
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginDuplicateMessage").length() > 0)
            {
                s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginDuplicateMessage");
            }
            s3 = s3 + "<br><a href=\"/SiteView/cgi/go.exe/SiteView?page=login\">Return to login</a>";
        } else
        if(array.size() == 1)
        {
            COM.dragonflow.SiteView.User user = (COM.dragonflow.SiteView.User)array.get(0);
            if(user.getProperty("_disabled").length() > 0)
            {
                s3 = "This account has been disabled.<hr>";
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginDisabledMessage").length() > 0)
                {
                    s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginDisabledMessage");
                }
                s3 = s3 + "<br><a href=\"/SiteView/cgi/go.exe/SiteView?page=login\">Return to login</a>";
            } else
            {
                boolean flag = true;
                String s4 = s2;
                if(request.getValue("_newPassword").length() > 0 && (user.getProperty(COM.dragonflow.SiteView.User.pLdap).length() == 0 || user.getProperty(COM.dragonflow.SiteView.User.pSecurityPrincipal).length() == 0))
                {
                    if(request.getValue("_newPassword").equals(request.getValue("_newPassword2")))
                    {
                        Array array1 = COM.dragonflow.SiteView.User.readUsers();
                        HashMap hashmap1 = COM.dragonflow.SiteView.User.findUser(array1, user.getProperty(COM.dragonflow.SiteView.User.pID));
                        if(hashmap1 != null)
                        {
                            hashmap1.put("_password", request.getValue("_newPassword"));
                            s4 = request.getValue("_newPassword");
                            COM.dragonflow.SiteView.User.writeUsers(array1);
                        }
                    } else
                    {
                        flag = false;
                        s3 = "New passwords did not match. Please re-enter them.<hr>";
                        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginNoNewMatchMessage").length() > 0)
                        {
                            s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginNoNewMatchMessage");
                        }
                        s3 = s3 + "<br><a href=\"/SiteView/cgi/go.exe/SiteView?page=login\">Return to login</a>";
                    }
                } else
                if(request.getValue("_newPassword").length() > 0)
                {
                    flag = false;
                    s3 = "You MUST use LDAP to change your password.<hr>";
                    if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginNoNewMatchMessage").length() > 0)
                    {
                        s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginNoNewMatchMessage");
                    }
                    s3 = s3 + "<br><a href=\"/SiteView/cgi/go.exe/SiteView?page=login\">Return to login</a>";
                }
                if(flag)
                {
                    String s6 = user.getProperty(COM.dragonflow.SiteView.User.pAccount);
                    String s7 = request.getValue("groupRedirect");
                    String s5;
                    if(s7 != null && s7.length() > 0)
                    {
                        String s8 = s6.equalsIgnoreCase("administrator") ? "" : "/accounts/" + s6;
                        s5 = "/SiteView" + s8 + "/htdocs/" + s7 + ".html";
                    } else
                    {
                        s5 = "/SiteView?account=" + user.getProperty(COM.dragonflow.SiteView.User.pAccount);
                    }
                    request.addOtherHeader("Set-Cookie: " + COM.dragonflow.SiteView.Platform.productName + "=" + user.getProperty(COM.dragonflow.SiteView.User.pAccount) + "|" + user.getProperty(COM.dragonflow.SiteView.User.pLogin) + "|" + COM.dragonflow.Utils.TextUtils.obscure(s4) + "; path=/");
                    request.addOtherHeader("Location: " + s5);
                    request.setStatus(301);
                    s3 = "<br>Correct username and password<br><a href=\"" + s5 + "\">Go to Main Console</a>" + "<meta http-equiv=\"refresh\" content=\"5;url=\"" + s5 + "\">";
                }
            }
        } else
        {
            s3 = "Incorrect username or password.<hr>";
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginIncorrectMessage").length() > 0)
            {
                s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_loginIncorrectMessage");
            }
            s3 = s3 + "<br><a href=\"/SiteView/cgi/go.exe/SiteView?page=login\">Return to login</a>";
        }
        super.printCGIHeader();
        outputStream.println(s3);
        printFooter(outputStream);
    }

    public void printCGIHeader()
    {
    }

    void printErrorPage(String s)
    {
        printBodyHeader("User Preferences");
        outputStream.println("<p><CENTER><H2></H2></CENTER>\n<HR>There were errors in the entered information.  Use your browser's back button to return\nthe general preferences editing page\n");
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, "\t");
        outputStream.print("<UL>\n");
        for(int i = 0; i < as.length; i++)
        {
            if(as[i].length() > 0)
            {
                outputStream.print("<LI><B>" + as[i] + "</B>\n");
            }
        }

        outputStream.print("</UL><HR></BODY>\n");
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        if(s.length() == 0)
        {
            s = "LoginForm";
        }
        if(s.equals("LoginForm"))
        {
            printLoginForm(s);
        } else
        if(s.equals("Login"))
        {
            printLoginComplete(s);
        } else
        {
            printError("The link was incorrect", "unknown operation", "/SiteView/" + request.getAccountDirectory() + "/SiteView.html");
        }
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        if(args.length == 1 && args[0].equals("-d"))
        {
            Array array = COM.dragonflow.SiteView.User.readUsers();
            HashMap hashmap = new HashMap();
            for(int i = 1; i < array.size(); i++)
            {
                HashMap hashmap1 = (HashMap)array.get(i);
                String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_id");
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_login");
                String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "_password");
                String s3 = s1 + "/" + s2;
                if(hashmap.get(s3) != null)
                {
                    HashMap hashmap2 = (HashMap)hashmap.get(s3);
                    java.lang.System.out.println("Duplicate found " + s1 + " " + s2 + "  account=" + s);
                    java.lang.System.out.println("Original        " + COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_login") + " " + COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_password") + "  account=" + COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "_id"));
                }
                hashmap.put(s3, hashmap1);
            }

            java.lang.System.exit(0);
        }
        COM.dragonflow.Page.loginPage loginpage = new loginPage();
        if(args.length > 0)
        {
            loginpage.args = args;
        }
        loginpage.handleRequest();
    }
}
