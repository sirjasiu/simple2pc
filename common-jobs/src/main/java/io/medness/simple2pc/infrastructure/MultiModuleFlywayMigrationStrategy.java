package io.medness.simple2pc.infrastructure;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

/**
 * This is custom implementation of Flyway migration strategy. When instantiated as a bean in spring boot application
 * it is used instead of a default implementation delivered with spring boot starters.
 *
 * The following strategy delivers separate migration context for simple2pc database structure in the schema simple2pc.
 * Migration scripts are taken from the db/simple2pc folder.
 *
 * Default migration is also executed. Its configuration can be defined in spring boot application properties.
 */
public class MultiModuleFlywayMigrationStrategy implements FlywayMigrationStrategy {

    @Override
    public void migrate(Flyway flyway) {
        var dataSource = flyway.getConfiguration().getDataSource();
        Flyway simple2pcModule = Flyway.configure()
                .schemas("simple2pc")
                .locations("db/simple2pc")
                .table("simple2pcHistory")
                .dataSource(dataSource).load();

        simple2pcModule.migrate();
        flyway.migrate();

    }
}