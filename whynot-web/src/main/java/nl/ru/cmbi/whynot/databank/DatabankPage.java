package nl.ru.cmbi.whynot.databank;

import java.awt.Color;

import jfreechart.MappedChart;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.data.general.DefaultPieDataset;

public class DatabankPage extends HomePage {
	@SpringBean
	protected EntryDAO	entrydao;

	public DatabankPage(PageParameters parameters) {
		Databank db = databankdao.findByName(parameters.getString("name"));

		// Create a simple pie chart 
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		pieDataset.setValue("Obsolete", entrydao.getObsolete(db).size());
		pieDataset.setValue("Valid", entrydao.getValid(db).size());
		pieDataset.setValue("Annotated", entrydao.getAnnotated(db).size());
		pieDataset.setValue("Unannotated", entrydao.getUnannotated(db).size());
		JFreeChart chart = ChartFactory.createPieChart(db.getName(), pieDataset, true, true, false);
		chart.setBackgroundPaint(Color.red);
		chart.setBorderVisible(false);

		add(new MappedChart("chart", chart, 330, 270) {
			@Override
			protected void onClickCallback(AjaxRequestTarget target, ChartEntity entity) {
				//TODO Handle click on pie and label
				System.out.println(entity + " | " + entity.getArea() + " | " + entity.getShapeType());
			}
		});
	}
}
