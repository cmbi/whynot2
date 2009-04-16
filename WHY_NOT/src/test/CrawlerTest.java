package test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import crawl.Crawler;

public class CrawlerTest {

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void crawlPDB() throws Exception {
		crawl("PDB", "/home/tbeek/Desktop/raw/");
	}

	@Test
	public void crawlDSSP() throws Exception {
		crawl("DSSP", "/home/tbeek/Desktop/raw/");
	}

	@Test
	public void crawlHSSP() throws Exception {
		crawl("HSSP", "/home/tbeek/Desktop/raw/");
	}

	@Test
	public void crawlPDBFINDER() throws Exception {
		crawl("PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT");
	}

	private void crawl(String db, String pth) throws Exception {
		Assert.assertTrue(Crawler.crawl(db, pth));
	}
}
