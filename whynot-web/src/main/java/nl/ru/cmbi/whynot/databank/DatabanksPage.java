package nl.ru.cmbi.whynot.databank;

import java.awt.Color;

import jfreechart.complex.MappedChart;
import nl.ru.cmbi.whynot.HomePage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.data.general.DefaultPieDataset;

public class DatabanksPage extends HomePage {
	@SpringBean
	protected EntryDAO	entrydao;

	public DatabanksPage() {
		ChildFragment childfragment = new ChildFragment("hierarchy", databankdao.findByName("PDB"));
		add(childfragment);

		ListView<Databank> chartlist = new ListView<Databank>("chartlist", databankdao.findAll()) {
			private static final long	serialVersionUID	= -5581168078571199303L;

			@Override
			protected void populateItem(ListItem<Databank> item) {
				Databank db = item.getModelObject();
				// Create a simple pie chart 
				DefaultPieDataset pieDataset = new DefaultPieDataset();
				pieDataset.setValue("Obsolete", entrydao.getObsolete(db).size());
				pieDataset.setValue("Valid", entrydao.getValid(db).size());
				pieDataset.setValue("Annotated", entrydao.getAnnotated(db).size());
				pieDataset.setValue("Unannotated", entrydao.getUnannotated(db).size());
				JFreeChart chart = ChartFactory.createPieChart(db.getName(), pieDataset, true, true, false);
				chart.setBackgroundPaint(Color.white);
				chart.setBorderVisible(false);

				item.add(new MappedChart("chart", chart, 330, 270) {
					private static final long	serialVersionUID	= -3158613184296067291L;

					@Override
					protected void onClickCallback(AjaxRequestTarget target, ChartEntity entity) {
						//TODO Handle click on pie and label
						System.out.println(entity + " | " + entity.getArea() + " | " + entity.getShapeType());
					}
				});
			}
		};
		add(chartlist);
	}

	public class ChildFragment extends Fragment {
		public ChildFragment(String id, final Databank db) {
			super(id, "child", DatabanksPage.this, new Model<Databank>(db));
			add(new Label("name", db.getName()));
			RepeatingView children = new RepeatingView("children");
			add(children);
			for (Databank child : databankdao.getChildren(db))
				children.add(new ChildFragment(children.newChildId(), child));
		}
	}
}
