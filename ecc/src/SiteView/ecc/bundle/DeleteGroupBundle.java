package SiteView.ecc.bundle;

import java.rmi.RemoteException;
import java.util.Map;

import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.data.SiteViewData;
import SiteView.ecc.view.EccTreeControl;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;

import siteview.IAutoTaskExtension;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;

public class DeleteGroupBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public DeleteGroupBundle() {
	}
	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		deleteGroup(bo);
		return null;
	}
	public void deleteGroup(String id){
		if(rmiServer==null){
			rmiServer=EditGroupBundle.createAmiServer();
		}
		try {
			rmiServer.adjustGroups("", id, "delete");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteGroup(BusinessObject bo){
		String groupId=bo.GetField("RecId").get_NativeValue().toString();
		String parentId=bo.GetField("ParentGroupId").get_NativeValue().toString();
		
		ICollection subGroups=getBusinessObject("ParentGroupId",groupId,"EccGroup");
		IEnumerator interfaceTableIEnum = subGroups.GetEnumerator();
		while(interfaceTableIEnum.MoveNext()){
			BusinessObject subGroup=(BusinessObject) interfaceTableIEnum.get_Current();
			subGroup.DeleteObject(ConnectionBroker.get_SiteviewApi());
		}
		
		ICollection monitors=getBusinessObject("Groups_Valid",groupId,"Ecc");
		interfaceTableIEnum = monitors.GetEnumerator();
		while(interfaceTableIEnum.MoveNext()){
			BusinessObject monitor=(BusinessObject) interfaceTableIEnum.get_Current();
			monitor.DeleteObject(ConnectionBroker.get_SiteviewApi());
		}
		
		if(parentId!=null&&!parentId.equals("")){
			GroupModle group=SiteViewData.subgroups.get(parentId);
				if(group.getGroups().size()<=0){
					BusinessObject parentbo=group.getBo();
					if(parentbo!=null){
						parentbo.GetField("HasSubGroup").SetValue(new SiteviewValue("false"));
						parentbo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
					}
				}
				EditGroupBundle edit=new EditGroupBundle();
				edit.updateGroup("GroupId="+parentId);
		}
		deleteGroup("GroupId="+groupId);
		EccTreeControl.treeViewer.refresh();
	}
	
	public ICollection getBusinessObject(String key,String value,String table){
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(table, QueryInfoToGet.All);
		XmlElement xml ;
		xml=query.get_CriteriaBuilder().FieldAndValueExpression(key,
				Operators.Equals, value);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction =  ConnectionBroker.get_SiteviewApi().get_BusObService()
				.get_SimpleQueryResolver().ResolveQueryToBusObList(query);
		return iCollenction;
	}
}
