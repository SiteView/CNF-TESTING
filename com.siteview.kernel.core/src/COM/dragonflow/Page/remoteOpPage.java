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
import java.util.Vector;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

import COM.dragonflow.HTTP.HTTPRequestException;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.RemoteFile;

// Referenced classes of package COM.dragonflow.Page:
// CGI, alertPage, monitorPage

public class remoteOpPage extends COM.dragonflow.Page.CGI
{

    public static String CRLF = "\r\n";

    public remoteOpPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        if(!request.actionAllowed("_file"))
        {
            throw new HTTPRequestException(557);
        }
        String s = request.getValue("operation");
        try
        {
            if(s.equals("run"))
            {
                String s1 = request.getValue("command");
                String s4 = request.getValue("machineID");
                HashMap hashmap = null;
                String s6 = COM.dragonflow.SiteView.Machine.getCommandString(s1, s4, hashmap);
                COM.dragonflow.Utils.CommandLine commandline = new CommandLine();
                Array array2 = commandline.exec(s6, s4, COM.dragonflow.SiteView.Platform.getLock(s4));
                outputStream.println("<PRE>");
                for(int k1 = 0; k1 < array2.size(); k1++)
                {
                    outputStream.println(array2.get(k1));
                }

                outputStream.println("</PRE>");
            } else
            if(s.equals("scripts"))
            {
                String s2 = request.getValue("machineID");
                outputStream.println("<PRE>");
                java.util.Vector vector1 = COM.dragonflow.Page.remoteOpPage.getScriptList(s2, request);
                for(int k = 0; k < vector1.size(); k++)
                {
                    outputStream.println(vector1.elementAt(k));
                }

                outputStream.println("</PRE>");
            } else
            if(s.equals("updateStaticPages"))
            {
                String s3 = COM.dragonflow.Utils.I18N.toDefaultEncoding(request.getValue("group"));
                if(s3.length() == 0)
                {
                    COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages();
                } else
                {
                    COM.dragonflow.SiteView.SiteViewGroup.updateStaticPages(s3);
                }
            } else
            if(s.equals("getServers"))
            {
                java.util.Vector vector = COM.dragonflow.SiteView.Platform.getServers();
                outputStream.println("<PRE>");
                for(int i = 0; i < vector.size(); i += 2)
                {
                    outputStream.println(vector.elementAt(i));
                }

                outputStream.println("</PRE>");
            } else
            if(s.equals("monitorClasses"))
            {
                outputStream.println("<PRE>");
                Array array = COM.dragonflow.Page.monitorPage.getMonitorClasses();
                for(int j = 0; j < array.size(); j++)
                {
                    String s5 = ((java.lang.Class)array.get(j)).getName();
                    int i1 = s5.lastIndexOf(".");
                    if(i1 >= 0)
                    {
                        s5 = s5.substring(i1 + 1);
                    }
                    outputStream.println(s5);
                }

                outputStream.println("</PRE>");
            } else
            if(s.equals("actionClasses"))
            {
                COM.dragonflow.Page.alertPage alertpage = new alertPage();
                outputStream.println("<PRE>");
                Array array1 = alertpage.getActionClasses();
                for(int l = 0; l < array1.size(); l++)
                {
                    String s7 = ((java.lang.Class)array1.get(l)).getName();
                    int j1 = s7.lastIndexOf(".");
                    if(j1 >= 0)
                    {
                        s7 = s7.substring(j1 + 1);
                    }
                    outputStream.println(s7);
                }

                outputStream.println("</PRE>");
            } else
            if(s.equals("platform"))
            {
                outputStream.println("<PRE>");
                outputStream.println("os.name=" + java.lang.System.getProperty("os.name"));
                outputStream.println("os.version=" + java.lang.System.getProperty("os.version"));
                outputStream.println("version=" + COM.dragonflow.SiteView.Platform.getVersion());
                outputStream.println("</PRE>");
            } else
            {
                throw new Exception("unknown operation, " + s);
            }
        }
        catch(java.lang.Exception exception)
        {
            outputStream.println("error: " + exception + ", " + s);
        }
    }

    public void printCGIHeader()
    {
        request.printHeader(outputStream);
    }

    public void printCGIFooter()
    {
        outputStream.flush();
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.remoteOpPage remoteoppage = new remoteOpPage();
        if(args.length > 0)
        {
            remoteoppage.args = args;
        }
        remoteoppage.handleRequest();
    }

    public static java.util.Vector getScriptList(String s, COM.dragonflow.HTTP.HTTPRequest httprequest)
    {
        java.util.Vector vector = new Vector();
        if(COM.dragonflow.SiteView.Platform.isCommandLineRemote(s))
        {
            COM.dragonflow.Utils.RemoteFile remotefile = new RemoteFile(s, "scripts");
            int i = COM.dragonflow.SiteView.Machine.getOS(s);
            Array array = remotefile.listFiles();
            for(int k = 0; k < array.size(); k++)
            {
                String s2 = (String)array.get(k);
                if(!s2.endsWith(".txt") && (!COM.dragonflow.SiteView.Platform.isUnix(i) || !s2.startsWith(".")))
                {
                    vector.addElement(s2);
                    vector.addElement(s2);
                }
            }

        } else
        {
            java.io.File file = new File(COM.dragonflow.SiteView.Platform.getUsedDirectoryPath("scripts", httprequest.getAccount()));
            String as[] = file.list();
            for(int j = 0; j < as.length; j++)
            {
                String s1 = as[j];
                if(s1.endsWith(".txt") || COM.dragonflow.SiteView.Platform.isUnix() && s1.startsWith("."))
                {
                    continue;
                }
                java.io.File file1 = new File(file, as[j]);
                if(!file1.isDirectory())
                {
                    vector.addElement(s1);
                    vector.addElement(s1);
                }
            }

        }
        return vector;
    }

}
