/*
 * 
 * Created on 2005-2-28 6:53:07
 *
 * CounterValueXMLProperty.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>CounterValueXMLProperty</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Referenced classes of package COM.dragonflow.Properties:
// XMLProperty, StringProperty, NumericProperty

public class CounterValueXMLProperty extends XMLProperty {
    private static class XML {

        public static final String COUNTERS = "Counters";

        public static final String COUNTERINFO = "CounterInfo";

        public static final String ATTR_ID = "id";

        public static final String ATTR_TYPE = "Type";

        public static final String ATTR_VALUE = "Value";

        private XML() {
        }
    }

    public CounterValueXMLProperty(String s) {
        super(s);
    }

    public static StringProperty[] decodeDocument(String s, Document document) {
        if (document == null) {
            return new StringProperty[0];
        }
        NodeList nodelist = document.getElementsByTagName("CounterInfo");
        int i = nodelist.getLength();
        StringProperty astringproperty[] = new StringProperty[i];
        for (int j = 0; j < nodelist.getLength(); j++) {
            astringproperty[j] = parseCounter((Element) nodelist.item(j), s);
        }

        return astringproperty;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param astringproperty
     * @return
     */
    public static Document encodeDocument(StringProperty astringproperty[]) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element element = document.createElement("Counters");
            document.appendChild(element);
            for (int i = 0; i < astringproperty.length; i++) {
                StringProperty stringproperty = astringproperty[i];
                Element element1 = document.createElement("CounterInfo");
                element1.setAttribute("id", Integer.toString(i));
                if (stringproperty.getClass().equals(COM.dragonflow.Properties.NumericProperty.class)) {
                    element1.setAttribute("Type", "Number");
                } else {
                    element1.setAttribute("Type", "String");
                }
                element1.setAttribute("Value", stringproperty.getValue());
                element.appendChild(element1);
            }
            return document;
        } catch (Exception e) {
            return null;
        }

    }

    static StringProperty parseCounter(Element element, String s) {
        Object obj = null;
        try {
            String s1 = getAttribute(element, "id");
            String s2 = getAttribute(element, "Type");
            String s3 = getAttribute(element, "Value");
            if (s2.equals("String")) {
                obj = new StringProperty(s + s1);
            } else if (s2.equals("Number")) {
                obj = new NumericProperty(s + s1);
            }
            ((StringProperty) (obj)).setValue(s3);
        } catch (Exception exception) {
        }
        return ((StringProperty) (obj));
    }
}