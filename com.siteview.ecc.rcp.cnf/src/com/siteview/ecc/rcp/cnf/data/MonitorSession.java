
package com.siteview.ecc.rcp.cnf.data;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;

import COM.dragonflow.Api.APIInterfaces;


/**
 * Encapsulates the state for a session, including the connection details (user
 * name, password, server) and the connection itself.
 */
public class MonitorSession implements IAdaptable {

	private MonitorConnectionDetails connectionDetails;
    private  APIInterfaces rmiServer;

	private static MonitorSession INSTANCE;

	public static MonitorSession getInstance() {
		if (INSTANCE == null)
			INSTANCE = new MonitorSession();
		return INSTANCE;
	}

	public MonitorSession() {
		// enforce the singleton patter
	}

	public APIInterfaces getRmiServer() {
		return rmiServer;
	}

	public void setRmiServer(APIInterfaces rmiServer) {
		this.rmiServer = rmiServer;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public MonitorConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	public void setConnectionDetails(MonitorConnectionDetails connectionDetails) {
		this.connectionDetails = connectionDetails;
	}

	/**
	 * Establishes the connection to the server and logs in. The connection
	 * details must have already been set.
	 * @throws NotBoundException 
	 */
	public void connectAndLogin(final IProgressMonitor progress)
			throws RemoteException, NotBoundException {
	    	Registry registry=LocateRegistry.getRegistry(connectionDetails.getServer(),(new Integer(connectionDetails.getPort())).intValue());
		  	rmiServer=(APIInterfaces)(registry.lookup("kernelApiRmiServer"));
	}

	public Object getRoot() {
		// TODO Auto-generated method stub
		return null;
	}
}
