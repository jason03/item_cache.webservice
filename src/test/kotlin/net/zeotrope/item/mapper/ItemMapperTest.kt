package net.zeotrope.item.mapper

import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.model.ItemDto
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.assertEquals

class ItemMapperTest {

    companion object {
        @JvmStatic
        fun itemDtoToDomain(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Named.of(
                    "Article Title",
                    ItemDto(
                        status = ItemStatus.CURRENT,
                        name = "Article Title",
                        description = "Article Summary"
                    )
                )
            ),
            Arguments.of(
                Named.of(
                    "Article Title Two",
                    ItemDto(
                        status = ItemStatus.CURRENT,
                        name = "Article Title Two",
                        description = "Article Summary Two"
                    )
                )
            )
        )
    }

    @ParameterizedTest(name = "should map item created DTO to item domain object with title: {0}")
    @MethodSource("itemDtoToDomain")
    fun `should map item DTO to domain`(dto: ItemDto) {
        // given
        val baseDateTime = LocalDateTime.now()
        // when
        val actual = dto.toNewItem()

        // then
        assertAll(
            { assertEquals(0, actual.id) },
            { assertEquals(dto.status, actual.status) },
            { assertEquals(dto.name, actual.name) },
            { assertEquals(dto.description, actual.summary) },
            { assertTrue(actual.createdAt.isAfter(baseDateTime)) },
            { assertTrue(actual.lastModifiedAt == actual.createdAt) }
        )
    }

    @Test
    fun `should map item DTO to domain when updating existing item`() {
        // given
        val baseDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        val existingItem = Item(
            id = 1234567890,
            status = ItemStatus.CURRENT,
            name = "Article Title One",
            summary = "Article Summary One",
            createdAt = baseDateTime,
            lastModifiedAt = baseDateTime
        )
        val item = ItemDto(
            status = ItemStatus.CURRENT,
            name = "Article Title One",
            description = "Article Summary"
        )
        // when
        val actual = item.toUpdateItem(existingItem)
        // then
        assertAll(
            { assertEquals(existingItem.id, actual.id) },
            { assertEquals(item.status, actual.status) },
            { assertEquals(item.name, actual.name) },
            { assertEquals(item.description, actual.summary) },
            { assertTrue(actual.createdAt == existingItem.createdAt) },
            { assertTrue(actual.lastModifiedAt.isAfter(existingItem.lastModifiedAt)) }
        )
    }

    @CsvSource(
        "DISCONTINUED, true",
        "CURRENT, false"
    )
    @ParameterizedTest(name = "should update item status to {0} and discontinuedAt populated {1}")
    fun `should update item to new item status and correctly set discontinuedAt datetime`(status: ItemStatus, hasDiscontinuedAt: Boolean) {
        // given
        val createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0)

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
        val actual = item.toUpdateItemStatus(status)
        // then
        assertAll(
            { assertEquals(status, actual.status) },
            {
                assertTrue(
                    when (hasDiscontinuedAt) {
                        false -> actual.discontinuedAt == null
                        true -> actual.discontinuedAt?.let { it.isAfter(createdDate) } ?: false
                    }
                )
            }
        )
    }
}
