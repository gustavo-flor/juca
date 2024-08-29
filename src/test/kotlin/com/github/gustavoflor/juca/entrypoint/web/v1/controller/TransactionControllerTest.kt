package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.ApiTest
import com.github.gustavoflor.juca.entrypoint.web.v1.Endpoints
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus

class TransactionControllerTest : ApiTest() {
    companion object {
        private const val INVALID_REQUEST_CODE = "INVALID_REQUEST"
    }

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

    @Test
    fun `Given a null account ID, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(accountId = null)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("accountId: must not be null"))

        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, -1L, -999999999999L])
    fun `Given a non-positive account ID, when create, then should return 400 (Bad Request)`(accountId: Long) {
        val request = Faker.transactRequest().copy(accountId = accountId)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("accountId: must be greater than 0"))

        verify(transactUseCase, never()).execute(any())
    }

    @Test
    fun `Given a null amount, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(amount = null)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("amount: must not be null"))

        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ["", " "])
    fun `Given an invalid external ID, when create, then should return 400 (Bad Request)`(externalId: String?) {
        val request = Faker.transactRequest().copy(externalId = externalId)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("externalId: must not be blank"))

        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ["", " "])
    fun `Given an invalid merchant, when create, then should return 400 (Bad Request)`(merchant: String?) {
        val request = Faker.transactRequest().copy(merchant = merchant)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("merchant: must not be blank"))

        verify(transactUseCase, never()).execute(any())
    }

    @Test
    fun `Given a blank MCC, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(mcc = null)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("mcc: must not be null"))

        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(strings = ["XXXX", "A123", "44.4"])
    fun `Given an invalid MCC, when create, then should return 400 (Bad Request)`(mcc: String) {
        val request = Faker.transactRequest().copy(mcc = mcc)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("mcc: numeric value out of bounds (<4 digits>.<0 digits> expected)"))

        verify(transactUseCase, never()).execute(any())
    }
}
