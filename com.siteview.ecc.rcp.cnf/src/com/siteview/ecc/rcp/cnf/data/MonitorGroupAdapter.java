/**
* The monitor group object adapter
* <p>
* The monitoring can be distributed to different servers, each have a root node 
* The UI can connect to different monitor servers at the same time and showing each  
* in a separate tree node
* </p>
* @see MonitorGroup
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

import COM.dragonflow.SiteView.AtomicMonitor;
import COM.dragonflow.SiteView.Monitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteView.SiteViewObject;
import COM.dragonflow.SiteViewException.SiteViewException;
import COM.dragonflow.Utils.jglUtils;

import com.siteview.ecc.rcp.cnf.internal.Application;
import com.siteview.ecc.rcp.cnf.ui.IImageKeys;

public class MonitorGroupAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2,IWorkbenchAdapter3 {
	
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

	//return the MonitorServer upon which the monitor group is run.
	public Object getParent(Object o) {
		MonitorGroup mg = (MonitorGroup) o;		
		return MonitorServerManager.getInstance().getServers().get(mg.getHostname());
	}
	
	public String getLabel(Object parentElement) {
		if (parentElement instanceof MonitorGroup) {
			MonitorGroup monitorGroup = ((MonitorGroup) parentElement);
			int total = getNumMonitors(monitorGroup);
			return monitorGroup.getProperty("_name") + "(" + total + ")";
		} else if (parentElement instanceof Monitor) {
			Monitor monitor = (Monitor) parentElement;
//			return (String) monitor.get("_name");
			return "ping1";
		} else {
			return "default";
		} 
			
		
		//FIXME: get:  error/warning/ok counts			
	}
	
	private int getNumMonitors(MonitorGroup monitorGroup) {
		int total = 0;
//		server.getRmiServer()

		return total;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.GROUP);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof MonitorGroup)
        {
            try {
            	List<Object> mgobjList = new  ArrayList<Object>(); 
            	
            	//the MonitorServer upon which the monitor group is run.
            	String hostname = ((MonitorGroup)parentElement).getHostname();
            	MonitorServer server  = (MonitorServer) MonitorServerManager.getInstance().getServers().get(hostname);
            	//obtain the groupid
            	String groupid = ((MonitorGroup)parentElement).getProperty("_name");

            	List<Map<String, Object>> subGroupList = server.getRmiServer().getChildGroupInstances(groupid); 
            	for(Map<String, Object> mg:subGroupList) {
    			  mg.put("_class","MonitorGroup");
    			  mgobjList.add((MonitorGroup)SiteViewObject.createObject(jglUtils.toJgl(mg)));
            	}

            	List<Map<String, Object>> monitorList =  server.getRmiServer().getMonitorsForGroup(groupid);
            	for(Map<String, Object> monitor:monitorList) {
//            	  Class mon = Class.forName("COM.dragonflow.StandardMonitor.TestMonitor");	
//            	  SiteViewObject siteviewobject = (SiteViewObject) mon.newInstance();
//    			  monitor.put("_class","TestMonitor");
//    			  Monitor m = (Monitor) SiteViewObject.createObject(jglUtils.toJgl(monitor));
    			  mgobjList.add(monitor);
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
        } else  if (parentElement instanceof AtomicMonitor) {
        	//obtain the groupid, then monitorserver
        	MonitorGroup mg = (MonitorGroup) ((AtomicMonitor) parentElement).getParentGroup();        	
        	String hostname = mg.getHostname();
        	MonitorServer server  = (MonitorServer) MonitorServerManager.getInstance().getServers().get(hostname);
        	String groupid = ((MonitorGroup)parentElement).getProperty("_name");
        	
            return EMPTY_ARRAY;
        } else {
        	return  EMPTY_ARRAY;
        }
		
	}

}
