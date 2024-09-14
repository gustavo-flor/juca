package com.github.gustavoflor.juca.core.repository

import com.github.gustavoflor.juca.core.entity.Transaction
import java.util.UUID

interface TransactionRepository {
    fun findAllByAccountId(accountId: Long): List<Transaction>

    fun create(transaction: Transaction): Transaction

    fun createAll(transactions: List<Transaction>): List<Transaction>

    fun findByExternalId(externalId: UUID): Transaction?
}
