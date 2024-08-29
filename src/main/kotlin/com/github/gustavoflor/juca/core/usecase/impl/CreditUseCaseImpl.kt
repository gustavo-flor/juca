package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.CreditUseCase
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreditUseCaseImpl(
    private val walletRepository: WalletRepository
) : CreditUseCase {
    @Transactional
    override fun execute(input: CreditUseCase.Input): CreditUseCase.Output {
        val wallet = walletRepository.findByAccountIdAndMerchantCategory(input.accountId, input.merchantCategory)
            ?: throw AccountNotFoundException()
        return wallet.credit(input.amount)
            .let { walletRepository.update(it) }
            .let { CreditUseCase.Output(it) }
    }
}
