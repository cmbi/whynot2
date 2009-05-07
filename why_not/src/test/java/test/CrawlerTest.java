package test;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import crawl.Crawler;

public class CrawlerTest {
	@Before
	public void pause() throws IOException {
		;//new BufferedReader(new InputStreamReader(System.in)).readLine();
	}

	@Test
	public void crawlPDB() throws Exception {
		crawl("PDB", "/home/tbeek/Desktop/raw/pdb/");
	}

	//@Test
	public void crawlDSSP() throws Exception {
		crawl("DSSP", "/home/tbeek/Desktop/raw/dssp/");
	}

	//@Test
	public void crawlHSSP() throws Exception {
		crawl("HSSP", "/home/tbeek/Desktop/raw/hssp/");
	}

	@Test
	public void crawlPDBFINDER() throws Exception {
		crawl("PDBFINDER", "/home/tbeek/Desktop/raw/pdbfinder/PDBFIND.TXT");
	}

	private void crawl(String db, String pth) throws Exception {
		Assert.assertTrue(new Crawler().crawl(db, pth));
	}
}
