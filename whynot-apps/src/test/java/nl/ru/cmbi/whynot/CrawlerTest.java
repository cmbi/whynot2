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
	@Ignore
	public void doDSSP() throws Exception {
		crawler.removeChanged("DSSP");
		crawler.addCrawled("DSSP", "src/test/resources/dssp_present.txt");
	}

	@Test
	@Ignore
	public void crawlPDBDSSPHSSP() throws Exception {
		crawler.addCrawled("PDB", "src/test/resources/pdb_present.txt");
		crawler.addCrawled("DSSP", "src/test/resources/dssp_present.txt");
		crawler.addCrawled("HSSP", "src/test/resources/hssp_present.txt");
	}

	@Test
	@Ignore
	public void removePDBDSSPHSSP() throws Exception {
		crawler.removeChanged("PDB");
		crawler.removeChanged("DSSP");
		crawler.removeChanged("HSSP");
	}

	@Test
	@Ignore
	public void crawlNMRNRG() throws Exception {
		crawler.addCrawled("NMR", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nmr.csv");
		crawler.addCrawled("NRG", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg.csv");
		crawler.addCrawled("NRG-DOCR", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg_docr.csv");
		crawler.addCrawled("NRG-CING", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_done.csv");
	}
}
