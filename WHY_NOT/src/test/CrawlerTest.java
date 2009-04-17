package test;

import junit.framework.Assert;

import org.junit.Test;

import crawl.Crawler;

public class CrawlerTest {
	@Test
	public void crawlPDB() throws Exception {
		crawl("PDB", "/home/tbeek/Desktop/raw/pdb/");
	}

	@Test
	public void crawlDSSP() throws Exception {
		crawl("DSSP", "/home/tbeek/Desktop/raw/dssp/");
	}

	//@Test
	public void crawlHSSP() throws Exception {
		crawl("HSSP", "/home/tbeek/Desktop/raw/hssp/");
	}

	//@Test
	public void crawlPDBFINDER() throws Exception {
		crawl("PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT");
	}

	private void crawl(String db, String pth) throws Exception {
		Assert.assertTrue(Crawler.crawl(db, pth));
	}
}
