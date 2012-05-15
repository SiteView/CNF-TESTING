package SiteView.ecc.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FormLayout;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import SiteView.ecc.reportchart.EccReportChart;

public class EccControl extends EditorPart {
	public EccControl() {
	}

	public static final String ID = "SiteView.ecc.editors.EccControl";
	private ChartComposite frame, frame1;
	private TableViewer tableViewer, toptableViewer;
	private Table table, toptable;

	@Override
	public void doSave(IProgressMonitor arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);// 设置site
		this.setInput(input);// 设置输入的IEditorInput对象
		this.setPartName(input.getName());// 设置编辑器上方显示的名称

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(parent, SWT.BORDER);
		sashForm.setOrientation(SWT.VERTICAL);

		Composite composite = new Composite(sashForm, SWT.BORDER);
		composite.setLayout(new FormLayout());

		toptableViewer = new TableViewer(composite, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		toptable = toptableViewer.getTable();
		toptable.setLinesVisible(true);
		toptable.setHeaderVisible(true);
		toptable.setBounds(97, 79, 373, 154);

		TableColumn newColumnTableColumn_top = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top.setWidth(100);
		newColumnTableColumn_top.setText("状态");
		TableColumn newColumnTableColumn_top1 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top1.setWidth(100);
		newColumnTableColumn_top1.setText("刷新");
		TableColumn newColumnTableColumn_top2 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top2.setWidth(300);
		newColumnTableColumn_top2.setText("名称");
		TableColumn newColumnTableColumn_top3 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top3.setWidth(600);
		newColumnTableColumn_top3.setText("描述");
		TableColumn newColumnTableColumn_top4 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top4.setWidth(200);
		newColumnTableColumn_top4.setText("最后更新");

		Composite composite_reportControl = new Composite(sashForm, SWT.BORDER);
		composite_reportControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] { 1, 1 });

		SashForm reportForm = new SashForm(composite_reportControl, SWT.BORDER);
		reportForm.setOrientation(SWT.VERTICAL);

		Composite composite_reportimgControl = new Composite(reportForm,
				SWT.BORDER);
		composite_reportimgControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite_reportdescControl = new Composite(reportForm,
				SWT.BORDER);
		composite_reportdescControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] { 1, 1 });
		// TODO Auto-generated method stub
		XYDataset dataset = EccReportChart.createDataset();
		JFreeChart chart = EccReportChart.createChart(dataset);
		frame = new ChartComposite(composite_reportimgControl, SWT.NONE, chart,
				true);
		frame1 = new ChartComposite(composite_reportimgControl, SWT.NONE,
				chart, true);
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

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
