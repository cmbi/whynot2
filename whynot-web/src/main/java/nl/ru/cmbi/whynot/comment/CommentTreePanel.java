package nl.ru.cmbi.whynot.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.panels.MoreLessPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class CommentTreePanel extends Panel {
	
	private List<Entry> shownEntries = new ArrayList<Entry>();
	private String toggleText = "";
	
    public CommentTreePanel(String id, IModel<String> node, Map<Comment, List<Entry>> map)
    {
        super(id, new CompoundPropertyModel<String>(node));

    	String comment = node.getObject();
		
		final List<Entry> matches = new ArrayList<Entry>();
		for(Comment c : map.keySet()) {
			
			String	fullText = c.getText(),
					prefix=comment + CommentTreeProvider.separator;
			
			if (fullText.startsWith(prefix) || fullText.equals(comment)) {
				
				matches.addAll(map.get(c));
			}
		}
		
		add( new Label("text", String.format("%s (%d)", comment, matches.size())));
		
		add( new MoreLessPanel( "entries", matches, 100 ));
    }
}
