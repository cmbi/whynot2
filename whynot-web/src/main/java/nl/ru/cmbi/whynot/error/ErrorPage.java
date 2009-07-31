package nl.ru.cmbi.whynot.error;

import nl.ru.cmbi.whynot.feedback.FeedbackPanelWrapper;
import nl.ru.cmbi.whynot.search.SearchPanel;

import org.apache.wicket.markup.html.WebPage;

public class ErrorPage extends WebPage {
	public ErrorPage() {
		add(new FeedbackPanelWrapper("feedback"));
		add(new SearchPanel("search"));
	}
}
