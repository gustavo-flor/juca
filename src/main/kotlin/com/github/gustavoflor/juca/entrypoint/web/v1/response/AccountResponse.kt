package com.github.gustavoflor.juca.entrypoint.web.v1.response

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Wallet
import java.math.BigDecimal

data class AccountResponse(
    val id: Long,
    val wallets: List<WalletDTO>,
) {
    companion object {
        fun of(accountId: Long, wallets: List<Wallet>) = AccountResponse(
            id = accountId,
            wallets = wallets.map {
                WalletDTO(
                    balance = it.balance,
                    merchantCategory = it.merchantCategory
                )
            }
        )
    }

    data class WalletDTO(
        val balance: BigDecimal,
        val merchantCategory: MerchantCategory
    )
}
