package net.zeotrope.item.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "items")
data class Item(
    @Id
    val id: Long = 0,
    val status: ItemStatus = ItemStatus.CURRENT,
    val name: String,
    val summary: String,
    @Column("created_at")
    val createdAt: LocalDateTime,
    @Column("last_modified_at")
    val lastModifiedAt: LocalDateTime,
    @Column("discontinued_at")
    val discontinuedAt: LocalDateTime? = null
)

val statusCreatedSort = Comparator<Item> { c1, c2 ->
    compareValuesBy(
        c1,
        c2,
        { it.status.sortOrder() },
        { it.createdAt }
    )
}
