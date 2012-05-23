package COM.dragonflow.itsm.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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
			String URL = "jdbc:sqlserver://192.168.9.131:1433;DatabaseName=ECC9ITSM";
			String USER = "sa"; // 根据你自己设置的数据库连接用户进行设�?
			String PASSWORD = "siteview"; // 根据你自己设置的数据库连接密码进行设�?
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

//	public static void main(String[] args) {
//		JDBCForSQL jdbc = new JDBCForSQL();
//		jdbc.testConnection();
//	}
}
