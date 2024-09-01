package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.usecase.GetStatementUseCase

@UseCase
class GetStatementUseCaseImpl(
    private val transactionRepository: TransactionRepository
) : GetStatementUseCase {
    override fun execute(input: GetStatementUseCase.Input): GetStatementUseCase.Output {
        return transactionRepository.findAllByAccountId(input.accountId)
            .let { GetStatementUseCase.Output(it) }
    }
}
