package com.github.gustavoflor.juca.data.repository.query.mapper

import com.github.gustavoflor.juca.core.entity.Account
import com.github.gustavoflor.juca.shared.util.DateTimeUtil.DATE_TIME_ISO_8601_FORMATTER
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDateTime

class AccountMapper : RowMapper<Account> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = Account(
        id = rs.getLong("id"),
        createdAt = rs.getString("created_at").let { LocalDateTime.parse(it, DATE_TIME_ISO_8601_FORMATTER) },
        updatedAt = rs.getString("updated_at").let { LocalDateTime.parse(it, DATE_TIME_ISO_8601_FORMATTER) }
    )
}
