/*
 * 
 * Created on 2005-2-16 17:04:04
 *
 * SunOneXMLHandlerUpdate.java
 *
 * History:
 *
 */
package com.dragonflow.SiteView;

/**
 * Comment for <code>SunOneXMLHandlerUpdate</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.dragonflow.StandardMonitor.SunOneMonitor;
import com.dragonflow.Utils.SunOneFormulaEngine;

public class SunOneXMLHandlerUpdate extends DefaultHandler {

    private static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

    private boolean inExcludeElement;

    private String excludeList;

    private SunOneFormulaEngine formulaEngine;

    private HashMap selectedCounters;

    private String counterValues[];

    private String _uniqueParentName;

    private boolean process_element_seen;

    private boolean inDuplicateProcessElement;

    public static void main(String args[]) throws Exception {
        XMLReader xmlreader = XMLReaderFactory
                .createXMLReader("org.apache.xerces.parsers.SAXParser");
        SunOneXMLHandlerUpdate sunonexmlhandlerupdate = new SunOneXMLHandlerUpdate();
        xmlreader.setContentHandler(sunonexmlhandlerupdate);
        xmlreader.setErrorHandler(sunonexmlhandlerupdate);
        String args1[] = new String[30];
        HashMap hashmap = new HashMap();
        hashmap.put("versionServer", new Integer(1));
        hashmap.put("secondsTimeout", new Integer(2));
        hashmap.put("percentUser", new Integer(3));
        sunonexmlhandlerupdate.setSelectedCounters(hashmap);
        sunonexmlhandlerupdate.setCounterValuesArray(args1);
        for (int i = 0; i < args.length; i++) {
            FileReader filereader = new FileReader(args[i]);
            xmlreader.parse(new InputSource(filereader));
        }

        Set set = hashmap.keySet();
        String s;
        Integer integer;
        for (Iterator iterator = set.iterator(); iterator.hasNext(); System.out
                .println("Selected Counter: " + s + "="
                        + args1[integer.intValue()])) {
            s = (String) iterator.next();
            integer = (Integer) hashmap.get(s);
        }

    }

    public SunOneXMLHandlerUpdate() {
        inExcludeElement = false;
        excludeList = null;
        formulaEngine = null;
        selectedCounters = null;
        counterValues = null;
        _uniqueParentName = null;
        process_element_seen = false;
        inDuplicateProcessElement = false;
    }

    public void setExcludeList(String s) {
        excludeList = s;
    }

    public void setSelectedCounters(HashMap hashmap) {
        selectedCounters = hashmap;
    }

    public void setCounterValuesArray(String as[]) {
        counterValues = as;
    }

    public void setFormulaEngine(SunOneFormulaEngine sunoneformulaengine) {
        formulaEngine = sunoneformulaengine;
    }

    public void startDocument() {
    }

    public void endDocument() {
    }

    public void startElement(String s, String s1, String s2,
            Attributes attributes) {
        int i = -1;
        if (excludeList != null) {
            i = excludeList.indexOf("," + s2 + ",");
        }
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
        boolean flag = SunOneMonitor.isElementInMakeUniqueList(s1);
        if (flag) {
            _uniqueParentName = formulateUniqueName(s1, attributes, "", true);
        }
        int j = attributes.getLength();
        for (int k = 0; k < j; k++) {
            String s3 = attributes.getLocalName(k);
            Integer integer;
            if (flag) {
                integer = findCounterIndex(s1, attributes, s3, true);
            } else if (weAreInAUniquesizedElement(s1)) {
                integer = findCounterIndex(s1, attributes, s3, false);
            } else {
                integer = (Integer) selectedCounters.get(s2 + "/" + s3);
            }
            if (integer != null) {
                counterValues[integer.intValue()] = attributes.getValue(k);
            }
            String s4 = formulateUniqueName(s1, attributes, s3, flag);
            formulaEngine.setImpliedCounterValue(s4, attributes.getValue(k));
        }

    }

    public void endElement(String s, String s1, String s2) {
        if (excludeList == null) {
            return;
        }
        int i = excludeList.indexOf("," + s2 + ",");
        if (i >= 0) {
            inExcludeElement = false;
        }
        if (SunOneMonitor.isElementInMakeUniqueList(s2)) {
            _uniqueParentName = null;
        }
        if (s2.equals("process") && inDuplicateProcessElement) {
            inDuplicateProcessElement = false;
        }
    }

    public void characters(char ac[], int i, int j) {
    }

    private boolean weAreInAUniquesizedElement(String s) {
        return _uniqueParentName != null;
    }

    private Integer findCounterIndex(String s, Attributes attributes,
            String s1, boolean flag) {
        String s2 = formulateUniqueName(s, attributes, s1, flag);
        return (Integer) selectedCounters.get(s2);
    }

    private String formulateUniqueName(String s, Attributes attributes,
            String s1, boolean flag) {
        String s2 = s1;
        if (flag) {
            String s3 = SunOneMonitor.getAttrForUniqueCounter(s);
            String s4 = attributes.getValue(s3);
            s2 = "/" + s + "@" + s3 + "-" + s4 + "/" + s1;
        } else {
            s2 = s + "/" + s1;
            if (_uniqueParentName != null) {
                s2 = _uniqueParentName + s2;
            }
        }
        return s2;
    }
}
