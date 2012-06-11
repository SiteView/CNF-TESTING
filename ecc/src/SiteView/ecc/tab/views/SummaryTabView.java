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
 * �������Ҫ��Ϣ��ͼ.
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class SummaryTabView extends LayoutViewBase {
	public static List<String> xydata = new ArrayList<String>();// ���������X��Y��������
	public static List<String> descList = new ArrayList<String>();// ������Ϣ
	public static int goodcount, warningcount, errorcount, disablecount = 0;// ������Σ�ա����󡢽�ֹ����
	public static String startTime, endTime = "";// �������ݲ�ѯ��ʼʱ�䡢����ʱ��
	public static String newvalue = "";// ����ֵ
	public static int max, min, avg = 0;// ���ֵ����Сֵ��ƽ��ֵ
	public static String monitorName = "";// ���������
	public static String reportDescName = "";// ��������
	public static String alarmCondition = "";// ������ֵ����
	public static String xname, yname = "";// X��Y��������
	public static List<Map<String, List<String>>> reportDescList = new ArrayList<Map<String, List<String>>>();// ������ֵ�����ֵ��ƽ��ֵ������ֵ����
	public static String returnKeyString = "";// ����ֵ����
	public static List<String> logTimeAndlogInfoArrayList = new ArrayList<String>();// ��־ʱ��#��־���ݼ���

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
	 * ���ݳ�ʼ��
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
	 * ��ȡ�ͽ����������Ҫ����
	 * 
	 * @param bo
	 */
	public static void setSummaryData(BusinessObject bo) {
		// TODO Auto-generated method stub
		dataInitialization();// ��ʼ������
		String monitortype = bo.get_Name();
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
		String time = MonitorLogTabView.getTwoHoursAgoTime();
		startTime = time.substring(time.indexOf("*") + 1);
		endTime = time.substring(0, time.indexOf("*"));
		parmsmap.put("startTime", startTime);
		parmsmap.put("endTime", endTime);
		alarmCondition = getAlarmCondition(bo.get_RecId(), monitortype);// ��ȡ��ֵ����
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
			// System.out.println("�����������־����: "+loginfo);
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
			max = ArrayTool.getIntArrayMax(intarray);// ���ֵ
			min = ArrayTool.getIntArrayMin(intarray);// ��Сֵ
			avg = ArrayTool.getIntArrayAvg(intarray);// ƽ��ֵ
		}
		Map<String, String> xyLineMap = getXYName(monitortype);
		xname = xyLineMap.get("XLineName");
		yname = xyLineMap.get("YLineName");
		if (newvalue.equals(""))
			newvalue = "0";
		descList = getReturnDescList(monitortype, li);// ��ȡ�������������ֵ�б�
	}

	/**
	 * ��ȡ�������������ֵ�б�
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
	 *            monitortype��������� List<String>
	 *            logTimeAndlogInfoList�������־ʱ��#���ݼ��� �����������־������
	 *            ������ֵ�����ֵ��ƽ��ֵ������ֵ����
	 */
	public static List<Map<String, List<String>>> analyticLogReturnMap(
			String monitortype, List<String> logTimeAndlogInfoList) {
		List<String> arrayList = new ArrayList<String>();// ���ÿ����־�Ĳ�ͬ����
		List<String> intArrayList = new ArrayList<String>();
		Map<String, List<String>> arrayListMap = new HashMap<String, List<String>>();// ���ÿһ�з���ֵ�Ͳ���
		for (String loginfoAndLogTimestr : logTimeAndlogInfoList) {
			String logtime = loginfoAndLogTimestr.substring(0,
					loginfoAndLogTimestr.indexOf("#"));// �������־ʱ��
			String loginfo = loginfoAndLogTimestr.substring(
					loginfoAndLogTimestr.indexOf("#") + 1,
					loginfoAndLogTimestr.length());// �������־����
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
					arrayList.add(downLoadTime);// ��������ʱ���������
					returnKeyString = "����ʱ��(s)";
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
						String createdTime = loginfo.substring(loginfo.indexOf(",")+2,loginfo.indexOf("minutes")-1);//�ļ�����ʱ��
						arrayList.add(createdTime);// �����ļ�����ʱ�伯��
						returnKeyString = "����ʱ��(s)";
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
			double arraymax = ArrayTool.getDoubleArrayMax(arrayintarray);// ���ֵ
			double arrayavg = ArrayTool.getDoubleArrayAvg(arrayintarray);// ƽ��ֵ
			intArrayList.add(monitortype);// ���������
			intArrayList.add(String.valueOf(arraymax));
			intArrayList.add(String.valueOf(arrayavg));
			intArrayList.add(String.valueOf(arrayintarray[0]));// ����ֵ
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
	 * ��ȡ����XY������
	 */
	public static Map<String, String> getXYName(String monitortype) {
		Map<String, String> xylinemap = new HashMap<String, String>();
		String xLineName = "";
		String yLineName = "";
		if (monitortype.equals("Ecc.Memory")) {
			xLineName = "�ڴ�ʹ����(%)���ֵ" + Double.parseDouble(String.valueOf(max))
					+ "  ��Сֵ" + Double.parseDouble(String.valueOf(min)) + "ƽ��ֵ"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "�ڴ�ʹ����(%)";
			reportDescName = "�ڴ�ʹ���ʰٷֱ�  ";
		} else if (monitortype.equals("Ecc.ping")) {
			xLineName = "���ɹ���(%)���ֵ" + Double.parseDouble(String.valueOf(max))
					+ "  ��Сֵ" + Double.parseDouble(String.valueOf(min)) + "ƽ��ֵ"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "���ɹ���(%)";
			reportDescName = "���ɹ��ʰٷֱ�  ";
		} else if (monitortype.equals("Ecc.DiskSpace")) {
			xLineName = "Diskʹ����(%)���ֵ"
					+ Double.parseDouble(String.valueOf(max)) + "  ��Сֵ"
					+ Double.parseDouble(String.valueOf(min)) + "ƽ��ֵ"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "Diskʹ����(%)";
			reportDescName = "Diskʹ���ʰٷֱ�  ";
		} else if (monitortype.equals("Ecc.URL")) {
			xLineName = "������  ���ֵ" + Double.parseDouble(String.valueOf(max))
					+ "  ��Сֵ" + Double.parseDouble(String.valueOf(min)) + "ƽ��ֵ"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "������";
			reportDescName = "������  ";
		} else if (monitortype.equals("Ecc.File")) {
			xLineName = "�ļ���С(B) ���ֵ" + Double.parseDouble(String.valueOf(max))
					+ "  ��Сֵ" + Double.parseDouble(String.valueOf(min)) + "ƽ��ֵ"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "�ļ���С(B) ";
			reportDescName = "�ļ���С(B)  ";
		}else if (monitortype.equals("Ecc.Service")) {
			xLineName = "����ʵ������(��) ���ֵ" + Double.parseDouble(String.valueOf(max))
					+ "  ��Сֵ" + Double.parseDouble(String.valueOf(min)) + "ƽ��ֵ"
					+ Double.parseDouble(String.valueOf(avg));
			yLineName = "����ʵ������(��) ";
			reportDescName = "����ʵ������(��) ";
		}
		xylinemap.put("XLineName", xLineName);
		xylinemap.put("YLineName", yLineName);
		return xylinemap;
	}

	/**
	 * ��ȡ����������ֵ
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
				sf.append("[�ڴ�ʹ�� " + " " + operator + " " + alramValue + "]");
			} else if (monitorType.equals("Ecc.ping")) {
				sf.append("[���ɹ��� " + " " + operator + " " + alramValue + "]");
			} else if (monitorType.equals("Ecc.ping")) {
				sf.append("[Diskʹ���� " + " " + operator + " " + alramValue + "]");
			} else if (monitorType.equals("Ecc.URL")) {
				sf.append("[������ " + " " + operator + " " + alramValue + "]");
			} else if (monitorType.equals("Ecc.File")) {
				sf.append("[�ļ���С(B) " + " " + operator + " " + alramValue + "]");
			}
		}
		return sf.toString();
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
