package nl.ru.cmbi.whynot.entries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.ru.cmbi.whynot.entries.CommentTreeProvider.CommentTreeNode;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.panels.MoreLessPanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class CommentTreePanel extends Panel {
	
	private List<Entry> shownEntries = new ArrayList<Entry>();
	private String toggleText = "";
	
    public CommentTreePanel(String id, IModel<CommentTreeNode> model, Map<String, List<Entry>> map)
    {
        super(id, new CompoundPropertyModel<CommentTreeNode>(model));

        CommentTreeNode node = model.getObject();
    	String commentText = node.getTitle();
		
		final List<Entry> entries = new ArrayList<Entry>();
		for(String c : node.getMembers()) {
			
			entries.addAll(map.get(c));
		}
		
		if(node.getChildren().size()<=0) {
			
			commentText = node.getMembers().get(0);
		}
		
		add( new Label("text", String.format("%s (%d)", commentText, entries.size() )).setEscapeModelStrings(false) );
		
		add( new MoreLessPanel( "entries", entries, 100 ));
    }
}
