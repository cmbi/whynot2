package nl.ru.cmbi.whynot.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Entry;

public class AnnotationPanel extends Panel {
	public AnnotationPanel(String id, Entry entry) {
		super(id);
		RepeatingView rv = new RepeatingView("annotation");
		for (Annotation ann : entry.getAnnotations())
			rv.add(new Label(rv.newChildId(), ann.getComment().getText()));
		add(rv);
	}
}
