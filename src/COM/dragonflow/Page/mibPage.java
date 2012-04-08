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

import Snmp.MibModule;
import Snmp.SnmpAPI;

// Referenced classes of package COM.dragonflow.Page:
// monitorPage

public class mibPage extends COM.dragonflow.Page.monitorPage
{

    static Snmp.SnmpAPI cachedAPI = null;

    public mibPage()
    {
    }

    static Snmp.SnmpAPI getAPI()
    {
        if(cachedAPI == null)
        {
            cachedAPI = new SnmpAPI();
            cachedAPI.start();
            long l = 10000L;
            do
            {
                if(cachedAPI.client != null)
                {
                    break;
                }
                try
                {
                    java.lang.Thread.sleep(10L);
                    if(COM.dragonflow.SiteView.Platform.timeMillis() - l > l)
                    {
                        break;
                    }
                }
                catch(java.lang.Exception exception) { }
            } while(true);
        }
        return cachedAPI;
    }

    public void printBody()
        throws java.lang.Exception
    {
        boolean flag = false;
        printBodyHeader("MIB Browser");
        outputStream.println("<H2>MIB Browser Utility</H2>\n");
        outputStream.println("<TABLE>");
        outputStream.println("<FORM ACTION=/SiteView/cgi/go.exe/SiteView method=POST>\n");
        outputStream.print("<input type=hidden name=page value=\"" + request.getValue("page") + "\">\n");
        if(request.getValue("submit").equals("List OIDs"))
        {
            String s = COM.dragonflow.SiteView.Platform.getRoot() + "/templates.mib/" + request.getValue("file");
            outputStream.print("<tr><td colspan=3>Mibfile: " + request.getValue("file") + " ScriptPath: " + s + "</td></tr>");
            Snmp.SnmpAPI snmpapi = COM.dragonflow.Page.mibPage.getAPI();
            Snmp.MibModule mibmodule = null;
            boolean flag1;
            try
            {
                mibmodule = new MibModule(s, snmpapi, true);
                flag1 = true;
            }
            catch(java.lang.Exception exception)
            {
                java.lang.System.out.println("MibModule Creation Exception, " + exception.getMessage());
                outputStream.print("<TR><TD>MibModule Creation Exception  " + exception.getMessage() + "</TD></TR>");
                flag1 = false;
            }
            if(flag1)
            {
                outputStream.print("<TR><TD colspan=3>Copy the desired Object ID and paste it into the  OID field on the SMMP Monitor set up page. <br>Also note the Index value, for table entries, where available, and enter it in the Index field  on the SMMP Monitor set up page. <br>The Index value  for non-table items is 0 (zero) which is the default.</TD></TR>");
                outputStream.print("<TR><TD><input type=submit name=\"submit\" value=\"List MIBs\"></TD></TR>");
                outputStream.print("<TR><TD><b>String</b></TD><TD><b>OID</b></TD><TD><b>Index Values</b></TD></TR>");
                Snmp.MibModule _tmp = mibmodule;
                String s1;
                String s2;
                String s3;
                for(Enumeration enumeration = Snmp.MibModule.nodeList.elements(); enumeration.hasMoreElements(); outputStream.print("<TR><TD>" + s1 + "</TD><TD>" + s2 + "</TD><TD>" + s3 + "</TD></TR>"))
                {
                    s1 = enumeration.nextElement().toString();
                    s2 = mibmodule.translateToNumbers(s1);
                    s3 = "";
                    Snmp.MibNode mibnode = mibmodule.getNode(s1);
                    if(mibnode == null)
                    {
                        continue;
                    }
                    if(mibnode.indexNames != null)
                    {
                        int j = 0;
                        for(Enumeration enumeration1 = mibnode.indexNames.elements(); enumeration1.hasMoreElements();)
                        {
                            s3 = s3 + ++j + "=" + enumeration1.nextElement() + ",";
                        }

                    } else
                    {
                        s3 = "0";
                    }
                }

            }
            outputStream.print("<TR><TD><input type=submit name=\"submit\" value=\"List MIBs\"></TD></TR>");
            snmpapi.stop();
        } else
        {
            outputStream.print("<TR><TD><select name=file>");
            java.io.File file = new File(COM.dragonflow.SiteView.Platform.getRoot() + "/templates.mib");
            String as[] = file.list();
            if(as.length > 0)
            {
                for(int i = 0; i < as.length; i++)
                {
                    java.io.File file1 = new File(file, as[i]);
                    if(!file1.isDirectory())
                    {
                        outputStream.print("<option value=\"" + as[i] + "\">" + as[i] + "</option>");
                    }
                }

            } else
            {
                outputStream.print("<option value=\"\">&#060;n/a&#062;</option>");
            }
            outputStream.print("</select></TD></TR>\n");
            outputStream.print("<TR><TD> </TD></TR>");
            outputStream.print("<TR><TD>Pick a MIB file to browse and click the List OIDs button displaythe list of OIDs. <p>(A copy of the MIB must be in the <tt>~SiteView/templates.mib</tt> subdirectory)</p></TD></TR>");
            outputStream.print("<TR><TD><input type=submit name=\"submit\" value=\"List OIDs\"></TD></TR>");
        }
        outputStream.print("</FORM>\n</TABLE>\n");
        printFooter(outputStream);
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.mibPage mibpage = new mibPage();
        if(args.length > 0)
        {
            mibpage.args = args;
        }
        mibpage.handleRequest();
    }

}
