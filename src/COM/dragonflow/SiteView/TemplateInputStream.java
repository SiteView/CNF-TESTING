/*
 * 
 * Created on 2005-2-16 17:04:40
 *
 * TemplateInputStream.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>TemplateInputStream</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Enumeration;

import jgl.Array;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Monitor, SiteViewObject, Platform

public class TemplateInputStream extends FilterInputStream {

    private StringBufferInputStream bufferStream;

    private StringBuffer buffer;

    SiteViewObject object;

    public TemplateInputStream(InputStream inputstream,
            SiteViewObject siteviewobject) {
        super(inputstream);
        bufferStream = null;
        buffer = new StringBuffer();
        object = null;
        object = siteviewobject;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public int read() throws IOException {
        if (bufferStream != null) {
            int i = bufferStream.read();
            if (i != -1) {
                return i;
            }
            bufferStream = null;
        }
        int j = in.read();
        if (j == 60) {
            buffer.setLength(0);
            for (j = in.read(); j != -1 && j != 62; j = in.read()) {
                buffer.append((char) j);
            }

            String s = buffer.toString();
            buffer.setLength(0);
            if (s.equalsIgnoreCase("name")) {
                buffer.append(object.getProperty(Monitor.pName));
            } else if (s.equalsIgnoreCase("state")) {
                buffer.append(object.getProperty(Monitor.pStateString));
            } else if (s.equalsIgnoreCase("group")) {
                buffer.append(object.getProperty(SiteViewObject.pOwnerID));
            } else if (s.equalsIgnoreCase("time")) {
                buffer.append(TextUtils.prettyDate(Platform.timeMillis()
                        + object.getSettingAsLong("_timeOffset") * 1000L));
            } else if (s.equalsIgnoreCase("mainParameters")) {
                for (Enumeration enumeration = object.getParameterObjects(); enumeration
                        .hasMoreElements();) {
                    StringProperty stringproperty = (StringProperty) enumeration
                            .nextElement();
                    if (buffer.length() > 0) {
                        buffer.append(Platform.FILE_NEWLINE);
                    }
                    buffer.append(stringproperty.getLabel());
                    buffer.append(": ");
                    if (stringproperty.isPassword) {
                        buffer.append(TextUtils.filledString('*',
                                stringproperty.valueString(
                                        object.getProperty(stringproperty))
                                        .length()));
                    } else {
                        buffer.append(stringproperty.valueString(object
                                .getProperty(stringproperty)));
                    }
                }

            } else if (s.equalsIgnoreCase("mainStateProperties")) {
                int k = 1;
                while (true) {
                    StringProperty stringproperty1 = object
                            .getStatePropertyObject(k);
                    if (stringproperty1 == null) {
                        break;
                    }
                    if (buffer.length() > 0) {
                        buffer.append(Platform.FILE_NEWLINE);
                    }
                    buffer.append(stringproperty1.getLabel());
                    buffer.append(": ");
                    buffer.append(stringproperty1.valueString(object
                            .getProperty(stringproperty1)));
                    k++;
                } 
            } else if (s.equalsIgnoreCase("secondaryStateProperties")) {
                Enumeration enumeration1 = object.getProperties().elements();
                while (enumeration1.hasMoreElements()) {
                    StringProperty stringproperty2 = (StringProperty) enumeration1
                            .nextElement();
                    if (stringproperty2.isStateProperty
                            && stringproperty2.getOrder() == 0) {
                        if (buffer.length() > 0) {
                            buffer.append(Platform.FILE_NEWLINE);
                        }
                        buffer.append(stringproperty2.getLabel());
                        buffer.append(": ");
                        buffer.append(stringproperty2.valueString(object
                                .getProperty(stringproperty2)));
                    }
                } 
            } else if (s.equalsIgnoreCase("secondaryParameters")) {
                Enumeration enumeration2 = object.getProperties().elements();
                while (enumeration2.hasMoreElements()) {
                    StringProperty stringproperty3 = (StringProperty) enumeration2
                            .nextElement();
                    if (stringproperty3.isParameter
                            && stringproperty3.getOrder() == 0) {
                        if (buffer.length() > 0) {
                            buffer.append(Platform.FILE_NEWLINE);
                        }
                        buffer.append(stringproperty3.getLabel());
                        buffer.append(": ");
                        if (stringproperty3.isPassword) {
                            buffer
                                    .append(TextUtils
                                            .filledString(
                                                    '*',
                                                    stringproperty3
                                                            .valueString(
                                                                    object
                                                                            .getProperty(stringproperty3))
                                                            .length()));
                        } else {
                            buffer.append(stringproperty3.valueString(object
                                    .getProperty(stringproperty3)));
                        }
                    }
                }
            } else if (s.equalsIgnoreCase("all")) {
                Array array = object.getProperties();
                for (Enumeration enumeration3 = array.elements(); enumeration3
                        .hasMoreElements(); buffer
                        .append(Platform.FILE_NEWLINE)) {
                    StringProperty stringproperty4 = (StringProperty) enumeration3
                            .nextElement();
                    buffer.append(stringproperty4.getLabel());
                    buffer.append(": ");
                    buffer.append(object.getProperty(stringproperty4));
                }

            } else if (object.hasProperty(s)) {
                buffer.append(object.getProperty(s));
            } else if (object.getSetting(s).length() > 0) {
                buffer.append(object.getSetting(s));
            } else {
                buffer.append("*** property not found (" + s + ")");
            }
            if (buffer.length() == 0) {
                buffer.append(' ');
            }
            bufferStream = new StringBufferInputStream(buffer.toString());
            return bufferStream.read();
        } else {
            return j;
        }
    }
}