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

import com.recursionsw.jgl.BinaryPredicate;
import com.recursionsw.jgl.HashMap;

public final class GreaterCounterByXml implements BinaryPredicate {

    private HashMap countersWithSortIndex;

    public GreaterCounterByXml(COM.datachannel.xml.om.Document document) {
        countersWithSortIndex = new HashMap();
        if (document.getDocumentElement() != null) {
            org.w3c.dom.NodeList nodelist = document.getDocumentElement().getChildNodes();
            int i = nodelist.getLength();
            for (int j = 0; j < i; j ++) {
                org.w3c.dom.NodeList nodelist1 = nodelist.item(j).getChildNodes();
                int k = nodelist1.getLength();
                String s = ((org.w3c.dom.Element) nodelist.item(j)).getAttribute("name");
                for (int l = 0; l < k; l ++) {
                    String s1 = ((org.w3c.dom.Element) nodelist1.item(l)).getAttribute("name");
                    countersWithSortIndex.add(java.net.URLEncoder.encode(s + "/" + s1), new Integer(j * 1000 + l));
                }

            }

        }
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1) {
        java.lang.Integer integer = (java.lang.Integer) countersWithSortIndex.get(obj);
        java.lang.Integer integer1 = (java.lang.Integer) countersWithSortIndex.get(obj1);
        if (integer != null && integer1 != null) {
            return integer1.intValue() > integer.intValue();
        } else {
            return false;
        }
    }
}
