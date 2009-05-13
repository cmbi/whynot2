package nl.ru.cmbi.why_not;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.why_not.jfreechart.complex.MappedChart;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.data.general.DefaultPieDataset;

/**
 * Homepage
 */
public class HomePage extends WebPage {

	private static final long	serialVersionUID	= 1L;

	// TODO Add any page properties or variables here

	@SpringBean
	private EntryDAO			entrydao;

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	@SuppressWarnings("serial")
	public HomePage(final PageParameters parameters) {

		// Add the simplest type of label
		add(new Label("message", "If you see this message wicket is properly configured and running"));
		add(new Label("count", new Model<String>() {
			@Override
			public String getObject() {
				return "" + entrydao.countAll();
			}
		}));

		// Create a simple pie chart
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		pieDataset.setValue("A", new Integer(75));
		pieDataset.setValue("B", new Integer(10));
		pieDataset.setValue("C", new Integer(10));
		pieDataset.setValue("D", new Integer(5));
		JFreeChart chart = ChartFactory.createPieChart("CSC408 Mark Distribution", pieDataset, true, true, false);

		add(new MappedChart("panel", chart, 500, 300) {
			@Override
			protected void onClickCallback(AjaxRequestTarget target, ChartEntity entity) {
				// TODO Auto-generated method stub
				System.out.println(entity);
				System.out.println(entity.getURLText());
			}
		});

		// TODO Add your page's components here
	}
}
