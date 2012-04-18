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

import java.util.Enumeration;
import java.util.Vector;

import com.recursionsw.jgl.HashMap;
import COM.dragonflow.Utils.HTMLTagParser;

// Referenced classes of package COM.dragonflow.Page:
// monitorPage, CGI

public class tranWizardPage extends COM.dragonflow.Page.monitorPage
{

    String addMonitor;
    String updateMonitor;
    String addStep;
    String stepBack;
    String otherHelp;
    String urlType;
    String urlPrefix;
    String urlHelp;
    String linkType;
    String linkHelp;
    String formType;
    String formHelp;
    String inputHelp;
    String frameType;
    String frameHelp;
    String refreshType;
    String refreshHelp;
    String openBrace;
    String closeBrace;
    char cOpenBrace;
    char cCloseBrace;
    static final String TARGET_TAGS[] = {
        "/A", "INPUT", "/FORM", "/OPTION", "/SELECT", "/TEXTAREA", "FRAME", "BASE", "AREA", "META", 
        "IFRAME"
    };
    static final String FORM_INPUT_TAGS[] = {
        "INPUT", "SELECT"
    };
    int displayMax;
    int formNameMax;
    int nextStep;
    String thisRef;
    String thisType;
    String thisPostData;
    String lastRef;
    String lastType;
    String lastPostData;
    String thisEncoding;
    static final boolean $assertionsDisabled; /* synthetic field */

    public tranWizardPage()
    {
        addMonitor = "Add Monitor";
        updateMonitor = "Update Monitor";
        addStep = "Add Step";
        stepBack = "Back 1 Step";
        otherHelp = "Select desired radio button and enter field here<P>";
        urlType = "url";
        urlPrefix = "http://";
        urlHelp = "Example: http://demo.siteview.com<P>";
        linkType = "link";
        linkHelp = "This is a list of available links on this page<P>";
        formType = "form";
        formHelp = "This is a list of available Submit buttons on this page, the format is {FormName}ButtonName";
        inputHelp = "This is a list of available input items on this page, the format is {FormName}InputName<P>";
        frameType = "frame";
        frameHelp = "This is a list of available frames on this page<p>";
        refreshType = "refresh";
        refreshHelp = "This is a list of available meta refresh items on this page<p>";
        openBrace = "{";
        closeBrace = "}";
        cOpenBrace = openBrace.charAt(0);
        cCloseBrace = closeBrace.charAt(0);
        displayMax = 80;
        formNameMax = 30;
        nextStep = 0;
        thisRef = "";
        thisType = "";
        thisPostData = "";
        lastRef = "";
        lastType = "";
        lastPostData = "";
        thisEncoding = "";
    }

    void printVars(String s)
    {
        java.lang.System.out.println(s);
        String s1;
        for(Enumeration enumeration = request.getVariables(); enumeration.hasMoreElements(); java.lang.System.out.println(s1 + "=" + request.getValue(s1)))
        {
            s1 = (String)enumeration.nextElement();
        }

        java.lang.System.out.println(s);
    }

    String trimNL(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s);
        int i = stringbuffer.length();
        for(int j = stringbuffer.length() - 1; j >= 0 && (stringbuffer.charAt(j) == '\n' || stringbuffer.charAt(j) == '\r'); j--)
        {
            i = j;
        }

        stringbuffer.setLength(i);
        return stringbuffer.toString();
    }

    public String referenceTypeOptions(int i)
    {
        String s = request.getValue("_referenceType" + i);
        String s1 = s.equals("url") ? "selected" : "";
        String s2 = s.equals("link") ? "selected" : "";
        String s3 = s.equals("form") ? "selected" : "";
        String s4 = !s.equals("frame") && !s.equals("iframe") ? "" : "selected";
        String s5 = s.equals("refresh") ? "selected" : "";
        String s6 = "<option value=url " + s1 + ">URL</option>";
        if(i > 1)
        {
            s6 = s6 + "<option value=link " + s2 + ">Link</option>" + "<option value=form " + s3 + ">Form Submit Button</option>" + "<option value=frame " + s4 + ">Frame Name</option>" + "<option value=refresh " + s5 + ">Refresh URL</option>";
        }
        return s6;
    }

    void printHeaders(String s, String s1)
    {
        String s2 = "URLRemoteSequenceMonitor";
        if(s2.equals(s))
        {
            s2 = "Global ";
        } else
        {
            s2 = "";
        }
        printBodyHeader("Sequence Wizard");
        printButtonBar("URLSeqMon.htm", "");
        outputStream.println("<p><H2>Add " + s2 + "URL Sequence Monitor in Group : <A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s1) + ">" + COM.dragonflow.Page.CGI.getGroupName(s1) + "</a></H2>\n");
        outputStream.println("<P>\n" + getPagePOST("tranWizard", request.getValue("operation")) + "\n");
    }

    void printErrorHtml(String s, StringBuffer stringbuffer)
    {
        printBodyHeader("Sequence Wizard");
        printButtonBar("URLSeqMon.htm", "");
        outputStream.println("<P>\n<H2>Add URL Sequence Error</H2>\n<P><br>There was an error checking the next step of the URL Sequence:<blockquote><b>" + s + "</b></blockquote>\n" + "<P><br>Press BACK on your browser to continue\n");
        if(stringbuffer.length() > 0)
        {
            outputStream.println("<br><br>");
            int i = stringbuffer.toString().toLowerCase().lastIndexOf("<hr><b><a name=step");
            if(i >= 0 && i < stringbuffer.length())
            {
                outputStream.println(stringbuffer.toString().substring(i));
            }
        }
        printFooter(outputStream);
    }

    void postToMonitorPage()
    {
        request.requestMethod = "POST";
        request.setValue("page", "monitor");
        if(!request.getValue("operation").equals("Add"))
        {
            request.setValue("operation", "Edit");
        }
        try
        {
            outputStream.println("Content-type: text/html\n\n<html>");
            java.lang.Class class1 = java.lang.Class.forName("COM.dragonflow.Page.monitorPage");
            COM.dragonflow.Page.CGI cgi = (COM.dragonflow.Page.CGI)class1.newInstance();
            cgi.request = request;
            cgi.outputStream = outputStream;
            cgi.printBody();
            outputStream.println("</HTML>");
        }
        catch(java.lang.ClassNotFoundException classnotfoundexception)
        {
            String s = "";
            if(request != null)
            {
                s = request.getURL();
            }
            COM.dragonflow.HTTP.HTTPRequest.printErrorMessage(request, 404, s, classnotfoundexception, outputStream);
        }
        catch(java.lang.Exception exception)
        {
            String s1 = "";
            if(request != null)
            {
                s1 = request.getURL();
            }
            COM.dragonflow.HTTP.HTTPRequest.printErrorMessage(request, 999, s1, exception, outputStream);
        }
    }

    void removeStepVariables(int i)
    {
        request.unsetValue("_reference" + i);
        request.unsetValue("_referenceType" + i);
        request.unsetValue("_postData" + i);
        request.unsetValue("_stepDelay" + i);
        request.unsetValue("_username" + i);
        request.unsetValue("_password" + i);
        request.unsetValue("_domain" + i);
        request.unsetValue("_whenToAuthenticate" + i);
        request.unsetValue("_content" + i);
        request.unsetValue("_errorContent" + i);
        request.unsetValue("_encoding" + i);
        request.unsetValue("_URLDropDownEncodePostData" + i);
    }

    void setStrings(int i)
    {
        thisRef = "_reference" + i;
        thisType = "_referenceType" + i;
        thisPostData = "_postData" + i;
        thisEncoding = "_encoding" + i;
        lastRef = "_reference" + (i - 1);
        lastType = "_referenceType" + (i - 1);
        lastPostData = "_postData" + (i - 1);
    }

    void printPriorStep(int i)
    {
        String s = request.getValue("_reference" + i);
        String s1 = request.getValue("_encoding" + i);
        s = COM.dragonflow.Utils.I18N.escapeString(s, s1);
        outputStream.print("<TR><TD ALIGN=RIGHT>Step " + i + "</TD><TD>" + request.getValue("_referenceType" + i) + "</TD>" + "<TD>" + COM.dragonflow.Utils.I18N.escapeString(request.getValue("_reference" + i), s1) + "</TD>" + "<TD><input type=hidden name=_reference" + i + " value=\"" + COM.dragonflow.Utils.I18N.escapeString(request.getValue("_reference" + i), s1) + "\"></TD>" + "<TD><input type=hidden name=_referenceType" + i + " value=\"" + COM.dragonflow.Utils.I18N.escapeString(request.getValue("_referenceType" + i), s1) + "\"></TD>" + "</TR>\n");
        if(request.getValue("_referenceType" + i).equals("form"))
        {
            outputStream.print("<TR><TD></TD><TD></TD><TD>" + request.getValue("_postData" + i) + "</TD>" + "</TR>\n");
        }
    }

    void printUrlOption(int i)
    {
        if(nextStep == 1)
        {
            outputStream.print("<TR><TD ALIGN=RIGHT>Step 1</TD><TD></TD><TD>Start by entering the URL of the first page of the sequence</TD></TR>");
        }
        outputStream.print("<TR><TD></TD><TD ALIGN=RIGHT>URL: <input type=radio name=\"" + thisType + "\" value= \"" + urlType + "\" CHECKED></TD><TD>Enter URL in \"Other\" Field below.</TD></TR>" + "<TR><TD></TD><TD></TD><TD><small>" + urlHelp + "</small></TD></TR>" + "<TR><TD></TD><TD ALIGN=RIGHT>Other:</TD><TD><input type=text name=urlReference value=\"");
        if(thisType.equals(urlType) && request.getValue(thisRef).length() > 0)
        {
            outputStream.print(COM.dragonflow.Utils.I18N.escapeString(request.getValue(thisRef), request.getValue(thisEncoding)));
        } else
        {
            outputStream.print(urlPrefix);
        }
        outputStream.print("\" size=80></TD></TR>\n<TR><TD></TD><TD></TD><TD><small>" + otherHelp + "</small><p></TD></TR>");
        if(nextStep == 1)
        {
            outputStream.print("<TR><TD></TD><TD>POST Data</TD><TD ALIGN=LEFT><TEXTAREA name=formData rows=4 cols=80></TEXTAREA></TD></TR><TR><TD></TD><TD></TD><TD><small>optional, enter name=value variables, one per line, to send with a POST request for this step</small><p></TD></TR>");
        }
    }

    void printEncodingOption()
    {
        outputStream.print("<TR><TD></TD><TD></TD><TD><small>" + urlHelp + "</small></TD></TR>" + "<TR><TD></TD><TD ALIGN=RIGHT>Encoding Character Set:</TD><TD><input type=text name=_encoding" + nextStep + " value=\"");
        String s = COM.dragonflow.Page.tranWizardPage.getStepEncoding(nextStep - 1, request);
        outputStream.print(s + "\" size=80></TD></TR>\n" + "<TR><TD></TD><TD></TD><TD><small>Enter code page (ie Cp1252 or Shift_JIS or EUC-JP)</small><p></TD></TR>");
    }

    void printFloatingWindowOption()
    {
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        String s = siteviewgroup.getSetting("_transFloatWindow");
        outputStream.print("<TR><TD></TD><TD></TD><TD><small>" + urlHelp + "</small></TD></TR>" + "<TR><TD></TD><TD ALIGN=RIGHT></TD><TD><input type=checkbox name=floatWindow");
        outputStream.print(" " + (s.length() <= 0 ? "" : "CHECKED") + "> Displays HTML in a Floating Window.</TD></TR>\n" + "<TR><TD></TD><TD></TD><TD><small>When checked, this displays the HTML at the bottom of the page in a floating browser window.</small><p></TD></TR>");
    }

    boolean displayList(Enumeration enumeration, String s, StringBuffer stringbuffer, boolean flag)
    {
        String s1 = COM.dragonflow.Page.tranWizardPage.getStepEncoding(nextStep - 1, request);
        if(enumeration.hasMoreElements() || flag)
        {
            String s2 = "";
            String s3 = "";
            String s4 = "";
            String s5 = "";
            if(s.equals("link"))
            {
                s5 = "Link:";
                s2 = linkType;
                s3 = linkHelp;
                s4 = "getLinks Enumeration Error";
            } else
            if(s.equals("form"))
            {
                s5 = "Form:";
                s2 = formType;
                s3 = formHelp;
                s4 = "getForms Enumeration Error";
            } else
            if(s.equals("frame") || s.equals("iframe"))
            {
                s5 = "Frame:";
                s2 = frameType;
                s3 = frameHelp;
                s4 = "getFrames Enumeration Error";
            } else
            if(s.equals("refresh"))
            {
                s5 = "Refresh:";
                s2 = refreshType;
                s3 = refreshHelp;
                s4 = "getRefresh Enumeration Error";
            }
            outputStream.print("<TR><TD ALIGN=RIGHT></TD><TD ALIGN=RIGHT>" + s5 + "<input type=radio name=\"" + thisType + "\" value= \"" + s2 + "\"></TD>" + "<TD><select name=" + s + "Reference>");
            if(enumeration.hasMoreElements())
            {
                String s6;
                String s7;
                for(; enumeration.hasMoreElements(); outputStream.print("<option value=\"" + COM.dragonflow.Utils.I18N.escapeString(s6, s1) + "\">" + COM.dragonflow.Utils.I18N.escapeString(s7, s1) + "</option>"))
                {
                    s6 = (String)enumeration.nextElement();
                    if(!enumeration.hasMoreElements())
                    {
                        printErrorHtml(s4, stringbuffer);
                        return false;
                    }
                    s7 = (String)enumeration.nextElement();
                }

            } else
            {
                outputStream.print("<option value=\"\">&#060;n/a&#062;</option>");
            }
            outputStream.print("</select></TD></TR>\n<TR><TD></TD><TD></TD><TD><small>" + s3 + "</small></TD></TR>");
        }
        return true;
    }

    Enumeration getRefresh(COM.dragonflow.Utils.HTMLTagParser htmltagparser, COM.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        java.util.Vector vector = new Vector();
        Enumeration enumeration = htmltagparser.findTags("meta");
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            HashMap hashmap = (HashMap)enumeration.nextElement();
            String s = new String("");
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "http-equiv").equalsIgnoreCase("refresh"))
            {
                String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "content").trim();
                vector.addElement(s1);
                vector.addElement(s1.length() <= displayMax ? ((java.lang.Object) (s1)) : ((java.lang.Object) (s1.substring(0, displayMax - 1))));
            }
        } while(true);
        return vector.elements();
    }

    Enumeration getFrames(COM.dragonflow.Utils.HTMLTagParser htmltagparser, COM.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        java.util.Vector vector = new Vector();
        Enumeration enumeration = htmltagparser.findTags(frameType);
        Enumeration enumeration1 = htmltagparser.findTags("iframe");
        String s;
        for(; enumeration.hasMoreElements(); vector.addElement(s.length() <= displayMax ? ((java.lang.Object) (s)) : ((java.lang.Object) (s.substring(0, displayMax - 1)))))
        {
            HashMap hashmap = (HashMap)enumeration.nextElement();
            s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "name").trim();
            if(s.length() == 0)
            {
                s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "src").trim();
            }
            vector.addElement(s);
        }

        String s1;
        for(; enumeration1.hasMoreElements(); vector.addElement(s1.length() <= displayMax ? ((java.lang.Object) (s1)) : ((java.lang.Object) (s1.substring(0, displayMax - 1)))))
        {
            HashMap hashmap1 = (HashMap)enumeration1.nextElement();
            s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name").trim();
            if(s1.length() == 0)
            {
                s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "src").trim();
            }
            vector.addElement(s1);
        }

        return vector.elements();
    }

    Enumeration getForms(COM.dragonflow.Utils.HTMLTagParser htmltagparser, COM.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        java.util.Vector vector = new Vector();
        Enumeration enumeration = htmltagparser.findTags(formType);
        int i = 0;
        do
        {
            if(!enumeration.hasMoreElements())
            {
                break;
            }
            i++;
            HashMap hashmap = (HashMap)enumeration.nextElement();
            String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "name").trim();
            if(s.length() == 0)
            {
                s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "action").trim();
            }
            s = "[" + i + "]" + s;
            if(s.length() > formNameMax)
            {
                s = s.substring(0, formNameMax - 1);
            }
            Enumeration enumeration1 = htmltagparser.findTags(hashmap, FORM_INPUT_TAGS);
            int j = 0;
            String s1 = "";
            String s4 = "";
            String s6 = "";
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                String s2 = "";
                String s5 = "";
                String s7 = "";
                HashMap hashmap1 = (HashMap)enumeration1.nextElement();
                String s8 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "tag");
                if(s8.equals("select"))
                {
                    String s9 = null;
                    String s12 = null;
                    Enumeration enumeration2 = htmltagparser.findTags(hashmap1, "option");
                    do
                    {
                        if(!enumeration2.hasMoreElements())
                        {
                            break;
                        }
                        HashMap hashmap2 = (HashMap)enumeration2.nextElement();
                        if(s12 == null)
                        {
                            s12 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "value");
                        } else
                        if(s9 == null && hashmap2.get("selected") != null)
                        {
                            s9 = COM.dragonflow.Utils.TextUtils.getValue(hashmap2, "value");
                        }
                    } while(true);
                    if(s9 == null)
                    {
                        s9 = s12;
                    }
                    s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                    s7 = s9;
                } else
                {
                    String s10 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "type").toLowerCase();
                    if(s10.equals("submit"))
                    {
                        j++;
                        s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                        if(s2.length() == 0)
                        {
                            s2 = "Submit";
                        }
                    } else
                    if(s10.equals("image"))
                    {
                        j++;
                        s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                        if(s2.length() == 0)
                        {
                            s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "alt");
                            if(s2.length() == 0)
                            {
                                s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                                if(s2.length() == 0)
                                {
                                    s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "src");
                                    if(s2.length() == 0)
                                    {
                                        s2 = "[" + j + "]";
                                    }
                                }
                            }
                        }
                    } else
                    if(s10.equals("text") || s10.equals("password") || s10.equals("hidden") || s10.equals("checkbox") || s10.equals(""))
                    {
                        s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                        s7 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                    } else
                    if(s10.equals("radio") && hashmap1.get("checked") != null)
                    {
                        s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "name");
                        s7 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "value");
                    }
                    if(s2.length() > 0)
                    {
                        String s13 = "";
                        if(i > 1)
                        {
                            Enumeration enumeration3 = vector.elements();
                            if(enumeration3.hasMoreElements())
                            {
                                String s14 = (String)enumeration3.nextElement();
                                if(s2.equals(s14))
                                {
                                    s13 = "[" + i + "]";
                                    break;
                                }
                                s14 = (String)enumeration3.nextElement();
                            }
                        }
                        if(s2.length() > displayMax / 2)
                        {
                            s2 = s2.substring(0, displayMax / 2 - 1);
                        }
                        vector.addElement(openBrace + s + closeBrace + s2 + s13);
                        vector.addElement(openBrace + s + closeBrace + s2);
                    }
                }
                if(s5.length() > 0)
                {
                    String s11 = atomicmonitor.getProperty(thisPostData);
                    atomicmonitor.setProperty(thisPostData, s11 + (s11.length() <= 0 ? "" : "\n") + openBrace + s + closeBrace + s5 + "=" + s7);
                }
            } while(true);
            if(j == 0)
            {
                String s3 = "[" + i + "]";
                vector.addElement(openBrace + s + closeBrace + s3);
                vector.addElement(openBrace + s + closeBrace + s3);
            }
        } while(true);
        return vector.elements();
    }

    Enumeration getLinks(COM.dragonflow.Utils.HTMLTagParser htmltagparser, COM.dragonflow.SiteView.AtomicMonitor atomicmonitor)
    {
        java.util.Vector vector = new Vector();
        String s;
        for(Enumeration enumeration = htmltagparser.findTags("a"); enumeration.hasMoreElements(); vector.addElement(s.length() <= displayMax ? ((java.lang.Object) (s)) : ((java.lang.Object) (s.substring(0, displayMax - 1)))))
        {
            HashMap hashmap = (HashMap)enumeration.nextElement();
            s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "contents").trim();
            vector.addElement(s);
        }

        String s1;
        for(Enumeration enumeration1 = htmltagparser.findTags("area"); enumeration1.hasMoreElements(); vector.addElement(s1.length() <= displayMax ? ((java.lang.Object) (s1)) : ((java.lang.Object) (s1.substring(0, displayMax - 1)))))
        {
            HashMap hashmap1 = (HashMap)enumeration1.nextElement();
            s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "contents").trim();
            if(s1.length() == 0 || s1.toLowerCase().lastIndexOf("<img") >= 0)
            {
                s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap1, "href").trim();
            }
            vector.addElement(s1);
        }

        return vector.elements();
    }

    public void printBody()
        throws java.lang.Exception
    {
        StringBuffer stringbuffer = new StringBuffer();
        StringBuffer stringbuffer1 = new StringBuffer();
        boolean flag = false;
        COM.dragonflow.SiteView.AtomicMonitor atomicmonitor = null;
        Array array3 = null;
        String s = request.getValue("group");
        String s1 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s);
        String s2 = COM.dragonflow.Utils.I18N.getDefaultEncoding();
        String s5 = s2;
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if(request.isPost())
        {
            String s10 = request.getValue("floatWindow");
            COM.dragonflow.SiteView.MasterConfig.clearConfigCache();
            siteviewgroup.loadSettings();
            siteviewgroup.unsetProperty("_transFloatWindow");
            if(s10.length() > 0)
            {
                siteviewgroup.setProperty("_transFloatWindow", "CHECKED");
            }
            siteviewgroup.saveSettings();
            COM.dragonflow.SiteView.MasterConfig.clearConfigCache();
        }
        HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        if(s.length() > 0)
        {
            array3 = ReadGroupFrames(s);
        }
        if(request.getValue("operation").equals("Edit") || request.getValue("operation").equals("Tool"))
        {
            addMonitor = updateMonitor;
        }
        if(request.getValue("submit").equals("Wizard"))
        {
            request.unsetValue("submit");
            request.setValue("newWizardStep", request.getValue("WizardStep"));
            request.unsetValue("WizardStep");
            request.unsetValue("tranStep");
            request.setValue("operation", "Edit");
        }
        String s11 = "URLRemoteSequenceMonitor";
        int i = COM.dragonflow.StandardMonitor.URLSequenceMonitor.numberOfSteps;
        if(s11.equals(request.getValue("class")))
        {
            int j = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue((HashMap)array3.get(0), "_URLRemoteSequenceMonitorSteps"));
            if(j <= COM.dragonflow.StandardMonitor.URLSequenceMonitor.numberOfSteps && j != 0)
            {
                i = j;
            }
        }
        if(request.getValue("operation").equals("Tool") && request.getValue("tranStep").length() > 0)
        {
            nextStep = 0;
            for(int k = 1; k <= i && request.getValue("_reference" + k).length() > 0; k++)
            {
                nextStep = k;
            }

            request.setValue("tranStep", "" + (nextStep + 1));
        }
        nextStep = 0;
        if(request.getValue("tranStep").length() > 0)
        {
            nextStep = (new Integer(request.getValue("tranStep"))).intValue();
            if(nextStep < 1 && !request.getValue("submit").equals(addMonitor))
            {
                printErrorHtml("tranStep = " + nextStep + " is an invalid step", stringbuffer);
                return;
            }
            if(nextStep > i && !request.getValue("submit").equals(addMonitor))
            {
                String s12 = "_reference" + nextStep;
                if(!request.getValue(s12).equals(""))
                {
                    printErrorHtml("tranStep = " + nextStep + " exceeds the maximum number of allowed steps.", stringbuffer);
                    return;
                }
            }
        } else
        if(!request.getValue("operation").equals("Add") && request.getValue("group").length() > 0)
        {
            atomicmonitor = COM.dragonflow.SiteView.AtomicMonitor.MonitorCreate(array3, request.getValue("id"), request.getPortalServer());
            Array array = atomicmonitor.getProperties();
            Enumeration enumeration = (Enumeration) array.iterator();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                String s13 = "";
                if((stringproperty instanceof COM.dragonflow.Properties.ScalarProperty) && ((COM.dragonflow.Properties.ScalarProperty)stringproperty).multiple)
                {
                    Enumeration enumeration3 = atomicmonitor.getMultipleValues(stringproperty);
                    while(enumeration3.hasMoreElements()) 
                    {
                        request.addValue(stringproperty.getName(), (String)enumeration3.nextElement());
                    }
                } else
                {
                    if(stringproperty.isMultiLine)
                    {
                        for(Enumeration enumeration4 = atomicmonitor.getMultipleValues(stringproperty); enumeration4.hasMoreElements();)
                        {
                            s13 = s13 + enumeration4.nextElement() + "\n";
                        }

                    } else
                    {
                        s13 = atomicmonitor.getProperty(stringproperty);
                    }
                    request.setValue(stringproperty.getName(), s13);
                    if(stringproperty.getName().equals("_frequency") && (stringproperty instanceof COM.dragonflow.Properties.FrequencyProperty))
                    {
                        request.setValue("_frequencyUnits", "");
                    }
                    if(stringproperty.getName().equals("_errorFrequency") && (stringproperty instanceof COM.dragonflow.Properties.FrequencyProperty))
                    {
                        request.setValue("_errorFrequencyUnits", "");
                    }
                }
            } while(true);
            flag = true;
            for(int l = 1; l <= i && request.getValue("_reference" + l).length() > 0; l++)
            {
                nextStep = l;
            }

            if(request.getValue("newWizardStep").length() > 0)
            {
                int i1 = (new Integer(request.getValue("newWizardStep"))).intValue();
                if(i1 > 0 && i1 < nextStep)
                {
                    nextStep = i1;
                }
            }
            for(int j1 = nextStep + 1; j1 <= i; j1++)
            {
                removeStepVariables(j1);
            }

        }
        if(atomicmonitor == null)
        {
            atomicmonitor = COM.dragonflow.SiteView.AtomicMonitor.MonitorCreate(request.getValue("class"));
        }
        COM.dragonflow.SiteView.SiteViewObject siteviewobject = getSiteView();
        COM.dragonflow.SiteView.MonitorGroup monitorgroup = null;
        if(request.getValue("group").length() > 0)
        {
            monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)siteviewobject.getElement(COM.dragonflow.Page.tranWizardPage.getGroupIDRelative(COM.dragonflow.Utils.I18N.toDefaultEncoding(request.getValue("group"))));
        }
        atomicmonitor.setOwner(monitorgroup);
        String s14 = request.getPermission("_monitorType", (String)atomicmonitor.getClassProperty("class"));
        if(s14.equals("optional"))
        {
            printErrorHtml("Operation not available to current user", stringbuffer);
            return;
        }
        if(request.getValue("submit").equals(addMonitor))
        {
            removeStepVariables(nextStep);
            postToMonitorPage();
            return;
        }
        if(request.getValue("submit").equals(stepBack) && nextStep > 1)
        {
            removeStepVariables(nextStep--);
            removeStepVariables(nextStep--);
            flag = true;
        }
        setStrings(++nextStep);
        if(nextStep > 1)
        {
            String s15 = "";
            if(!flag)
            {
                if(request.getValue("urlReference").length() > 0 && !request.getValue("urlReference").equals(urlPrefix) && !request.getValue(lastType).equals(urlType))
                {
                    request.setValue(lastRef, request.getValue("urlReference"));
                } else
                {
                    request.setValue(lastRef, request.getValue(request.getValue(lastType) + "Reference"));
                }
                String s17 = request.getValue(lastRef);
                if(request.getValue(lastType).equals(formType))
                {
                    int i2 = s17.indexOf(cOpenBrace);
                    int k2 = s17.indexOf(cCloseBrace);
                    if(i2 == 0 && k2 > i2)
                    {
                        s15 = s17.substring(i2 + 1, k2);
                        if(s17.length() > 2 + s15.length())
                        {
                            request.setValue(lastRef, s17.substring(2 + s15.length()));
                        }
                    }
                }
            }
            Array array1 = atomicmonitor.getProperties();
            Enumeration enumeration1 =  (Enumeration) array1.iterator();
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                COM.dragonflow.Properties.StringProperty stringproperty1 = (COM.dragonflow.Properties.StringProperty)enumeration1.nextElement();
                if(stringproperty1.isEditable && !stringproperty1.isMultiLine && !stringproperty1.isAdvanced)
                {
                    if((stringproperty1 instanceof COM.dragonflow.Properties.ScalarProperty) && ((COM.dragonflow.Properties.ScalarProperty)stringproperty1).multiple)
                    {
                        Enumeration enumeration5 = request.getValues(stringproperty1.getName());
                        atomicmonitor.unsetProperty(stringproperty1);
                        for(; enumeration5.hasMoreElements(); atomicmonitor.addProperty(stringproperty1, (String)enumeration5.nextElement())) { }
                    } else
                    {
                        String s18 = stringproperty1.getName();
                        if(s18.equals("_frequency") && (stringproperty1 instanceof COM.dragonflow.Properties.FrequencyProperty))
                        {
                            String s20 = request.getValue(s18);
                            String s24 = request.getValue("_frequencyUnits");
                            boolean flag3 = false;
                            if(s20.length() > 0 && COM.dragonflow.Utils.TextUtils.isInteger(s20))
                            {
                                int i4 = java.lang.Integer.parseInt(s20);
                                COM.dragonflow.Properties.FrequencyProperty _tmp = (COM.dragonflow.Properties.FrequencyProperty)stringproperty1;
                                int k4 = COM.dragonflow.Properties.FrequencyProperty.toSeconds(i4, s24);
                                atomicmonitor.setProperty(stringproperty1, "" + k4);
                            }
                        }
                        if(s18.equals("_name"))
                        {
                            String s21 = request.getValue(s18);
                            atomicmonitor.setProperty(stringproperty1, s21);
                        } else
                        if(s18.indexOf("_encoding") >= 0)
                        {
                            String s22 = request.getValue(s18);
                            atomicmonitor.setProperty(stringproperty1, s22);
                            request.setValue(stringproperty1.getName(), s22);
                        }
                    }
                }
                if(!stringproperty1.isEditable || !stringproperty1.isAdvanced || stringproperty1.isVariableCountProperty() && !stringproperty1.shouldPrintVariableCountProperty(nextStep))
                {
                    continue;
                }
                String s19 = request.getValue(stringproperty1.getName());
                if(stringproperty1.isMultiLine)
                {
                    s19 = trimNL(s19);
                }
                boolean flag2 = request.getValue(lastType).equals(formType);
                atomicmonitor.setProperty(stringproperty1, "");
                if(stringproperty1.getName().equals("_errorFrequency") && (stringproperty1 instanceof COM.dragonflow.Properties.FrequencyProperty))
                {
                    String s25 = request.getValue("_errorFrequencyUnits");
                    if(s19.length() > 0)
                    {
                        COM.dragonflow.Properties.FrequencyProperty _tmp1 = (COM.dragonflow.Properties.FrequencyProperty)stringproperty1;
                        int j4 = COM.dragonflow.Properties.FrequencyProperty.toSeconds(java.lang.Integer.parseInt(s19), s25);
                        atomicmonitor.setProperty(stringproperty1, "" + j4);
                        continue;
                    }
                }
                if(s19.equals("*********") && stringproperty1.isPassword)
                {
                    s19 = COM.dragonflow.Properties.StringProperty.getPrivate(request, stringproperty1.getName(), myVerySecretName(stringproperty1), null, null);
                }
                if(stringproperty1.getName().equals(lastPostData) && !flag)
                {
                    String as[] = COM.dragonflow.Utils.TextUtils.split(request.getValue("formData"), COM.dragonflow.StandardMonitor.URLMonitor.CRLF);
                    String as1[] = COM.dragonflow.Utils.TextUtils.split(s19, COM.dragonflow.StandardMonitor.URLMonitor.CRLF);
                    String s28 = "";
                    for(int i5 = 0; i5 < as.length; i5++)
                    {
                        s19 = as[i5];
                        int l5 = 0;
                        do
                        {
                            if(l5 >= as1.length)
                            {
                                break;
                            }
                            if(s19.equals(as1[l5]))
                            {
                                s19 = "";
                                break;
                            }
                            l5++;
                        } while(true);
                        if(flag2 && s19.startsWith(openBrace + s15 + closeBrace) && s19.length() > 2 + s15.length())
                        {
                            s19 = s19.substring(2 + s15.length());
                        }
                        s28 = s28 + (s28.length() <= 0 ? "" : "\n") + s19;
                    }

                    s19 = s28;
                }
                atomicmonitor.setProperty(stringproperty1, s19);
                request.setValue(stringproperty1.getName(), s19);
            } while(true);
            HashMap hashmap2 = new HashMap();
            super.saveThresholds(atomicmonitor, request, hashmap2);
            StringBuffer stringbuffer2 = new StringBuffer();
            StringBuffer stringbuffer4 = new StringBuffer();
            String s26 = "";
            long al[] = {
                (long)COM.dragonflow.StandardMonitor.URLMonitor.kURLok
            };
            String s3 = COM.dragonflow.Page.tranWizardPage.getStepEncoding(nextStep - 1, request);
            if(request.getValue("_offline").equals("offline"))
            {
                stringbuffer4 = new StringBuffer("<html></html>");
                stringbuffer1 = new StringBuffer("<html></html>");
            } else
            {
                int l4 = 0;
                StringBuffer stringbuffer3 = new StringBuffer();
                stringbuffer4 = new StringBuffer();
                stringbuffer1 = new StringBuffer();
                if(isPortalServerRequest())
                {
                    request.setValue("_location", request.getPortalServer());
                }
                String s30 = request.getValue("_content" + (nextStep - 1));
                if(s30.length() > 0)
                {
                    request.setValue("_content" + (nextStep - 1), COM.dragonflow.Utils.I18N.UnicodeToString(s30, COM.dragonflow.Utils.I18N.nullEncoding()));
                }
                al = COM.dragonflow.StandardMonitor.URLSequenceMonitor.checkURLSequence(request.variables, stringbuffer3, null, stringbuffer, stringbuffer4, stringbuffer1, (COM.dragonflow.StandardMonitor.URLSequenceMonitor)atomicmonitor);
                l4 = (int)al[0];
                if(l4 == COM.dragonflow.StandardMonitor.URLMonitor.kURLok)
                {
                    String s32 = COM.dragonflow.Utils.TextUtils.floatToString((float)al[1] / 1000F, 2) + " sec";
                    s26 = "ok, " + s32;
                } else
                {
                    if(stringbuffer3.length() > 0)
                    {
                        s26 = stringbuffer3.toString();
                    } else
                    {
                        s26 = COM.dragonflow.StandardMonitor.URLMonitor.lookupStatus(l4);
                    }
                    if(request.getValue("operation").equals("Add"))
                    {
                        printErrorHtml(s26, stringbuffer);
                        return;
                    }
                }
            }
            String s29 = null;
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_transFloatWindow").equalsIgnoreCase("CHECKED"))
            {
                s29 = COM.dragonflow.SiteView.Platform.getRoot() + "/htdocs/CurTransStep.html";
                s29 = s29.replace('\\', '/');
            }
            if(request.getValue("operation").equals("Tool"))
            {
                printBodyHeader("Check URL Sequence");
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_transFloatWindow").equalsIgnoreCase("CHECKED"))
                {
                    outputStream.println("<script language=\"JavaScript\">function openHTMLWindow() {t=window.open(\"" + s29 + "\",\"open_window\",\"menubar=no,toolbar=no,dependent,directories=no,status=no,scrollbars=yes,resizable=1,width=640,height=480\");t.focus();}</script>");
                }
                if(!request.getValue("AWRequest").equals("yes"))
                {
                    printButtonBar("URLSeq.htm", "");
                } else
                {
                    outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a></center><p>");
                }
                outputStream.println("<p><H2>Check URL Sequence Monitor in Group : <A HREF=" + COM.dragonflow.Page.CGI.getGroupDetailURL(request, s1) + ">" + COM.dragonflow.Page.CGI.getGroupName(s1) + "</a></H2>\n");
                outputStream.println("<p>\n" + getPagePOST("tranWizard", request.getValue("operation")) + "\nThis form will retrieve a sequence of URLs. \n");
                outputStream.println("<TABLE>");
                outputStream.println("<tr><td></td><td><b>Reference Type</b></td><td><b>Reference</b></td><td><b>Charset Encoding</b></td></tr>");
                String s31 = new String("http://");
                for(int i6 = 1; i6 <= nextStep; i6++)
                {
                    String s4 = COM.dragonflow.Page.tranWizardPage.getStepEncoding(nextStep - 1, request);
                    String s6 = request.getValue("_encoding" + i6);
                    if(s6.length() == 0)
                    {
                        s6 = s4;
                    }
                    if(COM.dragonflow.Utils.I18N.escapeString(request.getValue("_reference" + i6), s6).length() > 0)
                    {
                        s31 = COM.dragonflow.Utils.I18N.escapeString(request.getValue("_reference" + i6), s6);
                    } else
                    if(i6 > 1)
                    {
                        s31 = "";
                    }
                    outputStream.print("<TR><TD ALIGN=RIGHT>Step " + i6 + ":</TD><TD><select name=_referenceType" + i6 + ">" + referenceTypeOptions(i6) + "</select></TD>" + "<TD><input type=text name=_reference" + i6 + " value=\"" + s31 + "\"size=40></TD>" + "<TD><input type=text name=_encoding" + i6 + " value=\"" + s6 + "\"size=20></TD>" + "</TR>\n");
                }

                outputStream.print("</TABLE><BR><P><B>Result: " + COM.dragonflow.Utils.TextUtils.escapeHTML(s26) + "</B><P>");
                int j6;
                for(j6 = 0; request.getValue("_reference" + (j6 + 1)).length() > 0; j6++) { }
                int k6 = 1;
                int l6 = 1;
                for(int i7 = 7; l6 <= j6; i7 += 6)
                {
                    String s35 = "n/a";
                    if(i7 < al.length && al[i7 + 0] >= 0L)
                    {
                        s35 = " in " + COM.dragonflow.Utils.TextUtils.floatToString((float)al[i7 + 0] / 1000F, 2) + " sec";
                    }
                    if(!$assertionsDisabled && al.length <= i7)
                    {
                        throw new AssertionError("results array too small.");
                    }
                    if(al[i7 + 5] > 0L)
                    {
                        outputStream.println("<B>WARNING: " + COM.dragonflow.StandardMonitor.URLMonitor.getURLContentMatchMaxTruncateError(al[i7 + 5]) + "</B><BR>\n");
                    }
                    outputStream.println("<A HREF=#step" + l6 + ">Step " + l6 + " Page Retrieved</A> " + s35 + "<br>\n");
                    String s7 = atomicmonitor.getProperty("_encoding" + l6);
                    String s37 = atomicmonitor.getProperty("_content" + l6);
                    outputStream.println("Match Expression: " + COM.dragonflow.Utils.I18N.escapeString(s37, s7) + "<br>\n");
                    for(; s37.indexOf(")") >= 0; s37 = s37.substring(s37.indexOf(")") + 1))
                    {
                        String s38 = atomicmonitor.getProperty("matchValue" + k6++);
                        outputStream.println("Matched value at: {$$" + l6 + "." + (k6 - 1) + "} = " + COM.dragonflow.Utils.I18N.escapeString(s38, s7) + "<br>\n");
                    }

                    outputStream.println("&nbsp;<br>\n");
                    l6++;
                }

                outputStream.println("<HR>\n");
            } else
            {
                printHeaders(request.getValue("class"), s1);
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_transFloatWindow").equalsIgnoreCase("CHECKED"))
                {
                    outputStream.println("<script language=\"JavaScript\">function openHTMLWindow() {t=window.open(\"" + s29 + "\",\"open_window\",\"menubar=no,toolbar=no,dependent,directories=no,status=no,scrollbars=yes,resizable=1,width=640,height=480\");t.focus();}</script>");
                }
                outputStream.println("<TABLE>");
                outputStream.print("<TR><TD></TD><TD></TD><TD><b>Current Steps</b></TD></TR>");
                for(int j5 = 1; j5 < nextStep; j5++)
                {
                    printPriorStep(j5);
                }

                outputStream.println("<TR><TD></TD><TD></TD><TD>" + s26 + "<p></TD></TR>");
                int k5 = 7 + (nextStep - 2) * 6 + 5;
                if(!$assertionsDisabled && al.length <= k5)
                {
                    throw new AssertionError("results array too small.");
                }
                if(al[k5] > 0L)
                {
                    outputStream.println("<TR><TD></TD><TD></TD><TD><B>WARNING: " + COM.dragonflow.StandardMonitor.URLMonitor.getURLContentMatchMaxTruncateError(al[k5]) + "</B><p></TD></TR>\n");
                }
                outputStream.print("<TR><TD></TD><TD></TD><TD>");
                outputStream.print("<input type=submit name=\"submit\" value=\"" + stepBack + "\">&nbsp;&nbsp;&nbsp;&nbsp; ");
                outputStream.print("<input type=submit name=\"submit\" value=\"" + addMonitor + "\"></TD></TR>");
                outputStream.print("<TR><TD></TD><TD></TD><TD><br>Choose <b>Back</b> to go back to the previous step in the sequence");
                outputStream.print("<TR><TD></TD><TD></TD><TD>Choose <b>" + addMonitor + "</b> when you have added all the steps for this URL Sequence monitor");
                outputStream.print("<p><HR>");
                outputStream.print("<TR><TD></TD><TD></TD><TD><b>Next Step</b></TD></TR>");
                outputStream.print("<TR><TD ALIGN=RIGHT>Step " + nextStep + "</TD><TD></TD><TD>Select which option to use for the next step in the sequence<br></TD></tr>");
                String s33 = COM.dragonflow.StandardMonitor.URLMonitor.getHTTPContent(stringbuffer4.toString());
                if(s33.length() > 0)
                {
                    COM.dragonflow.Utils.HTMLTagParser htmltagparser = new HTMLTagParser(s33, TARGET_TAGS);
                    htmltagparser.ignoreScripts = COM.dragonflow.Page.tranWizardPage.getValue(getMasterConfig(), "_urlHTMLInJavaScript").length() == 0;
                    htmltagparser.ignoreNoscripts = COM.dragonflow.Page.tranWizardPage.getValue(getMasterConfig(), "_urlHTMLInJavaScript").length() == 0;
                    htmltagparser.process();
                    Enumeration enumeration6 = getLinks(htmltagparser, atomicmonitor);
                    if(!displayList(enumeration6, "link", stringbuffer, true))
                    {
                        return;
                    }
                    Enumeration enumeration7 = getForms(htmltagparser, atomicmonitor);
                    if(!displayList(enumeration7, "form", stringbuffer, true))
                    {
                        return;
                    }
                    String s36 = atomicmonitor.getProperty(thisPostData);
                    outputStream.print("<TR><TD></TD><TD></TD><TD ALIGN=LEFT><TEXTAREA name=formData rows=4 cols=80>" + s36 + "</TEXTAREA></TD></TR>" + "<TR><TD></TD><TD></TD><TD><small>" + inputHelp + "</small><p></TD></TR>");
                    Enumeration enumeration8 = getFrames(htmltagparser, atomicmonitor);
                    if(!displayList(enumeration8, "frame", stringbuffer, true))
                    {
                        return;
                    }
                    Enumeration enumeration9 = getRefresh(htmltagparser, atomicmonitor);
                    if(!displayList(enumeration9, "refresh", stringbuffer, true))
                    {
                        return;
                    }
                }
            }
        } else
        if(request.getValue("operation").equals("Tool"))
        {
            printBodyHeader("Check URL Sequence");
            if(!request.getValue("AWRequest").equals("yes"))
            {
                printButtonBar("URLSeq.htm", "");
            } else
            {
                outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a></center><p>");
            }
            outputStream.println("<p>\n<CENTER><H2>Check URL Sequence</H2></CENTER><P>\n<p>\n" + getPagePOST("tranWizard", request.getValue("operation")) + "This form will retrieve a sequence of URLs. \n");
            outputStream.println("<TABLE>");
            String s16 = new String("http://");
            for(int l1 = 1; l1 <= nextStep; l1++)
            {
                String s8 = request.getValue("_encoding" + l1);
                if(request.getValue("_reference" + l1).length() > 0)
                {
                    s16 = COM.dragonflow.Utils.I18N.escapeString(request.getValue("_reference" + l1), s8);
                } else
                if(l1 > 1)
                {
                    s16 = "";
                }
                outputStream.print("<TR><TD ALIGN=RIGHT>Step " + l1 + " Reference:</TD><TD><select name=_referenceType" + l1 + ">" + referenceTypeOptions(l1) + "</select></TD>" + "<TD><input type=text name=_reference" + l1 + " value=\"" + s16 + "\"size=40></TD>" + "</TR>\n");
            }

        } else
        {
            int k1 = request.getPermissionAsInteger("_minimumFrequency");
            long l2 = atomicmonitor.getPropertyAsLong(COM.dragonflow.SiteView.AtomicMonitor.pFrequency);
            java.lang.Object obj = atomicmonitor.getClassProperty("defaultFrequency");
            if(obj instanceof String)
            {
                long l3 = COM.dragonflow.Utils.TextUtils.toInt((String)obj);
                if(l3 > 0L)
                {
                    l2 = l3;
                }
            }
            if(k1 > 0 && l2 < (long)k1)
            {
                l2 = k1;
            }
            atomicmonitor.setProperty(COM.dragonflow.SiteView.AtomicMonitor.pFrequency, l2);
            request.setValue(thisType, urlType);
            request.setValue(thisRef, "");
            request.setValue("_timeout", "60");
            printHeaders(request.getValue("class"), s1);
            outputStream.println("<TABLE>");
        }
        if(!request.getValue("operation").equals("Tool"))
        {
            printUrlOption(nextStep);
            if(COM.dragonflow.Utils.I18N.isI18N)
            {
                printEncodingOption();
            }
            printFloatingWindowOption();
        }
        outputStream.print("<input type=hidden name=tranStep value=\"" + nextStep + "\">\n" + "<input type=hidden name=class value=\"" + request.getValue("class") + "\">\n" + "<input type=hidden name=group value=\"" + request.getValue("group") + "\">\n" + "<input type=hidden name=id value=\"" + request.getValue("id") + "\">\n");
        if(request.getValue("operation").equals("Tool"))
        {
            outputStream.print("</TABLE><TABLE><TR><TD>");
            if(!request.getValue("AWRequest").equals("yes"))
            {
                outputStream.print("<input type=submit name=\"submit\" value=\"Check URL Sequence\"></TD><TD>");
            } else
            {
                outputStream.print("<input type=submit name=\"submit\" value=\"Check URL Sequence\" class=\"VerBl8\"></TD><TD>");
            }
            if(nextStep > 1 && request.getValue("group").length() > 0)
            {
                outputStream.print("<input type=submit name=\"submit\" value=\"" + addMonitor + "\"></TD><TD>");
                outputStream.print("<input type=submit name=\"submit\" value=\"Wizard\">");
                outputStream.print(" (Stop at step <input type=text name=\"WizardStep\" value=\"" + (nextStep - 1) + "\" size=\"1\"> )");
            }
            outputStream.print("</TD></TR></TABLE>");
            outputStream.print("Choose <b>Check URL Sequence</b> to run the sequence<BR>");
            if(nextStep > 1 && request.getValue("group").length() > 0)
            {
                outputStream.print("Choose <b>" + addMonitor + "</b> to save the changes to this URL Sequence monitor<BR>");
            }
        } else
        {
            outputStream.print("<TR><TD></TD><TD></TD><TD>");
            outputStream.print("<input type=submit name=\"submit\" value=\"" + addStep + "\">&nbsp;&nbsp");
            outputStream.print("</TD></TR>");
            outputStream.print("<TR><TD></TD><TD></TD><TD>Choose <b>Add Step</b> to add this step and go on to the next step in the sequence");
        }
        outputStream.println("</table><p><HR><p><table>");
        HashMap hashmap1 = new HashMap();
        boolean flag1 = false;
        Array array2 = COM.dragonflow.Properties.StringProperty.sortByOrder(atomicmonitor.getProperties());
        Enumeration enumeration2 =  (Enumeration) array2.iterator();
        do
        {
            if(!enumeration2.hasMoreElements())
            {
                break;
            }
            COM.dragonflow.Properties.StringProperty stringproperty2 = (COM.dragonflow.Properties.StringProperty)enumeration2.nextElement();
            if(!stringproperty2.isAdvanced && !stringproperty2.isVariableCountProperty() && stringproperty2.isEditable)
            {
                printProperty(stringproperty2, outputStream, atomicmonitor, request, hashmap1, flag1);
            }
        } while(true);
        outputStream.println("</TABLE>");
        outputStream.println("<P><HR><CENTER><B>Advanced Settings</B></CENTER><P>");
        outputStream.println("<TABLE>");
        array2 = COM.dragonflow.Properties.StringProperty.sortByOrder(atomicmonitor.getProperties());
        enumeration2 =  (Enumeration) array2.iterator();
        do
        {
            if(!enumeration2.hasMoreElements())
            {
                break;
            }
            COM.dragonflow.Properties.StringProperty stringproperty3 = (COM.dragonflow.Properties.StringProperty)enumeration2.nextElement();
            String s23 = stringproperty3.getName();
            int j3 = getStep(s23);
            String s9 = request.getValue("_encoding" + j3);
            stringproperty3.setEncoding(s9);
            if(stringproperty3.isEditable && stringproperty3.isAdvanced)
            {
                if(!stringproperty3.isVariableCountProperty() || stringproperty3.shouldPrintVariableCountProperty(nextStep))
                {
                    if(s23.equals(thisPostData) && !request.getValue("operation").equals("Tool"))
                    {
                        outputStream.println("<input type=hidden name=" + s23 + " value=\"" + COM.dragonflow.Utils.I18N.escapeString(atomicmonitor.getProperty(stringproperty3), s9) + "\">");
                    } else
                    if(stringproperty3.isPassword)
                    {
                        StringBuffer stringbuffer5 = new StringBuffer();
                        stringbuffer5.append("<TR><TD ALIGN=\"RIGHT\" VALIGN=\"TOP\">" + stringproperty3.getLabel() + "</TD>" + "<TD><TABLE><TR><TD ALIGN=\"left\" VALIGN=\"top\">");
                        StringBuffer stringbuffer6 = new StringBuffer();
                        COM.dragonflow.Properties.StringProperty.getPrivate(request, stringproperty3.getName(), myVerySecretName(stringproperty3), stringbuffer5, stringbuffer6);
                        java.lang.Object obj1 = hashmap1.get(stringproperty3);
                        String s34 = obj1 != null ? (String)obj1 : "";
                        stringbuffer5.append("</TD></TR><TR><TD ALIGN=\"left\" VALIGN=\"top\"><FONT SIZE=-1>" + stringproperty3.getDescription() + "</FONT></TD></TR>\n" + stringbuffer6.toString() + "</TABLE></TD><TD><I>" + s34 + "</I></TD></TR>");
                        outputStream.println(stringbuffer5.toString());
                    } else
                    {
                        printProperty(stringproperty3, outputStream, atomicmonitor, request, hashmap1, flag1);
                    }
                }
            } else
            if(s23.indexOf("_encoding") >= 0)
            {
                outputStream.println("<input type=hidden name=" + s23 + " value=\"" + atomicmonitor.getProperty(stringproperty3) + "\">");
            }
        } while(true);
        if(array3 != null)
        {
            printOrdering(atomicmonitor, array3);
        }
        printThresholds(atomicmonitor, hashmap1);
        outputStream.print("<TR><td><TD><input type=checkbox name=\"_offline\" value=\"offline\"" + (request.getValue("_offline").equals("offline") ? "CHECKED" : "") + "> Offline</TD><TD></TD><TD>");
        outputStream.println("</TABLE></FORM>\n");
        int j2 = -1;
        if(stringbuffer.length() > 0)
        {
            outputStream.println("<P>");
            int i3;
            if(request.getValue("operation").equals("Add") || request.getValue("operation").equals("Edit"))
            {
                i3 = stringbuffer.toString().toLowerCase().lastIndexOf("<hr><b><a name=step");
            } else
            {
                i3 = 0;
            }
            if(i3 >= 0 && i3 < stringbuffer.length())
            {
                outputStream.println(stringbuffer.toString().substring(i3));
                if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_transFloatWindow").equalsIgnoreCase("CHECKED"))
                {
                    int k3 = -1;
                    for(k3 = stringbuffer1.toString().toLowerCase().indexOf("content-type:"); k3 >= 0 && stringbuffer1.toString().substring(k3 + 13).toLowerCase().indexOf("content-type:") >= 0; k3 += stringbuffer1.toString().substring(k3 + 13).toLowerCase().indexOf("content-type:") + 13) { }
                    if(k3 >= 0)
                    {
                        j2 = stringbuffer1.toString().substring(k3).toLowerCase().indexOf("<html>") + k3;
                        String s27 = stringbuffer1.toString().substring(j2);
                        COM.dragonflow.Utils.FileUtils.writeFile(COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "htdocs" + java.io.File.separator + "CurTransStep.html", s27);
                    }
                }
            }
        }
        if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_transFloatWindow").equalsIgnoreCase("CHECKED") && j2 >= 0)
        {
            outputStream.println("<script language=\"JavaScript\"> URLfloat=window.open(\"http://" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_webserverAddress") + ":" + COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_httpActivePort") + COM.dragonflow.SiteView.Platform.getURLPath("htdocs", request.getAccount()) + "/CurTransStep.html\",\"URLfloat\", \"scrollbars=yes, width=400, height=300, resizable=yes, status=yes, location=yes\");" + "</script>");
        }
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printFooter(outputStream);
        } else
        {
            outputStream.println("</BODY>");
        }
    }

    private String myVerySecretName(COM.dragonflow.Properties.StringProperty stringproperty)
    {
        return COM.dragonflow.Utils.TextUtils.obscure(stringproperty.getName()).substring(5, COM.dragonflow.Utils.TextUtils.obscure(stringproperty.getName()).length());
    }

    private int getStep(String s)
    {
        for(int i = s.length() - 1; i > 0; i--)
        {
            if(!java.lang.Character.isDigit(s.charAt(i)))
            {
                if(i == s.length() - 1)
                {
                    return -1;
                } else
                {
                    return java.lang.Integer.parseInt(s.substring(i + 1));
                }
            }
        }

        return -1;
    }

    private static String getStepEncoding(int i, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        String s = "";
        String s2 = COM.dragonflow.Utils.I18N.getDefaultEncoding();
        for(int j = 1; j <= i + 1; j++)
        {
            String s1 = httprequest.getValue("_encoding" + j);
            if(s1.length() > 0)
            {
                s2 = new String(s1);
            }
        }

        return s2;
    }

    public static void main(String args[])
    {
        COM.dragonflow.Page.tranWizardPage tranwizardpage = new tranWizardPage();
        if(args.length > 0)
        {
            tranwizardpage.args = args;
        }
        tranwizardpage.handleRequest();
    }

    static 
    {
        $assertionsDisabled = !(COM.dragonflow.Page.tranWizardPage.class).desiredAssertionStatus();
    }
}
