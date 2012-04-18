/*
 * 
 * Created on 2005-2-16 17:03:42
 *
 * SunOneXMLHandler.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>SunOneXMLHandler</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.FileReader;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.dragonflow.StandardMonitor.SunOneMonitor;
import com.dragonflow.Utils.RawXmlWriter;
import com.dragonflow.Utils.SunOneFormulaEngine;

public class SunOneXMLHandler extends DefaultHandler {

    private static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    private boolean inExcludeElement;

    private RawXmlWriter writer;

    private StringBuffer xml;

    private String excludeList;

    private String derivedList[];

    private SunOneFormulaEngine formulaEngine;

    private boolean process_element_seen;

    private boolean inDuplicateProcessElement;

    public static void main(String args[]) throws Exception {
        XMLReader xmlreader = XMLReaderFactory
                .createXMLReader("org.apache.xerces.parsers.SAXParser");
        SunOneXMLHandler sunonexmlhandler = new SunOneXMLHandler();
        xmlreader.setContentHandler(sunonexmlhandler);
        xmlreader.setErrorHandler(sunonexmlhandler);
        for (int i = 0; i < args.length; i++) {
            FileReader filereader = new FileReader(args[i]);
            xmlreader.parse(new InputSource(filereader));
            String s = sunonexmlhandler.getXML();
            System.out.println(s);
        }

    }

    public SunOneXMLHandler() {
        inExcludeElement = false;
        writer = null;
        xml = null;
        excludeList = null;
        derivedList = null;
        formulaEngine = null;
        process_element_seen = false;
        inDuplicateProcessElement = false;
        xml = new StringBuffer();
        writer = new RawXmlWriter(xml);
    }

    public void setExcludeList(String s) {
        excludeList = s;
    }

    public void setDerivedList(String as[]) {
        derivedList = as;
    }

    public void setFormulaEngine(SunOneFormulaEngine sunoneformulaengine) {
        formulaEngine = sunoneformulaengine;
    }

    public void startDocument() {
        writer.startElement("browse_data");
    }

    public void endDocument() {
        writer.endElement("browse_data");
    }

    public void startElement(String s, String s1, String s2,
            Attributes attributes) {
        int i = excludeList.indexOf("," + s2 + ",");
        if (i >= 0) {
            inExcludeElement = true;
            return;
        }
        if (s2.equals("process")) {
            if (process_element_seen) {
                inDuplicateProcessElement = true;
                return;
            }
            process_element_seen = true;
        }
        if (inExcludeElement || inDuplicateProcessElement) {
            return;
        }
        if (SunOneMonitor.isElementInMakeUniqueList(s2)) {
            String s3 = process_uniqueCounter(s2, attributes);
            formulaEngine.registerElementForDerivedCounter(s3);
            return;
        }
        formulaEngine.registerElementForDerivedCounter(s2);
        writer.startElement("object name=\"" + s2 + "\"");
        int j = attributes.getLength();
        for (int k = 0; k < j; k++) {
            String s4 = attributes.getLocalName(k);
            String s5 = attributes.getValue(k);
            String s6 = s4 + "+" + s5;
            writer
                    .emptyElement("counter name=\"" + s4 + "\" id=\"" + s6
                            + "\"");
        }

    }

    public void endElement(String s, String s1, String s2) {
        int i = excludeList.indexOf("," + s2 + ",");
        if (i >= 0) {
            inExcludeElement = false;
            return;
        }
        if (s2.equals("process")) {
            if (inDuplicateProcessElement) {
                inDuplicateProcessElement = false;
            } else {
                writer.endElement("object");
            }
            return;
        }
        if (inExcludeElement || inDuplicateProcessElement) {
            return;
        }
        if (s2.equalsIgnoreCase("stats")) {
            writer.write(getDerivedCountersXml());
        }
        writer.endElement("object");
    }

    public void characters(char ac[], int i, int j) {
    }

    public String getXML() {
        return xml.toString();
    }

    private String process_uniqueCounter(String s, Attributes attributes) {
        String s1 = null;
        String s2 = SunOneMonitor.getAttrForUniqueCounter(s);
        int i = attributes.getLength();
        int j = 0;
        do {
            if (j >= i) {
                break;
            }
            String s3 = attributes.getLocalName(j);
            if (s3.equals(s2)) {
                s1 = s + "@" + s2 + "-" + attributes.getValue(j);
                break;
            }
            j++;
        } while (true);
        if (s1 == null) {
            s1 = s;
        }
        writer.startElement("object name=\"" + s1 + "\"");
        for (int k = 0; k < i; k++) {
            String s4 = attributes.getLocalName(k);
            String s5 = attributes.getValue(k);
            String s6 = s4 + "+" + s5;
            if (!s4.equals(s2)) {
                writer.emptyElement("counter name=\"" + s4 + "\" id=\"" + s6
                        + "\"");
            }
        }

        return s1;
    }

    private String getDerivedCountersXml() {
        return formulaEngine.generateBrowseData();
    }

    public static void checkMaxProcs(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); i++) {
            String s = attributes.getLocalName(i);
            if (!s.equalsIgnoreCase("maxProcs")) {
                continue;
            }
            String s1 = attributes.getValue(i);
            int j = Integer.parseInt(s1);
            String s2 = SunOneMonitor.getMakeUniqueList();
            if (j > 1) {
                if (s2.indexOf("process#pid,") < 0) {
                    s2 = s2 + "process#pid,";
                }
            } else {
                int k = s2.indexOf("process#pid,");
                if (k >= 0) {
                    s2 = s2.substring(0, k);
                }
            }
            SunOneMonitor.setMakeUniqueList(s2);
        }

    }
}
