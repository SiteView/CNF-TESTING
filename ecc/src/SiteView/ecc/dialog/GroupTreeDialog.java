package SiteView.ecc.dialog;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.data.MonitorServer;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

public class GroupTreeDialog extends Dialog {
	public static TreeItem group;
	private String title = "移动组";
	/*
	 * 数据
	 */
	private MonitorServer monitorServer;

	public GroupTreeDialog(Shell Shell) {
		super(Shell);
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
				group = tree.getSelection()[0];
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		TreeItem trtmNewTreeitem = new TreeItem(tree, SWT.NONE);
		trtmNewTreeitem.setText("root");
		trtmNewTreeitem.setExpanded(true);
		try {
			monitorServer = new MonitorServer();
			List<Map<String, Object>> groups = monitorServer.Group();
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
		//Button button = super.createButton((Composite) super.getButtonBar(),
		//		100, "无依赖", false);
	}

	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
			String parentId=((Map<String,Object>)group.getData()).get("_id").toString();
			parentId=parentId.substring(parentId.indexOf("/")+1);
			BusinessObject bo=EccTreeControl.CreateBo(EccTreeControl.s, "EccGroup");
			String oldParentId=bo.GetField("ParentGroupId").get_NativeValue().toString();
			if(oldParentId!=null&&!oldParentId.equals("")){
				try {
					if(monitorServer.GroupChild(oldParentId).size()==1){
						BusinessObject oldParentbo=EccTreeControl.CreateBo(oldParentId, "EccGroup");
						oldParentbo.GetField("HasSubGroup").SetValue(new SiteviewValue("false"));
						oldParentbo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SiteViewException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			bo.GetField("ParentGroupId").SetValue(new SiteviewValue(parentId));
			bo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
			BusinessObject bo1=EccTreeControl.CreateBo(parentId, "EccGroup");
			 
			String s=bo1.GetField("HasSubGroup").get_NativeValue().toString();
			if(!s.equals("true")){
				bo1.GetField("HasSubGroup").SetValue(new SiteviewValue("true"));
				bo1.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
			}
			
		}
		super.buttonPressed(buttonId);
	}

	public void getTreeItem(TreeItem treeItem, Map<String, Object> map)
			throws RemoteException, SiteViewException {
		TreeItem tree = new TreeItem(treeItem, SWT.NONE);
		tree.setText(map.get("_name").toString());
		tree.setData(map);
		tree.setExpanded(true);
//		List<Map<String, Object>> list = monitorServer.getMonitorsForGroup(map.get("_id").toString());
//		list.addAll(monitorServer.GroupChild(map.get("_id").toString()));
		List<Map<String, Object>> list=monitorServer.GroupChild(map.get("_id").toString());
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				getTreeItem(tree, list.get(i));
			}
		}
	}
}
