/*
 * Created on 2005-3-10 22:16:20
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.StandardAction;

/**
 * Comment for <code></code>
 * 
 * @author
 * @version 0.0
 * 
 * 
 */

import COM.dragonflow.Properties.StringProperty;

public class DatabaseAlert extends COM.dragonflow.SiteView.Action {

    public static COM.dragonflow.Properties.StringProperty pDatabase;

    public static COM.dragonflow.Properties.StringProperty pStatement;

    public static COM.dragonflow.Properties.StringProperty pUsername;

    public static COM.dragonflow.Properties.StringProperty pPassword;

    public static COM.dragonflow.Properties.StringProperty pDriver;

    public static COM.dragonflow.Properties.StringProperty pDatabaseBackup;

    private static java.lang.Object databaseAlertLock = new Object();

    boolean driverLoaded;

    public void initializeFromArguments(jgl.Array array, jgl.HashMap hashmap) {
        switch (array.size()) {
        case 6: // '\006'
            String s = COM.dragonflow.Utils.TextUtils.storedValueToValue((String) array.at(5));
            setProperty(pDatabaseBackup, s);
        // fall through

        case 5: // '\005'
            String s1 = COM.dragonflow.Utils.TextUtils.storedValueToValue((String) array.at(4));
            setProperty(pDriver, s1);
        // fall through

        case 4: // '\004'
            String s2 = COM.dragonflow.Utils.TextUtils.storedValueToValue((String) array.at(3));
            setProperty(pPassword, s2);
        // fall through

        case 3: // '\003'
            String s3 = COM.dragonflow.Utils.TextUtils.storedValueToValue((String) array.at(2));
            setProperty(pUsername, s3);
        // fall through

        case 2: // '\002'
            String s4 = COM.dragonflow.Utils.TextUtils.storedValueToValue((String) array.at(1));
            setProperty(pStatement, s4);
        // fall through

        case 1: // '\001'
            String s5 = COM.dragonflow.Utils.TextUtils.storedValueToValue((String) array.at(0));
            setProperty(pDatabase, s5);
        // fall through

        default:
            return;
        }
    }

    public String getActionString() {
        String s = getProperty(pDatabase).length() <= 0 ? " " : getProperty(pDatabase);
        String s1 = getProperty(pStatement).length() <= 0 ? " " : getProperty(pStatement);
        String s2 = getProperty(pUsername).length() <= 0 ? " " : getProperty(pUsername);
        String s3 = getProperty(pPassword).length() <= 0 ? " " : getProperty(pPassword);
        String s4 = getProperty(pDriver).length() <= 0 ? " " : getProperty(pDriver);
        String s5 = getProperty(pDatabaseBackup).length() <= 0 ? " " : getProperty(pDatabaseBackup);
        s = COM.dragonflow.Utils.TextUtils.replaceString(s, "=", EQUALS_SUBTITUTE);
        s5 = COM.dragonflow.Utils.TextUtils.replaceString(s5, "=", EQUALS_SUBTITUTE);
        s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, "=", EQUALS_SUBTITUTE);
        String s6 = "DatabaseAlert";
        s6 = s6 + " " + COM.dragonflow.Utils.TextUtils.valueToStoredValue(s);
        s6 = s6 + " " + COM.dragonflow.Utils.TextUtils.valueToStoredValue(s1);
        s6 = s6 + " " + COM.dragonflow.Utils.TextUtils.valueToStoredValue(s2);
        s6 = s6 + " " + COM.dragonflow.Utils.TextUtils.valueToStoredValue(s3);
        s6 = s6 + " " + COM.dragonflow.Utils.TextUtils.valueToStoredValue(s4);
        s6 = s6 + " " + COM.dragonflow.Utils.TextUtils.valueToStoredValue(s5);
        return s6;
    }

    public String verify(COM.dragonflow.Properties.StringProperty stringproperty, String s, COM.dragonflow.HTTP.HTTPRequest httprequest, jgl.HashMap hashmap) {
        if (stringproperty == pDatabase) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            } else if (COM.dragonflow.Utils.TextUtils.hasSpaces(s)) {
                hashmap.put(stringproperty, "no spaces are allowed");
            }
            return s;
        }
        if (stringproperty == pStatement) {
            if (s.length() == 0) {
                hashmap.put(stringproperty, stringproperty.getLabel() + " missing");
            }
            s = s.trim();
            return s;
        } else {
            return super.verify(stringproperty, s, httprequest, hashmap);
        }
    }

    public String getActionDescription() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append((String) getClassProperty("label"));
        stringbuffer.append(" " + getProperty(pStatement));
        stringbuffer.append(" " + getProperty(pDatabase));
        return stringbuffer.toString();
    }

    public DatabaseAlert() {
        driverLoaded = false;
        runType = 3;
    }

    public boolean execute() {
        boolean flag = false;
        String s = "";
        maxRuns = getSettingAsLong("_databaseAlertAttempts", 4);
        attemptDelay = getSettingAsLong("_databaseAlertAttemptDelay", 60);
        if (attemptDelay < 1L) {
            attemptDelay = 1L;
        }
        attemptDelay *= 1000L;
        String s1 = "none";
        String s2 = "none";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        String s6 = "";
        switch (args.length) {
        case 6: // '\006'
            s6 = COM.dragonflow.Utils.TextUtils.storedValueToValue(args[5]);
            if (s6.equals(" ")) {
                s6 = "";
            }
        // fall through

        case 5: // '\005'
            s5 = COM.dragonflow.Utils.TextUtils.storedValueToValue(args[4]);
        // fall through

        case 4: // '\004'
            s4 = COM.dragonflow.Utils.TextUtils.storedValueToValue(args[3]);
            if (s4.equals(" ")) {
                s4 = "";
            }
        // fall through

        case 3: // '\003'
            s3 = COM.dragonflow.Utils.TextUtils.storedValueToValue(args[2]);
            if (s3.equals(" ")) {
                s3 = "";
            }
        // fall through

        case 2: // '\002'
            s2 = COM.dragonflow.Utils.TextUtils.storedValueToValue(args[1]);
        // fall through

        case 1: // '\001'
            s1 = COM.dragonflow.Utils.TextUtils.storedValueToValue(args[0]);
        // fall through

        default:
            s1 = COM.dragonflow.Utils.TextUtils.replaceString(s1, EQUALS_SUBTITUTE, "=");
            s2 = COM.dragonflow.Utils.TextUtils.replaceString(s2, EQUALS_SUBTITUTE, "=");
            s6 = COM.dragonflow.Utils.TextUtils.replaceString(s6, EQUALS_SUBTITUTE, "=");
            s2 = monitor.createFromTemplate(s2);
            break;
        }
        if (COM.dragonflow.Utils.TextUtils.startsWithIgnoreCase(s2, "call")) {
            s2 = "{" + s2 + "}";
        }
        String s7 = alertSend(s1, s2, s3, s4, s5);
        flag = s7.length() == 0;
        if (!flag && s6.length() > 0) {
            s7 = alertSend(s6, s2, s3, s4, s5);
            flag = s7.length() == 0;
        }
        String s8 = "Database alert sent";
        if (!flag) {
            String s9 = "(" + triggerCount + "/" + maxRuns + ")";
            if (triggerCount >= maxRuns) {
                s8 = "DATABASE ALERT NOT SENT " + s9;
            } else {
                s8 = "Database alert retry " + s9;
            }
        }
        if (!flag) {
            String s10 = getSetting("_autoEmail");
            if (s10.length() != 0) {
                String s11 = "There was a problem sending a " + COM.dragonflow.SiteView.Platform.productName + " database alert." + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + s
                        + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + COM.dragonflow.SiteView.Platform.FILE_NEWLINE + "output: " + s7 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE;
                COM.dragonflow.SiteView.SiteViewGroup siteviewgroup = COM.dragonflow.SiteView.SiteViewGroup.currentSiteView();
                siteviewgroup.simpleMail(s10, COM.dragonflow.SiteView.Platform.productName + " " + s8, s11);
            }
        }
        messageBuffer.append(s8 + ", " + s2 + ", " + s1);
        logAlert(baseAlertLogEntry(s8, s2 + ", " + s1, flag) + " alert-output: " + s7 + COM.dragonflow.SiteView.Platform.FILE_NEWLINE);
        return flag;
    }

    String alertSend(String s, String s1, String s2, String s3, String s4) {
        String s5 = "";
        synchronized (databaseAlertLock) {
            java.sql.Connection connection = null;
            java.sql.Statement statement = null;
            java.sql.ResultSet resultset = null;
            String s6 = "driver connect";
            try {
                if (!driverLoaded) {
                    COM.dragonflow.Log.LogManager.log("RunMonitor", "database alert, loading, driver=" + s4);
                    java.lang.Class.forName(s4).newInstance();
                    driverLoaded = true;
                }
                s6 = "connect";
                COM.dragonflow.Log.LogManager.log("RunMonitor", "database alert, connecting, database=" + s + ", " + s2);
                connection = java.sql.DriverManager.getConnection(s, s2, s3);
                statement = connection.createStatement();
                s6 = "execute";
                statement.executeUpdate(s1);
                java.sql.SQLWarning sqlwarning = connection.getWarnings();
                int i = 1;
                for (; sqlwarning != null; sqlwarning = sqlwarning.getNextWarning()) {
                    COM.dragonflow.Log.LogManager.log("Error", "Database warning " + i ++ + ", " + s1 + ", " + getProperty(pDatabase) + ", " + sqlwarning.getMessage() + ", " + sqlwarning.getErrorCode() + ", " + sqlwarning.getSQLState());
                }

                s6 = "closing";
            } catch (java.sql.SQLException sqlexception) {
                s5 = s6 + " error, " + sqlexception.getMessage();
                int j = 1;
                for (; sqlexception != null; sqlexception = sqlexception.getNextException()) {
                    COM.dragonflow.Log.LogManager.log("Error", "Database error " + j ++ + ", " + s6 + ", " + s1 + ", " + getProperty(pDatabase) + ", " + sqlexception.getMessage() + ", " + sqlexception.getErrorCode() + ", " + sqlexception.getSQLState());
                }

            } catch (java.lang.Throwable throwable) {
                s5 = s6 + " error, " + throwable.getMessage();
                COM.dragonflow.Log.LogManager.log("Error", "Database error, " + s6 + ", " + s1 + ", " + getProperty(pDatabase) + ", " + throwable.getMessage() + ", " + throwable);
            } finally {
                if (resultset != null) {
                    try {
                        resultset.close();
                    } catch (java.lang.Exception exception1) {
                        COM.dragonflow.Log.LogManager.log("Error", "Database close resultSet error, " + s6 + ", " + s1 + ", " + getProperty(pDatabase) + ", " + exception1);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (java.lang.Exception exception2) {
                        COM.dragonflow.Log.LogManager.log("Error", "Database close statement error, " + s6 + ", " + s1 + ", " + getProperty(pDatabase) + ", " + exception2);
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (java.lang.Exception exception3) {
                        COM.dragonflow.Log.LogManager.log("Error", "Database close connection error, " + s6 + ", " + s1 + ", " + getProperty(pDatabase) + ", " + exception3);
                    }
                }
            }
        }
        return s5;
    }

    public String toString() {
        return "database alert";
    }

    static {
        pDatabase = new StringProperty("_database", "", "database name");
        pDatabase.setDisplayText("Database Connection URL", "Enter the URL to the database connection (for example, if the ODBC connection is called test, the URL would be jdbc:odbc:test)");
        pDatabase.setParameterOptions(true, 1, false);
        pStatement = new StringProperty("_statement", "INSERT INTO SiteViewAlert VALUES('<time>', '<group>', '<name>', '<state>')", "statement");
        pStatement.setDisplayText("SQL Statement", "the SQL statement to call a stored procedure or insert a record");
        pStatement.setParameterOptions(true, 2, false);
        pUsername = new StringProperty("_user", "", "user name");
        pUsername.setDisplayText("Database User Name", "optional, user name used to connect to the database");
        pUsername.setParameterOptions(true, 2, true);
        pPassword = new StringProperty("_password", "", "password");
        pPassword.setDisplayText("Database Password", "optional,  password used to connect to the database");
        pPassword.setParameterOptions(true, 3, true);
        pPassword.isPassword = true;
        pDatabaseBackup = new StringProperty("_database", "", "database name");
        pDatabaseBackup.setDisplayText("Backup Database Connection URL",
                "optional, enter a URL to a backup database connection to use if the main database connection fails (for example, if the ODBC connection is called test2, the URL would be jdbc:odbc:test2)");
        pDatabaseBackup.setParameterOptions(true, 4, true);
        pDriver = new StringProperty("_driver", "sun.jdbc.odbc.JdbcOdbcDriver", "driver");
        pDriver.setDisplayText("Database Driver", "driver used to connect to the database");
        pDriver.setParameterOptions(true, 5, true);
        COM.dragonflow.Properties.StringProperty astringproperty[] = { pDatabase, pStatement, pUsername, pPassword, pDriver, pDatabaseBackup };
        COM.dragonflow.StandardAction.DatabaseAlert.addProperties("COM.dragonflow.StandardAction.DatabaseAlert", astringproperty);
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "description", "Send an alert to a database by calling a stored procedure or inserting a record");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "help", "AlertDatabase.htm");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "title", "Alert Database");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "name", "Database");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "class", "DatabaseAlert");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "label", "Database");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "prefs", "");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "loadable", "true");
        COM.dragonflow.StandardAction.DatabaseAlert.setClassProperty("COM.dragonflow.StandardAction.DatabaseAlert", "classType", "advanced");
    }
}
