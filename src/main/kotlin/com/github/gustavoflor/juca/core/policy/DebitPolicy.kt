package com.github.gustavoflor.juca.core.policy

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.shared.util.BigDecimalUtil.positiveOrNull
import java.math.BigDecimal

interface DebitPolicy {
    fun execute(input: Input): Output

    data class Input(
        val amount: BigDecimal,
        val targetWallet: Wallet,
        val fallbackWallet: Wallet?
    )

    data class Output(
        val transactionResult: TransactionResult,
        val targetAmount: BigDecimal?,
        val fallbackAmount: BigDecimal?
    ) {
        companion object {
            fun approved(targetAmount: BigDecimal?, fallbackAmount: BigDecimal?) = Output(
                transactionResult = TransactionResult.APPROVED,
                targetAmount = targetAmount?.let { positiveOrNull(it) },
                fallbackAmount = fallbackAmount?.let { positiveOrNull(it) }
            )

            fun insufficientBalance() = Output(
                transactionResult = TransactionResult.INSUFFICIENT_BALANCE,
                targetAmount = null,
                fallbackAmount = null
            )
        }
    }
}
