package SiteView.ecc.tab.views;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.widgets.Composite;
import SiteView.ecc.tools.ArrayTool;
import SiteView.ecc.tools.Config;
import SiteView.ecc.tools.FileTools;
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

public class TotalTabView extends LayoutViewBase {
	public static String startTime, endTime = "";// 报表数据查询开始时间、结束时间
	public static String goodAlarmCondition, errorAlarmCondition, warningAlarmCondition = "";// 正常、错误、危险报警阀值条件
	public static int goodcount, warningcount, errorcount, disablecount,totalcount = 0;// 正常、危险、错误、禁止数量、记录总数量
	public static String goodPercentOf,errorPercentOf,warningPercentOf,laststatus,lastTime = "";//正常、错误、危险百分比、最新状态、最新值时间
	public static String monitorName = "";// 监测器名称
	public static List<String> logTimeAndlogInfoArrayList = new ArrayList<String>();// 日志时间#日志内容集合
	public static String newvalue = "";// 最新值
	public static List<String> xyDataArrayList = new ArrayList<String>();//需要显示的报表数据源
	public static String xname, yname = "";// X、Y坐标名称
	public static String reportDescName = "";// 报表描述
	public static List<Map<String, List<String>>> reportDescList = new ArrayList<Map<String, List<String>>>();// 报表返回值、最大值、平均值、最新值集合
	public static BusinessObject businessObj = null;

	public TotalTabView(Composite parent) {
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
	public static void dataInitialization(){
		goodAlarmCondition = "";
		errorAlarmCondition = "";
		warningAlarmCondition = "";
		laststatus = "";
		lastTime = "";
		goodPercentOf = "";
		errorPercentOf = "";
		warningPercentOf = "";
		goodcount = 0;
		errorcount = 0;
		warningcount = 0;
		disablecount = 0;
		totalcount = 0;
		xname = "";
		yname = "";
		reportDescList.clear();
		xyDataArrayList.clear();
		monitorName = "";
		logTimeAndlogInfoArrayList.clear();
		
	}
	/**
	 * 获取和解析监测器概要数据
	 * 
	 * @param bo
	 */
	public static void setTotalData(BusinessObject bo) {
		dataInitialization();//初始化数据
	    businessObj = bo;
		String monitortype = bo.get_Name();
		monitorName = bo.GetField("title")
				.get_NativeValue().toString();
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
//		String time = MonitorLogTabView.getTwoHoursAgoTime();
//		startTime = time.substring(time.indexOf("*") + 1);
//		endTime = time.substring(0, time.indexOf("*"));
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
			String logLastTime = monitorlogbo.GetField("CreatedDateTime")
					.get_NativeValue().toString();
			if (laststatus.equals("")) {
				if (monitorstatus.equals("good")) {
					laststatus ="正常";
				}else if (monitorstatus.equals("error")) {
					laststatus ="错误";
				}else if(monitorstatus.equals("warning")){
					laststatus = "危险";
				}
			}
			if (lastTime.equals("")) {
				lastTime = logLastTime;
			}
			if (monitorstatus.equals("good")) {
				goodcount++;
				totalcount++;
			}else if (monitorstatus.equals("error")) {
				errorcount++;
				totalcount++;
			}else if (monitorstatus.equals("warning")) {
				warningcount++;
				totalcount++;
			}else if(monitorstatus.equals("disabled")){
				disablecount++;
			}
			
			String loginfo = monitorlogbo.GetField("MonitorMassage")
					.get_NativeValue().toString();
			String logtime = monitorlogbo.GetField("CreatedDateTime")
					.get_NativeValue().toString();
			logTimeAndlogInfoArrayList.add(logtime + "#" + loginfo);
		}
		goodPercentOf = percent(goodcount, totalcount);//计算正确百分比
		errorPercentOf = percent(errorcount, totalcount);//计算错误百分比
		warningPercentOf = percent(warningcount, totalcount);//计算危险百分比
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
		if (templateString==null) {
			templateString = "isdraw=0,?=?";
		}
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
				String logTime = logTimeAndlogInfoList.get(i).substring(0, logTimeAndlogInfoList.get(i).indexOf("#"));
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
							descValue = descValue+key+"="+otherValue+"$"+logTime+",";
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
			
			Map<String,String> timeLogValueMap = new HashMap<String, String>();
			for (int i = 0; i < strarray.size(); i++) {
				double[] arrayintarray = new double[strarray.get(i).size()];
				int h = 0;
				for (String str : strarray.get(i)) {
					String valuekey = str.substring(0, str.indexOf("$"));
					String timeValue = str.substring(str.indexOf("$")+1,str.length());
					timeLogValueMap.put(valuekey, timeValue);
					if (!valuekey.equals("n/a")) {
						if (!valuekey.equals("no data")) {
							arrayintarray[h++] = Double.parseDouble(valuekey);
						}
					}
				}
				List<String> otherIntArrayList = new ArrayList<String>();
				if (arrayintarray.length > 0) {
					double arraymax = ArrayTool.getDoubleArrayMax(arrayintarray);// 获取最大值
					double arrayavg = ArrayTool.getDoubleArrayAvg(arrayintarray);// 获取平均值
					double arraymin = ArrayTool.getDoubleArrayMin(arrayintarray);//获取最小值
					otherIntArrayList.add(String.valueOf(arraymax));//最大值(下标0)
					otherIntArrayList.add(String.valueOf(arrayavg));//平均值(下标1)
					otherIntArrayList.add(String.valueOf(arrayintarray[0]));// 最新值(下标2)
					otherIntArrayList.add(String.valueOf(arraymin));//最小值(下标3)
					if (timeLogValueMap.get(String.valueOf(arraymax))==null) {
						otherIntArrayList.add(timeLogValueMap.get(String.valueOf((int)arraymax)));//最大值时间(下标4)
					}else{
						otherIntArrayList.add(timeLogValueMap.get(String.valueOf(arraymax)));//最大值时间(下标4)
					}
					timeLogValueMap.clear();
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
	/**
	 * 计算百分比
	 * @param y
	 * @param z
	 * @return
	 */
	public static String percent(int y,int z){
		   String baifenbi="";//百分比的值
		   double baiy=y*1.0;
		   double baiz=z*1.0;
		   double fen=baiy/baiz;
		   NumberFormat nf   =   NumberFormat.getPercentInstance();
		   nf.setMinimumFractionDigits( 2 );//保留到小数点后几位
		   baifenbi=nf.format(fen);
		   if (String.valueOf(fen).equals("NaN")) {
			   baifenbi = "无数据";
		}
		   return baifenbi;
		}

}
