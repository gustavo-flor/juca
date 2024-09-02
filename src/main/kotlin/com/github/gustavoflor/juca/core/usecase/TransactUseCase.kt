package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.domain.TransactionResult
import com.github.gustavoflor.juca.core.domain.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import java.math.BigDecimal
import java.util.UUID

interface TransactUseCase {
    fun execute(input: Input): Output

    data class Input(
        val externalId: UUID,
        val accountId: Long,
        val amount: BigDecimal,
        val mcc: String,
        val merchantName: String,
        val address: String
    ) {
        fun transaction(merchantCategory: MerchantCategory, result: TransactionResult) = Transaction(
            accountId = accountId,
            externalId = externalId,
            amount = amount,
            merchantCategory = merchantCategory,
            origin = "$merchantName - $address",
            type = TransactionType.DEBIT,
            result = result
        )
    }

    data class Output(
        val result: TransactionResult
    )
}
