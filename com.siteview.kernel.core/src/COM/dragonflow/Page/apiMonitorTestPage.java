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

import java.util.Vector;

import com.recursionsw.jgl.Array;
import COM.datachannel.xml.om.Document;
import COM.dragonflow.Api.APIMonitor;
import COM.dragonflow.Api.APISiteView;
import COM.dragonflow.Api.SSInstanceProperty;
import COM.dragonflow.Api.SSMonitorInstance;
import COM.dragonflow.Api.SSPropertyDetails;
import COM.dragonflow.Api.SSStringReturnValue;

// Referenced classes of package COM.dragonflow.Page:
// apiMasterTestPage

public class apiMonitorTestPage extends apiMasterTestPage
{

    private int level;
    private String selectedMonitorType;
    private String selectedMonitorOperationType;
    private SSInstanceProperty currentPropertyState[];

    public apiMonitorTestPage()
    {
        level = 0;
        selectedMonitorType = "";
        selectedMonitorOperationType = "";
        currentPropertyState = null;
    }

    public void printBody()
        throws java.lang.Exception
    {
        try
        {
            APIMonitor apimonitor = new APIMonitor();
            if(request.getValue("testName").equals("createMonitorProperties"))
            {
                try
                {
                    selectedMonitorType = request.getValue("selectedMonitorTypeCreate");
                    String s = request.getValue("createMonitorGroupID");
                    SSPropertyDetails asspropertydetails1[] = apimonitor.getClassPropertiesDetails(selectedMonitorType, APISiteView.FILTER_CONFIGURATION_ADD_ALL, new SSInstanceProperty[0]);
                    String s31 = "";
                    for(int l = 0; l < asspropertydetails1.length; l++)
                    {
                        String s41 = asspropertydetails1[l].getDefaultValue();
                        s31 = s31 + asspropertydetails1[l].getName() + "=";
                        if(s41 != null && s41.length() > 0)
                        {
                            s31 = s31 + s41;
                        }
                        s31 = s31 + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"createMonitor\">");
                    outputStream.println("<input type=hidden name=\"selectedMonitorTypeCreate\" value=\"" + selectedMonitorType + "\">");
                    outputStream.println("<input type=hidden name=\"createMonitorGroupID\" value=\"" + s + "\">");
                    outputStream.println("<table><tr><td>Monitor Properties: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"createMonitorProperties\" rows=30 cols=80>" + s31 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"createMonitor\" value=\"Create This Monitor\"></form>");
                }
                catch(java.lang.Exception exception1)
                {
                    outputStream.println("<p>Error in create");
                    outputStream.println("<p>" + exception1.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("createMonitor"))
            {
                try
                {
                    selectedMonitorType = request.getValue("selectedMonitorTypeCreate");
                    String s1 = request.getValue("createMonitorGroupID");
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    String s18 = request.getValue("createMonitorProperties");
                    String as[] = splitString(s18, "\n");
                    int i1 = 0;
                    for(int j2 = 0; j2 < as.length; j2++)
                    {
                        int k3 = as[j2].indexOf("=");
                        if(k3 != -1 && k3 != 0 && as[j2].substring(k3 + 1) != null && as[j2].substring(k3 + 1).length() > 0)
                        {
                            i1++;
                        }
                    }

                    SSInstanceProperty assinstanceproperty6[] = new SSInstanceProperty[i1];
                    int l3 = 0;
                    for(int j5 = 0; j5 < as.length; j5++)
                    {
                        int k6 = as[j5].indexOf("=");
                        if(k6 != -1 && k6 != 0 && as[j5].substring(k6 + 1) != null && as[j5].substring(k6 + 1).length() > 0)
                        {
                            assinstanceproperty6[l3] = new SSInstanceProperty(as[j5].substring(0, k6), as[j5].substring(k6 + 1));
                            l3++;
                        }
                    }
                    
                    assinstanceproperty6[0] = new SSInstanceProperty("_server", "192.168.0.248");
                    assinstanceproperty6[1] = new SSInstanceProperty("_snmpversion", "v2");
                    assinstanceproperty6[2] = new SSInstanceProperty("_showRTTraffic", "true");
                    assinstanceproperty6[3] = new SSInstanceProperty("_community", "dragon");
                    

                    SSStringReturnValue ssstringreturnvalue2 = apimonitor.create(selectedMonitorType, s1, assinstanceproperty6);
                    outputStream.println("<p>Successful! The created id is: " + ssstringreturnvalue2.getValue());
                    outputStream.println("<br><p>Remember that you MUST press the \"forceSignalReload\" button in order to see the results of the operation!</form>");
                }
                catch(java.lang.Exception exception2)
                {
                    outputStream.println("<p>Error in create");
                    outputStream.println("<p>" + exception2.getMessage());
                }
            } else
            if(request.getValue("testName").equals("editMonitorProperties"))
            {
                String s2 = request.getValue("editMonitorMonitorID");
                String s19 = request.getValue("editMonitorGroupID");
                try
                {
                    SSInstanceProperty assinstanceproperty1[] = apimonitor.getInstanceProperties(s2, s19, APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
                    String s40 = "";
                    for(int k2 = 0; k2 < assinstanceproperty1.length; k2++)
                    {
                        s40 = s40 + assinstanceproperty1[k2].getName() + "=";
                        s40 = s40 + assinstanceproperty1[k2].getValue() + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"editMonitor\">");
                    outputStream.println("<input type=hidden name=\"editMonitorGroupID\" value=\"" + s19 + "\">");
                    outputStream.println("<input type=hidden name=\"editMonitorMonitorID\" value=\"" + s2 + "\">");
                    outputStream.println("<table><tr><td>Monitor Properties: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"editMonitorProperties\" rows=30 cols=80>" + s40 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"editMonitor\" value=\"Update This Monitor\"></form>");
                }
                catch(java.lang.Exception exception15)
                {
                    outputStream.println("The monitor " + s19 + " " + s2 + " could not be retrieved.<br>");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("editMonitor"))
            {
                try
                {
                    String s3 = request.getValue("editMonitorGroupID");
                    String s20 = request.getValue("editMonitorMonitorID");
                    String s32 = request.getValue("editMonitorProperties");
                    String as3[] = splitString(s32, "\n");
                    int l2 = 0;
                    for(int i4 = 0; i4 < as3.length; i4++)
                    {
                        int k5 = as3[i4].indexOf("=");
                        if(k5 != -1 && k5 != 0)
                        {
                            l2++;
                        }
                    }

                    SSInstanceProperty assinstanceproperty9[] = new SSInstanceProperty[l2];
                    int l5 = 0;
                    for(int l6 = 0; l6 < as3.length; l6++)
                    {
                        int k7 = as3[l6].indexOf("=");
                        if(k7 != -1 && k7 != 0)
                        {
                            assinstanceproperty9[l5] = new SSInstanceProperty(as3[l6].substring(0, k7), as3[l6].substring(k7 + 1));
                            l5++;
                        }
                    }

                    apimonitor.update(s20, s3, assinstanceproperty9);
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("The monitor " + s3 + " " + s20 + " has been updated Successfully!.<br>");
                }
                catch(java.lang.Exception exception3)
                {
                    outputStream.println("<p>Error in update");
                    outputStream.println("<p>" + exception3.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("deleteMonitor"))
            {
                String s4 = request.getValue("deleteMonitorID");
                String s21 = request.getValue("deleteGroupID");
                if(s21 == null || s21.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                if(s4 == null || s4.length() <= 0)
                {
                    outputStream.println("<p>Monitor ID must be supplied!");
                } else
                {
                    try
                    {
                        apimonitor.delete(s4, s21);
                        outputStream.println("Monitor target " + s21 + " " + s4 + " deleted succesfully!\n");
                    }
                    catch(java.lang.Exception exception16)
                    {
                        outputStream.println("<p>Error in delete");
                        outputStream.println("<p>" + exception16.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("moveMonitor"))
            {
                String s5 = request.getValue("moveMonitorID");
                String s22 = request.getValue("moveGroupID");
                String s33 = request.getValue("moveDestGroupID");
                if(s22 == null || s22.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                if(s5 == null || s5.length() <= 0)
                {
                    outputStream.println("<p>Monitor ID must be supplied!");
                } else
                if(s33 == null || s33.length() <= 0)
                {
                    outputStream.println("<p>Destination Group ID must be supplied!");
                } else
                {
                    try
                    {
                        SSStringReturnValue ssstringreturnvalue = apimonitor.move(s5, s22, s33);
                        if(s33 == null || s33.length() == 0)
                        {
                            s33 = "top-level group";
                        }
                        outputStream.println("Monitor moved succesfully to " + s33 + " and id remains: " + ssstringreturnvalue.getValue());
                    }
                    catch(java.lang.Exception exception19)
                    {
                        outputStream.println("<p>Error in move");
                        outputStream.println("<p>" + exception19.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("copyMonitor"))
            {
                String s6 = request.getValue("copyMonitorID");
                String s23 = request.getValue("copyGroupID");
                String s34 = request.getValue("copyDestGroupID");
                if(s23 == null || s23.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                if(s6 == null || s6.length() <= 0)
                {
                    outputStream.println("<p>Monitor ID must be supplied!");
                } else
                if(s34 == null || s34.length() <= 0)
                {
                    outputStream.println("<p>Destination Group ID must be supplied!");
                } else
                {
                    try
                    {
                        SSStringReturnValue ssstringreturnvalue1 = apimonitor.copy(s6, s23, s34);
                        if(s34 == null || s34.length() == 0)
                        {
                            s34 = "top-level group";
                        }
                        outputStream.println("Monitor copy to " + s34 + " was succesfully and new id is: " + ssstringreturnvalue1.getValue());
                    }
                    catch(java.lang.Exception exception20)
                    {
                        outputStream.println("<p>Error in copy");
                        outputStream.println("<p>" + exception20.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("runExistingMonitor"))
            {
                String s7 = request.getValue("runExistingMonitorID");
                String s24 = request.getValue("runExistingGroupID");
                if(s24 == null || s24.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                if(s7 == null || s7.length() <= 0)
                {
                    outputStream.println("<p>Monitor ID must be supplied!");
                } else
                {
                    try
                    {
                        SSMonitorInstance ssmonitorinstance = apimonitor.runExisting(s7, s24, 10000L);
                        outputStream.println("Here are the Run-Time properties after running the Existing monitor instance: \"" + s24 + "\" monitorID: \"" + s7 + "\"");
                        SSInstanceProperty assinstanceproperty3[] = ssmonitorinstance.getInstanceProperties();
                        printInstancePropertyArray(assinstanceproperty3);
                    }
                    catch(java.lang.Exception exception17)
                    {
                        outputStream.println("<p>Error in runExisting");
                        outputStream.println("<p>" + exception17.getMessage());
                    }
                }
            } else
            if(request.getValue("testName").equals("runTemporaryMonitorProperties"))
            {
                try
                {
                    selectedMonitorType = request.getValue("selectedMonitorTypeRunTemporary");
                    String s8 = request.getValue("runTemporaryMonitorGroupID");
                    SSPropertyDetails asspropertydetails2[] = apimonitor.getClassPropertiesDetails(selectedMonitorType, APISiteView.FILTER_CONFIGURATION_ADD_ALL, new SSInstanceProperty[0]);
                    String s35 = "";
                    for(int j1 = 0; j1 < asspropertydetails2.length; j1++)
                    {
                        String s42 = asspropertydetails2[j1].getDefaultValue();
                        s35 = s35 + asspropertydetails2[j1].getName() + "=";
                        if(s42 != null && s42.length() > 0)
                        {
                            s35 = s35 + s42;
                        }
                        s35 = s35 + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"runTemporaryMonitor\">");
                    outputStream.println("<input type=hidden name=\"selectedMonitorTypeRunTemporary\" value=\"" + selectedMonitorType + "\">");
                    outputStream.println("<input type=hidden name=\"runTemporaryMonitorGroupID\" value=\"" + s8 + "\">");
                    outputStream.println("<table><tr><td>Monitor Properties: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"runTemporaryMonitorProperties\" rows=30 cols=80>" + s35 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"RunTemporaryMonitor\" value=\"Run This Monitor\"></form>");
                }
                catch(java.lang.Exception exception4)
                {
                    outputStream.println("<p>Error in runTemporary");
                    outputStream.println("<p>" + exception4.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("runTemporaryMonitor"))
            {
                try
                {
                    selectedMonitorType = request.getValue("selectedMonitorTypeRunTemporary");
                    String s9 = request.getValue("runTemporaryMonitorGroupID");
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    String s25 = request.getValue("runTemporaryMonitorProperties");
                    String as1[] = splitString(s25, "\n");
                    int k1 = 0;
                    for(int i3 = 0; i3 < as1.length; i3++)
                    {
                        int j4 = as1[i3].indexOf("=");
                        if(j4 != -1 && j4 != 0 && as1[i3].substring(j4 + 1) != null && as1[i3].substring(j4 + 1).length() > 0)
                        {
                            k1++;
                        }
                    }

                    SSInstanceProperty assinstanceproperty7[] = new SSInstanceProperty[k1];
                    int k4 = 0;
                    for(int i6 = 0; i6 < as1.length; i6++)
                    {
                        int i7 = as1[i6].indexOf("=");
                        if(i7 != -1 && i7 != 0 && as1[i6].substring(i7 + 1) != null && as1[i6].substring(i7 + 1).length() > 0)
                        {
                            assinstanceproperty7[k4] = new SSInstanceProperty(as1[i6].substring(0, i7), as1[i6].substring(i7 + 1));
                            k4++;
                        }
                    }

                    SSInstanceProperty assinstanceproperty10[] = apimonitor.runTemporary(selectedMonitorType, assinstanceproperty7, 10000L);
                    outputStream.println("<p>The returned properties are:");
                    printInstancePropertyArray(assinstanceproperty10);
                }
                catch(java.lang.Exception exception5)
                {
                    outputStream.println("<p>Error in runTemporary");
                    outputStream.println("<p>" + exception5.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listMonitorAttributes"))
            {
                try
                {
                    level = 1;
                    selectedMonitorType = request.getValue("selectedMonitorTypeA");
                    outputStream.println("<p>List of all the attributes of a " + selectedMonitorType + "</p>\n");
                    SSInstanceProperty assinstanceproperty[] = apimonitor.getClassAttributes(selectedMonitorType);
                    printInstancePropertyArray(assinstanceproperty);
                }
                catch(java.lang.Exception exception6)
                {
                    outputStream.println("<p>Error in getClassAttributes");
                    outputStream.println("<p>" + exception6.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listMonitorClassPropertyDetails"))
            {
                try
                {
                    level = 1;
                    selectedMonitorType = request.getValue("selectedMonitorType");
                    selectedMonitorOperationType = request.getValue("selectedMonitorOperationType");
                    outputStream.println("<p>List of all the properties of a " + selectedMonitorType + "</p>\n");
                    SSPropertyDetails asspropertydetails[] = apimonitor.getClassPropertiesDetails(selectedMonitorType, (new Integer(selectedMonitorOperationType)).intValue(), new SSInstanceProperty[0]);
                    printPropertyDetails(asspropertydetails, 0);
                }
                catch(java.lang.Exception exception7)
                {
                    outputStream.println("<p>Error in getClassPropertyDetails");
                    outputStream.println("<p>" + exception7.getMessage());
                }
            } else
            if(request.isPost() && request.hasValue("testName") && request.getValue("testName").equals("listMonitorClassPropertyDetailPhased"))
            {
                level = java.lang.Integer.parseInt(request.getValue("level")) + 1;
                try
                {
                    int i;
                    for(i = 0; request.hasValue("currentPropertyStateKey" + i); i++) { }
                    currentPropertyState = new SSInstanceProperty[i];
                    for(int j = 0; request.hasValue("currentPropertyStateKey" + j); j++)
                    {
                        currentPropertyState[j] = new SSInstanceProperty(request.getValue("currentPropertyStateKey" + j), request.getValue("currentPropertyStateValue" + j));
                    }

                    selectedMonitorType = request.getValue("selectedMonitorType");
                    selectedMonitorOperationType = request.getValue("selectedMonitorOperationType");
                    SSPropertyDetails asspropertydetails4[] = apimonitor.getClassPropertiesDetails(selectedMonitorType, (new Integer(selectedMonitorOperationType)).intValue(), currentPropertyState);
                    printPropertyDetails(asspropertydetails4, level);
                }
                catch(java.lang.Exception exception8)
                {
                    outputStream.println("<p>Error in getClassPropertyDetails - multi phase");
                    outputStream.println("<p>" + exception8.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listMonitorInstancePropertyDetails"))
            {
                try
                {
                    String s10 = request.getValue("getMonitorInstancePropertyName");
                    String s26 = request.getValue("getMonitorInstancePropertyGroupID");
                    String s36 = request.getValue("getMonitorInstancePropertyMonitorID");
                    outputStream.println("<p>List of the details of a property " + s10 + "of a group instance: " + s26 + " </p>\n");
                    SSPropertyDetails asspropertydetails5[] = new SSPropertyDetails[1];
                    asspropertydetails5[0] = apimonitor.getInstancePropertyDetails(s10, s36, s26);
                    printPropertyDetails(asspropertydetails5, 0);
                }
                catch(java.lang.Exception exception9)
                {
                    outputStream.println("<p>Error in getClassProperties");
                    outputStream.println(exception9.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listMonitorInstances"))
            {
                String s11 = request.getValue("groupIDInstanceChild");
                if(s11 == null || s11.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                {
                    try
                    {
                        selectedMonitorOperationType = request.getValue("selectedMonitorInstanceOperationTypeChild");
                        SSMonitorInstance assmonitorinstance[] = apimonitor.getInstances(s11, (new Integer(selectedMonitorOperationType)).intValue());
                        outputStream.println("List ALL monitor instance properties of groupID: \"" + s11 + "\" for Operation Type " + selectedMonitorOperationType);
                        for(int k = 0; k < assmonitorinstance.length; k++)
                        {
                            SSInstanceProperty assinstanceproperty4[] = assmonitorinstance[k].getInstanceProperties();
                            outputStream.println("<p><b>Monitor ID:</b> " + assmonitorinstance[k].getMonitorId());
                            outputStream.println("<b>Group ID:</b> " + assmonitorinstance[k].getGroupId());
                            printInstancePropertyArray(assinstanceproperty4);
                        }

                    }
                    catch(java.lang.Exception exception14)
                    {
                        outputStream.println("<p>Error in getInstances");
                        outputStream.println("<p>" + exception14.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listMonitorInstanceProperties"))
            {
                String s12 = request.getValue("monitorIDInstance");
                String s27 = request.getValue("groupIDInstance");
                if(s27 == null || s27.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                if(s12 == null || s12.length() <= 0)
                {
                    outputStream.println("<p>Monitor ID must be supplied!");
                } else
                {
                    try
                    {
                        selectedMonitorOperationType = request.getValue("selectedMonitorInstanceOperationType");
                        outputStream.println("List monitor instance properties of monitorID: \"" + s12 + "\" and groupID: " + s27 + "\" for Operation Type " + selectedMonitorOperationType);
                        SSInstanceProperty assinstanceproperty2[] = apimonitor.getInstanceProperties(s12, s27, (new Integer(selectedMonitorOperationType)).intValue());
                        printInstancePropertyArray(assinstanceproperty2);
                    }
                    catch(java.lang.Exception exception18)
                    {
                        outputStream.println("<p>Error in getInstanceProperties");
                        outputStream.println("<p>" + exception18.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listMonitorInstanceProperty"))
            {
                String s13 = request.getValue("groupIDInstanceProperty");
                String s28 = request.getValue("monitorIDInstanceProperty");
                String s37 = request.getValue("propertyNameInstanceProperty");
                if(s13 == null || s13.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                if(s28 == null || s28.length() <= 0)
                {
                    outputStream.println("<p>Monitor ID must be supplied!");
                } else
                if(s37 == null || s28.length() <= 0)
                {
                    outputStream.println("<p>Property Name must be supplied!");
                } else
                {
                    try
                    {
                        selectedMonitorOperationType = request.getValue("selectedMonitorInstanceOperationTypeChild");
                        SSInstanceProperty assinstanceproperty5[] = new SSInstanceProperty[1];
                        assinstanceproperty5[0] = apimonitor.getInstanceProperty(s37, s28, s13);
                        outputStream.println("List ALL monitor instance properties details of groupID: \"" + s13 + "\" and monitorID: \"" + s28 + "\" for Operation Type " + selectedMonitorOperationType);
                        printInstancePropertyArray(assinstanceproperty5);
                    }
                    catch(java.lang.Exception exception21)
                    {
                        outputStream.println("<p>Error in getInstanceProperties");
                        outputStream.println("<p>" + exception21.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listMonitorInstancePropertyDetails"))
            {
                String s14 = request.getValue("groupIDInstanceDetail");
                String s29 = request.getValue("monitorIDInstanceDetail");
                String s38 = request.getValue("propertyNameInstanceDetail");
                if(s14 == null || s14.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                if(s29 == null || s29.length() <= 0)
                {
                    outputStream.println("<p>Monitor ID must be supplied!");
                } else
                if(s38 == null || s29.length() <= 0)
                {
                    outputStream.println("<p>Property Name must be supplied!");
                } else
                {
                    try
                    {
                        selectedMonitorOperationType = request.getValue("selectedMonitorInstanceOperationTypeChild");
                        SSPropertyDetails asspropertydetails6[] = new SSPropertyDetails[1];
                        asspropertydetails6[0] = apimonitor.getInstancePropertyDetails(s38, s29, s14);
                        outputStream.println("List ALL monitor instance properties details of groupID: \"" + s14 + "\" and monitorID: \"" + s29 + "\" for Operation Type " + selectedMonitorOperationType);
                        printPropertyDetails(asspropertydetails6, 0);
                    }
                    catch(java.lang.Exception exception22)
                    {
                        outputStream.println("<p>Error in getInstanceProperties");
                        outputStream.println("<p>" + exception22.getMessage());
                    }
                }
            } else
            if(request.getValue("testName").equals("getURLStepProperties"))
            {
                try
                {
                    String s15 = request.getValue("getURLStepGroupID");
                    SSPropertyDetails asspropertydetails3[] = apimonitor.getClassPropertiesDetails("URLSequenceMonitor", APISiteView.FILTER_CONFIGURATION_ADD_ALL, new SSInstanceProperty[0]);
                    String s39 = "";
                    for(int l1 = 0; l1 < asspropertydetails3.length; l1++)
                    {
                        String s43 = asspropertydetails3[l1].getDefaultValue();
                        s39 = s39 + asspropertydetails3[l1].getName() + "=";
                        if(s43 != null && s43.length() > 0)
                        {
                            s39 = s39 + s43;
                        }
                        s39 = s39 + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"getURLStep\">");
                    outputStream.println("<input type=hidden name=\"getURLStepGroupID\" value=\"" + s15 + "\">");
                    outputStream.println("<table><tr><td>URL SEQ Dynamic HTML Attributes: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"getURLStepProperties\" rows=30 cols=80>" + s39 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"GetURLStepProperties\" value=\"Get HTML Properties\"></form>");
                }
                catch(java.lang.Exception exception10)
                {
                    outputStream.println("<p>Error in getURLStepProperties");
                    outputStream.println("<p>" + exception10.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("getURLStep"))
            {
                try
                {
                    String s16 = request.getValue("getURLStepGroupID");
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    String s30 = request.getValue("getURLStepProperties");
                    String as2[] = splitString(s30, "\n");
                    int i2 = 0;
                    for(int j3 = 0; j3 < as2.length; j3++)
                    {
                        int l4 = as2[j3].indexOf("=");
                        if(l4 != -1 && l4 != 0 && as2[j3].substring(l4 + 1) != null && as2[j3].substring(l4 + 1).length() > 0)
                        {
                            i2++;
                        }
                    }

                    SSInstanceProperty assinstanceproperty8[] = new SSInstanceProperty[i2];
                    int i5 = 0;
                    for(int j6 = 0; j6 < as2.length; j6++)
                    {
                        int j7 = as2[j6].indexOf("=");
                        if(j7 != -1 && j7 != 0 && as2[j6].substring(j7 + 1) != null && as2[j6].substring(j7 + 1).length() > 0)
                        {
                            assinstanceproperty8[i5] = new SSInstanceProperty(as2[j6].substring(0, j7), as2[j6].substring(j7 + 1));
                            i5++;
                        }
                    }

                    SSInstanceProperty assinstanceproperty11[] = apimonitor.getURLStepProperties("URLSequenceMonitor", s16, assinstanceproperty8, "1");
                    outputStream.println("<p>The returned properties are:");
                    printInstancePropertyArray(assinstanceproperty11);
                }
                catch(java.lang.Exception exception11)
                {
                    outputStream.println("<p>Error in runTemporary");
                    outputStream.println("<p>" + exception11.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAllMonitors"))
            {
                outputStream.println("<p>List of all monitors in this SiteView:<p>\n");
                try
                {
                    java.util.Collection collection = apimonitor.getAllMonitors();
                    printMonitorInstanceInfoArray(collection);
                }
                catch(java.lang.Exception exception12)
                {
                    outputStream.println("<p>Error in getAllInstancesInfo");
                    outputStream.println("<p>" + exception12.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAllMonitorsGroup"))
            {
                try
                {
                    String s17 = request.getValue("listMonitorInfoGroupID");
                    outputStream.println("<p>List of all monitors in this SiteView for the group: " + s17);
                    java.util.Collection collection1 = apimonitor.getChildMonitors(s17);
                    printMonitorInstanceInfoArray(collection1);
                }
                catch(java.lang.Exception exception13)
                {
                    outputStream.println("<p>Error in getAllInstancesInfo");
                    outputStream.println("<p>" + exception13.getMessage());
                }
            } else
            {
                outputStream.println("<p>NO TEST SELECTED!");
            }
            outputStream.println("\n<p><a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
            outputStream.flush();
        }
        catch(java.lang.Exception exception)
        {
            outputStream.println("<p>Failure to initialize APIMonitor");
            outputStream.println("<p>" + exception.toString());
        }
    }

    private void printInstancePropertyArray(SSInstanceProperty assinstanceproperty[])
    {
        if(assinstanceproperty.length > 0)
        {
            outputStream.println("<p><table>");
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                outputStream.println("<tr><td>Name: " + assinstanceproperty[i].getName() + "</td><td>Value:");
                outputStream.println(assinstanceproperty[i].getValue() + "</td></tr>\n");
                outputStream.println("<tr></tr>");
            }

            outputStream.println("</table>");
        } else
        {
            outputStream.println("<p>No Instance Properties Found!");
        }
    }

    private void printMonitorInstanceInfoArray(java.util.Collection collection)
    {
        if(collection.size() > 0)
        {
            outputStream.println("<p><table>");
            COM.dragonflow.SiteView.Monitor monitor;
            for(java.util.Iterator iterator = collection.iterator(); iterator.hasNext(); outputStream.println("<tr><td>Monitor Name: " + monitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pName) + "</td><td> GroupID: " + monitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pOwnerID) + "</td><td> MonitorID: " + monitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "</td><td>Type: " + monitor.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pClass) + "</td></tr>\n"))
            {
                monitor = (COM.dragonflow.SiteView.Monitor)iterator.next();
            }

            outputStream.println("</table>");
        } else
        {
            outputStream.println("<p>No Monitor Instances Found!");
        }
    }

    private void printPropertyDetails(SSPropertyDetails asspropertydetails[], int i)
    {
        if(i > 0)
        {
            outputStream.println("<p>Results from PHASE " + i + "<p>");
        }
        java.util.Vector vector = new Vector();
        for(int j = 0; j < asspropertydetails.length; j++)
        {
            String s = "";
            if(asspropertydetails[j].isPrerequisite())
            {
                s = "<br>   (*** THIS PROPERTY IS A PREREQUISITE***)";
            }
            String s1 = asspropertydetails[j].getPropertyType();
            outputStream.println("<ul><li>name: <b>" + asspropertydetails[j].getName() + "</b>;" + s + "</li>\n");
            outputStream.println("<li>type: " + asspropertydetails[j].getPropertyType() + ";</li>\n");
            outputStream.println("<li>description: " + asspropertydetails[j].getDescription() + ";</li>\n");
            outputStream.println("<li>label: " + asspropertydetails[j].getLabel() + ";</li>\n");
            outputStream.println("<li>isEditable: " + asspropertydetails[j].isEditable() + ";</li>\n");
            outputStream.println("<li>isMultiValued: " + asspropertydetails[j].isMultiValued() + ";</li>\n");
            outputStream.println("<li>default value: " + COM.dragonflow.Utils.TextUtils.escapeHTML(asspropertydetails[j].getDefaultValue()) + ";</li>\n");
            outputStream.println("<li>prerequisite: " + asspropertydetails[j].isPrerequisite() + ";</li>\n");
            outputStream.println("<li>order: " + asspropertydetails[j].getOrder() + ";</li>\n");
            outputStream.println("<li>display units: " + asspropertydetails[j].getDisplayUnits() + ";</li>\n");
            outputStream.println("<li>advanced: " + asspropertydetails[j].isAdvanced() + ";</li>\n");
            outputStream.println("<li>password: " + asspropertydetails[j].isPassword() + ";</li>\n");
            String as[] = asspropertydetails[j].getSelectionDisplayNames();
            String as1[] = asspropertydetails[j].getSelectionIDs();
            String s2 = "";
            if(as != null)
            {
                outputStream.println("<li>selection choice data type: " + asspropertydetails[j].getSelectionChoiceType() + "</li>\n");
                outputStream.println("<li>selection choice display data: </li>\n");
                outputStream.println("<table border=\"border\">");
                outputStream.println("<tr><th>ID</th><th>Value</th></tr>");
                for(int j1 = 0; j1 < as.length; j1++)
                {
                    outputStream.println("<tr><td> " + COM.dragonflow.Utils.TextUtils.escapeHTML(as1[j1]) + "</td><td> " + COM.dragonflow.Utils.TextUtils.escapeHTML(as[j1]) + "</td></tr>\n");
                    s2 = s2 + COM.dragonflow.Utils.TextUtils.escapeHTML(as[j1]);
                }

                outputStream.println("</table>\n");
            }
            if(s1.equals("BROWSABLE") && asspropertydetails[j].getSelectionChoiceType().equals("XML"))
            {
                COM.datachannel.xml.om.Document document = new Document();
                document.loadXML(s2);
                java.util.Vector vector1 = new Vector();
                java.util.Vector vector2 = new Vector();
                org.w3c.dom.NodeList nodelist = document.getDocumentElement().getChildNodes();
                int k1 = nodelist.getLength();
                for(int l1 = 1; l1 < k1; l1++)
                {
                    findCounters(vector2, vector1, nodelist.item(l1), 1);
                }

                outputStream.println("<li>selection choice XML translated to list items: </li>\n");
                outputStream.println("<ul>");
                for(int i2 = 0; i2 < vector2.size(); i2++)
                {
                    outputStream.println((String)vector2.elementAt(i2) + "::" + (String)vector1.elementAt(i2));
                }

                outputStream.println("</ul>\n");
            }
            outputStream.println("</ul>\n");
            if(asspropertydetails[j].isPrerequisite())
            {
                vector.addElement(asspropertydetails[j].getName());
            }
        }

        if(vector.size() > 0)
        {
            outputStream.println("<form method=POST>");
            outputStream.println("<input type=hidden name=\"page\" value=\"apiMonitorTest\">");
            outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
            outputStream.println("<input type=hidden name=\"selectedMonitorType\" value=\"" + selectedMonitorType + "\">");
            outputStream.println("<input type=hidden name=\"selectedMonitorOperationType\" value=\"" + selectedMonitorOperationType + "\">");
            outputStream.println("<input type=hidden name=\"level\" value=\"" + level + "\">");
            outputStream.println("<input type=hidden name=\"testname\" value=\"listMonitorPropertiesPhased\">");
            outputStream.println("<p><b>Call AGAIN</b> with values for these properties:<ul>");
            int k = 0;
            if(currentPropertyState != null)
            {
                for(int l = 0; l < currentPropertyState.length; l++)
                {
                    outputStream.println("<input type=hidden name=\"currentPropertyStateKey" + l + "\" value=\"" + currentPropertyState[l].getName() + "\">");
                    outputStream.println("<input type=hidden name=\"currentPropertyStateValue" + l + "\" value=\"" + currentPropertyState[l].getValue() + "\">");
                }

                k = currentPropertyState.length;
            }
            for(int i1 = 0; i1 < vector.size(); i1++)
            {
                outputStream.println("<input type=hidden name=\"currentPropertyStateKey" + (i1 + k) + "\" value=\"" + vector.elementAt(i1) + "\">");
                outputStream.println("<li>" + vector.elementAt(i1) + " Value: <input type=text name=\"currentPropertyStateValue" + (i1 + k) + "\" value=\"\"></li>");
            }

            outputStream.println("</ul>");
            outputStream.println("<input type=submit name=\"Recall\" value=\"Recall\"></form>");
        } else
        {
            outputStream.println("<b?COMPLETE - NO PHASED CALLS TO GetClassProperty NEEDED</b>");
        }
        outputStream.println("<p>");
    }

    public void findCounters(java.util.Vector vector, java.util.Vector vector1, org.w3c.dom.Node node, int i)
    {
        if(node.getNodeType() == 1)
        {
            org.w3c.dom.NodeList nodelist = node.getChildNodes();
            int j = nodelist.getLength();
            String s = ((org.w3c.dom.Element)node).getAttribute("name");
            if(s != null)
            {
                String s1 = COM.dragonflow.Utils.TextUtils.arrayToString(getNodeNames(node));
                String s2 = node.getNodeName();
                if(s2.toLowerCase().equals("counter"))
                {
                    String s3 = COM.dragonflow.Utils.TextUtils.arrayToString(getNodeIdNames(node));
                    vector1.addElement(s3);
                    vector.addElement(s1);
                }
            }
            for(int k = 0; k < j; k++)
            {
                findCounters(vector, vector1, nodelist.item(k), i + 2);
            }

        }
    }

    String getIndentHTML(int i)
    {
        int j = i * 11;
        if(j == 0)
        {
            j = 1;
        }
        return "<img src=/SiteView/htdocs/artwork/empty1111.gif height=11 width=" + j + " border=0>";
    }

    jgl.Array getNodeNames(org.w3c.dom.Node node)
    {
        Array array = new Array();
        String s = ((org.w3c.dom.Element)node).getAttribute("name");
        if(s == null)
        {
            return array;
        }
        array.add(s);
        org.w3c.dom.Node node1 = node.getParentNode();
        do
        {
            if(node1 == null)
            {
                break;
            }
            String s1 = ((org.w3c.dom.Element)node1).getAttribute("name");
            if(s1 == null || s1.length() <= 0)
            {
                break;
            }
            array.add(s1);
            node1 = node1.getParentNode();
        } while(true);
        return array;
    }

    jgl.Array getNodeIdNames(org.w3c.dom.Node node)
    {
        Array array = new Array();
        String s = "id";
        String s1 = ((org.w3c.dom.Element)node).getAttribute(s);
        if(s1 == null || s1.length() == 0)
        {
            s = "name";
            s1 = ((org.w3c.dom.Element)node).getAttribute(s);
        }
        if(s1 == null)
        {
            return array;
        }
        array.add(s1);
        org.w3c.dom.Node node1 = node.getParentNode();
        do
        {
            if(node1 == null)
            {
                break;
            }
            String s2 = ((org.w3c.dom.Element)node1).getAttribute(s);
            if(s2 == null || s2.length() <= 0)
            {
                break;
            }
            array.add(s2);
            node1 = node1.getParentNode();
        } while(true);
        return array;
    }

    private String[] splitString(String s, String s1)
    {
        Array array = new Array();
        int i = s1.length();
        int j = 0;
        for(int k = s.indexOf(s1); k != -1; k = s.indexOf(s1, j))
        {
            array.add(s.substring(j, k).trim());
            j = k + i;
        }

        String as[] = new String[array.size()];
        for(int l = 0; l < as.length; l++)
        {
            as[l] = (String)array.get(l);
        }

        return as;
    }
}
