package SiteView.ecc.table.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import SiteView.ecc.reportchart.EccReportChart;
import Siteview.Api.BusinessObject;

import siteview.windows.forms.LayoutViewBase;

public class MonitorLogTableView extends LayoutViewBase{

	private Composite reportComposite;
	private ChartComposite frame;
	private TableViewer tableViewer;
	private Table table;
//	public static final String ID = "SiteView.ecc.view.EccReportView";
	public static Composite composite_reportimgControl = null;
	public static JFreeChart chart = null;
	public MonitorLogTableView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createView(Composite parent) {
		// TODO Auto-generated method stub
		SashForm reportForm = new SashForm(parent, SWT.BORDER);
		reportForm.setOrientation(SWT.VERTICAL);

		composite_reportimgControl = new Composite(reportForm, SWT.BORDER);
		composite_reportimgControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite_reportdescControl = new Composite(reportForm,
				SWT.BORDER);
		composite_reportdescControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		// sashForm.setWeights(new int[] { 1, 1 });
		reportComposite = new Composite(composite_reportimgControl, SWT.NONE);
		reportComposite.setLayout(new FillLayout());
		XYDataset dataset = EccReportChart.createDataset();
		chart = EccReportChart.createChart(dataset);
		frame = new ChartComposite(reportComposite, SWT.NONE, chart, true);

		Group group1 = new Group(composite_reportimgControl,
				SWT.SHADOW_ETCHED_OUT);
		group1.setLayout(new FillLayout(SWT.VERTICAL));
		group1.setText("数据记录");

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
	}

	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
		
	}

}
