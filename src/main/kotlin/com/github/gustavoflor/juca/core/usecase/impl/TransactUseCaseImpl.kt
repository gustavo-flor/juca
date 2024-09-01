package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.mapping.UseCase
import com.github.gustavoflor.juca.core.policy.DebitPolicy
import com.github.gustavoflor.juca.core.repository.MerchantCategoryTermRepository
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import org.apache.logging.log4j.LogManager
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@UseCase
class TransactUseCaseImpl(
    private val walletRepository: WalletRepository,
    private val merchantCategoryTermRepository: MerchantCategoryTermRepository,
    private val transactionRepository: TransactionRepository,
    private val debitPolicy: DebitPolicy
) : TransactUseCase {
    companion object {
        private val FALLBACK_MERCHANT_CATEGORY = MerchantCategory.CASH
    }

    private val log = LogManager.getLogger(javaClass)

    @Transactional
    override fun execute(input: TransactUseCase.Input): TransactUseCase.Output {
        log.info("Executing transact use case...")
        val transaction = findByExternalId(input.externalId)
            ?: transact(input)
        return TransactUseCase.Output(transaction.result)
    }

    private fun findByExternalId(externalId: UUID): Transaction? {
        return transactionRepository.findByExternalId(externalId)
    }

    private fun transact(input: TransactUseCase.Input): Transaction {
        val merchantCategory = getMerchantCategory(input)
        val targetWallet = getTargetWallet(input.accountId, merchantCategory)
        val fallbackWallet = getFallbackWallet(input.amount, targetWallet)
        val debitOutput = DebitPolicy.Input(input.amount, targetWallet, fallbackWallet)
            .let { debitPolicy.execute(it) }
        debitOutput.targetAmount
            ?.let { targetWallet.debit(it) }
            ?.let { walletRepository.update(it) }
        debitOutput.fallbackAmount
            ?.let { fallbackWallet?.debit(it) }
            ?.let { walletRepository.update(it) }
        return input.transaction(merchantCategory, debitOutput.transactionResult)
            .let { transactionRepository.create(it) }
    }

    private fun getTargetWallet(accountId: Long, merchantCategory: MerchantCategory): Wallet {
        return walletRepository.findByAccountIdAndMerchantCategoryForUpdate(accountId, merchantCategory)
            ?: throw AccountNotFoundException()
    }

    private fun getMerchantCategory(input: TransactUseCase.Input): MerchantCategory {
        return merchantCategoryTermRepository.findAll()
            .filterValues { terms -> terms.any() { input.merchantName.contains(it, true) } }
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
