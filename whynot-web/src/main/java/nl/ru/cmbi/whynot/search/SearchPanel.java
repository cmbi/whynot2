package nl.ru.cmbi.whynot.search;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

public class SearchPanel extends Panel {
	public SearchPanel(final String id) {
		super(id);

		final TextField<String> tt = new TextField<String>("pdbid", new Model<String>());
		tt.setRequired(true);
		tt.add(new PatternValidator("[a-zA-Z0-9]{4}(\\s+[a-zA-Z0-9]{4})*"));
		Form<Void> form = new Form<Void>("form") {
			@Override
			protected void onSubmit() {
				PageParameters pp = new PageParameters();
				pp.put("pdbid", tt.getConvertedInput().toLowerCase().split("\\s+"));
				setResponsePage(ResultsPage.class, pp);
			}
		};
		form.add(tt);
		add(form);
	}
}
