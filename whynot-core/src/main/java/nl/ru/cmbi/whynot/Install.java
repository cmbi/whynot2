package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.hibernate.SpringUtil;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CrawlType;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Install {
	public static void main(String[] args) throws Exception {
		AnnotationSessionFactoryBean asfb = (AnnotationSessionFactoryBean) SpringUtil.getContext().getBean("&sessionFactory");
		asfb.dropDatabaseSchema();
		asfb.createDatabaseSchema();

		Install installer = (Install) SpringUtil.getContext().getBean("install");
		installer.setUserRights();
		installer.storeDatabanks();
	}

	@Autowired
	private SessionFactory	sf;

	public void setUserRights() {
		Session s = sf.getCurrentSession();
		s.createSQLQuery("grant usage on schema public to whynotuser;").executeUpdate();
		s.createSQLQuery("grant select on table annotation, comment, databank, entry, file to whynotuser;").executeUpdate();
		Logger.getLogger(getClass()).info("User rights set.");
	}

	@Autowired
	private DatabankDAO	dbdao;

	public void storeDatabanks() {
		Databank pdb, dssp, nmr, nrg, nrg_docr, sf;
		dbdao.makePersistent(pdb = new Databank("PDB", CrawlType.FILE, ".*/pdb([\\w]{4})\\.ent(\\.gz)?", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=pdb&compression=NO&structureId=", "http://www.wwpdb.org/"));
		dbdao.makePersistent(new Databank("PDBFINDER", pdb, CrawlType.LINE, "ID           : ([\\w]{4})", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=pdbfinder2&id=", "http://swift.cmbi.ru.nl/gv/pdbfinder/"));
		dbdao.makePersistent(new Databank("PDBREPORT", pdb, CrawlType.FILE, ".*pdbreport.*/([\\w]{4})", "http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=", "http://swift.cmbi.ru.nl/gv/pdbreport/"));

		dbdao.makePersistent(dssp = new Databank("DSSP", pdb, CrawlType.FILE, ".*/([\\w]{4})\\.dssp", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=dssp&id=", "http://swift.cmbi.ru.nl/gv/dssp/"));
		dbdao.makePersistent(new Databank("HSSP", dssp, CrawlType.FILE, ".*/([\\w]{4})\\.hssp", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=hssp&id=", "http://swift.cmbi.ru.nl/gv/hssp/"));

		dbdao.makePersistent(nmr = new Databank("NMR", pdb, CrawlType.LINE, "([\\w]{4})", "link", "http://www.bmrb.wisc.edu/"));
		dbdao.makePersistent(new Databank("RECOORD", nmr, CrawlType.FILE, ".*/([\\w]{4})_cns_w\\.pdb", "http://www.cmbi.ru.nl/whynot/recoord_trampoline/", "http://www.ebi.ac.uk/msd-srv/docs/NMR/recoord/main.html"));
		dbdao.makePersistent(nrg = new Databank("NRG", nmr, CrawlType.LINE, "([\\w]{4})", "link", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet"));
		dbdao.makePersistent(nrg_docr = new Databank("NRG-DOCR", nrg, CrawlType.LINE, "([\\w]{4})", "link", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet"));
		dbdao.makePersistent(new Databank("NRG-CING", nrg_docr, CrawlType.LINE, "([\\w]{4})", "link", "http://nmr.cmbi.ru.nl/cing/"));

		dbdao.makePersistent(sf = new Databank("STRUCTUREFACTORS", pdb, CrawlType.FILE, ".*/r([\\w]{4})sf\\.ent", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=STRUCTFACT&compression=NO&structureId=", "http://www.pdb.org/"));
		dbdao.makePersistent(new Databank("PDB_REDO", sf, CrawlType.FILE, ".*/[\\w]{2}/([\\d][\\w]{3})", "http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=", "http://www.cmbi.ru.nl/pdb_redo/"));

		Logger.getLogger(getClass()).info("Databanks stored.");
	}
}
