package net.zeotrope.item

import com.redis.testcontainers.RedisContainer
import net.zeotrope.item.util.TestServiceContainers
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection(name = "postgres")
    fun postgresContainer(): PostgreSQLContainer<*> = TestServiceContainers.postgresContainer

    @Bean
    @ServiceConnection(name = "redis")
    fun redisContainer(): RedisContainer = TestServiceContainers.redisContainer
}
