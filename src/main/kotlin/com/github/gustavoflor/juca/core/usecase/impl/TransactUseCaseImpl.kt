package com.github.gustavoflor.juca.core.usecase.impl

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
    private val log = LogManager.getLogger(javaClass)

    @Transactional
    override fun execute(input: TransactUseCase.Input): TransactUseCase.Output = runCatching {
        val wallets = walletRepository.findByAccountId(input.accountId)
        if (wallets.isEmpty()) {
            throw AccountNotFoundException()
        }
        val targetWallet = Wallet.get(wallets, input.merchantCategory)
        val fallbackWallet = Wallet.fallback(wallets, input.merchantCategory)
        val code = debit(input.amount, targetWallet, fallbackWallet)
        return TransactUseCase.Output(code)
    }.getOrElse {
        log.error("Something went wrong on transact use case execution [{}]", it.message, it)
        return TransactUseCase.Output(TransactionResult.ERROR)
    }

    private fun debit(
        amount: BigDecimal,
        targetWallet: Wallet,
        fallbackWallet: Wallet? = null
    ): TransactionResult {
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
}
