package com.github.gustavoflor.juca.core.entity

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.domain.TransactionResult
import com.github.gustavoflor.juca.core.domain.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class Transaction(
    val id: Long? = null,
    val accountId: Long,
    val externalId: UUID,
    val origin: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val merchantCategory: MerchantCategory,
    val result: TransactionResult,
    val createdAt: LocalDateTime? = null
) {
    fun isNew() = id == null
}
