/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * TelnetMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>TelnetMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
//            URLMonitor

public class TelnetMonitor extends AtomicMonitor
{

    public static StringProperty pServer;
    public static StringProperty pTimeout;
    public static StringProperty pUserName;
    public static StringProperty pPassword;
    public static StringProperty pLoginPrompt;
    public static StringProperty pPasswordPrompt;
    public static StringProperty pPrompt;
    public static StringProperty pCommand;
    public static StringProperty pContentMatch;
    public static StringProperty pStatus;
    public static StringProperty pRoundTripTime;
    static boolean debug = false;

    public TelnetMonitor()
    {
    }

    public String getHostname()
    {
        return getProperty(pServer);
    }

    public String getTestURL()
    {
        String s = "/SiteView/cgi/go.exe/SiteView?page=telnet&server=" + getProperty(pServer) + "&username=" + URLEncoder.encode(getProperty(pUserName)) + "&password=" + URLEncoder.encode(TextUtils.obscure(getProperty(pPassword))) + "&loginPrompt=" + URLEncoder.encode(getProperty(pLoginPrompt)) + "&passwordPrompt=" + URLEncoder.encode(getProperty(pPasswordPrompt)) + "&prompt=" + URLEncoder.encode(getProperty(pPrompt)) + "&command=" + URLEncoder.encode(getProperty(pCommand));
        return s;
    }

    protected boolean update()
    {
        int i = StringProperty.toInteger(getProperty(pTimeout));
        if(i == 0)
        {
            i = URLMonitor.DEFAULT_TIMEOUT;
        } else
        {
            i *= 1000;
        }
        long l = Platform.timeMillis();
        long l1 = (long)i + Platform.timeMillis();
        StringBuffer stringbuffer = new StringBuffer();
        int ai[] = checkTelnet(getProperty(pServer), getProperty(pUserName), getProperty(pPassword), getProperty(pCommand), getProperty(pLoginPrompt), getProperty(pPasswordPrompt), getProperty(pPrompt), stringbuffer, l1, null);
        int j = ai[0];
        long l2 = Platform.timeMillis() - l;
        if(stillActive())
        {
            synchronized(this)
            {
                if(j == kURLok)
                {
                    float f = (float)l2 / 1000F;
                    String s = TextUtils.floatToString(f, 2) + " sec";
                    setProperty(pRoundTripTime, l2);
                    setProperty(pStateString, s);
                } else
                {
                    setProperty(pStateString, URLMonitor.lookupStatus(j));
                    setProperty(pRoundTripTime, "n/a");
                }
                setProperty(pStatus, j);
            }
        }
        return true;
    }

    public Array getLogProperties()
    {
        Array array = super.getLogProperties();
        array.add(pStatus);
        array.add(pRoundTripTime);
        return array;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, HashMap hashmap)
    {
        if(stringproperty == pServer)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else
            if(TextUtils.hasSpaces(s))
            {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if(stringproperty == pTimeout)
        {
            if(TextUtils.digits(s) != s.length())
            {
                hashmap.put(stringproperty, "time out must be a number");
            } else
            if(TextUtils.toInt(s) < 1)
            {
                hashmap.put(stringproperty, "time out must be greater than 0");
            }
            return s;
        }
        if(stringproperty == pUserName || stringproperty == pPassword)
        {
            if(s.length() == 0)
            {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            return s;
        }
        if(stringproperty == pContentMatch)
        {
            String s1 = TextUtils.legalMatchString(s);
            if(s1.length() > 0)
            {
                hashmap.put(stringproperty, s1);
            }
            return s;
        } else
        {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public static int[] checkTelnet(String s, String s1, String s2, String s3, String s4, String s5, String s6, StringBuffer stringbuffer, 
            long l, PrintWriter printwriter)
    {
        Array array = new Array();
        if(s1.length() > 0)
        {
            array.add(s4);
            array.add(s1);
            array.add(s5);
            array.add(s2);
            array.add(s6);
            if(s3.length() > 0)
            {
                array.add(s3);
                array.add(s6);
            }
            array.add("exit");
        }
        return checkTelnet(s, array, stringbuffer, l, printwriter);
    }

    public static int[] checkTelnet(String s, Array array, StringBuffer stringbuffer, long l, PrintWriter printwriter)
    {
        int i = kURLUnknownError;
        Socket socket = null;
        PrintWriter printwriter1 = null;
        BufferedReader bufferedreader = null;
        String s1 = "";
        int j = 23;
        int k = s.indexOf(':');
        if(k != -1)
        {
            j = TextUtils.readInteger(s, k + 1);
            s = s.substring(0, k);
        }
        int i1 = 0;
        try
        {
            socket = new Socket(s, j);
            printwriter1 = FileUtils.MakeOutputWriter(socket.getOutputStream(), "");
            bufferedreader = FileUtils.MakeInputReader(socket.getInputStream(), "");
            Enumeration enumeration = array.elements();
            do
            {
                if(!enumeration.hasMoreElements())
                {
                    break;
                }
                String s3 = (String)enumeration.nextElement();
                String s2 = read(bufferedreader, printwriter1, s3, socket, l, printwriter);
                i1 = stringbuffer.length();
                stringbuffer.append(s2);
                if(enumeration.hasMoreElements())
                {
                    String s4 = (String)enumeration.nextElement();
                    write(printwriter1, s4, printwriter);
                }
            } while(true);
            i = kURLok;
        }
        catch(UnknownHostException unknownhostexception)
        {
            i = kURLBadHostNameError;
        }
        catch(SocketException socketexception)
        {
            if(Platform.noRoute(socketexception))
            {
                i = kURLNoRouteToHostError;
            } else
            {
                i = kURLNoConnectionError;
            }
        }
        catch(InterruptedIOException interruptedioexception)
        {
            i = kURLTimeoutError;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            System.err.println(exception + " error: " + exception.getMessage());
            i = kURLUnknownError;
        }
        finally
        {
            try
            {
                if(printwriter1 != null)
                {
                    printwriter1.close();
                }
                if(bufferedreader != null)
                {
                    bufferedreader.close();
                }
                if(socket != null)
                {
                    socket.close();
                }
            }
            catch(IOException ioexception)
            {
                System.err.println("Could not close FTP streams");
            }
        }
        int ai[] = new int[2];
        ai[0] = i;
        ai[1] = i1;
        return ai;
    }

    static String read(BufferedReader bufferedreader, PrintWriter printwriter, String s, Socket socket, long l, PrintWriter printwriter1)
        throws IOException
    {
        StringBuffer stringbuffer = new StringBuffer();
label0:
        do
        {
            int i;
label1:
            do
            {
                for(i = 0; i != -1;)
                {
                    i = readChar(bufferedreader, socket, l, printwriter1);
                    if(i != -1)
                    {
                        if(i != 255)
                        {
                            continue label1;
                        }
                        char c = readChar(bufferedreader, socket, l, printwriter1);
                        char c1 = readChar(bufferedreader, socket, l, printwriter1);
                        if(debug)
                        {
                            System.out.println("CMD=" + (int)c);
                            System.out.println("OPT=" + (int)c1);
                        }
                        if(c == '\375')
                        {
                            stringbuffer.append("NEG: IAC DO " + (int)c1 + "/IAC WONT " + (int)c1 + "\n");
                            writeChar(printwriter, '\377', printwriter1);
                            writeChar(printwriter, '\374', printwriter1);
                            writeChar(printwriter, c1, printwriter1);
                        } else
                        if(c == '\373')
                        {
                            stringbuffer.append("NEG: IAC WILL " + (int)c1 + "/IAC WONT " + (int)c1 + "\n");
                            writeChar(printwriter, '\377', printwriter1);
                            writeChar(printwriter, '\374', printwriter1);
                            writeChar(printwriter, c1, printwriter1);
                        } else
                        if(c == '\376')
                        {
                            stringbuffer.append("NEG: IAC DONT " + (int)c1 + "/IAC WONT " + (int)c1 + "\n");
                            writeChar(printwriter, '\377', printwriter1);
                            writeChar(printwriter, '\374', printwriter1);
                            writeChar(printwriter, c1, printwriter1);
                        } else
                        {
                            stringbuffer.append("NEG: IAC " + (int)c + " " + (int)c1 + "/<NORESP>\n");
                        }
                    }
                }

                break label0;
            } while(i == 0);
            stringbuffer.append(i);
            String s1 = stringbuffer.toString();
            if(s1.endsWith(s))
            {
                return s1;
            }
        } while(true);
        return "";
    }

    static char readChar(BufferedReader bufferedreader, Socket socket, long l, PrintWriter printwriter)
        throws IOException
    {
        int i = (int)(l - Platform.timeMillis());
        if(i <= 0)
        {
            throw new InterruptedIOException();
        }
        Platform.setSocketTimeout(socket, i);
        char c = (char)bufferedreader.read();
        if(debug)
        {
            System.out.println("GOT CHAR: " + c + " (" + (int)c + ")");
        }
        return c;
    }

    static void write(PrintWriter printwriter, String s, PrintWriter printwriter1)
    {
        if(debug)
        {
            System.out.println("Debug: Telnet OUT =" + s);
        }
        printwriter.print(s + URLMonitor.CRLF);
        printwriter.flush();
    }

    static void writeChar(PrintWriter printwriter, char c, PrintWriter printwriter1)
    {
        if(debug)
        {
            System.out.println("Debug: Telnet OUT Char =" + c + " (" + (int)c + ")");
        }
        printwriter.print(c);
        printwriter.flush();
    }

    public static void main(String args[])
        throws IOException
    {
        String s = args[0];
        Array array = new Array();
        for(int i = 1; i < args.length; i++)
        {
            array.add(args[i]);
        }

        StringBuffer stringbuffer = new StringBuffer();
        PrintWriter printwriter = FileUtils.MakeOutputWriter(System.out);
        checkTelnet(s, array, stringbuffer, Platform.timeMillis() + 15000L, printwriter);
        printwriter.flush();
        System.out.println("CONTENT BUFFER=\n" + stringbuffer);
    }

    static 
    {
        pServer = new StringProperty("_server", "");
        pServer.setDisplayText("Telnet Server", "the IP address or host name of the Telnet server (examples: 206.168.191.21 or demo." + Platform.exampleDomain + ")");
        pServer.setParameterOptions(true, 1, false);
        pUserName = new StringProperty("_username");
        pUserName.setDisplayText("User Name", "user name used to log in to the server via Telnet");
        pUserName.setParameterOptions(true, 2, false);
        pPassword = new StringProperty("_password");
        pPassword.setDisplayText("Password", "password use to log in to the server via Telnet");
        pPassword.setParameterOptions(true, 3, false);
        pPassword.isPassword = true;
        pCommand = new StringProperty("_command");
        pCommand.setDisplayText("Command", "optional command to run on the server");
        pCommand.setParameterOptions(true, 4, false);
        pLoginPrompt = new StringProperty("_loginPrompt", "login:");
        pLoginPrompt.setDisplayText("Login Prompt", "the label printed out when the remote system prompts for a login");
        pLoginPrompt.setParameterOptions(true, 5, true);
        pPasswordPrompt = new StringProperty("_passwordPrompt", "Password:");
        pPasswordPrompt.setDisplayText("Password Prompt", "the label printed out when the remote system prompts for a password");
        pPasswordPrompt.setParameterOptions(true, 5, true);
        pPrompt = new StringProperty("_prompt", "#");
        pPrompt.setDisplayText("Prompt", "the prompt printed out when the remote system is waiting for input");
        pPrompt.setParameterOptions(true, 5, true);
        pTimeout = new NumericProperty("_timeout", "60", "seconds");
        pTimeout.setDisplayText("Timeout", "the time out, seconds, to wait for the telnet connection to made and the command to complete");
        pTimeout.setParameterOptions(true, 5, true);
        pContentMatch = new StringProperty("_content");
        pContentMatch.setDisplayText("Match Content", "optional text to match against content of the file");
        pContentMatch.setParameterOptions(true, 8, true);
        pRoundTripTime = new NumericProperty("roundTripTime", "0", "milliseconds");
        pRoundTripTime.setLabel("round trip time");
        pRoundTripTime.setStateOptions(1);
        pStatus = new StringProperty("status", "no data");
        StringProperty astringproperty[] = {
            pServer, pTimeout, pCommand, pUserName, pPassword, pLoginPrompt, pPasswordPrompt, pPrompt, pContentMatch, pStatus, 
            pRoundTripTime
        };
        addProperties("COM.dragonflow.StandardMonitor.TelnetMonitor", astringproperty);
        addClassElement("COM.dragonflow.StandardMonitor.TelnetMonitor", Rule.stringToClassifier("status != 200\terror"));
        addClassElement("COM.dragonflow.StandardMonitor.TelnetMonitor", Rule.stringToClassifier("status == 200\tgood"));
        addClassElement("COM.dragonflow.StandardMonitor.TelnetMonitor", Rule.stringToClassifier("always\twarning", true));
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "description", "Connects to an Telnet server and verifies that a comand can be run.");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "help", "TelnetMon.htm");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "title", "Telnet");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "class", "TelnetMonitor");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "target", "_server");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "classType", "beta");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "topazName", "Telnet");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "topazType", "System Resources");
        setClassProperty("COM.dragonflow.StandardMonitor.TelnetMonitor", "loadable", "false");
        if(System.getProperty("TelnetMonitor.debug") != null)
        {
            debug = true;
        }
    }
}
