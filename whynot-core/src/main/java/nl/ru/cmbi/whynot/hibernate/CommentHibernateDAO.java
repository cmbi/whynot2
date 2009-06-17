package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.model.Comment;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class CommentHibernateDAO extends GenericHibernateDAO<Comment, Long> implements CommentDAO {
	@SuppressWarnings("unchecked")
	@Override
	public List<Comment> getAll() {
		return createCriteria().addOrder(Order.asc("text")).list();
	}

	public Comment findByText(String text) {
		return (Comment) createCriteria(Restrictions.naturalId().set("text", text)).uniqueResult();
	}
}
