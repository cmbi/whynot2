package nl.ru.cmbi.whynot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import nl.ru.cmbi.whynot.hibernate.DomainObjectRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = { DomainObjectRepository.class })
public class DatabaseConfig {}
