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
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Referenced classes of package COM.dragonflow.Page:
// monitorSetPage

public class appServerSolutionTemplatePage extends
        COM.dragonflow.Page.monitorSetPage {

    private static final String APPLICATIONS_WINDOW = "applicationsTreeWindow";

    private static final String APPLICATIONS_WINDOW_OPTS = "toolbar=no,width=700,height=600,directories=no,status=no,scrollbars=yes,resizable=1,menubar=no";

    private static final String SELECTED_APPS_DIV = "SelectedModulesDIV";

    private static final String WEBLOGIC_XSL = COM.dragonflow.SiteView.Platform
            .getRoot()
            + "/templates.solutions/support/weblogic.xsl";

    private static final String WEBSPHERE_XSL = COM.dragonflow.SiteView.Platform
            .getRoot()
            + "/templates.solutions/support/websphere.xsl";

    private static final String SHOW_APPS_TREE = "showApplicationsTree";

    private static final String APP_SERVER_TREE_CSS = "/SiteView/htdocs/artwork/appServerTree.css";

    public static final String WL_SERVER = "$WebLogic Server:0$";

    public static final String WL_PORT = "$WebLogic Port:1$";

    public static final String WL_USERNAME = "$WebLogic Username:2$";

    public static final String WL_PASSWORD = "$WebLogic Password:3$";

    public static final String WL_JARFILE = "$WebLogic Jar File:4$";

    public static final String WL_SECURE = "$Secure Server:5$";

    public static final String WS_SERVER = "$WebSphere Server:0$";

    public static final String WS_PORT = "$WebSphere Port:1$";

    public static final String WS_USERNAME = "$WebSphere Username:2$";

    public static final String WS_PASSWORD = "$WebSphere Password:3$";

    public static final String WS_DIRECTORY = "$WebSphere Directory:4$";

    public static final String WS_CLIENT_PROPS = "$WebSphere Client Properties File:5$";

    public static final String WS_CLASPATH = "$WebSphere Classpath:6$";

    public appServerSolutionTemplatePage() {
        positionDelimiter = ':';
        thisPageName = "appServerSolutionTemplate";
    }

    private void mainWindowJavaScript(String s, String s1) {
        outputStream.println("<SCRIPT LANGUAGE=\"JavaScript\">\n");
        outputStream
                .println("<!--\nfunction showApplications() {\n   var treePageURL = \""
                        + s1
                        + "\";\n"
                        + "   var appsForm = document."
                        + s
                        + ";\n"
                        + "   for (var i = 0; i < appsForm.length; i++) {\n"
                        + "      if (appsForm.elements[i].type == \"text\" || appsForm.elements[i].type == \"password\") {\n"
                        + "          treePageURL += \"&\" + appsForm.elements[i].name + \"=\" + appsForm.elements[i].value;\n"
                        + "      } else if (appsForm.elements[i].type == \"checkbox\" && appsForm.elements[i].checked == true) { \n"
                        + "          treePageURL += \"&\" + appsForm.elements[i].name + \"=\" + appsForm.elements[i].value;\n"
                        + "      } else if (appsForm.elements[i].type == \"select-one\" && appsForm.elements[i].selectedIndex > -1) { \n"
                        + "          var idx = appsForm.elements[i].selectedIndex;\n"
                        + "          treePageURL += \"&\" + appsForm.elements[i].name + \"=\" + appsForm.elements[i].options[idx].value;\n"
                        + "      }\n"
                        + "   }\n"
                        + "   treeWin=window.open( treePageURL, \""
                        + "applicationsTreeWindow"
                        + "\", \""
                        + "toolbar=no,width=700,height=600,directories=no,status=no,scrollbars=yes,resizable=1,menubar=no"
                        + "\");\n" + "   treeWin.focus();\n" + "}//-->\n");
        outputStream.println("</SCRIPT>\n");
    }

    private void applicationsWindowJavaScript(String s) {
        outputStream.println("<SCRIPT LANGUAGE=\"JavaScript\">\n");
        outputStream
                .println("<!--\nfunction getSelectedModulesHTML() {\nvar selectedModulesHTML = \"<UL><h4>Modules Selected for Monitoring</h4>\"\nvar appsForm = document."
                        + s
                        + "; \n"
                        + "var selectedBoxes = new Array(); \n"
                        + "var selectedIdx = 0; \n"
                        + "for (var i = 0; i < appsForm.length; i++) {\n"
                        + "if ( (appsForm.elements[i].type == \"checkbox\" && "
                        + "appsForm.elements[i].checked == true) || (appsForm.elements[i].type == \"hidden\") ) {\n"
                        + "selectedModulesHTML += \" <LI><INPUT TYPE=CHECKBOX NAME=\\\"\" + appsForm.elements[i].name + \"\\\" CHECKED=ON "
                        + " VALUE=\\\"\" + appsForm.elements[i].value + \"\\\" > \""
                        + " + removeAtSymbols(appsForm.elements[i].name) + \"</LI>\"; \n"
                        + "} \n "
                        + "}\n"
                        + "selectedModulesHTML += \" </UL> \"; \n"
                        + "return selectedModulesHTML; \n" + "}\n");
        outputStream
                .println("function removeAtSymbols(s) { \n   var regex = /@/g; \n   return s.replace(regex,''); \n}\n");
        outputStream
                .println("function closeWindow() {\nwindow.opener.focus();\nself.close();\n}\n");
        outputStream
                .println("function updateMainWindow() {\nvar appsDiv = window.opener.document.getElementById('SelectedModulesDIV');\nappsDiv.innerHTML = getSelectedModulesHTML(); \nwindow.opener.focus();\nself.close();\n}\n");
        outputStream.println("//--> \n");
        outputStream.println("</SCRIPT>\n");
    }

    protected String getFormName() {
        String s = request.getValue("templatefile");
        if (s.indexOf('.') > -1) {
            s = s.substring(0, s.indexOf('.'));
        }
        return s;
    }

    protected void printJavaScript() {
        String s = "/SiteView/cgi/go.exe/SiteView?page="
                + thisPageName + "&operation=showApplicationsTree&group="
                + request.getValue("group") + "&account="
                + request.getAccount() + "&templatefile="
                + request.getValue("templatefile") + "&" + "solution" + "=true";
        mainWindowJavaScript(getFormName(), s);
    }

    protected void printVariablesFormSubmit(boolean flag) {
        HTMLPage
                .append("<P>Enter the values to be used by the monitors in this Solution Template.<P>\n");
        HTMLPage
                .append("<BR><input type=button onclick=showApplications() value=\"Show Applications\">&nbsp;Display a hierarchical tree of the modules available for monitoring on the Application Server.<P>\n");
        HTMLPage.append("<DIV><SPAN id=\"SelectedModulesDIV\" > </SPAN></DIV>");
        HTMLPage
                .append("<BR><input type=submit value=Submit>&nbsp;these values<P>\n");
    }

    public void printBody() throws java.lang.Exception {
        String s = request.getValue("operation");
        String s1 = "";
        if (s.equals("showApplicationsTree")) {
            showAppserverTreeWindow();
        } else {
            super.printBody();
            return;
        }
        printBodyHeader(COM.dragonflow.SiteView.Platform.productName + " " + s1);
        outputStream.println(HTMLPage);
        printFooter(outputStream);
        outputStream.println("\n</body>\n</html>");
    }

    private String showAppserverTreeWindow() {
        String s = "selectedApps";
        applicationsWindowJavaScript(s);
        java.io.File file = null;
        String s1 = null;
        String s2 = "";
        if (isWebLogicSolution()) {
            String s3 = request.getValue("$WebLogic Server:0$");
            String s6 = request.getValue("$WebLogic Port:1$");
            String s8 = request.getValue("$WebLogic Username:2$");
            String s10 = request.getValue("$WebLogic Password:3$");
            String s12 = request.getValue("$WebLogic Jar File:4$");
            String s14 = request.getValue("$Secure Server:5$");
            s1 = COM.dragonflow.StandardMonitor.WebLogic6xMonitor
                    .getAvailableApplicationsXML(s3, s6, s8, s10, s12, s14);
            file = new File(WEBLOGIC_XSL);
            s2 = "Applications, EJBs, WebApps, and Servlets";
        } else if (isWebSphereSolution()) {
            String s4 = request.getValue("$WebSphere Server:0$");
            String s7 = request.getValue("$WebSphere Port:1$");
            String s9 = request.getValue("$WebSphere Username:2$");
            String s11 = request.getValue("$WebSphere Password:3$");
            String s13 = request.getValue("$WebSphere Directory:4$");
            String s15 = request
                    .getValue("$WebSphere Client Properties File:5$");
            String s16 = request.getValue("$WebSphere Classpath:6$");
            s1 = COM.dragonflow.StandardMonitor.WebSphereMonitor
                    .getAvailableApplicationsXML(s4, s7, s9, s11, "", "5.x",
                            s13, s15, s16);
            file = new File(WEBSPHERE_XSL);
            s2 = "Servers and Modules";
        }
        HTMLPage.append("<FORM NAME=" + s + ">");
        String s5 = "";
        try {
            s5 = transformAppServerXML(file, s1);
        } catch (javax.xml.transform.TransformerException transformerexception) {
            COM.dragonflow.Log.LogManager.log("Error",
                    "TransformerException while applying XSL transform to app server counter XML: "
                            + transformerexception.getMessage());
            HTMLPage.append("<CENTER>");
            HTMLPage.append("<h2>Error</h2>\n");
            HTMLPage
                    .append("There was a problem retrieving counters from the specified application server.  Please check connection properties and try again.");
            HTMLPage.append("<P><A HREF=javascript:closeWindow()>Close</A>");
            HTMLPage.append("</CENTER>");
            return "Could not Retrieve Counters";
        }
        HTMLPage
                .append("<LINK REL=StyleSheet HREF=\"/SiteView/htdocs/artwork/appServerTree.css\" TYPE=\"text/css\">");
        HTMLPage.append("<DIV ID=\"appsTree\" >");
        HTMLPage.append(s5);
        HTMLPage.append("</DIV >");
        HTMLPage.append("</FORM>");
        HTMLPage.append("<CENTER>");
        HTMLPage
                .append("<INPUT TYPE=BUTTON VALUE=\"Select Modules\" ONCLICK=updateMainWindow()><BR>Update the main window with your current selections.");
        HTMLPage.append("</CENTER>");
        return s2;
    }

    private String transformAppServerXML(java.io.File file,
            String s) throws javax.xml.transform.TransformerException {
        javax.xml.transform.TransformerFactory transformerfactory = javax.xml.transform.TransformerFactory
                .newInstance();
        javax.xml.transform.stream.StreamSource streamsource = new StreamSource(
                file);
        javax.xml.transform.stream.StreamSource streamsource1 = new StreamSource(
                new StringReader(s));
        java.io.StringWriter stringwriter = new StringWriter();
        javax.xml.transform.stream.StreamResult streamresult = new StreamResult(
                stringwriter);
        javax.xml.transform.Transformer transformer = transformerfactory
                .newTransformer(streamsource);
        transformer.setOutputProperty("indent", "yes");
        transformer.transform(streamsource1, streamresult);
        return stringwriter.getBuffer().toString();
    }

    private boolean isWebSphereSolution() {
        return request.getValue("templatefile").indexOf("WebSphere") > -1;
    }

    private boolean isWebLogicSolution() {
        return request.getValue("templatefile").indexOf("WebLogic") > -1;
    }

    protected String getNextPage(String s) {
        if (s.equals("monitorSetCreatePrep") || s.equals("monitorSetCreate")
                || s.equals("monitorSetProps")) {
            return thisPageName;
        } else {
            return super.getNextPage(s);
        }
    }

}
