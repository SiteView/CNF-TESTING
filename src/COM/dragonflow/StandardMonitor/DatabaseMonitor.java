/*
 * 
 * Created on 2005-3-7 0:58:21
 *
 * DatabaseMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>DatabaseMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.*;

public class DatabaseMonitor extends DBMonitorBase
{

 private static StringProperty pDatabase;
 private static StringProperty pQuery;
 private static StringProperty pUser;
 private static StringProperty pPassword;
 private static StringProperty pConnectTimeout;
 private static StringProperty pQueryTimeout;
 private static StringProperty pDriver;
 private static StringProperty pContentMatch;
 private static StringProperty pQueryFilePath;
 private static StringProperty pColumnLabels;
 private static StringProperty pStatus;
 private static StringProperty pResultContents;
 private static StringProperty pRows;
 private static StringProperty pRoundTripTime;
 private static StringProperty pMatchValue;
 public static StringProperty pColumn1;
 public static StringProperty pColumn2;
 static StringProperty pDBMachineName;

 public DatabaseMonitor()
 {
 }

 protected StringProperty getDatabaseProperty()
 {
     return pDatabase;
 }

 protected StringProperty getQueryProperty()
 {
     return pQuery;
 }

 protected StringProperty getUserProperty()
 {
     return pUser;
 }

 protected StringProperty getPasswordProperty()
 {
     return pPassword;
 }

 protected StringProperty getConnectTimeoutProperty()
 {
     return pConnectTimeout;
 }

 protected StringProperty getQueryTimeoutProperty()
 {
     return pQueryTimeout;
 }

 protected StringProperty getDriverProperty()
 {
     return pDriver;
 }

 protected StringProperty getContentMatchProperty()
 {
     return pContentMatch;
 }

 protected StringProperty getQueryFilePathProperty()
 {
     return pQueryFilePath;
 }

 protected StringProperty getColumnLabelsProperty()
 {
     return pColumnLabels;
 }

 protected StringProperty getStatusProperty()
 {
     return pStatus;
 }

 protected StringProperty getResultContentsProperty()
 {
     return pResultContents;
 }

 protected StringProperty getRowsProperty()
 {
     return pRows;
 }

 protected StringProperty getRoundTripTimeProperty()
 {
     return pRoundTripTime;
 }

 protected StringProperty getMatchValueProperty()
 {
     return pMatchValue;
 }

 protected StringProperty getColumn1Property()
 {
     return pColumn1;
 }

 protected StringProperty getColumn2Property()
 {
     return pColumn2;
 }

 public String getHostname()
 {
     String s = getProperty(pDBMachineName);
     if(s != null && s.length() > 0)
     {
         return s;
     } else
     {
         return super.getHostname();
     }
 }

 static 
 {
     pDatabase = new StringProperty(getBasePropertyName(databaseName), getBasePropertyDefValue(databaseName), getBasePropertyLabel(databaseName));
     pDatabase.setDisplayText(getBasePropertyTitle(databaseName), getBasePropertyDesc(databaseName));
     pDatabase.setParameterOptions(true, 1, false);
     pQuery = new StringProperty(getBasePropertyName(queryName), getBasePropertyDefValue(queryName), getBasePropertyLabel(queryName));
     pQuery.setDisplayText(getBasePropertyTitle(queryName), getBasePropertyDesc(queryName));
     pQuery.setParameterOptions(true, 2, false);
     pDriver = new StringProperty(getBasePropertyName(driverName), getBasePropertyDefValue(driverName), getBasePropertyLabel(driverName));
     pDriver.setDisplayText(getBasePropertyTitle(driverName), getBasePropertyDesc(driverName));
     pDriver.setParameterOptions(true, 3, false);
     pContentMatch = new StringProperty(getBasePropertyName(contentMatchName));
     pContentMatch.setDisplayText(getBasePropertyTitle(contentMatchName), getBasePropertyDesc(contentMatchName));
     pContentMatch.setParameterOptions(true, 9, true);
     pUser = new StringProperty(getBasePropertyName(userName), getBasePropertyDefValue(userName), getBasePropertyLabel(userName));
     pUser.setDisplayText(getBasePropertyTitle(userName), getBasePropertyDesc(userName));
     pUser.setParameterOptions(true, 10, true);
     pPassword = new StringProperty(getBasePropertyName(passName), getBasePropertyDefValue(passName), getBasePropertyLabel(passName));
     pPassword.setDisplayText(getBasePropertyTitle(passName), getBasePropertyDesc(passName));
     pPassword.setParameterOptions(true, 11, true);
     pPassword.isPassword = true;
     pQueryFilePath = new StringProperty(getBasePropertyName(path2QueryFile), getBasePropertyDefValue(path2QueryFile));
     pQueryFilePath.setDisplayText(getBasePropertyTitle(path2QueryFile), getBasePropertyDesc(path2QueryFile));
     pQueryFilePath.setParameterOptions(true, 12, true);
     pConnectTimeout = new NumericProperty(getBasePropertyName(connectionTimeoutName), getBasePropertyDefValue(connectionTimeoutName), "seconds");
     pConnectTimeout.setDisplayText(getBasePropertyTitle(connectionTimeoutName), getBasePropertyDesc(connectionTimeoutName));
     pConnectTimeout.setParameterOptions(true, 13, true);
     pQueryTimeout = new NumericProperty(getBasePropertyName(queryTimeoutName), getBasePropertyDefValue(queryTimeoutName), "seconds");
     pQueryTimeout.setDisplayText(getBasePropertyTitle(queryTimeoutName), getBasePropertyDesc(queryTimeoutName));
     pQueryTimeout.setParameterOptions(true, 14, true);
     pColumnLabels = new StringProperty(getBasePropertyName(columnLabelNames), getBasePropertyDefValue(columnLabelNames));
     pColumnLabels.setDisplayText(getBasePropertyTitle(columnLabelNames), getBasePropertyDesc(columnLabelNames));
     pColumnLabels.setParameterOptions(true, 15, true);
     pDBMachineName = new StringProperty("_targetMachineName");
//     pDBMachineName.setDisplayText("DB Machine Name", "Identifier for target DB server, used for reporting to " + TopazInfo.getTopazName() + " (optional)");
     pDBMachineName.setParameterOptions(true, 16, true);
     pRoundTripTime = new NumericProperty(getBasePropertyName(roundTripTimeName), getBasePropertyDefValue(roundTripTimeName), "milliseconds");
     pRoundTripTime.setLabel(getBasePropertyLabel(roundTripTimeName));
     pRoundTripTime.setStateOptions(1);
     pResultContents = new StringProperty(getBasePropertyName(resultContentsName), getBasePropertyDefValue(resultContentsName));
     pStatus = new StringProperty(getBasePropertyName(statusName), getBasePropertyDefValue(statusName));
     pMatchValue = new StringProperty(getBasePropertyName(matchValueName));
     pMatchValue.setLabel(getBasePropertyLabel(matchValueName));
     pMatchValue.setIsThreshold(true);
     pColumn1 = new NumericProperty(getBasePropertyName(column1Name), getBasePropertyDefValue(column1Name));
     pColumn1.setDisplayText(getBasePropertyTitle(column1Name), getBasePropertyDesc(column1Name));
     pColumn1.setStateOptions(2);
     pColumn2 = new NumericProperty(getBasePropertyName(column2Name), getBasePropertyDefValue(column2Name));
     pColumn2.setDisplayText(getBasePropertyTitle(column2Name), getBasePropertyDesc(column2Name));
     pColumn2.setStateOptions(3);
     pRows = new NumericProperty(getBasePropertyName(rowsName), getBasePropertyDefValue(rowsName));
     pRows.setLabel(getBasePropertyLabel(rowsName));
     pRows.setStateOptions(4);
     StringProperty astringproperty[] = {
         pDatabase, pQuery, pContentMatch, pDriver, pUser, pPassword, pConnectTimeout, pQueryTimeout, pRoundTripTime, pResultContents, 
         pRows, pStatus, pMatchValue, pColumn1, pColumn2, pQueryFilePath, pColumnLabels, pDBMachineName
     };
     String s = (COM.dragonflow.StandardMonitor.DatabaseMonitor.class).getName();
     addProperties(s, astringproperty);
     addClassElement(s, Rule.stringToClassifier("status != 200\terror"));
     addClassElement(s, Rule.stringToClassifier("status == 200\tgood"));
     addClassElement(s, Rule.stringToClassifier("always\twarning", true));
     setClassProperty(s, "description", "Tests database availability and content by connecting to it and performing a query.");
     setClassProperty(s, "help", "DatabaseMon.htm");
     setClassProperty(s, "title", "Database Query");
     setClassProperty(s, "class", "DatabaseMonitor");
     setClassProperty(s, "target", "_database");
     setClassProperty(s, "loadable", "true");
     setClassProperty(s, "topazName", "SQL Query");
     setClassProperty(s, "toolName", "Database Connection");
     setClassProperty(s, "toolDescription", "Provides an interface to test a JDBC or ODBC connection to a database.");
     setClassProperty(s, "topazType", "Database Server");
 }
}
