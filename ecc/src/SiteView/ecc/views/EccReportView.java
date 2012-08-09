package SiteView.ecc.views;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import SiteView.ecc.dialog.ParticularInfo;
import SiteView.ecc.editors.EccControl;
import SiteView.ecc.reportchart.EccReportChart;
import SiteView.ecc.tab.views.TotalTabView;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

import core.busobmaint.BusObMaintView;
/**
 * 概要信息视图
 * @author zhongping.wang
 *
 */
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
		parent.setLayout(new FillLayout());
		SashForm reportForm = new SashForm(parent, SWT.BORDER);
		
		reportForm.setOrientation(SWT.VERTICAL);
		reportForm.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_reportimgControl = new Composite(reportForm, SWT.NONE);
		composite_reportimgControl.setLayout(new FormLayout());
		Composite composite_reportdescControl = new Composite(reportForm,
				SWT.NONE);
		composite_reportdescControl.setLayout(new FillLayout(SWT.HORIZONTAL));
		reportComposite = new Composite(composite_reportimgControl, SWT.NONE);
		reportComposite.setLayoutData(new FormData());
		reportComposite.setLayout(new FillLayout());
		XYDataset dataset = EccReportChart.createDataset(TotalTabView.xyDataArrayList);
		chart = EccReportChart.createChart(dataset, TotalTabView.xname,
				TotalTabView.yname);

		Group group1 = new Group(composite_reportimgControl,
				SWT.SHADOW_ETCHED_OUT);
		FormData fd_group1 = new FormData();
		fd_group1.top = new FormAttachment(reportComposite, 0, SWT.TOP);
		fd_group1.bottom = new FormAttachment(100);
		fd_group1.right = new FormAttachment(100);
		group1.setBackground(new Color(null, 255, 255, 255));
		group1.setLayoutData(fd_group1);
		group1.setLayout(new FillLayout(SWT.VERTICAL));
		group1.setText("数据记录");
		
		Composite composite = new Composite(group1, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Button button = new Button(composite, SWT.NONE);
		button.setBounds(0, 0, 72, 22);
		button.setText("\u8BE6\u7EC6\u4FE1\u606F");
		button.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_FOREGROUND));
		
		Button button_1 = new Button(composite, SWT.NONE);
		button_1.setBounds(81, 0, 72, 22);
		button_1.setText("\u7F16\u8F91");
		button_1.addListener(SWT.MouseDown,new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				BusObMaintView.open(ConnectionBroker.get_SiteviewApi(),(BusinessObject)EccControl.item.getData());
			}
		});
		
		
		Button button_2 = new Button(composite, SWT.NONE);
		button_2.setBounds(159, 0, 72, 22);
		button_2.setText("\u5220\u9664");
		button_2.addListener(SWT.MouseDown,new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
			}
		});
		
		button.addListener(SWT.MouseDown,new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				ParticularInfo par=new ParticularInfo(null);
				par.open();
			}
		});
		
		Label goodlabel = new Label(group1, SWT.NONE);
		goodlabel.setForeground(new Color(null,0,255,0));
		goodlabel.setBackground(new Color(null, 255, 255, 255));
		goodlabel.setText("   正常:   " + TotalTabView.goodcount+ "条");
		Label warninglabel = new Label(group1, SWT.NONE);
		warninglabel.setForeground(new Color(null,192,194,20));
		warninglabel.setBackground(new Color(null, 255, 255, 255));
		warninglabel.setText("   危险:   "+ TotalTabView.warningcount+ "条");
		Label errorlabel = new Label(group1, SWT.NONE);
		errorlabel.setForeground(new Color(null,255,0,0));
		errorlabel.setBackground(new Color(null, 255, 255, 255));
		errorlabel.setText("   错误:   " + TotalTabView.errorcount+ "条 ");
		Label disablelabel = new Label(group1, SWT.NONE);
		disablelabel.setForeground(new Color(null,255,170,102));
		disablelabel.setBackground(new Color(null, 255, 255, 255));
		disablelabel.setText("   禁止:   " + TotalTabView.disablecount+ "条");
		Label errorValve = new Label(group1, SWT.NONE);
		errorValve.setText("   错误阀值:   "+TotalTabView.errorAlarmCondition);
		errorValve.setBackground(new Color(null, 255, 255, 255));
		Label warningValve = new Label(group1, SWT.NONE);
		warningValve.setText("   危险阀值:   "+TotalTabView.warningAlarmCondition);
		warningValve.setBackground(new Color(null, 255, 255, 255));
		Label goodValve = new Label(group1, SWT.NONE);
		goodValve.setText("   正常阀值:   "+TotalTabView.goodAlarmCondition);
		goodValve.setBackground(new Color(null, 255, 255, 255));
		Label labelName3 = new Label(group1, SWT.NONE);
		labelName3.setText("   时间段：   ");
		labelName3.setBackground(new Color(null, 255, 255, 255));
		Label labelName4 = new Label(group1, SWT.NONE);
		labelName4.setText("   从：   "+TotalTabView.startTime+" 开始");
		labelName4.setBackground(new Color(null, 255, 255, 255));
		Label labelName5 = new Label(group1, SWT.NONE);
		labelName5.setText("   到：   "+TotalTabView.endTime+" 结束");
		labelName5.setBackground(new Color(null, 255, 255, 255));
		frame = new ChartComposite(composite_reportimgControl, SWT.NONE, chart, true);
		fd_group1.left = new FormAttachment(frame, 1);
		FormData fd_frame = new FormData();
		fd_frame.right = new FormAttachment(100, -235);
		fd_frame.left = new FormAttachment(0);
		fd_frame.top = new FormAttachment(0);
		fd_frame.bottom = new FormAttachment(100);
		frame.setLayoutData(fd_frame);
		tableViewer = new TableViewer(composite_reportdescControl, SWT.MULTI
				| SWT.FULL_SELECTION | SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBackground(new Color(null, 255, 255, 255));
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
		reportForm.setWeights(new int[] {293, 165});
		
		for (Map<String, List<String>> map : TotalTabView.reportDescList) {
			Set<Map.Entry<String, List<String>>> set = map.entrySet();
			for (Iterator<Map.Entry<String, List<String>>> it = set.iterator(); it
					.hasNext();) {
				TableItem item = new TableItem(table, SWT.NONE);
				Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) it
						.next();
				List<String> arrlist = entry.getValue();
				if (arrlist.size()>0) {
					String str = entry.getKey()+"&"+arrlist.get(0)+"&"+arrlist.get(1)+"&"+arrlist.get(2);
					String[] strdata = str.split("&");
					item.setText(strdata);
				}
			}
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
		XYDataset dataset = EccReportChart.createDataset(TotalTabView.xyDataArrayList);
		chart = EccReportChart.createChart(dataset, TotalTabView.xname,
				TotalTabView.yname);
		frame = new ChartComposite(reportComposite, SWT.NONE, chart, true);
		reportComposite.layout();
	}
}
