/*
 * 
 * Created on 2005-2-16 15:06:45
 *
 * ExchangeToolBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>ExchangeToolBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import jgl.Array;
import jgl.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.Pair;
import COM.dragonflow.Utils.TempFileManager;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor

public abstract class ExchangeToolBase extends AtomicMonitor {

    protected static StringProperty pStatus;

    protected static StringProperty pReportingDir;

    public static String REPORT_FILE_DATE_FORMAT;

    private static DateFormat mFileDateFormat;

    public ExchangeToolBase() {
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == pReportingDir && !s.trim().equals("")) {
            File file = new File(s);
            if (!file.exists() || !file.isDirectory()) {
                hashmap.put(stringproperty, "directory does not exist");
                return s;
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public boolean isMultiThreshold() {
        return false;
    }

    protected Document createNewDocument(String s) {
        Document document;
        try {
            DocumentBuilder documentbuilder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            Document document1 = documentbuilder.newDocument();
            Element element = document1.createElement("root");
            document1.appendChild(element);
            element.setAttribute("title", s);
            element.setAttribute("date", DateFormat.getDateTimeInstance()
                    .format(new Date()));
            element.setAttribute("server", getHostname());
            document = document1;
        } catch (Exception exception) {
            return null;
        }
        return document;
    }

    protected void addListContent(Vector vector, String s, Document document) {
        Element element = document.getDocumentElement();
        Element element1 = document.createElement("list-data");
        element1.setAttribute("title", s);
        element.appendChild(element1);
        for (int i = 0; i < vector.size(); i++) {
            Element element2 = document.createElement("list-item");
            element1.appendChild(element2);
            Vector vector1 = (Vector) vector.elementAt(i);
            for (int j = 0; j < vector1.size(); j++) {
                Pair pair = (Pair) vector1.elementAt(j);
                Element element3 = document.createElement("property");
                element2.appendChild(element3);
                element3.setAttribute("key", (String) pair.first);
                element3.setAttribute("value", (String) pair.second);
            }

        }

    }

    protected void saveDocument(Document document)
            throws TransformerConfigurationException, IOException,
            TransformerException {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(getTargetDirectory()).append(File.separator);
        String s = getProperty("_internalId");
        stringbuffer.append(s).append(".");
        String s1;
        synchronized (mFileDateFormat) {
            s1 = mFileDateFormat.format(new Date());
        }
        stringbuffer.append(s1);
        stringbuffer.append(".xml");
        FileWriter filewriter = new FileWriter(stringbuffer.toString());
        filewriter.write("<?xml version=\"1.0\"?>\n");
        filewriter.write(document.getDocumentElement().toString());
        filewriter.flush();
    }

    protected String getTargetDirectory() {
        String s = getProperty(pReportingDir);
        if (s.endsWith(File.separator)) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.trim().equals("")) {
            s = TempFileManager.getTempAccordingToSizeDirPath();
        }
        return s;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        array.add(pStatus);
        return array.elements();
    }

    public Array getLogProperties() {
        Array array = new Array();
        array.add(pStatus);
        return array;
    }

    public String getTestURL() {
        String string;
        try {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer
                    .append("/SiteView/cgi/go.exe/SiteView?page=exchangeTool&internalId=");
            stringbuffer.append(URLEncoder.encode(getProperty("_internalId"),
                    "UTF-8"));
            stringbuffer.append("&targetDir=");
            stringbuffer.append(getTargetDirectory());
            stringbuffer.append("&name=");
            stringbuffer.append(URLEncoder.encode(getProperty(pName), "UTF-8"));
            string = stringbuffer.toString();
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            return super.getTestURL();
        }
        return string;
    }

    public String getTestTitle() {
        return "Usage Stats";
    }

    static {
        REPORT_FILE_DATE_FORMAT = "yyyy-MM-dd-hhmmss";
        mFileDateFormat = new SimpleDateFormat(REPORT_FILE_DATE_FORMAT);
        String s = (COM.dragonflow.SiteView.ExchangeToolBase.class).getName();
        pStatus = new StringProperty("status", "no data");
        pStatus.setLabel("status");
        pStatus.setIsThreshold(true);
        pReportingDir = new StringProperty("_reportingDir", TempFileManager
                .getTempAccordingToSizeDirPath());
        pReportingDir.setDisplayText("Directory to write reports to", "");
        pReportingDir.setParameterOptions(true, 4, true);
        StringProperty astringproperty[] = { pReportingDir, pStatus };
        addProperties(s, astringproperty);
    }
}