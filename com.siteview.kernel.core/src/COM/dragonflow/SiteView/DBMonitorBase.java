/*
 * 
 * Created on 2005-2-15 12:48:14
 *
 * DBMonitorBase.java
 *
 * History:
 *
 */
package COM.dragonflow.SiteView;

/**
 * Comment for <code>DBMonitorBase</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.Utils.FileUtils;
import COM.dragonflow.Utils.HTTPUtils;
import COM.dragonflow.Utils.I18N;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.SiteView:
// AtomicMonitor, Platform, Monitor, MasterConfig

public abstract class DBMonitorBase extends AtomicMonitor {

    private static boolean cacheConnections = false;

    private Connection connection;

    public static final String statusName[] = { "status", "no data" };

    public static final String resultContentsName[] = { "resultContents", "" };

    public static final String rowsName[] = { "rows", "n/a", "rows" };

    public static final String roundTripTimeName[] = { "roundTripTime", "0",
            "round trip time" };

    public static final String matchValueName[] = { "matchValue", null,
            "content match" };

    public static final String column1Name[] = { "column1", "n/a", null,
            "result column 1", " the result of query, first column" };

    public static final String column2Name[] = { "column2", "n/a", null,
            "result column 2", " the result of query, second column" };

    public static final String databaseName[] = {
            "_database",
            "",
            "host name",
            "Database Connection URL",
            "Enter the URL to the database connection (for example, if the ODBC connection is called test, the URL would be jdbc:odbc:test)" };

    public static final String queryName[] = {
            "_query",
            "",
            "query",
            "Query",
            "the SQL statement to be performed on the database.  (for example, select * from sysobjects)" };

    public static final String driverName[] = { "_driver",
            "sun.jdbc.odbc.JdbcOdbcDriver", "driver", "Database Driver",
            "the driver used to connect to the database" };

    public static final String userName[] = { "_user", "", "user name",
            "Database User Name",
            "optional, user name used to connect to the database" };

    public static final String passName[] = { "_password", "", "password",
            "Database Password",
            "optional,  password used to connect to the database" };

    public static final String path2QueryFile[] = { "_path", "", null,
            "File Path", "Excute a query stored in a file" };

    public static final String connectionTimeoutName[] = { "_connectTimeout",
            "60", null, "Connection Timeout",
            "the time out, in seconds, to wait for a database connection" };

    public static final String queryTimeoutName[] = { "_queryTimeout", "60",
            null, "Query Timeout",
            "the time out, in seconds, to wait for a database query" };

    public static final String contentMatchName[] = {
            "_content",
            null,
            null,
            "Match Content",
            "optional, match against query result, using a string or a <a href=/SiteView/docs/regexp.htm>regular expression</a> or <a href=/SiteView/docs/XMLMon.htm>XML names</a>." };

    public static final String columnLabelNames[] = { "_columnLabels", "",
            null, "Column Labels",
            "Labels for the two columns returned by the query, separated by a \",\"" };

    private long lastUpdate;

    boolean driverLoaded;

    HashMap labelsCache;

    public DBMonitorBase() {
        connection = null;
        driverLoaded = false;
        labelsCache = null;
    }

    public static String getBasePropertyName(String as[]) {
        return as[0];
    }

    public static String getBasePropertyDefValue(String as[]) {
        return as[1];
    }

    public static String getBasePropertyLabel(String as[]) {
        return as[2];
    }

    public static String getBasePropertyTitle(String as[]) {
        return as[3];
    }

    public static String getBasePropertyDesc(String as[]) {
        return as[4];
    }

    protected abstract StringProperty getDatabaseProperty();

    protected abstract StringProperty getQueryProperty();

    protected abstract StringProperty getUserProperty();

    protected abstract StringProperty getPasswordProperty();

    protected abstract StringProperty getConnectTimeoutProperty();

    protected abstract StringProperty getQueryTimeoutProperty();

    protected abstract StringProperty getDriverProperty();

    protected abstract StringProperty getContentMatchProperty();

    protected abstract StringProperty getQueryFilePathProperty();

    protected abstract StringProperty getColumnLabelsProperty();

    protected abstract StringProperty getStatusProperty();

    protected abstract StringProperty getResultContentsProperty();

    protected abstract StringProperty getRowsProperty();

    protected abstract StringProperty getRoundTripTimeProperty();

    protected abstract StringProperty getMatchValueProperty();

    protected abstract StringProperty getColumn1Property();

    protected abstract StringProperty getColumn2Property();

    public String getHostname() {
        return HTTPUtils.hostFromURL(getProperty(getDatabaseProperty()));
    }

    public String getTestURL() {
        String s = getProperty(getQueryProperty());
        if (s.length() > 0) {
            s = TextUtils.replaceString(s, " ", "+");
        }
        String s1 = "/SiteView/cgi/go.exe/SiteView?page=dbConnection&connectionURL="
                + getProperty(getDatabaseProperty())
                + "&driver="
                + getProperty(getDriverProperty())
                + "&username="
                + getProperty(getUserProperty()) + "&query=" + escapeHTML(s);
        return s1;
    }

    protected boolean update() {
        currentStatus = "DatabaseMonitor: getting the driver...";
        String s = getProperty(getDriverProperty());
        if (s != null && s.length() > 0) {
            s = s.trim();
        }
        String s1 = getProperty(getDatabaseProperty());
        String s2 = getProperty(getQueryProperty());
        String s3 = getProperty(getUserProperty());
        String s4 = getProperty(getPasswordProperty());
        String s5 = getProperty(getQueryFilePathProperty());
        String s6 = "";
        if (s5.length() > 0) {
            try {
                s6 = FileUtils.readFile(s5).toString();
            } catch (IOException ioexception) {
                LogManager.log("Error", " File load error "
                        + ioexception.toString());
            }
        }
        int i = StringProperty
                .toInteger(getProperty(getConnectTimeoutProperty()));
        int j = StringProperty
                .toInteger(getProperty(getQueryTimeoutProperty()));
        int k = -1;
        long l = -1L;
        String s7 = "";
        String s8 = "";
        String s9 = "";
        String s11 = "";
        String s12 = "";
        int i1 = -1000;
        Statement statement = null;
        ResultSet resultset = null;
        long l1 = getSettingAsLong("_databaseMaxColumns", 10);
        long l2 = getSettingAsLong("_databaseMaxRows", 1);
        int j1 = getSettingAsLong("_databaseMaxSummary", 100);
        long l3 = Platform.timeMillis();
        String s13 = "driver connect";
        currentStatus = "DatabaseMonitor: initializing the driver...";
        try {
            if (!driverLoaded) {
                LogManager.log("RunMonitor",
                        "database monitor, loading, driver=" + s);
                Class.forName(s).newInstance();
                driverLoaded = true;
            }
            currentStatus = "DatabaseMonitor: connecting...";
            s13 = "connect";
            LogManager.log("RunMonitor",
                    "database monitor, connecting, database=" + s1 + ", " + s3);
            if (!cacheConnections || connection == null) {
                createConnection(i, s1, s3, s4);
            }
            try {
                statement = connection.createStatement();
            } catch (SQLException sqlexception) {
                statement = recreateConnectionAndStatment(i, s1, s3, s4,
                        statement, sqlexception);
            }
            if (j > 0) {
                statement.setQueryTimeout(j);
            }
            s13 = "query";
            currentStatus = "DatabaseMonitor: executing the query...";
            try {
                resultset = executeQuery(s5, statement, s6, resultset, s2);
            } catch (SQLException sqlexception1) {
                statement = recreateConnectionAndStatment(i, s1, s3, s4,
                        statement, sqlexception1);
                if (j > 0) {
                    statement.setQueryTimeout(j);
                }
                resultset = executeQuery(s5, statement, s6, resultset, s2);
            }
            currentStatus = "DatabaseMonitor: analyzing results...";
            if (resultset == null) {
                throw new Exception("no results from query");
            }
            s13 = "results";
            k = 0;
            int k1 = resultset.getMetaData().getColumnCount();
            if (l1 > (long) k1) {
                l1 = k1;
            }
            while (resultset.next()) {
                String as[] = processResults(resultset, s8, k, s11, s12, l2, l1);
                s8 = as[0];
                s11 = as[1];
                s12 = as[2];
                k++;
            }
            String s10 = I18N.UnicodeToString(s8, "");
            if (s10.length() > j1) {
                s10 = s10.substring(0, j1) + " ...";
            }
            l = Platform.timeMillis() - l3;
            currentStatus = "DatabaseMonitor: getting warnings...";
            SQLWarning sqlwarning = connection.getWarnings();
            int j2 = 1;
            String s14 = getProperty("_class") + "Warning";
            for (; sqlwarning != null
                    && getSetting("_databaseHideWarning").length() <= 0; sqlwarning = sqlwarning
                    .getNextWarning()) {
                if (getProperty(s14).length() > 0
                        || getProperty(s14 + ":" + sqlwarning.getErrorCode())
                                .length() > 0) {
                    LogManager.log("Error", "Database warning " + j2++ + ", "
                            + getGroupPathID() + ": " + getProperty(pName)
                            + ", " + sqlwarning.getMessage() + ", "
                            + sqlwarning.getErrorCode() + ", "
                            + sqlwarning.getSQLState());
                }
            }

            String s15 = TextUtils.floatToString((float) l / 1000F, 2) + " sec";
            String s16 = "rows";
            if (k == 1) {
                s16 = "row";
            }
            s7 = s15 + ", " + k + " " + s16 + ", " + s10;
            i1 = 200;
            s13 = "content match";
            String s17 = I18N
                    .UnicodeToString(getProperty(getContentMatchProperty()),
                            I18N.nullEncoding());
            if (s17.length() != 0) {
                StringBuffer stringbuffer = new StringBuffer();
                Array array = new Array();
                i1 = TextUtils.matchExpression(s8, s17, array, stringbuffer);
                if (i1 != Monitor.kURLok) {
                    i1 = TextUtils.matchExpression(s8, I18N.StringToUnicode(
                            s17, ""), array, stringbuffer);
                }
                if (i1 != 200) {
                    s7 = "content match error, " + s7;
                }
                if (array.size() > 0) {
                    String s18 = I18N.UnicodeToString((String) array.at(0), "");
                    s18 = I18N.StringToUnicode(s18, I18N.nullEncoding());
                    setProperty(getMatchValueProperty(), s18);
                    s7 = "matched: " + TextUtils.escapeHTML(s18) + ", " + s7;
                }
            }
            s13 = "closing";
        } catch (SQLException sqlexception2) {
            i1 = -1;
            s7 = s13 + " error, " + sqlexception2.getMessage();
            int i2 = 1;
            for (; sqlexception2 != null; sqlexception2 = sqlexception2
                    .getNextException()) {
                LogManager.log("Error", "Database error " + i2++ + ", " + s13
                        + ", " + getGroupPathID() + ": " + getProperty(pName)
                        + ", " + sqlexception2.getMessage() + ", "
                        + sqlexception2.getErrorCode() + ", "
                        + sqlexception2.getSQLState());
            }

        } catch (Throwable throwable) {
            i1 = -1;
            s7 = s13 + " error, " + throwable.getMessage();
            LogManager.log("Error", "Database error, " + s13 + ", "
                    + getGroupPathID() + ": " + getProperty(pName) + ", "
                    + throwable.getMessage() + ", " + throwable);
        } finally {
            if (resultset != null) {
                try {
                    resultset.close();
                } catch (Exception exception1) {
                    LogManager.log("Error", "Database close resultSet error, "
                            + s13 + ", " + getGroupPathID() + ": "
                            + getProperty(pName) + ", " + exception1);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception exception2) {
                    LogManager.log("Error", "Database close statement error, "
                            + s13 + ", " + getGroupPathID() + ": "
                            + getProperty(pName) + ", " + exception2);
                }
            }
            if (connection != null) {
                try {
                    if (!cacheConnections) {
                        connection.close();
                    } else {
                        connection.clearWarnings();
                    }
                } catch (Exception exception3) {
                    LogManager.log("Error", "Database clear connection error, "
                            + s13 + ", " + getGroupPathID() + ": "
                            + getProperty(pName) + ", " + exception3);
                }
            }
        }
        if (stillActive()) {
            synchronized (this) {
                currentStatus = "DatabaseMonitor: setting values to properties...";
                setProperty(getRoundTripTimeProperty(), l);
                setProperty(getRowsProperty(), "" + k);
                if (s11 != null) {
                    setProperty(getColumn1Property(), s11);
                } else {
                    setProperty(getColumn1Property(), "n/a");
                }
                if (s12 != null) {
                    setProperty(getColumn2Property(), s12);
                } else {
                    setProperty(getColumn2Property(), "n/a");
                }
                if (i1 == -1) {
                    setProperty(getRoundTripTimeProperty(), "n/a");
                    setProperty(getRowsProperty(), "n/a");
                    setProperty(getColumn1Property(), "n/a");
                    setProperty(getColumn2Property(), "n/a");
                    setProperty(pNoData, "n/a");
                }
                setProperty(getStatusProperty(), i1);
                setProperty(pStateString, I18N.StringToUnicode(s7, I18N
                        .nullEncoding()));
                setProperty(getResultContentsProperty(), s8);
            }
        }
        return true;
    }

    private ResultSet executeQuery(String s, Statement statement, String s1,
            ResultSet resultset, String s2) throws SQLException {
        if (s.length() > 0) {
            resultset = statement.executeQuery(s1);
        } else {
            resultset = statement.executeQuery(s2);
        }
        return resultset;
    }

    private Statement recreateConnectionAndStatment(int i, String s, String s1,
            String s2, Statement statement, SQLException sqlexception)
            throws SQLException {
        if (cacheConnections) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlexception1) {
                }
            }
            createConnection(i, s, s1, s2);
            statement = connection.createStatement();
        } else {
            throw sqlexception;
        }
        return statement;
    }

    private void createConnection(int i, String s, String s1, String s2)
            throws SQLException {
        if (i > 0) {
            DriverManager.setLoginTimeout(i);
        }
        connection = DriverManager.getConnection(s, s1, s2);
    }

    protected String[] processResults(ResultSet resultset, String s, int i,
            String s1, String s2, long l, long l1) {
        if ((long) i < l) {
            try {
                if (i == 0) {
                    s1 = resultset.getString(1);
                    s = s + s1;
                    if (l1 > 1L) {
                        s2 = resultset.getString(2);
                        s = s + ", " + s2;
                        for (int j = 3; (long) j <= l1; j++) {
                            s = s + ", " + resultset.getString(j);
                        }

                    }
                } else {
                    s = s + "\n";
                    s = s + resultset.getString(1);
                    for (int k = 2; (long) k <= l1; k++) {
                        s = s + ", " + resultset.getString(k);
                    }

                }
            } catch (Exception exception) {
                TextUtils.debugPrint("e=" + exception);
            }
        }
        String as[] = { s, s1, s2 };
        return as;
    }

    public Array getLogProperties() {
        Array array = super.getLogProperties();
        array.add(getStatusProperty());
        array.add(getRoundTripTimeProperty());
        array.add(getColumn1Property());
        array.add(getColumn2Property());
        array.add(getRowsProperty());
        array.add(getMatchValueProperty());
        return array;
    }

    synchronized HashMap getLabels() {
        if (labelsCache == null) {
            labelsCache = new HashMap();
            if (getProperty(getColumnLabelsProperty()).length() > 0) {
                String as[] = TextUtils.split(
                        getProperty(getColumnLabelsProperty()), ",");
                if (as.length > 0) {
                    if (as[0] != null && as[0].length() > 0) {
                        labelsCache.add("result column 1", as[0].trim());
                    } else {
                        labelsCache.add("result column 1", "result column 1");
                    }
                }
                if (as.length > 1) {
                    if (as[1] != null && as[1].length() > 0) {
                        labelsCache.add("result column 2", as[1].trim());
                    } else {
                        labelsCache.add("result column 2", "result column 2");
                    }
                } else {
                    labelsCache.add("result column 2", "result column 2");
                }
            } else {
                labelsCache.add("result column 1", "result column 1");
                labelsCache.add("result column 2", "result column 2");
            }
            labelsCache.add("rows", "rows");
            labelsCache.add("round trip time", "round trip time");
            labelsCache.add("content match", "content match");
        }
        return labelsCache;
    }

    public String getPropertyName(StringProperty stringproperty) {
        String s = stringproperty.getName();
        String s1 = TextUtils.getValue(getLabels(), stringproperty.getLabel());
        if (s1.length() == 0) {
            s1 = s;
        }
        return s1;
    }

    public Enumeration getStatePropertyObjects(boolean flag) {
        Array array = new Array();
        array.add(getRoundTripTimeProperty());
        array.add(getColumn1Property());
        if (getProperty(getColumn2Property()).length() > 0) {
            array.add(getColumn2Property());
        }
        array.add(getRowsProperty());
        array.add(getMatchValueProperty());
        return array.elements();
    }

    public String GetPropertyLabel(StringProperty stringproperty, boolean flag) {
        String s = stringproperty.printString();
        String s1 = TextUtils.getValue(getLabels(), s);
        if (s1.length() != 0) {
            return s1;
        }
        if (flag) {
            return "";
        } else {
            return s;
        }
    }

    public String getProperty(StringProperty stringproperty)
            throws NullPointerException {
        if (stringproperty == pDiagnosticText) {
            return "Query Results:\n\n"
                    + getProperty(getResultContentsProperty());
        } else {
            return super.getProperty(stringproperty);
        }
    }

    public static void main(String args[]) throws Exception {
        System.out.println("registering...");
        Connection connection1 = DriverManager.getConnection(
                "jdbc:oracle:thin:@database." + Platform.exampleDomain
                        + ":1521:ORCL", "scott", "tiger");
        Statement statement = connection1.createStatement();
        for (ResultSet resultset = statement
                .executeQuery("select ENAME from EMP"); resultset.next(); System.out
                .println(resultset.getString(1))) {
        }
    }

    public String verify(StringProperty stringproperty, String s,
            HTTPRequest httprequest, HashMap hashmap) {
        if (stringproperty != null) {
            if (getBasePropertyName(databaseName).equals(
                    stringproperty.getName())) {
                if (s.length() == 0) {
                    hashmap.put(stringproperty, stringproperty.getLabel()
                            + " missing");
                } else if (TextUtils.hasSpaces(s)) {
                    hashmap.put(stringproperty, "no spaces are allowed");
                }
                return s;
            }
            if (getBasePropertyName(queryName).equals(stringproperty.getName())) {
                return s;
            }
            if (getBasePropertyName(path2QueryFile).equals(
                    stringproperty.getName())) {
                return s;
            }
            if (getBasePropertyName(queryTimeoutName).equals(
                    stringproperty.getName())) {
                if (TextUtils.digits(s) != s.length()) {
                    hashmap.put(stringproperty, "time out must be a number");
                } else if (TextUtils.toInt(s) < 0) {
                    hashmap
                            .put(stringproperty,
                                    "time out must be 0 or greater");
                }
                return s;
            }
            if (getBasePropertyName(connectionTimeoutName).equals(
                    stringproperty.getName())) {
                if (TextUtils.digits(s) != s.length()) {
                    hashmap.put(stringproperty, "time out must be a number");
                } else if (TextUtils.toInt(s) < 0) {
                    hashmap
                            .put(stringproperty,
                                    "time out must be 0 or greater");
                }
                return s;
            }
        }
        return super.verify(stringproperty, s, httprequest, hashmap);
    }

    static {
        StringProperty astringproperty[] = new StringProperty[0];
        String s = (COM.dragonflow.SiteView.DBMonitorBase.class).getName();
        addProperties(s, astringproperty);
        HashMap hashmap = MasterConfig.getMasterConfig();
        String s1 = TextUtils.getValue(hashmap,
                "_databaseMonitorCacheConnections");
        if (s1.length() > 0 && s1.equalsIgnoreCase("true")) {
            cacheConnections = true;
        }
    }
}
