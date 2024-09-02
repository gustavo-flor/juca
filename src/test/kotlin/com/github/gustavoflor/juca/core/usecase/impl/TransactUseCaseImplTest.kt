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
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.UUID
import kotlin.random.Random

class TransactUseCaseImplTest : IntegrationTest() {
    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Autowired
    private lateinit var transactUseCase: TransactUseCaseImpl

    @Test
    fun `Given concurrent requests with enough money, when execute, then should approve all requests`() {
        val merchantCategory = MerchantCategory.CASH
        val balance = Faker.money(999_999.99)
        val account = accountRepository.create()
        Wallet.of(account, merchantCategory).copy(balance = balance)
            .let { walletRepository.createAll(listOf(it)) }
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
        val wallet = walletRepository.findByAccountIdAndMerchantCategoryForUpdate(account.id, merchantCategory)
        assertThat(wallet?.balance).isEqualTo(balance - amount * threadCount.toBigDecimal())
    }

    @Test
    fun `Given concurrent requests with limited money, when execute, then should approve valid requests and deny others`() {
        val merchantCategory = MerchantCategory.CASH
        val balance = BigDecimal.TEN
        val account = accountRepository.create()
        Wallet.of(account, merchantCategory).copy(balance = balance)
            .let { walletRepository.createAll(listOf(it)) }
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
        val wallet = walletRepository.findByAccountIdAndMerchantCategoryForUpdate(account.id, merchantCategory)
        assertThat(wallet?.balance).isZero()
    }
}
