package com.siteview.ecc.rcp.cnf.provider;

import org.eclipse.ui.navigator.CommonNavigator;

import com.siteview.ecc.rcp.cnf.data.MonitorServer;
import com.siteview.ecc.rcp.cnf.data.MonitorServerManager;


/**
 * @author Dragonflow
 * @version $Id$
 */
public class MonitorExplorer extends CommonNavigator
{
	private MonitorServer server;
    protected Object getInitialInput()
    {
    	server = new MonitorServer();
//    	return server.getRoot();
        return new MonitorServerManager();
    }
}
