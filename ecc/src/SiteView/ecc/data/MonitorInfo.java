package SiteView.ecc.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MonitorInfo {
	private Long id;
	private String status;
	private String refresh;
	private String monitorname;
	private String desc;
	private Date lastupdateDate;

	public MonitorInfo(Long id, String status, String refresh,
			String monitorname, String desc, Date lastupdateDate) {
		this.id = id;
		this.status = status;
		this.refresh = refresh;
		this.monitorname = monitorname;
		this.desc = desc;
		this.lastupdateDate = lastupdateDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRefresh() {
		return refresh;
	}

	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}

	public String getMonitorname() {
		return monitorname;
	}

	public void setMonitorname(String monitorname) {
		this.monitorname = monitorname;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getLastupdateDate() {
		return lastupdateDate;
	}

	public void setLastupdateDate(Date lastupdateDate) {
		this.lastupdateDate = lastupdateDate;
	}

	public static List getMonitorInfo() {
		List list = new ArrayList();
		int n = 30;
		for (int i = 0; i < n; i++) {
			Long id = new Long(i);
			String monitorname = "¼à²âÆ÷Ãû³Æ" + i;
			String status = "";
			String refresh = "";
			String desc = "¼à²âÆ÷ÃèÊöÐÅÏ¢" + i;
			Date lastupdateDate = new Date();
			MonitorInfo m = new MonitorInfo(id, status, refresh, monitorname,
					desc, lastupdateDate);
			list.add(m);
		}
		return list;
	}

}
