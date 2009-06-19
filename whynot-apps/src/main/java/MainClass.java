import nl.ru.cmbi.whynot.annotate.Annotater;
import nl.ru.cmbi.whynot.backup.Backuper;
import nl.ru.cmbi.whynot.crawl.Crawler;
import nl.ru.cmbi.whynot.list.Lister;
import nl.ru.cmbi.whynot.util.Install;

public class MainClass {
	public static void main(String[] args) {
		System.out.println("Usage:");
		String base = "java -cp dependency/*:whynot-apps.jar ";
		for (Class<Object> clazz : new Class[] { Install.class, Crawler.class, Annotater.class, Lister.class, Backuper.class })
			System.out.println(base + clazz.getCanonicalName());
	}
}
