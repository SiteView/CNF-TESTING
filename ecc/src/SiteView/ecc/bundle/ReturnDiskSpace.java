package SiteView.ecc.bundle;

import java.util.Map;

import siteview.IAutoTaskExtension;
import COM.dragonflow.Api.APIInterfaces;
import SiteView.ecc.dialog.ChooseDiskSpace;
import Siteview.Api.BusinessObject;

public class ReturnDiskSpace implements IAutoTaskExtension {
	APIInterfaces rmiServer;
	public ReturnDiskSpace() {
		
	}

	@Override
	public String run(Map<String, Object> params) throws Exception {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String address=bo.GetField("HostNameDiskSpace").get_NativeValue().toString();
		rmiServer=EditGroupBundle.createAmiServer();
		String[] disk=rmiServer.getDiskSpace(address);
		if(disk.length>0){
			ChooseDiskSpace space=new ChooseDiskSpace(null,disk,bo);
			space.open();	
		}
		return null;
	}
}
