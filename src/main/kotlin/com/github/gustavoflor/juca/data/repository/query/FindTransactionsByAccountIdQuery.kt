package com.github.gustavoflor.juca.data.repository.query

import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.data.repository.query.mapper.TransactionMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class FindTransactionsByAccountIdQuery(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private const val SQL = """
            SELECT *
            FROM transaction
            WHERE account_id = :accountId
            ORDER BY created_at DESC
        """
    }

    fun execute(accountId: Long): List<Transaction> {
        val params = mapOf("accountId" to accountId)
        return jdbcTemplate.query(SQL, params, TransactionMapper())
    }
}
