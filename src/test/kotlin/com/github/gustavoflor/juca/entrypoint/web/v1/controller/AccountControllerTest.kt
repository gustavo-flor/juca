package com.github.gustavoflor.juca.entrypoint.web.v1.controller

import com.github.gustavoflor.juca.core.exception.AccountNotFoundException
import com.github.gustavoflor.juca.core.usecase.CreateAccountUseCase
import com.github.gustavoflor.juca.core.usecase.FindWalletsByAccountIdUseCase
import com.github.gustavoflor.juca.entrypoint.EntrypointTest
import com.github.gustavoflor.juca.entrypoint.web.v1.Endpoints
import com.github.gustavoflor.juca.shared.util.Faker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import kotlin.random.Random

class AccountControllerTest : EntrypointTest() {
    companion object {
        private const val RESOURCE_NOT_FOUND_CODE = "RESOURCE_NOT_FOUND"
    }

    @MockBean
    private lateinit var findWalletsByAccountIdUseCase: FindWalletsByAccountIdUseCase

    @MockBean
    private lateinit var createAccountUseCase: CreateAccountUseCase

    @Test
    fun `Given a request, when create account, then should return 201 (Created)`() {
        val account = Faker.account()
        val wallet = Faker.wallet().copy(accountId = account.id)
        doReturn(CreateAccountUseCase.Output(account, wallet)).`when`(createAccountUseCase).execute()

        Endpoints.AccountController.create()
            .statusCode(HttpStatus.CREATED.value())
            .body("id", `is`(account.id.toInt()))
            .body("wallet.foodBalance", `is`(wallet.foodBalance.toFloat()))
            .body("wallet.mealBalance", `is`(wallet.mealBalance.toFloat()))
            .body("wallet.cashBalance", `is`(wallet.cashBalance.toFloat()))

        verify(createAccountUseCase).execute()
    }

    @Test
    fun `Given an ID, when find by ID, then should return 200 (OK)`() {
        val accountId = Random.nextLong(1, 99999)
        val wallet = Faker.wallet().copy(accountId = accountId)
        doReturn(FindWalletsByAccountIdUseCase.Output(wallet)).`when`(findWalletsByAccountIdUseCase).execute(any())

        Endpoints.AccountController.findById(accountId)
            .statusCode(HttpStatus.OK.value())
            .body("id", `is`(accountId.toInt()))
            .body("wallet.foodBalance", `is`(wallet.foodBalance.toFloat()))
            .body("wallet.mealBalance", `is`(wallet.mealBalance.toFloat()))
            .body("wallet.cashBalance", `is`(wallet.cashBalance.toFloat()))

        val inputCaptor = argumentCaptor<FindWalletsByAccountIdUseCase.Input>()
        verify(findWalletsByAccountIdUseCase).execute(inputCaptor.capture())
        val input = inputCaptor.firstValue
        assertThat(input.accountId).isEqualTo(accountId)
    }

    @Test
    fun `Given an account not found exception, when find by ID, then should return 404 (Not Found)`() {
        val accountId = Random.nextLong(1, 99999)
        doThrow(AccountNotFoundException()).`when`(findWalletsByAccountIdUseCase).execute(any())

        Endpoints.AccountController.findById(accountId)
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("code", `is`(RESOURCE_NOT_FOUND_CODE))
            .body("message", `is`("Account not found"))

        val inputCaptor = argumentCaptor<FindWalletsByAccountIdUseCase.Input>()
        verify(findWalletsByAccountIdUseCase).execute(inputCaptor.capture())
        val input = inputCaptor.firstValue
        assertThat(input.accountId).isEqualTo(accountId)
    }
}
