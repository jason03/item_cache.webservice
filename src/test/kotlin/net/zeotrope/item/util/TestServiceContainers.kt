package net.zeotrope.item.util

import com.redis.testcontainers.RedisContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

abstract class TestServiceContainers {
    companion object {
        @JvmStatic
        val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:17.6")).apply {
            withDatabaseName("items_db")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        val redisContainer = RedisContainer(DockerImageName.parse("redis:8.2.3-alpine")).apply {
            withExposedPorts(6379)
            waitingFor(Wait.defaultWaitStrategy())
        }
    }
}
