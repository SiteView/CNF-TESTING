package SiteView.ecc.tab.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.widgets.Composite;
import SiteView.ecc.tools.ArrayTool;
import SiteView.ecc.views.EccReportView;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import siteview.windows.forms.LayoutViewBase;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;

/**
 * <p>
 * 监测器概要信息视图.
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class SummaryTabView extends LayoutViewBase {
	public static List<String> xydata = new ArrayList<String>();// 构建报表的X、Y坐标数据
	public static List<String> descList = new ArrayList<String>();// 描述信息
	public static int goodcount, warningcount, errorcount, disablecount = 0;// 正常、危险、错误、禁止数量
	public static String startTime, endTime = "";// 报表数据查询开始时间、结束时间
	public static String newvalue = "";// 最新值
	public static int max, min, avg = 0;// 最大值、最小值、平均值
	public static String monitorName = "";// 监测器名称
	public static String reportDescName = "";// 报表描述
	public static String alarmCondition = "";// 报警阀值条件

	public static String getStartTime() {
		return startTime;
	}

	public static void setStartTime(String startTime) {
		SummaryTabView.startTime = startTime;
	}

	public static String getEndTime() {
		return endTime;
	}

	public static void setEndTime(String endTime) {
		SummaryTabView.endTime = endTime;
	}

	public static int getDisablecount() {
		return disablecount;
	}

	public static void setDisablecount(int disablecount) {
		SummaryTabView.disablecount = disablecount;
	}

	public static int getGoodcount() {
		return goodcount;
	}

	public static void setGoodcount(int goodcount) {
		SummaryTabView.goodcount = goodcount;
	}

	public static int getWarningcount() {
		return warningcount;
	}

	public static void setWarningcount(int warningcount) {
		SummaryTabView.warningcount = warningcount;
	}

	public static int getErrorcount() {
		return errorcount;
	}

	public static void setErrorcount(int errorcount) {
		SummaryTabView.errorcount = errorcount;
	}

	public static List<String> getDescList() {
		return descList;
	}

	public static void setDescList(List<String> descList) {
		SummaryTabView.descList = descList;
	}

	public static String xname, yname = "";// X、Y坐标名称

	public static String getXname() {
		return xname;
	}

	public static void setXname(String xname) {
		SummaryTabView.xname = xname;
	}

	public static String getYname() {
		return yname;
	}

	public static void setYname(String yname) {
		SummaryTabView.yname = yname;
	}

	public List<String> getXydata() {
		return xydata;
	}

	public static void setXydata(List<String> xydata) {
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

	/**
	 * 获取和解析监测器概要数据
	 * 
	 * @param bo
	 */
	public static void setSummaryData(BusinessObject bo) {
		// TODO Auto-generated method stub
		dataInitialization();// 初始化数据
		String monitortype = bo.get_Name();
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
		String time = MonitorLogTabView.getTwoHoursAgoTime();
		startTime = time.substring(time.indexOf("*") + 1);
		endTime = time.substring(0, time.indexOf("*"));
		parmsmap.put("startTime", startTime);
		parmsmap.put("endTime", endTime);
		alarmCondition = getAlarmCondition(bo.get_RecId(), monitortype);// 获取阀值条件
		// parmsmap.put("startTime", "2012-06-04 18:17:26");
		// parmsmap.put("endTime", "2012-06-05 15:32:13");
		ICollection iCollenction = MonitorLogTabView.getLog(parmsmap);
		IEnumerator monitorlog = iCollenction.GetEnumerator();
		BusinessObject monitorlogbo = null;
		while (monitorlog.MoveNext()) {
			monitorlogbo = (BusinessObject) monitorlog.get_Current();
			String monitorstatus = monitorlogbo.GetField("MonitorStatus")
					.get_NativeValue().toString();
			if (monitorstatus.equals("good")) {
				goodcount++;
			}
			if (monitorstatus.equals("error")) {
				errorcount++;
			}
			if (monitorstatus.equals("warning")) {
				warningcount++;
			}
			String loginfo = monitorlogbo.GetField("MonitorMassage")
					.get_NativeValue().toString();
			// System.out.println("监测器描述日志数据: "+loginfo);
			String logtime = monitorlogbo.GetField("CreatedDateTime")
					.get_NativeValue().toString();
			monitorName = monitorlogbo.GetField("MonitorName")
					.get_NativeValue().toString();
			analyticLog(monitortype, loginfo, logtime);
		}
		int[] intarray = new int[xydata.size()];
		int i = 0;
		for (String str : xydata) {
			String x = str.substring(0, str.indexOf("#"));
			intarray[i++] = Integer.parseInt(x);
		}
		if (intarray.length > 0) {
			max = ArrayTool.getMax(intarray);// 最大值
			min = ArrayTool.getMin(intarray);// 最小值
			avg = ArrayTool.getAvg(intarray);// 平均值
		}
		Map<String, String> xyLineMap = getXYName(monitortype);
		xname = xyLineMap.get("XLineName");
		yname = xyLineMap.get("YLineName");
		if (newvalue.equals(""))
			newvalue = "0";
		descList = getReturnDescList(monitortype);//获取监测器返回描述值列表
		setXydata(xydata);
		setXname(xname);
		setYname(yname);
		setDescList(descList);
		setGoodcount(goodcount);
		setErrorcount(errorcount);
		setWarningcount(warningcount);
		setStartTime(startTime);
		setEndTime(endTime);
	}

	/**
	 * 数据初始化
	 */
	public static void dataInitialization() {
		newvalue = "";
		monitorName = "";
		reportDescName = "";
		alarmCondition = "";
		max = 0;
		min = 0;
		avg = 0;
		goodcount = 0;
		errorcount = 0;
		warningcount = 0;
		disablecount = 0;
		xname = "";
		yname = "";
		descList.clear();
		xydata.clear();
	}

	/**
	 * 获取监测器返回描述值列表
	 * 
	 * @return descList
	 */
	public static List<String> getReturnDescList(String monitorType) {
		descList.add(yname + "&"
				+ String.valueOf(Double.parseDouble(String.valueOf(max))) + "&"
				+ String.valueOf(Double.parseDouble(String.valueOf(avg))) + "&"
				+ Double.parseDouble(newvalue));
		if (monitorType.equals("Ecc.URL")) {
//			descList.add("下载时间(s)"+"&"+"1.84"+"&"+"0.906"+"&"+"0.45");
		}
		return descList;
	}

	/**
	 * 解析监测器日志
	 */
	public static void analyticLog(String monitortype, String loginfo,
			String logtime) {
		if (monitortype.equals("Ecc.Memory")
				|| monitortype.equals("Ecc.DiskSpace")) {
			if (!loginfo.startsWith("no data") && loginfo.length() > 0) {
				if (newvalue.equals("")) {
					newvalue = loginfo.substring(0, loginfo.indexOf("%"));
					xydata.add(newvalue + "#" + logtime);
				} else {
					String used = loginfo.substring(0, loginfo.indexOf("%"));
					xydata.add(used + "#" + logtime);
				}
			} else {
				xydata.add("0#" + logtime + "");
			}
		}
		if (monitortype.equals("Ecc.ping")) {
			if (!loginfo.startsWith("no data") && loginfo.length() > 0) {
				if (newvalue.equals("")) {
					newvalue = loginfo.substring(loginfo.lastIndexOf("\t") + 1);
					xydata.add(newvalue + "#" + logtime);
				} else {
					String used = loginfo
							.substring(loginfo.lastIndexOf("\t") + 1);
					xydata.add(used + "#" + logtime);
				}
			} else {
				xydata.add("0#" + logtime + "");
			}
		}
		if (monitortype.equals("Ecc.URL")) {
			if (!loginfo.startsWith("no data") && loginfo.length() > 0) {
				String s1 = loginfo.substring(0, loginfo.indexOf("sec"));
				loginfo = loginfo.substring(loginfo.indexOf("\t") + 1);
				if (newvalue.equals("")) {
					newvalue = loginfo.substring(0, loginfo.indexOf("\t") + 1)
							.trim();
					xydata.add(newvalue + "#" + logtime);
				} else {
					String used = loginfo.substring(0,
							loginfo.indexOf("\t") + 1).trim();
					xydata.add(used + "#" + logtime);
				}
			} else {
				xydata.add("0#" + logtime + "");
			}
		}
	}

	/**
	 * 获取报表XY轴名称
	 */
	public static Map<String, String> getXYName(String monitortype) {
		Map<String, String> xylinemap = new HashMap<String, String>();
		String xLineName = "";
		String yLineName = "";
		if (monitortype.equals("Ecc.Memory")) {
			xLineName = "内存使用率(%)最大值" + Double.parseDouble(String.valueOf(max))
					+ "  最小值" + Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "内存使用率(%)";
			reportDescName = "内存使用率百分比  ";
		}
		if (monitortype.equals("Ecc.ping")) {
			xLineName = "包成功率(%)最大值" + Double.parseDouble(String.valueOf(max))
					+ "  最小值" + Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "包成功率(%)";
			reportDescName = "包成功率百分比  ";
		}
		if (monitortype.equals("Ecc.DiskSpace")) {
			xLineName = "Disk使用率(%)最大值"
					+ Double.parseDouble(String.valueOf(max)) + "  最小值"
					+ Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "Disk使用率(%)";
			reportDescName = "Disk使用率百分比  ";
		}
		if (monitortype.equals("Ecc.URL")) {
			xLineName = "返回码  最大值" + Double.parseDouble(String.valueOf(max))
					+ "  最小值" + Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "返回码";
			reportDescName = "返回码  ";
		}
		xylinemap.put("XLineName", xLineName);
		xylinemap.put("YLineName", yLineName);
		return xylinemap;
	}

	/**
	 * 获取报警条件阀值
	 */
	public static String getAlarmCondition(String monitorId, String monitorType) {
		ICollection iCollenction = getAlarmConditionCollenction(monitorId);
		IEnumerator alarmEnumerator = iCollenction.GetEnumerator();
		BusinessObject alarmBo = null;
		StringBuffer sf = new StringBuffer();
		while (alarmEnumerator.MoveNext()) {
			alarmBo = (BusinessObject) alarmEnumerator.get_Current();
			String operator = alarmBo.GetField("Operator").get_NativeValue()
					.toString();
			String alramValue = alarmBo.GetField("AlramValue")
					.get_NativeValue().toString();
			if (monitorType.equals("Ecc.Memory")) {
				sf.append("[内存使用 " + " " + operator + " " + alramValue + "]");
			}
			if (monitorType.equals("Ecc.ping")) {
				sf.append("[包成功率 " + " " + operator + " " + alramValue + "]");
			}
			if (monitorType.equals("Ecc.ping")) {
				sf.append("[Disk使用率 " + " " + operator + " " + alramValue + "]");
			}
			if (monitorType.equals("Ecc.URL")) {
				sf.append("[返回码 " + " " + operator + " " + alramValue + "]");
			}
		}
		return sf.toString();
	}

	/**
	 * 获取报警条件阀值集合
	 * 
	 * @param recId
	 * @return iCollenction
	 */
	public static ICollection getAlarmConditionCollenction(String recId) {
		ISiteviewApi siteviewApi = ConnectionBroker.get_SiteviewApi();
		SiteviewQuery siteviewquery = new SiteviewQuery();
		siteviewquery.AddBusObQuery("Alarm", QueryInfoToGet.All);
		XmlElement xmlElementscanconfigid = siteviewquery.get_CriteriaBuilder()
				.FieldAndValueExpression("ParentLink_RecID", Operators.Equals,
						recId);
		siteviewquery.set_BusObSearchCriteria(xmlElementscanconfigid);
		ICollection iCollenction = siteviewApi.get_BusObService()
				.get_SimpleQueryResolver()
				.ResolveQueryToBusObList(siteviewquery);
		return iCollenction;
	}
}
