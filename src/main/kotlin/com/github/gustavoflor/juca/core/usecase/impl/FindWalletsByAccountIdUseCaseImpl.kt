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
        val wallets = walletRepository.findByAccountId(input.accountId)
        if (wallets.isEmpty()) {
            throw AccountNotFoundException()
        }
        return FindWalletsByAccountIdUseCase.Output(wallets)
    }
}
