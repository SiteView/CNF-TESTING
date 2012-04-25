package com.siteview.ecc.rcp.cnf.data;

import java.util.HashMap;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import COM.dragonflow.SiteView.MonitorGroup;

public class AdapterFactory implements IAdapterFactory {


	private IWorkbenchAdapter monitorServerManagerAdapter = new MonitorServerManagerAdapter();	
	private IWorkbenchAdapter monitorServerAdapter = new MonitorServerAdapter();	
	private IWorkbenchAdapter monitorGroupAdapter = new MonitorGroupAdapter();	
//	private IWorkbenchAdapter monitorAdapter = new MonitorAdapter();	
	private IWorkbenchAdapter monitorAdapter = new MonitorAdapter();


	/* (non-Javadoc)
	* @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	*/
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof MonitorServerManager)
			return monitorServerManagerAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof MonitorServer)
			return monitorServerAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof MonitorGroup)
			return monitorGroupAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof HashMap)
			return monitorAdapter;
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}
}
