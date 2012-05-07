package core.monitor.counter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;


import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;

import COM.dragonflow.Api.APIInterfaces;
import COM.dragonflow.SiteViewException.SiteViewException;
import Core.ui.method.GetIeditorInput;

public class CounterDlg extends Dialog {

	private Tree tree;
	private Map<String, String> counters;
	private ISiteviewApi m_api;
	private BusinessObject busob = null;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public CounterDlg(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		container.setLayout(new FillLayout());

		tree = new Tree(container, SWT.CHECK);
	

		initCounters();

		return container;
	}
	
	/**
	 * init Monitor Counters
	 */
	public void initCounters() {
		APIInterfaces rmiServer;
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());

			rmiServer = (APIInterfaces) (registry.lookup("kernelApiRmiServer"));
			//Get SiteviewApi
			this.m_api = ConnectionBroker.get_SiteviewApi();
			//Get all Object
			Object[] object = GetIeditorInput.getIeditorInput(m_api);
			//Get BusinessObject
			this.busob = (BusinessObject) object[1];
			String monitortype = busob.GetField("EccType").get_NativeValue().toString();
			String hostname = "";
			if (monitortype.equals("SQLServerMonitor")) {//Get sqlserver monitor hostname
				hostname = busob.GetField("SQLHostName").get_NativeValue().toString();
			}
			System.out.println("The hostname is : "+hostname+" and the monitortype is : "+monitortype);
			//Call siteview9.2 api and return monitor counter string.
			String returnstr =  rmiServer.getMonitorCounters(hostname, monitortype);
			String[] str = returnstr.split(",");
			for (String s: str) {
				new TreeItem(tree, SWT.NONE).setText(s);
			}
			
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println(e);
		} catch (SiteViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(453, 360);
	}

	public Map<String, String> getSelectedCounters() {

		return this.counters;
	}

	private void getSelectedCounter(Map<String, String> map, TreeItem tree) {
		for (int i = 0; i < tree.getItemCount(); i++) {
			if (tree.getItem(i).getChecked()) {
				map.put(tree.getItem(i).getText(), tree.getItem(i).getText());
			}
			getSelectedCounter(map, tree.getItem(i));
		}
	}

	@Override
	protected void okPressed() {
		counters = new HashMap<String, String>();
		for (int i = 0; i < tree.getItemCount(); i++) {
			if (tree.getItem(i).getChecked()) {
				counters.put(tree.getItem(i).getText(), tree.getItem(i)
						.getText());
			}
			getSelectedCounter(counters, tree.getItem(i));
		}
		super.okPressed();
	}


}
