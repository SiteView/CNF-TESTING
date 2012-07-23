package SiteView.ecc.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

import SiteView.ecc.editors.EccControl;
import SiteView.ecc.reportchart.StatusCTIReport;
import SiteView.ecc.reportchart.TimeContrastReport;
import SiteView.ecc.views.ContrastReportView;
import SiteView.ecc.views.TrendReportView;
import Siteview.Api.BusinessObject;

public class ParticularInfo extends Dialog {

	public ParticularInfo(Shell Shell) {
		super(Shell);
	}
	/*
	 * 初始化配置
	 */
	protected void configureShell(Shell newShell) {
		newShell.setSize(1000, 700);
		newShell.setLocation(150, 150);
		newShell.setText("详细信息");
		super.configureShell(newShell);
	}
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new FillLayout());
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		TabFolder tab=new TabFolder(composite, SWT.FULL_SELECTION);
        
    	TabItem tabItem_2=new TabItem(tab, SWT.NONE);
		tabItem_2.setText("趋势报告");
		Composite c2=new Composite(tab, SWT.FULL_SELECTION);
        TrendReportView  trv = new TrendReportView();
		trv.createPartControl(c2);
		tabItem_2.setControl(c2);
		
		TimeContrastReport.setData((BusinessObject)EccControl.item.getData());
		TabItem tabItem_1=new TabItem(tab, SWT.NONE);
		tabItem_1.setText("时段对比报告");
		Composite c1=new Composite(tab, SWT.FULL_SELECTION);
        TimeContrastReport m1=new TimeContrastReport(c1);
        m1.createView(c1);
        tabItem_1.setControl(c1);  
       
        StatusCTIReport.setData((BusinessObject)EccControl.item.getData());
        TabItem comaTabItem_3 = new TabItem(tab, SWT.NONE);  
        comaTabItem_3.setText("状态报表");
        Composite c3=new Composite(tab, SWT.FULL_SELECTION);
        StatusCTIReport m=new StatusCTIReport(c3);
        m.createView(c3);
        comaTabItem_3.setControl(c3);  
        
        TabItem comaTabItem_4 = new TabItem(tab, SWT.NONE);  
        comaTabItem_4.setText("状态报表");
        Composite c4=new Composite(tab, SWT.FULL_SELECTION);
        ContrastReportView crv = new ContrastReportView();
		crv.createPartControl(c4);
        comaTabItem_4.setControl(c4);  
        
		parent.layout();
		return composite;
	}
}
