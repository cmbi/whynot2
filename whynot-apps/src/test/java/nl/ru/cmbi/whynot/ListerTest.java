package nl.ru.cmbi.whynot;

import nl.ru.cmbi.whynot.list.Lister;
import nl.ru.cmbi.whynot.list.Lister.CollectionType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring.xml" })
public class ListerTest {
	@Autowired
	private Lister	lister;

	@Test
	public void nrgcing() throws Exception {
		for (String dbname : new String[] { "NMR", "NRG", "NRG-DOCR", "NRG-CING" })
			for (CollectionType ct : CollectionType.values())
				lister.list(dbname, ct);
	}
}
