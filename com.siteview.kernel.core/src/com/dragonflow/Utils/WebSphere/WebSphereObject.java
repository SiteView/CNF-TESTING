/*
 * 
 * Created on 2005-3-9 18:55:36
 *
 * PDHRawCounterCache.java
 *
 * History:
 *
 */
package com.dragonflow.Utils.WebSphere;

import java.util.Vector;

// Referenced classes of package com.dragonflow.Utils.WebSphere:
// WebSphereCounter, WSUtils

public class WebSphereObject {

    private com.dragonflow.Utils.WebSphere.WebSphereObject parent;

    private java.util.Vector subObjects;

    public String name;

    public String id;

    public String desc;

    public java.util.Vector counters;

    public WebSphereObject() {
        subObjects = new Vector();
        counters = new Vector();
    }

    public WebSphereObject(com.dragonflow.Utils.WebSphere.WebSphereObject websphereobject, String s, String s1, String s2) {
        subObjects = new Vector();
        counters = new Vector();
        parent = websphereobject;
        name = s;
        id = com.dragonflow.Utils.WebSphere.WebSphereCounter.makeId(s1);
        desc = s2;
        websphereobject.addSubObject(this);
    }

    public void addCounter(String s, String s1, String s2) {
        counters.add(new WebSphereCounter(s, s1, s2));
    }

    public void addSubObject(com.dragonflow.Utils.WebSphere.WebSphereObject websphereobject) {
        subObjects.add(websphereobject);
    }

    public void removeSubObject(com.dragonflow.Utils.WebSphere.WebSphereObject websphereobject) {
        subObjects.remove(websphereobject);
    }

    public void toXML(StringBuffer stringbuffer, int i) {
        String s;
        if (parent == null) {
            stringbuffer.append("<browse_data>\n");
            s = "</browse_data>\n";
        } else {
            stringbuffer.append(com.dragonflow.Utils.WebSphere.WSUtils.indent(i) + "<object name=\"" + com.dragonflow.SiteView.JMXObject.safeAttribute(name) + "\"");
            if (id.length() > 0) {
                stringbuffer.append(" id=\"" + com.dragonflow.SiteView.JMXObject.safeAttribute(id) + "\"");
            }
            if (desc.length() > 0) {
                stringbuffer.append(" desc=\"" + com.dragonflow.SiteView.JMXObject.safeAttribute(desc) + "\"");
            }
            stringbuffer.append(">\n");
            s = "</object>\n";
        }
        com.dragonflow.Utils.WebSphere.WebSphereObject websphereobject;
        for (java.util.Iterator iterator = subObjects.iterator(); iterator.hasNext(); websphereobject.toXML(stringbuffer, i + 1)) {
            websphereobject = (com.dragonflow.Utils.WebSphere.WebSphereObject) iterator.next();
        }

        com.dragonflow.Utils.WebSphere.WebSphereCounter webspherecounter;
        for (java.util.Iterator iterator1 = counters.iterator(); iterator1.hasNext(); webspherecounter.toXML(stringbuffer, i + 1)) {
            webspherecounter = (com.dragonflow.Utils.WebSphere.WebSphereCounter) iterator1.next();
        }

        stringbuffer.append(com.dragonflow.Utils.WebSphere.WSUtils.indent(i) + s);
    }

    void purge() {
        java.lang.Object aobj[] = subObjects.toArray();
        for (int i = 0; i < aobj.length; i ++) {
            ((com.dragonflow.Utils.WebSphere.WebSphereObject) aobj[i]).purge();
        }

        if (parent != null && subObjects.isEmpty() && counters.isEmpty()) {
            parent.removeSubObject(this);
        }
    }
}
