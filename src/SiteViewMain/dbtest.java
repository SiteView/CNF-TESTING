// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-3-8 14:05:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package SiteViewMain;

import java.io.*;
import java.sql.*;

public class dbtest
{

    public dbtest()
    {
    }

    boolean doConnect()
    {
        System.out.println("Going to get driver: " + dbDriver);
        try
        {
            Class.forName(dbDriver).newInstance();
        }
        catch(Exception exception)
        {
            System.out.println("Exception getting driver: " + exception);
            return false;
        }
        System.out.println("database monitor, connecting, database=" + dbConnection + ", user=" + login);
        if(connectTimeout > 0)
            DriverManager.setLoginTimeout(connectTimeout);
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(dbConnection, login, passwd);
        }
        catch(Exception exception1)
        {
            System.out.println("Exception getting connection: " + exception1);
            return false;
        }
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
        }
        catch(Exception exception2)
        {
            System.out.println("Exception getting statement: " + exception2);
            return false;
        }
        if(queryTimeout > 0)
            try
            {
                statement.setQueryTimeout(queryTimeout);
            }
            catch(Exception exception3)
            {
                System.out.println("Exception getting statement: " + exception3);
                return false;
            }
        ResultSet resultset = null;
        try
        {
            resultset = statement.executeQuery(query);
        }
        catch(Exception exception4)
        {
            System.out.println("Exception getting query: " + exception4);
            return false;
        }
        if(resultset == null)
        {
            System.out.println("no results from query");
            return false;
        }
        int i;
        try
        {
            i = resultset.getMetaData().getColumnCount();
        }
        catch(Exception exception5)
        {
            System.out.println("Exception getting query: " + exception5);
            return false;
        }
        System.out.println("Got column count of: " + i);
        try
        {
            while(resultset.next()) 
                try
                {
                    int j = 1;
                    while(j <= i) 
                    {
                        System.out.println("Column " + j + ": " + resultset.getString(j));
                        j++;
                    }
                }
                catch(Exception exception6)
                {
                    System.out.println("Exception getting column=" + exception6);
                }
        }
        catch(Exception exception7)
        {
            System.out.println("Exception getting results=" + exception7);
        }
        SQLWarning sqlwarning = null;
        try
        {
            sqlwarning = connection.getWarnings();
        }
        catch(Exception exception8)
        {
            System.out.println("Exception getting warnings=" + exception8);
        }
        for(; sqlwarning != null; sqlwarning = sqlwarning.getNextWarning())
            System.out.println("SQLWarning: " + sqlwarning.getMessage() + ", " + sqlwarning.getErrorCode() + ", " + sqlwarning.getSQLState());

        return true;
    }

    public static void main(String args[])
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            System.out.println("\tfor odbc: sun.jdbc.odbc.JdbcOdbcDriver\n\tfor oracle: oracle.jdbc.driver.OracleDriver");
            System.out.print("Enter DB Driver: ");
            dbDriver = bufferedreader.readLine();
            System.out.println("\n\tfor odbc: jdbc:odbc:<DSN>\n\t (eg. jdbc:odbc:my_msql_connection\n\tfor oracle: jdbc:oracle:thin:@<tcp address>:<SID>\n\t (eg. jdbc:oracle:thin:@206.168.191.19:1521:ORCL");
            System.out.print("Enter DB URL: ");
            dbConnection = bufferedreader.readLine();
            System.out.print("Enter Query: ");
            query = bufferedreader.readLine();
            System.out.print("Enter login (if it applies): ");
            login = bufferedreader.readLine();
            System.out.print("Enter password (if it applies): ");
            passwd = bufferedreader.readLine();
        }
        catch(Exception exception)
        {
            System.out.println("Exception reading stdin");
        }
        dbtest dbtest1 = new dbtest();
        dbtest1.doConnect();
    }

    static String dbConnection;
    static String dbDriver;
    static String query;
    static String login;
    static String passwd;
    static int connectTimeout;
    static int queryTimeout;
}