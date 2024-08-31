package com.github.gustavoflor.juca.core.entity

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class Transaction(
    val id: Long? = null,
    val accountId: Long,
    val externalId: String,
    val origin: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val merchantCategory: MerchantCategory,
    val result: TransactionResult,
    val createdAt: LocalDateTime? = null
) {
    fun isNew() = id == null
}
