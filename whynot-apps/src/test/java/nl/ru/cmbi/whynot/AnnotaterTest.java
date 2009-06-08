package nl.ru.cmbi.whynot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Scanner;

import nl.ru.cmbi.whynot.annotate.CommentParser;
import nl.ru.cmbi.whynot.annotate.Converter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
			commentFile = new File("testfile_comment.txt");
			PrintWriter pwr = new PrintWriter(commentFile);
			while (scn.hasNextLine())
				pwr.println(scn.nextLine());
			pwr.close();
			scn.close();
			commentFile = Converter.getFile(commentFile);
		}
		{//UncommentFile one in every 5 lines in commentfile
			Scanner scn = new Scanner(commentFile);
			uncommentFile = new File("testfile_uncomment.txt");
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
	@Ignore
	public void comment() throws IOException, ParseException {
		commentFile = cp.comment(commentFile);
	}

	@Test
	public void uncomment() throws IOException, ParseException {
		uncommentFile = cp.uncomment(uncommentFile);
	}

	@AfterClass
	public static void resetfiles() throws Exception {
		commentFile.delete();
		uncommentFile.delete();
	}
}
