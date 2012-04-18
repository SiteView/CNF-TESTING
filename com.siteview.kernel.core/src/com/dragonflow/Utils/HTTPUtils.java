/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package com.dragonflow.Utils;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;

import com.recursionsw.jgl.Array;
import com.recursionsw.jgl.HashMap;
import com.dragonflow.Properties.HashMapOrdered;

// Referenced classes of package com.dragonflow.Utils:
// TextUtils

public class HTTPUtils {

    public static Array locations;

    public static HashMap locationMap = new HashMap();

    public static final String DEFAULT_LOCATION_SPEC = "this_server,this server,";

    public HTTPUtils() {
    }

    public static String getLocationID(String s) {
        return com.dragonflow.Utils.HTTPUtils.getLocationPart(s, 0);
    }

    public static String getLocationName(String s) {
        return com.dragonflow.Utils.HTTPUtils.getLocationPart(s, 1);
    }

    public static String getLocationURL(String s) {
        return com.dragonflow.Utils.HTTPUtils.getLocationPart(s, 2);
    }

    public static String getPingURL(String s) {
        return com.dragonflow.Utils.HTTPUtils.getLocationPart(s, 3);
    }

    public static String getTraceURL(String s) {
        return com.dragonflow.Utils.HTTPUtils.getLocationPart(s, 4);
    }

    public static String getPortURL(String s) {
        return com.dragonflow.Utils.HTTPUtils.getLocationPart(s, 5);
    }

    public static int getDisplayOrder(String s) {
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        int i = com.dragonflow.Utils.TextUtils.toInt(com.dragonflow.Utils.TextUtils.getValue(hashmap, "_URLLocationComponent"));
        if (i <= 0) {
            i = 6;
        }
        String s1 = com.dragonflow.Utils.HTTPUtils.getLocationPart(s, i);
        if (s1 == null || s1.equals("") || s1.equals("0")) {
            return 1;
        } else {
            return (new Integer(s1)).intValue();
        }
    }

    public static String getLocationNameByID(String s) {
        String s1 = "";
        Enumeration enumeration = (Enumeration) locations.iterator();
        while (enumeration.hasMoreElements()) {
            String s2 = (String) enumeration.nextElement();
            if (s.equals(com.dragonflow.Utils.HTTPUtils.getLocationID(s2))) {
            s1 = com.dragonflow.Utils.HTTPUtils.getLocationName(s2);
            break;
            }
        } 
        return s1;
    }

    public static String locationsHTML(String s, com.dragonflow.HTTP.HTTPRequest httprequest) {
        StringBuffer stringbuffer = new StringBuffer();
        if (locations.size() > 1) {
            stringbuffer.append("Location: <SELECT name=location size=6>\n");
            Enumeration enumeration = (Enumeration) locations.iterator();
            com.dragonflow.Properties.HashMapOrdered hashmapordered = new HashMapOrdered(true);
            int i;
            int j;
            for (i = 0; enumeration.hasMoreElements(); i = java.lang.Math.max(i, j)) {
                String s1 = (String) enumeration.nextElement();
                String s3 = com.dragonflow.Utils.HTTPUtils.getLocationID(s1);
                j = com.dragonflow.Utils.HTTPUtils.getDisplayOrder(s1);
                if (j >= 0) {
                    hashmapordered.add(new Integer(j), s1);
                    continue;
                }
                com.dragonflow.SiteView.SiteViewGroup siteviewgroup = com.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                com.dragonflow.SiteView.MonitorGroup monitorgroup = (com.dragonflow.SiteView.MonitorGroup) siteviewgroup.getElement(httprequest.getValue("group"));
                String s5 = monitorgroup.getSetting("_allowInvalidNode");
                if (s5 != null && s5.indexOf(s3) >= 0) {
                    hashmapordered.add(new Integer(0), s1);
                }
            }

            for (int k = 0; k <= i; k ++) {
                java.lang.Integer integer = new Integer(k);
                String s2;
                String s6;
                for (Enumeration enumeration1 = (Enumeration) hashmapordered.values(integer); enumeration1.hasMoreElements(); stringbuffer.append("<option " + s6 + " value=" + com.dragonflow.Utils.HTTPUtils.getLocationID(s2) + ">"
                        + com.dragonflow.Utils.HTTPUtils.getLocationName(s2) + "</option>\n")) {
                    s6 = "";
                    s2 = (String) enumeration1.nextElement();
                    String s4 = com.dragonflow.Utils.HTTPUtils.getLocationID(s2);
                    if (s.indexOf(s4) >= 0) {
                        s6 = "SELECTED";
                    }
                    if (s.length() == 0 && s4.indexOf("this_server") >= 0) {
                        s6 = "SELECTED";
                    }
                }

            }

            stringbuffer.append("</SELECT><P>\n");
        }
        return stringbuffer.toString();
    }

    public static String getTraceURLByID(String s) {
        String s1 = "";
        Enumeration enumeration = (Enumeration) locations.iterator();
        while (enumeration.hasMoreElements()) {
            String s2 = (String) enumeration.nextElement();
            if (s.equals(com.dragonflow.Utils.HTTPUtils.getLocationID(s2))) {
            s1 = com.dragonflow.Utils.HTTPUtils.getTraceURL(s2);
            break;
            }
        } 
        return s1;
    }

    public static String getPingURLByID(String s) {
        String s1 = "";
        Enumeration enumeration = (Enumeration) locations.iterator();
        while (enumeration.hasMoreElements()) {
            String s2 = (String) enumeration.nextElement();
            if (s.equals(com.dragonflow.Utils.HTTPUtils.getLocationID(s2))) {
            s1 = com.dragonflow.Utils.HTTPUtils.getPingURL(s2);
            break;
            }
        } 
        return s1;
    }

    public static String getLocationIDByURL(String s) {
        String s1 = "";
        Enumeration enumeration = (Enumeration) locations.iterator();
        while (enumeration.hasMoreElements()) {
            String s2 = (String) enumeration.nextElement();
            if (s.equals(com.dragonflow.Utils.HTTPUtils.getLocationURL(s2))) {
            s1 = com.dragonflow.Utils.HTTPUtils.getLocationID(s2);
            break;
            }
        } 
        return s1;
    }

    public static String getLocationURLByID(String s) {
        String s1 = "";
        Enumeration enumeration = (Enumeration) locations.iterator();
        while (enumeration.hasMoreElements()) {
            String s2 = (String) enumeration.nextElement();
            if (s.equals(com.dragonflow.Utils.HTTPUtils.getLocationID(s2))) {
            s1 = com.dragonflow.Utils.HTTPUtils.getLocationURL(s2);
            break;
            }
        } 
        return s1;
    }

    public static String getLocationPart(String s, int i) {
        HashMap hashmap = com.dragonflow.SiteView.MasterConfig.getMasterConfig();
        int j = com.dragonflow.Utils.TextUtils.toInt(com.dragonflow.Utils.TextUtils.getValue(hashmap, "_URLLocationComponent"));
        if (j <= 0) {
            j = 6;
        }
        String as[] = new String[j + 1];
        int k = com.dragonflow.Utils.TextUtils.splitChar(s, ',', as);
        if (i < k) {
            return as[i];
        } else {
            return "";
        }
    }

    public static String hostFromURL(String s) {
        String s1 = "";
        if (s.length() > 0) {
            int i = s.indexOf("://");
            if (i > 0) {
                i += 3;
                int j = s.indexOf(":", i);
                if (j < 0) {
                    j = s.indexOf('/', i);
                }
                if (j < 0) {
                    j = s.length();
                }
                s1 = s.substring(i, j);
            }
        }
        return s1;
    }

    public static String getHTTPPart(String s, boolean flag) {
        int i = s.length();
        int j = 0;
        do {
            int k = s.indexOf('\n', j);
            if (k == -1) {
                break;
            }
            String s1 = s.substring(j, k).trim();
            if (s1.length() == 0) {
                i = k + 1;
                break;
            }
            j = k + 1;
        } while (true);
        if (flag) {
            return s.substring(0, i);
        } else {
            return s.substring(i);
        }
    }

    static {
        locations = new Array();
        locations.add("this_server,this server,");
    }
}
