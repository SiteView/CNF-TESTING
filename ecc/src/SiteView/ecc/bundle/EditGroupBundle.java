package SiteView.ecc.bundle;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.dialog.GroupTreeDialog;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

import siteview.IAutoTaskExtension;

public class EditGroupBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public EditGroupBundle() {
	}

	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetField("RecId").get_NativeValue().toString();
		if(GroupTreeDialog.map.get(groupId)==null||!GroupTreeDialog.map.get(groupId)){
			return null;
		}
		String parentId=bo.GetField("ParentGroupId").get_NativeValue().toString();
		if(EccTreeControl.oldParentId.get(groupId)!=null&&parentId!=null&&!parentId.equals("")){
			String oldparentId=EccTreeControl.oldParentId.get(groupId);
			if(!oldparentId.equals(parentId)){
				APIInterfaces api=createAmiServer();
				try {
					if(api.getChildGroupInstances("SiteView/"+oldparentId).size()==1){
						BusinessObject oldparentbo=EccTreeControl.CreateBo(oldparentId, "EccGroup");
						oldparentbo.GetField("HasSubGroup").SetValue(new SiteviewValue("false"));
						oldparentbo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
					}
					EccTreeControl.oldParentId.put(groupId,parentId);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (SiteViewException e) {
					e.printStackTrace();
				}
				updateGroup("GroupId="+oldparentId);
				BusinessObject bo1=EccTreeControl.CreateBo(parentId, "EccGroup");
				String s=bo1.GetField("HasSubGroup").get_NativeValue().toString();
				if(!s.equals("true")){
					bo1.GetField("HasSubGroup").SetValue(new SiteviewValue("true"));
					bo1.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
				}
				updateGroup("GroupId="+parentId);
			}
		}
		updateGroup("GroupId="+groupId);
		return null;
	}
	public void updateGroup(String id){
		if(rmiServer==null){
			rmiServer=createAmiServer();
		}
		try {
			rmiServer.adjustGroups("",id,"edit");
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
	public static void main(String[]args){
		String RootFilePath=System.getProperty("user.dir");
		System.out.println(RootFilePath);
	}
	public static APIInterfaces createAmiServer(){
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		APIInterfaces rmiServer=null;
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());
			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
		} catch(Exception e){
			e.printStackTrace();
		}
		return rmiServer;
	}
}
