package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.TransactionResult
import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.entrypoint.ApiTest
import com.github.gustavoflor.juca.entrypoint.web.v1.Endpoints
import com.github.gustavoflor.juca.entrypoint.web.v1.response.TransactResponse
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
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

class TransactionControllerTest : ApiTest() {
    companion object {
        private const val INVALID_REQUEST_CODE = "INVALID_REQUEST"
        private const val TIMEOUT_DURATION = "60000"
    }

    @MockBean
    private lateinit var transactUseCase: TransactUseCase

    @SpyBean(name = "transactionExecutorService")
    private lateinit var transactionExecutorService: ExecutorService

    @Test
    fun `Given a request, when transact, then should return 200 (Ok)`() {
        val result = TransactionResult.entries.random()
        val merchantCategory = Faker.merchantCategory()
        doReturn(TransactUseCase.Output(result)).`when`(transactUseCase).execute(any())
        val request = Faker.transactRequest(merchantCategory)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.OK.value())
            .body("code", `is`(result.code))

        val inputCaptor = argumentCaptor<TransactUseCase.Input>()
        verify(transactionExecutorService).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase).execute(inputCaptor.capture())
        val input = inputCaptor.firstValue
        assertThat(input.mcc).isEqualTo(request.mcc)
        assertThat(input.amount).isEqualTo(request.amount)
        assertThat(input.externalId).isEqualTo(request.externalId)
        assertThat(input.accountId).isEqualTo(request.accountId)
        assertThat(input.merchantName).isEqualTo(request.merchant?.take(25)?.trim())
        assertThat(input.address).isEqualTo(request.merchant?.takeLast(15)?.trim())
    }

    @Test
    fun `Given a timeout, when transact, then should return 200 (Ok) with error code`() {
        val timeoutDuration = -1L
        val merchantCategory = Faker.merchantCategory()
        val request = Faker.transactRequest(merchantCategory)

        Endpoints.TransactionController.transact(request, timeoutDuration.toString())
            .statusCode(HttpStatus.OK.value())
            .body("code", `is`(TransactionResult.ERROR.code))

        verify(transactionExecutorService).submit(any<Callable<TransactResponse>>())
    }

    @Test
    fun `Given an exception, when transact, then should return 200 (Ok) with error code`() {
        val merchantCategory = Faker.merchantCategory()
        doThrow(RuntimeException()).`when`(transactUseCase).execute(any())
        val request = Faker.transactRequest(merchantCategory)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.OK.value())
            .body("code", `is`(TransactionResult.ERROR.code))
    }

    @Test
    fun `Given a request without timeout duration, when transact, then should return 400 (Bad Request)`() {
        val result = TransactionResult.entries.random()
        val merchantCategory = Faker.merchantCategory()
        doReturn(TransactUseCase.Output(result)).`when`(transactUseCase).execute(any())
        val request = Faker.transactRequest(merchantCategory)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("X-Max-Request-Duration: Failed to convert value of type 'java.lang.String' to required type 'long'; For input string: \"\""))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @Test
    fun `Given a null account ID, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(accountId = null)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("accountId: must not be null"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, -1L, -999999999999L])
    fun `Given a non-positive account ID, when create, then should return 400 (Bad Request)`(accountId: Long) {
        val request = Faker.transactRequest().copy(accountId = accountId)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("accountId: must be greater than 0"))

        verify(transactionExecutorService, never()).submit(any())
        verify(transactUseCase, never()).execute(any())
    }

    @Test
    fun `Given a null amount, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(amount = null)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("amount: must not be null"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(floats = [0F, -1F])
    fun `Given a negative or zero amount, when create, then should return 400 (Bad Request)`(amount: Float) {
        val request = Faker.transactRequest().copy(amount = amount.toBigDecimal())

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("amount: must be greater than 0"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(floats = [0.001F, 100_000_000_000_000.00F])
    fun `Given an amount out of bounds, when create, then should return 400 (Bad Request)`(amount: Float) {
        val request = Faker.transactRequest().copy(amount = amount.toBigDecimal())

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("amount: numeric value out of bounds (<14 digits>.<2 digits> expected)"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @Test
    fun `Given an invalid external ID, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(externalId = null)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("externalId: must not be null"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @Test
    fun `Given a null merchant, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(merchant = null)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("merchant: must not be null"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "",
        " ",
        "PADARIA DO ZE*##",
        "PADARIA DO ZE*#            SAO PAULO BR",
        "PADARIA DO ZE*###            SAO PAULO BR"
    ])
    fun `Given an invalid merchant, when create, then should return 400 (Bad Request)`(merchant: String) {
        val request = Faker.transactRequest()
            .copy(merchant = Faker.numerify(merchant))

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("merchant: must have exactly 40 chars"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @Test
    fun `Given a blank MCC, when create, then should return 400 (Bad Request)`() {
        val request = Faker.transactRequest().copy(mcc = null)

        Endpoints.TransactionController.transact(request, TIMEOUT_DURATION)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("mcc: must not be null"))

        verify(transactionExecutorService, never()).submit(any<Callable<TransactResponse>>())
        verify(transactUseCase, never()).execute(any())
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "22", "333", " ", "XXXX", "A123", "44.4", "55555"])
    fun `Given a MCC with an unexpected size, when create, then should return 400 (Bad Request)`(mcc: String) {
        val request = Faker.transactRequest().copy(mcc = mcc)

        Endpoints.TransactionController.transact(request)
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", `is`(INVALID_REQUEST_CODE))
            .body("message", `is`("mcc: must be a valid MCC"))

        verify(transactUseCase, never()).execute(any())
    }
}
