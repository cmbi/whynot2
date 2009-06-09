package nl.ru.cmbi.whynot.search;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;

public class SearchPanel extends Panel {
	public SearchPanel(String id) {
		super(id);

		Form<Void> form = new Form<Void>("form");
		form.add(new TextField<String>("pdbid"));
		add(form);
	}
}
