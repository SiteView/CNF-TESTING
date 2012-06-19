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
	public static Map<String, List<String>> xydataMap = new HashMap<String, List<String>>();// ���������X��Y��������
	public static String startTime, endTime = "";// �������ݲ�ѯ��ʼʱ�䡢����ʱ��
	public static String goodAlarmCondition, errorAlarmCondition, warningAlarmCondition = "";// ����������Σ�ձ�����ֵ����
	public static int goodcount, warningcount, errorcount, disablecount = 0;// ������Σ�ա����󡢽�ֹ����
	public static String monitorName = "";// ���������
	public static List<String> logTimeAndlogInfoArrayList = new ArrayList<String>();// ��־ʱ��#��־���ݼ���
	public static String newvalue = "";// ����ֵ
	public static List<String> xyDataArrayList = new ArrayList<String>();//��Ҫ��ʾ�ı�������Դ
	public static String xname, yname = "";// X��Y��������
	public static String reportDescName = "";// ��������
	public static List<Map<String, List<String>>> reportDescList = new ArrayList<Map<String, List<String>>>();// ������ֵ�����ֵ��ƽ��ֵ������ֵ����

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
	 * ���ݳ�ʼ��
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
	 * ��ȡ�ͽ����������Ҫ����
	 * 
	 * @param bo
	 */
	public static void setTotalData(BusinessObject bo) {
		dataInitialization();//��ʼ������
		String monitortype = bo.get_Name();
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
		String time = MonitorLogTabView.getTwoHoursAgoTime();
		startTime = time.substring(time.indexOf("*") + 1);
		endTime = time.substring(0, time.indexOf("*"));
		parmsmap.put("startTime", startTime);
		parmsmap.put("endTime", endTime);
		getAlarmCondition(bo.get_RecId(), monitortype);// ��ȡ��ֵ����
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
	 * <p>�����������־</p>
	 * @param monitortype
	 * @param logTimeAndlogInfoList
	 * @return
	 */
	public static List<Map<String, List<String>>> analyticLogReturnMap(
			String monitortype, List<String> logTimeAndlogInfoList) {
		String filePath = FileTools.getRealPath("\\files\\MonitorTemplate.properties");
		String templateString = Config.getReturnStr(filePath, monitortype);
		String[] templateArray = templateString.split(",");//������Ҫ��ʾ��������λ�úͷ���ֵ
		String isdraw = templateArray[0].substring(templateArray[0].indexOf("=")+1, templateArray[0].length());//��Ҫչʾ�������������λ��
		xname = templateArray[1].substring(templateArray[1].indexOf("=")+1, templateArray[1].length());
		yname = xname;
		reportDescName = xname+" ";
		List<String> otherDataArrayList = new ArrayList<String>();//��Ҫ��ʾ�ı�������Դ
		for (String loginfoAndLogTimestr : logTimeAndlogInfoList) {
			String logtime = loginfoAndLogTimestr.substring(0,
					loginfoAndLogTimestr.indexOf("#"));// �������־ʱ��
			String loginfo = loginfoAndLogTimestr.substring(
					loginfoAndLogTimestr.indexOf("#") + 1,
					loginfoAndLogTimestr.length());// �������־����
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
				String loginfo = logTimeAndlogInfoList.get(i).substring(logTimeAndlogInfoList.get(i).indexOf("#") + 1,logTimeAndlogInfoList.get(i).length());// �������־����
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
					double arraymax = ArrayTool.getDoubleArrayMax(arrayintarray);// ���ֵ
					double arrayavg = ArrayTool.getDoubleArrayAvg(arrayintarray);// ƽ��ֵ
					otherIntArrayList.add(String.valueOf(arraymax));
					otherIntArrayList.add(String.valueOf(arrayavg));
					otherIntArrayList.add(String.valueOf(arrayintarray[0]));// ����ֵ
				}
				    String mapkey = templateArray[i+1].substring(templateArray[i+1].indexOf("=")+1, templateArray[i+1].length());
					Map<String, List<String>> arrayListMap = new HashMap<String, List<String>>();// ���ÿһ�з���ֵ�Ͳ���
					arrayListMap.put(mapkey, otherIntArrayList);
					reportDescList.add(arrayListMap);
			}
			return reportDescList;
	}

	/**
	 * ��ȡ����������ֵ
	 */
	public static void getAlarmCondition(String monitorId, String monitorType) {
		ICollection iCollenction = getAlarmConditionCollenction(monitorId);
		IEnumerator alarmEnumerator = iCollenction.GetEnumerator();
		BusinessObject alarmBo = null;
		String filePath = FileTools.getRealPath("\\files\\MonitorReturnValveReference.properties");
		String monitorReturnColumn = Config.getReturnStr(filePath, monitorType);//��ȡ�������ֵ����
		while (alarmEnumerator.MoveNext()) {
			alarmBo = (BusinessObject) alarmEnumerator.get_Current();
			String alarmStatus = alarmBo.GetField("AlarmStatus").get_NativeValue().toString();//��ֵ����(����������Σ��)
			String monitorReturnVlaue =  alarmBo.GetFieldOrSubfield(monitorReturnColumn).get_NativeValue().toString();//��ֵ����ֵ
			String operator = alarmBo.GetField("Operator").get_NativeValue().toString();//��ֵ������
			String alramValue = alarmBo.GetField("AlramValue").get_NativeValue().toString();//��ֵ
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
	 * ��ȡ����������ֵ����
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
