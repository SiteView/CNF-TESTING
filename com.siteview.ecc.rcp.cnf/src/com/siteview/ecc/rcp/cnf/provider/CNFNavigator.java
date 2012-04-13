package com.siteview.ecc.rcp.cnf.provider;

import org.eclipse.ui.navigator.CommonNavigator;


/**
 * @author Dragonflow
 * @version $Id$
 */
public class CNFNavigator extends CommonNavigator
{
    protected Object getInitialInput()
    {
        return new com.siteview.ecc.rcp.cnf.data.Root();
    }
}
