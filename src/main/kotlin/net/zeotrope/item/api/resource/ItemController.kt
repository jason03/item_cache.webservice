package net.zeotrope.item.api.resource

import kotlinx.coroutines.flow.Flow
import net.zeotrope.item.domain.Item
import net.zeotrope.item.domain.ItemStatus
import net.zeotrope.item.model.ItemDto
import net.zeotrope.item.service.ItemService
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/v1"],
    produces = ["application/json"]
)
class ItemController(private val itemService: ItemService) {

    @GetMapping(value = ["/items"])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAllItems(@RequestParam(name = "status")status: ItemStatus? = null): ResponseEntity<Flow<Item>> =
        ResponseEntity.ok(itemService.getAllItems(status))

    @GetMapping(value = ["/items/{id}"])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getItem(@PathVariable id: Long): ResponseEntity<Any> = ResponseEntity.ok(itemService.get(id))

    @PostMapping(value = ["/items"])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createItem(@RequestBody item: ItemDto): ResponseEntity<Item> = ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(item))

    @PutMapping(value = ["/items/{id}"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updateItem(@PathVariable id: Long, @NotBlank @RequestBody item: ItemDto): ResponseEntity<Item> =
        ResponseEntity.status(HttpStatus.NO_CONTENT).body(itemService.update(id, item))

    @PutMapping(
        value = ["/items/{id}"],
        params = ["status"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updateItemStatus(@PathVariable id: Long, @RequestParam(name = "status") status: ItemStatus): ResponseEntity<Item> =
        ResponseEntity.status(HttpStatus.NO_CONTENT).body(itemService.updateItemStatus(id, status))

    @DeleteMapping(value = ["/items/{id}"])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteItem(@PathVariable id: Long): ResponseEntity<Unit> = ResponseEntity.status(HttpStatus.NO_CONTENT).body(itemService.delete(id))
}
