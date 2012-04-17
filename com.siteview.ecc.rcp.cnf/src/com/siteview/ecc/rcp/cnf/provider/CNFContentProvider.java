package com.siteview.ecc.rcp.cnf.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import COM.dragonflow.Api.APIGroup;
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

    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof Root)
        {
            if (apigroup == null) 
            	this.apigroup = new APIGroup();

            try {
				return apigroup.getTopLevelGroupInstances().toArray();
			} catch (SiteViewException e) {
				e.printStackTrace();
				return EMPTY_ARRAY;
			}
            
        } else if (parentElement instanceof MonitorGroup)
        {
        	try {
				return apigroup.getChildGroupInstances(((MonitorGroup) parentElement).getProperty("_id")).toArray();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SiteViewException e) {
				// TODO Auto-generated catch block
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
