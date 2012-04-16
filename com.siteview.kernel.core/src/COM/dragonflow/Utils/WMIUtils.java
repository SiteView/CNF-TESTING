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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

//import COM.dragonflow.MdrvBase.xdr.ss_dispatcher_request_header;
//import COM.dragonflow.WMIMonitor.xdr.wmi_request_header;
//import COM.dragonflow.WMIMonitor.xdr.wmi_status;
//
//import com.dragonflow.infra.xdr.XDRMemInputStream;
//import com.dragonflow.infra.xdr.XDRMemOutputStream;

// Referenced classes of package COM.dragonflow.Utils:
// Pair, Base64Encoder, CommandLine, Base64Decoder,
// Base64FormatException, TextUtils

public class WMIUtils {

    private static java.text.DateFormat mWMIDateFormatter = new SimpleDateFormat("yyyyMMddhhmmss'.000000+000'");

    private static java.util.regex.Pattern dateFunctionPattern = java.util.regex.Pattern.compile("DATEFUNCTION\\s*\\(\\s*curdate\\s*([+-]|)\\s*(\\d*)\\)", 2);

    private static java.text.DateFormat mReverseFormatter = new SimpleDateFormat("yyyyMMddhhmmss");

    private static boolean usePerfexProcessPool = false;

    private static String mTargetMDRVnode = "SSWMI";

    private static java.util.Vector errorLookups;

    static final boolean $assertionsDisabled; /* synthetic field */

    public WMIUtils() {
    }

//    public static boolean sendWmiRequest(int i, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller1, StringBuffer stringbuffer, int j) {
//        return COM.dragonflow.Utils.WMIUtils.sendWmiRequestJMDRV(i, xdrmarshaller, xdrmarshaller1, stringbuffer);
//    }

    /**
     * 
     * 
     * @param s
     * @return
     */
    public static java.util.Date makeJavaDateFromWMIDate(String s) {
        try {
            synchronized (mReverseFormatter) {
                return mReverseFormatter.parse(s);
            }
        } catch (java.lang.Exception exception) {
            return null;
        }
    }

    public static String applyDateFunctionsToQuery(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        java.util.regex.Matcher matcher = dateFunctionPattern.matcher(s);
        int i = 0;
        java.util.GregorianCalendar gregoriancalendar = null;
        while (matcher.find()) {
            if (gregoriancalendar == null) {
                gregoriancalendar = new GregorianCalendar();
                gregoriancalendar.setTime(new Date());
            }
            stringbuffer.append(s.substring(i, matcher.start()));
            i = matcher.end();
            String s1 = matcher.group(1);
            int j = 0;
            try {
                j = java.lang.Integer.parseInt(matcher.group(2));
            } catch (java.lang.Exception exception) {
            }
            if (s1.equals("-")) {
                j = -j;
            }
            gregoriancalendar.add(12, j);
            synchronized (mWMIDateFormatter) {
                stringbuffer.append(mWMIDateFormatter.format(gregoriancalendar.getTime()));
            }
        }
        if (i > 0) {
            stringbuffer.append(s.substring(i));
            return stringbuffer.toString();
        } else {
            return s;
        }
    }

    /**
     * 
     * 
     * @param i
     * @param xdrmarshaller
     * @param xdrmarshaller1
     * @param stringbuffer
     * @return
     */
//    private static boolean sendWmiRequestJMDRV(int i, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller1, StringBuffer stringbuffer) {
//        try {
//            COM.dragonflow.WMIMonitor.xdr.wmi_status wmi_status1;
//            java.lang.Object aobj[] = new java.lang.Object[1];
//            com.dragonflow.infra.xdr.XDRMemOutputStream xdrmemoutputstream = new XDRMemOutputStream();
//            (new wmi_request_header(i)).encode(xdrmemoutputstream);
//            xdrmarshaller.encode(xdrmemoutputstream);
//            if (COM.dragonflow.MdrvMain.RequestSync.sendRequest(new ss_dispatcher_request_header("ssmon_wmi.dll", "ssmon_wmi_request_handler", mTargetMDRVnode), xdrmemoutputstream.getBytes(), aobj, stringbuffer)) {
//                com.dragonflow.infra.xdr.XDRMemInputStream xdrmeminputstream = new XDRMemInputStream((byte[]) aobj[0]);
//                wmi_status1 = new wmi_status();
//                wmi_status1.decode(xdrmeminputstream);
//                if (wmi_status1.get_stat() != 0) {
//                    String s = wmi_status1.get_description();
//                    java.util.Iterator iterator = errorLookups.iterator();
//                    while (iterator.hasNext()) {
//                        COM.dragonflow.Utils.Pair pair = (COM.dragonflow.Utils.Pair) iterator.next();
//                        if (s.indexOf((String) pair.first) > -1) {
//                            s = (String) pair.second;
//                            break;
//                        }
//                    }
//                    stringbuffer.append(s);
//                } else {
//                    xdrmarshaller1.decode(xdrmeminputstream);
//                }
//                return wmi_status1.get_stat() == 0;
//            }
//            return false;
//        } catch (com.dragonflow.infra.xdr.XDRException e) {
//            e.printStackTrace();
//            stringbuffer.append(e.getMessage());
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    /**
     * 
     * 
     * @param i
     * @param xdrmarshaller
     * @param xdrmarshaller1
     * @param stringbuffer
     * @param j
     * @return
     */
//    private static boolean sendWmiRequestPerfex(int i, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller1, StringBuffer stringbuffer, int j) {
//        try {
//            String s2;
//            com.dragonflow.infra.xdr.XDRMemOutputStream xdrmemoutputstream = new XDRMemOutputStream();
//            (new wmi_request_header(i)).encode(xdrmemoutputstream);
//            xdrmarshaller.encode(xdrmemoutputstream);
//            java.io.ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(xdrmemoutputstream.getBytes());
//            java.io.ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
//            COM.dragonflow.Utils.Base64Encoder base64encoder = new Base64Encoder(bytearrayinputstream, bytearrayoutputstream);
//            base64encoder.process();
//            String s = bytearrayoutputstream.toString();
//            if (j == 0) {
//                j = 60;
//            }
//            jgl.Array array = null;
//            COM.dragonflow.Utils.CommandLine commandline = new CommandLine();
//            String s1 = COM.dragonflow.SiteView.Platform.perfexCommand("") + " -timeout " + j + " -wmi " + s;
//            if (usePerfexProcessPool) {
//                array = commandline.exec(s1);
//            } else {
//                array = commandline.exec(COM.dragonflow.Utils.TextUtils.split(s1, " "));
//            }
//            if (!$assertionsDisabled && array == null) {
//                throw new AssertionError();
//            }
//            StringBuffer stringbuffer1 = new StringBuffer();
//            for (int k = 0; k < array.size(); k ++) {
//                String s3 = (String) array.at(k);
//                if (s3.indexOf("perfex") == -1) {
//                    stringbuffer1.append((String) array.at(k));
//                }
//            }
//
//            s2 = stringbuffer1.toString();
//            if (s2.startsWith("ERROR")) {
//                stringbuffer.append(s2);
//                return false;
//            }
//            if (s2.trim().equals("")) {
//                stringbuffer.append("Server unreachable");
//                return false;
//            }
//            COM.dragonflow.WMIMonitor.xdr.wmi_status wmi_status1;
//            COM.dragonflow.Utils.Base64Decoder base64decoder = new Base64Decoder(s2);
//            byte abyte0[] = null;
//            try {
//                abyte0 = base64decoder.processBytes();
//            } catch (COM.dragonflow.Utils.Base64FormatException base64formatexception) {
//                COM.dragonflow.Utils.Base64Decoder base64decoder1 = new Base64Decoder(s2.substring(0, s2.length() - 2));
//                abyte0 = base64decoder1.processBytes();
//            }
//            com.dragonflow.infra.xdr.XDRMemInputStream xdrmeminputstream = new XDRMemInputStream(abyte0);
//            wmi_status1 = new wmi_status();
//            wmi_status1.decode(xdrmeminputstream);
//            if (wmi_status1.get_stat() != 0) {
//                String s4 = wmi_status1.get_description();
//                java.util.Iterator iterator = errorLookups.iterator();
//                while (iterator.hasNext()) {
//                    COM.dragonflow.Utils.Pair pair = (COM.dragonflow.Utils.Pair) iterator.next();
//                    if (s4.indexOf((String) pair.first) > -1) {
//                        s4 = (String) pair.second;
//                        break;
//                    }
//                }
//                stringbuffer.append(s4);
//            } else {
//                xdrmarshaller1.decode(xdrmeminputstream);
//            }
//            return wmi_status1.get_stat() == 0;
//        } catch (java.lang.Exception exception) {
//            exception.printStackTrace();
//            stringbuffer.append(exception.getMessage());
//            return false;
//        }
//    }

    static {
        $assertionsDisabled = !(COM.dragonflow.Utils.WMIUtils.class).desiredAssertionStatus();
        errorLookups = new Vector();
        errorLookups.add(new Pair("E_ACCESSDENIED", "Failed to logon to server or WMI namespace"));
        errorLookups.add(new Pair("E_ACCESS_DENIED", "Failed to logon to server or WMI namespace"));
        errorLookups.add(new Pair("WBEM_E_INVALID_NAMESPACE", "The desired WMI namespace does not exist on the server"));
        errorLookups.add(new Pair("WBEM_E_INVALID_QUERY", "The submitted WMI query is not valid"));
        errorLookups.add(new Pair("WBEM_E_INVALID_CLASS", "The desired WMI class does not exist or is invalid"));
        errorLookups.add(new Pair("ConnectServer failed", "Could not connect to the server"));
    }
}
