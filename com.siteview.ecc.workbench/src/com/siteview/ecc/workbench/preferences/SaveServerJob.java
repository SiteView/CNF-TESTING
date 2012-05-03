package com.siteview.ecc.workbench.preferences;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import com.siteview.ecc.rcp.cnf.data.MonitorServer;

public class SaveServerJob extends Job {

	public static final String NAME = "Saving Server...";

	public SaveServerJob() {
		super(NAME);
		setPriority(Job.SHORT);
		setRule(StorageRule.getInstance());
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {		
		monitor.beginTask("Connecting...", IProgressMonitor.UNKNOWN);
		monitor.subTask("Contacting " + "..." + "...");		
		saveDescriptors();
		monitor.done();
		// TODO Auto-generated method stub
		return Status.OK_STATUS;
	}

	private List newServers;

	public List getNewServers() {
		return newServers;
	}

	public void setNewServers(List newServer) {
		this.newServers = newServer;
	}
	
	public void saveDescriptors() {
		try {
			ISecurePreferences preferences = SecurePreferencesFactory.getDefault();

//			preferences.put(LAST_USER, server.getUserId(), false);
			ISecurePreferences connections = preferences.node(com.siteview.ecc.rcp.cnf.ui.SecureLoginDialog.SAVED);

			for (Iterator it = this.newServers.iterator(); it.hasNext();) {
				String name = (String) it.next();
//				MonitorServer d = (MonitorServer) this.newServers.get(name);
//				ISecurePreferences connection = connections.node(name);				
//				connection.put(SERVER, d.getHost(), false);
//				connection.put(PASSWORD, d.getPassword(), true);
			}
			preferences.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
