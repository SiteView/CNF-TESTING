package SiteView.ecc.views;

import java.awt.Dialog;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import COM.dragonflow.SiteViewException.SiteViewException;
import SiteView.ecc.data.MonitorGroup;
import SiteView.ecc.editors.EccControl;
import SiteView.ecc.editors.EccControlInput;
import Siteview.Windows.Forms.controlproperties.DropDownMenu;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;


public class EccTreeControl extends ViewPart {
	public EccTreeControl() {
	}
	MonitorGroup mg=new MonitorGroup();

	public static final String ID = "SiteView.ecc.views.EccTreeControl";
	@Override
	public void createPartControl(Composite cp) {
		// TODO Auto-generated method stub
		cp.setLayout(new FillLayout());	
		final Tree tree = new Tree(cp, SWT.BORDER);
		final Menu menu=new Menu(tree);
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
		MenuItem m6=new MenuItem(menu,SWT.NONE);
		m6.setText("禁止");
		MenuItem m7=new MenuItem(menu,SWT.NONE);
		m7.setText("批量删除");
		MenuItem m8=new MenuItem(menu,SWT.NONE);
		m8.setText("批量启用");
		MenuItem m9=new MenuItem(menu,SWT.NONE);
		m9.setText("批量禁用");
		MenuItem m10=new MenuItem(menu,SWT.NONE);
		m10.setText("批量刷新");
		MenuItem m11=new MenuItem(menu,SWT.NONE);
		m11.setText("批量移动");
		MenuItem m12=new MenuItem(menu,SWT.NONE);
		m12.setText("帮助");
		tree.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.button==3){
					menu.setLocation(tree.toDisplay(e.x, e.y));
					menu.setVisible(true);
				}else if((e.button!=1)&&(e.button!=3)){
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().openEditor(new EccControlInput(),EccControl.ID);
					} catch (PartInitException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		TreeItem trtmNewTreeitem = new TreeItem(tree, SWT.NONE);
		trtmNewTreeitem.setText("整体视图");
		trtmNewTreeitem.setExpanded(true);
		try {
			List groups=mg.Group();
			for(int i=0;i<groups.size();i++){
				Map<String,Object> maps=(Map<String, Object>) groups.get(i);
				getTreeItem(trtmNewTreeitem,maps);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SiteViewException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getTreeItem(TreeItem treeItem,Map<String,Object> map) throws RemoteException, SiteViewException{
		TreeItem tree=new TreeItem(treeItem, SWT.NONE);
		tree.setText(map.get("_name").toString());
		tree.setExpanded(true);
		List<Map<String,Object>> list=mg.GroupChild(map.get("_id").toString());
		if(list.size()!=0){
			for(int i=0;i<list.size();i++){
				getTreeItem(tree,list.get(i));
			}
		}
	}

	@Override
	public  void setFocus() {
		// TODO Auto-generated method stub
	}
}

