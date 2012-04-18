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
import COM.dragonflow.Api.APIGroup;
import COM.dragonflow.Api.SSInstanceProperty;

// Referenced classes of package COM.dragonflow.Page:
// apiMasterTestPage

public class apiGroupTestPage extends COM.dragonflow.Page.apiMasterTestPage
{

    public apiGroupTestPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        try
        {
            COM.dragonflow.Api.APIGroup apigroup = new APIGroup();
            outputStream.println("<H1>API Group Test Page</H1>\n");
            if(request.getValue("testName").equals("createGroupProperties"))
            {
                try
                {
                    String s = request.getValue("createGroupName");
                    String s14 = request.getValue("createParentGroupID");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails2[] = apigroup.getClassPropertiesDetails(COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL);
                    String s26 = "";
                    for(int l = 0; l < asspropertydetails2.length; l++)
                    {
                        s26 = s26 + asspropertydetails2[l].getName() + "=";
                        if(asspropertydetails2[l].getName().equals("_name"))
                        {
                            s26 = s26 + s;
                        } else
                        {
                            String s27 = asspropertydetails2[l].getDefaultValue();
                            if(s27 != null && s27.length() > 0)
                            {
                                s26 = s26 + s27;
                            }
                        }
                        s26 = s26 + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiGroupTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"createGroup\">");
                    outputStream.println("<input type=hidden name=\"createGroupName\" value=\"" + s + "\">");
                    outputStream.println("<input type=hidden name=\"createParentGroupID\" value=\"" + s14 + "\">");
                    if(s14 == null || s14.length() <= 0)
                    {
                        outputStream.println("<table><tr><td>Parent Group: TOP-LEVEL GROUP</td></tr>");
                    } else
                    {
                        outputStream.println("<table><tr><td>Parent Group: " + s14 + "</td></tr>");
                    }
                    outputStream.println("<table><tr><td>Group Properties: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"createGroupProperties\" rows=30 cols=80>" + s26 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"CreateGroup\" value=\"Create This Group\"></form>");
                }
                catch(java.lang.Exception exception1)
                {
                    outputStream.println("<p>Error in create");
                    outputStream.println("<p>" + exception1.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("createGroup"))
            {
                try
                {
                    String s1 = request.getValue("createGroupName");
                    String s15 = null;
                    if(request.hasValue("createParentGroupID"))
                    {
                        s15 = request.getValue("createParentGroupID");
                    }
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiGroupTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    String s23 = request.getValue("createGroupProperties");
                    String as1[] = splitString(s23, "\n");
                    int i1 = 0;
                    for(int k1 = 0; k1 < as1.length; k1++)
                    {
                        int j2 = as1[k1].indexOf("=");
                        if(j2 != -1 && j2 != 0 && as1[k1].substring(j2 + 1) != null && as1[k1].substring(j2 + 1).length() > 0)
                        {
                            i1++;
                        }
                    }

                    COM.dragonflow.Api.SSInstanceProperty assinstanceproperty6[] = new COM.dragonflow.Api.SSInstanceProperty[i1];
                    int k2 = 0;
                    for(int i3 = 0; i3 < as1.length; i3++)
                    {
                        int k3 = as1[i3].indexOf("=");
                        if(k3 != -1 && k3 != 0 && as1[i3].substring(k3 + 1) != null && as1[i3].substring(k3 + 1).length() > 0)
                        {
                            assinstanceproperty6[k2] = new SSInstanceProperty(as1[i3].substring(0, k3), as1[i3].substring(k3 + 1));
                            k2++;
                        }
                    }

                    COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue2 = apigroup.create(s1, s15, assinstanceproperty6);
                    outputStream.println("<p>The created group id is: " + ssstringreturnvalue2.getValue());
                    outputStream.println("<br><p>Remember that you MUST press the \"forceSignalReload\" button in order to see the results of the operation!</form>");
                }
                catch(java.lang.Exception exception2)
                {
                    outputStream.println("<p>Error in create");
                    outputStream.println("<p>" + exception2.getMessage());
                }
            } else
            if(request.getValue("testName").equals("editGroupProperties"))
            {
                String s2 = request.getValue("editGroupID");
                try
                {
                    COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = apigroup.getInstanceProperties(s2, COM.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
                    String s24 = "";
                    for(int i = 0; i < assinstanceproperty.length; i++)
                    {
                        s24 = s24 + assinstanceproperty[i].getName() + "=";
                        s24 = s24 + assinstanceproperty[i].getValue() + "\n";
                    }

                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiGroupTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("<input type=hidden name=\"testName\" value=\"editGroup\">");
                    outputStream.println("<input type=hidden name=\"editGroupID\" value=\"" + s2 + "\">");
                    outputStream.println("<table><tr><td>Group Properties: </td></tr>");
                    outputStream.println("<tr><td><textarea type=text name=\"editGroupProperties\" rows=30 cols=80>" + s24 + "</textarea></td></tr></table>");
                    outputStream.println("<input type=submit name=\"EditGroup\" value=\"Update This Group\"></form>");
                }
                catch(java.lang.Exception exception11)
                {
                    outputStream.println("The group " + s2 + " could not be retrieved.<br>");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("editGroup"))
            {
                try
                {
                    String s3 = request.getValue("editGroupID");
                    String s16 = request.getValue("editGroupProperties");
                    String as[] = splitString(s16, "\n");
                    int j = 0;
                    for(int j1 = 0; j1 < as.length; j1++)
                    {
                        int l1 = as[j1].indexOf("=");
                        if(l1 != -1 && l1 != 0)
                        {
                            j++;
                        }
                    }

                    COM.dragonflow.Api.SSInstanceProperty assinstanceproperty4[] = new COM.dragonflow.Api.SSInstanceProperty[j];
                    int i2 = 0;
                    for(int l2 = 0; l2 < as.length; l2++)
                    {
                        int j3 = as[l2].indexOf("=");
                        if(j3 != -1 && j3 != 0)
                        {
                            assinstanceproperty4[i2] = new SSInstanceProperty(as[l2].substring(0, j3), as[l2].substring(j3 + 1));
                            i2++;
                        }
                    }

                    apigroup.update(s3, assinstanceproperty4);
                    outputStream.println("<form method=POST>");
                    outputStream.println("<input type=hidden name=\"page\" value=\"apiGroupTest\">");
                    outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                    outputStream.println("The group " + s3 + " has been edited.<br>");
                }
                catch(java.lang.Exception exception3)
                {
                    outputStream.println("<p>Error in update");
                    outputStream.println("<p>" + exception3.getMessage());
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("delete"))
            {
                try
                {
                    String s4 = request.getValue("groupID");
                    if(s4 != null && s4.length() > 0)
                    {
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty1[] = new COM.dragonflow.Api.SSInstanceProperty[1];
                        assinstanceproperty1[0] = new SSInstanceProperty("_name", s4);
                        outputStream.println("<p>Delete a group " + s4 + " and any Monitors or additional subGroups contained within this group");
                        apigroup.delete(s4);
                        outputStream.println("<p>Group id " + s4 + " deleted!");
                    } else
                    {
                        outputStream.println("<p>Both Group ID and Parent Group ID must be supplied!");
                    }
                }
                catch(java.lang.Exception exception4)
                {
                    outputStream.println("<p>Error in delete");
                    outputStream.println(exception4.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("moveGroup"))
            {
                String s5 = request.getValue("grpMoveGroupID");
                String s17 = null;
                if(request.hasValue("grpMoveDestGroupID"))
                {
                    s17 = request.getValue("grpMoveDestGroupID");
                }
                if(s5 == null || s5.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                {
                    try
                    {
                        COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue = apigroup.move(s5, s17);
                        if(s17 == null || s17.length() == 0)
                        {
                            s17 = "top-level group";
                        }
                        outputStream.println("Group moved succesfully to " + s17 + " and id remains: " + ssstringreturnvalue.getValue());
                    }
                    catch(java.lang.Exception exception14)
                    {
                        outputStream.println("<p>Error in move");
                        outputStream.println("<p>" + exception14.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("copyGroup"))
            {
                String s6 = request.getValue("grpCopyGroupID");
                String s18 = null;
                if(request.hasValue("grpCopyDestGroupID"))
                {
                    s18 = request.getValue("grpCopyDestGroupID");
                }
                if(s6 == null || s6.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                {
                    try
                    {
                        COM.dragonflow.Api.SSStringReturnValue ssstringreturnvalue1 = apigroup.copy(s6, s18);
                        if(s18 == null || s18.length() == 0)
                        {
                            s18 = "top-level group";
                        }
                        outputStream.println("Group copy to " + s18 + " was succesfully and new id is: " + ssstringreturnvalue1.getValue());
                    }
                    catch(java.lang.Exception exception15)
                    {
                        outputStream.println("<p>Error in copy");
                        outputStream.println("<p>" + exception15.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listGroupPropertiesDetails"))
            {
                try
                {
                    String s7 = request.getValue("listGroupPropertiesOperation");
                    outputStream.println("<p>List of the properties of a group for the selected Operation:</p>\n");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails[] = apigroup.getClassPropertiesDetails((new Integer(s7)).intValue());
                    printPropertyDetails(asspropertydetails);
                }
                catch(java.lang.Exception exception5)
                {
                    outputStream.println("<p>Error in getClassPropertiesDetail");
                    outputStream.println(exception5.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listGroupPropertyDetails"))
            {
                try
                {
                    String s8 = request.getValue("getClassPropertyName");
                    outputStream.println("<p>List of the properties of a group:</p>\n");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails1[] = new COM.dragonflow.Api.SSPropertyDetails[1];
                    asspropertydetails1[0] = apigroup.getClassPropertyDetails(s8);
                    printPropertyDetails(asspropertydetails1);
                }
                catch(java.lang.Exception exception6)
                {
                    outputStream.println("<p>Error in getClassPropertyDetails");
                    outputStream.println(exception6.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listGroupInstancePropertyDetails"))
            {
                try
                {
                    String s9 = request.getValue("getGroupInstancePropertyName");
                    String s19 = request.getValue("getGroupInstanceGroupID");
                    outputStream.println("<p>List of the properties of a group:</p>\n");
                    COM.dragonflow.Api.SSPropertyDetails asspropertydetails3[] = new COM.dragonflow.Api.SSPropertyDetails[1];
                    asspropertydetails3[0] = apigroup.getInstancePropertyScalars(s9, s19);
                    printPropertyDetails(asspropertydetails3);
                }
                catch(java.lang.Exception exception7)
                {
                    outputStream.println("<p>Error in getInstancePropertyDetails");
                    outputStream.println(exception7.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listGroupInstances"))
            {
                String s10 = request.getValue("getGroupInstancesGroupID");
                if(s10 == null || s10.length() <= 0)
                {
                    outputStream.println("<p>Group ID must be supplied!");
                } else
                {
                    try
                    {
                        String s20 = request.getValue("getGroupInstancesOperation");
                        COM.dragonflow.Api.SSGroupInstance assgroupinstance[] = apigroup.getInstances(s10, (new Integer(s20)).intValue());
                        outputStream.println("List ALL group instance properties of groupID: \"" + s10);
                        for(int k = 0; k < assgroupinstance.length; k++)
                        {
                            COM.dragonflow.Api.SSInstanceProperty assinstanceproperty5[] = assgroupinstance[k].getInstanceProperties();
                            outputStream.println("<p><b>Group ID:</b> " + assgroupinstance[k].getGroupId());
                            printInstancePropertyArray(assinstanceproperty5);
                        }

                    }
                    catch(java.lang.Exception exception12)
                    {
                        outputStream.println("<p>Error in getInstances");
                        outputStream.println("<p>" + exception12.getMessage());
                    }
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listInstanceProperties"))
            {
                try
                {
                    String s11 = request.getValue("getInstancePropertiesGroupID");
                    String s21 = request.getValue("getInstancePropertiesOperation");
                    if(s11 != null && s11.length() > 0)
                    {
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty2[] = apigroup.getInstanceProperties(s11, (new Integer(s21)).intValue());
                        outputStream.println("List properties of new groupID " + s11);
                        printInstancePropertyArray(assinstanceproperty2);
                    } else
                    {
                        outputStream.println("<p>Group ID must be supplied!");
                    }
                }
                catch(java.lang.Exception exception8)
                {
                    outputStream.println("<p>Error in getInstanceProperties");
                    outputStream.println(exception8.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listInstanceProperty"))
            {
                try
                {
                    String s12 = request.getValue("getInstancePropertyGroupID");
                    String s22 = request.getValue("getInstancePropertyName");
                    String s25 = request.getValue("getInstancePropertyOperation");
                    if(s12 != null && s12.length() > 0)
                    {
                        COM.dragonflow.Api.SSInstanceProperty assinstanceproperty3[] = new COM.dragonflow.Api.SSInstanceProperty[1];
                        assinstanceproperty3[0] = apigroup.getInstanceProperty(s22, s12, (new Integer(s25)).intValue());
                        outputStream.println("List properties of new groupID " + s12);
                        printInstancePropertyArray(assinstanceproperty3);
                    } else
                    {
                        outputStream.println("<p>Group ID must be supplied!");
                    }
                }
                catch(java.lang.Exception exception9)
                {
                    outputStream.println("<p>Error in getInstanceProperties");
                    outputStream.println(exception9.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listAllGroups"))
            {
                outputStream.println("<p>List of all groups in this SiteView:<br>\n");
                try
                {
                    java.util.Collection collection = apigroup.getAllGroupInstances();
                    printGroupInstanceInfoArray(collection);
                }
                catch(java.lang.Exception exception10)
                {
                    outputStream.println("<p>Error in getAllInstancesInfo");
                    outputStream.println(exception10.getMessage() + "<br>\n");
                }
            } else
            if(request.hasValue("testName") && request.getValue("testName").equals("listChildGroups"))
            {
                outputStream.println("<p>List of all child groups in this SiteView:<br>\n");
                String s13 = request.getValue("parentGroupID");
                if(s13 != null && s13.length() > 0)
                {
                    try
                    {
                        java.util.Collection collection1 = apigroup.getChildGroupInstances(s13);
                        printGroupInstanceInfoArray(collection1);
                    }
                    catch(java.lang.Exception exception13)
                    {
                        outputStream.println("<p>Error in getChildInstancesInfo");
                        outputStream.println(exception13.getMessage() + "<br>\n");
                    }
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

    private void printInstancePropertyArray(COM.dragonflow.Api.SSInstanceProperty assinstanceproperty[])
    {
        if(assinstanceproperty.length > 0)
        {
            outputStream.println("<p><table>");
            for(int i = 0; i < assinstanceproperty.length; i++)
            {
                outputStream.println("<tr><td>Name: " + assinstanceproperty[i].getName() + "</td><td>Value: " + assinstanceproperty[i].getValue() + "</td><tr>\n");
            }

            outputStream.println("</table>");
        } else
        {
            outputStream.println("<p>No Instance Properties Found!");
        }
    }

    private void printGroupInstanceInfoArray(java.util.Collection collection)
    {
        if(collection.size() > 0)
        {
            outputStream.println("<p><table>");
            COM.dragonflow.SiteView.MonitorGroup monitorgroup;
            for(java.util.Iterator iterator = collection.iterator(); iterator.hasNext(); outputStream.println("<tr><td>Groupo Name: " + monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pName) + "</td><td> Parent Group ID: " + monitorgroup.getProperty("_parent") + "</td><td> Group ID: " + monitorgroup.getProperty(COM.dragonflow.SiteView.Monitor.pID) + "</td></tr>\n"))
            {
                monitorgroup = (COM.dragonflow.SiteView.MonitorGroup)iterator.next();
            }

            outputStream.println("</table>");
        } else
        {
            outputStream.println("<p>No Monitor Instances Found!");
        }
    }

    private void printPropertyDetails(COM.dragonflow.Api.SSPropertyDetails asspropertydetails[])
    {
        for(int i = 0; i < asspropertydetails.length; i++)
        {
            String s = "";
            if(asspropertydetails[i].isPrerequisite())
            {
                s = "<br>   (***PREREQUISITE***)";
            }
            outputStream.println("<ul><li>name: <b>" + asspropertydetails[i].getName() + "</b>;" + s + "</li>\n");
            outputStream.println("<li>type: " + asspropertydetails[i].getPropertyType() + ";</li>\n");
            outputStream.println("<li>description: " + asspropertydetails[i].getDescription() + ";</li>\n");
            outputStream.println("<li>label: " + asspropertydetails[i].getLabel() + ";</li>\n");
            outputStream.println("<li>isEditable: " + asspropertydetails[i].isEditable() + ";</li>\n");
            outputStream.println("<li>isMultiValued: " + asspropertydetails[i].isMultiValued() + ";</li>\n");
            outputStream.println("<li>default value: " + asspropertydetails[i].getDefaultValue() + ";</li>\n");
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
                    outputStream.println("<tr><td> " + as1[j] + "</td><td> " + as[j] + "</td></tr>\n");
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
