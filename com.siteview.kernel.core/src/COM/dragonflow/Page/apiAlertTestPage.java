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
import com.recursionsw.jgl.HashMap;
import COM.dragonflow.Api.APIAlert;
import COM.dragonflow.Api.SSInstanceProperty;

// Referenced classes of package COM.dragonflow.Page:
// apiMasterTestPage

public class apiAlertTestPage extends COM.dragonflow.Page.apiMasterTestPage
{

    private String selectedAlertType;
    private String selectedAlertOperationType;

    public apiAlertTestPage()
    {
        selectedAlertType = "";
        selectedAlertOperationType = "";
    }

    public void printBody()
        throws java.lang.Exception
    {
        try
        {
            COM.dragonflow.Api.APIAlert apialert = new APIAlert();
            outputStream.println("<H1>API Alert Test Page</H1>\n");
            if(request.getValue("testName").equals("createAlertProperties"))
            {
                try
                {
                    selectedAlertType = request.getValue("selectedAlertTypeCreate");
                    String s = request.getValue("createAddAlertGroupID");
                    String s12 = request.getValue("createAddAlertMonitorID");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails2[] = apialert.getClassPropertiesDetails(selectedAlertType, (new Integer(COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL)).intValue(), new HashMap());
                    String s30 = "";
                    for(int i = 0; i < asspropertydetails2.length; i++)
                    {
                        String s37 = asspropertydetails2[i].getDefaultValue();
                        s30 = s30 + asspropertydetails2[i].getName() + "=";
                        if(s37 != null && s37.length() > 0)
                        {
                            s30 = s30 + s37;
                        }
                        s30 = s30 + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiAlertTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"createAlert\">");
                    outputStream.println("<input type=hidden name=\"selectedAlertTypeCreate\" value=\"" + selectedAlertType + "\">");
                    outputStream.println("<input type=hidden name=\"createAddAlertGroupID\" value=\"" + s + "\">");
                    outputStream.println("<input type=hidden name=\"createAddAlertMonitorID\" value=\"" + s12 + "\">");
                    outputStream.println("<table><tr><td>Alert Properties: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"createAlertProperties\" rows=30 cols=80>" + s30 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"CreateAlert\" value=\"Create This Alert\"></form>");
                }
                catch(java.lang.Exception exception1)
                {
                    outputStream.println("<p>Error in create");
                    outputStream.println("<p>" + exception1.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("createAlert"))
            {
                try
                {
                    selectedAlertType = request.getValue("selectedAlertTypeCreate");
                    String s1 = request.getValue("createAddAlertGroupID");
                    String s13 = request.getValue("createAddAlertMonitorID");
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiAlertTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    String s22 = request.getValue("createAlertProperties");
                    String as[] = splitString(s22, "\n");
                    int j = 0;
                    for(int l = 0; l < as.length; l++)
                    {
                        int k1 = as[l].indexOf("=");
                        if(k1 != -1 && k1 != 0 && as[l].substring(k1 + 1) != null && as[l].substring(k1 + 1).length() > 0)
                        {
                            j++;
                        }
                    }

                    COM.dragonflow.Api.SSInstanceProperty assinstanceproperty4[] = new COM.dragonflow.Api.SSInstanceProperty[j];
                    int l1 = 0;
                    for(int j2 = 0; j2 < as.length; j2++)
                    {
                        int j3 = as[j2].indexOf("=");
                        if(j3 != -1 && j3 != 0 && as[j2].substring(j3 + 1) != null && as[j2].substring(j3 + 1).length() > 0)
                        {
                            assinstanceproperty4[l1] = new SSInstanceProperty(as[j2].substring(0, j3), as[j2].substring(j3 + 1));
                            l1++;
                        }
                    }

                    String s38 = s1;
                    if(s13 != null && s13.length() > 0)
                    {
                        s38 = s38 + " " + s13;
                    }
                    COM.dragonflow.Api.Alert alert = apialert.create(selectedAlertType, s38, assinstanceproperty4);
                    outputStream.println("<p>The created alert id is: " + alert.toString() + "</form>");
                }
                catch(java.lang.Exception exception2)
                {
                    outputStream.println("<p>Error in create");
                    outputStream.println("<p>" + exception2.getMessage());
                }
            } else
            if(request.getValue("testName").equals("editAlertProperties"))
            {
                String s2 = request.getValue("editAlertMonitorID");
                String s14 = request.getValue("editAlertGroupID");
                String s23 = request.getValue("editAlertAlertID");
                try
                {
                    COM.dragonflow.Api.SSInstanceProperty assinstanceproperty1[] = apialert.getInstanceProperties(s23, s2, s14, COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
                    String s35 = "";
                    for(int i1 = 0; i1 < assinstanceproperty1.length; i1++)
                    {
                        s35 = s35 + assinstanceproperty1[i1].getName() + "=";
                        if(assinstanceproperty1[i1].getValue() instanceof String)
                        {
                            if(assinstanceproperty1[i1].getValue() != null)
                            {
                                s35 = s35 + assinstanceproperty1[i1].getValue();
                            }
                            s35 = s35 + "\n";
                            continue;
                        }
                        if(!(assinstanceproperty1[i1].getValue() instanceof String[]))
                        {
                            continue;
                        }
                        if(assinstanceproperty1[i1].getValue() != null)
                        {
                            String as2[] = (String[])assinstanceproperty1[i1].getValue();
                            for(int k2 = 0; k2 < as2.length; k2++)
                            {
                                if(k2 != 0)
                                {
                                    s35 = s35 + ", ";
                                }
                                s35 = s35 + as2[k2];
                            }

                        }
                        s35 = s35 + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiAlertTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"editAlert\">");
                    outputStream.println("<input type=hidden name=\"editAlertGroupID\" value=\"" + s14 + "\">");
                    outputStream.println("<input type=hidden name=\"editAlertMonitorID\" value=\"" + s2 + "\">");
                    outputStream.println("<input type=hidden name=\"editAlertAlertID\" value=\"" + s23 + "\">");
                    outputStream.println("<table><tr><td>Alert Properties: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"editAlertProperties\" rows=30 cols=80>" + s35 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"EditAlert\" value=\"Update This Alert\"></form>");
                }
                catch(java.lang.Exception exception9)
                {
                    String s36 = "SiteView";
                    if(s14.length() > 0)
                    {
                        s36 = s36 + "/" + s14;
                    }
                    if(s2.length() > 0)
                    {
                        s36 = s36 + "/" + s2;
                    }
                    outputStream.println("The alert " + s36 + "/" + s23 + " could not be retrieved.<br>");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("editAlert"))
            {
                try
                {
                    String s3 = request.getValue("editAlertGroupID");
                    String s15 = request.getValue("editAlertMonitorID");
                    String s24 = request.getValue("editAlertAlertID");
                    String s31 = request.getValue("editAlertProperties");
                    String as1[] = splitString(s31, "\n");
                    int j1 = 0;
                    for(int i2 = 0; i2 < as1.length; i2++)
                    {
                        int l2 = as1[i2].indexOf("=");
                        if(l2 != -1 && l2 != 0)
                        {
                            j1++;
                        }
                    }

                    COM.dragonflow.Api.SSInstanceProperty assinstanceproperty6[] = new COM.dragonflow.Api.SSInstanceProperty[j1];
                    int i3 = 0;
                    for(int k3 = 0; k3 < as1.length; k3++)
                    {
                        int l3 = as1[k3].indexOf("=");
                        if(l3 != -1 && l3 != 0)
                        {
                            assinstanceproperty6[i3] = new SSInstanceProperty(as1[k3].substring(0, l3), as1[k3].substring(l3 + 1));
                            i3++;
                        }
                    }

                    String s39 = "";
                    if(s3 != null && s3.length() > 0 && !s3.equals("_master"))
                    {
                        s39 = s3;
                    }
                    if(s15 != null && s15.length() > 0 && !s15.equals("_config"))
                    {
                        s39 = s39 + " " + s15;
                    }
                    apialert.update(s24, s39, assinstanceproperty6);
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiAlertTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    String s40 = "SiteView";
                    if(s3.length() > 0)
                    {
                        s40 = s40 + "/" + s3;
                    }
                    if(s15.length() > 0)
                    {
                        s40 = s40 + "/" + s15;
                    }
                    outputStream.println("The alert " + s40 + "/" + s24 + " has been updated.<br>");
                }
                catch(java.lang.Exception exception3)
                {
                    outputStream.println("<p>Error in update");
                    outputStream.println("<p>" + exception3.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("deleteAlert"))
            {
                String s4 = request.getValue("deleteAlertAlertID");
                String s16 = request.getValue("deleteAlertGroupID");
                String s25 = request.getValue("deleteAlertMonitorID");
                if(s4 != null && s4.length() > 0)
                {
                    try
                    {
                        String s32 = "";
                        if(s16 != null && s16.length() > 0 && !s16.equals("_master"))
                        {
                            s32 = s16;
                        }
                        if(s25 != null && s25.length() > 0 && !s25.equals("_config"))
                        {
                            s32 = s32 + " " + s25;
                        }
                        outputStream.println("The alert id " + s4 + " for target id " + s32 + "  has been deleted.<br>");
                    }
                    catch(java.lang.Exception exception10)
                    {
                        outputStream.println("<p>Error in delete");
                        outputStream.println(exception10.getMessage() + "<br>\n");
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAlertAttributes"))
            {
                try
                {
                    selectedAlertType = request.getValue("selectedAlertTypeA");
                    outputStream.println("<p>List of all the attributes of a " + selectedAlertType + "</p>\n");
                    COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = apialert.getClassAttributes(selectedAlertType);
                    printInstancePropertyArray(assinstanceproperty);
                }
                catch(java.lang.Exception exception4)
                {
                    outputStream.println("<p>Error in getClassAttributes");
                    outputStream.println("<p>" + exception4.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAlertPropertiesDetails"))
            {
                try
                {
                    selectedAlertType = request.getValue("selectedAlertType");
                    selectedAlertOperationType = request.getValue("selectedAlertOperationType");
                    outputStream.println("<p>List of all the properties of a " + selectedAlertType + "</p>\n");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails[] = apialert.getClassPropertiesDetails(selectedAlertType, (new Integer(selectedAlertOperationType)).intValue(), new HashMap());
                    printPropertyDetails(asspropertydetails);
                }
                catch(java.lang.Exception exception5)
                {
                    outputStream.println("<p>Error in getClassProperties");
                    outputStream.println("<p>" + exception5.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAlertPropertyDetails"))
            {
                try
                {
                    selectedAlertType = request.getValue("selectedAlertType2");
                    String s5 = request.getValue("getClassPropertyName");
                    outputStream.println("<p>List of the property details for " + s5 + " for a " + selectedAlertType + "</p>\n");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails1[] = new COM.dragonflow.Api.SSPropertyDetails[1];
                    asspropertydetails1[0] = apialert.getClassPropertyDetails(s5, selectedAlertType, new HashMap());
                    printPropertyDetails(asspropertydetails1);
                }
                catch(java.lang.Exception exception6)
                {
                    outputStream.println("<p>Error in getClassProperties");
                    outputStream.println("<p>" + exception6.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAlertInstancePropertyDetails"))
            {
                try
                {
                    String s6 = request.getValue("alertInstanceAlertID");
                    String s17 = request.getValue("alertInstanceGroupID");
                    String s26 = request.getValue("alertInstanceMonitorID");
                    String s33 = request.getValue("getInstancePropertyName");
                    outputStream.println("<p>List of the property details for " + s17 + "\\" + s26 + "\\" + s6 + "\"" + s33 + " for a " + selectedAlertType + "</p>\n");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails3[] = new COM.dragonflow.Api.SSPropertyDetails[1];
                    asspropertydetails3[0] = apialert.getInstancePropertyDetails(s33, s6, s26, s17);
                    printPropertyDetails(asspropertydetails3);
                }
                catch(java.lang.Exception exception7)
                {
                    outputStream.println("<p>Error in getClassProperties");
                    outputStream.println("<p>" + exception7.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("getInstanceAlerts"))
            {
                String s7 = request.getValue("alertInstanceMonitorGroupID");
                String s18 = request.getValue("alertInstanceMonitorMonitorID");
                outputStream.println("<p>List of all alert properties that are directly associated with group id \"" + s7 + "\" and monitor id \"" + s18 + "\"<br>\n");
                try
                {
                    String s27 = request.getValue("selectedAlertInstanceOperationType2");
                    COM.dragonflow.Api.SVAlertInstance assalertinstance[] = apialert.getInstances(s18, s7, (new Integer(s27)).intValue());
                    for(int k = 0; k < assalertinstance.length; k++)
                    {
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty5[] = assalertinstance[k].getInstanceProperties();
                        outputStream.println("<p>Alert ID: " + assalertinstance[k].getAlertId());
                        printInstancePropertyArray(assinstanceproperty5);
                    }

                }
                catch(java.lang.Exception exception8)
                {
                    outputStream.println("<p>Error in getInstances");
                    outputStream.println(exception8.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAlertInstanceProperties"))
            {
                String s8 = request.getValue("alertIDInstance");
                String s19 = request.getValue("monitorIDInstance");
                String s28 = request.getValue("groupIDInstance");
                if(s8 == null || s8.length() <= 0)
                {
                    outputStream.println("<p>Alert ID must be supplied!");
                } else
                {
                    try
                    {
                        selectedAlertOperationType = request.getValue("selectedAlertInstanceOperationType3");
                        outputStream.println("List alert instance properties of alertID: \"" + s8 + "\", monitorID: \"" + s19 + "\" and groupID: \"" + s28 + "\" for Operation Type " + selectedAlertOperationType);
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty2[] = apialert.getInstanceProperties(s8, s19, s28, (new Integer(selectedAlertOperationType)).intValue());
                        printInstancePropertyArray(assinstanceproperty2);
                    }
                    catch(java.lang.Exception exception11)
                    {
                        outputStream.println("<p>Error in getInstanceProperties");
                        outputStream.println("<p>" + exception11.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAlertInstanceProperty"))
            {
                String s9 = request.getValue("groupIDInstanceProperty");
                String s20 = request.getValue("monitorIDInstanceProperty");
                String s29 = request.getValue("alertIDInstanceProperty");
                String s34 = request.getValue("propertyNameInstanceProperty");
                if(s29 == null || s29.length() <= 0)
                {
                    outputStream.println("<p>Alert ID must be supplied!");
                } else
                if(s34 == null || s34.length() <= 0)
                {
                    outputStream.println("<p>Property name must be supplied!");
                } else
                {
                    try
                    {
                        selectedAlertOperationType = request.getValue("selectedAlertInstanceOperationTypeChild");
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty3[] = new COM.dragonflow.Api.SSInstanceProperty[1];
                        assinstanceproperty3[0] = apialert.getInstanceProperty(s34, s29, s20, s9);
                        outputStream.println("List ALL alert instance properties details of alertID: \"" + s29 + "\", groupID: \"" + s9 + "\" and monitorID: \"" + s20 + "\" for Operation Type " + selectedAlertOperationType);
                        printInstancePropertyArray(assinstanceproperty3);
                    }
                    catch(java.lang.Exception exception12)
                    {
                        outputStream.println("<p>Error in getInstanceProperties");
                        outputStream.println("<p>" + exception12.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAllAlerts"))
            {
                outputStream.println("<p>List of all alerts in this SiteView:<br>\n");
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listChildAlerts"))
            {
                String s10 = request.getValue("alertParentGroupID");
                outputStream.println("<p>List of all alerts that are children of the group " + s10 + "<br>\n");
                if(s10 == null || s10.length() <= 0);
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listChildMonitorAlerts"))
            {
                String s11 = request.getValue("alertGroupGroupID");
                String s21 = request.getValue("alertGroupMonitorID");
                outputStream.println("<p>List of all alerts that are directly associated with this group " + s11 + " and monitor id " + s21 + "<br>\n");
                if(s11 == null || s11.length() <= 0 || s21 == null || s21.length() <= 0);
            } else
            {
                outputStream.println("<p>NO TEST SELECTED!");
            }
            outputStream.println("\n<p><a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
            outputStream.flush();
        }
        catch(java.lang.Exception exception)
        {
            outputStream.println("<p>Failure to initialize APIAlert");
            outputStream.println("<p>" + exception.toString());
        }
    }

    private void printGroupInstanceInfoArray(COM.dragonflow.Api.SSAlertInstanceInfo assalertinstanceinfo[])
    {
        if(assalertinstanceinfo.length > 0)
        {
            outputStream.println("<p><table>");
            for(int i = 0; i < assalertinstanceinfo.length; i++)
            {
                outputStream.println("<tr><td><b>Alert Name:</b> " + assalertinstanceinfo[i].getName() + "</td><td><b> Alert Full ID:</b> " + assalertinstanceinfo[i].getAlertFullId() + "</td><td><b> Alert ID:</b> " + assalertinstanceinfo[i].getAlertId() + "</td><td><b> Group ID:</b> " + assalertinstanceinfo[i].getGroupId() + "</td><td><b> Monitor ID:</b> " + assalertinstanceinfo[i].getMonitorId() + "</td><td><b> Type:</b> " + assalertinstanceinfo[i].getType() + "</td></tr>\n");
            }

            outputStream.println("</table>");
        } else
        {
            outputStream.println("<p>No Alert Instances Found!");
        }
    }

    private void printInstancePropertyArray(COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
    {
        if(assinstanceproperty.length > 0)
        {
            outputStream.println("<p><table>");
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                if(assinstanceproperty[i].getValue() instanceof String)
                {
                    outputStream.println("<tr><td><b>Name:</b> " + assinstanceproperty[i].getName() + "</td><td><b>Value:</b> " + assinstanceproperty[i].getValue() + "</td><tr>\n");
                    continue;
                }
                if(!(assinstanceproperty[i].getValue() instanceof String[]))
                {
                    continue;
                }
                String as[] = (String[])assinstanceproperty[i].getValue();
                for(int j = 0; j < as.length; j++)
                {
                    if(j == 0)
                    {
                        outputStream.println("<tr><td><b>Name:</b> " + assinstanceproperty[i].getName() + "</td><td><b>Value:</b> " + as[j] + "</td><tr>\n");
                    } else
                    {
                        outputStream.println("<tr><td></td><td><b>Value:</b> " + as[j] + "</td><tr>\n");
                    }
                }

            }

            outputStream.println("</table>");
        } else
        {
            outputStream.println("<p>No Instance Properties Found!");
        }
    }

    private void printPropertyDetails(COM.dragonflow.Api.SSPropertyDetails asspropertydetails[])
    {
        for(int i = 0; i < asspropertydetails.length; i++)
        {
            String s = "";
            if(asspropertydetails[i].isPrerequisite())
            {
                s = "<br>   (*** THIS PROPERTY IS A PREREQUISITE***)";
            }
            outputStream.println("<ul><li>name: <b>" + asspropertydetails[i].getName() + "</b>;" + s + "</li>\n");
            outputStream.println("<li>type: " + asspropertydetails[i].getPropertyType() + ";</li>\n");
            outputStream.println("<li>description: " + asspropertydetails[i].getDescription() + ";</li>\n");
            outputStream.println("<li>label: " + asspropertydetails[i].getLabel() + ";</li>\n");
            outputStream.println("<li>isEditable: " + asspropertydetails[i].isEditable() + ";</li>\n");
            outputStream.println("<li>isMultiValued: " + asspropertydetails[i].isMultiValued() + ";</li>\n");
            outputStream.println("<li>default value: " + COM.dragonflow.Utils.TextUtils.escapeHTML(asspropertydetails[i].getDefaultValue()) + ";</li>\n");
            outputStream.println("<li>prerequisite: " + asspropertydetails[i].isPrerequisite() + ";</li>\n");
            outputStream.println("<li>order: " + asspropertydetails[i].getOrder() + ";</li>\n");
            outputStream.println("<li>display units: " + asspropertydetails[i].getDisplayUnits() + ";</li>\n");
            outputStream.println("<li>advanced: " + asspropertydetails[i].isAdvanced() + ";</li>\n");
            outputStream.println("<li>password: " + asspropertydetails[i].isPassword() + ";</li>\n");
            String as[] = asspropertydetails[i].getSelectionDisplayNames();
            String as1[] = asspropertydetails[i].getSelectionIDs();
            if(as != null)
            {
                outputStream.println("<li>selection choice data type: " + asspropertydetails[i].getSelectionChoiceType() + "</li>\n");
                outputStream.println("<li>selection choice display data: </li>\n");
                outputStream.println("<table border=\"border\">");
                outputStream.println("<tr><th>ID</th><th>Value</th></tr>");
                for(int j = 0; j < as.length; j++)
                {
                    outputStream.println("<tr><td> " + COM.dragonflow.Utils.TextUtils.escapeHTML(as1[j]) + "</td><td> " + COM.dragonflow.Utils.TextUtils.escapeHTML(as[j]) + "</td></tr>\n");
                }

                outputStream.println("</table>\n");
            }
            outputStream.println("</ul>\n");
        }

        outputStream.println("<p>");
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
