package com.github.gustavoflor.juca.data.repository.query

import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.data.repository.query.mapper.TransactionMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class FindTransactionByExternalIdQuery(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private const val SQL = """
            SELECT *
            FROM transaction
            WHERE external_id = :externalId
        """
    }

    fun execute(externalId: String): Transaction? {
        val params = mapOf("externalId" to externalId)
        return try {
            jdbcTemplate.queryForObject(SQL, params, TransactionMapper())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
}
