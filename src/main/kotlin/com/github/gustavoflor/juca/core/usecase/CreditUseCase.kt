package com.github.gustavoflor.juca.core.usecase

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Wallet
import java.math.BigDecimal

interface CreditUseCase {
    fun execute(input: Input): Output

    data class Input(
        val accountId: Long,
        val amount: BigDecimal,
        val merchantCategory: MerchantCategory
    )

    data class Output(
        val wallet: Wallet
    )
}
