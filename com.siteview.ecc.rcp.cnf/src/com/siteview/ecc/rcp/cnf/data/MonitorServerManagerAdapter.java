/**
* The monitor server object adapter
* <p>
* The monitoring can be distributed to different servers, each have a root node 
* The UI can connect to different monitor servers at the same time and showing each  
* in a separate tree node
* </p>
* @see MonitorServerManager
* @see MonitorServer
* @see MonitorConnectionDetails
*/	

package com.siteview.ecc.rcp.cnf.data;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter3;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

import com.siteview.ecc.rcp.cnf.internal.Application;
import com.siteview.ecc.rcp.cnf.ui.IImageKeys;

public class MonitorServerManagerAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2,IWorkbenchAdapter3 {
	
	private static final Object[] EMPTY_ARRAY = new Object[0];
	private List monitorServers = new ArrayList();
	private List monitorGroups= new ArrayList();
    private List monitors = new ArrayList();

	@Override
	public StyledString getStyledText(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RGB getForeground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RGB getBackground(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontData getFont(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getParent(Object o) {
		return MonitorServerManager.getInstance().getServers();
	}
	
	public String getLabel(Object o) {
		MonitorServerManager serverManager = ((MonitorServerManager) o);
		return serverManager.getName(); 
	}
	
	private int getNumMonitors(MonitorServer server) {
		int total = 0;
		return total;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.GROUP);
	}

	public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof MonitorServerManager)
        {
            if (monitorServers.size() == 0)
            {
            	Map<String,Object> serverList = MonitorServerManager.getInstance().getServers();
            	for (Object s : serverList.values()) {
					monitorServers.add(s);
				}                
            }
            return monitorServers.toArray();
        } else if (parentElement instanceof MonitorServer)
        {
            try {
				return ((MonitorServer) parentElement).getRmiServer().getTopLevelGroupInstances().toArray();
			} catch (RemoteException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			} catch (SiteViewException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			}
        } else if (parentElement instanceof MonitorGroup)
        {
            return EMPTY_ARRAY;
        } else
        {
            return EMPTY_ARRAY;
        }

	}

}
