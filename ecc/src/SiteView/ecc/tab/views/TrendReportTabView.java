package SiteView.ecc.tab.views;


import org.eclipse.swt.widgets.Composite;
import SiteView.ecc.views.TrendReportView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;
/**
 * <p>趋势报告Tab视图</p>
 * @author Administrator
 *
 */
public class TrendReportTabView extends LayoutViewBase {

	public TrendReportTabView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createView(Composite parent) {
		// TODO Auto-generated method stub
		TrendReportView  trv = new TrendReportView();
		trv.createPartControl(parent);
	}

	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
		
	}

}
