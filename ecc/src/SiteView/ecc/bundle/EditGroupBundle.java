package SiteView.ecc.bundle;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.SiteViewEcc;
import SiteView.ecc.data.SiteViewData;
import SiteView.ecc.dialog.GroupTreeDialog;
import SiteView.ecc.view.EccTreeControl;
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
		GroupModle oldGroup=SiteViewData.subgroups.get(bo.get_RecId());
		BusinessObject oldbo=oldGroup.getBo();
		String oldParntId=oldbo.GetField("ParentGroupId").get_NativeValue().toString();
		String newParntId=bo.GetField("ParentGroupId").get_NativeValue().toString();
		GroupModle oldParent;
		if(oldParntId.equals(newParntId)){
			if(SiteViewData.subgroups.get(oldParntId)!=null){
				oldParent=SiteViewData.subgroups.get(oldParntId);
				oldParent.getGroups().remove(oldGroup);
				oldGroup.setBo(bo);
				oldParent.getGroups().add(oldGroup);
				SiteViewData.subgroups.put(oldParntId, oldParent);
				SiteViewData.subgroups.put(bo.get_RecId(), oldGroup);
				EccTreeControl.treeViewer.update(oldParent, new String[]{"groups"});
				EccTreeControl.treeViewer.update(oldGroup, new String[]{"bo"});
				EccTreeControl.treeViewer.refresh();
				updateGroup("GroupId="+oldParntId);
			}else{
				List<SiteViewEcc> list=(List<SiteViewEcc>) EccTreeControl.treeViewer.getInput();
				SiteViewEcc site=list.get(0);
				site.getList().remove(oldGroup);
				oldGroup.setBo(bo);
				site.getList().add(oldGroup);
				SiteViewData.subgroups.put(bo.get_RecId(), oldGroup);
				EccTreeControl.treeViewer.update(site, new String[]{"list"});
				EccTreeControl.treeViewer.update(oldGroup, new String[]{"bo"});
				EccTreeControl.treeViewer.refresh();
			}
			return null;
		}
		if(oldParntId!=null && !oldParntId.equals("")){
			GroupModle oldparent=SiteViewData.subgroups.get(oldParntId);
			oldparent.getGroups().remove(oldGroup);
			if(oldparent.getGroups().size()>=0){
				BusinessObject oldParntbo=oldparent.getBo();
				oldParntbo.GetField("HasSubGroup").SetValue(new SiteviewValue(false));
				oldParntbo.SaveObject(ConnectionBroker.get_SiteviewApi(), true, true);
			}
			SiteViewData.subgroups.put(oldParntId, oldparent);
			EccTreeControl.treeViewer.update(oldparent, new String[]{"groups"});
			EccTreeControl.treeViewer.remove(oldGroup);
			updateGroup("GroupId="+oldParntId);
		}else{
			List<SiteViewEcc> list=(List<SiteViewEcc>) EccTreeControl.treeViewer.getInput();
			SiteViewEcc site=list.get(0);
			site.getList().remove(oldGroup);
			EccTreeControl.treeViewer.update(site, new String[]{"list"});
			EccTreeControl.treeViewer.remove(oldGroup);
		}
		if(newParntId!=null && !newParntId.equals("")){
			GroupModle newparent=SiteViewData.subgroups.get(newParntId);
			BusinessObject newbo=newparent.getBo();
			newbo.GetField("HasSubGroup").SetValue(new SiteviewValue(true));
			newbo.SaveObject(ConnectionBroker.get_SiteviewApi(), true, true);
			newparent.getGroups().add(oldGroup);
			SiteViewData.subgroups.put(newParntId, newparent);
			EccTreeControl.treeViewer.update(newparent,  new String[]{"groups"});
			EccTreeControl.treeViewer.insert(newparent, oldGroup, 0);
			updateGroup("GroupId="+newParntId);
		}else{
			List<SiteViewEcc> list=(List<SiteViewEcc>) EccTreeControl.treeViewer.getInput();
			SiteViewEcc site=list.get(0);
			oldGroup.setBo(bo);
			site.getList().add(oldGroup);
			EccTreeControl.treeViewer.update(site, new String[]{"list"});
			EccTreeControl.treeViewer.insert(site, oldGroup, 0);
		}
		EccTreeControl.treeViewer.refresh();
		SiteViewData.subgroups.put(bo.get_RecId(),oldGroup);
		String groupId=bo.get_RecId();
//		if(EccTreeControl.oldParentId.get(groupId)!=null&&parentId!=null&&!parentId.equals("")){
//			String oldparentId=EccTreeControl.oldParentId.get(groupId);
//			if(!oldparentId.equals(parentId)){
//				APIInterfaces api=createAmiServer();
//				try {
//					if(api.getChildGroupInstances("SiteView/"+oldparentId).size()==1){
//						BusinessObject oldparentbo=EccTreeControl.CreateBo("RecId",oldparentId, "EccGroup");
//						oldparentbo.GetField("HasSubGroup").SetValue(new SiteviewValue("false"));
//						oldparentbo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
//					}
//					EccTreeControl.oldParentId.put(groupId,parentId);
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				} catch (SiteViewException e) {
//					e.printStackTrace();
//				}
//				
//				BusinessObject bo1=EccTreeControl.CreateBo("RecId",parentId, "EccGroup");
//				String s=bo1.GetField("HasSubGroup").get_NativeValue().toString();
//				if(!s.equals("true")){
//					bo1.GetField("HasSubGroup").SetValue(new SiteviewValue("true"));
//					bo1.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
//				}
//				updateGroup("GroupId="+parentId);
//			}
//		}
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
