package SiteView.ecc.tab.views;

import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.views.ContrastReportView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;
/**
 * 对比报告扩展视图
 * @author zhongping.wang
 *
 */
public class ContrastReportTabView extends LayoutViewBase{

	public ContrastReportTabView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public  void createView(Composite parent) {
		// TODO Auto-generated method stub
		ContrastReportView crv = new ContrastReportView();
		crv.createPartControl(parent);
	}

	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
		
	}
}
