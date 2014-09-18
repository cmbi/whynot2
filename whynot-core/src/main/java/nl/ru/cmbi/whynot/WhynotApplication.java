package nl.ru.cmbi.whynot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WhynotApplication {
	public static void main(final String[] args) throws Exception {
		SpringApplication.run(WhynotApplication.class, args);
	}
}
