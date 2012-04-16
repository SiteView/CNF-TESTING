/*
 * 
 * Created on 2005-3-9 18:55:12
 *
 * PDHSupport.java
 *
 * History:
 *
 */
package COM.dragonflow.Utils.PDH;

//import COM.dragonflow.MdrvBase.xdr.ss_dispatcher_request_header;
//import COM.dragonflow.PDHMonitor.xdr.pdh_connect;
//import COM.dragonflow.PDHMonitor.xdr.pdh_request_header;
//import COM.dragonflow.PDHMonitor.xdr.pdh_status;
//
//import com.dragonflow.infra.xdr.XDRMemInputStream;
//import com.dragonflow.infra.xdr.XDRMemOutputStream;

public class PDHSupport {

    public PDHSupport() {
    }

//    public static COM.dragonflow.PDHMonitor.xdr.pdh_connect getXdrHost(String s) {
//        String s1 = "";
//        String s2 = "";
//        if (COM.dragonflow.SiteView.Platform.isNTRemote(s)) {
//            COM.dragonflow.SiteView.Machine machine = COM.dragonflow.SiteView.Machine.getNTMachine(s);
//            if (machine != null) {
//                s1 = machine.getProperty(COM.dragonflow.SiteView.Machine.pLogin);
//                s2 = machine.getProperty(COM.dragonflow.SiteView.Machine.pPassword);
//            }
//        }
//        return new pdh_connect(s, s1, s2);
//    }

    /**
     * 
     * 
     * @param i
     * @param xdrmarshaller
     * @param xdrmarshaller1
     * @param stringbuffer
     * @return
     */
//    public static boolean sendPdhRequest(int i, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller, com.dragonflow.infra.xdr.XDRMarshaller xdrmarshaller1, StringBuffer stringbuffer) {
//        try {
//            COM.dragonflow.PDHMonitor.xdr.pdh_status pdh_status1;
//            java.lang.Object aobj[] = new java.lang.Object[1];
//            com.dragonflow.infra.xdr.XDRMemOutputStream xdrmemoutputstream = new XDRMemOutputStream();
//            (new pdh_request_header(i)).encode(xdrmemoutputstream);
//            xdrmarshaller.encode(xdrmemoutputstream);
//            if (COM.dragonflow.MdrvMain.RequestSync.sendRequest(new ss_dispatcher_request_header("ssmon_pdh.dll", "ssmon_pdh_request_handler"), xdrmemoutputstream.getBytes(), aobj, stringbuffer)) {
//                com.dragonflow.infra.xdr.XDRMemInputStream xdrmeminputstream = new XDRMemInputStream((byte[]) aobj[0]);
//                pdh_status1 = new pdh_status();
//                pdh_status1.decode(xdrmeminputstream);
//                if (pdh_status1.get_stat() != 0) {
//                    stringbuffer.append(pdh_status1.get_description());
//                }
//                if (pdh_status1.get_stat() != 1) {
//                    xdrmarshaller1.decode(xdrmeminputstream);
//                }
//                return pdh_status1.get_stat() != 1;
//            }
//            return false;
//        } catch (com.dragonflow.infra.xdr.XDRException xdrexception) {
//            xdrexception.printStackTrace();
//            stringbuffer.append(xdrexception.getMessage());
//            return false;
//        }
//    }
}
