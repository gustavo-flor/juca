package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.domain.TransactionResult
import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.MerchantCategoryTermRepository
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import org.apache.logging.log4j.LogManager
import org.springframework.transaction.annotation.Transactional

@UseCase
class TransactUseCaseImpl(
    private val walletRepository: WalletRepository,
    private val merchantCategoryTermRepository: MerchantCategoryTermRepository,
    private val transactionRepository: TransactionRepository
) : TransactUseCase {

    private val log = LogManager.getLogger(javaClass)

    @Transactional
    override fun execute(input: TransactUseCase.Input): TransactUseCase.Output {
        log.info("Executing transact use case...")
        val transaction = transact(input)
        return TransactUseCase.Output(transaction.result)
    }

    private fun transact(input: TransactUseCase.Input): Transaction {
        val merchantCategory = getMerchantCategory(input)
        val result = debit(merchantCategory, input)
        return input.transaction(merchantCategory, result)
            .let { transactionRepository.create(it) }
    }

    private fun debit(merchantCategory: MerchantCategory, input: TransactUseCase.Input): TransactionResult {
        val amount = input.amount
        val wallet = getWallet(input.accountId)
        val totalBalance = merchantCategory.getTotalBalance(wallet)
        if (amount > totalBalance) {
            return TransactionResult.INSUFFICIENT_BALANCE
        }
        merchantCategory.debit(amount, wallet).let { walletRepository.update(it) }
        return TransactionResult.APPROVED
    }

    private fun getWallet(accountId: Long): Wallet {
        return walletRepository.findByAccountIdForUpdate(accountId)
            ?: throw AccountNotFoundException()
    }

    private fun getMerchantCategory(input: TransactUseCase.Input): MerchantCategory {
        return merchantCategoryTermRepository.findAll()
            .filterValues { terms -> terms.any() { input.merchantName.contains(it, true) } }
            .map { it.key }
            .firstOrNull() ?: MerchantCategory.getByCode(input.mcc)
    }
}
