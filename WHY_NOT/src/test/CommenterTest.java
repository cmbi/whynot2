package test;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import annotate.Commenter;

public class CommenterTest {

	String	comfile		= "comment/com1.txt";
	String	uncomfile	= "uncomment/uncom1.txt";

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {
		new File(comfile + "~").renameTo(new File(comfile));
		new File(uncomfile + "~").renameTo(new File(uncomfile));
	}

	@Test
	public void comment() throws Exception {
		Assert.assertTrue(Commenter.comment(comfile));
	}

	@Test
	public void uncomment() throws Exception {
		Assert.assertTrue(Commenter.uncomment(uncomfile));
	}
}
