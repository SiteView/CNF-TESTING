package SiteView.ecc.tab.views;

import java.sql.ResultSet;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.editors.ContentProvider;
import SiteView.ecc.editors.TableLabelProvider;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import siteview.windows.forms.LayoutViewBase;
import system.Collections.ICollection;
import system.Xml.XmlElement;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;


public class MonitorLogTabView extends LayoutViewBase {
	//数据
	private ICollection iCollenction;
	//控件
	private ToolBar toolBar;
	private Button good;
	private Button error;
	private Button waring;
	private Button disable;
	private Button noData;
	private Table table_1;
	
	
	public MonitorLogTabView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createView(Composite parent) {
		// TODO Auto-generated method stub
		toolBar=new ToolBar(parent, SWT.NONE);
		toolBar.setBounds(10, 1, 505, 30);
		
		good=new Button(toolBar, SWT.RADIO|SWT.LEFT);
		good.setText("全部");
		good.setBounds(10, 9, 50, 20);
		
		error=new Button(toolBar, SWT.RADIO|SWT.LEFT);
		error.setText("错误");
		error.setBounds(70, 9, 50, 20);
		
		waring=new Button(toolBar, SWT.RADIO|SWT.LEFT);
		waring.setText("危险");
		waring.setBounds(130, 9,50, 20);
		
		noData=new Button(toolBar, SWT.RADIO|SWT.LEFT);
		noData.setText("正常");
		noData.setBounds(190, 9, 60, 20);
		
		disable=new Button(toolBar, SWT.RADIO|SWT.LEFT);
		disable.setText("禁止");
		disable.setBounds(260, 9, 50, 20);
		
		table_1 = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBounds(0, 30, 505, 270);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("描述");
		
		TableColumn tableColumn = new TableColumn(table_1, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("时间");
		
		 TableColumn tblclmnNewColumn_1 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("名称");
		
		 TableItem item = new TableItem(table_1, SWT.NONE);
		 item.setText( new String[] { "a", "b", "c" } );
		 
	}

	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
		iCollenction=MonitorLogTabView.getLog(bo.get_RecId());
	}
	
	
	public static ICollection getLog(String id){
		ISiteviewApi siteviewApi=ConnectionBroker.get_SiteviewApi();
		BusinessObject bo;
		SiteviewQuery siteviewquery_interfaceTable = new SiteviewQuery();
		siteviewquery_interfaceTable.AddBusObQuery("MonitorLog", QueryInfoToGet.All);
		XmlElement xmlElementscanconfigid = siteviewquery_interfaceTable.get_CriteriaBuilder().FieldAndValueExpression(
		"monitorId", Operators.Equals, id);  
		siteviewquery_interfaceTable.set_BusObSearchCriteria(xmlElementscanconfigid);
		ICollection interfaceTableCollection = siteviewApi.get_BusObService().get_SimpleQueryResolver()
		.ResolveQueryToBusObList(siteviewquery_interfaceTable);
		
		return interfaceTableCollection;
	}
}
