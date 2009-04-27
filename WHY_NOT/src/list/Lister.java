package list;

import model.Databank;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import dao.hibernate.DAOFactory;
import dao.interfaces.DatabankDAO;

public class Lister {
	public static void main(String[] args) throws Exception {
		if (args.length == 2)
			new Lister().list(args[0], args[1], null);
		else
			if (args.length == 3)
				if (args[2].equals("WITH"))
					new Lister().list(args[0], args[1], true);
				else
					if (args[2].equals("WITHOUT"))
						new Lister().list(args[0], args[1], false);
					else
						throw new IllegalArgumentException("Usage: list DATABASE [VALID | MISSING | OBSOLETE] [WITH | WITHOUT]");
		new Lister();
	}

	private static DAOFactory	factory;

	public Lister() {
		Lister.factory = DAOFactory.instance(DAOFactory.HIBERNATE);

	}

	private boolean list(String dbname, String collection, Boolean comment) throws Exception {
		boolean succes = false;
		Transaction transact = null;
		try {
			transact = Lister.factory.getSession().beginTransaction(); //Plain JDBC

			DatabankDAO dbdao = Lister.factory.getDatabankDAO();

			Databank db = dbdao.findById(dbname, false);

			//TODO: dbdao.

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
				Logger.getLogger(Lister.class).info(dbname + ": Succes");
			else
				Logger.getLogger(Lister.class).error(dbname + ": Failure");
		}
		return succes;
	}
}
