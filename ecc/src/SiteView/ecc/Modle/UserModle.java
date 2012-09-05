package SiteView.ecc.Modle;

import org.eclipse.swt.graphics.Image;

public class UserModle {
	private String username;
	private String logname;
	private String status;
	private String userType;
	private Siteview.User users;
	
	public UserModle(){}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLogname() {
		return logname;
	}
	public void setLogname(String logname) {
		this.logname = logname;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public UserModle(String username, String logname, String status,
			String userType,Siteview.User user) {
		super();
		this.username = username;
		this.logname = logname;
		this.status = status;
		this.userType = userType;
		this.users=user;
	}
	public Siteview.User getUsers() {
		return users;
	}
	public void setUsers(Siteview.User users) {
		this.users = users;
	}
}
