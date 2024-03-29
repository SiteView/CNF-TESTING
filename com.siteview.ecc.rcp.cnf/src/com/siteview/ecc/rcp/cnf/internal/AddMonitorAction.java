
package com.siteview.ecc.rcp.cnf.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import COM.dragonflow.SiteView.MonitorGroup;

import com.siteview.ecc.rcp.cnf.ui.IImageKeys;

public class AddMonitorAction extends Action implements ISelectionListener,
		ActionFactory.IWorkbenchAction {
	private final IWorkbenchWindow window;

	public final static String ID = "com.siteview.ecc.rcp.cnf.addMonitor";

	private IStructuredSelection selection;

	public AddMonitorAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(ID);
		setText("&Add Monitor...");
		setToolTipText("Add a monitor to group.");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Application.PLUGIN_ID, IImageKeys.ADD_CONTACT));
		window.getSelectionService().addSelectionListener(this);
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		// Selection containing elements
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			setEnabled(selection.size() == 1
					&& selection.getFirstElement() instanceof MonitorGroup);
		} else {
			// Other selections, for example containing text or of other kinds.
			setEnabled(false);
		}
	}

	public void run() {
		AddMonitorDialog d = new AddMonitorDialog(window.getShell());
		int code = d.open();
		MonitorGroup group = (MonitorGroup) selection.getFirstElement();

	}

}
