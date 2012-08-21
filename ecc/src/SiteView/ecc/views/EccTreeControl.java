package SiteView.ecc.views;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import COM.dragonflow.Api.ApiRmiServer;
import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.Activator;
import SiteView.ecc.bundle.EditGroupBundle;
import SiteView.ecc.data.MonitorServer;
import SiteView.ecc.dialog.GroupTreeDialog;
import SiteView.ecc.editors.EccControl;
import SiteView.ecc.editors.EccControlInput;
import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.BusinessLogic.SetDataFlags;
import Siteview.Windows.Forms.ConnectionBroker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import core.busobmaint.BusObMaintView;
import siteview.windows.forms.ImageHelper;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;
import org.eclipse.wb.swt.SWTResourceManager;


public class EccTreeControl extends ViewPart {
	public EccTreeControl() {
	}
	//没有父组的组
	public static List<Map<String,Object>> groups_0;
	//所有组id对应组属性
	public static Map<String,Map<String,Object>> groups;
	//父组id对应子组属性
	public static Map<String,List<Map<String,Object>>> groups_subgroups;
	//父组id对应监测器属性
	public static Map<String,List<Map<String,Object>>> groups_monitors;
	
	public static Map<String,BusinessObject> monitors_bo;
	public static Map<String,Map<String, Object>> monitors_siteview;
	MonitorServer mg=new MonitorServer();
	public static List<String> monitors=new ArrayList<String>();
	public static final String ID = "SiteView.ecc.views.EccTreeControl";
	public static TreeItem item;
	public static String s=null;
	public static Map<String,String> oldParentId=new HashMap<String,String>();
	EccControlInput eee=new EccControlInput();
	TreeItem trtmNewTreeitem;//整体视图
	public static TreeItem trtmNewTreeitem1;//root
	TreeItem trtmNewTreeitem2;//设备
	TreeItem trtmNewTreeitem3;//设置
	TreeItem trtmNewTreeitem4;//报警
	static{
		monitors.add("Ecc.ping");
		monitors.add("Ecc.DNS");
		monitors.add("Ecc.DiskSpace");
		monitors.add("Ecc.LinkCheck");
		monitors.add("Ecc.Radius");
		monitors.add("Ecc.URL");
		monitors.add("Ecc.URLContent");
		monitors.add("Ecc.WinPerformanceCounter");
		monitors.add("Ecc.LogFile");
		monitors.add("Ecc.WinResources");
		monitors.add("Ecc.eBusinessChain");
		monitors.add("Ecc.Mail");
		monitors.add("Ecc.RTSP");
		monitors.add("Ecc.Tuxedo");
		monitors.add("Ecc.WinMediaServer");
		monitors.add("Ecc.FormulaComposite");
		monitors.add("Ecc.MGHealthMonitor");
		monitors.add("Ecc.MonitorLoadMonitor");
		monitors.add("Ecc.HistoryHealthMonitor");
		monitors.add("Ecc.NewsMonitor");
		monitors.add("Ecc.FtpMonitor");
		monitors.add("Ecc.NetWorkMonitor");
		monitors.add("Ecc.SnmpTrapMonitor");
		monitors.add("Ecc.SnmpMonitor");
		monitors.add("Ecc.SQLServerMonitor");
		monitors.add("Ecc.OracleJDBCMonitor");
		monitors.add("Ecc.InterfaceMonitor");
		monitors.add("Ecc.Apache");
		monitors.add("Ecc.EJB");
		monitors.add("Ecc.MAPI");
		monitors.add("Ecc.SAP");
		monitors.add("Ecc.URLList");
		monitors.add("Ecc.ASP");
		monitors.add("Ecc.F5Big-IP");
		monitors.add("Ecc.MasterHealth");
		monitors.add("Ecc.ScriptMonitor");
		
		monitors.add("Ecc.URLOriginal");
		monitors.add("Ecc.BrowsableWindowsPerformance");
		monitors.add("Ecc.File");
		monitors.add("Ecc.Memory");
		monitors.add("Ecc.Service");
		monitors.add("Ecc.WindowsEventLog");
		monitors.add("Ecc.Database_Query");
		monitors.add("Ecc.DHCP");
		monitors.add("Ecc.Directory");
		monitors.add("Ecc.IISServer");
		monitors.add("Ecc.LDAP");
		monitors.add("Ecc.WebServer");
		monitors.add("Ecc.CompositeMonitor");
		monitors.add("Ecc.Service");
		monitors.add("Ecc.Port");
		monitors.add("Ecc.TestPing");
		monitors.add("Ecc.WindowsDialup");
	}
	
	public void createPartControl(final Composite cp) {
		cp.setLayout(new FillLayout());
		final Tree tree = new Tree(cp, SWT.BORDER);
		tree.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		tree.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				int y = e.y;
				int x=e.x;
				item = tree.getItem(new Point(x, y));
				if(item!=null){
					if(item.getText().equals("设备")){
						if(e.button==3){
							final Menu menu=createMenu(tree,cp,"shebei");
							menu.setLocation(tree.toDisplay(e.x, e.y));
							menu.setVisible(true);
						}
					}else if(item.getText().equals("root")){
						if(e.button==3){
							final Menu menu=createMenu(tree,cp,"root");
							menu.setLocation(tree.toDisplay(e.x, e.y));
							menu.setVisible(true);
						}
					}else{
						@SuppressWarnings("unchecked")
						String s0=((Map<String,Object>)item.getData()).get("_id").toString();
						if(s0.contains("/")&&s0.lastIndexOf("/")==8){
							if(e.button==3){
								final Menu menu=createMenu(tree,cp,"group");
								menu.setLocation(tree.toDisplay(e.x, e.y));
								menu.setVisible(true);
							}else if(e.button==1){
								try {
									if(item.getItemCount()<=0){
										s0=s0.substring(s0.lastIndexOf("/")+1);
									//	List<Map<String, Object>> list = mg.GroupChild(s0);
										List<Map<String, Object>> list = groups_subgroups.get(s0);
										if(list!=null&&list.size()!=0){
											for(int i=0;i<list.size();i++){
												getTreeItem(item,list.get(i));
											}
										}
									}
									 IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();  
									 IEditorPart editor = page.findEditor(eee);  
									 if(editor==null){
										 page.openEditor(eee, EccControl.ID);  
									 }else{
										((EccControl)editor).createTableItem();
										if(EccControl.item==null){
											((EccControl)editor).tab(null);
										}else{
											((EccControl)editor).tab((BusinessObject)EccControl.item.getData());
										}
									 }
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}
						}else if(s0.contains("/")&&s0.lastIndexOf("/")>8){
							if(e.button==1){
								s0=s0.substring(s0.lastIndexOf("/")+1);
								BusinessObject bo=CreateBo("RecId",s0,"Ecc");
								BusObMaintView.open(ConnectionBroker.get_SiteviewApi(), bo);
							}else{
								final Menu menu=createMenu(tree,cp,"monitor");
								menu.setLocation(tree.toDisplay(e.x, e.y));
								menu.setVisible(true);
							}
						}
					}
				}
			}
		});
		trtmNewTreeitem = new TreeItem(tree, SWT.NONE);
		trtmNewTreeitem.setText("整体视图");
		trtmNewTreeitem.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/ztst.jpg"));
		trtmNewTreeitem.setExpanded(true);
		refurbishView(trtmNewTreeitem,cp);
	}
	private void refurbishView(TreeItem trtmNewTreeitem, Composite cp) {
		if(trtmNewTreeitem1!=null){
			trtmNewTreeitem1.dispose();
			trtmNewTreeitem2.dispose();
			trtmNewTreeitem3.dispose();
		}
		trtmNewTreeitem1 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem1.setText("root");
		trtmNewTreeitem1.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/logo.jpg"));
		trtmNewTreeitem1.setExpanded(true);
		
		trtmNewTreeitem2 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem2.setText("设备");
		trtmNewTreeitem2.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/shebei.jpg"));
		trtmNewTreeitem2.setExpanded(true);
		
		trtmNewTreeitem3 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem3.setText("设置");
		trtmNewTreeitem3.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/Setup.jpg"));
		trtmNewTreeitem3.setExpanded(true);
		
		trtmNewTreeitem4 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem4.setText("报警");
		trtmNewTreeitem4.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/Alarm.jpg"));
		trtmNewTreeitem4.setExpanded(true);
		
		TreeItem trtmNewTreeitem3_1=new TreeItem(trtmNewTreeitem3,SWT.NONE);
		trtmNewTreeitem3_1.setText("用户管理");
		trtmNewTreeitem3_1.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/user.bmp"));
		trtmNewTreeitem3_1.setExpanded(true);
//		mg=new MonitorServer();
//		List<Map<String, Object>> groups=mg.Group();
//		for(int i=0;i<groups.size();i++){
//			Map<String,Object> maps=(Map<String, Object>) groups.get(i);
//			getTreeItem(trtmNewTreeitem1,maps);
//		}
		getData();
		for(int i=0;i<groups_0.size();i++){
			Map<String,Object> maps=(Map<String, Object>) groups_0.get(i);
			getTreeItem(trtmNewTreeitem1,maps);
		}
		cp.layout();
	}
	public void getTreeItem(TreeItem treeItem,Map<String,Object> map){
		String groupId=map.get("_id").toString();
		TreeItem tree=new TreeItem(treeItem, SWT.NONE);
		tree.setData(map);
		if(map.get("_id").toString().lastIndexOf("/")>8){
			tree.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/Monitor.jpg"));
		}else{
			tree.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/node.jpg"));
		}
		tree.setExpanded(true);
		groupId=groupId.substring(groupId.lastIndexOf("/")+1);
		int i=0;
		int j=0;
		if(groups_subgroups.get(groupId)!=null){
			i=groups_subgroups.get(groupId).size();
		}
		if(groups_monitors.get(groupId)!=null){
			j=groups_monitors.get(groupId).size();
		}
		tree.setText(map.get("_name").toString()+"("+i+")");
	}
	public Menu createMenu(final Tree tree,final Composite cp,String s0){
		Menu menu=new Menu(tree);
		if(s0.equals("group")){
			MenuItem m1=new MenuItem(menu,SWT.NONE);
			m1.setText("编辑组");
			MenuItem m2=new MenuItem(menu,SWT.NONE);
			m2.setText("添加组");
			MenuItem m3=new MenuItem(menu,SWT.NONE);
			m3.setText("移动组");
			MenuItem m4=new MenuItem(menu,SWT.NONE);
			m4.setText("删除组");
			MenuItem m5=new MenuItem(menu,SWT.NONE);
			m5.setText("增加监测器");
			m1.addSelectionListener(new SelectionListener() {
				@SuppressWarnings("unchecked")
				public void widgetSelected(SelectionEvent e) {
					String s0=((Map<String,Object>)item.getData()).get("_id").toString();
					s0=s0.substring(s0.lastIndexOf("/")+1);
					BusinessObject bo=CreateBo("RecId",s0,"EccGroup");
					if(bo!=null){
						if(bo.GetField("ParentGroupId").get_NativeValue().toString()!=null){
							oldParentId.put(s0,bo.GetField("ParentGroupId").get_NativeValue().toString());
						}
						BusObMaintView.open(ConnectionBroker.get_SiteviewApi(), bo);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			m2.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					BusObMaintView.newBusOb(ConnectionBroker.get_SiteviewApi(), "EccGroup");
				}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			m3.addSelectionListener(new SelectionListener() {
				@SuppressWarnings("unchecked")
				public void widgetSelected(SelectionEvent e) {
					s=((Map<String,Object>)item.getData()).get("_id").toString();
					s=s.substring(s.lastIndexOf("/")+1);
					GroupTreeDialog gt=new GroupTreeDialog(null);
					gt.open();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			m4.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					MonitorServer monitorServer=new MonitorServer();
					try {
						@SuppressWarnings("unchecked")
						String  s=((Map<String,Object>)item.getData()).get("_id").toString();
						s=s.substring(s.lastIndexOf("/")+1);
						
						String s0=item.getText();
						monitorServer.deleteGroup(s0);
						BusinessObject bo=CreateBo("RecId",s,"EccGroup");
						String parentId=bo.GetField("ParentGroupId").get_NativeValue().toString();
						if(parentId!=null&&!parentId.equals("")){
							List<Map<String,Object>> g=groups_subgroups.get(parentId);
							g.remove(groups.get(s));
							groups_subgroups.put(parentId, g);
							TreeItem tt=item.getParentItem();
							String text=tt.getText();
							text=text.substring(0,text.lastIndexOf("(")+1);
							tt.setText(text+g.size()+")");
						}else{
							groups_0.remove(groups.get(s));
						}
						if(bo!=null){
							bo.DeleteObject(ConnectionBroker.get_SiteviewApi());
						}
						groups.put(s, null);
						TreeItem treItem=item;
						treItem.dispose();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SiteViewException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			m5.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					final Menu menu=new Menu(tree);
					for(String s:monitors){
						 MenuItem m1=new MenuItem(menu,SWT.NONE);
						 m1.setText(s);
						 m1.addSelectionListener(new SelectionListener() {
							public void widgetSelected(SelectionEvent e) {
								MenuItem s=(MenuItem)e.widget;
								BusObMaintView.newBusOb(ConnectionBroker.get_SiteviewApi(),s.getText());
							}
							public void widgetDefaultSelected(SelectionEvent e) {}
						});
					}
					menu.setLocation(tree.toDisplay(e.x+120, e.y+50));
					menu.setVisible(true);
				}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}else if(s0.equals("monitor")){
			MenuItem m1=new MenuItem(menu,SWT.NONE);
			m1.setText("编辑监测器");
			m1.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					@SuppressWarnings("unchecked")
					String s0=((Map<String,Object>)item.getData()).get("_id").toString();
					s0=s0.substring(s0.lastIndexOf("/")+1);
					BusinessObject bo=CreateBo("RecId",s0,"Ecc");
					BusObMaintView.open(ConnectionBroker.get_SiteviewApi(), bo);
				}

				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			MenuItem m2=new MenuItem(menu,SWT.NONE);
			m2.setText("添加监测器");
			MenuItem m3=new MenuItem(menu,SWT.NONE);
			m3.setText("删除监测器");
			m3.addSelectionListener(new SelectionListener() {
				@SuppressWarnings("unchecked")
				public void widgetSelected(SelectionEvent e) {
						String  s=((Map<String,Object>)item.getData()).get("_id").toString();
						s=s.substring(s.lastIndexOf("/")+1);
						BusinessObject bo=CreateBo("RecId",s,"Ecc");
						String groupId=bo.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString();
						EditGroupBundle edit=new EditGroupBundle();
						edit.updateGroup("GroupId="+groupId);
						if(bo!=null){
							bo.DeleteObject(ConnectionBroker.get_SiteviewApi());
						}
					}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}else if(s0.equals("shebei")){
			MenuItem m1=new MenuItem(menu,SWT.NONE);
			m1.setText("添加设备");
			m1.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					//s=null;
					BusObMaintView.newBusOb(ConnectionBroker.get_SiteviewApi(), "InventoryItem.Computer");
				}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}
		MenuItem m6=new MenuItem(menu,SWT.NONE);
		m6.setText("刷新");
		m6.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(item.getText().equals("root")||item.getText().equals("设备")){
					item.dispose();
					refurbishView(trtmNewTreeitem, cp);
				}else{
					//getData();
					@SuppressWarnings("unchecked")
					Map<String,Object> map=(Map<String, Object>) item.getData();
					TreeItem i=item.getParentItem();
					item.dispose();
					getTreeItem(i, map);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return menu;
	}
	public void setFocus() {
	}
	public TreeItem getItem() {
		return item;
	}
	public void setItem(TreeItem item) {
		this.item = item;
	}
	public static  BusinessObject CreateBo(String key,String s,String s1) {
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s1, QueryInfoToGet.All);
		XmlElement xml ;
		xml=query.get_CriteriaBuilder().FieldAndValueExpression(key,
				Operators.Equals, s);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction = ConnectionBroker.get_SiteviewApi().get_BusObService()
				.get_SimpleQueryResolver().ResolveQueryToBusObList(query);
		BusinessObject bo=null;
		IEnumerator interfaceTableIEnum = iCollenction.GetEnumerator();
		if(interfaceTableIEnum.MoveNext()){
			bo = (BusinessObject) interfaceTableIEnum
					.get_Current();
		}
		return bo;
	}	
	public void getData(){
		mg=new MonitorServer();
		//没有父组的组
		groups_0=mg.Group();
		groups=new HashMap<String,Map<String,Object>>();
		groups_subgroups=new HashMap<String, List<Map<String,Object>>>();
		groups_monitors=new HashMap<String, List<Map<String,Object>>>();
		monitors_siteview=new HashMap<String,Map<String,Object>>();
		monitors_bo=new HashMap<String,BusinessObject>();
		getData_1(groups_0);
	}
	public void getData_1(List<Map<String, Object>> group){
		for(int i=0;i<group.size();i++){
			Map<String,Object> maps=(Map<String, Object>) group.get(i);
			String groupid=maps.get("_id").toString();
			groupid=groupid.substring(groupid.lastIndexOf("/")+1);
			groups.put(groupid, maps);
			List<Map<String,Object>> g= mg.GroupChild(groupid);
			if(g.size()>0){
				groups_subgroups.put(groupid,g);
				getData_1(g);
			}
			List<Map<String,Object>> m= mg.getMonitorsForGroup(groupid);
			for(int n=0;n<m.size();n++){
				Map<String,Object> mo=m.get(n);
				String monitorid=mo.get("_id").toString();
				monitorid=monitorid.substring(monitorid.lastIndexOf("/")+1);
				monitors_siteview.put(monitorid, mo);
				String type=mo.get("_class").toString();
				String filePath = FileTools.getRealPath("\\files\\siteview9.2_itsm.properties");
				type= Config.getReturnStr(filePath,type);
				//monitors_bo.put(monitorid,CreateBo("RecId", monitorid, "Ecc."+type));
			}
			if(m.size()>0){
				groups_monitors.put(groupid,m);	
			}
		}
		
	}
}

