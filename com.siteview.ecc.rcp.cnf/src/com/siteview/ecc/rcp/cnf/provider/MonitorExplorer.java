package com.siteview.ecc.rcp.cnf.provider;

import org.eclipse.ui.navigator.CommonNavigator;

import com.siteview.ecc.rcp.cnf.data.MonitorSession;


/**
 * @author Dragonflow
 * @version $Id$
 */
public class MonitorExplorer extends CommonNavigator
{
	private MonitorSession session;
    protected Object getInitialInput()
    {
    	session = new MonitorSession();
//    	return session.getRoot();
        return new com.siteview.ecc.rcp.cnf.data.Root();
    }
}
