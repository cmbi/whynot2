package nl.ru.cmbi.whynot.executables;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.ParseException;

import nl.ru.cmbi.whynot.comment.Converter;

import org.junit.Ignore;
import org.junit.Test;

public class ConverterTest {
	@Test
	@Ignore
	public void copyFileAndConvert() throws IOException, ParseException {
		//Read
		File src = new File("src/test/resources/20090519.txt");
		LineNumberReader lnr = new LineNumberReader(new FileReader(src));
		//Write
		File dest = new File("comment/20090519.txt");
		PrintWriter pw = new PrintWriter(new FileWriter(dest));

		String line;
		while ((line = lnr.readLine()) != null)
			pw.println(line);
		lnr.close();
		pw.close();

		//Convert
		Converter.main(new String[] { dest.getPath() });
	}
}
