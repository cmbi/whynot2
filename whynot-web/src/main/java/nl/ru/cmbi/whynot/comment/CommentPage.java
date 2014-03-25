package nl.ru.cmbi.whynot.comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import nl.ru.cmbi.whynot.entries.EntriesPage;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.AnnotationDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Comment;
import nl.ru.cmbi.whynot.model.Entry;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("comments")
public class CommentPage extends HomePage {
	@SpringBean
	AnnotationDAO		annotationdao;
	@SpringBean
	private CommentDAO	commentdao;

	public CommentPage() {
		ListView<Comment> commentlist = new ListView<Comment>("commentlist", commentdao.getAll()) {
			private SimpleDateFormat	sdf	= new SimpleDateFormat("dd/MM/yyyy HH:mm");

			@Override
			protected void populateItem(final ListItem<Comment> item) {
				final Comment com = item.getModelObject();
				long count = annotationdao.countWith(com);
				long latest = annotationdao.getLastUsed(com);
				item.add(new Label("text", com.getText()).setEscapeModelStrings(false));
				Link<Void> lnk = new Link<Void>("entries") {
					@Override
					public void onClick() {
						setResponsePage(new EntriesPage(com.getText(), new LoadableDetachableModel<List<Entry>>() {
							@Override
							protected List<Entry> load() {
								return annotationdao.getEntriesForComment(com.getId());
							}
						}));
					}
				};
				item.add(lnk.add(new Label("count", "" + count)));
				item.add(new Label("latest", sdf.format(new Date(latest))));
			}
		};
		add(commentlist);
	}
}
