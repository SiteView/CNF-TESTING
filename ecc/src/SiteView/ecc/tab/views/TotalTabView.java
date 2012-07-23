package SiteView.ecc.tab.views;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
	public static String startTime, endTime = "";// �������ݲ�ѯ��ʼʱ�䡢����ʱ��
	public static String goodAlarmCondition, errorAlarmCondition, warningAlarmCondition = "";// ����������Σ�ձ�����ֵ����
	public static int goodcount, warningcount, errorcount, disablecount,totalcount = 0;// ������Σ�ա����󡢽�ֹ��������¼������
	public static String goodPercentOf,errorPercentOf,warningPercentOf,laststatus,lastTime = "";//����������Σ�հٷֱȡ�����״̬������ֵʱ��
	public static String monitorName = "";// ���������
	public static List<String> logTimeAndlogInfoArrayList = new ArrayList<String>();// ��־ʱ��#��־���ݼ���
	public static String newvalue = "";// ����ֵ
	public static List<String> xyDataArrayList = new ArrayList<String>();//��Ҫ��ʾ�ı�������Դ
	public static String xname, yname = "";// X��Y��������
	public static String reportDescName = "";// ��������
	public static List<Map<String, List<String>>> reportDescList = new ArrayList<Map<String, List<String>>>();// ������ֵ�����ֵ��ƽ��ֵ������ֵ����
	public static List<Map<String, List<String>>> reportEveryDescList = new ArrayList<Map<String, List<String>>>();// ����ÿ�����ݵķ��������б�
	public static BusinessObject businessObj = null;

	public TotalTabView(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createView(final Composite parent) {
		// TODO Auto-generated method stub
		parent.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
			}
			public void controlMoved(ControlEvent e) {
				EccReportView erv = new EccReportView();
				erv.createPartControl(parent);
			}
		});
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
		reportEveryDescList.clear();
		xyDataArrayList.clear();
		monitorName = "";
		logTimeAndlogInfoArrayList.clear();
		
	}
	/**
	 * ��ȡ�ͽ����������Ҫ����
	 * 
	 * @param bo
	 */
	public static void setTotalData(BusinessObject bo) {
		dataInitialization();//��ʼ������
	    businessObj = bo;
		String monitortype = bo.get_Name();
		monitorName = bo.GetField("title")
				.get_NativeValue().toString();
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
		if (startTime.equals("") || endTime.equals("")) {
			String time = MonitorLogTabView.getHoursAgoTime(2);
			startTime = time.substring(time.indexOf("*") + 1);
			endTime = time.substring(0, time.indexOf("*"));
		}
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
			String logLastTime = monitorlogbo.GetField("CreatedDateTime")
					.get_NativeValue().toString();
			if (laststatus.equals("")) {
				if (monitorstatus.equals("good")) {
					laststatus ="����";
				}else if (monitorstatus.equals("error")) {
					laststatus ="����";
				}else if(monitorstatus.equals("warning")){
					laststatus = "Σ��";
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
		goodPercentOf = percent(goodcount, totalcount);//������ȷ�ٷֱ�
		errorPercentOf = percent(errorcount, totalcount);//�������ٷֱ�
		warningPercentOf = percent(warningcount, totalcount);//����Σ�հٷֱ�
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
		if (templateString==null) {
			templateString = "isdraw=0,?=?";
		}
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
				String logTime = logTimeAndlogInfoList.get(i).substring(0, logTimeAndlogInfoList.get(i).indexOf("#"));
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
						if (!valuekey.equals("no data")&&!valuekey.equals("failed")) {
							arrayintarray[h++] = Double.parseDouble(valuekey);
						}
					}
				}
				List<String> otherIntArrayList = new ArrayList<String>();
				if (arrayintarray.length > 0) {
					double arraymax = ArrayTool.getDoubleArrayMax(arrayintarray);// ��ȡ���ֵ
					double arrayavg = ArrayTool.getDoubleArrayAvg(arrayintarray);// ��ȡƽ��ֵ
					double arraymin = ArrayTool.getDoubleArrayMin(arrayintarray);//��ȡ��Сֵ
					otherIntArrayList.add(String.valueOf(arraymax));//���ֵ(�±�0)
					otherIntArrayList.add(String.valueOf(arrayavg));//ƽ��ֵ(�±�1)
					otherIntArrayList.add(String.valueOf(arrayintarray[0]));// ����ֵ(�±�2)
					otherIntArrayList.add(String.valueOf(arraymin));//��Сֵ(�±�3)
					if (timeLogValueMap.get(String.valueOf(arraymax))==null) {
						otherIntArrayList.add(timeLogValueMap.get(String.valueOf((int)arraymax)));//���ֵʱ��(�±�4)
					}else{
						otherIntArrayList.add(timeLogValueMap.get(String.valueOf(arraymax)));//���ֵʱ��(�±�4)
					}
					timeLogValueMap.clear();
				}
				    String mapkey = templateArray[i+1].substring(templateArray[i+1].indexOf("=")+1, templateArray[i+1].length());
					Map<String, List<String>> arrayListMap = new HashMap<String, List<String>>();// ���ÿһ�з���ֵ�Ͳ���
					Map<String, List<String>> descarrayListMap = new HashMap<String, List<String>>();// ���ÿһ����������
					arrayListMap.put(mapkey, otherIntArrayList);
					descarrayListMap.put(mapkey, strarray.get(i));
					reportDescList.add(arrayListMap);
					reportEveryDescList.add(descarrayListMap);
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
	/**
	 * ����ٷֱ�
	 * @param y
	 * @param z
	 * @return
	 */
	public static String percent(int y,int z){
		   String baifenbi="";//�ٷֱȵ�ֵ
		   double baiy=y*1.0;
		   double baiz=z*1.0;
		   double fen=baiy/baiz;
		   NumberFormat nf   =   NumberFormat.getPercentInstance();
		   nf.setMinimumFractionDigits( 2 );//������С�����λ
		   baifenbi=nf.format(fen);
		   if (String.valueOf(fen).equals("NaN")) {
			   baifenbi = "������";
		}
		   return baifenbi;
		}

}
