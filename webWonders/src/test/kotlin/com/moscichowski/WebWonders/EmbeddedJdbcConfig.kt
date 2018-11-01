package com.moscichowski.WebWonders

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.opentable.db.postgres.embedded.FlywayPreparer
import com.opentable.db.postgres.junit.EmbeddedPostgresRules
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@ComponentScan("com.moscichowski.jdbc")
class EmbeddedJdbcConfig {
    @Bean
    fun mysqlDataSource(): DataSource {
        val start = EmbeddedPostgres.start()
        return start.postgresDatabase


    }
}