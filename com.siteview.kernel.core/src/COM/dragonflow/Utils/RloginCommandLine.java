 /*
  * Created on 2005-2-9 3:06:20
  *
  * .java
  *
  * History:
  *
  */
  package COM.dragonflow.Utils;

 /**
     * Comment for <code></code>
     * 
     * @author
     * @version 0.0
     * 
     * 
     */

import java.io.BufferedInputStream;

import com.recursionsw.jgl.Array;
import COM.dragonflow.SiteView.Machine;

// Referenced classes of package COM.dragonflow.Utils:
// RemoteCommandLine, TextUtils, FileUtils

public class RloginCommandLine extends COM.dragonflow.Utils.RemoteCommandLine
{

    public static boolean debug = false;

    public RloginCommandLine()
    {
    }

    public String getMethodName()
    {
        return "rlogin";
    }

    public String getMethodDisplayName()
    {
        return "rlogin";
    }

    public Array exec(String s, COM.dragonflow.SiteView.Machine machine, boolean flag)
    {
        super.exec(s, machine, flag);
        long l = COM.dragonflow.Properties.StringProperty.toLong(machine.getProperty(COM.dragonflow.SiteView.Machine.pTimeout));
        if(l == 0L)
        {
            l = 60000L;
        } else
        {
            l *= 1000L;
        }
        java.net.Socket socket = null;
        Object obj = null;
        java.io.PrintWriter printwriter = null;
        java.io.BufferedInputStream bufferedinputstream = null;
        String s1 = machine.getProperty(COM.dragonflow.SiteView.Machine.pHost);
        int i = 513;
        int j = s1.indexOf(':');
        if(j != -1)
        {
            i = COM.dragonflow.Utils.TextUtils.readInteger(s1, j + 1);
            s1 = s1.substring(0, j);
        }
        String s2 = machine.getProperty(COM.dragonflow.SiteView.Machine.pLogin);
        String s3 = machine.getProperty(COM.dragonflow.SiteView.Machine.pPassword);
        String s4 = machine.getProperty(COM.dragonflow.SiteView.Machine.pLocalUser);
        String s5 = machine.getProperty(COM.dragonflow.SiteView.Machine.pPasswordPrompt);
        String s6 = machine.getProperty(COM.dragonflow.SiteView.Machine.pPrompt);
        String s7 = "";
        String s8 = "tty/9600";
        int k = COM.dragonflow.StandardMonitor.URLMonitor.kURLok;
        byte abyte0[] = new byte[s2.length() + s4.length() + s8.length() + 4];
        int i1 = 0;
        abyte0[i1++] = 0;
        for(int j1 = 0; j1 < s4.length(); j1++)
        {
            abyte0[i1++] = (byte)s4.charAt(j1);
        }

        abyte0[i1++] = 0;
        for(int k1 = 0; k1 < s2.length(); k1++)
        {
            abyte0[i1++] = (byte)s2.charAt(k1);
        }

        abyte0[i1++] = 0;
        for(int l1 = 0; l1 < s8.length(); l1++)
        {
            abyte0[i1++] = (byte)s8.charAt(l1);
        }

        abyte0[i1++] = 0;
        traceMessage(s2 + "," + s4 + "," + s8, machine, TO_REMOTE);
        StringBuffer stringbuffer = new StringBuffer();
        progressMessage("Connecting to " + s1 + "...");
        try
        {
            socket = COM.dragonflow.Utils.RloginCommandLine.getReservedPort(s1, i);
            progressMessage("Logging In...");
            java.io.OutputStream outputstream = socket.getOutputStream();
            outputstream.write(abyte0);
            outputstream.flush();
            printwriter = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(outputstream, "Cp1252");
            bufferedinputstream = new BufferedInputStream(socket.getInputStream());
            long l2 = COM.dragonflow.SiteView.Platform.timeMillis() + l * 1000L;
            int j2 = 0;
            boolean flag1 = false;
            boolean flag2 = false;
            do
            {
                if(j2 == -1)
                {
                    break;
                }
                j2 = readChar(bufferedinputstream, socket, l2);
                if(j2 == 65535)
                {
                    for(int k2 = 0; k2 < 11; k2++)
                    {
                        j2 = readChar(bufferedinputstream, socket, l2);
                    }

                    byte abyte1[] = new byte[12];
                    abyte1[0] = -1;
                    abyte1[1] = -1;
                    abyte1[2] = 115;
                    abyte1[3] = 115;
                    abyte1[4] = 0;
                    abyte1[5] = 24;
                    abyte1[6] = 0;
                    abyte1[7] = 80;
                    abyte1[8] = 4;
                    abyte1[9] = 0;
                    abyte1[10] = 4;
                    abyte1[11] = 0;
                    outputstream.write(abyte1);
                    outputstream.flush();
                    continue;
                }
                if(j2 == -1)
                {
                    continue;
                }
                stringbuffer.append(j2);
                String s10 = toEncoding(stringbuffer.toString());
                if(detail && progressStream != null)
                {
                    progressStream.print(j2);
                }
                if(s7.length() > 0 && s10.endsWith(s7))
                {
                    progressMessage("Login Failed");
                    k = 401;
                    break;
                }
                if(!flag2 && stringbuffer.length() < 15)
                {
                    if(s10.endsWith(s5))
                    {
                        traceMessage("Password: " + COM.dragonflow.Utils.TextUtils.filledString('*', s3.length()), machine, TO_REMOTE);
                        progressMessage("Sending Password...");
                        printwriter.print(s3 + '\n');
                        printwriter.flush();
                        stringbuffer = new StringBuffer();
                        s7 = machine.getProperty("_loginIncorrectPrompt");
                        if(s7.length() == 0)
                        {
                            s7 = "ogin incorrect";
                        }
                    }
                    continue;
                }
                if(!s10.endsWith(s6))
                {
                    continue;
                }
                if(flag1)
                {
                    progressMessage("Command completed...");
                    break;
                }
                progressMessage("Running command...");
                traceMessage(s, machine, TO_REMOTE);
                printwriter.print(s + '\n');
                printwriter.flush();
                stringbuffer = new StringBuffer();
                resetBuffer();
                flag1 = true;
                flag2 = true;
                s7 = "";
            } while(true);
        }
        catch(java.net.UnknownHostException unknownhostexception)
        {
            k = COM.dragonflow.StandardMonitor.URLMonitor.kURLBadHostNameError;
        }
        catch(java.net.SocketException socketexception)
        {
            if(COM.dragonflow.SiteView.Platform.noRoute(socketexception))
            {
                k = COM.dragonflow.StandardMonitor.URLMonitor.kURLNoRouteToHostError;
            } else
            {
                k = COM.dragonflow.StandardMonitor.URLMonitor.kURLNoConnectionError;
            }
        }
        catch(java.io.InterruptedIOException interruptedioexception)
        {
            k = COM.dragonflow.StandardMonitor.URLMonitor.kURLTimeoutError;
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            java.lang.System.err.println(exception + " error: " + exception.getMessage());
            k = COM.dragonflow.StandardMonitor.URLMonitor.kURLUnknownError;
        }
        finally
        {
            try
            {
                if(printwriter != null)
                {
                    printwriter.close();
                }
                if(bufferedinputstream != null)
                {
                    bufferedinputstream.close();
                }
                if(socket != null)
                {
                    socket.close();
                }
            }
            catch(java.io.IOException ioexception)
            {
                java.lang.System.err.println("Could not close FTP streams");
            }
        }
        Array array = null;
        if(k == COM.dragonflow.StandardMonitor.URLMonitor.kURLok)
        {
            array = COM.dragonflow.SiteView.Platform.split('\n', toEncoding(stringbuffer.toString()));
            if(!flag)
            {
                if(array.size() > 0)
                {
                    array.popFront();
                }
                int i2 = COM.dragonflow.SiteView.Machine.stringToOS(machine.getProperty(COM.dragonflow.SiteView.Machine.pOS));
                if((COM.dragonflow.SiteView.Platform.isSGI(i2) || COM.dragonflow.SiteView.Platform.isSolaris(i2)) && array.size() > 0)
                {
                    String s9 = (String)array.get(0);
                    if(s9.trim().length() == 0)
                    {
                        array.popFront();
                    }
                }
                if(array.size() > 0)
                {
                    array.popBack();
                }
            }
            traceMessage(array, machine, FROM_REMOTE);
        } else
        {
            progressMessage("Connection failed: " + COM.dragonflow.SiteView.Monitor.lookupStatus(k));
            array = new Array();
            traceMessage("ERROR - STATUS = " + k, machine, FROM_REMOTE);
            exitValue = k;
        }
        return array;
    }

    public static void main(String args[])
    {
        String s = args[0];
        String s1 = args[1];
        String s2 = args[2];
        String s3 = args[3];
        String s4 = "ls";
        COM.dragonflow.SiteView.Machine machine = new Machine();
        machine.setProperty(COM.dragonflow.SiteView.Machine.pHost, s);
        machine.setProperty(COM.dragonflow.SiteView.Machine.pLogin, s1);
        machine.setProperty(COM.dragonflow.SiteView.Machine.pPassword, s2);
        machine.setProperty(COM.dragonflow.SiteView.Machine.pLocalUser, s3);
        if(args.length >= 5)
        {
            s4 = args[4];
        }
        machine.setProperty("_trace", "true");
        COM.dragonflow.Utils.RloginCommandLine rlogincommandline = new RloginCommandLine();
        rlogincommandline.exec(s4, machine, false);
    }

}
