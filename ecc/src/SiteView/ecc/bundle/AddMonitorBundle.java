package SiteView.ecc.bundle;

import java.util.Map;

import Siteview.Api.BusinessObject;


import siteview.IAutoTaskExtension;

public class AddMonitorBundle implements IAutoTaskExtension {
	public AddMonitorBundle() {
	}
	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString();
		EditGroupBundle edit=new EditGroupBundle();
		edit.updateGroup("GroupId="+groupId);
		return null;
	}
}
