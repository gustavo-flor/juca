package com.github.gustavoflor.juca.entrypoint.web.v1.response

data class ErrorResponse(
    val code: Code,
    val message: String
) {
    enum class Code {
        INVALID_REQUEST,
        RESOURCE_NOT_FOUND,
        INTERNAL_SERVER_ERROR;
    }
}
