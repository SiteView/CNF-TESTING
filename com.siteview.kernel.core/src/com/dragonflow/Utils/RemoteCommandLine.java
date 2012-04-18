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

import java.io.InterruptedIOException;

import com.recursionsw.jgl.Array;

// Referenced classes of package com.dragonflow.Utils:
// I18N

public class RemoteCommandLine
{

    public int exitValue;
    public boolean debug;
    public boolean detail;
    protected String encoding;
    public java.io.PrintWriter progressStream;
    public static final String SS_REDIRECT = " sIiTeSsCoPeReDiReCTtOkEN* ";
    public static int startPort;
    public static int endPort = 1022;
    public static int skipPorts[] = {
        888, 996, 997, 998, 999, 1000
    };
    public static int localPortRetryCount = 10;
    public static int currentPort;
    public static boolean TO_REMOTE = true;
    public static boolean FROM_REMOTE = false;
    int receiveBufferSize;
    byte receiveBytes[];
    int currentChar;
    int readBytes;

    public RemoteCommandLine()
    {
        exitValue = 0;
        debug = false;
        detail = false;
        encoding = com.dragonflow.Utils.I18N.getDefaultEncoding();
        progressStream = null;
        receiveBufferSize = 1024;
        receiveBytes = new byte[receiveBufferSize];
        currentChar = 0;
        readBytes = -1;
    }

    protected String toEncoding(String s)
    {
        return com.dragonflow.Utils.I18N.StringToUnicode(s, encoding);
    }

    public Array exec(String s, com.dragonflow.SiteView.Machine machine)
    {
        return exec(s, machine, false);
    }

    public Array test(String s, com.dragonflow.SiteView.Machine machine, boolean flag)
    {
        detail = flag;
        return exec(s, machine, true);
    }

    public Array exec(String s, com.dragonflow.SiteView.Machine machine, boolean flag)
    {
        com.dragonflow.SiteView.Machine _tmp = machine;
        encoding = machine.getProperty(com.dragonflow.SiteView.Machine.pEncoding);
        if(encoding.length() == 0)
        {
            encoding = com.dragonflow.Utils.I18N.getDefaultEncoding();
        }
        return new Array();
    }

    public Array exec(String as[], com.dragonflow.SiteView.Machine machine)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < as.length; i++)
        {
            boolean flag = i > 0 && !as[i].startsWith("\"");
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append(" ");
            }
            if(flag)
            {
                stringbuffer.append("\"");
            }
            stringbuffer.append(as[i]);
            if(flag)
            {
                stringbuffer.append("\"");
            }
        }

        return exec(stringbuffer.toString(), machine);
    }

    public String getMethodName()
    {
        return "unknown";
    }

    public String getMethodDisplayName()
    {
        return "Unknown";
    }

    public static java.net.Socket getReservedPort(String s, int i)
        throws java.io.IOException
    {
        int j = com.dragonflow.Utils.RemoteCommandLine.getLocalPort();
        int k = 0;
        java.net.Socket socket;
        for(socket = null; socket == null;)
        {
            k++;
            try
            {
                socket = com.dragonflow.SiteView.PlatformNew.createSocketOnLocalPort(s, i, j);
            }
            catch(java.io.IOException ioexception)
            {
                if(k > localPortRetryCount)
                {
                    throw ioexception;
                }
                java.lang.System.out.println("Socket open port " + j + " : " + ioexception);
                j = com.dragonflow.Utils.RemoteCommandLine.getLocalPort();
            }
        }

        return socket;
    }

    public static synchronized int getLocalPort()
    {
        int i = com.dragonflow.Utils.RemoteCommandLine.nextPort(currentPort);
        currentPort = i;
        return i;
    }

    public static int nextPort(int i)
    {
        do
        {
            if(++i > endPort)
            {
                i = startPort;
            }
        } while(com.dragonflow.Utils.RemoteCommandLine.skipPort(i));
        return i;
    }

    public static boolean skipPort(int i)
    {
        for(int j = 0; j < skipPorts.length; j++)
        {
            if(skipPorts[j] == i)
            {
                return true;
            }
        }

        return false;
    }

    public void traceMessage(String as[], com.dragonflow.SiteView.Machine machine, boolean flag)
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < as.length; i++)
        {
            if(stringbuffer.length() > 0)
            {
                stringbuffer.append("\n");
            }
            stringbuffer.append("[" + i + "]" + as[i]);
        }

        traceMessage(stringbuffer.toString(), machine, flag);
    }

    public void traceMessage(Array array, com.dragonflow.SiteView.Machine machine, boolean flag)
    {
        com.dragonflow.SiteView.Machine _tmp = machine;
        if(machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace))
        {
            for(int i = 0; i < array.size(); i++)
            {
                traceMessage((String)array.get(i), machine, flag);
            }

        }
    }

    public void traceMessage(String s, com.dragonflow.SiteView.Machine machine, boolean flag)
    {
        com.dragonflow.SiteView.Machine _tmp = machine;
        if(machine.getPropertyAsBoolean(com.dragonflow.SiteView.Machine.pTrace))
        {
            String s1 = "rem: " + machine.getProperty(com.dragonflow.SiteView.Machine.pName) + " ";
            if(flag)
            {
                s1 = s1 + "-->";
            } else
            {
                s1 = s1 + "<--";
            }
            if(com.dragonflow.Log.LogManager.loggerRegistered("RunMonitor"))
            {
                com.dragonflow.Log.LogManager.log("RunMonitor", s1 + s);
            } else
            {
                java.lang.System.out.println(s1 + s);
            }
        }
    }

    public void progressMessage(String s)
    {
        if(progressStream != null)
        {
            if(detail)
            {
                progressStream.print("<BR><B>");
            }
            progressStream.print(s);
            if(detail)
            {
                progressStream.print("</B><BR>");
            }
            progressStream.println("");
            progressStream.flush();
        }
        if(debug)
        {
            java.lang.System.out.println(com.dragonflow.SiteView.Platform.timeMillis() + "  " + s);
        }
    }

    void resetBuffer()
    {
        receiveBytes = new byte[receiveBufferSize];
        currentChar = 0;
        readBytes = -1;
    }

    char readChar(java.io.BufferedInputStream bufferedinputstream, java.net.Socket socket, long l)
        throws java.io.IOException
    {
        if(currentChar >= readBytes)
        {
            readBytes = readBuffer(receiveBytes, bufferedinputstream, socket, l);
            if(readBytes == -1)
            {
                return (char)readBytes;
            }
            currentChar = 0;
        }
        int i = receiveBytes[currentChar++] & 0xff;
        return (char)i;
    }

    int readBuffer(byte abyte0[], java.io.BufferedInputStream bufferedinputstream, java.net.Socket socket, long l)
        throws java.io.IOException
    {
        if(socket != null)
        {
            int i = (int)(l - com.dragonflow.SiteView.Platform.timeMillis());
            if(i <= 0)
            {
                throw new InterruptedIOException();
            }
            com.dragonflow.SiteView.Platform.setSocketTimeout(socket, i);
        }
        return bufferedinputstream.read(abyte0);
    }

    public static String mangleCmdStringForFileGet(String s, String s1)
    {
        return s + " sIiTeSsCoPeReDiReCTtOkEN* " + s1 + " sIiTeSsCoPeReDiReCTtOkEN* ";
    }

    public static String unMangleCmdStringForFileGet(String s)
    {
        return s.substring(0, s.indexOf(" sIiTeSsCoPeReDiReCTtOkEN* ")).trim();
    }

    public static String extractDestFileIfAny(String s)
    {
        Object obj = null;
        int i = s.indexOf(" sIiTeSsCoPeReDiReCTtOkEN* ");
        if(i < 0)
        {
            return null;
        }
        int j = i + " sIiTeSsCoPeReDiReCTtOkEN* ".length();
        int k = s.indexOf(" sIiTeSsCoPeReDiReCTtOkEN* ", j);
        if(k < 0)
        {
            return null;
        } else
        {
            String s1 = s.substring(j, k).trim();
            return s1;
        }
    }

    static 
    {
        startPort = 810;
        currentPort = startPort;
    }
}
