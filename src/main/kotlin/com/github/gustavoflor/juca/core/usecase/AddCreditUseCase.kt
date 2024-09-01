package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.core.entity.Wallet
import java.math.BigDecimal
import java.util.UUID

interface AddCreditUseCase {
    companion object {
        private const val ORIGIN = "Juca API"
    }

    fun execute(input: Input): Output

    data class Input(
        val accountId: Long,
        val amount: BigDecimal,
        val merchantCategory: MerchantCategory
    ) {
        fun transaction() = Transaction(
            accountId = accountId,
            externalId = UUID.randomUUID(),
            amount = amount,
            origin = ORIGIN,
            type = TransactionType.CREDIT,
            merchantCategory = merchantCategory,
            result = TransactionResult.APPROVED
        )
    }

    data class Output(
        val wallet: Wallet
    )
}
