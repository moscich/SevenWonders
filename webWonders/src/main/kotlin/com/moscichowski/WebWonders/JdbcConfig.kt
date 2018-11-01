package com.moscichowski.WebWonders

import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource
import com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER
import java.io.IOException
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.opentable.db.postgres.embedded.FlywayPreparer
import com.opentable.db.postgres.junit.EmbeddedPostgresRules


//@Configuration
//@ComponentScan("com.moscichowski.jdbc")
//class SpringJdbcConfig {
//    @Bean
//    fun postgresDataSource(): DataSource {
//
//        val dataSource = DriverManagerDataSource()
//        dataSource.url = "jdbc:postgresql://localhost:5432/wonders"
//        dataSource.username = "wonders"
//        dataSource.password = "wonders"
//
//        return dataSource
//    }
//}

//@Bean
//fun embeddedDataSource(): DataSource {
//    val dataSource: DataSource
//        dataSource = EmbeddedPostgres
//                .builder().set
////                .setPgBinaryResolver(ClasspathBinaryResolver())
////                .setPort(port)
//                .start().templateDatabase
//        return dataSource
//}