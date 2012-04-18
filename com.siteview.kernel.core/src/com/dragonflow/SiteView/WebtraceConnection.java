/*
 * 
 * Created on 2005-2-16 17:29:30
 *
 * WebtraceConnection.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>WebtraceConnection</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.dragonflow.Log.LogManager;
import com.dragonflow.Utils.TextUtils;

public class WebtraceConnection {

    private Socket socket;

    private OutputStream out;

    private InputStream in;

    private String host;

    private int port;

    private int timeout;

    public WebtraceConnection() {
        socket = null;
        out = null;
        in = null;
        getConfig();
    }

    public String connect() {
        String s = new String("");
        try {
            socket = new Socket(host, port);
            if (socket != null) {
                socket.setSoTimeout(timeout);
            }
            out = new BufferedOutputStream(socket.getOutputStream());
            in = new BufferedInputStream(socket.getInputStream());
        } catch (UnknownHostException unknownhostexception) {
            LogManager.log("Error", "UnknownHostException thrown in WebtraceConnection: " + unknownhostexception);
            LogManager.log("Error", "Could not find host: " + host);
            s = "UnknownHostException thrown in WebtraceConnection: " + unknownhostexception;
        } catch (SocketException socketexception) {
            LogManager.log("Error", "SocketException thrown in WebtraceConnection: " + socketexception);
            s = "SocketException thrown in WebtraceConnection: " + socketexception;
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioexception1) {
                LogManager.log("Error", "Unable to close socket");
            }
        } catch (InterruptedIOException interruptedioexception) {
            LogManager.log("Error", "InterruptedIOException thrown in WebtraceConnection: " + interruptedioexception);
            s = "InterruptedIOException thrown in WebtraceConnection: " + interruptedioexception;
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioexception2) {
                LogManager.log("Error", "Unable to close socket");
            }
        } catch (IOException ioexception) {
            LogManager.log("Error", "IOException thrown in WebtraceConnection: " + ioexception);
            s = "IOException thrown in WebtraceConnection: " + ioexception;
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioexception3) {
                LogManager.log("Error", "Unable to close socket");
            }
        } catch (Exception exception) {
            LogManager.log("Error", "Unknown exception thrown in WebtraceConnection: " + exception);
            s = "Unknown exception thrown in WebtraceConnection: " + exception;
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ioexception4) {
                LogManager.log("Error", "Unable to close socket");
            }
        }
        return s;
    }

    public void sendCommand(String s) {
        try {
            byte abyte0[] = TextUtils.stringToBytes(s + System.getProperty("line.separator"));
            out.write(abyte0);
            out.flush();
            LogManager.log("RunMonitor", "Webtrace Tool: Sent to " + host + ":" + port + " " + new String(abyte0));
        } catch (IOException ioexception) {
            LogManager.log("Error", "An exception occurred while sending request to webtrace server: " + ioexception);
        } catch (Exception exception) {
            LogManager.log("Error", "An exception occurred while sending request to webtrace server: " + exception);
        }
    }

    public String getResult() {
        StringBuffer stringbuffer = new StringBuffer();
        int i;
        try {
            do {
                byte abyte0[] = new byte[100];
                i = in.read(abyte0);
                stringbuffer.append(new String(abyte0));
            } while (i != -1);
        } catch (IOException ioexception) {
            LogManager.log("Error", "An exception occurred while getting result from webtrace server:" + ioexception);
        }
        return stringbuffer.toString();
    }

    private void getConfig() {
        port = 8008;
        host = "127.0.0.1";
        timeout = 0x1d4c0;
    }

    public void closeAll() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException ioexception) {
            LogManager.log("Error", "Error closing connection with webtrace server: " + ioexception);
        } catch (Exception exception) {
            LogManager.log("Error", "Unknown error closing connection with webtrace server: " + exception);
        }
    }
}
