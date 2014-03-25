package nl.ru.cmbi.whynot.entries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.io.*;

import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.Component;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

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
		add(new Link("export-comments") {

			@Override
			public void onClick() {
				
				AbstractResourceStreamWriter rstream = new AbstractResourceStreamWriter() {

		            @Override
		            public void write(OutputStream output) throws IOException {
			        
				        Writer writer = new OutputStreamWriter(output);
				        
						for (Comment com : map.keySet()) {
							
							writer.write("COMMENT: ");
							writer.write(com.getText());
							writer.write('\n');
							List<Entry> withAnnotation = map.get(com);
							for (Entry entry : withAnnotation) {
								writer.write(entry.toString());
								writer.write('\n');
							}
						}
		            }
				};
		        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(rstream, source.replaceAll("[\\W]", "") + "_comments.txt");        
		        getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
			}
		});

		//Comments
		add(new ListView<Comment>("commentlist", new ArrayList<Comment>(map.keySet())) {
			@Override
			protected void populateItem(ListItem<Comment> commentItem) {
				Comment com = commentItem.getModelObject();
				List<Entry> entries = map.get(com);
				commentItem.add(new Label("text", com.getText() + " (" + entries.size() + ")"));
				commentItem.add(new ListView<Entry>("entrylist", entries) {
					@Override
					protected void populateItem(ListItem<Entry> entryItem) {
						entryItem.add(new Label("entry", entryItem.getModelObject().toString()));
					}
				});
			}
		});
	}
}
