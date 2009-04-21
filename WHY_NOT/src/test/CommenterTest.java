package test;

import java.io.File;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;

import comment.Commenter;

public class CommenterTest {
	static final String	comfile		= "comment/com1.txt";
	static final String	uncomfile	= "uncomment/uncom1.txt";

	@Test
	public void comment() throws Exception {
		Assert.assertTrue(new Commenter().comment(CommenterTest.comfile));
	}

	@Test
	public void uncomment() throws Exception {
		Assert.assertTrue(new Commenter().uncomment(CommenterTest.uncomfile));
	}

	@AfterClass
	public static void resetfiles() {
		new File(CommenterTest.comfile + ".done").renameTo(new File(CommenterTest.comfile));
		new File(CommenterTest.uncomfile + ".done").renameTo(new File(CommenterTest.uncomfile));
	}
}
