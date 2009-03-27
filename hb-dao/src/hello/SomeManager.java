package hello;

import model.Author;

import org.hibernate.Session;

public class SomeManager {

	public static void main(String[] args) {
		SomeManager mgr = new SomeManager();

		mgr.createAndStoreAuthor("Pietje");

		HibernateUtil.getSession().close();
	}

	private String createAndStoreAuthor(String name) {

		Session session = HibernateUtil.getSession().getCurrentSession();
		session.beginTransaction();

		Author author = new Author();
		author.setName(name);

		session.save(author);

		session.getTransaction().commit();

		return author.getName();
	}
}
