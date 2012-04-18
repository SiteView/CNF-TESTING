/*
 * 
 * Created on 2005-2-15 11:51:11
 *
 * BrowsableMdrvBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsableMdrvBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.recursionsw.jgl.Array;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;

//import com.dragonflow.infra.xdr.XDRMemInputStream;
//import com.dragonflow.infra.xdr.XDRMemOutputStream;

// Referenced classes of package COM.dragonflow.SiteView:
// BrowsableBase, Platform, MasterConfig

public abstract class BrowsableMdrvBase extends BrowsableBase {

    static String GET_BROWSE_DATA = "GET_BROWSE_DATA";

    static String GET_NEW_DATA = "GET_NEW_DATA";

    static int exeTimeout;

    static int nMaxCounters;

    public BrowsableMdrvBase() {
    }

    protected boolean update() {
        LinkedList linkedlist = new LinkedList();
        int i = 0;
        do {
            if (i >= nMaxCounters) {
                break;
            }
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
            if (s.length() <= 0) {
                break;
            }
            linkedlist.add(s);
            i++;
        } while (true);
        i = linkedlist.size();
        Array array = getConnArgs();
        HashMap hashmap = new HashMap();
        Array array1 = getConnectionProperties();
        for (int j = 0; j < array1.size(); j++) {
            StringProperty stringproperty = (StringProperty) array1.get(j);
            String s1 = stringproperty.getName().substring(1);
            hashmap.put(s1, getProperty(stringproperty));
        }

        Array array2 = new Array();
        int k = exec(GET_NEW_DATA, linkedlist, array2);
        for (int l = 1; l <= nMaxCounters; l++) {
            setProperty(PROPERTY_NAME_COUNTER_VALUE + l, "n/a");
        }

        if (stillActive()) {
            synchronized (this) {
                String s2 = "";
                setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                        i);
                if (array2.size() == 0) {
                    s2 = "Failed to get performance data. Error code: " + k;
                } else if (k != 0) {
                    StringBuffer stringbuffer = new StringBuffer();
                    for (int j1 = 0; j1 < array2.size(); j1++) {
                        stringbuffer.append((String) array2.get(j1) + " ");
                    }

                    setProperty(pNoData, "n/a");
                    s2 = stringbuffer.toString();
                    setProperty(
                            getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                            i);
                } else {
                    int i1 = array2.size();
                    setProperty(
                            getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                            i - i1);
                    s2 = saveResultProps(array2);
                }
                setProperty(pStateString, s2);
            }
        }
        return true;
    }

    private int exec(String s, List list, Array array) {
        int i = 0;
        try {
            String s1 = Platform.getRoot() + File.separator + "logs";
            HashMap hashmap = new HashMap();
            Array array1 = getConnectionProperties();
            for (int j = 0; j < array1.size(); j++) {
                StringProperty stringproperty = (StringProperty) array1.get(j);
                String s2 = stringproperty.getName().substring(1);
                hashmap.put(s2, getProperty(stringproperty));
            }

//            ss_mon_sybase_request ss_mon_sybase_request1 = new ss_mon_sybase_request();
//            ss_mon_sybase_request1.set_timeout(getExeTimeout());
//            ss_mon_sybase_request1.set_logsPath(s1);
//            ss_mon_sybase_request1.set_requestType(s);
//            ss_mon_sybase_request1.set_monitorId(getFullID());
//            property_pair_seq property_pair_seq1 = new property_pair_seq();
            java.util.Map.Entry entry;
//            for (Iterator iterator = hashmap.entrySet().iterator(); iterator
//                    .hasNext(); property_pair_seq1.add(new property_pair(
//                    (String) entry.getKey(), (String) entry.getValue()))) {
//                entry = (java.util.Map.Entry) iterator.next();
//            }

//            ss_mon_sybase_request1.set_properties(property_pair_seq1);
//            string_seq string_seq1 = new string_seq();
            if (list != null) {
                String s4;
//                for (Iterator iterator1 = list.iterator(); iterator1.hasNext(); string_seq1
//                        .add(s4)) {
//                    s4 = (String) iterator1.next();
//                }

            }
//            ss_mon_sybase_request1.set_counterSeq(string_seq1);
            String s3 = getMonDll();
//            ss_dispatcher_request_header ss_dispatcher_request_header1 = new ss_dispatcher_request_header(
//                    s3, "ss_mon_mdrv_request_handler");
            String s5 = getTargetNode();
            if (s5 != null && !s5.trim().equals("")) {
//                ss_dispatcher_request_header1.set_targetNode(s5);
            }
            Object aobj[] = new Object[1];
//            XDRMemOutputStream xdrmemoutputstream = new XDRMemOutputStream();
//            ss_mon_sybase_request1.encode(xdrmemoutputstream);
            StringBuffer stringbuffer = new StringBuffer();
            Object obj = null;
//            if (RequestSync.sendRequest(ss_dispatcher_request_header1,
//                    xdrmemoutputstream.getBytes(), aobj, stringbuffer)) {
//                XDRMemInputStream xdrmeminputstream = new XDRMemInputStream(
//                        (byte[]) aobj[0]);
//                ss_mon_sybase_response ss_mon_sybase_response1 = new ss_mon_sybase_response();
//                ss_mon_sybase_response1.decode(xdrmeminputstream);
//                String s6 = ss_mon_sybase_response1.get_result();
//                String as[] = TextUtils.split(s6, "\n");
//                for (int k = 0; k < as.length; k++) {
//                    array.add(as[k]);
//                }
//
//                i = ss_mon_sybase_response1.get_returnCode();
//            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return i;
    }

    String saveResultProps(Array array) {
        String s = "";
        HashMap hashmap = new HashMap();
        for (int i = 0; i < array.size(); i++) {
            String s1 = (String) array.get(i);
            int k = s1.indexOf('=');
            String s4 = s1.substring(0, k);
            String s5 = s1.substring(k + 1);
            hashmap.add(s4, s5);
        }

        for (int j = 1; j <= nMaxCounters; j++) {
            String s2 = getProperty(PROPERTY_NAME_COUNTER_ID + j);
            if (s2.length() <= 0) {
                break;
            }
            String s3 = (String) hashmap.get(s2);
            if (s3 != null) {
                setProperty(PROPERTY_NAME_COUNTER_VALUE + j, s3);
                if (s.length() > 0) {
                    s = s + ", ";
                }
            }
            s = s + getProperty(PROPERTY_NAME_COUNTER_NAME + j) + "="
                    + getProperty(PROPERTY_NAME_COUNTER_VALUE + j);
        }

        return s;
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        Array array = new Array();
        int i = exec(GET_BROWSE_DATA, null, array);
        if (array.size() == 0) {
            stringbuffer.append("Failed to get browse data. Error code: " + i);
            return "";
        }
        if (i != 0) {
            stringbuffer.append("Failed to get browse data. Error code: " + i
                    + ". Description: ");
            for (int j = 0; j < array.size(); j++) {
                stringbuffer.append((String) array.get(j));
            }

            return "";
        }
        int k = 0;
        for (int l = 0; l < array.size(); l++) {
            k += ((String) array.get(l)).length();
        }

        if (k <= 0) {
            stringbuffer.append("Failed to get browse data. Unspecified error");
            return "";
        }
        StringBuffer stringbuffer1 = new StringBuffer(k);
        for (int i1 = 0; i1 < array.size(); i1++) {
            stringbuffer1.append((String) array.get(i1));
        }

        return stringbuffer1.toString().trim();
    }

    protected Array getConnArgs() {
        Array array = getConnectionProperties();
        int i = array.size();
        Array array1 = new Array();
        for (int j = 0; j < i; j++) {
            StringProperty stringproperty = (StringProperty) array.get(j);
            String s = stringproperty.getName().substring(1);
            array1.add(s + "=" + getProperty(stringproperty));
        }

        return array1;
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty == getPropertyObject(PROPERTY_NAME_BROWSABLE)) {
            String s1 = getProperty(PROPERTY_NAME_COUNTER_ID + 1);
            if (s1.length() <= 0) {
                String s2 = httprequest.getValue("browseDataError");
                if (s2 != null && s2.length() > 0) {
                    hashmap.put(stringproperty, s2);
                } else {
                    hashmap.put(stringproperty, "No counters selected");
                }
            }
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    protected abstract String getMonDll();

    protected String getTargetNode() {
        return null;
    }

    protected int getExeTimeout() {
        return exeTimeout;
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_DispatcherMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    static {
        nMaxCounters = 30;
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_DispatcherMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 30;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(
                nMaxCounters, true);
        exeTimeout = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_browsableExeTimeout"));
        if (exeTimeout <= 0) {
            exeTimeout = 45000;
        }
        String s = (COM.dragonflow.SiteView.BrowsableMdrvBase.class).getName();
        addProperties(s, astringproperty);
    }
}