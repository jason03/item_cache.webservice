package net.zeotrope.item

import net.zeotrope.item.util.TestServiceContainers
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class ItemServiceApplicationTest(@Autowired private val applicationContext: ApplicationContext) : TestServiceContainers() {

    @Test
    fun contextLoads() {
        assertNotNull(applicationContext)
    }
}
