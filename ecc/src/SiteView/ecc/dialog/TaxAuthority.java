package SiteView.ecc.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import SiteView.ecc.Activator;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.data.SiteViewData;
import SiteView.ecc.data.UserInfor;
import SiteView.ecc.editors.UserManager;
import SiteView.ecc.tools.FileTools;
import SiteView.ecc.views.EccTreeControl;
import Siteview.SiteviewValue;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;

import siteview.windows.forms.ImageHelper;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

public class TaxAuthority extends Dialog {
	private String title = "用户授权";
	List<BusinessObject> bos;
	private Map<String, BusinessObject> userPermission;
	TreeItem item;
	TreeItem treeItem;
	TreeItem treeItem1;
	Combo combo;
	Button btnCheckButton;
	Button but1;
	Button but2;
	Button but3;
	Button but4;
	Button but5;
	Button but6;
	Button but7;
	Button but8;
	Button but9;
	Button but10;
	Button but11;
	Button but12;
	Button but13;
	Tree tree;
	Composite composite_2;
	Composite composite_5;
	Composite composite_6;
	Composite composite_7;
	boolean[] flag = new boolean[4];

	public TaxAuthority(Shell parent) {
		super(parent);
	}

	protected void configureShell(Shell newShell) {
		newShell.setSize(500, 500);
		newShell.setLocation(250, 150);
		newShell.setText(title);
		super.configureShell(newShell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new FillLayout());

		SashForm sashForm = new SashForm(composite, SWT.NONE);

		Composite composite_1 = new Composite(sashForm, SWT.VIRTUAL);
		composite_1.setLayout(new FillLayout());

		SashForm sashForm_1 = new SashForm(composite_1, SWT.VERTICAL);

		Composite composite_3 = new Composite(sashForm_1, SWT.NONE);

		btnCheckButton = new Button(composite_3, SWT.CHECK);
		btnCheckButton.setBounds(0, 0, 51, 20);
		btnCheckButton.setText("\u5168\u9009");// 全选
		btnCheckButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckButton.getSelection()) {
					TreeItem[] items = tree.getItems();
					for (TreeItem treeItem : items) {
						treeItem.setChecked(true);
						selectTree(treeItem);
					}
				} else {
					TreeItem[] items = tree.getItems();
					for (TreeItem treeItem : items) {
						treeItem.setChecked(false);
						removeTree(treeItem);
					}
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		combo = new Combo(composite_3, SWT.NONE);
		for (int i = 0; i < UserInfor.list.size(); i++) {
			String s = UserInfor.list.get(i).getLogname();
			combo.add(s);
			if (s.equals( ((SiteView.ecc.Modle.UserModle)UserManager.tableItem
					.getData()).getLogname())) {
				combo.select(i);
			}
		}
		combo.setBounds(112, 0, 92, 20);
		getPermissions("UserId", combo.getText());
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getPermissions("UserId", combo.getText());
				createTreeItem();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label label = new Label(composite_3, SWT.NONE);
		label.setBounds(70, 4, 36, 12);
		label.setText("\u7528\u6237\uFF1A");

		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		composite_4.setVisible(true);
		composite_4.setLayout(new FillLayout());

		sashForm_1.setWeights(new int[] { 11, 217 });

		ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(220);
		scrolledComposite.setMinHeight(500);

		SashForm sashForm_2 = new SashForm(scrolledComposite, SWT.VERTICAL);

		composite_2 = new Composite(sashForm_2, SWT.BORDER);
		composite_2.setToolTipText("SE权限");
		composite_2.setLayout(new FormLayout());

		but1 = new Button(composite_2, SWT.CHECK);
		FormData fd_btnCheckButton_1 = new FormData();
		fd_btnCheckButton_1.top = new FormAttachment(0, 10);
		fd_btnCheckButton_1.left = new FormAttachment(0, 10);
		but1.setLayoutData(fd_btnCheckButton_1);
		but1.setText("\u6DFB\u52A0\u7EC4");

		composite_5 = new Composite(sashForm_2, SWT.BORDER);
		composite_5.setToolTipText("组权限");

		but2 = new Button(composite_5, SWT.CHECK);
		but2.setBounds(10, 10, 93, 16);
		but2.setText("\u6DFB\u52A0\u5B50\u7EC4");

		but3 = new Button(composite_5, SWT.CHECK);
		but3.setBounds(10, 40, 93, 16);
		but3.setText("\u6DFB\u52A0\u8BBE\u5907");

		but4 = new Button(composite_5, SWT.CHECK);
		but4.setBounds(10, 69, 93, 16);
		but4.setText("\u7F16\u8F91\u7EC4");

		but5 = new Button(composite_5, SWT.CHECK);
		but5.setBounds(10, 101, 93, 16);
		but5.setText("\u5220\u9664\u7EC4");

		but6 = new Button(composite_5, SWT.CHECK);
		but6.setBounds(10, 131, 93, 16);
		but6.setText("\u6DFB\u52A0\u76D1\u6D4B\u5668");

		composite_6 = new Composite(sashForm_2, SWT.BORDER);

		but7 = new Button(composite_6, SWT.CHECK);
		but7.setBounds(10, 10, 93, 16);
		but7.setText("\u6DFB\u52A0\u76D1\u6D4B\u5668");

		but8 = new Button(composite_6, SWT.CHECK);
		but8.setBounds(10, 40, 93, 16);
		but8.setText("\u6DFB\u52A0\u8BBE\u5907");

		but9 = new Button(composite_6, SWT.CHECK);
		but9.setBounds(10, 69, 93, 16);
		but9.setText("\u7F16\u8F91\u8BBE\u5907");

		but10 = new Button(composite_6, SWT.CHECK);
		but10.setBounds(10, 101, 93, 16);
		but10.setText("\u5220\u9664\u8BBE\u5907");

		composite_7 = new Composite(sashForm_2, SWT.BORDER);
		composite_7.setToolTipText("监测器权限");

		but11 = new Button(composite_7, SWT.CHECK);
		but11.setBounds(10, 10, 93, 16);
		but11.setText("\u7F16\u8F91\u76D1\u6D4B\u5668");

		but12 = new Button(composite_7, SWT.CHECK);
		but12.setBounds(10, 44, 93, 16);
		but12.setText("\u5220\u9664\u76D1\u6D4B\u5668");

		but13 = new Button(composite_7, SWT.CHECK);
		but13.setBounds(10, 77, 93, 16);
		but13.setText("\u5237\u65B0\u76D1\u6D4B\u5668");

		tree = new Tree(composite_4, SWT.BORDER | SWT.CHECK);
		tree.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tree.setVisible(true);
		tree.setHeaderVisible(true);
		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				item = (TreeItem) e.item;
				if (item.getChecked()) {
					SelectParent(item);
					SelectChild(item);
					getPermissions("UserId", combo.getText());
					if (item.getData() == null) {
						selectionButton(false);
						flag[0] = true;
						flag[1] = false;
						flag[2] = false;
						flag[3] = false;
						but1.setSelection((Boolean) userPermission.get("SE")
								.GetField("AddGroup").get_NativeValue());
						Authority();
					} else if (item.getData() instanceof BusinessObject) {
						selectionButton(false);
						BusinessObject bo = (BusinessObject) item.getData();
						String macid = bo.get_RecId();
						flag[0] = false;
						flag[1] = false;
						flag[2] = true;
						flag[3] = true;
						if (userPermission.get(macid) != null) {
							but7.setSelection((Boolean) userPermission
									.get(macid).GetField("AddMonitor")
									.get_NativeValue());
							but8.setSelection((Boolean) userPermission
									.get(macid).GetField("AddMachine")
									.get_NativeValue());
							but9.setSelection((Boolean) userPermission
									.get(macid).GetField("EditMachine")
									.get_NativeValue());
							but10.setSelection((Boolean) userPermission
									.get(macid).GetField("DeleteMachine")
									.get_NativeValue());
							but11.setSelection((Boolean) userPermission
									.get(macid).GetField("EditMonitor")
									.get_NativeValue());
							but12.setSelection((Boolean) userPermission
									.get(macid).GetField("DeleteMonitor")
									.get_NativeValue());
						}
						Authority();
					} else {
						selectionButton(false);
						Map map = (Map) item.getData();
						String id = map.get("_id").toString();
						id = id.substring(id.lastIndexOf("/") + 1);
						flag[0] = false;
						flag[1] = true;
						flag[2] = false;
						flag[3] = true;
						if (userPermission.get(id) != null) {
							but2.setSelection((Boolean) userPermission.get(id)
									.GetField("AddGroup").get_NativeValue());
							but3.setSelection((Boolean) userPermission.get(id)
									.GetField("AddMachine").get_NativeValue());
							but4.setSelection((Boolean) userPermission.get(id)
									.GetField("EditGroup").get_NativeValue());
							but5.setSelection((Boolean) userPermission.get(id)
									.GetField("DeleteGroup").get_NativeValue());
							but6.setSelection((Boolean) userPermission.get(id)
									.GetField("AddMonitor").get_NativeValue());
							but11.setSelection((Boolean) userPermission.get(id)
									.GetField("EditMonitor").get_NativeValue());
							but12.setSelection((Boolean) userPermission.get(id)
									.GetField("DeleteMonitor")
									.get_NativeValue());
						}
						Authority();
					}
				} else {
					DeletChild(item);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		createTreeItem();
		sashForm_2.setWeights(new int[] { 26, 163, 117, 185 });

		scrolledComposite.setContent(sashForm_2);

		sashForm.setWeights(new int[] { 1, 1 });
		return composite;
	}

	private void createTreeItem() {
		for (TreeItem s1 : tree.getItems()) {
			s1.dispose();
		}
		treeItem = new TreeItem(tree, SWT.NONE | SWT.CHECK);
		treeItem.setText("Ecc9.2");
		treeItem.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
				"icons/logo.jpg"));
		if (userPermission.get("SE") != null) {
			treeItem.setChecked(true);
		}
		for(int i=0;i<SiteViewData.groups_0.size();i++){
			if(SiteViewData.groups_0.get(0) instanceof GroupModle){
				GroupModle group=SiteViewData.groups_0.get(i);
				BusinessObject bo=group.getBo();
				String s=bo.GetField("GroupName").get_NativeValue().toString();
				treeItem1 = new TreeItem(treeItem, SWT.NONE | SWT.CHECK);
				treeItem1.setText(s);
				treeItem1.setData(bo);
				String id=bo.get_RecId();
				if (userPermission.get(id) != null) {
					treeItem1.setChecked((Boolean) userPermission.get(id)
							.GetField("SelectPerimissions").get_NativeValue());
				} else {
					treeItem1.setChecked(false);
				}
				treeItem1.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,"icons/node.jpg"));
				createItem(group, treeItem1);
				treeItem1.setExpanded(true);
			}
		}
		
//		for (int i = 0; i < EccTreeControl.groups_0.size(); i++) {
//			Map map = EccTreeControl.groups_0.get(i);
//			String id = map.get("_id").toString();
//			id = id.substring(id.lastIndexOf("/") + 1);
//			String s = map.get("_name").toString();
//			treeItem1 = new TreeItem(treeItem, SWT.NONE | SWT.CHECK);
//			treeItem1.setText(s);
//			treeItem1.setData(map); // SelectPerimissions
//			if (userPermission.get(id) != null) {
//				treeItem1.setChecked((Boolean) userPermission.get(id)
//						.GetField("SelectPerimissions").get_NativeValue());
//			} else {
//				treeItem1.setChecked(false);
//			}
//			treeItem1.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
//					"icons/node.jpg"));
//			createItem(map, treeItem1, id);
//			treeItem1.setExpanded(true);
//		}
		treeItem.setExpanded(true);
	}

	private void createItem(GroupModle group, TreeItem treeItem12) {
		List<GroupModle> subgroup=group.getGroups();
		for(int i=0;i<subgroup.size();i++){
			GroupModle g=subgroup.get(i);
			BusinessObject bo=g.getBo();
			String subid=bo.get_RecId();
			TreeItem treeItem2 = new TreeItem(treeItem1, SWT.NONE
					| SWT.CHECK);
			treeItem2.setText(bo.GetField("GroupName").get_NativeValue().toString());
			treeItem2.setData(bo);
			if (userPermission.get(subid) != null) {
				treeItem2.setChecked(true);
			} else {
				treeItem2.setChecked(false);
			}
			treeItem2.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
					"icons/node.jpg"));
			createItem(g, treeItem2);
		}
		List<MachineModle> machines=group.getMachines();
		for(int i=0;i<machines.size();i++){
			MachineModle machine=machines.get(i);
			BusinessObject bo=machine.getBo();
			TreeItem treeItem3 = new TreeItem(treeItem1, SWT.NONE
					| SWT.CHECK);
			treeItem3.setText(bo.GetField("ServerAddress")
					.get_NativeValue().toString());
			treeItem3.setData(bo);
			String macid = bo.get_RecId();
			if (userPermission.get(macid) != null) {
				treeItem3.setChecked(true);
			} else {
				treeItem3.setChecked(false);
			}
			treeItem3.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
					"icons/shebei.jpg"));
		}
	}

	// 点击全选按钮的时候选中
	private void selectTree(TreeItem treeItem) {
		TreeItem[] treeitem1 = treeItem.getItems();
		for (TreeItem treeItem2 : treeitem1) {
			treeItem2.setChecked(true);
			selectTree(treeItem2);
		}
	}

	// 取消全选按钮的时候取消全选
	private void removeTree(TreeItem treeItem) {
		TreeItem[] treeitem1 = treeItem.getItems();
		for (TreeItem treeItem2 : treeitem1) {
			treeItem2.setChecked(false);
			removeTree(treeItem2);
		}
	}

	private void createItem(Map map, TreeItem treeItem1, String id) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> subgroup = EccTreeControl.groups_subgroups
				.get(id);
		if (subgroup != null) {
			for (int i = 0; i < subgroup.size(); i++) {
				Map submap = subgroup.get(i);
				String subid = submap.get("_id").toString();
				subid = subid.substring(subid.lastIndexOf("/") + 1);
				TreeItem treeItem2 = new TreeItem(treeItem1, SWT.NONE
						| SWT.CHECK);
				treeItem2.setText(submap.get("_name").toString());
				treeItem2.setData(submap);
				if (userPermission.get(subid) != null) {
					treeItem2.setChecked(true);
				} else {
					treeItem2.setChecked(false);
				}
				treeItem2.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
						"icons/node.jpg"));
				createItem(submap, treeItem2, subid);

			}
		}
		ICollection machine = EccTreeControl.groups_machines.get(id);
		if (machine != null && machine.get_Count() > 0) {
			IEnumerator interfaceTableIEnum = machine.GetEnumerator();
			if (interfaceTableIEnum.MoveNext()) {
				BusinessObject bo = (BusinessObject) interfaceTableIEnum
						.get_Current();
				TreeItem treeItem3 = new TreeItem(treeItem1, SWT.NONE
						| SWT.CHECK);
				treeItem3.setText(bo.GetField("ServerAddress")
						.get_NativeValue().toString());
				treeItem3.setData(bo);
				String macid = bo.get_RecId();
				if (userPermission.get(macid) != null) {
					treeItem3.setChecked(true);
				} else {
					treeItem3.setChecked(false);
				}
				treeItem3.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID,
						"icons/shebei.jpg"));
			}
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button applyButton = createButton(parent, IDialogConstants.OK_ID,
				"授予勾选节点", true);
		Button giveupButton = createButton(parent, IDialogConstants.CANCEL_ID,
				"关闭", false);
		Button closeButton = createButton(parent, IDialogConstants.CLOSE_ID,
				"授予勾选功能", true);
	}

	private void SelectParent(TreeItem item) {
		if (item.getParent() != null && !item.getText().equals("Ecc9.2")) {
			TreeItem treeItem = item.getParentItem();
			treeItem.setChecked(true);
			SelectParent(treeItem);
		}
	}

	protected void SelectChild(TreeItem item) {
		if (item.getItemCount() > 0) {
			for (TreeItem t : item.getItems()) {
				t.setChecked(true);
				SelectChild(t);
			}
		}
	}

	protected void DeletChild(TreeItem item) {
		if (item.getItemCount() > 0) {
			for (TreeItem t : item.getItems()) {
				t.setChecked(false);
				DeletChild(t);
			}
		}
	}

	private void Authority() {
		for (Control c : composite_2.getChildren()) {
			c.setEnabled(flag[0]);
		}
		for (Control c : composite_5.getChildren()) {
			c.setEnabled(flag[1]);
		}
		for (Control c : composite_6.getChildren()) {
			c.setEnabled(flag[2]);
		}
		for (Control c : composite_7.getChildren()) {
			c.setEnabled(flag[3]);
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			bos = new ArrayList<BusinessObject>();
			TreeItem[] items = tree.getItems();
			for (TreeItem treeitem : items) {
				if (treeitem.getChecked()) {
					BusinessObject bo = ConnectionBroker.get_SiteviewApi()
							.get_BusObService().Create("Permissions");
					getPermissions("UserId", combo.getText());
					if (userPermission.size() == 0) {
						bo.GetField("PermissionsType").SetValue(
								new SiteviewValue("SE"));
						bo.GetField("PermissionsId").SetValue(
								new SiteviewValue("SE"));
						bo.GetField("SelectPerimissions").SetValue(
								new SiteviewValue(true));
						bo.GetField("UserId").SetValue(
								new SiteviewValue(combo.getText()));
						bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true,
								true);
						bos.add(bo);
					}
					createChaild(treeitem);
				}
			}
		} else if (buttonId == IDialogConstants.CLOSE_ID) {
			bos = new ArrayList<BusinessObject>();
			TreeItem[] items = tree.getItems();
			for (TreeItem treeitem : items) {
				if (treeitem.getChecked()) {
					BusinessObject bo = ConnectionBroker.get_SiteviewApi()
							.get_BusObService().Create("Permissions");
					List<BusinessObject> list = getPermissions("UserId",
							combo.getText());
					if (userPermission.size() == 0) {
						bo.GetField("PermissionsType").SetValue(
								new SiteviewValue("SE"));
						bo.GetField("PermissionsId").SetValue(
								new SiteviewValue("SE"));
						bo.GetField("AddGroup").SetValue(
								new SiteviewValue(but1.getSelection()));
						bo.GetField("SelectPerimissions").SetValue(
								new SiteviewValue(true));
						bo.GetField("UserId").SetValue(
								new SiteviewValue(combo.getText()));
						bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true,
								true);
						bos.add(bo);
					} else if (userPermission.get("SE") != null) {
						BusinessObject bo1 = userPermission.get("SE");
						bo1.GetField("AddGroup").SetValue(
								new SiteviewValue(but1.getSelection()));
						bo1.SaveObject(ConnectionBroker.get_SiteviewApi(),
								true, true);
						bos.add(bo1);
					}
					createFunction(treeitem, list);
				}
			}
		} else {
			this.close();
		}
	}

	// 节点的功能选择
	private void createFunction(TreeItem treeitem, List<BusinessObject> list) {
		TreeItem[] items = treeitem.getItems();
		for (TreeItem treeitem1 : items) {
			if (treeitem1.getChecked()) {
				BusinessObject bo = ConnectionBroker.get_SiteviewApi()
						.get_BusObService().Create("Permissions");
				if (treeitem1.equals(item)) {
					if (item.getData() instanceof Map) {
						Map map = (Map) item.getData();
						String id = map.get("_id").toString();
						id = id.substring(id.lastIndexOf("/") + 1);
						if (userPermission.size() == 0
								|| userPermission.get(id) == null) {
							bo.GetField("PermissionsType").SetValue(
									new SiteviewValue("Group"));
							bo.GetField("PermissionsId").SetValue(
									new SiteviewValue(id));
							bo.GetField("AddGroup").SetValue(
									new SiteviewValue(but2.getSelection()));
							bo.GetField("AddMachine").SetValue(
									new SiteviewValue(but3.getSelection()));
							bo.GetField("EditGroup").SetValue(
									new SiteviewValue(but4.getSelection()));
							bo.GetField("DeleteGroup").SetValue(
									new SiteviewValue(but5.getSelection()));
							bo.GetField("AddMonitor").SetValue(
									new SiteviewValue(but6.getSelection()));
							bo.GetField("EditMonitor").SetValue(
									new SiteviewValue(but11.getSelection()));
							bo.GetField("DeleteMonitor").SetValue(
									new SiteviewValue(but12.getSelection()));
							bo.GetField("SelectPerimissions").SetValue(
									new SiteviewValue(true));
							bo.GetField("UserId").SetValue(
									new SiteviewValue(combo.getText()));
							bos.add(bo);
							bo.SaveObject(ConnectionBroker.get_SiteviewApi(),
									true, true);
						} else {
							if (userPermission.get(id) != null) {
								BusinessObject bo1 = userPermission.get(id);
								bo1.GetField("AddGroup").SetValue(
										new SiteviewValue(but2.getSelection()));
								bo1.GetField("AddMachine").SetValue(
										new SiteviewValue(but3.getSelection()));
								bo1.GetField("EditGroup").SetValue(
										new SiteviewValue(but4.getSelection()));
								bo1.GetField("DeleteGroup").SetValue(
										new SiteviewValue(but5.getSelection()));
								bo1.GetField("AddMonitor").SetValue(
										new SiteviewValue(but6.getSelection()));
								bo1.GetField("EditMonitor")
										.SetValue(
												new SiteviewValue(but11
														.getSelection()));
								bo1.GetField("DeleteMonitor")
										.SetValue(
												new SiteviewValue(but12
														.getSelection()));
								bos.add(bo1);
								bo1.SaveObject(
										ConnectionBroker.get_SiteviewApi(),
										true, true);
							}
						}
					} else if (item.getData() instanceof BusinessObject) {
						BusinessObject str = (BusinessObject) item.getData();
						String id = str.get_RecId();
						if (userPermission.size() == 0
								|| userPermission.get(id) == null) {
							bo.GetField("PermissionsType").SetValue(
									new SiteviewValue("Machine"));
							bo.GetField("PermissionsId").SetValue(
									new SiteviewValue(id));
							bo.GetField("AddMonitor").SetValue(
									new SiteviewValue(but7.getSelection()));
							bo.GetField("AddMachine").SetValue(
									new SiteviewValue(but8.getSelection()));
							bo.GetField("EditMachine").SetValue(
									new SiteviewValue(but9.getSelection()));
							bo.GetField("DeleteMachine").SetValue(
									new SiteviewValue(but10.getSelection()));
							bo.GetField("EditMonitor").SetValue(
									new SiteviewValue(but11.getSelection()));
							bo.GetField("DeleteMonitor").SetValue(
									new SiteviewValue(but12.getSelection()));
							bo.GetField("SelectPerimissions").SetValue(
									new SiteviewValue(true));
							bo.GetField("UserId").SetValue(
									new SiteviewValue(combo.getText()));
							bos.add(bo);
							bo.SaveObject(ConnectionBroker.get_SiteviewApi(),
									true, true);
						} else {
							if (userPermission.get(id) != null) {
								BusinessObject bo1 = userPermission.get(id);
								bo1.GetField("AddMonitor").SetValue(
										new SiteviewValue(but7.getSelection()));
								bo1.GetField("AddMachine").SetValue(
										new SiteviewValue(but8.getSelection()));
								bo1.GetField("EditMachine").SetValue(
										new SiteviewValue(but9.getSelection()));
								bo1.GetField("DeleteMachine")
										.SetValue(
												new SiteviewValue(but10
														.getSelection()));
								bo1.GetField("EditMonitor")
										.SetValue(
												new SiteviewValue(but11
														.getSelection()));
								bo1.GetField("DeleteMonitor")
										.SetValue(
												new SiteviewValue(but12
														.getSelection()));
								bos.add(bo1);
								bo1.SaveObject(
										ConnectionBroker.get_SiteviewApi(),
										true, true);
							}
						}
					}
				} else {
					if (treeitem1.getData() instanceof Map) {
						Map map = (Map) treeitem1.getData();
						String id = map.get("_id").toString();
						id = id.substring(id.lastIndexOf("/") + 1);
						if (userPermission.size() == 0
								|| userPermission.get(id) == null) {
							bo.GetField("PermissionsType").SetValue(
									new SiteviewValue("Group"));
							bo.GetField("PermissionsId").SetValue(
									new SiteviewValue(id));
							bo.GetField("SelectPerimissions").SetValue(
									new SiteviewValue(true));
							bo.GetField("UserId").SetValue(
									new SiteviewValue(combo.getText()));
							bos.add(bo);
							bo.SaveObject(ConnectionBroker.get_SiteviewApi(),
									true, true);
						} else {
							for (BusinessObject bo1 : list) {
								String str = bo1.GetField("PermissionsId")
										.get_NativeValue().toString();
								if (str.equals(id)) {
									bos.add(bo1);
									bo1.SaveObject(
											ConnectionBroker.get_SiteviewApi(),
											true, true);
									break;
								}
							}
						}
					} else if (treeitem1.getData() instanceof BusinessObject) {
						BusinessObject str = (BusinessObject) treeitem1
								.getData();
						String id = str.get_RecId();
						if (userPermission.size() == 0
								|| userPermission.get(id) == null) {
							bo.GetField("PermissionsType").SetValue(
									new SiteviewValue("Machine"));
							bo.GetField("PermissionsId").SetValue(
									new SiteviewValue(id));
							bo.GetField("SelectPerimissions").SetValue(
									new SiteviewValue(true));
							bo.GetField("UserId").SetValue(
									new SiteviewValue(combo.getText()));
							bos.add(bo);
							bo.SaveObject(ConnectionBroker.get_SiteviewApi(),
									true, true);
						} else {
							for (BusinessObject bo1 : list) {
								String str1 = bo1.GetField("PermissionsId")
										.get_NativeValue().toString();
								if (str1.equals(id)) {
									bos.add(bo1);
									bo1.SaveObject(
											ConnectionBroker.get_SiteviewApi(),
											true, true);
									break;
								}
							}
						}
					}
				}
				createFunction(treeitem1, list);
			}

		}
	}

	private void createChaild(TreeItem treeitem) {
		TreeItem[] items = treeitem.getItems();
		for (TreeItem treeitem1 : items) {
			if (treeitem1.getChecked()) {
				BusinessObject bo = ConnectionBroker.get_SiteviewApi()
						.get_BusObService().Create("Permissions");
				if (treeitem1.getData() instanceof Map) {
					Map map = (Map) treeitem1.getData();
					String id = map.get("_id").toString();
					id = id.substring(id.lastIndexOf("/") + 1);
					if (userPermission.size() == 0
							|| userPermission.get(id) == null) {
						bo.GetField("PermissionsType").SetValue(
								new SiteviewValue("Group"));
						bo.GetField("PermissionsId").SetValue(
								new SiteviewValue(id));
						bo.GetField("SelectPerimissions").SetValue(
								new SiteviewValue(true));
						bo.GetField("UserId").SetValue(
								new SiteviewValue(combo.getText()));
						bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true,
								true);
						bos.add(bo);
					}
				} else if (treeitem1.getData() instanceof BusinessObject) {
					BusinessObject str = (BusinessObject) treeitem1.getData();
					String id = str.get_RecId();
					if (userPermission.size() == 0
							|| userPermission.get(id) == null) {
						bo.GetField("PermissionsType").SetValue(
								new SiteviewValue("Machine"));
						bo.GetField("PermissionsId").SetValue(
								new SiteviewValue(id));
						bo.GetField("SelectPerimissions").SetValue(
								new SiteviewValue(true));
						bo.GetField("UserId").SetValue(
								new SiteviewValue(combo.getText()));
						bo.SaveObject(ConnectionBroker.get_SiteviewApi(), true,
								true);
						bos.add(bo);
					}
				}
				createChaild(treeitem1);
			}

		}
	}

	public List<BusinessObject> getPermissions(String key, String vlue) {
		ICollection icollection = FileTools.getBussCollection(key, vlue,
				"Permissions");
		List<BusinessObject> bos = new ArrayList<BusinessObject>();
		IEnumerator iEnumerator = icollection.GetEnumerator();
		userPermission = new HashMap<String, BusinessObject>();
		while (iEnumerator.MoveNext()) {
			BusinessObject ob = (BusinessObject) iEnumerator.get_Current();
			userPermission.put(ob.GetField("PermissionsId").get_NativeValue()
					.toString(), ob);
			bos.add(ob);
		}
		return bos;
	}

	private void selectionButton(boolean flag) {
		but1.setSelection(flag);
		but2.setSelection(flag);
		but3.setSelection(flag);
		but4.setSelection(flag);
		but5.setSelection(flag);
		but6.setSelection(flag);
		but7.setSelection(flag);
		but8.setSelection(flag);
		but9.setSelection(flag);
		but10.setSelection(flag);
		but11.setSelection(flag);
		but12.setSelection(flag);
		but13.setSelection(flag);
	}
}
