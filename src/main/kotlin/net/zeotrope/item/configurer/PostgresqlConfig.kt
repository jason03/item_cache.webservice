package net.zeotrope.item.configurer

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.codec.EnumCodec
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import net.zeotrope.item.domain.ItemStatus
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories
class PostgresqlConfig(private val connectionDetails: R2dbcConnectionDetails) :
    AbstractR2dbcConfiguration(),
    Logging {
    @Bean
    override fun connectionFactory(): ConnectionFactory = connectionDetails.connectionFactoryOptions.run {
        PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(this.getValue(ConnectionFactoryOptions.HOST) as String)
                .port((this.getValue(ConnectionFactoryOptions.PORT) as Number).toInt())
                .database(this.getValue(ConnectionFactoryOptions.DATABASE) as String)
                .username(this.getValue(ConnectionFactoryOptions.USER) as String)
                .password(this.getValue(ConnectionFactoryOptions.PASSWORD) as CharSequence)
                .codecRegistrar(EnumCodec.builder().withEnum("item_status", ItemStatus::class.java).build())
                .build()
        )
    }

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions = R2dbcCustomConversions(storeConversions, listOf(ItemStatusWritingConverter()))

    @WritingConverter
    class ItemStatusWritingConverter : Converter<ItemStatus, ItemStatus> {
        override fun convert(source: ItemStatus): ItemStatus = source
    }
}
