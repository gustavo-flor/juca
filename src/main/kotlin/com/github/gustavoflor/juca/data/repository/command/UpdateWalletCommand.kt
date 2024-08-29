package com.github.gustavoflor.juca.data.repository.command

import com.github.gustavoflor.juca.core.entity.Wallet
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class UpdateWalletCommand(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private const val SQL = """
            UPDATE wallet
            SET balance = :balance, updated_at = NOW()
            WHERE account_id = :accountId AND merchant_category = :merchantCategory
        """
    }

    fun execute(wallet: Wallet): Wallet {
        val params = mapOf(
            "balance" to wallet.balance,
            "accountId" to wallet.accountId,
            "merchantCategory" to wallet.merchantCategory.name,
        )
        val keyHolder = GeneratedKeyHolder()
        val keys = arrayOf("updated_at")
        jdbcTemplate.update(SQL, MapSqlParameterSource(params), keyHolder, keys)
        return wallet.copy(
            updatedAt = keyHolder.keys?.get("updated_at").let { it as Timestamp }.toLocalDateTime(),
        )
    }
}
