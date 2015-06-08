package nl.ru.cmbi.whynot.comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import nl.ru.cmbi.whynot.entries.EntriesPage;
import nl.ru.cmbi.whynot.home.HomePage;
import nl.ru.cmbi.whynot.model.Entry;
import nl.ru.cmbi.whynot.mongo.EntryRepo;

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
	EntryRepo entrydao;

	public CommentPage() {
		ListView<String> commentlist = new ListView<String>("commentlist", entrydao.listComments()) {
			private SimpleDateFormat	sdf	= new SimpleDateFormat("dd/MM/yyyy HH:mm");

			@Override
			protected void populateItem(final ListItem<String> item) {
				long count = entrydao.countWithComment(item.getModelObject());
				
				item.add(new Label("text", item.getModelObject()).setEscapeModelStrings(false));
				Link<Void> lnk = new Link<Void>("entries") {
					@Override
					public void onClick() {
						setResponsePage(new EntriesPage(item.getModelObject(), 
								new LoadableDetachableModel<List<Entry>>() {
							@Override
							protected List<Entry> load() {
								return entrydao.findWithComment(item.getModelObject());
							}
						}));
					}
				};
				item.add(lnk.add(new Label("count", "" + count)));
				
				String dateString="";
				if(count>0) {
					long latest = entrydao.getLastAnnotation((String)item.getModelObject());
					dateString = sdf.format(new Date(latest));
				}
				item.add(new Label("latest", dateString ));
			}
		};
		add(commentlist);
	}
}
