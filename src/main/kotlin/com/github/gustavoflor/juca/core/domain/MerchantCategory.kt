package com.github.gustavoflor.juca.core.domain

import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.shared.util.BigDecimalUtil.positiveOrNull
import java.math.BigDecimal

enum class MerchantCategory(
    val codes: Array<out String> = arrayOf(),
    fallbackSupplier: () -> MerchantCategory? = { null }
) {
    FOOD(arrayOf("5411", "5412"), { CASH }) {
        override fun getBalance(wallet: Wallet) = wallet.foodBalance

        override fun credit(amount: BigDecimal, wallet: Wallet) = wallet.copy(foodBalance = getBalance(wallet) + amount)
    },
    MEAL(arrayOf("5811", "5812"), { CASH }) {
        override fun getBalance(wallet: Wallet) = wallet.mealBalance

        override fun credit(amount: BigDecimal, wallet: Wallet) = wallet.copy(mealBalance = getBalance(wallet) + amount)
    },
    CASH {
        override fun getBalance(wallet: Wallet) = wallet.cashBalance

        override fun credit(amount: BigDecimal, wallet: Wallet) = wallet.copy(cashBalance = getBalance(wallet) + amount)
    };

    private val fallback: MerchantCategory? by lazy(fallbackSupplier)

    companion object {
        fun getByCode(code: String) = entries.firstOrNull() { it.codes.contains(code) } ?: CASH

        fun getDirtyCategories(wallet: Wallet, otherWallet: Wallet): List<MerchantCategory> {
            return entries.filter { it.getBalance(wallet) != it.getBalance(otherWallet) }
        }
    }

    abstract fun getBalance(wallet: Wallet): BigDecimal

    abstract fun credit(amount: BigDecimal, wallet: Wallet): Wallet

    fun getTotalBalance(wallet: Wallet) = getBalance(wallet) + (fallback?.getBalance(wallet) ?: BigDecimal.ZERO)

    fun debit(amount: BigDecimal, wallet: Wallet): Wallet {
        if (amount <= BigDecimal.ZERO) {
            return wallet
        }
        val categoryBalance = getBalance(wallet)
        val categoryAmount = positiveOrNull(amount.min(categoryBalance)) ?: BigDecimal.ZERO
        val fallbackAmount = positiveOrNull(amount.subtract(categoryBalance)) ?: BigDecimal.ZERO
        return credit(categoryAmount.negate(), wallet)
            .let { fallback?.credit(fallbackAmount.negate(), it) ?: it }
    }
}
