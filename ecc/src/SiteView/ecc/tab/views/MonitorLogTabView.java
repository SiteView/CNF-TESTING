package SiteView.ecc.tab.views;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

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

		format(iCollenction);
	}
	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
	}

	public static void SetData(BusinessObject bo) {
		MonitorLogTabView.bo=bo;
		map=setMap(bo);
		iCollenction=getLog(map);
	}
	
	private static Map<String,Object> setMap(BusinessObject bo) {
		map = new java.util.HashMap();
		map.put("monitorId", bo.get_RecId());
		String time=getTwoHoursAgoTime();
		map.put("startTime", time.substring(time.indexOf("*")+1));
		map.put("endTime", time.substring(0,time.indexOf("*")));
//		map.put("startTime", "2012-06-04 18:17:26");
//		map.put("endTime", "2012-06-05 15:32:13");
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
		Iterator iterator = map.entrySet().iterator();
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

	public static void format(ICollection iCollection) {
		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();
		while (interfaceTableIEnum.MoveNext()) {
			BusinessObject bo = (BusinessObject) interfaceTableIEnum
					.get_Current();
			TableItem item = new TableItem(table_1, SWT.NONE);
			item.setText(new String[] { bo.GetField("monitorStatus").get_NativeValue().toString()
					, bo.GetField("CreatedDateTime").get_NativeValue().toString()
					, bo.GetField("monitorName").get_NativeValue().toString() });
		}
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
