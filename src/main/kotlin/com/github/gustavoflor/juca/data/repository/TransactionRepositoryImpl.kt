package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.data.repository.command.CreateTransactionCommand
import com.github.gustavoflor.juca.data.repository.query.FindTransactionByExternalIdQuery
import com.github.gustavoflor.juca.data.repository.query.FindTransactionsByAccountIdQuery
import org.springframework.stereotype.Repository

@Repository
class TransactionRepositoryImpl(
    private val findTransactionByExternalIdQuery: FindTransactionByExternalIdQuery,
    private val findTransactionsByAccountIdQuery: FindTransactionsByAccountIdQuery,
    private val createTransactionCommand: CreateTransactionCommand
) : TransactionRepository {
    override fun findAllByAccountId(accountId: Long): List<Transaction> {
        return findTransactionsByAccountIdQuery.execute(accountId)
    }

    override fun create(transaction: Transaction): Transaction {
        return createTransactionCommand.execute(transaction)
    }

    override fun findByExternalId(externalId: String): Transaction? {
        return findTransactionByExternalIdQuery.execute(externalId)
    }
}
