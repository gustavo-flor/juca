package com.github.gustavoflor.juca.core.entity

import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
    fun `Given an account, when instantiate, then should return the new wallet`() {
        val account = Faker.account()

        val wallet = Wallet.of(account)

        assertThat(wallet.foodBalance).isZero()
        assertThat(wallet.mealBalance).isZero()
        assertThat(wallet.cashBalance).isZero()
        assertThat(wallet.accountId).isEqualTo(account.id)
        assertThat(wallet.id).isNull()
        assertThat(wallet.createdAt).isNull()
        assertThat(wallet.updatedAt).isNull()
    }
}
