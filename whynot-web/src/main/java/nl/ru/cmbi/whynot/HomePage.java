package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.comments.CommentsPanel;
import nl.ru.cmbi.whynot.databanks.DatabanksPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

public class HomePage extends WebPage {
	public HomePage(final PageParameters parameters) {
		add(new CommentsPanel("comments"));
		add(new DatabanksPanel("databanks"));
	}
}
