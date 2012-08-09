package SiteView.ecc.bundle;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;
import COM.dragonflow.Api.APIInterfaces;
import siteview.IAutoTaskExtension;


public class addGroupBundle implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public static String oid="1.3.6.1.2.1.1.2";
	public addGroupBundle(){}

	public String run(Map<String, Object> params){
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetField("RecId").get_NativeValue().toString();
		
		String parentId=bo.GetField("ParentGroupId").get_NativeValue().toString();//父组ID
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
		}
		//加载本组
		addGroups("GroupId="+groupId);
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
