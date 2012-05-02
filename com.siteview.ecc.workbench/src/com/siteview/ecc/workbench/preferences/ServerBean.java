package com.siteview.ecc.workbench.preferences;

import java.util.List;
import java.util.Vector;

//import rcpdev.common.ui.databinding.AbstractBean;

public class ServerBean extends AbstractBean {

	private List servers;

	public ServerBean() {
		super();
		servers = new Vector();
	}

	public List getServers() {
		return (List) ((Vector) servers).clone();
	}

	public static final String ADD = "ServerBean.add";

	public static final String REMOVE = "ServerBean.remove";

	public static final String EDIT = "ServerBean.edit";

	public void addServer(String server) {
		servers.add(server);
		firePropertyChange(ADD, null, server);
	}

	public void removeServer(String server) {
		servers.remove(server);
		firePropertyChange(REMOVE, server, null);
	}

	public void updateServer(String oldServer, String newServer) {
		int oldIndex = servers.indexOf(oldServer);
		if (!servers.contains(newServer)) {
			servers.set(oldIndex, newServer);
			firePropertyChange(EDIT, oldServer, newServer);
		}
	}
}
