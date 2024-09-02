package com.github.gustavoflor.juca.core.entity

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import java.math.BigDecimal
import java.time.LocalDateTime

data class Wallet(
    val id: Long? = null,
    val accountId: Long,
    val balance: BigDecimal,
    val merchantCategory: MerchantCategory,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(account: Account, merchantCategory: MerchantCategory) = Wallet(
            accountId = account.id,
            merchantCategory = merchantCategory,
            balance = BigDecimal.ZERO
        )
    }

    fun isNew() = id == null

    fun debit(value: BigDecimal): Wallet {
        val newBalance = balance.subtract(value)
        return copy(balance = newBalance)
    }

    fun credit(value: BigDecimal) = debit(value.negate())
}
