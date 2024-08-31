package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.TransactionResult
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
    )

    data class Output(
        val result: TransactionResult
    )
}
