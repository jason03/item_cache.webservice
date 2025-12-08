package net.zeotrope.item.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.exceptions.ItemNotFoundException
import net.zeotrope.item.model.ItemDto
import net.zeotrope.item.repository.ItemCacheRepository
import net.zeotrope.item.repository.ItemRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest(
    classes = [
        ItemService::class,
        ItemCacheRepository::class,
        ItemRepository::class
    ]
)
class ItemServiceTest(@Autowired private val itemService: ItemService) {

    @MockkBean
    private lateinit var itemRepository: ItemRepository

    @MockkBean
    private lateinit var itemCacheRepository: ItemCacheRepository

    private val createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0)
    private val modifiedDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0)

    private val items = listOf<Item>(
        Item(
            id = 1,
            status = ItemStatus.CURRENT,
            name = "Article Title One",
            summary = "Article Summary One",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        ),
        Item(
            id = 2,
            status = ItemStatus.CURRENT,
            name = "Article Title Two",
            summary = "Article Summary Two",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        ),
        Item(
            id = 3,
            status = ItemStatus.CURRENT,
            name = "Article Title Three",
            summary = "Article Summary Three",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        )
    )

    private val itemDto = ItemDto(
        status = ItemStatus.CURRENT,
        name = "Article Title Ten",
        description = "Article Summary Ten"
    )

    @Test
    fun `should return an item from the repository`() = runTest {
        // given
        val item = Item(
            id = 1,
            status = ItemStatus.CURRENT,
            name = "Article Title Ten",
            summary = "Article Summary Ten",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        )
        // when
        coEvery { itemCacheRepository.get(any(Long::class)) } returns Mono.empty()
        coEvery { itemCacheRepository.put(any(), any()) } returns Mono.just(true)
        coEvery { itemCacheRepository.cacheTtl } returns 1000L
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.just(item)

        val actual = itemService.get(1)
        // then
        assertEquals(item, actual)
        coVerify(exactly = 1) { itemRepository.findById(any(Long::class)) }
    }

    @Test
    fun `should throw an item not found exception when failing to retrieve item by id`() = runTest {
        // given
        // when
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.empty()
        coEvery { itemCacheRepository.get(any(Long::class)) } returns Mono.empty()
        // then
        assertThrows<ItemNotFoundException> {
            itemService.get(1)
        }.also {
            assertEquals("Item with id 1 not found", it.message)
        }
    }

    @Test
    fun `should create a new item`() = runTest {
        // given
        val itemDto = ItemDto(
            status = ItemStatus.CURRENT,
            name = "Article Title Twenty",
            description = "Article Summary Twenty"
        )
        val expectedItem = Item(
            status = ItemStatus.CURRENT,
            name = "Article Title Twenty",
            summary = "Article Summary Twenty",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        )

        // when
        coEvery { itemRepository.save(any(Item::class)) } returns Mono.just(expectedItem)
        val actual = itemService.createItem(itemDto)
        // then
        assertEquals(expectedItem, actual)
        coVerify(exactly = 1) { itemRepository.save(any(Item::class)) }
    }

    @Test
    fun `should return a list of items`() = runTest {
        // given
        // when
        coEvery { itemRepository.findAll() } returns Flux.fromIterable(items)
        // then
        val actual = itemService.getAllItems()

        assertEquals(items, actual.toList())
        coVerify(exactly = 1) { itemRepository.findAll() }
    }

    @Test
    fun `should return a list of items filtered by status`() = runTest {
        // given
        // when
        coEvery { itemRepository.findByStatus(ItemStatus.CURRENT) } returns Flux.fromIterable(items)
        val actual = itemService.getAllItems(ItemStatus.CURRENT)

        // then
        assertEquals(items, actual.toList())
        coVerify(exactly = 1) { itemRepository.findByStatus(any(ItemStatus::class)) }
    }

    @Test
    fun `should delete an existing item`() = runTest {
        // given
        val item = Item(
            id = 1,
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        )
        // when
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.just(item)
        coEvery { itemRepository.deleteById(any(Long::class)) } returns Mono.empty()
        coEvery { itemCacheRepository.evict(any()) } returns Mono.just(true)

        itemService.delete(1)

        // then
        coVerify(exactly = 1) { itemRepository.findById(any(Long::class)) }
        coVerify(exactly = 1) { itemRepository.deleteById(any(Long::class)) }
        coVerify(exactly = 1) { itemCacheRepository.evict(any(Long::class)) }
    }

    @Test
    fun `should not error or call repository and cache delete when deleting item with invalid Id`() = runTest {
        // given
        val item = Item(
            id = 1,
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        )
        // when
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.empty()

        // then
        itemService.delete(1)

        coVerify(exactly = 1) { itemRepository.findById(any(Long::class)) }
        coVerify(exactly = 0) { itemRepository.delete(any()) }
        coVerify(exactly = 0) { itemCacheRepository.evict(any()) }
    }

    @Test
    fun `should throw an item not found exception when updating item with invalid Id`() = runTest {
        // given
        // when
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.empty()
        // then
        assertThrows<ItemNotFoundException> {
            itemService.update(1, itemDto)
        }.also {
            assertEquals("Item with id 1 not found", it.message)
        }
        coVerify(exactly = 1) { itemRepository.findById(any(Long::class)) }
        coVerify(exactly = 0) { itemRepository.save(any(Item::class)) }
    }

    @Test
    fun `should update an existing item`() = runTest {
        // given
        val item = Item(
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        )

        // when
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.just(item)
        coEvery { itemRepository.save(any(Item::class)) } returns Mono.just(item)
        coEvery { itemCacheRepository.cacheTtl } returns 300L
        coEvery { itemCacheRepository.put(any(Item::class), any(Duration::class)) } returns Mono.just(true)

        itemService.update(1, itemDto)
        // then
        coVerify(exactly = 1) { itemRepository.findById(any(Long::class)) }
        coVerify(exactly = 1) { itemRepository.save(any(Item::class)) }
        coVerify(exactly = 1) { itemCacheRepository.cacheTtl }
        coVerify(exactly = 1) { itemCacheRepository.put(any(Item::class), any(Duration::class)) }
    }

    @Test
    fun `should throw a item not found exception when updating item with invalid id`() = runTest {
        // given
        // when
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.empty()
        assertThrows<ItemNotFoundException> {
            itemService.updateItemStatus(1, ItemStatus.DISCONTINUED)
        }.also {
            assertEquals("Item with id 1 not found", it.message)
        }
        // then
        coVerify(exactly = 1) { itemRepository.findById(any(Long::class)) }
        coVerify(exactly = 0) { itemRepository.save(any(Item::class)) }
    }

    @Test
    fun `should update an existing item's status`() = runTest {
        // given
        val newItemStatus = ItemStatus.DISCONTINUED
        val item = Item(
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = modifiedDate,
            discontinuedAt = null
        )
        // when
        coEvery { itemRepository.findById(any(Long::class)) } returns Mono.just(item)
        coEvery { itemRepository.save(any(Item::class)) } returns Mono.just(item.copy(status = newItemStatus))
        coEvery { itemCacheRepository.cacheTtl } returns 300L
        coEvery { itemCacheRepository.put(any(Item::class), any(Duration::class)) } returns Mono.just(true)

        val actual = itemService.updateItemStatus(1, newItemStatus)
        // then
        assertEquals(newItemStatus, actual.status)
        coVerify(exactly = 1) { itemRepository.findById(any(Long::class)) }
        coVerify(exactly = 1) { itemRepository.save(any(Item::class)) }
        coVerify(exactly = 1) { itemCacheRepository.cacheTtl }
        coVerify(exactly = 1) { itemCacheRepository.put(any(Item::class), any(Duration::class)) }
    }
}
