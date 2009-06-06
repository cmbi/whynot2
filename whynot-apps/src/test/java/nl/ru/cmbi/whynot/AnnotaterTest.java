package nl.ru.cmbi.whynot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Scanner;

import nl.ru.cmbi.whynot.annotate.CommentParser;
import nl.ru.cmbi.whynot.annotate.Converter;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.CommentDAO;
import nl.ru.cmbi.whynot.hibernate.GenericDAO.EntryDAO;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
public class AnnotaterTest {
	@Autowired
	private CommentParser	cp;

	private static File		commentFile;
	private static File		uncommentFile;

	@BeforeClass
	public static void copyConvertAndOptimize() throws IOException, ParseException {
		{//CommentFile as copy of backup
			Scanner scn = new Scanner(new File("src/test/resources/20090519.txt.converted.optimized"));
			commentFile = new File("comment/testfile.txt");
			PrintWriter pwr = new PrintWriter(commentFile);
			while (scn.hasNextLine())
				pwr.println(scn.nextLine());
			pwr.close();
			scn.close();
			commentFile = Converter.getFile(commentFile);
		}
		{//UncommentFile as subset of commentfile
			Scanner scn = new Scanner(commentFile);
			uncommentFile = new File("uncomment/testfile.txt");
			PrintWriter pwr = new PrintWriter(uncommentFile);
			int count = 0;
			while (scn.hasNextLine()) {
				String line = scn.nextLine();
				if (count++ % 5 == 0)
					pwr.println(line);
			}
			pwr.close();
			scn.close();
			uncommentFile = Converter.getFile(uncommentFile);
		}
	}

	@Test
	public void comment() throws IOException, ParseException {
		cp.comment(commentFile);
	}

	@Test
	public void uncomment() throws IOException, ParseException {
		cp.uncomment(uncommentFile);
	}

	@Autowired
	private CommentDAO	comdao;
	@Autowired
	private EntryDAO	entdao;

	@Test
	public void cleanUp() {
		comdao.cleanUp();
		entdao.cleanUp();
	}

	@AfterClass
	public static void resetfiles() throws Exception {
		File dirComments = new File("comment/");
		File dirUncomments = new File("uncomment/");

		//Make sure comment directories exist
		if (!dirComments.isDirectory() && !dirComments.mkdir())
			throw new FileNotFoundException(dirComments.getAbsolutePath());
		if (!dirUncomments.isDirectory() && !dirUncomments.mkdir())
			throw new FileNotFoundException(dirUncomments.getAbsolutePath());

		//Rename files
		for (File file : dirComments.listFiles())
			file.renameTo(strip(file));
		for (File file : dirUncomments.listFiles())
			file.renameTo(strip(file));
	}

	private static File strip(File file) {
		String path = file.getAbsolutePath();
		path = path.replace(".converted", "");
		path = path.replace(".optimized", "");
		path = path.replace(".done", "");
		return new File(path);
	}
}
