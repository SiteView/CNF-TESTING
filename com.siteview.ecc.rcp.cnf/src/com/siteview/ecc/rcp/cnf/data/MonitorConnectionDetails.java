
package com.siteview.ecc.rcp.cnf.data;

public class MonitorConnectionDetails {
	private String userId, server, port, password;

	public MonitorConnectionDetails(String userId, String server, String port, String password) {
		this.userId = userId;
		this.server = server;
		this.port = port;
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public String getServer() {
		return server;
	}

	public String getPassword() {
		return password;
	}

	public String getResource() {
		return String.valueOf(System.currentTimeMillis());
	}

	public String getPort() {
		return port;
	}
}
