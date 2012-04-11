/*
 * 
 * Created on 2005-2-15 11:55:12
 *
 * BrowsablePdhBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsablePdhBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.TextUtils;
import COM.dragonflow.Utils.PDH.PDHRawCounterCache;

// Referenced classes of package COM.dragonflow.SiteView:
// BrowsableBase, MasterConfig

public abstract class BrowsablePdhBase extends BrowsableBase {

    static int nMaxCounters;

    static StringProperty pPreviousCounters;

    public BrowsablePdhBase() {
    }

    protected boolean update() {
        PDHRawCounterCache pdhrawcountercache = (PDHRawCounterCache) getPropertyAsObject(pPreviousCounters);
        boolean flag = true;
        boolean flag1 = true;
        if (getFullID().equals("1")) {
            flag1 = false;
        }
        Vector vector = new Vector();
        int i = 0;
        int j = 0;
        do {
            if (j >= nMaxCounters) {
                break;
            }
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
            if (s.length() <= 0) {
                break;
            }
            vector.add(s);
            i++;
            j++;
        } while (true);
        String as[] = new String[i];
        vector.toArray(as);
        String s1 = getHostname();
        if (pdhrawcountercache == null) {
            flag = false;
        } else {
            flag = pdhrawcountercache.checkValid(s1, as);
        }
        setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), i);
//        pdh_measurement_seq pdh_measurement_seq1 = new pdh_measurement_seq();
//        for (int k = 0; k < i; k++) {
//            String s2 = as[k];
//            String as1[] = ArgsPackagerUtil.unpackageArgsToStrArray(s2);
//            pdh_raw_counter pdh_raw_counter1 = null;
//            if (flag) {
//                pdh_raw_counter1 = pdhrawcountercache.get(k);
//            }
//            if (pdh_raw_counter1 == null) {
//                pdh_raw_counter1 = new pdh_raw_counter(1, "", "", "", 0);
//            }
//            if (as1.length > 2) {
//                pdh_measurement_seq1.add(new pdh_measurement(as1[0], as1[1],
//                        as1[2], 0.0D, false, pdh_raw_counter1));
//            } else {
//                pdh_measurement_seq1.add(new pdh_measurement(as1[0], "",
//                        as1[1], 0.0D, false, pdh_raw_counter1));
//            }
//        }
//
//        pdh_measurement_seq pdh_measurement_seq2 = new pdh_measurement_seq();
//        StringBuffer stringbuffer = new StringBuffer();
//        COM.dragonflow.PDHMonitor.xdr.pdh_connect pdh_connect = PDHSupport
//                .getXdrHost(s1);
        char c = '\u03E8';
//        if (!PDHSupport.sendPdhRequest(3, new pdh_run_measurement(getFullID(),
//                flag1, flag, c, pdh_connect, pdh_measurement_seq1),
//                pdh_measurement_seq2, stringbuffer)) {
//            setProperty(pStateString, stringbuffer.toString());
//            setProperty(pNoData, "n/a");
//            setProperty(pPreviousCounters, "");
//            return true;
//        }
        if (flag1) {
            pdhrawcountercache = new PDHRawCounterCache(s1);
        }
        for (int l = 1; l <= nMaxCounters; l++) {
            setProperty(PROPERTY_NAME_COUNTER_VALUE + l, "n/a");
        }

//        int i1 = 0;
//        for (int j1 = 0; j1 < pdh_measurement_seq2.size(); j1++) {
//            String as2[] = new String[3];
//            as2[0] = pdh_measurement_seq2.get(j1).get_object();
//            as2[1] = pdh_measurement_seq2.get(j1).get_instance();
//            String s3 = null;
//            if (as2[1].length() == 0) {
//                as2[1] = pdh_measurement_seq2.get(j1).get_counter();
//                s3 = ArgsPackagerUtil.packageArgs(as2, 0, 1);
//            } else {
//                as2[2] = pdh_measurement_seq2.get(j1).get_counter();
//                s3 = ArgsPackagerUtil.packageArgs(as2, 0, 2);
//            }
//            double d = pdh_measurement_seq2.get(j1).get_resultValue();
//            setProperty(PROPERTY_NAME_COUNTER_VALUE + (j1 + 1), TextUtils
//                    .floatToString((float) d, 2));
//            i1++;
//            if (flag1) {
//                pdh_raw_counter pdh_raw_counter2 = pdh_measurement_seq2.get(j1)
//                        .get_rawCounter();
//                pdhrawcountercache.put(s3, pdh_raw_counter2);
//            }
//        }

        if (flag1) {
            try {
                setPropertyWithObject(pPreviousCounters, pdhrawcountercache);
            } catch (IOException ioexception) {
                LogManager.logException(ioexception);
            }
        }
//        int k1 = i - i1;
		int k1 = 0;
        setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), k1);
        StringBuffer stringbuffer1 = new StringBuffer();
        for (int l1 = 1; l1 <= nMaxCounters; l1++) {
            String s4 = getProperty(PROPERTY_NAME_COUNTER_ID + l1);
            if (s4.length() <= 0) {
                continue;
            }
            if (stringbuffer1.length() > 0) {
                stringbuffer1.append(", ");
            }
            stringbuffer1.append(getProperty(PROPERTY_NAME_COUNTER_NAME + l1)
                    + "=" + getProperty(PROPERTY_NAME_COUNTER_VALUE + l1));
        }

//        stringbuffer1.append(stringbuffer);
        setProperty(pStateString, stringbuffer1);
        return true;
    }

    protected abstract List getBrowsableObjects();

    public String getBrowseData(StringBuffer stringbuffer) 
	{
        String string;
//        try {
//            Document document = DocumentBuilderFactory.newInstance()
//                    .newDocumentBuilder().newDocument();
//            Element browseDataElement = document.createElement("browse_data");
//            document.appendChild(browseDataElement);
//            List list = getBrowsableObjects();
//            StringBuffer stringbuffer_16_ = new StringBuffer();
//            Iterator iterator = list.iterator();
//            while (iterator.hasNext()) {
//                Pair pair = (Pair) iterator.next();
//                String name1 = (String) pair.first;
//                boolean flag = ((Boolean) pair.second).booleanValue();
//                Element objectElement1 = document.createElement("object");
//                objectElement1.setAttribute("name", name1);
//                browseDataElement.appendChild(objectElement1);
//                pdh_object_info var_pdh_object_info = new pdh_object_info();
//                pdh_connect var_pdh_connect = PDHSupport
//                        .getXdrHost(getHostname());
//                if (PDHSupport.sendPdhRequest(2, new pdh_get_object_info(
//                        var_pdh_connect, name1), var_pdh_object_info,
//                        stringbuffer_16_)) {
//                    pdh_counter_info_seq var_pdh_counter_info_seq = var_pdh_object_info
//                            .get_counters();
//                    pdh_string_seq var_pdh_string_seq = var_pdh_object_info
//                            .get_instances();
//                    for (int i = 0; i < var_pdh_string_seq.size(); i++) {
//                        String name2 = var_pdh_string_seq.get(i).get_strVal();
//                        Element objectElement2 = document
//                                .createElement("object");
//                        objectElement1.appendChild(objectElement2);
//                        objectElement2.setAttribute("name", name2);
//                        for (int j = 0; j < var_pdh_counter_info_seq.size(); j++) {
//                            pdh_counter_info var_pdh_counter_info = var_pdh_counter_info_seq
//                                    .get(j);
//                            Element counterElement1 = document
//                                    .createElement("counter");
//                            objectElement2.appendChild(counterElement1);
//                            counterElement1.setAttribute("name",
//                                    var_pdh_counter_info.get_counter());
//                            counterElement1.setAttribute("desc",
//                                    var_pdh_counter_info.get_description());
//                        }
//                    }
//                    if (!flag) {
//                        for (int i = 0; i < var_pdh_counter_info_seq.size(); i++) {
//                            pdh_counter_info var_pdh_counter_info = var_pdh_counter_info_seq
//                                    .get(i);
//                            Element counterElement2 = document
//                                    .createElement("counter");
//                            objectElement1.appendChild(counterElement2);
//                            counterElement2.setAttribute("name",
//                                    var_pdh_counter_info.get_counter());
//                            counterElement2.setAttribute("desc",
//                                    var_pdh_counter_info.get_description());
//                        }
//                    }
//                }
//            }
//            StringWriter stringwriter = new StringWriter();
//            Transformer transformer = TransformerFactory.newInstance()
//                    .newTransformer();
//            transformer.setOutputProperty("omit-xml-declaration", "yes");
//            transformer.transform(new DOMSource(document.getDocumentElement()),
//                    new StreamResult(stringwriter));
//            string = stringwriter.toString();
//        } catch (Exception exception) {
//            stringbuffer.append("Failed to get browse data. Unspecified error");
//            return "";
//        }
        return "";
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

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_DispatcherMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    public static void main(String args[]) {
        System.exit(0);
    }

    static {
        nMaxCounters = 30;
        pPreviousCounters = null;
        pPreviousCounters = new StringProperty("previousCounters");
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_DispatcherMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 30;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(
                nMaxCounters, true);
        String s = (COM.dragonflow.SiteView.BrowsablePdhBase.class).getName();
        StringProperty astringproperty1[] = new StringProperty[astringproperty.length + 1];
        astringproperty1[0] = pPreviousCounters;
        System.arraycopy(astringproperty, 0, astringproperty1, 1,
                astringproperty.length);
        addProperties(s, astringproperty1);
    }
}