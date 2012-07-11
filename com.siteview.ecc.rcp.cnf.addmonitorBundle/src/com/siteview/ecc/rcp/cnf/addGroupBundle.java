package com.siteview.ecc.rcp.cnf;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import SiteView.ecc.views.EccTreeControl;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.Api.ApiRmiServer;

import siteview.IAutoTaskExtension;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;


public class addGroupBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public static String oid="1.3.6.1.2.1.1.2";
	public addGroupBundle(){}

	public String run(Map<String, Object> params){
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetField("RecId").get_NativeValue().toString();
		String parentId=bo.GetField("ParentGroupId").get_NativeValue().toString();
		if(parentId!=null && !parentId.equals("")){
			BusinessObject bo1=EccTreeControl.CreateBo(parentId, "EccGroup");
			String s=bo1.GetField("HasSubGroup").get_NativeValue().toString();
			if(!s.equals("true")){
				bo1.GetField("HasSubGroup").SetValue(new SiteviewValue("true"));
				bo1.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
			}
			addGroups("GroupId="+parentId);
		}
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
	public void updateGroup(String s){
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery("EccGroup", QueryInfoToGet.All);
		XmlElement xml ;
		xml=query.get_CriteriaBuilder().FieldAndValueExpression("RecId",
				Operators.Equals, s);
		query.set_BusObSearchCriteria(xml);
	}
}
