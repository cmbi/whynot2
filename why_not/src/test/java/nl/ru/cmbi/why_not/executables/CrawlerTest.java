package nl.ru.cmbi.why_not.executables;

import nl.ru.cmbi.why_not.crawl.Crawler;

import org.junit.Test;


public class CrawlerTest {
	@Test
	public void crawlPDB() throws Exception {
		Crawler.main(new String[] { "PDB", "/home/tbeek/Desktop/raw/pdb/" });
	}

	@Test
	public void crawlDSSP() throws Exception {
		Crawler.main(new String[] { "DSSP", "/home/tbeek/Desktop/raw/dssp/" });
	}

	@Test
	public void crawlHSSP() throws Exception {
		Crawler.main(new String[] { "HSSP", "/home/tbeek/Desktop/raw/hssp/" });
	}

	@Test
	public void crawlPDBFINDER() throws Exception {
		Crawler.main(new String[] { "PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT" });
	}
}
