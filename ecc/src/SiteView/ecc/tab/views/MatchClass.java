package SiteView.ecc.tab.views;


import SiteView.ecc.reportchart.StatusCTIReport;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.MatchStrategyBase;

public class MatchClass extends MatchStrategyBase {

	public MatchClass() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean match(BusinessObject bo) {
		// TODO Auto-generated method stub
		boolean flag=false;
		if(bo.get_Name().startsWith("Ecc.")){
			flag=!(bo.GetField("groups").get_NativeValue().toString().equals(""));
			String time = MonitorLogTabView.getHoursAgoTime(2);
			TotalTabView.startTime = time.substring(time.indexOf("*") + 1);
			TotalTabView.endTime = time.substring(0, time.indexOf("*"));
			MonitorLogTabView.SetData(bo);
			TotalTabView.setTotalData(bo);
			StatusCTIReport.setData(bo);
		}
		return flag;
	}

}
