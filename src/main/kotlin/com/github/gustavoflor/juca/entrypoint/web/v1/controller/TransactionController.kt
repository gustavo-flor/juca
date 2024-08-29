package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.request.TransactRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.response.TransactResponse
import jakarta.validation.Valid
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
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun transact(@Valid @RequestBody request: TransactRequest): TransactResponse {
        val input = request.input()
        val output = transactUseCase.execute(input)
        return TransactResponse(output.result.code)
    }
}
