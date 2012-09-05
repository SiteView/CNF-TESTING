package SiteView.ecc.view;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;

import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;
import SiteView.ecc.Activator;
import SiteView.ecc.Action.AddGroupAction;
import SiteView.ecc.Action.DNSAction;
import SiteView.ecc.Action.DeleteGroupAction;
import SiteView.ecc.Action.DeleteMachineAction;
import SiteView.ecc.Action.EditorGroupAction;
import SiteView.ecc.Action.EditorMachineAction;
import SiteView.ecc.Action.SNMPAction;
import SiteView.ecc.Action.UnixAction;
import SiteView.ecc.Action.WindowsAction;
import SiteView.ecc.Action.pingAction;
import SiteView.ecc.Control.EccTreeComparer;
import SiteView.ecc.Control.EccTreeContentProvider;
import SiteView.ecc.Control.EccTreeLabelProvider;
import SiteView.ecc.Modle.GroupModle;
import SiteView.ecc.Modle.MachineModle;
import SiteView.ecc.Modle.SiteViewEcc;
import SiteView.ecc.Modle.UserManageModle;
import SiteView.ecc.data.SiteViewData;
import SiteView.ecc.editors.EccControl;
import SiteView.ecc.editors.EccControlInput;
import SiteView.ecc.editors.UserManager;
import SiteView.ecc.editors.UserManagerInput;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.RunForChildrenBusObsActionDef.Children;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;

public class EccTreeControl extends ViewPart {
	EccControlInput eee=new EccControlInput();
	public static Object item;
	public static TreeViewer treeViewer;

	public static TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public EccTreeControl() {
	}

	public static String ID = "SiteView.ecc.view";

	@Override
	public void createPartControl(Composite parent) {
		parent.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_FOREGROUND));
		treeViewer = new TreeViewer(parent);
		Tree tree = treeViewer.getTree();
		tree.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_FOREGROUND));
		treeViewer.setContentProvider(new EccTreeContentProvider());
		treeViewer.setLabelProvider(new EccTreeLabelProvider());
		SiteViewData s = new SiteViewData();
		treeViewer.setInput(s.getData());
		treeViewer.setComparer(new EccTreeComparer());
		treeViewer.getTree().addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				item=e.item.getData();
				if(item instanceof SiteViewData){
					return ;
				}else if(item instanceof UserManageModle){
					 try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new UserManagerInput(), UserManager.ID);
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
				}else if(item instanceof GroupModle){
					 IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();  
					 IEditorPart editor = page.findEditor(eee); 
					 if(editor==null){
						 try {
							page.openEditor(eee, EccControl.ID);
						} catch (PartInitException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}  
					 }else{
						((EccControl)editor).createTableItem();
						if(EccControl.item==null){
							((EccControl)editor).tab(null);
						}else{
							((EccControl)editor).tab((BusinessObject)EccControl.item.getData());
						}
					 }
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		createContextMenu(parent);
	}

	@Override
	public void setFocus() {

	}

	private void createContextMenu(Composite parent) {// 添加菜单
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) treeViewer
						.getSelection();
				Object element = selection.getFirstElement();
				item=element;
				if (element instanceof GroupModle) {
					GroupModle gm = (GroupModle) element;
					fillContextMenu1(manager, gm);
				} else if (element instanceof MachineModle) {
					MachineModle machine = (MachineModle) element;
					fillContextMenu2(manager,machine);
				}else if(element instanceof SiteViewEcc){
					fillContextMenu2(manager,(SiteViewEcc)element);
				}
			}
		});
		Menu menu = mgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(mgr, treeViewer);

	}

	protected void fillContextMenu2(IMenuManager manager,SiteViewEcc siteview) {
		AddGroupAction addGroupAction=new AddGroupAction();
		manager.add(addGroupAction);
		if(!siteview.isAddGroup()){
			manager.setVisible(false);
		}
	}

	public void fillContextMenu1(IMenuManager manager,GroupModle gm) {// 菜单具体项目
		URL url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),
				"Image/AddMachine.bmp");
		ImageDescriptor temp1 = ImageDescriptor.createFromURL(url);
		MenuManager machineMenu = new MenuManager("增加设备", temp1, "1");
		manager.add(machineMenu);
		machineMenu.add(new WindowsAction());
		machineMenu.add(new UnixAction());
		machineMenu.add(new SNMPAction());

		URL url2 = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),
				"Image/AddMonitor.bmp");
		ImageDescriptor temp2 = ImageDescriptor.createFromURL(url2);
		MenuManager monitorMenu = new MenuManager("增加监测器", temp2, "2");
		manager.add(monitorMenu);
		monitorMenu.add(new pingAction("Ecc.ping"));
		monitorMenu.add(new pingAction("Ecc.DNS"));
		monitorMenu.add(new pingAction("Ecc.LinkCheck"));
		monitorMenu.add(new pingAction("Ecc.URL"));
		monitorMenu.add(new pingAction("Ecc.URLContent"));
		monitorMenu.add(new pingAction("Ecc.Mail"));
		EditorGroupAction editorGroupAction=new EditorGroupAction();
		AddGroupAction addGroupAction=new AddGroupAction();
		DeleteGroupAction deleteGroupAction=new DeleteGroupAction();
		manager.add(editorGroupAction);
		manager.add(addGroupAction);
		manager.add(deleteGroupAction);
		
		if(!gm.isAddMachine()){
			machineMenu.setVisible(false);
		}
		if(!gm.isAddMonitor()){
			monitorMenu.setVisible(false);
		}
		if(!gm.isAddSubGroup()){
			addGroupAction.setEnabled(false);
		}
		if(!gm.isDeleteGroup()){
			deleteGroupAction.setEnabled(false);
		}
		if(!gm.isEditGroup()){
			editorGroupAction.setEnabled(false);
		}

	}

	public void fillContextMenu2(IMenuManager manager,MachineModle machine) {// 菜单具体项目
		URL url;
		url = BundleUtility.find(Platform.getBundle(Activator.PLUGIN_ID),
				"Image/AddMonitor.bmp");
		ImageDescriptor temp = ImageDescriptor.createFromURL(url);
		MenuManager monitorMenu = new MenuManager("增加监测器", temp, "1");
		manager.add(monitorMenu);
		monitorMenu.add(new DNSAction("Ecc.ping"));
		monitorMenu.add(new DNSAction("Ecc.DNS"));
		monitorMenu.add(new DNSAction("Ecc.LinkCheck"));
		monitorMenu.add(new DNSAction("Ecc.URL"));
		monitorMenu.add(new DNSAction("Ecc.URLContent"));
		monitorMenu.add(new DNSAction("Ecc.Mail"));
		monitorMenu.add(new DNSAction("Ecc.DiskSpace"));
		monitorMenu.add(new DNSAction("Ecc.WinPerformanceCounter"));
		monitorMenu.add(new DNSAction("Ecc.WinResources"));
		monitorMenu.add(new DNSAction("Ecc.WinMediaServer"));
		monitorMenu.add(new DNSAction("Ecc.NetWorkMonitor"));
		monitorMenu.add(new DNSAction("Ecc.WindowsDialup"));
		monitorMenu.add(new DNSAction("Ecc.WindowsEventLog"));
		monitorMenu.add(new DNSAction("Ecc.Port"));
		monitorMenu.add(new DNSAction("Ecc.Memory"));
		monitorMenu.add(new DNSAction("Ecc.Service"));
		EditorMachineAction editorMachineAction=new EditorMachineAction();
		DeleteMachineAction deleteMachineAction=new DeleteMachineAction();
		DeleteGroupAction deleteMonitorAction=new DeleteGroupAction();
		manager.add(editorMachineAction);//编辑设备
		manager.add(deleteMachineAction);//删除设备
		if(!machine.isDeleteMchine()){
			
			deleteMachineAction.setEnabled(false);
			}
		if(!machine.isAddMonitor()){
			
			monitorMenu.setVisible(false);
		}
		if(!machine.isDeleteMonitor()){
			
			deleteMonitorAction.setEnabled(false);
		}
		if(!machine.isEditMachine()){
			
			editorMachineAction.setEnabled(false);
		}
	}

	public static BusinessObject CreateBo(String key, String s, String s1) {
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s1, QueryInfoToGet.All);
		XmlElement xml;
		xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
				Operators.Equals, s);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi()
				.get_BusObService().get_SimpleQueryResolver()
				.ResolveQueryToBusObList(query);
		BusinessObject bo = null;
		IEnumerator interfaceTableIEnum = iCollenction.GetEnumerator();
		if (interfaceTableIEnum.MoveNext()) {
			bo = (BusinessObject) interfaceTableIEnum.get_Current();
		}
		return bo;
	}
}
