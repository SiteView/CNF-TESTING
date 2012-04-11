/*
 * 
 * Created on 2005-3-9 22:12:36
 *
 * .java
 *
 * History:
 *
 */
package COM.dragonflow.Page;


// Referenced classes of package COM.dragonflow.Page:
// CGI

public class dbConnectionPage extends COM.dragonflow.Page.CGI
{

    public dbConnectionPage()
    {
    }

    public void printBody()
        throws java.lang.Exception
    {
        printBodyHeader("Database Connection Diagnostic Tool");
        printButtonBar("databaseTool.htm", "");
        boolean flag = true;
        String s = request.getValue("connectionURL");
        String s1 = request.getValue("driver");
        String s2 = request.getValue("query");
        String s3 = request.getValue("username");
        String s4 = request.getValue("password");
        String s5 = request.getValue("maxcol");
        String s6 = request.getValue("maxrow");
        String s7 = "";
        if(s5.length() == 0 || s5 == null)
        {
            s5 = "10";
        }
        if(s6.length() == 0 || s6 == null)
        {
            s6 = "10";
        }
        if(s.length() > 0 && s1.length() > 0)
        {
            java.sql.Connection connection = null;
            java.sql.Statement statement = null;
            java.sql.ResultSet resultset = null;
            try
            {
                s7 = s7 + "<p>Step 1: <br>Attempting to Locate Driver Class...";
                java.lang.Class class1 = java.lang.Class.forName(s1);
                s7 = s7 + "<br>Driver Class: " + s1 + " <b>LOCATED SUCESSFULLY!</b>";
                try
                {
                    s7 = s7 + "<p>Step 2: <br>Attempting to Load Driver Class...";
                    class1.newInstance();
                    s7 = s7 + "<br>Driver Class <b>LOADED SUCCESSFULLY!</b>";
                    try
                    {
                        s7 = s7 + "<p>Step 3: Attempting to Connect to Database...";
                        java.sql.DriverManager.setLoginTimeout(60);
                        connection = java.sql.DriverManager.getConnection(s, s3, s4);
                        s7 = s7 + "<br>Database: " + s + " Connection <b>ESTABLISHED SUCESSFULLY!</b>";
                        if(s2.length() > 0)
                        {
                            try
                            {
                                s7 = s7 + "<p>Step 4: Attempting to Execute Query...";
                                s7 = s7 + "<br>Statement created.";
                                statement = connection.createStatement();
                                try
                                {
                                    statement.setQueryTimeout(60);
                                    resultset = statement.executeQuery(s2);
                                    s7 = s7 + "<br>Statement executed.";
                                    long l = (new Long(s5)).longValue();
                                    long l1 = (new Long(s6)).longValue();
                                    long l2 = resultset.getMetaData().getColumnCount();
                                    if(l > l2)
                                    {
                                        l = l2;
                                    }
                                    try
                                    {
                                        int i = 0;
                                        String s9 = "<p><TABLE BORDER=2 cellspacing=0>";
                                        for(int j = 0; (long)j < l; j++)
                                        {
                                            s9 = s9 + "<TD><B>";
                                            s9 = s9 + resultset.getMetaData().getColumnName(j + 1);
                                            s9 = s9 + "</B></TD>";
                                        }

                                        for(; resultset.next(); i++)
                                        {
                                            if((long)i >= l1)
                                            {
                                                continue;
                                            }
                                            s9 = s9 + "<TR>";
                                            for(int k = 0; (long)k < l; k++)
                                            {
                                                s9 = s9 + "<TD>";
                                                s9 = s9 + resultset.getString(k + 1);
                                                s9 = s9 + "</TD>";
                                            }

                                            s9 = s9 + "</TR>";
                                        }

                                        s9 = s9 + "</TABLE>";
                                        s7 = s7 + "<p>" + i + " rows <b>RETURNED SUCCESSFULLY!</b>";
                                        s7 = s7 + s9;
                                    }
                                    catch(java.lang.Exception exception5)
                                    {
                                        flag = false;
                                        s7 = s7 + "<br><b>***ERROR***:</b> Unable to retrieve and iterate through query results.";
                                        s7 = s7 + "<br><b>Suggested resolution:</b> Check your query.";
                                    }
                                }
                                catch(java.lang.Exception exception1)
                                {
                                    flag = false;
                                    s7 = s7 + "<br><b>***ERROR***:</b> The supplied Query will not execute because it is either:\n<br><ol><li>invalid</li><li>the table does not exist</li><li>some of the columns do not exist</li></ol>";
                                }
                            }
                            catch(java.lang.Exception exception2)
                            {
                                flag = false;
                                s7 = s7 + "<br><b>***ERROR***:</b> Unable to create statement.";
                            }
                        }
                    }
                    catch(java.lang.Exception exception3)
                    {
                        flag = false;
                        s7 = s7 + "<br><b>***ERROR***:</b> Unable to Connect to Database.";
                        String s8 = exception3.getMessage();
                        s7 = s7 + "<br>Exception Message: " + s8;
                        if(s8.equals("No suitable driver") || s8.equals("Invalid connection string format"))
                        {
                            s7 = s7 + "<br><b>Suggested resolution:</b> Check to make sure the URL Conneciton String is well formed.\n<br>(i.e. jdbc:odbc:dbname) The names within the URL Conntion String must be separated by a \":\".";
                        } else
                        if(s8.indexOf("Login failed for user") != -1)
                        {
                            s7 = s7 + "<br><b>Suggested resolution:</b> Check to make sure the user and password you supplied are correct.";
                        } else
                        if(s8.endsWith("Data source name not found and no default driver specified"))
                        {
                            s7 = s7 + "<br><b>Suggested resolution:</b> Check to make sure the database SID, tcp address, and tcp port is correct.";
                        } else
                        if(s8.startsWith("Refused"))
                        {
                            s7 = s7 + "<br><b>Suggested resolution:</b> Check to make sure the database SID, tcp address, and tcp port is correct.";
                        } else
                        if(s8.equals("General error") && s.endsWith(" "))
                        {
                            s7 = s7 + "<br><b>Suggested resolution:</b> Your Connection URL ends with space.\nMake sure there are no trailing spaces.";
                        }
                    }
                }
                catch(java.lang.Exception exception4)
                {
                    flag = false;
                    s7 = s7 + "<br><b>***ERROR:***</b> Unable to instaniate the driver class.  The class name was found, but could\nnot be instantiated.\n";
                }
            }
            catch(java.lang.Exception exception)
            {
                flag = false;
                s7 = s7 + "<br><b>***ERROR:***</b>Unable to find the supplied driver.\n";
                s7 = s7 + "<br>Check the \\SiteView\\java\\lib\\ext\ndirectory to verify that the driver exists.  If you see your vendor below, click on the link\nto read technical information on how to setup your specific driver.<p><br><a href=\"/SiteView/docs/DatabaseMon.htm#MySQL\">MySQL</a>\n<br><a href=\"/SiteView/docs/DatabaseMon.htm#OracleThin\">Oracle</a>\n<br><a href=\"/SiteView/docs/DatabaseMon.htm#Informix\">Informix</a>\n";
            }
            finally
            {
                if(flag)
                {
                    if(s2.length() > 0)
                    {
                        s7 = s7 + "<p>Step 5: Attempting to Close ResultSet, Statement, and Connection...";
                    } else
                    {
                        s7 = s7 + "<p>Step 4: Attempting to Close Connection...";
                    }
                }
                if(resultset != null)
                {
                    try
                    {
                        resultset.close();
                    }
                    catch(java.lang.Exception exception7)
                    {
                        flag = false;
                        s7 = s7 + "<br><b>Error:</b> Database close resultSet error.";
                    }
                }
                if(statement != null)
                {
                    try
                    {
                        statement.close();
                    }
                    catch(java.lang.Exception exception8)
                    {
                        flag = false;
                        s7 = s7 + "<br><b>Error:</b> Database statement close error.";
                    }
                }
                if(connection != null)
                {
                    try
                    {
                        connection.close();
                    }
                    catch(java.lang.Exception exception9)
                    {
                        flag = false;
                        s7 = s7 + "<br><b>Error:</b> Database connection close error.";
                    }
                }
                if(flag)
                {
                    s7 = s7 + "<br>All Resources <b>CLOSED SUCCESSFULLY!</b>";
                }
            }
        } else
        {
            s7 = "<p><b>***ERROR***:</b> You must supply a \"Database Connection\" URL and \"Database Driver\" at a minimum.";
        }
        outputStream.println("<center><a href=/SiteView/cgi/go.exe/SiteView?page=monitor&operation=Tools&account=" + request.getAccount() + ">Diagnostic Tools</a></center><p>");
        outputStream.println("<p>\n<CENTER><H2>Database Connection Test</H2></CENTER><P>\n<p>\n" + getPagePOST("dbConnection", "") + "<FORM ACTION=/SiteView/cgi/go.exe/SiteView&page=monitor&operation=Tools&account=" + request.getAccount() + " method=POST>\n" + "This is an interface to a database connection diagnostic tool which uses a supplied JDBC or ODBC\n" + "driver and URL Connection string to test the connection to a database.  The tool will check to see if\n" + "1) the supplied driver can be found and loaded, 2) a connection can be made to the database, 3) an \n" + "optional SQL query can be executed and the results displayed, and finally, 4) the database connection\n" + "and resources can be closed.  When exceptions and errors are encountered, the information is printed and\n" + "a suggested resolution is given to help with troubleshooting.  This tool can be excellent in helping to\n" + "determine connection parameter values to be supplied in monitor setup, database alerts, and database logging.\n" + "<p>\n" + "<p><TABLE BORDER=0>\n" + "<TR><TD ALIGN=RIGHT>Database Connection URL:<TD><input type=text name=connectionURL value=\"" + s + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Database Driver:<TD><input type=text name=driver value=\"" + s1 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Database User Name:<TD><input type=text name=username value=\"" + s3 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Database Password:<TD><input type=password name=password value=\"" + s4 + "\" size=60><br>\n" + "<TR><TD ALIGN=RIGHT>Query:<TD><input type=text name=query value=\"" + escapeSQLQuery(s2) + "\" size=90><br>\n" + "<TR><TD><TD><i>(Optional SQL Query)</TD></TR>\n" + "<TR><TD ALIGN=RIGHT>Results Set Max Columns:<TD><input type=text name=maxcol value=\"" + s5 + "\" size=30><br>\n" + "<TR><TD ALIGN=RIGHT>Results Set Max Rows:<TD><input type=text name=maxrow value=\"" + s6 + "\" size=30><br>\n" + "</TABLE><p>\n");
        outputStream.println("<input type=submit value=\"Connect and Execute Query\" class=\"VerBl8\">\n</FORM>\n");
        outputStream.println(s7);
        printFooter(outputStream);
    }

    private String escapeSQLQuery(String s)
    {
        return s.replaceAll("\"", "&quot;").replaceAll("\\+", "&#43;");
    }

    public static void main(String args[])
        throws java.io.IOException
    {
        COM.dragonflow.Page.dbConnectionPage dbconnectionpage = new dbConnectionPage();
        if(args.length > 0)
        {
            dbconnectionpage.args = args;
        }
        dbconnectionpage.handleRequest();
    }
}
