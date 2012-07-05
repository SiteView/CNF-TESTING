package SiteView.ecc.dialog;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import siteview.windows.forms.ImageHelper;

import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.Activator;
import SiteView.ecc.data.MonitorServer;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewSecurityException;

public class GroupTreeDialog extends Dialog {
	public static TreeItem descriptionitem;
	private String title = "依赖关系";
	/*
	 * 数据
	 */
	private MonitorServer monitorServer;
	private EccTreeControl eccTreeControl;

	protected GroupTreeDialog(Shell Shell) {
		super(Shell);
		monitorServer = new MonitorServer();
		eccTreeControl = new EccTreeControl();
	}

	/*
	 * 初始化配置
	 */
	protected void configureShell(Shell newShell) {
		newShell.setSize(500, 500);
		newShell.setLocation(250, 150);
		newShell.setText(title);
		super.configureShell(newShell);
	}

	/*
	 * 创建面板元素
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new FillLayout());
		final Tree tree = new Tree(composite, SWT.BORDER);
		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				descriptionitem = tree.getSelection()[0];
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		TreeItem trtmNewTreeitem = new TreeItem(tree, SWT.NONE);
		trtmNewTreeitem.setText("root");
		trtmNewTreeitem.setExpanded(true);
		try {
			List groups = monitorServer.Group();
			for (int i = 0; i < groups.size(); i++) {
				Map<String, Object> maps = (Map<String, Object>) groups.get(i);
				getTreeItem(trtmNewTreeitem, maps);

			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (SiteViewException e) {
			e.printStackTrace();
		}
		return composite;
	}

	protected void initializeBounds() {
		super.getButton(IDialogConstants.OK_ID).setText("确定");
		super.getButton(IDialogConstants.CANCEL_ID).setText("取消");
		Button button = super.createButton((Composite) super.getButtonBar(),
				100, "无依赖", false);
	}

	protected void buttonPressed(int buttonId) {
		try {
			EditGroup.dependsOnitem = (Map<String, Object>) descriptionitem.getData();
		} catch (SiteviewSecurityException exception) {
		}
		super.buttonPressed(buttonId);
	}

	public void getTreeItem(TreeItem treeItem, Map<String, Object> map)
			throws RemoteException, SiteViewException {
		TreeItem tree = new TreeItem(treeItem, SWT.NONE);
		tree.setText(map.get("_name").toString());
		tree.setData(map);
		tree.setExpanded(true);
		List<Map<String, Object>> list = monitorServer.getMonitorsForGroup(map.get("_id").toString());
		list.addAll(monitorServer.GroupChild(map.get("_id").toString()));
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				getTreeItem(tree, list.get(i));
			}
		}
	}
}
