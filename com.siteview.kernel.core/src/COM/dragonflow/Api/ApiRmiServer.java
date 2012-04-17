
package COM.dragonflow.Api;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;

public class ApiRmiServer extends java.rmi.server.UnicastRemoteObject implements APIInterfaces{
	String address;
	Registry registry; 


	public ApiRmiServer() throws RemoteException{
		try{  
			address = (InetAddress.getLocalHost()).toString();
		}
		catch(Exception e){
			System.out.println("can't get inet address.");
		}
		int port=3232; 
		System.out.println("this address=" + address +  ",port=" + port);
		try{
			registry = LocateRegistry.createRegistry(port);
			registry.rebind("rmiServer", this);
		}
		catch(RemoteException e){
			System.out.println("remote exception"+ e);
		}
	}


	public Collection getAllGroupInstances() {
		// TODO Auto-generated method stub
		return null;
	}


	public Collection getTopLevelGroupInstances() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
