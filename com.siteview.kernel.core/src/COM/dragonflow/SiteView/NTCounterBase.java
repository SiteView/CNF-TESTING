/*
 * 
 * Created on 2005-2-16 15:14:00
 *
 * NTCounterBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>NTCounterBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.PropertiedObject;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.PerfCounter;
import COM.dragonflow.Utils.PerfResult;
import COM.dragonflow.Utils.SSHCommandLine;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// ServerMonitor, ISelectableCounter, Platform, AtomicMonitor,
// Machine, TopazAPI, MasterConfig

public abstract class NTCounterBase extends ServerMonitor implements
        ISelectableCounter {

    public static StringProperty pMeasurements[];

    private static StringProperty pValues[];

    private static StringProperty pLastMeasurements[];

    private static StringProperty pLastBaseMeasurements[];

    private static StringProperty pLastMeasurementTime;

    private static StringProperty pLastMeasurementTicks;

    private Map mIDMap;

    public static final String notAvailable = "n/a";

    public static final String pageURL = "/SiteView/cgi/go.exe/SiteView/?page=counter";

    public static int nMaxCounters;

    static boolean showDebug = false;

    public NTCounterBase() {
        mIDMap = null;
    }

    public abstract String getCounterObjects();

    public abstract String getDefaultInstance();

    public abstract String getDefaultCounters();

    public abstract String getReturnURL();

    public abstract String getCountersContent();

    public abstract StringProperty getCountersProperty();

    public abstract void setCountersContent(String s);

    public void increaseCounters(int i) {
        for (int j = nMaxCounters; j < i; j++) {
            StringProperty stringproperty = new StringProperty("value" + j);
            stringproperty.setDisplayText("Counter " + (j + 1) + " Value",
                    "The NT Performance Counter");
            stringproperty.setStateOptions(j + 1);
            PropertiedObject.addPropertyToPropertyMap(
                    "COM.dragonflow.SiteView.NTCounterBase", stringproperty);
        }

        nMaxCounters = i;
    }

    public String getHostname() {
        return getProperty(pMachineName);
    }

    public boolean remoteCommandLineAllowed() {
        return false;
    }

    public void initialize(HashMap hashmap) {
        super.initialize(hashmap);
        setCountersContent(getCountersContent());
    }

    public String getCountersParameter(String s, String s1) {
        String s2 = "";
        Array array = getAvailableCounters();
        boolean flag = true;
        String as[] = TextUtils.split(s, ",");
        int i = 0;
        label0: for (int j = 0; j < as.length; j++) {
            String s3 = as[j].trim();
            if (i >= nMaxCounters) {
                break;
            }
            int k = 0;
            do {
                if (k >= array.size()) {
                    continue label0;
                }
                String s4 = ((PerfCounter) array.at(k)).object + " -- ";
                s4 = s4 + ((PerfCounter) array.at(k)).counterName;
                if (((PerfCounter) array.at(k)).instance.length() > 0) {
                    s4 = s4 + " -- " + ((PerfCounter) array.at(k)).instance;
                }
                if (s3.equals(s4)) {
                    i++;
                    if (flag) {
                        s2 = s2 + k;
                        flag = false;
                    } else {
                        s2 = s2 + "," + k;
                    }
                    continue label0;
                }
                k++;
            } while (true);
        }

        return s2;
    }

    public String replaceDisplayPropertiesForURL(String s, String s1) {
        String s2 = getCountersProperty().getDescription();
        String s3 = "";
        boolean flag = false;
        int i = s2.indexOf("&" + s);
        if (s1.length() > 0) {
            flag = true;
        }
        if (i == -1) {
            i = s2.indexOf("<A HREF");
            i = (i += 7) + s2.substring(i).indexOf(">");
            s3 = s2.substring(0, i - 1);
            if (flag) {
                s3 = s3 + "&" + s + "=" + s1;
            }
            s3 = s3 + s2.substring(i - 1);
        } else {
            int j = s2.substring(i + 1).indexOf("&");
            if (j == -1) {
                j = s2.substring(i + 1).indexOf(">") - 1;
            }
            s3 = s2.substring(0, i);
            if (flag) {
                s3 = s3 + "&" + s + "=" + s1;
            }
            s3 = s3 + s2.substring(i + j + 1);
        }
        getCountersProperty().setDisplayText("Counters", s3);
        return s3;
    }

    public String getPropertyName(StringProperty stringproperty) {
        String s = stringproperty.getName();
        String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
        if (s1.length() == 0) {
            s1 = s;
        }
        return s1;
    }

    public synchronized HashMap getLabels() {
        HashMap hashmap = new HashMap();
        hashmap = new HashMap();
        Array array = getCounters();
        for (int i = 0; i < array.size(); i++) {
            PerfCounter perfcounter = (PerfCounter) array.at(i);
            hashmap.add("Counter " + (i + 1) + " Value", perfcounter.object
                    + " : " + perfcounter.counterName + ":"
                    + perfcounter.instance);
        }

        return hashmap;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        int i = getActiveCounters();
        for (int j = 0; j < i; j++) {
            if (getProperty("value1").length() > 0) {
                array.add(getPropertyObject("value" + j));
            }
        }

        return array.elements();
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        int i = getActiveCounters();
        for (int j = 0; j < i; j++) {
            if (getProperty("value1").length() > 0) {
                array.add(getPropertyObject("value" + j));
            }
        }

        return array;
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    synchronized Array getCounters() {
        Array array = new Array();
        String as[] = TextUtils.split(getCountersContent(), ",");
        for (int i = 0; i < as.length; i++) {
            PerfCounter perfcounter = new PerfCounter();
            String s = new String(as[i]);
            String as1[] = { "", "", "" };
            for (int j = 0; j < 3; j++) {
                int k = s.indexOf(" -- ");
                if (k > 0) {
                    as1[j] = s.substring(0, k);
                    s = s.substring(k + 4);
                } else {
                    as1[j] = s;
                    s = "";
                }
            }

            perfcounter.object = as1[0];
            perfcounter.counterName = as1[1];
            perfcounter.instance = as1[2];
            array.add(perfcounter);
        }

        return array;
    }

    int getActiveCounters() {
        Array array = getCounters();
        if (array == null) {
            return 0;
        }
        int i = array.size();
        if (i > nMaxCounters) {
            i = nMaxCounters;
        }
        return i;
    }

    public Array getAvailableCounters() {
        Array array = new Array();
        StringBuffer stringbuffer = new StringBuffer();
        String as[] = TextUtils.split(getCounterObjects(), ",");
        for (int i = 0; i < as.length; i++) {
            array.add(as[i]);
        }

        return getPerfCounters(getProperty(pMachineName), array, stringbuffer,
                "");
    }

    public void setCountersPropertyValue(AtomicMonitor atomicmonitor, String s) {
        setCountersContent(s);
    }

    public static Array getPerfCounters(String s, Array array,
            StringBuffer stringbuffer, String s1) {
        return getPerfCounters(s, array, stringbuffer, s1, null);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param array
     * @param stringbuffer
     * @param s1
     * @param atomicmonitor
     * @return
     */
    public static Array getPerfCounters(String s, Array array,
            StringBuffer stringbuffer, String s1, AtomicMonitor atomicmonitor) {
        // TODO need review
        boolean flag = false;
        Array array1 = new Array();
        String s2 = "";
        for (int i = 0; i < array.size(); i++) {
            s2 = s2 + "-o \"" + (String) array.at(i) + "\" ";
        }

        s2 = s2.trim();
        String s3 = Platform.perfexCommand(s) + " " + s2;
        CommandLine commandline = new CommandLine();
        if ((Platform.platformDebugTrace & Platform.kDebugPerfex) != 0) {
            LogManager.log("RunMonitor", "NTCounterBase Command: " + s3);
        }
        Array array2 = commandline.exec(s3, Platform.getLock(s));
        int j = commandline.getExitValue();
        if (showDebug) {
            LogManager.log("RunMonitor", "Counter Monitor, " + s + ", " + j);
        }
        if (j != 0) {
            LogManager.log("Error", "Counter Enumeration error, for "
                    + array
                    + " on "
                    + s
                    + (atomicmonitor == null ? "" : ", monitor id: "
                            + atomicmonitor.getFullID()));
        } else {
            String as[] = TextUtils.split(s1, ",");
            int k = as.length;
            if (k <= 0) {
                k = 1;
            }
            for (int l = 0; l < k; l++) {
                String s4 = "";
                if (as.length > 0) {
                    s4 = as[l].trim();
                }
                Enumeration enumeration = array2.elements();
                boolean flag1 = false;
                boolean flag2 = false;
                PerfCounter perfcounter = null;
                String s5 = "";
                String s6 = "";
                label0: do {
                    if (!enumeration.hasMoreElements()) {
                        break;
                    }
                    flag = false;
                    String s7 = ((String) enumeration.nextElement()).trim();
                    int i1 = s7.indexOf("object:");
                    if (i1 >= 0) {
                        flag2 = false;
                        flag1 = false;
                        s5 = s7.substring(s7.indexOf(": ") + 2, s7
                                .lastIndexOf(" "));
                        int k1 = 0;
                        do {
                            if (k1 >= array.size()) {
                                continue label0;
                            }
                            if (array.at(k1).equals(s5)) {
                                flag2 = true;
                                continue label0;
                            }
                            k1++;
                        } while (true);
                    }
                    if (s7.startsWith("name:")) {
                        flag1 = true;
                        s6 = s7.substring(s7.indexOf(": ") + 2);
                        continue;
                    }
                    if (!flag1 || !flag2) {
                        continue;
                    }
                    if (s7.equals("")) {
                        flag1 = false;
                        continue;
                    }
                    i1 = s7.indexOf(":");
                    if (i1 == -1 || s7.endsWith("_BASE")) {
                        continue;
                    }
                    flag = true;
                    perfcounter = new PerfCounter();
                    perfcounter.counterName = s7.substring(0, i1);
                    perfcounter.object = s5;
                    perfcounter.instance = s6;
                    String s8 = perfcounter.object + " -- ";
                    s8 = s8 + perfcounter.counterName;
                    if (perfcounter.instance.length() > 0) {
                        s8 = s8 + " -- " + perfcounter.instance;
                    }
                    if (as.length > 0) {
                        flag = false;
                        if (!s4.equals(s8)) {
                            continue;
                        }
                        flag = true;
                        break;
                    }
                    array1.add(perfcounter);
                    flag = false;
                } while (true);
                
                if (flag) {
                    array1.add(perfcounter);
                    continue;
                }
                PerfCounter perfcounter1 = new PerfCounter();
                int j1 = s4.indexOf("--");
                if (j1 != -1) {
                    perfcounter1.object = s4.substring(0, j1 - 1);
                    s4 = s4.substring(j1 + 3);
                    int l1 = s4.indexOf("--");
                    perfcounter1.instance = s4.substring(l1 + 3, s4.length());
                    s4 = s4.substring(0, l1 - 1);
                    perfcounter1.counterName = s4 + " (default not available)";
                    array1.add(perfcounter1);
                }
            }

        }
        return array1;
    }

    public static Array getPerfData(String s, Array array,
            StringBuffer stringbuffer, boolean flag,
            AtomicMonitor atomicmonitor, Array array1) {
        return getPerfData(s, array, stringbuffer, flag, atomicmonitor, array1,
                null);
    }

    public static Array getPerfData(String s, Array array,
            StringBuffer stringbuffer, boolean flag,
            AtomicMonitor atomicmonitor, Array array1, Map map) {
        boolean bMapOk = map != null;
        jgl.HashMap hashmap = new jgl.HashMap();
        int iCounters = array.size() > nMaxCounters ? nMaxCounters : array.size();
        String strPerfTime100nSec = "";
        String strPerfFreq = "";
        String strPerfTime = "";
        int i_43_ = 0;
        boolean[] bCounterAv = new boolean[iCounters];
        String[] strCountType = new String[iCounters];
        String[] strCountValue = new String[iCounters];
        String[] strUnit = new String[iCounters];
        StringBuffer strCmds = new StringBuffer();
        jgl.HashMap mapObject = new jgl.HashMap();
        for (int i = 0; i < iCounters; i++) {
        	bCounterAv[i] = false;
            PerfCounter perfcounter = (PerfCounter) array.at(i);
            List list = (List) mapObject.get(perfcounter.object);
            if (list == null)
            	mapObject.put(perfcounter.object, list = new ArrayList());
            list.add(perfcounter);
        }
        if (bMapOk) {
            HashMap hashmap_49_ = new HashMap();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                StringBuffer stringbuffer_50_ = (StringBuffer) hashmap_49_
                        .get(entry.getValue());
                if (stringbuffer_50_ == null)
                    hashmap_49_.put(entry.getValue(),
                            stringbuffer_50_ = new StringBuffer());
                if (stringbuffer_50_.length() > 0)
                    stringbuffer_50_.append(",");
                stringbuffer_50_.append(entry.getKey());
            }
            iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                List list = (List) mapObject.get(entry.getValue());
                if (list != null) {
                    HashSet hashset = new HashSet();
                    strCmds.append("-oic \"").append(entry.getKey())
                            .append("/");
                    Iterator iterator_51_ = list.iterator();
                    while (iterator_51_.hasNext()) {
                        PerfCounter perfcounter = (PerfCounter) iterator_51_
                                .next();
                        if (perfcounter.instance != null
                                && !perfcounter.instance.equals("")
                                && !hashset.contains(perfcounter.instance)) {
                            if (strCmds.charAt(strCmds
                                    .length() - 1) != '/')
                            	strCmds.append(",");
                            strCmds.append(perfcounter.instance);
                            hashset.add(perfcounter.instance);
                        }
                    }
                    strCmds.append("/");
                    iterator_51_ = list.iterator();
                    while (iterator_51_.hasNext()) {
                        PerfCounter perfcounter = (PerfCounter) iterator_51_
                                .next();
                        if (strCmds
                                .charAt(strCmds.length() - 1) != '/')
                        	strCmds.append(",");
                        strCmds.append(hashmap_49_
                                .get(perfcounter.counterName));
                    }
                    strCmds.append("\" ");
                }
            }
        } else {
            Enumeration enumeration = mapObject.keys();
            while (enumeration.hasMoreElements())
            	strCmds.append("-o ").append("\"").append(
                        enumeration.nextElement()).append("\" ");
        }
        String string_52_ = (Platform.perfexCommand(s) + " " + strCmds
                .toString().trim());
        //if ((Platform.platformDebugTrace & Platform.kDebugPerfex) != 0)
            LogManager
                    .log("RunMonitor", "NTCounterBase Command: " + string_52_);
        String string_53_ = s;
        if (string_53_.startsWith("\\\\"))
            string_53_ = string_53_.substring(2);
        Machine machine = Machine.getNTMachine(string_53_);
        Object object = null;
        boolean bool_54_ = false;
        Array array_55_;
        int iExitCode;
        if (machine != null && Machine.isNTSSH(string_53_)) {
            if (string_52_.indexOf("\\\\" + string_53_) > 0)
                string_52_ = TextUtils.replaceString(string_52_, "\\\\"
                        + string_53_, "");
            string_52_ = string_52_.substring(string_52_.indexOf("perfex"));
            SSHCommandLine sshcommandline = new SSHCommandLine();
            array_55_ = sshcommandline.exec(string_52_, machine, false);
            iExitCode = sshcommandline.exitValue;
        } else {
            CommandLine commandline = new CommandLine();
            array_55_ = commandline.exec(string_52_, Platform.getLock(s),
                    atomicmonitor);
            iExitCode = commandline.getExitValue();
        }
        if (array1 != null)
            array1.copy(array_55_);
        if (flag)
            LogManager.log("RunMonitor", ("Counter Monitor, " + s + ", "
                    + iExitCode + ", " + string_52_));
        if (iExitCode != 0) {
            if (iExitCode == 5)
            	iExitCode = 401;
            else if (iExitCode == 53)
            	iExitCode = kURLNoConnectionError;
            stringbuffer.append(lookupStatus((long) iExitCode) + ", ");
            LogManager.log("Error", ("Counter Monitor error, "
                    + (Object) stringbuffer + iExitCode + ", " + s
                    + ", monitor id: " + atomicmonitor.getFullID()));
            Enumeration enumeration = array_55_.elements();
            if (flag) {
                while (enumeration.hasMoreElements())
                    LogManager.log("Error",
                            ("Counter Monitor error     :"
                                    + enumeration.nextElement()
                                    + ", monitor id: " + atomicmonitor
                                    .getFullID()));
            }
        } else {
            Enumeration enumeration = array_55_.elements();
            boolean bInstance = false;
            String strName = "";
            String strObject = "";
            while (enumeration.hasMoreElements()) {
                String strLine = ((String) enumeration.nextElement()).trim();
                if (flag)
                    LogManager.log("RunMonitor", " <-- " + strLine);
                
                if (strPerfFreq.length() == 0
                        && (strLine.startsWith("PerfFreq: ") || strLine
                                .indexOf("PerfFreq: ") >= 0))
                	strPerfFreq = strLine.substring(
                			strLine.lastIndexOf(":") + 1).trim();
                
                if (strPerfTime100nSec.length() == 0
                        && (strLine.startsWith("PerfTime100nSec: ") || strLine
                                .indexOf("PerfTime100nSec: ") >= 0))
                	strPerfTime100nSec = strLine.substring(
                			strLine.lastIndexOf(":") + 1).trim();
                
                if (strPerfTime.length() == 0
                        && (strLine.startsWith("PerfTime: ") || strLine
                                .indexOf("PerfTime: ") >= 0))
                	strPerfTime = strLine.substring(
                			strLine.lastIndexOf(":") + 1).trim();
                
                if (strLine.startsWith("name: ")) {
                	strName = strLine
                            .substring(strLine.indexOf(": ") + 2);
                    if (!TextUtils.isNumber(strName)) {
//                        if (TopazAPI.getKey(hashmap, string_58_) != null) 
//                        {
//                            Enumeration enumeration_61_ = hashmap
//                                    .keys(string_58_);
//                            int i_62_ = 0;
//                            while (enumeration_61_.hasMoreElements()) {
//                                enumeration_61_.nextElement();
//                                i_62_++;
//                            }
//                            String string_63_ = string_58_ + "#" + i_62_;
//                            hashmap.put(string_63_, string_58_);
//                            string_58_ = string_63_;
//                        } else
                            hashmap.put(strName, strName);
                    }
                }
                
                if (strLine.startsWith("object: ")) {
                	strObject = strLine.substring(
                    		strLine.indexOf(": ") + 2, strLine
                                    .lastIndexOf(" "));
                    if (bMapOk)
                    	strObject = (String) map.get(strObject);
                    hashmap.clear();
                }
                
                if (bInstance) {
                    if (strLine.endsWith("_BASE")) {
                        String[] strings_64_ = TextUtils.split(strLine);
                        if (strings_64_.length > 2)
                        	strUnit[i_43_] = strings_64_[strings_64_.length - 2];
                    }
                    bInstance = false;
                }
                
                for (int j = 0; j < iCounters; j++) {
                    PerfCounter perfcounter = (PerfCounter) array.at(j);
                    
                    if (perfcounter.object.equalsIgnoreCase(strObject)) {
                        boolean bool_66_ = false;
                        String strCounterName = perfcounter.counterName;
                        
                        String strLineTmp = strLine;
                        int iPos2 = strLineTmp.lastIndexOf(':');
                        
                        if (iPos2 != -1)
                        	strLineTmp = strLine.substring(0, iPos2);
                        
                        if (bMapOk)
                        	strLineTmp = (String) map.get(strLineTmp);
                        
                        if (strCounterName.endsWith("*")) {
                            if (strLineTmp != null)
                                bool_66_ = (strLineTmp
                                        .startsWith(strCounterName.substring(0,
                                                (strCounterName.length() - 1))));
                        } else
                            bool_66_ = strCounterName.equals(strLineTmp);
                        
                        if (bool_66_) {
                            boolean bInstanceAv = false;
                            boolean bool_71_ = false;
                            
                            if (perfcounter.instance.equals("*total*")) {
                                bool_71_ = true;
                                bInstanceAv = true;
                            } else if (!bCounterAv[j]) {
                                if (perfcounter.instance.length() == 0)
                                	bInstanceAv = true;
                                else if (perfcounter.instance
                                        .equalsIgnoreCase(strName))
                                	bInstanceAv = true;
                            }
                            
                            if (bInstanceAv) {
                            	bCounterAv[j] = true;
                            	bInstance = true;
                                i_43_ = j;
                                int iPos = strLine.lastIndexOf(":");
                                if (iPos >= 0) {
                                    String[] strings_73_ = (TextUtils
                                            .split(strLine
                                                    .substring(iPos + 1)));
                                    if (strings_73_.length == 2)
                                    	strCountType[j] = strings_73_[1];
                                    if (strings_73_.length == 3)
                                    	strCountType[j] = strings_73_[2];
                                    if (bool_71_)
                                    	strCountValue[j] = ""
                                                + (TextUtils
                                                        .toLong(strings_73_[0]) + (TextUtils
                                                        .toLong(strCountValue[j])));
                                    else
                                    	strCountValue[j] = strings_73_[0];
                                    strUnit[j] = "";
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        Array array_74_ = new Array();
        array_74_.add(strPerfTime100nSec);
        array_74_.add(strPerfTime);	
        array_74_.add(strPerfFreq);
        for (int i = 0; i < iCounters; i++) {
            if (bCounterAv[i]) {
                array_74_.add(strCountType[i]);
                array_74_.add(strCountValue[i]);
                array_74_.add(strUnit[i]);
            } else {
                String string_76_ = "";
                array_74_.add(string_76_);
                array_74_.add(string_76_);
                array_74_.add(string_76_);
            }
            if (flag) {
                PerfCounter perfcounter = (PerfCounter) array.at(i);
                LogManager.log("RunMonitor", ("Counter Monitor, "
                        + perfcounter.counterName + "/" + perfcounter.object
                        + "/" + perfcounter.instance + ", " + bCounterAv[i]
                        + ", " + strCountValue[i] + ", " + strUnit[i]
                        + ", " + strCountType[i]));
            }
        }
        return array_74_;
    }

    public static Map getIDCacheForCounters(String s, Array array) {
        java.util.HashMap hashmap = new java.util.HashMap();
        java.util.HashMap hashmap1 = new java.util.HashMap();
        for (int i = 0; i < array.size(); i++) {
            PerfCounter perfcounter = (PerfCounter) array.at(i);
            hashmap1.put(perfcounter.counterName.toLowerCase(),
                    perfcounter.counterName);
            hashmap1.put(perfcounter.object.toLowerCase(), perfcounter.object);
        }

        String s1 = Platform.perfexCommand(s) + " -map";
        Array array1 = (new CommandLine()).exec(s1);
        if (array1 != null) {
            String s2 = "id:";
            String s3 = "name:";
            for (int j = 0; j < array1.size(); j++) {
                String s4 = (String) array1.at(j);
                if (!s4.startsWith(s2)) {
                    continue;
                }
                try {
                    int k = s4.indexOf(" ");
                    String s5 = s4.substring(s2.length(), k);
                    k = s4.indexOf(s3);
                    String s6 = s4.substring(k + s3.length());
                    String s7 = (String) hashmap1.get(s6.toLowerCase());
                    if (s7 != null) {
                        hashmap.put(s5, s7);
                    }
                } catch (Exception exception) {
                }
            }

        }
        if (hashmap.size() == 0) {
            return null;
        } else {
            return hashmap;
        }
    }

    protected boolean update() {
        String s = getProperty(pMachineName);
        Array array = getCounters();
        if (array.size() == 0 || array.size() > nMaxCounters) {
            setProperty(pLastMeasurementTime, 0);
            for (int i = 0; i < nMaxCounters; i++) {
                setProperty("value" + i, "n/a");
                setProperty("measurement" + i, "0");
                setProperty("lastMeasurement" + i, "0");
                setProperty(pNoData, "n/a");
            }

            setProperty(
                    pStateString,
                    array.size() != 0 ? "You have exceeded the maximum number of counters available"
                            : "No counters selected");
            return false;
        } else {
            return getPerformanceData(s, array);
        }
    }

    protected Map checkIdMap(String s, Array array) {
        if (mIDMap == null) {
            mIDMap = getIDCacheForCounters(s, array);
        }
        return mIDMap;
    }

    public boolean getPerformanceData(String s, Array array) {
        return getPerformanceData(s, array, nMaxCounters, 1.0F, "");
    }

    protected boolean getPerformanceData(String s, Array array, long l,
            float f, String s1) {
        PerfResult perfresult = new PerfResult();
        perfresult.measurementTime = 0L;
        perfresult.measurementFreq = 1L;
        perfresult.measurementTicks = 0L;
        perfresult.lastMeasurementTime = TextUtils
                .toLong(getProperty(pLastMeasurementTime));
        perfresult.lastMeasurementTicks = TextUtils
                .toLong(getProperty(pLastMeasurementTicks));
        String s2 = jdbcDateFormat(Platform.makeDate());
        Enumeration enumeration = null;
        String s3 = "";
        Map map = null;
        map = checkIdMap(s, array);
        boolean flag = true;
        label0: for (int i = 0; flag && i < 2; i++) {
            if (i != 0) {
                Platform.sleep(4000L);
            }
            StringBuffer stringbuffer = new StringBuffer();
            Array array1 = null;
            if (monitorDebugLevel == 3) {
                array1 = new Array();
            }
            Array array2 = getPerfData(s, array, stringbuffer, showDebug, this,
                    array1, map);
            s3 = stringbuffer.toString();
            enumeration = array2.elements();
            if (enumeration.hasMoreElements()) {
                perfresult.measurementTime = TextUtils
                        .toLong((String) enumeration.nextElement());
            }
            if (enumeration.hasMoreElements()) {
                perfresult.measurementTicks = TextUtils
                        .toLong((String) enumeration.nextElement());
            }
            if (enumeration.hasMoreElements()) {
                perfresult.measurementFreq = TextUtils
                        .toLong((String) enumeration.nextElement());
            }
            flag = perfresult.measurementTime - perfresult.lastMeasurementTime <= 0L
                    || perfresult.measurementTicks
                            - perfresult.lastMeasurementTicks <= 0L
                    || perfresult.lastMeasurementTime <= 0L
                    || perfresult.lastMeasurementTicks <= 0L;
            if (i > 0) {
                break;
            }
            if (!flag) {
                continue;
            }
            perfresult.lastMeasurementTime = perfresult.measurementTime;
            perfresult.lastMeasurementTicks = perfresult.measurementTicks;
            int k = 0;
            do {
                if (!enumeration.hasMoreElements()) {
                    continue label0;
                }
                String s5 = (String) enumeration.nextElement();
                if (s5.equals("n/a")) {
                    enumeration.nextElement();
                    enumeration.nextElement();
                    setProperty("lastMeasurement" + k, "0");
                    setProperty("lastBaseMeasurement" + k, "0");
                    k++;
                } else {
                    setProperty("lastMeasurement" + k, (String) enumeration
                            .nextElement());
                    setProperty("lastBaseMeasurement" + k, (String) enumeration
                            .nextElement());
                    k++;
                }
            } while (true);
        }

        for (int j = 0; enumeration.hasMoreElements(); j++) {
            PerfCounter perfcounter = (PerfCounter) array.at(j);
            perfresult.precision = 2;
            perfresult.percent = false;
            perfresult.perSec = false;
            perfresult.counterType = (String) enumeration.nextElement();
            perfresult.measurement = TextUtils.toLong((String) enumeration
                    .nextElement());
            perfresult.baseMeasurement = TextUtils.toLong((String) enumeration
                    .nextElement());
            perfresult.lastMeasurement = TextUtils
                    .toLong(getProperty("lastMeasurement" + j));
            perfresult.lastBaseMeasurement = TextUtils
                    .toLong(getProperty("lastBaseMeasurement" + j));
            perfresult.value = (0.0F / 0.0F);
            if (!perfresult.counterType.equals("n/a")) {
                if (!flag && perfresult.measurement >= 0L) {
                    perfresult.calcPerfResult();
                }
                if (showDebug) {
                    LogManager
                            .log(
                                    "RunMonitor",
                                    "Counter Monitor, type="
                                            + perfresult.counterType
                                            + ", value="
                                            + perfresult.value
                                            + ", delta="
                                            + (perfresult.measurement - perfresult.lastMeasurement)
                                            + ", deltaBase="
                                            + (perfresult.baseMeasurement - perfresult.lastBaseMeasurement)
                                            + ", deltaTime="
                                            + (perfresult.measurementTime - perfresult.lastMeasurementTime));
                }
            }
            if (!stillActive()) {
                continue;
            }
            synchronized (this) {
                logPerfValue(perfcounter, s2, perfresult.value);
                String s4 = "";
                if (Float.isNaN(perfresult.value)) {
                    setProperty(pLastMeasurementTime, 0);
                    setProperty("value" + j, "n/a");
                    setProperty("measurement" + j, "0");
                    setProperty("lastMeasurement" + j, "0");
                    s4 = perfcounter.counterName + " " + "n/a";
                } else {
                    perfresult.value *= f;
                    setProperty("value" + j, TextUtils.floatToString(
                            perfresult.value, perfresult.precision));
                    setProperty("lastMeasurement" + j, String
                            .valueOf(perfresult.measurement));
                    setProperty("lastBaseMeasurement" + j, String
                            .valueOf(perfresult.baseMeasurement));
                    setProperty("measurement" + j, getMeasurement(
                            getPropertyObject("value" + j), 10L));
                    s4 = perfcounter.counterName
                            + " = "
                            + TextUtils.floatToString(perfresult.value,
                                    perfresult.precision);
                    if (s1.length() > 0) {
                        s3 = s3 + " " + s1;
                    }
                    if (perfresult.percent) {
                        s4 = s4 + "%";
                    }
                    if (perfresult.perSec) {
                        s4 = s4 + "/sec";
                    }
                }
                if ((long) j < l) {
                    if (j > 0) {
                        s3 = s3 + ", ";
                    }
                    s3 = s3 + s4;
                } else if ((long) j == l) {
                    s3 = s3 + "...";
                }
            }
        }

        setProperty(pLastMeasurementTime, perfresult.measurementTime);
        setProperty(pLastMeasurementTicks, perfresult.measurementTicks);
        setProperty(pStateString, s3);
        return true;
    }

    void logPerfValue(PerfCounter perfcounter, String s, float f) {
    }

    String jdbcDateFormat(Date date) {
        return "";
    }

    protected String getTestMachine() {
        return "\\\\TESTWIN2K1.qa.Dragonflow.com";
    }

    public int getCostInLicensePoints() {
        int i = getActiveCounters();
        return 1 * i;
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        return getPropertyName(stringproperty).replace('+', ' ');
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public String getTopazCounterLabel(StringProperty stringproperty) {
        return GetPropertyLabel(stringproperty, true);
    }

    static {
        nMaxCounters = 0;
        HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap,
                "_ApplicationMonitorMaxCounters"));
        if (nMaxCounters <= 0) {
            nMaxCounters = 20;
        }
        Array array = new Array();
        pValues = new StringProperty[nMaxCounters];
        pMeasurements = new StringProperty[nMaxCounters];
        pLastMeasurements = new StringProperty[nMaxCounters];
        pLastBaseMeasurements = new StringProperty[nMaxCounters];
        for (int i = 0; i < nMaxCounters; i++) {
            pValues[i] = new NumericProperty("value" + i);
            pValues[i].setDisplayText("Counter " + (i + 1) + " Value",
                    "The NT Performance Counter");
            pValues[i].setStateOptions(i + 1);
            array.add(pValues[i]);
            pMeasurements[i] = new NumericProperty("measurement" + i);
            array.add(pMeasurements[i]);
            pLastMeasurements[i] = new NumericProperty("lastMeasurement" + i);
            array.add(pLastMeasurements[i]);
            pLastBaseMeasurements[i] = new NumericProperty(
                    "lastBaseMeasurement" + i);
            array.add(pLastBaseMeasurements[i]);
        }

        pLastMeasurementTime = new NumericProperty("lastMeasurementTime");
        pLastMeasurementTicks = new NumericProperty("lastMeasurementTicks");
        array.add(pLastMeasurementTime);
        array.add(pLastMeasurementTicks);
        StringProperty astringproperty[] = new StringProperty[array.size()];
        for (int j = 0; j < array.size(); j++) {
            astringproperty[j] = (StringProperty) array.at(j);
        }

        addProperties("COM.dragonflow.SiteView.NTCounterBase", astringproperty);
    }
}
