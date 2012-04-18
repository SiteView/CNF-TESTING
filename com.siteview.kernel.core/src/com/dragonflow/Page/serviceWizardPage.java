/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Page;

import java.io.File;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.HTTP.HTTPRequestException;
import com.dragonflow.Utils.WSDLParser;

// Referenced classes of package com.dragonflow.Page:
// monitorPage, CGI

public class serviceWizardPage extends com.dragonflow.Page.monitorPage
{

    String addMonitor;
    String getMethods;
    String getArgs;
    String updateMonitor;
    String urlType;
    String urlPrefix;
    String methodType;
    private final String methodHelp = "This is a list of available methods for this Web Service";
    private final String inputHelp = "This is the input argument list for the chosen web service method<P>";
    private final String inputHelp_noArg = "This method takes no arguments - leave the above text field blank<P>";
    String methodArgs;
    int displayMax;
    int methodNameMax;
    String stepHelp;
    String startHelp;
    private com.dragonflow.Utils.WSDLParser wsdlParser;
    String WSDL_DIRECTORY;
    public static boolean debug = false;

    public serviceWizardPage()
    {
        addMonitor = "Add Monitor";
        getMethods = "Get Methods";
        getArgs = "Get Arguments";
        updateMonitor = "Update Monitor";
        urlType = "url";
        urlPrefix = "http://";
        methodType = "_methodname";
        methodArgs = "";
        displayMax = 80;
        methodNameMax = 30;
        stepHelp = "";
        startHelp = "";
        wsdlParser = null;
        WSDL_DIRECTORY = "templates.wsdl";
    }

    void printHeaders(String s)
    {
        printBodyHeader("Web Service Wizard");
        printButtonBar("WebServiceMonitor.htm", "");
        outputStream.println("<P><H2>Add Web Service Monitor</H2></P>\n<P><FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n" + s);
    }

    void printErrorHtml(String s)
    {
        printBodyHeader("Web Service Wizard");
        printButtonBar("WebServiceMonitor.htm", "");
        outputStream.println("<P>\n<H2>Get Method Error</H2>\n<P><br>There was an error retrieving the methods for the Web Service:<blockquote><b>" + s + "</b></blockquote>\n" + "<P><br>Press BACK on your browser to continue\n");
        printFooter(outputStream);
    }

    private void setHiddenInfo()
    {
        outputStream.print("<input type=hidden name=class value=\"" + request.getValue("class") + "\">\n" + "<input type=hidden name=page value=\"" + request.getValue("page") + "\">\n" + "<input type=hidden name=group value=\"" + request.getValue("group") + "\">\n" + "<input type=hidden name=id value=\"" + request.getValue("id") + "\">\n" + "<input type=hidden name=operation value=\"" + request.getValue("operation") + "\">\n" + "<input type=hidden name=account value=\"" + request.getAccount() + "\">\n");
    }

    public void printBody()
        throws java.lang.Exception
    {
        com.dragonflow.SiteView.AtomicMonitor atomicmonitor = null;
        Array array2 = null;
        if(request.getValue("group").length() > 0)
        {
            array2 = ReadGroupFrames(request.getValue("group"));
        }
        if(request.getValue("operation").equals("Edit") || request.getValue("operation").equals("Tool"))
        {
            addMonitor = updateMonitor;
        }
        if(!request.getValue("operation").equals("Add") && request.getValue("group").length() > 0)
        {
            atomicmonitor = com.dragonflow.SiteView.AtomicMonitor.MonitorCreate(array2, request.getValue("id"), request.getPortalServer());
            Array array = atomicmonitor.getProperties();
            com.dragonflow.Properties.StringProperty stringproperty;
            String s2;
            for(Enumeration enumeration = (Enumeration) array.iterator(); enumeration.hasMoreElements(); request.setValue(stringproperty.getName(), s2))
            {
                stringproperty = (com.dragonflow.Properties.StringProperty)enumeration.nextElement();
                s2 = "";
                if(stringproperty.isMultiLine)
                {
                    for(Enumeration enumeration2 = atomicmonitor.getMultipleValues(stringproperty); enumeration2.hasMoreElements();)
                    {
                        s2 = s2 + enumeration2.nextElement() + "\n";
                    }

                } else
                {
                    s2 = atomicmonitor.getProperty(stringproperty);
                }
            }

        }
        if(request.getValue("operation").equals("Tool"))
        {
            if(!request.actionAllowed("_tools"))
            {
                throw new HTTPRequestException(557);
            }
            printBodyHeader("Web Service Test");
            if(!request.getValue("AWRequest").equals("yes"))
            {
                printButtonBar("WebService.htm", "");
            } else
            {
                outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + "&AWRequest=yes>Diagnostic Tools</a></center><p>");
            }
            String s = request.getValue("_wsdlurl");
            String s3 = request.getValue("_methodname");
            String s4 = request.getValue("_argnames");
            String s6 = request.getValue("_contenttype");
            String s8 = request.getValue("_schema");
            String s9 = request.getValue("_methodns");
            String s11 = request.getValue("_actionuri");
            String s13 = request.getValue("_serverurl");
            String s15 = request.getValue("_username");
            String s16 = request.getValue("_ntlmDomain");
            String s17 = request.getValue("_proxy");
            String s18 = request.getValue("_proxyuser");
            String s19 = request.getValue("_useDotNetSoap");
            String s20 = request.getValue("_URLUserAgent");
            StringBuffer stringbuffer3 = new StringBuffer();
            StringBuffer stringbuffer4 = new StringBuffer();
            StringBuffer stringbuffer5 = new StringBuffer();
            StringBuffer stringbuffer6 = new StringBuffer();
            String s21 = com.dragonflow.Properties.StringProperty.getPrivate(request, "_password", "serviceSuff", stringbuffer3, stringbuffer4);
            String s22 = com.dragonflow.Properties.StringProperty.getPrivate(request, "_proxypassword", "pSevericeSuff", stringbuffer5, stringbuffer6);
            com.dragonflow.Utils.TextUtils.debugPrint(" hello " + s + " methodname " + s3 + " serverurl " + s13);
            String s23 = s19.length() <= 0 ? "" : "checked";
            String s24 = com.dragonflow.Utils.TextUtils.encodeArgs(s4);
            com.dragonflow.Page.serviceWizardPage.status("NEWARGS = " + s24);
            outputStream.println("<p>\n<CENTER><H2>Web Service Test</H2></CENTER><P>\n<p>\n<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\nUse this form to test an existing Web Service for availability by sending it a true SOAP request over HTTP. In addition, you can examine the actual SOAP response message that gets returned.  The Web Service URL specifies which\nserver to contact, the method name and arguments are then used to build the request.\nEnter the URL of the Web Service endpoint, and optionally, the user name, password and/or proxy server \nto use for contacting the web service.\n If you have an existing web service monitor, you can click its Tools link to have these input fields filled in automatically. See the Tools online help for details.\n<p><TABLE BORDER=0>\n<TR><TD ALIGN=RIGHT>WSDL Path or URL:<TD><input type=text name=_wsdlurl value=\"" + s + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Web Service URL:<TD><input type=text name=_serverurl value=\"" + s13 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Method Name:<TD><input type=text name=_methodname value=\"" + s3 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Arguments:</TD><TD ALIGN=LEFT><TEXTAREA name=_argnames rows=4 cols=80>" + s24 + "</TEXTAREA></TD></TR>" + "<TR><TD ALIGN=RIGHT>SOAP Action URI:<TD><input type=text name=_actionuri value=\"" + s11 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>User-Agent:<TD><input type=text name=_URLUserAgent value=\"" + s20 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Method Namespace:<TD><input type=text name=_methodns value=\"" + s9 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>User Name:<TD><input type=text name=_username value=\"" + s15 + "\" size=30><br>\n" + "<TR><TD ALIGN=RIGHT>Password:<TD>" + stringbuffer3.toString() + " size=30><br>\n" + stringbuffer4.toString() + "<TR><TD ALIGN=RIGHT>NTLM Domain:<TD><input type=text name=_ntlmDomain value=\"" + s16 + "\" size=40><br>\n" + "<TR><TD ALIGN=RIGHT>Proxy:<TD><input type=text name=_proxy value=\"" + s17 + "\" size=40><br>\n" + "<TR><TD ALIGN=RIGHT>Proxy User:<TD><input type=text name=_proxyuser value=\"" + s18 + "\" size=40><br>\n" + "<TR><TD ALIGN=RIGHT>Proxy Password:<TD>" + stringbuffer5.toString() + " size=30><br>\n" + stringbuffer6.toString() + "<TR><TD ALIGN=RIGHT><TD><input type=checkbox name=_useDotNetSoap size=50 " + s23 + ">Check if testing MS .NET web service<br>\n" + "</TABLE><p>\n");
            outputStream.println("<input type=hidden name=operation value=Tool>\n<input type=hidden name=page value=serviceWizard>\n<input type=hidden name=account value=\"" + request.getAccount() + "\">");
            if(request.getValue("AWRequest").equals("yes"))
            {
                outputStream.println("<input type=hidden name=AWRequest value=yes>");
            }
            outputStream.println("<input type=submit value=\"Web Service Request\" class=\"VerBl8\">\n</FORM>\n");
            if(s13.length() > 0 && s3.length() > 0 && s.length() > 0)
            {
                outputStream.println("Retrieving.... ");
                outputStream.flush();
                StringBuffer stringbuffer7 = new StringBuffer();
                StringBuffer stringbuffer8 = new StringBuffer();
                boolean flag2 = s19.length() > 0;
                java.lang.Object aobj[] = com.dragonflow.StandardMonitor.WebServiceMonitor.rpcCall(s, s3, s4, s6, s8, s9, s11, s20, s13, stringbuffer7, stringbuffer8, s15, s21, s17, s18, s22, s16, flag2);
                int j = ((java.lang.Long)aobj[0]).intValue();
                long l2 = ((java.lang.Long)aobj[2]).longValue();
                String s25 = com.dragonflow.StandardMonitor.URLMonitor.lookupStatus(j);
                outputStream.print("<P>Result: <B>" + s25 + "</B><P>\n" + "Total time (msec): <B>" + l2 + "</B><BR>\n" + "<!--WEB SERVICE STATUS=" + j + " " + stringbuffer7.toString() + "-->\n");
                if(stringbuffer8 != null)
                {
                    outputStream.print("<HR><PRE>" + com.dragonflow.Utils.TextUtils.escapeHTML(stringbuffer8.toString()) + "</PRE></HR>");
                }
            }
            if(!request.getValue("AWRequest").equals("yes"))
            {
                printFooter(outputStream);
            } else
            {
                outputStream.println("</BODY>");
            }
            return;
        }
        if(atomicmonitor == null)
        {
            atomicmonitor = com.dragonflow.SiteView.AtomicMonitor.MonitorCreate(request.getValue("class"));
        }
        String s1 = request.getPermission("_monitorType", (String)atomicmonitor.getClassProperty("class"));
        if(s1.equals("optional"))
        {
            printErrorHtml("Operation not available to current user");
            return;
        }
        if(request.getValue("submit").equals(addMonitor))
        {
            postToMonitorPage();
            return;
        }
        boolean flag = false;
        if(request.getValue("_url").length() > 0 || request.getValue("_file").length() > 0)
        {
            flag = true;
        }
        if(flag && request.getValue("_methodname").length() <= 0)
        {
            String s5 = request.getValue("_url").equals("http://") ? request.getValue("_file") : request.getValue("_url");
            if(s5.indexOf("http") < 0)
            {
                s5 = com.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.wsdl", request.getAccount()) + "/" + s5;
            }
            StringBuffer stringbuffer1 = new StringBuffer();
            wsdlParser = new WSDLParser();
            wsdlParser.readWSDL(s5, stringbuffer1);
            if(stringbuffer1.length() > 0)
            {
                printErrorHtml(stringbuffer1.toString());
                return;
            }
            java.util.List list = wsdlParser.getOperationNames(stringbuffer1);
            if(stringbuffer1.length() > 0)
            {
                printErrorHtml(stringbuffer1.toString());
                return;
            }
            String s10 = wsdlParser.getServiceEndpointURL(stringbuffer1);
            if(stringbuffer1.length() > 0)
            {
                printErrorHtml(stringbuffer1.toString());
                return;
            }
            printHeaders(stepHelp);
            outputStream.print("<TR><TD></TD><TD><b>Web Service URL:</b></TD></TR><BR>");
            outputStream.print("<TR><TD></TD><TD></TD><TD>" + com.dragonflow.Utils.TextUtils.escapeHTML(s10) + "</TD>" + "</TR>\n");
            outputStream.println("<TABLE>");
            displayMethodList(list);
            outputStream.print("<input type=hidden name=_wsdlurl value=\"" + s5 + "\">\n" + "<input type=hidden name=_serverurl value=\"" + s10 + "\">\n");
            setHiddenInfo();
            outputStream.print("<BR>\n<BR>\n");
            outputStream.println("</TABLE>");
            outputStream.print("<BR>\n<BR>\n");
            outputStream.print("<TR><TD></TD><TD></TD><TD>");
            outputStream.print("<input type=submit name=\"submit\" value=\"" + getArgs + "\">&nbsp;&nbsp");
            outputStream.print("</TD></TR>");
            outputStream.print("<TR><TD></TD><TD></TD><TD>Choose <b>Get Arguments</b>  retrieve the arguments of the above method");
        } else
        if(request.getValue("_methodname").length() > 0)
        {
            StringBuffer stringbuffer = new StringBuffer();
            if(wsdlParser == null)
            {
                wsdlParser = new WSDLParser();
                wsdlParser.readWSDL(request.getValue("_wsdlurl"), stringbuffer);
                if(stringbuffer.length() > 0)
                {
                    printErrorHtml(stringbuffer.toString());
                    return;
                }
            }
            String s7 = request.getValue("_methodname");
            java.util.List list1 = wsdlParser.generateArgXMLforUI(s7, stringbuffer);
            if(stringbuffer.length() > 0)
            {
                printErrorHtml(stringbuffer.toString());
                return;
            }
            StringBuffer stringbuffer2 = new StringBuffer();
            String s12 = wsdlParser.getOperationNamespace(s7, stringbuffer2, stringbuffer);
            if(stringbuffer.length() > 0)
            {
                printErrorHtml(stringbuffer.toString());
                return;
            }
            String s14 = stringbuffer2.toString();
            printHeaders(stepHelp);
            outputStream.print("<TR><TD></TD><TD></TD><TD><b>Web Service URL:</b></TD></TR>");
            outputStream.print("<TR><DD>" + com.dragonflow.Utils.TextUtils.escapeHTML(request.getValue("_serverurl")) + "</DD>" + "</TR><BR>\n<BR>\n<BR>\n");
            outputStream.print("<TR><TD></TD><TD></TD><TD><b>Web Service Method:</b></TD></TR>");
            outputStream.print("<TR><DD>" + com.dragonflow.Utils.TextUtils.escapeHTML(request.getValue("_methodname")) + "</DD>" + "</TR><BR>\n<BR>\n");
            outputStream.print("<TR><TD></TD><TD></TD><TD><b>Web Service Arguments:</b></TD></TR><BR>\n<BR>\n");
            outputStream.println("<TABLE>");
            displayArgNames(list1);
            outputStream.print("<input type=hidden name=_wsdlurl value=\"" + request.getValue("_wsdlurl") + "\">\n" + "<input type=hidden name=_methodname value=\"" + request.getValue("_methodname") + "\">\n" + "<input type=hidden name=_serverurl value=\"" + request.getValue("_serverurl") + "\">\n" + "<input type=hidden name=_methodns value=\"" + s12 + "\">\n" + "<input type=hidden name=_actionuri value=\"" + s14 + "\">\n");
            setHiddenInfo();
            HashMap hashmap = new HashMap();
            boolean flag1 = false;
            Array array1 = com.dragonflow.Properties.StringProperty.sortByOrder(atomicmonitor.getProperties());
            Enumeration enumeration1 =  (Enumeration) array1.iterator();
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                com.dragonflow.Properties.StringProperty stringproperty1 = (com.dragonflow.Properties.StringProperty)enumeration1.nextElement();
                if(!stringproperty1.isAdvanced && !stringproperty1.isVariableCountProperty() && stringproperty1.getName() != "_wsdlurl" && stringproperty1.getName() != "_methodname" && stringproperty1.getName() != "_argnames" && stringproperty1.getName() != "_serverurl" && stringproperty1.isEditable)
                {
                    printProperty(stringproperty1, outputStream, atomicmonitor, request, hashmap, flag1);
                }
            } while(true);
            if(request.getValue("group").length() > 0)
            {
                outputStream.println("</TABLE>");
                outputStream.print("<TR><TD></TD><TD></TD><TD>");
                outputStream.print("<input type=submit name=\"submit\" value=\"" + addMonitor + "\">&nbsp;&nbsp");
                outputStream.print("<TR><TD></TD><TD></TD><TD>Choose <b>" + addMonitor + "</b> to save the changes to  monitor");
                outputStream.println("<TABLE>");
            }
            outputStream.println("</TABLE>");
            outputStream.println("<P><HR><CENTER><B>Advanced Settings</B></CENTER><P>");
            outputStream.println("<TABLE>");
            enumeration1 =  (Enumeration) array1.iterator();
            do
            {
                if(!enumeration1.hasMoreElements())
                {
                    break;
                }
                com.dragonflow.Properties.StringProperty stringproperty2 = (com.dragonflow.Properties.StringProperty)enumeration1.nextElement();
                if(stringproperty2.isAdvanced && stringproperty2.getName() != "_methodns" && stringproperty2.getName() != "_actionuri" && stringproperty2.isEditable)
                {
                    generateSubHeading(stringproperty2);
                    printProperty(stringproperty2, outputStream, atomicmonitor, request, hashmap, flag1);
                }
            } while(true);
            if(array2 != null)
            {
                printOrdering(atomicmonitor, array2);
            }
            printThresholds(atomicmonitor, hashmap);
            outputStream.println("</TABLE>");
        } else
        {
            int i = request.getPermissionAsInteger("_minimumFrequency");
            long l = atomicmonitor.getPropertyAsLong(com.dragonflow.SiteView.AtomicMonitor.pFrequency);
            java.lang.Object obj = atomicmonitor.getClassProperty("defaultFrequency");
            if(obj instanceof String)
            {
                long l1 = com.dragonflow.Utils.TextUtils.toInt((String)obj);
                if(l1 > 0L)
                {
                    l = l1;
                }
            }
            if(i > 0 && l < (long)i)
            {
                l = i;
            }
            atomicmonitor.setProperty(com.dragonflow.SiteView.AtomicMonitor.pFrequency, l);
            request.setValue("_timeout", "60");
            printHeaders(startHelp);
            printUrlOption();
            setHiddenInfo();
            outputStream.println("</TABLE>");
            outputStream.println("<BR>\n<BR>\n");
            outputStream.println("<TABLE>");
            outputStream.print("<TR><TD></TD><TD></TD>");
            outputStream.print("<input type=submit name=\"submit\" value=\"" + getMethods + "\">&nbsp;&nbsp");
            outputStream.print("</TD></TR>");
            outputStream.print("<TR><TD></TD><TD></TD><TD>Choose <b>Get Methods</b> to retrieve the methods of the Service");
            outputStream.println("</TABLE>");
        }
        if(!request.getValue("AWRequest").equals("yes"))
        {
            printFooter(outputStream);
        } else
        {
            outputStream.println("</BODY>");
        }
    }

    private void generateSubHeading(com.dragonflow.Properties.StringProperty stringproperty)
    {
        if(stringproperty.getName().equals("_useDotNetSoap"))
        {
            outputStream.println("</TABLE><P><B>SOAP Options</B><P><TABLE>");
        } else
        if(stringproperty.getName().equals("_ntlmDomain"))
        {
            outputStream.println("</TABLE><P><B>Authentication</B><P><TABLE>");
        } else
        if(stringproperty.getName().equals("_proxy"))
        {
            outputStream.println("</TABLE><P><B>Proxy Options</B><P><TABLE>");
        }
    }

    private void displayArgNames(java.util.List list)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s = null;
        if(list.size() > 0)
        {
            s = "This is the input argument list for the chosen web service method<P>";
        } else
        {
            s = "This method takes no arguments - leave the above text field blank<P>";
        }
        java.util.Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            String s1 = (String)iterator.next();
            stringbuffer.append(s1);
            if(iterator.hasNext())
            {
                stringbuffer.append(com.dragonflow.StandardMonitor.URLMonitor.CRLF);
            }
        } while(true);
        outputStream.print("<TR><TD></TD><TD ALIGN=RIGHT><TEXTAREA name=_argnames rows=4 cols=80>" + stringbuffer.toString() + "</TEXTAREA></TD></TR>" + "<TR><TD></TD><TD><small>" + s + "</small><p></TD></TR>");
    }

    private void displayMethodList(java.util.List list)
    {
        String s = "Web Service Methods:";
        outputStream.print("<TR><TD><b>" + s + "</b></TD></TR>" + "<TR><TD><select name=_methodname>");
        String s1;
        for(java.util.Iterator iterator = list.iterator(); iterator.hasNext(); outputStream.print("<option value=\"" + s1 + "\">" + s1 + "</option>"))
        {
            s1 = (String)iterator.next();
        }

        outputStream.print("</select></TD></TR>\n<TD ALIGN=RIGHT><small>This is a list of available methods for this Web Service</small></TD>");
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
            java.lang.Class class1 = java.lang.Class.forName("com.dragonflow.Page.monitorPage");
            com.dragonflow.Page.CGI cgi = (com.dragonflow.Page.CGI)class1.newInstance();
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
            com.dragonflow.HTTP.HTTPRequest.printErrorMessage(request, 404, s, classnotfoundexception, outputStream);
        }
        catch(java.lang.Exception exception)
        {
            String s1 = "";
            if(request != null)
            {
                s1 = request.getURL();
            }
            com.dragonflow.HTTP.HTTPRequest.printErrorMessage(request, 999, s1, exception, outputStream);
        }
    }

    void printUrlOption()
    {
        outputStream.print("<TR><TD></TD><TD>Start by entering the URL of the Web Service Descriptor Language for the Service\n</TD></TR><TR><TD></TD><TD>or selecting the correct WSDL file\n\n\n</TD></TR>");
        outputStream.println("<H3></H3><H3></H3>");
        outputStream.println("<TABLE>");
        outputStream.print("<TR><TD ALIGN=RIGHT>URL : </TD><TD><input type=text name=_url value=\"");
        String s = "Example: http://demo.siteview.com";
        outputStream.print(urlPrefix + "\" size=80></TD></TR>" + "<TR><TD></TD><TD><small>" + s + "</small></TD></TR>");
        Array array = new Array();
        java.io.File file = new File(com.dragonflow.SiteView.Platform.getUsedDirectoryPath("templates.wsdl", request.getAccount()));
        if(!file.exists())
        {
            return;
        }
        String as[] = file.list();
        for(int i = 0; i < as.length; i++)
        {
            String s1 = as[i];
            if(s1.endsWith(".wsdl"))
            {
                array.add(s1);
            }
        }

        Enumeration enumeration = (Enumeration) array.iterator();
        outputStream.print("<TR><TD></TD></TR>");
        outputStream.print("<TR><TD ALIGN=RIGHT>File :</TD><TD><select name=_file>");
        if(enumeration.hasMoreElements())
        {
            String s2;
            for(; enumeration.hasMoreElements(); outputStream.print("<option value=\"" + s2 + "\">" + s2 + "</option>"))
            {
                s2 = (String)enumeration.nextElement();
            }

        }
        s = "";
        outputStream.print("</select></TD></TR>\n<TR><TD></TD><TD></TD><TD><small>" + s + "</small></TD></TR>");
    }

    public static void main(String args[])
    {
        com.dragonflow.Page.serviceWizardPage servicewizardpage = new serviceWizardPage();
        if(args.length > 0)
        {
            servicewizardpage.args = args;
        }
        servicewizardpage.handleRequest();
    }

    private static void status(String s)
    {
        if(debug)
        {
            java.lang.System.out.println(s);
        }
    }

    static 
    {
        String s = java.lang.System.getProperty("serviceWizardPage.debug", "false");
        if(s.equalsIgnoreCase("true"))
        {
            debug = true;
        }
    }
}
