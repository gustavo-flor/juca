package com.github.gustavoflor.juca.data.repository.query.mapper

import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.shared.util.DateTimeUtil.DATE_TIME_ISO_8601_FORMATTER
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDateTime

class WalletMapper : RowMapper<Wallet> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = Wallet(
        id = rs.getLong("id"),
        accountId = rs.getLong("account_id"),
        foodBalance = rs.getBigDecimal("food_balance"),
        mealBalance = rs.getBigDecimal("meal_balance"),
        cashBalance = rs.getBigDecimal("cash_balance"),
        createdAt = rs.getString("created_at").let { LocalDateTime.parse(it, DATE_TIME_ISO_8601_FORMATTER) },
        updatedAt = rs.getString("updated_at").let { LocalDateTime.parse(it, DATE_TIME_ISO_8601_FORMATTER) }
    )
}
