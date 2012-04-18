/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.HTTP;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;

// Referenced classes of package com.dragonflow.HTTP:
// HTTPRequestException, HTTPRequest, HTTPServer

public class BinaryFileRequestHandler {

    public static final String FILE = "file";

    public static final String MACHINE = "machine";

    public static final String GROUP = "group";

    public static final String OPERATION = "operation";

    public static final String GET_COMPRESSED_FILE = "getFileCompressed";

    private static java.util.Random rand = null;

    com.dragonflow.HTTP.HTTPServer httpServer;

    public BinaryFileRequestHandler(com.dragonflow.HTTP.HTTPServer httpserver) {
        httpServer = httpserver;
    }

    public void handleRequest(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.OutputStream outputstream) throws java.lang.Exception {
        if (httprequest.getURL().indexOf("go.exe") < 0) {
            throw new HTTPRequestException(404, httprequest.getURL());
        }
        String s = com.dragonflow.Utils.I18N.toDefaultEncoding(httprequest.getValue("group"));
        if (s.length() > 0 && !com.dragonflow.Page.CGI.isGroupAllowedForAccount(s, httprequest)) {
            throw new HTTPRequestException(557);
        }
        com.dragonflow.HTTP.HTTPRequest _tmp = httprequest;
        com.dragonflow.HTTP.HTTPRequest.noCache = true;
        try {
            printCGIHeader(httprequest, outputstream);
            if (httprequest.getValue("operation").startsWith("getFileCompressed")) {
                printBodyForCompressed(httprequest, outputstream);
            } else {
                printBody(httprequest, outputstream);
            }
            printCGIFooter(httprequest, outputstream);
        } catch (java.io.IOException ioexception) {
            com.dragonflow.Log.LogManager.log("Error", "Exception writing binary data to socket.\n");
            ioexception.printStackTrace();
            throw ioexception;
        }
    }

    public void printBody(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.OutputStream outputstream) throws java.io.IOException {
        String s = httprequest.getValue("file");
        String s1 = com.dragonflow.SiteView.Platform.getRoot();
        s1 = s1 + s;
        try {
            boolean flag = !s.startsWith("/cache") && !s.startsWith("/groups") && !s.startsWith("/htdocs") && !s.startsWith("/logs") && !s.startsWith("/scripts") && !s.startsWith("/templates");
            if (s.startsWith("/accounts") && httprequest.getAccount().equals("administrator")) {
                flag = false;
            }
            if (flag) {
                throw new Exception("access not allowed");
            }
            if (s.indexOf("..") >= 0) {
                throw new Exception("access not allowed");
            }
            if (s.startsWith(".")) {
                throw new Exception("access not allowed");
            }
            com.dragonflow.Utils.FileUtils.copyBinaryFileToStream(s1, outputstream);
        } catch (java.lang.Exception exception) {
            outputstream.write(com.dragonflow.Utils.TextUtils.convertStringToBytes("error: " + exception + ", " + s + ", " + s1));
        }
    }

    /**
     * 
     * 
     * @param httprequest
     * @param outputstream
     * @throws java.io.IOException
     * @throws com.dragonflow.HTTP.HTTPRequestException
     */
    public void printBodyForCompressed(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.OutputStream outputstream) throws java.io.IOException, com.dragonflow.HTTP.HTTPRequestException {

        String s6;
        String s = httprequest.getValue("file");
        String s1 = httprequest.getValue("machine");
        com.dragonflow.SiteView.Machine machine = null;

        if (s1.equals("") || com.dragonflow.SiteView.Machine.isLocalHostname(s1)) {
            getCompressedFileLocal(com.dragonflow.SiteView.Platform.makeAbsolutePath(s), outputstream, null);
            return;
        }

        else if (com.dragonflow.SiteView.Machine.isNetBIOSFormattedHostname(s1) || s.startsWith("\\\\")) {
            String s2 = com.dragonflow.SiteView.Platform.closeAndConnectNetBIOSIfRemoteDefined(s);
            if (s2.length() > 0) {
                throw new HTTPRequestException(400, httprequest.getURL());
            } else {
                getCompressedFileLocal(s, outputstream, null);
                return;
            }
        }

        else if ((machine = com.dragonflow.SiteView.Machine.getMachineByHost(s1)) != null) {
            String s3 = com.dragonflow.SiteView.Machine.REMOTE_PREFIX + machine.getProperty(com.dragonflow.SiteView.Machine.pID);
            HashMap hashmap = new HashMap();
            hashmap.put("file", s);
            String s4 = com.dragonflow.SiteView.Machine.getCommandString("catFile", s3, hashmap);
            Array array = null;
            com.dragonflow.Utils.RemoteCommandLine remotecommandline = null;
            remotecommandline = com.dragonflow.SiteView.Machine.getRemoteCommandLine(machine);
            if (rand == null) {
                rand = new Random();
            }
            String s5 = "siebelTemp_" + (new Date()).getTime() + "_" + rand.nextInt() + ".tmp";
            s6 = com.dragonflow.Utils.TempFileManager.getTempAccordingToSizeDirPath() + java.io.File.separator + s5;
            s4 = com.dragonflow.Utils.RemoteCommandLine.mangleCmdStringForFileGet(s4, s6);
            array = remotecommandline.exec(s4, machine);
            if (array != null && remotecommandline.exitValue == 0 && array.size() > 0) {
                com.dragonflow.Utils.RemoteCommandLine _tmp = remotecommandline;
                if (array.get(0).equals(" sIiTeSsCoPeReDiReCTtOkEN* ")) {
                    getCompressedFileLocal(s6, outputstream, "cat: cannot open");
                }
            } else {
                (new File(s6)).delete();
                if (remotecommandline.exitValue == com.dragonflow.SiteView.Monitor.kURLTimeoutError) {
                    throw new HTTPRequestException(400, "Request timed out: " + httprequest.getURL());
                } else {
                    throw new HTTPRequestException(400, httprequest.getURL());
                }
            }
            (new File(s6)).delete();
        } else {
            throw new HTTPRequestException(400, httprequest.getURL());
        }
    }

    private void getCompressedFileLocal(String s, java.io.OutputStream outputstream, String s1) throws java.io.IOException, com.dragonflow.HTTP.HTTPRequestException {
        boolean flag = true;
        java.util.zip.GZIPOutputStream gzipoutputstream = new GZIPOutputStream(outputstream);
        java.io.FileInputStream fileinputstream = new FileInputStream(s);
        byte abyte0[] = new byte[1024];
        int i;
        for (; (i = fileinputstream.read(abyte0)) > 0; gzipoutputstream.write(abyte0, 0, i)) {
            if (s1 == null) {
                continue;
            }
            if (flag && !checkForErrors(abyte0, s1)) {
                fileinputstream.close();
                throw new HTTPRequestException(400, s);
            }
            flag = false;
        }

        fileinputstream.close();
        gzipoutputstream.finish();
        gzipoutputstream.close();
    }

    private boolean checkForErrors(byte abyte0[], String s) {
        int i = s.length();
        if (i == 0) {
            return true;
        } else {
            String s1 = new String(abyte0, 0, i + 10);
            return !s1.startsWith(s);
        }
    }

    public void printCGIHeader(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.OutputStream outputstream) throws java.io.IOException {
        String s = httprequest.generateHeader();
        outputstream.write(com.dragonflow.Utils.TextUtils.convertStringToBytes(s));
    }

    public void printCGIFooter(com.dragonflow.HTTP.HTTPRequest httprequest, java.io.OutputStream outputstream) throws java.io.IOException {
        outputstream.flush();
    }

}
