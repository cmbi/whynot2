package test;

import install.Installer;

import org.junit.Assert;
import org.junit.Test;

public class InstallTest {
	@Test
	public void testMain() {
		try {
			Installer.main(new String[0]);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
	}
}
