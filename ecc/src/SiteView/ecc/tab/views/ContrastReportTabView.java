package SiteView.ecc.tab.views;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.views.ContrastReportView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;
/**
 * �Աȱ�����չ��ͼ
 * @author zhongping.wang
 *
 */
public class ContrastReportTabView extends LayoutViewBase{

	public ContrastReportTabView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public  void createView(final Composite parent) {
		// TODO Auto-generated method stub
		parent.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
			}
			public void controlMoved(ControlEvent e) {
				ContrastReportView crv = new ContrastReportView();
				crv.createPartControl(parent);
			}
		});
	}
	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
	}
}
