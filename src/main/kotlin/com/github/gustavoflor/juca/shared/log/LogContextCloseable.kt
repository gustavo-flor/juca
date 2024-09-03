package com.github.gustavoflor.juca.shared.log

import org.slf4j.MDC
import java.io.Closeable
import java.util.UUID

class LogContextCloseable(
    private val values: Map<String, Any?>
) : Closeable {
    companion object {
        fun addAccountId(accountId: Long?): Builder {
            return Builder()
                .addAccountId(accountId)
        }
    }

    init {
        values.filterValues { it != null }
            .forEach { (key, value) -> MDC.put(key, value.toString()) }
    }

    override fun close() {
        values.forEach { MDC.remove(it.key) }
    }

    class Builder {
        var accountId: Long? = null
        var externalId: UUID? = null

        fun addAccountId(accountId: Long?): Builder {
            this.accountId = accountId
            return this
        }

        fun addExternalId(externalId: UUID?): Builder {
            this.externalId = externalId
            return this
        }

        fun track(): LogContextCloseable {
            val values = mapOf(
                "account_id" to accountId,
                "external_id" to externalId
            )
            return LogContextCloseable(values)
        }
    }
}
