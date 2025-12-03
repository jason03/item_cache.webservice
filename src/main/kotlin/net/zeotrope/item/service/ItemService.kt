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
import net.zeotrope.item.repository.ItemRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@CacheConfig(cacheNames = ["items"])
@Service
class ItemService(private val itemRepository: ItemRepository) {

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

    @Cacheable(cacheNames = [CACHE_NAME], key = "#id")
    fun getReactive(id: Long): Mono<Item> = itemRepository.findById(id)

    @CachePut(cacheNames = [CACHE_NAME], key = "#id")
    fun updateReactive(id: Long, item: ItemDto): Mono<Item> = itemRepository.findById(id).flatMap { itemRepository.save(item.toUpdateItem(it)) }

    @CachePut(cacheNames = [CACHE_NAME], key = "#id", value = ["items"])
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

    @CacheEvict(cacheNames = [CACHE_NAME], key = "#id")
    fun deleteReactive(id: Long): Mono<Void> = itemRepository.findById(id).flatMap { item -> itemRepository.delete(item) }
}
