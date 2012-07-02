package SiteView.ecc.reportchart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.tab.views.MonitorLogTabView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ErrorCTIReport extends LayoutViewBase {
	public static BusinessObject bo;
	private BusinessObject bo1=null;
	private ICollection iCollenction;//日志数据
	Map<String, Object> map=null;//查询日志条件（列:值）
	String starTime=null;//查询日志的开始时间
	String endTime=null;//查询日志的结束时间
	
	private Table table;
	
	public ErrorCTIReport(Composite parent) {
		super(parent);
	}

	protected void createView(final Composite parent) {
		setAll();
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("开始时间：");
		
		final DateTime startdate = new DateTime(composite, SWT.DROP_DOWN);
		int year=Integer.parseInt(starTime.substring(0,starTime.indexOf("-")));
		int month=Integer.parseInt(starTime.substring(starTime.indexOf("-")+1,starTime.lastIndexOf("-")));
		int day=Integer.parseInt(starTime.substring(starTime.lastIndexOf("-")+1,starTime.indexOf(" ")));
		int hours=Integer.parseInt(starTime.substring(starTime.indexOf(" ")+1,starTime.indexOf(":")));
		int minutes=Integer.parseInt(starTime.substring(starTime.indexOf(":")+1,starTime.lastIndexOf(":")));
		int seconds=Integer.parseInt(starTime.substring(starTime.lastIndexOf(":")+1));
		startdate.setDate(year, month-1, day);
		final DateTime starttime = new DateTime(composite, SWT.TIME);
		starttime.setTime(hours, minutes, seconds);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setText("结束时间：");
		
		year=Integer.parseInt(endTime.substring(0,endTime.indexOf("-")));
		month=Integer.parseInt(endTime.substring(endTime.indexOf("-")+1,endTime.lastIndexOf("-")));
		day=Integer.parseInt(endTime.substring(endTime.lastIndexOf("-")+1,endTime.indexOf(" ")));
		hours=Integer.parseInt(endTime.substring(endTime.indexOf(" ")+1,endTime.indexOf(":")));
		minutes=Integer.parseInt(endTime.substring(endTime.indexOf(":")+1,endTime.lastIndexOf(":")));
		seconds=Integer.parseInt(endTime.substring(endTime.lastIndexOf(":")+1));
		final DateTime enddate = new DateTime(composite, SWT.DROP_DOWN);
		enddate.setDate(year, month-1, day);
		final DateTime endtime = new DateTime(composite, SWT.TIME);
		endtime.setTime(hours, minutes, seconds);
		
		Button button = new Button(composite, SWT.NONE);
		button.setText("查询");
		button.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				starTime= startdate.getYear() + "-"
						+ (startdate.getMonth() + 1) + "-" + startdate.getDay()
						+ " " + starttime.getHours() + ":"
						+ starttime.getMinutes()+":"+starttime.getSeconds();
				endTime= enddate.getYear() + "-"
						+ (enddate.getMonth() + 1) + "-" + enddate.getDay()
						+ " " + endtime.getHours() + ":"
						+ endtime.getMinutes()+":"+endtime.getSeconds();
				createView(parent);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(122);
		tblclmnNewColumn.setText("错误开始时间");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(126);
		tblclmnNewColumn_1.setText("错误结束时间");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(166);
		tblclmnNewColumn_2.setText("错误状态值");
		createItem(table);
		sashForm.setWeights(new int[] {36, 261});
		parent.layout(); 
	}

	public void SetDataFromBusOb(BusinessObject bo) {
	}

	public static void setData(BusinessObject bo){
		ErrorCTIReport.bo=bo;
	}
	public void setAll(){
		if(starTime==null||endTime==null){
			bo1=bo;
			String time =MonitorLogTabView.getHoursAgoTime(24);
			starTime=time.substring(time.indexOf("*") + 1);
			endTime=time.substring(0,time.indexOf("*"));
		}
		map= new java.util.HashMap<String, Object>();
		map.put("endTime", endTime);
		map.put("startTime",starTime);
		map.put("monitorId", bo1.get_RecId());
		iCollenction=MonitorLogTabView.getLog(map);
	}
	public void createItem(Table table){
		IEnumerator tableItems= iCollenction.GetEnumerator();
		List<String> statuslist=new ArrayList<String>();
		List<String> timelist=new ArrayList<String>();
		String status=null;
		while(tableItems.MoveNext()){
			BusinessObject bo = (BusinessObject) tableItems.get_Current();
			if(status==null){
				statuslist.add(bo.GetField("MonitorStatus").get_NativeValue().toString());
				timelist.add(bo.GetField("CreatedDateTime").get_NativeValue().toString());
			}else if(!status.equals(bo.GetField("MonitorStatus").get_NativeValue().toString())){
				statuslist.add(bo.GetField("MonitorStatus").get_NativeValue().toString());
				timelist.add(bo.GetField("CreatedDateTime").get_NativeValue().toString());
			}
			status=bo.GetField("MonitorStatus").get_NativeValue().toString();
		}
		Map<String,String> map=new HashMap<String,String>();
		String[] data=new String[3];
		for(int i=0;i<statuslist.size()-1;i+=2){
			if(statuslist.get(i).equals("error")){
				TableItem tableItem=new TableItem(table,SWT.NONE);
				data[0]=timelist.get(i);
				data[1]=timelist.get(i+1);
				data[2]="status=error";
				tableItem.setText(data);
			}
		}
		if(statuslist.get(statuslist.size()-1).equals("error")){
			TableItem tableItem=new TableItem(table,SWT.NONE);
			data[0]=timelist.get(statuslist.size()-1);
			data[1]=endTime;
			data[2]="status=error";
			tableItem.setText(data);
		}
	}
}
