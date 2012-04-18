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

// Referenced classes of package com.dragonflow.Utils.WebSphere:
// WSUtils

public class WebSphereCounter implements java.io.Serializable {

    private String name;

    public String id;

    private String description;

    private String denormalizedName;

    private String value;

    public WebSphereCounter(String s) {
        name = "";
        denormalizedName = "";
        value = "";
        name = s;
        denormalizedName = com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(name);
    }

    public WebSphereCounter(String s, String s1) {
        name = "";
        denormalizedName = "";
        value = "";
        name = s;
        denormalizedName = com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(name);
        id = com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(s1);
    }

    public String getName() {
        return name;
    }

    public String getDenormalizedCounterName() {
        return denormalizedName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String s) {
        value = s;
    }

    public WebSphereCounter(String s, String s1, String s2) {
        name = "";
        denormalizedName = "";
        value = "";
        name = s;
        id = com.dragonflow.Utils.WebSphere.WebSphereCounter.makeId(s1);
        description = s2;
    }

    public void toXML(StringBuffer stringbuffer, int i) {
        stringbuffer.append(com.dragonflow.Utils.WebSphere.WSUtils.indent(i) + "<counter name=\"" + com.dragonflow.SiteView.JMXObject.safeAttribute(name) + "\"");
        if (id.length() > 0) {
            stringbuffer.append(" id=\"" + com.dragonflow.SiteView.JMXObject.safeAttribute(id) + "\"");
        }
        if (description.length() > 0) {
            stringbuffer.append(" desc=\"" + com.dragonflow.SiteView.JMXObject.safeAttribute(description) + "\"");
        }
        stringbuffer.append("/>\n");
    }

    public static String makeId(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < s.length(); i ++) {
            if (s.charAt(i) == '/' || s.charAt(i) == '\\') {
                stringbuffer.append('\\');
            }
            stringbuffer.append(s.charAt(i));
        }

        return stringbuffer.toString();
    }

    public static String denormalize(String s) {
        String s1 = s;
        int i = 0;
        do {
            if (i == -1) {
                break;
            }
            i = s1.indexOf("0x2F", i);
            if (i != -1) {
                if (i + 8 < s1.length() && s1.charAt(i + 4) == '0' && s1.charAt(i + 5) == 'x' && s1.charAt(i + 6) == '2' && s1.charAt(i + 7) == 'F') {
                    s1 = s1.substring(0, i + 4) + s1.substring(i + 8);
                } else {
                    s1 = s1.substring(0, i) + "/" + s1.substring(i + 4);
                }
            }
        } while (true);
        return s1;
    }

    public static String normalize(String s) {
        String s1 = s;
        s1 = com.dragonflow.Utils.WebSphere.WebSphereCounter.replace(s1, "0x2F", "0x2F0x2F");
        s1 = com.dragonflow.Utils.WebSphere.WebSphereCounter.replace(s1, "/", "0x2F");
        return s1;
    }

    private static String replace(String s, String s1, String s2) {
        String s3 = s;
        int i = 0;
        do {
            if (i == -1) {
                break;
            }
            i = s3.indexOf(s1, i);
            if (i != -1) {
                s3 = s3.substring(0, i) + s2 + s3.substring(i + s1.length());
                i += s2.length();
            }
        } while (true);
        return s3;
    }

    public static String getLeafName(String s, int i) {
        int j = s.lastIndexOf(i);
        if (j > 0) {
            return s.substring(j + 1);
        } else {
            return s;
        }
    }

    public String getObjectNameFromID() {
        if (id == null) {
            return "";
        }
        int i = id.lastIndexOf('/');
        if (i == -1) {
            return com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(id);
        }
        String s = id.substring(0, i);
        int j = s.lastIndexOf('/');
        if (j == -1) {
            return com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(s);
        } else {
            return com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(s.substring(j + 1));
        }
    }

    public String getAttributeNameFromID() {
        if (id == null) {
            return "";
        }
        int i = id.lastIndexOf('/');
        if (i == -1) {
            return com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(id);
        } else {
            return com.dragonflow.Utils.WebSphere.WebSphereCounter.denormalize(id.substring(i + 1));
        }
    }
}
