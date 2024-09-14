package com.github.gustavoflor.juca.data.repository.query

import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.data.repository.query.FindWalletByAccountIdForUpdateQuery.Companion
import com.github.gustavoflor.juca.data.repository.query.mapper.WalletMapper
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class FindWalletByAccountIdQuery(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private const val SQL = """
            SELECT *
            FROM wallet
            WHERE account_id = :accountId
        """
    }

    fun execute(accountId: Long): Wallet? {
        val params = mapOf("accountId" to accountId)
        return try {
            jdbcTemplate.queryForObject(SQL, params, WalletMapper())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
}
