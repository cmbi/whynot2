package nl.ru.cmbi.why_not.executables;

import java.io.IOException;
import java.text.ParseException;

import nl.ru.cmbi.why_not.comment.Converter;

import org.junit.Ignore;
import org.junit.Test;

public class ConverterTest {
	@Test
	@Ignore
	public void testMain() throws IOException, ParseException {
		Converter.main(new String[] { "comment/20090407_comments.txt" });
	}
}
