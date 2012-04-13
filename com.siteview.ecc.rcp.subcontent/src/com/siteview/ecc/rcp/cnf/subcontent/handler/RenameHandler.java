package com.siteview.ecc.rcp.cnf.subcontent.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Dragonflow
 * @version $Id$
 */
public class RenameHandler extends AbstractHandler
{

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        System.out.println("Rename " + currentSelection);
        
        return null;
    }

}
