package nl.ru.cmbi.whynot.entries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.Component;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import nl.ru.cmbi.whynot.comment.CommentTreePanel;
import nl.ru.cmbi.whynot.comment.CommentTreeProvider;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;

public class CommentsPanel extends Panel {
	public CommentsPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Filter list
		final Map<Comment, List<Entry>> map = new TreeMap<Comment, List<Entry>>();
		for (Entry ent : entrylist.getObject())
			if (!ent.getAnnotations().isEmpty())
				for (Annotation ann : ent.getAnnotations()) {
					if (!map.containsKey(ann.getComment()))
						map.put(ann.getComment(), new ArrayList<Entry>());
					map.get(ann.getComment()).add(ent);
				}
		if (map.isEmpty())
			map.put(new Comment("Comments"), new ArrayList<Entry>());

		//Download link
		add(new ResourceLink<ByteArrayResource>("export-comments", new ByteArrayResource( "text/plain", null, source.replaceAll("[\\W]", "") + "_comments.txt" ) {

			@Override
			protected byte[] getData(Attributes attributes) {
			
				StringBuilder sb = new StringBuilder();
				for (Comment com : map.keySet()) {
				
					sb.append("COMMENT: ");
					sb.append(com.getText());
					sb.append('\n');
					
					List<Entry> withAnnotation = map.get(com);
					for (Entry entry : withAnnotation) {
						sb.append(entry.toString());
						sb.append('\n');
					}
				}
				return sb.toString().getBytes();
			}
				
			@Override
			protected void configureResponse(ResourceResponse response, Attributes attributes) {
				super.configureResponse(response, attributes);
				
				response.disableCaching();
			}
		}));
		
		//Comments
		AbstractTree<String> tree = new NestedTree<String>("commenttree", new CommentTreeProvider(map))
		{
            @Override
            protected Component newContentComponent(String id, IModel<String> node) {
            	
                return new CommentTreePanel(id, node, map);
            }
		};
		
		
		
		tree.add(new Behavior()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onComponentTag(Component component, ComponentTag tag)
            {
                theme.onComponentTag(component, tag);
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response)
            {
                theme.renderHead(component, response);
            }
        });
		
		add(tree);
	}
	
	private Behavior theme = new HumanTheme();
}
