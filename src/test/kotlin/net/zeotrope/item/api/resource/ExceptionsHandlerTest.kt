package net.zeotrope.item.api.resource

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import net.zeotrope.item.exceptions.InvalidStatusException
import net.zeotrope.item.service.ItemService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClientResponseException.InternalServerError
import org.springframework.web.reactive.resource.NoResourceFoundException

@WebFluxTest(
    value = [
        ItemController::class,
        CustomExceptionsHandler::class,
        DefaultExceptionHandler::class
    ]
)
@ActiveProfiles("test")
class ExceptionsHandlerTest {

    @MockkBean
    private lateinit var itemService: ItemService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `should handle an internal server error exception`() = runTest {
        // given
        val uri = "/api/v1/items/1234567890"
        // when
        coEvery { itemService.get(any()) } throws InternalServerError.create(
            500,
            "Test Error",
            HttpHeaders.EMPTY,
            byteArrayOf(),
            null
        )
        // then
        webTestClient.get()
            .uri("$uri")
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody()
            .jsonPath("$.message").isEqualTo("Internal Server Error for request: $uri")
    }

    @Test
    fun `should handle a bad gateway exception`() = runTest {
        // given
        val uri = "/api/v1/items/1234567890"
        // when
        coEvery { itemService.get(any()) } throws InternalServerError.create(
            502,
            "Test Error",
            HttpHeaders.EMPTY,
            byteArrayOf(),
            null
        )
        // then
        webTestClient.get()
            .uri("$uri")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Bad Gateway for request: $uri")
    }

    @Test
    fun `should handle a no resource found exception`() = runTest {
        // given
        val uri = "/api/v1/items"
        // when
        coEvery { itemService.getAllItems() } throws NoResourceFoundException(uri)
        // then
        webTestClient.get()
            .uri("$uri")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo(
                "Resource Not Found for request: /api/v1/items"
            )
    }

    @Test
    fun `should handle an invalid status exception`() = runTest {
        // given
        val invalidStatus = "invalid"
        val uri = "/api/v1/items"
        // when
        coEvery { itemService.getAllItems() } throws InvalidStatusException(invalidStatus)
        // then
        webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path(uri)
                    .queryParam("status", invalidStatus)
                    .build()
            }.exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.message").isEqualTo(
                "Item request is invalid: /api/v1/items"
            )
    }

    @Test
    fun `should handle a generic exception`() = runTest {
        // given
        val uri = "/api/v1/items/123456"
        // when
        coEvery { itemService.get(any()) } throws Exception(uri)
        // then
        webTestClient.get()
            .uri("$uri")
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody()
            .jsonPath("$.message").isEqualTo("Internal server error for request: $uri")
    }
}
