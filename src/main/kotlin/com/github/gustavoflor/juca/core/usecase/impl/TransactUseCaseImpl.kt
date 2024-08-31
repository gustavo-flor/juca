package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.policy.DebitPolicy
import com.github.gustavoflor.juca.core.repository.MerchantCategoryTermRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import org.apache.logging.log4j.LogManager
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@UseCase
class TransactUseCaseImpl(
    private val walletRepository: WalletRepository,
    private val merchantCategoryTermRepository: MerchantCategoryTermRepository,
    private val debitPolicy: DebitPolicy
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
        val fallbackWallet = getFallbackWallet(input.amount, targetWallet)
        val debitOutput = DebitPolicy.Input(input.amount, targetWallet, fallbackWallet)
            .let { debitPolicy.execute(it) }
        debitOutput.targetAmount
            ?.let { targetWallet.debit(it) }
            ?.let { walletRepository.update(it) }
        debitOutput.fallbackAmount
            ?.let { fallbackWallet?.debit(it) }
            ?.let { walletRepository.update(it) }
        return debitOutput.transactionResult
    }

    private fun getTargetWallet(input: TransactUseCase.Input): Wallet {
        val merchantCategory = getMerchantCategory(input)
        return walletRepository.findByAccountIdAndMerchantCategoryForUpdate(input.accountId, merchantCategory)
            ?: throw AccountNotFoundException()
    }

    private fun getMerchantCategory(input: TransactUseCase.Input): MerchantCategory {
        return merchantCategoryTermRepository.findAll()
            .filterValues { terms -> terms.any() { it.contains(input.merchantName, true) } }
            .map { it.key }
            .firstOrNull() ?: MerchantCategory.getByCode(input.mcc)
    }

    private fun getFallbackWallet(amount: BigDecimal, targetWallet: Wallet): Wallet? {
        if (targetWallet.balance >= amount || targetWallet.merchantCategory == FALLBACK_MERCHANT_CATEGORY) {
            return null
        }
        return walletRepository.findByAccountIdAndMerchantCategoryForUpdate(targetWallet.accountId, FALLBACK_MERCHANT_CATEGORY)
    }
}
