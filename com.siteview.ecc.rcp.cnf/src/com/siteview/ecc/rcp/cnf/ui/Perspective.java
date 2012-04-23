package com.siteview.ecc.rcp.cnf.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.siteview.ecc.rcp.cnf.provider.MonitorExplorer;

public class Perspective implements IPerspectiveFactory
{

    public static final String PERSPECTIVE_ID = "com.siteview.ecc.rcp.cnf.perspective";

    public void createInitialLayout(IPageLayout layout)
    {
    	layout.setEditorAreaVisible(false);
    	layout.setEditorAreaVisible(true);
//        layout.setFixed(true);
//        layout.addStandaloneView(MonitorExplorer.ID, false,IPageLayout.LEFT,0.5f,layout.getEditorArea());
    }

}
