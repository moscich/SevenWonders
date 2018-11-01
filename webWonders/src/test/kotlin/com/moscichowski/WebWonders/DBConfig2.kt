package com.moscichowski.WebWonders

import de.flapdoodle.embed.process.runtime.Network
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.context.annotation.DependsOn
import org.springframework.transaction.annotation.EnableTransactionManagement
import ru.yandex.qatools.embed.postgresql.PostgresProcess
import ru.yandex.qatools.embed.postgresql.PostgresStarter
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig
import ru.yandex.qatools.embed.postgresql.distribution.Version
import java.lang.String.format
import java.util.*
import javax.sql.DataSource


/**
 * the db spring configuration to use in production , to be replaced with actual production configuration , that is for local run only
 */
@Configuration
@EnableTransactionManagement
class DbConfig {
    /**
     * @param config the PostgresConfig configuration which will be used to get the needed host, port..
     * @return the created DB datasource
     */
    @Bean
    @DependsOn("postgresProcess")
    fun dataSource(config: PostgresConfig): DataSource {
        val ds = DriverManagerDataSource()
        ds.setDriverClassName("org.postgresql.Driver")
        ds.url = format("jdbc:postgresql://%s:%s/%s", config.net().host(), config.net().port(), config.storage().dbName())
        ds.username = config.credentials().username()
        ds.password = config.credentials().password()
        return ds
    }

    /**
     * @return PostgresConfig that contains embedded db configuration like user name , password
     * @throws IOException
     */
    @Bean
    @Throws(IOException::class)
    fun postgresConfig(): PostgresConfig {
        // make it readable from configuration source file or system , it is hard coded here for explanation purpose only
        val postgresConfig = PostgresConfig(Version.V9_6_8,
                AbstractPostgresConfig.Net("localhost", Network.getFreeServerPort()),
                AbstractPostgresConfig.Storage("test"),
                AbstractPostgresConfig.Timeout(),
                AbstractPostgresConfig.Credentials("user", "pass")
        )
        postgresConfig.getAdditionalInitDbParams().addAll(DEFAULT_ADDITIONAL_INIT_DB_PARAMS)
        return postgresConfig
    }

    /**
     * @param config the PostgresConfig configuration to use to start Postgres db process
     * @return PostgresProcess , the started db process
     * @throws IOException
     */
    @Bean(destroyMethod = "stop")
    @Throws(IOException::class)
    fun postgresProcess(config: PostgresConfig): PostgresProcess {
        val runtime = PostgresStarter.getDefaultInstance()
        val exec = runtime.prepare(config)
        return exec.start()
    }

    companion object {
        private val DEFAULT_ADDITIONAL_INIT_DB_PARAMS = Arrays
                .asList("--nosync", "--locale=en_US.UTF-8")
    }
}