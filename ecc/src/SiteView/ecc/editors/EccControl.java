package SiteView.ecc.editors;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import SiteView.ecc.data.MonitorServer;
import SiteView.ecc.dialog.ParticularInfo;
import SiteView.ecc.reportchart.StatusCTIReport;
import SiteView.ecc.reportchart.TimeContrastReport;
import SiteView.ecc.tab.views.MonitorLogTabView;
import SiteView.ecc.tab.views.RelativelyMonitor;
import SiteView.ecc.tab.views.TotalTabView;
import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
import SiteView.ecc.views.EccReportView;
import SiteView.ecc.views.EccTreeControl;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.TabFolder;

import core.busobmaint.BusObMaintView;
/**
 * 点击左边树 展开在右边的 editor 监测器列表
 * @author Administrator
 *
 */

public class EccControl extends EditorPart {
	public EccControl() {
	}
	MonitorServer mg=new MonitorServer();
	public static final String ID = "SiteView.ecc.editors.EccControl";
	public static TableItem item=null;
	
	public BusinessObject bo1=null;
	private Table toptable;
	TabFolder tabFolder ;
	private Composite c_1;

	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		final SashForm sashForm = new SashForm(parent, SWT.BORDER);
		sashForm.setOrientation(SWT.VERTICAL);
		Label lable=new Label(sashForm,SWT.NONE);
		lable.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lable.setText("监测器");
		Composite c=new Composite(sashForm, SWT.NONE);
		c.setVisible(true);
		c.setLayout(new FillLayout());
		createTable(c);
		final Composite c1=new Composite(sashForm, SWT.NONE);
		c1.setLayout(new FillLayout());
		tab(c1,bo1);
		toptable.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				item=toptable.getItem(new Point(e.x, e.y));
				if(e.button==3){
					final Menu menu=getMenu(toptable);
					menu.setLocation(toptable.toDisplay(e.x, e.y));
					menu.setVisible(true);
				}
			}
		});
		toptable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(item!=e.item){
					item=(TableItem) e.item;
					BusinessObject bo=(BusinessObject)item.getData();
					if(bo!=null){
						tab(c1,bo);
					}
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		sashForm.setWeights(new int[] {10, 157, 289});
		parent.layout();
	}
	public  void createTable(Composite c) {
		if (toptable != null && !toptable.isDisposed()) {
			toptable.dispose();
		}
		toptable = new Table(c, SWT.FULL_SELECTION);
		toptable.setLinesVisible(true);
		toptable.setHeaderVisible(true);
		toptable.setBackground(new Color(null, 255, 255, 255));
		
		TableColumn newColumnTableColumn_top = new TableColumn(toptable,
				SWT.NONE );
		newColumnTableColumn_top.setWidth(80);
		newColumnTableColumn_top.setText("状态");
		TableColumn newColumnTableColumn_top2 = new TableColumn(toptable,SWT.NONE);
		newColumnTableColumn_top2.setWidth(100);
		newColumnTableColumn_top2.setText("名称");
		TableColumn newColumnTableColumn_top3 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top3.setWidth(400);
		newColumnTableColumn_top3.setText("描述");
		TableColumn newColumnTableColumn_top4 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top4.setWidth(200);
		newColumnTableColumn_top4.setText("最后更新");
		ICollection iCollection=FileTools.getBussCollection("groupid",EccTreeControl.item.getText(),"EccDyn");
		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();
		while (interfaceTableIEnum.MoveNext()) {
			String[] data=new String [4];
			BusinessObject bo = (BusinessObject) interfaceTableIEnum.get_Current();
			String monitorid=bo.GetField("monitorid").get_NativeValue().toString();
			String type=bo.GetField("MonitorType").get_NativeValue().toString();
			String filePath = FileTools
					.getRealPath("\\files\\siteview9.2_itsm.properties");
			type= Config.getReturnStr(filePath,type);
			if(type==null||type.equals("")){
				type="Ecc";
			}else{
				type="Ecc."+type;
			}
			BusinessObject monitorbo=EccTreeControl.CreateBo(monitorid, type);
			if(monitorbo==null){
				continue;
			}
			String monitorname = bo.GetField("monitorName").get_NativeValue().toString();
			data[0] = bo.GetField("category").get_NativeValue().toString();
			data[1]=monitorname.substring(monitorname.indexOf("：")+1);
			String desc =bo.GetField("monitorDesc").get_NativeValue().toString();
			if(bo1==null){
				bo1=monitorbo;
			}
			data[2]=format(desc,monitorbo.GetField("EccType").get_NativeValue().toString());
			data[3] = bo.GetField("LastModDateTime").get_NativeValue().toString();
			TableItem tableItem=new TableItem(toptable, SWT.NONE);
			tableItem.setData(monitorbo);
			tableItem.setText(data);
			item=tableItem;
		}
	}
	private static String format(String desc2,String type) {
		desc2+="*";
		String filePath = FileTools
				.getRealPath("\\files\\MonitorLogTabView.properties");
		String s1 = Config.getReturnStr(filePath, "Ecc."+type);
		if(s1==null){
			return desc2;
		}
		String[] s2=s1.split(",");
		s1="";
		for(int i=0;i<s2.length;i++){
			String s0=s2[i].substring(s2[i].indexOf(":")+1);
			if(desc2.contains(s0+"=")){
				String s=desc2.substring(desc2.indexOf(s0+"="));
				s=s.substring(s.indexOf("=")+1,s.indexOf("*"));
				s1+=s2[i].substring(0,s2[i].indexOf(":"))+"="+s+" ";
			}
		}
		return s1;
	}
	//建立下边tab页
	public void tab(Composite composite,BusinessObject bo){
		if (tabFolder != null && !tabFolder.isDisposed()) {
			tabFolder.dispose();
		}
		tabFolder= new TabFolder(composite, SWT.FULL_SELECTION);
		String time = MonitorLogTabView.getHoursAgoTime(2);
		TotalTabView.startTime = time.substring(time.indexOf("*") + 1);
		TotalTabView.endTime = time.substring(0, time.indexOf("*"));
		if(bo!=null){
			TotalTabView.setTotalData(bo);
		    TabItem comaTabItem = new TabItem(tabFolder, SWT.NONE);  
	        comaTabItem.setText("概要");  
	        Composite c=new Composite(tabFolder, SWT.FULL_SELECTION);
	        EccReportView erv = new EccReportView();
			erv.createPartControl(c);
	        comaTabItem.setControl(c);  
	        
	        TabItem comaTabItem_2 = new TabItem(tabFolder, SWT.NONE);
	     	comaTabItem_2.setText("相关监测器");
	     	Composite c2=new Composite(tabFolder, SWT.FULL_SELECTION);
	     	RelativelyMonitor.bo=bo;
	     	RelativelyMonitor mo=new RelativelyMonitor(c2);
	     	mo.createView(c2);
	     	comaTabItem_2.setControl(c2);  
	     			
	        
	        TabItem comaTabItem_3 = new TabItem(tabFolder, SWT.NONE);
			  comaTabItem_3.setText("日志数据");
			  Composite c3=new Composite(tabFolder, SWT.FULL_SELECTION);
			  MonitorLogTabView molog=new MonitorLogTabView(c3);
			  MonitorLogTabView.SetData(bo);
			  molog.createView(c3);
			  comaTabItem_3.setControl(c3);        
		}
		composite.layout();
	}
	public void doSave(IProgressMonitor arg0) {}
	public void doSaveAs() {}
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);// 设置site
		this.setInput(input);// 设置输入的IEditorInput对象
		this.setPartName(input.getName());// 设置编辑器上方显示的名称
	}
	public  Menu getMenu(Table tableItem){
		Menu menu=new Menu(tableItem);
		MenuItem m1=new MenuItem(menu,SWT.NONE);
		m1.setText("编辑监测器");
		MenuItem m2=new MenuItem(menu,SWT.NONE);
		m2.setText("详细信息");
		MenuItem m3=new MenuItem(menu,SWT.NONE);
		m3.setText("刷新监测器");
		MenuItem m4=new MenuItem(menu,SWT.NONE);
		m4.setText("删除监测器");
		m1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				BusObMaintView.open(ConnectionBroker.get_SiteviewApi(),(BusinessObject)item.getData());
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		m2.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ParticularInfo par=new ParticularInfo(null);
				par.open();
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		m3.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				BusinessObject bo=(BusinessObject)item.getData();
				bo.RefreshAdHocRelationships();
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		m4.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				BusinessObject bo=(BusinessObject)item.getData();
				bo.DeleteObject(ConnectionBroker.get_SiteviewApi());
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		return menu;
	}
	public boolean isDirty() {
		return false;
	}
	public boolean isSaveAsAllowed() {
		return false;
	}
	public void setFocus() {}
}
