package SiteView.ecc.editors;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
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
	private ChartComposite frame,frame1,frame2;

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

		Composite composite_reportControl = new Composite(sashForm, SWT.BORDER);
		composite_reportControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] { 1, 1 });
		
		SashForm reportForm = new SashForm(composite_reportControl, SWT.BORDER);
		reportForm.setOrientation(SWT.VERTICAL);
		//这个 Composite
		Composite composite_reportimgControl = new Composite(reportForm, SWT.BORDER);
		composite_reportimgControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite_reportdescControl = new Composite(reportForm, SWT.BORDER);
		composite_reportdescControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] { 1, 1 });
		// TODO Auto-generated method stub
		XYDataset dataset = EccReportChart.createDataset();
		JFreeChart chart = EccReportChart.createChart(dataset);
		frame = new ChartComposite(composite_reportimgControl, SWT.NONE, chart, true);
		frame1 = new ChartComposite(composite_reportimgControl, SWT.NONE, chart, true);
		frame2 = new ChartComposite(composite_reportdescControl, SWT.NONE, chart, true);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}


}
