package com.github.gustavoflor.juca.core.domain

import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MerchantCategoryTest {
    @ParameterizedTest
    @ValueSource(strings = ["5411", "5412"])
    fun `Given a food code, when get by code, then should return food merchant category`(code: String) {
        val merchantCategory = MerchantCategory.getByCode(code)

        assertThat(merchantCategory).isEqualTo(MerchantCategory.FOOD)
    }

    @ParameterizedTest
    @ValueSource(strings = ["5811", "5812"])
    fun `Given a meal code, when get by code, then should return meal merchant category`(code: String) {
        val merchantCategory = MerchantCategory.getByCode(code)

        assertThat(merchantCategory).isEqualTo(MerchantCategory.MEAL)
    }

    @Test
    fun `Given an unknown code, when get by code, then should return cash merchant category`() {
        val code = Faker.numerify("#9##")

        val merchantCategory = MerchantCategory.getByCode(code)

        assertThat(merchantCategory).isEqualTo(MerchantCategory.CASH)
    }
}
