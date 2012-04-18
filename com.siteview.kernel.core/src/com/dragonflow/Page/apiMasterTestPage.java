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

import com.dragonflow.Api.APIAlert;
import com.dragonflow.Api.APIMonitor;
import com.dragonflow.Api.APIPreference;

// Referenced classes of package com.dragonflow.Page:
// CGI

public class apiMasterTestPage extends CGI {

    public static String server = "localhost";

    public static String port = "8888";

    public apiMasterTestPage() {
    }

    public void printBody() throws java.lang.Exception {
        outputStream
                .println("<HTML><HEAD><TITLE>Master Test Operations</TITLE></HEAD><BODY>");
        outputStream.println("<H1>API Master Test Operations</H1>");
        outputStream
                .println("<hr>Go to ---> SiteView On <a href=\"http://localhost:"
                        + port + "\">http://localhost:" + port + "</a>");
        com.dragonflow.Api.APIMonitor apimonitor = new APIMonitor();
        com.dragonflow.Api.APIAlert apialert = new APIAlert();
        outputStream.println("<hr><H2>General API methods</H2>");
        outputStream.println("<hr><form method=POST>");
        outputStream
                .println("<input type=hidden name=\"page\" value=\"apiMasterTest\">");
        outputStream
                .println("<input type=hidden name=\"account\" value=\"administrator\">");
        outputStream
                .println("<input type=hidden name=\"force\" value=\"true\">");
        outputStream
                .println("<input type=submit name=\"Force a Signal Reload\" value=\"Force a Signal Reload\"></form>");
        if (request.hasValue("force")) {
            com.dragonflow.Api.APIMonitor _tmp = apimonitor;
            com.dragonflow.Api.APIMonitor.forceConfigurationRefresh();
        }
        outputStream.println("<hr><H2>API Group Test Operations</H2>");
        outputStream.println("<form method=POST>");
        outputStream
                .println("<input type=hidden name=\"page\" value=\"apiGroupTest\">");
        outputStream
                .println("<input type=hidden name=\"account\" value=\"administrator\">");
        outputStream
                .println("<table><tr><td><input type=radio name=\"testName\" value=\"createGroupProperties\">(create) Create a Group (Force Reload to see new monitor)</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group Name: </td><td><input type=text name=\"createGroupName\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Parent Group ID: (leave blank if top-level group)</td><td><input type=text name=\"createParentGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"editGroupProperties\">(update) Edit Existing Group (Force Reload to see new monitor) </td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"editGroupID\"></td><tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"delete\">(delete) Delete a Group (Force Reload to see new monitor)</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"groupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"moveGroup\">(move) Move a Group</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"grpMoveGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Destination Group ID: (leave blank if top-level group)</td><td><input type=text name=\"grpMoveDestGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"copyGroup\">(copy) Copy a Group</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"grpCopyGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Destination Group ID: (leave blank if top-level group)</td><td><input type=text name=\"grpCopyDestGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listGroupPropertiesDetails\">(getClassPropertiesDetails) List The Properties of the Group Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"listGroupPropertiesOperation\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listGroupPropertyDetails\">(getClassPropertyDetails) List The Properties of the Group Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"getClassPropertyName\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"getClassPropertyGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listGroupInstancePropertyDetails\">(getInstancePropertyDetails) List The Properties of a Group Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"getGroupInstancePropertyName\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"getGroupInstancePropertyGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"listGroupPropertiesOperationInstance\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listGroupInstances\">(getInstances) List Property Names/Values for a Group Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"getGroupInstancesGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"getGroupInstancesOperation\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listInstanceProperties\">(getInstanceProperties) List Property Names/Values for a Group Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"getInstancePropertiesGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"getInstancePropertiesOperation\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listInstanceProperty\">(getInstanceProperty)List Property Names/Values for a Group Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"getInstancePropertyName\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"getInstancePropertyGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"getInstancePropertyOperation\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAllGroups\">(getAllInstancesInfo) List Information Showing All Existing Group Instances</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listChildGroups\">(getChildInstanceInfo) List Information Showing All Existing Child Group Instances</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Parent Group ID: </td><td><input type=text name=\"parentGroupID\"></td></tr></table>");
        outputStream
                .println("<input type=submit name=\"Execute Selected Group Test\" value=\"Execute Selected Group Test\"></form>");
        outputStream.println("<hr><H2>API Monitor Test Operations</H2>");
        outputStream.println("<form method=POST>");
        outputStream
                .println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
        outputStream
                .println("<input type=hidden name=\"account\" value=\"administrator\">");
        com.dragonflow.Api.SSStringReturnValue assstringreturnvalue[] = apimonitor
                .getMonitorTypes();
        outputStream
                .println("<table><tr><td><input type=radio name=\"testName\" value=\"createMonitorProperties\">(create) Create a Monitor (Force Reload to see new monitor)</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor Type: </td><td><select name = \"selectedMonitorTypeCreate\">");
        for (int i = 0; i < assstringreturnvalue.length; i++) {
            String s = assstringreturnvalue[i].getValue();
            outputStream.println("<option value=\"" + s + "\">" + s
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID for Monitor Create: </td><td><input type=text name=\"createMonitorGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"editMonitorProperties\">(update) Edit Existing Monitor (Force Reload to see updated changes to monitor)</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"editMonitorGroupID\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"editMonitorMonitorID\"></td><tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"deleteMonitor\">(delete) Delete a Monitor (Force Reload to see deleted monitor)</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"deleteGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"deleteMonitorID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"moveMonitor\">(move) Move a Monitor</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"moveGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"moveMonitorID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Destination Group ID: </td><td><input type=text name=\"moveDestGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"copyMonitor\">(copy) Copy a Monitor</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"copyGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"copyMonitorID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Destination Group ID: </td><td><input type=text name=\"copyDestGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"runExistingMonitor\">(run Existing) Run an Existing Monitor Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"runExistingGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"runExistingMonitorID\"></td></tr>");
        assstringreturnvalue = apimonitor.getMonitorTypes();
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"runTemporaryMonitorProperties\">(runTemporary) Run a Temporary Monitor</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor Type: </td><td><select name = \"selectedMonitorTypeRunTemporary\">");
        for (int j = 0; j < assstringreturnvalue.length; j++) {
            String s1 = assstringreturnvalue[j].getValue();
            outputStream.println("<option value=\"" + s1 + "\">" + s1
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID for Monitor Run Temporary: </td><td><input type=text name=\"runTemporaryMonitorGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorAttributes\">(getClassAttributes) List The Attributes of the Monitor Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor Type: </td><td><select name = \"selectedMonitorTypeA\">");
        for (int k = 0; k < assstringreturnvalue.length; k++) {
            String s2 = assstringreturnvalue[k].getValue();
            outputStream.println("<option value=\"" + s2 + "\">" + s2
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorClassPropertiesDetails\">(getClassPropertiesDetails) List The Properties of the Monitor Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor Type: </td><td><select name = \"selectedMonitorType\">");
        for (int l = 0; l < assstringreturnvalue.length; l++) {
            String s3 = assstringreturnvalue[l].getValue();
            outputStream.println("<option value=\"" + s3 + "\">" + s3
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"selectedMonitorOperationType\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorClassPropertyDetails\">(getClassPropertyDetails) List The Properties of the Monitor Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"getClassPropertyName\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorInstancePropertyDetails\">(getInstancePropertyDetails) List The Properties of a Group Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"getMonitorInstancePropertyName\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"getMonitorInstancePropertyGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"getMonitorInstancePropertyMonitorID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorInstances\">(getInstances) List Property Names/Values for EACH Monitor Instance of a Group</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"groupIDInstanceChild\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"selectedMonitorInstanceOperationType\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorInstanceProperties\">(getInstanceProperties) List All Property Names/Values for a Monitor Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"groupIDInstance\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"monitorIDInstance\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"selectedMonitorInstanceOperationTypeChild\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorInstanceProperty\">(getInstanceProperty) List a specific Property Names and Value for a specific Monitor Instance and Property Name</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"propertyNameInstanceProperty\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"groupIDInstanceProperty\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"monitorIDInstanceProperty\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorInstancePropertyDetails\">(getInstancePropertyDetail) List a specific a set of Property Details for a Monitor Instance and Property Name</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"propertyNameInstanceDetail\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"groupIDInstanceDetail\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"monitorIDInstanceDetail\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"getURLStepProperties\">(getURLStepProperties) Get back step properties based on the run of all of the steps of a URL Sequence Monitor</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID for URL Step Properties: </td><td><input type=text name=\"getURLStepGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAllMonitors\">(getAllInstancesInfo) List Information Showing All Existing Monitor Instances</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAllMonitorsGroup\">(getChildInstancesInfo) List Information Showing All Existing Monitor Instances of a Group</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"listMonitorInfoGroupID\"></td><tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAllMonitorsType\">(getInstanceInfoByType) List Information Showing All Existing Monitor Instances of Type</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor Type: </td><td><select name = \"selectedMonitorInfoType\">");
        for (int i1 = 0; i1 < assstringreturnvalue.length; i1++) {
            String s4 = assstringreturnvalue[i1].getValue();
            outputStream.println("<option value=\"" + s4 + "\">" + s4
                    + "</option>");
        }

        outputStream.println("</select></td></tr></table>");
        outputStream
                .println("<input type=submit name=\"Execute Selected Monitor Test\" value=\"Execute Selected Monitor Test\"></form>");
        outputStream.println("<hr><H2>API Alert Test Operations</H2>");
        outputStream.println("<form method=POST>");
        outputStream
                .println("<input type=hidden name=\"page\" value=\"apiAlertTest\">");
        outputStream
                .println("<input type=hidden name=\"account\" value=\"administrator\">");
        com.dragonflow.Api.SSStringReturnValue assstringreturnvalue1[] = apialert
                .getAlertTypes();
        outputStream
                .println("<table><tr><td><input type=radio name=\"testName\" value=\"createAlertProperties\">(create) Create an Alert</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert Type: </td><td><select name = \"selectedAlertTypeCreate\">");
        for (int j1 = 0; j1 < assstringreturnvalue1.length; j1++) {
            String s5 = assstringreturnvalue1[j1].getValue();
            outputStream.println("<option value=\"" + s5 + "\">" + s5
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"createAddAlertGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"createAddAlertMonitorID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"editAlertProperties\">(update) Edit Existing Alert: </td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"editAlertGroupID\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"editAlertMonitorID\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert ID: </td><td><input type=text name=\"editAlertAlertID\"></td><tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"deleteAlert\">(delete) Delete Existing Alert: </td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"deleteAlertGroupID\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"deleteAlertMonitorID\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert ID: </td><td><input type=text name=\"deleteAlertAlertID\"></td><tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAlertAttributes\">(getClassAttributes) List The Attributes of the Alert Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert Type: </td><td><select name = \"selectedAlertTypeA\">");
        for (int k1 = 0; k1 < assstringreturnvalue1.length; k1++) {
            String s6 = assstringreturnvalue1[k1].getValue();
            outputStream.println("<option value=\"" + s6 + "\">" + s6
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAlertPropertiesDetails\">(getClassPropertiesDetails) List The Properties of the Alert Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert Type: </td><td><select name = \"selectedAlertType\">");
        for (int l1 = 0; l1 < assstringreturnvalue1.length; l1++) {
            String s7 = assstringreturnvalue1[l1].getValue();
            outputStream.println("<option value=\"" + s7 + "\">" + s7
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"selectedAlertOperationType\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAlertPropertyDetails\">(getClassPropertyDetails) List The Attributes of a Property of the Alert Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert Type: </td><td><select name = \"selectedAlertType2\">");
        for (int i2 = 0; i2 < assstringreturnvalue1.length; i2++) {
            String s8 = assstringreturnvalue1[i2].getValue();
            outputStream.println("<option value=\"" + s8 + "\">" + s8
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"getClassPropertyName\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAlertInstancePropertyDetails\">(getInstancePropertyDetails) List The Attributes of a Property of the Alert Class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"getInstancePropertyName\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"alertInstanceGroupID\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"alertInstanceMonitorID\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert ID: </td><td><input type=text name=\"alertInstanceAlertID\"></td><tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"getInstanceAlerts\">(getInstances) List Property Names/Values for each Alert Associated with an owner target Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"alertInstanceMonitorGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"alertInstanceMonitorMonitorID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"selectedAlertInstanceOperationType2\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAlertInstanceProperties\">(getInstanceProperties) List All Property Names/Values for a Alert Instance</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"groupIDInstance\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"monitorIDInstance\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert ID: </td><td><input type=text name=\"alertIDInstance\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"selectedAlertInstanceOperationType3\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAlertInstanceProperty\">(getInstanceProperty) List a specific Property Names and Value for a specific Alert Instance and Property Name</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Property Name: </td><td><input type=text name=\"propertyNameInstanceProperty\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"groupIDInstanceProperty\"></td>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"monitorIDInstanceProperty\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert ID: </td><td><input type=text name=\"alertIDInstanceProperty\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAllAlerts\">(getAllInstanceInfo) List Information Showing All Existing Alert Instances</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listChildAlerts\">(getChildInstancesInfo) List Information Showing All Existing Alert Instances associated with the monitors of a Group</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Parent Group ID: </td><td><input type=text name=\"alertParentGroupID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listMonitorAlerts\">(getChildInstancesInfo) List Information Showing All Existing Alert Instances associated with a Monitor</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Group ID: </td><td><input type=text name=\"alertGroupGroupID\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Monitor ID: </td><td><input type=text name=\"alertGroupMonitorID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listAllMonitorsType\">(getInstanceInfoByType) List Information Showing All Existing Alert Instances of Type></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Alert Type: </td><td><select name = \"selectedAlertInfoType\">");
        for (int j2 = 0; j2 < assstringreturnvalue1.length; j2++) {
            String s9 = assstringreturnvalue1[j2].getValue();
            outputStream.println("<option value=\"" + s9 + "\">" + s9
                    + "</option>");
        }

        outputStream.println("</select></td></tr></table>");
        outputStream
                .println("<input type=submit name=\"Execute Selected Alerts Test\" value=\"Execute Selected Alerts Test\"></form>");
        outputStream.println("<hr><H2>API Preferences Test Operations</H2>");
        outputStream.println("<form method=POST>");
        outputStream
                .println("<input type=hidden name=\"page\" value=\"apiPreferenceTest\">");
        outputStream
                .println("<input type=hidden name=\"account\" value=\"administrator\">");
        com.dragonflow.Api.APIPreference apipreference = new APIPreference();
        com.dragonflow.Api.SSStringReturnValue assstringreturnvalue2[] = apipreference
                .getPreferenceTypes();
        outputStream
                .println("<table><tr><td><input type=radio name=\"testName\" value=\"createPreferenceProperties\">(create) Create a Preference</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference Type: </td><td><select name = \"selectedPreferenceTypeCreate\">");
        for (int k2 = 0; k2 < assstringreturnvalue2.length; k2++) {
            String s10 = assstringreturnvalue2[k2].getValue();
            outputStream.println("<option value=\"" + s10 + "\">" + s10
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"updatePreferenceProperties\">(update) Update a Preference</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference Type: </td><td><select name = \"selectedPreferenceTypeUpdate\">");
        for (int l2 = 0; l2 < assstringreturnvalue2.length; l2++) {
            String s11 = assstringreturnvalue2[l2].getValue();
            outputStream.println("<option value=\"" + s11 + "\">" + s11
                    + "</option>");
        }

        outputStream
                .println("</select><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Attribute Name: </td><td><input type=text name=\"updateAttributeIdentifier\"></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Attribute Value: </td><td><input type=text name=\"updateAttributeValue\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listPreferenceInstances\">List Property Names/Values for Preference</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Setting Name: </td><td><input type=text name=\"settingName\"></td><tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference Type: </td><td><select name = \"selectedPreferenceType\">");
        for (int i3 = 0; i3 < assstringreturnvalue2.length; i3++) {
            String s12 = assstringreturnvalue2[i3].getValue();
            outputStream.println("<option value=\"" + s12 + "\">" + s12
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"listPreferenceProperties\">List The Properties in a Preference class</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference Type: </td><td><select name = \"selectedPreferenceTypeProps\">");
        for (int j3 = 0; j3 < assstringreturnvalue2.length; j3++) {
            String s13 = assstringreturnvalue2[j3].getValue();
            outputStream.println("<option value=\"" + s13 + "\">" + s13
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Operation Type: </td><td><select name = \"selectedPreferenceOperationType\">");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL
                + "\">ADD_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL
                + "\">EDIT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ALL
                + "\">IMPORT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_ALL
                + "\">RUN_TIME_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_RUNTIME_MEASUREMENTS
                + "\">MEASUREMENT_OPERATION</option>");
        outputStream.println("<option value=\""
                + com.dragonflow.Api.APISiteView.FILTER_ALL
                + "\">ALL_OPERATION</option>");
        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"testPreference\">Test a Preference</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference Type: </td><td><select name = \"selectedPreferenceTypeTest\">");
        for (int k3 = 0; k3 < assstringreturnvalue2.length; k3++) {
            String s14 = assstringreturnvalue2[k3].getValue();
            outputStream.println("<option value=\"" + s14 + "\">" + s14
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference ID: </td><td><input type=text name=\"testPreferenceID\"></td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"deletePreference\">Delete a Preference</td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference Type: </td><td><select name = \"selectedPreferenceTypeDelete\">");
        for (int l3 = 0; l3 < assstringreturnvalue2.length; l3++) {
            String s15 = assstringreturnvalue2[l3].getValue();
            outputStream.println("<option value=\"" + s15 + "\">" + s15
                    + "</option>");
        }

        outputStream.println("</select></td></tr>");
        outputStream
                .println("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;Preference ID: </td><td><input type=text name=\"deletePreferenceID\"></td></tr></table>");
        outputStream
                .println("<tr></td></tr><input type=submit name=\"Execute Selected Preference Test\" value=\"Execute Selected Preference Test\"></form>");
        outputStream.println("<hr>");
        outputStream
                .println("<hr><H2>API Performance Statistics Test Operations</H2>");
        outputStream.println("<form method=POST>");
        outputStream
                .println("<input type=hidden name=\"page\" value=\"apiStatsTest\">");
        outputStream
                .println("<input type=hidden name=\"account\" value=\"administrator\">");
        outputStream
                .println("<table><tr><td><input type=radio name=\"testName\" value=\"logMonitorStats\">Log Monitor Stats</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"serverLoadStats\">Server Load Stats</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"currentMonitorsPerMinute\">Current Monitors Per Minute</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"currentMonitorsRunning\">Current Monitors Running</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"currentMonitorsWaiting\">Current Monitors Waiting</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"maximumMonitorsPerMinute\">Maximum Monitors Per Minute</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"maximumMonitorsRunning\">Maximum Monitors Running</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"maximumMonitorsWaiting\">Maximum Monitors Waiting</td></tr>");
        outputStream
                .println("<tr><td><input type=radio name=\"testName\" value=\"runningMonitorStats\">Running Monitor Stats</td></tr></table>");
        outputStream
                .println("<tr><td></td></tr><input type=submit name=\"Execute Selected Stats Test\" value=\"Execute Selected Stats Test\"></form>");
        outputStream.println("</BODY></HTML>");
    }

}
