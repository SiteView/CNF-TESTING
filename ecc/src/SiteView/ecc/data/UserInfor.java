package SiteView.ecc.data;

import java.util.ArrayList;
import java.util.List;

import SiteView.ecc.Modle.UserModle;
import SiteView.ecc.tools.FileTools;
import Siteview.BusUnitInfo;
import Siteview.Api.BusinessObject;
import Siteview.Windows.Forms.ConnectionBroker;

import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Data.DataSet;
import system.Data.DataTable;

public class UserInfor {
	public static List<String> usersid=null;
	public static List<UserModle> list=null;
	public static  List<UserModle> getUserInfor() {
		list=new ArrayList<UserModle>();
		usersid=new ArrayList<String>();
		DataSet ds = ConnectionBroker.get_SiteviewApi().get_AuthenticationService().GetLoginIdsByGroupId("");
		DataTable dt = ds.get_Tables().get_Item(0);
		for(int i = 0; i< dt.get_Rows().get_Count();i++){
			String s=dt.get_Rows().get_Item(i).get_Item(0).toString();
			usersid.add(s);
			Siteview.User u= ConnectionBroker.get_SiteviewApi()
					.get_AuthenticationService().GetUser("User", s);
			if(u.get_SecurityGroupName().equals("������Ա")){
				UserModle user=new UserModle();
				user.setUsername(u.get_LoginId());
				user.setLogname(u.get_LoginId());
				user.setStatus("����");
				user.setUserType("����Ա�û�");
				user.setUsers(u);
	 			list.add(user); 
			}else if(u.get_SecurityGroupName().equals("��⾭��")){
				UserModle user=new UserModle();
				user.setUsername(u.get_LoginId());
				user.setLogname(u.get_LoginId());
				user.setStatus("����");
				user.setUserType("��ͨԱ�û�");
				user.setUsers(u);
				list.add(user);
			}
		}		
		return list;
	}

}
