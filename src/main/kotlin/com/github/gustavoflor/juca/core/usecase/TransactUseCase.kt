package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import java.math.BigDecimal

interface TransactUseCase {
    fun execute(input: Input): Output

    data class Input(
        val externalId: String,
        val accountId: Long,
        val amount: BigDecimal,
        val merchantCategory: MerchantCategory,
        val merchantName: String
    )

    data class Output(
        val result: TransactionResult
    )
}
