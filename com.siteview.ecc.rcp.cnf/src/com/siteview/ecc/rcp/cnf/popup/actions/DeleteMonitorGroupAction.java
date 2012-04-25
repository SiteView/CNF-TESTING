package com.siteview.ecc.rcp.cnf.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import com.siteview.ecc.rcp.cnf.data.MonitorServer;
import com.siteview.ecc.rcp.cnf.data.MonitorServerManager;

import COM.dragonflow.SiteView.MonitorGroup;

/**
 * An  action that can delete a PropertiesTreeData item from a property file. 
 * 
 * @since 3.2
 */
public class DeleteMonitorGroupAction extends ActionDelegate {
	
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
		  
		WorkspaceModifyOperation deletePropertyOperation = new WorkspaceModifyOperation() {
			/* (non-Javadoc)
			 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
			 */
			protected void execute(IProgressMonitor monitor) throws CoreException {
				// In production code, you should always externalize strings, but this is an example.
				monitor.beginTask("Deleting monitor group", 5); //$NON-NLS-1$ 

					if(selection.size() == 1) {
						
						 Object firstElement = selection.getFirstElement();
						 if(firstElement instanceof MonitorGroup) {
							 MonitorGroup data = (MonitorGroup) firstElement;
							 
							 monitor.worked(1);
									// obtaining the server
						    		 MonitorGroup mg = (MonitorGroup) selection.getFirstElement() ;
						             String hostname = mg.getHostname();
						             MonitorServer server  = (MonitorServer) MonitorServerManager.getInstance().getServers().get(hostname);
						             String groupid = mg.getProperty("_name");
									 monitor.worked(1);  
									 
									 // delete the group 
									 server.getRmiServer().deleteGroup(groupid);
									 monitor.worked(1);
									
				
									 monitor.done();
						 }

				}
			}
		};
		
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, false, deletePropertyOperation);
		} catch (InvocationTargetException e) { 
			// handle error gracefully			
//			Activator.logError(0, "Could not delete property!", e); //$NON-NLS-1$
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
	 				"Error Deleting Property",  //$NON-NLS-1$
	 				"Could not delete property!");   //$NON-NLS-1$
		} catch (InterruptedException e) {
			 // handle error gracefully
//			Activator.logError(0, "Could not delete property!", e); //$NON-NLS-1$
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
	 				"Error Deleting Property",  //$NON-NLS-1$
	 				"Could not delete property!");   //$NON-NLS-1$
		}
		 
	}
}
