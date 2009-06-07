package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.crawl.Crawler;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
public class CrawlerTest {
	@Autowired
	private Crawler	crawler;

	@Test
	public void crawlNMRNRG() throws Exception {
		crawler.crawl("NMR", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nmr.csv");
		crawler.crawl("NRG", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg.csv");
		crawler.crawl("NRG-DOCR", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg_docr.csv");
	}

	@Test
	public void validateNMRNRG() throws Exception {
		crawler.validate("NMR");
		crawler.validate("NRG");
		crawler.validate("NRG-DOCR");
	}

	@Test
	public void crawlNRGCING() throws Exception {
		crawler.crawl("NRG-CING", "/home/tbeek/workspace/whynot-apps/download/httpnmrcmbirunlNRGCINGentry_list_donecsv");
	}

	@Test
	public void validateNRGCING() throws Exception {
		crawler.validate("NRG-CING");
	}

	@Test
	@Ignore
	public void crawlPDB() throws Exception {
		crawler.crawl("PDB", "/home/tbeek/Desktop/raw/pdb/");
	}

	@Test
	@Ignore
	public void crawlDSSP() throws Exception {
		crawler.crawl("DSSP", "/home/tbeek/Desktop/raw/dssp/");
	}

	@Test
	@Ignore
	public void crawlHSSP() throws Exception {
		crawler.crawl("HSSP", "/home/tbeek/Desktop/raw/hssp/");
	}

	@Test
	@Ignore
	public void crawlPDBFINDER() throws Exception {
		crawler.crawl("PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT");
	}
}
