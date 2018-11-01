package com.moscichowski.WebWonders

//import com.opentable.db.postgres.embedded.EmbeddedPostgres
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.context.annotation.Configuration
//import javax.sql.DataSource


//@Configuration
//@ComponentScan("com.moscichowski.jdbc")
//class SpringJdbcConfig {
//    @Bean
//    fun postgresDataSource(): DataSource {
//
//        val start = EmbeddedPostgres.start()
////        val flyway = Flyway.configure().dataSource("jdbc:h2:file:./target/foobar", "sa", null).load()
////
//        // Start the migration
////        flyway.migrate()
//
//        return start.postgresDatabase
//
//
////        val dataSource = DriverManagerDataSource()
////        dataSource.url = "jdbc:postgresql://localhost:5432/wonders"
////        dataSource.username = "wonders"
////        dataSource.password = "wonders"
////
////        return dataSource
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