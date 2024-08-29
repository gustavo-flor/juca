package com.github.gustavoflor.juca.entrypoint.web.v1.request

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.usecase.CreditUseCase
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreditRequest(
    @field:NotNull
    @field:Positive
    val accountId: Long? = null,
    @field:NotNull
    @field:Positive
    val amount: BigDecimal? = null,
    @field:NotNull
    val merchantCategory: MerchantCategory? = null
) {
    fun input() = CreditUseCase.Input(
        accountId = accountId!!,
        amount = amount!!,
        merchantCategory = merchantCategory!!
    )
}
