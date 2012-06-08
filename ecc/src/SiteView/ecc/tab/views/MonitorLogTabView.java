package SiteView.ecc.tab.views;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oracle.net.aso.e;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import system.Collections.IEnumerator;
import system.Xml.XmlElement;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

public class MonitorLogTabView extends LayoutViewBase {
	// ����
	private static BusinessObject bo;
	private BusinessObject bo1;
	private static ICollection iCollenction;
	static Map<String, Object> map;
	static List cloumns;
	// �ؼ�
	private ToolBar toolBar;
	private Button good;
	private Button error;
	private Button waring;
	private Button disable;
	private Button all;
	private Table table_1;

	public MonitorLogTabView(Composite parent) {
		super(parent);
	}
	public void SetDataFromBusOb(BusinessObject bo) {
		bo1=bo;
	}
	//����ʼֵ
	public static void SetData(BusinessObject bo) {
		MonitorLogTabView.bo=bo;
		map=setMap(bo);
		cloumns=setCloumns(bo);
	}
	private static Map<String,Object> setMap(BusinessObject bo) {
		map = new java.util.HashMap<String, Object>();
		map.put("monitorId", bo.get_RecId());
		String time=getTwoHoursAgoTime();
		map.put("endTime", time.substring(0,time.indexOf("*")));
		map.put("startTime", time.substring(time.indexOf("*")+1));
		return map;
	}
	//��ʱ��ֵ���õ���ǰ������Сʱʱ�䣩
	public static String getTwoHoursAgoTime() {
		Calendar cal = Calendar.getInstance();
		String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
		cal.add(Calendar.HOUR, -2);
		String twoHoursAgoTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		return currentTime+"*"+twoHoursAgoTime;
	}
	//����־
	public static ICollection getLog(Map<String, Object> map) {
		ISiteviewApi siteviewApi = ConnectionBroker.get_SiteviewApi();
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery("MonitorLog", QueryInfoToGet.All);
		XmlElement[] xmls = new XmlElement[map.size()-1];
		XmlElement xml;
		int i = 0;
		if (map.get("startTime") != null) {
			xml = query.get_CriteriaBuilder().QueryListExpression(
					"CreatedDateTime", Operators.Between.name());
			query.get_CriteriaBuilder().AddLiteralValue(xml,
					map.get("startTime").toString());
			query.get_CriteriaBuilder().AddLiteralValue(xml,
					map.get("endTime").toString());
			xmls[i++] = xml;
			map.remove("startTime");
			map.remove("endTime");
		}
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			key = key.substring(0, key.indexOf("="));
			xml = query.get_CriteriaBuilder().FieldAndValueExpression(key,
					Operators.Equals, map.get(key).toString());
			xmls[i++] = xml;
		}
		query.AddOrderByDesc("CreatedDateTime");
		query.set_BusObSearchCriteria(query.get_CriteriaBuilder()
				.AndExpressions(xmls));

		ICollection iCollenction = siteviewApi.get_BusObService()
				.get_SimpleQueryResolver().ResolveQueryToBusObList(query);
		return iCollenction;
	}
	protected void createView(final Composite parent) {
		createToolbar(parent);
		iCollenction=getLog(map);
		createTable(parent,iCollenction);
		all.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(	all.getSelection()){
					SetData(bo1);
					iCollenction=getLog(map);
					createTable(parent,iCollenction);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		waring.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(	waring.getSelection()){
					SetData(bo1);
					map.put("MonitorStatus", "warning");
					iCollenction=getLog(map);
					createTable(parent,iCollenction);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		disable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(	all.getSelection()){
					SetData(bo1);
					map.put("MonitorStatus", "disable");
					iCollenction=getLog(map);
					createTable(parent,iCollenction);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		good.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(	good.getSelection()){
					SetData(bo1);
					map.put("MonitorStatus", "good");
					iCollenction=getLog(map);
					createTable(parent,iCollenction);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		error.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(	error.getSelection()){
					SetData(bo1);
					map.put("MonitorStatus", "error");
					iCollenction=getLog(map);
					createTable(parent,iCollenction);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});	
	}
    private void createToolbar(Composite parent) {
    	toolBar = new ToolBar(parent, SWT.NONE);
		toolBar.setBounds(10, 1, 505, 30);

		all = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		all.setText("ȫ��");
		all.setBounds(10, 9, 50, 20);
		all.setSelection(true);

		error = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		error.setText("����");
		error.setBounds(70, 9, 50, 20);

		waring = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		waring.setText("Σ��");
		waring.setBounds(130, 9, 50, 20);

		good = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		good.setText("����");
		good.setBounds(190, 9, 60, 20);

		disable = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		disable.setText("��ֹ");
		disable.setBounds(260, 9, 50, 20);
		
	}
	//��־���ݱ�
	public  void createTable(Composite parent,ICollection iCollection) {
		if(table_1!=null&&!table_1.isDisposed()){
			table_1.dispose();
		}
		table_1 = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBounds(0, 30, 755, 270);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		for(int i=0;i<cloumns.size();i++){
			TableColumn tblclmnNewColumn = new TableColumn(table_1, SWT.NONE);
			if(i==(cloumns.size()-1)){
				tblclmnNewColumn.setWidth(250);
			}else{
				tblclmnNewColumn.setWidth(100);
			}
			tblclmnNewColumn.setText(cloumns.get(i).toString());
		}
		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();
		while (interfaceTableIEnum.MoveNext()) {
			String [] data=new String[cloumns.size()];
			int j=0;
			BusinessObject bo = (BusinessObject) interfaceTableIEnum
					.get_Current();
			TableItem item = new TableItem(table_1, SWT.NONE);
			data[j++]=bo.GetField("CreatedDateTime").get_NativeValue().toString();
			data[j++]=bo.GetField("monitorName").get_NativeValue().toString();
			String s=bo.GetField("MonitorMassage").get_NativeValue().toString();
			if(bo.GetField("MonitorStatus").get_NativeValue().toString().equals("good")){
				List<String> massage=format(s);
				for(int n=0;n<massage.size();n++){
					data[j++]=massage.get(n).toString();
				}
			}else{
				while(j<cloumns.size()){
					data[j++]="no data";
				}
			}
			item.setText(data);
		}
		parent.layout();
	}
	//���ݲ�ͬ��������͵õ���ͷ
	private static List<String> setCloumns(BusinessObject bo2) {
		List<String> list=new ArrayList<String>();
		list.add("ʱ��");
		list.add("����");
		if(bo.get_Name().contains("ping")){
			list.add("���ɹ���(%)");
			list.add("����ʱ��(ms)");
			list.add("״ֵ̬");
		}else if(bo.get_Name().contains("DiskSpace")){
			list.add("Diskʹ����(%)");
			list.add("ʣ��ռ�(MB)");
			list.add("�ܿռ�(MB)");
		}else if(bo.get_Name().contains("Memory")){
			list.add("�ڴ�������(%)");
			list.add("�ڴ�ʣ��ռ�(MB)");
			return list;
		}else if(bo.get_Name().equals("Ecc.URL")){
			list.add("������(200=ok)");
			list.add("����ʱ��(s)");
			list.add("�ļ���С(Bytes)");
		}else if(bo.get_Name().equals("Ecc.DNS")||bo.get_Name().equals("Ecc.Mail")){
			list.add("��������ʱ��(s)");
		}else if(bo.get_Name().equals("Ecc.URLList")){
			list.add("��״̬");
			list.add("��һ��״̬");
			list.add("�ڶ���״̬");
		}else if(bo.get_Name().equals("Ecc.File")){
			list.add("�޸�ʱ��(minutes)");
			list.add("�ļ���С(bytes)");
		}else if(bo.get_Name().equals("Ecc.Service")){
			list.add("����״̬");
			list.add("������");
			list.add("ռ��cpu");
			list.add("ռ��memory");
		}
		list.add("����");
		return list;
	}
	//���ݲ�ͬ��������ͽ�����־����
	private static List<String> format(String s) {
		List<String> massage=new ArrayList<String>();
		s=s.trim();
		if(bo.get_Name().contains("ping")){
			massage.add(s.substring(s.lastIndexOf("\t")+1));
			s=s.substring(0,s.lastIndexOf("\t"));
			massage.add(s.substring(0,s.indexOf("sec")).trim());
			s=s.substring(s.indexOf("\t")+1);
			massage.add(s.substring(0,s.indexOf("\t")));
			massage.add("���ɹ���(%)="+massage.get(0));
		}else if(bo.get_Name().contains("DiskSpace")){
			massage.add(s.substring(0,s.indexOf("%")));
			s=s.substring(s.indexOf(",")+1).trim();
			massage.add(s.substring(0,s.indexOf("MB")));
			s=s.substring(s.indexOf(",")+1).trim();
			massage.add(s.substring(0,s.indexOf("MB")));
			massage.add("Diskʹ����(%)="+massage.get(0)+",�ܿռ�(MB)="+massage.get(2)+",ʣ��ռ�(MB)="+massage.get(1));
		}else if(bo.get_Name().contains("Memory")){
			massage.add(s.substring(0,s.indexOf("%")));
			s=s.substring(s.indexOf(",")+1).trim();
			massage.add(s.substring(0,s.indexOf("MB")));
		}else if(bo.get_Name().equals("Ecc.URL")){
			String s1=s.substring(0,s.indexOf("sec"));
			s=s.substring(s.indexOf("\t")+1);
			massage.add(s.substring(0,s.indexOf("\t")+1).trim());
			massage.add(s1.trim());
			s=s.substring(s.indexOf("\t")+1);
			s=s.substring(s.indexOf("\t")+1);
			s=s.substring(s.indexOf("\t")+1);
			massage.add(s.substring(0,s.indexOf("\t")));
			massage.add("������="+massage.get(0)+",����ʱ��(s)="+massage.get(1)+",�ļ���С(Bytes)="+massage.get(2));
		}else if(bo.get_Name().equals("Ecc.URLList")){
			massage.add("200");
			massage.add("");
			massage.add("");
			massage.add("����ʱ��(ms)="+s.substring(s.indexOf("avg")+4,s.indexOf("ms")).trim());
		}else if(bo.get_Name().equals("Ecc.File")){
			massage.add(s.substring(s.lastIndexOf("\t")+1));
			s=s.substring(0,s.indexOf("\t")).trim();
			massage.add(s.substring(s.lastIndexOf("\t")+1,s.indexOf("bytes")));
			long time=System.currentTimeMillis()-Long.parseLong(massage.get(0))*60*1000;
			String lastSaveTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
			massage.add("�ļ���С(bytes)="+massage.get(1)+",�ϴ��޸�ʱ��="+lastSaveTime);
		}else if(bo.get_Name().equals("Ecc.Service")){
			massage.add(s.substring(0,s.indexOf(",")));
			s=s.substring(s.lastIndexOf(massage.get(0)));
			s=s.substring(s.indexOf("\t")+1).trim();
			massage.add(s.substring(0,s.indexOf("\t")));
			s=s.substring(s.indexOf("\t")+1);
			massage.add(s.substring(0,s.indexOf("\t")));
			massage.add(s.substring(s.lastIndexOf("\t")+1));
			massage.add("����״̬="+massage.get(0));
		}
		return massage;
	}
}
