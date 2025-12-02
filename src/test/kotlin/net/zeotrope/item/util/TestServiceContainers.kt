package net.zeotrope.item.util

import com.redis.testcontainers.RedisContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

// @Testcontainers
abstract class TestServiceContainers {
    companion object {
        //        @Container
        @JvmStatic
        val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:17.6")).apply {
            withDatabaseName("items_db")
            withUsername("test")
            withPassword("test")
        }

        //        @Container
        @JvmStatic
        val redisContainer = RedisContainer(DockerImageName.parse("redis:8.2.3-alpine")).apply {
            withExposedPorts(6379)
            waitingFor(Wait.defaultWaitStrategy())
        }

//        @JvmStatic
//        @DynamicPropertySource
//        fun properties(registry: DynamicPropertyRegistry) {
//            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
//            registry.add("spring.datasource.username", postgresContainer::getUsername)
//            registry.add("spring.datasource.password", postgresContainer::getPassword)
//
//            registry.add("spring.data.redis.host", redisContainer::getHost)
//            registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort)
//        }
    }
}
