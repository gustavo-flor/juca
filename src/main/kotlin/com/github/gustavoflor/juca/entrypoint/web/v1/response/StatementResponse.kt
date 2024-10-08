package com.github.gustavoflor.juca.entrypoint.web.v1.response

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.domain.TransactionResult
import com.github.gustavoflor.juca.core.domain.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class StatementResponse(
    val transactions: List<TransactionDTO>
) {
    companion object {
        fun of(transactions: List<Transaction>) = StatementResponse(
            transactions = transactions.map { TransactionDTO.of(it) }
        )
    }

    data class TransactionDTO(
        val externalId: UUID,
        @Schema(example = "UBER EATS - SAO PAULO BR")
        val origin: String,
        @Schema(example = "100.0")
        val amount: BigDecimal,
        val type: TransactionType,
        val merchantCategory: MerchantCategory,
        val result: TransactionResult,
        val createdAt: LocalDateTime
    ) {
        companion object {
            fun of(transaction: Transaction) = TransactionDTO(
                externalId = transaction.externalId,
                origin = transaction.origin,
                amount = transaction.amount,
                type = transaction.type,
                merchantCategory = transaction.merchantCategory,
                result = transaction.result,
                createdAt = transaction.createdAt!!
            )
        }
    }
}
