package com.github.gustavoflor.juca.core.repository

import com.github.gustavoflor.juca.core.entity.Transaction

interface TransactionRepository {
    fun findAllByAccountId(accountId: Long): List<Transaction>

    fun create(transaction: Transaction): Transaction

    fun findByExternalId(externalId: String): Transaction?
}
