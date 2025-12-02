package net.zeotrope.item.model

import net.zeotrope.item.domain.ItemStatus

data class ItemDto(val name: String, val status: ItemStatus, val description: String)
