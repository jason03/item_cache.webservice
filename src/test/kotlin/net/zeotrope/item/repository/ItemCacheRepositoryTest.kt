package net.zeotrope.item.repository

import net.zeotrope.item.TestcontainersConfiguration
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.util.TestServiceContainers
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@Import(TestcontainersConfiguration::class)
@DirtiesContext
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class ItemCacheRepositoryTest(@Autowired private val itemCacheRepository: ItemCacheRepository) : TestServiceContainers() {

    @Before
    fun setUp() {
    }

    @Test
    fun `should check that invalid cache key returns empty`() {
        // given
        val invalidCacheKey = 1234567890L

        // when
        val actual = itemCacheRepository.get(invalidCacheKey)

        // then
        StepVerifier.create(actual)
            .verifyComplete()
    }

    @Test
    fun `should add item to the cache`() {
        // given
        val createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        val id = Random.nextLong()
        val item = Item(
            id = id,
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = createdDate,
            discontinuedAt = null
        )
        // when
        val actual = itemCacheRepository.put(item, Duration.ofSeconds(10))
        // then
        StepVerifier.create(actual)
            .assertNext {
                assertTrue(it)
            }
            .verifyComplete()
    }

    @Test
    fun `should remove an item from the cache`() {
        // given
        val createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        val id = Random.nextLong()
        val item = Item(
            id = id,
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = createdDate,
            discontinuedAt = null
        )
        // when
        val actual = itemCacheRepository.put(item, Duration.ofSeconds(100)).flatMap {
            when (it) {
                true -> itemCacheRepository.evict(id)
                false -> Mono.just(false)
            }
        }
        // then
        StepVerifier.create(actual)
            .assertNext {
                assertTrue(it)
            }
            .verifyComplete()
    }

    @Disabled
    @Test
    fun `should add item to the cache and retrieve it`() {
        // given
        val createdDate = LocalDateTime.of(2025, 6, 1, 0, 0, 0)
        val id = Random.nextLong()
        val item = Item(
            id = id,
            status = ItemStatus.CURRENT,
            name = "Test Item Title",
            summary = "Item Summary",
            createdAt = createdDate,
            lastModifiedAt = createdDate,
            discontinuedAt = null
        )
        // when
        val actual = itemCacheRepository.put(item, Duration.ofSeconds(10)).flatMap {
            itemCacheRepository.get(id)
        }
        // then
        StepVerifier.create(actual)
            .assertNext {
                assertTrue(it != null)
                assertEquals(item, it)
            }
            .verifyComplete()
    }

    @Test
    fun `should confirm that cache timeout defaults to 300 seconds`() {
        // given
        // when
        val actualTtl = itemCacheRepository.cacheTtl
        // then
        assertEquals(300, actualTtl)
    }
}
