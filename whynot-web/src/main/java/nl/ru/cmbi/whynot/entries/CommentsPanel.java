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

import nl.ru.cmbi.whynot.entries.CommentTreeProvider.CommentTreeNode;
import nl.ru.cmbi.whynot.model.Entry;

public class CommentsPanel extends Panel {
	public CommentsPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Filter list
		final Map<String, List<Entry>> map = new TreeMap<String, List<Entry>>();
		for (Entry ent : entrylist.getObject()) {
			
			if (ent.getComment()!=null) {
				
				String comment = ent.getComment();
				if (!map.containsKey(comment))
					map.put(comment, new ArrayList<Entry>());
				
				map.get(comment).add(ent);
			}
		}
				
		if (map.isEmpty())
			map.put("Comments", new ArrayList<Entry>());

		//Download link
		add(new ResourceLink<ByteArrayResource>("export-comments", new ByteArrayResource( "text/plain", null, source.replaceAll("[\\W]", "") + "_comments.txt" ) {

			@Override
			protected byte[] getData(Attributes attributes) {
			
				StringBuilder sb = new StringBuilder();
				for (String com : map.keySet()) {
				
					sb.append("COMMENT: ");
					sb.append(com);
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
		AbstractTree<CommentTreeNode> tree = new NestedTree<CommentTreeNode>("commenttree", new CommentTreeProvider(map.keySet()))
		{
            @Override
            protected Component newContentComponent(String id, IModel<CommentTreeNode> node) {
            	
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
