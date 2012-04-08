/*
 * 
 * Created on 2005-2-16 17:35:45
 *
 * WebSphereJMXObject.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>WebSphereJMXObject</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import COM.dragonflow.Utils.WebSphere.WebSphereCounter;
import COM.dragonflow.Utils.WebSphereJMXInterface;

// Referenced classes of package COM.dragonflow.SiteView:
// JMXObject

public class WebSphereJMXObject extends JMXObject {

    String cell;

    String node;

    String process;

    String id;

    HashSet filters;

    HashMap configs;

    Object perfOName;

    public WebSphereJMXObject() {
        cell = null;
        node = null;
        process = null;
        id = null;
        filters = null;
        configs = null;
        perfOName = null;
    }

    public WebSphereJMXObject(Object obj, Object obj1, WebSphereJMXInterface webspherejmxinterface, HashSet hashset, HashMap hashmap, Object obj2) {
        super(obj, obj1, webspherejmxinterface);
        cell = null;
        node = null;
        process = null;
        id = null;
        filters = null;
        configs = null;
        perfOName = null;
        if (obj == null) {
            id = "";
        } else {
            id = obj.toString();
        }
        filters = hashset;
        configs = hashmap;
        perfOName = obj2;
        try {
            type = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "type" });
            name = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "name" });
            cell = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "cell" });
            node = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "node" });
            process = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "process" });
            if (type != null && cell != null && node != null && process != null) {
                addCounters();
            }
        } catch (Exception exception) {
            System.err.println("WebSphereJMXObject exception: " + exception);
        }
    }

    public WebSphereJMXObject(Object obj, WebSphereJMXObject webspherejmxobject, Object obj1, WebSphereJMXInterface webspherejmxinterface, HashSet hashset, HashMap hashmap, Object obj2) {
        this(obj, obj1, webspherejmxinterface, hashset, hashmap, obj2);
        parent = webspherejmxobject;
        webspherejmxobject.addSubObject(this);
    }

    public WebSphereJMXObject(Object obj, Object obj1, WebSphereJMXInterface webspherejmxinterface, HashSet hashset, Object obj2, HashMap hashmap, String s) {
        super(obj, obj1, webspherejmxinterface);
        cell = null;
        node = null;
        process = null;
        id = null;
        filters = null;
        configs = null;
        perfOName = null;
        if (obj != null) {
            try {
                type = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "type" });
                name = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "name" });
                cell = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "cell" });
                node = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "node" });
                process = (String) webspherejmxinterface.getKeyPropertyMethod.invoke(obj, new Object[] { "process" });
                if (type != null && cell != null && node != null && process != null) {
                    addCounters();
                }
            } catch (Exception exception) {
                System.err.println("WebSphereJMXObject exception: " + exception);
            }
        }
        filters = hashset;
        if (obj2 != null) {
            try {
                String s1 = (String) webspherejmxinterface.getStatsNameMethod.invoke(obj2, null);
                name = (String) webspherejmxinterface.getNLSValueMethod.invoke(null, new Object[] { s1 });
                id = s + "/" + WebSphereCounter.normalize(s1);
                type = name;
                Object aobj[] = (Object[]) webspherejmxinterface.getStatisticsMethod.invoke(obj2, null);
                if (aobj != null) {
                    for (int i = 0; i < aobj.length; i ++) {
                        String s2 = (String) webspherejmxinterface.getStatisticNameMethod.invoke(aobj[i], null);
                        if (filterCounter(type, s2)) {
                            continue;
                        }
                        String s3 = (String) hashmap.get(s2);
                        if (s3 == null) {
                            s3 = s2;
                        }
                        // counters.add(new JMXObject.Counter(this, id + "/" +
                        // WebSphereCounter.normalize(s2), s3));
                        counters.add(new JMXObject.Counter(this, id + "/" + WebSphereCounter.normalize(s2), s3));
                    }

                }
                Object aobj1[] = (Object[]) webspherejmxinterface.getSubStatsMethod.invoke(obj2, null);
                if (aobj1 != null) {
                    for (int j = 0; j < aobj1.length; j ++) {
                        WebSphereJMXObject webspherejmxobject = new WebSphereJMXObject(null, obj1, webspherejmxinterface, hashset, aobj1[j], hashmap, id);
                        addSubObject(webspherejmxobject);
                        webspherejmxobject.setParent(this);
                    }

                }
            } catch (Exception exception1) {
                System.err.println("WebSphereJMXObject constructor: " + exception1);
            }
        }
    }

    public String getCell() {
        return cell;
    }

    public String getNode() {
        return node;
    }

    public String getProcess() {
        return process;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    int addCounters() {
        if (cachedCounters.containsKey(type)) {
            counters = (Vector) cachedCounters.get(type);
        } else {
            counters = new Vector();
            try {
                Object obj = management.getMBeanInfoMethod.invoke(mbs, new Object[] { objectName });
                Object aobj[] = (Object[]) management.getAttributesMethod.invoke(obj, null);
                for (int i = 0; i < aobj.length; i ++) {
                    if (((Boolean) management.isReadableMethod.invoke(aobj[i], null)).booleanValue()) {
                        String s = (String) management.getTypeMethod.invoke(aobj[i], null);
                        String s1 = (String) management.getNameMethod.invoke(aobj[i], null);
                        String as[] = { "byte", "short", "int", "long", "float", "double", "String", "java.lang.Integer" };
                        for (int j = 0; j < as.length; j ++) {
                            if (as[j].equals(s)) {
                                String s2 = (String) management.getDescriptionMethod.invoke(aobj[i], null);
                                if (s2 == null || s2.length() == 0) {
                                    s2 = s1;
                                }
                                if (!filterCounter(type, s1)) {
                                    // counters.add(new JMXObject.Counter(this,
                                    // s1, s2));
                                    counters.add(new JMXObject.Counter(this, s1, s2));
                                }
                                break;
                            }
                        }
                    }
                }

            } catch (Exception exception) {
                System.err.println("WebSphereJMXObject addCounters exception for objectName '" + objectName + "': " + exception);
            }
            cachedCounters.put(type, counters);
        }
        return counters.size();
    }

    protected boolean filterCounter(String s, String s1) {
        boolean flag = filters.contains(s + ":" + s1) || filters.contains("*:" + s1) || filters.contains(s + ":*");
        return flag;
    }

    void setParent(WebSphereJMXObject webspherejmxobject) {
        parent = webspherejmxobject;
    }

    void printXML(PrintWriter printwriter, int i) {
        for (int j = 0; j < i; j ++) {
            printwriter.print(" ");
        }

        if (parent == null) {
            printwriter.println("<browse_data>");
        } else {
            printwriter.println("<object name=\"" + safeAttribute(name) + "\" id=\"" + safeAttribute(objectName == null ? "" : objectName.toString()) + "\" desc=\"" + safeAttribute(type) + "\" type=\"" + safeAttribute(type) + "\" solutionsID=\""
                    + safeAttribute(id == null ? "" : WebSphereCounter.normalize(id)) + "\">");
        }
        JMXObject jmxobject;
        for (Iterator iterator = children.iterator(); iterator.hasNext(); jmxobject.printXML(printwriter, i + 1)) {
            jmxobject = (JMXObject) iterator.next();
        }

        JMXObject.Counter counter;
        for (Iterator iterator1 = counters.iterator(); iterator1.hasNext(); printwriter.println("<counter name=\"" + safeAttribute(WebSphereCounter.getLeafName(counter.name, 46)) + "\" id=\"" + safeAttribute(counter.name) + "\" desc=\""
                + safeAttribute(counter.description) + "\"/>")) {
            counter = (JMXObject.Counter) iterator1.next();
            for (int l = 0; l <= i; l ++) {
                printwriter.print(" ");
            }

        }

        for (int k = 0; k < i; k ++) {
            printwriter.print(" ");
        }

        if (parent == null) {
            printwriter.println("</browse_data>");
        } else {
            printwriter.println("</object>");
        }
    }
}
