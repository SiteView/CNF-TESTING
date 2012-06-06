package SiteView.ecc.tab.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import SiteView.ecc.tools.ArrayTool;
import SiteView.ecc.views.EccReportView;
import Siteview.Api.BusinessObject;
import siteview.windows.forms.LayoutViewBase;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

/**
 * <p>
 * Monitor Summary Info View.
 * </p>
 * 
 * @author zhongping.wang
 * 
 */
public class SummaryTabView extends LayoutViewBase {
	public static List<String> xydata = new ArrayList<String>();// ���������X��Y��������
	public static List<String> descList = new ArrayList<String>();// ������Ϣ

	public static List<String> getDescList() {
		return descList;
	}

	public static void setDescList(List<String> descList) {
		SummaryTabView.descList = descList;
	}

	public static String xname, yname = "";// X��Y��������

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
	 * ��ȡ�ͽ����������Ҫ����
	 * 
	 * @param bo
	 */
	public static void setSummaryData(BusinessObject bo) {
		// TODO Auto-generated method stub
		String monitortype = bo.get_Name();
		// System.out.println("monitortype:"+monitortype);
		Map<String, Object> parmsmap = new HashMap<String, Object>();
		parmsmap.put("monitorId", bo.get_RecId());
		String time = MonitorLogTabView.getTwoHoursAgoTime();
		parmsmap.put("startTime", time.substring(time.indexOf("*") + 1));
		parmsmap.put("endTime", time.substring(0, time.indexOf("*")));
		// parmsmap.put("startTime", "2012-06-04 18:17:26");
		// parmsmap.put("endTime", "2012-06-05 15:32:13");
		ICollection iCollenction = MonitorLogTabView.getLog(parmsmap);
		IEnumerator monitorlog = iCollenction.GetEnumerator();
		BusinessObject monitorlogbo = null;
		String newvalue = "";// ����ֵ
		while (monitorlog.MoveNext()) {
			monitorlogbo = (BusinessObject) monitorlog.get_Current();
			String loginfo = monitorlogbo.GetField("MonitorMassage")
					.get_NativeValue().toString();
			// System.out.println("�����������־����: "+loginfo);
			if (monitortype.equals("Ecc.Memory")) {
				if (!loginfo.startsWith("no data")) {
					if (newvalue.equals("")) {
						newvalue = loginfo.substring(0, loginfo.indexOf("%"));
						xydata.add(newvalue + "#" + "100");
					} else {
						String used = loginfo
								.substring(0, loginfo.indexOf("%"));
						xydata.add(used + "#" + "100");
					}
				}
			}
		}
		int[] intarray = new int[xydata.size()];
		int i = 0;
		for (String str : xydata) {
			String x = str.substring(0, str.indexOf("#"));
			intarray[i++] = Integer.parseInt(x);
		}
		int max = ArrayTool.getMax(intarray);// ���ֵ
		int min = ArrayTool.getMin(intarray);// ��Сֵ
		int avg = ArrayTool.getAvg(intarray);// ƽ��ֵ
		xname = "�����ڴ�ʹ����(%)���ֵ" + Double.parseDouble(String.valueOf(max))
				+ "  ��Сֵ" + Double.parseDouble(String.valueOf(min)) + "ƽ��ֵ"
				+ Double.parseDouble(String.valueOf(avg));
		yname = "�����ڴ�ʹ����(%)";
		descList.clear();
		descList.add(yname + "&"
				+ String.valueOf(Double.parseDouble(String.valueOf(max))) + "&"
				+ String.valueOf(Double.parseDouble(String.valueOf(avg))) + "&"
				+ Double.parseDouble(newvalue));
		setXydata(xydata);
		setXname(xname);
		setYname(yname);
		setDescList(descList);
	}

}
