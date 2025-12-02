package net.zeotrope.item.repository

import kotlinx.coroutines.flow.Flow
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : CoroutineCrudRepository<Item, Long> {
    suspend fun findByStatus(status: ItemStatus): Flow<Item>
}
