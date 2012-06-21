package SiteView.ecc.views;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
	public String endTimeStr   = "";
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
		Label queryLabel = new Label(trendComposite, SWT.NONE);
		FormData fd_queryLabel = new FormData();
		fd_queryLabel.left = new FormAttachment(0);
		fd_queryLabel.right = new FormAttachment(100);
		fd_queryLabel.top = new FormAttachment(0);
		queryLabel.setLayoutData(fd_queryLabel);
		queryLabel.setText("查询条件");
		queryLabel.setBackground(new Color(null, 191, 198, 216));

		Label start = new Label(trendComposite, SWT.None);
		FormData fd_start = new FormData();
		start.setBackground(new Color(null, 255, 255, 255));
		fd_start.left = new FormAttachment(queryLabel, 0, SWT.LEFT);
		start.setLayoutData(fd_start);
		start.setText("开始时间:");
		final DateTime startDate = new DateTime(trendComposite, SWT.DATE);
		Calendar cal = Calendar.getInstance();
		startDate.setDay(cal.get(Calendar.DAY_OF_MONTH) - 1);
		fd_start.bottom = new FormAttachment(startDate, 0, SWT.BOTTOM);
		FormData fd_startDate = new FormData();
		fd_startDate.left = new FormAttachment(0, 60);
		fd_startDate.top = new FormAttachment(queryLabel, 6);
		startDate.setLayoutData(fd_startDate);
		final DateTime startTime = new DateTime(trendComposite, SWT.TIME | SWT.SHORT);
		FormData fd_startTime = new FormData();
		fd_startTime.left = new FormAttachment(startDate, 6);
		startTime.setLayoutData(fd_startTime);
		Label end = new Label(trendComposite, SWT.None);
		end.setBackground(new Color(null, 255, 255, 255));
		FormData fd_end = new FormData();
		fd_end.left = new FormAttachment(startTime, 6);
		end.setLayoutData(fd_end);
		end.setText("结束时间:");
		final DateTime endDate = new DateTime(trendComposite, SWT.DATE | SWT.TIME);
		FormData fd_endDate = new FormData();
		fd_endDate.top = new FormAttachment(queryLabel, 6);
		fd_endDate.left = new FormAttachment(end, 6);
		endDate.setLayoutData(fd_endDate);
		final DateTime endTime = new DateTime(trendComposite, SWT.TIME | SWT.SHORT);
		FormData fd_endTime = new FormData();
		fd_endTime.top = new FormAttachment(queryLabel, 6);
		fd_endTime.left = new FormAttachment(endDate, 1);
		endTime.setLayoutData(fd_endTime);
		Button queryBtn = new Button(trendComposite, SWT.NONE);
		FormData fd_selete = new FormData();
		fd_selete.top = new FormAttachment(start, -5, SWT.TOP);
		fd_selete.left = new FormAttachment(endTime, 6);
		queryBtn.setLayoutData(fd_selete);
		queryBtn.setText("查询");
		queryBtn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				startTimeStr = startDate.getYear() + "-"
						+ (startDate.getMonth() + 1) + "-" + startDate.getDay()
						+ " " + startTime.getHours() + ":"
						+ startTime.getMinutes()+":"+startTime.getSeconds();
				endTimeStr = endDate.getYear() + "-"
						+ (endDate.getMonth() + 1) + "-" + endDate.getDay()
						+ " " + endTime.getHours() + ":"
						+ endTime.getMinutes()+":"+endTime.getSeconds();
				refreshComposite(trendComposite);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
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

		TableViewer totaltableViewer = new TableViewer(trendComposite, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
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
		ChartComposite frame = new ChartComposite(trendComposite, SWT.NONE, chart, true);
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
