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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.net.Socket;
import java.util.Enumeration;

import com.dragonflow.SiteView.Machine;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.Utils:
// RemoteCommandLine, TelnetConnection, CounterLock, TextUtils,
// FileUtils

public class TelnetCommandLine extends com.dragonflow.Utils.RemoteCommandLine
{

    static final char WILL = 251;
    static final char WONT = 252;
    static final char DO = 253;
    static final char DONT = 254;
    static final char IAC = 255;
    static final char ECHO = 1;
    static HashMap openConnections = new HashMap();
    static String endOfCommand = "siteview-command-end";
    static String echoCommand = "echo ";
    String host;
    String login;
    String password;
    String loginPrompt;
    String passwordPrompt;
    String secondaryPrompt;
    String secondaryResponse;
    String prompt;
    String loginIncorrect;
    String initShellEnvironment;
    StringBuffer contentBuffer;
    int commandResultIndex;
    boolean serverWillEcho;
    boolean askedForEcho;

    public TelnetCommandLine()
    {
        host = "";
        login = "";
        password = "";
        loginPrompt = "";
        passwordPrompt = "";
        secondaryPrompt = "";
        secondaryResponse = "";
        prompt = "";
        loginIncorrect = "";
        initShellEnvironment = "";
        contentBuffer = new StringBuffer();
        commandResultIndex = 0;
        serverWillEcho = true;
        askedForEcho = false;
    }

    public String getMethodName()
    {
        return "telnet";
    }

    public String getMethodDisplayName()
    {
        return "Telnet";
    }

    public Array exec(String s, com.dragonflow.SiteView.Machine machine, boolean flag)
    {
        super.exec(s, machine, flag);
        int i = com.dragonflow.Properties.StringProperty.toInteger(machine.getProperty(com.dragonflow.SiteView.Machine.pTimeout));
        if(i == 0)
        {
            i = 60000;
        } else
        {
            i *= 1000;
        }
        host = machine.getProperty(com.dragonflow.SiteView.Machine.pHost);
        login = machine.getProperty(com.dragonflow.SiteView.Machine.pLogin);
        password = machine.getProperty(com.dragonflow.SiteView.Machine.pPassword);
        loginPrompt = machine.getProperty(com.dragonflow.SiteView.Machine.pLoginPrompt);
        passwordPrompt = machine.getProperty(com.dragonflow.SiteView.Machine.pPasswordPrompt);
        prompt = machine.getProperty(com.dragonflow.SiteView.Machine.pPrompt);
        loginIncorrect = machine.getProperty("_loginIncorrectPrompt");
        com.dragonflow.SiteView.Machine _tmp = machine;
        secondaryPrompt = machine.getProperty(com.dragonflow.SiteView.Machine.pSecondaryPrompt);
        com.dragonflow.SiteView.Machine _tmp1 = machine;
        secondaryResponse = machine.getProperty(com.dragonflow.SiteView.Machine.pSecondaryResponse);
        com.dragonflow.SiteView.Machine _tmp2 = machine;
        initShellEnvironment = machine.getProperty(com.dragonflow.SiteView.Machine.pInitShellEnvironment);
        if(loginIncorrect.length() == 0)
        {
            loginIncorrect = "ogin incorrect";
        }
        com.dragonflow.Utils.CounterLock counterlock = com.dragonflow.SiteView.Platform.getLock(machine.getProperty(com.dragonflow.SiteView.Machine.pID));
        int j = com.dragonflow.SiteView.Monitor.kURLUnknownError;
        Array array = null;
        try
        {
            traceMessage("Requesting lock for: " + s, machine, TO_REMOTE);
            counterlock.get();
            traceMessage("Received lock for: " + s, machine, TO_REMOTE);
            long l = (long)i + com.dragonflow.SiteView.Platform.timeMillis();
            com.dragonflow.Utils.TelnetConnection telnetconnection = (com.dragonflow.Utils.TelnetConnection)openConnections.get(machine.getProperty(com.dragonflow.SiteView.Machine.pID));
            if(telnetconnection != null && telnetconnection.machine != machine)
            {
                com.dragonflow.Utils.TelnetCommandLine.removeConnection(telnetconnection);
                telnetconnection = null;
            }
            j = executeCommand(telnetconnection, machine, s, l);
            if(j != com.dragonflow.StandardMonitor.URLMonitor.kURLok && telnetconnection != null)
            {
                com.dragonflow.Utils.TelnetCommandLine.removeConnection(telnetconnection);
                telnetconnection = null;
                j = executeCommand(telnetconnection, machine, s, l);
            }
            String s1 = "";
            if(!flag)
            {
                s1 = contentBuffer.toString().substring(commandResultIndex);
            } else
            {
                s1 = contentBuffer.toString();
            }
            array = com.dragonflow.SiteView.Platform.split('\n', s1);
            if(!flag)
            {
                if(array.size() > 0)
                {
                    String s2 = (String)array.popFront();
                    if(s2.indexOf(s) == -1)
                    {
                        array.pushFront(s2);
                    }
                }
                if(array.size() > 0)
                {
                    String s3 = (String)array.popBack();
                    if(s3.indexOf(prompt) == -1)
                    {
                        array.pushBack(s3);
                    }
                }
            }
            if(j != com.dragonflow.StandardMonitor.URLMonitor.kURLok)
            {
                progressMessage("Connection failed: " + com.dragonflow.SiteView.Monitor.lookupStatus(j));
                exitValue = j;
            }
            traceMessage(array, machine, FROM_REMOTE);
            String s4 = machine.getProperty("_disableCache");
            if(j != com.dragonflow.SiteView.Monitor.kURLok || s4.length() > 0)
            {
                if(telnetconnection != null)
                {
                    com.dragonflow.Utils.TelnetCommandLine.removeConnection(telnetconnection);
                }
                telnetconnection = null;
            }
        }
        finally
        {
            traceMessage("Releasing lock for: " + s + ", status=" + j, machine, TO_REMOTE);
            counterlock.release();
        }
        return array;
    }

    int executeCommand(com.dragonflow.Utils.TelnetConnection telnetconnection, com.dragonflow.SiteView.Machine machine, String s, long l)
    {
        int i = com.dragonflow.StandardMonitor.URLMonitor.kURLok;
        try
        {
            if(telnetconnection == null)
            {
                telnetconnection = new TelnetConnection();
                i = connect(telnetconnection, l, machine);
                if(i == com.dragonflow.StandardMonitor.URLMonitor.kURLok)
                {
                    telnetconnection.machine = machine;
                    com.dragonflow.Utils.TelnetCommandLine.addConnection(telnetconnection);
                }
            }
            if(i == com.dragonflow.StandardMonitor.URLMonitor.kURLok)
            {
                i = runCommand(telnetconnection, s, l);
            }
        }
        catch(java.net.UnknownHostException unknownhostexception)
        {
            i = com.dragonflow.SiteView.Monitor.kURLBadHostNameError;
        }
        catch(java.net.SocketException socketexception)
        {
            if(com.dragonflow.SiteView.Platform.noRoute(socketexception))
            {
                i = com.dragonflow.SiteView.Monitor.kURLNoRouteToHostError;
            } else
            {
                i = com.dragonflow.SiteView.Monitor.kURLNoConnectionError;
            }
        }
        catch(java.io.InterruptedIOException interruptedioexception)
        {
            i = com.dragonflow.SiteView.Monitor.kURLTimeoutError;
        }
        catch(java.lang.Exception exception)
        {
            exception.printStackTrace();
            java.lang.System.err.println(exception + " error: " + exception.getMessage());
            i = com.dragonflow.SiteView.Monitor.kURLUnknownError;
        }
        return i;
    }

    static void addConnection(com.dragonflow.Utils.TelnetConnection telnetconnection)
    {
        openConnections.put(telnetconnection.machine.getProperty(com.dragonflow.SiteView.Machine.pID), telnetconnection);
    }

    static void removeConnection(com.dragonflow.Utils.TelnetConnection telnetconnection)
    {
        openConnections.remove(telnetconnection.machine.getProperty(com.dragonflow.SiteView.Machine.pID));
        com.dragonflow.Utils.TelnetCommandLine.closeConnection(telnetconnection);
    }

    static void closeConnection(com.dragonflow.Utils.TelnetConnection telnetconnection)
    {
        try
        {
            if(telnetconnection.out != null)
            {
                telnetconnection.out.close();
            }
            if(telnetconnection.in != null)
            {
                telnetconnection.in.close();
            }
            if(telnetconnection.socket != null)
            {
                telnetconnection.socket.close();
            }
        }
        catch(java.io.IOException ioexception)
        {
            java.lang.System.err.println("Could not close Telnet streams");
        }
    }

    public static void closeAll()
    {
        com.dragonflow.Utils.TelnetConnection telnetconnection;
        for(Enumeration enumeration = (Enumeration) openConnections.keys(); enumeration.hasMoreElements(); com.dragonflow.Utils.TelnetCommandLine.closeConnection(telnetconnection))
        {
            java.lang.Object obj = enumeration.nextElement();
            telnetconnection = (com.dragonflow.Utils.TelnetConnection)openConnections.get(obj);
        }

        openConnections = new HashMap();
    }

    int connect(com.dragonflow.Utils.TelnetConnection telnetconnection, long l, com.dragonflow.SiteView.Machine machine)
        throws java.lang.Exception
    {
        int i = 23;
        int j = host.indexOf(':');
        if(j != -1)
        {
            i = com.dragonflow.Utils.TextUtils.readInteger(host, j + 1);
            host = host.substring(0, j);
        }
        String s = "";
        String as[] = com.dragonflow.Utils.TextUtils.split(secondaryPrompt, ",");
        String as1[] = com.dragonflow.Utils.TextUtils.split(secondaryResponse, ",");
        progressMessage("Connecting to " + host + "...");
        telnetconnection.socket = new Socket(host, i);
        telnetconnection.out = com.dragonflow.Utils.FileUtils.MakeOutputWriter(telnetconnection.socket.getOutputStream(), "Cp1252");
        telnetconnection.in = new BufferedInputStream(telnetconnection.socket.getInputStream());
        progressMessage("Looking for login prompt...");
        s = read(telnetconnection.in, telnetconnection.out, loginPrompt, telnetconnection.socket, l, "");
        if(s == null)
        {
            return com.dragonflow.SiteView.Monitor.kURLContentMatchError;
        }
        progressMessage("Sending login..");
        contentBuffer.append(s);
        write(telnetconnection.out, login);
        progressMessage("Looking for password prompt...");
        s = read(telnetconnection.in, telnetconnection.out, passwordPrompt, telnetconnection.socket, l, "");
        if(s == null)
        {
            return com.dragonflow.SiteView.Monitor.kURLContentMatchError;
        }
        progressMessage("Sending password..");
        contentBuffer.append(s);
        write(telnetconnection.out, password);
        if(secondaryPrompt.length() > 0 && secondaryResponse.length() > 0)
        {
            for(int k = 0; k < as1.length; k++)
            {
                progressMessage("Looking for secondary prompt " + as[k] + "...");
                s = read(telnetconnection.in, telnetconnection.out, as[k], telnetconnection.socket, l, "");
                if(s == null)
                {
                    return com.dragonflow.SiteView.Monitor.kURLContentMatchError;
                }
                if(as1[k].equals("\\n"))
                {
                    as1[k] = "";
                }
                progressMessage("Sending secondary response " + as1[k] + " ..");
                write(telnetconnection.out, as1[k]);
            }

        }
        progressMessage("Looking for prompt...");
        s = read(telnetconnection.in, telnetconnection.out, prompt, telnetconnection.socket, l, loginIncorrect);
        if(s == null)
        {
            return 401;
        }
        contentBuffer.append(s);
        String s3 = machine.getProperty("_shell");
        String s4 = machine.getProperty("_shellInit");
        if(s3.length() == 0)
        {
            com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
            s3 = siteviewgroup.getSetting("_remoteShell");
            s4 = siteviewgroup.getSetting("_remoteShellInit");
        }
        if(s3.length() > 0)
        {
            progressMessage("Starting shell...(shell=" + s3 + ", shellInit=" + s4 + ")");
            write(telnetconnection.out, s3);
            String s5 = s4 + ";" + echoCommand + endOfCommand;
            write(telnetconnection.out, s5);
            String s1 = read(telnetconnection.in, telnetconnection.out, endOfCommand, telnetconnection.socket, l, "", echoCommand + endOfCommand, null);
            if(s1 == null)
            {
                return 401;
            }
            contentBuffer.append(s1);
        }
        if(initShellEnvironment.length() != 0)
        {
            progressMessage("Initializing shell environment with:" + initShellEnvironment);
            write(telnetconnection.out, initShellEnvironment);
            String s6 = echoCommand + "start;" + echoCommand + endOfCommand;
            write(telnetconnection.out, s6);
            String s2 = read(telnetconnection.in, telnetconnection.out, endOfCommand, telnetconnection.socket, l, "", echoCommand + endOfCommand, null);
            progressMessage("Looking for prompt...");
            s2 = read(telnetconnection.in, telnetconnection.out, prompt, telnetconnection.socket, l, "");
            if(s2 == null)
            {
                return 401;
            }
            contentBuffer.append(s2);
        }
        return com.dragonflow.StandardMonitor.URLMonitor.kURLok;
    }

    int runCommand(com.dragonflow.Utils.TelnetConnection telnetconnection, String s, long l)
        throws java.io.IOException
    {
        String s1 = "";
        progressMessage("Running command..");
        commandResultIndex = contentBuffer.length();
        String s2 = s.trim() + ";" + echoCommand + endOfCommand;
        if(telnetconnection.in.available() > 0)
        {
            traceMessage("(flushing)", telnetconnection.machine, FROM_REMOTE);
            StringBuffer stringbuffer = new StringBuffer(32);
            for(; telnetconnection.in.available() > 0; stringbuffer.append((char)telnetconnection.in.read())) { }
            traceMessage(stringbuffer.toString() + "(flushed)", telnetconnection.machine, FROM_REMOTE);
        } else
        {
            traceMessage("(input buffer empty)", telnetconnection.machine, FROM_REMOTE);
        }
        String s3 = com.dragonflow.Utils.TelnetCommandLine.extractDestFileIfAny(s);
        if(s3 != null)
        {
            s2 = com.dragonflow.Utils.TelnetCommandLine.unMangleCmdStringForFileGet(s) + ";" + echoCommand + endOfCommand;
        }
        traceMessage(s2, telnetconnection.machine, TO_REMOTE);
        write(telnetconnection.out, s2);
        progressMessage("Looking for prompt...");
        s1 = read(telnetconnection.in, telnetconnection.out, endOfCommand, telnetconnection.socket, l, "", echoCommand + endOfCommand, s3);
        traceMessage("&&&& Telnet read() raw result: " + s1, telnetconnection.machine, FROM_REMOTE);
        if(s1 == null)
        {
            return com.dragonflow.StandardMonitor.URLMonitor.kURLContentMatchError;
        }
        if(!s1.equals(" sIiTeSsCoPeReDiReCTtOkEN* "))
        {
            if(s1.endsWith(endOfCommand))
            {
                s1 = s1.substring(0, s1.length() - endOfCommand.length());
            } else
            if(s1.length() == 0)
            {
                com.dragonflow.Utils.TelnetCommandLine.closeConnection(telnetconnection);
            }
        }
        contentBuffer.append(s1);
        return com.dragonflow.StandardMonitor.URLMonitor.kURLok;
    }

    String read(java.io.BufferedInputStream bufferedinputstream, java.io.PrintWriter printwriter, String s, java.net.Socket socket, long l, String s1)
        throws java.io.IOException
    {
        return read(bufferedinputstream, printwriter, s, socket, l, s1, null, null);
    }

    String read(java.io.BufferedInputStream bufferedinputstream, java.io.PrintWriter printwriter, String s, java.net.Socket socket, long l, String s1, 
            String s2, String s3)
        throws java.io.IOException
    {
        StringBuffer stringbuffer = new StringBuffer();
        int i = 0;
        java.io.FileWriter filewriter = null;

    while(true)
        {
                if (i != 65535) 
                {
                    i = readChar(bufferedinputstream, socket, l);
                    if(i != 65535)
                    {
                        if(i != 255)
                        {
                            break;
                        }
                        char c = readChar(bufferedinputstream, socket, l);
                        char c1 = readChar(bufferedinputstream, socket, l);
                        if(c == '\373')
                        {
                            if(c1 == '\001')
                            {
                                if(!askedForEcho)
                                {
                                    sendIAC(printwriter, '\375', '\001');
                                    askedForEcho = true;
                                    if(detail && progressStream != null)
                                    {
                                        progressStream.print("POS: IAC WILL ECHO /IAC DO ECHO\n");
                                    }
                                } else
                                if(detail && progressStream != null)
                                {
                                    progressStream.print("<-- IAC WILL ECHO\n");
                                }
                                serverWillEcho = true;
                            } else
                            {
                                sendIAC(printwriter, '\376', c1);
                                if(detail && progressStream != null)
                                {
                                    progressStream.print("NEG: IAC WILL " + (int)c1 + "/IAC DONT " + (int)c1 + "\n");
                                }
                            }
                        } else
                        if(c == '\374')
                        {
                            if(c1 == '\001' && askedForEcho)
                            {
                                if(com.dragonflow.Log.LogManager.loggerRegistered("RunMonitor"))
                                {
                                    com.dragonflow.Log.LogManager.log("RunMonitor", "Telnet Server has refused to ECHO back to SiteView.");
                                }
                                progressStream.print("Server has refused to ECHO back to SiteView.\n");
                                serverWillEcho = false;
                            } else
                            {
                                sendIAC(printwriter, '\376', c1);
                                if(detail && progressStream != null)
                                {
                                    progressStream.print("NEG: IAC WONT " + (int)c1 + "/IAC DONT " + (int)c1 + "\n");
                                }
                            }
                        } else
                        if(c == '\375')
                        {
                            sendIAC(printwriter, '\374', c1);
                            if(detail && progressStream != null)
                            {
                                progressStream.print("NEG: IAC DO " + (int)c1 + "/IAC WONT " + (int)c1 + "\n");
                            }
                        } else
                        if(c == '\376')
                        {
                            sendIAC(printwriter, '\374', c1);
                            if(detail && progressStream != null)
                            {
                                progressStream.print("NEG: IAC DONT " + (int)c1 + "/IAC WONT " + (int)c1 + "\n");
                            }
                        } else
                        if(detail && progressStream != null)
                        {
                            progressStream.print("NEG: IAC " + (int)c + " " + (int)c1 + "/<NORESP>\n");
                        }
                        if(!askedForEcho)
                        {
                            askedForEcho = true;
                            sendIAC(printwriter, '\375', '\001');
                            if(detail && progressStream != null)
                            {
                                progressStream.print("-->IAC DO ECHO\n");
                            }
                        }
                    }
                }
                else if(i != 0) {
            if(detail && progressStream != null)
            {
                progressStream.print(i);
            }
            stringbuffer.append(i);
            if(s2 == null || !com.dragonflow.Utils.TextUtils.stringBufferEndsWith(stringbuffer, s2))
            {
                if(com.dragonflow.Utils.TextUtils.stringBufferEndsWith(stringbuffer, s))
                {
                    if(s3 == null)
                    {
                        return toEncoding(stringbuffer.toString());
                    }
                    if(filewriter != null)
                    {
                        filewriter.close();
                        return " sIiTeSsCoPeReDiReCTtOkEN* ";
                    }
                } else
                if(s3 != null && s3.length() > 0)
                {
                    if(filewriter == null)
                    {
                        filewriter = new FileWriter(new File(s3));
                    }
                    writeToFileWhenReady(stringbuffer, filewriter, s);
                }
                if(s1.length() > 0 && com.dragonflow.Utils.TextUtils.stringBufferEndsWith(stringbuffer, s1))
                {
                    return null;
                }
            }
            }
        }

        return "";
    }

    private void sendIAC(java.io.PrintWriter printwriter, char c, char c1)
    {
        writeChar(printwriter, '\377');
        writeChar(printwriter, c);
        writeChar(printwriter, c1);
    }

    void write(java.io.PrintWriter printwriter, String s)
    {
        printwriter.print(s + com.dragonflow.StandardMonitor.URLMonitor.CRLF);
        printwriter.flush();
    }

    void writeChar(java.io.PrintWriter printwriter, char c)
    {
        printwriter.print(c);
        printwriter.flush();
    }

    private void writeToFileWhenReady(StringBuffer stringbuffer, java.io.FileWriter filewriter, String s)
        throws java.io.IOException
    {
        if(stringbuffer.length() == 0)
        {
            return;
        }
        if(stringbuffer.charAt(stringbuffer.length() - 1) == '\n' || stringbuffer.length() >= 32000)
        {
            if(stringbuffer.indexOf(s) == -1)
            {
                filewriter.write(stringbuffer.toString());
            }
            stringbuffer.setLength(0);
        }
    }

    public static void main(String args[])
    {
        String s = args[0];
        String s1 = args[1];
        String s2 = args[2];
        String s3 = args[3];
        com.dragonflow.SiteView.Machine machine = new Machine();
        machine.setProperty(com.dragonflow.SiteView.Machine.pID, "1");
        machine.setProperty(com.dragonflow.SiteView.Machine.pHost, s);
        machine.setProperty(com.dragonflow.SiteView.Machine.pLogin, s1);
        machine.setProperty(com.dragonflow.SiteView.Machine.pPassword, s2);
        machine.setProperty(com.dragonflow.SiteView.Machine.pLocalUser, s3);
        if(args.length >= 5)
        {
            machine.setProperty(com.dragonflow.SiteView.Machine.pPrompt, args[4]);
        }
        machine.setProperty("_trace", "true");
        com.dragonflow.Utils.TelnetCommandLine telnetcommandline = new TelnetCommandLine();
        Array array = telnetcommandline.exec("ls", machine, false);
        for(int i = 0; i < array.size(); i++)
        {
            java.lang.System.out.println(array.get(i));
        }

        telnetcommandline = new TelnetCommandLine();
        array = telnetcommandline.exec("ps -ef", machine, false);
        for(int j = 0; j < array.size(); j++)
        {
            java.lang.System.out.println(array.get(j));
        }

    }

}
