package nl.ru.cmbi.whynot.install;

import nl.ru.cmbi.whynot.hibernate.SpringUtil;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CrawlType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Installer {
	public static void main(String[] args) throws Exception {
		AnnotationSessionFactoryBean asfb = (AnnotationSessionFactoryBean) SpringUtil.getContext().getBean("&sessionFactory");
		asfb.dropDatabaseSchema();
		asfb.createDatabaseSchema();

		((Installer) SpringUtil.getContext().getBean("installer")).fill();
	}

	@Autowired
	private DatabankDAO	dbdao;

	public void fill() {
		Databank pdb, dssp, nmr, nrg, nrg_docr, sf;
		dbdao.makePersistent(pdb = new Databank("PDB", "H.M. Berman, K. Henrick, H. Nakamura\nAnnouncing the worldwide Protein Data Bank.\nNature Structural Biology 10 (12), p. 980 (2003)", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=pdb&compression=NO&structureId=", ".*/pdb([\\w]{4})\\.ent(\\.gz)?", CrawlType.FILE));
		dbdao.makePersistent(new Databank("PDBFINDER", "http://swift.cmbi.ru.nl/gv/pdbfinder/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=pdbfinder2&id=", pdb, "ID           : ([\\w]{4})", CrawlType.LINE));
		dbdao.makePersistent(new Databank("PDBREPORT", "http://swift.cmbi.ru.nl/gv/pdbreport/", "http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=", pdb, ".*pdbreport.*/([\\w]{4})", CrawlType.FILE));

		dbdao.makePersistent(dssp = new Databank("DSSP", "http://swift.cmbi.ru.nl/gv/dssp/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=dssp&id=", pdb, ".*/([\\w]{4})\\.dssp", CrawlType.FILE));
		dbdao.makePersistent(new Databank("HSSP", "http://swift.cmbi.ru.nl/gv/hssp/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=hssp&id=", dssp, ".*/([\\w]{4})\\.hssp", CrawlType.FILE));

		dbdao.makePersistent(nmr = new Databank("NMR", "http://www.bmrb.wisc.edu/", "link", pdb, "([\\w]{4})", CrawlType.LINE));
		dbdao.makePersistent(new Databank("RECOORD", "http://www.ebi.ac.uk/msd-srv/docs/NMR/recoord/main.html", "http://www.cmbi.ru.nl/whynot/recoord_trampoline/", nmr, ".*/([\\w]{4})_cns_w\\.pdb", CrawlType.FILE));
		dbdao.makePersistent(nrg = new Databank("NRG", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet", "link", nmr, "([\\w]{4})", CrawlType.LINE));
		dbdao.makePersistent(nrg_docr = new Databank("NRG-DOCR", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet", "link", nrg, "([\\w]{4})", CrawlType.LINE));
		dbdao.makePersistent(new Databank("NRG-CING", "http://nmr.cmbi.ru.nl/cing/", "link", nrg_docr, "([\\w]{4})", CrawlType.LINE));

		dbdao.makePersistent(sf = new Databank("STRUCTUREFACTORS", "http://www.pdb.org/", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=STRUCTFACT&compression=NO&structureId=", pdb, ".*/r([\\w]{4})sf\\.ent", CrawlType.FILE));
		dbdao.makePersistent(new Databank("PDB_REDO", "http://www.cmbi.ru.nl/pdb_redo/", "http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=", sf, ".*/[\\w]{2}/([\\d][\\w]{3})", CrawlType.FILE));

		Logger.getLogger(getClass()).info("Databanks stored.");
	}
}
