/**
* The monitor object adapter
* <p>
* The monitoring can be distributed to different servers, each have a root node 
* The UI can connect to different monitor servers at the same time and showing each  
* in a separate tree node
* </p>
* @see Monitor
*  
*/	

package com.siteview.ecc.rcp.cnf.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter3;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import COM.dragonflow.SiteView.Monitor;

import com.siteview.ecc.rcp.cnf.internal.Application;
import com.siteview.ecc.rcp.cnf.ui.IImageKeys;

public class MonitorAdapter implements IWorkbenchAdapter, IWorkbenchAdapter2,IWorkbenchAdapter3 {

	private static final Object[] EMPTY_ARRAY = new Object[0];
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
		Map monitor= (HashMap) o;
		return (String)monitor.get("_name");		
	}
	
	public ImageDescriptor getImageDescriptor(Object object) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.GROUP);
	}

	public Object[] getChildren(Object o) {
		return EMPTY_ARRAY;
		
	}

}
