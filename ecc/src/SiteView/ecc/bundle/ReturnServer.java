package SiteView.ecc.bundle;

import java.util.Map;

import SiteView.ecc.dialog.ChooseService;
import Siteview.Api.BusinessObject;

import COM.dragonflow.Api.APIInterfaces;

import siteview.IAutoTaskExtension;

public class ReturnServer implements IAutoTaskExtension {
	APIInterfaces rmiServer;

	public ReturnServer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String run(Map<String, Object> params) throws Exception {
		BusinessObject bo = (BusinessObject) params.get("_CUROBJ_");
		String address=bo.GetField("Service").get_NativeValue().toString();
		rmiServer=EditGroupBundle.createAmiServer();
		String[] service = rmiServer.getServer(address);
		for (int i = 0; i < service.length; i++) {
			System.out.println(service[i]);
		}
		ChooseService cs=new ChooseService(null, service, bo);
		cs.open();
		return null;
	}

}
