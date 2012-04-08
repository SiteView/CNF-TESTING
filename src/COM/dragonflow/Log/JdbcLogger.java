/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Log;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import java.io.File;
import java.util.Enumeration;

import jgl.Array;
import jgl.HashMap;

// Referenced classes of package COM.dragonflow.Log:
// Logger, LogManager

public class JdbcLogger extends COM.dragonflow.Log.Logger
{

    public String logName;
    public static COM.dragonflow.Log.JdbcLogger logger = null;
    java.sql.Connection connection;
    int reconnectSeconds;
    long reconnectTime;
    int maxStringLength;
    int autoCommitCounter;
    int autoCommit;
    java.sql.PreparedStatement logStatementCache;
    jgl.Array logCache;
    String insertStatement;
    String server;
    int vars;
    String driver;
    String method;
    boolean useNulls;
    String propertyFilter[];
    String propertySkip[];
    boolean usingBackupSignalFile;
    boolean usingBackupDatabase;
    boolean debug;
    java.sql.PreparedStatement linkStatementCache;
    jgl.Array linkCache;
    String linkServer;
    int linkVars;
    static jgl.HashMap customCaches = new HashMap();
    static jgl.HashMap customStatements = new HashMap();

    public JdbcLogger(jgl.HashMap hashmap)
    {
        logName = "SiteViewLog";
        connection = null;
        reconnectSeconds = 600;
        reconnectTime = 0L;
        maxStringLength = 255;
        autoCommitCounter = 0;
        autoCommit = 20;
        logStatementCache = null;
        logCache = new Array();
        insertStatement = "";
        server = null;
        vars = 0;
        driver = "";
        method = "";
        useNulls = false;
        propertyFilter = null;
        propertySkip = null;
        usingBackupSignalFile = false;
        usingBackupDatabase = false;
        debug = false;
        linkStatementCache = null;
        linkCache = new Array();
        linkServer = "";
        logger = this;
        logCache = new Array();
        method = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcMethod" + logName);
        usingBackupDatabase = shouldUseBackupDatabase();
    }

    int countVars(String s)
    {
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            if(s.charAt(j) == '?')
            {
                i++;
            }
        }

        return i;
    }

    boolean shouldUseBackupDatabase()
    {
        String s = COM.dragonflow.SiteView.Platform.getRoot() + java.io.File.separator + "groups" + java.io.File.separator + "backupdatabase.enabled";
        return (new File(s)).exists();
    }

    synchronized void setupConnection(jgl.HashMap hashmap, String s)
        throws java.lang.Exception
    {
        if(connection == null)
        {
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcDebug").length() > 0)
            {
                debug = true;
            }
            if(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcDebugOutput").length() > 0)
            {
                java.sql.DriverManager.setLogStream(java.lang.System.out);
            }
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcURLBackup" + logName);
            usingBackupSignalFile = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcBackupSignal").length() > 0;
            if(usingBackupSignalFile)
            {
                if(usingBackupDatabase)
                {
                    s = s1;
                }
                s1 = "";
            }
            driver = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcDriver" + logName);
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcUser" + logName);
            String s3 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcPassword" + logName);
            String s4 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcPropertyFilter" + logName);
            if(s4.length() > 0)
            {
                propertyFilter = COM.dragonflow.Utils.TextUtils.split(s4, ",");
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, filter " + s4);
            }
            String s5 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcPropertySkip" + logName);
            if(s5.length() > 0)
            {
                propertySkip = COM.dragonflow.Utils.TextUtils.split(s5, ",");
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, skip " + s5);
            }
            int i = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcMaxString" + logName));
            if(i > 0)
            {
                maxStringLength = i;
            }
            autoCommit = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcAutoCommit" + logName));
            if(autoCommit == 0)
            {
                autoCommit = 20;
            }
            reconnectSeconds = COM.dragonflow.Utils.TextUtils.toInt(COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcReconnect" + logName));
            if(reconnectSeconds == 0)
            {
                reconnectSeconds = 600;
            }
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, reconnect after " + reconnectSeconds + " seconds");
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, commit after " + autoCommit + " records");
            if(method.length() > 0)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, method, " + method);
            }
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, loading, driver=" + driver);
            java.lang.Class.forName(driver).newInstance();
            try
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, connecting, url=" + s + ", " + s2);
                connection = java.sql.DriverManager.getConnection(s, s2, s3);
                connection.setAutoCommit(false);
            }
            catch(java.lang.Exception exception)
            {
                if(s1.length() != 0)
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, connecting, backup=" + s1 + ", " + s2);
                    connection = java.sql.DriverManager.getConnection(s1, s2, s3);
                    connection.setAutoCommit(false);
                } else
                {
                    throw exception;
                }
            }
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, logged in");
        }
    }

    String createTable(java.sql.Connection connection1, String s)
    {
        String s1 = "";
        java.sql.Statement statement = null;
        try
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, log table exists?");
            statement = connection1.createStatement();
            if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(s, "call"))
            {
                s = "{" + s + "}";
            }
            statement.executeUpdate(s);
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, log table created");
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, log table not created, " + exception);
            s1 = exception.getMessage();
        }
        finally
        {
            try
            {
                if(statement != null)
                {
                    statement.close();
                    connection1.commit();
                }
            }
            catch(java.lang.Exception exception2) { }
        }
        return s1;
    }

    public static String padWithZeros(int i, int j)
    {
        String s = "" + i;
        int k = s.length();
        for(int l = 0; l < j - k; l++)
        {
            s = "0" + s;
        }

        return s;
    }

    public static String jdbcDateFormat(java.util.Date date)
    {
        return (date.getYear() + 1900) + "-" + COM.dragonflow.Log.JdbcLogger.padWithZeros(date.getMonth() + 1, 2) + "-" + COM.dragonflow.Log.JdbcLogger.padWithZeros(date.getDate(), 2) + " " + COM.dragonflow.Log.JdbcLogger.padWithZeros(date.getHours(), 2) + ":" + COM.dragonflow.Log.JdbcLogger.padWithZeros(date.getMinutes(), 2) + ":" + COM.dragonflow.Log.JdbcLogger.padWithZeros(date.getSeconds(), 2);
    }

    boolean allowProperty(COM.dragonflow.Properties.PropertiedObject propertiedobject, COM.dragonflow.Properties.StringProperty stringproperty)
    {
        if(propertySkip != null)
        {
            String s = propertiedobject.getPropertyName(stringproperty);
            for(int i = 0; i < propertySkip.length; i++)
            {
                if(s.startsWith(propertySkip[i]))
                {
                    return false;
                }
            }

        }
        if(propertyFilter != null)
        {
            String s1 = propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pClass) + "." + propertiedobject.getPropertyName(stringproperty);
            for(int j = 0; j < propertyFilter.length; j++)
            {
                if(s1.indexOf(propertyFilter[j]) >= 0)
                {
                    return false;
                }
            }

        }
        return true;
    }

    public void clearParameters(java.sql.PreparedStatement preparedstatement)
    {
        try
        {
            preparedstatement.clearParameters();
            if(driver.indexOf("sun.jdbc.odbc") != -1)
            {
                ((sun.jdbc.odbc.JdbcOdbcPreparedStatement)preparedstatement).FreeParams();
            }
        }
        catch(java.sql.SQLException sqlexception)
        {
            COM.dragonflow.Log.LogManager.log("Error", "could not free parameters" + sqlexception.toString());
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public synchronized void log(String s, java.util.Date date, COM.dragonflow.Properties.PropertiedObject propertiedobject)
    {
        if(method.equals("customOnly"))
        {
            return;
        }
        
        try {
        Enumeration enumeration;
        jgl.Array array1;
        jgl.Array array = propertiedobject.getLogProperties();
        enumeration = array.elements();
        if(method.equals("oneRowPerProperty"))
        {
        array1 = new Array();
        int i = 0;
        for(byte byte0 = 5; enumeration.hasMoreElements() && i < byte0; i++)
        {
            COM.dragonflow.Properties.StringProperty stringproperty = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
            array1.add(stringproperty);
        }

        jgl.Array array2;
        java.sql.PreparedStatement preparedstatement1;
        while (enumeration.hasMoreElements())
        {
        array2 = new Array();
        preparedstatement1 = getStatement();
        if(preparedstatement1 == null)
        {
            return;
        }
        COM.dragonflow.Properties.StringProperty stringproperty2 = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
        if(allowProperty(propertiedobject, stringproperty2))
        {
            int k = 1;
            if(k <= vars)
            {
                array2.add(COM.dragonflow.Log.JdbcLogger.jdbcDateFormat(date));
                k++;
            }
            if(server != null && k <= vars)
            {
                array2.add(server);
                k++;
            }
            if(k <= vars)
            {
                array2.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pClass));
                k++;
            }
            if(k <= vars)
            {
                array2.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
                k++;
            }
            Enumeration enumeration1 = array1.elements();
            while (enumeration1.hasMoreElements())
                {
                COM.dragonflow.Properties.StringProperty stringproperty3 = (COM.dragonflow.Properties.StringProperty)enumeration1.nextElement();
                if(k <= vars)
                {
                    String s3 = propertiedobject.getProperty(stringproperty3);
                    if(stringproperty3 == COM.dragonflow.SiteView.AtomicMonitor.pName)
                    {
                        s3 = getMonitorName(propertiedobject);
                    }
                    if(s3.length() > maxStringLength)
                    {
                        s3 = s3.substring(0, maxStringLength);
                    }
                    array2.add(s3);
                    k++;
                }
            } 
            
            if(k <= vars)
            {
                array2.add(propertiedobject.getPropertyName(stringproperty2));
                k++;
            }
            if(k <= vars)
            {
                String s2 = propertiedobject.getProperty(stringproperty2);
                if(s2.length() > maxStringLength)
                {
                    s2 = s2.substring(0, maxStringLength);
                }
                array2.add(s2);
                k++;
            }
            for(; k <= vars; k++)
            {
                if(useNulls)
                {
                    array2.add(null);
                } else
                {
                    array2.add("");
                }
            }

            execute(preparedstatement1, logCache, array2);
        }
        }
        }

        java.sql.PreparedStatement preparedstatement;
        array1 = new Array();
        preparedstatement = getStatement();
        if(preparedstatement == null)
        {
            return;
        }

            int j = 1;
            if(j <= vars)
            {
                array1.add(COM.dragonflow.Log.JdbcLogger.jdbcDateFormat(date));
                j++;
            }
            if(server != null && j <= vars)
            {
                array1.add(server);
                j++;
            }
            if(j <= vars)
            {
                array1.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pClass));
                j++;
            }
            if(j <= vars)
            {
                array1.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
                j++;
            }
            while (enumeration.hasMoreElements() && j <= vars)
                {
                COM.dragonflow.Properties.StringProperty stringproperty1 = (COM.dragonflow.Properties.StringProperty)enumeration.nextElement();
                if(allowProperty(propertiedobject, stringproperty1))
                {
                    String s1 = propertiedobject.getProperty(stringproperty1);
                    if(stringproperty1 == COM.dragonflow.SiteView.AtomicMonitor.pName)
                    {
                        s1 = getMonitorName(propertiedobject);
                    }
                    if(s1.length() > maxStringLength)
                    {
                        s1 = s1.substring(0, maxStringLength);
                    }
                    array1.add(s1);
                    j++;
                }
            } 
            
            for(; j <= vars; j++)
            {
                if(useNulls)
                {
                    array1.add(null);
                } else
                {
                    array1.add("");
                }
            }

            execute(preparedstatement, logCache, array1);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error: " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            reconnectTime = (long)(reconnectSeconds * 1000) + java.lang.System.currentTimeMillis();
            closeConnection();
        }
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param date
     * @param propertiedobject
     * @param s
     * @param as
     * @param i
     */
    public synchronized void logFields(java.util.Date date, COM.dragonflow.Properties.PropertiedObject propertiedobject, String s, String as[], int i)
    {
        jgl.Array array = new Array();
        try {
        java.sql.PreparedStatement preparedstatement = getStatement();
        if(preparedstatement == null)
        {
            return;
        }
            int j = 1;
            if(j <= vars)
            {
                array.add(COM.dragonflow.Log.JdbcLogger.jdbcDateFormat(date));
                j++;
            }
            if(server != null && j <= vars)
            {
                array.add(server);
                j++;
            }
            if(j <= vars)
            {
                array.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pClass));
                j++;
            }
            if(j <= vars)
            {
                array.add(s);
                j++;
            }
            for(int k = 0; k < i && j <= vars; k++)
            {
                String s1 = as[k];
                if(s1.length() > maxStringLength)
                {
                    s1 = s1.substring(0, maxStringLength);
                }
                array.add(s1);
                j++;
            }

            for(; j <= vars; j++)
            {
                if(useNulls)
                {
                    array.add(null);
                } else
                {
                    array.add("");
                }
            }

            execute(preparedstatement, logCache, array);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error: " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            reconnectTime = (long)(reconnectSeconds * 1000) + java.lang.System.currentTimeMillis();
            closeConnection();
        }
        return;
    }

    java.sql.PreparedStatement getStatement()
        throws java.lang.Exception
    {
        if(usingBackupSignalFile && shouldFlush())
        {
            boolean flag = shouldUseBackupDatabase();
            if(flag != usingBackupDatabase)
            {
                closeConnection();
            }
            usingBackupDatabase = flag;
        }
        if(logStatementCache == null)
        {
            if(reconnectTime != 0L && java.lang.System.currentTimeMillis() < reconnectTime)
            {
                return null;
            }
            jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcURL" + logName);
            if(s.length() == 0)
            {
                return null;
            }
            setupConnection(hashmap, s);
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcInsert" + logName);
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcCreate" + logName);
            method = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcMethod" + logName);
            if(s2.indexOf("serverName") != -1)
            {
                server = COM.dragonflow.SiteView.SiteViewGroup.getServerID(hashmap);
            }
            String s3 = createTable(connection, s2);
            if(s3.indexOf("invalid column name") != -1)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, fixup date column, " + s3);
                s2 = COM.dragonflow.Utils.TextUtils.replaceString(s2, "date VAR", "datex VAR");
                hashmap.put("_logJdbcCreate" + logName, s2);
                COM.dragonflow.SiteView.MasterConfig.saveMasterConfig(hashmap);
                s3 = createTable(connection, s2);
            }
            vars = countVars(s1);
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, prepare insert, " + vars + ", " + s1);
            if(COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(s1, "call"))
            {
                logStatementCache = connection.prepareCall("{" + s1 + "}");
            } else
            {
                logStatementCache = connection.prepareStatement(s1);
            }
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, connected");
        }
        return logStatementCache;
    }

    /**
     * CAUTION: Decompiled by hand.
     */
    public synchronized void log(String s, java.util.Date date, String s1)
    {
        jgl.Array array;
        java.sql.PreparedStatement preparedstatement;
        array = new Array();
        try {
        preparedstatement = getStatement();
        if(preparedstatement == null)
        {
            return;
        }
            int i = 1;
            if(i < vars)
            {
                array.add(COM.dragonflow.Log.JdbcLogger.jdbcDateFormat(date));
                i++;
            }
            if(i < vars)
            {
                array.add(s1);
                i++;
            }
            for(; i <= vars; i++)
            {
                if(useNulls)
                {
                    array.add(null);
                } else
                {
                    array.add("");
                }
            }

            execute(preparedstatement, logCache, array);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error: " + exception);
            COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: " + exception);
            reconnectTime = (long)(reconnectSeconds * 1000) + java.lang.System.currentTimeMillis();
            closeConnection();
        }
        return;
    }

    boolean shouldFlush()
    {
        return autoCommit == -1 || autoCommitCounter % autoCommit == 0;
    }

    void execute(java.sql.PreparedStatement preparedstatement, jgl.Array array, jgl.Array array1)
        throws java.sql.SQLException
    {
        array.add(array1);
        if(array.size() > autoCommit)
        {
            flush(preparedstatement, array);
        }
        autoCommitCounter++;
    }

    void flush(java.sql.PreparedStatement preparedstatement, jgl.Array array)
        throws java.sql.SQLException
    {
        if(debug)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger commit start");
        }
        int i = array.size();
        java.sql.SQLException sqlexception = null;
        while(array.size() > 0) 
        {
            jgl.Array array1 = (jgl.Array)array.popFront();
            int j = array1.size();
            try
            {
                for(int k = 0; k < j; k++)
                {
                    preparedstatement.setObject(k + 1, array1.at(k), 12);
                }

                preparedstatement.executeUpdate();
                continue;
            }
            catch(java.sql.SQLException sqlexception1)
            {
                sqlexception = sqlexception1;
                String s = "";
                for(int l = 0; l < j; l++)
                {
                    if(l != 0)
                    {
                        s = s + ",";
                    }
                    s = s + array1.at(l);
                }

                COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: exec, " + sqlexception1 + ", record=" + s);
                clearParameters(preparedstatement);
            }
        }
        connection.commit();
        if(debug)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger commit complete, " + i + " records");
        }
        if(sqlexception != null)
        {
            throw sqlexception;
        } else
        {
            return;
        }
    }

    void flushsafe(java.sql.PreparedStatement preparedstatement, jgl.Array array)
    {
        try
        {
            flush(preparedstatement, array);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error: " + exception);
            COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: " + exception);
        }
    }

    public void close()
    {
        flush();
        closeConnection();
    }

    public void flush()
    {
        flushsafe(logStatementCache, logCache);
        flushsafe(linkStatementCache, linkCache);
        for(Enumeration enumeration = customStatements.keys(); enumeration.hasMoreElements();)
        {
            String s = (String)enumeration.nextElement();
            java.sql.PreparedStatement preparedstatement = (java.sql.PreparedStatement)customStatements.get(s);
            jgl.Array array = (jgl.Array)customCaches.get(s);
            try
            {
                flushsafe(preparedstatement, array);
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger, flushed " + s);
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error, closing " + s + ", " + exception);
                COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: closing " + s + ", " + exception);
            }
        }

    }

    void closeConnection()
    {
        COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, closing database connection");
        if(logStatementCache != null)
        {
            try
            {
                logStatementCache.close();
                logStatementCache = null;
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error: closing statement, " + exception);
                COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: closing statement, " + exception);
            }
        }
        if(linkStatementCache != null)
        {
            try
            {
                linkStatementCache.close();
                linkStatementCache = null;
            }
            catch(java.lang.Exception exception1)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error: closing link statement, " + exception1);
                COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: closing link statement, " + exception1);
            }
        }
        for(Enumeration enumeration = customStatements.keys(); enumeration.hasMoreElements();)
        {
            String s = (String)enumeration.nextElement();
            java.sql.PreparedStatement preparedstatement = (java.sql.PreparedStatement)customStatements.get(s);
            try
            {
                preparedstatement.close();
                customStatements.remove(s);
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger, closed " + s);
            }
            catch(java.lang.Exception exception3)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error, closing " + s + ", " + exception3);
                COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: closing " + s + ", " + exception3);
            }
        }

        if(connection != null)
        {
            try
            {
                connection.close();
                connection = null;
            }
            catch(java.lang.Exception exception2)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error: closing, " + exception2);
                COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error: closing, " + exception2);
            }
        }
        COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, closed");
    }

    java.sql.PreparedStatement getLinkStatement()
        throws java.lang.Exception
    {
        if(linkStatementCache == null)
        {
            if(reconnectTime != 0L && java.lang.System.currentTimeMillis() < reconnectTime)
            {
                return null;
            }
            jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcURL" + logName);
            if(s.length() == 0)
            {
                return null;
            }
            setupConnection(hashmap, s);
            String s1 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcInsertLink");
            if(s1.length() == 0)
            {
                s1 = "INSERT INTO SiteViewLinks VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            }
            String s2 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcCreateLink");
            if(s2.length() == 0)
            {
                s2 = "CREATE TABLE SiteViewLinks (date VARCHAR(255), serverName VARCHAR(255), class VARCHAR(255), sample VARCHAR(255), groupName VARCHAR(255), monitorName VARCHAR(255), monitorID VARCHAR(255), url VARCHAR(255) NULL, status VARCHAR(255) NULL, contentType VARCHAR(255) NULL, referenceCount VARCHAR(255) NULL, isExternal VARCHAR(255) NULL,duration VARCHAR(255) NULL,contentSize VARCHAR(255) NULL,source VARCHAR(255) NULL)";
            }
            linkServer = COM.dragonflow.SiteView.SiteViewGroup.getServerID(hashmap);
            java.sql.Statement statement = null;
            try
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, checking link table");
                statement = connection.createStatement();
                statement.executeUpdate(s2);
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, created link table");
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, link table exists, " + exception);
            }
            finally
            {
                try
                {
                    if(statement != null)
                    {
                        statement.close();
                        connection.commit();
                    }
                }
                catch(java.lang.Exception exception2) { }
            }
            linkVars = countVars(s1);
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, prepare insert, " + linkVars + ", " + s1);
            if(s1.startsWith("call"))
            {
                linkStatementCache = connection.prepareCall("{" + s1 + "}");
            } else
            {
                linkStatementCache = connection.prepareStatement(s1);
            }
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, link connected");
        }
        return linkStatementCache;
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param date
     * @param propertiedobject
     * @param as
     */
    public synchronized void logLink(java.util.Date date, COM.dragonflow.Properties.PropertiedObject propertiedobject, String as[])
    {
        jgl.Array array;
        java.sql.PreparedStatement preparedstatement;
        array = new Array();
        
        try {
        preparedstatement = getLinkStatement();
        if(preparedstatement == null)
        {
            return;
        }
            int i = 1;
            if(i <= linkVars)
            {
                array.add(COM.dragonflow.Log.JdbcLogger.jdbcDateFormat(date));
                i++;
            }
            if(i <= linkVars)
            {
                array.add(linkServer);
                i++;
            }
            if(i <= linkVars)
            {
                array.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pClass));
                i++;
            }
            if(i <= linkVars)
            {
                array.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
                i++;
            }
            if(i <= linkVars)
            {
                array.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pOwnerID));
                i++;
            }
            if(i <= linkVars)
            {
                array.add(getMonitorName(propertiedobject));
                i++;
            }
            if(i <= linkVars)
            {
                array.add(propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID));
                i++;
            }
            for(int j = 0; j < as.length; j++)
            {
                String s = as[j];
                if(s.length() > maxStringLength)
                {
                    s = s.substring(0, maxStringLength);
                }
                if(i <= linkVars)
                {
                    array.add(s);
                    i++;
                }
            }

            for(; i <= linkVars; i++)
            {
                if(useNulls)
                {
                    array.add(null);
                } else
                {
                    array.add("");
                }
            }

            execute(preparedstatement, linkCache, array);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc link logger error: " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            COM.dragonflow.Log.LogManager.log("Error", "jdbc link logger error: " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            reconnectTime = (long)(reconnectSeconds * 1000) + java.lang.System.currentTimeMillis();
            closeConnection();
        }
        return;
    }

    jgl.Array getCustomCache(String s)
    {
        jgl.Array array = (jgl.Array)customCaches.get(s);
        if(array == null)
        {
            array = new Array();
            customCaches.put(s, array);
        }
        return array;
    }

    public java.sql.PreparedStatement getCustomStatement(String s, String s1, String s2, String s3, String s4, String s5)
        throws java.lang.Exception
    {
        java.lang.Object obj = (java.sql.PreparedStatement)customStatements.get(s);
        if(obj == null)
        {
            if(reconnectTime != 0L && java.lang.System.currentTimeMillis() < reconnectTime)
            {
                return null;
            }
            jgl.HashMap hashmap = COM.dragonflow.SiteView.MasterConfig.getMasterConfig();
            String s6 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcURL" + logName);
            if(s6.length() == 0)
            {
                return null;
            }
            setupConnection(hashmap, s6);
            String s7 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcInsert" + s);
            if(s7.length() == 0)
            {
                s7 = s5;
            }
            String s8 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcDefine" + s);
            if(s8.length() == 0)
            {
                s8 = s4;
            }
            String s9 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcCreate" + s);
            if(s9.length() == 0)
            {
                s9 = s1;
            }
            String s10 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcCreate2" + s);
            if(s10.length() == 0)
            {
                s10 = s2;
            }
            String s11 = COM.dragonflow.Utils.TextUtils.getValue(hashmap, "_logJdbcCreate3" + s);
            if(s11.length() == 0)
            {
                s11 = s3;
            }
            java.sql.Statement statement = null;
            try
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, checking " + s + " table");
                statement = connection.createStatement();
                statement.executeUpdate(s9);
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, created " + s + " table, " + s9);
            }
            catch(java.lang.Exception exception)
            {
                COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, " + s + " table exists, " + exception);
            }
            finally
            {
                try
                {
                    if(statement != null)
                    {
                        statement.close();
                        connection.commit();
                    }
                }
                catch(java.lang.Exception exception5) { }
            }
            if(s10 != null && s10.length() > 0)
            {
                java.sql.Statement statement1 = null;
                try
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, checking " + s + " table2");
                    statement1 = connection.createStatement();
                    statement1.executeUpdate(s10);
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, created2 " + s + ", " + s10);
                }
                catch(java.lang.Exception exception2)
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, " + s + " table2 exists,  " + exception2);
                }
                finally
                {
                    try
                    {
                        if(statement1 != null)
                        {
                            statement1.close();
                            connection.commit();
                        }
                    }
                    catch(java.lang.Exception exception7) { }
                }
            }
            if(s11 != null && s11.length() > 0)
            {
                java.sql.Statement statement2 = null;
                try
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, checking " + s + " table3");
                    statement2 = connection.createStatement();
                    statement2.executeUpdate(s11);
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, created3 " + s + ", " + s11);
                }
                catch(java.lang.Exception exception3)
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, " + s + " table3 exists,  " + exception3);
                }
                finally
                {
                    try
                    {
                        if(statement2 != null)
                        {
                            statement2.close();
                            connection.commit();
                        }
                    }
                    catch(java.lang.Exception exception9) { }
                }
            }
            if(s8.length() > 0)
            {
                java.sql.Statement statement3 = null;
                try
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, defining " + s);
                    statement3 = connection.createStatement();
                    statement3.executeUpdate(s8);
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, defined " + s + ", " + s8);
                }
                catch(java.lang.Exception exception4)
                {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, not defined, " + s + ", " + exception4);
                }
                finally
                {
                    try
                    {
                        if(statement3 != null)
                        {
                            statement3.close();
                            connection.commit();
                        }
                    }
                    catch(java.lang.Exception exception11) { }
                }
            }
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, prepare " + s + " insert, " + s7);
            if(s7.startsWith("call"))
            {
                obj = connection.prepareCall("{" + s7 + "}");
            } else
            {
                obj = connection.prepareStatement(s7);
            }
            customStatements.put(s, obj);
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc log, " + s + " connected");
        }
        return ((java.sql.PreparedStatement) (obj));
    }

    public synchronized void logCustom(COM.dragonflow.Properties.PropertiedObject propertiedobject, jgl.Array array, String s, String s1, String s2, String s3)
    {
        logCustom(propertiedobject, array, s, s1, null, s2, s3);
    }

    public synchronized void logCustom(COM.dragonflow.Properties.PropertiedObject propertiedobject, jgl.Array array, String s, String s1, String s2, String s3, String s4)
    {
        logCustom(propertiedobject, array, s, s1, s2, null, s3, s4);
    }

    /**
     * CAUTION: Decompiled by hand.
     * 
     * @param propertiedobject
     * @param array
     * @param s
     * @param s1
     * @param s2
     * @param s3
     * @param s4
     * @param s5
     */
    public synchronized void logCustom(COM.dragonflow.Properties.PropertiedObject propertiedobject, jgl.Array array, String s, String s1, String s2, String s3, String s4, 
            String s5)
    {
        java.sql.PreparedStatement preparedstatement;
        
        try {
        preparedstatement = getCustomStatement(s, s1, s2, s3, s4, s5);
        if(preparedstatement == null)
        {
            return;
        }
            jgl.Array array1 = getCustomCache(s);
            execute(preparedstatement, array1, array);
        }
        catch(java.lang.Exception exception)
        {
            COM.dragonflow.Log.LogManager.log("RunMonitor", "jdbc logger error, " + s + ", " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            COM.dragonflow.Log.LogManager.log("Error", "jdbc logger error, " + s + ", " + exception + ", " + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pGroupID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pID) + "/" + propertiedobject.getProperty(COM.dragonflow.SiteView.AtomicMonitor.pSample));
            reconnectTime = (long)(reconnectSeconds * 1000) + java.lang.System.currentTimeMillis();
            closeConnection();
        }
        return;
    }

}
