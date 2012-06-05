package SiteView.ecc.reportchart;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class EccReportChart {
	public static String reporttitle = "";

	public static String getReporttitle() {
		return reporttitle;
	}

	public void setReporttitle(String reporttitle) {
		this.reporttitle = reporttitle;
	}

	public static JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(getReporttitle(),
				"时间点", "峰值", dataset, PlotOrientation.VERTICAL, true, true,
				false);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis axis = (NumberAxis) plot.getRangeAxis();
		axis.setAutoRangeIncludesZero(false);
		axis.setAutoRangeMinimumSize(1.0);
		return chart;
	}
	/**
	 * 构建曲线报表数据
	 * @param xydata
	 * @return
	 */
	public static XYDataset createDataset(Map<String, String> xydata) {
		XYSeries series = new XYSeries("数据趋势");
		 Set<Map.Entry<String, String>> set = xydata.entrySet();
		  for (Iterator<Map.Entry<String, String>> it = set.iterator(); it.hasNext();) {
	            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
//	            System.out.println(entry.getKey() + "--->" + entry.getValue());
	            double x=Double.parseDouble(entry.getKey());
				double y=Double.parseDouble(entry.getValue());
//				System.out.println(x+":"+y);
	            series.add(x, y);
	        }
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		return dataset;
	}
}
