/*
 * 
 * Created on 2005-3-7 1:20:16
 *
 * OracleJDBCMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>OracleJDBCMonitor</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import jgl.Array;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.Rule;
//import COM.dragonflow.SiteView.TopazInfo;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.RawXmlWriter;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.StandardMonitor:
// URLMonitor

public class OracleJDBCMonitor extends BrowsableBase {

    static StringProperty pServerName;

    static StringProperty pUser;

    static StringProperty pPassword;

    static StringProperty pConnectTimeout;

    static StringProperty pQueryTimeout;

    static StringProperty pDriver;

    static StringProperty pCounters;

    static StringProperty pDBMachineName;
    
    static String template_path= Platform.getRoot() + File.separator + "templates.applications" + File.separator + "commands.oraclejdbc";

    static String instanceNameQuery = getSettingValue("INSTANCENAMEQUERY=");

    static String counterNameQuery = getSettingValue("COUNTERNAMEQUERY=");

    static String statCounterQuery = getSettingValue("STATCOUNTERQUERY=");

    static String freeTableSpaceQuery = getSettingValue("FREETABLESPACEQUERY=");

    static Vector tableQueryVector;

    static Vector tableNameVector;

    static String sidValueQuery = getSettingValue("SIDVALUEQUERY=");

    static String sysResults = getSettingValue("SYSRESULTS=");

    static String sesResults = getSettingValue("SESRESULTS=");

    static int nMaxQuery = 10;

    static int nMaxCounters;

    private Boolean cachingConnection;

    private Connection connection;

    private HashMap statNameMap;

    private static final String SYSTEM_TABLE = "V$SYSSTAT";

    private static final String SESSION_TABLE = "V$SESSTAT";

    private static final String FREE_TABLE_TABLE = "% Free Table Space";

    boolean driverLoaded;

    public OracleJDBCMonitor() {
        cachingConnection = null;
        connection = null;
        statNameMap = null;
        driverLoaded = false;
    }

    static String getSettingValue(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        try {
            stringbuffer = FileUtils.readFile(template_path);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        String as[] = TextUtils.split(stringbuffer.toString(), URLMonitor.CRLF);
        for (int i = 0; i < as.length; i ++) {
            if (as[i].startsWith(s)) {
                return as[i].substring(s.length());
            }
        }

        return "";
    }

    public Array getConnectionProperties() {
        Array array = new Array();
        array.add(pServerName);
        array.add(pUser);
        array.add(pPassword);
        array.add(pDriver);
        array.add(pConnectTimeout);
        array.add(pQueryTimeout);
        return array;
    }

    HashMap getStatNameMap() {
        StringBuffer stringbuffer = new StringBuffer();
        HashMap hashmap = new HashMap();
        Array array = getResult(stringbuffer, statCounterQuery);
        for (int i = 0; i < array.size(); i ++) {
            String as[] = TextUtils.split((String) array.at(i), "\t");
            hashmap.put(as[1], as[0]);
        }

        return hashmap;
    }

    protected boolean update() {
        if (cachingConnection == null) {
            if (getSetting("_oracleJDBCConnectionCaching").equalsIgnoreCase("true")) {
                cachingConnection = new Boolean(true);
            } else {
                cachingConnection = new Boolean(false);
            }
        }
        boolean flag = true;
        StringBuffer stringbuffer = new StringBuffer();
        HashMap hashmap = new HashMap();
        try {
            Array array = null;
            int i;
            for (i = 0; i < nMaxCounters && getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1)).length() != 0; i ++) {
            }
            String as[] = new String[i];
            label0: for (int j = 0; j < i; j ++) {
                String s = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
                if (s.indexOf("V$SYSSTAT") >= 0) {
                    Array array1;
                    if ((array1 = (Array) hashmap.get(sysResults)) == null) {
                        array1 = getResult(stringbuffer, sysResults);
                        hashmap.put(sysResults, array1);
                    }
                    s = s.substring(s.indexOf("V$SYSSTAT") + "V$SYSSTAT".length(), s.length());
                    String s4 = s.substring(s.indexOf(" ") + 1, s.length());
                    int l1 = 0;
                    do {
                        if (l1 >= array1.size()) {
                            continue label0;
                        }
                        String s11 = (String) array1.at(l1);
                        if (s11.indexOf(s4) >= 0) {
                            String as2[] = TextUtils.split(s11, "\t");
                            as[j] = as2[1];
                        }
                        l1 ++;
                    } while (true);
                }
                if (s.indexOf("V$SESSTAT") >= 0) {
                    s = s.substring(s.indexOf("V$SESSTAT") + "V$SESSTAT".length(), s.length());
                    s = s.substring(s.indexOf(" ") + 1, s.length());
                    String s2 = s.substring(s.lastIndexOf("/") + 1, s.length());
                    s2 = s2.substring(s2.indexOf(" ") + 1, s2.length());
                    String s5 = s.substring(0, s.indexOf("/"));
                    if (statNameMap == null) {
                        statNameMap = getStatNameMap();
                    }
                    String s7 = (String) statNameMap.get(s2);
                    String s12 = sesResults + " WHERE SID =" + s5;
                    Array array3;
                    if ((array3 = (Array) hashmap.get(s12)) == null) {
                        array3 = getResult(stringbuffer, s12);
                        hashmap.put(s12, array3);
                    }
                    int i2 = 0;
                    do {
                        if (i2 >= array3.size()) {
                            continue label0;
                        }
                        String s15 = (String) array3.at(i2);
                        String as3[] = TextUtils.split(s15, "\t");
                        if (as3[1].equals(s7)) {
                            as[j] = as3[2];
                        }
                        i2 ++;
                    } while (true);
                }
                if (s.indexOf("% Free Table Space") >= 0) {
                    if (array == null) {
                        array = getResult(stringbuffer, freeTableSpaceQuery);
                    }
                    s = s.substring(s.indexOf("% Free Table Space") + "% Free Table Space".length(), s.length());
                    String s3 = s.substring(s.indexOf(" ") + 1, s.length());
                    int j1 = 0;
                    do {
                        if (j1 >= array.size()) {
                            continue label0;
                        }
                        String s8 = (String) array.at(j1);
                        if (s8.indexOf(s3) >= 0) {
                            String as1[] = TextUtils.split(s8, "\t");
                            as[j] = "" + (100 - TextUtils.toInt(as1[1]));
                        }
                        j1 ++;
                    } while (true);
                }
                for (int k = 0; k < tableNameVector.size(); k ++) {
                    String s6 = (String) tableNameVector.get(k);
                    if (s.indexOf(s6) < 0) {
                        continue;
                    }
                    String s9 = (String) tableQueryVector.get(k);
                    Array array2;
                    if ((array2 = (Array) hashmap.get(s9)) == null) {
                        array2 = getResult(stringbuffer, s9);
                        hashmap.put(s9, array2);
                    }
                    String s13 = s6;
                    s = s.substring(s.indexOf(s13) + s13.length(), s.length());
                    String s14 = s.substring(s.indexOf(" ") + 1, s.length());
                    for (int j2 = 0; j2 < array2.size(); j2 ++) {
                        String s16 = (String) array2.at(j2);
                        if (s16.toUpperCase().indexOf(s14.toUpperCase()) >= 0) {
                            String as4[] = TextUtils.split(s16, "\t");
                            as[j] = as4[1];
                        }
                    }

                }

            }

            if (stillActive()) {
                synchronized (this) {
                    String s1 = "";
                    if (stringbuffer.length() <= 0) {
                        int l = 0;
                        for (int k1 = 0; k1 < as.length; k1 ++) {
                            String s10 = getProperty(PROPERTY_NAME_COUNTER_NAME + (k1 + 1));
                            if (as[k1] != null) {
                                setProperty(PROPERTY_NAME_COUNTER_VALUE + (k1 + 1), as[k1]);
                            } else {
                                setProperty(PROPERTY_NAME_COUNTER_VALUE + (k1 + 1), "n/a");
                                l ++;
                            }
                            s1 = s1 + s10 + " = " + as[k1];
                            if (k1 != as.length - 1) {
                                s1 = s1 + ", ";
                            }
                            setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), l);
                        }

                    } else {
                        for (int i1 = 0; i1 < nMaxCounters; i1 ++) {
                            setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1), "n/a");
                        }

                        setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
                        s1 = stringbuffer.toString();
                        setProperty(pNoData, "n/a");
                        flag = false;
                    }
                    setProperty(pStateString, s1);
                }
            }
        } finally {
            try {
                if (cachingConnection == null || !cachingConnection.booleanValue()) {
                    closeConnection();
                }
            } catch (SQLException sqlexception) {
            }
        }
        return flag;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param stringbuffer
     * @param s
     * @return
     */
    public Array getResult(StringBuffer stringbuffer, String s) {
        String s1 = getProperty(pDriver);
        String s2 = getProperty(pServerName);
        String s3 = getProperty(pUser);
        String s4 = getProperty(pPassword);
        int i = StringProperty.toInteger(getProperty(pConnectTimeout));
        int j = StringProperty.toInteger(getProperty(pQueryTimeout));
        boolean flag = false;
        Statement statement = null;
        ResultSet resultset = null;
        Array array = new Array();

        try {
            if (!driverLoaded) {
                LogManager.log("RunMonitor", "DB monitor, loading, driver=" + s1);
                Class.forName(s1).newInstance();
                driverLoaded = true;
            }
            if (i > 0) {
                DriverManager.setLoginTimeout(i);
            }
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(s2, s3, s4);
                LogManager.log("RunMonitor", "DB monitor, connecting, database=" + s2 + ", " + s3);
            }
            statement = connection.createStatement();
            if (j > 0) {
                statement.setQueryTimeout(j);
            }
            resultset = statement.executeQuery(s);
            if (resultset == null) {
                stringbuffer.append("no results from query");
                throw new Exception("no results from query");
            }
            int k = resultset.getMetaData().getColumnCount();
            while (resultset.next()) {
                try {
                    String s5 = "";
                    for (int l = 1; l <= k; l ++) {
                        s5 = s5 + resultset.getString(l);
                        if (l < k) {
                            s5 = s5 + "\t";
                        }
                    }

                    array.add(s5);
                } catch (Exception exception) {
                    stringbuffer.append(exception.getMessage());
                }
            }
        } catch (SQLException e) {
            flag = true;
            stringbuffer.append(" error, " + e.getMessage());
            for (; e != null; e = e.getNextException()) {
                LogManager.log("Error", "DB error , " + getGroupPathID() + ": " + getProperty(pName) + ", " + e.getMessage() + ", " + e.getErrorCode() + ", " + e.getSQLState());
            }
        } catch (ClassNotFoundException e) {
            flag = true;
            stringbuffer.append("Driver not found: " + e.getMessage());
            LogManager.log("Error", "DB error, , " + getGroupPathID() + ": " + getProperty(pName) + ", " + e.getMessage() + ", " + e);
        } catch (Throwable e) {
            flag = true;
            stringbuffer.append(e.getMessage());
            LogManager.log("Error", "DB error, , " + getGroupPathID() + ": " + getProperty(pName) + ", " + e.getMessage() + ", " + e);
        } finally {
            if (resultset != null) {
                try {
                    resultset.close();
                } catch (Exception exception2) {
                    LogManager.log("Error", "DBclose resultSet error, , " + getGroupPathID() + ": " + getProperty(pName) + ", " + exception2);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception exception3) {
                    LogManager.log("Error", "Database close statement error, , " + getGroupPathID() + ": " + getProperty(pName) + ", " + exception3);
                }
            }
            if (connection != null && flag) {
                try {
                    connection.close();
                    connection = null;
                }

                catch (Exception exception4) {
                    try {
                        LogManager.log("Error", "Database close connection error, , " + getGroupPathID() + ": " + getProperty(pName) + ", " + exception4);
                        connection = null;
                    } catch (RuntimeException exception5) {
                        connection = null;
                        throw exception5;
                    }
                }
            }
        }

        return array;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public String getBrowseData(StringBuffer stringbuffer) {
        try {
            String s2;
            Array array = new Array();
            Array array1 = new Array();
            Array aarray[] = new Array[tableNameVector.size()];
            Array array2 = getResult(stringbuffer, counterNameQuery);
            if (stringbuffer.length() <= 0) {
                array = getResult(stringbuffer, instanceNameQuery);
                if (stringbuffer.length() <= 0) {
                    array1 = getResult(stringbuffer, sidValueQuery);
                }
            }
            Array array3 = getResult(stringbuffer, freeTableSpaceQuery);
            int i = 0;
            for (int j = 0; j < tableQueryVector.size(); j ++) {
                String s1 = (String) tableQueryVector.get(j);
                aarray[i ++] = getResult(stringbuffer, s1);
            }

            String s = buildXML(array2, array, array1, array3, aarray);
            s2 = s;
            try {
                closeConnection();
            } catch (SQLException sqlexception) {
                /* empty */
            }
            return s2;
        } catch (RuntimeException exception) {
            try {
                closeConnection();
            } catch (SQLException sqlexception1) {
                /* empty */
            }
            throw exception;
        }
    }

    private void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    String buildXML(Array array, Array array1, Array array2, Array array3, Array aarray[]) {
        StringBuffer stringbuffer = new StringBuffer();
        RawXmlWriter rawxmlwriter = new RawXmlWriter(stringbuffer);
        rawxmlwriter.startElement("browse_data");
        rawxmlwriter.startElement("object name =\"V$SYSSTAT\"");
        for (int i = 0; i < array.size(); i ++) {
            String s = (String) array.at(i);
            rawxmlwriter.emptyElement("counter name=\"" + s + "\" ");
        }

        rawxmlwriter.endElement("object");
        rawxmlwriter.startElement("object name =\"V$SESSTAT\"");
        for (int j = 0; j < array2.size(); j ++) {
            String s1 = (String) array2.at(j);
            s1 = s1.replace('\t', '/');
            if (!s1.endsWith("/")) {
                s1 = s1 + "/";
            }
            rawxmlwriter.startElement("object name=\"" + s1 + "\" ");
            for (int i1 = 0; i1 < array.size(); i1 ++) {
                String s4 = (String) array.at(i1);
                rawxmlwriter.emptyElement("counter name=\"" + s4 + "\" ");
            }

            rawxmlwriter.endElement("object");
        }

        rawxmlwriter.endElement("object");
        rawxmlwriter.startElement("object name=\"% Free Table Space\"");
        for (int k = 0; k < array3.size(); k ++) {
            String s2 = (String) array3.at(k);
            s2 = s2.toUpperCase();
            if (!s2.startsWith("--") && !s2.startsWith("TSNAME")) {
                rawxmlwriter.emptyElement("counter name=\"" + s2.substring(0, s2.indexOf("\t")) + "\" ");
            }
        }

        rawxmlwriter.endElement("object");
        if (aarray.length > 0) {
            int l = 0;
            do {
                if (l >= aarray.length) {
                    break;
                }
                Array array4 = aarray[l];
                if (array4 == null) {
                    break;
                }
                String s3 = (String) tableNameVector.get(l);
                rawxmlwriter.startElement("object name=\"" + s3 + "\"");
                for (int j1 = 0; j1 < array4.size(); j1 ++) {
                    String s5 = (String) array4.at(j1);
                    s5 = s5.toUpperCase();
                    if (s5.startsWith("--") || s5.startsWith("TSNAME")) {
                        continue;
                    }
                    if (s5.indexOf('\t') >= 0) {
                        rawxmlwriter.emptyElement("counter name=\"" + s5.substring(0, s5.indexOf("\t")) + "\" ");
                    } else {
                        LogManager.log("Error", "OracleJDBCMonitor retrieved counter in unknown format, counter=" + s5);
                    }
                }

                rawxmlwriter.endElement("object");
                l ++;
            } while (true);
        }
        rawxmlwriter.endElement("browse_data");
        return stringbuffer.toString();
    }

    public String getHostname() {
        String s = getProperty(pDBMachineName);
        if (s != null && s.length() > 0) {
            return s;
        } else {
            return getProperty(pServerName);
        }
    }

    public int getMaxCounters() {
        return nMaxCounters;
    }

    public void setMaxCounters(int i) {
        nMaxCounters = i;
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_oracleJDBCMaxCounters", (new Integer(i)).toString());
        MasterConfig.saveMasterConfig(hashmap);
    }

    static {
        tableQueryVector = new Vector();
        tableNameVector = new Vector();
//        template_path = Platform.getRoot() + File.separator + "templates.applications" + File.separator + "commands.oraclejdbc";
        nMaxCounters = 30;
        Array array = new Array();
        jgl.HashMap hashmap = MasterConfig.getMasterConfig();
        nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_oracleJDBCMaxCounters"));
        if (nMaxCounters == 0) {
            nMaxCounters = 30;
        }
        StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
        int i = 0;
        do {
            String s = getSettingValue("TABLENAME" + i + "=");
            String s2 = getSettingValue("TABLEQUERY" + i + "=");
            if (s == null || s.trim().length() == 0 || s2 == null || s2.trim().length() == 0) {
                break;
            }
            tableNameVector.add(s);
            tableQueryVector.add(s2);
            i ++;
        } while (true);
        pServerName = new StringProperty("_server", "", "host name");
        pServerName.setDisplayText("Database Connection URL", "Enter the URL to the database connection<br> (for example:jdbc:oracle:thin:@206.168.191.19:1521:ORCL )");
        pServerName.setParameterOptions(false, true, 1, false);
        array.add(pServerName);
        pUser = new StringProperty("_user", "", "user name");
        pUser.setDisplayText("Database User Name", "optional, user name used to connect to the database");
        pUser.setParameterOptions(false, true, 2, false);
        array.add(pUser);
        pPassword = new StringProperty("_password", "", "password");
        pPassword.setDisplayText("Database Password", "optional,  password used to connect to the database");
        pPassword.setParameterOptions(false, true, 3, false);
        pPassword.isPassword = true;
        array.add(pPassword);
        pDriver = new StringProperty("_driver", "oracle.jdbc.driver.OracleDriver", "driver");
        pDriver.setDisplayText("Database Driver", "driver used to connect to the database");
        pDriver.setParameterOptions(false, true, 4, false);
        array.add(pDriver);
        pConnectTimeout = new NumericProperty("_connectTimeout", "600", "seconds");
        pConnectTimeout.setDisplayText("Connection Timeout", "the time out, in seconds, to wait for a database connection");
        pConnectTimeout.setParameterOptions(false, true, 6, false);
        array.add(pConnectTimeout);
        pQueryTimeout = new NumericProperty("_queryTimeout", "600", "seconds");
        pQueryTimeout.setDisplayText("Query Timeout", "the time out, in seconds, to wait for a database query");
        pQueryTimeout.setParameterOptions(false, true, 7, false);
        array.add(pQueryTimeout);
        pDBMachineName = new StringProperty("_targetMachineName");
//        pDBMachineName.setDisplayText("DB Machine Name", "Identifier for target DB server, used for reporting to " + TopazInfo.getTopazName() + " (optional)");
        pDBMachineName.setParameterOptions(true, 10, true);
        array.add(pDBMachineName);
        StringProperty astringproperty1[] = new StringProperty[array.size()];
        for (int j = 0; j < array.size(); j ++) {
            astringproperty1[j] = (StringProperty) array.at(j);
        }

        String s1 = (COM.dragonflow.StandardMonitor.OracleJDBCMonitor.class).getName();
        StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
        System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
        System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
        addProperties(s1, astringproperty2);
        addClassElement(s1, Rule.stringToClassifier("countersInError > 0\terror", true));
        addClassElement(s1, Rule.stringToClassifier("always\tgood"));
        setClassProperty(s1, "description", "Tests an Oracle database by connecting to it and performing a query using JDBC.");
        setClassProperty(s1, "help", "oracleMon.htm");
        setClassProperty(s1, "title", "Oracle Database");
        setClassProperty(s1, "class", "OracleJDBCMonitor");
        setClassProperty(s1, "target", "_server");
        setClassProperty(s1, "loadable", "true");
        setClassProperty(s1, "classType", "application");
        setClassProperty(s1, "topazName", "Oracle");
        setClassProperty(s1, "topazType", "Database Server");
    }
}
