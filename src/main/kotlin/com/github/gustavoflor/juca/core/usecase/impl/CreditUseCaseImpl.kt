package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.CreditUseCase
import org.apache.logging.log4j.LogManager
import org.springframework.transaction.annotation.Transactional

@UseCase
class CreditUseCaseImpl(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : CreditUseCase {
    private val log = LogManager.getLogger(javaClass)

    @Transactional
    override fun execute(input: CreditUseCase.Input): CreditUseCase.Output {
        log.info("Executing credit use case...")
        val wallet = walletRepository.findByAccountIdAndMerchantCategoryForUpdate(input.accountId, input.merchantCategory)
            ?: throw AccountNotFoundException()
        return wallet.credit(input.amount)
            .let { walletRepository.update(it) }
            .also { transactionRepository.create(input.transaction()) }
            .let { CreditUseCase.Output(it) }
    }
}
