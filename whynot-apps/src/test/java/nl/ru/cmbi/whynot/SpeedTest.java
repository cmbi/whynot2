package nl.ru.cmbi.whynot;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import nl.ru.cmbi.whynot.annotate.Annotater;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WhynotApplication.class)
@Slf4j
@Ignore("whynot-testing database is not available in all run/test environments")
public class SpeedTest {
	private static final String	WI_OPTIMIZED	= "src/test/resources/wi.optimized.txt";

	@Autowired
	private Annotater	annotator;

	@Test(timeout = 180 * 1000)
	// Initial run after update to SpringBoot took 240 seconds; Any further changes may not add to this time
	public void annotate() throws Exception {
		log.info("Start");
		Annotater.comment(annotator, new File(WI_OPTIMIZED));
		log.info("Stop");
	}

	@After
	@SuppressWarnings("static-method")
	public void renameDone() {
		Assert.assertTrue(new File(WI_OPTIMIZED + Annotater.append).renameTo(new File(WI_OPTIMIZED)));
	}
}
