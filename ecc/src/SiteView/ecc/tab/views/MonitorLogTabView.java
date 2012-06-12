package SiteView.ecc.tab.views;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import COM.dragonflow.Page.indexPage;
import SiteView.ecc.editors.ContentProvider;
import SiteView.ecc.editors.TableLabelProvider;
import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
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
	// 数据
	private static BusinessObject bo;
	private BusinessObject bo1;
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
	private Table table_1;

	public MonitorLogTabView(Composite parent) {
		super(parent);
	}

	public void SetDataFromBusOb(BusinessObject bo) {
		bo1 = bo;
	}

	// 赋初始值
	public static void SetData(BusinessObject bo) {
		MonitorLogTabView.bo = bo;
		map = setMap(bo);
		cloumns = setCloumns(bo.get_Name());
	}

	private static Map<String, Object> setMap(BusinessObject bo) {
		map = new java.util.HashMap<String, Object>();
		map.put("monitorId", bo.get_RecId());
		String time = getTwoHoursAgoTime();
		map.put("endTime", time.substring(0, time.indexOf("*")));
		map.put("startTime", time.substring(time.indexOf("*") + 1));
		return map;
	}

	// 赋时间值（得到当前及两个小时时间）
	public static String getTwoHoursAgoTime() {
		Calendar cal = Calendar.getInstance();
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		cal.add(Calendar.HOUR, -2);
		String twoHoursAgoTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(cal.getTime());
		return currentTime + "*" + twoHoursAgoTime;
	}

	// 查日志
	public static ICollection getLog(Map<String, Object> map) {
		ISiteviewApi siteviewApi = ConnectionBroker.get_SiteviewApi();
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery("MonitorLog", QueryInfoToGet.All);
		XmlElement[] xmls = new XmlElement[map.size() - 1];
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
		iCollenction = getLog(map);
		createTable(parent, iCollenction);
		all.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (all.getSelection()) {
					SetData(bo1);
					iCollenction = getLog(map);
					createTable(parent, iCollenction);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		waring.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (waring.getSelection()) {
					SetData(bo1);
					map.put("MonitorStatus", "warning");
					iCollenction = getLog(map);
					createTable(parent, iCollenction);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		disable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (disable.getSelection()) {
					SetData(bo1);
					map.put("MonitorStatus", "disable");
					iCollenction = getLog(map);
					createTable(parent, iCollenction);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		good.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (good.getSelection()) {
					SetData(bo1);
					map.put("MonitorStatus", "good");
					iCollenction = getLog(map);
					createTable(parent, iCollenction);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		error.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (error.getSelection()) {
					SetData(bo1);
					map.put("MonitorStatus", "error");
					iCollenction = getLog(map);
					createTable(parent, iCollenction);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createToolbar(Composite parent) {
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

	}
	// 日志数据表
	public void createTable(Composite parent, ICollection iCollection) {
		if (table_1 != null && !table_1.isDisposed()) {
			table_1.dispose();
		}
		table_1 = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBounds(0, 30, 755, 270);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);

		for (int i = 0; i < cloumns.size(); i++) {
			TableColumn tblclmnNewColumn = new TableColumn(table_1, SWT.NONE);
			if (i == (cloumns.size() - 1)) {
				tblclmnNewColumn.setWidth(250);
			} else {
				tblclmnNewColumn.setWidth(100);
			}
			tblclmnNewColumn.setText(cloumns.get(i).toString());
		}

		IEnumerator interfaceTableIEnum = iCollection.GetEnumerator();
		while (interfaceTableIEnum.MoveNext()) {
			String[] data = new String[cloumns.size()];
			int j = 0;
			BusinessObject bo = (BusinessObject) interfaceTableIEnum
					.get_Current();
			TableItem item = new TableItem(table_1, SWT.NONE);
			data[j++] = bo.GetField("CreatedDateTime").get_NativeValue()
					.toString();
			data[j++] = bo.GetField("monitorName").get_NativeValue().toString();
			String s = bo.GetField("MonitorMassage").get_NativeValue()
					.toString();
			if (bo.GetField("MonitorStatus").get_NativeValue().toString()
					.equals("good")) {
				List<String> massage = formatItem(s);
				for (int n = 0; n < massage.size(); n++) {
					data[j++] = massage.get(n).toString();
				}
			} else {
				while (j < cloumns.size()) {
					data[j++] = "no data";
				}
			}
			item.setText(data);
		}
		parent.layout();
	}
	//解析日志文件monitorMassage字段（公用方法）
	private List<String> formatItem(String s) {
		List<String> massage = new ArrayList<String>();
		String[] s1 = s.split("\t");
		for (String s0 : s1) {
			massage.add(s0.substring(s0.indexOf("=") + 1));
		}
		massage.add(s);
		return massage;
	}
	//获取表列
	private static List<String> setCloumns(String s) {
		List<String> list = new ArrayList<String>();
		list.add("时间");
		list.add("名称");
		String filePath = FileTools
				.getRealPath("\\files\\MonitorLogTabView.properties");
		s = Config.getReturnStr(filePath, s);
		if (s!=null) {
			String[] column = s.split(",");
			for (String s1 : column) {
				list.add(s1);
			}
		}
		list.add("描述");
		return list;
	}
}