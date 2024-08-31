package com.github.gustavoflor.juca.entrypoint.web.v1.response

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime

data class StatementResponse(
    val transactions: List<TransactionDTO>
) {
    companion object {
        fun of(transactions: List<Transaction>) = StatementResponse(
            transactions = transactions.map { TransactionDTO.of(it) }
        )
    }

    data class TransactionDTO(
        val externalId: String,
        val origin: String,
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
