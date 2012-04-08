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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.Enumeration;

import jgl.Array;

// Referenced classes of package COM.dragonflow.Utils:
// Base64Encoder, TextUtils, I18N, FileUtils

public class SMTP
{

    public static int defaultTimeout = 60000;
    public static jgl.Array noLogging = null;
    public static int defaultSMTPPort = 25;
    public static String CRLF = "\r\n";
    private String contentType;
    private String subjectCharSet;
    private java.io.BufferedReader reply;
    private java.io.PrintWriter send;
    private java.net.Socket server;
    private jgl.Array log;
    private String value;

    public SMTP(String s, int i, jgl.Array array)
        throws java.net.UnknownHostException, java.io.IOException
    {
        contentType = null;
        subjectCharSet = null;
        reply = null;
        send = null;
        server = null;
        log = null;
        value = "";
        log = array;
        int j = defaultSMTPPort;
        String s1 = s;
        int k = s1.indexOf(':');
        if(k != -1)
        {
            j = COM.dragonflow.Utils.TextUtils.readInteger(s1, k + 1);
            s1 = s1.substring(0, k);
        }
        if(array != null)
        {
            array.add("-->  connecting to " + s1 + ":" + j + ", timeout=" + i);
        }
        server = new Socket(s1, j);
        COM.dragonflow.SiteView.Platform.setSocketTimeout(server, i);
        InitMailEncoding();
        String s2 = readResult();
        if(!s2.startsWith("220"))
        {
            throw new ProtocolException(s2);
        } else
        {
            return;
        }
    }

    public void close()
    {
        try
        {
            sendData("QUIT");
            if(log != null)
            {
                log.add("<-- closing connection");
            }
            server.close();
        }
        catch(java.io.IOException ioexception)
        {
            if(log != null)
            {
                log.add("--> close error:" + ioexception);
            }
        }
    }

    private String formatContentType(String s)
    {
        if(s == null || s.length() == 0)
        {
            return "Content-Type: text/plain";
        } else
        {
            return "Content-Type: text/plain; charset=\"" + s + "\"";
        }
    }

    private String formatSubjectCharSet(String s)
    {
        return "=?" + s + "?B?<subject>?=";
    }

    private void InitMailEncoding()
        throws java.io.IOException
    {
        jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
        String s = null;
        String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_httpCharSet");
        if(s1 != null && s1.length() > 0)
        {
            contentType = formatContentType(s1);
            subjectCharSet = formatSubjectCharSet(s1);
            s = s1;
        }
        String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailCharSet");
        if(s2 != null && s2.length() > 0)
        {
            contentType = formatContentType(s2);
            s = s2;
        } else
        {
            if(contentType == null)
            {
                contentType = formatContentType(COM.dragonflow.Utils.I18N.isI18N ? "UTF-8" : null);
            }
            if(s == null)
            {
                s = COM.dragonflow.Utils.I18N.isI18N ? "UTF-8" : COM.dragonflow.Utils.I18N.getDefaultEncoding();
            }
        }
        reply = COM.dragonflow.Utils.FileUtils.MakeInputReader(server.getInputStream(), s);
        send = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(server.getOutputStream(), s);
        String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailSubjectCharSet");
        if(s3 != null && s3.length() > 0)
        {
            subjectCharSet = formatSubjectCharSet(s3);
        } else
        if(subjectCharSet == null)
        {
            subjectCharSet = COM.dragonflow.Utils.I18N.isI18N ? formatSubjectCharSet("UTF-8") : null;
        }
    }

    String encodeSubject(String s)
    {
        String s1 = s;
        if(subjectCharSet != null)
        {
            jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailSubjectCharSet");
            if(s2 == null || s2.equals(""))
            {
                s2 = COM.dragonflow.Utils.I18N.isI18N ? "UTF-8" : COM.dragonflow.Utils.I18N.getDefaultEncoding();
            }
            String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_mailjavaCharSet");
            COM.dragonflow.Utils.Base64Encoder base64encoder = new Base64Encoder(s, s2);
            s1 = base64encoder.processString();
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, "\n", "");
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(subjectCharSet, "<subject>", s1);
        }
        return s1;
    }

    public void send(String s, String s1, String s2, String s3, String s4)
        throws java.io.IOException, java.net.ProtocolException
    {
        send(s, s1, s2, s3, s4, null);
    }

    public void sendSecure(String s, String s1, String s2, String s3, String s4, String s5, String s6, 
            String s7)
        throws java.io.IOException, java.net.ProtocolException
    {
        s4 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s4);
        if(s3.length() > 0)
        {
            s3 = COM.dragonflow.Utils.I18N.isI18N ? new String(s3.getBytes("ISO-8859-1")) : s3;
        }
        String s8 = "";
        try
        {
            java.net.InetAddress inetaddress = java.net.InetAddress.getLocalHost();
            s8 = inetaddress.getHostName();
        }
        catch(java.net.UnknownHostException unknownhostexception) { }
        String s9 = "EHLO " + s8;
        sendCommandWithResult(s9);
        String s12 = value;
        if(s12.length() > 0 && s12.indexOf("AUTH") > 0)
        {
            if(s6.startsWith("["))
            {
                String s15 = s6.substring(1, s6.indexOf("]"));
                s6 = s6.substring(s15.length() + 2);
                s9 = "AUTH " + s15;
            } else
            {
                s9 = "AUTH LOGIN";
            }
            sendCommandWithResult(s9);
            String s13 = value;
            COM.dragonflow.Log.LogManager.log("RunMonitor", " value login " + s13);
            COM.dragonflow.Utils.Base64Encoder base64encoder = new Base64Encoder(s6);
            s9 = base64encoder.processString();
            sendCommandWithResult(s9);
            s13 = value;
            COM.dragonflow.Log.LogManager.log("RunMonitor", " value username " + s13);
            if(s7.length() > 0)
            {
                COM.dragonflow.Utils.Base64Encoder base64encoder1 = new Base64Encoder(s7);
                s9 = base64encoder1.processString();
                sendCommandWithResult(s9);
                String s14 = value;
                COM.dragonflow.Log.LogManager.log("RunMonitor", " value password " + s14);
            }
        }
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if(siteviewgroup.getSetting("_mailToFromRemoveBrackets").length() > 0)
        {
            s9 = "MAIL FROM: " + s;
        } else
        {
            s9 = "MAIL FROM:<" + s + ">";
        }
        sendCommand(s9);
        String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
        for(int i = 0; i < as.length; i++)
        {
            String s10;
            if(siteviewgroup.getSetting("_mailToFromRemoveBrackets").length() > 0)
            {
                s10 = "RCPT TO: " + as[i];
            } else
            {
                s10 = "RCPT TO:<" + as[i] + ">";
            }
            sendCommand(s10);
        }

        if(s2.length() > 0)
        {
            String as1[] = COM.dragonflow.Utils.TextUtils.split(s2, ",");
            for(int j = 0; j < as1.length; j++)
            {
                String s11;
                if(siteviewgroup.getSetting("_mailToFromRemoveBrackets").length() > 0)
                {
                    s11 = "RCPT TO: " + as1[j];
                } else
                {
                    s11 = "RCPT TO:<" + as1[j] + ">";
                }
                sendCommand(s11);
            }

        }
        sendData("DATA");
        String s16 = readResult();
        if(!s16.startsWith("354"))
        {
            throw new ProtocolException(s16);
        }
        String s17 = "[attachments]";
        String s18 = "";
        int k = s3.indexOf(s17);
        if(k != -1)
        {
            String s19 = s3.substring(k + s17.length());
            boolean flag = s19.indexOf("Related") != -1;
            s3 = s3.substring(0, k);
            sendData("From: " + s);
            sendData("To: " + s1);
            if(s2.length() > 0)
            {
                sendData("Cc: " + s2);
            }
            if(log != null)
            {
                log.add("--> Subject: " + s3);
            }
            sendData("Subject: " + encodeSubject(s3));
            sendData("X-Mailer: SiteView");
            sendData("MIME-Version: 1.0");
            if(flag)
            {
                String s20 = COM.dragonflow.Utils.I18N.isI18N ? " charset=\"UTF-8\"" : "";
                sendData("Content-Type: multipart/related; boundary=\"" + s19 + "\"; type=text/html" + s20);
            } else
            {
                sendData("Content-Type: multipart/mixed; boundary=\"" + s19 + "\"");
            }
        } else
        if(s3.indexOf("[skipheaders]") == -1)
        {
            sendData("From: " + s);
            sendData("To: " + s1);
            if(s2.length() > 0)
            {
                sendData("Cc: " + s2);
            }
            if(log != null)
            {
                log.add("--> Subject: " + s3);
            }
            sendData("Subject: " + encodeSubject(s3));
            sendData("X-Mailer: SiteView");
            if(contentType != null)
            {
                sendData(contentType);
            }
        }
        sendData("Date: " + COM.dragonflow.Utils.SMTP.RFCDateFormat(COM.dragonflow.SiteView.Platform.makeDate()));
        sendData("");
        sendData(COM.dragonflow.Utils.SMTP.convertNewlines(s4));
        if(s5 != null)
        {
            java.io.FileInputStream fileinputstream = new FileInputStream(s5);
            java.io.BufferedReader bufferedreader = COM.dragonflow.Utils.FileUtils.MakeInputReader(fileinputstream);
            do
            {
                String s21 = bufferedreader.readLine();
                if(s21 == null)
                {
                    break;
                }
                sendData(COM.dragonflow.Utils.SMTP.convertNewlines(s21));
            } while(true);
            bufferedreader.close();
        }
        sendCommand(".");
    }

    public void send(String s, String s1, String s2, String s3, String s4, String s5)
        throws java.io.IOException, java.net.ProtocolException
    {
        s4 = COM.dragonflow.Utils.I18N.toDefaultEncoding(s4);
        if(s3.length() > 0)
        {
            s3 = COM.dragonflow.Utils.I18N.isI18N ? new String(s3.getBytes("ISO-8859-1")) : s3;
        }
        String s6 = "";
        try
        {
            java.net.InetAddress inetaddress = java.net.InetAddress.getLocalHost();
            s6 = inetaddress.getHostName();
        }
        catch(java.net.UnknownHostException unknownhostexception) { }
        String s7 = "HELO " + s6;
        sendCommand(s7);
        COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
        if(siteviewgroup.getSetting("_mailToFromRemoveBrackets").length() > 0)
        {
            s7 = "MAIL FROM: " + s;
        } else
        {
            s7 = "MAIL FROM:<" + s + ">";
        }
        sendCommand(s7);
        String as[] = COM.dragonflow.Utils.TextUtils.split(s1, ",");
        for(int i = 0; i < as.length; i++)
        {
            try
            {
                String s8;
                if(siteviewgroup.getSetting("_mailToFromRemoveBrackets").length() > 0)
                {
                    s8 = "RCPT TO: " + as[i];
                } else
                {
                    s8 = "RCPT TO:<" + as[i] + ">";
                }
                sendCommand(s8);
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("Error", "Recipient: " + as[i] + ", has error: " + exception.toString());
            }
        }

        if(s2.length() > 0)
        {
            String as1[] = COM.dragonflow.Utils.TextUtils.split(s2, ",");
            for(int j = 0; j < as1.length; j++)
            {
                try
                {
                    String s9;
                    if(siteviewgroup.getSetting("_mailToFromRemoveBrackets").length() > 0)
                    {
                        s9 = "RCPT TO: " + as1[j];
                    } else
                    {
                        s9 = "RCPT TO:<" + as1[j] + ">";
                    }
                    sendCommand(s9);
                }
                catch(java.lang.Exception exception1)
                {
                    COM.dragonflow.Log.LogManager.log("Error", "Recipient: " + as1[j] + ", has error: " + exception1.toString());
                }
            }

        }
        sendData("DATA");
        String s10 = readResult();
        if(!s10.startsWith("354"))
        {
            throw new ProtocolException(s10);
        }
        String s11 = "[attachments]";
        String s12 = "";
        int k = s3.indexOf(s11);
        if(k != -1)
        {
            String s13 = s3.substring(k + s11.length());
            boolean flag = s13.indexOf("Related") != -1;
            s3 = s3.substring(0, k);
            sendData("From: " + s);
            sendData("To: " + s1);
            if(s2.length() > 0)
            {
                sendData("Cc: " + s2);
            }
            if(log != null)
            {
                log.add("--> Subject: " + s3);
            }
            sendData("Subject: " + encodeSubject(s3));
            sendData("X-Mailer: SiteView");
            sendData("MIME-Version: 1.0");
            if(flag)
            {
                sendData("Content-Type: multipart/related; boundary=\"" + s13 + "\"; type=text/html");
            } else
            {
                sendData("Content-Type: multipart/mixed; boundary=\"" + s13 + "\"");
            }
        } else
        if(s3.indexOf("[skipheaders]") == -1)
        {
            sendData("From: " + s);
            sendData("To: " + s1);
            if(s2.length() > 0)
            {
                sendData("Cc: " + s2);
            }
            if(log != null)
            {
                log.add("--> Subject: " + s3);
            }
            sendData("Subject: " + encodeSubject(s3));
            sendData("X-Mailer: SiteView");
            if(contentType != null)
            {
                sendData(contentType);
            }
        }
        sendData("Date: " + COM.dragonflow.Utils.SMTP.RFCDateFormat(COM.dragonflow.SiteView.Platform.makeDate()));
        sendData("");
        sendData(COM.dragonflow.Utils.SMTP.convertNewlines(s4));
        if(s5 != null)
        {
            java.io.FileInputStream fileinputstream = new FileInputStream(s5);
            java.io.BufferedReader bufferedreader = COM.dragonflow.Utils.FileUtils.MakeInputReader(fileinputstream);
            do
            {
                String s14 = bufferedreader.readLine();
                if(s14 == null)
                {
                    break;
                }
                sendData(COM.dragonflow.Utils.SMTP.convertNewlines(s14));
            } while(true);
            bufferedreader.close();
        }
        sendCommand(".");
    }

    String readResult()
    {
        value = "";
        String s;
        try
        {
            s = reply.readLine();
            if(s == null)
            {
                throw new IOException("error reading from SMTP server");
            }
            if(log != null)
            {
                log.add("<-- " + s);
            }
            for(value += s; !COM.dragonflow.Utils.TextUtils.isStatusLine(s); value += s)
            {
                s = reply.readLine();
                if(s == null)
                {
                    throw new IOException("error reading from SMTP server");
                }
                if(log != null)
                {
                    log.add("<-- " + s);
                }
            }

        }
        catch(java.io.IOException ioexception)
        {
            s = "" + ioexception;
        }
        return s;
    }

    void sendData(String s)
    {
        s = s + CRLF;
        send.print(s);
        send.flush();
        if(log != null && s.indexOf("Subject: =?") == -1)
        {
            log.add("--> " + s);
        }
    }

    String sendCommandWithResult(String s)
        throws java.net.ProtocolException, java.io.IOException
    {
        sendData(s);
        String s1 = readResult();
        if(!s1.startsWith("250") && !s1.startsWith("334") && !s1.startsWith("235"))
        {
            throw new ProtocolException(s1);
        } else
        {
            return s1;
        }
    }

    void sendCommand(String s)
        throws java.net.ProtocolException, java.io.IOException
    {
        sendData(s);
        String s1 = readResult();
        if(!s1.startsWith("250"))
        {
            throw new ProtocolException(s1);
        } else
        {
            return;
        }
    }

    static String twoDigits(int i)
    {
        if(i < 10)
        {
            return "0" + i;
        } else
        {
            return "" + i;
        }
    }

    static String convertNewlines(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length() + 100);
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(c == '\r')
            {
                continue;
            }
            if(c == '\n')
            {
                stringbuffer.append(CRLF);
            } else
            {
                stringbuffer.append(c);
            }
        }

        return stringbuffer.toString();
    }

    static String RFCDateFormat(java.util.Date date)
    {
        String s;
        if(date.getTimezoneOffset() < 0)
        {
            s = "+";
        } else
        {
            s = "-";
        }
        String s1;
        if(date.getYear() > 99)
        {
            s1 = String.valueOf(date.getYear() + 1900);
        } else
        {
            s1 = String.valueOf(date.getYear());
        }
        String as[] = {
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
        };
        String as1[] = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", 
            "Nov", "Dec"
        };
        return as[date.getDay()] + ", " + String.valueOf(date.getDate()) + " " + as1[date.getMonth()] + " " + s1 + " " + COM.dragonflow.Utils.SMTP.twoDigits(date.getHours()) + ":" + COM.dragonflow.Utils.SMTP.twoDigits(date.getMinutes()) + ":" + COM.dragonflow.Utils.SMTP.twoDigits(date.getSeconds()) + " " + s + COM.dragonflow.Utils.SMTP.twoDigits(java.lang.Math.abs(date.getTimezoneOffset()) / 60) + COM.dragonflow.Utils.SMTP.twoDigits(java.lang.Math.abs(date.getTimezoneOffset()) % 60);
    }

    public static void main(String args[])
        throws java.lang.Exception
    {
        jgl.Array array = new Array();
        String s = "siteseer1.dragonflowinteractive.com";
        String s1 = "siteviewtest@siteview.com";
        String s2 = "siteviewtest@siteview.com";
        String s3 = "";
        String s4 = "this is a two line message\nsecond line";
        String s5 = "test";
        int i = defaultTimeout;
        if(args.length > 0)
        {
            s5 = args[0];
        }
        if(args.length > 1)
        {
            s = args[1];
        }
        if(args.length > 2)
        {
            s1 = args[2];
        }
        if(args.length > 3)
        {
            i = COM.dragonflow.Utils.TextUtils.toInt(args[3]);
        }
        try
        {
            COM.dragonflow.Utils.SMTP smtp = new SMTP(s, i, array);
            smtp.send(s2, s1, s3, s5, s4, null);
            smtp.close();
        }
        catch(java.lang.Exception exception)
        {
            java.lang.System.out.println(exception + "\n");
        }
        for(Enumeration enumeration = array.elements(); enumeration.hasMoreElements(); java.lang.System.out.println(enumeration.nextElement())) { }
    }

}
