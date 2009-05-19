package nl.ru.cmbi.whynot.databanks;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class DatabanksPanel extends Panel {
	@SpringBean
	private DatabankDAO	databankdao;

	public DatabanksPanel(String id) {
		super(id);

		ListView<Databank> list = new ListView<Databank>("list", databankdao.findAll()) {
			@Override
			protected void populateItem(ListItem<Databank> item) {
				item.add(new Label("text", item.getModelObject().getName()));
			}
		};
		add(list);
	}
}
