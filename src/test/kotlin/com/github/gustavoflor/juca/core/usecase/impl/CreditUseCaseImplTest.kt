package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.TransactionType
import com.github.gustavoflor.juca.core.entity.Transaction
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class CreditUseCaseImplTest {
    @Mock
    private lateinit var walletRepository: WalletRepository

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @InjectMocks
    private lateinit var creditUseCase: AddCreditUseCaseImpl

    @Test
    fun `Given an unknown account, when execute, then should throw account not found exception`() {
        val input = Faker.creditUseCaseInput()

        assertThatThrownBy { creditUseCase.execute(input) }.isInstanceOf(AccountNotFoundException::class.java)

        verify(walletRepository).findByAccountIdAndMerchantCategoryForUpdate(input.accountId, input.merchantCategory)
    }

    @Test
    fun `Given a known account, when execute, then should return an updated wallet`() {
        val input = Faker.creditUseCaseInput()
        val wallet = Faker.wallet().copy(merchantCategory = input.merchantCategory)
        doReturn(wallet).`when`(walletRepository).findByAccountIdAndMerchantCategoryForUpdate(input.accountId, input.merchantCategory)
        doAnswer { it.getArgument(0, Wallet::class.java) }.`when`(walletRepository).update(any())

        val output = creditUseCase.execute(input)

        assertThat(output.wallet.balance).isEqualTo(wallet.balance.add(input.amount))
        assertThat(output.wallet.accountId).isEqualTo(wallet.accountId)
        assertThat(output.wallet.id).isEqualTo(wallet.id)
        assertThat(output.wallet.merchantCategory).isEqualTo(wallet.merchantCategory)
        assertThat(output.wallet.createdAt).isEqualTo(wallet.createdAt)
        assertThat(output.wallet.updatedAt).isEqualTo(wallet.updatedAt)
        verify(walletRepository).findByAccountIdAndMerchantCategoryForUpdate(input.accountId, input.merchantCategory)
        verify(walletRepository).update(output.wallet)
        val transactionCaptor = argumentCaptor<Transaction>()
        verify(transactionRepository).create(transactionCaptor.capture())
        val transaction = transactionCaptor.firstValue
        assertThat(transaction.id).isNull()
        assertThat(transaction.accountId).isEqualTo(input.accountId)
        assertThat(transaction.externalId).isNotNull()
        assertThat(transaction.origin).isEqualTo("Juca API")
        assertThat(transaction.amount).isEqualTo(input.amount)
        assertThat(transaction.type).isEqualTo(TransactionType.CREDIT)
        assertThat(transaction.merchantCategory).isEqualTo(input.merchantCategory)
        assertThat(transaction.result).isEqualTo(TransactionResult.APPROVED)
        assertThat(transaction.createdAt).isNull()
    }
}
