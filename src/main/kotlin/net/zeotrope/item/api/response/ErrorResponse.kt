package net.zeotrope.item.api.response

import java.time.Instant

sealed interface ErrorResponse

data class GenericErrorResponse(
    val timestamp: Instant,
    val status: Int,
    val error: String,
    val message: String
) : ErrorResponse
