package com.siteview.ecc.rcp.cnf.provider;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import COM.dragonflow.SiteView.MonitorGroup;

/**
 * @author Dragonflow
 * @version $Id$
 */
public class CNFFilter extends ViewerFilter
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if (element instanceof MonitorGroup) 
        {
        	MonitorGroup mg = (MonitorGroup) element;
//            return (mg.getProperty("_name").equals("test"));
        	return true;
        } else {
            return true;
        }
    }

}
