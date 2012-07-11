package SiteView.ecc.views;

import java.awt.Color;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
import org.eclipse.ui.part.ViewPart;
import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.Activator;
import SiteView.ecc.data.MonitorServer;
import SiteView.ecc.dialog.GroupTreeDialog;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
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
	public static ISiteviewApi siteviewApi = ConnectionBroker.get_SiteviewApi();
	public EccTreeControl() {
	}
	MonitorServer mg;
	public static List<String> monitors=new ArrayList<String>();
	public static final String ID = "SiteView.ecc.views.EccTreeControl";
	public static TreeItem item;
	public static String s=null;
	
	TreeItem trtmNewTreeitem;//整体视图
	TreeItem trtmNewTreeitem1;//root
	TreeItem trtmNewTreeitem2;//设备
	
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
							}
						}else if(s0.contains("/")&&s0.lastIndexOf("/")>8){
							if(e.button==1){
								s0=s0.substring(s0.lastIndexOf("/")+1);
								BusinessObject bo=CreateBo(s0,"Ecc");
								BusObMaintView.open(siteviewApi, bo);
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
		trtmNewTreeitem.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/ztst.gif"));
		trtmNewTreeitem.setExpanded(true);
	
		refurbishView(trtmNewTreeitem,cp);
	}
	private void refurbishView(TreeItem trtmNewTreeitem, Composite cp) {
		if(trtmNewTreeitem1!=null){
			trtmNewTreeitem1.dispose();
			trtmNewTreeitem2.dispose();
		}
		trtmNewTreeitem1 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem1.setText("root");
		trtmNewTreeitem1.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/logo.gif"));
		trtmNewTreeitem1.setExpanded(true);
		
		trtmNewTreeitem2 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem2.setText("设备");
		trtmNewTreeitem2.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/shebei.jpg"));
		trtmNewTreeitem2.setExpanded(true);
		try {
			 mg=new MonitorServer();
			List<Map<String, Object>> groups=mg.Group();
			for(int i=0;i<groups.size();i++){
				Map<String,Object> maps=(Map<String, Object>) groups.get(i);
				getTreeItem(trtmNewTreeitem1,maps);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (SiteViewException e) {
			e.printStackTrace();
		}
		cp.layout();
	}
	public void getTreeItem(TreeItem treeItem,Map<String,Object> map) throws RemoteException, SiteViewException{
		TreeItem tree=new TreeItem(treeItem, SWT.NONE);
		tree.setText(map.get("_name").toString());
		tree.setData(map);
		if(map.get("_id").toString().lastIndexOf("/")>8){
			tree.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/Monitor.jpg"));
		}else{
			tree.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/node.jpg"));
		}
		tree.setExpanded(true);
		List<Map<String, Object>> list = mg.getMonitorsForGroup(map.get("_id").toString());
		list.addAll(mg.GroupChild(map.get("_id").toString()));
		if(list.size()!=0){
			for(int i=0;i<list.size();i++){
				getTreeItem(tree,list.get(i));
			}
		}
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
				public void widgetSelected(SelectionEvent e) {
					@SuppressWarnings("unchecked")
					String s0=((Map<String,Object>)item.getData()).get("_id").toString();
					s0=s0.substring(s0.lastIndexOf("/")+1);
					BusinessObject bo=CreateBo(s0,"EccGroup");
					if(bo!=null){
						BusObMaintView.open(siteviewApi, bo);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {}
			});
			m2.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					s=null;
					BusObMaintView.newBusOb(siteviewApi, "EccGroup");
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
						BusinessObject bo=CreateBo(s,"EccGroup");
						if(bo!=null){
							bo.DeleteObject(siteviewApi);
						}
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
								BusObMaintView.newBusOb(siteviewApi,s.getText());
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
					BusinessObject bo=CreateBo(s0,"Ecc");
					BusObMaintView.open(siteviewApi, bo);
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
						BusinessObject bo=CreateBo(s,"Ecc");
						if(bo!=null){
							bo.DeleteObject(siteviewApi);
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
					BusObMaintView.newBusOb(siteviewApi, "InventoryItem.Computer");
				}
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}
		MenuItem m6=new MenuItem(menu,SWT.NONE);
		m6.setText("刷新");
		m6.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			//	BusObMaintView.open(siteviewApi, "Ecc", new IVirtualKeyList)
				refurbishView(trtmNewTreeitem,cp);
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
	public static  BusinessObject CreateBo(String s,String s1) {
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(s1, QueryInfoToGet.All);
		XmlElement xml ;
		xml=query.get_CriteriaBuilder().FieldAndValueExpression("RecId",
				Operators.Equals, s);
		query.set_BusObSearchCriteria(xml);
		ICollection iCollenction = siteviewApi.get_BusObService()
				.get_SimpleQueryResolver().ResolveQueryToBusObList(query);
		BusinessObject bo=null;
		IEnumerator interfaceTableIEnum = iCollenction.GetEnumerator();
		if(interfaceTableIEnum.MoveNext()){
			bo = (BusinessObject) interfaceTableIEnum
					.get_Current();
		}
		return bo;
	}	
}

