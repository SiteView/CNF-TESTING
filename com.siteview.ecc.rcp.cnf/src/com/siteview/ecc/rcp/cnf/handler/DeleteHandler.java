package com.siteview.ecc.rcp.cnf.handler;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.siteview.ecc.rcp.cnf.data.MonitorServer;
import com.siteview.ecc.rcp.cnf.data.MonitorServerManager;

import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

/**
 * @author 
 * @version $Id$
 */
public class DeleteHandler extends AbstractHandler
{

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        System.out.println("Rename " + currentSelection);
        
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) currentSelection;
			delete(ss);
		}
        return null;
    }
    
    void delete (IStructuredSelection selection) {
    	if (selection.size() == 1) {
    		if (selection.getFirstElement() instanceof MonitorGroup) {
    			MonitorGroup mg = (MonitorGroup) selection.getFirstElement() ;
            	String hostname = mg.getHostname();
            	MonitorServer server  = (MonitorServer) MonitorServerManager.getInstance().getServers().get(hostname);
            	String groupid = mg.getProperty("_name");

            	try {
					server.getRmiServer().deleteGroup(groupid);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (SiteViewException e) {
					e.printStackTrace();
				}     			
            	
    		} else if (selection.getFirstElement() instanceof HashMap) {
    			
    		}
    		
    	}
    }

}
