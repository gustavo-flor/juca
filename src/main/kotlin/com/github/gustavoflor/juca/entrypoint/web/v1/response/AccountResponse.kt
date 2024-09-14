package com.github.gustavoflor.juca.entrypoint.web.v1.response

import com.github.gustavoflor.juca.core.entity.Wallet
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

data class AccountResponse(
    val id: Long,
    val wallet: WalletDTO,
) {
    companion object {
        fun of(accountId: Long, wallet: Wallet) = AccountResponse(
            id = accountId,
            wallet = WalletDTO(
                foodBalance = wallet.foodBalance,
                mealBalance = wallet.mealBalance,
                cashBalance = wallet.cashBalance
            )
        )
    }

    data class WalletDTO(
        @Schema(example = "100.0")
        val foodBalance: BigDecimal,
        @Schema(example = "100.0")
        val mealBalance: BigDecimal,
        @Schema(example = "100.0")
        val cashBalance: BigDecimal
    )
}
