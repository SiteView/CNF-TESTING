package com.siteview.ecc.rcp.cnf.provider;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import COM.dragonflow.Api.APIGroup;
import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.Api.APIMonitor;
import COM.dragonflow.SiteView.MonitorGroup;
import COM.dragonflow.SiteViewException.SiteViewException;


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
        if (rmiServer == null) {
  		  try {
			registry=LocateRegistry.getRegistry(serverAddress,(new Integer(serverPort)).intValue());
  		  	rmiServer=(APIInterfaces)(registry.lookup("kernelApiRmiServer"));
  		  } catch (NumberFormatException e) {
			e.printStackTrace();
  		  } catch (RemoteException e) {
			e.printStackTrace();
  		  } catch (NotBoundException e) {
			e.printStackTrace();
  		  }
        }
        
    	if (parentElement instanceof Root)
        {
            try {

            	ArrayList<HashMap<String, String>> groups1 = rmiServer.getTopLevelAllowedGroupInstances();
            	ArrayList<HashMap<String, String>> groups = new ArrayList();
            	groups.addAll(groups1);
            	
            	//µÝ¹éÃ¶¾ÙÊ÷Êý¾Ý?
            	
            	for(HashMap<String, String> group:groups1)
            	{
            		String strId =  group.get("GroupID");
            		ArrayList<HashMap<String, String>> chldGroup = rmiServer.getChildGroupInstances(strId);
            		groups.addAll(chldGroup);
            	}
            	
            	ArrayList<HashMap<String, String>> monitors = new ArrayList();
            	for(HashMap<String, String> group:groups)
            	{
            		String strId =  group.get("GroupID");
            		ArrayList<HashMap<String, String>> chldMonitors = rmiServer.getChildMonitors(strId);
            		monitors.addAll(chldMonitors);
            	}
            	
            	groups.addAll(monitors);
//            	Parent[] parents = new Parent[groups.size()];

            	ArrayList<HashMap<String, String>> groups = rmiServer.getTopLevelAllowedGroupInstances();
				return groups.toArray();
			} catch (SiteViewException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			} catch (RemoteException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			}
            
        } else if (parentElement instanceof HashMap)
        {
        	try {
				return apigroup.getChildGroupInstances(((MonitorGroup) parentElement).getProperty("_id")).toArray();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (SiteViewException e) {
				e.printStackTrace();
			}
        	return EMPTY_ARRAY;
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

        return null;
    }

    public boolean hasChildren(Object element)
    {

        return true;
//    	return (element instanceof Root || element instanceof Parent);

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
