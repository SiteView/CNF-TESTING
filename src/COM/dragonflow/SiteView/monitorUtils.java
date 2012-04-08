/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * monitorUtils.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>monitorUtils</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.CounterXMLProperty;
import COM.dragonflow.Properties.FrameFile;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Properties.XMLProperty;
import COM.dragonflow.Resource.SiteViewErrorCodes;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.SiteViewException.SiteViewParameterException;
import COM.dragonflow.Utils.ArgsPackagerUtil;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// Monitor, SiteViewGroup, Platform, PerfmonMonitorBase

public class monitorUtils {

    public static final String SUFFIX_COUNTER = "Counter";

    public static final String SUFFIX_INSTANCE = "Instance";

    public static final String SUFFIX_OBJECT = "Object";

    public static final String SUFFIX_DESCRIPTION = "Description";

    public static final String PREFIX_MSMT = "perfmonMsmt";

    public static final String PREFIX_COUNTER = "counter";

    public static final String PREFIX_INSTANCE = "instance";

    public static final String PARAM_OBJECT_NAME = "perfmonObject";

    public static final String PARAM_MSMT_COUNTER = "perfmonMsmtCounter";

    public static final String PARAM_MSMT_INSTANCE = "perfmonMsmtInstance";

    public static final String PARAM_MSMT_OBJECT = "perfmonMsmtObject";

    public static final String PARAM_MSMT_DESCRIPTION = "perfmonMsmtDescription";

    public monitorUtils() {
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param array
     * @param s
     * @return
     * @throws Exception
     */
    public static int findMonitorIndex(Array array, String s) throws Exception {
        if (s.equals("_config")) {
            return 0;
        }
        Enumeration enumeration = array.elements();
        int i = 0;
        enumeration.nextElement();
        i ++;
        int j = -1;
        while (enumeration.hasMoreElements()) {
            HashMap hashmap = (HashMap) enumeration.nextElement();
            if (Monitor.isMonitorFrame(hashmap) && hashmap.get("_id").equals(s)) {
                j = i;
                break;
            }
            i ++;
        }

        if (j == -1) {
            throw new Exception("monitor id (" + s + ") could not be found");
        } else {
            return j;
        }
    }

    public static HashMap findMonitor(Array array, String s) throws Exception {
        int i = findMonitorIndex(array, s);
        return (HashMap) array.at(i);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @return
     */
    public static int countFrameAndImageMonitors(String s) {
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        Monitor monitor = (Monitor) siteviewgroup.getElement(s);
        Enumeration enumeration = monitor.getMonitors();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            Monitor monitor1 = (Monitor) enumeration.nextElement();
//            if (((monitor1 instanceof COM.dragonflow.StandardMonitor.URLRemoteMonitor) || (monitor1 instanceof COM.dragonflow.StandardMonitor.URLRemoteSequenceMonitor))
//			
//            // XXX COM.dragonflow.StandardMonitor.URLRemoteMonitor
//                    // and
//                    // COM.dragonflow.StandardMonitor.URLRemoteSequenceMonitor
//                    // is missing.
//                    && (monitor1.getProperty("_getImages").equals("on") || monitor1.getProperty("_getFrames").equals("on"))) {
//                i ++;
//            }
        }
        return i;
    }

    public static String getPermission(HashMap hashmap, String s, String s1) {
        if (hashmap == null) {
            return "";
        }
        for (Enumeration enumeration = hashmap.values(s); enumeration.hasMoreElements();) {
            String s2 = (String) enumeration.nextElement();
            String as[] = TextUtils.split(s2, ",");
            if (as.length == 2 && as[0].equals(s1)) {
                return as[1];
            }
        }

        return "";
    }

    public static void getGroupMonitors(HashMap hashmap, HashMap hashmap1, Array array, String s) {
        String s1 = Platform.getRoot();
        try {
            if (array == null) {
                s1 = s1 + File.separator + "groups" + File.separator + s + ".mg";
                array = FrameFile.readFromFile(s1);
            }
            for (int i = 0; i < array.size(); i ++) {
                hashmap1 = (HashMap) array.at(i);
                String s2 = (String) hashmap1.get("_class");
                if (s2 != null) {
                    if (s2.equals("SubGroup")) {
                        String s3 = I18N.toDefaultEncoding((String) hashmap1.get("_group"));
                        getGroupMonitors(hashmap, hashmap1, null, s3);
                    } else {
                        hashmap.add(hashmap1, s);
                    }
                }
            }

        } catch (Exception exception) {
            LogManager.log("RunMonitor", "exception occurred: " + exception);
        }
    }

    public static String transformMgFormatToPerfmonMeasurements(String s) {
        String s1 = "";
        StringProperty astringproperty[] = PerfmonMonitorBase.createMeasurements(s);
        for (int i = 0; i < astringproperty.length; i ++) {
            if (i > 0) {
                s1 = s1 + ",";
            }
            String as[] = ArgsPackagerUtil.unpackageArgsToStrArray(astringproperty[i].getLabel());
            for (int j = 0; j < as.length; j ++) {
                if (j > 0) {
                    s1 = s1 + "\\";
                }
                s1 = s1 + as[j];
            }

        }

        return s1;
    }

    public static String transformPerfmonMeasurementsToMgFormat(String s) throws SiteViewException {
        Vector vector = new Vector();
        String as[] = TextUtils.split(s, ",");
        for (int i = 0; i < as.length; i ++) {
            String as1[] = TextUtils.split(as[i], "\\");
            if (as1.length < 2) {
                throw new SiteViewParameterException(SiteViewErrorCodes.ERR_PARAM_API_MONITOR_INVALID_COUNTER, new String[] { s });
            }
            if (as1.length == 2) {
                String s1 = as1[0];
                String s3 = as1[1];
                as1 = new String[3];
                as1[0] = s1;
                as1[1] = "";
                as1[2] = s3;
            }
            StringProperty stringproperty = new StringProperty("_perfmonMsmtProp" + i);
            String s4 = ArgsPackagerUtil.packageArgs(as1, 0, 2);
            stringproperty.setLabel(s4);
            stringproperty.setDescription("no description");
            vector.add(stringproperty);
        }

        StringProperty astringproperty[] = new StringProperty[vector.size()];
        vector.toArray(astringproperty);
        org.w3c.dom.Document document = CounterXMLProperty.encodeDocument(astringproperty);
        String s2 = XMLProperty.generateXMLString(document);
        return s2;
    }
}
