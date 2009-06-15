package nl.ru.cmbi.whynot.comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.ru.cmbi.whynot.entries.EntriesPage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Annotation;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class CommentPage extends HomePage {
	@SpringBean
	private AnnotationDAO	annotationdao;
	@SpringBean
	private CommentDAO		commentdao;

	public CommentPage() {
		ListView<Comment> commentlist = new ListView<Comment>("commentlist", commentdao.findAll()) {
			private SimpleDateFormat	sdf	= new SimpleDateFormat("dd/MM/yyyy hh:mm");

			@Override
			protected void populateItem(ListItem<Comment> item) {
				final Comment com = item.getModelObject();
				long count = annotationdao.countAllWith(com);
				long latest = annotationdao.getLastUsed(com);
				ResourceLink<WebResource> rl = new ResourceLink<WebResource>("export", getEntriesResource(com));
				item.add(rl.add(new Label("text", com.getText())));
				item.add(new Label("latest", sdf.format(new Date(latest))));
				Link<Void> lnk = new Link<Void>("entries") {
					@Override
					public void onClick() {
						setResponsePage(new EntriesPage(com.getText(), new LoadableDetachableModel<List<Entry>>() {
							@Override
							protected List<Entry> load() {
								commentdao.makePersistent(com);
								List<Entry> entries = new ArrayList<Entry>();
								for (Annotation ann : com.getAnnotations())
									entries.add(ann.getEntry());
								return entries;
							}
						}));
					}
				};
				item.add(lnk.add(new Label("count", "" + count)));
			}
		};
		add(commentlist);
	}

	public Resource getEntriesResource(final Comment com) {
		WebResource export = new WebResource() {
			@Override
			public IResourceStream getResourceStream() {
				commentdao.makePersistent(com);
				StringBuilder sb = new StringBuilder();
				sb.append("COMMENT: " + com.getText() + '\n');
				for (Annotation ann : com.getAnnotations()) {
					Entry entry = ann.getEntry();
					sb.append(entry.getDatabank().getName());
					sb.append(',');
					sb.append(entry.getPdbid());
					sb.append('\n');
				}
				return new StringResourceStream(sb, "text/plain");
			}

			@Override
			protected void setHeaders(WebResponse response) {
				super.setHeaders(response);
				response.setAttachmentHeader(com.getText().replaceAll("[\\W]", "") + "_comment.txt");
			}
		};
		return export.setCacheable(false);
	}
}
