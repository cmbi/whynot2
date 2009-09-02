package nl.ru.cmbi.whynot.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jfreechart.MappedChart;

import nl.ru.cmbi.whynot.databank.ListInitializer;
import nl.ru.cmbi.whynot.entries.EntriesPage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.model.Databank.CollectionType;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;

public class PieChartPanel extends Panel {
	public PieChartPanel(String id, final Databank databank) {
		super(id);
		add(new AjaxLazyLoadPanel("lazy") {
			@Override
			public Component getLazyLoadComponent(String markupId) {
				return new PieChartFragment(markupId, databank);
			}
		});
	}

	@SpringBean
	protected DatabankDAO	databankdao;
	@SpringBean
	protected EntryDAO		entrydao;

	private class PieChartFragment extends Fragment {
		public PieChartFragment(String id, Databank db) {
			super(id, "piechartfragment", PieChartPanel.this);
			final String dbname = db.getName();

			long val = entrydao.countValid(db);
			long obs = entrydao.countObsolete(db);
			long pre = entrydao.countPresent(db);

			long ann = entrydao.countAnnotated(db);
			long una = entrydao.counUnannotated(db);
			long mis = entrydao.countMissing(db);

			//Legend
			add(new LegendItemFragment("valid", dbname, CollectionType.VALID, val));
			add(new LegendItemFragment("obsolete", dbname, CollectionType.OBSOLETE, obs));
			add(new LegendItemFragment("present", dbname, CollectionType.PRESENT, pre));

			add(new LegendItemFragment("annotated", dbname, CollectionType.ANNOTATED, ann));
			add(new LegendItemFragment("unannotated", dbname, CollectionType.UNANNOTATED, una));
			add(new LegendItemFragment("missing", dbname, CollectionType.MISSING, mis));

			//Chart
			add(new MappedChart("chart", createPieChart(obs, val, ann, una), 250, 150) {
				@Override
				protected void onClickCallback(AjaxRequestTarget target, ChartEntity entity) {
					//Determine selection
					for (final CollectionType test : CollectionType.values())
						if (entity.toString().toUpperCase().contains(test.toString()))
							setResponsePage(new EntriesPage(dbname + " " + test.toString().toLowerCase(), getEntriesModel(dbname, test)));
				}
			});
		}

		private JFreeChart createPieChart(long obs, long val, long ann, long una) {
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
			plot.setCircular(false);
			plot.setForegroundAlpha(0.6f);
			plot.setBackgroundPaint(Color.WHITE);
			plot.setSectionPaint("Obsolete", new Color(184, 0, 0));
			plot.setSectionPaint("Valid", new Color(0, 112, 184));//#0070B8
			plot.setSectionPaint("Annotated", new Color(0, 184, 112));
			plot.setSectionPaint("Unannotated", Color.YELLOW);
			plot.setLabelGenerator(null);
			plot.setOutlineVisible(false);

			return chart;
		}
	}

	private class LegendItemFragment extends Fragment {
		public LegendItemFragment(String id, final String dbname, final CollectionType colType, long count) {
			super(id, "legenditemfragment", PieChartPanel.this);
			//Entries
			final String clname = colType.name().toLowerCase();
			Link<Void> lnk = new Link<Void>("entrylink") {
				@Override
				public void onClick() {
					setResponsePage(new EntriesPage(dbname + " " + clname, getEntriesModel(dbname, colType)));
				}
			};
			lnk.add(new Label("label", clname));
			lnk.add(new Label("count", "" + count));
			add(lnk);

			//Resource
			ResourceReference reference = new ResourceReference(ListInitializer.class, dbname + '_' + colType.name().toUpperCase());
			add(new ResourceLink<String>("resourcelink", reference));
		}
	}

	private LoadableDetachableModel<List<Entry>> getEntriesModel(final String databank, final CollectionType test) {
		return new LoadableDetachableModel<List<Entry>>() {
			@Override
			protected List<Entry> load() {
				Databank db = databankdao.findByName(databank);
				switch (test) {
				case PRESENT:
					return entrydao.getPresent(db);
				case VALID:
					return entrydao.getValid(db);
				case OBSOLETE:
					return entrydao.getObsolete(db);
				case MISSING:
					warn("Your request includes missing entries without annotations. \n"//
							+ "Because these are not stored by Why Not, parent entries are returned.");
					return entrydao.getMissing(db);
				case ANNOTATED:
					return entrydao.getAnnotated(db);
				case UNANNOTATED:
					warn("Your request includes missing entries without annotations. \n"//
							+ "Because these are not stored by Why Not, parent entries are returned.");
					return entrydao.getUnannotated(db);
				default:
					error("Invalid CollectionType specified, please notify an administrator.");
					return new ArrayList<Entry>();
				}
			}
		};
	}
}
