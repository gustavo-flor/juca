package com.github.gustavoflor.juca.core.entity

import java.math.BigDecimal
import java.time.LocalDateTime

data class Wallet(
    val id: Long? = null,
    val accountId: Long,
    val foodBalance: BigDecimal,
    val mealBalance: BigDecimal,
    val cashBalance: BigDecimal,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun of(account: Account) = Wallet(
            accountId = account.id,
            foodBalance = BigDecimal.ZERO,
            mealBalance = BigDecimal.ZERO,
            cashBalance = BigDecimal.ZERO
        )
    }

    fun isNew() = id == null
}
