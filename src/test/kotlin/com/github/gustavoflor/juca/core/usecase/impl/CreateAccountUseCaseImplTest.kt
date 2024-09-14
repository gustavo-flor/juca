package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.domain.MerchantCategory
import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.repository.AccountRepository
import com.github.gustavoflor.juca.core.repository.WalletRepository
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

@ExtendWith(MockitoExtension::class)
class CreateAccountUseCaseImplTest {
    @Mock
    private lateinit var walletRepository: WalletRepository

    @Mock
    private lateinit var accountRepository: AccountRepository

    @InjectMocks
    private lateinit var createAccountUseCase: CreateAccountUseCaseImpl

    @Test
    fun `Given a request, when create, then should create account and wallets`() {
        val account = Faker.account()
        doReturn(account).`when`(accountRepository).create()
        doAnswer { it.getArgument(0, Wallet::class.java) }.`when`(walletRepository).create(any())

        createAccountUseCase.execute()

        val walletCaptor = argumentCaptor<Wallet>()
        verify(accountRepository).create()
        verify(walletRepository).create(walletCaptor.capture())
        val wallet = walletCaptor.firstValue
        assertThat(wallet.id).isNull()
        assertThat(wallet.createdAt).isNull()
        assertThat(wallet.updatedAt).isNull()
        assertThat(wallet.foodBalance).isZero()
        assertThat(wallet.mealBalance).isZero()
        assertThat(wallet.cashBalance).isZero()
        assertThat(wallet.accountId).isEqualTo(account.id)
    }
}
