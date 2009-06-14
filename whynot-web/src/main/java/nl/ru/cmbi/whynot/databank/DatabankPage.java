package nl.ru.cmbi.whynot.databank;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jfreechart.MappedChart;
import nl.ru.cmbi.whynot.entries.EntriesPage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;

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
				item.add(new Label("name", db.getName()));
				item.add(databankChart(db));
				item.add(new ExternalLink("reference", db.getReference()).add(new Label("href", db.getReference())));
			}

			private MappedChart databankChart(final Databank db) {
				//Create a DataSet
				DefaultPieDataset pieDataset = new DefaultPieDataset();
				long obs = entrydao.getObsoleteCount(db);
				long val = entrydao.getValidCount(db);
				long ann = entrydao.getAnnotatedCount(db);
				long una = entrydao.getUnannotatedCount(db);
				pieDataset.setValue("Obsolete " + obs, obs);
				pieDataset.setValue("Valid " + val, val);
				pieDataset.setValue("Annotated " + ann, ann);
				pieDataset.setValue("Unannotated " + una, una);

				//Create Chart
				JFreeChart chart = ChartFactory.createPieChart3D(null, pieDataset, true, true, false);
				chart.setBackgroundPaint(Color.WHITE);
				chart.getLegend().setPosition(RectangleEdge.RIGHT);
				chart.getLegend().setItemPaint(new Color(1, 165, 236));
				PiePlot3D plot = (PiePlot3D) chart.getPlot();
				plot.setBackgroundPaint(Color.WHITE);
				plot.setForegroundAlpha(0.6f);
				plot.setCircular(true);
				plot.setLabelGenerator(null);
				plot.setOutlineVisible(false);

				//Create Mapped Chart
				MappedChart mc = new MappedChart("chart", chart, 350, 230) {
					@Override
					protected void onClickCallback(AjaxRequestTarget target, ChartEntity entity) {
						//Determine selection
						for (final CollectionType test : CollectionType.values())
							if (entity.toString().toUpperCase().contains(test.toString()))
								setResponsePage(new EntriesPage(db.getName() + " " + test.toString().toLowerCase(), new LoadableDetachableModel<List<Entry>>() {
									@Override
									protected List<Entry> load() {
										switch (test) {
										case OBSOLETE:
											return entrydao.getObsolete(db);
										case VALID:
											return entrydao.getValid(db);
										case MISSING:
											return entrydao.getMissing(db);
										case ANNOTATED:
											return entrydao.getAnnotated(db);
										case UNANNOTATED:
											return entrydao.getUnannotated(db);
										default:
											return new ArrayList<Entry>();
										}
									};
								}));
					}
				};
				return mc;
			}
		};
		return chartlist;
	}
}
