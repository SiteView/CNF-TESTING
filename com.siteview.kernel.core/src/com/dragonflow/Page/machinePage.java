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

import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Page:
// remoteBase, MachineTestPrinter

public class machinePage extends com.dragonflow.Page.remoteBase
{

    public machinePage()
    {
    }

    String getTestMachineID()
    {
        String s = getMachineID();
        String s1 = com.dragonflow.SiteView.Machine.REMOTE_PREFIX + s;
        String s2 = com.dragonflow.SiteView.Machine.getFullMachineID(s1, request);
        return s2;
    }

    String getTestMachineName()
    {
        String s = getTestMachineID();
        String s1 = com.dragonflow.SiteView.Machine.getMachineName(s);
        return s1;
    }

    String getTestDescription()
    {
        String s = "<p>For this test, SiteView will attempt to run each of the common types of remote  monitors once on the remote machine.\n If these tests are successful, monitors created for the remote machine  will function properly.\n";
        s = s + "<p>To add a given type of monitor <B>permanently</B> to " + com.dragonflow.SiteView.Platform.productName + ", go the page for the group\n" + "that the monitor will be added to, click the Add Monitor link, and click the desired monitor type link.\n" + "When adding the monitor, click <B>choose server</B> link, and select the " + getTestMachineName() + " item from\n" + "the popup.";
        s = s + "<P>";
        return s;
    }

    com.dragonflow.SiteView.Machine getTestMachine()
    {
        String s = getTestMachineID();
        com.dragonflow.SiteView.Machine machine = com.dragonflow.SiteView.Machine.getMachine(s);
        return machine;
    }

    String getTestCentra()
    {
        return "_centrascopeRemoteTestMatch";
    }

    String getHelpPage()
    {
        return "Remote.htm";
    }

    String getPage()
    {
        return "machine";
    }

    String getIDName()
    {
        return "machineID";
    }

    String getNextMachineName()
    {
        return "_nextRemoteID";
    }

    String getRemoteName()
    {
        return "_remoteMachine";
    }

    String getListTitle()
    {
        return "Remote Unix Servers";
    }

    String getListSubtitle()
    {
        return "<p>A list of UNIX servers with connectivity and remote access permissions for  monitoring from this SiteView instance. Remote connection options include: HTTP, telnet, rlogin, or SSH.<br>  <b>Note:</b> Connections using SSH version 2 require additional set up.";
    }

    String getPrefTitle()
    {
        return "Unix Remotes";
    }

    String getPrintFormSubmit()
    {
        return "Remote Server";
    }

    boolean displayFormTable(HashMap hashmap, String s)
    {
        boolean flag = true;
        outputStream.println(field("Server Address", "<input type=text name=host size=50 value=\"" + com.dragonflow.Utils.TextUtils.getValue(hashmap, "_host") + "\">", "The server address of the remote server (for example, demo." + com.dragonflow.SiteView.Platform.exampleDomain + ")." + "  For HTTP connections, enter the URL of the CGI\n" + " (for example, http://demo." + com.dragonflow.SiteView.Platform.exampleDomain + "/cgi-bin/remote.sh). " + " To use the same login credentials for multiple servers, enter multiple server addresses " + " separated by commas. "));
        if(flag)
        {
            outputStream.println(field("OS", "<select name=os size=1>" + com.dragonflow.Page.machinePage.getOptionsHTML(com.dragonflow.SiteView.Machine.getAllowedOSs(), com.dragonflow.Utils.TextUtils.getValue(hashmap, "_os")) + "</select>", "The operating system (OS) running on the remote server"));
            String s1 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_method");
            if(s1.length() == 0)
            {
                if(com.dragonflow.SiteView.Platform.isSGI())
                {
                    s1 = "rlogin";
                } else
                {
                    s1 = "telnet";
                }
            }
            outputStream.println(field("Connection Method", "<select name=method size=1>" + com.dragonflow.Page.machinePage.getOptionsHTML(com.dragonflow.SiteView.Machine.getAllowedMethods(), s1) + "</select>", "Select the method to use to connect to the remote server. For <b>telnet</b> or <b>rlogin</b>, enter the applicable shell prompt character(s)  in the <b>Prompt</b> field below."));
            formLogin(hashmap);
            outputStream.println(field("Prompt", "<input type=text name=prompt size=30 value=\"" + com.dragonflow.Page.machinePage.getValue(hashmap, "_prompt") + "\">", "For <b>telnet</b> and <b>rlogin</b> connections, enter the shell prompt output when the system is ready to accept a command. For example: $  (the default is #)"));
            outputStream.println(field("Login Prompt", "<input type=text name=loginPrompt size=30 value=\"" + com.dragonflow.Page.machinePage.getValue(hashmap, "_loginPrompt") + "\">", "For <b>telnet</b> and <b>rlogin</b> connections, the prompt output when the system is waiting for the login to be entered - the default is \"ogin:\""));
            outputStream.println(field("Password Prompt", "<input type=text name=passwordPrompt size=30 value=\"" + com.dragonflow.Page.machinePage.getValue(hashmap, "_passwordPrompt") + "\">", "For <b>telnet</b> connections, the prompt output when the system is waiting for the password to be entered - the default is \"assword:\""));
            outputStream.println(field("Secondary Prompt", "<input type=text name=secondaryPrompt size=30 value=\"" + com.dragonflow.Page.machinePage.getValue(hashmap, "_secondaryPrompt") + "\">", "Secondary prompt. Separate by ',' for multiple prompts"));
            outputStream.println(field("Secondary Response", "<input type=text name=secondaryResponse size=30 value=\"" + com.dragonflow.Page.machinePage.getValue(hashmap, "_secondaryResponse") + "\">", "response to the above prompt. Separate by ',' for multiple responses."));
            outputStream.println(field("Initialize Shell Environment", "<input type=text name=initShellEnvironment size=30 value=\"" + com.dragonflow.Page.machinePage.getValue(hashmap, "_initShellEnvironment") + "\">", "Shell commands to be executed at begining of session. Seperate by ';' for multiple commands."));
            if(com.dragonflow.Utils.I18N.isI18N)
            {
                String s3 = com.dragonflow.Page.machinePage.getValue(hashmap, "_encoding");
                if(s3.length() == 0)
                {
                    s3 = com.dragonflow.Utils.I18N.getDefaultEncoding();
                }
                byte abyte0[] = new byte[10];
                abyte0[0] = -112;
                String s4 = "null";
                try
                {
                    s4 = new String(abyte0, 0, 1, "Shift_JIS");
                }
                catch(java.lang.Exception exception)
                {
                    java.lang.System.out.println(exception.getMessage());
                    exception.printStackTrace();
                }
                com.dragonflow.Utils.I18N.dmp("E:", s4);
                outputStream.println(field("Remote Machine encoding", "<input type=text name=encoding size=30 value=\"" + s3 + "\">", "Enter code page (ie Cp1252, Shift_JIS, EUC-JP, etc.)"));
            }
        }
        String s2 = com.dragonflow.Page.machinePage.getValue(hashmap, "_trace").length() <= 0 ? "" : "CHECKED";
        outputStream.println(field("Trace", "<input type=checkbox name=trace " + s2 + ">", "Check this box to trace messages to and from the remote server (messages are  written in the RunMonitor.log file)"));
        outputStream.println(field(s + " and Test", "<input type=\"radio\" name=\"addtest\" value=\"test\" CHECKED>", "Check to " + s + " the profile and test the connection"));
        outputStream.println(field(s + " Only", "<input type=\"radio\" name=\"addtest\" value=\"add\">", "Check to " + s + " the profile only"));
        outputStream.println(getAdnvacedSSHOptions(hashmap));
        return flag;
    }

    void saveAddProperties(String s, HashMap hashmap, String s1)
    {
        super.saveAddProperties(s, hashmap, s1);
        hashmap.put("_id", s1);
        hashmap.put("_login", request.getValue("login"));
        hashmap.put("_os", request.getValue("os"));
        hashmap.put("_method", request.getValue("method"));
        hashmap.put("_status", "unknown");
        if(request.getValue("prompt").length() == 0)
        {
            hashmap.remove("_prompt");
        } else
        {
            hashmap.put("_prompt", request.getValue("prompt"));
        }
        if(request.getValue("loginPrompt").length() == 0)
        {
            hashmap.remove("_loginPrompt");
        } else
        {
            hashmap.put("_loginPrompt", request.getValue("loginPrompt"));
        }
        if(request.getValue("passwordPrompt").length() == 0)
        {
            hashmap.remove("_passwordPrompt");
        } else
        {
            hashmap.put("_passwordPrompt", request.getValue("passwordPrompt"));
        }
        if(request.getValue("secondaryPrompt").length() == 0)
        {
            hashmap.remove("_secondaryPrompt");
        } else
        {
            hashmap.put("_secondaryPrompt", request.getValue("secondaryPrompt"));
        }
        if(request.getValue("secondaryResponse").length() == 0)
        {
            hashmap.remove("_secondaryResponse");
        } else
        {
            hashmap.put("_secondaryResponse", request.getValue("secondaryResponse"));
        }
        if(request.getValue("initShellEnvironment").length() == 0)
        {
            hashmap.remove("_initShellEnvironment");
        } else
        {
            hashmap.put("_initShellEnvironment", request.getValue("initShellEnvironment"));
        }
        if(request.getValue("encoding").length() == 0)
        {
            hashmap.remove("_encoding");
        } else
        {
            hashmap.put("_encoding", request.getValue("encoding"));
        }
        hashmap.put("_trace", request.getValue("trace"));
    }

    Array getListTableHeaders(String s)
    {
        Array array = new Array();
        array.add(new String("Name"));
        array.add(new String("Server"));
        array.add(new String("Status"));
        array.add(new String("OS"));
        array.add(new String("Method"));
        if(request.actionAllowed("_preference") || s.length() > 0)
        {
            array.add(new String("Edit"));
            array.add(new String("Test"));
            array.add(new String("Detailed Test"));
            array.add(new String("Del"));
        }
        return array;
    }

    Array getListTableRow(HashMap hashmap, String s)
    {
        Array array = new Array();
        Array array1 = com.dragonflow.SiteView.Machine.getAllowedMethods();
        Array array2 = com.dragonflow.SiteView.Machine.getAllowedOSs();
        String s1 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_id");
        String s2 = com.dragonflow.Page.machinePage.getValue(hashmap, "_name");
        if(s2.length() == 0)
        {
            s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_host");
        }
        array.add(new String("<b>" + com.dragonflow.Utils.TextUtils.getValue(hashmap, "_name") + "</b>"));
        array.add(new String(com.dragonflow.Utils.TextUtils.getValue(hashmap, "_host")));
        array.add(new String("<b><i>" + com.dragonflow.Utils.TextUtils.getValue(hashmap, "_status") + "</i></b>"));
        array.add(new String(com.dragonflow.Utils.TextUtils.keyToDisplayString(com.dragonflow.Page.machinePage.getValue(hashmap, "_os"), array2)));
        array.add(new String(com.dragonflow.Utils.TextUtils.keyToDisplayString(com.dragonflow.Page.machinePage.getValue(hashmap, "_method"), array1)));
        if(request.actionAllowed("_preference") || s.length() > 0)
        {
            array.add(new String("<A href=" + getPageLink("machine", "Edit") + "&machineID=" + s1 + "&storeID=" + s + ">Edit</a>"));
            array.add(new String("<A href=" + getPageLink("machine", "Test") + "&machineID=" + s1 + "&storeID=" + s + ">Test</a>"));
            array.add(new String("<A href=" + getPageLink("machine", "Test") + "&machineID=" + s1 + "&storeID=" + s + "&detail=true>Detailed Test</a>"));
            array.add(new String("<A href=" + getPageLink("machine", "Delete") + "&machineID=" + s1 + "&storeID=" + s + ">X</a>"));
        }
        return array;
    }

    void doTest(com.dragonflow.SiteView.Machine machine)
    {
        String s = "";
        boolean flag = request.getValue("detail").length() > 0;
        com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if(!siteviewgroup.internalServerActive())
        {
            HashMap hashmap = getMasterConfig();
            com.dragonflow.SiteView.Machine.registerMachines((Enumeration) hashmap.values("_remoteMachine"));
        }
        boolean flag1 = false;
        if(machine != null)
        {
            com.dragonflow.Utils.RemoteCommandLine remotecommandline = com.dragonflow.SiteView.Machine.getRemoteCommandLine(machine);
            remotecommandline.progressStream = outputStream;
            String s1 = "echo testing connection";
            outputStream.println("<p>Testing connection to : <b>" + machine.getProperty(com.dragonflow.SiteView.Machine.pName) + "</b>");
            outputStream.println("<HR><PRE>");
            outputStream.flush();
            Array array1 = remotecommandline.test(s1, machine, flag);
            outputStream.println("</PRE><HR>");
            if(remotecommandline.exitValue != 0)
            {
                s = "remote command error " + com.dragonflow.SiteView.Monitor.lookupStatus(remotecommandline.exitValue) + " (" + remotecommandline.exitValue + ")";
                outputStream.println("<H3>" + s + "</H3>");
            }
            for(int i = 0; i < array1.size(); i++)
            {
                String s2 = (String)array1.get(i);
                if(s2.indexOf("testing connection") >= 0)
                {
                    flag1 = true;
                    s = "connection successful";
                }
            }

        } else
        {
            s = "unable to get machine info";
            outputStream.println("<HR>" + s + "for :" + getMachineID() + "<HR>");
        }
        outputStream.flush();
        if(flag1)
        {
            outputStream.println("<P><B>Connection Successful</B>, attempting monitor tests...</P>");
            boolean flag2 = false;
            try
            {
                outputStream.println("<PRE>");
                java.io.PipedWriter pipedwriter = new PipedWriter();
                java.io.PipedReader pipedreader = new PipedReader(pipedwriter);
                com.dragonflow.Page.MachineTestPrinter machinetestprinter = new MachineTestPrinter(outputStream, pipedreader);
                machinetestprinter.start();
                com.dragonflow.SiteView.OSAdapter.runTests(machine, null, pipedwriter, flag);
                pipedwriter.close();
                machinetestprinter.join();
                s = s.concat(machinetestprinter.getMessage());
            }
            catch(java.lang.Exception exception)
            {
                s = s.concat(", tests failed");
                outputStream.println("Could not perform test: ");
            }
            outputStream.println("</PRE>");
        }
        Array array = getFrames();
        HashMap hashmap1 = findMachine(array, machine.getProperty(com.dragonflow.SiteView.Machine.pID));
        hashmap1.put("_status", s);
        try
        {
            saveMachines(array, getRemoteName());
        }
        catch(java.lang.Exception exception1)
        {
            java.lang.System.out.println("There was a problem updating the server status." + exception1.toString());
        }
    }

    public static void main(String args[])
    {
        (new machinePage()).handleRequest();
    }

    public String getRemoteUniqueId()
    {
        return request.getValue("machineID");
    }
}
