package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import org.apache.logging.log4j.LogManager
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@UseCase
class TransactUseCaseImpl(
    private val walletRepository: WalletRepository
) : TransactUseCase {
    companion object {
        private val FALLBACK_MERCHANT_CATEGORY = MerchantCategory.CASH
    }

    private val log = LogManager.getLogger(javaClass)

    @Transactional
    override fun execute(input: TransactUseCase.Input): TransactUseCase.Output = runCatching {
        val result = transact(input)
        return TransactUseCase.Output(result)
    }.getOrElse {
        log.error("Something went wrong on transact use case execution [{}]", it.message, it)
        return TransactUseCase.Output(TransactionResult.ERROR)
    }

    private fun transact(input: TransactUseCase.Input): TransactionResult {
        val targetWallet = getTargetWallet(input)
        val fallbackWallet = getFallbackWallet(input)
        val amount = input.amount
        val fallbackBalance = fallbackWallet?.balance ?: BigDecimal.ZERO
        val targetBalance = targetWallet.balance
        val totalBalance = targetBalance.add(fallbackBalance)
        if (totalBalance < amount) {
            return TransactionResult.INSUFFICIENT_BALANCE
        }
        val targetAmount = amount.min(targetBalance)
        val fallbackAmount = amount.subtract(targetBalance)
        targetWallet.debit(targetAmount).let { walletRepository.update(it) }
        if (fallbackAmount > BigDecimal.ZERO) {
            fallbackWallet?.debit(fallbackAmount)?.let { walletRepository.update(it) }
        }
        return TransactionResult.APPROVED
    }

    private fun getTargetWallet(input: TransactUseCase.Input): Wallet {
        return walletRepository.findByAccountIdAndMerchantCategory(input.accountId, input.merchantCategory)
            ?: throw AccountNotFoundException()
    }

    private fun getFallbackWallet(input: TransactUseCase.Input): Wallet? {
        if (input.merchantCategory == FALLBACK_MERCHANT_CATEGORY) {
            return null
        }
        return walletRepository.findByAccountIdAndMerchantCategory(input.accountId, FALLBACK_MERCHANT_CATEGORY)
    }
}
