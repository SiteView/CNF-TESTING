package SiteView.ecc.bundle;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

import SiteView.ecc.Activator;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;
import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;
import siteview.IAutoTaskExtension;
import siteview.windows.forms.ImageHelper;


public class addGroupBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public static String oid="1.3.6.1.2.1.1.2";
	public addGroupBundle(){}

	public String run(Map<String, Object> params){
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetField("RecId").get_NativeValue().toString();
		String parentId=bo.GetField("ParentGroupId").get_NativeValue().toString();//父组ID
		//加载本组
		addGroups("GroupId="+groupId);
		//存在父组，先加载父组
		if(parentId!=null && !parentId.equals("")){
			BusinessObject bo1=EccTreeControl.CreateBo("RecId",parentId, "EccGroup");
			String s=bo1.GetField("HasSubGroup").get_NativeValue().toString();
			if(!s.equals("true")){
				bo1.GetField("HasSubGroup").SetValue(new SiteviewValue("true"));
				bo1.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
			}
			EditGroupBundle edit=new EditGroupBundle();
			edit.updateGroup("GroupId="+parentId);
			try {
				List<Map<String, Object>> subgroups=rmiServer.getChildGroupInstances(parentId);
				EccTreeControl.groups_subgroups.put(parentId, subgroups);
				for(int i=0;i<subgroups.size();i++){
					String id=subgroups.get(i).get("_id").toString();
					id=id.substring(id.lastIndexOf("/")+1);
					if(id.equals(groupId)){
						EccTreeControl.groups.put(groupId, subgroups.get(i));
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (SiteViewException e) {
				e.printStackTrace();
			}
		}else{
			try {
				List<Map<String, Object>>groups=rmiServer.getAllGroupInstances();
				for(int i=0;i<groups.size();i++){
					String id=groups.get(i).get("_id").toString();
					id=id.substring(id.lastIndexOf("/")+1);
					if(id.equals(groupId)){
						EccTreeControl.groups.put(groupId, groups.get(i));
						EccTreeControl.groups_0.add(groups.get(i));
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SiteViewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(parentId!=null&&!parentId.equals("")){
			for(TreeItem treeItem:EccTreeControl.trtmNewTreeitem1.getItems()){
				String id=((Map<String,Object>)treeItem.getData()).get("_id").toString();
				id=id.substring(id.lastIndexOf("/")+1);
				if(id.equals(parentId)){
					String text=treeItem.getText();
					text=text.substring(0,text.indexOf("(")+1);
					treeItem.setText(text+EccTreeControl.groups_subgroups.get(parentId).size()+")");
					if(EccTreeControl.groups_subgroups.size()==1||treeItem.getItemCount()>0){
						TreeItem treeItem_1=new TreeItem(treeItem,SWT.NONE);
						treeItem_1.setText(EccTreeControl.groups.get(groupId).get("_name").toString()+"(0)");
						treeItem_1.setData(EccTreeControl.groups.get(groupId));
						treeItem_1.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/node.jpg"));
					}
				}
			}
			
		}else{
			TreeItem treeItem=new TreeItem(EccTreeControl.trtmNewTreeitem1,SWT.NONE);
			treeItem.setText(EccTreeControl.groups.get(groupId).get("_name").toString()+"(0)");
			treeItem.setData(EccTreeControl.groups.get(groupId));
			treeItem.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/node.jpg"));
		}
		return null;
	}
	public void addGroups(String s){
		if(rmiServer==null){
			rmiServer =EditGroupBundle.createAmiServer();
		}
		try {
			rmiServer.adjustGroups(s,"","add");	
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
