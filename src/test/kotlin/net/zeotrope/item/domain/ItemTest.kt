package net.zeotrope.item.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime

class ItemTest {

    @Test
    fun `should return a list of item statues in order when sort order is asc`() {
        // given
        val preSortedStatuses = listOf(
            ItemStatus.DISCONTINUED,
            ItemStatus.CURRENT,
            ItemStatus.DISCONTINUED,
            ItemStatus.CURRENT
        )
        val expectedStatuses = listOf(
            ItemStatus.CURRENT,
            ItemStatus.CURRENT,
            ItemStatus.DISCONTINUED,
            ItemStatus.DISCONTINUED
        )

        // when
        val actual = preSortedStatuses.sortedBy { it.sortOrder() }

        // then
        assert(actual == expectedStatuses)
    }

    @Test
    fun `should return a list of items sorted by status and created date order`() {
        // given
        val dateTime1 = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        val dateTime2 = LocalDateTime.of(2025, 1, 14, 0, 0, 0)
        val dateTime3 = LocalDateTime.of(2025, 1, 14, 2, 0, 0)
        val dateTime4 = LocalDateTime.of(2025, 2, 7, 12, 0, 0)
        val dateTime5 = LocalDateTime.of(2025, 2, 14, 15, 0, 0)
        val discontinuedDateTime = LocalDateTime.of(2025, 6, 1, 0, 0, 0, 0)
        val items = listOf<Item>(
            Item(
                id = 1234,
                status = ItemStatus.CURRENT,
                name = "Article Title",
                summary = "Article Summary",
                createdAt = dateTime1,
                lastModifiedAt = dateTime1,
                discontinuedAt = null
            ),
            Item(
                id = 1235,
                status = ItemStatus.DISCONTINUED,
                name = "Article Title Two",
                summary = "Article Summary Two",
                createdAt = dateTime5,
                lastModifiedAt = dateTime5,
                discontinuedAt = discontinuedDateTime
            ),
            Item(
                id = 1236,
                status = ItemStatus.CURRENT,
                name = "Article Title Three",
                summary = "Article Summary Three",
                createdAt = dateTime3,
                lastModifiedAt = dateTime3,
                discontinuedAt = null
            ),
            Item(
                id = 1237,
                status = ItemStatus.DISCONTINUED,
                name = "Article Title Four",
                summary = "Article Summary Four",
                createdAt = dateTime4,
                lastModifiedAt = dateTime4,
                discontinuedAt = discontinuedDateTime
            ),
            Item(
                id = 20001,
                status = ItemStatus.CURRENT,
                name = "Article Title Five",
                summary = "Article Summary Five",
                createdAt = dateTime2,
                lastModifiedAt = dateTime2
            )
        )

        // when
        val actual = items.sortedWith(statusCreatedSort)
        // then
        assertAll(
            { assertEquals(items.size, actual.size) },
            { assertEquals(1234, actual[0].id) },
            { assertEquals(ItemStatus.CURRENT, actual[0].status) },
            { assertEquals(20001, actual[1].id) },
            { assertEquals(ItemStatus.CURRENT, actual[1].status) },
            { assertEquals(1236, actual[2].id) },
            { assertEquals(ItemStatus.CURRENT, actual[2].status) },
            { assertEquals(1237, actual[3].id) },
            { assertEquals(ItemStatus.DISCONTINUED, actual[3].status) },
            { assertEquals(1235, actual[4].id) },
            { assertEquals(ItemStatus.DISCONTINUED, actual[4].status) }
        )
    }
}
