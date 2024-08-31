package com.github.gustavoflor.juca.entrypoint.web.v1.controller.impl

import com.github.gustavoflor.juca.core.usecase.CreateAccountUseCase
import com.github.gustavoflor.juca.core.usecase.FindAccountDetailsByIdUseCase
import com.github.gustavoflor.juca.core.usecase.GetStatementUseCase
import com.github.gustavoflor.juca.entrypoint.web.v1.controller.AccountController
import com.github.gustavoflor.juca.entrypoint.web.v1.response.AccountResponse
import com.github.gustavoflor.juca.entrypoint.web.v1.response.StatementResponse
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountControllerImpl(
    private val findAccountDetailsByIdUseCase: FindAccountDetailsByIdUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val getStatementUseCase: GetStatementUseCase
) : AccountController {
    override fun findById(id: Long): AccountResponse {
        val input = FindAccountDetailsByIdUseCase.Input(id)
        val output = findAccountDetailsByIdUseCase.execute(input)
        return AccountResponse.of(id, output.wallets)
    }

    @Transactional
    override fun create(): AccountResponse {
        return createAccountUseCase.execute()
            .let { AccountResponse.of(it.account.id, it.wallets) }
    }

    override fun getStatement(id: Long): StatementResponse {
        val input = GetStatementUseCase.Input(id)
        val output = getStatementUseCase.execute(input)
        return StatementResponse.of(output.transactions)
    }
}
