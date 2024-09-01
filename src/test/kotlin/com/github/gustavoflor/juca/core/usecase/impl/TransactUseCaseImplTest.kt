package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.policy.DebitPolicy
import com.github.gustavoflor.juca.core.repository.MerchantCategoryTermRepository
import com.github.gustavoflor.juca.core.repository.TransactionRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InOrder
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class TransactUseCaseImplTest {
    @Mock
    private lateinit var walletRepository: WalletRepository

    @Mock
    private lateinit var merchantCategoryTermRepository: MerchantCategoryTermRepository

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var debitPolicy: DebitPolicy

    @InjectMocks
    private lateinit var transactUseCase: TransactUseCaseImpl

    private lateinit var inOrder: InOrder

    @BeforeEach
    fun beforeEach() {
        if (!::inOrder.isInitialized) {
            inOrder = inOrder(walletRepository, merchantCategoryTermRepository, transactionRepository, debitPolicy)
        }
    }

    @Test
    fun `Given an already executed external ID, when execute, then should not create a new transaction`() {
        val transaction = Faker.transaction()
        val input = Faker.transactUseCaseInput().copy(
            externalId = transaction.externalId
        )
        doReturn(transaction).`when`(transactionRepository).findByExternalId(input.externalId)

        val output = transactUseCase.execute(input)

        assertThat(output.result).isEqualTo(transaction.result)
        inOrder.verify(transactionRepository).findByExternalId(input.externalId)
        inOrder.verifyNoMoreInteractions()
        verify(merchantCategoryTermRepository, never()).findAll()
        verify(walletRepository, never()).findByAccountIdAndMerchantCategoryForUpdate(any(), any())
        verify(debitPolicy, never()).execute(any())
        verify(transactionRepository, never()).create(any())
    }

    @Test
    fun `Given an unknown account, when execute, then should return error result`() {
        val merchantCategory = Faker.merchantCategory()
        val input = Faker.transactUseCaseInput(merchantCategory)

        assertThatThrownBy { transactUseCase.execute(input) }.isInstanceOf(AccountNotFoundException::class.java)

        inOrder.verify(transactionRepository).findByExternalId(input.externalId)
        inOrder.verify(merchantCategoryTermRepository).findAll()
        inOrder.verify(walletRepository).findByAccountIdAndMerchantCategoryForUpdate(input.accountId, merchantCategory)
        inOrder.verifyNoMoreInteractions()
        verify(debitPolicy, never()).execute(any())
        verify(transactionRepository, never()).create(any())
    }

    @Test
    fun `Given, when, then`() {
        val merchantCategory = Faker.merchantCategory()
        val input = Faker.transactUseCaseInput(merchantCategory)

        assertThatThrownBy { transactUseCase.execute(input) }.isInstanceOf(AccountNotFoundException::class.java)

        inOrder.verify(transactionRepository).findByExternalId(input.externalId)
        inOrder.verify(merchantCategoryTermRepository).findAll()
        inOrder.verify(walletRepository).findByAccountIdAndMerchantCategoryForUpdate(input.accountId, merchantCategory)
        inOrder.verifyNoMoreInteractions()
        verify(debitPolicy, never()).execute(any())
        verify(transactionRepository, never()).create(any())
    }
}
