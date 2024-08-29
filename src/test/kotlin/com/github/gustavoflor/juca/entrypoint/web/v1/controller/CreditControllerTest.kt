package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.usecase.CreditUseCase
import com.github.gustavoflor.juca.entrypoint.ApiTest
import com.github.gustavoflor.juca.entrypoint.web.v1.Endpoints
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus

class CreditControllerTest : ApiTest() {
    companion object {
        private const val RESOURCE_NOT_FOUND_CODE = "RESOURCE_NOT_FOUND"
        private const val INVALID_REQUEST_CODE = "INVALID_REQUEST"
        private const val INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR"
    }

    @MockBean
    private lateinit var creditUseCase: CreditUseCase

    @Test
    fun `Given a request, when create credit, then should return 201 (Created)`() {
        val wallet = Faker.wallet()
        doReturn(CreditUseCase.Output(wallet)).`when`(creditUseCase).execute(any())
        val request = Faker.creditRequest(wallet)

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.CREATED.value())
            .body("balance", `is`(wallet.balance.toFloat()))

        val inputCaptor = argumentCaptor<CreditUseCase.Input>()
        verify(creditUseCase).execute(inputCaptor.capture())
        val input = inputCaptor.firstValue
        assertThat(input.merchantCategory)
    }

    @Test
    fun `Given an account not found exception, when create credit, then should return 404 (Not Found)`() {
        val wallet = Faker.wallet()
        doThrow(AccountNotFoundException()).`when`(creditUseCase).execute(any())
        val request = Faker.creditRequest(wallet)

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("code", `is`(RESOURCE_NOT_FOUND_CODE))
            .body("message", `is`("Account not found"))

        val inputCaptor = argumentCaptor<CreditUseCase.Input>()
        verify(creditUseCase).execute(inputCaptor.capture())
        val input = inputCaptor.firstValue
        assertThat(input.merchantCategory)
    }

    @Test
    fun `Given an unknown exception, when create credit, then should return 500 (Internal Server Error)`() {
        val wallet = Faker.wallet()
        doThrow(RuntimeException()).`when`(creditUseCase).execute(any())
        val request = Faker.creditRequest(wallet)

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("code", `is`(INTERNAL_SERVER_ERROR_CODE))
            .body("message", `is`("Something went wrong, try again later. If it persists, please check the logs to see the problems"))

        val inputCaptor = argumentCaptor<CreditUseCase.Input>()
        verify(creditUseCase).execute(inputCaptor.capture())
        val input = inputCaptor.firstValue
        assertThat(input.merchantCategory)
    }

    @Test
    fun `Given no request body, when create, then should return 400 (Bad Request)`() {
        Endpoints.CreditController.create()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("Invalid request content, please check the docs to see the requirements"))

        verify(creditUseCase, never()).execute(any())
    }

    @Test
    fun `Given a null account ID, when create, then should return 400 (Bad Request)`() {
        val request = Faker.creditRequest().copy(accountId = null)

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("accountId: must not be null"))

        verify(creditUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, -1L, -999999999999L])
    fun `Given a non-positive account ID, when create, then should return 400 (Bad Request)`(accountId: Long) {
        val request = Faker.creditRequest().copy(accountId = accountId)

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("accountId: must be greater than 0"))

        verify(creditUseCase, never()).execute(any())
    }

    @Test
    fun `Given a null amount, when create, then should return 400 (Bad Request)`() {
        val request = Faker.creditRequest().copy(amount = null)

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("amount: must not be null"))

        verify(creditUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, -1.0, -0.5, -9999999999.9])
    fun `Given a non-positive amount, when create, then should return 400 (Bad Request)`(amount: Double) {
        val request = Faker.creditRequest().copy(amount = amount.toBigDecimal())

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("amount: must be greater than 0"))

        verify(creditUseCase, never()).execute(any())
    }

    @Test
    fun `Given a null merchant category, when create, then should return 400 (Bad Request)`() {
        val request = Faker.creditRequest().copy(merchantCategory = null)

        Endpoints.CreditController.create(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("merchantCategory: must not be null"))

        verify(creditUseCase, never()).execute(any())
    }
}
