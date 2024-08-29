package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.ApiTest
import com.github.gustavoflor.juca.entrypoint.web.v1.Endpoints
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus

class TransactionControllerTest : ApiTest() {
    @MockBean
    private lateinit var transactUseCase: TransactUseCase

    @Test
    fun `Given a request, when transact, then should return 200 (Ok)`() {
        val result = TransactionResult.entries.random()
        val merchantCategory = Faker.merchantCategory()
        doReturn(TransactUseCase.Output(result)).`when`(transactUseCase).execute(any())
        val request = Faker.transactRequest(merchantCategory)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.OK.value())
            .body("code", `is`(result.code))

        val inputCaptor = argumentCaptor<TransactUseCase.Input>()
        verify(transactUseCase).execute(inputCaptor.capture())
        val input = inputCaptor.firstValue
        assertThat(input.merchantCategory).isEqualTo(merchantCategory)
        assertThat(input.amount).isEqualTo(request.amount)
        assertThat(input.externalId).isEqualTo(request.externalId)
        assertThat(input.merchantName).isEqualTo(request.merchant)
        assertThat(input.accountId).isEqualTo(request.accountId)
    }
}
