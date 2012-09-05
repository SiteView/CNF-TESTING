package SiteView.ecc.bundle;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;

import SiteView.ecc.Activator;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.Modle.SiteViewEcc;
import SiteView.ecc.data.SiteViewData;
import SiteView.ecc.view.EccTreeControl;
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
		GroupModle groupModle=new GroupModle(bo,true,true,true,true,true,true,true);
		SiteViewData.subgroups.put(groupId, groupModle);
		if(parentId!=null && !parentId.equals("")){
			GroupModle group=SiteViewData.subgroups.get(parentId); 
			BusinessObject bo1=group.getBo();
			String s=bo1.GetField("HasSubGroup").get_NativeValue().toString();
			if(!s.equals("true")){
				bo1.GetField("HasSubGroup").SetValue(new SiteviewValue("true"));
				bo1.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
			}
			EditGroupBundle edit=new EditGroupBundle();
			edit.updateGroup("GroupId="+parentId);
			
			group.getGroups().add(groupModle);
			SiteViewData.subgroups.put(parentId, group);
			EccTreeControl.treeViewer.update(group, new String[]{"groups"});
			EccTreeControl.treeViewer.insert(group, groupModle, 0);
		}else{
			SiteViewEcc site=((List<SiteViewEcc>)EccTreeControl.treeViewer.getInput()).get(0);
			List list=site.getList();
			list.add(groupModle);
			EccTreeControl.treeViewer.update(site, new String[]{"list"});
			EccTreeControl.treeViewer.insert(site, groupModle, 0);
			EccTreeControl.treeViewer.refresh();
		}
		setAuther(groupModle,bo.get_RecId());
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
	public static void setAuther(Object obj,String id){
		BusinessObject bo=ConnectionBroker.get_SiteviewApi().get_BusObService().Create("Permissions");
		if(obj instanceof GroupModle ){
			GroupModle group=(GroupModle) obj;
			bo.GetField("PermissionsType").SetValue(new SiteviewValue("Group"));
			bo.GetField("AddMachine").SetValue(new SiteviewValue(true));
			bo.GetField("AddGroup").SetValue(new SiteviewValue(true));
			bo.GetField("EditGroup").SetValue(new SiteviewValue(true));
			bo.GetField("DeleteGroup").SetValue(new SiteviewValue(true));
			
		}else if(obj instanceof MachineModle){
			MachineModle machine=(MachineModle) obj;
			bo.GetField("PermissionsType").SetValue(new SiteviewValue("Machine"));
			bo.GetField("DeleteMachine").SetValue(new SiteviewValue(true));
			bo.GetField("EditMachine").SetValue(new SiteviewValue(true));
		}
		bo.GetField("PermissionsId").SetValue(new SiteviewValue(id));
		bo.GetField("SelectPerimissions").SetValue(new SiteviewValue(true));
		bo.GetField("UserId").SetValue(new SiteviewValue(ConnectionBroker.get_SiteviewApi().get_AuthenticationService().get_CurrentAuthenticationId()));
		bo.GetField("AddMonitor").SetValue(new SiteviewValue(true));
		bo.GetField("EditMonitor").SetValue(new SiteviewValue(true));
		bo.GetField("DeleteMonitor").SetValue(new SiteviewValue(true));
		SiteViewData.permissions.put(id, bo);
		bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true, true);
	}
}
