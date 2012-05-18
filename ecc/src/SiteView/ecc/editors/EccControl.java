package SiteView.ecc.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.jfree.experimental.chart.swt.ChartComposite;

import SiteView.ecc.data.MonitorInfo;
import SiteView.ecc.reportchart.EccReportChart;
import SiteView.ecc.views.EccReportView;

public class EccControl extends EditorPart {
	public EccControl() {
	}

	public static final String ID = "SiteView.ecc.editors.EccControl";
	private ChartComposite frame, frame1;
	private TableViewer tableViewer, toptableViewer;
	private Table table, toptable;
	private Composite reportComposite;

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

		final Composite composite = new Composite(sashForm, SWT.BORDER);
		composite.setLayout(new FillLayout());

		toptableViewer = new TableViewer(composite, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.CHECK);
		toptable = toptableViewer.getTable();
		toptable.setLayoutData(new FormData());
		toptable.setLinesVisible(true);
		toptable.setHeaderVisible(true);

		TableColumn newColumnTableColumn_top = new TableColumn(toptable,
				SWT.NONE | SWT.CHECK);
		newColumnTableColumn_top.setWidth(80);
		newColumnTableColumn_top.setText("状态");
		TableColumn newColumnTableColumn_top1 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top1.setWidth(50);
		newColumnTableColumn_top1.setText("刷新");
		TableColumn newColumnTableColumn_top2 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top2.setWidth(350);
		newColumnTableColumn_top2.setText("名称");
		TableColumn newColumnTableColumn_top3 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top3.setWidth(550);
		newColumnTableColumn_top3.setText("描述");
		TableColumn newColumnTableColumn_top4 = new TableColumn(toptable,
				SWT.NONE);
		newColumnTableColumn_top4.setWidth(200);
		newColumnTableColumn_top4.setText("最后更新");

		// 设置内容器
		toptableViewer.setContentProvider(new ContentProvider());
		// 设置标签器
		toptableViewer.setLabelProvider(new TableLabelProvider());
		// 把数据集合给toptableViewer
		toptableViewer.setInput(MonitorInfo.getMonitorInfo());

		// 给toptableViewer添加鼠标事件
		toptableViewer.getTable().addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				int x = e.x;
				int y = e.y;
				Table table = toptableViewer.getTable();
				TableItem item = table.getItem(new Point(x, y));
				if (item != null) {
					MonitorInfo mo = (MonitorInfo) item.getData();
					EccReportChart erc = new EccReportChart();
					erc.setReporttitle(mo.getMonitorname());
					IWorkbenchPart part = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.findView(EccReportView.ID);
					if (part instanceof EccReportView) {
						((EccReportView) part).refreshReport();
					}
				}
			}   

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
