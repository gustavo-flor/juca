package com.github.gustavoflor.juca.data.repository.command

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.domain.TransactionResult
import com.github.gustavoflor.juca.core.domain.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.UUID

@Component
class CreateTransactionCommand(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private val KEYS = arrayOf(
            "id",
            "account_id",
            "external_id",
            "origin",
            "amount",
            "type",
            "merchant_category",
            "result",
            "created_at"
        )

        private const val SQL = """
            INSERT INTO transaction (
                account_id,
                external_id,
                origin,
                amount,
                type,
                merchant_category,
                result
            )
            VALUES (
                :accountId,
                :externalId,
                :origin,
                :amount,
                :type,
                :merchantCategory,
                :result
            )
        """
    }

    fun execute(transaction: Transaction): Transaction {
        return executeAll(listOf(transaction)).first()
    }

    fun executeAll(transactions: List<Transaction>): List<Transaction> {
        val isNew = transactions.all { it.isNew() }
        if (!isNew) {
            throw IllegalArgumentException("Transaction is not new")
        }
        val keyHolder = GeneratedKeyHolder()
        val params = transactions.map { it.params() }.toTypedArray()
        jdbcTemplate.batchUpdate(SQL, params, keyHolder, KEYS)
        return keyHolder.keyList.map { keys ->
            Transaction(
                id = keys["id"] as Long,
                accountId = keys["account_id"] as Long,
                externalId = keys["external_id"] as UUID,
                origin = keys["origin"] as String,
                amount = keys["amount"] as BigDecimal,
                type = keys["type"].let { TransactionType.valueOf(it as String) },
                merchantCategory = keys["merchant_category"].let { MerchantCategory.valueOf(it as String) },
                result = keys["result"].let { TransactionResult.valueOf(it as String) },
                createdAt = keys["created_at"].let { it as Timestamp }.toLocalDateTime()
            )
        }
    }

    private fun Transaction.params() = MapSqlParameterSource(
        mapOf(
            "accountId" to accountId,
            "externalId" to externalId,
            "origin" to origin,
            "amount" to amount,
            "type" to type.name,
            "merchantCategory" to merchantCategory.name,
            "result" to result.name
        )
    )
}
