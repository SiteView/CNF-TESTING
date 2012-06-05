package SiteView.ecc.tab.views;

import org.eclipse.swt.widgets.Composite;
import SiteView.ecc.views.EccReportView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;

/**
 * <p>
 * Monitor Summary Info View.
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class SummaryTabView extends LayoutViewBase {
	public SummaryTabView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createView(Composite parent) {
		// TODO Auto-generated method stub
		EccReportView erv = new EccReportView();
		erv.createPartControl(parent);
	}

	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub

	}

}
