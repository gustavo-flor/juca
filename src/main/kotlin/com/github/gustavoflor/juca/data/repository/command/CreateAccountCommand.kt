package com.github.gustavoflor.juca.data.repository.command

import com.github.gustavoflor.juca.core.entity.Account
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class CreateAccountCommand(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    companion object {
        private val KEYS = arrayOf("id", "created_at", "updated_at")
        private val PARAMS = MapSqlParameterSource(mapOf<String, Any>())
        private const val SQL = "INSERT INTO account DEFAULT VALUES"
    }

    fun execute(): Account {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(SQL, PARAMS, keyHolder, KEYS)
        return Account(
            id = keyHolder.keys?.get("id") as Long,
            createdAt = keyHolder.keys?.get("created_at").let { it as Timestamp }.toLocalDateTime(),
            updatedAt = keyHolder.keys?.get("updated_at").let { it as Timestamp }.toLocalDateTime()
        )
    }
}
