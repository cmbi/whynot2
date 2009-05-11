package list;

import hibernate.DAOFactory;
import hibernate.GenericDAO.DatabankDAO;

import java.util.SortedSet;

import model.Databank;
import model.Entry;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class Lister {
	public static void main(String[] args) throws Exception {
		String dbname = "DATABASE";
		String fileFilter = "withFile|withoutFile";
		String parentFilter = "withParentFile|withoutParentFile";
		String commentFilter = "withComment|withoutComment|withOlderComment";
		String comment = "[\"Example comment\"]";

		if ((args.length == 4 || args.length == 5) && args[1].matches(fileFilter) && args[2].matches(parentFilter) && args[3].matches(commentFilter)) {
			dbname = args[0];
			fileFilter = args[1];
			parentFilter = args[2];
			commentFilter = args[3];
			if (args.length < 5)
				comment = "%"; //Wildcard
			else
				comment = args[4];

			new Lister().list(dbname, fileFilter, parentFilter, commentFilter, comment);
		}
		else {
			System.err.println("Usage: list DATABASE " + fileFilter + " " + parentFilter + " " + commentFilter + " " + comment);
			System.exit(1);
		}
	}

	private static DAOFactory	factory;

	public Lister() {
		Lister.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	private void list(String dbname, String fileFilter, String parentFilter, String commentFilter, String comment) throws Exception {
		Transaction transact = null;
		try {
			transact = Lister.factory.getSession().beginTransaction(); //Plain JDBC

			DatabankDAO dbdao = Lister.factory.getDatabankDAO();
			Databank db = dbdao.findByNaturalId(Restrictions.naturalId().set("name", dbname));
			if (db == null)
				new IllegalArgumentException("Databank with name " + dbname + " not found.");

			factory.getSession().enableFilter(fileFilter);
			factory.getSession().enableFilter(parentFilter);
			factory.getSession().enableFilter(commentFilter).setParameter("comment", comment);

			SortedSet<Entry> entries = db.getEntries();
			System.out.println("#" + dbname + " " + fileFilter + " " + parentFilter + " " + commentFilter + ": " + entries.size() + " entries");
			for (Entry entry : entries)
				System.out.println(entry + "," + (entry.getFile() != null ? entry.getFile().getTimestamp() : -1));

			transact.commit(); //Plain JDBC
			Logger.getLogger(Lister.class).debug("list DATABASE " + fileFilter + " " + parentFilter + " " + commentFilter + " " + comment + ": Succes");
		}
		catch (Exception e) {
			if (transact != null)
				transact.rollback();
			Logger.getLogger(Lister.class).error("list DATABASE " + fileFilter + " " + parentFilter + " " + commentFilter + " " + comment + ": Failure");
			throw e;
		}
	}
}
