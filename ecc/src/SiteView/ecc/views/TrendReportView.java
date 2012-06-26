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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import SiteView.ecc.reportchart.EccReportChart;
import SiteView.ecc.tab.views.MonitorLogTabView;
import SiteView.ecc.tab.views.TotalTabView;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * 趋势报告视图
 * 
 * @author Administrator
 * 
 */
public class TrendReportView extends ViewPart {
	public Composite trendComposite;
	public String startTimeStr = "";
	public String endTimeStr = "";
	private Button queryBtn;
	private Label start;
	private Label queryLabel;

	public TrendReportView() {

	}

	@Override
	public void createPartControl(final Composite parent) {
		// TODO Auto-generated method stub
		refreshComposite(parent);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * refresh Composite
	 */
	public void refreshComposite(final Composite trendComposite) {
		if (trendComposite.getChildren().length > 0) {
			for (Control control : trendComposite.getChildren()) {
				control.dispose();
			}
		}
		TotalTabView.startTime = startTimeStr;
		TotalTabView.endTime = endTimeStr;
		TotalTabView.setTotalData(TotalTabView.businessObj);

		trendComposite.setLayout(new FormLayout());
		trendComposite.setBackground(new Color(null, 255, 255, 255));
		queryLabel = new Label(trendComposite, SWT.NONE);
		FormData fd_queryLabel = new FormData();
		fd_queryLabel.left = new FormAttachment(0);
		fd_queryLabel.right = new FormAttachment(100);
		fd_queryLabel.top = new FormAttachment(0);
		queryLabel.setLayoutData(fd_queryLabel);
		queryLabel.setText("查询条件");
		queryLabel.setBackground(new Color(null, 191, 198, 216));

		start = new Label(trendComposite, SWT.None);
		FormData fd_start = new FormData();
		start.setBackground(new Color(null, 255, 255, 255));
		fd_start.left = new FormAttachment(queryLabel, 0, SWT.LEFT);
		start.setLayoutData(fd_start);
		start.setText("开始时间:");
		final DateTime startDate = new DateTime(trendComposite, SWT.DROP_DOWN);
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
		fd_start.bottom = new FormAttachment(startDate, 0, SWT.BOTTOM);
		FormData fd_startDate = new FormData();
		fd_startDate.left = new FormAttachment(0, 60);
		fd_startDate.top = new FormAttachment(queryLabel, 6);
		startDate.setLayoutData(fd_startDate);
		final DateTime startTime = new DateTime(trendComposite, SWT.TIME
				| SWT.SHORT);
		FormData fd_startTime = new FormData();
		fd_startTime.left = new FormAttachment(startDate, 6);
		startTime.setLayoutData(fd_startTime);
		startTime.setHours(startcal.get(Calendar.HOUR));
		startTime.setMinutes(startcal.get(Calendar.MINUTE));
		startTime.setSeconds(startcal.get(Calendar.SECOND));
		Label end = new Label(trendComposite, SWT.None);
		end.setBackground(new Color(null, 255, 255, 255));
		FormData fd_end = new FormData();
		fd_end.left = new FormAttachment(startTime, 6);
		end.setLayoutData(fd_end);
		end.setText("结束时间:");
		final DateTime endDate = new DateTime(trendComposite, SWT.DROP_DOWN);
		Calendar endcal = Calendar.getInstance();
		endcal.setTime(endDateTime);
		endDate.setYear(endcal.get(Calendar.YEAR));
		endDate.setMonth(endcal.get(Calendar.MONTH));
		endDate.setDay(endcal.get(Calendar.DAY_OF_MONTH));
		FormData fd_endDate = new FormData();
		fd_endDate.top = new FormAttachment(queryLabel, 6);
		fd_endDate.left = new FormAttachment(end, 6);
		endDate.setLayoutData(fd_endDate);
		final DateTime endTime = new DateTime(trendComposite, SWT.TIME
				| SWT.SHORT);
		endTime.setHours(endcal.get(Calendar.HOUR));
		endTime.setMinutes(endcal.get(Calendar.MINUTE));
		endTime.setSeconds(endcal.get(Calendar.SECOND));
		FormData fd_endTime = new FormData();
		fd_endTime.top = new FormAttachment(queryLabel, 6);
		fd_endTime.left = new FormAttachment(endDate, 1);
		endTime.setLayoutData(fd_endTime);
		queryBtn = new Button(trendComposite, SWT.NONE);
		FormData fd_queryBtn = new FormData();
		fd_queryBtn.top = new FormAttachment(start, -5, SWT.TOP);
		fd_queryBtn.left = new FormAttachment(endTime, 6);
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
				refreshComposite(trendComposite);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label towHourBtn = new Label(trendComposite, SWT.NONE);
		towHourBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_lblNewLabel.left = new FormAttachment(queryBtn, 10);
		towHourBtn.setLayoutData(fd_lblNewLabel);
		towHourBtn.setText("2小时");
		towHourBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(2);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label fourHourBtn = new Label(trendComposite, SWT.NONE);
		fourHourBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_lblNewLabel_1.left = new FormAttachment(towHourBtn, 10);
		fourHourBtn.setLayoutData(fd_lblNewLabel_1);
		fourHourBtn.setText("4小时");
		fourHourBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(4);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label eightHourBtn = new Label(trendComposite, SWT.NONE);
		eightHourBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_lblNewLabel_2.left = new FormAttachment(fourHourBtn, 10);
		eightHourBtn.setLayoutData(fd_lblNewLabel_2);
		eightHourBtn.setText("8小时");
		eightHourBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(8);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label oneDayBtn = new Label(trendComposite, SWT.NONE);
		oneDayBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_oneDayBtn = new FormData();
		fd_oneDayBtn.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_oneDayBtn.left = new FormAttachment(eightHourBtn, 10);
		oneDayBtn.setLayoutData(fd_oneDayBtn);
		oneDayBtn.setText("1天");
		oneDayBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(24);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label twoDayBtn = new Label(trendComposite, SWT.NONE);
		twoDayBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(start, 0, SWT.TOP);
		fd_label.left = new FormAttachment(oneDayBtn, 10);
		twoDayBtn.setLayoutData(fd_label);
		twoDayBtn.setText("2天");
		twoDayBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(48);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label threeDayBtn = new Label(trendComposite, SWT.NONE);
		threeDayBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_threeDayBtn = new FormData();
		fd_threeDayBtn.top = new FormAttachment(start, 0, SWT.TOP);
		fd_threeDayBtn.left = new FormAttachment(twoDayBtn, 10);
		threeDayBtn.setLayoutData(fd_threeDayBtn);
		threeDayBtn.setText("3天");
		threeDayBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(72);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		Label fiveDayBtn = new Label(trendComposite, SWT.NONE);
		fiveDayBtn.setBackground(new Color(null, 255, 255, 255));
		FormData five_label = new FormData();
		five_label.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		five_label.left = new FormAttachment(threeDayBtn, 10);
		fiveDayBtn.setLayoutData(five_label);
		fiveDayBtn.setText("5天");
		fiveDayBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(120);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label thisWeekBtn = new Label(trendComposite, SWT.NONE);
		thisWeekBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_oneWeekBtn = new FormData();
		fd_oneWeekBtn.top = new FormAttachment(start, 0, SWT.TOP);
		fd_oneWeekBtn.left = new FormAttachment(fiveDayBtn, 10);
		thisWeekBtn.setLayoutData(fd_oneWeekBtn);
		thisWeekBtn.setText("本周");
		thisWeekBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				Calendar cal = Calendar.getInstance();
				int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
				cal.add(Calendar.DATE, -day_of_week);
				startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				cal.add(Calendar.DATE, 6);
				endTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label oneWeekBtn = new Label(trendComposite, SWT.NONE);
		oneWeekBtn.setBackground(new Color(null, 255, 255, 255));
		FormData oneWeek_label = new FormData();
		oneWeek_label.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		oneWeek_label.left = new FormAttachment(thisWeekBtn, 10);
		oneWeekBtn.setLayoutData(oneWeek_label);
		oneWeekBtn.setText("1周");
		oneWeekBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				String time = MonitorLogTabView.getHoursAgoTime(168);
				startTimeStr = time.substring(time.indexOf("*") + 1);
				endTimeStr = time.substring(0, time.indexOf("*"));
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label oneMonthBtn = new Label(trendComposite, SWT.NONE);
		oneMonthBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_oneMonthblNewLabel = new FormData();
		fd_oneMonthblNewLabel.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_oneMonthblNewLabel.left = new FormAttachment(oneWeekBtn, 10);
		oneMonthBtn.setLayoutData(fd_oneMonthblNewLabel);
		oneMonthBtn.setText("1个月");
		oneMonthBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				Calendar cal = Calendar.getInstance();
				endTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				cal.add(cal.MONTH, -1);
				startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label twoMonthBtn = new Label(trendComposite, SWT.NONE);
		twoMonthBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_label_1.left = new FormAttachment(oneMonthBtn, 10);
		twoMonthBtn.setLayoutData(fd_label_1);
		twoMonthBtn.setText("2个月");
		twoMonthBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				Calendar cal = Calendar.getInstance();
				endTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				cal.add(cal.MONTH, -2);
				startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label sixMonthBtn = new Label(trendComposite, SWT.NONE);
		sixMonthBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_label_2 = new FormData();
		fd_label_2.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_label_2.left = new FormAttachment(twoMonthBtn, 10);
		sixMonthBtn.setLayoutData(fd_label_2);
		sixMonthBtn.setText("6个月");
		sixMonthBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				Calendar cal = Calendar.getInstance();
				endTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				cal.add(cal.MONTH, -6);
				startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(cal.getTime());
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label thisDayBtn = new Label(trendComposite, SWT.NONE);
		thisDayBtn.setBackground(new Color(null, 255, 255, 255));
		FormData fd_label_3 = new FormData();
		fd_label_3.bottom = new FormAttachment(start, 0, SWT.BOTTOM);
		fd_label_3.left = new FormAttachment(sixMonthBtn, 10);
		thisDayBtn.setLayoutData(fd_label_3);
		thisDayBtn.setText("当天");
		thisDayBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				Calendar todayStart = Calendar.getInstance();
				todayStart.set(Calendar.HOUR, 0);
				todayStart.set(Calendar.MINUTE, 0);
				todayStart.set(Calendar.SECOND, 0);
				todayStart.set(Calendar.MILLISECOND, 0);
				startTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(todayStart.getTime());
				Calendar todayEnd = Calendar.getInstance();
				todayEnd.set(Calendar.HOUR, 23);
				todayEnd.set(Calendar.MINUTE, 59);
				todayEnd.set(Calendar.SECOND, 59);
				todayEnd.set(Calendar.MILLISECOND, 999);
				endTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(todayEnd.getTime());
				refreshComposite(trendComposite);
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Label runningLabel = new Label(trendComposite, SWT.NONE);
		fd_end.bottom = new FormAttachment(runningLabel, -6);
		fd_startTime.bottom = new FormAttachment(runningLabel, -6);
		FormData fd_runningLabel = new FormData();
		fd_runningLabel.top = new FormAttachment(start, 6);
		fd_runningLabel.left = new FormAttachment(queryLabel, 0, SWT.LEFT);
		fd_runningLabel.right = new FormAttachment(100);
		runningLabel.setLayoutData(fd_runningLabel);
		runningLabel.setText("运行情况表");
		runningLabel.setBackground(new Color(null, 191, 198, 216));

		TableViewer tableViewer = new TableViewer(trendComposite, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		Table table = tableViewer.getTable();
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(runningLabel, 6);
		fd_table.left = new FormAttachment(0);
		table.setLayoutData(fd_table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBackground(new Color(null, 255, 255, 255));
		TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(200);
		newColumnTableColumn.setText("名称");
		TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(150);
		newColumnTableColumn_1.setText("正常运行时间(%)");
		TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(110);
		newColumnTableColumn_2.setText("危险(%)");
		TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_3.setWidth(110);
		newColumnTableColumn_3.setText("错误(%)");
		TableColumn newColumnTableColumn_4 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_4.setWidth(110);
		newColumnTableColumn_4.setText("最新");
		TableColumn newColumnTableColumn_5 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_5.setWidth(520);
		newColumnTableColumn_5.setText("阀值");

		TableItem runningitem = new TableItem(table, SWT.NONE);
		String runningstr = TotalTabView.monitorName + "&"
				+ TotalTabView.goodPercentOf + "&"
				+ TotalTabView.warningPercentOf + "&"
				+ TotalTabView.errorPercentOf + "&" + TotalTabView.laststatus
				+ "&错误阀值：" + TotalTabView.errorAlarmCondition + " 危险阀值："
				+ TotalTabView.warningAlarmCondition + " 正常阀值："
				+ TotalTabView.goodAlarmCondition;
		String[] runningstrdata = runningstr.split("&");
		runningitem.setText(runningstrdata);

		Label monitorTotalReportLabel = new Label(trendComposite, SWT.NONE);
		FormData fd_monitorTotalReportLabel = new FormData();
		fd_monitorTotalReportLabel.top = new FormAttachment(table, 5);
		fd_monitorTotalReportLabel.right = new FormAttachment(queryLabel, 0,
				SWT.RIGHT);
		fd_monitorTotalReportLabel.left = new FormAttachment(queryLabel, 0,
				SWT.LEFT);
		monitorTotalReportLabel.setLayoutData(fd_monitorTotalReportLabel);
		monitorTotalReportLabel.setText("监测器统计报表");
		monitorTotalReportLabel.setBackground(new Color(null, 191, 198, 216));

		TableViewer totaltableViewer = new TableViewer(trendComposite,
				SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL
						| SWT.H_SCROLL);
		Table totaltable = totaltableViewer.getTable();
		FormData fd_totaltable = new FormData();
		fd_totaltable.top = new FormAttachment(monitorTotalReportLabel, 11);
		totaltable.setLayoutData(fd_totaltable);
		totaltable.setLinesVisible(true);
		totaltable.setHeaderVisible(true);
		totaltable.setBackground(new Color(null, 255, 255, 255));
		String[] headers = { "名称", "返回值名称", "最大值", "最小值", "平均值", "最近一次",
				"最大值时间" };
		TableColumn totalColumnTableColumn = new TableColumn(totaltable,
				SWT.NONE);
		totalColumnTableColumn.setWidth(200);
		totalColumnTableColumn.setText(headers[0]);

		TableColumn totalColumnTableColumn_1 = new TableColumn(totaltable,
				SWT.NONE);
		totalColumnTableColumn_1.setWidth(200);
		totalColumnTableColumn_1.setText(headers[1]);
		TableColumn totalColumnTableColumn_2 = new TableColumn(totaltable,
				SWT.NONE);
		totalColumnTableColumn_2.setWidth(150);
		totalColumnTableColumn_2.setText(headers[2]);
		TableColumn totalColumnTableColumn_3 = new TableColumn(totaltable,
				SWT.NONE);
		totalColumnTableColumn_3.setWidth(150);
		totalColumnTableColumn_3.setText(headers[3]);
		TableColumn totalColumnTableColumn_4 = new TableColumn(totaltable,
				SWT.NONE);
		totalColumnTableColumn_4.setWidth(150);
		totalColumnTableColumn_4.setText(headers[4]);
		TableColumn totalColumnTableColumn_5 = new TableColumn(totaltable,
				SWT.NONE);
		totalColumnTableColumn_5.setWidth(150);
		totalColumnTableColumn_5.setText(headers[5]);
		TableColumn totalColumnTableColumn_6 = new TableColumn(totaltable,
				SWT.NONE);
		totalColumnTableColumn_6.setWidth(180);
		totalColumnTableColumn_6.setText(headers[6]);

		for (Map<String, List<String>> map : TotalTabView.reportDescList) {
			Set<Map.Entry<String, List<String>>> set = map.entrySet();
			for (Iterator<Map.Entry<String, List<String>>> it = set.iterator(); it
					.hasNext();) {
				TableItem item = new TableItem(totaltable, SWT.NONE);
				Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) it
						.next();
				List<String> arrlist = entry.getValue();
				if (arrlist.size() > 0) {
					String str = TotalTabView.monitorName + "&"
							+ entry.getKey() + "&" + arrlist.get(0) + "&"
							+ arrlist.get(3) + "&" + arrlist.get(1) + "&"
							+ arrlist.get(2) + "&" + arrlist.get(4);
					String[] strdata = str.split("&");
					item.setText(strdata);
				}
			}
		}

		Label reportImgLabel = new Label(trendComposite, SWT.NONE);
		fd_totaltable.bottom = new FormAttachment(reportImgLabel, -6);
		FormData fd_reportImgLabel = new FormData();
		fd_reportImgLabel.right = new FormAttachment(queryLabel, 0, SWT.RIGHT);
		fd_reportImgLabel.left = new FormAttachment(queryLabel, 0, SWT.LEFT);
		reportImgLabel.setLayoutData(fd_reportImgLabel);
		reportImgLabel.setText("图表");
		reportImgLabel.setBackground(new Color(null, 191, 198, 216));

		XYDataset dataset = EccReportChart
				.createDataset(TotalTabView.xyDataArrayList);
		JFreeChart chart = EccReportChart.createChart(dataset,
				TotalTabView.xname, TotalTabView.yname);
		ChartComposite frame = new ChartComposite(trendComposite, SWT.NONE,
				chart, true);
		fd_reportImgLabel.bottom = new FormAttachment(frame);
		FormData fd_frame = new FormData();
		fd_frame.top = new FormAttachment(0, 248);
		fd_frame.bottom = new FormAttachment(100);
		fd_frame.left = new FormAttachment(queryLabel, 0, SWT.LEFT);
		fd_frame.right = new FormAttachment(queryLabel, 0, SWT.RIGHT);
		frame.setLayoutData(fd_frame);
		trendComposite.layout();
	}

}
