package com.moscichowski.WebWonders

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class SpringJdbcConfig {
    @Bean
    fun postgresDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.url = "jdbc:postgresql://localhost:5432/wonders"
        dataSource.username = "wonders"
        dataSource.password = "wonders"

        return dataSource
    }
}