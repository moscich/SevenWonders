package com.moscichowski.WebWonders

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class SpringJdbcConfig {
    @Value("\${spring.datasource.url}")
    lateinit var url: String
    @Value("\${spring.datasource.username}")
    lateinit var username: String
    @Value("\${spring.datasource.password}")
    lateinit var password: String


    @Bean
    fun postgresDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.url = url
        dataSource.username = username
        dataSource.password = password

        return dataSource
    }
}