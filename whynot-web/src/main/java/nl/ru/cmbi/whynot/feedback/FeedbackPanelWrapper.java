package nl.ru.cmbi.whynot.feedback;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class FeedbackPanelWrapper extends Panel {
	public FeedbackPanel	feedbackpanel	= new FeedbackPanel("feedbackpanel");

	public FeedbackPanelWrapper(String id) {
		super(id);
		add(feedbackpanel);
	}

}
