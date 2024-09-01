package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.data.DataTest
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TransactionRepositoryImplTest : DataTest() {
    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Test
    fun `Given a non-new transaction, when create, then should throw illegal argument exception`() {
        val transaction = Faker.transaction()

        assertThatThrownBy { transactionRepository.create(transaction) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Transaction is not new")
    }

    @Test
    fun `Given a new transaction, when create, then should find by external ID`() {
        val accountId = createAccount()
        val transaction = Faker.newTransaction().copy(accountId = accountId)

        val newTransaction = transactionRepository.create(transaction)

        val foundTransaction = transactionRepository.findByExternalId(transaction.externalId)
        assertThat(foundTransaction).isEqualTo(newTransaction)
    }

    @Test
    fun `Given a new transactions, when create, then should find all by account ID`() {
        val accountId = createAccount()
        val transactions = List(5) { Faker.newTransaction().copy(accountId = accountId) }

        val newTransactions = transactions.map { transactionRepository.create(it) }

        val foundTransactions = transactionRepository.findAllByAccountId(accountId)
        assertThat(newTransactions.size).isEqualTo(transactions.size)
        assertThat(foundTransactions.size).isEqualTo(transactions.size)
        assertThat(foundTransactions).containsAll(newTransactions)
    }
}
