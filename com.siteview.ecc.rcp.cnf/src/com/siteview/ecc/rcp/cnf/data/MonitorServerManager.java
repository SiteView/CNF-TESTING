
package com.siteview.ecc.rcp.cnf.data;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.PlatformObject;


/**
 * 
 * 
 */
public class MonitorServerManager extends PlatformObject {

	private Map<String,MonitorServer> servers = new HashMap<String, MonitorServer>();
	private MonitorServer curServer;
	private boolean bLogin = false;

	private static MonitorServerManager INSTANCE;

	public static MonitorServerManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new MonitorServerManager();
		return INSTANCE;
	}

	private String name="root";

	public MonitorServerManager() {
		// enforce the singleton patter
	}

	public Map getServers() {
		
		return servers;
	}
	

	public void registerServer(MonitorServer server) throws RemoteException, NotBoundException {
//		server.connectAndLogin();
		servers.put(server.getHost(),server) ;
//		servers.put(server.getUserId(), server) ;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public MonitorServer getCurServer() {
		return this.curServer;
	}
	
	public void setCurServer(MonitorServer server) {
		this.curServer = server;
	}
	
	public boolean getLoginSucess()	{
		return this.bLogin;
	}
	
	public boolean setLoginSucess(boolean bIn) {
		return this.bLogin = bIn;
	}
}
