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
		Logger.getLogger(Installer.class).info("Done.");
	}

	@Autowired
	private DatabankDAO	dbdao;

	public void fill() {
		Databank pdb, dssp, sf;
		dbdao.makePersistent(pdb = new Databank("PDB", "http://www.pdb.org/", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=pdb&compression=NO&structureId=", ".*/pdb([\\d\\w]{4})\\.ent(\\.gz)?", CrawlType.FILE));
		dbdao.makePersistent(new Databank("PDBFINDER", "http://swift.cmbi.ru.nl/gv/pdbfinder/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=pdbfinder2&id=", pdb, "ID           : ([\\d\\w]{4})", CrawlType.LINE));
		dbdao.makePersistent(new Databank("PDBREPORT", "http://swift.cmbi.ru.nl/gv/pdbreport/", "http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=", pdb, ".*pdbreport.*/([\\d\\w]{4})", CrawlType.FILE));
		dbdao.makePersistent(new Databank("NRG-CING", "http://nmr.cmbi.ru.nl/cing/", "http://www.cmbi.ru.nl/cgi-bin/cing.pl?id=", pdb, ".*/([\\d\\w]{4})\\.exist", CrawlType.FILE));
		dbdao.makePersistent(new Databank("RECOORD", "http://www.ebi.ac.uk/msd-srv/docs/NMR/recoord/main.html", "http://www.cmbi.ru.nl/whynot/recoord_trampoline/", pdb, ".*/([\\d\\w]{4})_cns_w\\.pdb", CrawlType.FILE));

		dbdao.makePersistent(dssp = new Databank("DSSP", "http://swift.cmbi.ru.nl/gv/dssp/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=dssp&id=", pdb, ".*/([\\d\\w]{4})\\.dssp", CrawlType.FILE));
		dbdao.makePersistent(new Databank("HSSP", "http://swift.cmbi.ru.nl/gv/hssp/", "http://mrs.cmbi.ru.nl/mrs-3/entry.do?db=hssp&id=", dssp, ".*/([\\d\\w]{4})\\.hssp", CrawlType.FILE));

		dbdao.makePersistent(sf = new Databank("STRUCTUREFACTORS", "http://www.pdb.org/", "http://www.pdb.org/pdb/download/downloadFile.do?fileFormat=STRUCTFACT&compression=NO&structureId=", pdb, ".*/r([\\d\\w]{4})sf\\.ent", CrawlType.FILE));
		dbdao.makePersistent(new Databank("PDB_REDO", "http://www.cmbi.ru.nl/pdb_redo/", "http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=", sf, ".*/[\\d\\w]{2}/([\\d][\\d\\w]{3})", CrawlType.FILE));

	}
}
