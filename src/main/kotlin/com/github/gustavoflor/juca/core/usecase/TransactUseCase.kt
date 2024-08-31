package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import java.math.BigDecimal

interface TransactUseCase {
    fun execute(input: Input): Output

    data class Input(
        val externalId: String,
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
