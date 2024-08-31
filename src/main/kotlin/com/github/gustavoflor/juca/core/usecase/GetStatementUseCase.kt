package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.entity.Transaction

interface GetStatementUseCase {
    fun execute(input: Input): Output

    data class Input(
        val accountId: Long
    )

    data class Output(
        val transactions: List<Transaction>
    )
}
