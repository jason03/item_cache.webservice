package net.zeotrope.item.configurer

import net.zeotrope.item.domain.Item
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext

@Configuration
@EnableRedisRepositories
class RedisCacheConfig(
    @Value("\${cache.redis.ttl:600}")
    val cacheTtl: Long
) {

//    @Bean
//    fun redisConnectionFactory(): RedisConnectionFactory = LettuceConnectionFactory()
//
//    @Bean
//    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager = RedisCacheManager.builder(connectionFactory)
//        .cacheDefaults(
//            RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(
//                    RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
//                )
//                .entryTtl(Duration.ofSeconds(cacheTtl)) // default TTL
//                .disableCachingNullValues()
//        )
//        .transactionAware()
//        .build()

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Item> {
        val serializer = GenericJackson2JsonRedisSerializer()
        val context = RedisSerializationContext
            .newSerializationContext<String, Item>(serializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }
}
