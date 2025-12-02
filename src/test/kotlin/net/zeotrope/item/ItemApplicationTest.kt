package net.zeotrope.item

import org.springframework.boot.fromApplication

fun main(args: Array<String>) {
    val argsWithProfile = arrayOf("--spring.profiles.active=test") + args
    fromApplication<ItemServiceApplication>().with(TestcontainersConfiguration::class.java).run(*argsWithProfile)
}
