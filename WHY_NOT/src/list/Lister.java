package list;

import java.util.SortedSet;

import model.Databank;
import model.File;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.DatabankDAO;
import dao.interfaces.DatabankDAO.AnnotationType;
import dao.interfaces.DatabankDAO.CollectionType;

public class Lister {
	public static void main(String[] args) throws Exception {
		Lister ls = new Lister();
		CollectionType ct = CollectionType.ALL;
		AnnotationType at = AnnotationType.ALL;
		switch (args.length) {
		case 3:
			for (AnnotationType ref : AnnotationType.values())
				if (args[2].equals(ref.name()))
					at = ref;
		case 2:
			for (CollectionType ref : CollectionType.values())
				if (args[1].equals(ref.name()))
					ct = ref;
		case 1:
			break;
		default:
			throw new IllegalArgumentException("Usage: list DATABASE [ALL | VALID | MISSING | OBSOLETE] [ALL | WITH | WITHOUT]");
		}
		ls.list(args[0], ct, at);
	}

	private static DAOFactory	factory;

	public Lister() {
		Lister.factory = DAOFactory.instance(DAOFactory.HIBERNATE);
	}

	private boolean list(String dbname, CollectionType ct, AnnotationType at) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		try {
			transact = Lister.factory.getSession().beginTransaction(); //Plain JDBC

			DatabankDAO dbdao = Lister.factory.getDatabankDAO();

			Databank db = dbdao.findById(dbname, false);

			if (db == null)
				new IllegalArgumentException("Database unknown");

			SortedSet<File> files = null;
			switch (ct) {
			case ALL:
				files = dbdao.getEntries(db, at);
				break;
			case VALID:
				files = dbdao.getValidEntries(db, at);
				break;
			case MISSING:
				files = dbdao.getMissingEntries(db, at);
				break;
			case OBSOLETE:
				files = dbdao.getObsoleteEntries(db, at);
				break;
			}
			if (files != null)
				for (File file : files)
					System.out.println(file.getPdbid());

			transact.commit(); //Plain JDBC
			succes = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			if (transact != null)
				transact.rollback();
			succes = false;
			throw e;
		}
		finally {
			//Close session if using anything other than current session
			if (succes)
				Logger.getLogger(Lister.class).debug(dbname + ": Succes");
			else
				Logger.getLogger(Lister.class).error(dbname + ": Failure");
		}
		return succes;
	}
}
