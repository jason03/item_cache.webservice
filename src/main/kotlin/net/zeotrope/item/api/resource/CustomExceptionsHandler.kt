package net.zeotrope.item.api.resource

import net.zeotrope.item.api.response.ErrorResponse
import net.zeotrope.item.api.response.GenericErrorResponse
import net.zeotrope.item.exceptions.InvalidStatusException
import net.zeotrope.item.exceptions.ItemNotFoundException
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.client.WebClientResponseException.BadGateway
import org.springframework.web.reactive.function.client.WebClientResponseException.InternalServerError
import org.springframework.web.reactive.resource.NoResourceFoundException
import java.time.Instant

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class CustomExceptionsHandler : Logging {

    @ExceptionHandler(InternalServerError::class)
    fun handleInternalServerError(exception: InternalServerError, request: ServerHttpRequest): ResponseEntity<ErrorResponse> = ResponseEntity(
        GenericErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Internal Server Error for request: ${request.path}"
        ),
        HttpStatus.INTERNAL_SERVER_ERROR
    )

    @ExceptionHandler(BadGateway::class)
    fun handleBadGatewayException(exception: BadGateway, request: ServerHttpRequest): ResponseEntity<ErrorResponse> = ResponseEntity(
        GenericErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_GATEWAY.value(),
            error = HttpStatus.BAD_GATEWAY.reasonPhrase,
            message = "Bad Gateway for request: ${request.path}"
        ),
        HttpStatus.BAD_GATEWAY
    )

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(exception: NoResourceFoundException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> = ResponseEntity(
        GenericErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = "Resource Not Found for request: ${request.path}"
        ),
        HttpStatus.NOT_FOUND
    )

    @ExceptionHandler(ItemNotFoundException::class)
    fun handleItemNotFoundException(exception: ItemNotFoundException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> = ResponseEntity(
        GenericErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = "Item not found for request: ${request.path}"
        ),
        HttpStatus.NOT_FOUND
    )

    @ExceptionHandler(InvalidStatusException::class)
    fun handleInvalidStatusException(exception: InvalidStatusException, request: ServerHttpRequest): ResponseEntity<ErrorResponse> = ResponseEntity(
        GenericErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Item request is invalid: ${request.path}"
        ),
        HttpStatus.BAD_REQUEST
    )
}

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class DefaultExceptionHandler : Logging {

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: ServerHttpRequest): ResponseEntity<ErrorResponse> = ResponseEntity(
        GenericErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Internal server error for request: ${request.path}"
        ),
        HttpStatus.INTERNAL_SERVER_ERROR
    )
}
