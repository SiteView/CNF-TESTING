package com.siteview.ecc.workbench.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CommitHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public CommitHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Workbench",
				"Hello, SiteView world");
		
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        System.out.println("Select " + currentSelection);
        
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) currentSelection;
			submit(ss);
		}

		return null;
		
		
	}

	private void submit(IStructuredSelection selection) {
		if (selection.size() == 1) {
    		if (selection.getFirstElement() instanceof IProject) {
    			System.out.println("seletion is project");
    			
    		}
		
		}
	}
}
