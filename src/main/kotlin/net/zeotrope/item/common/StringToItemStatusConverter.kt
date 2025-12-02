package net.zeotrope.item.common

import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.exceptions.InvalidStatusException
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToItemStatusConverter : Converter<String, ItemStatus> {
    override fun convert(source: String): ItemStatus? = ItemStatus.entries.find { it.name.equals(source, ignoreCase = true) }.apply {
        if (this == null) throw InvalidStatusException(source)
    }
}
