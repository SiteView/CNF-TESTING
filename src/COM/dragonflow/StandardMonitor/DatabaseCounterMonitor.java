/*
 * 
 * Created on 2005-3-7 0:54:55
 *
 * DatabaseCounterMonitor.java
 *
 * History:
 *
 */
package COM.dragonflow.StandardMonitor;

/**
 * Comment for <code>DatabaseCounterMonitor</code>
 * 
 * @author 
 * @version 0.0
 *
 *
 */
import COM.dragonflow.HTTP.HTTPRequest;
import COM.dragonflow.Log.LogManager;
import COM.dragonflow.Properties.BooleanProperty;
import COM.dragonflow.Properties.NumericProperty;
import COM.dragonflow.Properties.StringProperty;
import COM.dragonflow.SiteView.BrowsableBase;
import COM.dragonflow.SiteView.MasterConfig;
import COM.dragonflow.SiteView.Rule;
//import COM.dragonflow.SiteView.TopazInfo;
import COM.dragonflow.Utils.LUtils;
import COM.dragonflow.Utils.TextUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import jgl.Array;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DatabaseCounterMonitor extends BrowsableBase
{

 static StringProperty pServerName;
 static StringProperty pUser;
 static StringProperty pPassword;
 static StringProperty pConnectTimeout;
 static StringProperty pQueryTimeout;
 static StringProperty pDriver;
 static StringProperty pQuery;
 static StringProperty pCounters;
 static StringProperty pDBMachineName;
 static StringProperty pDivisorQuery;
 static BooleanProperty pNoDivisor;
 static BooleanProperty pNoCumulativeCounting;
 static StringProperty plastMeasurementTreeMap;
 static BooleanProperty pTool;
 static BooleanProperty pInitialSolutionDeployment;
 static int nMaxCounters;
 private Boolean cachingConnection;
 private Connection connection;
 Statement statement;
 boolean driverLoaded;
 private static final String COUNTER_SEPERATOR = "|||";
 private static final String COUNTER_ID_ERROR = "MALFORMED_COUNTER_ID";
 private static final String RUN_TIME_MILLIS = "SITEVIEW_RUN_TIME_MILLIS_KEY";
 private static final String DIVISOR_RESULT = "SITEVIEW_DIVISOR_QUERY_RESULT";

 public DatabaseCounterMonitor()
 {
     cachingConnection = null;
     connection = null;
     statement = null;
     driverLoaded = false;
 }

 public Array getConnectionProperties()
 {
     Array array = new Array();
     array.add(pServerName);
     array.add(pQuery);
     array.add(pUser);
     array.add(pPassword);
     array.add(pDriver);
     array.add(pConnectTimeout);
     array.add(pQueryTimeout);
     return array;
 }

 protected boolean update()
 {
     if(!getPropertyAsBoolean(pDisabled) && getPropertyAsBoolean(pInitialSolutionDeployment))
     {
         setProperty(pInitialSolutionDeployment, "false");
     }
     if(cachingConnection == null)
     {
         if(getSetting("_databaseMonitorCacheConnections").equalsIgnoreCase("true"))
         {
             cachingConnection = new Boolean(true);
         } else
         {
             cachingConnection = new Boolean(false);
         }
     }
     boolean flag = true;
     StringBuffer stringbuffer = new StringBuffer();
     try
     {
         int i;
         for(i = 0; i < nMaxCounters && getProperty(PROPERTY_NAME_COUNTER_ID + (i + 1)).length() != 0; i++) { }
         String as[] = new String[i];
         TreeMap treemap = null;
         TreeMap treemap1 = (TreeMap)getPropertyAsObject(plastMeasurementTreeMap);
         String s = getProperty(pQuery);
         if(treemap1 == null && !getPropertyAsBoolean(pNoCumulativeCounting))
         {
             treemap1 = getResult(stringbuffer, s);
             try
             {
                 Thread.sleep(2000L);
             }
             catch(InterruptedException interruptedexception) { }
         }
         treemap = getResult(stringbuffer, s);
         double d = 0.0D;
         if(getProperty(pDivisorQuery).length() == 0)
         {
             d = (float)(getResultTimeStamp(treemap) - getResultTimeStamp(treemap1)) / 1000F;
         } else
         {
             Double double1 = (Double)treemap.get("SITEVIEW_DIVISOR_QUERY_RESULT");
             Double double2 = treemap1 != null ? (Double)treemap1.get("SITEVIEW_DIVISOR_QUERY_RESULT") : new Double(0.0D);
             if(treemap != null && treemap1 != null && double1 != null && double2 != null)
             {
                 d = double1.longValue() - double2.longValue();
             } else
             {
                 d = 1.7976931348623157E+308D;
             }
         }
         boolean flag1 = getProperty(pNoDivisor).length() == 0;
         boolean flag2 = getProperty(pNoCumulativeCounting).length() == 0;
         for(int j = 0; j < i; j++)
         {
             String s1 = "n/a";
             String s3 = getProperty(PROPERTY_NAME_COUNTER_ID + (j + 1));
             String s4 = getCounterValue(treemap, s3);
             String s5 = getCounterValue(treemap1, s3);
             if(!flag1 && !flag2)
             {
                 s1 = "" + s4;
             } else
             if(s4 != null && s5 != null)
             {
                 try
                 {
                     double d1 = Double.parseDouble(s4);
                     double d2 = Double.parseDouble(s5);
                     double d3 = d1;
                     if(flag2)
                     {
                         d3 -= d2;
                     }
                     if(flag1)
                     {
                         if(d != 0.0D)
                         {
                             d3 /= d;
                         } else
                         {
                             d3 = 0.0D;
                         }
                     }
                     s1 = TextUtils.floatToString((float)d3, 4);
                 }
                 catch(NumberFormatException numberformatexception)
                 {
                     LogManager.log("Error", "DatabaseCounterMonitor: Unable to parse values: " + numberformatexception.getMessage());
                     s1 = "n/a";
                 }
             } else
             if(s4 == null)
             {
                 s1 = "n/a";
                 LogManager.log("Error", "DatabaseCounterMonitor: Unable to retrieve counter " + s3 + " from database.  Possible counter id format error. ");
             }
             as[j] = s1;
         }

         try
         {
             setPropertyWithObject(plastMeasurementTreeMap, treemap);
         }
         catch(IOException ioexception)
         {
             LogManager.log("Error", "Database Counter Monitor: Unable to save previous results.");
         }
         if(stillActive())
         {
             synchronized(this)
             {
                 String s2 = "";
                 if(stringbuffer.length() <= 0)
                 {
                     int k = 0;
                     TreeSet treeset = new TreeSet();
                     for(int i1 = 0; i1 < as.length; i1++)
                     {
                         String s6 = getProperty(PROPERTY_NAME_COUNTER_NAME + (i1 + 1));
                         if(as[i1] != null && !as[i1].equals("n/a"))
                         {
                             setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1), as[i1]);
                         } else
                         {
                             setProperty(PROPERTY_NAME_COUNTER_VALUE + (i1 + 1), "n/a");
                             k++;
                         }
                         treeset.add(s6 + " = " + as[i1]);
                         setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), k);
                     }

                     StringBuffer stringbuffer1 = new StringBuffer();
                     Iterator iterator = treeset.iterator();
                     do
                     {
                         if(!iterator.hasNext())
                         {
                             break;
                         }
                         String s7 = (String)iterator.next();
                         stringbuffer1.append(s7);
                         if(iterator.hasNext())
                         {
                             stringbuffer1.append(",");
                         }
                     } while(true);
                     s2 = stringbuffer1.toString();
                 } else
                 {
                     for(int l = 0; l < nMaxCounters; l++)
                     {
                         setProperty(PROPERTY_NAME_COUNTER_VALUE + (l + 1), "n/a");
                     }

                     setProperty(getPropertyObject(PROPERTY_NAME_COUNTERS_IN_ERROR), nMaxCounters);
                     s2 = stringbuffer.toString();
                     setProperty(pNoData, "n/a");
                     flag = false;
                 }
                 setProperty(pStateString, s2);
             }
         }
     }
     finally
     {
         try
         {
             if(cachingConnection == null || !cachingConnection.booleanValue())
             {
                 closeConnection();
             }
         }
         catch(SQLException sqlexception)
         {
             LogManager.log("Error", "SQLException encountered while closing JDBC connection in DatabaseCounterMonitor: " + sqlexception);
         }
     }
     return flag;
 }

 public String getTestURL()
 {
//     String s = "/SiteView/cgi/go.exe/SiteView?page=dbConnection&connectionURL=" + getProperty(pServerName) + "&driver=" + getProperty(pDriver) + "&username=" + getProperty(pUser) + "&password=" + getProperty(pPassword) + "&maxcol=100&maxrow=100" + "&query=" + URLEncoder.encode(getProperty(pQuery));
	 String s = "";
     return s;
 }

 private Double getDivisor()
     throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
 {
     String s = getProperty(pDivisorQuery);
     ResultSet resultset = null;
     Double double1;
     try
     {
         resultset = runQuery(s);
         resultset.next();
         double1 = new Double(resultset.getDouble(1));
     }
     finally
     {
         if(resultset != null)
         {
             try
             {
                 resultset.close();
             }
             catch(SQLException sqlexception)
             {
                 LogManager.log("Error", "DatabaseCounterMonitor.getDivisor() encountered an SQLException when trying to close the JDBC connection: " + sqlexception);
             }
         }
     }
     return double1;
 }

 /**
  * CAUTION: Decompiled by hand.
  * 
  * @param stringbuffer
  * @param s
  * @return
  */
 public TreeMap getResult(StringBuffer stringbuffer, String s)
 {
     boolean flag = false;
     ResultSet resultset = null;
     TreeMap treemap = new TreeMap();
     
     try {
     resultset = runQuery(s);
     if(getProperty(pDivisorQuery).length() > 0)
     {
         treemap.put("SITEVIEW_DIVISOR_QUERY_RESULT", getDivisor());
     }
     treemap.put("SITEVIEW_RUN_TIME_MILLIS_KEY", System.currentTimeMillis() + "");
     if(resultset == null)
     {
         stringbuffer.append("no results from query");
         throw new Exception("no results from query");
     }
     ResultSetMetaData resultsetmetadata = resultset.getMetaData();
     int i = resultsetmetadata.getColumnCount();
     String[] as = new String[i - 1];
     for(int j = 0; j < as.length; j++)
     {
         as[j] = resultsetmetadata.getColumnLabel(j + 2);
     }

while(resultset.next())
     {
     try {
    String s1 = resultset.getString(1);
     HashMap hashmap = new HashMap();
     for(int k = 0; k < as.length; k++)
     {
         String s2 = as[k];
         hashmap.put(s2, resultset.getString(k + 2));
     }

     treemap.put(s1, hashmap);
     }
     catch (Exception exception) {
     stringbuffer.append(exception.getMessage());
     }
     }
     }
     catch (SQLException e) {
     flag = true;
     stringbuffer.append("Error from database driver: " + e.getMessage());
     for(; e != null; e = e.getNextException())
     {
         LogManager.log("Error", "DB error , " + getGroupPathID() + ": " + getProperty(pName) + ", " + e.getMessage() + ", " + e.getErrorCode() + ", " + e.getSQLState());
     }
     }
     catch (ClassNotFoundException e) {
     flag = true;
     stringbuffer.append("Driver not found: " + e.getMessage());
     LogManager.log("Error", "DB error, , " + getGroupPathID() + ": " + getProperty(pName) + ", " + e.getMessage() + ", " + e);
     }
     catch (Throwable e) {
     flag = true;
     stringbuffer.append(e.getMessage());
     LogManager.log("Error", "DB error, , " + getGroupPathID() + ": " + getProperty(pName) + ", " + e.getMessage() + ", " + e);
     }
     finally {
     if(resultset != null)
     {
         try
         {
             resultset.close();
         }
         catch(Exception exception2)
         {
             LogManager.log("Error", "DatabaseCounterMonitor (" + getGroupPathID() + ": " + getProperty(pName) + ") encountered an exception while closing a java.sql.ResultSet: " + exception2);
         }
     }
     if(statement != null)
     {
         try
         {
             statement.close();
         }
         catch(Exception exception3)
         {
             LogManager.log("Error", "DatabaseCounterMonitor (" + getGroupPathID() + ": " + getProperty(pName) + ") encountered an exception while closing database java.sql.Statement: " + exception3);
         }
     }
     if(connection != null && flag)
     {
     try {
         connection.close();
     connection = null;    
     }
     catch (Exception exception4) {
     LogManager.log("Error", "DatabaseCounterMonitor (" + getGroupPathID() + ": " + getProperty(pName) + ") encountered an exception while closing database java.sql.Connection: " + exception4);
     connection = null;
     }
     connection = null;
     }
     }

     return treemap;
 }

 private ResultSet runQuery(String s)
     throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
 {
     ResultSet resultset = null;
     String s1 = getProperty(pDriver);
     String s2 = getProperty(pServerName);
     String s3 = getProperty(pUser);
     String s4 = getProperty(pPassword);
     int i = StringProperty.toInteger(getProperty(pConnectTimeout));
     int j = StringProperty.toInteger(getProperty(pQueryTimeout));
     if(!driverLoaded)
     {
         LogManager.log("RunMonitor", "DB monitor, loading, driver=" + s1);
         Class.forName(s1).newInstance();
         driverLoaded = true;
     }
     if(i > 0)
     {
         DriverManager.setLoginTimeout(i);
     }
     if(connection == null || connection.isClosed())
     {
         connection = DriverManager.getConnection(s2, s3, s4);
         LogManager.log("RunMonitor", "DatabaseCounterMonitor connecting, database=" + s2 + ", " + s3);
     }
     statement = connection.createStatement();
     if(j > 0)
     {
         statement.setQueryTimeout(j);
     }
     resultset = statement.executeQuery(s);
     return resultset;
 }

 /**
  * CAUTION: Decompiled by hand.
  */
 public String getBrowseData(StringBuffer stringbuffer)
 {
     try {
     TreeMap treemap = getResult(stringbuffer, getProperty(pQuery));
     String s = buildXml(treemap);
     String s1 = s;
     try
     {
         closeConnection();
     }
     catch(SQLException sqlexception)
     {
         LogManager.log("Error", "DatabaseCounterMonitor.getBrowseData() encountered an SQLException while trying to close the JDBC connection: " + sqlexception);
     }
     return s1;
     }
     catch (RuntimeException exception) {
     try
     {
         closeConnection();
     }
     catch(SQLException sqlexception1)
     {
         LogManager.log("Error", "DatabaseCounterMonitor.getBrowseData() encountered an SQLException while trying to close the JDBC connection: " + sqlexception1);
     }
     throw exception;
     }
 }

 private String getCounterValue(TreeMap treemap, String s)
 {
     if(treemap != null)
     {
         HashMap hashmap = (HashMap)treemap.get(getTreeKey(s));
         if(hashmap != null)
         {
             return (String)hashmap.get(getCounterKey(s));
         }
     }
     return null;
 }

 private long getResultTimeStamp(TreeMap treemap)
 {
     if(treemap != null)
     {
         String s = (String)treemap.get("SITEVIEW_RUN_TIME_MILLIS_KEY");
         if(s != null)
         {
             return Long.parseLong(s);
         }
     }
     return 0L;
 }

 private String getTreeKey(String s)
 {
     int i = s.indexOf("|||");
     int j = s.indexOf(" ");
     if(i >= 0)
     {
         return s.substring(j + 1, i);
     } else
     {
         return "MALFORMED_COUNTER_ID";
     }
 }

 private String getCounterKey(String s)
 {
     int i = s.indexOf("|||");
     if(i >= 0)
     {
         return s.substring(i + "|||".length());
     } else
     {
         return "MALFORMED_COUNTER_ID";
     }
 }

 private String buildXml(TreeMap treemap)
 {
     DocumentBuilder documentbuilder = null;
     try
     {
         documentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
     }
     catch(ParserConfigurationException parserconfigurationexception)
     {
         parserconfigurationexception.printStackTrace();
     }
     catch(FactoryConfigurationError factoryconfigurationerror)
     {
         factoryconfigurationerror.printStackTrace();
     }
     Document document = documentbuilder.newDocument();
     Element element = document.createElement("browse_data");
     document.appendChild(element);
     Set set = getObjectKeys(treemap);
     for(Iterator iterator = set.iterator(); iterator.hasNext();)
     {
         String s = (String)iterator.next();
         Element element1 = document.createElement("object");
         element.appendChild(element1);
         element1.setAttribute("name", s);
         HashMap hashmap = (HashMap)treemap.get(s);
         Set set1 = hashmap.keySet();
         Iterator iterator1 = set1.iterator();
         while(iterator1.hasNext()) 
         {
             String s1 = (String)iterator1.next();
             addCounter(document, element1, s1, s + "|||" + s1, (String)hashmap.get(s1));
         }
     }

     return printDocumentAsString(document);
 }

 private Set getObjectKeys(TreeMap treemap)
 {
     Set set = treemap.keySet();
     set.remove("SITEVIEW_RUN_TIME_MILLIS_KEY");
     set.remove("SITEVIEW_DIVISOR_QUERY_RESULT");
     return set;
 }

 private void addCounter(Document document, Element element, String s, String s1, String s2)
 {
     Element element1 = document.createElement("counter");
     element.appendChild(element1);
     element1.setAttribute("name", s);
     element1.setAttribute("id", s1);
     element1.setAttribute("desc", s2);
 }

 public String verify(StringProperty stringproperty, String s, HTTPRequest httprequest, jgl.HashMap hashmap)
 {
     if(stringproperty == pQuery)
     {
         if(s.length() == 0)
         {
             hashmap.put(stringproperty, "The SQL query cannot be blank.");
         } else
         if(s.endsWith(";"))
         {
             s = s.replaceAll(";$", "");
         }
         return s;
     }
     if(stringproperty.getName().equals(PROPERTY_NAME_BROWSABLE) && httprequest.getValue("page").equals("monitorSet"))
     {
         return s;
     } else
     {
         return super.verify(stringproperty, s, httprequest, hashmap);
     }
 }

 private String printDocumentAsString(Document document)
 {
     OutputFormat outputformat = new OutputFormat();
     outputformat.setIndent(3);
     outputformat.setOmitXMLDeclaration(true);
     outputformat.setOmitDocumentType(true);
     XMLSerializer xmlserializer = new XMLSerializer(outputformat);
     StringWriter stringwriter = new StringWriter();
     try
     {
         xmlserializer.setOutputCharStream(stringwriter);
         xmlserializer.serialize(document);
     }
     catch(Exception exception)
     {
         LogManager.log("Error", "XML serialization failed: " + exception.toString());
     }
     return stringwriter.toString();
 }

 private void closeConnection()
     throws SQLException
 {
     if(connection != null && !connection.isClosed())
     {
         connection.close();
     }
 }

 public String getHostname()
 {
     String s = getProperty(pDBMachineName);
     if(s != null && s.length() > 0)
     {
         return s;
     } else
     {
         return getProperty(pServerName);
     }
 }

 public int getMaxCounters()
 {
     return nMaxCounters;
 }

 public void setMaxCounters(int i)
 {
     nMaxCounters = i;
     jgl.HashMap hashmap = MasterConfig.getMasterConfig();
     hashmap.put("_browsableContentMaxCounters", (new Integer(i)).toString());
     MasterConfig.saveMasterConfig(hashmap);
 }

 public String whyDisabled()
 {
     if(getPropertyAsBoolean(pTool))
     {
         return "Solution Tool";
     }
     if(getPropertyAsBoolean(pDisabled) && getPropertyAsBoolean(pInitialSolutionDeployment))
     {
         return "Deployed by Oracle Solution--please select counters and enable this monitor.";
     } else
     {
         return super.whyDisabled();
     }
 }

 protected void printTableEditEntry(PrintWriter printwriter, HTTPRequest httprequest)
 {
     if(getPropertyAsBoolean(pTool))
     {
         printwriter.print("<TD ALIGN=CENTER>&nbsp;</TD>");
     } else
     {
         super.printTableEditEntry(printwriter, httprequest);
     }
 }

 protected void printTableNameEntry(PrintWriter printwriter, HTTPRequest httprequest, String s)
 {
     if(getPropertyAsBoolean(pTool))
     {
         StringBuffer stringbuffer = (new StringBuffer("<TD ALIGN=LEFT><A HREF=")).append(getTestURL()).append(">").append(getProperty(pName)).append("</A></TD>");
         printwriter.print(stringbuffer.toString());
     } else
     {
         super.printTableNameEntry(printwriter, httprequest, s);
     }
 }

 protected void printTableAlertEntry(PrintWriter printwriter, HTTPRequest httprequest, Array array)
 {
     if(getPropertyAsBoolean(pTool))
     {
         printwriter.print("<td align=\"center\" bgcolor=\"#000000\">&nbsp;</td>");
     } else
     {
         super.printTableAlertEntry(printwriter, httprequest, array);
     }
 }

 protected String printTableReportEntry(boolean flag, HTTPRequest httprequest)
 {
     if(getPropertyAsBoolean(pTool))
     {
         return "<td align=\"center\" bgcolor=\"#000000\">&nbsp;</td>";
     } else
     {
         return super.printTableReportEntry(flag, httprequest);
     }
 }

 protected void printTableCategoryEntry(PrintWriter printwriter, HTTPRequest httprequest, boolean flag, boolean flag1, String s, String s1)
 {
     if(getPropertyAsBoolean(pTool))
     {
         printwriter.print("<TD ALIGN=CENTER BGCOLOR=#000000><A HREF=" + getTestURL() + ">" + "<IMG BORDER=0 SRC=/SiteView/htdocs/artwork/disabled.gif ALT=\"Tool\"></A></TD>");
     } else
     {
         super.printTableCategoryEntry(printwriter, httprequest, flag, flag1, s, s1);
     }
 }

 static 
 {
     nMaxCounters = 30;
     Array array = new Array();
     jgl.HashMap hashmap = MasterConfig.getMasterConfig();
     nMaxCounters = TextUtils.toInt(TextUtils.getValue(hashmap, "_browsableContentMaxCounters"));
     if(nMaxCounters == 0)
     {
         nMaxCounters = 30;
     }
     StringProperty astringproperty[] = BrowsableBase.staticInitializer(nMaxCounters, true);
     pServerName = new StringProperty("_server", "", "host name");
     pServerName.setDisplayText("Database Connection URL", "JDBC URL to the database connection.<br> For example: jdbc:oracle:thin:@192.168.0.50:1521:ORCL.");
     pServerName.setParameterOptions(false, true, 1, false);
     array.add(pServerName);
     pQuery = new StringProperty("_query", "", "query");
     pQuery.setDisplayText("Query", "SQL query to be performed on the database.  Needs to return \"Counter Name , Value1, Value2...\" format.  For example: SELECT name,gets,misses FROM V$LATCH;");
     pQuery.setParameterOptions(false, 2, false);
     array.add(pQuery);
     pUser = new StringProperty("_user", "", "user name");
     pUser.setDisplayText("Database User Name", "User name used to connect to the database.");
     pUser.setParameterOptions(false, true, 2, false);
     array.add(pUser);
     pPassword = new StringProperty("_password", "", "password");
     pPassword.setDisplayText("Database Password", "Password used to connect to the database.");
     pPassword.setParameterOptions(false, true, 3, false);
     pPassword.isPassword = true;
     array.add(pPassword);
     pDriver = new StringProperty("_driver", "oracle.jdbc.driver.OracleDriver", "driver");
     pDriver.setDisplayText("Database Driver", "Driver used to connect to the database.");
     pDriver.setParameterOptions(false, true, 4, false);
     array.add(pDriver);
     pConnectTimeout = new NumericProperty("_connectTimeout", "600", "seconds");
     pConnectTimeout.setDisplayText("Connection Timeout", "Time in seconds to wait for a database connection.");
     pConnectTimeout.setParameterOptions(false, true, 6, false);
     array.add(pConnectTimeout);
     pQueryTimeout = new NumericProperty("_queryTimeout", "600", "seconds");
     pQueryTimeout.setDisplayText("Query Timeout", "Time in seconds to wait for a database query to complete.");
     pQueryTimeout.setParameterOptions(false, true, 7, false);
     array.add(pQueryTimeout);
//     pDBMachineName = new StringProperty("_targetMachineName");
//     pDBMachineName.setDisplayText("DB Machine Name", "Identifier for target DB server, used for reporting to " + TopazInfo.getTopazName() + " (optional).");
//     pDBMachineName.setParameterOptions(true, 10, true);
//     array.add(pDBMachineName);
     pDivisorQuery = new StringProperty("_divisorQuery");
     pDivisorQuery.setDisplayText("Divisor Query", "Optional query to divide counters by.  The default is to divide by the time elapsed between monitor runs.");
     pDivisorQuery.setParameterOptions(true, 9, true);
     array.add(pDivisorQuery);
     pNoCumulativeCounting = new BooleanProperty("_noCumulativeCounting", "");
     pNoCumulativeCounting.setDisplayText("No Cumulative Counters", "When selected, none of the counters will be treated as cumulative counters. The value will be determined directly from the query of the database.");
     pNoCumulativeCounting.setParameterOptions(true, 11, true);
     array.add(pNoCumulativeCounting);
     pNoDivisor = new BooleanProperty("_noDivideCounters", "");
     pNoDivisor.setDisplayText("No Divide Counters", "When selected, none of the counters will be divided by the divisor.");
     pNoDivisor.setParameterOptions(true, 12, true);
     array.add(pNoDivisor);
     plastMeasurementTreeMap = new StringProperty("lastMeasureMentTreeMap");
     array.add(plastMeasurementTreeMap);
     pTool = new BooleanProperty("_tool", "");
     pTool.setParameterOptions(false, 13, true);
     array.add(pTool);
     pInitialSolutionDeployment = new BooleanProperty("_initialSolutionDeployment", "");
     pInitialSolutionDeployment.setParameterOptions(false, 13, true);
     array.add(pInitialSolutionDeployment);
     StringProperty astringproperty1[] = new StringProperty[array.size()];
     for(int i = 0; i < array.size(); i++)
     {
         astringproperty1[i] = (StringProperty)array.at(i);
     }

     String s = (COM.dragonflow.StandardMonitor.DatabaseCounterMonitor.class).getName();
     StringProperty astringproperty2[] = new StringProperty[astringproperty1.length + astringproperty.length];
     System.arraycopy(astringproperty1, 0, astringproperty2, 0, astringproperty1.length);
     System.arraycopy(astringproperty, 0, astringproperty2, astringproperty1.length, astringproperty.length);
     addProperties(s, astringproperty2);
     addClassElement(s, Rule.stringToClassifier("countersInError > 0\terror", true));
     addClassElement(s, Rule.stringToClassifier("always\tgood"));
     setClassProperty(s, "description", "Tests a database by connecting to it and performing a query using JDBC. The counters retrieved can be calculated as the cumulative difference between monitor runs and optionally as a rate.");
     setClassProperty(s, "help", "databaseCounterMon.htm");
     setClassProperty(s, "title", "Database Counter");
     setClassProperty(s, "class", "DatabaseCounterMonitor");
     setClassProperty(s, "target", "_server");
     setClassProperty(s, "loadable", "true");
     setClassProperty(s, "classType", "application");
     setClassProperty(s, "topazName", "DatabaseCounter");
     setClassProperty(s, "topazType", "Database Server");
     if(!LUtils.isValidSSforXLicense("OracleSolution"))
     {
         setClassProperty(s, "loadable", "false");
     }
     setClassProperty(s, "addable", "false");
 }
}