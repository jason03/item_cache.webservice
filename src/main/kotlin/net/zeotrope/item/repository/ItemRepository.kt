package net.zeotrope.item.repository

import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ItemRepository : ReactiveCrudRepository<Item, Long> {
    fun findByStatus(status: ItemStatus): Flux<Item>
}
