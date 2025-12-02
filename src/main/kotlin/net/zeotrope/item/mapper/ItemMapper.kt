package net.zeotrope.item.mapper

import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.model.ItemDto
import java.time.LocalDateTime

fun ItemDto.toNewItem(): Item = with(this) {
    val dateTime = LocalDateTime.now()
    Item(
        status = status,
        name = name,
        summary = description,
        createdAt = dateTime,
        lastModifiedAt = dateTime,
        discontinuedAt = null
    )
}

fun ItemDto.toUpdateItem(oldItemState: Item): Item = with(this) {
    val modifiedDateTime = LocalDateTime.now()

    Item(
        id = oldItemState.id,
        status = status,
        name = name,
        summary = description,
        createdAt = oldItemState.createdAt,
        lastModifiedAt = modifiedDateTime,
        discontinuedAt = if (this.status == ItemStatus.DISCONTINUED && oldItemState.status != ItemStatus.DISCONTINUED) modifiedDateTime else null
    )
}
