package net.zeotrope.item.api.resource

import kotlinx.coroutines.test.runTest
import net.zeotrope.item.TestcontainersConfiguration
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.model.ItemDto
import net.zeotrope.item.util.TestServiceContainers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import reactor.test.StepVerifier
import java.time.LocalDateTime

@Import(TestcontainersConfiguration::class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemIntegrationTest : TestServiceContainers() {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Order(1)
    @Test
    fun `should return 200 and when get all items`() = runTest {
        // given
        // when
        // then
        webTestClient.get()
            .uri("/api/v1/items")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Item::class.java)
            .value<ListBodySpec<Item>> { items ->
                val countByStatus = items.map { it.status }.groupingBy { it }.eachCount()
                assertAll(
                    { assertEquals(5, countByStatus[ItemStatus.CURRENT]) },
                    { assertEquals(1, countByStatus[ItemStatus.DISCONTINUED]) }
                )
            }
    }

    @Order(2)
    @Test
    fun `should return 200 and get item by id`() {
        // given
        val itemId = 1L
        val testItem1 = Item(
            id = 1,
            status = ItemStatus.CURRENT,
            name = "Item 1",
            summary = "Item 1 summary",
            createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0),
            lastModifiedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0),
            discontinuedAt = null
        )
        // when
        val actual = webTestClient.get()
            .uri("/api/v1/items/$itemId")
            .exchange()
            .expectStatus().isOk
            .returnResult(Item::class.java)

        // then
        StepVerifier.create(actual.responseBody)
            .assertNext {
                assertEquals(testItem1, it)
            }
            .verifyComplete()
    }

    @Order(3)
    @Test
    fun `should return 404 when get item by invalid id`() {
        // given
        val itemId = 100L

        // when
        // then
        webTestClient.get()
            .uri("/api/v1/items/$itemId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Item not found for request: /api/v1/items/$itemId")
    }

    @Order(4)
    @Test
    fun `should create an item into the test data db and return 201 with id 7`() {
        // given
        val item = ItemDto(
            name = "New Test Item",
            status = ItemStatus.CURRENT,
            description = "New Test Item Description"
        )

        // when
        // then
        webTestClient.post()
            .uri("/api/v1/items")
            .bodyValue(item)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isEqualTo(7)
    }

    @Order(5)
    @Test
    fun `should update an item into the test data db and return 204`() {
        // given
        val item = ItemDto(
            name = "New Test Item Two",
            status = ItemStatus.CURRENT,
            description = "New Test Item Two Description"
        )
        val updatedDescription = "New Test Item Two with a longer description"
        val updatedItem = ItemDto(
            name = "New Test Item Two",
            status = ItemStatus.CURRENT,
            description = updatedDescription
        )

        // when
        // then
        webTestClient.post()
            .uri("/api/v1/items")
            .bodyValue(item)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isEqualTo(7)
        webTestClient.put()
            .uri("/api/v1/items/7")
            .bodyValue(updatedItem)
            .exchange()
            .expectStatus().isNoContent
    }

    @Order(6)
    @Test
    fun `should update an item's status to DISCONTINUED and set the discontinuedAt date`() {
        // given
        // when
        // then
        webTestClient.put()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/v1/items/1")
                    .queryParam("status", "discontinued")
                    .build()
            }
            .exchange()
            .expectStatus().isNoContent
            .expectBody()
            .jsonPath("$.discontinuedAt").isNotEmpty
            .jsonPath("$.status").isEqualTo("DISCONTINUED")
    }

    @Order(7)
    @Test
    fun `should delete an item with an invalid id and return 204`() {
        // given
        // when
        // then
        webTestClient.delete()
            .uri("/api/v1/items/100")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }

    @Order(7)
    @Test
    fun `should delete an item with an valid id and return 204 and confirm with get returning a 404`() {
        // given
        // when
        // then
        webTestClient.delete()
            .uri("/api/v1/items/1")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
        webTestClient.get()
            .uri("/api/v1/items/1")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message")
            .isEqualTo("Item not found for request: /api/v1/items/1")
    }
}
