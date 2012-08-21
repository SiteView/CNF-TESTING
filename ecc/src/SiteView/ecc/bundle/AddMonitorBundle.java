package SiteView.ecc.bundle;

import java.util.Map;

import SiteView.ecc.editors.EccControl;
import SiteView.ecc.views.EccTreeControl;
import Siteview.Api.BusinessObject;

import COM.dragonflow.Api.APIInterfaces;

import siteview.IAutoTaskExtension;

public class AddMonitorBundle implements IAutoTaskExtension {
	public AddMonitorBundle() {
	}
	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String groupId=bo.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString();
		EditGroupBundle edit=new EditGroupBundle();
		edit.updateGroup("GroupId="+groupId);
		edit.load(groupId, bo);
		return null;
	}
}
