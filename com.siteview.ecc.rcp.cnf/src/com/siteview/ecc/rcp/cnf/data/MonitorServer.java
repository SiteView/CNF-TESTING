
package com.siteview.ecc.rcp.cnf.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;

import COM.dragonflow.Api.APIInterfaces;


/**
 * Encapsulates the state for a monitor server, including the connection details (user
 * name, password, server) and the connection itself.
 * FIXME: add real time statics about the monitoring server 
 */
public class MonitorServer extends PlatformObject {

    private  APIInterfaces rmiServer;
	private String host, port, userId,password,alias,description;

	public MonitorServer() {
		
    	String hostname = "";
    	try {
    	    InetAddress addr = InetAddress.getLocalHost();

    	    // Get IP Address
    	    byte[] ipAddr = addr.getAddress();

    	    // Get hostname
    	    this.host = addr.getHostName();
    	} catch (UnknownHostException e) {
    	}

		this.port = "3232";
		this.alias=this.host;
	}
	
	public MonitorServer(String host, String port, String userId,String password,String alias,String description) {
		this.userId = userId;
		this.host = host;
		this.port = port;
		this.password = password;
		this.alias = alias;
		this.description= description;
	}

	public APIInterfaces getRmiServer() {
		return rmiServer;
	}

	public void setRmiServer(APIInterfaces rmiServer) {
		this.rmiServer = rmiServer;
	}

	public void setConnectionDetails(String host, String port, String userId,String password,String alias,String description) {
		this.userId = userId;
		this.host = host;
		this.port = port;
		this.password = password;
		this.alias = alias;
		this.description= description;
	}
	
	public void setHost(String host) {
		this.host= host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(String port) {
		this.port= port;
	}

	public String getPort() {
		return port;
	}
	
	public void setUserId(String userid) {
		this.userId= userid;
	}

	public String getUserId() {
		return userId;
	}

	public void setPassword(String password) {
		this.password= password;
	}

	public String getPassword() {
		return password;
	}
	
	public void setAlias(String alias) {
		this.alias= alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setDescription(String description) {
		this.description= description;
	}

	public String getDescription() {
		return description;
	}	

	/**
	 * Establishes the connection to the server and logs in. The connection
	 * details must have already been set.
	 * @throws NotBoundException 
	 */
	public void connectAndLogin()
			throws RemoteException, NotBoundException {
	    	Registry registry=LocateRegistry.getRegistry(host,(new Integer(port).intValue()));
		  	rmiServer=(APIInterfaces)(registry.lookup("kernelApiRmiServer"));		  	
	}

}
