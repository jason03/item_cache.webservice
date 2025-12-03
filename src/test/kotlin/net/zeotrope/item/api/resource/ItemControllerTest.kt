package net.zeotrope.item.api.resource

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.exceptions.ItemNotFoundException
import net.zeotrope.item.mapper.toNewItem
import net.zeotrope.item.model.ItemDto
import net.zeotrope.item.service.ItemService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@ActiveProfiles("test")
@WebFluxTest(
    value = [
        ItemController::class,
        CustomExceptionsHandler::class
    ]
)
class ItemControllerTest {

    @MockkBean
    private lateinit var itemService: ItemService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private val createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    private val discontinuedDate = LocalDateTime.of(2025, 6, 30, 0, 0, 0)

    @Test
    fun `should return 200 when get all items`() = runTest {
        // give
        val items = listOf(
            Item(
                id = 1234567890,
                status = ItemStatus.CURRENT,
                name = "Article Title One",
                summary = "Article Summary One",
                createdAt = createdDate,
                lastModifiedAt = createdDate,
                discontinuedAt = null
            ),
            Item(
                id = 1234567891,
                status = ItemStatus.DISCONTINUED,
                name = "Article Title Two",
                summary = "Article Summary Two",
                createdAt = createdDate,
                lastModifiedAt = createdDate,
                discontinuedAt = discontinuedDate
            ),
            Item(
                id = 1234567892,
                status = ItemStatus.CURRENT,
                name = "Article Title Three",
                summary = "Article Summary Three",
                createdAt = createdDate,
                lastModifiedAt = createdDate,
                discontinuedAt = null
            )
        )

        // when
        coEvery { itemService.getAllItems() } returns items.asFlow()

        // then
        webTestClient.get()
            .uri("/api/v1/items")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(3)
            .jsonPath("$[0].id").isEqualTo(1234567890)
            .jsonPath("$[1].id").isEqualTo(1234567891)
            .jsonPath("$[2].id").isEqualTo(1234567892)

        coVerify(exactly = 1) { itemService.getAllItems() }
    }

    @Test
    fun `should return 200 when get all items filtered by status`() = runTest {
        // given
        val items = listOf(
            Item(
                id = 1234567891,
                status = ItemStatus.CURRENT,
                name = "Article Title Two",
                summary = "Article Summary Two",
                createdAt = createdDate,
                lastModifiedAt = createdDate,
                discontinuedAt = null
            )
        )

        // when
        coEvery { itemService.getAllItems(any(ItemStatus::class)) } returns items.asFlow()

        // then
        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/items")
                    .queryParam("status", "current")
                    .build()
            }.exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isEqualTo(1234567891)

        coVerify(exactly = 1) { itemService.getAllItems(any(ItemStatus::class)) }
    }

    @Test
    fun `should return 200 when get item by id`() = runTest {
        // given
        val item = Item(
            id = 1234567890,
            status = ItemStatus.CURRENT,
            name = "Article Title One",
            summary = "Article Summary One",
            createdAt = createdDate,
            lastModifiedAt = createdDate,
            discontinuedAt = null
        )
        // when
        coEvery { itemService.get(any()) } returns item
        // then
        webTestClient.get()
            .uri("/api/v1/items/1234567890")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").isEqualTo(7)
            .jsonPath("$.id").isEqualTo("1234567890")
            .jsonPath("$.status").isEqualTo("CURRENT")
            .jsonPath("$.name").isEqualTo("Article Title One")
            .jsonPath("$.summary").isEqualTo("Article Summary One")
            .jsonPath("$.createdAt").isEqualTo("2025-01-01T00:00:00")
            .jsonPath("$.lastModifiedAt").isEqualTo("2025-01-01T00:00:00")
            .jsonPath("$.discontinuedAt").isEmpty()

        coVerify(exactly = 1) { itemService.get(any()) }
    }

    @Test
    fun `should throw exception when item not found when getting item by id`() = runTest {
        // given
        // when
        coEvery { itemService.get(any()) } throws ItemNotFoundException("Test Error")
        // then
        webTestClient.get()
            .uri("/api/v1/items/1234567890")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Item not found for request: /api/v1/items/1234567890")

        coVerify(exactly = 1) { itemService.get(any()) }
    }

    @Test
    fun `should return 201 when create item`() = runTest {
        // given
        val item = ItemDto(
            name = "Article Title",
            status = ItemStatus.CURRENT,
            description = "Article Summary"
        )
        // when
        coEvery { itemService.createItem(any()) } returns item.toNewItem()
        // then
        webTestClient.post()
            .uri("/api/v1/items")
            .bodyValue(item)
            .exchange()
            .expectStatus().isCreated
            .expectBody()

        coVerify(exactly = 1) { itemService.createItem(any()) }
    }

    @Test
    fun `should return 204 when update item`() = runTest {
        // given
        val item = ItemDto(
            name = "Article Title",
            status = ItemStatus.CURRENT,
            description = "Article Summary"
        )
        val updatedItem = Item(
            id = 12345678,
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = createdDate,
            discontinuedAt = null
        )
        // when
        coEvery { itemService.update(any(), any()) } returns updatedItem
        // then
        webTestClient.put()
            .uri("/api/v1/items/1234")
            .bodyValue(item)
            .exchange()
            .expectStatus().isNoContent
            .expectBody()
            .jsonPath("$.length()").isEqualTo(7)

        coVerify(exactly = 1) { itemService.update(any(), any()) }
    }

    @Test
    fun `should throw exception when item not found when updating item`() = runTest {
        // given
        val itemDto = ItemDto(
            name = "Article Title",
            status = ItemStatus.CURRENT,
            description = "Article Summary"
        )
        // when
        coEvery { itemService.update(any(), any()) } throws ItemNotFoundException("Test Error")
        // then
        webTestClient.put()
            .uri("/api/v1/items/1234")
            .bodyValue(itemDto)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Item not found for request: /api/v1/items/1234")

        coVerify(exactly = 1) { itemService.update(any(), any()) }
    }

    @Test
    fun `should return 204 when update item status`() = runTest {
        // given
        val updatedItem = Item(
            id = 12345678,
            status = ItemStatus.CURRENT,
            name = "Article Title",
            summary = "Article Summary",
            createdAt = createdDate,
            lastModifiedAt = createdDate,
            discontinuedAt = null
        )
        // when
        coEvery { itemService.updateItemStatus(any(), any(ItemStatus::class)) } returns updatedItem
        // then
        webTestClient.put()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/items/1234")
                    .queryParam("status", "discontinued")
                    .build()
            }.exchange()
            .expectStatus().isNoContent
            .expectBody()
            .jsonPath("$.length()").isEqualTo(7)
        coVerify(exactly = 1) { itemService.updateItemStatus(any(), any(ItemStatus::class)) }
    }

    @Test
    fun `should return 204 when successful delete item`() = runTest {
        // given
        // when
        coEvery { itemService.delete(any()) } returns null
        // then
        webTestClient.delete()
            .uri("/api/v1/items/1234")
            .exchange()
            .expectStatus().isNoContent
            .expectBody()

        coVerify(exactly = 1) { itemService.delete(any()) }
    }

    @Test
    fun `should throw exception when item not found when deleting item `() = runTest {
        // given
        // when
        coEvery { itemService.delete(any()) } throws ItemNotFoundException("Test Error")
        // then
        webTestClient.delete()
            .uri("/api/v1/items/1234")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Item not found for request: /api/v1/items/1234")
    }
}
