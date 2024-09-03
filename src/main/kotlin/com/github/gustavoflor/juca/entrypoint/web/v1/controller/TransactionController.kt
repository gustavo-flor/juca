package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.ApiHeaders
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.response.TransactResponse
import com.github.gustavoflor.juca.shared.log.LogContextCloseable.Companion.addAccountId
import com.github.gustavoflor.juca.shared.util.TimeoutUtil
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
    ): TransactResponse = addAccountId(request.accountId).addExternalId(request.externalId).track().use {
        try {
            val task = Callable {
                val input = request.input()
                val output = transactUseCase.execute(input)
                TransactResponse(output.result.code)
            }
            return TimeoutUtil.runUntil(requestDuration) {
                transactionExecutorService.submit(task)
            }
        } catch (e: Exception) {
            log.error("Something went wrong on transact use case execution [{}]", e.message, e)
            return TransactResponse.error()
        }
    }
}
