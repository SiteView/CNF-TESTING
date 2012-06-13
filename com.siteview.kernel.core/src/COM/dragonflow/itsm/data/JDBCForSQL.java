package COM.dragonflow.itsm.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JDBCForSQL {
	private static Connection conn = null;

	/**
	 * get Connection
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
					.newInstance();
			String URL = "jdbc:sqlserver://192.168.9.131:1433;DatabaseName=SiteView";
			String USER = "sa";
			String PASSWORD = "siteview";
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (java.lang.ClassNotFoundException ce) {
			System.out.println("Get Connection error:");
			ce.printStackTrace();
		} catch (java.sql.SQLException se) {
			System.out.println("Get Connection error:");
			se.printStackTrace();
		} catch (Exception e) {
			System.out.println("Get Connection error:");
			e.printStackTrace();
		}
		return conn;
	}

	/*
	 * the Program is to Select the database!!!
	 */
	public static ResultSet sql_ConnectExecute_Select(String query_sql) {
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			if (!conn.isClosed()) {
				Statement statement = conn.createStatement();
				rs = statement.executeQuery(query_sql);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static void savaLog(String sql) {

		try {
			Connection conn = getConnection();
			if (!conn.isClosed()) {
				Statement statement = conn.createStatement();
				statement.execute(sql);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}

	}

	public void testConnection() {
		if (conn == null)
			this.getConnection();
		try {
			String sql = "SELECT * FROM Ecc";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.getString("RecId"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
	}

	public static void main(String[] args) {
		JDBCForSQL jdbc = new JDBCForSQL();
		// SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Timestamp d2=new Timestamp(System.currentTimeMillis());
		// jdbc.savaLog("insert into MonitorLog(RecId,ownerID,MonitorStatus,MonitorName,MonitorId,MonitorMassage,CreatedDateTime)values('ad26c8ee090d47dfaa9a3ee477c1ba90','lili','good','ping131','1','0.01 sec*39**','"+Timestamp.valueOf(f.format(d2))+"')");
		String query_sql = "select * from Ecc where Groups_Valid ='5042ACCD47B2495A97D814DCB4D3E2B9'";
		ResultSet eccrs = JDBCForSQL.sql_ConnectExecute_Select(query_sql);
		ResultSetMetaData metaData;
		try {
			metaData = eccrs.getMetaData();
			int colum = metaData.getColumnCount();
			while (eccrs.next()) {
				for (int i = 1; i < colum; i++) {
					// Get colum name
					String columName = metaData.getColumnName(i);
					String datavalue = eccrs.getString(columName);
					System.out.println("ÁÐ£º" + columName + " Öµ£º" + datavalue);
					if (datavalue == null )
						System.out.println("¿ÕÁÐ£º" + columName);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
