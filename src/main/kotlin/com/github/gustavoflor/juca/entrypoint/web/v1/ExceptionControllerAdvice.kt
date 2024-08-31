package com.github.gustavoflor.juca.entrypoint.web.v1

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.entrypoint.web.v1.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.concurrent.TimeoutException

@RestControllerAdvice
class ExceptionControllerAdvice {
    companion object {
        private const val INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong, try again later. If it persists, please check the logs to see the problems"
        private const val INVALID_REQUEST_MESSAGE = "Invalid request content, please check the docs to see the requirements"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException) = ErrorResponse(
        code = ErrorResponse.Code.INVALID_REQUEST,
        message = exception.fieldError?.let { "${it.field}: ${it.defaultMessage}" } ?: INVALID_REQUEST_MESSAGE
    )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ErrorResponse {
        log.warn(exception.message, exception)
        return ErrorResponse(
            code = ErrorResponse.Code.INVALID_REQUEST,
            message = INVALID_REQUEST_MESSAGE
        )
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHandlerMethodValidationException(exception: HandlerMethodValidationException): ErrorResponse {
        log.warn(exception.message, exception)
        return ErrorResponse(
            code = ErrorResponse.Code.INVALID_REQUEST,
            message = exception.valueResults.firstOrNull()
                ?.let { "${it.methodParameter.parameterName}: ${it.resolvableErrors.first().defaultMessage}" }
                ?: INVALID_REQUEST_MESSAGE
        )
    }

    @ExceptionHandler(AccountNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleOfferNotFoundException(exception: AccountNotFoundException) = ErrorResponse(
        code = ErrorResponse.Code.RESOURCE_NOT_FOUND,
        message = exception.message
    )

    @ExceptionHandler(MissingRequestHeaderException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingRequestHeaderException(exception: MissingRequestHeaderException) = ErrorResponse(
        code = ErrorResponse.Code.INVALID_REQUEST,
        message = exception.message
    )

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException) = ErrorResponse(
        code = ErrorResponse.Code.INVALID_REQUEST,
        message = "${exception.propertyName}: ${exception.message}"
    )

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exception: Exception): ErrorResponse {
        log.error(exception.message, exception)
        return ErrorResponse(
            code = ErrorResponse.Code.INTERNAL_SERVER_ERROR,
            message = INTERNAL_SERVER_ERROR_MESSAGE
        )
    }
}
