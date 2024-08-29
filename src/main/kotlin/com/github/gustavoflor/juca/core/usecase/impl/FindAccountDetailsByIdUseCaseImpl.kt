package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.FindAccountDetailsByIdUseCase

@UseCase
class FindAccountDetailsByIdUseCaseImpl(
    private val walletRepository: WalletRepository
) : FindAccountDetailsByIdUseCase {
    override fun execute(input: FindAccountDetailsByIdUseCase.Input): FindAccountDetailsByIdUseCase.Output {
        val wallets = walletRepository.findByAccountId(input.id)
        if (wallets.isEmpty()) {
            throw AccountNotFoundException()
        }
        return FindAccountDetailsByIdUseCase.Output(wallets)
    }
}
