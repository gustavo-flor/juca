package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.core.usecase.FindAccountDetailsByIdUseCase
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import kotlin.random.Random

@ExtendWith(MockitoExtension::class)
class FindAccountDetailsByIdUseCaseImplTest {
    @Mock
    private lateinit var walletRepository: WalletRepository

    @InjectMocks
    private lateinit var findAccountDetailsByIdUseCase: FindAccountDetailsByIdUseCaseImpl

    @Test
    fun `Given an unknown ID, when execute, then should throw account not found exception`() {
        val id = Random.nextLong(1, 99999)
        val input = FindAccountDetailsByIdUseCase.Input(id)

        assertThatThrownBy { findAccountDetailsByIdUseCase.execute(input) }
            .isInstanceOf(AccountNotFoundException::class.java)

        verify(walletRepository).findByAccountId(id)
    }

    @Test
    fun `Given a known, when execute, then should return a list of wallets`() {
        val id = Random.nextLong(1, 99999)
        val input = FindAccountDetailsByIdUseCase.Input(id)
        val wallets = Faker.wallets()
        doReturn(wallets).`when`(walletRepository).findByAccountId(id)

        val output = findAccountDetailsByIdUseCase.execute(input)

        verify(walletRepository).findByAccountId(id)
        assertThat(output.wallets).isEqualTo(wallets)
    }
}
