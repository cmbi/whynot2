package nl.ru.cmbi.whynot.databank;

import java.awt.Color;
import java.util.Arrays;

import jfreechart.MappedChart;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;

public class DatabankPage extends HomePage {
	@SpringBean
	protected EntryDAO	entrydao;

	public DatabankPage() {
		add(databankListView(databankdao.findAll().toArray(new Databank[0])));
	}

	public DatabankPage(PageParameters parameters) {
		Databank db = null;
		if (parameters.containsKey("name")) {
			String name = parameters.getString("name");
			db = databankdao.findByName(name);
		}

		if (db != null)
			add(databankListView(db));
		else {
			error("Could not find databank for parameter name.");
			add(databankListView(databankdao.findAll().toArray(new Databank[0])));
		}
	}

	public ListView<Databank> databankListView(Databank... databanks) {
		ListView<Databank> chartlist = new ListView<Databank>("chartlist", Arrays.asList(databanks)) {
			@Override
			protected void populateItem(ListItem<Databank> item) {
				Databank db = item.getModelObject();
				item.add(databankChart(db));
				item.add(new Label("reference", db.getReference()));
			}

			private MappedChart databankChart(final Databank db) {
				//Create a DataSet
				DefaultPieDataset pieDataset = new DefaultPieDataset();
				long obs = entrydao.getObsoleteCount(db);
				long val = entrydao.getValidCount(db);
				long ann = entrydao.getAnnotatedCount(db);
				long una = entrydao.getUnannotatedCount(db);
				pieDataset.setValue("Obsolete\n " + obs, obs);
				pieDataset.setValue("Valid\n " + val, val);
				pieDataset.setValue("Annotated\n " + ann, ann);
				pieDataset.setValue("Unannotated\n " + una, una);

				//Create Chart
				JFreeChart chart = ChartFactory.createPieChart3D(db.getName(), pieDataset, true, true, false);
				PiePlot3D plot = (PiePlot3D) chart.getPlot();
				plot.setBackgroundPaint(Color.WHITE);
				plot.setForegroundAlpha(0.6f);
				plot.setCircular(true);
				chart.setBackgroundPaint(Color.WHITE);

				//Create Mapped Chart
				MappedChart mc = new MappedChart("chart", chart, 350, 250) {
					@Override
					protected void onClickCallback(AjaxRequestTarget target, ChartEntity entity) {
						PageParameters params = new PageParameters();
						params.put("name", db.getName());
						for (String test : new String[] { "Obsolete", "Valid", "Annotated", "Unannotated" })
							if (entity.toString().contains(test)) {
								params.put("selection", test);
								setResponsePage(DatabankEntriesPage.class, params);
							}
					}
				};
				return mc;
			}
		};
		return chartlist;
	}
}
