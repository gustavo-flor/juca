package com.github.gustavoflor.juca.data.repository.command

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
        private val KEYS = arrayOf(
            "id",
            "account_id",
            "food_balance",
            "meal_balance",
            "cash_balance",
            "created_at",
            "updated_at"
        )

        private const val SQL = """
            INSERT INTO wallet
                (account_id, food_balance, meal_balance, cash_balance)
            VALUES
                (:accountId, :foodBalance, :mealBalance, :cashBalance)
        """
    }

    fun execute(wallet: Wallet): Wallet {
        if (!wallet.isNew()) {
            throw IllegalArgumentException("Wallet is not new")
        }
        val keyHolder = GeneratedKeyHolder()
        val params = getParams(wallet)
        jdbcTemplate.update(SQL, params, keyHolder, KEYS)
        return Wallet(
            id = keyHolder.keys!!["id"] as Long,
            accountId = keyHolder.keys!!["account_id"] as Long,
            foodBalance = keyHolder.keys!!["food_balance"] as BigDecimal,
            mealBalance = keyHolder.keys!!["meal_balance"] as BigDecimal,
            cashBalance = keyHolder.keys!!["cash_balance"] as BigDecimal,
            createdAt = keyHolder.keys!!["created_at"].let { it as Timestamp }.toLocalDateTime(),
            updatedAt = keyHolder.keys!!["updated_at"].let { it as Timestamp }.toLocalDateTime()
        )
    }

    private fun getParams(wallet: Wallet) = MapSqlParameterSource(
        mapOf(
            "accountId" to wallet.accountId,
            "foodBalance" to wallet.foodBalance,
            "mealBalance" to wallet.mealBalance,
            "cashBalance" to wallet.cashBalance
        )
    )
}
