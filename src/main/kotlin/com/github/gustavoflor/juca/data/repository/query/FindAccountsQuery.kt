package com.github.gustavoflor.juca.data.repository.query

import com.github.gustavoflor.juca.core.entity.Account
import com.github.gustavoflor.juca.data.repository.query.mapper.AccountMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class FindAccountsQuery(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private const val SQL = """
            SELECT *
            FROM account
        """
    }

    fun execute(): List<Account> {
        val params = mapOf<String, Any?>()
        return jdbcTemplate.query(SQL, params, AccountMapper())
    }
}
