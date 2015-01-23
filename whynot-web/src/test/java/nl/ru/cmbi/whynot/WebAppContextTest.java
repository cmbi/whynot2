package nl.ru.cmbi.whynot;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WicketApplication.class)
public class WebAppContextTest {
	@Test
	@SuppressWarnings("static-method")
	public void configLoads() {
		Assert.assertTrue("We should be able to load the security config without any start-up problems", true);
	}
}