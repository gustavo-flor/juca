package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.AddCreditUseCase
import org.apache.logging.log4j.LogManager
import org.springframework.transaction.annotation.Transactional

@UseCase
class AddCreditUseCaseImpl(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : AddCreditUseCase {
    private val log = LogManager.getLogger(javaClass)

    @Transactional
    override fun execute(input: AddCreditUseCase.Input): AddCreditUseCase.Output {
        log.info("Executing credit use case...")
        val wallet = walletRepository.findByAccountIdForUpdate(input.accountId)
            ?: throw AccountNotFoundException()
        val merchantCategory = input.merchantCategory
        val amount = input.amount
        return merchantCategory.credit(amount, wallet)
            .let { walletRepository.update(it) }
            .also { transactionRepository.create(input.transaction()) }
            .let { AddCreditUseCase.Output(it) }
    }
}
