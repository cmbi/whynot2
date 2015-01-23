/**
 * Copyright 2011 Tim te Beek <tim.te.beek@nbic.nl>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.ru.cmbi.whynot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Utility class to access the SpringContext if necessary (should hardly ever be used).
 *
 * @author tbeek
 */
@Deprecated
public class SpringUtil {
	private static final Logger			log	= LoggerFactory.getLogger(SpringUtil.class);

	private static ApplicationContext	applicationContext;

	/**
	 * Provide access to the Spring ApplicationContext to retrieve initial bean(s) from static void main functions.
	 *
	 * @return initialized Spring ApplicationContext
	 */
	public static ApplicationContext getContext() {
		return getContext("spring.xml");
	}

	public static ApplicationContext getContext(final String configLocation) {
		if (null == applicationContext) {
			log.warn("Setting SpringContext from ClassPathXmlApplicationContext: Should not happen with WAR!");
			AbstractApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(configLocation);
			classPathXmlApplicationContext.registerShutdownHook();
			applicationContext = classPathXmlApplicationContext;
		}
		return applicationContext;
	}
}
