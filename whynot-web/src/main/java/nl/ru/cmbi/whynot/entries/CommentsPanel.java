package nl.ru.cmbi.whynot.entries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class CommentsPanel extends Panel {
	public CommentsPanel(String id, final String source, final IModel<List<Entry>> entrylist) {
		super(id, entrylist);

		//Filter list
		final Map<Comment, List<Entry>> map = new HashMap<Comment, List<Entry>>();
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
		add(new ResourceLink<WebResource>("export", new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				StringBuilder sb = new StringBuilder();
				for (Comment com : map.keySet()) {
					sb.append("COMMENT: ");
					sb.append(com.getText());
					sb.append('\n');
					List<Entry> withAnnotation = map.get(com);
					for (Entry entry : withAnnotation) {
						sb.append(entry.getDatabank().getName());
						sb.append(',');
						sb.append(entry.getPdbid());
						sb.append('\n');
					}
				}
				return new StringResourceStream(sb, "text/plain");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader(source.replaceAll("[\\W]", "") + "_comments.txt");
			}
		}.setCacheable(false)));

		//Comments
		add(new ListView<Comment>("commentlist", new ArrayList<Comment>(map.keySet())) {
			@Override
			protected void populateItem(ListItem<Comment> item) {
				Comment com = item.getModelObject();
				List<Entry> entries = map.get(com);
				item.add(new Label("text", com.getText() + " (" + entries.size() + ")"));
				item.add(new ListView<Entry>("entrylist", entries) {
					@Override
					protected void populateItem(ListItem<Entry> item) {
						Entry entry = item.getModelObject();
						item.add(new Label("databank", entry.getDatabank().getName()));
						item.add(new Label("pdbid", entry.getPdbid()));
					}
				});
			}
		});
	}
}
