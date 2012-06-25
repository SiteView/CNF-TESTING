package SiteView.ecc.reportchart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


import COM.dragonflow.Api.SSAlertInstanceInfo;
import COM.dragonflow.SiteView.MonitorGroup;
import SiteView.ecc.tab.views.MonitorLogTabView;
import SiteView.ecc.tab.views.TotalTabView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StackLayout;
import swing2swt.layout.BoxLayout;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

import org.eclipse.swt.custom.SashForm;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.util.Rotation;


public class StatusCTIReport extends LayoutViewBase {
	private static BusinessObject bo;
	private BusinessObject bo1;
	private ICollection iCollenction;//日志数据
	Map<String, Object> map=null;//查询日志条件（列:值）
	String starTime=null;
	String endTime=null;
	double datas[];
	//控件
	private Table table;
	private Table table_1;
	private Text text;
	private Text text_1;
	private String string;

	
	public StatusCTIReport(Composite parent) {
		super(parent);
	}

	protected void createView(final Composite parent) {
		if (parent.getChildren().length > 0) {
			for (Control control : parent.getChildren()) {
				control.dispose();
			}
		}
		setMap();
		final String [] column={"时间","状态","持续次数","持续时间"};
		final String [] column_1={"正常","危险","错误","禁用","无数据"};
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		final SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		Composite group = new Composite(sashForm, SWT.NONE);
		group.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		Label label = new Label(group, SWT.NONE);
		label.setBounds(0, 0, 54, 12);
		label.setText("\u5F00\u59CB\u65F6\u95F4\uFF1A");
		
		final DateTime startdate = new DateTime(group, SWT.DROP_DOWN);
		int year=Integer.parseInt(starTime.substring(0,starTime.indexOf("-")));
		int month=Integer.parseInt(starTime.substring(starTime.indexOf("-")+1,starTime.lastIndexOf("-")));
		int day=Integer.parseInt(starTime.substring(starTime.lastIndexOf("-")+1,starTime.indexOf(" ")));
		int hours=Integer.parseInt(starTime.substring(starTime.indexOf(" ")+1,starTime.indexOf(":")));
		int minutes=Integer.parseInt(starTime.substring(starTime.indexOf(":")+1,starTime.lastIndexOf(":")));
		int seconds=Integer.parseInt(starTime.substring(starTime.lastIndexOf(":")+1));
		startdate.setDate(year, month-1, day);
		startdate.setBounds(0, 0, 84, 20);
		final DateTime starttime = new DateTime(group, SWT.TIME);
		starttime.setTime(hours, minutes, seconds);
		starttime.setBounds(0, 0, 84, 20);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 54, 12);
		lblNewLabel.setText("\u7ED3\u675F\u65F6\u95F4\uFF1A");
		
		year=Integer.parseInt(endTime.substring(0,endTime.indexOf("-")));
		month=Integer.parseInt(endTime.substring(endTime.indexOf("-")+1,endTime.lastIndexOf("-")));
		day=Integer.parseInt(endTime.substring(endTime.lastIndexOf("-")+1,endTime.indexOf(" ")));
		hours=Integer.parseInt(endTime.substring(endTime.indexOf(" ")+1,endTime.indexOf(":")));
		minutes=Integer.parseInt(endTime.substring(endTime.indexOf(":")+1,endTime.lastIndexOf(":")));
		seconds=Integer.parseInt(endTime.substring(endTime.lastIndexOf(":")+1));
		final DateTime enddate = new DateTime(group, SWT.DROP_DOWN);
		enddate.setDate(year, month-1, day);
		enddate.setBounds(0, 0, 84, 20);
		final DateTime endtime = new DateTime(group, SWT.TIME);
		endtime.setTime(hours, minutes, seconds);
		endtime.setBounds(0, 0, 84, 20);
		
		Button button = new Button(group, SWT.NONE);
		button.setBounds(0, 0, 72, 22);
		button.setText("\u67E5\u8BE2");
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
		
		refurbishView(sashForm,column,column_1); 
	}
	private void refurbishView(SashForm sashForm, String[] column,
			String[] column_1) {
		Label lbl= new Label(sashForm, SWT.NONE);
		lbl.setFont(SWTResourceManager.getFont("宋体", 7, SWT.NORMAL));
		lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lbl.setText("\u72B6\u6001\u5217\u8868");
		
		table=createTable(sashForm,column);
		
		Label lblNewLabel_1 = new Label(sashForm, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_1.setText("\u72B6\u6001\u5206\u5E03\u56FE");
		
		Composite comp= new Composite(sashForm, SWT.BORDER);
		comp.setLayout(new FillLayout(SWT.VERTICAL));
		DefaultCategoryDataset datast = new DefaultCategoryDataset();
        JFreeChart chart_0= ChartFactory.createStackedBarChart("", "", "", datast, PlotOrientation.HORIZONTAL, true, true, false);
        ChartComposite frame_0 = new ChartComposite(comp, SWT.NONE, chart_0, true);
        
		Label lblNewLabel_2 = new Label(sashForm, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_2.setText("\u72B6\u6001\u7EDF\u8BA1\u5217\u8868");
		
		table_1=createTable(sashForm,column_1);
		TableItem(table,table_1);
		
		Label lblNewLabel_3 = new Label(sashForm, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_3.setText("\u72B6\u6001\u7EDF\u8BA1\u56FE");
		
		Composite c= new Composite(sashForm, SWT.BORDER);
		c.setLayout(new FillLayout(SWT.VERTICAL));
		DefaultPieDataset dataset = new DefaultPieDataset();
		 dataset.setValue("error", datas[2]);
		 dataset.setValue("warning", datas[1]);
		 dataset.setValue("good", datas[0]);
		 dataset.setValue("disable", datas[3]);
		 dataset.setValue("nodata", datas[4]);
		 JFreeChart chart=ChartFactory.createPieChart("a", dataset, true, true, false);
		 ChartComposite frame = new ChartComposite(c, SWT.NONE, chart, true);
		sashForm.setWeights(new int[] {40,15,100,15, 50, 15, 60, 15, 200});
	}

	protected Table createTable(SashForm sashForm,String[] column){
		Table table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		for(int i=0;i<column.length;i++){
			TableColumn tblclmnNewColumn = new TableColumn(table, SWT.CENTER);
			tblclmnNewColumn.setWidth(150);
			tblclmnNewColumn.setText(column[i]);
		}
		return table;
	}
	
	protected void TableItem(Table table,Table table_1){
		int s=iCollenction.get_Count();
		int j=0;
		int good_Count=0;
		int error_Count=0;
		int disable_Count=0;
		int warning_Count=0;
		int nodata_Count=0;
		BusinessObject[] bos=new BusinessObject[s];
		IEnumerator tableItems= iCollenction.GetEnumerator();
		while(tableItems.MoveNext()){
			BusinessObject bo = (BusinessObject) tableItems
					.get_Current();
			String status=bo.GetField("MonitorStatus").get_NativeValue().toString();
			if(status.equals("good")){
				good_Count+=1;
			}else if(status.equals("error")){
				error_Count+=1;
			}else if(status.equals("disable")){
				disable_Count+=1;
			}else if(status.equals("warning")){
				warning_Count+=1;
			}else{
				nodata_Count+=1;
			}
			bos[j++]=bo;
		}
		//208-225 把日志数据时间顺序倒放入bos数组中
		
		String[] statusdata=new String[4];
		String status=null;//上次数据的状态
		List<BusinessObject> itsms=new ArrayList();//按时间先后顺序放入的日志
		Map<String,Integer> stand_counts=new HashMap();
		for(int n=s-1;n>=0;n--){
			if(stand_counts.size()==5){
				continue;
			}
			if(status==null){
				status=bos[n].GetField("MonitorStatus").get_NativeValue().toString();
				itsms.add(bos[n]);
				stand_counts.put(status, 1);
				continue;
			}
			if(!status.equals(bos[n].GetField("MonitorStatus").get_NativeValue().toString())
			&& stand_counts.get(bos[n].GetField("MonitorStatus").get_NativeValue().toString())==null){
				status=bos[n].GetField("MonitorStatus").get_NativeValue().toString();
				itsms.add(bos[n]);
				stand_counts.put(status, 1);
			}else{
				status=bos[n].GetField("MonitorStatus").get_NativeValue().toString();
				if(stand_counts.get(status)==null){
					break;
				}
				int i=stand_counts.get(status)+1;
				stand_counts.put(status, i);
			}
			
			
		}
		
		for(int n=0;n<itsms.size();n++){
			status=itsms.get(n).GetField("MonitorStatus").get_NativeValue().toString();
			statusdata[0]=itsms.get(n).GetField("CreatedDateTime").get_NativeValue().toString();
			statusdata[1]=status;
			statusdata[2]=stand_counts.get(status)+"";
			if(n==(itsms.size()-1)){
				Date d=new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				statusdata[3]=getTime(statusdata[0],sdf.format(d));
			}else{
				statusdata[3]=getTime(statusdata[0],itsms.get(n+1).GetField("CreatedDateTime").get_NativeValue().toString());	
			}
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(statusdata);
		}
		
		TableItem item = new TableItem(table_1, SWT.NONE);
		String[] data = new String[5];
		if(s!=0){
			datas=new double[5];
			data[0]=TotalTabView.percent(good_Count,s);
			datas[0]=Double.parseDouble(data[0].substring(0,data[0].indexOf("%")));
			
			data[1]=TotalTabView.percent(warning_Count,s);
			datas[1]=Double.parseDouble(data[1].substring(0,data[1].indexOf("%")));
			
			data[2]=TotalTabView.percent(error_Count,s);
			datas[2]=Double.parseDouble(data[2].substring(0,data[2].indexOf("%")));
			
			data[3]=TotalTabView.percent(disable_Count,s);
			datas[3]=Double.parseDouble(data[3].substring(0,data[3].indexOf("%")));
			
			data[4]=TotalTabView.percent(nodata_Count,s);
			datas[4]=Double.parseDouble(data[4].substring(0,data[4].indexOf("%")));
		}
		item.setText(data);
	}
	public static String getTime(String s,String s1){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.US);
		String ss=null;
		try {
			Date d = sdf.parse(s);
			Date d1 = sdf.parse(s1);
			long time=(d1.getTime()-d.getTime())/1000;
			if((time/(3600*24))>0){
				ss=time/(3600*24)+"天";
				ss+=(time%(3600*24))/3600+"小时";
				ss+=((time%(3600*24))%3600)/60+"分钟";
			}else if((time/3600)>0){
				ss=(time%(3600*24))/3600+"小时";
				ss+=((time%(3600*24))%3600)/60+"分钟";
			}else{
				ss=time/60+"分钟";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ss;
	}
	public void SetDataFromBusOb(BusinessObject bo) {
	}
	public static void setData(BusinessObject bo) {
		StatusCTIReport.bo = bo;
	}
	public void setMap(){
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
		iCollenction = MonitorLogTabView.getLog(map);
	}
}
