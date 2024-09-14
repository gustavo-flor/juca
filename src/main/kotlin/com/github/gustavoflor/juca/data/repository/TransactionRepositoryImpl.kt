package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.data.repository.command.CreateTransactionCommand
import com.github.gustavoflor.juca.data.repository.query.FindTransactionByExternalIdQuery
import com.github.gustavoflor.juca.data.repository.query.FindTransactionsByAccountIdQuery
import org.springframework.stereotype.Repository
import java.util.UUID

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

    override fun createAll(transactions: List<Transaction>): List<Transaction> {
        return createTransactionCommand.executeAll(transactions)
    }

    override fun findByExternalId(externalId: UUID): Transaction? {
        return findTransactionByExternalIdQuery.execute(externalId)
    }
}
