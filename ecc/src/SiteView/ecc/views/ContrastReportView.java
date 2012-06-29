package SiteView.ecc.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import SiteView.ecc.reportchart.EccReportChart;
import SiteView.ecc.tab.views.MonitorLogTabView;
import SiteView.ecc.tab.views.TotalTabView;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
public class ContrastReportView extends ViewPart {
	public String startTimeStr = "";
	public String endTimeStr = "";
	public ContrastReportView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		refreshComposite(parent);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	public void refreshComposite(final Composite parent) {
		if (parent.getChildren().length > 0) {
			for (Control control : parent.getChildren()) {
				control.dispose();
			}
		}
		parent.setLayout(new FillLayout());
		parent.setBackground(new Color(null, 255, 255, 255));
		SashForm reportForm = new SashForm(parent, SWT.BORDER);
		reportForm.setOrientation(SWT.VERTICAL);
		reportForm.setLayout(new FillLayout());
		
		TotalTabView.startTime = startTimeStr;
		TotalTabView.endTime = endTimeStr;
		TotalTabView.setTotalData(TotalTabView.businessObj);
		
		Composite  queryComposite = new Composite(reportForm, SWT.NONE);
		queryComposite.setBackground(new Color(null, 255, 255, 255));
		queryComposite.setLayout(new FormLayout());
		Label querylabel = new Label(queryComposite, SWT.NONE);
		FormData fd_querylabel = new FormData();
		fd_querylabel.right = new FormAttachment(100);
		querylabel.setLayoutData(fd_querylabel);
		querylabel.setBackground(new Color(null, 191, 198, 216));
		querylabel.setText("查询条件");

		Label startlabel = new Label(queryComposite, SWT.NONE);
		fd_querylabel.bottom = new FormAttachment(startlabel, -6);
		fd_querylabel.left = new FormAttachment(startlabel, 0, SWT.LEFT);
		FormData fd_startlabel = new FormData();
		fd_startlabel.top = new FormAttachment(0, 18);
		fd_startlabel.left = new FormAttachment(0);
		startlabel.setLayoutData(fd_startlabel);
		startlabel.setBackground(new Color(null, 255, 255, 255));
		startlabel.setText("开始时间：");
		final DateTime startDate = new DateTime(queryComposite, SWT.DROP_DOWN);
		FormData fd_startDate = new FormData();
		fd_startDate.top = new FormAttachment(startlabel, 0, SWT.TOP);
		fd_startDate.left = new FormAttachment(startlabel, 6);
		startDate.setLayoutData(fd_startDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.US);
		Date startDateTime  = new Date();
		Date endDateTime  = new Date();
		try {
			if (startTimeStr.equals("")||endTimeStr.equals("")) {
				String time = MonitorLogTabView.getHoursAgoTime(2);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
			}
			 startDateTime = sdf.parse(startTimeStr);
			 endDateTime = sdf.parse(endTimeStr);
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Calendar startcal = Calendar.getInstance();
		startcal.setTime(startDateTime);
		startDate.setYear(startcal.get(Calendar.YEAR));
		startDate.setMonth(startcal.get(Calendar.MONTH));
		startDate.setDay(startcal.get(Calendar.DAY_OF_MONTH));
		final DateTime startTime = new DateTime(queryComposite, SWT.TIME | SWT.SHORT);
		FormData fd_startTime = new FormData();
		fd_startTime.top = new FormAttachment(querylabel, 6);
		fd_startTime.left = new FormAttachment(startDate, 6);
		startTime.setLayoutData(fd_startTime);
		startTime.setHours(startcal.get(Calendar.HOUR_OF_DAY));
		startTime.setMinutes(startcal.get(Calendar.MINUTE));
		startTime.setSeconds(startcal.get(Calendar.SECOND));

		Label endlabel = new Label(queryComposite, SWT.NONE);
		FormData fd_endlabel = new FormData();
		fd_endlabel.left = new FormAttachment(startDate, 78);
		fd_endlabel.top = new FormAttachment(startlabel, 0, SWT.TOP);
		endlabel.setLayoutData(fd_endlabel);
		endlabel.setBackground(new Color(null, 255, 255, 255));
		endlabel.setText("结束时间：");
		final DateTime endDate = new DateTime(queryComposite, SWT.DROP_DOWN);
		FormData fd_endDate = new FormData();
		fd_endDate.top = new FormAttachment(startlabel, 0, SWT.TOP);
		fd_endDate.left = new FormAttachment(endlabel, 6);
		endDate.setLayoutData(fd_endDate);
		Calendar endcal = Calendar.getInstance();
		endcal.setTime(endDateTime);
		endDate.setYear(endcal.get(Calendar.YEAR));
		endDate.setMonth(endcal.get(Calendar.MONTH));
		endDate.setDay(endcal.get(Calendar.DAY_OF_MONTH));
		final DateTime endTime = new DateTime(queryComposite, SWT.TIME | SWT.SHORT);
		endTime.setHours(endcal.get(Calendar.HOUR_OF_DAY));
		endTime.setMinutes(endcal.get(Calendar.MINUTE));
		endTime.setSeconds(endcal.get(Calendar.SECOND));
		FormData fd_endTime = new FormData();
		fd_endTime.bottom = new FormAttachment(startDate, 0, SWT.BOTTOM);
		endTime.setLayoutData(fd_endTime);
		Button queryBtn = new Button(queryComposite, SWT.PUSH);
		fd_endTime.right = new FormAttachment(queryBtn, -6);
		FormData fd_queryBtn = new FormData();
		fd_queryBtn.left = new FormAttachment(endDate, 66);
		fd_queryBtn.top = new FormAttachment(startlabel, -5, SWT.TOP);
		queryBtn.setLayoutData(fd_queryBtn);
		queryBtn.setText("查询");
		queryBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				startTimeStr = startDate.getYear() + "-"
						+ (startDate.getMonth() + 1) + "-" + startDate.getDay()
						+ " " + startTime.getHours() + ":"
						+ startTime.getMinutes() + ":" + startTime.getSeconds();
				endTimeStr = endDate.getYear() + "-" + (endDate.getMonth() + 1)
						+ "-" + endDate.getDay() + " " + endTime.getHours()
						+ ":" + endTime.getMinutes() + ":"
						+ endTime.getSeconds();
				refreshComposite(parent);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Label runninglabel = new Label(queryComposite, SWT.NONE);
		FormData fd_runninglabel = new FormData();
		fd_runninglabel.top = new FormAttachment(startDate, 6);
		fd_runninglabel.left = new FormAttachment(querylabel, 0, SWT.LEFT);
		fd_runninglabel.right = new FormAttachment(100);
		runninglabel.setLayoutData(fd_runninglabel);
		runninglabel.setBackground(new Color(null, 191, 198, 216));
		runninglabel.setText("运行报表情况");

		TableViewer tableViewer = new TableViewer(queryComposite, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		Table table = tableViewer.getTable();
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(runninglabel, 0);
		table.setLayoutData(fd_table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBackground(new Color(null, 255, 255, 255));
		TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(250);
		newColumnTableColumn.setText("名称");
		TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(250);
		newColumnTableColumn_1.setText("返回值名称");
		TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(100);
		newColumnTableColumn_2.setText("最大值");
		TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_3.setWidth(100);
		newColumnTableColumn_3.setText("平均值");
		TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_4.setWidth(100);
		newColumnTableColumn_4.setText("最小值");
		TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_5.setWidth(100);
		newColumnTableColumn_5.setText("最近一次");
		TableColumn newColumnTableColumn_6 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_6.setWidth(300);
		newColumnTableColumn_6.setText("最近一次时间");
		Label chatlabel = new Label(queryComposite, SWT.BORDER);
		FormData fd_chatlabel = new FormData();
		fd_chatlabel.right = new FormAttachment(querylabel, 0, SWT.RIGHT);
		fd_chatlabel.top = new FormAttachment(table, 6);
		fd_chatlabel.left = new FormAttachment(querylabel, 0, SWT.LEFT);
		chatlabel.setLayoutData(fd_chatlabel);
		chatlabel.setBackground(new Color(null, 191, 198, 216));
		chatlabel.setText("图表");
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(reportForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(400);
	    scrolledComposite.setMinHeight((TotalTabView.reportDescList.size()+1)*300);

		Composite  chatComposite = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(chatComposite);// 设置chatComposite被scrolledComposite控制
		chatComposite.setBackground(new Color(null, 255, 255, 255));
		chatComposite.setLayout(new FillLayout(SWT.VERTICAL));
		for (int i = 0;i<TotalTabView.reportDescList.size();i++) {
			Map<String, List<String>> map  = TotalTabView.reportDescList.get(i);
			Set<Map.Entry<String, List<String>>> set = map.entrySet();
			for (Iterator<Map.Entry<String, List<String>>> it = set.iterator(); it
					.hasNext();) {
				TableItem item = new TableItem(table, SWT.NONE);
				Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) it
						.next();
				List<String> arrlist = entry.getValue();
				if (arrlist.size() > 0) {
					String str = TotalTabView.monitorName + "&"
							+ entry.getKey() + "&" + arrlist.get(0) + "&"
							+ arrlist.get(1) + "&" + arrlist.get(3) + "&"
							+ arrlist.get(2) + "&" + TotalTabView.lastTime;
					String[] strdata = str.split("&");
					item.setText(strdata);
					Composite liComposite = new Composite(chatComposite, SWT.NONE);
					liComposite.setBackground(new Color(null, 255, 255, 255));
					liComposite.setLayout(new FormLayout());
					Label childlabel = new Label(liComposite, SWT.BORDER);
					FormData fd_childlabel = new FormData();
					fd_childlabel.left = new FormAttachment(0);
					fd_childlabel.right = new FormAttachment(100);
					fd_childlabel.top = new FormAttachment(0);
					childlabel.setLayoutData(fd_childlabel);
					childlabel.setBackground(new Color(null, 191, 198, 216));
					childlabel.setText(entry.getKey());
					TableViewer childTableViewer = new TableViewer(liComposite, SWT.MULTI
							| SWT.FULL_SELECTION | SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
					Table childTable = childTableViewer.getTable();
					FormData fd_childTable = new FormData();
					fd_childTable.top = new FormAttachment(childlabel, 1);
					childTable.setLayoutData(fd_childTable);
					childTable.setLinesVisible(true);
					childTable.setHeaderVisible(true);
					childTable.setBackground(new Color(null, 255, 255, 255));
					TableColumn nameTableColumn = new TableColumn(childTable, SWT.NONE);
					nameTableColumn.setWidth(200);
					nameTableColumn.setText("名称");
					TableColumn maxTableColumn = new TableColumn(childTable, SWT.NONE);
					maxTableColumn.setWidth(150);
					maxTableColumn.setText("最大值");
					TableColumn avgTableColumn = new TableColumn(childTable, SWT.NONE);
					avgTableColumn.setWidth(150);
					avgTableColumn.setText("平均值");
					TableColumn minTableColumn = new TableColumn(childTable, SWT.NONE);
					minTableColumn.setWidth(150);
					minTableColumn.setText("最小值");
					TableColumn whenMaxTableColumn = new TableColumn(childTable, SWT.NONE);
					whenMaxTableColumn.setWidth(520);
					whenMaxTableColumn.setText("最大值时间");
					TableItem childTableitem = new TableItem(childTable, SWT.NONE);
					String childStr = TotalTabView.monitorName + "&"
							+ arrlist.get(0) + "&"
							+ arrlist.get(1) + "&" + arrlist.get(3) + "&"
							+ arrlist.get(4);
					String[] childstrdata = childStr.split("&");
					childTableitem.setText(childstrdata);
					Map<String, List<String>> xyDataArrayMap = TotalTabView.reportEveryDescList.get(i);
					Set<Map.Entry<String, List<String>>> xyset = xyDataArrayMap.entrySet();
					for (Iterator<Map.Entry<String, List<String>>> xyit = xyset.iterator(); xyit
							.hasNext();){
						Map.Entry<String, List<String>> xyentry = (Map.Entry<String, List<String>>) xyit
								.next();
						XYDataset dataset = EccReportChart
								.createDataset(xyentry.getValue(),xyentry.getKey());
						JFreeChart chart = EccReportChart.createChart(dataset,
								xyentry.getKey(), xyentry.getKey());
						ChartComposite frame = new ChartComposite(liComposite, SWT.NONE , chart, true);
						FormData fd_frame = new FormData();
						fd_frame.top = new FormAttachment(childTable, 0);
						fd_frame.left = new FormAttachment(0);
						fd_frame.right = new FormAttachment(100);
						fd_frame.bottom = new FormAttachment(100, -10);
						frame.setLayoutData(fd_frame);
					}
				
				}
			}
		}
		
		parent.layout();
	}
}
