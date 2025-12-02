package net.zeotrope.item.domain

enum class ItemStatus {
    CURRENT,
    DISCONTINUED
}

fun ItemStatus.sortOrder(): Int = when (this) {
    ItemStatus.CURRENT -> 1
    ItemStatus.DISCONTINUED -> 2
}
