package core.monitor.counter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
			// Get SiteviewApi
			this.m_api = ConnectionBroker.get_SiteviewApi();
			// Get all Object
			Object[] object = GetIeditorInput.getIeditorInput(m_api);
			// Get BusinessObject
			this.busob = (BusinessObject) object[1];
			String rcptype = busob.GetField("EccType").get_NativeValue()
					.toString();
			String monitortype = this.getMonitorType(rcptype);
			if (monitortype == null) {
				monitortype = rcptype;
			}
			String rcphostnamefiled = this.getMonitorHostName(monitortype);
			// Get sqlserver monitor hostname
			String hostname = busob.GetField(rcphostnamefiled)
					.get_NativeValue().toString();
			System.out.println("The hostname is : " + hostname
					+ " and the monitortype is : " + monitortype);
			Map parmsmap = new HashMap();
			parmsmap.put("monitortype", monitortype);
			// Call siteview9.2 api and return monitor counter string.
			if (monitortype.equals("OracleJDBCMonitor")) {
				String oracleuserurl = busob.GetField("DatabaseConnectionURL")
						.get_NativeValue().toString();
				String oracleusername = busob.GetField("DatabaseUserName")
						.get_NativeValue().toString();
				String oracleuserpwd = busob.GetField("DatabasePassword")
						.get_NativeValue().toString();
				String oracleuserdriver = busob.GetField("DatabaseDriver")
						.get_NativeValue().toString();
				String connectiontimeout = busob.GetField("ConnectionTimeout")
						.get_NativeValue().toString();
				String querytimeout = busob.GetField("QueryTimeout")
						.get_NativeValue().toString();
				parmsmap.put("oracleuserurl", oracleuserurl);
				parmsmap.put("oracleusername", oracleusername);
				parmsmap.put("oracleuserpwd", oracleuserpwd);
				parmsmap.put("oracleuserdriver", oracleuserdriver);
				parmsmap.put(
						"connectiontimeout",
						connectiontimeout.substring(0,
								connectiontimeout.indexOf(".")));
				parmsmap.put("querytimeout",
						querytimeout.substring(0, querytimeout.indexOf(".")));
			} else if (monitortype.equals("InterfaceMonitor")) {
				String serverInterface = busob.GetField("ServerInterface")
						.get_NativeValue().toString();
				String SNMPVersionInterface = busob
						.GetField("SNMPVersionInterface").get_NativeValue()
						.toString();
				String InterfaceSNMPPublic = busob
						.GetField("InterfaceSNMPPublic").get_NativeValue()
						.toString();
				String TimeOutInterface = busob.GetField("TimeOutInterface")
						.get_NativeValue().toString();
				String PortInterface = busob.GetField("PortInterface")
						.get_NativeValue().toString();
				parmsmap.put("serverInterface", serverInterface);
				parmsmap.put("SNMPVersionInterface", SNMPVersionInterface);
				parmsmap.put("InterfaceSNMPPublic", InterfaceSNMPPublic);
				parmsmap.put("PortInterface", PortInterface);
				parmsmap.put(
						"TimeOutInterface",
						TimeOutInterface.substring(0,
								TimeOutInterface.indexOf(".")));
			} else {
				parmsmap.put("hostname", hostname);
			}
			String returnstr = rmiServer.getMonitorCounters(parmsmap);
			String[] str = returnstr.split(",");
			for (String s : str) {
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

	private static String getMonitorType(String monitorType) {
		String filePath;
		String RootFilePath = System.getProperty("user.dir");
		filePath = RootFilePath + "\\itsm_siteview9.2.properties";
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			String value = props.getProperty(monitorType);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getMonitorHostName(String monitorType) {
		String filePath;
		String RootFilePath = System.getProperty("user.dir");
		filePath = RootFilePath + "\\itsm_rcphostfiled.properties";
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					filePath));
			props.load(in);
			String value = props.getProperty(monitorType);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
