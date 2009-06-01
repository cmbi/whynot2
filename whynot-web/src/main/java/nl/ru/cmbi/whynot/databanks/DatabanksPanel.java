package nl.ru.cmbi.whynot.databanks;

import jfreechart.complex.MappedChart;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.data.general.DefaultPieDataset;

public class DatabanksPanel extends Panel {
	@SpringBean
	private DatabankDAO	databankdao;
	@SpringBean
	private EntryDAO	entrydao;

	public DatabanksPanel(String id) {
		super(id);

		ListView<Databank> list = new ListView<Databank>("list", databankdao.findAll()) {
			@Override
			protected void populateItem(ListItem<Databank> item) {
				Databank db = item.getModelObject();
				item.add(new Label("name", db.getName()));

				// Create a simple pie chart 
				DefaultPieDataset pieDataset = new DefaultPieDataset();
				pieDataset.setValue("Valid", entrydao.getValid(child));
				pieDataset.setValue("Obsolete", new Integer(10));
				pieDataset.setValue("Annotated", new Integer(10));
				pieDataset.setValue("Unannotated", 5);
				JFreeChart chart = ChartFactory.createPieChart(db.getName(), pieDataset, true, true, false);

				item.add(new MappedChart("graph", chart, 300, 300) {
					@Override
					protected void onClickCallback(AjaxRequestTarget target, ChartEntity entity) {
						// TODO Auto-generated method stub
						System.out.println(entity);
						System.out.println(entity.getURLText());
					}
				});
			}
		};
		add(list);
	}
}
