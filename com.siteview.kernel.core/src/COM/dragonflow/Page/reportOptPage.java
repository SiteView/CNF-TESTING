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

import COM.dragonflow.HTTP.HTTPRequestException;

// Referenced classes of package COM.dragonflow.Page:
// prefsPage

public class reportOptPage extends COM.dragonflow.Page.prefsPage
{

    public reportOptPage()
    {
    }

    void printPreferencesSaved()
    {
        printPreferencesSaved("/SiteView/cgi/go.exe/SiteView?page=reportOpt", "", 0);
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_preference"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("operation");
        String s1 = request.getAccount();
        s1 = s1.trim();
        if(request.isPost())
        {
            if(s.equals("save"))
            {
                savePreferences();
                printPreferencesSaved();
            }
        } else
        if(s1.equalsIgnoreCase("administrator"))
        {
            printForm();
        } else
        {
            printErrorPage("You must use the administrator account to use this page.<br>Your account = " + s1);
        }
    }

    private HashMap updateSetting(HashMap hashmap, String s)
    {
        String s1 = null;
        s1 = request.getValue(s + "CB");
        outputStream.print("<br>Debug->" + s1);
        if(s1.equalsIgnoreCase("on"))
        {
            String s2 = request.getValue(s);
            outputStream.print("<br>Debug->" + s2);
            if(s2.equalsIgnoreCase("custom"))
            {
                s2 = request.getValue(s + "Custom");
                outputStream.print("<br>Debug->" + s2);
                hashmap.put(s, s2.toUpperCase());
            } else
            {
                outputStream.print("<br>Debug->" + s2);
                hashmap.put(s, s2);
            }
        }
        return hashmap;
    }

    public void savePreferences()
    {
        HashMap hashmap = getMasterConfig();
        hashmap = updateSetting(hashmap, "_reportGraphBackgroundColor");
        hashmap = updateSetting(hashmap, "_reportGraphDisabledColor");
        hashmap = updateSetting(hashmap, "_reportGraphErrorColor");
        hashmap = updateSetting(hashmap, "_reportGraphGoodColor");
        hashmap = updateSetting(hashmap, "_reportGraphMaxColor");
        hashmap = updateSetting(hashmap, "_reportGraphNoDataColor");
        hashmap = updateSetting(hashmap, "_reportGraphWarningColor");
        hashmap = updateSetting(hashmap, "_reportTableDataHTML=BGCOLOR");
        hashmap = updateSetting(hashmap, "_reportTableDisabledColor");
        hashmap = updateSetting(hashmap, "_reportTableErrorColor");
        hashmap = updateSetting(hashmap, "_reportTableGoodColor");
        hashmap = updateSetting(hashmap, "_reportTableHeaderHTML");
        hashmap = updateSetting(hashmap, "_reportTableHTML=BORDER");
        hashmap = updateSetting(hashmap, "_reportTableNoDataColor");
        hashmap = updateSetting(hashmap, "_reportTableWarningColor");
        try
        {
            saveMasterConfig(hashmap);
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public static void main(String args[])
    {
    }

    void printForm()
    {
        printBodyHeader("Report Options");
        printButtonBar("LogPref.htm", "");
        outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=post>\n<input type=hidden name=account value=" + request.getAccount() + ">\n" + "<input type=hidden name=page value=reportOpt>\n" + "<input type=hidden name=operation value=save>\n");
        outputStream.print("<HR><CENTER><H3>Report Options</H3></CENTER><BR>\n");
        String s = "Report Background Color";
        printOption(s, "_reportGraphBackgroundColor", "white");
        s = "Report Graph Disabled Color";
        printOption(s, "_reportGraphDisabledColor", "blue");
        s = "Report Graph Error Color";
        printOption(s, "_reportGraphErrorColor", "red");
        s = "Report Graph Good Color";
        printOption(s, "_reportGraphGoodColor", "ltGreen");
        s = "Report Graph Max Color";
        printOption(s, "_reportGraphMaxColor", "yellow");
        s = "Report Graph No Data Color";
        printOption(s, "_reportGraphNoDataColor", "grey");
        s = "Report Graph Warning Color";
        printOption(s, "_reportGraphWarningColor", "red");
        s = "Report Table Data HTML Backround Color";
        printOption(s, "_reportTableDataHTML=BGCOLOR", "black");
        s = "Report Table Disabled Color";
        printOption(s, "_reportTableDisabledColor", "grey");
        s = "Report Table Error Color";
        printOption(s, "_reportTableErrorColor", "red");
        s = "Report Table Good Color";
        printOption(s, "_reportTableGoodColor", "green");
        s = "Report Table HTML Header Background Color";
        printOption(s, "_reportTableHeaderHTML=BGCOLOR", "green");
        s = "Report Table No Data Color";
        printOption(s, "_reportTableNoDataColor", "grey");
        s = "Report Table Warning Color";
        printOption(s, "_reportTableWarningColor", "yellow");
        outputStream.print("<br><input type=\"submit\" value=\"Save Changes\" name=\"B1\"\n></FORM>\n<CENTER><B>Version " + COM.dragonflow.SiteView.Platform.getVersion() + "</B><p>\n");
        printFooter(outputStream);
    }

    private void printOption(String s, String s1)
    {
        outputStream.print("<input type=\"checkbox\" name=\"" + s1 + "CB\" value=\"ON\">" + s + "<br>\n" + "&nbsp;&nbsp;<select size=\"1\" name=\"" + s1 + ">\n" + selctOpts("green") + "&nbsp; </select><input type=\"text\" name=\"" + s1 + "Custom\" size=\"20\"><br><br><br>\n");
    }

    private void printOption(String s, String s1, String s2)
    {
        outputStream.print("<input type=\"checkbox\" name=\"" + s1 + "CB\" value=\"ON\">" + s + "<br>\n" + "&nbsp;&nbsp;<select size=\"1\" name=\"" + s1 + "\">\n" + selctOpts(s2) + "&nbsp; </select><input type=\"text\" name=\"" + s1 + "Custom\" size=\"20\"><br><br><br>\n");
    }

    private String selected(String s, String s1)
    {
        String s2 = "";
        if(s.equalsIgnoreCase(s1))
        {
            s2 = "selected";
        }
        return s2;
    }

    private String selctOpts(String s)
    {
        String s1 = "<option value=\"#FF0000\" " + selected(s, "RED") + ">Red</option>\n" + "<option value=\"#FFFFFF\" " + selected(s, "White") + ">White</option>\n" + "<option value=\"#0000FF\" " + selected(s, "Blue") + ">Blue</option>\n" + "<option value=\"#99FFCC\" " + selected(s, "ltGreen") + " >Light Green</option>\n" + "<option value=\"#008000\" " + selected(s, "green") + ">Green</option>\n" + "<option value=\"#FFFF00\" " + selected(s, "Yellow") + ">Yellow</option>\n" + "<option value=\"#FF6600\" " + selected(s, "Orange") + ">Orange</option>\n" + "<option value=\"#FF00FF\" " + selected(s, "Fuchsia") + ">Fuchsia</option>\n" + "<option value=\"#800000\" " + selected(s, "Maroon") + ">Maroon</option>\n" + "<option value=\"#3399FF\" " + selected(s, "Light Blue") + ">Light Blue</option>\n" + "<option value=\"#9900CC\" " + selected(s, "Purple") + ">Purple</option>\n" + "<option value=\"#008080\" " + selected(s, "Teal") + ">Teal</option>\n" + "<option value=\"#C0C0C0\" " + selected(s, "Grey") + ">Grey</option>\n" + "<option value=\"#000000\" " + selected(s, "Black") + ">Black</option>\n" + "<option value=\"Custom\" >Custom</option>\n";
        return s1;
    }

    void printErrorPage(String s)
    {
        printBodyHeader("Log Preferences");
        outputStream.println("<p><CENTER><H2></H2></CENTER>\n<HR>There were errors in the entered information.  Use your browser's back button to return\nthe Log Preferences form\n");
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
}
