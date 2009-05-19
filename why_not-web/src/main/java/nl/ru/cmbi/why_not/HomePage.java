package nl.ru.cmbi.why_not;

import nl.ru.cmbi.why_not.comments.CommentsPanel;
import nl.ru.cmbi.why_not.databanks.DatabanksPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

public class HomePage extends WebPage {
	public HomePage(final PageParameters parameters) {
		add(new CommentsPanel("comments"));
		add(new DatabanksPanel("databanks"));
	}
}
