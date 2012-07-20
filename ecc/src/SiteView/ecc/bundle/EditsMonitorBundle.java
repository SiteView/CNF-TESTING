package SiteView.ecc.bundle;

import java.util.Map;

import Siteview.Api.BusinessObject;

import siteview.IAutoTaskExtension;

public class EditsMonitorBundle implements IAutoTaskExtension {

	public EditsMonitorBundle() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String run(Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		EditGroupBundle edit=new EditGroupBundle();
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString();
		String id=bo.get_RecId();
		if(EditMonitorBundle.map!=null){
			String s=EditMonitorBundle.map.get(id);
			edit.updateGroup("GroupId="+s);
		}
		edit.updateGroup("GroupId="+groupId);
		return null;
	}

}
