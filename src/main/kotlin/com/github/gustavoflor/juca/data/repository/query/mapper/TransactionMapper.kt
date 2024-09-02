package com.github.gustavoflor.juca.data.repository.query.mapper

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.domain.TransactionResult
import com.github.gustavoflor.juca.core.domain.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.shared.util.DateTimeUtil.DATE_TIME_ISO_8601_FORMATTER
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.UUID

class TransactionMapper : RowMapper<Transaction> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = Transaction(
        id = rs.getLong("id"),
        accountId = rs.getLong("account_id"),
        externalId = rs.getString("external_id").let { UUID.fromString(it) },
        origin = rs.getString("origin"),
        amount = rs.getBigDecimal("amount"),
        type = rs.getString("type").let { TransactionType.valueOf(it) },
        merchantCategory = rs.getString("merchant_category").let { MerchantCategory.valueOf(it) },
        result = rs.getString("result").let { TransactionResult.valueOf(it) },
        createdAt = rs.getString("created_at").let { LocalDateTime.parse(it, DATE_TIME_ISO_8601_FORMATTER) }
    )
}
