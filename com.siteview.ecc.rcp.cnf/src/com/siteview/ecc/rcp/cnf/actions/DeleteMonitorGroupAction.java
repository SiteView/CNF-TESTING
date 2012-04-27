package com.siteview.ecc.rcp.cnf.actions;

import java.rmi.RemoteException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

import com.siteview.ecc.rcp.cnf.data.MonitorServer;
import com.siteview.ecc.rcp.cnf.data.MonitorServerManager;

/**
 * An action that can delete a monitor group from system. 
 * 
 * @since 1.0.0.0
 */
public class DeleteMonitorGroupAction extends ActionDelegate {

	public DeleteMonitorGroupAction() {
	}
	
	private IStructuredSelection selection = StructuredSelection.EMPTY;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		if(sel instanceof IStructuredSelection)
			selection = (IStructuredSelection) sel;
		else 
			selection = StructuredSelection.EMPTY;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		if(selection.size() == 1) {
			
			 Object firstElement = selection.getFirstElement();
			 if(firstElement instanceof MonitorGroup) {
				 MonitorGroup data = (MonitorGroup) firstElement;

				 // obtaining the server
			    MonitorGroup mg = (MonitorGroup) selection.getFirstElement() ;
			    String hostname = mg.getHostname();
			    MonitorServer server  = (MonitorServer) MonitorServerManager.getInstance().getServers().get(hostname);
			    String groupid = mg.getProperty("_name");
						 
				// delete the group 
				try {//FIXME: delete based on full path, not the name
					server.getRmiServer().deleteGroup(groupid);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SiteViewException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
		
	}

}
