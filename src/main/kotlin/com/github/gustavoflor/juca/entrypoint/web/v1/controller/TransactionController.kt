package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.ApiHeaders
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.response.TransactResponse
import com.github.gustavoflor.juca.shared.util.TimeLimiterUtil
import jakarta.validation.Valid
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

@RestController
@RequestMapping("/v1/transactions")
class TransactionController(
    private val transactUseCase: TransactUseCase,
    @Qualifier("transactionExecutorService")
    private val transactionExecutorService: ExecutorService
) {
    private val log = LogManager.getLogger(javaClass)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun transact(
        @Valid @RequestBody request: TransactRequest,
        @RequestHeader(value = ApiHeaders.MAX_REQUEST_DURATION) requestDuration: Long
    ): TransactResponse = runCatching {
        val task = Callable {
            val input = request.input()
            val output = transactUseCase.execute(input)
            TransactResponse(output.result.code)
        }
        return TimeLimiterUtil.runOrCancel("transaction-time-limiter", requestDuration) {
            transactionExecutorService.submit(task)
        }
    }.getOrElse {
        log.error("Something went wrong on transact use case execution [{}]", it.message, it)
        return TransactResponse.error()
    }
}
