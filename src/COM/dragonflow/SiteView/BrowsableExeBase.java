/*
 * 
 * Created on 2005-2-15 11:44:14
 *
 * BrowsableExeBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>BrowsableExeBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.ArgsPackagerUtil;
import COM.dragonflow.Utils.CommandLine;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// BrowsableBase, Platform, MasterConfig, AtomicMonitor

public abstract class BrowsableExeBase extends BrowsableBase {

    static String exePath;

    static String GET_BROWSE_DATA = "GET_BROWSE_DATA";

    static String GET_NEW_DATA = "GET_NEW_DATA";

    static String VALID_RES_START_INDICATOR = "VALID_RES_START_INDICATOR";

    static String VALID_RES_END_INDICATOR = "VALID_RES_END_INDICATOR";

    static int exeTimeout;

    static int nMaxCounters;

    public BrowsableExeBase() {
    }

    protected boolean update() {
        Array array = new Array();
        int i = 0;
        do {
            if (i >= nMaxCounters) {
                break;
            }
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
            if (s.length() <= 0) {
                break;
            }
            array.add(s);
            i++;
        } while (true);
        i = array.size();
        Array array1 = getConnArgs();
        Array array2 = new Array();
        array2.add(GET_NEW_DATA);
        array2.add(array1);
        array2.add(array);
        String s1 = ArgsPackagerUtil.packageArgs(array2, 0, array2.size() - 1);
        String s2 = ArgsPackagerUtil.encodeArgs(s1);
        Array array3 = new Array();
        int j = exec(getExePath() + " " + s2, array3);
        int k = getFirstValidIndex(array3);
        for (int l = 1; l <= nMaxCounters; l++) {
            setProperty(PROPERTY_NAME_COUNTER_VALUE + l, "n/a");
        }

        if (stillActive()) {
            synchronized (this) {
                String s3 = "";
                setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                        i);
                if (k < 0) {
                    s3 = "Failed to get performance data. Error code: " + j;
                } else if (j != 0) {
                    StringBuffer stringbuffer = new StringBuffer();
                    for (int j1 = k; j1 < array3.size(); j1++) {
                        stringbuffer.append((String) array3.at(j1) + " ");
                    }

                    setProperty(pNoData, "n/a");
                    s3 = stringbuffer.toString();
                    setProperty(
                            getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                            i);
                } else {
                    int i1 = array3.size() - k;
                    setProperty(
                            getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR),
                            i - i1);
                    s3 = saveResultProps(array3, k);
                }
                setProperty(pStateString, s3);
            }
        }
        return true;
    }

    private int exec(String s, Array array) {
        int i = -1;
        BufferedReader bufferedreader = null;
        Process process = null;
        try {
            process = CommandLine.execSync(s);
            bufferedreader = FileUtils
                    .MakeInputReader(process.getInputStream());
            do {
                String s1 = bufferedreader.readLine();
                if (s1 == null) {
                    i = process.exitValue();
                    break;
                }
                if (s1.startsWith(VALID_RES_END_INDICATOR)) {
                    i = Integer.parseInt(s1.substring(VALID_RES_END_INDICATOR
                            .length()));
                    break;
                }
                array.add(s1);
            } while (true);
        } catch (Exception exception) {
            LogManager.log("error", exception.getMessage());
            i = -1;
            array.clear();
        }
        if (bufferedreader != null) {
            try {
                bufferedreader.close();
            } catch (IOException ioexception) {
                LogManager.log("error", ioexception.getMessage());
            }
        }
        if (process != null) {
            process.destroy();
        }
        return i;
    }

    String saveResultProps(Array array, int i) {
        String s = "";
        HashMap hashmap = new HashMap();
        for (int j = i; j < array.size(); j++) {
            String s1 = (String) array.at(j);
            int l = s1.indexOf('=');
            String s4 = s1.substring(0, l);
            String s5 = s1.substring(l + 1);
            hashmap.add(s4, s5);
        }

        for (int k = 1; k <= nMaxCounters; k++) {
            String s2 = getProperty(PROPERTY_NAME_COUNTER_ID + k);
            if (s2.length() <= 0) {
                break;
            }
            String s3 = (String) hashmap.get(s2);
            if (s3 != null) {
                setProperty(PROPERTY_NAME_COUNTER_VALUE + k, s3);
                if (s.length() > 0) {
                    s = s + ", ";
                }
            }
            s = s + getProperty(PROPERTY_NAME_COUNTER_NAME + k) + "="
                    + getProperty(PROPERTY_NAME_COUNTER_VALUE + k);
        }

        return s;
    }

    int getFirstValidIndex(Array array) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.size() - 1; i++) {
            if (array.at(i).equals(VALID_RES_START_INDICATOR)) {
                return i + 1;
            }
        }

        return -1;
    }

    public String getBrowseData(StringBuffer stringbuffer) {
        Array array = getConnArgs();
        Array array1 = new Array();
        array1.add(GET_BROWSE_DATA);
        array1.add(array);
        String s = ArgsPackagerUtil.encodeArgs(ArgsPackagerUtil.packageArgs(
                array1, 0, array1.size() - 1));
        Array array2 = new Array();
        int i = exec(getExePath() + " " + s, array2);
        int j = getFirstValidIndex(array2);
        if (j < 0) {
            stringbuffer.append("Failed to get browse data. Error code: " + i);
            return "";
        }
        if (i != 0) {
            stringbuffer.append("Failed to get browse data. Error code: " + i
                    + ". Description: ");
            for (int k = j; k < array2.size(); k++) {
                stringbuffer.append((String) array2.at(k));
            }

            return "";
        }
        int l = 0;
        for (int i1 = j; i1 < array2.size(); i1++) {
            l += ((String) array2.at(i1)).length();
        }

        if (l <= 0) {
            stringbuffer.append("Failed to get browse data. Unspecified error");
            return "";
        }
        StringBuffer stringbuffer1 = new StringBuffer(l);
        for (int j1 = j; j1 < array2.size(); j1++) {
            stringbuffer1.append((String) array2.at(j1));
        }

        return stringbuffer1.toString().trim();
    }

    protected Array getConnArgs() {
        Array array = getConnectionProperties();
        int i = array.size();
        Array array1 = new Array();
        for (int j = 0; j < i; j++) {
            StringProperty stringproperty = (StringProperty) array.at(j);
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

    String getExePath() {
        String s = Platform.getRoot() + File.separator + "logs";
        return exePath + getExeTimeout() + " \"" + s + "\" " + getMonDll()
                + " ";
    }

    protected abstract String getMonDll();

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

    public static void main(String args[]) {
        try {
            int i = 0;
            Array array = new Array();
            Array array1 = new Array();
            for (int j = i; j < args.length - 1; j++) {
                array1.add(args[j]);
            }

            Array array2 = new Array();
            array2.add(args[args.length - 1]);
            array.add(GET_NEW_DATA);
            array.add(array1);
            array.add(array2);
            BrowsableExeBase browsableexebase = (BrowsableExeBase) AtomicMonitor
                    .MonitorCreate(args[i]);
            String s = ArgsPackagerUtil.packageArgs(array, 0, array.size() - 1);
            String s1 = ArgsPackagerUtil.encodeArgs(s);
            Array array3 = new Array();
            String s2 = browsableexebase.getExePath() + " " + s1;
            System.out.println(s2);
            int k = browsableexebase.exec(s2, array3);
            System.out.println("exitCode: " + k);
            for (int l = 0; l < array3.size(); l++) {
                System.out.println(array3.at(l));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        System.exit(0);
    }

    static {
        exePath = Platform.getRoot() + File.separator + "bin" + File.separator
                + "ss_mon_single_run.exe ";
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
        String s = (COM.dragonflow.SiteView.BrowsableExeBase.class).getName();
        addProperties(s, astringproperty);
    }
}
