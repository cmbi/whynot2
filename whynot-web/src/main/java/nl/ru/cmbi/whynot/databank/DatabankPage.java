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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;

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
				item.add(new PieChartFragment("piechart", db));
				item.add(new ExternalLink("reference", db.getReference()).add(new Label("href", db.getReference())));
			}
		};
		return chartlist;
	}

	private class PieChartFragment extends Fragment {
		public PieChartFragment(String id, final Databank db) {
			super(id, "piechartfragment", DatabankPage.this);

			long pre = entrydao.getPresentCount(db);
			long val = entrydao.getValidCount(db);
			long obs = entrydao.getObsoleteCount(db);
			long mis = entrydao.getMissingCount(db);
			long ann = entrydao.getAnnotatedCount(db);
			long una = entrydao.getUnannotatedCount(db);

			//Chart
			add(createPieChart("chart", db, obs, val, ann, una));

			//Legend
			add(new Link<Void>("present") {
				@Override
				public void onClick() {
					setResponsePage(new EntriesPage(db.getName() + " present", new LoadableDetachableModel<List<Entry>>() {
						@Override
						protected List<Entry> load() {
							return entrydao.getPresent(db);
						}
					}));
				}
			}.add(new Label("count", "" + pre)));
			add(new Link<Void>("valid") {
				@Override
				public void onClick() {
					setResponsePage(new EntriesPage(db.getName() + " valid", new LoadableDetachableModel<List<Entry>>() {
						@Override
						protected List<Entry> load() {
							return entrydao.getValid(db);
						}
					}));
				}
			}.add(new Label("count", "" + val)));
			add(new Link<Void>("obsolete") {
				@Override
				public void onClick() {
					setResponsePage(new EntriesPage(db.getName() + " obsolete", new LoadableDetachableModel<List<Entry>>() {
						@Override
						protected List<Entry> load() {
							return entrydao.getObsolete(db);
						}
					}));
				}
			}.add(new Label("count", "" + obs)));
			add(new Link<Void>("missing") {
				@Override
				public void onClick() {
					setResponsePage(new EntriesPage(db.getName() + " missing", new LoadableDetachableModel<List<Entry>>() {
						@Override
						protected List<Entry> load() {
							return entrydao.getMissing(db);
						}
					}));
				}
			}.add(new Label("count", "" + mis)));
			add(new Link<Void>("annotated") {
				@Override
				public void onClick() {
					setResponsePage(new EntriesPage(db.getName() + " annotated", new LoadableDetachableModel<List<Entry>>() {
						@Override
						protected List<Entry> load() {
							return entrydao.getAnnotated(db);
						}
					}));
				}
			}.add(new Label("count", "" + ann)));
			add(new Link<Void>("unannotated") {
				@Override
				public void onClick() {
					setResponsePage(new EntriesPage(db.getName() + " unannotated", new LoadableDetachableModel<List<Entry>>() {
						@Override
						protected List<Entry> load() {
							return entrydao.getUnannotated(db);
						}
					}));
				}
			}.add(new Label("count", "" + una)));
		}

		private MappedChart createPieChart(String id, final Databank db, long obs, long val, long ann, long una) {
			//Create a DataSet
			DefaultPieDataset pieDataset = new DefaultPieDataset();
			pieDataset.setValue("Obsolete", obs);
			pieDataset.setValue("Valid", val);
			pieDataset.setValue("Annotated", ann);
			pieDataset.setValue("Unannotated", una);

			//Create Chart
			JFreeChart chart = ChartFactory.createPieChart3D(null, pieDataset, false, true, false);
			chart.setBackgroundPaint(Color.WHITE);
			chart.setPadding(RectangleInsets.ZERO_INSETS);
			PiePlot3D plot = (PiePlot3D) chart.getPlot();
			plot.setCircular(true);
			plot.setForegroundAlpha(0.6f);
			plot.setBackgroundPaint(Color.WHITE);
			plot.setSectionPaint("Obsolete", new Color(184, 0, 0));
			plot.setSectionPaint("Valid", new Color(0, 112, 184));//#0070B8
			plot.setSectionPaint("Annotated", new Color(0, 184, 112));
			plot.setSectionPaint("Unannotated", Color.YELLOW);
			plot.setLabelGenerator(null);
			plot.setOutlineVisible(false);

			//Create Mapped Chart
			MappedChart mc = new MappedChart(id, chart, 150, 150) {
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

	}
}
