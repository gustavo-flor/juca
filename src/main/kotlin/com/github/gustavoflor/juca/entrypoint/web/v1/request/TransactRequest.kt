package com.github.gustavoflor.juca.entrypoint.web.v1.request

import com.github.gustavoflor.juca.core.usecase.TransactUseCase
import com.github.gustavoflor.juca.shared.validation.MCC
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class TransactRequest(
    @field:NotBlank
    val externalId: String? = null,
    @field:NotNull
    @field:Positive
    val accountId: Long? = null,
    @field:NotNull
    @field:Positive
    val amount: BigDecimal? = null,
    @field:NotNull
    @field:MCC
    val mcc: String? = null,
    @field:NotNull
    @field:Size(min = 40, max = 40, message = "must have exactly 40 chars")
    val merchant: String? = null
) {
    fun input(): TransactUseCase.Input {
        val merchantName = merchant!!.take(25).trim()
        val address = merchant.takeLast(15).trim()
        return TransactUseCase.Input(
            externalId = externalId!!,
            accountId = accountId!!,
            amount = amount!!,
            mcc = mcc!!,
            merchantName = merchantName,
            address = address
        )
    }
}
