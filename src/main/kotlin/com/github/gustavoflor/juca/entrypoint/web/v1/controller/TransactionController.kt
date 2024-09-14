package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.response.TransactResponse
import com.github.gustavoflor.juca.shared.log.LogContextCloseable.Companion.addAccountId
import jakarta.validation.Valid
import org.apache.logging.log4j.LogManager
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/transactions")
class TransactionController(
    private val transactUseCase: TransactUseCase
) {
    private val log = LogManager.getLogger(javaClass)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun transact(
        @Valid @RequestBody request: TransactRequest
    ): TransactResponse = addAccountId(request.accountId).addExternalId(request.externalId).track().use {
        return try {
            val input = request.input()
            val output = transactUseCase.execute(input)
            TransactResponse(output.result.code)
        } catch (e: Exception) {
            log.error("Something went wrong on transact use case execution [{}]", e.message, e)
            TransactResponse.error()
        }
    }
}
