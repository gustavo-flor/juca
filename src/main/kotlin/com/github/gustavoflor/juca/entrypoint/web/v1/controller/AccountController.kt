package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.usecase.CreateAccountUseCase
import com.github.gustavoflor.juca.core.usecase.FindAccountDetailsByIdUseCase
import com.github.gustavoflor.juca.core.usecase.GetStatementUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.response.AccountResponse
import com.github.gustavoflor.juca.entrypoint.web.v1.response.StatementResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val findAccountDetailsByIdUseCase: FindAccountDetailsByIdUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val getStatementUseCase: GetStatementUseCase
) {
    @GetMapping(path = ["/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable id: Long): AccountResponse {
        val input = FindAccountDetailsByIdUseCase.Input(id)
        val output = findAccountDetailsByIdUseCase.execute(input)
        return AccountResponse.of(id, output.wallets)
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun create(): AccountResponse {
        return createAccountUseCase.execute()
            .let { AccountResponse.of(it.account.id, it.wallets) }
    }

    @GetMapping(path = ["/{id}/statement"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getStatement(@PathVariable id: Long): StatementResponse {
        val input = GetStatementUseCase.Input(id)
        val output = getStatementUseCase.execute(input)
        return StatementResponse.of(output.transactions)
    }
}
