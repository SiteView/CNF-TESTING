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

import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

// Referenced classes of package COM.dragonflow.Utils:
// popStatus, TextUtils, FileUtils, I18N

public class pop3 {

    final int AUTHORIZATION = 1;

    final int TRANSACTION = 2;

    final int UPDATE = 3;

    protected int _TotalMsgs;

    protected int _TotalSize;

    protected boolean _StatusOK;

    protected int State;

    protected String Host;

    protected String User;

    protected String Password;

    protected int Port;

    java.net.Socket socket;

    java.io.BufferedReader in;

    java.io.PrintWriter out;

    public int socketTimeout;

    public java.io.PrintWriter output;

    public COM.dragonflow.SiteView.AtomicMonitor monitor;

    static String CRLF = "\r\n";

    public pop3(String s, String s1, String s2, COM.dragonflow.SiteView.AtomicMonitor atomicmonitor, java.io.PrintWriter printwriter) {
        _TotalMsgs = 0;
        _TotalSize = 0;
        _StatusOK = false;
        State = 0;
        Host = null;
        User = null;
        Password = null;
        Port = 110;
        socket = null;
        in = null;
        out = null;
        socketTimeout = 0x493e0;
        output = null;
        monitor = null;
        int i = s.indexOf(':');
        if (i != -1) {
            Port = COM.dragonflow.Utils.TextUtils.readInteger(s, i + 1);
            s = s.substring(0, i);
        }
        Host = s;
        User = s1;
        Password = s2;
        monitor = atomicmonitor;
        output = printwriter;
    }

    public synchronized COM.dragonflow.Utils.popStatus connect() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        debug("--> connecting to " + Host + ":" + Port);
        if (Host == null) {
            popstatus._Response = "-ERR Host not specified";
            popstatus._OK = false;
            return popstatus;
        }
        try {
            socket = new Socket(Host, Port);
            if (socket == null) {
                debug("<-- -ERR could not connect to server");
                popstatus._OK = false;
                popstatus._Response = "-ERR Error while connecting to POP3 server";
            } else {
                debug("<-- connected");
                in = COM.dragonflow.Utils.FileUtils.MakeInputReader(socket.getInputStream());
                out = COM.dragonflow.Utils.FileUtils.MakeOutputWriter(socket.getOutputStream());
            }
        } catch (java.lang.Exception exception) {
            String s = "-ERR " + exception.toString();
            debug("<-- " + s);
            popstatus._OK = false;
            popstatus._Response = s;
            socket = null;
        }
        if (socket != null) {
            popstatus._OK = true;
            _StatusOK = true;
            popstatus._Response = recv();
            Parse(popstatus, 2);
            if (popstatus._OK) {
                State = 1;
            }
        }
        return popstatus;
    }

    public COM.dragonflow.Utils.popStatus login(String s, String s1) {
        User = s;
        Password = s1;
        return login();
    }

    public synchronized COM.dragonflow.Utils.popStatus login() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        if (User == null || Password == null) {
            popstatus._Response = "-ERR Userid or Password not specified";
            return popstatus;
        }
        send("USER " + User);
        popstatus._Response = recv();
        Parse(popstatus, 1);
        if (popstatus._OK) {
            send("PASS " + Password);
            popstatus._Response = recv();
            Parse(popstatus, 1);
            if (popstatus._OK) {
                State = 2;
                stat();
            }
        }
        return popstatus;
    }

    public synchronized void close() {
        debug("--> closing connection");
        try {
            if (socket != null) {
                socket.close();
            }
            State = 0;
        } catch (java.io.IOException ioexception) {
            debug("Failure in Server.close()");
        }
    }

    public synchronized COM.dragonflow.Utils.popStatus stat() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        if (State != 2) {
            popstatus._Response = "-ERR Server not in Transaction mode";
            return popstatus;
        }
        send("STAT");
        popstatus._Response = recv();
        String as[] = Parse(popstatus, 4);
        if (popstatus._OK) {
            _TotalMsgs = java.lang.Integer.parseInt(as[1]);
            _TotalSize = java.lang.Integer.parseInt(as[2]);
        }
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus quit() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        if (socket != null) {
            send("QUIT");
            State = 3;
            popstatus._Response = recv();
            Parse(popstatus, 2);
        }
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus list(int i) {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("LIST " + i);
        popstatus._Response = recv();
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus list() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("LIST");
        recvN(popstatus);
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus uidl(int i) {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("UIDL " + i);
        popstatus._Response = recv();
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus uidl() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("UIDL");
        recvN(popstatus);
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus retr(int i) {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("RETR " + i);
        recvN(popstatus);
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus top(int i, int j) {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("TOP " + i + " " + j);
        recvN(popstatus);
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus dele(int i) {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("DELE " + i);
        popstatus._Response = recv();
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus rset() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("RSET");
        popstatus._Response = recv();
        Parse(popstatus, 2);
        return popstatus;
    }

    public synchronized COM.dragonflow.Utils.popStatus noop() {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("NOOP");
        popstatus._Response = recv();
        Parse(popstatus, 2);
        return popstatus;
    }

    public int get_TotalMsgs() {
        return _TotalMsgs;
    }

    public int get_TotalSize() {
        return _TotalSize;
    }

    public synchronized COM.dragonflow.Utils.popStatus appendFile(String s, int i) {
        COM.dragonflow.Utils.popStatus popstatus = new popStatus();
        send("RETR " + i);
        recvN(popstatus);
        Parse(popstatus, 2);
        if (popstatus._OK) {
            java.io.RandomAccessFile randomaccessfile;
            try {
                randomaccessfile = new RandomAccessFile(s, "rw");
            } catch (java.io.IOException ioexception) {
                popstatus._OK = false;
                popstatus._Response = "-ERR File open failed";
                return popstatus;
            }
            java.util.Date date = new Date();
            String as[] = popstatus.Responses();
            try {
                randomaccessfile.seek(randomaccessfile.length());
                randomaccessfile.writeBytes("From - " + date.toString() + "\r\n");
                for (int j = 0; j < as.length; j ++) {
                    randomaccessfile.writeBytes(as[j] + "\r\n");
                }

                randomaccessfile.close();
            } catch (java.io.IOException ioexception1) {
                popstatus._OK = false;
                popstatus._Response = "-ERR File write failed";
                return popstatus;
            }
        }
        popstatus._OK = true;
        return popstatus;
    }

    String[] Parse(COM.dragonflow.Utils.popStatus popstatus, int i) {
        String as[] = null;
        popstatus._OK = false;
        String s = popstatus._Response;
        if (s != null) {
            int j = 0;
            if (s.trim().startsWith("+OK")) {
                popstatus._OK = true;
            } else {
                debug("<-- ERROR STATUS");
            }
            java.util.StringTokenizer stringtokenizer = new StringTokenizer(s);
            int k;
            if (i == -1) {
                k = stringtokenizer.countTokens();
            } else {
                k = i;
            }
            as = new String[k + 1];
            for (; stringtokenizer.hasMoreTokens() && j < k; j ++) {
                as[j] = new String(stringtokenizer.nextToken());
            }

            if (stringtokenizer.hasMoreTokens()) {
                StringBuffer stringbuffer = new StringBuffer(stringtokenizer.nextToken());
                for (; stringtokenizer.hasMoreTokens(); stringbuffer.append(" " + stringtokenizer.nextToken())) {
                }
                as[k] = new String(stringbuffer);
            }
        }
        return as;
    }

    void send(String s) {
        if (s.startsWith("PASS")) {
            debug("--> PASS ********");
        } else {
            debug("--> " + s);
        }
        try {
            out.print(s + CRLF);
            out.flush();
            _StatusOK = true;
        } catch (java.lang.Exception exception) {
            java.lang.System.err.println("Send: Unexpected exception: " + exception.toString());
            _StatusOK = false;
        }
    }

    String recv() {
        String s = "";
        if (!_StatusOK) {
            s = "-ERR Failed sending command to Server";
            debug("<-- " + s);
            return s;
        }
        COM.dragonflow.SiteView.Platform.setSocketTimeout(socket, socketTimeout);
        try {
            s = in.readLine();
        } catch (java.io.IOException ioexception) {
            java.lang.System.err.println("Caught exception while reading");
            s = "-ERR Caught IOException while reading from server";
        } catch (java.lang.Exception exception) {
            java.lang.System.err.println("Unexpected exception: " + exception.toString());
            s = "-ERR Unexpected exception while reading from server";
        }
        String s1 = "";
        if (s == null) {
            s1 = "-ERR <NULL>";
        } else if (s.trim().startsWith("-ERR")) {
            s1 = s;
        } else if (s.trim().startsWith("+OK")) {
            s1 = s;
        } else {
            debug("<-- " + s);
            s1 = "-ERR Invalid response";
        }
        debug("<-- " + s1);
        return s1;
    }

    void recvN(COM.dragonflow.Utils.popStatus popstatus) {
        java.util.Vector vector = new Vector(100, 100);
        String s = "";
        try {
            COM.dragonflow.SiteView.Platform.setSocketTimeout(socket, socketTimeout);
            boolean flag = false;
            int i = 0;
            while (!flag) {
                String s1 = COM.dragonflow.Utils.I18N.UnicodeToString(in.readLine(), COM.dragonflow.Utils.I18N.nullEncoding());
                i ++;
                debug("<-- '" + s1 + "'");
                if (i == 1) {
                    if (s1.trim().startsWith("-ERR ")) {
                        flag = true;
                        popstatus._Response = s1;
                    } else if (s1.trim().startsWith("+OK")) {
                        popstatus._Response = s1;
                    } else {
                        flag = true;
                        popstatus._Response = "-ERR Invalid response";
                    }
                } else if (s1.startsWith(".")) {
                    if (s1.length() == 1) {
                        flag = true;
                    } else {
                        vector.addElement(s1.substring(1));
                    }
                } else {
                    vector.addElement(s1);
                }
            }
            debug("<-- " + i + " lines");
        } catch (java.io.IOException ioexception) {
            java.lang.System.err.println("Caught exception while reading");
            popstatus._Response = "-ERR Caught IOException while reading from server";
        } catch (java.lang.Exception exception) {
            java.lang.System.err.println("Unexpected exception: " + exception.toString());
            popstatus._Response = "-ERR Unexpected exception while reading from server";
        }
        popstatus._Responses = new String[vector.size()];
        vector.copyInto(popstatus._Responses);
    }

    public void debug(String s) {
        if (output != null) {
            output.println(s + "\n");
            output.flush();
        }
        if (monitor != null) {
            monitor.progressString += s + "\n";
        }
    }

}
