package SiteView.ecc.tab.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.tools.ArrayTool;
import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
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

public class TotalTabView extends LayoutViewBase {
	public static Map<String, List<String>> xydataMap = new HashMap<String, List<String>>();// 构建报表的X、Y坐标数据
	public static String startTime, endTime = "";// 报表数据查询开始时间、结束时间
	public static String goodAlarmCondition, errorAlarmCondition, warningAlarmCondition = "";// 正常、错误、危险报警阀值条件
	public static int goodcount, warningcount, errorcount, disablecount = 0;// 正常、危险、错误、禁止数量
	public static String monitorName = "";// 监测器名称
	public static List<String> logTimeAndlogInfoArrayList = new ArrayList<String>();// 日志时间#日志内容集合
	public static String newvalue = "";// 最新值
	public static List<String> xyDataArrayList = new ArrayList<String>();//需要显示的报表数据源
	public static String xname, yname = "";// X、Y坐标名称
	public static String reportDescName = "";// 报表描述
	public static List<Map<String, List<String>>> reportDescList = new ArrayList<Map<String, List<String>>>();// 报表返回值、最大值、平均值、最新值集合

	public TotalTabView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createView(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SetDataFromBusOb(BusinessObject bo) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 数据初始化
	 */
	public static void dataInitialization(){
		goodAlarmCondition = "";
		errorAlarmCondition = "";
		warningAlarmCondition = "";
		goodcount = 0;
		errorcount = 0;
		warningcount = 0;
		disablecount = 0;
		xname = "";
		yname = "";
		xydataMap.clear();
		reportDescList.clear();
		xyDataArrayList.clear();
		logTimeAndlogInfoArrayList.clear();
		
	}
	/**
	 * 获取和解析监测器概要数据
	 * 
	 * @param bo
	 */
	public static void setTotalData(BusinessObject bo) {
		dataInitialization();//初始化数据
		String monitortype = bo.get_Name();
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
		String time = MonitorLogTabView.getTwoHoursAgoTime();
		startTime = time.substring(time.indexOf("*") + 1);
		endTime = time.substring(0, time.indexOf("*"));
		parmsmap.put("startTime", startTime);
		parmsmap.put("endTime", endTime);
		getAlarmCondition(bo.get_RecId(), monitortype);// 获取阀值条件
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
			String logtime = monitorlogbo.GetField("CreatedDateTime")
					.get_NativeValue().toString();
			monitorName = monitorlogbo.GetField("MonitorName")
					.get_NativeValue().toString();
			logTimeAndlogInfoArrayList.add(logtime + "#" + loginfo);
		}
		analyticLogReturnMap(monitortype,logTimeAndlogInfoArrayList);
	}

	/**
	 * <p>解析监测器日志</p>
	 * @param monitortype
	 * @param logTimeAndlogInfoList
	 * @return
	 */
	public static List<Map<String, List<String>>> analyticLogReturnMap(
			String monitortype, List<String> logTimeAndlogInfoList) {
		String filePath = FileTools.getRealPath("\\files\\MonitorTemplate.properties");
		String templateString = Config.getReturnStr(filePath, monitortype);
		String[] templateArray = templateString.split(",");//返回需要显示报表数据位置和返回值
		String isdraw = templateArray[0].substring(templateArray[0].indexOf("=")+1, templateArray[0].length());//需要展示报表的数据索引位置
		xname = templateArray[1].substring(templateArray[1].indexOf("=")+1, templateArray[1].length());
		yname = xname;
		reportDescName = xname+" ";
		List<String> otherDataArrayList = new ArrayList<String>();//需要显示的报表数据源
		for (String loginfoAndLogTimestr : logTimeAndlogInfoList) {
			String logtime = loginfoAndLogTimestr.substring(0,
					loginfoAndLogTimestr.indexOf("#"));// 监测器日志时间
			String loginfo = loginfoAndLogTimestr.substring(
					loginfoAndLogTimestr.indexOf("#") + 1,
					loginfoAndLogTimestr.length());// 监测器日志内容
			String [] logArrayStr = loginfo.split("\t");
				if (!loginfo.startsWith("no data") && loginfo.length() > 0) {
					String indexstr = logArrayStr[Integer.parseInt(isdraw)];
					
					if (newvalue.equals("")) {
						newvalue = indexstr.substring(indexstr.indexOf("=")+1, indexstr.length());
						xyDataArrayList.add(newvalue + "#" + logtime);
						otherDataArrayList.add(newvalue);
					} else {
						String used = indexstr.substring(indexstr.indexOf("=")+1, indexstr.length());
						xyDataArrayList.add(used + "#" + logtime);
						otherDataArrayList.add(used);
					}
				} else {
					xyDataArrayList.add("0#" + logtime + "");
				}
				
		}
		  	StringBuffer descsf = new StringBuffer(); 
			for (int i = 0; i < logTimeAndlogInfoList.size(); i++) {
				String descValue = "";
				String loginfo = logTimeAndlogInfoList.get(i).substring(logTimeAndlogInfoList.get(i).indexOf("#") + 1,logTimeAndlogInfoList.get(i).length());// 监测器日志内容
				String [] logArrayStr = loginfo.split("\t");
				for (int k = 0; k < logArrayStr.length; k++) {
						String str = logArrayStr[k];
						if (!str.startsWith("no data")) {
							String key = str.substring(0, str.indexOf("="));
							String otherValue = str.substring((str.indexOf("=")+1), str.length());
							if (otherValue.equals("ok")) {
								otherValue = "200";
							}
							descValue = descValue+key+"="+otherValue+",";
						}
						if (k==logArrayStr.length-1) {
							descsf.append(descValue+"#");
						}
				
				}
			}
			String[] descArray = descsf.toString().split("#");
			List<List<String>> strarray = new ArrayList<List<String>>();
			for (int i = 0; i < templateArray.length-1; i++) {
				List<String> strlist = new ArrayList<String>();
				strarray.add(strlist);
			}
			for (int i = 0; i < descArray.length; i++) {
				String indexstr = descArray[i];
				String[] strs = new String[templateArray.length-1];
				for (int k = 1; k < templateArray.length; k++) {
					String ss = templateArray[k].substring(0, templateArray[k].indexOf("="));
					strs[k-1] = ss;
				}
				for (int k = 0; k<strs.length; k++) {
					List<String> lsist = strarray.get(k);
					if (indexstr.contains(strs[k])) {
						String fs = indexstr.substring(indexstr.indexOf(strs[k]));
						lsist.add(fs.substring(fs.indexOf("=")+1, fs.indexOf(",")));
					}
				}
			}
			
			for (int i = 0; i < strarray.size(); i++) {
				double[] arrayintarray = new double[strarray.get(i).size()];
				int h = 0;
				for (String str : strarray.get(i)) {
					if (!str.equals("n/a")) {
						arrayintarray[h++] = Double.parseDouble(str);
					}
				}
				List<String> otherIntArrayList = new ArrayList<String>();
				if (arrayintarray.length > 0) {
					double arraymax = ArrayTool.getDoubleArrayMax(arrayintarray);// 最大值
					double arrayavg = ArrayTool.getDoubleArrayAvg(arrayintarray);// 平均值
					otherIntArrayList.add(String.valueOf(arraymax));
					otherIntArrayList.add(String.valueOf(arrayavg));
					otherIntArrayList.add(String.valueOf(arrayintarray[0]));// 最新值
				}
				    String mapkey = templateArray[i+1].substring(templateArray[i+1].indexOf("=")+1, templateArray[i+1].length());
					Map<String, List<String>> arrayListMap = new HashMap<String, List<String>>();// 存放每一行返回值和参数
					arrayListMap.put(mapkey, otherIntArrayList);
					reportDescList.add(arrayListMap);
			}
			return reportDescList;
	}

	/**
	 * 获取报警条件阀值
	 */
	public static void getAlarmCondition(String monitorId, String monitorType) {
		ICollection iCollenction = getAlarmConditionCollenction(monitorId);
		IEnumerator alarmEnumerator = iCollenction.GetEnumerator();
		BusinessObject alarmBo = null;
		String filePath = FileTools.getRealPath("\\files\\MonitorReturnValveReference.properties");
		String monitorReturnColumn = Config.getReturnStr(filePath, monitorType);//获取监测器阀值列名
		while (alarmEnumerator.MoveNext()) {
			alarmBo = (BusinessObject) alarmEnumerator.get_Current();
			String alarmStatus = alarmBo.GetField("AlarmStatus").get_NativeValue().toString();//阀值条件(错误、正常、危险)
			String monitorReturnVlaue =  alarmBo.GetFieldOrSubfield(monitorReturnColumn).get_NativeValue().toString();//阀值返回值
			String operator = alarmBo.GetField("Operator").get_NativeValue().toString();//阀值操作符
			String alramValue = alarmBo.GetField("AlramValue").get_NativeValue().toString();//阀值
			if (alarmStatus.equals("good")) {
				goodAlarmCondition = "["+monitorReturnVlaue+"" + " " + operator + " " + alramValue + "]";
			}else if (alarmStatus.equals("warning")) {
				warningAlarmCondition = "["+monitorReturnVlaue+"" + " " + operator + " " + alramValue + "]";
			}else if (alarmStatus.equals("error")) {
				errorAlarmCondition = "["+monitorReturnVlaue+"" + " " + operator + " " + alramValue + "]";
			}
		}
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
