package net.zeotrope.item.repository

import kotlinx.coroutines.test.runTest
import net.zeotrope.item.TestcontainersConfiguration
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.mapper.toNewItem
import net.zeotrope.item.model.ItemDto
import net.zeotrope.item.util.TestServiceContainers
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

@Import(TestcontainersConfiguration::class)
@DirtiesContext
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class ItemRepositoryTest : TestServiceContainers() {

    @Autowired
    private lateinit var itemRepository: ItemRepository

    @Before
    fun setUp() {
    }

    @Test
    fun `should add an item to the database`() = runTest {
        // given
        val itemDto = ItemDto(
            status = ItemStatus.CURRENT,
            name = "Article Title",
            description = "Article Summary"
        )
        val item = itemDto.toNewItem()

        // when
        val actual = itemRepository.save(item)

        // then
        assertAll(
            { assertEquals(item.status, actual.status) },
            { assertEquals(item.name, actual.name) },
            { assertEquals(item.summary, actual.summary) },
            { assertEquals(item.createdAt, actual.createdAt) },
            { assertEquals(item.lastModifiedAt, actual.lastModifiedAt) }
        )
    }

    @Test
    fun `should return null when retrieving a item with an invalid id`() = runTest {
        // given
        val invalidId: Long = 1234567890

        // when
        val actual = itemRepository.findById(invalidId)
        // then
        assertNull(actual)
    }

    @Test
    fun `should return an item when using a valid id`() = runTest {
        // given
        val itemDto = ItemDto(
            status = ItemStatus.CURRENT,
            name = "Article Title",
            description = "Article Summary"
        )
        val item = itemDto.toNewItem()

        // when
        val newItem = itemRepository.save(item)
        val actual = itemRepository.findById(newItem.id)

        // then
        with(actual) {
            assertAll(
                { assertEquals(newItem.id, this?.id) },
                { assertEquals(newItem.name, this?.name) },
                { assertEquals(newItem.createdAt.truncatedTo(ChronoUnit.MILLIS), this?.let { createdAt.truncatedTo(ChronoUnit.MILLIS) }) }
            )
        }
    }
}
