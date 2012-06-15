package com.siteview.ecc.rcp.cnf;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import Siteview.Api.BusinessObject;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.Api.ApiRmiServer;

import siteview.IAutoTaskExtension;


public class addGroupBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public static String oid="1.3.6.1.2.1.1.2";
	public addGroupBundle(){}

	public String run(Map<String, Object> params){
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetField("RecId").get_NativeValue().toString();
		addGroups("GroupId="+groupId);
		return null;
	}
	public void addGroups(String s){
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());
			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
				rmiServer.adjustGroups(s,"");	
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
