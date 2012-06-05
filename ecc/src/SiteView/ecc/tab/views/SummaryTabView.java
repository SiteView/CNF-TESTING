package SiteView.ecc.tab.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.views.EccReportView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

/**
 * <p>
 * Monitor Summary Info View.
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class SummaryTabView extends LayoutViewBase {
	public static Map<String, String> xydata = new HashMap<String, String>();

	public Map<String, String> getXydata() {
		return xydata;
	}

	public static void setXydata(Map<String, String> xydata) {
		SummaryTabView.xydata = xydata;
	}

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

	public static void setData(BusinessObject bo) {
		// TODO Auto-generated method stub
		String boid = bo.get_Id();
		String monitortype = bo.get_Name();
		// System.out.println("monitortype:"+monitortype);
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
		String time=MonitorLogTabView.getTwoHoursAgoTime();
		parmsmap.put("startTime", time.substring(time.indexOf("*")+1));
		parmsmap.put("endTime", time.substring(0,time.indexOf("*")));
		System.out.println(time.substring(time.indexOf("*")+1)+"  "+time.substring(0,time.indexOf("*")));
//		parmsmap.put("startTime", "2012-06-04 18:17:26");
//		parmsmap.put("endTime", "2012-06-05 15:32:13");
		ICollection iCollenction = MonitorLogTabView.getLog(parmsmap);
		IEnumerator monitorlog = iCollenction.GetEnumerator();
		BusinessObject monitorlogbo = null;
		// Map<String, String> xydata = new HashMap<String, String>();
		while (monitorlog.MoveNext()) {
			monitorlogbo = (BusinessObject) monitorlog.get_Current();
			String loginfo = monitorlogbo.GetField("MonitorMassage")
					.get_NativeValue().toString();
			// System.out.println("监测器描述日志数据: "+loginfo);
			if (monitortype.equals("Ecc.Memory")) {
				if (!loginfo.startsWith("no data")) {
					String used = loginfo.substring(0, loginfo.indexOf("%"));
					xydata.put(used, "100");
				}
			}
		}
		setXydata(xydata);
	}

}
