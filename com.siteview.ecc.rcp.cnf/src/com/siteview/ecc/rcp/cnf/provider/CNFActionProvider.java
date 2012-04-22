package com.siteview.ecc.rcp.cnf.provider;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import COM.dragonflow.SiteView.Monitor;

/**
 * @author Dragonflow
 * @version $Id$
 */
public class CNFActionProvider extends CommonActionProvider
{

    private OpenChildAction openAction;


    public CNFActionProvider()
    {

    }

    @Override
    public void init(ICommonActionExtensionSite site)
    {
        ICommonViewerSite viewSite = site.getViewSite();
        if (viewSite instanceof ICommonViewerWorkbenchSite) 
        {
            ICommonViewerWorkbenchSite workbenchSite = (ICommonViewerWorkbenchSite) viewSite;
            openAction = new OpenChildAction(workbenchSite.getPage(), workbenchSite.getSelectionProvider());
        }
    }
    
    
    

    @Override
    public void restoreState(IMemento memento)
    {
        super.restoreState(memento);
    }

    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
    }

    @Override
    public void fillActionBars(IActionBars actionBars)
    {
        if (openAction.isEnabled())
        {
            actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
        }
    }
    
    @Override
    public void fillContextMenu(IMenuManager menu)
    {
        if (openAction.isEnabled())
        {
            menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openAction);
        }
    }

    
    /**
     * 
     * 
     * 
     */
    class OpenChildAction extends Action
    {
        private ISelectionProvider provider;
        private Monitor data;

        public OpenChildAction(IWorkbenchPage workbenchPage, ISelectionProvider selectionProvider)
        {
            super("Open item");
            provider = selectionProvider;
        }
        

        @Override
        public void run()
        {
            
            if (data != null)
            {
                System.out.println("Run on " + data.getProperty("_name"));
            } 
            super.run();
        }

        @Override
        public boolean isEnabled()
        {
            ISelection selection = provider.getSelection();
            if (!selection.isEmpty())
            {
                IStructuredSelection sSelection = (IStructuredSelection) selection;
                if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof Monitor) 
                {
                    data = (Monitor)sSelection.getFirstElement();
                    return true;
                }
            }
            return false;
        }

        
        
    }


}
