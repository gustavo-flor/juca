package com.github.gustavoflor.juca.core

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class TransactionResultTest {
    @Test
    fun `Given an error result, when get code, then should return 07`() {
        val transactionResult = TransactionResult.ERROR

        assertThat(transactionResult.code).isEqualTo("07")
    }

    @Test
    fun `Given an insufficient balance result, when get code, then should return 51`() {
        val transactionResult = TransactionResult.INSUFFICIENT_BALANCE

        assertThat(transactionResult.code).isEqualTo("51")
    }

    @Test
    fun `Given an approved result, when get code, then should return 00`() {
        val transactionResult = TransactionResult.APPROVED

        assertThat(transactionResult.code).isEqualTo("00")
    }
}
