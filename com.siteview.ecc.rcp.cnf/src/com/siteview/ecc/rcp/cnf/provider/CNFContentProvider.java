package com.siteview.ecc.rcp.cnf.provider;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import COM.dragonflow.Api.APIGroup;
import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.Api.APIMonitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;

import com.siteview.ecc.rcp.cnf.data.Child;
import com.siteview.ecc.rcp.cnf.data.Parent;
import com.siteview.ecc.rcp.cnf.data.Root;

public class CNFContentProvider implements ITreeContentProvider
{

    private static final Object[] EMPTY_ARRAY = new Object[0];
    private APIGroup apigroup;
    private  APIInterfaces rmiServer;
    private  Registry registry;
    private  String serverAddress= "localhost";
    private  String serverPort="3232";

    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof Root)
        {
            if (rmiServer == null) {
      		  try {
				registry=LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
	  		  	rmiServer=(APIInterfaces)(registry.lookup("kernelRmiServer"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
            }

            try {
            	ArrayList<Map<String, Object>> groups = rmiServer.getTopLevelGroupInstances();
				return groups.toArray();
			} catch (SiteViewException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			} catch (RemoteException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			}
            
        } else if (parentElement instanceof ArrayList)
        {
        	try {
				return apigroup.getChildGroupInstances(((MonitorGroup) parentElement).getProperty("_id")).toArray();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (SiteViewException e) {
				e.printStackTrace();
			}
        	return EMPTY_ARRAY;
//            return ((Parent) parentElement).getChildren();
        } else if (parentElement instanceof APIMonitor)
        {
            return EMPTY_ARRAY;
        } else
        {
            return EMPTY_ARRAY;
        }
    }

    public Object getParent(Object element)
    {
        if (element instanceof Child)
        {
            return ((Child) element).getParent();
        } else if (element instanceof Parent)
        {
            return ((Parent) element).getRoot();
        }
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return (element instanceof Root || element instanceof Parent);
    }

    public Object[] getElements(Object inputElement)
    {
        return getChildren(inputElement);
    }

    public void dispose()
    {
        this.apigroup = null;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    { /* ... */
    }

}
