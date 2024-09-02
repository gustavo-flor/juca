package com.github.gustavoflor.juca.data.repository.command

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Wallet
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Timestamp

@Component
class CreateWalletCommand(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private val KEYS = arrayOf("id", "account_id", "balance", "merchant_category", "created_at", "updated_at")

        private const val SQL = """
            INSERT INTO wallet
                (account_id, balance, merchant_category)
            VALUES
                (:accountId, :balance, :merchantCategory)
        """
    }

    fun executeAll(wallets: List<Wallet>): List<Wallet> {
        val isNew = wallets.all { it.isNew() }
        if (!isNew) {
            throw IllegalArgumentException("Wallet is not new")
        }
        val keyHolder = GeneratedKeyHolder()
        val params = wallets.map { getParams(it) }.toTypedArray()
        jdbcTemplate.batchUpdate(SQL, params, keyHolder, KEYS)
        return keyHolder.keyList.map { keys ->
            Wallet(
                id = keys["id"] as Long,
                accountId = keys["account_id"] as Long,
                balance = keys["balance"] as BigDecimal,
                merchantCategory = keys["merchant_category"].let { MerchantCategory.valueOf(it as String) },
                createdAt = keys["created_at"].let { it as Timestamp }.toLocalDateTime(),
                updatedAt = keys["updated_at"].let { it as Timestamp }.toLocalDateTime()
            )
        }
    }

    private fun getParams(wallet: Wallet) = MapSqlParameterSource(
        mapOf(
            "accountId" to wallet.accountId,
            "balance" to wallet.balance,
            "merchantCategory" to wallet.merchantCategory.name
        )
    )
}
