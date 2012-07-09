package SiteView.ecc.views;

import java.rmi.RemoteException;
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


public class EccTreeControl extends ViewPart {
	public static ISiteviewApi siteviewApi = ConnectionBroker.get_SiteviewApi();
	public EccTreeControl() {
	}
	MonitorServer mg=new MonitorServer();

	public static final String ID = "SiteView.ecc.views.EccTreeControl";
	public static TreeItem item;
	public static String s=null;
	@Override
	public void createPartControl(final Composite cp) {
		// TODO Auto-generated method stub
		if (cp.getChildren().length > 0) {
			for (Control control : cp.getChildren()) {
				control.dispose();
			}
		}
		cp.setLayout(new FillLayout());	
		final Tree tree = new Tree(cp, SWT.BORDER);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				int y = e.y;
				int x=e.x;
				item = tree.getItem(new Point(x, y));
				if(item!=null&&item.getText().equals("设备")){
					if(e.button==3){
						final Menu menu=createMenu(tree,cp,"shebei");
						menu.setLocation(tree.toDisplay(e.x, e.y));
						menu.setVisible(true);
					}
				}else if(item!=null&&item.getText().equals("root")){
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
		});
		
		TreeItem trtmNewTreeitem = new TreeItem(tree, SWT.NONE);
		trtmNewTreeitem.setText("整体视图");
		trtmNewTreeitem.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/ztst.gif"));
		trtmNewTreeitem.setExpanded(true);
		TreeItem trtmNewTreeitem1 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem1.setText("root");
		trtmNewTreeitem1.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/logo.gif"));
		trtmNewTreeitem1.setExpanded(true);
		TreeItem trtmNewTreeitem2 = new TreeItem(trtmNewTreeitem, SWT.NONE);
		trtmNewTreeitem2.setText("设备");
		trtmNewTreeitem2.setImage( ImageHelper.LoadImage(Activator.PLUGIN_ID, "icons/shebei.jpg"));
		trtmNewTreeitem2.setExpanded(true);
		
		
		try {
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
	
	public Menu createMenu(Tree tree,final Composite cp,String s0){
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
					s="m1";
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
				public void widgetSelected(SelectionEvent e) {
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
				createPartControl(cp);
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

