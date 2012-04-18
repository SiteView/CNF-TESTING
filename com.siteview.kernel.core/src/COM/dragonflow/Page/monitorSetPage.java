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
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.FrequencyProperty;
import COM.dragonflow.Properties.ScalarProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Machine;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Portal;
import COM.dragonflow.SiteView.ServerMonitor;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteView.monitorUtils;
import COM.dragonflow.StandardMonitor.ADReplicationMonitor;
import COM.dragonflow.StandardMonitor.Exchange2k3MailboxMonitor;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.Page:
// CGI, SSrtn, monitorSetTemplate, monitorSetFilter,
// vMachinePage

public class monitorSetPage extends CGI
{

    public static final String TEMPLATES_DIR;
    public static final String SOLUTIONS_DIR;
    protected String thisPageName;
    public StringBuffer HTMLPage;
    protected String helpPage;
    protected static final String SOLUTION = "solution";
    protected static final String OPERATION = "operation";
    protected static final String PICK_SOLUTION = "PickSolution";
    protected static final String ADD_SET = "AddSet";
    protected static final String MSET_PROPERTIES = "monitorSetProps";
    protected static final String MSET_CREATE_PREP = "monitorSetCreatePrep";
    protected static final String MSET_CREATE = "monitorSetCreate";
    protected static final String TEMPLATE_FILE = "templatefile";
    protected static final String GROUP = "group";
    protected char positionDelimiter;
    private static final String HELP_FILE = "_helpFile";
    protected static final String DISPLAY_FILTER = "_propertyDisplayFilter";
    protected static final String REPLACE_FILTER = "_propertyReplace";
    protected static final String SERVERLABEL = "Server";
    private static final String DEPLOY_CONTROL_SETTING = "_deployControlVar";
    public static final java.util.regex.Pattern FOREACH_VARNAME_PATTERN = java.util.regex.Pattern.compile("^@([a-zA-Z_0-9 ]+)@:.+");
    public static final String NAME_IDX = "name";
    public static final String VALUE_IDX = "value";

    public monitorSetPage()
    {
        thisPageName = "monitorSet";
        HTMLPage = new StringBuffer();
        helpPage = "../docs/MonitorSet.htm";
        positionDelimiter = '#';
    }

    public monitorSetPage(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        thisPageName = "monitorSet";
        HTMLPage = new StringBuffer();
        helpPage = "../docs/MonitorSet.htm";
        request = httprequest;
    }

    public CGI.menus getNavItems(COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        CGI.menus menus1 = new CGI.menus();
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Browse", "browse", "", "page", "Browse Monitors"));
        }
        if(httprequest.actionAllowed("_preference"))
        {
            menus1.add(new CGI.menuItems("Remote UNIX", "machine", "", "page", "Add/Edit Remote UNIX/Linux profiles"));
            menus1.add(new CGI.menuItems("Remote NT", "ntmachine", "", "page", "Add/Edit Remote Win NT/2000 profiles"));
        }
        if(httprequest.actionAllowed("_tools"))
        {
            menus1.add(new CGI.menuItems("Tools", "monitor", "Tools", "operation", "Use monitor diagnostic tools"));
        }
        if(httprequest.actionAllowed("_progress"))
        {
            menus1.add(new CGI.menuItems("Progress", "Progress", "", "url", "View current monitoring progress"));
        }
        if(httprequest.actionAllowed("_browse"))
        {
            menus1.add(new CGI.menuItems("Summary", "monitorSummary", "", "page", "View current monitor settings"));
        }
        return menus1;
    }

    public void printBody()
        throws java.lang.Exception
    {
        String s = request.getValue("operation");
        boolean flag = request.getValue("solution").length() > 0;
        String s1 = "";
        SSrtn ssrtn;
        if(s.equals("PickSolution"))
        {
            s1 = "Choose Solution";
            ssrtn = pickSolution();
        } else
        if(s.equals("AddSet"))
        {
            s1 = "Add " + (flag ? "Solution" : "Monitor Set");
            ssrtn = pickMonitorSet();
        } else
        if(s.equals("monitorSetProps"))
        {
            s1 = (flag ? "Solution Template  " : "Monitor Set ") + "Properties";
            ssrtn = enterSetProperties();
        } else
        if(s.equals("monitorSetCreatePrep"))
        {
            s1 = flag ? "Verify Solution Template" : "Monitor Set Verify";
            ssrtn = checkSetParams();
        } else
        if(s.equals("monitorSetCreate"))
        {
            s1 = flag ? "Create Solution" : "Monitor Set Create";
            ssrtn = createSet();
        } else
        {
            s1 = "";
            ssrtn = new SSrtn(SSrtn.ERR_InvalidOpr);
        }
        if(ssrtn.isError())
        {
            printError(ssrtn.message(), ssrtn.errormsg(), "");
            return;
        }
        CGI.menus menus1 = getNavItems(request);
        if(request.getValue("refreshURL").length() > 0)
        {
            String s2 = "/SiteView/cgi/go.exe/SiteView?page=" + request.getValue("refreshURL") + "&account=" + request.getAccount();
            monitorSetPage.printRefreshHeader(outputStream, Platform.productName + " " + s1, s2, 8);
            printButtonBar(helpPage, "", menus1);
        } else
        {
            printBodyHeader(Platform.productName + " " + s1);
            printButtonBar(helpPage, "", menus1);
        }
        outputStream.println(HTMLPage);
        printFooter(outputStream);
        outputStream.println("\n</body>\n</html>");
    }

    private SSrtn pickSolution()
    {
        SSrtn ssrtn = new SSrtn();
        String s = COM.dragonflow.HTTP.HTTPRequest.encodeString(request.getValue("group"));
        helpPage = "../docs/Solutions.htm";
        HTMLPage.append("<H2>Add a Monitoring Solution to group : <A HREF=" + CGI.getGroupDetailURL(request, I18N.toDefaultEncoding(s)) + ">" + CGI.getGroupName(I18N.toDefaultEncoding(s)) + "</a></H2>\n");
        HTMLPage.append("<h3>Available Monitoring Solutions:</h3><p>");
        HTMLPage.append("<br>");
        HTMLPage.append("<hr>");
        HTMLPage.append("<br clear=all>");
        HTMLPage.append("</p>");
        HTMLPage.append("<TABLE BORDER=1 CELLPADDING=5 CELLSPACING=0 WIDTH=100%>");
        HTMLPage.append("<TR >");
        HTMLPage.append("<th align=center>Solution Name</th>");
        HTMLPage.append("<th align=center>Description</th>");
        HTMLPage.append("<th align=center>More Information</th>");
        HTMLPage.append("</tr>");
        String as[] = getAvailableTemplates(SOLUTIONS_DIR, monitorSetTemplate.fileSuffix);
        if(as == null || as.length == 0)
        {
            HTMLPage.append("<tr><td>A seperate License is required to enable Solutions Sets for Exchange and Active Diretory.</td></tr>");
            return ssrtn;
        }
        String as1[] = getTemplateNames(as, SOLUTIONS_DIR);
        String as2[] = getTemplateDescriptions(as, SOLUTIONS_DIR);
        String as3[] = getTemplateMoreInfo(as, SOLUTIONS_DIR);
        String as4[] = getTemplateSolutionPages(as, SOLUTIONS_DIR);
        for(int i = 0; i < as.length; i++)
        {
            boolean flag = false;
            String s1 = as1[i];
            String s2 = as2[i];
            String s3 = as3[i];
            if(s1.toUpperCase().indexOf("EXCHANGE") >= 0)
            {
                if(LUtils.isValidSSforXLicense(new Exchange2k3MailboxMonitor()))
                {
                    flag = true;
                }
            } else
            if(s1.toUpperCase().indexOf("ACTIVE") >= 0)
            {
                if(LUtils.isValidSSforXLicense(new ADReplicationMonitor()))
                {
                    flag = true;
                }
            } else
            if(s1.toUpperCase().indexOf("WEBSPHERE") >= 0)
            {
                if(LUtils.isValidSSforXLicense("WebSphereSolution"))
                {
                    flag = true;
                }
            } else
            if(s1.toUpperCase().indexOf("WEBLOGIC") >= 0)
            {
                if(LUtils.isValidSSforXLicense("WebLogicSolution"))
                {
                    flag = true;
                }
            } else
            if(s1.toUpperCase().indexOf("ORACLE") >= 0)
            {
                if(LUtils.isValidSSforXLicense("OracleSolution"))
                {
                    flag = true;
                }
            } else
            if(s1.toUpperCase().indexOf("SIEBEL") >= 0)
            {
                if(LUtils.isValidSSforXLicense("SiebelSolution"))
                {
                    flag = true;
                }
            } else
            {
                flag = true;
            }
            if(flag)
            {
                HTMLPage.append("<tr><td><A HREF=/SiteView/cgi/go.exe/SiteView?page=" + as4[i] + "&operation=monitorSetProps&group=" + s + "&account=" + request.getAccount() + "&templatefile=" + as[i] + "&" + "solution" + "=true" + ">" + s1 + "</A> </td><td>" + s2 + "</td><td>" + s3 + "</td></tr>");
            } else
            {
                HTMLPage.append("<tr><td>" + s1 + "</td><td>" + s2 + "<br>(additional licensing required)</td><td><a href=../../docs/Solutions.htm>Solutions Overview</a></td></tr>");
            }
        }

        HTMLPage.append("</TABLE><p><br clear=all><hr><br></p>");
        return ssrtn;
    }

    public boolean createMonitorSet(String s, String s1, HashMap hashmap)
    {
        SSrtn ssrtn = new SSrtn();
        String s2 = I18N.toDefaultEncoding(s);
        String s3 = TEMPLATES_DIR + java.io.File.separator + s1;
        monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s3);
        String as[] = monitorsettemplate.getVariables();
        for(int i = 0; i < as.length; i++)
        {
            String s4 = (String)hashmap.get(as[i]);
            if(s4 != null)
            {
                monitorsettemplate.replaceVariable(as[i], s4);
            } else
            {
                java.lang.System.out.print("monitor set variable mismatch in: " + s1);
            }
        }

        int j = monitorsettemplate.getMonitorCount();
        for(int k = 0; k < j; k++)
        {
            HashMap hashmap1 = monitorsettemplate.getNthMonitor(k);
            try
            {
                ssrtn = createMonitor(s2, hashmap1);
            }
            catch(java.lang.Exception exception)
            {
                ssrtn.setError(SSrtn.ERR_Exception, "Exception: " + exception);
            }
            if(ssrtn.isError())
            {
                return false;
            }
            HTMLPage.append("Successfully created monitor. Check group page for status.<br></p>\n");
        }

        SiteViewGroup.updateStaticPages(s2);
        return true;
    }

    SSrtn pickMonitorSet()
    {
        SSrtn ssrtn = new SSrtn();
        String s = request.getValue("group");
        HTMLPage.append("<H2>Add Monitor Set to group : <A HREF=" + CGI.getGroupDetailURL(request, I18N.toDefaultEncoding(s)) + ">" + CGI.getGroupName(I18N.toDefaultEncoding(s)) + "</a></H2>\n");
        HTMLPage.append("<h3>Current Monitor Set Templates:</h3>");
        String as[] = getAvailableTemplates();
        if(as == null || as.length == 0)
        {
            ssrtn.setError(SSrtn.ERR_NoTemplatesFound, "Please verify that the SiteView/templates.sets directory exists, and that it contains template files");
            return ssrtn;
        }
        HTMLPage.append("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=page value=" + getNextPage(request.getValue("operation")) + ">\n" + "<input type=hidden name=operation value=" + "monitorSetProps" + ">\n" + "<input type=hidden name=account value= \"" + request.getAccount() + "\">\n" + "<input type=hidden name=group value= \"" + s + "\">\n");
        HTMLPage.append("<TABLE BORDER=1 CELLSPACING=0 WIDTH=100%>\n<TR><th>Name</th><th>Description</th><th>Filename</th></tr>");
        String as1[] = getTemplateNames(as);
        String as2[] = getTemplateDescriptions(as);
        for(int i = 0; i < as.length; i++)
        {
            HTMLPage.append("<tr><td><input type=radio name=templatefile value=\"" + as[i] + "\">  " + as1[i] + "</td><td>" + as2[i] + "</td><td>" + as[i] + "</td></tr>\n");
        }

        HTMLPage.append("</TABLE>");
        HTMLPage.append("<BR><input type=submit value=Configure> Monitor Set\n<P>\n");
        HTMLPage.append("\n</FORM>");
        HTMLPage.append("<HR><P>Use Monitor Sets to quickly replicate a set of monitors across multiple servers or locations. ");
        HTMLPage.append("Select a Monitor Set Template and click Configure to enter settings for this set of  monitors. Click <a href=\"../../docs/MonitorSet.htm\">Help</a> for instructions on creating new Monitor Set Templates.\n<P>\n");
        return ssrtn;
    }

    SSrtn enterSetProperties()
    {
        SSrtn ssrtn = new SSrtn();
        String s = request.getValue("templatefile");
        String s1 = request.getValue("group");
        boolean flag = request.getValue("solution").length() > 0;
        if(s.length() == 0)
        {
            ssrtn.setError(SSrtn.ERR_NoTemplateFile, "Please select a monitor set template");
            return ssrtn;
        } else
        {
            String s2 = getTemplateFilePath(s);
            monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s2);
            setHelpLink(monitorsettemplate);
            printJavaScript();
            verifyTemplate(monitorsettemplate, ssrtn);
            printNameAndDescription(monitorsettemplate, flag, s);
            String s3 = getFormName();
            openVariablesForm(s1, s, s3, thisPageName);
            openVariablesTable(flag);
            String s4 = getRemotesHTML();
            printVariablesForm(monitorsettemplate, flag, s4);
            closeVariableTable();
            printVariablesFormSubmit(flag);
            closeVariablesForm();
            return ssrtn;
        }
    }

    protected void printJavaScript()
    {
    }

    protected String getFormName()
    {
        return null;
    }

    protected void printVariablesFormSubmit(boolean flag)
    {
        HTMLPage.append("<p>Enter the values to be used by the monitors in this " + (flag ? "Solution Template" : "Monitor Set") + ".<P>\n");
        HTMLPage.append("<BR><input type=submit value=Submit>&nbsp;these values<P>\n");
    }

    protected void printNameAndDescription(monitorSetTemplate monitorsettemplate, boolean flag, String s)
    {
        String s1 = monitorsettemplate.getName();
        HTMLPage.append("<P><h3>" + (flag ? "Solution Properties for " : "Monitor Set Properties for template: ") + s1 + "</h3>\n");
        HTMLPage.append("<P><h3>Description for " + s1 + ": </h3>\n");
        String as[] = getTemplateDescriptions(s, flag ? SOLUTIONS_DIR : TEMPLATES_DIR);
        HTMLPage.append("<P>" + as[0] + "\n");
    }

    protected void verifyTemplate(monitorSetTemplate monitorsettemplate, SSrtn ssrtn)
    {
        for(int i = 0; i < monitorsettemplate.getMonitorCount(); i++)
        {
            String s = monitorsettemplate.verifyNthMonitor(i);
            if(s.length() != 0)
            {
                ssrtn.setError(SSrtn.ERR_badTemplateData, s);
            }
        }

    }

    protected void openVariablesForm(String s, String s1, String s2, String s3)
    {
        HTMLPage.append("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST ");
        if(s2 != null && s2.length() > 0)
        {
            HTMLPage.append(" NAME=").append(s2);
        }
        HTMLPage.append(">\n<input type=hidden name=page value=" + s3 + ">\n" + "<input type=hidden name=operation value=" + "monitorSetCreatePrep" + ">\n" + "<input type=hidden name=account value= \"" + request.getAccount() + "\">\n" + "<input type=hidden name=group value= \"" + s + "\">\n" + "<input type=hidden name=" + "solution" + " value= \"" + request.getValue("solution") + "\">\n" + "<input type=hidden name=templatefile value= \"" + s1 + "\">\n");
    }

    protected void closeVariablesForm()
    {
        HTMLPage.append("\n</FORM>");
    }

    protected void openVariablesTable(boolean flag)
    {
        if(flag)
        {
            HTMLPage.append("<TABLE BORDER=0 CELLSPACING=0 WIDTH=\"100%\">\n");
        } else
        {
            HTMLPage.append("<TABLE BORDER=1 CELLSPACING=0 WIDTH=\"100%\">\n");
            HTMLPage.append("<TR><TH>Variable Name</TH><TH>Description</TH><TH>Enter Value</TH><TH>Monitor Type</TH></TR>\n");
        }
    }

    protected void closeVariableTable()
    {
        HTMLPage.append("\n</TABLE><P>\n");
    }

    private String getRemotesHTML()
    {
        HashMap hashmap = Machine.getMachineTable();
        String s = "";
        Enumeration enumeration = (Enumeration) hashmap.keys();
        java.util.Vector vector = new Vector();
        for(; enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement())) { }
        vector.addElement("other");
        for(int i = 0; i < vector.size(); i++)
        {
            String s1 = (String)vector.elementAt(i);
            boolean flag = s1.equals("other");
            String s2 = "";
            if(!flag)
            {
                Machine machine = (Machine)hashmap.get(s1);
                s2 = machine.getProperty("_name");
            } else
            {
                s2 = "other";
            }
            s = s + vMachinePage.getListOptionHTML(s1, s2, flag);
        }

        return s;
    }

    protected void printVariablesForm(monitorSetTemplate monitorsettemplate)
    {
        printVariablesForm(monitorsettemplate, true, "");
    }

    protected void printVariablesForm(monitorSetTemplate monitorsettemplate, boolean flag, String s)
    {
        String as[] = monitorsettemplate.getVariables();
        as = sortVars(as);
        for(int i = 0; i < as.length; i++)
        {
            String s1 = as[i];
            HashMap hashmap = monitorsettemplate.getVariableInfo(s1);
            String s2 = TextUtils.getValue(hashmap, "_description");
            String s3 = TextUtils.getValue(hashmap, "_value");
            boolean flag1 = TextUtils.getValue(hashmap, "_scalar").length() > 0;
            boolean flag2 = TextUtils.getValue(hashmap, "_server").length() > 0;
            boolean flag3 = TextUtils.getValue(hashmap, "_checkbox").length() > 0;
            String s4 = getDisplayVariable(s1);
            String as1[] = monitorsettemplate.getWhereUsed(s1);
            if(flag)
            {
                if(flag2)
                {
                    String s5 = monitorsettemplate.getSetting("_platform");
                    String s9 = "";
                    String s11 = "";
                    String s12 = s5.toLowerCase().equals("windows") ? "true" : "";
                    String s14 = s5.toLowerCase().equals("unix") ? "true" : "";
                    String s16 = "";
                    String s17 = "\\\\servername";
                    String s18 = request.getValue("server");
                    String s19 = s17;
                    if(s18.length() > 0)
                    {
                        if(s18.toUpperCase().equals("other"))
                        {
                            s17 = request.getValue("otherServer");
                        } else
                        {
                            s17 = s18;
                        }
                        int l = s17.indexOf("\\\\");
                        if(l >= 0)
                        {
                            s19 = s17 = s17.substring(2);
                        } else
                        if(s17.startsWith("remote:"))
                        {
                            s19 = Machine.getMachineName(s17);
                        }
                    }
                    HTMLPage.append("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">Server</TD><TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><TABLE border=1 cellspacing=0><TR><TD>" + s19 + "</TD></TR></TABLE></TD><TD>" + "<input type=hidden name=\"" + s1 + "\" value=\"" + s17 + "\">" + "<a href=" + getPageLink("server", "") + "&server=" + java.net.URLEncoder.encode(s9) + s11 + "&returnURL=" + java.net.URLEncoder.encode(request.rawURL) + "&noremote=" + s12 + "&noNTRemote=" + s14 + ">choose server</a>" + "</TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + s2 + "</FONT></TD></TR>" + "</TABLE></TD><TD><I>" + s16 + "</I></TD></TR>");
                } else
                if(flag3)
                {
                    HTMLPage.append("<TR><TD align=\"right\" valign=\"top\">" + s4 + " </TD>");
                    HTMLPage.append("<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><INPUT TYPE=\"checkbox\" NAME=\"" + s1 + "\"" + (s3.length() <= 0 ? "" : " CHECKED") + "></TD></TR>");
                    HTMLPage.append("<tr><TD ALIGN=\"left\" VALIGN=\"top\">" + s2 + "</td></tr></TD></TABLE>\n");
                    HTMLPage.append("</TD>");
                    HTMLPage.append("</TR>\n");
                } else
                if(flag1)
                {
                    String s6 = TextUtils.getValue(hashmap, "_scalarValues");
                    String as2[] = s6.split(",");
                    if(s6 != null)
                    {
                        HTMLPage.append("<TR><TD align=\"right\" valign=\"top\">" + s4 + " </TD>");
                        HTMLPage.append("<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><SELECT NAME=\"" + s1 + "\" SIZE=1 >");
                        for(int j = 1; j < as2.length; j += 2)
                        {
                            String s13 = as2[j - 1];
                            String s15 = as2[j];
                            HTMLPage.append("<option value=\"" + s15 + "\">" + s13 + " </option> \n");
                        }

                        HTMLPage.append("</INPUT></TD></TR>");
                        HTMLPage.append("<tr><TD ALIGN=\"left\" VALIGN=\"top\">" + s2 + "</td></tr></TD></TABLE>\n");
                        HTMLPage.append("</TD>");
                        HTMLPage.append("</TR>\n");
                    }
                } else
                {
                    HTMLPage.append("<TR><TD align=\"right\" valign=\"top\">" + s4 + " </TD>");
                    String s7 = "TEXT";
                    if(s1.toUpperCase().indexOf("ASSWORD") >= 0)
                    {
                        s7 = "PASSWORD";
                    }
                    HTMLPage.append("<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\"><INPUT TYPE=\"").append(s7).append("\" NAME=\"" + s1 + "\" VALUE=\"" + s3 + "\" SIZE=50></TD></TR>");
                    HTMLPage.append("<tr><TD ALIGN=\"left\" VALIGN=\"top\">" + s2 + "</td></tr></TD></TABLE>\n");
                    HTMLPage.append("</TD>");
                    HTMLPage.append("</TR>\n");
                }
                HTMLPage.append("<INPUT TYPE=hidden name=\"" + s1 + "remotes\" value=\"other\">");
                continue;
            }
            HTMLPage.append("<TR>");
            HTMLPage.append("<TD>" + s1 + "</TD>");
            HTMLPage.append("<TD>" + s2 + "</TD>");
            String s8 = "TEXT";
            if(s1.toUpperCase().indexOf("ASSWORD") >= 0)
            {
                s8 = "PASSWORD";
            }
            HTMLPage.append("<TD><INPUT TYPE=\"").append(s8).append("\" NAME=\"" + s1 + "\" VALUE=\"" + s3 + "\" SIZE=50>");
            HTMLPage.append("or <select size=1 name=" + s1 + "remotes>" + s + "</select>");
            HTMLPage.append("</TD>\n");
            HTMLPage.append("<TD>");
            String s10 = "";
            if(as1 != null)
            {
                for(int k = 0; k < as1.length; k++)
                {
                    HTMLPage.append(s10 + as1[k]);
                    s10 = ",";
                }

            }
            HTMLPage.append("</TD>");
            HTMLPage.append("</TR>\n");
        }

    }

    protected String getDisplayVariable(String s)
    {
        int i = s.indexOf('$') + 1;
        if(i < 0)
        {
            return s;
        }
        String s1 = s.substring(i, s.length() - 1);
        int j = s1.indexOf(positionDelimiter);
        if(j > 0)
        {
            return s1.substring(0, j);
        } else
        {
            return s1;
        }
    }

    /**
     * 
     * 
     * @param as
     * @return
     */
    protected String[] sortVars(String as[])
    {
        String[] as1 = new String[as.length];
        
        int j;
        for (int i = 0; i < as.length; i ++)
        {
            String s = as[i];
            j = s.indexOf(positionDelimiter);
            if(j > 0)
            {
                try
                {
                    int k = java.lang.Integer.parseInt(s.substring(j + 1, s.length() - 1));
                    if(k < 0 || k > as.length - 1 || as1[k] != null)
                    {
                        return as;
                    }
                    as1[k] = s;
                }
                catch(java.lang.Exception exception)
                {
                    return as;
                }
            }
            else {
                return as;
            }
        }
        
        return as1;
    }

    protected void setHelpLink(monitorSetTemplate monitorsettemplate)
    {
        String s = monitorsettemplate.getSetting("_helpFile");
        if(s.length() > 0)
        {
            helpPage = s;
        }
    }

    protected String getTemplateFilePath(String s)
    {
        String s1;
        if(request.getValue("solution").length() > 0)
        {
            s1 = SOLUTIONS_DIR + java.io.File.separator + s;
        } else
        {
            s1 = TEMPLATES_DIR + java.io.File.separator + s;
        }
        return s1;
    }

    SSrtn checkSetParams()
        throws java.lang.Exception
    {
        SSrtn ssrtn = new SSrtn();
        String s = request.getValue("templatefile");
        String s1 = request.getValue("group");
        boolean flag = request.getValue("solution").length() > 0;
        String s2 = getTemplateFilePath(s);
        monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s2);
        setHelpLink(monitorsettemplate);
        String s3 = monitorsettemplate.getName();
        String s4 = monitorsettemplate.getSetting("_propertyDisplayFilter");
        boolean flag1 = s4.length() > 0;
        String as[] = new String[0];
        if(flag1)
        {
            as = s4.split(",");
        }
        String s5 = monitorsettemplate.getSetting("_propertyReplace");
        boolean flag2 = s5.length() > 0;
        String as1[][] = new String[0][0];
        if(flag2)
        {
            String as2[] = s5.split(",");
            as1 = new String[as2.length][];
            for(int i = 0; i < as2.length; i++)
            {
                String s6 = as2[i];
                String as3[] = s6.split("=");
                if(as3.length == 2)
                {
                    as1[i] = as3;
                }
            }

        }
        HTMLPage.append("<p><h3>Property and Syntax Check for " + (flag ? "Solution:" : "Template: ") + s3 + "</h3>\n");
        HashMap hashmap = populateRegularVariables(monitorsettemplate, true);
        HashMap hashmap1 = populateForeachVariables();
        monitorsettemplate.replaceAllVariables(hashmap, hashmap1);
        int j = monitorsettemplate.getMonitorCount();
        boolean flag3 = false;
        if(flag)
        {
            for(int k = 0; k < j; k++)
            {
                HashMap hashmap2 = monitorsettemplate.getNthMonitor(k);
                ssrtn = checkMonitorParams(s1, hashmap2);
                if(ssrtn.isErrorList())
                {
                    flag3 = true;
                }
            }

            if(flag3)
            {
                HTMLPage.append("<P><b>Errors were found with this Solution set. The errors should be corrected before creating these monitors otherwise invalid monitors may be created.</b>\n");
            } else
            {
                HTMLPage.append("<p>No errors detected in " + (flag ? "Solution Template" : "Monitor Set") + " syntax. Click 'Create' button at bottom of page to create the monitors with these settings. <P>\n");
            }
            HTMLPage.append("<hr>");
        }
        for(int l = 0; l < j; l++)
        {
            HashMap hashmap3 = monitorsettemplate.getNthMonitor(l);
            String s8 = TextUtils.getValue(hashmap3, "_class");
            String s10 = TextUtils.getValue(hashmap3, "_name");
            String s11 = TextUtils.getValue(hashmap3, "_monitorDescription");
            if(doNotDeployMonitor(hashmap3))
            {
                continue;
            }
            HTMLPage.append("<P><font size=\"+1\">Monitor Type:<b> " + s8 + "</b>  &nbsp;&nbsp;&nbsp;  Monitor Name:<b> " + s10 + "</b></font></p>\n");
            if(s11.length() > 0)
            {
                HTMLPage.append("<p>Monitor Description: <b> " + s11 + "</b></p>");
            }
            HTMLPage.append("<TABLE BORDER=1 CELLSPACING=0 WIDTH=\"100%\">\n");
            HTMLPage.append("<TR><TH width=\"20%\">Property</TH><TH width=\"30%\">Value</TH><TH width=\"50%\"> Property or Syntax Errors</TH></TR>\n");
            Enumeration enumeration2 = (Enumeration) hashmap3.keys();
            do
            {
                if(!enumeration2.hasMoreElements())
                {
                    break;
                }
                String s12 = (String)enumeration2.nextElement();
                if(s12.equals("_class") || s12.equals("_id") || s12.equals("_group"))
                {
                    continue;
                }
                if(flag1)
                {
                    boolean flag4 = true;
                    for(int i1 = 0; i1 < as.length; i1++)
                    {
                        String s13 = as[i1];
                        if(s12.indexOf(s13) >= 0)
                        {
                            flag4 = false;
                        }
                    }

                    if(!flag4)
                    {
                        continue;
                    }
                }
                Array array = TextUtils.getMultipleValues(hashmap3, s12);
                StringBuffer stringbuffer = new StringBuffer();
                for(int j1 = 0; j1 < array.size(); j1++)
                {
                    if(j1 > 0)
                    {
                        stringbuffer.append("<br>");
                    }
                    stringbuffer.append((String)array.get(j1));
                }

                String s14 = "BGCOLOR=\"#DDDDDD\"";
                if(monitorsettemplate.didReplacement(l, s12))
                {
                    if(flag)
                    {
                        s14 = "BGCOLOR=\"#557755\"";
                    } else
                    {
                        s14 = "BGCOLOR=\"#007700\"";
                    }
                }
                String s16 = stringbuffer.toString();
                if(stringbuffer.toString().matches("remote:\\d+"))
                {
                    s16 = Machine.getMachineName(stringbuffer.toString());
                }
                if(flag2)
                {
                    for(int k1 = 0; k1 < as1.length; k1++)
                    {
                        String as4[] = as1[k1];
                        if(as4.length == 2 && s12.indexOf(as4[0]) >= 0)
                        {
                            s12 = s12.replaceAll(as4[0], as4[1]);
                        }
                    }

                }
                if(flag)
                {
                    s12 = s12.replaceAll("_", " ");
                }
                HTMLPage.append("<TR><TD>" + s12 + "</TD><TD " + s14 + ">" + s16 + "</TD><TD>&nbsp;</TR>\n");
            } while(true);
            HTMLPage.append("<P>\n");
            ssrtn = checkMonitorParams(s1, hashmap3);
            if(ssrtn.isErrorList())
            {
                HashMap hashmap5 = ssrtn.getErrorList();
                for(Enumeration enumeration3 = (Enumeration) hashmap5.keys(); enumeration3.hasMoreElements();)
                {
                    java.lang.Object obj = enumeration3.nextElement();
                    String s15 = (String)hashmap5.get(obj);
                    String s17;
                    if(obj instanceof StringProperty)
                    {
                        s17 = ((StringProperty)obj).getName();
                    } else
                    {
                        s17 = (String)obj;
                    }
                    HTMLPage.append("<TR><TD>" + s17 + "</TD><TD>&nbsp;</TD>" + "<TD BGCOLOR=\"#DD0000\">" + s15 + "</TD></TR>\n");
                    flag3 = true;
                }

            }
            HTMLPage.append("</TABLE>\n");
            if(ssrtn.isError())
            {
                HTMLPage.append("<P><B>Errors found with this monitor:<br>\n");
                HTMLPage.append(ssrtn.errormsg() + ": " + ssrtn.message() + "<br>\n");
                flag3 = true;
            }
        }

        if(!flag)
        {
            if(flag3)
            {
                HTMLPage.append("<P><b>Errors were found with this monitor set. The errors should be corrected before creating these monitors otherwise invalid monitors may be created.</b>\n");
            } else
            {
                HTMLPage.append("<p>No errors detected in Monitor Set syntax. Click 'Create' button to create the monitors with these settings. <P>\n");
            }
        }
        HTMLPage.append("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n<input type=hidden name=page value=\"" + getNextPage(request.getValue("operation")) + "\">\n" + "<input type=hidden name=operation value=" + "monitorSetCreate" + " >\n" + "<input type=hidden name=account value= \"" + request.getAccount() + "\">\n" + "<input type=hidden name=group value= \"" + s1 + "\">\n" + "<input type=hidden name=" + "solution" + " value= \"" + request.getValue("solution") + "\">\n" + "<input type=hidden name=templatefile value= \"" + s + "\">\n");
        String s7;
        String s9;
        for(Enumeration enumeration = (Enumeration) hashmap.keys(); enumeration.hasMoreElements(); HTMLPage.append("<input type=hidden name=\"" + s7 + "\" value=\"" + s9 + "\">\n"))
        {
            s7 = (String)enumeration.nextElement();
            s9 = TextUtils.getValue(hashmap, s7);
        }

        HashMap hashmap4;
        for(Enumeration enumeration1 = hashmap1.elements(); enumeration1.hasMoreElements(); HTMLPage.append("<input type=hidden name=\"" + (String)hashmap4.get("name") + "\" value=\"" + (String)hashmap4.get("value") + "\">\n"))
        {
            hashmap4 = (HashMap)enumeration1.nextElement();
        }

        HTMLPage.append("<BR><input type=submit value=Create>" + (flag ? " Solution" : " Monitor Set") + "\n<P>\n");
        HTMLPage.append("\n</FORM>");
        return ssrtn;
    }

    protected String getNextPage(String s)
    {
        return thisPageName;
    }

    protected HashMap populateForeachVariables()
    {
        HashMap hashmap = new HashMap(true);
        Enumeration enumeration = (Enumeration) request.variables.keys();
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            String s = (String)enumeration.nextElement();
            java.util.regex.Matcher matcher = FOREACH_VARNAME_PATTERN.matcher(s);
            if(matcher.matches())
            {
                HashMap hashmap1 = new HashMap();
                String s1 = matcher.group(1).replaceAll(" ", "");
                String s2 = request.getValue(s);
                hashmap1.put("name", s);
                hashmap1.put("value", s2);
                hashmap.add(s1, hashmap1);
            }
        } while(true);
        return hashmap;
    }

    protected boolean doNotDeployMonitor(HashMap hashmap)
    {
        if(hashmap.get("_deployControlVar") != null)
        {
            if(TextUtils.getValue(hashmap, "_deployControlVar").length() == 0)
            {
                return true;
            }
            hashmap.remove("_deployControlVar");
        }
        return false;
    }

    SSrtn createSet()
        throws java.lang.Exception
    {
        SSrtn ssrtn = new SSrtn();
        String s = I18N.toDefaultEncoding(request.getValue("group"));
        String s1 = request.getValue("templatefile");
        String s2 = request.getValue("debug");
        boolean flag = s2.length() > 0;
        boolean flag1 = request.getValue("solution").length() > 0;
        String s3 = getTemplateFilePath(s1);
        monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s3);
        setHelpLink(monitorsettemplate);
        String s4 = monitorsettemplate.getName();
        HTMLPage.append("<p><h3>Create " + (flag1 ? "Solution Set for " : "Monitor Set for template: ") + s4 + (flag1 ? "..." : "") + "</h3>");
        HashMap hashmap = populateRegularVariables(monitorsettemplate, false);
        HashMap hashmap1 = populateForeachVariables();
        monitorsettemplate.replaceAllVariables(hashmap, hashmap1);
        if(!flag1)
        {
            HTMLPage.append("</p><p>Creating Monitor Configurations...<p>");
        }
        int i = monitorsettemplate.getMonitorCount();
        for(int j = 0; j < i; j++)
        {
            HashMap hashmap2 = monitorsettemplate.getNthMonitor(j);
            if(doNotDeployMonitor(hashmap2))
            {
                continue;
            }
            hashmap2.remove("_internalId");
            String s6 = TextUtils.getValue(hashmap2, "_class");
            String s7 = TextUtils.getValue(hashmap2, "_name");
            HTMLPage.append("<p>Creating Monitor: " + s6 + ": " + s7 + " ... ");
            if(flag)
            {
                Enumeration enumeration = (Enumeration) hashmap2.keys();
                do
                {
                    if(!enumeration.hasMoreElements())
                    {
                        break;
                    }
                    String s8 = (String)enumeration.nextElement();
                    if(!s8.equals("_class") && !s8.equals("_id") && !s8.equals("_group"))
                    {
                        Array array = TextUtils.getMultipleValues(hashmap2, s8);
                        StringBuffer stringbuffer = new StringBuffer();
                        for(int k = 0; k < array.size(); k++)
                        {
                            if(k > 0)
                            {
                                stringbuffer.append("<br>");
                            }
                            stringbuffer.append((String)array.get(k));
                        }

                        HTMLPage.append(s8 + ": " + stringbuffer + "<br>\n");
                    }
                } while(true);
                HTMLPage.append("<P>\n");
            }
            try
            {
                ssrtn = createMonitor(s, hashmap2);
            }
            catch(java.lang.Exception exception)
            {
                ssrtn.setError(SSrtn.ERR_Exception, "Exception: " + exception);
            }
            if(ssrtn.isError())
            {
                return ssrtn;
            }
            HTMLPage.append("Successfully created monitor. Check group page for status.<br></p>\n");
        }

        SiteViewGroup.updateStaticPages(s);
        String s5 = "";
        if(request.getValue("refreshURL").length() > 0)
        {
            s5 = "/SiteView/cgi/go.exe/SiteView?page=" + request.getValue("refreshURL") + "&account=" + request.getAccount();
            s = "Previous";
        } else
        {
            s5 = CGI.getGroupDetailURL(request, s);
        }
        HTMLPage.append("<HR><A HREF=" + s5 + ">Return to " + s + "</A><P>");
        return ssrtn;
    }

    protected String replaceBoolean(String s, monitorSetTemplate monitorsettemplate)
    {
        String s1 = request.getValue(s);
        HashMap hashmap = monitorsettemplate.getVariableInfo(s);
        if(hashmap.get("_boolean") != null)
        {
            String s2 = "";
            if(s1.length() > 0)
            {
                s2 = (String)hashmap.get("_onTrue");
            } else
            {
                s2 = (String)hashmap.get("_onFalse");
            }
            monitorsettemplate.replaceVariable("BOOLEAN[" + s + "]", s2);
        }
        return s1;
    }

    public String[] getAvailableTemplates(String s, String s1)
    {
        java.io.File file = new File(s);
        String as[];
        if(file.exists())
        {
            monitorSetFilter monitorsetfilter = new monitorSetFilter(s1);
            as = file.list(monitorsetfilter);
        } else
        {
            as = null;
        }
        return as;
    }

    public String[] getAvailableTemplates()
    {
        return getAvailableTemplates(TEMPLATES_DIR, monitorSetTemplate.fileSuffix);
    }

    public String[] getTemplateNames(String as[])
    {
        return getTemplateNames(as, TEMPLATES_DIR);
    }

    public String[] getTemplateNames(String as[], String s)
    {
        String as1[] = new String[as.length];
        for(int i = 0; i < as.length; i++)
        {
            String s1 = s + java.io.File.separator + as[i];
            monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s1);
            as1[i] = monitorsettemplate.getName();
        }

        return as1;
    }

    public String[] getTemplateDescriptions(String as[], String s)
    {
        String as1[] = new String[as.length];
        for(int i = 0; i < as.length; i++)
        {
            String s1 = s + java.io.File.separator + as[i];
            monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s1);
            as1[i] = monitorsettemplate.getSetDescriptor();
        }

        return as1;
    }

    public String[] getTemplateMoreInfo(String as[], String s)
    {
        String as1[] = new String[as.length];
        for(int i = 0; i < as.length; i++)
        {
            String s1 = s + java.io.File.separator + as[i];
            monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s1);
            as1[i] = monitorsettemplate.getSetting("_monitorTemplateMoreInfo");
        }

        return as1;
    }

    public String[] getTemplateSolutionPages(String as[], String s)
    {
        String as1[] = new String[as.length];
        for(int i = 0; i < as.length; i++)
        {
            String s1 = s + java.io.File.separator + as[i];
            monitorSetTemplate monitorsettemplate = new monitorSetTemplate(s1);
            as1[i] = monitorsettemplate.getSetting("_solutionSetPage");
            if(as1[i].length() == 0)
            {
                as1[i] = thisPageName;
            }
        }

        return as1;
    }

    public String[] getTemplateDescriptions(String as[])
    {
        return getTemplateDescriptions(as, TEMPLATES_DIR);
    }

    public String[] getTemplateDescriptions(String s, String s1)
    {
        return getTemplateDescriptions(new String[] {
            s
        }, s1);
    }

    boolean checkPermissions(String s, HashMap hashmap, AtomicMonitor atomicmonitor, SSrtn ssrtn)
        throws java.lang.Exception
    {
        String s1 = TextUtils.getValue(hashmap, "_class");
        String s2 = request.getPermission("_monitorType", s1);
        if(s2.equals("optional"))
        {
            ssrtn.setError(SSrtn.ERR_MonNotAvailable, "This monitor cannot be part of this monitor set");
            return false;
        }
        SiteViewObject siteviewobject = Portal.getSiteViewForID(s);
        Monitor monitor = (Monitor)siteviewobject.getElement(s);
        if(TextUtils.toInt(s2) > 0)
        {
            int i = monitor.countMonitorsOfClass(s1);
            if(i >= TextUtils.toInt(s2))
            {
                ssrtn.setError(SSrtn.ERR_MonTooManyClass, "This monitor cannot be part of this monitor set");
                return false;
            }
        }
        int j = request.getPermissionAsInteger("_maximumMonitors");
        if(j > 0)
        {
            int k = monitor.countMonitors();
            if(k >= j)
            {
                ssrtn.setError(SSrtn.ERR_MonTooManyGroup, "This monitor cannot be part of this monitor set");
                return false;
            }
        }
        if(LUtils.wouldExceedLimit(atomicmonitor))
        {
            ssrtn.setError(SSrtn.ERR_MonTooManyLicense, "This monitor cannot be part of this monitor set");
            return false;
        }
        if(!LUtils.isMonitorTypeAllowed(atomicmonitor))
        {
            ssrtn.setError(SSrtn.ERR_MonTooManyClass, "This monitor cannot be part of this monitor set");
            return false;
        }
        String s3 = TextUtils.getValue(hashmap, "_getImages");
        String s4 = TextUtils.getValue(hashmap, "_getFrames");
        if((s3.equals("on") || s4.equals("on")) && s1.startsWith("URLRemote") && request.isSiteSeerAccount())
        {
            int l = request.getPermissionAsInteger("_maximumFAndIMonitors");
            if(monitorUtils.countFrameAndImageMonitors(s) > l)
            {
                ssrtn.setError(SSrtn.ERR_MonTooManyFAndI, "This monitor cannot be part of this monitor set");
                return false;
            }
        }
        return true;
    }

    SSrtn createMonitor(String s, HashMap hashmap)
        throws java.lang.Exception
    {
        SSrtn ssrtn = new SSrtn();
        String s1 = TextUtils.getValue(hashmap, "_class");
        HashMap hashmap1 = new HashMap();
        if(s1.indexOf("SubGroup") == -1)
        {
            AtomicMonitor atomicmonitor = AtomicMonitor.MonitorCreate(s1);
            if(!checkPermissions(s, hashmap, atomicmonitor, ssrtn))
            {
                return ssrtn;
            }
            int i = request.getPermissionAsInteger("_minimumFrequency");
            long l = atomicmonitor.getPropertyAsLong(AtomicMonitor.pFrequency);
            java.lang.Object obj = atomicmonitor.getClassProperty("defaultFrequency");
            if(obj instanceof String)
            {
                long l1 = TextUtils.toInt((String)obj);
                if(l1 > 0L)
                {
                    l = l1;
                }
            }
            if(i > 0 && l < (long)i)
            {
                l = i;
            }
            atomicmonitor.setProperty(AtomicMonitor.pFrequency, l);
            String s6 = atomicmonitor.defaultTitle();
            HashMap hashmap4 = new HashMap();
            if(request.hasValue(ServerMonitor.pMachineName.getName()))
            {
                atomicmonitor.setProperty(ServerMonitor.pMachineName, request.getValue(ServerMonitor.pMachineName.getName()));
            }
            Array array2 = atomicmonitor.getProperties();
            array2 = StringProperty.sortByOrder(array2);
            for(Enumeration enumeration =  (Enumeration) array2.iterator(); enumeration.hasMoreElements();)
            {
                StringProperty stringproperty = (StringProperty)enumeration.nextElement();
                String s7 = stringproperty.getName();
                if(stringproperty.isMultiLine)
                {
                    Array array3 = TextUtils.getMultipleValues(hashmap, stringproperty.getName());
                    atomicmonitor.unsetProperty(stringproperty);
                    int k = 0;
                    while(k < array3.size()) 
                    {
                        String s11 = (String)array3.get(k);
                        s11 = atomicmonitor.verify(stringproperty, s11, request, hashmap4);
                        atomicmonitor.addProperty(stringproperty, s11);
                        k++;
                    }
                } else
                {
                    String s9 = TextUtils.getValue(hashmap, s7);
                    if(i > 0 && (stringproperty instanceof FrequencyProperty))
                    {
                        int i1 = TextUtils.toInt(s9);
                        if(i1 != 0 && i1 < i)
                        {
                            hashmap4.put(stringproperty, "For this account, monitors must run at intervals of " + TextUtils.secondsToString(i) + " or more.");
                        }
                    }
                    if((stringproperty instanceof ScalarProperty) && ((ScalarProperty)stringproperty).multiple)
                    {
                        Array array4 = TextUtils.getMultipleValues(hashmap, stringproperty.getName());
                        atomicmonitor.unsetProperty(stringproperty);
                        int j1 = 0;
                        while(j1 < array4.size()) 
                        {
                            String s13 = (String)array4.get(j1);
                            s13 = atomicmonitor.verify(stringproperty, s13, request, hashmap4);
                            atomicmonitor.addProperty(stringproperty, s13);
                            j1++;
                        }
                    } else
                    {
                        if(stringproperty == AtomicMonitor.pName)
                        {
                            if(s9.equals(s6))
                            {
                                atomicmonitor.setProperty(stringproperty, "");
                            } else
                            {
                                atomicmonitor.setProperty(stringproperty, s9);
                            }
                        }
                        atomicmonitor.setProperty(stringproperty, s9);
                    }
                }
            }

            HashMap hashmap5 = getMasterConfig();
            Enumeration enumeration1 = (Enumeration) hashmap5.values("_monitorEditCustom");
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                String s8 = (String)enumeration1.nextElement();
                String as[] = TextUtils.split(s8, "|");
                String s10 = as[0];
                if(s10.length() > 0)
                {
                    if(!s10.startsWith("_"))
                    {
                        s10 = "_" + s10;
                    }
                    String s12 = TextUtils.getValue(hashmap, s10);
                    s12 = s12.replace('\r', ' ');
                    s12 = s12.replace('\n', ' ');
                    atomicmonitor.setProperty(s10, s12);
                }
            } while(true);
            AtomicMonitor.saveThresholds(atomicmonitor, hashmap, hashmap4);
            AtomicMonitor.saveClassifier(atomicmonitor, hashmap, hashmap4);
            AtomicMonitor.saveCustomProperties(getMasterConfig(), atomicmonitor, request);
            if(atomicmonitor.getProperty(AtomicMonitor.pName).length() == 0)
            {
                atomicmonitor.setProperty(AtomicMonitor.pName, atomicmonitor.defaultTitle());
            }
            hashmap1 = atomicmonitor.getValuesTable();
        } else
        {
            String s2 = TextUtils.getValue(hashmap, "_name");
            String s3 = "";
            if(s2.indexOf(' ') != -1)
            {
                s3 = TextUtils.replaceChar(s2, ' ', "_");
            } else
            {
                s3 = s2;
            }
            hashmap1.put("_class", s1);
            hashmap1.put("_group", s3);
            hashmap1.put("_name", s2);
            hashmap1.put("_id", "");
            Array array1 = new Array();
            HashMap hashmap2 = new HashMap();
            hashmap2.put("_name", s2);
            hashmap2.put("_parent", s);
            hashmap2.put("_dependsCondition", "good");
            hashmap2.put("_nextID", "1");
            array1.add(hashmap2);
            try
            {
                FrameFile.writeToFile(Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + TextUtils.getValue(hashmap1, "_group") + ".mg", array1);
            }
            catch(java.lang.Exception exception)
            {
                java.lang.System.out.println(exception + "An error occurred when trying to write the file: " + TextUtils.getValue(hashmap1, "_group"));
            }
        }
        Array array = ReadGroupFrames(s);
        String s4 = "";
        int j = array.size();
        HashMap hashmap3 = (HashMap)array.get(0);
        s4 = TextUtils.getValue(hashmap3, "_nextID");
        if(s4.length() == 0)
        {
            s4 = "1";
        }
        hashmap1.remove(Monitor.pID);
        hashmap1.remove("_id");
        hashmap1.put("_id", s4);
        array.insert(j++, j++, hashmap1);
        String s5 = TextUtils.increment(s4);
        hashmap3.put("_nextID", s5);
        WriteGroupFrames(s, array);
        return ssrtn;
    }

    SSrtn checkMonitorParams(String s, HashMap hashmap)
        throws java.lang.Exception
    {
        SSrtn ssrtn = new SSrtn();
        String s1 = TextUtils.getValue(hashmap, "_class");
        String s2;
        if(s1.indexOf("SubGroup") == -1)
        {
            AtomicMonitor atomicmonitor = AtomicMonitor.MonitorCreate(s1);
            if(!checkPermissions(s, hashmap, atomicmonitor, ssrtn))
            {
                return ssrtn;
            }
            int i = request.getPermissionAsInteger("_minimumFrequency");
            long l = atomicmonitor.getPropertyAsLong(AtomicMonitor.pFrequency);
            java.lang.Object obj = atomicmonitor.getClassProperty("defaultFrequency");
            if(obj instanceof String)
            {
                long l1 = TextUtils.toInt((String)obj);
                if(l1 > 0L)
                {
                    l = l1;
                }
            }
            if(i > 0 && l < (long)i)
            {
                l = i;
            }
            atomicmonitor.setProperty(AtomicMonitor.pFrequency, l);
            HashMap hashmap1 = new HashMap();
            ssrtn.setErrorList(hashmap1);
            if(request.hasValue(ServerMonitor.pMachineName.getName()))
            {
                atomicmonitor.setProperty(ServerMonitor.pMachineName, request.getValue(ServerMonitor.pMachineName.getName()));
            }
            Array array = atomicmonitor.getProperties();
            array = StringProperty.sortByOrder(array);
            Enumeration enumeration = (Enumeration) array.iterator();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                StringProperty stringproperty = (StringProperty)enumeration.nextElement();
                if(stringproperty.isMultiLine)
                {
                    Array array1 = TextUtils.getMultipleValues(hashmap, stringproperty.getName());
                    int j = 0;
                    while(j < array1.size()) 
                    {
                        String s5 = (String)array1.get(j);
                        s5 = atomicmonitor.verify(stringproperty, s5, request, hashmap1);
                        atomicmonitor.addProperty(stringproperty, s5);
                        j++;
                    }
                } else
                {
                    String s3 = stringproperty.getName();
                    String s4 = TextUtils.getValue(hashmap, s3);
                    if((stringproperty instanceof ScalarProperty) && ((ScalarProperty)stringproperty).multiple)
                    {
                        Array array2 = TextUtils.getMultipleValues(hashmap, stringproperty.getName());
                        for(int i1 = 0; i1 < array2.size(); i1++)
                        {
                            String s6 = (String)array2.get(i1);
                            s6 = atomicmonitor.verify(stringproperty, s6, request, hashmap1);
                            atomicmonitor.addProperty(stringproperty, s6);
                        }

                    } else
                    {
                        s4 = atomicmonitor.verify(stringproperty, s4, request, hashmap1);
                        atomicmonitor.addProperty(stringproperty, s4);
                    }
                    if(i > 0 && (stringproperty instanceof FrequencyProperty))
                    {
                        int k = TextUtils.toInt(s4);
                        if(k != 0 && k < i)
                        {
                            hashmap1.put(stringproperty, "For this account, monitors must run at intervals of " + TextUtils.secondsToString(i) + " or more.");
                        }
                    }
                }
            } while(true);
            AtomicMonitor.checkThresholds(hashmap, hashmap1);
        } else
        {
            s2 = TextUtils.getValue(hashmap, "_group");
        }
        return ssrtn;
    }

    protected HashMap populateRegularVariables(monitorSetTemplate monitorsettemplate, boolean flag)
    {
        String as[] = monitorsettemplate.getVariables();
        HashMap hashmap = new HashMap();
        for(int i = 0; i < as.length; i++)
        {
            String s = request.getValue(as[i]);
            if(flag && s.length() <= 0 && !request.getValue(as[i] + "remotes").equals("other"))
            {
                String s1 = request.getValue(as[i] + "remotes");
                s = "remote:" + s1;
            }
            hashmap.add(as[i], s);
        }

        return hashmap;
    }

    static 
    {
        TEMPLATES_DIR = Platform.getRoot() + java.io.File.separator + "templates.sets";
        SOLUTIONS_DIR = Platform.getRoot() + java.io.File.separator + "templates.solutions";
    }
}
