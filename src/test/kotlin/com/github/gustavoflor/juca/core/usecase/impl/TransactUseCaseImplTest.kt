package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.policy.DebitPolicy
import com.github.gustavoflor.juca.core.repository.MerchantCategoryTermRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class TransactUseCaseImplTest {
    @Mock
    private lateinit var walletRepository: WalletRepository

    @Mock
    private lateinit var merchantCategoryTermRepository: MerchantCategoryTermRepository

    @Mock
    private lateinit var debitPolicy: DebitPolicy

    @InjectMocks
    private lateinit var transactUseCase: TransactUseCaseImpl

    @Test
    fun `Given an unknown account, when execute, then should return error result`() {
        val merchantCategory = Faker.merchantCategory()
        val input = Faker.transactUseCaseInput(merchantCategory)

        val output = transactUseCase.execute(input)

        assertThat(output.result).isEqualTo(TransactionResult.ERROR)
        verify(merchantCategoryTermRepository).findAll()
        verify(walletRepository).findByAccountIdAndMerchantCategoryForUpdate(input.accountId, merchantCategory)
        verify(debitPolicy, never()).execute(any())
    }
}
