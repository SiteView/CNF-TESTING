package SiteView.ecc.tab.views;

import java.util.HashMap;
import java.util.Map;

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
			MonitorLogTabView.SetData(bo);
			SummaryTabView.setData(bo);
		}
		return flag;
	}

}
