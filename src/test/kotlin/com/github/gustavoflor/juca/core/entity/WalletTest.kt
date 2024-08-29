package com.github.gustavoflor.juca.core.entity

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.random.Random

class WalletTest {
    @Test
    fun `Given a null ID, when is new, then should return false`() {
        val wallet = Faker.wallet().copy(id = null)

        assertThat(wallet.isNew()).isTrue()
    }

    @Test
    fun `Given a non-null ID, when is new, then should return false`() {
        val wallet = Faker.wallet().copy(id = Random.nextLong(1, 99999))

        assertThat(wallet.isNew()).isFalse()
    }

    @Test
    fun `Given an amount, when debit, then should reduce and create new instance`() {
        val wallet = Faker.wallet()
        val amount = Faker.money()

        val debitedWallet = wallet.debit(amount)

        assertThat(debitedWallet.balance).isEqualTo(wallet.balance.subtract(amount))
    }

    @Test
    fun `Given an amount, when credit, then should add and create new instance`() {
        val wallet = Faker.wallet()
        val amount = Faker.money()

        val debitedWallet = wallet.credit(amount)

        assertThat(debitedWallet.balance).isEqualTo(wallet.balance.add(amount))
    }

    @Test
    fun `Given a cash merchant category, when get fallback, then should return null`() {
        val wallets = Faker.wallets()

        val wallet = Wallet.fallback(wallets, MerchantCategory.CASH)

        assertThat(wallet).isNull()
    }

    @ParameterizedTest
    @EnumSource(value = MerchantCategory::class, mode = EnumSource.Mode.EXCLUDE, names = ["CASH"])
    fun `Given a non-cash merchant category, when get fallback, then should return null`(merchantCategory: MerchantCategory) {
        val wallets = Faker.wallets()

        val wallet = Wallet.fallback(wallets, merchantCategory)

        assertThat(wallet).isNotNull
        assertThat(wallet?.merchantCategory).isNotEqualTo(merchantCategory)
    }

    @Test
    fun `Given an account, when instantiate, then should return the new wallet`() {
        val account = Faker.account()
        val merchantCategory = Faker.merchantCategory()

        val wallet = Wallet.of(account, merchantCategory)

        assertThat(wallet.merchantCategory).isEqualTo(merchantCategory)
        assertThat(wallet.balance).isZero()
        assertThat(wallet.accountId).isEqualTo(account.id)
    }
}
