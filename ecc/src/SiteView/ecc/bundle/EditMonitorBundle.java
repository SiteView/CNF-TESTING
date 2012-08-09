package SiteView.ecc.bundle;

import java.util.HashMap;
import java.util.Map;

import SiteView.ecc.views.EccTreeControl;
import Siteview.Api.BusinessObject;

import siteview.IAutoTaskExtension;

public class EditMonitorBundle implements IAutoTaskExtension {
	public static Map<String,String> map=null;
	public EditMonitorBundle() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String run(Map<String, Object> params) {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String id=bo.get_RecId();
		BusinessObject bo1=EccTreeControl.CreateBo("RecId",id, "Ecc");
		if(bo1!=null){
			String oldgroupId=bo1.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString();
			map=new HashMap<String,String>();
			map.put(id, oldgroupId);
		}
		return null;
	}

}
