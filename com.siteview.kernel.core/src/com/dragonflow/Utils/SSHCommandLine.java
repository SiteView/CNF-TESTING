 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package com.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */
import com.dragonflow.SiteView.Machine;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Utils:
// RemoteCommandLine, TextUtils

public class SSHCommandLine extends com.dragonflow.Utils.RemoteCommandLine
{

    public SSHCommandLine()
    {
    }

    public String getMethodName()
    {
        return "ssh";
    }

    public String getMethodDisplayName()
    {
        return "SSH";
    }

    public Array exec(String s, com.dragonflow.SiteView.Machine machine, boolean flag)
    {
        super.exec(s, machine, flag);
        String s1 = "";
        if(s == null || s.length() == 0)
        {
            s1 = "SSH Command error: null or zero length command on machine: " + machine.getProperty(com.dragonflow.SiteView.Machine.pName);
        }
        if(s1.length() > 0)
        {
            com.dragonflow.Log.LogManager.log("Error", s1);
            exitValue = com.dragonflow.SiteView.Monitor.kURLUnknownError;
            return new Array();
        }
        int i = com.dragonflow.Properties.StringProperty.toInteger(machine.getProperty(com.dragonflow.SiteView.Machine.pTimeout));
        if(i == 0)
        {
            i = 60000;
        } else
        {
            i *= 1000;
        }
        if(machine.getSetting("_os").equals("NT") && machine.getSetting("_method").equals("ssh"))
        {
            HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s2 = com.dragonflow.Utils.TextUtils.getValue(hashmap, "_NTSSHTimeout");
            if(s2.length() > 0)
            {
                int k = com.dragonflow.Utils.TextUtils.toInt(s2) * 1000;
                if(k > 0)
                {
                    i = k;
                }
            }
        }
        Array array = new Array();
        exitValue = com.dragonflow.SSH.SSHManager.getInstance().execute(this, machine, s, i, flag, progressStream, array);
        for(int j = 0; j < array.size(); j++)
        {
            String s3 = new String(toEncoding((String)array.get(j)));
            array.put(j, s3);
        }

        return array;
    }

    public static void main(String args[])
    {
        String s = args[0];
        String s1 = args[1];
        String s2 = args[2];
        String s3 = args[3];
        com.dragonflow.SiteView.Machine machine = new Machine();
        machine.setProperty(com.dragonflow.SiteView.Machine.pHost, s);
        machine.setProperty(com.dragonflow.SiteView.Machine.pLogin, s1);
        machine.setProperty(com.dragonflow.SiteView.Machine.pPassword, s2);
        machine.setProperty(com.dragonflow.SiteView.Machine.pLocalUser, s3);
        if(args.length >= 5)
        {
            machine.setProperty(com.dragonflow.SiteView.Machine.pPrompt, args[4]);
        }
        machine.setProperty("_trace", "true");
        com.dragonflow.Utils.SSHCommandLine sshcommandline = new SSHCommandLine();
        sshcommandline.debug = true;
        Array array = sshcommandline.exec("ps -ef", machine, false);
        for(int i = 0; i < array.size(); i++)
        {
            java.lang.System.out.println("RESULT=" + array.get(i));
        }

    }
}
