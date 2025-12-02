package net.zeotrope.item

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class ItemServiceApplication

fun main(args: Array<String>) {
    runApplication<ItemServiceApplication>(*args)
}
