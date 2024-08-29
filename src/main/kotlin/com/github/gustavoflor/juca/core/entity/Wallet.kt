package com.github.gustavoflor.juca.core.entity

import com.github.gustavoflor.juca.core.MerchantCategory
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
        private val merchantCategoryFallback = MerchantCategory.CASH

        fun of(account: Account, merchantCategory: MerchantCategory) = Wallet(
            accountId = account.id,
            merchantCategory = merchantCategory,
            balance = BigDecimal.ZERO
        )

        fun get(wallets: List<Wallet>, merchantCategory: MerchantCategory) = wallets.first() { it.merchantCategory == merchantCategory }

        fun fallback(wallets: List<Wallet>, merchantCategory: MerchantCategory): Wallet? {
            if (merchantCategory == merchantCategoryFallback) {
                return null
            }
            return get(wallets, merchantCategoryFallback)
        }
    }

    fun isNew() = id == null

    fun debit(value: BigDecimal): Wallet {
        val newBalance = balance.subtract(value)
        return copy(balance = newBalance)
    }

    fun credit(value: BigDecimal) = debit(value.negate())
}
