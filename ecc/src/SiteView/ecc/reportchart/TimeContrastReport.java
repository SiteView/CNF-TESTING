package SiteView.ecc.reportchart;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;
import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.tab.views.MonitorLogTabView;
import SiteView.ecc.tools.ArrayTool;
import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
import Siteview.Api.BusinessObject;

import siteview.windows.forms.LayoutViewBase;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Drawing.IconConverter;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

public class TimeContrastReport extends LayoutViewBase{
	private int sel;//�ȶԷ�ʽ��0Ϊ����ȶԣ�1Ϊ���ܱȶԣ�2Ϊ���±ȶԣ�
	private BusinessObject bo1=null;
	public static BusinessObject bo;
	private Map<String, Object> map=null;//��һ��ʱ��εĲ�ѯ����
	private Map<String, Object> map1=null;//�ڶ���ʱ��εĲ�ѯ����
	private String sstartTime=null;//��һ��ʱ��εĿ�ʼʱ��
	private String sendTime=null;//��һ��ʱ��εĽ���ʱ��
	private String estartTime=null;//�ڶ���ʱ��εĿ�ʼʱ��
	private String eendTime=null;//�ڶ���ʱ��εĽ���ʱ��
	private ICollection icCollection;//��һ��ʱ��ε���־
	private ICollection icCollection1;//�ڶ���ʱ��ε���־
	public  static List<Map<String, List<String>>> xyDataArrayList = new ArrayList<Map<String, List<String>>>();
	public List<Map<String, List<String>>> xydate;//��һ��ʱ��εı�������Դ
	public List<Map<String, List<String>>> xydate1;//��һ��ʱ��εı�������Դ
	List<Map<String, List<String>>> des=null;//��һʱ��� ������ֵ�ģ����������ֵ��ƽ��ֵ������ֵ����Сֵ�����ֵʱ�䣩����
	List<Map<String, List<String>>> des1=null;//�ڶ�ʱ��� ������ֵ�ģ����������ֵ��ƽ��ֵ������ֵ����Сֵ�����ֵʱ�䣩����
	private String firstTime=null;
	private String lastTime=null;
	
	
	public TimeContrastReport(Composite parent) {
		super(parent);
	}
	//����tab
	public void createView(final Composite parent) {
		parent.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
			}
			public void controlMoved(ControlEvent e) {
				create(parent);
			}
		});
	}

	public void SetDataFromBusOb(BusinessObject bo) {
	}
	
	public static void setData(BusinessObject bo){
		TimeContrastReport.bo=bo;
	}
	//�ֱ�����ʱ��εĲ�ѯ������ʱ�䡢��־ֵ���������ݳ�ʼ����
	public  void setAllData(){
		map=new HashMap<String,Object>();
		map1=new HashMap<String,Object>();
		if(bo1==null){
			bo1=bo;
			sel=0;
		}
		if(sstartTime==null||sendTime==null||estartTime==null||eendTime==null){
			String time=MonitorLogTabView.getHoursAgoTime(24);
			String time1=time.substring(0,time.indexOf("*"));
			time=time.substring(time.indexOf("*") + 1);
			sstartTime=time.substring(0,time.indexOf(" "))+" 00:00:00";
			sendTime=time.substring(0,time.indexOf(" "))+" 23:59:59";
			estartTime=time1.substring(0,time1.indexOf(" "))+" 00:00:00";
			eendTime=time1.substring(0,time1.indexOf(" "))+" 23:59:59";
			firstTime=sstartTime;
			lastTime=eendTime;
		}
		map.put("monitorId", bo1.get_RecId());
		map1.put("monitorId", bo1.get_RecId());
		map.put("startTime", sstartTime);
		map.put("endTime", sendTime);
		map1.put("startTime", estartTime);
		map1.put("endTime", eendTime);
		icCollection=MonitorLogTabView.getLog(map);
		icCollection1=MonitorLogTabView.getLog(map1);
		des=analyticLogReturnMap(bo1.get_Name(),icCollection);
		xydate=xyDataArrayList;
		des1=analyticLogReturnMap(bo1.get_Name(),icCollection1);
		xydate1=xyDataArrayList;
	}
	//ͨ������monitortype ���ļ� ����iCollection ����List����
	public static List<Map<String, List<String>>> analyticLogReturnMap(String monitortype, ICollection iCollection) {
		List<Map<String, List<String>>> reportDescList = new ArrayList<Map<String, List<String>>>();// ������ֵ�����ֵ��ƽ��ֵ������ֵ����
		xyDataArrayList=new ArrayList<Map<String, List<String>>>();
		String filePath = FileTools.getRealPath("\\files\\MonitorTemplate.properties");
		String templateString = Config.getReturnStr(filePath, monitortype);
		if (templateString==null) {
			templateString = "isdraw=0,?=?";
		}
		String[] templateArray = templateString.split(",");//������Ҫ��ʾ��������λ�úͷ���ֵ
		String isdraw = templateArray[0].substring(templateArray[0].indexOf("=")+1, templateArray[0].length());//��Ҫչʾ�������������λ��
		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();
		List<String> otherDataArrayList = new ArrayList<String>();//��Ҫ��ʾ�ı�������Դ
		StringBuffer descsf = new StringBuffer(); 
		while (interfaceTableIEnum.MoveNext()) {
			String newvalue="";
			BusinessObject bo = (BusinessObject) interfaceTableIEnum.get_Current();
			String logtime=bo.GetField("CreatedDateTime").get_NativeValue().toString();
			String loginfo=bo.GetField("MonitorMassage").get_NativeValue().toString();
			String [] logArrayStr = loginfo.split("\t");
			String descValue = "";
				for (int k = 0; k < logArrayStr.length; k++) {
						String str = logArrayStr[k];
						if (!str.startsWith("no data")) {
							String key = str.substring(0, str.indexOf("="));
							String otherValue = str.substring((str.indexOf("=")+1), str.length());
							if (otherValue.equals("ok")) {
								otherValue = "200";
							}
							descValue = descValue+key+"="+otherValue+"$"+logtime+",";
						}
						if (k==logArrayStr.length-1) {
							descsf.append(descValue+"#");
						}
				}
			}
			String[] descArray = descsf.toString().split("#");
			List<List<String>> strarray = new ArrayList<List<String>>();
			for (int i = 0; i < templateArray.length-1; i++) {
				List<String> strlist = new ArrayList<String>();
				strarray.add(strlist);
			}
			for (int i = 0; i < descArray.length; i++) {
				String indexstr = descArray[i];
				String[] strs = new String[templateArray.length-1];
				List<String> xydata=new ArrayList<String>();
				
				for (int k = 1; k < templateArray.length; k++) {
					String ss = templateArray[k].substring(0, templateArray[k].indexOf("="));
					strs[k-1] = ss;
				}
				for (int k = 0; k<strs.length; k++) {
					List<String> lsist = strarray.get(k);
					if (indexstr.contains(strs[k])) {
						String fs = indexstr.substring(indexstr.indexOf(strs[k]));
						lsist.add(fs.substring(fs.indexOf("=")+1, fs.indexOf(",")));
					}
				}
			}
			
			Map<String,String> timeLogValueMap = new HashMap<String, String>();
			for (int i = 0; i < strarray.size(); i++) {
				double[] arrayintarray = new double[strarray.get(i).size()];
				int h = 0;
				for (String str : strarray.get(i)) {
					String valuekey = str.substring(0, str.indexOf("$"));
					String timeValue = str.substring(str.indexOf("$")+1,str.length());
					if(valuekey.equals("failed")||valuekey.equals("n/a")||valuekey.equals("no data")){
						valuekey=0+"";
					}
					arrayintarray[h++] = Double.parseDouble(valuekey);
					timeLogValueMap.put(valuekey, timeValue);
				}
			List<String> otherIntArrayList = new ArrayList<String>();
			if (arrayintarray.length > 0) {
				double arraymax = ArrayTool.getDoubleArrayMax(arrayintarray);// ��ȡ���ֵ
				double arrayavg = ArrayTool.getDoubleArrayAvg(arrayintarray);// ��ȡƽ��ֵ
				double arraymin = ArrayTool.getDoubleArrayMin(arrayintarray);//��ȡ��Сֵ
				otherIntArrayList.add(String.valueOf(arraymax));//���ֵ(�±�0)
				otherIntArrayList.add(String.valueOf(arrayavg));//ƽ��ֵ(�±�1)
				otherIntArrayList.add(String.valueOf(arrayintarray[0]));// ����ֵ(�±�2)
				otherIntArrayList.add(String.valueOf(arraymin));//��Сֵ(�±�3)
				if (timeLogValueMap.get(String.valueOf(arraymax))==null) {
					otherIntArrayList.add(timeLogValueMap.get(String.valueOf((int)arraymax)));//���ֵʱ��(�±�4)
				}else{
					otherIntArrayList.add(timeLogValueMap.get(String.valueOf(arraymax)));//���ֵʱ��(�±�4)
				}
				timeLogValueMap.clear();
			}
			    String mapkey = templateArray[i+1].substring(templateArray[i+1].indexOf("=")+1, templateArray[i+1].length());
				Map<String, List<String>> arrayListMap = new HashMap<String, List<String>>();// ���ÿһ�з���ֵ�Ͳ���
				Map<String, List<String>> descarrayListMap = new HashMap<String, List<String>>();// ���ÿһ����������
				arrayListMap.put(mapkey, otherIntArrayList);
				descarrayListMap.put(mapkey, strarray.get(i));
				reportDescList.add(arrayListMap);
				xyDataArrayList.add(descarrayListMap);
		}
		return reportDescList;
	}
	//ÿ�θ����з���������
	public  void createItem(Table table,Map<String,List<String>> map,Map<String,List<String>> map2){
		String[] date=new String[5];
		TableItem tableItem=new TableItem(table, SWT.NONE);
		List<String> list=new ArrayList<String>();
		if(map.keySet().iterator().hasNext()){
			list=map.get(map.keySet().iterator().next());
		}
		if(list.size()>=4){
			date[0]=bo1.get_Name()+"("+sstartTime+" ~ "+sendTime+")";
			date[1]=list.get(0);
			date[2]=list.get(1);
			date[3]=list.get(3);
			date[4]=list.get(4);
		}
		tableItem.setText(date);
		TableItem tableItem1=new TableItem(table, SWT.NONE);
		if(map.keySet().iterator().hasNext()){
			list=map.get(map.keySet().iterator().next());
		}
		if(list.size()>=4){
			date[0]=bo1.get_Name()+"("+estartTime+" ~ "+eendTime+")";
			date[1]=list.get(0);
			date[2]=list.get(1);
			date[3]=list.get(3);
			date[4]=list.get(4);
		}
		tableItem1.setText(date);
		
	}
	public static void main(String[] args){
		Calendar cal=Calendar.getInstance();
		cal.set(2012, 6, 7);
		cal.add(Calendar.MONTH, -1);
		System.out.println(new SimpleDateFormat("yyyy-MM")
		.format(cal.getTime()));
		System.out.println();
	}
	
	public  XYDataset createDataset(List<String> xydata1,List<String> xydata,String s,String ss) {
		if(xydata1.size()<=0){
			return null;
		}
		TimeSeries s1 = new TimeSeries(s,Second.class);
		TimeSeries s2 = new TimeSeries(ss, Second.class);
		String s0=xydata1.get(0);;
		
	    s0=s0.substring(s0.indexOf("$")+1);
		String ye =s0.substring(0,s0.indexOf("/"));
		String mo =s0.substring(s0.indexOf("/")+1,s0.lastIndexOf("/"));
		String da =s0.substring(s0.lastIndexOf("/")+1,s0.indexOf(" "));
		
		for(int i=0;i<xydata1.size();i++){
			s0=xydata1.get(i);
			String sss=s0.substring(0,s0.indexOf("$"));
			if(sss.contains("n/a")||sss.contains("no data")||sss.contains("failed")){
				sss=0+"";
			}
			double value=Double.parseDouble(sss);
			s0=s0.substring(s0.indexOf("$")+1);
			String ho = s0.substring(s0.indexOf(" ")+1,s0.indexOf(":"));
			String mi = s0.substring(s0.indexOf(":")+1,s0.lastIndexOf(":"));
			if(sel==2){
				da=s0.substring(s0.lastIndexOf("/")+1,s0.indexOf(" "));
			}
			int se = Integer.parseInt(s0.substring(s0.lastIndexOf(":")+1));
			Minute m = new Minute(Integer.parseInt(mi),
					Integer.parseInt(ho), Integer.parseInt(da),
					Integer.parseInt(mo), Integer.parseInt(ye));
				s1.add(new Second(se,m),value);
		}
		for(int i=0;i<xydata.size();i++){
			 s0=xydata.get(i);
			String sss=s0.substring(0,s0.indexOf("$"));
			if(sss.contains("n/a")||sss.contains("no data")||sss.contains("failed")){
				sss=0+"";
			}
			double value=Double.parseDouble(sss);
			s0=s0.substring(s0.indexOf("$")+1);
			String ho = s0.substring(s0.indexOf(" ")+1,s0.indexOf(":"));
			String mi = s0.substring(s0.indexOf(":")+1,s0.lastIndexOf(":"));
			if(sel==2){
				da=s0.substring(s0.lastIndexOf("/")+1,s0.indexOf(" "));
			}
			int se = Integer.parseInt(s0.substring(s0.lastIndexOf(":")+1));
			Minute m = new Minute(Integer.parseInt(mi),
					Integer.parseInt(ho), Integer.parseInt(da),
					Integer.parseInt(mo), Integer.parseInt(ye));
				s2.add(new Second(se,m),value);
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);

        return dataset;
	}
	public JFreeChart createChart(XYDataset dataset, String xname,String yname,String titile) {
		 JFreeChart chart = ChartFactory.createTimeSeriesChart(
		            titile,  // title
		            xname,             // x-axis label
		            yname,   // y-axis label
		            dataset,            // data
		            true,               // create legend?
		            true,               // generate tooltips?
		            false               // generate URLs?
		        );
		        chart.setBackgroundPaint(Color.white);

		        XYPlot plot = (XYPlot) chart.getPlot();
		        DateAxis axis = (DateAxis) plot.getDomainAxis();
		        if(sel==0){
		        	axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		        }else if(sel==2){
		        	axis.setDateFormatOverride(new SimpleDateFormat("dd"));
		        }else{
		        	
		        }
		        return chart;
	}
	public JFreeChart create(CategoryDataset dataset, String xname,String yname,String titile){
		 JFreeChart chart=ChartFactory.createLineChart(xname,null, yname, dataset, PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}
	public CategoryDataset create(){
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(212, "Classes", "JDK 1.0");
        dataset.addValue(504, "Classes", "JDK 1.1");
        dataset.addValue(1520, "Classes", "JDK 1.2");
        dataset.addValue(1842, "Classes", "JDK 1.3");
        dataset.addValue(2991, "Classes", "JDK 1.4");
        dataset.addValue(3500, "Classes", "JDK 1.5");
        return dataset;
	}
	public void create(final Composite parent){
		if (parent.getChildren().length > 0) {
			for (Control control : parent.getChildren()) {
				control.dispose();
			}
		}
		setAllData();
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		
		Composite group = new Composite(sashForm, SWT.NONE);
		group.setLayout(new FormLayout());
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		Label label = new Label(group, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		FormData fd_label = new FormData();
		fd_label.right = new FormAttachment(100, 3);
		fd_label.left = new FormAttachment(0);
		label.setLayoutData(fd_label);
		label.setText("������ѯ");
		
		Label label_1 = new Label(group, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(label, 6);
		fd_label_1.left = new FormAttachment(label, 0, SWT.LEFT);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("�ȶԷ�ʽ");
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		final Combo combo = new Combo(group, SWT.NONE);
		combo.add("����Ա�");
		combo.add("���ܶԱ�");
		combo.add("���¶Ա�");
		combo.select(sel);
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(label_1, -3, SWT.TOP);
		fd_combo.left = new FormAttachment(label_1, 2);
		combo.setLayoutData(fd_combo);
		Label label_2 = new Label(group, SWT.NONE);
		FormData fd_label_2 = new FormData();
		fd_label_2.bottom = new FormAttachment(label_1, 0, SWT.BOTTOM);
		fd_label_2.left = new FormAttachment(combo, 6);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("��һ��ʱ��:");
		label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		final DateTime dateTime = new DateTime(group, SWT.DROP_DOWN);
		String s=firstTime.substring(0,firstTime.indexOf(" "));
		int year=Integer.parseInt(s.substring(0,s.indexOf("-")));
		int month=Integer.parseInt(s.substring(s.indexOf("-")+1,s.lastIndexOf("-")));
		int day=Integer.parseInt(s.substring(s.lastIndexOf("-")+1));
		dateTime.setDate(year, month-1, day);
		FormData fd_dateTime = new FormData();
		fd_dateTime.bottom = new FormAttachment(combo, 0, SWT.BOTTOM);
		fd_dateTime.left = new FormAttachment(label_2, 1);
		dateTime.setLayoutData(fd_dateTime);
		
		Label label_3 = new Label(group, SWT.NONE);
		FormData fd_label_3 = new FormData();
		fd_label_3.bottom = new FormAttachment(label_1, 0, SWT.BOTTOM);
		fd_label_3.left = new FormAttachment(dateTime, 6);
		label_3.setLayoutData(fd_label_3);
		label_3.setText("�ڶ���ʱ��:");
		label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		final DateTime dateTime_1 = new DateTime(group, SWT.DROP_DOWN);
		s=lastTime.substring(0,lastTime.indexOf(" "));
		year=Integer.parseInt(s.substring(0,s.indexOf("-")));
		month=Integer.parseInt(s.substring(s.indexOf("-")+1,s.lastIndexOf("-")));
		day=Integer.parseInt(s.substring(s.lastIndexOf("-")+1));
		dateTime_1.setDate(year, month-1, day);
		FormData fd_dateTime_1 = new FormData();
		fd_dateTime_1.bottom = new FormAttachment(combo, 0, SWT.BOTTOM);
		fd_dateTime_1.left = new FormAttachment(label_3, 2);
		dateTime_1.setLayoutData(fd_dateTime_1);
		
		Button button = new Button(group, SWT.NONE);
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(label_1, -5, SWT.TOP);
		fd_button.left = new FormAttachment(dateTime_1, 6);
		button.setLayoutData(fd_button);
		button.setText("��ѯ");
		
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				sel=combo.getSelectionIndex();
				int year=dateTime.getYear();
				int month=dateTime.getMonth();
				int day=dateTime.getDay();
				firstTime=year+"-"+(month+1)+"-"+day+" "+"00:00:00";
				int year_1=dateTime_1.getYear();
				int month_1=dateTime_1.getMonth();
				int day_1=dateTime_1.getDay();
				lastTime=year_1+"-"+(month_1+1)+"-"+day_1+" "+"23:59:59";
				if(sel==0){
					sstartTime=year+"-"+(month+1)+"-"+day+" "+"00:00:00";
					sendTime=year+"-"+(month+1)+"-"+day+" "+"23:59:59";
					
					estartTime=year_1+"-"+(month_1+1)+"-"+day_1+" "+"00:00:00";
					eendTime=year_1+"-"+(month_1+1)+"-"+day_1+" "+"23:59:59";
				}else if(sel==1){
					Calendar cal=Calendar.getInstance();
					cal.set(year, month, day);
					int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
					cal.add(Calendar.DATE, -day_of_week);
					String s=new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
					sstartTime=s+" "+"00:00:00";
					cal.add(Calendar.DATE, 6);
					s=new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
					sendTime=s+" "+"23:59:59";
					
					cal.set(year_1, month_1, day_1);
					day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
					cal.add(Calendar.DATE, -day_of_week);
					s=new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
					estartTime=s+" "+"00:00:00";
					cal.add(Calendar.DATE, 6);
					s=new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
					eendTime=s+" "+"23:59:59";
				}else{
					if(month==month_1){
						
					}
					Calendar cal=Calendar.getInstance();
					cal.set(year, month, day);
					sstartTime=year+"-"+(month+1)+"-1"+" "+"00:00:00";
					sendTime=year+"-"+(month+1)+"-"+cal.getActualMaximum(Calendar.DAY_OF_MONTH)+" "+"23:59:59";
					
					cal.set(year_1, month_1, day);
					estartTime=year_1+"-"+(month_1+1)+"-1"+" "+"00:00:00";
					eendTime=year_1+"-"+(month_1+1)+"-"+cal.getActualMaximum(Calendar.DAY_OF_MONTH)+" "+"23:59:59";
				}
				create(parent);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(label_1, 6);
		fd_lblNewLabel.left = new FormAttachment(0);
		fd_lblNewLabel.right = new FormAttachment(100, 3);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("ͼ��");
		
		
		ScrolledComposite group_1 = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		group_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		group_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		group_1.setExpandHorizontal(true);
		group_1.setExpandVertical(true);
		group_1.setMinWidth(400);
		group_1.setMinHeight(190*(des.size()+1));
		
		Composite  chatComposite = new Composite(group_1, SWT.NONE);
		group_1.setContent(chatComposite);// ����chatComposite��scrolledComposite����
		chatComposite.setLayout(new FillLayout());
		SashForm sashForm_1 = new SashForm(chatComposite, SWT.VERTICAL);
		if(icCollection.get_Count()!=0&&icCollection1.get_Count()!=0){
			int[] s0={5,22,90};
			int[] ss=new int[3*des.size()];
			for(int i=0;i<des.size();i++){
				String yname=des.get(i).keySet().iterator().next();
				Label lblNewLabel_1 = new Label(sashForm_1, SWT.NONE);
				lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
				lblNewLabel_1.setText(yname);
				
				
				Table table_1 = new Table(sashForm_1, SWT.BORDER | SWT.FULL_SELECTION);
				table_1.setHeaderVisible(true);
				table_1.setLinesVisible(true);
				table_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
				
				TableColumn tblclmnNewColumn = new TableColumn(table_1, SWT.NONE);
				tblclmnNewColumn.setText("����");
				tblclmnNewColumn.setWidth(100);
				
				TableColumn tblclmnNewColumn_1 = new TableColumn(table_1, SWT.NONE);
				tblclmnNewColumn_1.setWidth(100);
				tblclmnNewColumn_1.setText("���ֵ");
				
				TableColumn tableColumn = new TableColumn(table_1, SWT.NONE);
				tableColumn.setWidth(100);
				tableColumn.setText("ƽ��ֵ");
				
				TableColumn tableColumn_1 = new TableColumn(table_1, SWT.NONE);
				tableColumn_1.setWidth(100);
				tableColumn_1.setText("��Сֵ");
				
				TableColumn tableColumn_2 = new TableColumn(table_1, SWT.NONE);
				tableColumn_2.setWidth(100);
				tableColumn_2.setText("���ֵʱ��");
				
				createItem(table_1, des.get(i), des.get(i));
				
				Composite composite = new Composite(sashForm_1, SWT.NONE);
				composite.setLayout(new FillLayout(SWT.HORIZONTAL));
				composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
				XYDataset xydata=createDataset(xydate.get(i).get(yname),xyDataArrayList.get(i).get(xyDataArrayList.get(i).keySet().iterator().next()),sstartTime+"~"+sendTime,estartTime+"~"+eendTime);
				JFreeChart chart=createChart(xydata, "ʱ��",yname,bo1.get_Name());
				ChartComposite frame = new ChartComposite(composite, SWT.NONE, chart, true);
				System.arraycopy(s0, 0, ss, i*3, s0.length);
			}
			sashForm_1.setWeights(ss);
		}
		sashForm.setWeights(new int[] {60, 387});
		parent.layout();
	}
}
