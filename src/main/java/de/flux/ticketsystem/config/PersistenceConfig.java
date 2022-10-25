package de.flux.ticketsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "de.flux.ticketsystem.ticket")
@EnableJpaAuditing
public class PersistenceConfig {}
