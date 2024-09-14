package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.FindWalletsByAccountIdUseCase

@UseCase
class FindWalletsByAccountIdUseCaseImpl(
    private val walletRepository: WalletRepository
) : FindWalletsByAccountIdUseCase {
    override fun execute(input: FindWalletsByAccountIdUseCase.Input): FindWalletsByAccountIdUseCase.Output {
        val wallet = walletRepository.findByAccountId(input.accountId)
            ?: throw AccountNotFoundException()
        return FindWalletsByAccountIdUseCase.Output(wallet)
    }
}
