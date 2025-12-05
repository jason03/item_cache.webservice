package net.zeotrope.item.repository

import net.zeotrope.item.domain.Item
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration

@Repository
class ItemCacheRepository(
    private val redisTemplate: ReactiveRedisTemplate<String, Item>,
    @Value("\${cache.redis.ttl:600}")
    val cacheTtl: Long
) {
    private fun cacheKey(id: Long) = "items:$id"

    fun get(id: Long): Mono<Item> = redisTemplate.opsForValue().get(cacheKey(id)).log()

    fun put(item: Item, ttl: Duration = Duration.ofSeconds(cacheTtl)): Mono<Boolean> = redisTemplate.opsForValue().set(cacheKey(item.id), item, ttl).flatMap {
        Mono.just(it)
    }

    fun evict(id: Long): Mono<Boolean> = redisTemplate.opsForValue().delete(cacheKey(id)).log()
}
