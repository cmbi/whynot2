package nl.ru.cmbi.whynot.hibernate;

import java.util.List;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.model.Comment;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

@Service
public class CommentHibernateDAO extends GenericHibernateDAO<Comment, Long> implements CommentDAO {
	@SuppressWarnings("unchecked")
	@Override
	public List<Comment> findAll() {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		return crit.addOrder(Order.asc("text")).list();
	}

	public Comment findByText(String text) {
		Criteria crit = getSession().createCriteria(getPersistentClass());
		crit.add(Restrictions.naturalId().set("text", text));
		return (Comment) crit.uniqueResult();
	}
}
