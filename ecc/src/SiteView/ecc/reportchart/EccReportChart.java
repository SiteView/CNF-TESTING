package SiteView.ecc.reportchart;

import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import SiteView.ecc.tab.views.SummaryTabView;

public class EccReportChart {
	public static String reporttitle = "";

	public static String getReporttitle() {
		return reporttitle;
	}

	public void setReporttitle(String reporttitle) {
		this.reporttitle = reporttitle;
	}

	public static JFreeChart createChart(XYDataset dataset, String xname,
			String yname) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				SummaryTabView.monitorName, xname, yname, dataset, true, true,
				false);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(new Color(255, 255, 255));// ���ñ�����ɫ
		plot.setDomainGridlinePaint(new Color(46, 139, 87));// ��������������ɫ
		plot.setRangeGridlinePaint(new Color(46, 139, 87));// �������������ɫ
		NumberAxis axis = (NumberAxis) plot.getRangeAxis();
		axis.setAxisLinePaint(new Color(50, 205, 50));// ����������ɫ
		axis.setTickLabelPaint(new Color(50, 205, 50));
		axis.setAutoRangeIncludesZero(false);
		axis.setAutoRangeMinimumSize(1.0);
		return chart;
	}

	/**
	 * �������߱�������
	 * 
	 * @param xydata
	 * @return
	 */
	public static XYDataset createDataset(List<String> xydata) {
		TimeSeries series = new TimeSeries(SummaryTabView.reportDescName,
				Second.class);
		for (String str : xydata) {
			String x = str.substring(0, str.indexOf("#"));
			String y = str.substring(str.indexOf("#") + 1, str.length());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
					Locale.US);
			try {
				Date d = sdf.parse(y);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyyMMddHHmmss");
				String date = simpleDateFormat.format(d);
				String ye = date.substring(0, 4);
				String mo = date.substring(4, 6);
				String da = date.substring(6, 8);
				String ho = date.substring(8, 10);
				String mi = date.substring(10, 12);
				String ss = date.substring(12, 14);
				Minute m = new Minute(Integer.parseInt(mi),
						Integer.parseInt(ho), Integer.parseInt(da),
						Integer.parseInt(mo), Integer.parseInt(ye));
				series.add(new Second(Integer.parseInt(ss), m),
						Double.parseDouble(x));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TimeSeriesCollection dataset = new TimeSeriesCollection(series);
		return dataset;
	}
}
