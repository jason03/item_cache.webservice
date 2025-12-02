package net.zeotrope.item.common

import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.exceptions.InvalidStatusException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@ActiveProfiles("test")
@SpringBootTest(
    classes = [
        StringToItemStatusConverter::class
    ]
)
class StringToItemStatusConverterTest {

    @Autowired
    private lateinit var converterUnderTest: StringToItemStatusConverter

    @CsvSource(
        value = [
            "current, CURRENT",
            "Current, CURRENT",
            "disContinued, DISCONTINUED"
        ]
    )
    @ParameterizedTest(name = "should convert string {0} to item status {1}")
    fun `should convert string to supported post status`(status: String, expected: ItemStatus) {
        // given
        // when
        val actual = converterUnderTest.convert(status)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `should throw an invalid status exception for an invalid item status`() {
        // given
        // when
        // then
        assertThrows<InvalidStatusException> {
            converterUnderTest.convert("OUTDATED")
        }.also {
            assertEquals("OUTDATED", it.message)
        }
    }
}
