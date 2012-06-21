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
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


import COM.dragonflow.SiteView.MonitorGroup;
import SiteView.ecc.tab.views.MonitorLogTabView;
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


public class StatusCTIReport extends LayoutViewBase {
	private static BusinessObject bo;
	private BusinessObject bo1;
	private static ICollection iCollenction;//日志数据
	static Map<String, Object> map;//查询日志条件（列:值）
	
	//控件
	private Table table;
	private Table table_1;
	private Text text;

	
	public StatusCTIReport(Composite parent) {
		super(parent);
	}

	protected void createView(Composite parent) {
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		Composite group = new Composite(sashForm, SWT.NONE);
		group.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		Label label = new Label(group, SWT.NONE);
		label.setBounds(0, 0, 54, 12);
		label.setText("\u5F00\u59CB\u65F6\u95F4\uFF1A");
		DateTime dateTime = new DateTime(group, SWT.DATE);
		Calendar cal = Calendar.getInstance();
		dateTime.setDay(cal.get(Calendar.DAY_OF_MONTH) - 1);
		dateTime.setBounds(0, 0, 84, 20);
		DateTime dateTime_1 = new DateTime(group, SWT.TIME);
		dateTime_1.setBounds(0, 0, 84, 20);
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 54, 12);
		lblNewLabel.setText("\u7ED3\u675F\u65F6\u95F4\uFF1A");
		DateTime dateTime_2 = new DateTime(group, SWT.DATE);
		dateTime_2.setBounds(0, 0, 84, 20);
		DateTime dateTime_3 = new DateTime(group, SWT.TIME);
		dateTime_3.setBounds(0, 0, 84, 20);
		Button button = new Button(group, SWT.NONE);
		button.setBounds(0, 0, 72, 22);
		button.setText("\u67E5\u8BE2");
		
		Label lbl= new Label(sashForm, SWT.NONE);
		lbl.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lbl.setText("\u72B6\u6001\u5217\u8868");
		
		String [] column={"时间","状态","持续次数","持续时间"};
		String [] column_1={"正常","危险","错误","禁用","无数据"};
		
		table=createTable(sashForm,column);
		
		Label lblNewLabel_1 = new Label(sashForm, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_1.setText("\u72B6\u6001\u5206\u5E03\u56FE");
		
		text = new Text(sashForm, SWT.BORDER);
		
		Label lblNewLabel_2 = new Label(sashForm, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_2.setText("\u72B6\u6001\u7EDF\u8BA1\u5217\u8868");
		
		table_1=createTable(sashForm,column_1);
		TableItem(table,table_1);
		
		Label lblNewLabel_3 = new Label(sashForm, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		lblNewLabel_3.setText("\u72B6\u6001\u7EDF\u8BA1\u56FE");
		
		sashForm.setWeights(new int[] {40,20,120,20, 50, 20, 60, 80});
		
	}
	protected Table createTable(SashForm sashForm,String[] column){
		Table table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		for(int i=0;i<column.length;i++){
			TableColumn tblclmnNewColumn = new TableColumn(table, SWT.CENTER);
			tblclmnNewColumn.setWidth(100);
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
		String[] statusdata=new String[4];
		String status=null;
		List<BusinessObject> itsms=new ArrayList();
		Map<String,Integer> stand_counts=new HashMap();
		List<String> st=new ArrayList();
		for(int n=s-1;n>=0;n--){
			if(status==null){
				status=bos[n].GetField("MonitorStatus").get_NativeValue().toString();
				itsms.add(bo);
				st.add(status);
			}
			
			if(status.equals(bos[n].GetField("MonitorStatus").get_NativeValue().toString())){
				if(stand_counts.get(status)!=null){
					int i=stand_counts.get(status)+1;
					stand_counts.put(status, i);
				}else{
					stand_counts.put(status, 1);
				}
				continue;
			}else{
				itsms.add(bo);
				status=bos[n].GetField("MonitorStatus").get_NativeValue().toString();
				st.add(status);
				continue;
			}
		}
		
		for(int n=0;n<itsms.size();n++){
			status=st.get(n);
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
		data[0]=good_Count/s*100+"%";
		data[1]=warning_Count/s*100+"%";
		data[2]=error_Count/s*100+"%";
		data[3]=disable_Count/s*100+"%";
		data[4]=nodata_Count/s*100+"%";
		item.setText(data);
	}
	public static String getTime(String s,String s1){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.US);
		String ss=null;
		try {
			Date d = sdf.parse(s);
			Date d1 = sdf.parse(s1);
			long time=(d.getTime()-d1.getTime())/1000;
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
		map = new java.util.HashMap<String, Object>();
		map.put("monitorId", bo.get_RecId());
		String time =MonitorLogTabView.getHoursAgoTime(24);
		map.put("endTime", time.substring(0, time.indexOf("*")));
		map.put("startTime", time.substring(time.indexOf("*") + 1));
		iCollenction=MonitorLogTabView.getLog(map);
	}
}
