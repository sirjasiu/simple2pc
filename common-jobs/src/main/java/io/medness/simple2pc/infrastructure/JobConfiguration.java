package io.medness.simple2pc.infrastructure;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigurationPackage
@ComponentScan(basePackages = "io.medness.simple2pc.job")
@EntityScan(basePackages = "io.medness.simple2pc.job")
@EnableJpaRepositories("io.medness.simple2pc.job")
public class JobConfiguration {

    @Bean
    @ConditionalOnClass(Flyway.class)
    public FlywayMigrationStrategy flywayMigrationStrategyForJobs() {
        return new MultiModuleFlywayMigrationStrategy();
    }
}
