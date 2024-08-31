package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.usecase.GetStatementUseCase
import com.github.gustavoflor.juca.data.repository.query.FindTransactionsByAccountIdQuery

@UseCase
class GetStatementUseCaseImpl(
    private val findTransactionsByAccountIdQuery: FindTransactionsByAccountIdQuery
) : GetStatementUseCase {
    override fun execute(input: GetStatementUseCase.Input): GetStatementUseCase.Output {
        return findTransactionsByAccountIdQuery.execute(input.accountId)
            .let { GetStatementUseCase.Output(it) }
    }
}
