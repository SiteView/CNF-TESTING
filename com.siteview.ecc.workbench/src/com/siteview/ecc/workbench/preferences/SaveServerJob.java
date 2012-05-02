package com.siteview.ecc.workbench.preferences;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class SaveServerJob extends Job {

	public static final String NAME = "Saving Server...";

	public SaveServerJob() {
		super(NAME);
		setPriority(Job.SHORT);
		setRule(StorageRule.getInstance());
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	private List newServers;

	public List getNewServers() {
		return newServers;
	}

	public void setNewServers(List newServer) {
		this.newServers = newServer;
	}

}
