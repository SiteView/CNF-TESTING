/*
 * 
 * Created on 2005-2-28 7:11:10
 *
 * XMLProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>XMLProperty</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Referenced classes of package COM.dragonflow.Properties:
// StringProperty

public class XMLProperty extends StringProperty {

    public XMLProperty(String s) {
        super(s);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public static Document parseString(String s) {
        try {
            DocumentBuilder documentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentbuilder.parse(new InputSource(new StringReader(s)));
            return document;
        } catch (ParserConfigurationException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param document
     * @return
     */
    public static String generateXMLString(Document document) {
        try {
            StringWriter stringwriter;
            stringwriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(new DOMSource(document.getDocumentElement()), new StreamResult(stringwriter));
            return stringwriter.toString();
        } catch (Exception e) {
            return "";
        }
    }

    static String getAttribute(Element element, String s) throws Exception {
        String s1 = element.getAttribute(s);
        if (s1.equals("")) {
            throw new Exception("Attribute " + s + " not found for: " + element.getTagName());
        } else {
            return s1;
        }
    }

    static String getOptionalAttribute(Element element, String s) {
        String s1 = element.getAttribute(s);
        return s1;
    }

    static Element getSingleton(Element element, String s) throws Exception {
        NodeList nodelist = element.getElementsByTagName(s);
        if (nodelist.getLength() == 0) {
            throw new Exception("Missing element for tag: " + s);
        }
        if (nodelist.getLength() != 1) {
            throw new Exception("Multiple elements for tag: " + s);
        }
        Node node = nodelist.item(0);
        if (node.getNodeType() != 1) {
            throw new Exception("Malformed singleton - child not element; tag " + s);
        } else {
            return (Element) node;
        }
    }

    static Element getOptionalSingleton(Element element, String s) throws Exception {
        NodeList nodelist = element.getElementsByTagName(s);
        if (nodelist.getLength() == 0) {
            return null;
        }
        if (nodelist.getLength() != 1) {
            throw new Exception("Multiple elements for tag: " + s);
        }
        Node node = nodelist.item(0);
        if (node.getNodeType() != 1) {
            throw new Exception("Malformed singleton - child not element; tag " + s);
        } else {
            return (Element) node;
        }
    }

    static String getTextForElement(Element element, String s) throws Exception {
        String s1 = null;
        Element element1 = getSingleton(element, s);
        s1 = getTextForThisElement(element1);
        return s1;
    }

    static String getTextForThisElement(Element element) throws Exception {
        Object obj = null;
        NodeList nodelist = element.getChildNodes();
        if (nodelist.getLength() != 1) {
            throw new Exception("Missing text element for tag: " + element.getTagName());
        }
        Node node = element.getFirstChild();
        if (node.getNodeType() != 3) {
            throw new Exception("Malformed text for tag: " + element.getTagName());
        } else {
            String s = node.getNodeValue();
            return s;
        }
    }
}