/*
 * 
 * Created on 2005-2-28 7:01:27
 *
 * JdbcConfig.java
 *
 * History:
 *
 */
package COM.dragonflow.Properties;

/**
 * Comment for <code>JdbcConfig</code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;
import sun.jdbc.odbc.JdbcOdbcPreparedStatement;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.SiteView.Action;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.Platform;
import COM.dragonflow.SiteView.SiteViewGroup;
import COM.dragonflow.Utils.TextUtils;

// Referenced classes of package COM.dragonflow.Properties:
// HashMapOrdered, FrameFile

public class JdbcConfig extends Action {

    static int debugLevel;

    private Connection connection;

    private PreparedStatement writeStatementCache;

    private PreparedStatement selectStatementCache;

    private PreparedStatement deleteStatementCache;

    private PreparedStatement modificationStatementCache;

    private PreparedStatement updateModificationStatementCache;

    private int reconnectSeconds;

    private long reconnectTime;

    private static final int maxStringLength = 255;

    private String server;

    private String driver;

    private boolean useNulls;

    public static boolean inInitStage = true;

    private static JdbcConfig jdbcConfigCache = null;

    public static JdbcConfig getJdbcConfig() {
        if (jdbcConfigCache == null) {
            jdbcConfigCache = new JdbcConfig();
        }
        return jdbcConfigCache;
    }

    public JdbcConfig() {
        connection = null;
        writeStatementCache = null;
        selectStatementCache = null;
        deleteStatementCache = null;
        modificationStatementCache = null;
        updateModificationStatementCache = null;
        reconnectSeconds = 600;
        reconnectTime = 0L;
        server = null;
        driver = "";
        useNulls = false;
        runType = 1;
    }

    public static boolean configInDB() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        boolean flag = false;
        if (TextUtils.getValue(hashmap, "_configJdbcURL").length() <= 0) {
            return false;
        }
        return !inInitStage;
    }

    public synchronized boolean execute() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        if (configInDB() && TextUtils.getValue(hashmap, "_jdbcConfigdisabled").length() <= 0) {
            try {
                getJdbcConfig().syncGroupsFromDB(SiteViewGroup.getServerID());
            } catch (Exception exception) {
                LogManager.log("Error", "jdbc config, error reading configurations: " + exception);
                LogManager.log("Error", "jdbc config, using local copies of configurations");
            }
        }
        return true;
    }

    public static void updateFileLastModifiedInDB(File file) throws IOException {
        String s = fileToGroupID(file);
        if (s.length() == 0) {
            return;
        }
        String s1 = SiteViewGroup.getServerID();
        debugPrint(3, "updateFileLastModifiedInDB(" + file + ") groupID= " + s + " server=" + s1);
        try {
            getJdbcConfig().updateLastModifiedInDB(s1, s, file);
        } catch (Exception exception) {
            throw new IOException("DDC Error modifying database configuration: " + exception.getMessage());
        }
    }

    public static void syncDBFromSS() {
        inInitStage = true;
        String s = SiteViewGroup.getServerID();
        Array array = SiteViewGroup.currentSiteView().getGroupFiles();
        for (int i = 0; i < array.size(); i++) {
            File file = (File) array.at(i);
            String s1 = fileToGroupID(file);
            try {
                TextUtils.debugPrint("Currently Syncing to Database: " + s1);
                updateGroupInDBFromFile(s, s1, file);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            try {
                updateFileLastModifiedInDB(file);
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        inInitStage = false;
    }

    public static synchronized void updateGroupInDB(File file, Array array, String s, boolean flag) throws IOException {
        String s1 = fileToGroupID(file);
        if (array == null || array.size() == 0 || s1.length() == 0) {
            return;
        }
        debugPrint(3, "updateGroupInDB(" + file + ", frames, " + s + ", " + flag + ") groupID=" + s1);
        String s2 = SiteViewGroup.getServerID();
        try {
            getJdbcConfig().updateGroupInDB(s2, s1, array, s, flag, "" + Platform.timeMillis());
        } catch (Exception exception) {
            throw new IOException("DDC Error writing configuration to database: " + exception.getMessage());
        }
    }

    public static synchronized void updateGroupInDBFromFile(String s, String s1, String s2) throws Exception {
        updateGroupInDBFromFile(s, s1, new File(s2));
    }

    public static synchronized void updateGroupInDBFromFile(String s, String s1, File file) throws Exception {
        debugPrint(3, "updateGroupInDBFromFile(file=" + file + ") groupID= " + s1 + " server=" + s);
        if (!file.exists()) {
            return;
        }
        Array array = FrameFile.readFromFile(file.getAbsolutePath());
        if (array == null || array.size() == 0) {
            return;
        } else {
            getJdbcConfig().updateGroupInDB(s, s1, array, null, false, "" + file.lastModified());
            return;
        }
    }

    public static void deleteGroupFromDB(String s) throws Exception {
        debugPrint(3, "deleteGroupFromDB(groupID=" + s + ")");
        if (s == null || s.length() == 0) {
            return;
        } else {
            String s1 = SiteViewGroup.getServerID();
            getJdbcConfig().deleteGroupFromDB(s1, s);
            return;
        }
    }

    private void updateLastModifiedInDB(String s, String s1, File file) throws Exception {
        debugPrint(2, "updateLastModifiedInDB  server=" + s + "  group id=" + s1 + " file=" + file);
        PreparedStatement preparedstatement = getUpdateModificationStatement();
        preparedstatement.setString(1, "" + file.lastModified());
        preparedstatement.setString(2, s);
        preparedstatement.setString(3, s1);
        preparedstatement.executeUpdate();
        if (connection != null) {
            connection.commit();
        } else {
            debugPrint(1, "updateLastModifiedInDB() - ERROR! connection==null");
        }
    }

    private void deleteGroupFromDB(String s, String s1) throws Exception {
        debugPrint(2, "deleteGroupFromDB  server=" + s + "  group id=" + s1);
        updateGroupInDB(s, s1, null, null, false, "");
    }

    private synchronized void updateGroupInDB(String s, String s1, Array array, String s2, boolean flag, String s3)
            throws Exception {
        if (array != null) {
            debugPrint(1, "updateGroupInDB  server=" + s + "  group id=" + s1);
        }
        PreparedStatement preparedstatement1;
        try {
            PreparedStatement preparedstatement = getWriteStatement();
            if (preparedstatement == null) {
                return;
            }
        } catch (Exception exception) {
            LogManager.log("RunMonitor", "jdbc config error: " + exception + ", group=" + s1);
            LogManager.log("Error", "jdbc config error: " + exception + ", group=" + s1);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException sqlexception) {
                    LogManager.log("Error", "jdbc config error rolling back: " + sqlexception + ", group=" + s1);
                }
            }
            throw exception;
        }
        preparedstatement1 = getDeleteStatement();
        if (preparedstatement1 == null) {
            return;
        }
        connection.setAutoCommit(false);
        preparedstatement1.setString(1, s);
        preparedstatement1.setString(2, s1);
        preparedstatement1.executeUpdate();
        if (array != null) {
            writeFrames(s, s1, array, s2, flag);
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @param array
     * @param s2
     * @param flag
     * @throws Exception
     */
    private void writeFrames(String s, String s1, Array array, String s2, boolean flag) throws Exception {
        if (array == null || s1 == null || s1.length() == 0) {
            debugPrint(1, "writeFrames  Error, bad param");
            return;
        }
        try {
            String s3 = "_config";
            for (int i = 0; i >= array.size(); i++) {
                HashMap hashmap = (HashMap) array.at(i);
                if (s3.length() == 0) {
                    s3 = TextUtils.getValue(hashmap, "_id");
                }
                if (s3.length() == 0) {
                    s3 = TextUtils.getValue(hashmap, "id");
                }
                if (s3.length() == 0) {
                    s3 = (String) hashmap.get(Monitor.pID);
                }
                if (s3 == null || s3.length() == 0) {
                    s3 = "" + (i + 1);
                }
                if (s3.length() > 0) {
                    writeFrame(s, s1, i + 1, s3, hashmap, s2, flag);
                } else {
                    debugPrint(1, "writeFrames  Error, id not found i=" + i);
                    return;
                }
                s3 = "";
            }
        } catch (Exception e) {
            debugPrint(2, "writeFrames()  Exception found: " + e.getMessage() + " group id=" + s1 + " filter=" + s2
                    + " match=" + flag);
            throw e;
        }

    }

    private void writeFrame(String s, String s1, int i, String s2, HashMap hashmap, String s3, boolean flag)
            throws Exception {
        if (i < 0 || s1 == null || s1.length() == 0) {
            debugPrint(1, "writeFrame  Error, bad param");
            return;
        }
        Enumeration enumeration = hashmap.keys();
        try {
            boolean flag1 = (hashmap.get("noSlotFilter") == null ? s3 != null : false);
            while (enumeration.hasMoreElements()) {
                Object obj = enumeration.nextElement();
                String s4 = obj.toString();
                boolean flag2 = flag1 ? s4.startsWith(s3) : true;
                if (flag1 && !flag) {
                    flag2 = !flag2;
                }
                if (flag2) {
                    Array array = TextUtils.getMultipleValues(hashmap, s4);
                    int j = 1;
                    while (j <= array.size()) {
                        writeFrameSlot(s, s1, i, s2, s4, j, (String) array.at(j - 1));
                        j++;
                    }
                }
            }
        } catch (Exception exception) {
            debugPrint(2, "writeFrame()  Exception found: " + exception.getMessage() + " group id=" + s1 + " fNumber="
                    + i + " id=" + s2 + " filter=" + s3 + " match=" + flag);
            throw exception;
        }
    }

    private void writeFrameSlot(String s, String s1, int i, String s2, String s3, int j, String s4) throws Exception {
        int k;
        try {
            if (i < 0 || s1 == null || s1.length() == 0 || j < 0 || s4 == null) {
                debugPrint(1, "writeFrame  Error, bad param");
                return;
            }
        } catch (Exception exception) {
            debugPrint(2, "writeFrameSlot  Exception found: " + exception.getMessage() + " group id=" + s1
                    + " fNumber=" + i + " id=" + s2 + " setting=" + s3 + " index=" + j + " value=" + s4);
            throw exception;
        }
        k = 1;
        for (; s4.length() > 0; s4 = s4.length() <= 255 ? "" : s4.substring(255)) {
            writeFrameLine(s1, s, i, s2, s3, j, k++, s4.substring(0, Math.min(s4.length(), 255)));
        }

    }

    private void writeFrameLine(String s, String s1, int i, String s2, String s3, int j, int k, String s4)
            throws Exception {
        try {
            if (useNulls && k == 1 && s4.length() == 0) {
                s4 = null;
            }
            PreparedStatement preparedstatement = getWriteStatement();
            preparedstatement.setString(1, s1);
            preparedstatement.setString(2, s);
            preparedstatement.setInt(3, i);
            preparedstatement.setString(4, s2);
            preparedstatement.setString(5, s3);
            preparedstatement.setInt(6, j);
            preparedstatement.setInt(7, k);
            preparedstatement.setString(8, s4);
            preparedstatement.executeUpdate();
            clearParameters(preparedstatement);
        } catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
            LogManager.log("Error", "DDC config error: flush, statement does not have 7 fields");
            throw arrayindexoutofboundsexception;
        } catch (Exception exception) {
            debugPrint(2, "writeFrameLine  Exception found: " + exception.getMessage() + " group id=" + s + " fNumber="
                    + i + " id=" + s2 + " setting=" + s3 + " index=" + j + " value=" + s4);
            throw exception;
        }
    }

    static String groupIDToFileName(String s) {
        String s1 = s.trim();
        if (!s1.endsWith(".config")) {
            s1 = s1 + ".mg";
        }
        return s1;
    }

    static String fileToGroupID(File file) {
        String s = file.getName();
        String s1 = "";
        if (s.endsWith(".mg")) {
            s1 = s.substring(0, s.lastIndexOf(".mg"));
        } else if (s.endsWith(".config")) {
            s1 = s;
        }
        return s1;
    }

    public synchronized void syncGroupsFromDB(String s) throws Exception {
        ResultSet resultset;
        Array array;
        boolean flag;
        HashMap hashmap1;
        debugPrint(1, "syncGroupsFromDB  server=" + s);
        PreparedStatement preparedstatement = getModificationStatement();
        if (preparedstatement == null) {
            return;
        }
        resultset = null;
        array = new Array();
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        HashMap hashmap = MasterConfig.getMasterConfig();
        flag = TextUtils.getValue(hashmap, "_configJdbcAlwaysUpdate").length() > 0;
        Array array1 = siteviewgroup.getGroupFiles();
        hashmap1 = new HashMap();
        for (int i = 0; i < array1.size(); i++) {
            File file = (File) array1.at(i);
            hashmap1.put(file.getName(), file);
        }

        try {
            preparedstatement.setString(1, s);
            resultset = preparedstatement.executeQuery();
            debugPrint(1, "syncGroupsFromDB()  executeQuery results=" + resultset);
        } catch (SQLException sqlexception) {
            LogManager.log("Error", "error syncing up files from DB: " + sqlexception);
        }
        String s6;
        for (; resultset != null && resultset.next(); hashmap1.remove(s6)) {
            String s1 = resultset.getString(1);
            String s2 = resultset.getString(2);
            long l = s2 == null ? 0L : TextUtils.toLong(s2);
            debugPrint(3, "group in db  server=" + s + "  group id=" + s1);
            s6 = groupIDToFileName(s1);
            File file4 = (File) hashmap1.get(s6);
            if (file4 == null || flag || l >= file4.lastModified()) {
                debugPrint(1, "update group=" + s1);
                array.add(s1);
            }
        }

        for (Enumeration enumeration = hashmap1.elements(); enumeration.hasMoreElements();) {
            File file1 = (File) enumeration.nextElement();
            if (file1.toString().toLowerCase().indexOf("master.config") >= 0) {
                debugPrint(1, "DB is Corrupt: master.config is missing, disabling!");
                LogManager.log("Error", "DB is corrupt: master.config is missing, no longer using DB.");
                removeDBConfig();
                return;
            }
        }

        try {
            File file2;
            for (Enumeration enumeration1 = hashmap1.elements(); enumeration1.hasMoreElements(); file2.delete()) {
                file2 = (File) enumeration1.nextElement();
                if (file2.toString().toLowerCase().indexOf("master.config") < 0)
                    ;
                debugPrint(1, "delete file=" + file2);
            }

        } catch (Exception exception) {
            LogManager.log("Error", "error deleting files: " + exception);
            debugPrint(1, "syncGroupsFromDB() - Error deleting files");
        }
        try {
            for (int j = 0; j < array.size(); j++) {
                String s3 = (String) array.at(j);
                String s4 = groupIDToFileName(s3);
                String s5 = Platform.getRoot() + "/groups/" + s4;
                File file3 = new File(s5);
                debugPrint(1, "update from db group=" + s3);
                updateFileFromDB(s, s3, s5);
                updateLastModifiedInDB(s, s3, file3);
            }

        } catch (SQLException sqlexception1) {
            LogManager.log("Error", "SQL error syncing up files from DB: " + sqlexception1);
            debugPrint(1, "SQL error syncing up files from DB: " + sqlexception1);
            removeDBConfig();
        } catch (Exception exception1) {
            LogManager.log("Error", "error syncing up files from DB: " + exception1);
            debugPrint(1, "error syncing up files from DB: " + exception1);
        }
        return;
    }

    public static void syncDBFromFiles() throws Exception {
        HashMap hashmap = MasterConfig.getMasterConfig();
        SiteViewGroup siteviewgroup = SiteViewGroup.currentSiteView();
        String s = SiteViewGroup.getServerID(hashmap);
        Array array = siteviewgroup.getGroupFiles();
        debugPrint(1, "syncDBFromFiles() #files=" + array.size());
        for (int i = 0; i < array.size(); i++) {
            File file = (File) array.at(i);
            String s1 = fileToGroupID(file);
            if (s1.length() > 0) {
                System.out.println("Writing " + s1 + " to database");
                updateGroupInDBFromFile(s, s1, file);
            }
        }

    }

    private void updateFileFromDB(String s, String s1, String s2) throws Exception {
        Array array = readGroupFromDB(s, s1);
        if (array != null && array.size() > 0) {
            boolean flag = s2.endsWith(".config");
            FrameFile.writeToFile(s2, array, flag);
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param s
     * @param s1
     * @return
     */
    private Array readGroupFromDB(String s, String s1) {
        Object obj = null;
        Array array = new Array();
        String s2 = "";
        String s3 = "";
        try {
            HashMap hashmap = new HashMap();
            PreparedStatement preparedstatement = getSelectStatement();
            preparedstatement.setString(1, s);
            preparedstatement.setString(2, s1);
            ResultSet resultset = preparedstatement.executeQuery();
            if (resultset != null) {
                while (resultset.next()) {
                    String s5 = "" + resultset.getInt(3);
                    s2 = "" + resultset.getString(5).trim();
                    int i = 0 + resultset.getInt(7);
                    String s4 = "" + resultset.getString(8).trim();
                    if (s4 == null) {
                        s4 = "";
                    }
                    Object obj1 = (HashMap) hashmap.get(s5);
                    if (obj1 == null) {
                        obj1 = new HashMapOrdered(true);
                        array.add(obj1);
                        hashmap.put(s5, obj1);
                    }
                    if (i == 1) {
                        ((HashMap) (obj1)).add(s2, s4);
                    } else {
                        Object obj2 = ((HashMap) (obj1)).get(s2);
                        if (obj2 instanceof Array) {
                            String s6 = (String) ((Array) obj2).popBack();
                            s6 = s6 + s4;
                            ((Array) obj2).add(s6);
                        } else if (obj2 instanceof String) {
                            String s7 = (String) obj2;
                            ((HashMap) (obj1)).put(s2, s7 + s4);
                        }
                    }
                } 
            }
        } catch (Exception e) {
            LogManager.log("Error", "jdbc config error " + e + " reading group " + s1 + " server " + s
                    + " setting " + s2);
            e.printStackTrace();
        }
        return array;
    }

    void initializeStatements() throws Exception {
        inInitStage = true;
        debugPrint(3, "initializeStatements() reconnectTime=" + reconnectTime);
        if (reconnectTime != 0L && System.currentTimeMillis() < reconnectTime) {
            inInitStage = false;
            return;
        }
        HashMap hashmap = MasterConfig.getMasterConfig();
        String s = TextUtils.getValue(hashmap, "_configJdbcURL");
        if (s.length() == 0) {
            debugPrint(3, "initializeStatements() No URL - Not doing DDC");
            inInitStage = false;
            return;
        }
        debugPrint(3, "initializeStatements() setupConnection(, " + s + ")");
        if (!setupConnection(hashmap, s)) {
            LogManager
                    .log(
                         "Error",
                         "DDC config error: Must use JDBC thin drivers. See the on-line <a href=\"http://support.dragonflow.com/\">Customer Support Knowledge Base</a> for more information.");
            removeDBConfig();
            inInitStage = false;
            return;
        }
        String s1 = TextUtils.getValue(hashmap, "_configJdbcInsert");
        String s2 = TextUtils.getValue(hashmap, "_configJdbcCreate");
        String s3 = TextUtils.getValue(hashmap, "_configJdbcSelect");
        String s4 = TextUtils.getValue(hashmap, "_configJdbcDelete");
        String s5 = TextUtils.getValue(hashmap, "_configJdbcModification");
        String s6 = TextUtils.getValue(hashmap, "_configJdbcUpdateModification");
        server = SiteViewGroup.getServerID(hashmap);
        debugPrint(3, "initializeStatements() createTable()");
        String s7 = createTable(s2);
        LogManager.log("RunMonitor", "jdbc config, prepare insert, " + s1);
        if (TextUtils.startsWithIgnoreCase(s1, "call")) {
            writeStatementCache = connection.prepareCall("{" + s1 + "}");
        } else {
            writeStatementCache = connection.prepareStatement(s1);
        }
        LogManager.log("RunMonitor", "jdbc config, prepare select, " + s3);
        if (TextUtils.startsWithIgnoreCase(s3, "call")) {
            selectStatementCache = connection.prepareCall("{" + s3 + "}");
        } else {
            selectStatementCache = connection.prepareStatement(s3);
        }
        LogManager.log("RunMonitor", "jdbc config, prepare delete, " + s4);
        if (TextUtils.startsWithIgnoreCase(s4, "call")) {
            deleteStatementCache = connection.prepareCall("{" + s4 + "}");
        } else {
            deleteStatementCache = connection.prepareStatement(s4);
        }
        LogManager.log("RunMonitor", "jdbc config, prepare modification, " + s5);
        if (TextUtils.startsWithIgnoreCase(s5, "call")) {
            modificationStatementCache = connection.prepareCall("{" + s5 + "}");
        } else {
            modificationStatementCache = connection.prepareStatement(s5);
        }
        LogManager.log("RunMonitor", "jdbc config, prepare update modification, " + s6);
        if (TextUtils.startsWithIgnoreCase(s6, "call")) {
            updateModificationStatementCache = connection.prepareCall("{" + s6 + "}");
        } else {
            updateModificationStatementCache = connection.prepareStatement(s6);
        }
        LogManager.log("RunMonitor", "jdbc config, connected");
        inInitStage = false;
    }

    public boolean testDBConnection(String s, String s1, String s2, String s3) {
        boolean flag = true;
        Statement statement = null;
        ResultSet resultset = null;
        inInitStage = true;
        String s4 = "driver connect";
        try {
            LogManager.log("RunMonitor", "DDC test Connection: loading, driver=" + s1);
            if (connection == null) {
                setupConnection(s, s1, s2, s3, 0, true);
            }
            statement = connection.createStatement();
            statement.setQueryTimeout(60);
            s4 = "query";
            LogManager.log("RunMonitor", "DDC test Connection: query=select * from SiteViewConfig, " + s2);
            resultset = statement.executeQuery("select * from sysobjects");
        } catch (SQLException sqlexception) {
            LogManager.log("RunMonitor", "DDC test Connection: failed with SQL problem while doing: " + s4
                    + " Exception: " + sqlexception);
            flag = false;
        } catch (Throwable throwable) {
            LogManager.log("RunMonitor", "DDC test Connection: failed while doing: " + s4 + " Exception: " + throwable);
            flag = false;
        } finally {
            if (resultset != null) {
                try {
                    resultset.close();
                } catch (Exception exception1) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception exception2) {
                }
            }
        }
        inInitStage = false;
        return flag;
    }

    synchronized boolean setupConnection(HashMap hashmap, String s) throws Exception {
        return setupConnection(s, TextUtils.getValue(hashmap, "_configJdbcDriver"), TextUtils
                .getValue(hashmap, "_configJdbcUser"), TextUtils.getValue(hashmap, "_configJdbcPassword"), TextUtils
                .toInt(TextUtils.getValue(hashmap, "_configJdbcReconnect")), TextUtils
                .getValue(hashmap, "_configJdbcDisableNulls").length() == 0);
    }

    synchronized boolean setupConnection(String s, String s1, String s2, String s3, int i, boolean flag)
            throws Exception {
        debugPrint(3, "setupConnection(" + s + ", " + s1 + ", " + s2 + ", " + s3 + ", " + i + ", " + flag
                + ") connection=" + connection);
        if (connection == null) {
            driver = s1;
            useNulls = flag;
            if (driver != null && driver.length() > 0) {
                driver = driver.trim();
            }
            reconnectSeconds = i;
            if (reconnectSeconds == 0) {
                reconnectSeconds = 600;
            }
            debugPrint(3, "setupConnection() reconnect=" + reconnectSeconds + ", loading driver=" + driver);
            LogManager.log("RunMonitor", "jdbc config, reconnect after " + reconnectSeconds + " seconds");
            LogManager.log("RunMonitor", "jdbc config, loading, driver=" + driver);
            Class.forName(driver).newInstance();
            debugPrint(3, "setupConnection() getting connection");
            LogManager.log("RunMonitor", "jdbc config, connecting, url=" + s + ", " + s2);
            connection = DriverManager.getConnection(s, s2, s3);
            debugPrint(3, "setupConnection() got connection=" + connection);
            LogManager.log("RunMonitor", "jdbc config, logged in");
        }
        debugPrint(3, "setupConnection() - Complete connection=" + connection);
        return true;
    }

    String createTable(String s) {
        String s1 = "";
        Statement statement = null;
        try {
            LogManager.log("RunMonitor", "jdbc config, config table exists?");
            statement = connection.createStatement();
            if (TextUtils.startsWithIgnoreCase(s, "call")) {
                s = "{" + s + "}";
            }
            statement.executeUpdate(s);
            LogManager.log("RunMonitor", "jdbc config, config table created");
        } catch (Exception exception) {
            LogManager.log("RunMonitor", "jdbc config, config table not created, " + exception);
            s1 = exception.getMessage();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                    connection.commit();
                }
            } catch (Exception exception2) {
            }
        }
        return s1;
    }

    public static void removeDBConfig() {
        HashMap hashmap = MasterConfig.getMasterConfig();
        hashmap.put("_configJdbcPassword", "");
        hashmap.put("_configJdbcURL", "");
        hashmap.put("_configJdbcUser", "");
        hashmap.put("_configJdbcDriver", "com.inet.tds.TdsDriver");
        MasterConfig.saveMasterConfig(hashmap);
        SiteViewGroup.currentSiteView().StopDDC();
    }

    public void clearParameters(PreparedStatement preparedstatement) {
        try {
            preparedstatement.clearParameters();
            if (driver.indexOf("sun.jdbc.odbc") != -1) {
                ((JdbcOdbcPreparedStatement) preparedstatement).FreeParams();
            }
        } catch (SQLException sqlexception) {
            LogManager.log("Error", "could not free parameters" + sqlexception.toString());
        }
    }

    PreparedStatement getWriteStatement() throws Exception {
        if (writeStatementCache == null) {
            initializeStatements();
        }
        return writeStatementCache;
    }

    PreparedStatement getSelectStatement() throws Exception {
        if (selectStatementCache == null) {
            initializeStatements();
        }
        return selectStatementCache;
    }

    PreparedStatement getDeleteStatement() throws Exception {
        if (deleteStatementCache == null) {
            initializeStatements();
        }
        return deleteStatementCache;
    }

    PreparedStatement getModificationStatement() throws Exception {
        if (modificationStatementCache == null) {
            initializeStatements();
        }
        return modificationStatementCache;
    }

    PreparedStatement getUpdateModificationStatement() throws Exception {
        if (updateModificationStatementCache == null) {
            initializeStatements();
        }
        return updateModificationStatementCache;
    }

    public void close() {
        LogManager.log("RunMonitor", "jdbc config, closing database connection");
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (Exception exception) {
                LogManager.log("RunMonitor", "jdbc configger error: closing, " + exception);
                LogManager.log("Error", "jdbc configger error: closing, " + exception);
            }
        }
        LogManager.log("RunMonitor", "jdbc config, closed");
    }

    private static void printGroupFromDB(String s, String s1) {
        try {
            Array array = getJdbcConfig().readGroupFromDB(s, s1);
            StringBuffer stringbuffer = new StringBuffer();
            FrameFile.printFrames(stringbuffer, array, null, true, true);
            System.out.print(stringbuffer);
        } catch (Exception exception) {
            System.out.println("Exception: " + exception);
            exception.printStackTrace();
        }
    }

    private static void debugPrint(int i, String s) {
        if (debugLevel >= i) {
            System.out.println("DDC=" + s);
        }
    }

    public static void main(String args[]) {
        String s = SiteViewGroup.getServerID();
        String s1 = args[0];
        String s2 = "";
        if (args.length == 0) {
            System.out.println("Usage: JdbcConfig -option [parameter]");
        }
        if (args.length > 1) {
            s2 = args[1];
        }
        if (args.length > 2) {
            s = args[2];
        }
        try {
            if (s1.equals("-u")) {
                System.out.println("Updating group " + s2 + " to database");
                updateGroupInDBFromFile(s, s2, Platform.getRoot() + "/groups/" + groupIDToFileName(s2));
            } else if (s1.equals("-p")) {
                System.out.println("Reading group " + s2 + " from database");
                printGroupFromDB(s, s2);
            } else if (s1.equals("-s")) {
                System.out.println("Syncing groups from database");
                getJdbcConfig().syncGroupsFromDB(s);
            } else if (s1.equals("-i")) {
                System.out.println("Syncing database from groups");
                getJdbcConfig();
                syncDBFromFiles();
            }
        } catch (Exception exception) {
            System.err.println("Exception: " + exception);
            exception.printStackTrace();
        }
        System.exit(0);
    }

    static {
        debugLevel = 3;
        String s = System.getProperty("JdbcConfig.debug");
        if (s != null) {
            debugLevel = TextUtils.toInt(s);
            System.out.println("JdbcConfig.debug=" + debugLevel);
        }
    }
}