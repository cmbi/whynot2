package jfreechart.simple;

import org.apache.wicket.markup.html.WebPage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class PieChartExamplePage extends WebPage {

	public PieChartExamplePage() {
		DefaultPieDataset d = new DefaultPieDataset();
		d.setValue("JavaWorld", new Integer(75));
		d.setValue("Other", new Integer(25));
		JFreeChart chart = ChartFactory.createPieChart("Sample Pie Chart", d, //
		true, // Show legend  
		true, // Show tooltips
		true); // Show urls
		//chart.setBackgroundPaint(Color.white);
		//chart.setBorderVisible(false);
		add(new JFreeChartImage("image", chart, 300, 300));
	}

}
