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
        doAnswer { it.getArgument(0) as List<Wallet> }.`when`(walletRepository).createAll(any())

        createAccountUseCase.execute()

        val walletsCaptor = argumentCaptor<List<Wallet>>()
        verify(accountRepository).create()
        verify(walletRepository).createAll(walletsCaptor.capture())
        val wallets = walletsCaptor.firstValue
        MerchantCategory.entries.forEach { merchantCategory ->
            val wallet = wallets.firstOrNull() { it.merchantCategory == merchantCategory }
            assertThat(wallet).isNotNull
            assertThat(wallet?.balance).isZero()
            assertThat(wallet?.accountId).isEqualTo(account.id)
        }
    }
}
