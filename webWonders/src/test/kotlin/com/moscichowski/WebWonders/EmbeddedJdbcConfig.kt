package com.moscichowski.WebWonders

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class SpringJdbcConfig {
    @Bean
    @Primary
    fun postgresDataSource(): DataSource {
        val start = EmbeddedPostgres.start()
        return start.postgresDatabase


    }
}