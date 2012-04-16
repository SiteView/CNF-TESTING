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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

//import com.dragonflow.infra.xdr.XDRMemInputStream;
//import com.dragonflow.infra.xdr_utils.PropertyBag;

// Referenced classes of package COM.dragonflow.Utils:
// Base64Encoder, Base64Decoder

public class SerializerUtils {

    public SerializerUtils() {
    }

    /**
     * 
     * 
     * @param propertybag
     * @return
     */
//    public static String encodePropBagToString(com.dragonflow.infra.xdr_utils.PropertyBag propertybag) {
//        Object obj = null;
//        String s;
//        try {
//            byte abyte0[] = propertybag.encode();
//            s = COM.dragonflow.Utils.SerializerUtils.encodeByteArrayToString(abyte0);
//            return s;
//        } catch (com.dragonflow.infra.xdr.XDRException xdrexception) {
//            xdrexception.printStackTrace();
//            COM.dragonflow.Log.LogManager.logException(xdrexception);
//            return null;
//        }
//    }

    /**
     * 
     * 
     * @param serializable
     * @return
     */
    public static String encodeObject(java.io.Serializable serializable) {
        if (serializable == null) {
            return "";
        }
        java.io.ByteArrayOutputStream bytearrayoutputstream;
        java.io.ObjectOutputStream objectoutputstream = null;
        bytearrayoutputstream = new ByteArrayOutputStream();
        try {
            objectoutputstream = new ObjectOutputStream(bytearrayoutputstream);
            objectoutputstream.writeObject(serializable);
            return COM.dragonflow.Utils.SerializerUtils.encodeByteArrayToString(bytearrayoutputstream.toByteArray());
        } catch (java.io.IOException ioexception) {
            ioexception.printStackTrace();
            COM.dragonflow.Log.LogManager.log("Error", ioexception.getMessage());
            return null;
        }
    }

    public static String encodeObjectBase64(java.io.Serializable serializable) {
        return COM.dragonflow.Utils.SerializerUtils.encodeObjectBase64(serializable, true);
    }

    /**
     * 
     * 
     * @param serializable
     * @param flag
     * @return
     */
    public static String encodeObjectBase64(java.io.Serializable serializable, boolean flag) {
        if (serializable == null) {
            return "";
        }
        String s;
        try {
            java.io.ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            java.io.ObjectOutputStream objectoutputstream = new ObjectOutputStream(bytearrayoutputstream);
            objectoutputstream.writeObject(serializable);
            java.io.ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
            bytearrayoutputstream.reset();
            COM.dragonflow.Utils.Base64Encoder base64encoder = new Base64Encoder(bytearrayinputstream, bytearrayoutputstream);
            base64encoder.process();
            s = bytearrayoutputstream.toString();
            if (flag) {
                return s;
            }
            return s.replaceAll("\n|\r", "");
        } catch (java.io.IOException ioexception) {
            ioexception.printStackTrace();
            COM.dragonflow.Log.LogManager.log("Error", ioexception.getMessage());
            return null;
        }
    }

    public static java.io.Serializable decodeJavaObjectFromStringBase64(String s) {
        return COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromByteArray(COM.dragonflow.Utils.SerializerUtils.decodeBytesFromStringBase64(s));
    }

    public static java.io.Serializable decodeJavaObjectFromString(String s) {
        return COM.dragonflow.Utils.SerializerUtils.decodeJavaObjectFromByteArray(COM.dragonflow.Utils.SerializerUtils.decodeBytesFromString(s));
    }

//    public static com.dragonflow.infra.xdr_utils.PropertyBag decodePropBagFromString(String s) {
//        return COM.dragonflow.Utils.SerializerUtils.decodePropBagFromByteArray(COM.dragonflow.Utils.SerializerUtils.decodeBytesFromString(s));
//    }

    private static String encodeByteArrayToString(byte abyte0[]) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < abyte0.length; i ++) {
            stringbuffer.append(abyte0[i]);
            stringbuffer.append('.');
        }

        return stringbuffer.toString();
    }

    private static byte[] decodeBytesFromString(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        java.util.StringTokenizer stringtokenizer = new StringTokenizer(s, ".");
        byte abyte0[] = new byte[stringtokenizer.countTokens()];
        for (int i = 0; stringtokenizer.hasMoreElements(); i ++) {
            try {
                abyte0[i] = (byte) java.lang.Integer.parseInt((String) stringtokenizer.nextElement());
            } catch (java.lang.NumberFormatException numberformatexception) {
                numberformatexception.printStackTrace();
            }
        }

        return abyte0;
    }

    private static byte[] decodeBytesFromStringBase64(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        byte abyte0[] = null;
        COM.dragonflow.Utils.Base64Decoder base64decoder = new Base64Decoder(s);
        try {
            abyte0 = base64decoder.processBytes();
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
        }
        return abyte0;
    }

    /**
     * 
     * 
     * @param abyte0
     * @return
     */
    private static java.io.Serializable decodeJavaObjectFromByteArray(byte abyte0[]) {
        if (abyte0 == null || abyte0.length == 0) {
            return null;
        }
        java.io.Serializable serializable;
        try {
            java.io.ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
            java.io.ObjectInputStream objectinputstream = new ObjectInputStream(bytearrayinputstream);
            serializable = (java.io.Serializable) objectinputstream.readObject();
            bytearrayinputstream = null;
            objectinputstream = null;
            return serializable;
        } catch (IOException e) {
            e.printStackTrace();
            COM.dragonflow.Log.LogManager.logException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            COM.dragonflow.Log.LogManager.logException(e);
        }
        return null;
    }

//    private static com.dragonflow.infra.xdr_utils.PropertyBag decodePropBagFromByteArray(byte abyte0[]) {
//        if (abyte0 == null || abyte0.length == 0) {
//            return null;
//        }
//
//        try {
//            com.dragonflow.infra.xdr_utils.PropertyBag propertybag;
//            propertybag = new PropertyBag();
//            com.dragonflow.infra.xdr.XDRMemInputStream xdrmeminputstream = new XDRMemInputStream(abyte0);
//            propertybag.decode(xdrmeminputstream);
//            return propertybag;
//        } catch (com.dragonflow.infra.xdr.XDRException xdrexception) {
//            xdrexception.printStackTrace();
//            COM.dragonflow.Log.LogManager.logException(xdrexception);
//            return null;
//        }
//    }
}
