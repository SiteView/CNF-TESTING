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
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.jglUtils;

import com.siteview.ecc.rcp.cnf.internal.Application;
import com.siteview.ecc.rcp.cnf.ui.IImageKeys;

public class MonitorServerAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2,IWorkbenchAdapter3 {

	private static final Object[] EMPTY_ARRAY = new Object[0];
	private List monitorServers = new ArrayList();
	private List monitorGroups= new ArrayList();
	
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
		MonitorServer server = ((MonitorServer) o);
		int total = getNumMonitors(server);
		return  server.getAlias().isEmpty() || server.getAlias().equalsIgnoreCase(server.getHost()) ? 
					server.getHost() + ":"+ server.getPort() +"(" + total + ")" :
					server.getAlias() + "@" + server.getHost() + ":"+ server.getPort() +"(" + total + ")" ;	
		//TODO: get:  error/warning/ok counts			
	}
	
	private int getNumMonitors(MonitorServer server) {
		int total = 0;
//		server.getRmiServer()

		return total;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.GROUP);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MonitorServer)
        {
            try {
            	List<Map<String, Object>> mgList = ((MonitorServer) parentElement).getRmiServer().getTopLevelGroupInstances(); 
            	List<MonitorGroup> mgobjList = new  ArrayList<MonitorGroup>(); 
    		  
    		  for(Map<String, Object> mg:mgList) {
    			  mg.put("_class","MonitorGroup");
    			  mgobjList.add((MonitorGroup) SiteViewObject.createObject(jglUtils.toJgl(mg)));
    		  }
				return mgobjList.toArray();
			} catch (RemoteException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			} catch (SiteViewException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			} catch (InstantiationException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			}		
        } else  {
            return EMPTY_ARRAY;
        }
		
	}

}
