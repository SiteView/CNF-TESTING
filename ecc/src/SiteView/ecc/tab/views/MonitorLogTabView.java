package SiteView.ecc.tab.views;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import system.Collections.IEnumerator;
import system.Xml.XmlElement;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

public class MonitorLogTabView extends LayoutViewBase {
	// 数据
	private static BusinessObject bo;
	private static ICollection iCollenction;
	static Map<String, Object> map;
	static List cloumns;
	// 控件
	private ToolBar toolBar;
	private Button good;
	private Button error;
	private Button waring;
	private Button disable;
	private Button all;
	private static Table table_1;

	public MonitorLogTabView(Composite parent) {
		super(parent);
	}

	@Override
	protected void createView(Composite parent) {
		// TODO Auto-generated method stub
		toolBar = new ToolBar(parent, SWT.NONE);
		toolBar.setBounds(10, 1, 505, 30);

		all = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		all.setText("全部");
		all.setBounds(10, 9, 50, 20);
		all.setSelection(true);

		error = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		error.setText("错误");
		error.setBounds(70, 9, 50, 20);

		waring = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		waring.setText("危险");
		waring.setBounds(130, 9, 50, 20);

		good = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		good.setText("正常");
		good.setBounds(190, 9, 60, 20);

		disable = new Button(toolBar, SWT.RADIO | SWT.LEFT);
		disable.setText("禁止");
		disable.setBounds(260, 9, 50, 20);
		
		table_1 = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBounds(0, 30, 755, 270);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);

		createTable(iCollenction);
	}
	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
	}

	public static void SetData(BusinessObject bo) {
		MonitorLogTabView.bo=bo;
		map=setMap(bo);
		cloumns=setCloumns(bo);
		iCollenction=getLog(map);
	}
	
	private static List<String> setCloumns(BusinessObject bo2) {
		List<String> list=new ArrayList<String>();
		list.add("时间");
		list.add("名称");
		if(bo.get_Name().contains("ping")){
			list.add("包成功率(%)");
			list.add("往返时间(ms)");
			list.add("状态值");
		}else if(bo.get_Name().contains("DiskSpace")){
			list.add("Disk使用率(%)");
			list.add("剩余空间(MB)");
			list.add("总空间(MB)");
		}else if(bo.get_Name().contains("Memory")){
			list.add("内存利用率(%)");
			list.add("内存剩余空间(MB)");
			return list;
		}else if(bo.get_Name().equals("Ecc.URL")){
			list.add("返回码(200=ok)");
			list.add("下载时间(s)");
			list.add("文件大小(Bytes)");
		}else if(bo.get_Name().equals("Ecc.DNS")||bo.get_Name().equals("Ecc.Mail")){
			list.add("数据往返时间(s)");
		}else if(bo.get_Name().equals("Ecc.URLList")){
			list.add("总状态");
			list.add("第一步状态");
			list.add("第二步状态");
		}
		list.add("描述");
		return list;
	}

	private static Map<String,Object> setMap(BusinessObject bo) {
		map = new java.util.HashMap<String, Object>();
		map.put("monitorId", bo.get_RecId());
		String time=getTwoHoursAgoTime();
		map.put("endTime", time.substring(0,time.indexOf("*")));
		map.put("startTime", time.substring(time.indexOf("*")+1));
		return map;
	}

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

	public static void createTable(ICollection iCollection) {
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
	}
	
	private static List<String> format(String s) {
		List<String> massage=new ArrayList<String>();
		s=s.trim();
		if(bo.get_Name().contains("ping")){
			massage.add(s.substring(s.lastIndexOf("\t")+1));
			s=s.substring(0,s.lastIndexOf("\t"));
			massage.add(s.substring(0,s.indexOf("sec")).trim());
			s=s.substring(s.indexOf("\t")+1);
			massage.add(s.substring(0,s.indexOf("\t")));
			massage.add("包成功率(%)="+massage.get(0));
		}else if(bo.get_Name().contains("DiskSpace")){
			massage.add(s.substring(0,s.indexOf("%")));
			s=s.substring(s.indexOf(",")+1).trim();
			massage.add(s.substring(0,s.indexOf("MB")));
			s=s.substring(s.indexOf(",")+1).trim();
			massage.add(s.substring(0,s.indexOf("MB")));
			massage.add("Disk使用率(%)="+massage.get(0)+",总空间(MB)="+massage.get(2)+",剩余空间(MB)="+massage.get(1));
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
			massage.add("返回码="+massage.get(0)+",下载时间(s)="+massage.get(1)+",文件大小(Bytes)="+massage.get(2));
		}else if(bo.get_Name().equals("Ecc.URLList")){
			massage.add("200");
			massage.add("");
			massage.add("");
			massage.add("往返时间(ms)="+s.substring(s.indexOf("avg")+4,s.indexOf("ms")).trim());
		}
		return massage;
	}

	public static String getTwoHoursAgoTime() {
		Calendar cal = Calendar.getInstance();
		String currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
		cal.add(Calendar.HOUR, -2);
		String twoHoursAgoTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		return currentTime+"*"+twoHoursAgoTime;
	}
}
