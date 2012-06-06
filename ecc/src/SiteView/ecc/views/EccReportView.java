package SiteView.ecc.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import SiteView.ecc.reportchart.EccReportChart;
import SiteView.ecc.tab.views.SummaryTabView;

public class EccReportView extends ViewPart {
	private Composite reportComposite;
	private ChartComposite frame;
	private TableViewer tableViewer;
	private Table table;
	public static final String ID = "SiteView.ecc.views.EccReportView";
	public static Composite composite_reportimgControl = null;
	public static JFreeChart chart = null;

	public EccReportView() {

	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		SashForm reportForm = new SashForm(parent, SWT.BORDER);
		reportForm.setOrientation(SWT.VERTICAL);
		reportForm.setBounds(1, 1, 1000, 320);
		reportForm.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_reportimgControl = new Composite(reportForm, SWT.BORDER);
		composite_reportimgControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite_reportdescControl = new Composite(reportForm,
				SWT.BORDER);
		composite_reportdescControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		// sashForm.setWeights(new int[] { 1, 1 });
		reportComposite = new Composite(composite_reportimgControl, SWT.NONE);
		reportComposite.setLayout(new FillLayout());
		// System.out.println("报表曲线图数据Map:"+SummaryTabView.xydata);
		XYDataset dataset = EccReportChart.createDataset(SummaryTabView.xydata);
		chart = EccReportChart.createChart(dataset, SummaryTabView.xname,
				SummaryTabView.yname);
		frame = new ChartComposite(reportComposite, SWT.NONE, chart, true);

		Group group1 = new Group(composite_reportimgControl,
				SWT.SHADOW_ETCHED_OUT);
		group1.setLayout(new FillLayout(SWT.VERTICAL));
		group1.setText("数据记录");
		Label labelName = new Label(group1, SWT.NONE);
		labelName.setText("   正常:   " + SummaryTabView.goodcount
				+ "条               危险：   " + SummaryTabView.warningcount
				+ "条    ");
		Label labelName1 = new Label(group1, SWT.NONE);
		labelName1.setText("   错误:   " + SummaryTabView.errorcount
				+ "条               禁止：    " + SummaryTabView.disablecount
				+ "条    ");
		Label labelName2 = new Label(group1, SWT.NONE);
		labelName2.setText("   阀值:   [虚拟内存利用率(%) >= 95]    ");
		Label labelName3 = new Label(group1, SWT.NONE);
		labelName3.setText("   时间段：   ");
		Label labelName4 = new Label(group1, SWT.NONE);
		labelName4.setText("   从：   "+SummaryTabView.startTime+" 开始");
		Label labelName5 = new Label(group1, SWT.NONE);
		labelName5.setText("   到：   "+SummaryTabView.endTime+" 结束");
		// frame1 = new ChartComposite(group1, SWT.NONE, chart, true);
		tableViewer = new TableViewer(composite_reportdescControl, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(97, 79, 373, 154);

		TableColumn newColumnTableColumn = new TableColumn(table, SWT.NONE);
		newColumnTableColumn.setWidth(600);
		newColumnTableColumn.setText("返回值");
		TableColumn newColumnTableColumn_1 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_1.setWidth(150);
		newColumnTableColumn_1.setText("最大值");
		TableColumn newColumnTableColumn_2 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_2.setWidth(150);
		newColumnTableColumn_2.setText("平均值");
		TableColumn newColumnTableColumn_3 = new TableColumn(table, SWT.NONE);
		newColumnTableColumn_3.setWidth(150);
		newColumnTableColumn_3.setText("最新值");
		reportForm.setWeights(new int[] { 141, 76 });
		for (String str : SummaryTabView.descList) {
			TableItem item = new TableItem(table, SWT.NONE);
			String[] strdata = str.split("&");
			item.setText(strdata);
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * Refresh Report Part
	 */
	public void refreshReport() {
		if (reportComposite.getChildren().length > 0) {
			for (Control control : reportComposite.getChildren()) {
				control.dispose();
			}
		}
		XYDataset dataset = EccReportChart.createDataset(SummaryTabView.xydata);
		chart = EccReportChart.createChart(dataset, SummaryTabView.xname,
				SummaryTabView.yname);
		frame = new ChartComposite(reportComposite, SWT.NONE, chart, true);
		reportComposite.layout();
	}

}
