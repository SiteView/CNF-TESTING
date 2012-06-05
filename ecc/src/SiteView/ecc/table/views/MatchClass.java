package SiteView.ecc.table.views;

import Siteview.Api.BusinessObject;
import siteview.windows.forms.MatchStrategyBase;

public class MatchClass extends MatchStrategyBase {

	public MatchClass() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean match(BusinessObject bo) {
		// TODO Auto-generated method stub
		System.out.println( bo.get_Name());
		return bo.get_Name().startsWith("Ecc.");
	}

}
