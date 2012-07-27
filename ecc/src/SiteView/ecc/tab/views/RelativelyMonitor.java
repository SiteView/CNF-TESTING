package SiteView.ecc.tab.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
import SiteView.ecc.views.EccTreeControl;
import Siteview.Api.BusinessObject;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;

import siteview.windows.forms.LayoutViewBase;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class RelativelyMonitor extends LayoutViewBase{
	public static BusinessObject bo;
	private Table table;
	private Table table_1;
	Table table_2;
	public RelativelyMonitor(Composite parent) {
		super(parent);
	}
	@Override
	public void createView(final Composite parent) {
		parent.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
			}
			public void controlMoved(ControlEvent e) {
				createTab(parent);
			}
		});
	}
	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
	}
	
	public void createTab(Composite parent){
		if (parent.getChildren().length > 0) {
			for (Control control : parent.getChildren()) {
				control.dispose();
			}
		}
		parent.setLayout(new FillLayout());
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Label lblNewLabel_1 = new Label(sashForm, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_1.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NORMAL));
		lblNewLabel_1.setText("\u8BBE\u5907");
		
		table_2 = new Table(sashForm, SWT.NONE);
		table_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table_2.setHeaderVisible(true);
		table_2.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(table_2, SWT.CENTER);
		tableColumn.setWidth(110);
		tableColumn.setText("\u8BBE\u5907\u5730\u5740");
		TableColumn tableColumn_1 = new TableColumn(table_2, SWT.CENTER);
		tableColumn_1.setWidth(110);
		tableColumn_1.setText("\u6807\u9898");
		TableColumn tableColumn_2 = new TableColumn(table_2, SWT.CENTER);
		tableColumn_2.setWidth(110);
		tableColumn_2.setText("\u8FDE\u63A5\u65B9\u5F0F");
		TableColumn tableColumn_3 = new TableColumn(table_2, SWT.CENTER);
		tableColumn_3.setWidth(119);
		tableColumn_3.setText("\u72B6\u6001");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table_2, SWT.CENTER);
		tblclmnNewColumn_2.setWidth(110);
		tblclmnNewColumn_2.setText("\u521B\u5EFA\u65F6\u95F4");
		String filePath = FileTools
				.getRealPath("\\files\\HostName.properties");
		String hostname= Config.getReturnStr(filePath,bo.get_Name());
		hostname=bo.GetField(hostname).get_NativeValue().toString();
		if(hostname!=null&&!hostname.equals("")){
			ICollection iCollection=FileTools.getBussCollection("ServerAddress", hostname, "RemoteMachine");
			IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();
			while (interfaceTableIEnum.MoveNext()) {
				BusinessObject machine = (BusinessObject) interfaceTableIEnum.get_Current();
				TableItem tableItem=new TableItem(table_2, SWT.NONE);
				String[] s=new String[5];
				s[0]=hostname;
				s[1]=machine.GetField("Title").get_NativeValue().toString();
				s[2]=machine.GetField("ConnectionMethod").get_NativeValue().toString();
				s[3]=machine.GetField("Status").get_NativeValue().toString();
				s[4]=machine.GetField("CreatedDateTime").get_NativeValue().toString();
				tableItem.setText(s);
				tableItem.setData(machine);
			}
		}
		Label lblNewLabel_2 = new Label(sashForm, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NORMAL));
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_2.setText("\u76F8\u540CIP\u76D1\u6D4B\u5668");
		
		table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		Label lblNewLabel_3 = new Label(sashForm, SWT.NONE);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("宋体", 10, SWT.NORMAL));
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_3.setText("\u6709\u4F9D\u8D56\u5173\u7CFB\u76D1\u6D4B\u5668");
		
		table_1 = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		TableColumn tableColumn_4 = new TableColumn(table_1, SWT.CENTER);
		tableColumn_4.setWidth(100);
		tableColumn_4.setText("\u521B\u5EFA\u65F6\u95F4");
		TableColumn tblclmnNewColumn = new TableColumn(table_1, SWT.CENTER);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("\u6807\u9898");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("\u7EC4");
		TableColumn tblclmnNewColumn_1 = new TableColumn(table_1, SWT.CENTER);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("\u4F9D\u8D56\u6761\u4EF6");
		TableColumn tableColumn_5 = new TableColumn(table_1, SWT.CENTER);
		tableColumn_5.setWidth(100);
		tableColumn_5.setText("\u5173\u7CFB");
		String dependson=bo.GetField("dependson").get_NativeValue().toString();
		if(dependson!=null&&!dependson.equals("")){
			dependson=dependson.substring(dependson.indexOf(" ")+1);
			BusinessObject monitor=EccTreeControl.CreateBo(dependson, "Ecc");
			if(monitor!=null){
				TableItem tabitem=new TableItem(table_1, SWT.NONE);
				tabitem.setData(monitor);
				String[] s=new String[5];
				s[0]=monitor.GetField("CreatedDateTime").get_NativeValue().toString();
				s[1]=monitor.GetField("title").get_NativeValue().toString();
				s[2]=monitor.GetField("Groups").get_NativeValue().toString();
				s[3]=monitor.GetField("dependscondition").get_NativeValue().toString();
				s[4]="被依赖";
				tabitem.setText(s);
			}
		}
		ICollection iCollection_1=FileTools.getBussCollection("dependson",bo.GetFieldOrSubfield("Groups_valid").get_NativeValue().toString()+" "+bo.get_RecId(), "Ecc");	
		IEnumerator interfaceTableIEnum = iCollection_1.GetEnumerator();
		while (interfaceTableIEnum.MoveNext()) {
			BusinessObject monitor = (BusinessObject) interfaceTableIEnum.get_Current();
			String s[]=new String[5];
			s[0]=monitor.GetField("CreatedDateTime").get_NativeValue().toString();
			s[1]=monitor.GetField("title").get_NativeValue().toString();
			s[2]=monitor.GetField("Groups").get_NativeValue().toString();
			s[3]=monitor.GetField("dependscondition").get_NativeValue().toString();
			s[4]="依赖于";
			TableItem tableItem_2=new TableItem(table_1, SWT.NONE);
			tableItem_2.setData(monitor);
			tableItem_2.setText(s);
		}
		sashForm.setWeights(new int[] {10, 20, 10, 50, 10, 50});
	}
}
