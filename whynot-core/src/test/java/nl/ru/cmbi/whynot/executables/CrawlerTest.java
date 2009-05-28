package nl.ru.cmbi.whynot.executables;

import nl.ru.cmbi.whynot.crawl.Crawler;

import org.junit.Ignore;
import org.junit.Test;

public class CrawlerTest {
	@Test
	@Ignore
	public void crawlPDB() throws Exception {
		Crawler.main(new String[] { "PDB", "/home/tbeek/Desktop/raw/pdb/" });
	}

	@Test
	@Ignore
	public void crawlDSSP() throws Exception {
		Crawler.main(new String[] { "DSSP", "/home/tbeek/Desktop/raw/dssp/" });
	}

	@Test
	@Ignore
	public void crawlHSSP() throws Exception {
		Crawler.main(new String[] { "HSSP", "/home/tbeek/Desktop/raw/hssp/" });
	}

	@Test
	@Ignore
	public void crawlPDBFINDER() throws Exception {
		Crawler.main(new String[] { "PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT" });
	}

	@Test
	public void crawlNRGCING() throws Exception {
		Crawler.main(new String[] { "NMR", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nmr.csv" });
		Crawler.main(new String[] { "NRG", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg.csv" });
		Crawler.main(new String[] { "NRG-DOCR", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_nrg_docr.csv" });
		Crawler.main(new String[] { "NRG-CING", "http://nmr.cmbi.ru.nl/NRG-CING/entry_list_done.csv" });
	}
}
