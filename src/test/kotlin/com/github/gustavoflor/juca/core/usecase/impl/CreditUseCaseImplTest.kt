package com.github.gustavoflor.juca.core.usecase.impl

import com.github.gustavoflor.juca.core.entity.Wallet
import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class CreditUseCaseImplTest {
    @Mock
    private lateinit var walletRepository: WalletRepository

    @InjectMocks
    private lateinit var creditUseCase: CreditUseCaseImpl

    @Test
    fun `Given an unknown account, when execute, then should throw account not found exception`() {
        val input = Faker.creditUseCaseInput()

        assertThatThrownBy { creditUseCase.execute(input) }.isInstanceOf(AccountNotFoundException::class.java)

        verify(walletRepository).findByAccountIdAndMerchantCategory(input.accountId, input.merchantCategory)
    }

    @Test
    fun `Given a known account, when execute, then should return an updated wallet`() {
        val input = Faker.creditUseCaseInput()
        val wallet = Faker.wallet().copy(merchantCategory = input.merchantCategory)
        doReturn(wallet).`when`(walletRepository).findByAccountIdAndMerchantCategory(input.accountId, input.merchantCategory)
        doAnswer { it.getArgument(0, Wallet::class.java) }.`when`(walletRepository).update(any())

        val output = creditUseCase.execute(input)

        assertThat(output.wallet.balance).isEqualTo(wallet.balance.add(input.amount))
        assertThat(output.wallet.accountId).isEqualTo(wallet.accountId)
        assertThat(output.wallet.id).isEqualTo(wallet.id)
        assertThat(output.wallet.merchantCategory).isEqualTo(wallet.merchantCategory)
        assertThat(output.wallet.createdAt).isEqualTo(wallet.createdAt)
        assertThat(output.wallet.updatedAt).isEqualTo(wallet.updatedAt)
        verify(walletRepository).findByAccountIdAndMerchantCategory(input.accountId, input.merchantCategory)
    }
}
