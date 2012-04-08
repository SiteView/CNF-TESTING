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

// Referenced classes of package COM.dragonflow.Utils:
// TextUtils
public class URLInfo implements jgl.BinaryPredicate {

    public String url;

    public String source;

    public String contentType;

    public long size;

    public long duration;

    public int status;

    public int count;

    public boolean visited;

    public boolean externalLink;

    public static final int SORT_URL = 1;

    public static final int SORT_STATUS = 2;

    public static final int SORT_TYPE = 3;

    public static final int SORT_COUNT = 4;

    public static final int SORT_EXTERNAL = 5;

    public static final int SORT_SOURCE = 6;

    public static final int SORT_SIZE = 7;

    public static final int SORT_TIME = 8;

    public static final int SORT_MODEMTIME = 9;

    int sortKey;

    public void addChild(COM.dragonflow.Utils.URLInfo urlinfo) {
    }

    public URLInfo() {
        url = "";
        source = "unknown";
        contentType = "other";
        size = -1L;
        duration = -1L;
        status = -1;
        count = 0;
        visited = false;
        externalLink = false;
        sortKey = 1;
    }

    public URLInfo(String s) {
        url = "";
        source = "unknown";
        contentType = "other";
        size = -1L;
        duration = -1L;
        status = -1;
        count = 0;
        visited = false;
        externalLink = false;
        sortKey = 1;
        url = "" + s;
    }

    public URLInfo(String s, String s1) {
        url = "";
        source = "unknown";
        contentType = "other";
        size = -1L;
        duration = -1L;
        status = -1;
        count = 0;
        visited = false;
        externalLink = false;
        sortKey = 1;
        url = "" + s;
        source = s1;
    }

    public URLInfo(String s, boolean flag) {
        this(s);
        visited = flag;
    }

    public void setContentType(String s) {
        contentType = "" + s;
        int i = contentType.indexOf(";");
        if (i >= 0) {
            contentType = contentType.substring(0, i);
        }
        contentType = contentType.trim();
    }

    public void print(java.io.PrintWriter printwriter) {
        String s = COM.dragonflow.Utils.TextUtils.replaceChar(url, '\n', "\\n");
        printwriter.println(s + "\t" + status + "\t" + contentType + "\t" + count + "\t" + externalLink + "\t" + duration + "\t" + size + "\t" + source);
    }

    public boolean initialize(String s) {
        String as[] = COM.dragonflow.Utils.TextUtils.split(s, "\t");
        if (as.length >= 7) {
            url = as[0];
            status = COM.dragonflow.Utils.TextUtils.toInt(as[1]);
            contentType = as[2];
            count = COM.dragonflow.Utils.TextUtils.toInt(as[3]);
            externalLink = as[4].equals("true");
            duration = COM.dragonflow.Utils.TextUtils.toInt(as[5]);
            size = COM.dragonflow.Utils.TextUtils.toInt(as[6]);
            if (as.length >= 8) {
                source = as[7];
            }
        } else {
            return false;
        }
        return true;
    }

    public String getProtocol() {
        String s = "";
        int i = url.indexOf(":");
        if (i != -1) {
            s = url.substring(0, i);
        }
        return s.toLowerCase();
    }

    public String getRawHost() {
        int i = url.indexOf("://");
        if (i != -1) {
            String s = url.substring(i + "://".length());
            int j = s.indexOf("/");
            if (j != -1) {
                s = s.substring(0, j);
            } else {
                int k = s.indexOf("?");
                if (k != -1) {
                    s = s.substring(0, k);
                }
            }
            return s;
        } else {
            return "";
        }
    }

    public String getHost() {
        String s = getRawHost();
        int i = s.indexOf(":");
        if (i != -1) {
            s = s.substring(0, i);
        }
        return s;
    }

    public String getPort() {
        String s = getRawHost();
        int i = s.indexOf(":");
        if (i != -1) {
            return s.substring(i + 1);
        } else {
            return "";
        }
    }

    public int getConnectPort() {
        int i = COM.dragonflow.Utils.TextUtils.toInt(getPort());
        if (i == 0) {
            String s = getProtocol();
            if (s.equals("https")) {
                i = 443;
            } else {
                i = 80;
            }
        }
        return i;
    }

    public String getFile() {
        int i = url.indexOf(":");
        int j = ":".length();
        String s = "";
        if (i != -1) {
            if (url.charAt(i + 1) == '/' && url.charAt(i + 2) == '/') {
                j = 3;
            }
            s = url.substring(i + j);
            if (j == 3) {
                int k = s.indexOf("/");
                if (k != -1) {
                    return s.substring(k);
                } else {
                    return "/";
                }
            }
        }
        return s;
    }

    public static int sortType(String s) {
        if (s.equals("status")) {
            return 2;
        }
        if (s.equals("type")) {
            return 3;
        }
        if (s.equals("count")) {
            return 4;
        }
        if (s.equals("external")) {
            return 5;
        }
        if (s.equals("source")) {
            return 6;
        }
        if (s.equals("size")) {
            return 7;
        }
        if (s.equals("time")) {
            return 8;
        }
        return !s.equals("modemTime") ? 2 : 9;
    }

    public URLInfo(int i) {
        url = "";
        source = "unknown";
        contentType = "other";
        size = -1L;
        duration = -1L;
        status = -1;
        count = 0;
        visited = false;
        externalLink = false;
        sortKey = 1;
        sortKey = i;
    }

    public boolean execute(java.lang.Object obj, java.lang.Object obj1) {
        COM.dragonflow.Utils.URLInfo urlinfo = (COM.dragonflow.Utils.URLInfo) obj;
        COM.dragonflow.Utils.URLInfo urlinfo1 = (COM.dragonflow.Utils.URLInfo) obj1;
        switch (sortKey) {
        default:
            break;

        case 5: // '\005'
            if (urlinfo1.externalLink != urlinfo.externalLink) {
                return !urlinfo1.externalLink;
            }
            break;

        case 3: // '\003'
            int i = urlinfo1.contentType.compareTo(urlinfo.contentType);
            if (i != 0) {
                return i > 0;
            }
            break;

        case 6: // '\006'
            int j = urlinfo1.source.compareTo(urlinfo.source);
            if (j != 0) {
                return j > 0;
            }
            break;

        case 8: // '\b'
            if (urlinfo.duration != urlinfo1.duration) {
                return urlinfo.duration > urlinfo1.duration;
            }
            break;

        case 7: // '\007'
        case 9: // '\t'
            if (urlinfo.size != urlinfo1.size) {
                return urlinfo.size > urlinfo1.size;
            }
            break;

        case 2: // '\002'
            if (urlinfo.status != urlinfo1.status) {
                return java.lang.Math.abs(urlinfo.status) > java.lang.Math.abs(urlinfo1.status);
            }
            break;

        case 4: // '\004'
            if (urlinfo.count != urlinfo1.count) {
                return urlinfo.count > urlinfo1.count;
            }
            break;
        }
        return urlinfo1.url.compareTo(urlinfo.url) >= 0;
    }
}
