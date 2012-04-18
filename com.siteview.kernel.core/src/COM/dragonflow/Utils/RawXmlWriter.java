/*
 * Created on 2005-2-9 3:06:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Utils;

import com.recursionsw.jgl.Array;

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
public class RawXmlWriter {

    StringBuffer buf;

    public RawXmlWriter(StringBuffer stringbuffer) {
        buf = stringbuffer;
    }

    public void writeSOAPHeader(Array array) {
        if (array.size() <= 0) {
            buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            buf.append("<SOAP-ENV:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
            buf.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
            buf.append("xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
        } else {
            for (int i = 0; i < array.size(); i ++) {
                buf.append((String) array.get(i));
            }

        }
    }

    public void startElement(String s) {
        buf.append("<");
        buf.append(s);
        buf.append(">");
    }

    public void endElement(String s) {
        buf.append("</");
        buf.append(s);
        buf.append(">");
    }

    public void emptyElement(String s) {
        buf.append("<");
        buf.append(s);
        buf.append("/>");
    }

    public void startElementEnc(String s) {
        buf.append(xmlEncode("<"));
        buf.append(xmlEncode(s));
        buf.append(xmlEncode(">"));
    }

    public void endElementEnc(String s) {
        buf.append(xmlEncode("</"));
        buf.append(xmlEncode(s));
        buf.append(xmlEncode(">"));
    }

    public void emptyElementEnc(String s) {
        buf.append(xmlEncode("<"));
        buf.append(xmlEncode(s));
        buf.append(xmlEncode("/>"));
    }

    public void chardata(String s) {
        int i = s.length();
        for (int j = 0; j < i; j ++) {
            char c = s.charAt(j);
            switch (c) {
            case 60: // '<'
                buf.append("&lt;");
                break;

            case 62: // '>'
                buf.append("&gt;");
                break;

            case 34: // '"'
                buf.append("&quot;");
                break;

            case 38: // '&'
                buf.append("&amp;");
                break;

            default:
                buf.append(c);
                break;
            }
        }

    }

    public String xmlEncode(String s) {
        String s1 = "&";
        String s2 = "&amp;";
        String s3 = s;
        for (int i = -1; (i = s3.indexOf(s1, i + 1)) != -1;) {
            s3 = s3.substring(0, i) + s2 + s3.substring(i + s1.length());
        }

        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, "\"", "&quot;");
        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, ">", "&gt;");
        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, "<", "&lt;");
        return s3;
    }

    public void write(char ac[]) {
        buf.append(ac);
    }

    public void write(String s) {
        buf.append(s);
    }

    public String toString() {
        return buf.toString();
    }

    public static String enXMLElement(String s) {
        return s;
    }

    public static String enCodeElement(String s) {
        String s1 = "&";
        String s2 = "&amp;";
        String s3 = s;
        for (int i = -1; (i = s3.indexOf(s1, i + 1)) != -1;) {
            s3 = s3.substring(0, i) + s2 + s3.substring(i + s1.length());
        }

        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, "\"", "&quot;");
        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, ">", "&gt;");
        s3 = COM.dragonflow.Utils.TextUtils.replaceString(s3, "<", "&lt;");
        return s3;
    }
}
