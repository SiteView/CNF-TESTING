package SiteView.ecc.reportchart;

import java.util.List;

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

	public static JFreeChart createChart(XYDataset dataset,String xname,String yname) {
		JFreeChart chart = ChartFactory.createXYLineChart(getReporttitle(),
				xname, yname, dataset, PlotOrientation.VERTICAL, true, true,
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
	public static XYDataset createDataset(List<String> xydata) {
		XYSeries series = new XYSeries("数据趋势");
		for (String str : xydata) {
			String x = str.substring(0, str.indexOf("#"));
			String y = str.substring(str.indexOf("#")+1, str.length());
			series.add(Double.parseDouble(x), Double.parseDouble(y));
		}
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		return dataset;
	}
}
