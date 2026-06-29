package com.emras.shared.config;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
/**
 * Manual Flyway configuration.
 *
 * Spring Boot 4 restructured Flyway auto-configuration. This bean
 * wires Flyway explicitly to guarantee migrations run on startup
 * regardless of auto-configuration ordering.
 */
@Configuration
public class FlywayConfig {
    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .loggers("slf4j")
                .load();
    }
}