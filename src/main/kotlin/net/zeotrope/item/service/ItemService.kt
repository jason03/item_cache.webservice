package net.zeotrope.item.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.exceptions.ItemNotFoundException
import net.zeotrope.item.mapper.toNewItem
import net.zeotrope.item.mapper.toUpdateItem
import net.zeotrope.item.model.ItemDto
import net.zeotrope.item.repository.ItemCacheRepository
import net.zeotrope.item.repository.ItemRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@CacheConfig(cacheNames = ["items"])
@Service
class ItemService(private val itemRepository: ItemRepository, private val itemCacheRepository: ItemCacheRepository) {

    companion object {
        const val CACHE_NAME = "items"
    }

    @Transactional
    suspend fun getAllItems(status: ItemStatus? = null): Flow<Item> = getAllItemsReactive(status).asFlow()

    @Transactional
    suspend fun get(id: Long): Item = getReactive(id).awaitFirstOrNull() ?: throw ItemNotFoundException("Item with id $id not found")

    @Transactional
    suspend fun update(id: Long, item: ItemDto): Item = updateReactive(id, item).awaitFirstOrNull() ?: throw ItemNotFoundException("Item with id $id not found")

    @Transactional
    suspend fun updateItemStatus(id: Long, status: ItemStatus): Item = updateItemStatusReactive(id, status).awaitFirstOrNull()
        ?: throw ItemNotFoundException("Item with id $id not found")

    @Transactional
    suspend fun delete(id: Long) = deleteReactive(id).awaitFirstOrNull() // { throw ItemNotFoundException("Item with id $id not found") }

    @Transactional
    suspend fun createItem(item: ItemDto): Item = itemRepository.save(item.toNewItem()).awaitSingle()

    fun getAllItemsReactive(status: ItemStatus? = null): Flux<Item> = status?.let {
        itemRepository.findByStatus(status)
    } ?: itemRepository.findAll()

    fun getReactive(id: Long): Mono<Item> = itemCacheRepository.get(id).switchIfEmpty {
        itemRepository.findById(id).log().flatMap { item ->
            itemCacheRepository.put(item).log().thenReturn(item)
        }
    }

    fun updateReactive(id: Long, item: ItemDto): Mono<Item> = itemRepository.findById(id).flatMap { itemRepository.save(item.toUpdateItem(it)) }

    fun updateItemStatusReactive(id: Long, status: ItemStatus): Mono<Item> = itemRepository.findById(id).flatMap {
        itemRepository.save(
            it.copy(
                status = status,
                lastModifiedAt = LocalDateTime.now(),
                discontinuedAt = when (status) {
                    ItemStatus.DISCONTINUED -> LocalDateTime.now()
                    else -> null
                }
            )
        )
    }

    fun deleteReactive(id: Long): Mono<Void> = itemRepository.findById(id).flatMap { item ->
        itemCacheRepository.evict(id)
        itemRepository.delete(item)
    }
}
