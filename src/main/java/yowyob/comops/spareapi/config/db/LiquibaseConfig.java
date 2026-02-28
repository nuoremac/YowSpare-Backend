package yowyob.comops.spareapi.config.db;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Ensures Liquibase runs even in a reactive (WebFlux + R2DBC) app.
 *
 * Spring Boot can run Liquibase only when a JDBC DataSource exists.
 * This config creates a JDBC DataSource from spring.datasource.* if none exists.
 */
@Configuration
public class LiquibaseConfig {
    private static final Logger log = LoggerFactory.getLogger(LiquibaseConfig.class);

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(DataSourceProperties properties) {
        if (properties.getUrl() != null && !properties.getUrl().isBlank()) {
            log.info("Configuring JDBC datasource for Liquibase: {}", properties.getUrl());
        }
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    @ConditionalOnMissingBean(SpringLiquibase.class)
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
        return liquibase;
    }
}
