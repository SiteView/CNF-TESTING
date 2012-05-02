package com.siteview.ecc.workbench.preferences;

//import rcpdev.contact.ui.common.provider.AbstractContentProvider;

public class ServerContentProvider extends AbstractContentProvider {

	public Object[] getElements(Object inputElement) {
		return ((ServerBean) inputElement).getServers().toArray();
	}

}
