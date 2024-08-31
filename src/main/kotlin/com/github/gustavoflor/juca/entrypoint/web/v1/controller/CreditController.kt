package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.usecase.CreditUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.request.CreditRequest
import com.github.gustavoflor.juca.entrypoint.web.v1.response.CreditResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/credits")
class CreditController(
    private val creditUseCase: CreditUseCase
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreditRequest): CreditResponse {
        val input = request.input()
        val output = creditUseCase.execute(input)
        return CreditResponse(output.wallet.balance)
    }
}
