package com.github.gustavoflor.juca.data.repository.command

import com.github.gustavoflor.juca.core.entity.Transaction
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class CreateTransactionCommand(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private val KEYS = arrayOf("id", "created_at")
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
        if (!transaction.isNew()) {
            throw IllegalArgumentException("Transaction is not new")
        }
        val keyHolder = GeneratedKeyHolder()
        val params = transaction.params()
        jdbcTemplate.update(SQL, MapSqlParameterSource(params), keyHolder, KEYS)
        return transaction.copy(
            id = keyHolder.keys?.get("id") as Long,
            createdAt = keyHolder.keys?.get("created_at").let { it as Timestamp }.toLocalDateTime()
        )
    }

    private fun Transaction.params(): Map<String, Any?> = mapOf(
        "accountId" to accountId,
        "externalId" to externalId,
        "origin" to origin,
        "amount" to amount,
        "type" to type.name,
        "merchantCategory" to merchantCategory.name,
        "result" to result.name
    )
}
