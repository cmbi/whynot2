package nl.ru.cmbi.whynot.util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.ru.cmbi.whynot.hibernate.DatabankRepo;
import nl.ru.cmbi.whynot.model.Databank;
import nl.ru.cmbi.whynot.model.Databank.CrawlType;

@Service
@Transactional
@Deprecated
public class Install {
	private static final Logger	log	= LoggerFactory.getLogger(Install.class);

	public static void main(final String[] args) throws Exception {
		AnnotationSessionFactoryBean asfb = (AnnotationSessionFactoryBean) SpringUtil.getContext().getBean("&sessionFactory");
		asfb.dropDatabaseSchema();
		asfb.createDatabaseSchema();

		Install installer = (Install) SpringUtil.getContext().getBean("install");
		installer.setUserRights();
		installer.storeDatabanks();
	}

	@PersistenceContext
	private EntityManager	entityManager;

	public void setUserRights() {
		Session s = (Session) entityManager.getDelegate();
		s.createSQLQuery("grant usage on schema public to whynotuser;").executeUpdate();
		s.createSQLQuery("grant select on table annotation, comment, databank, entry, file to whynotuser;").executeUpdate();
		log.info("User rights set.");
	}

	@Autowired
	private DatabankRepo	dbdao;

	public void storeDatabanks() {
		// XXX Database dependencies, urls and file names should be stored in a configuration file instead, if not in DB
		// Links to proper urls with ${PDBID}
		Databank mmcif, pdb, pdbfinder, dssp, nmr, nrg, nrg_docr, sfr, pdb_redo;
		dbdao.save(mmcif = new Databank("MMCIF",
				CrawlType.FILE, ".*/([\w]{4})\.cif(\.gz)?",
				"http://www.wwpdb.org/",
				"ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/mmCIF/${PART}/${PDBID}.cif.gz"));
		dbdao.save(pdb = new Databank("PDB",mmcif
				CrawlType.FILE, ".*/pdb([\\w]{4})\\.ent(\\.gz)?",
				"http://www.wwpdb.org/",
				"ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/pdb/pdb${PDBID}.ent.gz"));
		dbdao.save(pdbfinder = new Databank("PDBFINDER", pdb,
				CrawlType.LINE, "ID           : ([\\w]{4})",
				"http://swift.cmbi.ru.nl/gv/pdbfinder/",
				"ftp://ftp.cmbi.ru.nl/pub/molbio/data/pdbfinder/PDBFIND.TXT.gz"));
		dbdao.save(new Databank("PDBFINDER2", pdbfinder,
				CrawlType.LINE, "ID           : ([\\w]{4})",
				"http://swift.cmbi.ru.nl/gv/pdbfinder/",
				"ftp://ftp.cmbi.ru.nl/pub/molbio/data/pdbfinder2/PDBFIND2.TXT.gz"));
		dbdao.save(new Databank("PDBREPORT", pdb,
				CrawlType.FILE, ".*pdbreport.*/([\\w]{4})",
				"http://swift.cmbi.ru.nl/gv/pdbreport/",
				"http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=${PDBID}"));

		dbdao.save(dssp = new Databank("DSSP", pdb,
				CrawlType.FILE, ".*/([\\w]{4})\\.dssp",
				"http://swift.cmbi.ru.nl/gv/dssp/",
				"ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/${PDBID}.dssp"));
		dbdao.save(new Databank("HSSP", dssp,
				CrawlType.FILE, ".*/([\\w]{4})\\.hssp",
				"http://swift.cmbi.ru.nl/gv/hssp/",
				"ftp://ftp.cmbi.ru.nl/pub/molbio/data/hssp/${PDBID}.hssp.bz2"));

		dbdao.save(nmr = new Databank("NMR", pdb,
				CrawlType.LINE, "([\\w]{4})",
				"http://www.bmrb.wisc.edu/",
				"ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/nmr_restraints/${PDBID}.mr.gz"));
		dbdao.save(new Databank("RECOORD", nmr,
				CrawlType.FILE, ".*/([\\w]{4})_cns_w\\.pdb",
				"http://www.ebi.ac.uk/msd-srv/docs/NMR/recoord/main.html",
				"http://www.ebi.ac.uk/msd/NMR/recoord/entries/${PDBID}.html"));//
		dbdao.save(nrg = new Databank("NRG", nmr,
				CrawlType.LINE, "([\\w]{4})",
				"http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet",
				"http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet?pdb_id=${PDBID}"));
		dbdao.save(nrg_docr = new Databank("NRG-DOCR", nrg,
				CrawlType.LINE, "([\\w]{4})",
				"http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet",
				"http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet?block_text_type=3-converted-DOCR&pdb_id=${PDBID}"));
		dbdao.save(new Databank("NRG-CING", nrg_docr,
				CrawlType.LINE, "([\\w]{4})",
				"http://nmr.cmbi.ru.nl/cing/",
				"http://nmr.cmbi.ru.nl/NRG-CING/data/${PART}/${PDBID}/${PDBID}.cing"));

		dbdao.save(sfr = new Databank("STRUCTUREFACTORS", mmcif,
				CrawlType.FILE, ".*/r([\\w]{4})sf\\.ent\\.gz",
				"http://www.pdb.org/",
				"ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/structure_factors/r${PDBID}sf.ent.gz"));
		dbdao.save(pdb_redo = new Databank("PDB_REDO", sfr,
				CrawlType.FILE, ".*/[\\w]{2}/([\\d][\\w]{3})",
				"http://www.cmbi.ru.nl/pdb_redo/",
				"http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=${PDBID}"));

		dbdao.save(new Databank("BDB", pdb,
				CrawlType.FILE, ".*/([\\w]{4})\\.bdb",
				"http://www.cmbi.ru.nl/bdb/",
				"ftp://ftp.cmbi.ru.nl/pub/molbio/data/bdb/${PART}/${PDBID}/${PDBID}.bdb"));

		String[] whatifLisTypes = {
				"acc","cal","cc1","cc2","cc3","chi","dsp","iod","sbh","sbr","ss1","ss2","tau","wat"
		};

		for (String lis : whatifLisTypes) {

			dbdao.save(pdbfinder = new Databank(String.format("WHATIF_PDB_%s", lis.toUpperCase()), pdb,
					CrawlType.FILE, String.format(".*/([\\w]{4})\\.%s(\\.bz2)?",lis),
					"http://swift.cmbi.ru.nl/whatif/",
					String.format("ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/pdb/%s/${PDBID}/${PDBID}.%s.bz2",lis,lis)));

			dbdao.save(pdbfinder = new Databank(String.format("WHATIF_REDO_%s", lis.toUpperCase()), pdb_redo,
					CrawlType.FILE, String.format(".*/([\\w]{4})\\.%s(\\.bz2)?",lis),
					"http://swift.cmbi.ru.nl/whatif/",
					String.format("ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/redo/%s/${PDBID}/${PDBID}.%s.bz2",lis,lis)));
		}

		log.info("Databanks stored.");
	}
}
