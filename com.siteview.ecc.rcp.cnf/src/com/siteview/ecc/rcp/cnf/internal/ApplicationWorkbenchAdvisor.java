package com.siteview.ecc.rcp.cnf.internal;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.siteview.ecc.rcp.cnf.ui.Perspective;

/**
 * Workbench advisor
 * @author Dragonflow
 * @version $Id$
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{

    public void initialize(IWorkbenchConfigurer configurer)
    {
    }

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    public String getInitialWindowPerspectiveId()
    {
        return Perspective.PERSPECTIVE_ID;
    }

}
