package nl.ru.cmbi.why_not.comments;

import nl.ru.cmbi.why_not.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.why_not.model.Comment;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CommentsPanel extends Panel {
	@SpringBean
	private CommentDAO	commentdao;

	public CommentsPanel(String id) {
		super(id);

		ListView<Comment> list = new ListView<Comment>("list", commentdao.findAll()) {
			@Override
			protected void populateItem(ListItem<Comment> item) {
				item.add(new Label("text", item.getModelObject().getText()));
			}
		};
		add(list);
	}
}
