/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * IPMIMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>IPMIMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import jgl.Array;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Rule;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.TextUtils;

//import com.dragonflow.IPMI.IPMIException;
//import com.dragonflow.IPMI.Commands.AnalogSensorReadingRecord;
//import com.dragonflow.IPMI.Commands.Chassis;
//import com.dragonflow.IPMI.Commands.DiscreteSensorReadingRecord;
//import com.dragonflow.IPMI.Commands.FullSensorInfoRecord;
//import com.dragonflow.IPMI.Commands.SDR;
//import com.dragonflow.IPMI.Commands.Sensor;
//import com.dragonflow.IPMI.Commands.SensorInfoRecord;
//import com.dragonflow.IPMI.Commands.SensorReadingRecord;
//import com.dragonflow.IPMI.Interfaces.LAN.LANIfs;
//import com.dragonflow.IPMI.Util.TranslatorFactory;

public class IPMIMonitor extends BrowsableBase {
    private static class CounterReading {

        String value;

        boolean error;

        boolean warning;

        boolean good;

        public boolean isError() {
            return error;
        }

        public void setError() {
            warning = good = false;
            error = true;
        }

        public boolean isWarning() {
            return warning;
        }

        public void setWarning() {
            error = good = false;
            warning = true;
        }

        public boolean isGood() {
            return good;
        }

        public void setGood() {
            warning = error = false;
            good = true;
        }

        private CounterReading() {
            value = "";
            error = false;
            warning = false;
            good = true;
        }

    }

    private HashMap sensorsInfoMap;

    private static int nMaxCounters;

    private static StringProperty pHostName;

    private static StringProperty pPortNumber;

    private static StringProperty pUserName;

    private static StringProperty pPassword;

    private static StringProperty pStatus;

    private static final String BROWSABLE_CHASSIS_ISPOWERON_COUNTER_ID = "ChassisPowerOn";

    private static final String BROWSABLE_CHASSIS_ISPOWEROVERLOAD_COUNTER_ID = "ChassisPowerOverload";

    private static final String BROWSABLE_CHASSIS_ISPOWERINTERLOCK_COUNTER_ID = "ChassisPowerInterlock";

    private static final String BROWSABLE_CHASSIS_ISPOWERFAULT_COUNTER_ID = "ChassisPowerFault";

    private static final String BROWSABLE_CHASSIS_ISPOWERCONTROLFAULT_COUNTER_ID = "ChassisPowerControlFault";

    private static final String BROWSABLE_CHASSIS_ISCHASSISINTRUSION_COUNTER_ID = "ChassisIntrusion";

    private static final String BROWSABLE_CHASSIS_ISFRONTPANELLOCKOUT_COUNTER_ID = "ChassisFrontPanelLockout";

    private static final String BROWSABLE_CHASSIS_ISDRIVEFAULT_COUNTER_ID = "ChassisDriveFault";

    private static final String BROWSABLE_CHASSIS_ISCOOLINGFANFAULT_COUNTER_ID = "ChassisCoolingFanFault";

    private static final String BROWSABLE_CHASSIS_ISPOWERON_COUNTER_NAME = "Power On";

    private static final String BROWSABLE_CHASSIS_ISPOWEROVERLOAD_COUNTER_NAME = "Power Overload";

    private static final String BROWSABLE_CHASSIS_ISPOWERINTERLOCK_COUNTER_NAME = "Power Interlock";

    private static final String BROWSABLE_CHASSIS_ISPOWERFAULT_COUNTER_NAME = "Power Fault";

    private static final String BROWSABLE_CHASSIS_ISPOWERCONTROLFAULT_COUNTER_NAME = "Power Control Fault";

    private static final String BROWSABLE_CHASSIS_ISCHASSISINTRUSION_COUNTER_NAME = "Chassis Intrusion";

    private static final String BROWSABLE_CHASSIS_ISFRONTPANELLOCKOUT_COUNTER_NAME = "Front-Panel Lockout";

    private static final String BROWSABLE_CHASSIS_ISDRIVEFAULT_COUNTER_NAME = "Drive Fault";

    private static final String BROWSABLE_CHASSIS_ISCOOLINGFANFAULT_COUNTER_NAME = "Cooling Fan Fault";

    private static final Hashtable chassisMericsMap;

    static final String CHASSIS_STATUS_TRUE = "True";

    static final String CHASSIS_STATUS_FALSE = "False";

    static final String CHASSIS_STATUS_ACTIVE = "Active";

    static final String CHASSIS_STATUS_INACTIVE = "Inactive";

    private static final String SENSORS_ROOT = "Sensors";

    private static final String CHASSIS_ROOT = "Chassis Status";

    private static final String BROWSABLE_SENSORS_COUNTER_ID_STATUS_SUFFIX = "_**Status";

    private static final String BROWSABLE_SENSORS_COUNTER_ID_VALUE_SUFFIX = "_**Value";

    private static final String STATUS_BROWSABLE_CHASSIS_COUNTER_NAME_SUFFIX = " (status)";

    private static HashMap machineMap = new HashMap();

    private static final String GENERAL_STATUS_PROPERTY_NAME = "status";

    public IPMIMonitor() {
        sensorsInfoMap = null;
    }

    public String getBrowseID() {
        return super.getBrowseID();
    }

    private String getSensorIdForCounter(String s) {
        String s1 = null;
        if (s.endsWith("_**Status")) {
            s1 = s.substring(0, s.indexOf("_**Status"));
        } else if (s.endsWith("_**Value")) {
            s1 = s.substring(0, s.indexOf("_**Value"));
        }
        return s1;
    }

    private Vector getUniqueSensorsIdsFromCounters(Vector vector) {
        HashMap hashmap = new HashMap();
        for (int i = 0; i < vector.size(); i ++) {
            String s = (String) vector.get(i);
            String s1 = getSensorIdForCounter(s);
            if (s1 != null) {
                hashmap.put(s1, null);
            }
        }

        return new Vector(hashmap.keySet());
    }

    private Vector getChassisIdsFromCounters(Vector vector) {
        Vector vector1 = new Vector();
        for (int i = 0; i < vector.size(); i ++) {
            String s = (String) chassisMericsMap.get(vector.get(i));
            if (s != null) {
                vector1.add(vector.get(i));
            }
        }

        return vector1;
    }

//    protected CounterReading updateChassisCounter(String s, com.dragonflow.IPMI.Commands.Chassis.ChassisStatus chassisstatus) {
//        CounterReading counterreading = new CounterReading();
//        if (s.equalsIgnoreCase("ChassisPowerOn")) {
//            counterreading.value = chassisstatus.isPowerOn() ? "True" : "False";
//            if (chassisstatus.isPowerOn()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        } else if (s.equalsIgnoreCase("ChassisPowerOverload")) {
//            counterreading.value = chassisstatus.isPowerOverload() ? "True" : "False";
//            if (!chassisstatus.isPowerOverload()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        } else if (s.equalsIgnoreCase("ChassisPowerInterlock")) {
//            counterreading.value = chassisstatus.isPowerInterlock() ? "True" : "False";
//            if (!chassisstatus.isPowerInterlock()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        } else if (s.equalsIgnoreCase("ChassisPowerFault")) {
//            counterreading.value = chassisstatus.isPowerFault() ? "True" : "False";
//            if (!chassisstatus.isPowerFault()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        } else if (s.equalsIgnoreCase("ChassisPowerControlFault")) {
//            counterreading.value = chassisstatus.isPowerControlFault() ? "True" : "False";
//            if (!chassisstatus.isPowerControlFault()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        }
//        if (s.equalsIgnoreCase("ChassisIntrusion")) {
//            counterreading.value = chassisstatus.isChassisIntrusion() ? "Active" : "Inactive";
//            if (!chassisstatus.isChassisIntrusion()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        }
//        if (s.equalsIgnoreCase("ChassisFrontPanelLockout")) {
//            counterreading.value = chassisstatus.isFrontPanelLockout() ? "Active" : "Inactive";
//            if (!chassisstatus.isFrontPanelLockout()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        } else if (s.equalsIgnoreCase("ChassisDriveFault")) {
//            counterreading.value = chassisstatus.isDriveFault() ? "True" : "False";
//            if (!chassisstatus.isDriveFault()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        } else if (s.equalsIgnoreCase("ChassisCoolingFanFault")) {
//            counterreading.value = chassisstatus.isCoolingFanFault() ? "True" : "False";
//            if (!chassisstatus.isCoolingFanFault()) {
//                counterreading.setGood();
//            } else {
//                counterreading.setError();
//            }
//        } else {
//            LogManager.log("error", "unexpected chassis type");
//        }
//        return counterreading;
//    }
//
//    protected CounterReading updateSensorCounter(String s, SensorReadingRecord sensorreadingrecord) {
//        CounterReading counterreading = new CounterReading();
//        int i = 0;
//        if (sensorreadingrecord == null || !sensorreadingrecord.isValidRead()) {
//            counterreading.value = "n/a";
//            counterreading.setError();
//        } else if (s.endsWith("_**Status")) {
//            if (sensorreadingrecord instanceof AnalogSensorReadingRecord) {
//                i = sensorreadingrecord.getStatus();
//                counterreading.value = TranslatorFactory.getTranslator(Locale.ENGLISH).getSensorStatus(i);
//            } else if (sensorreadingrecord instanceof DiscreteSensorReadingRecord) {
//                Vector vector = ((DiscreteSensorReadingRecord) sensorreadingrecord).getStateAsserted();
//                for (int j = 0; j < vector.size(); j ++) {
//                    SensorInfoRecord sensorinforecord = sensorreadingrecord.getSensorInfoRecord();
//                    counterreading.value += TranslatorFactory.getTranslator(Locale.ENGLISH).getSensorAssert(sensorinforecord.getEventReadingTypeCodeByte(), (byte) ((Integer) vector.get(j)).intValue());
//                }
//
//                i = sensorreadingrecord.getStatus();
//            }
//        } else if (s.endsWith("_**Value")) {
//            counterreading.value = "" + sensorreadingrecord.getValue();
//            i = sensorreadingrecord.getStatus();
//        } else {
//            LogManager.log("error", "Unexpected counter type. Counter is not value nor status. Counter id  is" + s);
//        }
//        if (i == 2) {
//            counterreading.setError();
//        }
//        if (i == 3) {
//            counterreading.setWarning();
//        }
//        return counterreading;
//    }

    public void onMonitorCreateFromPage(HTTPRequest httprequest) {
        super.onMonitorCreateFromPage(httprequest);
        String s = getBrowseName();
        boolean flag = getProperty(s + 1).length() <= 0;
        if (!flag) {
            return;
        }
        String s1 = getBrowseName();
        String s2 = getBrowseID();
        int i = 1;
        for (Enumeration enumeration = chassisMericsMap.keys(); enumeration.hasMoreElements();) {
            String s3 = (String) enumeration.nextElement();
            String s4 = (String) chassisMericsMap.get(s3);
            setProperty(s2 + i, s3);
            Array array = new Array();
            array.pushFront("Chassis Status");
            array.pushFront(s4);
            String s5 = setBrowseName(array);
            setProperty(s1 + i, s5);
            i ++;
        }

    }

    protected boolean update() throws SiteViewException {
        StringBuffer stringbuffer = new StringBuffer();
        Vector vector = new Vector();
        int i = 0;
        do {
            if (i >= nMaxCounters) {
                break;
            }
            String s = getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1));
            if (s.length() == 0) {
                break;
            }
            vector.add(s);
            i ++;
        } while (true);
        Vector vector1 = getUniqueSensorsIdsFromCounters(vector);
        Integer integer;
        synchronized (machineMap) {
            integer = (Integer) machineMap.get(getProperty(pHostName));
            if (integer == null) {
                integer = new Integer(0);
                machineMap.put(getProperty(pHostName), integer);
            }
        }
        HashMap hashmap1 = null;
        synchronized (integer) {
//            hashmap1 = readSensors(vector1);
        }
        if (hashmap1 == null) {
            LogManager.log("error", "Failed to update monitor status");
            setProperty(pStateString, "n/a - Failed to update monitor status");
            return false;
        }
        Vector vector2 = getChassisIdsFromCounters(vector);
//        com.dragonflow.IPMI.Commands.Chassis.ChassisStatus chassisstatus = null;
//        if (vector2.size() > 0) {
//            synchronized (integer) {
//                chassisstatus = readChassisStatus();
//            }
//            if (chassisstatus == null) {
//                LogManager.log("error", "Failed to read chassis status");
//                setProperty(pStateString, "n/a -Failed to read chassis status");
//                return false;
//            }
//        }
        int j = 0;
        int k = 0;
        if (stillActive()) {
            synchronized (this) {
                String s1 = "";
                if (stringbuffer.length() > 0) {
                    for (int l = 0; l < nMaxCounters; l ++) {
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1), "n/a");
                    }

                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
                    s1 = stringbuffer.toString();
                } else {
                    for (int i1 = 0; i1 < vector.size(); i1 ++) {
                        String s2 = getProperty(PROPERTY_NAME_COUNTER_NAME + (i1 + 1));
                        String s3 = (String) vector.get(i1);
                        CounterReading counterreading = null;
                        String s4 = getSensorIdForCounter(s3);
                        String s5 = (String) chassisMericsMap.get(s3);
                        if (s4 != null) {
//                            SensorReadingRecord sensorreadingrecord = (SensorReadingRecord) hashmap1.get(s4);
//                            counterreading = updateSensorCounter(s3, sensorreadingrecord);
                        } else if (s5 != null) {
//                            counterreading = updateChassisCounter(s3, chassisstatus);
                        }
                        if (counterreading == null) {
                            LogManager.log("log", "Failed to read counter");
                            continue;
                        }
                        setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1), TextUtils.removeChars(counterreading.value, ","));
                        s1 = s1 + s2 + " = " + counterreading.value;
                        if (i1 != vector.size() - 1) {
                            s1 = s1 + ", ";
                        }
                        if (counterreading.isError()) {
                            j ++;
                        }
                        if (counterreading.isWarning()) {
                            k ++;
                        }
                    }

                    setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), j);
                    if (j > 0) {
                        setProperty(pStatus, "Error");
                    } else if (k > 0) {
                        setProperty(pStatus, "Warning");
                    } else {
                        setProperty(pStatus, "OK");
                    }
                }
                setProperty(pStateString, s1);
            }
        }
        return true;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        return super.getStatePropertyObjects(flag);
    }

    public Array getLogProperties() {
        return super.getLogProperties();
    }

    public boolean isServerBased() {
        return true;
    }

    public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, jgl.HashMap hashmap) {
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    public String getHostname() {
        return getProperty(pHostName);
    }

    public Array getConnectionProperties() {
        Array array = new Array();
        array.add(pHostName);
        array.add(pPortNumber);
        array.add(pUserName);
        array.add(pPassword);
        return array;
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_browsableContentMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

//    public String getBrowseData(StringBuffer stringbuffer) {
//        HashMap hashmap = getSensorsInfo();
//        if (hashmap == null) {
//            return "";
//        }
////        HashMap hashmap1 = hashSensorsInfoByGroup(hashmap);
////        StringBuffer stringbuffer1 = new StringBuffer();
////        stringbuffer1.append("<browse_data>");
////        stringbuffer1.append("<object name=\"Sensors\" instances=\"_Total\">");
////        for (Iterator iterator = hashmap1.keySet().iterator(); iterator.hasNext(); stringbuffer1.append("</object>")) {
////            String s = (String) iterator.next();
////            stringbuffer1.append("<object name=\"" + s + "\" instances=\"_Total\">");
////            Vector vector = (Vector) hashmap1.get(s);
////            for (int i = 0; i < vector.size(); i ++) {
////                SensorInfoRecord sensorinforecord = (SensorInfoRecord) vector.get(i);
////                String s3 = new String(sensorinforecord.getIDStringBytes()) + " (status)";
////                String s4 = "" + sensorinforecord.getSensorNumber() + "_**Status";
////                stringbuffer1.append("\"<counter name=\"" + s3 + "\" id=\"" + s4 + "\"/>\"");
////                if (!sensorinforecord.isAnalog()) {
////                    continue;
////                }
////                String s5 = new String(sensorinforecord.getIDStringBytes());
////                String s6 = "" + sensorinforecord.getSensorNumber() + "_**Value";
////                if (sensorinforecord instanceof FullSensorInfoRecord) {
////                    s5 = s5 + " (" + ((FullSensorInfoRecord) sensorinforecord).getUnitsString(Locale.ENGLISH) + " )";
////                }
////                stringbuffer1.append("\"<counter name=\"" + s5 + "\" id=\"" + s6 + "\"/>\"");
//            }
//
//        }
//
//        stringbuffer1.append("</object>");
//        stringbuffer1.append("<object name=\"Chassis Status\" instances=\"_Total\">");
//        String s1;
//        String s2;
//        for (Enumeration enumeration = chassisMericsMap.keys(); enumeration.hasMoreElements(); stringbuffer1.append("\"<counter name=\"" + s2 + "\" id=\"" + s1 + "\"/>\"")) {
//            s1 = (String) enumeration.nextElement();
//            s2 = (String) chassisMericsMap.get(s1);
//        }
//
//        stringbuffer1.append("</object>");
//        stringbuffer1.append("</browse_data>");
//        LogManager.log("error", "xml: " + stringbuffer1);
//        return new String(stringbuffer1);
//    }

    public String setBrowseID(Array array) {
        String s = "";
        if (array.size() <= 0) {
            return s;
        }
        for (int i = array.size() - 1; i >= 0; i --) {
            String s1 = (String) array.at(i);
            s = s + s1;
        }

        return s;
    }

    private HashMap getSensorsInfo() {
        if (sensorsInfoMap != null) {
            return sensorsInfoMap;
        }
        int i = getPort();
//        LANIfs lanifs = new LANIfs(getProperty(pHostName), i, getProperty(pUserName).getBytes(), getProperty(pPassword).getBytes());
//        try {
//            lanifs.init();
//        } catch (IPMIException ipmiexception) {
//            LogManager.log("error", "Failed to init IPMI lan session" + ipmiexception.getMessage());
//            return null;
//        }
//        SDR sdr = null;
//        try {
//            sdr = new SDR(lanifs);
//        } catch (IPMIException ipmiexception1) {
//            LogManager.log("error", "Failed to retrieve DSR information." + ipmiexception1.getMessage());
//            return null;
//        }
//        Vector vector = null;
//        try {
//            vector = sdr.getFullRecords();
//        } catch (IPMIException ipmiexception2) {
//            LogManager.log("error", "Failed to retrieve sensors information." + ipmiexception2.getMessage());
//            return null;
//        }
//        HashMap hashmap = new HashMap();
//        for (int j = 0; j < vector.size(); j ++) {
//            SensorInfoRecord sensorinforecord = (SensorInfoRecord) vector.get(j);
//            hashmap.put(new Byte(sensorinforecord.getSensorNumber()), sensorinforecord);
//        }
//
//        try {
//            lanifs.end();
//        } catch (Exception exception) {
//            LogManager.log("error", "Failed to end IPMI lan session" + exception.getMessage());
//        }
//        sensorsInfoMap = hashmap;
        return sensorsInfoMap;
    }

//    private HashMap hashSensorsInfoByGroup(HashMap hashmap) {
//        HashMap hashmap1 = new HashMap();
////        SensorInfoRecord sensorinforecord;
//        Vector vector;
//        for (Iterator iterator = hashmap.values().iterator(); iterator.hasNext(); vector.add(sensorinforecord)) {
////            sensorinforecord = (SensorInfoRecord) iterator.next();
////            byte byte0 = sensorinforecord.getSensorType();
////            String s = TranslatorFactory.getTranslator(Locale.ENGLISH).getSensorType(byte0);
////            vector = (Vector) hashmap1.get(s);
////            if (vector == null) {
////                vector = new Vector();
////                hashmap1.put(s, vector);
//            }
//        }
//
////        return hashmap1;
//    }
//
    /**
     * CAUTION: Decompiled by hand.
     * 
     * @return
     */
    private int getPort() {
        try {
            int i = Integer.parseInt(getProperty(pPortNumber));
            return i;
        } catch (Exception e) {
            LogManager.log("error", "Failed to parse port string : " + getProperty(pPortNumber));
            return 623;
        }
    }

//    private HashMap readSensors(Vector vector) {
//        HashMap hashmap = getSensorsInfo();
//        if (hashmap == null) {
//            LogManager.log("error", "Failed to readSensors");
//            return null;
//        }
//        int i = getPort();
//        LANIfs lanifs = new LANIfs(getProperty(pHostName), i, getProperty(pUserName).getBytes(), getProperty(pPassword).getBytes());
//        LogManager.log("error", "Trying to start a session: host:" + getProperty(pHostName) + ", port:" + i + ", user:" + getProperty(pUserName) + ", password:" + getProperty(pPassword));
//        try {
//            lanifs.init();
//        } catch (IPMIException ipmiexception) {
//            LogManager.log("error", "Failed to init lan session: " + ipmiexception.getMessage());
//            return null;
//        }
//        Sensor sensor = new Sensor(lanifs);
//        HashMap hashmap1 = new HashMap();
//        LogManager.log("error", "number of sensors to read: " + vector.size());
//        for (int j = 0; j < vector.size(); j ++) {
//            String s = (String) vector.get(j);
//            LogManager.log("error", "Reading sensor for id: " + s);
//            SensorInfoRecord sensorinforecord = (SensorInfoRecord) hashmap.get(new Byte(s));
//            if (sensorinforecord == null) {
//                LogManager.log("error", "Failed to get SensorInfoRecord for record id: " + s);
//                continue;
//            }
//            SensorReadingRecord sensorreadingrecord = null;
//            try {
//                sensorreadingrecord = sensor.readSensor(sensorinforecord);
//            } catch (IPMIException ipmiexception2) {
//                LogManager.log("error", "Failed to read sensor info." + ipmiexception2.getMessage());
//                continue;
//            }
//            hashmap1.put(s, sensorreadingrecord);
//        }
//
//        try {
//            lanifs.end();
//        } catch (IPMIException ipmiexception1) {
//            LogManager.log("error", "Failed to end ipmi lan session: " + ipmiexception1.getMessage());
//        }
//        return hashmap1;
//    }
//
//    private com.dragonflow.IPMI.Commands.Chassis.ChassisStatus readChassisStatus() {
//        int i = getPort();
//        LANIfs lanifs = new LANIfs(getProperty(pHostName), i, getProperty(pUserName).getBytes(), getProperty(pPassword).getBytes());
//        LogManager.log("error", "Trying to start a session: host:" + getProperty(pHostName) + ", port:" + i + ", user:" + getProperty(pUserName) + ", password:" + getProperty(pPassword));
//        try {
//            lanifs.init();
//        } catch (IPMIException ipmiexception) {
//            LogManager.log("error", "Failed to init lan session: " + ipmiexception.getMessage());
//            return null;
//        }
//        com.dragonflow.IPMI.Commands.Chassis.ChassisStatus chassisstatus = null;
//        try {
//            chassisstatus = Chassis.getChassisStatus(lanifs);
//        } catch (IPMIException ipmiexception1) {
//            LogManager.log("error", "Failed to read chassis status info." + ipmiexception1.getMessage());
//            return null;
//        }
//        try {
//            lanifs.end();
//        } catch (IPMIException ipmiexception2) {
//            LogManager.log("error", "Failed to end lan session: " + ipmiexception2.getMessage());
//        }
//        return chassisstatus;
//    }

    static {
        nMaxCounters = 60;
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(MasterConfig.getMasterConfig(), "_browsableContentMaxCounters"));
        if (nMaxCounters == 0) {
            nMaxCounters = 60;
        }
        pHostName = new StringProperty("_server", "");
        pHostName.setDisplayText("Server name", "the IPMI server name or IP address");
        pHostName.setParameterOptions(false, 1, false);
        pPortNumber = new StringProperty("_port", "623");
        pPortNumber.setDisplayText("Port number", "the port number of the IPMI server (default is 623)");
        pPortNumber.setParameterOptions(false, 2, false);
        pUserName = new StringProperty("_user", "");
        pUserName.setDisplayText("User name", "user name to connect the IPMI server");
        pUserName.setParameterOptions(false, 3, false);
        pPassword = new StringProperty("_password", "");
        pPassword.setDisplayText("Password", "password to connect the IPMI server");
        pPassword.setParameterOptions(false, 4, false);
        pPassword.isPassword = true;
        chassisMericsMap = new Hashtable();
        chassisMericsMap.put("ChassisPowerOn", "Power On");
        chassisMericsMap.put("ChassisPowerOverload", "Power Overload");
        chassisMericsMap.put("ChassisPowerInterlock", "Power Interlock");
        chassisMericsMap.put("ChassisPowerFault", "Power Fault");
        chassisMericsMap.put("ChassisPowerControlFault", "Power Control Fault");
        chassisMericsMap.put("ChassisDriveFault", "Drive Fault");
        chassisMericsMap.put("ChassisCoolingFanFault", "Cooling Fan Fault");
        chassisMericsMap.put("ChassisIntrusion", "Chassis Intrusion");
        chassisMericsMap.put("ChassisFrontPanelLockout", "Front-Panel Lockout");
        pStatus = new StringProperty("status", "n/a");
        String s = (COM.dragonflow.StandardMonitor.IPMIMonitor.class).getName();
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
        StringProperty astringproperty1[] = new StringProperty[astringproperty.length + 5];
        System.arraycopy(astringproperty, 0, astringproperty1, 0, astringproperty.length);
        astringproperty1[astringproperty.length] = pHostName;
        astringproperty1[astringproperty.length + 1] = pPortNumber;
        astringproperty1[astringproperty.length + 2] = pUserName;
        astringproperty1[astringproperty.length + 3] = pPassword;
        astringproperty1[astringproperty.length + 4] = pStatus;
        addProperties(s, astringproperty1);
        setClassProperty(s, "description", "Collect IPMI data");
        setClassProperty(s, "help", "IPMIMonitor.htm");
        setClassProperty(s, "title", "IPMI");
        setClassProperty(s, "class", "IPMIMonitor");
        setClassProperty(s, "topazType", "IPMI");
        setClassProperty(s, "classType", "application");
        addClassElement(s, Rule.stringToClassifier("status == 'Error'\terror"));
        addClassElement(s, Rule.stringToClassifier("status == 'Warning'\twarning"));
        addClassElement(s, Rule.stringToClassifier("status == 'OK'\tgood"));
        setClassProperty(s, "target", "_server");
    }

	public String getBrowseData(StringBuffer stringbuffer) {
		// TODO Auto-generated method stub
		return null;
	}
}
