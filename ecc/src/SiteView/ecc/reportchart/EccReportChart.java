package SiteView.ecc.reportchart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class EccReportChart {

	public static JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart("Ecc Report Demo",
				"X", "Y", dataset, PlotOrientation.VERTICAL, true, true, false);

		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis axis = (NumberAxis) plot.getRangeAxis();
		axis.setAutoRangeIncludesZero(false);
		axis.setAutoRangeMinimumSize(1.0);
		return chart;
	}

	public static XYDataset createDataset() {
		XYSeries series = new XYSeries("¼à²â Êý¾Ý");
		series.add(1.0, 100.0);
		series.add(5.0, 100.0);
		series.add(4.0, 100.0);
		series.add(12.5, 50.0);
		series.add(17.3, 100.0);
		series.add(21.2, 100.0);
		series.add(21.9, 100.0);
		series.add(25.6, 100.0);
		series.add(30.0, 100.0);
		XYSeriesCollection dataset = new XYSeriesCollection(series);
		return dataset;
	}
}
