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

import com.dragonflow.Api.APIPreference;
import com.dragonflow.Api.SSInstanceProperty;

// Referenced classes of package com.dragonflow.Page:
// apiMasterTestPage

public class apiPreferenceTestPage extends com.dragonflow.Page.apiMasterTestPage
{

    private String selectedPreferenceType;
    private String selectedPreferenceOperationType;

    public apiPreferenceTestPage()
    {
        selectedPreferenceType = "";
        selectedPreferenceOperationType = "";
    }

    public void printBody()
        throws java.lang.Exception
    {
        com.dragonflow.Api.APIPreference apipreference = new APIPreference();
        outputStream.println("<H1>API Preference Test Page</H1>\n");
        if(request.getValue("testName").equals("createPreferenceProperties"))
        {
            try
            {
                selectedPreferenceType = request.getValue("selectedPreferenceTypeCreate");
                String s = request.getValue("createPreferenceID");
                com.dragonflow.Api.SSPropertyDetails asspropertydetails[] = apipreference.getClassPropertiesDetails(selectedPreferenceType, com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_ADD_ALL);
                String s12 = "";
                for(int k = 0; k < asspropertydetails.length; k++)
                {
                    String s15 = asspropertydetails[k].getDefaultValue();
                    s12 = s12 + asspropertydetails[k].getName() + "=";
                    if(s15 != null && s15.length() > 0)
                    {
                        s12 = s12 + s15;
                    }
                    s12 = s12 + "\n";
                }

                outputStream.println("<form method=POST>");
                outputStream.println("<input type=hidden name=\"page\" value=\"apiPreferenceTest\">");
                outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                outputStream.println("<input type=hidden name=\"testName\" value=\"createPreference\">");
                outputStream.println("<input type=hidden name=\"selectedPreferenceTypeCreate\" value=\"" + selectedPreferenceType + "\"createMonitor\">");
                outputStream.println("<input type=hidden name=\"createPreferenceID\" value=\"" + s + "\"createMonitorGroupID\">");
                outputStream.println("<table><tr><td>Preference Properties: </td></tr>");
                outputStream.println("<tr><td><textarea type=text name=\"createPreferenceProperties\" rows=30 cols=80>" + s12 + "</textarea></td></tr></table>");
                outputStream.println("<input type=submit name=\"CreatePreference\" value=\"Create Preference\"></form>");
            }
            catch(java.lang.Exception exception)
            {
                outputStream.println("<p>Error in create");
                outputStream.println("<p>" + exception.getMessage());
            }
        } else
        if(request.hasValue("testName") && request.getValue("testName").equals("createPreference"))
        {
            try
            {
                selectedPreferenceType = request.getValue("selectedPreferenceTypeCreate");
                selectedPreferenceOperationType = request.getValue("selectedPreferenceOperationType");
                outputStream.println("<form method=POST>");
                outputStream.println("<input type=hidden name=\"page\" value=\"apiPreferenceTest\">");
                outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                String s1 = request.getValue("createPreferenceProperties");
                String as[] = com.dragonflow.Utils.TextUtils.split(s1);
                int i = 0;
                for(int l = 0; l < as.length; l++)
                {
                    int j1 = as[l].indexOf("=");
                    if(j1 != -1 && j1 != 0 && as[l].substring(j1 + 1) != null && as[l].substring(j1 + 1).length() > 0)
                    {
                        i++;
                    }
                }

                com.dragonflow.Api.SSInstanceProperty assinstanceproperty1[] = new com.dragonflow.Api.SSInstanceProperty[i];
                int k1 = 0;
                for(int j2 = 0; j2 < as.length; j2++)
                {
                    int i3 = as[j2].indexOf("=");
                    if(i3 != -1 && i3 != 0 && as[j2].substring(i3 + 1) != null && as[j2].substring(i3 + 1).length() > 0)
                    {
                        assinstanceproperty1[k1] = new SSInstanceProperty(as[j2].substring(0, i3), as[j2].substring(i3 + 1));
                        k1++;
                    }
                }

                com.dragonflow.Api.SSInstanceProperty ssinstanceproperty = apipreference.create(selectedPreferenceType, assinstanceproperty1);
                outputStream.println("<p>The created preference attribute identifier is: \"" + ssinstanceproperty.getName() + "\" and it's value is: \"" + ssinstanceproperty.getValue() + "\"</form>");
            }
            catch(java.lang.Exception exception1)
            {
                outputStream.println("<p>Error in create");
                outputStream.println("<p>" + exception1.getMessage());
            }
        } else
        if(request.getValue("testName").equals("updatePreferenceProperties"))
        {
            try
            {
                selectedPreferenceType = request.getValue("selectedPreferenceTypeUpdate");
                String s2 = request.getValue("updateAttributeIdentifier");
                String s8 = request.getValue("updateAttributeValue");
                com.dragonflow.Api.SSInstanceProperty assinstanceproperty[] = apipreference.getInstanceProperties(selectedPreferenceType, "", s2, s8, com.dragonflow.Api.APISiteView.FILTER_CONFIGURATION_EDIT_ALL);
                String s14 = "";
                for(int l1 = 0; l1 < assinstanceproperty.length; l1++)
                {
                    String s16 = assinstanceproperty[l1].getName() + "=" + assinstanceproperty[l1].getValue() + "\n";
                    s14 = s14 + s16;
                }

                outputStream.println("<form method=POST>");
                outputStream.println("<input type=hidden name=\"page\" value=\"apiPreferenceTest\">");
                outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                outputStream.println("<input type=hidden name=\"testName\" value=\"updatePreference\">");
                outputStream.println("<input type=hidden name=\"selectedPreferenceTypeUpdate\" value=\"" + selectedPreferenceType + "\"createMonitor\">");
                outputStream.println("<input type=hidden name=\"updateAttributeIdentifier\" value=\"" + s2 + "\">");
                outputStream.println("<input type=hidden name=\"updateAttributeValue\" value=\"" + s8 + "\">");
                outputStream.println("<table><tr><td>Preference Properties: </td></tr>");
                outputStream.println("<tr><td><textarea type=text name=\"updatePreferenceProperties\" rows=30 cols=80>" + s14 + "</textarea></td></tr></table>");
                outputStream.println("<input type=submit name=\"UpdatePreference\" value=\"Update Preference\"></form>");
            }
            catch(java.lang.Exception exception2)
            {
                outputStream.println("<p>Error in update");
                outputStream.println("<p>" + exception2.getMessage());
            }
        } else
        if(request.hasValue("testName") && request.getValue("testName").equals("updatePreference"))
        {
            try
            {
                selectedPreferenceType = request.getValue("selectedPreferenceTypeUpdate");
                String s3 = request.getValue("updateAttributeIdentifier");
                String s9 = request.getValue("updateAttributeValue");
                outputStream.println("<form method=POST>");
                outputStream.println("<input type=hidden name=\"page\" value=\"apiPreferenceTest\">");
                outputStream.println("<input type=hidden name=\"account\" value=\"administrator\">");
                String s13 = request.getValue("updatePreferenceProperties");
                String as1[] = com.dragonflow.Utils.TextUtils.split(s13);
                int i2 = 0;
                for(int k2 = 0; k2 < as1.length; k2++)
                {
                    int j3 = as1[k2].indexOf("=");
                    if(j3 != -1 && j3 != 0 && as1[k2].substring(j3 + 1) != null && as1[k2].substring(j3 + 1).length() > 0)
                    {
                        i2++;
                    }
                }

                com.dragonflow.Api.SSInstanceProperty assinstanceproperty3[] = new com.dragonflow.Api.SSInstanceProperty[i2];
                int k3 = 0;
                for(int l3 = 0; l3 < as1.length; l3++)
                {
                    int i4 = as1[l3].indexOf("=");
                    if(i4 != -1 && i4 != 0 && as1[l3].substring(i4 + 1) != null && as1[l3].substring(i4 + 1).length() > 0)
                    {
                        assinstanceproperty3[k3] = new SSInstanceProperty(as1[l3].substring(0, i4), as1[l3].substring(i4 + 1));
                        k3++;
                    }
                }

                com.dragonflow.Api.SSInstanceProperty ssinstanceproperty1 = apipreference.update(selectedPreferenceType, s3, s9, assinstanceproperty3);
                outputStream.println("<p>The updated preference attribute identifier is: \"" + ssinstanceproperty1.getName() + "\" and it's value is: \"" + ssinstanceproperty1.getValue() + "\"</form>");
            }
            catch(java.lang.Exception exception3)
            {
                outputStream.println("<p>Error in update");
                outputStream.println("<p>" + exception3.getMessage());
            }
        } else
        if(request.hasValue("testName") && request.getValue("testName").equals("listPreferenceInstances"))
        {
            try
            {
                outputStream.println("<p>List of all child groups in this SiteView:<br>\n");
                String s4 = request.getValue("selectedPreferenceType");
                String s10 = request.getValue("settingName");
                com.dragonflow.Api.SSPreferenceInstance asspreferenceinstance[] = apipreference.getInstances(s4, s10, "", "", com.dragonflow.Api.APISiteView.FILTER_ALL);
                outputStream.println("<table>");
                for(int i1 = 0; i1 < asspreferenceinstance.length; i1++)
                {
                    com.dragonflow.Api.SSInstanceProperty assinstanceproperty2[] = asspreferenceinstance[i1].getInstanceProperties();
                    for(int l2 = 0; l2 < assinstanceproperty2.length; l2++)
                    {
                        outputStream.println("<tr><td>Setting Name: " + assinstanceproperty2[l2].getName() + "</td><td>Setting Value: " + assinstanceproperty2[l2].getValue() + "</td></tr>\n");
                    }

                }

                outputStream.println("</table>");
            }
            catch(java.lang.Exception exception4)
            {
                outputStream.println("Problem in APIPreference - getInstanceProperties");
                outputStream.println(exception4.getMessage() + "<br>\n");
            }
            outputStream.println("\n<p><a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else
        if(request.hasValue("testName") && request.getValue("testName").equals("listPreferenceProperties"))
        {
            try
            {
                String s5 = request.getValue("selectedPreferenceTypeProps");
                String s11 = request.getValue("selectedPreferenceOperationType");
                outputStream.println("<p>List of all the properties of a " + s5 + "</p>\n");
                com.dragonflow.Api.SSPropertyDetails asspropertydetails1[] = apipreference.getClassPropertiesDetails(s5, (new Integer(s11)).intValue());
                printPropertyDetails(asspropertydetails1);
            }
            catch(java.lang.Exception exception5)
            {
                outputStream.println("<p>Error in getClassProperties");
                outputStream.println("<p>" + exception5.getMessage());
            }
            outputStream.println("\n<p><a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        } else
        if(request.hasValue("testName") && request.getValue("testName").equals("testPreference"))
        {
            try
            {
                selectedPreferenceType = request.getValue("selectedPreferenceTypeTest");
                String s6 = request.getValue("testPreferenceID");
                java.util.Vector vector = apipreference.test(selectedPreferenceType, "_id", s6, "", false);
                if(vector.size() > 0)
                {
                    outputStream.println("<p>Test Output:<p>");
                    for(int j = 0; j < vector.size(); j++)
                    {
                        outputStream.println("<br>" + (String)vector.elementAt(j));
                    }

                } else
                {
                    outputStream.println("<p>No Test Output Available");
                }
                outputStream.println("\n<p><a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
            }
            catch(java.lang.Exception exception6)
            {
                outputStream.println("<p>Error in Test");
                outputStream.println("<p>" + exception6.getMessage());
            }
        } else
        if(request.hasValue("testName") && request.getValue("testName").equals("deletePreference"))
        {
            try
            {
                selectedPreferenceType = request.getValue("selectedPreferenceTypeDelete");
                String s7 = request.getValue("deletePreferenceID");
                apipreference.delete(selectedPreferenceType, "_id", s7);
                outputStream.println("<p>" + selectedPreferenceType + " attributeName: _id attribute Value: " + s7 + " was deleted successfully!");
                outputStream.println("\n<p><a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
            }
            catch(java.lang.Exception exception7)
            {
                outputStream.println("<p>Error in Delete");
                outputStream.println("<p>" + exception7.getMessage());
            }
        } else
        {
            outputStream.println("NO TEST SELECTED! <a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        }
        outputStream.println("\n<p><a href=http://" + server + ":" + port + "/SiteView/cgi/go.exe/SiteView?page=apiMasterTest&account=administrator>Back</a> to Master Test Page");
        outputStream.flush();
    }

    private void printPropertyDetails(com.dragonflow.Api.SSPropertyDetails asspropertydetails[])
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
            if(as != null)
            {
                outputStream.println("<li>selection choice data type: </li>\n");
                outputStream.println("<li>selection choice display data: </li>\n");
                outputStream.println("<ul>");
                for(int j = 0; j < as.length; j++)
                {
                    outputStream.println(" <li>" + as[j] + "</li>\n");
                }

                outputStream.println("</ul>\n");
            }
            outputStream.println("</ul>\n");
        }

        outputStream.println("<p>");
    }
}
