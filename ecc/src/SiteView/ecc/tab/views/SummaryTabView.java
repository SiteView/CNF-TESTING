package SiteView.ecc.tab.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public static String xname, yname = "";// X、Y坐标名称
	public static List<Map<String, List<String>>> reportDescList = new ArrayList<Map<String, List<String>>>();// 报表返回值、最大值、平均值、最新值集合
	public static String returnKeyString = "";// 返回值名称
	public static List<String> logTimeAndlogInfoArrayList = new ArrayList<String>();// 日志时间#日志内容集合

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
	 * 数据初始化
	 */
	public static void dataInitialization() {
		newvalue = "";
		monitorName = "";
		reportDescName = "";
		returnKeyString = "";
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
		logTimeAndlogInfoArrayList.clear();
		reportDescList.clear();
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
			logTimeAndlogInfoArrayList.add(logtime + "#" + loginfo);
		}
		List<Map<String, List<String>>> li = analyticLogReturnMap(monitortype,
				logTimeAndlogInfoArrayList);
		int[] intarray = new int[xydata.size()];
		int i = 0;
		for (String str : xydata) {
			String x = str.substring(0, str.indexOf("#"));
			intarray[i++] = Integer.parseInt(x);
		}
		if (intarray.length > 0) {
			max = ArrayTool.getIntArrayMax(intarray);// 最大值
			min = ArrayTool.getIntArrayMin(intarray);// 最小值
			avg = ArrayTool.getIntArrayAvg(intarray);// 平均值
		}
		Map<String, String> xyLineMap = getXYName(monitortype);
		xname = xyLineMap.get("XLineName");
		yname = xyLineMap.get("YLineName");
		if (newvalue.equals(""))
			newvalue = "0";
		descList = getReturnDescList(monitortype, li);// 获取监测器返回描述值列表
	}

	/**
	 * 获取监测器返回描述值列表
	 * 
	 * @return descList
	 */
	public static List<String> getReturnDescList(String monitorType,
			List<Map<String, List<String>>> list) {
		descList.add(yname + "&"
				+ String.valueOf(Double.parseDouble(String.valueOf(max))) + "&"
				+ String.valueOf(Double.parseDouble(String.valueOf(avg))) + "&"
				+ Double.parseDouble(newvalue));
		if (list!=null) {
			for (Map<String, List<String>> map : list) {
				Set<Map.Entry<String, List<String>>> set = map.entrySet();
				for (Iterator<Map.Entry<String, List<String>>> it = set.iterator(); it
						.hasNext();) {
					Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) it
							.next();
					List<String> arrlist = entry.getValue();
					System.out.println(arrlist.get(0));
					if (monitorType.equals(arrlist.get(0))) {
						descList.add(entry.getKey() + "&" + arrlist.get(1) + "&"
								+ arrlist.get(2) + "&" + arrlist.get(3));
					}
				}
			}
		}
		return descList;
	}

	/**
	 * @param String
	 *            monitortype监测器类型 List<String>
	 *            logTimeAndlogInfoList监测器日志时间#内容集合 解析监测器日志并返回
	 *            报表返回值、最大值、平均值、最新值集合
	 */
	public static List<Map<String, List<String>>> analyticLogReturnMap(
			String monitortype, List<String> logTimeAndlogInfoList) {
		List<String> arrayList = new ArrayList<String>();// 存放每条日志的不同参数
		List<String> intArrayList = new ArrayList<String>();
		Map<String, List<String>> arrayListMap = new HashMap<String, List<String>>();// 存放每一行返回值和参数
		for (String loginfoAndLogTimestr : logTimeAndlogInfoList) {
			String logtime = loginfoAndLogTimestr.substring(0,
					loginfoAndLogTimestr.indexOf("#"));// 监测器日志时间
			String loginfo = loginfoAndLogTimestr.substring(
					loginfoAndLogTimestr.indexOf("#") + 1,
					loginfoAndLogTimestr.length());// 监测器日志内容
			if (monitortype.equals("Ecc.Memory")
					|| monitortype.equals("Ecc.DiskSpace")) {
				if (!loginfo.startsWith("no data") && loginfo.length() > 0) {
					if (newvalue.equals("")) {
						newvalue = loginfo.substring(0, loginfo.indexOf("%"));
						xydata.add(newvalue + "#" + logtime);
					} else {
						String used = loginfo
								.substring(0, loginfo.indexOf("%"));
						xydata.add(used + "#" + logtime);
					}
				} else {
					xydata.add("0#" + logtime + "");
				}
			} else if (monitortype.equals("Ecc.ping")) {
				if (!loginfo.startsWith("no data") && loginfo.length() > 0) {
					if (newvalue.equals("")) {
						newvalue = loginfo
								.substring(loginfo.lastIndexOf("\t") + 1);
						xydata.add(newvalue + "#" + logtime);
					} else {
						String used = loginfo.substring(loginfo
								.lastIndexOf("\t") + 1);
						xydata.add(used + "#" + logtime);
					}
				} else {
					xydata.add("0#" + logtime + "");
				}
			} else if (monitortype.equals("Ecc.URL")) {
				if (!loginfo.startsWith("no data") && loginfo.length() > 0) {
					String downLoadTime = loginfo.substring(0,
							loginfo.indexOf("sec"));
					arrayList.add(downLoadTime);// 加入下载时间参数集合
					returnKeyString = "下载时间(s)";
					loginfo = loginfo.substring(loginfo.indexOf("\t") + 1);
					if (newvalue.equals("")) {
						newvalue = loginfo.substring(0,
								loginfo.indexOf("\t") + 1).trim();
						xydata.add(newvalue + "#" + logtime);
					} else {
						String used = loginfo.substring(0,
								loginfo.indexOf("\t") + 1).trim();
						xydata.add(used + "#" + logtime);
					}
				} else {
					xydata.add("0#" + logtime + "");
				}
			}else if (monitortype.equals("Ecc.File")) {
					if (!loginfo.startsWith("not found") && loginfo.length() > 0) {
						String createdTime = loginfo.substring(loginfo.indexOf(",")+2,loginfo.indexOf("minutes")-1);//文件生存时间
						arrayList.add(createdTime);// 加入文件生存时间集合
						returnKeyString = "生存时间(s)";
						if (newvalue.equals("")) {
							newvalue = loginfo.substring(0,loginfo.indexOf("bytes")-1);
							xydata.add(newvalue + "#" + logtime);
						} else {
							String used = loginfo.substring(0,loginfo.indexOf("bytes")-1);
							xydata.add(used + "#" + logtime);
						}
					} else {
						xydata.add("0#" + logtime + "");
					}
				}else if (monitortype.equals("Ecc.Service")) {
				if (!loginfo.startsWith("not found") && loginfo.length() > 0) {
					if (newvalue.equals("")) {
						String status = loginfo.substring(0,loginfo.indexOf(","));
						loginfo = loginfo.substring(loginfo.lastIndexOf(status));
						loginfo =loginfo.substring(loginfo.indexOf("\t")+1).trim();
						newvalue = loginfo.substring(0,loginfo.indexOf("\t"));
						xydata.add(newvalue + "#" + logtime);
					} else {
						String status = loginfo.substring(0,loginfo.indexOf(","));
						loginfo = loginfo.substring(loginfo.lastIndexOf(status));
						loginfo =loginfo.substring(loginfo.indexOf("\t")+1).trim();
						String used = loginfo.substring(0,loginfo.indexOf("\t"));
						xydata.add(used + "#" + logtime);
					}
				} else {
					xydata.add("0#" + logtime + "");
				}
			}
		}
		double[] arrayintarray = new double[arrayList.size()];
		int i = 0;
		for (String str : arrayList) {
			arrayintarray[i++] = Double.parseDouble(str);
		}
		if (arrayintarray.length > 0) {
			double arraymax = ArrayTool.getDoubleArrayMax(arrayintarray);// 最大值
			double arrayavg = ArrayTool.getDoubleArrayAvg(arrayintarray);// 平均值
			intArrayList.add(monitortype);// 监测器类型
			intArrayList.add(String.valueOf(arraymax));
			intArrayList.add(String.valueOf(arrayavg));
			intArrayList.add(String.valueOf(arrayintarray[0]));// 最新值
		}
		arrayListMap.put(returnKeyString, intArrayList);
		reportDescList.add(arrayListMap);
		if (returnKeyString.equals("")) {
			return null;
		} else {
			return reportDescList;
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
		} else if (monitortype.equals("Ecc.ping")) {
			xLineName = "包成功率(%)最大值" + Double.parseDouble(String.valueOf(max))
					+ "  最小值" + Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "包成功率(%)";
			reportDescName = "包成功率百分比  ";
		} else if (monitortype.equals("Ecc.DiskSpace")) {
			xLineName = "Disk使用率(%)最大值"
					+ Double.parseDouble(String.valueOf(max)) + "  最小值"
					+ Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "Disk使用率(%)";
			reportDescName = "Disk使用率百分比  ";
		} else if (monitortype.equals("Ecc.URL")) {
			xLineName = "返回码  最大值" + Double.parseDouble(String.valueOf(max))
					+ "  最小值" + Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "返回码";
			reportDescName = "返回码  ";
		} else if (monitortype.equals("Ecc.File")) {
			xLineName = "文件大小(B) 最大值" + Double.parseDouble(String.valueOf(max))
					+ "  最小值" + Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "文件大小(B) ";
			reportDescName = "文件大小(B)  ";
		}else if (monitortype.equals("Ecc.Service")) {
			xLineName = "运行实例个数(个) 最大值" + Double.parseDouble(String.valueOf(max))
					+ "  最小值" + Double.parseDouble(String.valueOf(min)) + "平均值"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "运行实例个数(个) ";
			reportDescName = "运行实例个数(个) ";
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
			} else if (monitorType.equals("Ecc.ping")) {
				sf.append("[包成功率 " + " " + operator + " " + alramValue + "]");
			} else if (monitorType.equals("Ecc.ping")) {
				sf.append("[Disk使用率 " + " " + operator + " " + alramValue + "]");
			} else if (monitorType.equals("Ecc.URL")) {
				sf.append("[返回码 " + " " + operator + " " + alramValue + "]");
			} else if (monitorType.equals("Ecc.File")) {
				sf.append("[文件大小(B) " + " " + operator + " " + alramValue + "]");
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
