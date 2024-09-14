package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.IntegrationTest
import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.domain.TransactionResult
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.repository.AccountRepository
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.UUID
import kotlin.random.Random

class TransactUseCaseImplTest : IntegrationTest() {
    companion object {
        @JvmStatic
        fun merchantTermsByCategoryArgs() = listOf(
            Arguments.of(listOf("EATS", "RESTAURANTE", "PADARIA"), MerchantCategory.MEAL),
            Arguments.of(listOf("MERCADO", "SUPERMERCADO"), MerchantCategory.FOOD),
            Arguments.of(listOf("BILHETEUNICO", "UBER TRIP"), MerchantCategory.CASH)
        )
    }

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var transactUseCase: TransactUseCaseImpl

    @ParameterizedTest
    @MethodSource("merchantTermsByCategoryArgs")
    fun `Given merchant terms, when transact, then should execute with success`(terms: List<String>, merchantCategory: MerchantCategory) {
        terms.forEach { term ->
            val balance = Faker.money(999_999.99)
            val account = accountRepository.create()
            val mcc = Faker.merchantCategory(listOf(merchantCategory), true).let { Faker.mcc(it) }
            Wallet.of(account)
                .let { merchantCategory.credit(balance, it) }
                .let { walletRepository.create(it) }
            val input = Faker.transactUseCaseInput().copy(
                accountId = account.id,
                merchantName = "PICPAY*$term",
                mcc = mcc
            )

            transactUseCase.execute(input)

            val transactions = transactionRepository.findAllByAccountId(account.id)
            assertThat(transactions.size).isEqualTo(1)
            transactions[0].let {
                assertThat(it.amount).isEqualTo(input.amount)
                assertThat(it.accountId).isEqualTo(input.accountId)
                assertThat(it.merchantCategory).isEqualTo(merchantCategory)
                assertThat(it.externalId).isEqualTo(input.externalId)
                assertThat(it.origin).isEqualTo("${input.merchantName} - ${input.address}")
                assertThat(it.result).isEqualTo(TransactionResult.APPROVED)
            }
            val wallet = walletRepository.findByAccountId(account.id)
            assertThat(wallet).isNotNull
            assertThat(merchantCategory.getBalance(wallet!!)).isEqualTo(balance - input.amount)
        }
    }

    @Test
    fun `Given a transaction with no money available, when transact, then should execute with success`() {
        val balance = BigDecimal.ZERO
        val account = accountRepository.create()
        val merchantCategory = Faker.merchantCategory()
        Wallet.of(account).let { walletRepository.create(it) }
        val input = Faker.transactUseCaseInput().copy(
            accountId = account.id,
            mcc = Faker.mcc(merchantCategory)
        )

        transactUseCase.execute(input)

        val transactions = transactionRepository.findAllByAccountId(account.id)
        assertThat(transactions.size).isEqualTo(1)
        transactions[0].let {
            assertThat(it.amount).isEqualTo(input.amount)
            assertThat(it.accountId).isEqualTo(input.accountId)
            assertThat(it.merchantCategory).isEqualTo(merchantCategory)
            assertThat(it.externalId).isEqualTo(input.externalId)
            assertThat(it.origin).isEqualTo("${input.merchantName} - ${input.address}")
            assertThat(it.result).isEqualTo(TransactionResult.INSUFFICIENT_BALANCE)
        }
        val wallet = walletRepository.findByAccountIdForUpdate(account.id)
        assertThat(wallet?.let { merchantCategory.getBalance(it) }).isEqualTo(balance)
    }

    @Test
    fun `Given a transaction with money available only on fallback, when transact, then should execute with success`() {
        val fallbackCategory = MerchantCategory.CASH
        val account = accountRepository.create()
        val balance = Faker.money(999_999.99)
        Wallet.of(account).let { fallbackCategory.credit(balance, it) }.let { walletRepository.create(it) }
        val input = Faker.transactUseCaseInput().copy(
            accountId = account.id,
            merchantName = "PADARIA DO ZE",
            mcc = Faker.mcc()
        )

        transactUseCase.execute(input)

        val transactions = transactionRepository.findAllByAccountId(account.id)
        assertThat(transactions.size).isEqualTo(1)
        transactions[0].let {
            assertThat(it.amount).isEqualTo(input.amount)
            assertThat(it.accountId).isEqualTo(input.accountId)
            assertThat(it.externalId).isEqualTo(input.externalId)
            assertThat(it.origin).isEqualTo("${input.merchantName} - ${input.address}")
            assertThat(it.result).isEqualTo(TransactionResult.APPROVED)
        }
        val wallet = walletRepository.findByAccountId(account.id)
        assertThat(wallet?.let { fallbackCategory.getBalance(it) }).isEqualTo(balance - input.amount)
    }

    @Test
    fun `Given concurrent requests with enough money, when execute, then should approve all requests`() {
        val merchantCategory = MerchantCategory.CASH
        val balance = Faker.money(999_999.99)
        val account = accountRepository.create()
        merchantCategory.credit(balance, Wallet.of(account))
            .let { walletRepository.create(it) }
        val threadCount = Random.nextInt(5, 20)
        val amount = BigDecimal.TEN

        doSyncAndConcurrently(threadCount) { index ->
            val input = Faker.transactUseCaseInput().copy(
                accountId = account.id,
                externalId = UUID.randomUUID(),
                amount = amount,
                merchantName = "TEST*$index",
                mcc = "0000",
            )
            transactUseCase.execute(input)
        }

        val transactions = transactionRepository.findAllByAccountId(account.id)
        assertThat(transactions.size).isEqualTo(threadCount)
        val wallet = walletRepository.findByAccountId(account.id)
        assertThat(wallet?.let { merchantCategory.getBalance(it) }).isEqualTo(balance - amount * threadCount.toBigDecimal())
    }

    @Test
    fun `Given concurrent requests with limited money, when execute, then should approve valid requests and deny others`() {
        val merchantCategory = MerchantCategory.CASH
        val balance = BigDecimal.TEN
        val account = accountRepository.create()
        Wallet.of(account)
            .let { merchantCategory.credit(balance, it) }
            .let { walletRepository.create(it) }
        val threadCount = Random.nextInt(10, 20)
        val amount = BigDecimal.ONE
        val expectedApproves = balance.toInt()

        doSyncAndConcurrently(threadCount) { index ->
            val input = Faker.transactUseCaseInput().copy(
                accountId = account.id,
                externalId = UUID.randomUUID(),
                amount = amount,
                merchantName = "TEST*$index",
                mcc = "0000",
            )
            transactUseCase.execute(input)
        }

        val transactions = transactionRepository.findAllByAccountId(account.id)
        assertThat(transactions.filter { it.result == TransactionResult.APPROVED }.size).isEqualTo(expectedApproves)
        assertThat(transactions.filter { it.result == TransactionResult.INSUFFICIENT_BALANCE }.size).isEqualTo(threadCount - expectedApproves)
        val wallet = walletRepository.findByAccountId(account.id)
        assertThat(wallet?.let { merchantCategory.getBalance(it) }).isZero()
    }
}
