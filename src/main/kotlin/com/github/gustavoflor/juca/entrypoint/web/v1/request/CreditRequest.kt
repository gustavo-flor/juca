package com.github.gustavoflor.juca.entrypoint.web.v1.request

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.usecase.AddCreditUseCase
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreditRequest(
    @field:NotNull
    @field:Positive
    val accountId: Long? = null,
    @field:NotNull
    @field:Positive
    @field:Digits(integer = 14, fraction = 2)
    @Schema(example = "100.0")
    val amount: BigDecimal? = null,
    @field:NotNull
    val merchantCategory: MerchantCategory? = null
) {
    fun input() = AddCreditUseCase.Input(
        accountId = accountId!!,
        amount = amount!!,
        merchantCategory = merchantCategory!!
    )
}
