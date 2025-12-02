package net.zeotrope.item.service

import kotlinx.coroutines.flow.Flow
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.exceptions.ItemNotFoundException
import net.zeotrope.item.mapper.toNewItem
import net.zeotrope.item.mapper.toUpdateItem
import net.zeotrope.item.model.ItemDto
import net.zeotrope.item.repository.ItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService(@Autowired private val itemRepository: ItemRepository) {

    companion object {
        const val CACHE_NAME = "items"
    }

    @Cacheable(cacheNames = [CACHE_NAME], key = "#id")
    suspend fun get(id: Long): Item = itemRepository.findById(id) ?: throw ItemNotFoundException("Item with id $id not found")

    @Transactional
    @CachePut(cacheNames = [CACHE_NAME], key = "#id")
    suspend fun update(id: Long, item: ItemDto) = itemRepository.findById(id)?.let {
        itemRepository.save(item.toUpdateItem(it))
    } ?: throw ItemNotFoundException("Item with id $id not found")

    @Transactional
    @CachePut(cacheNames = [CACHE_NAME], key = "#id")
    suspend fun updateItemStatus(id: Long, status: ItemStatus) = itemRepository.findById(id)?.let {
        itemRepository.save(it.copy(status = status))
    } ?: throw ItemNotFoundException("Item with id $id not found")

    @Transactional
    @CacheEvict(cacheNames = [CACHE_NAME], key = "#id")
    suspend fun delete(id: Long) = itemRepository.findById(id)?.let {
        itemRepository.delete(it)
    } ?: throw ItemNotFoundException("Item with id $id not found")

    suspend fun getAllItems(status: ItemStatus? = null): Flow<Item> = status?.let {
        itemRepository.findByStatus(status)
    } ?: itemRepository.findAll()

    @Transactional
    suspend fun createItem(item: ItemDto): Item = itemRepository.save(item.toNewItem())
}
