package nl.ru.cmbi.whynot.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.hibernate.GenericDAO.DatabankDAO;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CrawlType;

@Service
@Transactional
public class Install {
	private static final Logger	log	= LoggerFactory.getLogger(Install.class);

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
		log.info("User rights set.");
	}

	@Autowired
	private DatabankDAO	dbdao;

	public void storeDatabanks() {
		//Link's to proper urls with ${PDBID}
		Databank pdb, dssp, nmr, nrg, nrg_docr, sfr;
		dbdao.makePersistent(pdb = new Databank("PDB", CrawlType.FILE, ".*/pdb([\\w]{4})\\.ent(\\.gz)?", "http://www.wwpdb.org/", "ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/pdb/pdb${PDBID}.ent.gz"));
		dbdao.makePersistent(new Databank("PDBFINDER", pdb, CrawlType.LINE, "ID           : ([\\w]{4})", "http://swift.cmbi.ru.nl/gv/pdbfinder/", "ftp://ftp.cmbi.ru.nl/pub/molbio/data/pdbfinder/PDBFIND.TXT.gz"));
		dbdao.makePersistent(new Databank("PDBREPORT", pdb, CrawlType.FILE, ".*pdbreport.*/([\\w]{4})", "http://swift.cmbi.ru.nl/gv/pdbreport/", "http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=${PDBID}"));

		dbdao.makePersistent(dssp = new Databank("DSSP", pdb, CrawlType.FILE, ".*/([\\w]{4})\\.dssp", "http://swift.cmbi.ru.nl/gv/dssp/", "ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/${PDBID}.dssp"));
		dbdao.makePersistent(new Databank("HSSP", dssp, CrawlType.FILE, ".*/([\\w]{4})\\.hssp", "http://swift.cmbi.ru.nl/gv/hssp/", "ftp://ftp.cmbi.ru.nl/pub/molbio/data/hssp/${PDBID}.hssp"));

		dbdao.makePersistent(nmr = new Databank("NMR", pdb, CrawlType.LINE, "([\\w]{4})", "http://www.bmrb.wisc.edu/", "ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/nmr_restraints/${PDBID}.mr.gz"));
		dbdao.makePersistent(new Databank("RECOORD", nmr, CrawlType.FILE, ".*/([\\w]{4})_cns_w\\.pdb", "http://www.ebi.ac.uk/msd-srv/docs/NMR/recoord/main.html", "http://www.ebi.ac.uk/msd/NMR/recoord/entries/${PDBID}.html"));//
		dbdao.makePersistent(nrg = new Databank("NRG", nmr, CrawlType.LINE, "([\\w]{4})", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet?pdb_id=${PDBID}"));
		dbdao.makePersistent(nrg_docr = new Databank("NRG-DOCR", nrg, CrawlType.LINE, "([\\w]{4})", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet", "http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet?block_text_type=3-converted-DOCR&pdb_id=${PDBID}"));
		dbdao.makePersistent(new Databank("NRG-CING", nrg_docr, CrawlType.LINE, "([\\w]{4})", "http://nmr.cmbi.ru.nl/cing/", "http://nmr.cmbi.ru.nl/NRG-CING/data/${PART}/${PDBID}/${PDBID}.cing"));

		dbdao.makePersistent(sfr = new Databank("STRUCTUREFACTORS", pdb, CrawlType.FILE, ".*/r([\\w]{4})sf\\.ent", "http://www.pdb.org/", "ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/structure_factors/r${PDBID}sf.ent.gz"));
		dbdao.makePersistent(new Databank("PDB_REDO", sfr, CrawlType.FILE, ".*/[\\w]{2}/([\\d][\\w]{3})", "http://www.cmbi.ru.nl/pdb_redo/", "http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=${PDBID}"));

		log.info("Databanks stored.");
	}
}
