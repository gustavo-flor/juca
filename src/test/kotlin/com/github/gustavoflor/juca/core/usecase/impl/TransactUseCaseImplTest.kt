package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class TransactUseCaseImplTest {
    @Mock
    private lateinit var walletRepository: WalletRepository

    @InjectMocks
    private lateinit var transactUseCase: TransactUseCaseImpl

    @Test
    fun `Given an unknown account, when execute, then should return error result`() {
        val input = Faker.transactUseCaseInput()

        val output = transactUseCase.execute(input)

        assertThat(output.result).isEqualTo(TransactionResult.ERROR)

        verify(walletRepository).findByAccountId(input.accountId)
    }
}
