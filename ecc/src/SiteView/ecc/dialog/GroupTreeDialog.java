package SiteView.ecc.dialog;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
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
import SiteView.ecc.bundle.EditGroupBundle;
import SiteView.ecc.data.MonitorServer;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;
/**
 * 
 * @author Administrator
 *
 */
public class GroupTreeDialog extends Dialog {
	public static TreeItem group;
	private String title = "移动组";
	public static Map<String,Boolean> map=new HashMap<String,Boolean>();
	/*
	 * 数据
	 */
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
			List<Map<String, Object>> groups = EccTreeControl.groups_0;
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

	@SuppressWarnings("unchecked")
	protected void buttonPressed(int buttonId) {
		if(buttonId==IDialogConstants.OK_ID){
			BusinessObject bo=EccTreeControl.CreateBo("RecId",EccTreeControl.s, "EccGroup");
			map.put(EccTreeControl.s, true);
			String oldParentId=bo.GetField("ParentGroupId").get_NativeValue().toString();
			String parentId="";
			EditGroupBundle edit=new EditGroupBundle();
			Map<String,Object> subgroup=EccTreeControl.groups.get(EccTreeControl.s);
			if(group.getText().equals("root")){
				bo.GetField("ParentGroupId").SetValue(new SiteviewValue(""));
				bo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
				EccTreeControl.groups_0.add(subgroup);
			}else{
				parentId=((Map<String,Object>)group.getData()).get("_id").toString();
				parentId=parentId.substring(parentId.indexOf("/")+1);
			}
			if(oldParentId!=null&&!oldParentId.equals("")){
				List<Map<String,Object>> subgroups=EccTreeControl.groups_subgroups.get(oldParentId);
				if(subgroups.size()==1){
					BusinessObject oldParentbo=EccTreeControl.CreateBo("RecId",oldParentId, "EccGroup");
					oldParentbo.GetField("HasSubGroup").SetValue(new SiteviewValue("false"));
					oldParentbo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
				}
				subgroups.remove(subgroup);
				EccTreeControl.groups_subgroups.put(oldParentId, subgroups);
			}else{
				EccTreeControl.groups_0.remove(subgroup);
			}
			if(!parentId.equals("")){
				bo.GetField("ParentGroupId").SetValue(new SiteviewValue(parentId));
				bo.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
				List<Map<String,Object>> subgroups=EccTreeControl.groups_subgroups.get(parentId);
				if(subgroups==null||subgroups.size()==0){
					BusinessObject bo1=EccTreeControl.CreateBo("RecId",parentId, "EccGroup");
					bo1.GetField("HasSubGroup").SetValue(new SiteviewValue("true"));
					bo1.SaveObject(ConnectionBroker.get_SiteviewApi(), false, true);
					subgroups=new ArrayList<Map<String,Object>>();
				}
				subgroups.add(subgroup);
				EccTreeControl.groups_subgroups.put(parentId, subgroups);
			}
			if(oldParentId!=null&&!oldParentId.equals("")){
				edit.updateGroup("GroupId="+oldParentId);
			}
			if(!parentId.equals("")){
				edit.updateGroup("GroupId="+parentId);
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
		String ss=map.get("_id").toString();
		if(!ss.equals("SiteView/"+EccTreeControl.s)){
			List<Map<String, Object>> list=EccTreeControl.groups_subgroups.get(ss.substring(ss.lastIndexOf("/")+1));
			if (list!=null&&list.size()!= 0) {
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i)!=null){
						getTreeItem(tree, list.get(i));	
					}
				}
			}
		}
	}
}
