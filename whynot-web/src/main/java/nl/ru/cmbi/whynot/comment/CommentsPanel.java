package nl.ru.cmbi.whynot.comment;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;
import nl.ru.cmbi.whynot.model.Comment;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class CommentsPanel extends Panel {
	@SpringBean
	private CommentDAO	commentdao;

	@SpringBean
	private EntryDAO	entrydao;

	public CommentsPanel(String id) {
		super(id);

		PageableListView<Comment> list = new PageableListView<Comment>("list", commentdao.findAll(), 10) {
			@Override
			protected void populateItem(ListItem<Comment> item) {
				item.add(new Label("text", item.getModelObject().getText()));

				entrydao.enableFilter("withComment", "comment", item.getModelObject().getText());
				item.add(new Label("count", "" + entrydao.countAll()));
			}
		};
		add(list);
		add(new PagingNavigator("navigator", list));
	}
}
