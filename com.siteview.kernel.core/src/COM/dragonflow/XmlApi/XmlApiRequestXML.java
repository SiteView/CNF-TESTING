/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.XmlApi;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.StringReader;
import java.util.Enumeration;
import java.util.Vector;

import jgl.HashMap;

import org.xml.sax.InputSource;

// Referenced classes of package COM.dragonflow.XmlApi:
// XmlApiRequest, XmlApiObject

public class XmlApiRequestXML extends COM.dragonflow.XmlApi.XmlApiRequest {

    private static String headers[] = { "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" };

    StringBuffer xmlResponse;

    String xmlRequest;

    public static final String ALPHABETIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String NUMERIC = "0123456789";

    public static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private boolean encodeValues;

    public XmlApiRequestXML(String s) {
        xmlResponse = null;
        xmlRequest = null;
        encodeValues = true;
        if (debug) {
            java.lang.System.out.println("xmlRequest=" + s);
        }
        xmlRequest = s;
        javax.xml.parsers.DocumentBuilderFactory documentbuilderfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        org.w3c.dom.Document document = null;
        try {
            javax.xml.parsers.DocumentBuilder documentbuilder = documentbuilderfactory.newDocumentBuilder();
            document = documentbuilder.parse(new InputSource(new StringReader(s)));
        } catch (java.lang.Exception exception) {
            COM.dragonflow.Log.LogManager.log("Error", "SiteView API: Exception, " + exception.getMessage());
            return;
        }
        if (document.getDocumentElement() != null) {
            encodeValues = true;
            processNode(document.getDocumentElement(), request);
        } else {
            COM.dragonflow.Log.LogManager.log("error", "APIRequestXML: Document element is null");
        }
    }

    public java.lang.Object getRequest() {
        return xmlRequest;
    }

    public java.lang.Object getResponse() {
        return getResponse(true);
    }

    public java.lang.Object getResponse(boolean flag) {
        xmlResponse = new StringBuffer();
        if (flag) {
            for (int i = 0; i < headers.length; i ++) {
                xmlResponse.append(headers[i]);
            }

        }
        addXmlElement(request);
        endXmlElement(request);
        return xmlResponse.toString();
    }

    private void processNode(org.w3c.dom.Node node, COM.dragonflow.XmlApi.XmlApiObject xmlapiobject) {
        if (node.getNodeType() == 1) {
            String s = node.getNodeName();
            if (s != null && s.length() > 0) {
                jgl.HashMap hashmap = new HashMap();
                org.w3c.dom.NamedNodeMap namednodemap = node.getAttributes();
                for (int i = 0; i < namednodemap.getLength(); i ++) {
                    org.w3c.dom.Attr attr = (org.w3c.dom.Attr) namednodemap.item(i);
                    if (attr.getName() != null && attr.getName().equals("encodeValues") && attr.getValue() != null && (attr.getValue().equals("false") || attr.getValue().equals("true"))) {
                        encodeValues = (new Boolean(attr.getValue())).booleanValue();
                        continue;
                    }
                    String s1 = attr.getValue();
                    if (s1 != null) {
                        s1 = COM.dragonflow.Utils.TextUtils.enlighten(s1);
                    }
                    hashmap.put(attr.getName(), s1);
                }

                org.w3c.dom.NodeList nodelist = node.getChildNodes();
                java.util.Vector vector = findPropNodes(nodelist);
                for (int j = 0; j < vector.size(); j ++) {
                    org.w3c.dom.Node node1 = (org.w3c.dom.Node) vector.elementAt(j);
                    org.w3c.dom.NodeList nodelist1 = node1.getChildNodes();
                    String s2 = findPropertyName(nodelist1);
                    String as[] = findValueNodes(nodelist1);
                    if (as.length > 1) {
                        hashmap.put(s2, as);
                        continue;
                    }
                    if (as.length == 1) {
                        hashmap.put(s2, as[0]);
                    } else {
                        hashmap.put(s2, "");
                    }
                }

                xmlapiobject.setProperties(hashmap);
                xmlapiobject.setName(s);
                java.util.Vector vector1 = findChildrenNodes(nodelist);
                for (int k = 0; k < vector1.size(); k ++) {
                    org.w3c.dom.Node node2 = (org.w3c.dom.Node) vector1.elementAt(k);
                    processNode(node2, xmlapiobject.add());
                }

            }
        }
    }

    private java.util.Vector findPropNodes(org.w3c.dom.NodeList nodelist) {
        java.util.Vector vector = new Vector();
        int i = nodelist.getLength();
        for (int j = 0; j < i; j ++) {
            org.w3c.dom.Node node = nodelist.item(j);
            if (node.getNodeType() == 1 && node.getNodeName().equals("property")) {
                vector.addElement(node);
            }
        }

        return vector;
    }

    private String findPropertyName(org.w3c.dom.NodeList nodelist) {
        String s = "";
        int i = nodelist.getLength();
        for (int j = 0; j < i; j ++) {
            org.w3c.dom.Node node = nodelist.item(j);
            if (!node.getNodeName().equals("#text")) {
                continue;
            }
            String s1 = node.getNodeValue().trim();
            if (s1.length() <= 0) {
                continue;
            }
            s = s1;
            break;
        }

        return s;
    }

    private String[] findValueNodes(org.w3c.dom.NodeList nodelist) {
        java.util.Vector vector = new Vector();
        int i = nodelist.getLength();
        for (int j = 0; j < i; j ++) {
            org.w3c.dom.Node node = nodelist.item(j);
            if (!node.getNodeName().equals("value")) {
                continue;
            }
            org.w3c.dom.Node node1 = node.getFirstChild();
            String s = "";
            if (node1 != null) {
                s = node1.getNodeValue();
            }
            vector.addElement(s);
        }

        String as[] = new String[vector.size()];
        for (int k = 0; k < vector.size(); k ++) {
            as[k] = (String) vector.elementAt(k);
        }

        return as;
    }

    private java.util.Vector findChildrenNodes(org.w3c.dom.NodeList nodelist) {
        java.util.Vector vector = new Vector();
        int i = nodelist.getLength();
        for (int j = 0; j < i; j ++) {
            org.w3c.dom.Node node = nodelist.item(j);
            if (node.getNodeType() == 1 && !node.getNodeName().equals("property")) {
                vector.addElement(node);
            }
        }

        return vector;
    }

    private void addXmlElement(COM.dragonflow.XmlApi.XmlApiObject xmlapiobject) {
        startXmlElement(xmlapiobject.getName(), xmlapiobject.getProperties(false));
        COM.dragonflow.XmlApi.XmlApiObject xmlapiobject1;
        for (Enumeration enumeration = xmlapiobject.elements(); enumeration.hasMoreElements(); endXmlElement(xmlapiobject1)) {
            xmlapiobject1 = (COM.dragonflow.XmlApi.XmlApiObject) enumeration.nextElement();
            addXmlElement(xmlapiobject1);
        }

    }

    private void startXmlElement(String s, jgl.HashMap hashmap) {
        boolean flag = false;
        java.util.Vector vector = null;
        xmlResponse.append("<" + s);
        if (hashmap != null) {
            Enumeration enumeration = hashmap.keys();
            while (enumeration.hasMoreElements()) {
                String s1 = enumeration.nextElement().toString();
                java.lang.Object obj = hashmap.get(s1);
                if (obj instanceof String[]) {
                    flag = true;
                    if (vector == null) {
                        vector = new Vector();
                    }
                    vector.addElement(s1);
                } else if (obj instanceof String) {
                    if (COM.dragonflow.Utils.I18N.isNullEncoding(obj.toString())) {
                        xmlResponse.append(" " + s1 + "=\"" + COM.dragonflow.Utils.TextUtils.escapeHTML(COM.dragonflow.Utils.I18N.toDefaultEncoding(obj.toString())) + "\"");
                    } else {
                        xmlResponse.append(" " + s1 + "=\"" + COM.dragonflow.Utils.TextUtils.escapeHTML(obj.toString()) + "\"");
                    }
                }
            } 
            xmlResponse.append(">\r\n");
            if (flag) {
                for (int i = 0; i < vector.size(); i ++) {
                    String s2 = (String) vector.elementAt(i);
                    String as[] = (String[]) hashmap.get(s2);
                    xmlResponse.append("<property>\r\n");
                    xmlResponse.append(s2 + "\r\n");
                    for (int j = 0; j < as.length; j ++) {
                        if (as[j] == null) {
                            continue;
                        }
                        if (encodeValues) {
                            String s3 = COM.dragonflow.XmlApi.XmlApiRequestXML.escapeXML(as[j]);
                            xmlResponse.append("<value>" + s3 + "</value>\r\n");
                        } else {
                            xmlResponse.append("<value>" + as[j] + "</value>\r\n");
                        }
                    }

                    xmlResponse.append("</property>\r\n");
                }

            }
        }
    }

    private void endXmlElement(COM.dragonflow.XmlApi.XmlApiObject xmlapiobject) {
        xmlResponse.append("</" + xmlapiobject.getName() + ">\r\n");
    }

    private static int isEscaped(StringBuffer stringbuffer, String s, int i) {
        if (s.substring(i).startsWith("&gt;")) {
            stringbuffer.append("&gt;");
            return 4;
        }
        if (s.substring(i).startsWith("&lt;")) {
            stringbuffer.append("&lt;");
            return 4;
        }
        if (s.substring(i).startsWith("&#") || s.substring(i).startsWith("&amp;#")) {
            int j = 0;
            byte byte0 = ((byte) (s.substring(i).startsWith("&#") ? 2 : 6));
            for (j = i + byte0; j < s.length() && java.lang.Character.isDigit(s.charAt(j)); j ++) {
            }
            if (j < s.length() && s.charAt(j) == ';') {
                stringbuffer.append("&#");
                stringbuffer.append(s.substring(i + byte0, j + 1));
                return (j - i) + 1;
            }
        } else if (s.substring(i).startsWith("&amp;")) {
            stringbuffer.append("&amp;");
            return 5;
        }
        return 0;
    }

    public static String escapeHTMLPlusSpaces(String s) {
        String s1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.:/";
        StringBuffer stringbuffer = new StringBuffer(s.length());
        int i = 0;
        do {
            if (i >= s.length()) {
                break;
            }
            for (int j = 0; (j = COM.dragonflow.XmlApi.XmlApiRequestXML.isEscaped(stringbuffer, s, i)) > 0 && i < s.length(); i += j) {
            }
            if (i >= s.length()) {
                break;
            }
            char c = s.charAt(i ++);
            if (s1.indexOf(c) >= 0) {
                stringbuffer.append(c);
            } else {
                COM.dragonflow.Utils.TextUtils.escapeChar(c, stringbuffer);
            }
        } while (true);
        return stringbuffer.toString();
    }

    private static String escapeXML(String s) {
        StringBuffer stringbuffer = new StringBuffer(s.length());
        int i = 0;
        do {
            if (i >= s.length()) {
                break;
            }
            char c = s.charAt(i ++);
            if (!COM.dragonflow.XmlApi.XmlApiRequestXML.escapeSpecial(c, stringbuffer)) {
                stringbuffer.append(c);
            }
        } while (true);
        return stringbuffer.toString();
    }

    private static boolean escapeSpecial(char c, StringBuffer stringbuffer) {
        boolean flag = true;
        if (c == '>') {
            stringbuffer.append("&gt;");
        } else if (c == '<') {
            stringbuffer.append("&lt;");
        } else if (c == '&') {
            stringbuffer.append("&amp;");
        } else {
            flag = false;
        }
        return flag;
    }

}
